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
import com.streamvault.data.local.dao.ChannelDao
import com.streamvault.data.local.dao.FavoriteDao
import com.streamvault.data.local.dao.MovieDao
import com.streamvault.data.local.dao.ProviderDao
import com.streamvault.data.local.dao.SeriesDao
import com.streamvault.data.local.dao.VirtualGroupDao
import com.streamvault.data.local.entity.FavoriteEntity
import com.streamvault.data.local.entity.ProviderEntity
import com.streamvault.data.local.entity.VirtualGroupEntity
import com.streamvault.domain.model.ContentType
import com.streamvault.domain.model.ProviderType
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
private const val IPTV_FAVORITES_FEATURE = "iptv_favorites"
private const val IPTV_SYNC_SECRET_PREFIX = "syncenc:v1:"
private const val IPTV_SYNC_KEY_CONTEXT = "nuvio:iptv-provider-sync:"
private const val IPTV_FAVORITES_PENDING_MAX_MS = 10 * 60 * 1000L

@Singleton
class ProfileSettingsSyncService @Inject constructor(
    private val authManager: AuthManager,
    private val postgrest: Postgrest,
    private val profileManager: ProfileManager,
    private val profileDataStoreFactory: ProfileDataStoreFactory,
    private val providerDao: ProviderDao,
    private val favoriteDao: FavoriteDao,
    private val virtualGroupDao: VirtualGroupDao,
    private val channelDao: ChannelDao,
    private val movieDao: MovieDao,
    private val seriesDao: SeriesDao,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val syncMutex = Mutex()

    @Volatile
    private var applyingRemoteBlob: Boolean = false

    @Volatile
    private var skipNextPushSignature: String? = null
    @Volatile
    private var pendingIptvFavoritesUntilMs: Long = 0L
    private var foregroundPullJob: Job? = null
    private var iptvFavoritesRetryJob: Job? = null
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
                val remoteSignature = buildSettingsSignature(featuresJson)
                val localSignature = buildSettingsSignature(profileId)
                if (remoteSignature == localSignature) {
                    Log.d(TAG, "Remote profile settings already match local for profile $profileId")
                    return@withLock Result.success(false)
                }

                importSettingsBlob(profileId, featuresJson)
                skipNextPushSignature = buildSettingsSignature(profileId)
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
            put(IPTV_PROVIDERS_FEATURE, buildIptvProvidersJson(providerDao.getAllSync()))
            put(IPTV_FAVORITES_FEATURE, buildIptvFavoritesJson())
        }

        return buildJsonObject {
            put("version", 1)
            put("features", features)
        }
    }

    private suspend fun importSettingsBlob(profileId: Int, featuresJson: JsonObject) {
        applyingRemoteBlob = true
        try {
            val iptvProviderIdMap = importIptvProvidersJson(featuresJson[IPTV_PROVIDERS_FEATURE]?.jsonObject)
            syncedFeatures.forEach { feature ->
                val rawFeatureJson = featuresJson[feature]?.jsonObject ?: return@forEach
                val featureJson = if (feature == "iptv_settings") {
                    remapIptvSettingsProviderIds(rawFeatureJson, iptvProviderIdMap)
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
            importAndRetryIptvFavoritesJson(
                featureJson = featuresJson[IPTV_FAVORITES_FEATURE]?.jsonObject,
                providerIdMap = iptvProviderIdMap
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
                    val iptvProvidersFlow = providerDao.getAll()
                        .map { providers ->
                            "$IPTV_PROVIDERS_FEATURE={${buildFeatureSignature(buildIptvProvidersJson(providers))}}"
                        }
                    val iptvFavoritesFlow = combine(
                        virtualGroupDao.observeAllForSync(),
                        favoriteDao.observeAllForSync()
                    ) { groups, favorites ->
                        "$IPTV_FAVORITES_FEATURE={groups=${groups.buildVirtualGroupSignature()}|favorites=${favorites.buildFavoriteSignature()}}"
                    }

                    combine(featureFlows + iptvProvidersFlow + iptvFavoritesFlow) { signatures ->
                        signatures.joinToString(separator = "||")
                    }
                }
                .drop(1)
                .distinctUntilChanged()
                .debounce(SETTINGS_PUSH_DEBOUNCE_MS)
                .collect { signature ->
                    if (!authManager.isAuthenticated) return@collect
                    if (applyingRemoteBlob) return@collect
                    if (isWaitingForIptvFavoriteResolution()) {
                        Log.d(TAG, "Skipping settings push while IPTV favorites are waiting for catalog resolution")
                        return@collect
                    }
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
        signatures += "$IPTV_PROVIDERS_FEATURE={${buildFeatureSignature(buildIptvProvidersJson(providerDao.getAllSync()))}}"
        signatures += "$IPTV_FAVORITES_FEATURE={${buildFeatureSignature(buildIptvFavoritesJson())}}"
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
        } +
            "||$IPTV_PROVIDERS_FEATURE={${buildFeatureSignature(featuresJson[IPTV_PROVIDERS_FEATURE]?.jsonObject ?: JsonObject(emptyMap()))}}" +
            "||$IPTV_FAVORITES_FEATURE={${buildFeatureSignature(featuresJson[IPTV_FAVORITES_FEATURE]?.jsonObject ?: JsonObject(emptyMap()))}}"
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

    private suspend fun buildIptvFavoritesJson(): JsonObject {
        val groups = virtualGroupDao.getAllSync()
        val favorites = favoriteDao.getAllSync()
        val providerIds = favorites.map { it.providerId }.toSet() + groups.map { it.providerId }.toSet()

        val liveRemoteByProvider = providerIds.associateWith { providerId ->
            channelDao.getIdMappings(providerId).associate { it.id to it.remoteId.toString() }
        }
        val movieRemoteByProvider = providerIds.associateWith { providerId ->
            movieDao.getIdMappings(providerId).associate { it.id to it.remoteId.toString() }
        }
        val seriesRemoteByProvider = providerIds.associateWith { providerId ->
            seriesDao.getIdMappings(providerId).associate { it.id to it.remoteId }
        }

        return buildJsonObject {
            put("groups", JsonArray(groups.map { group ->
                buildJsonObject {
                    put("provider_id", group.providerId)
                    put("group_id", group.id)
                    put("content_type", group.contentType.name)
                    put("name", group.name)
                    group.iconEmoji?.let { put("icon_emoji", it) }
                    put("position", group.position)
                    put("created_at", group.createdAt)
                }
            }))
            put("favorites", JsonArray(favorites.mapNotNull { favorite ->
                val remoteContentId = when (favorite.contentType) {
                    ContentType.LIVE -> liveRemoteByProvider[favorite.providerId]?.get(favorite.contentId)
                    ContentType.MOVIE -> movieRemoteByProvider[favorite.providerId]?.get(favorite.contentId)
                    ContentType.SERIES -> seriesRemoteByProvider[favorite.providerId]?.get(favorite.contentId)
                    ContentType.SERIES_EPISODE -> null
                } ?: return@mapNotNull null

                buildJsonObject {
                    put("provider_id", favorite.providerId)
                    put("content_type", favorite.contentType.name)
                    put("content_remote_id", remoteContentId)
                    favorite.groupId?.let { put("group_id", it) }
                    put("position", favorite.position)
                    put("added_at", favorite.addedAt)
                }
            }))
        }
    }

    private fun isWaitingForIptvFavoriteResolution(): Boolean {
        val until = pendingIptvFavoritesUntilMs
        return until > 0L && SystemClock.elapsedRealtime() < until
    }

    private suspend fun importAndRetryIptvFavoritesJson(
        featureJson: JsonObject?,
        providerIdMap: Map<Long, Long>
    ) {
        if (featureJson == null || providerIdMap.isEmpty()) return
        val unresolved = importIptvFavoritesJson(featureJson, providerIdMap)
        if (unresolved <= 0) {
            pendingIptvFavoritesUntilMs = 0L
            iptvFavoritesRetryJob?.cancel()
            iptvFavoritesRetryJob = null
            return
        }

        pendingIptvFavoritesUntilMs = SystemClock.elapsedRealtime() + IPTV_FAVORITES_PENDING_MAX_MS
        iptvFavoritesRetryJob?.cancel()
        iptvFavoritesRetryJob = scope.launch {
            var remaining = unresolved
            listOf(5_000L, 15_000L, 45_000L, 120_000L, 240_000L).forEach { retryDelayMs ->
                if (remaining <= 0) return@forEach
                delay(retryDelayMs)
                applyingRemoteBlob = true
                try {
                    remaining = importIptvFavoritesJson(featureJson, providerIdMap)
                    skipNextPushSignature = buildSettingsSignature(profileManager.activeProfileId.value)
                } catch (e: Exception) {
                    Log.w(TAG, "IPTV favorites retry failed", e)
                } finally {
                    applyingRemoteBlob = false
                }
            }
            pendingIptvFavoritesUntilMs = 0L
            iptvFavoritesRetryJob = null
            if (remaining > 0) {
                Log.w(TAG, "IPTV favorites import still has $remaining unresolved item(s) after retries")
            }
        }
    }

    private suspend fun importIptvFavoritesJson(
        featureJson: JsonObject?,
        providerIdMap: Map<Long, Long>
    ): Int {
        if (featureJson == null || providerIdMap.isEmpty()) return 0
        val groupIdMap = mutableMapOf<Long, Long>()
        val existingGroups = virtualGroupDao.getAllSync()

        (featureJson["groups"]?.jsonArray ?: JsonArray(emptyList())).forEach { element ->
            val obj = element.jsonObject
            val remoteProviderId = obj.longValueOrNull("provider_id") ?: return@forEach
            val localProviderId = providerIdMap[remoteProviderId] ?: return@forEach
            val remoteGroupId = obj.longValueOrNull("group_id")
            val contentType = obj.contentTypeValue("content_type") ?: return@forEach
            val name = obj.stringValue("name")
            if (name.isBlank()) return@forEach

            val existing = existingGroups.firstOrNull {
                it.providerId == localProviderId &&
                    it.contentType == contentType &&
                    it.name == name
            }
            val localGroupId = existing?.id ?: virtualGroupDao.insert(
                VirtualGroupEntity(
                    providerId = localProviderId,
                    name = name,
                    iconEmoji = obj.stringValue("icon_emoji").ifBlank { null },
                    position = obj.intValue("position", 0),
                    createdAt = obj.longValue("created_at", System.currentTimeMillis()),
                    contentType = contentType
                )
            )
            if (remoteGroupId != null) groupIdMap[remoteGroupId] = localGroupId
        }

        val localProviderIds = providerIdMap.values.toSet()
        val liveLocalByProvider = localProviderIds.associateWith { providerId ->
            channelDao.getIdMappings(providerId).associate { it.remoteId.toString() to it.id }
        }
        val movieLocalByProvider = localProviderIds.associateWith { providerId ->
            movieDao.getIdMappings(providerId).associate { it.remoteId.toString() to it.id }
        }
        val seriesLocalByProvider = localProviderIds.associateWith { providerId ->
            seriesDao.getIdMappings(providerId).associate { it.remoteId to it.id }
        }

        var unresolvedFavorites = 0
        (featureJson["favorites"]?.jsonArray ?: JsonArray(emptyList())).forEach { element ->
            val obj = element.jsonObject
            val remoteProviderId = obj.longValueOrNull("provider_id") ?: return@forEach
            val localProviderId = providerIdMap[remoteProviderId] ?: return@forEach
            val contentType = obj.contentTypeValue("content_type") ?: return@forEach
            val remoteContentId = obj.stringValue("content_remote_id").ifBlank { return@forEach }
            val localContentId = when (contentType) {
                ContentType.LIVE -> liveLocalByProvider[localProviderId]?.get(remoteContentId)
                ContentType.MOVIE -> movieLocalByProvider[localProviderId]?.get(remoteContentId)
                ContentType.SERIES -> seriesLocalByProvider[localProviderId]?.get(remoteContentId)
                ContentType.SERIES_EPISODE -> null
            }
            if (localContentId == null) {
                unresolvedFavorites += 1
                return@forEach
            }
            val localGroupId = obj.longValueOrNull("group_id")?.let(groupIdMap::get)

            runCatching {
                favoriteDao.insert(
                    FavoriteEntity(
                        providerId = localProviderId,
                        contentId = localContentId,
                        contentType = contentType,
                        position = obj.intValue("position", 0),
                        groupId = localGroupId,
                        addedAt = obj.longValue("added_at", System.currentTimeMillis())
                    )
                )
            }
        }
        return unresolvedFavorites
    }

    private fun List<VirtualGroupEntity>.buildVirtualGroupSignature(): String =
        joinToString(separator = "|") {
            "${it.providerId}:${it.contentType}:${it.id}:${it.name}:${it.position}:${it.createdAt}"
        }

    private fun List<FavoriteEntity>.buildFavoriteSignature(): String =
        joinToString(separator = "|") {
            "${it.providerId}:${it.contentType}:${it.contentId}:${it.groupId}:${it.position}:${it.addedAt}"
        }

    private fun buildIptvProvidersJson(providers: List<ProviderEntity>): JsonObject {
        return buildJsonObject {
            put("providers", kotlinx.serialization.json.JsonArray(providers.map { p ->
                buildJsonObject {
                    put("name", p.name)
                    put("provider_id", p.id)
                    put("type", p.type.name)
                    put("server_url", p.serverUrl)
                    put("username", p.username)
                    put("password", p.password)
                    put("m3u_url", p.m3uUrl)
                    put("epg_url", p.epgUrl)
                    put("http_user_agent", p.httpUserAgent)
                    put("stalker_mac_address", p.stalkerMacAddress)
                    put("is_active", p.isActive)
                }
            }))
        }
    }

    private suspend fun importIptvProvidersJson(featureJson: JsonObject?): Map<Long, Long> {
        if (featureJson == null) return emptyMap()
        val providersArray = featureJson["providers"]?.jsonArray ?: return emptyMap()
        val existing = providerDao.getAllSync()
        val providerIdMap = mutableMapOf<Long, Long>()
        providersArray.forEach { element ->
            val obj = element.jsonObject
            val remoteProviderId = obj.longValueOrNull("provider_id")
            val name = obj.stringValue("name")
            val typeStr = obj.stringValue("type")
            val serverUrl = obj.stringValue("server_url")
            val username = obj.stringValue("username")
            if (name.isBlank()) return@forEach
            val existingProvider = existing.firstOrNull {
                it.name == name &&
                    it.serverUrl == serverUrl &&
                    it.username == username &&
                    it.stalkerMacAddress == obj.stringValue("stalker_mac_address")
            }
            if (existingProvider != null) {
                if (remoteProviderId != null) providerIdMap[remoteProviderId] = existingProvider.id
                return@forEach
            }
            val type = runCatching { ProviderType.valueOf(typeStr) }.getOrNull() ?: return@forEach
            val entity = ProviderEntity(
                id = 0L,
                name = name,
                type = type,
                serverUrl = serverUrl,
                username = username,
                password = obj.stringValue("password"),
                m3uUrl = obj.stringValue("m3u_url"),
                epgUrl = obj.stringValue("epg_url"),
                httpUserAgent = obj.stringValue("http_user_agent"),
                httpHeaders = "",
                stalkerMacAddress = obj.stringValue("stalker_mac_address"),
                stalkerDeviceProfile = "",
                stalkerDeviceTimezone = "",
                stalkerDeviceLocale = "",
                stalkerSerialNumber = "",
                stalkerDeviceId = "",
                stalkerDeviceId2 = "",
                stalkerSignature = "",
                stalkerAuthMode = com.streamvault.domain.model.StalkerAuthMode.AUTO,
                stalkerPortalProfile = com.streamvault.domain.model.StalkerPortalProfile.entries.first(),
                stalkerPortalFingerprint = com.streamvault.domain.model.StalkerPortalFingerprint.entries.first(),
                stalkerMagPreset = com.streamvault.domain.model.StalkerMagPreset.entries.first(),
                stalkerLastBootstrapRecipe = com.streamvault.domain.model.StalkerBootstrapRecipe.entries.first(),
                stalkerEndpointPreference = com.streamvault.domain.model.StalkerEndpointPreference.AUTO,
                stalkerCookieMode = com.streamvault.domain.model.StalkerCookieMode.NONE,
                stalkerPlaybackBackendHint = com.streamvault.domain.model.StalkerPlaybackBackendHint.AUTO,
                stalkerLastPlaybackMode = null,
                stalkerCredentialsRequired = false,
                stalkerMacRequired = false,
                stalkerUsesTemporaryLinks = false,
                stalkerModuleRestricted = false,
                stalkerStrictFingerprintRequired = false,
                stalkerRecipeFallbackUsed = false,
                stalkerRecipeRediscoveryAttempts = 0,
                isActive = obj.booleanValue("is_active", false),
                maxConnections = 1,
                expirationDate = null,
                apiVersion = null,
                allowedOutputFormatsJson = "",
                epgSyncMode = com.streamvault.domain.model.ProviderEpgSyncMode.entries.first(),
                xtreamFastSyncEnabled = false,
                xtreamLiveSyncMode = com.streamvault.domain.model.ProviderXtreamLiveSyncMode.AUTO,
                m3uVodClassificationEnabled = false,
                status = com.streamvault.domain.model.ProviderStatus.entries.first(),
                lastSyncedAt = 0L,
                createdAt = System.currentTimeMillis()
            )
            runCatching { providerDao.insert(entity) }
                .getOrNull()
                ?.let { insertedId ->
                    if (remoteProviderId != null) providerIdMap[remoteProviderId] = insertedId
                }
        }
        return providerIdMap
    }

    private fun remapIptvSettingsProviderIds(
        featureJson: JsonObject,
        providerIdMap: Map<Long, Long>
    ): JsonObject {
        if (providerIdMap.isEmpty()) return featureJson
        return buildJsonObject {
            featureJson.forEach { (keyName, encodedValue) ->
                put(remapIptvSettingsKey(keyName, providerIdMap), encodedValue)
            }
        }
    }

    private fun remapIptvSettingsKey(keyName: String, providerIdMap: Map<Long, Long>): String {
        val prefixes = listOf(
            "iptv_hidden_groups_",
            "iptv_hidden_channels_",
            "iptv_group_renames_"
        )
        prefixes.forEach { prefix ->
            if (!keyName.startsWith(prefix)) return@forEach
            val suffix = keyName.removePrefix(prefix)
            val remoteIdToken = suffix.takeWhile { it.isDigit() }
            val remoteProviderId = remoteIdToken.toLongOrNull() ?: return keyName
            val localProviderId = providerIdMap[remoteProviderId] ?: return keyName
            return prefix + localProviderId + suffix.drop(remoteIdToken.length)
        }
        return keyName
    }
    private fun decryptProviderPassword(password: String): String = password
    private suspend fun encryptSyncedProviderPassword(password: String): String = password
    private fun decryptSyncedProviderPassword(password: String): String = password

    private fun JsonObject.stringValue(key: String): String =
        this[key]?.jsonPrimitive?.contentOrNull.orEmpty()

    private fun JsonObject.booleanValue(key: String, default: Boolean): Boolean =
        this[key]?.jsonPrimitive?.booleanOrNull ?: default

    private fun JsonObject.intValue(key: String, default: Int): Int =
        this[key]?.jsonPrimitive?.intOrNull ?: default

    private fun JsonObject.longValue(key: String, default: Long): Long =
        this[key]?.jsonPrimitive?.longOrNull ?: default

    private fun JsonObject.longValueOrNull(key: String): Long? =
        this[key]?.jsonPrimitive?.longOrNull

    private fun JsonObject.contentTypeValue(key: String): ContentType? =
        stringValue(key).takeIf { it.isNotBlank() }?.let { raw ->
            runCatching { ContentType.valueOf(raw) }.getOrNull()
        }

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


