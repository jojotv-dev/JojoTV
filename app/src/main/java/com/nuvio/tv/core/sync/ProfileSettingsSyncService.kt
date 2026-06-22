package com.nuvio.tv.core.sync

import android.os.SystemClock
import android.util.Log
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.nuvio.tv.core.auth.AuthManager
import com.nuvio.tv.core.profile.ProfileManager
import com.nuvio.tv.data.local.ExperienceModeDataStore
import com.nuvio.tv.data.local.ProfileDataStoreFactory
import com.nuvio.tv.data.remote.supabase.SupabaseProfileSettingsBlob
import com.nuvio.tv.domain.model.DiscoverLocation
import com.streamvault.data.local.dao.ProviderDao
import com.streamvault.data.local.dao.FavoriteDao
import com.streamvault.data.local.dao.MovieDao
import com.streamvault.data.local.dao.SeriesDao
import com.streamvault.data.local.entity.FavoriteEntity
import com.streamvault.data.local.entity.ProviderEntity
import com.streamvault.domain.model.ContentType
import com.streamvault.domain.model.ProviderType
import com.streamvault.domain.repository.ProviderRepository
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.inject.Inject
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Singleton

private const val TAG = "ProfileSettingsSyncService"
private const val SETTINGS_PUSH_DEBOUNCE_MS = 1500L
private const val FOREGROUND_PULL_DELAY_MS = 2500L
private const val FOREGROUND_PULL_MIN_INTERVAL_MS = 60_000L
private const val SETTINGS_SYNC_PLATFORM = "tv"
private const val IPTV_PROVIDERS_FEATURE = "iptv_providers"
private const val IPTV_SYNC_SCHEMA_VERSION = 2
private const val IPTV_SYNC_SECRET_PREFIX = "syncenc:v1:"
private const val IPTV_SYNC_KEY_CONTEXT = "nuvio:iptv-provider-sync:"

private val IPTV_PROVIDER_SCOPED_KEY_PREFIXES = listOf(
    "iptv_hidden_groups_",
    "iptv_hidden_channels_",
    "iptv_group_renames_"
)

internal fun remapIptvPreferenceProviderIds(
    featureJson: JsonObject,
    providerIdMap: Map<Long, Long>
): JsonObject = buildJsonObject {
    featureJson.forEach { (key, value) ->
        val prefix = IPTV_PROVIDER_SCOPED_KEY_PREFIXES.firstOrNull(key::startsWith)
        val sourceProviderId = prefix?.let { key.removePrefix(it).toLongOrNull() }
        val localProviderId = sourceProviderId?.let(providerIdMap::get)
        put(if (prefix != null && localProviderId != null) "$prefix$localProviderId" else key, value)
    }
}

@Singleton
class ProfileSettingsSyncService @Inject constructor(
    private val authManager: AuthManager,
    private val postgrest: Postgrest,
    private val profileManager: ProfileManager,
    private val profileDataStoreFactory: ProfileDataStoreFactory,
    private val providerDao: ProviderDao,
    private val favoriteDao: FavoriteDao,
    private val movieDao: MovieDao,
    private val seriesDao: SeriesDao,
    private val providerRepository: ProviderRepository,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val syncMutex = Mutex()

    @Volatile
    private var applyingRemoteBlob: Boolean = false

    @Volatile
    private var skipNextPushSignature: String? = null
    private var foregroundPullJob: Job? = null
    private var lastForegroundPullAtMs: Long = 0L

    private val syncedFeatures = listOf(
        "theme_settings",
        "layout_settings",
        ExperienceModeDataStore.FEATURE,
        "player_settings",
        "trailer_settings",
        "tmdb_settings",
        "mdblist_settings",
        "trakt_settings",
        "debrid_settings",
        "animeskip_settings",
        "freebox_settings",
        "freebox_poster_overrides",
        "iptv_settings",
        "track_preference"
    )

    private val catalogKeysExcludedFromBlob = setOf(
        "home_catalog_order_keys",
        "disabled_home_catalog_keys",
        "custom_catalog_titles"
    )

    private val localOnlyLayoutKeys = setOf(
        "last_non_off_discover_location"
    )

    init {
        observeLocalSettingsChangesAndSync()
    }

    private suspend fun <T> withJwtRefreshRetry(block: suspend () -> T): T {
        return try {
            block()
        } catch (e: Exception) {
            if (!authManager.refreshSessionIfJwtExpired(e)) throw e
            block()
        }
    }

    suspend fun pushCurrentProfileToRemote(): Result<Unit> = withContext(Dispatchers.IO) {
        syncMutex.withLock {
            try {
                val profileId = profileManager.activeProfileId.value
                val settingsJson = exportSettingsBlob(profileId)

                val params = buildJsonObject {
                    put("p_profile_id", profileId)
                    put("p_settings_json", settingsJson)
                    put("p_platform", SETTINGS_SYNC_PLATFORM)
                }

                withJwtRefreshRetry {
                    postgrest.rpc("sync_push_profile_settings_blob", params)
                }

                Log.d(TAG, "Pushed profile settings blob for profile $profileId")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to push profile settings blob", e)
                Result.failure(e)
            }
        }
    }

    suspend fun pullCurrentProfileFromRemote(): Result<Boolean> = withContext(Dispatchers.IO) {
        syncMutex.withLock {
            try {
                val profileId = profileManager.activeProfileId.value
                val params = buildJsonObject {
                    put("p_profile_id", profileId)
                    put("p_platform", SETTINGS_SYNC_PLATFORM)
                }

                val response = withJwtRefreshRetry {
                    postgrest.rpc("sync_pull_profile_settings_blob", params)
                }
                lastForegroundPullAtMs = SystemClock.elapsedRealtime()
                val rows = response.decodeList<SupabaseProfileSettingsBlob>()
                val blob = rows.firstOrNull()?.settingsJson
                if (blob == null) {
                    val settingsJson = exportSettingsBlob(profileId)
                    val seedParams = buildJsonObject {
                        put("p_profile_id", profileId)
                        put("p_settings_json", settingsJson)
                        put("p_platform", SETTINGS_SYNC_PLATFORM)
                    }
                    withJwtRefreshRetry {
                        postgrest.rpc("sync_push_profile_settings_blob", seedParams)
                    }
                    Log.d(TAG, "No remote profile settings blob for profile $profileId; seeded it from local settings")
                    return@withLock Result.success(true)
                }

                val featuresJson = blob["features"]?.jsonObject ?: return@withLock Result.success(false)
                val remoteIptvSchemaVersion = featuresJson[IPTV_PROVIDERS_FEATURE]
                    ?.jsonObject
                    ?.get("schema_version")
                    ?.jsonPrimitive
                    ?.intOrNull
                    ?: 1
                val remoteSignature = buildSettingsSignature(featuresJson)
                val localSignature = buildSettingsSignature(profileId)
                if (remoteSignature == localSignature) {
                    Log.d(TAG, "Remote profile settings already match local for profile $profileId")
                    return@withLock Result.success(false)
                }

                importSettingsBlob(profileId, featuresJson)
                if (
                    remoteIptvSchemaVersion < IPTV_SYNC_SCHEMA_VERSION &&
                    favoriteDao.getAllGlobalSync().isNotEmpty()
                ) {
                    val upgradedSettings = exportSettingsBlob(profileId)
                    val upgradeParams = buildJsonObject {
                        put("p_profile_id", profileId)
                        put("p_settings_json", upgradedSettings)
                        put("p_platform", SETTINGS_SYNC_PLATFORM)
                    }
                    withJwtRefreshRetry {
                        postgrest.rpc("sync_push_profile_settings_blob", upgradeParams)
                    }
                    skipNextPushSignature = buildSettingsSignature(
                        upgradedSettings["features"]?.jsonObject ?: JsonObject(emptyMap())
                    )
                    Log.d(TAG, "Upgraded IPTV sync blob to schema $IPTV_SYNC_SCHEMA_VERSION")
                } else {
                    skipNextPushSignature = remoteSignature
                }
                Log.d(TAG, "Applied remote profile settings blob for profile $profileId")
                Result.success(true)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to pull profile settings blob", e)
                Result.failure(e)
            }
        }
    }

    fun requestForegroundPull(force: Boolean = false) {
        if (!authManager.isAuthenticated) return

        val now = SystemClock.elapsedRealtime()
        if (!force && foregroundPullJob?.isActive == true) return
        if (!force && now - lastForegroundPullAtMs < FOREGROUND_PULL_MIN_INTERVAL_MS) return

        foregroundPullJob = scope.launch {
            if (!force) {
                delay(FOREGROUND_PULL_DELAY_MS)
            }
            if (!authManager.isAuthenticated) return@launch

            lastForegroundPullAtMs = SystemClock.elapsedRealtime()
            pullCurrentProfileFromRemote()
        }
    }

    private suspend fun exportSettingsBlob(profileId: Int): JsonObject {
        val features = buildJsonObject {
            syncedFeatures.forEach { feature ->
                val prefs = profileDataStoreFactory.get(profileId, feature).data.first()
                val serialized = buildJsonObject {
                    prefs.asMap().forEach { (key, rawValue) ->
                        if (feature == "layout_settings" && key.name in catalogKeysExcludedFromBlob) return@forEach
                        if (feature == "layout_settings" && key.name in localOnlyLayoutKeys) return@forEach
                        if (feature == "layout_settings" && key.name == "search_discover_enabled") return@forEach
                        val encoded = encodePreferenceValue(rawValue) ?: return@forEach
                        put(key.name, encoded)
                    }
                }
                put(feature, serialized)
            }
            put(
                IPTV_PROVIDERS_FEATURE,
                buildIptvProvidersJson(providerDao.getAllSync(), favoriteDao.getAllGlobalSync())
            )
        }

        return buildJsonObject {
            put("version", 1)
            put("features", features)
        }
    }

    private suspend fun importSettingsBlob(profileId: Int, featuresJson: JsonObject) {
        applyingRemoteBlob = true
        try {
            val providerIdMap = importIptvProvidersJson(featuresJson[IPTV_PROVIDERS_FEATURE]?.jsonObject)
            syncedFeatures.forEach { feature ->
                val rawFeatureJson = featuresJson[feature]?.jsonObject ?: return@forEach
                val featureJson = if (feature == "iptv_settings") {
                    remapIptvPreferenceProviderIds(rawFeatureJson, providerIdMap)
                } else {
                    rawFeatureJson
                }
                profileDataStoreFactory.get(profileId, feature).edit { mutablePrefs ->
                    val preservedEntries = if (feature == "layout_settings") {
                        val entries = mutableMapOf<Preferences.Key<*>, Any>()
                        catalogKeysExcludedFromBlob.forEach { keyName ->
                            val strKey = stringPreferencesKey(keyName)
                            runCatching { mutablePrefs[strKey] }.getOrNull()?.let { entries[strKey] = it }
                            val boolKey = booleanPreferencesKey(keyName)
                            runCatching { mutablePrefs[boolKey] }.getOrNull()?.let { entries[boolKey] = it }
                        }
                        localOnlyLayoutKeys.forEach { keyName ->
                            val strKey = stringPreferencesKey(keyName)
                            runCatching { mutablePrefs[strKey] }.getOrNull()?.let { entries[strKey] = it }
                        }
                        entries
                    } else {
                        emptyMap()
                    }
                    val priorDiscoverLocation = if (feature == "layout_settings") {
                        mutablePrefs[stringPreferencesKey("discover_location")]
                    } else null
                    val priorLastNonOff = if (feature == "layout_settings") {
                        mutablePrefs[stringPreferencesKey("last_non_off_discover_location")]?.let {
                            runCatching { DiscoverLocation.valueOf(it) }.getOrNull()
                        }?.takeIf { it != DiscoverLocation.OFF }
                    } else null

                    mutablePrefs.clear()
                    val hasWellFormedNewDiscoverKey = feature == "layout_settings" &&
                        extractDiscoverLocationString(featureJson) != null
                    featureJson.forEach { (keyName, encodedValue) ->
                        if (feature == "layout_settings" && keyName in catalogKeysExcludedFromBlob) return@forEach
                        if (feature == "layout_settings" && keyName in localOnlyLayoutKeys) return@forEach
                        if (feature == "layout_settings" && keyName == "search_discover_enabled") {
                            if (!hasWellFormedNewDiscoverKey) {
                                val legacy = (encodedValue as? JsonObject)
                                    ?.get("value")?.jsonPrimitive?.contentOrNull
                                    ?.toBooleanStrictOrNull()
                                if (legacy != null) {
                                    val translated = DiscoverLocation.fromLegacySearchDiscoverEnabled(legacy)
                                    val priorLocation = priorDiscoverLocation?.let {
                                        runCatching { DiscoverLocation.valueOf(it) }.getOrNull()
                                    }
                                    val priorIsValidNonOff = priorLocation != null && priorLocation != DiscoverLocation.OFF
                                    when {
                                        translated == DiscoverLocation.OFF ->
                                            mutablePrefs[stringPreferencesKey("discover_location")] = translated.name
                                        priorIsValidNonOff -> {}
                                        priorLastNonOff != null ->
                                            mutablePrefs[stringPreferencesKey("discover_location")] = priorLastNonOff.name
                                        else ->
                                            mutablePrefs[stringPreferencesKey("discover_location")] = translated.name
                                    }
                                }
                            }
                            return@forEach
                        }
                        if (feature == "layout_settings" && keyName == "discover_location" && !hasWellFormedNewDiscoverKey) return@forEach
                        applyEncodedPreference(mutablePrefs, keyName, encodedValue)
                    }

                    @Suppress("UNCHECKED_CAST")
                    preservedEntries.forEach { (key, value) ->
                        when (value) {
                            is String -> mutablePrefs[key as Preferences.Key<String>] = value
                            is Boolean -> mutablePrefs[key as Preferences.Key<Boolean>] = value
                            is Int -> mutablePrefs[key as Preferences.Key<Int>] = value
                            is Long -> mutablePrefs[key as Preferences.Key<Long>] = value
                            is Float -> mutablePrefs[key as Preferences.Key<Float>] = value
                            is Double -> mutablePrefs[key as Preferences.Key<Double>] = value
                        }
                    }
                    if (feature == "layout_settings" && priorDiscoverLocation != null) {
                        val discoverKey = stringPreferencesKey("discover_location")
                        if (mutablePrefs[discoverKey] == null) {
                            mutablePrefs[discoverKey] = priorDiscoverLocation
                        }
                    }
                    if (feature == "layout_settings") {
                        val finalDiscover = mutablePrefs[stringPreferencesKey("discover_location")]?.let {
                            runCatching { DiscoverLocation.valueOf(it) }.getOrNull()
                        }
                        if (finalDiscover != null && finalDiscover != DiscoverLocation.OFF) {
                            mutablePrefs[stringPreferencesKey("last_non_off_discover_location")] =
                                finalDiscover.name
                        }
                    }
                }
            }
            importIptvFavoritesJson(
                featuresJson[IPTV_PROVIDERS_FEATURE]?.jsonObject,
                providerIdMap,
                refreshImportedIptvProviderCatalogs(providerIdMap.values)
            )
        } finally {
            applyingRemoteBlob = false
        }
    }

    private fun observeLocalSettingsChangesAndSync() {
        scope.launch {
            profileManager.activeProfileId
                .flatMapLatest { profileId ->
                    val featureFlows = syncedFeatures.map { feature ->
                        profileDataStoreFactory.get(profileId, feature).data
                            .map { prefs ->
                                "$feature={${buildFeatureSignature(prefs, feature)}}"
                            }
                    }
                    val iptvProvidersFlow = combine(
                        providerDao.getAll(),
                        favoriteDao.observeAllGlobal()
                    ) { providers, favorites ->
                            "$IPTV_PROVIDERS_FEATURE={${buildFeatureSignature(buildIptvProvidersJson(providers, favorites))}}"
                        }

                    combine(featureFlows + iptvProvidersFlow) { signatures ->
                        signatures.joinToString(separator = "||")
                    }
                }
                .drop(1)
                .distinctUntilChanged()
                .debounce(SETTINGS_PUSH_DEBOUNCE_MS)
                .collect { signature ->
                    if (!authManager.isAuthenticated) return@collect
                    if (applyingRemoteBlob) return@collect
                    if (profileDataStoreFactory.corruptedFileNames.isNotEmpty()) {
                        Log.w(TAG, "DataStore corruption detected (${profileDataStoreFactory.corruptedFileNames}) Ã¢â‚¬â€ pulling from remote instead of pushing")
                        profileDataStoreFactory.corruptedFileNames.clear()
                        pullCurrentProfileFromRemote()
                        return@collect
                    }
                    if (signature == skipNextPushSignature) {
                        skipNextPushSignature = null
                        return@collect
                    }
                    pushCurrentProfileToRemote()
                }
        }
    }

    private suspend fun buildSettingsSignature(profileId: Int): String {
        val signatures = ArrayList<String>(syncedFeatures.size)
        syncedFeatures.forEach { feature ->
            val prefs = profileDataStoreFactory.get(profileId, feature).data.first()
            signatures += "$feature={${buildFeatureSignature(prefs, feature)}}"
        }
        signatures += "$IPTV_PROVIDERS_FEATURE={${buildFeatureSignature(buildIptvProvidersJson(providerDao.getAllSync(), favoriteDao.getAllGlobalSync()))}}"
        return signatures.joinToString(separator = "||")
    }

    private fun extractDiscoverLocationString(featureJson: JsonObject): String? {
        val encoded = featureJson["discover_location"] as? JsonObject ?: return null
        val type = encoded["type"]?.jsonPrimitive?.contentOrNull
        if (type != "string") return null
        return encoded["value"]?.jsonPrimitive?.contentOrNull
    }

    private fun normalizeLayoutSettingsForSignature(featureJson: JsonObject): JsonObject {
        val hasLegacy = "search_discover_enabled" in featureJson
        val hasNewKey = "discover_location" in featureJson
        val hasLocalOnly = featureJson.keys.any { it in localOnlyLayoutKeys }
        if (!hasLegacy && !hasNewKey && !hasLocalOnly) return featureJson
        val newDiscoverString = extractDiscoverLocationString(featureJson)
        if (!hasLegacy && newDiscoverString != null && !hasLocalOnly) return featureJson
        return buildJsonObject {
            featureJson.forEach { (keyName, encodedValue) ->
                when {
                    keyName == "search_discover_enabled" -> return@forEach
                    keyName == "discover_location" && newDiscoverString == null -> return@forEach
                    keyName in localOnlyLayoutKeys -> return@forEach
                    else -> put(keyName, encodedValue)
                }
            }
            if (newDiscoverString == null && hasLegacy) {
                val legacy = (featureJson["search_discover_enabled"] as? JsonObject)
                    ?.get("value")?.jsonPrimitive?.contentOrNull
                    ?.toBooleanStrictOrNull()
                if (legacy != null) {
                    put(
                        "discover_location",
                        buildJsonObject {
                            put("type", "string")
                            put(
                                "value",
                                DiscoverLocation.fromLegacySearchDiscoverEnabled(legacy).name
                            )
                        }
                    )
                }
            }
        }
    }

    private fun buildSettingsSignature(featuresJson: JsonObject): String {
        return syncedFeatures.joinToString(separator = "||") { feature ->
            val featureJson = featuresJson[feature]?.jsonObject ?: JsonObject(emptyMap())
            val normalized = if (feature == "layout_settings") {
                normalizeLayoutSettingsForSignature(featureJson)
            } else {
                featureJson
            }
            "$feature={${buildFeatureSignature(normalized)}}"
        } + "||$IPTV_PROVIDERS_FEATURE={${buildFeatureSignature(featuresJson[IPTV_PROVIDERS_FEATURE]?.jsonObject ?: JsonObject(emptyMap()))}}"
    }

    private fun buildFeatureSignature(prefs: Preferences, feature: String = ""): String {
        return prefs.asMap()
            .entries
            .mapNotNull { (key, rawValue) ->
                if (feature == "layout_settings" && key.name in catalogKeysExcludedFromBlob) return@mapNotNull null
                if (feature == "layout_settings" && key.name in localOnlyLayoutKeys) return@mapNotNull null
                encodePreferenceValue(rawValue)?.let { encoded ->
                    key.name to encoded.toString()
                }
            }
            .sortedBy { it.first }
            .joinToString(separator = "|") { (key, value) -> "$key=$value" }
    }

    private fun buildFeatureSignature(featureJson: JsonObject): String {
        return featureJson.entries
            .sortedBy { it.key }
            .joinToString(separator = "|") { (key, value) -> "$key=$value" }
    }
    private suspend fun buildIptvProvidersJson(
        providers: List<ProviderEntity>,
        favorites: List<FavoriteEntity>
    ): JsonObject {
        val syncedFavorites = favorites.mapNotNull { favorite ->
            val remoteContentId = when (favorite.contentType) {
                ContentType.MOVIE -> movieDao.getById(favorite.contentId)?.streamId?.toString()
                ContentType.SERIES -> seriesDao.getById(favorite.contentId)?.let { series ->
                    series.providerSeriesId?.takeIf { it.isNotBlank() }
                        ?.let { "provider:$it" }
                        ?: "series:${series.seriesId}"
                }
                else -> null
            } ?: return@mapNotNull null
            buildJsonObject {
                put("source_provider_id", favorite.providerId)
                put("content_type", favorite.contentType.name)
                put("remote_content_id", remoteContentId)
                put("position", favorite.position)
                put("added_at", favorite.addedAt)
            }
        }
        return buildJsonObject {
            put("schema_version", IPTV_SYNC_SCHEMA_VERSION)
            put("providers", kotlinx.serialization.json.JsonArray(providers.map { p ->
                buildJsonObject {
                    put("source_id", p.id)
                    put("name", p.name)
                    put("type", p.type.name)
                    put("server_url", p.serverUrl)
                    put("username", p.username)
                    put("password", p.password)
                    put("m3u_url", p.m3uUrl)
                    put("epg_url", p.epgUrl)
                    put("http_user_agent", p.httpUserAgent)
                    put("http_headers", p.httpHeaders)
                    put("stalker_mac_address", p.stalkerMacAddress)
                    put("stalker_device_profile", p.stalkerDeviceProfile)
                    put("stalker_device_timezone", p.stalkerDeviceTimezone)
                    put("stalker_device_locale", p.stalkerDeviceLocale)
                    put("stalker_serial_number", p.stalkerSerialNumber)
                    put("stalker_device_id", p.stalkerDeviceId)
                    put("stalker_device_id2", p.stalkerDeviceId2)
                    put("stalker_signature", p.stalkerSignature)
                    put("stalker_auth_mode", p.stalkerAuthMode.name)
                    put("stalker_portal_profile", p.stalkerPortalProfile.name)
                    put("stalker_portal_fingerprint", p.stalkerPortalFingerprint.name)
                    put("stalker_mag_preset", p.stalkerMagPreset.name)
                    put("stalker_last_bootstrap_recipe", p.stalkerLastBootstrapRecipe.name)
                    put("stalker_endpoint_preference", p.stalkerEndpointPreference.name)
                    put("stalker_cookie_mode", p.stalkerCookieMode.name)
                    put("stalker_playback_backend_hint", p.stalkerPlaybackBackendHint.name)
                    p.stalkerLastPlaybackMode?.let { put("stalker_last_playback_mode", it) }
                    put("stalker_credentials_required", p.stalkerCredentialsRequired)
                    put("stalker_mac_required", p.stalkerMacRequired)
                    put("stalker_uses_temporary_links", p.stalkerUsesTemporaryLinks)
                    put("stalker_module_restricted", p.stalkerModuleRestricted)
                    put("stalker_strict_fingerprint_required", p.stalkerStrictFingerprintRequired)
                    put("stalker_recipe_fallback_used", p.stalkerRecipeFallbackUsed)
                    put("stalker_recipe_rediscovery_attempts", p.stalkerRecipeRediscoveryAttempts)
                    put("max_connections", p.maxConnections)
                    p.expirationDate?.let { put("expiration_date", it) }
                    p.apiVersion?.let { put("api_version", it) }
                    put("allowed_output_formats_json", p.allowedOutputFormatsJson)
                    put("epg_sync_mode", p.epgSyncMode.name)
                    put("xtream_fast_sync_enabled", p.xtreamFastSyncEnabled)
                    put("xtream_live_sync_mode", p.xtreamLiveSyncMode.name)
                    put("m3u_vod_classification_enabled", p.m3uVodClassificationEnabled)
                    put("is_active", p.isActive)
                }
            }))
            put("favorites", JsonArray(syncedFavorites))
        }
    }

    private suspend fun importIptvProvidersJson(featureJson: JsonObject?): Map<Long, Long> {
        if (featureJson == null) return emptyMap()
        val providersArray = featureJson["providers"]?.jsonArray ?: return emptyMap()
        val providerIdMap = mutableMapOf<Long, Long>()
        val existing = providerDao.getAllSync().toMutableList()
        providersArray.forEach { element ->
            val obj = element.jsonObject
            val sourceId = obj.longValue("source_id", 0L)
            val name = obj.stringValue("name")
            val typeStr = obj.stringValue("type")
            val serverUrl = obj.stringValue("server_url")
            val username = obj.stringValue("username")
            val stalkerMacAddress = obj.stringValue("stalker_mac_address")
            if (name.isBlank()) return@forEach
            val alreadyExists = existing.firstOrNull {
                it.serverUrl == serverUrl &&
                    it.username == username &&
                    it.stalkerMacAddress == stalkerMacAddress
            }
            val entity = buildSyncedIptvProviderEntity(obj, alreadyExists) ?: return@forEach
            if (alreadyExists != null) {
                if (sourceId > 0L) providerIdMap[sourceId] = alreadyExists.id
                if (entity != alreadyExists) {
                    runCatching { providerDao.update(entity) }
                        .onFailure { error ->
                            Log.w(TAG, "Failed to update synced IPTV provider ${alreadyExists.id}", error)
                        }
                }
                return@forEach
            }
            runCatching { providerDao.insert(entity) }.getOrNull()?.let { insertedId ->
                if (sourceId > 0L) providerIdMap[sourceId] = insertedId
                existing += entity.copy(id = insertedId)
            }
        }
        return providerIdMap
    }

    private suspend fun importIptvFavoritesJson(
        featureJson: JsonObject?,
        providerIdMap: Map<Long, Long>,
        refreshedProviderIds: Set<Long> = emptySet()
    ) {
        val favorites = featureJson?.get("favorites")?.jsonArray ?: return
        val localProviderIds = providerIdMap.values.distinct()
        if (localProviderIds.isNotEmpty()) {
            favoriteDao.deleteSyncedGlobalVodFavorites(localProviderIds)
        }
        var unresolved = restoreIptvFavorites(favorites, providerIdMap)
        if (unresolved.isEmpty()) return

        unresolved.mapNotNull { element ->
            val sourceProviderId = element.jsonObject.longValue("source_provider_id", 0L)
            providerIdMap[sourceProviderId]
        }.distinct().filterNot(refreshedProviderIds::contains).forEach { providerId ->
            providerRepository.refreshProviderData(providerId, force = true)
        }
        unresolved = restoreIptvFavorites(JsonArray(unresolved), providerIdMap)
        if (unresolved.isNotEmpty()) {
            Log.w(TAG, "Could not restore ${unresolved.size} IPTV favorite(s) after provider refresh")
        }
    }

    private suspend fun restoreIptvFavorites(
        favorites: JsonArray,
        providerIdMap: Map<Long, Long>
    ): List<JsonElement> {
        val unresolved = mutableListOf<JsonElement>()
        favorites.forEach { element ->
            val obj = element.jsonObject
            val sourceProviderId = obj.longValue("source_provider_id", 0L)
            val providerId = providerIdMap[sourceProviderId]
            val contentType = runCatching {
                ContentType.valueOf(obj.stringValue("content_type"))
            }.getOrNull()
            if (contentType == null) {
                unresolved += element
                return@forEach
            }
            val remoteContentId = obj.stringValue("remote_content_id")
            val localContentId = when (contentType) {
                ContentType.MOVIE -> remoteContentId.toLongOrNull()
                    ?.let { movieDao.getByStreamId(providerId ?: 0L, it)?.id }
                ContentType.SERIES -> when {
                    remoteContentId.startsWith("provider:") -> seriesDao.getByProviderSeriesId(
                        providerId ?: 0L,
                        remoteContentId.removePrefix("provider:")
                    )?.id
                    remoteContentId.startsWith("series:") -> remoteContentId.removePrefix("series:")
                        .toLongOrNull()
                        ?.let { seriesDao.getBySeriesId(providerId ?: 0L, it)?.id }
                    else -> null
                }
                else -> null
            }
            if (providerId == null || localContentId == null) {
                unresolved += element
                return@forEach
            }
            favoriteDao.insert(
                FavoriteEntity(
                    providerId = providerId,
                    contentId = localContentId,
                    contentType = contentType,
                    position = obj.intValue("position", 0).coerceAtLeast(0),
                    addedAt = obj.longValue("added_at", System.currentTimeMillis())
                )
            )
        }
        return unresolved
    }

    private fun buildSyncedIptvProviderEntity(
        obj: JsonObject,
        existing: ProviderEntity?
    ): ProviderEntity? {
        val type = runCatching { ProviderType.valueOf(obj.stringValue("type")) }.getOrNull()
            ?: existing?.type
            ?: return null
        val name = obj.optionalString("name") ?: existing?.name.orEmpty()
        if (name.isBlank()) return null
        return ProviderEntity(
            id = existing?.id ?: 0L,
            name = name,
            type = type,
            serverUrl = obj.optionalString("server_url") ?: existing?.serverUrl.orEmpty(),
            username = obj.optionalString("username") ?: existing?.username.orEmpty(),
            password = obj.optionalString("password") ?: existing?.password.orEmpty(),
            m3uUrl = obj.optionalString("m3u_url") ?: existing?.m3uUrl.orEmpty(),
            epgUrl = obj.optionalString("epg_url") ?: existing?.epgUrl.orEmpty(),
            httpUserAgent = obj.optionalString("http_user_agent") ?: existing?.httpUserAgent.orEmpty(),
            httpHeaders = obj.optionalString("http_headers") ?: existing?.httpHeaders.orEmpty(),
            stalkerMacAddress = obj.optionalString("stalker_mac_address") ?: existing?.stalkerMacAddress.orEmpty(),
            stalkerDeviceProfile = obj.optionalString("stalker_device_profile") ?: existing?.stalkerDeviceProfile.orEmpty(),
            stalkerDeviceTimezone = obj.optionalString("stalker_device_timezone") ?: existing?.stalkerDeviceTimezone.orEmpty(),
            stalkerDeviceLocale = obj.optionalString("stalker_device_locale") ?: existing?.stalkerDeviceLocale.orEmpty(),
            stalkerSerialNumber = obj.optionalString("stalker_serial_number") ?: existing?.stalkerSerialNumber.orEmpty(),
            stalkerDeviceId = obj.optionalString("stalker_device_id") ?: existing?.stalkerDeviceId.orEmpty(),
            stalkerDeviceId2 = obj.optionalString("stalker_device_id2") ?: existing?.stalkerDeviceId2.orEmpty(),
            stalkerSignature = obj.optionalString("stalker_signature") ?: existing?.stalkerSignature.orEmpty(),
            stalkerAuthMode = obj.enumValue("stalker_auth_mode", existing?.stalkerAuthMode ?: com.streamvault.domain.model.StalkerAuthMode.AUTO),
            stalkerPortalProfile = obj.enumValue("stalker_portal_profile", existing?.stalkerPortalProfile ?: com.streamvault.domain.model.StalkerPortalProfile.MAG_BASIC),
            stalkerPortalFingerprint = obj.enumValue("stalker_portal_fingerprint", existing?.stalkerPortalFingerprint ?: com.streamvault.domain.model.StalkerPortalFingerprint.BASIC_MAC),
            stalkerMagPreset = obj.enumValue("stalker_mag_preset", existing?.stalkerMagPreset ?: com.streamvault.domain.model.StalkerMagPreset.GENERIC_SAFE),
            stalkerLastBootstrapRecipe = obj.enumValue("stalker_last_bootstrap_recipe", existing?.stalkerLastBootstrapRecipe ?: com.streamvault.domain.model.StalkerBootstrapRecipe.GENERIC_SAFE),
            stalkerEndpointPreference = obj.enumValue("stalker_endpoint_preference", existing?.stalkerEndpointPreference ?: com.streamvault.domain.model.StalkerEndpointPreference.AUTO),
            stalkerCookieMode = obj.enumValue("stalker_cookie_mode", existing?.stalkerCookieMode ?: com.streamvault.domain.model.StalkerCookieMode.NONE),
            stalkerPlaybackBackendHint = obj.enumValue("stalker_playback_backend_hint", existing?.stalkerPlaybackBackendHint ?: com.streamvault.domain.model.StalkerPlaybackBackendHint.AUTO),
            stalkerLastPlaybackMode = obj.optionalString("stalker_last_playback_mode") ?: existing?.stalkerLastPlaybackMode,
            stalkerCredentialsRequired = obj.booleanValue("stalker_credentials_required", existing?.stalkerCredentialsRequired ?: false),
            stalkerMacRequired = obj.booleanValue("stalker_mac_required", existing?.stalkerMacRequired ?: true),
            stalkerUsesTemporaryLinks = obj.booleanValue("stalker_uses_temporary_links", existing?.stalkerUsesTemporaryLinks ?: false),
            stalkerModuleRestricted = obj.booleanValue("stalker_module_restricted", existing?.stalkerModuleRestricted ?: false),
            stalkerStrictFingerprintRequired = obj.booleanValue("stalker_strict_fingerprint_required", existing?.stalkerStrictFingerprintRequired ?: false),
            stalkerRecipeFallbackUsed = obj.booleanValue("stalker_recipe_fallback_used", existing?.stalkerRecipeFallbackUsed ?: false),
            stalkerRecipeRediscoveryAttempts = obj.intValue("stalker_recipe_rediscovery_attempts", existing?.stalkerRecipeRediscoveryAttempts ?: 0),
            isActive = obj.booleanValue("is_active", existing?.isActive ?: false),
            maxConnections = obj.intValue("max_connections", existing?.maxConnections ?: 1),
            expirationDate = obj.longValueOrNull("expiration_date") ?: existing?.expirationDate,
            apiVersion = obj.optionalString("api_version") ?: existing?.apiVersion,
            allowedOutputFormatsJson = obj.optionalString("allowed_output_formats_json") ?: existing?.allowedOutputFormatsJson ?: "[]",
            epgSyncMode = obj.enumValue("epg_sync_mode", existing?.epgSyncMode ?: com.streamvault.domain.model.ProviderEpgSyncMode.UPFRONT),
            xtreamFastSyncEnabled = obj.booleanValue("xtream_fast_sync_enabled", existing?.xtreamFastSyncEnabled ?: false),
            xtreamLiveSyncMode = obj.enumValue("xtream_live_sync_mode", existing?.xtreamLiveSyncMode ?: com.streamvault.domain.model.ProviderXtreamLiveSyncMode.AUTO),
            m3uVodClassificationEnabled = obj.booleanValue("m3u_vod_classification_enabled", existing?.m3uVodClassificationEnabled ?: false),
            status = existing?.status ?: com.streamvault.domain.model.ProviderStatus.UNKNOWN,
            lastSyncedAt = existing?.lastSyncedAt ?: 0L,
            createdAt = existing?.createdAt ?: System.currentTimeMillis()
        )
    }

    private suspend fun refreshImportedIptvProviderCatalogs(providerIds: Collection<Long>): Set<Long> {
        val refreshed = mutableSetOf<Long>()
        providerIds.distinct().forEach { providerId ->
            val result = runCatching {
                providerRepository.refreshProviderData(providerId, force = true)
            }.onFailure { error ->
                Log.w(TAG, "Failed to refresh synced IPTV provider $providerId", error)
            }.getOrNull()
            if (result?.isSuccess == true) {
                refreshed += providerId
            } else if (result != null) {
                Log.w(TAG, "Synced IPTV provider $providerId refresh returned ${result::class.simpleName}")
            }
        }
        return refreshed
    }

    private fun decryptProviderPassword(password: String): String = password
    private suspend fun encryptSyncedProviderPassword(password: String): String = password
    private fun decryptSyncedProviderPassword(password: String): String = password

    private fun JsonObject.stringValue(key: String): String =
        this[key]?.jsonPrimitive?.contentOrNull.orEmpty()

    private fun JsonObject.optionalString(key: String): String? =
        this[key]?.jsonPrimitive?.contentOrNull

    private fun JsonObject.booleanValue(key: String, default: Boolean): Boolean =
        this[key]?.jsonPrimitive?.booleanOrNull ?: default

    private fun JsonObject.intValue(key: String, default: Int): Int =
        this[key]?.jsonPrimitive?.intOrNull ?: default

    private fun JsonObject.longValue(key: String, default: Long): Long =
        this[key]?.jsonPrimitive?.longOrNull ?: default

    private fun JsonObject.longValueOrNull(key: String): Long? =
        this[key]?.jsonPrimitive?.longOrNull

    private inline fun <reified T : Enum<T>> JsonObject.enumValue(key: String, default: T): T =
        stringValue(key).takeIf { it.isNotBlank() }?.let { raw ->
            runCatching { enumValueOf<T>(raw) }.getOrNull()
        } ?: default

    private fun encodePreferenceValue(rawValue: Any?): JsonObject? {
        return when (rawValue) {
            is String -> buildJsonObject {
                put("type", "string")
                put("value", rawValue)
            }
            is Boolean -> buildJsonObject {
                put("type", "boolean")
                put("value", rawValue)
            }
            is Int -> buildJsonObject {
                put("type", "int")
                put("value", rawValue)
            }
            is Long -> buildJsonObject {
                put("type", "long")
                put("value", rawValue)
            }
            is Float -> buildJsonObject {
                put("type", "float")
                put("value", rawValue)
            }
            is Double -> buildJsonObject {
                put("type", "double")
                put("value", rawValue)
            }
            is Set<*> -> {
                val allStrings = rawValue.all { it is String }
                if (!allStrings) return null
                buildJsonObject {
                    put("type", "string_set")
                    val values = rawValue.map { it as String }.sorted()
                    put("value", JsonArray(values.map { JsonPrimitive(it) }))
                }
            }
            else -> null
        }
    }

    private fun applyEncodedPreference(
        mutablePrefs: androidx.datastore.preferences.core.MutablePreferences,
        keyName: String,
        encodedValue: JsonElement
    ) {
        val obj = encodedValue as? JsonObject ?: return
        val type = obj["type"]?.jsonPrimitive?.contentOrNull ?: return
        val value = obj["value"] ?: JsonNull

        when (type) {
            "string" -> {
                val parsed = value.jsonPrimitive.contentOrNull ?: return
                mutablePrefs[stringPreferencesKey(keyName)] = parsed
            }
            "boolean" -> {
                val parsed = value.jsonPrimitive.contentOrNull?.toBooleanStrictOrNull() ?: return
                mutablePrefs[booleanPreferencesKey(keyName)] = parsed
            }
            "int" -> {
                val parsed = value.jsonPrimitive.intOrNull ?: return
                mutablePrefs[intPreferencesKey(keyName)] = parsed
            }
            "long" -> {
                val parsed = value.jsonPrimitive.longOrNull ?: return
                mutablePrefs[longPreferencesKey(keyName)] = parsed
            }
            "float" -> {
                val parsed = value.jsonPrimitive.floatOrNull ?: return
                mutablePrefs[floatPreferencesKey(keyName)] = parsed
            }
            "double" -> {
                val parsed = value.jsonPrimitive.doubleOrNull ?: return
                mutablePrefs[doublePreferencesKey(keyName)] = parsed
            }
            "string_set" -> {
                val parsed = value.jsonArray.mapNotNull { it.jsonPrimitive.contentOrNull }.toSet()
                mutablePrefs[stringSetPreferencesKey(keyName)] = parsed
            }
        }
    }
}


