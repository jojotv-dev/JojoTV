package com.nuvio.tv.core.sync

import android.content.Context
import android.content.SharedPreferences
import android.os.SystemClock
import android.util.Log
import androidx.datastore.preferences.core.MutablePreferences
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
import com.nuvio.tv.calendar.CalendarProfileSyncSnapshot
import com.nuvio.tv.calendar.CalendarPreferences
import com.nuvio.tv.calendar.CalendarSettings
import com.nuvio.tv.data.local.ContinueWatchingEnrichmentCache
import com.nuvio.tv.data.local.ProfileBehaviorSyncSnapshot
import com.nuvio.tv.data.local.ProfileDataStore
import com.nuvio.tv.data.local.SentrySettingsDataStore
import com.nuvio.tv.data.local.AmbientModeDataStore
import com.nuvio.tv.data.local.DiscoverSelectionDataStore
import com.nuvio.tv.data.local.ExperienceModeDataStore
import com.nuvio.tv.data.local.PluginDataStore
import com.nuvio.tv.data.local.ProfileDataStoreFactory
import com.nuvio.tv.data.local.StreamBadgeSettingsDataStore
import com.nuvio.tv.data.local.TmdbSettingsDataStore
import com.nuvio.tv.data.freebox.FreeboxRepository
import com.nuvio.tv.livetv.data.LiveTvPreferences
import com.nuvio.tv.livetv.data.SportsProfileSyncSnapshot
import com.nuvio.tv.data.remote.supabase.SupabaseProfileSetupCopyResult
import com.nuvio.tv.data.remote.supabase.SupabaseProfileSettingsBlob
import com.nuvio.tv.domain.model.DiscoverLocation
import com.nuvio.tv.domain.model.ProfileHomePresentation
import com.nuvio.tv.domain.repository.MetaRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.sample
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
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.json.put
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "ProfileSettingsSyncService"
private const val SETTINGS_PUSH_DEBOUNCE_MS = 1500L
private const val FOREGROUND_PULL_DELAY_MS = 2500L
private const val FOREGROUND_PULL_MIN_INTERVAL_MS = 60_000L
private const val SETTINGS_SYNC_PLATFORM = "tv"
private const val PLAYER_SETTINGS_FEATURE = "player_settings"
private const val YOUTUBE_SETTINGS_FEATURE = "youtube_settings"
private const val APP_LOCALE_FEATURE = "app_locale"
private const val HOME_FAVORITE_ARTWORK_FEATURE = "home_favorite_artwork"
private const val CONTINUE_WATCHING_ARTWORK_FEATURE = "continue_watching_artwork"
private const val CONTINUE_WATCHING_ENRICHMENT_FEATURE = "continue_watching_enrichment"
private const val CALENDAR_SETTINGS_FEATURE = "calendar_settings"
private const val SPORTS_SETTINGS_FEATURE = "sports_settings"
private const val FREEBOX_SETTINGS_FEATURE = "freebox_settings"
private const val PROFILE_BEHAVIOR_FEATURE = "profile_behavior_settings"
private const val SENTRY_SETTINGS_FEATURE = "sentry_settings"
private const val IPTV_MENU_SETTINGS_FEATURE = "iptv_menu_settings"
private const val CONTINUE_WATCHING_ENRICHMENT_SYNC_INTERVAL_MS = 30_000L
private const val SUPPLEMENTAL_UPDATED_AT_KEY = "__updated_at"
private const val SUPPLEMENTAL_CONTENT_SIGNATURE_KEY = "__content_signature"
private val artworkPreferenceFeatures = setOf(
    CONTINUE_WATCHING_ARTWORK_FEATURE,
    HOME_FAVORITE_ARTWORK_FEATURE
)

private data class SharedPreferencesSyncFeature(
    val featureName: String,
    val preferencesName: String,
    val profileScoped: Boolean,
    val profileKeySuffix: Boolean = false,
    val excludedKeys: Set<String> = emptySet()
)

data class ProfileSettingsSupplementalChange(
    val profileId: Int,
    val features: Set<String>
)

private val catalogKeysExcludedFromProfileSettingsBlob = setOf(
    "home_catalog_order_keys",
    "disabled_home_catalog_keys",
    "custom_catalog_titles"
)

private val localOnlyLayoutProfileSettingsKeys = setOf(
    "last_non_off_discover_location"
)

private val localOnlyPlayerProfileSettingsKeys = setOf(
    "player_preference",
    "internal_player_engine",
    "auto_switch_internal_player_on_error",
    "use_libass",
    "libass_render_type",
    "decoder_priority",
    "downmix_enabled",
    "audio_output_channels",
    "maintain_original_audio_on_downmix",
    "downmix_lfe_level",
    "downmix_normalization_enabled",
    "tunneling_enabled",
    "force_optical_passthrough",
    "audio_amplification_db",
    "center_mix_level_db",
    "night_mode",
    "persist_audio_amplification",
    "remember_audio_delay_per_device",
    "experimental_dv5_to_dv81_enabled",
    "experimental_dv7_to_dv81_preserve_mapping_enabled",
    "dv7_handling_mode",
    "map_dv7_to_hevc",
    "dv7_libdovi_mode_override",
    "strip_hdr10plus_sei",
    "exo_chapters_enabled",
    "mpv_hardware_decode_mode",
    "frame_rate_matching",
    "frame_rate_matching_mode",
    "resolution_matching_enabled",
    "external_player_forward_subtitles",
    "external_player_send_skip_segments",
    "vod_cache_enabled",
    "vod_cache_size_mode",
    "vod_cache_size_mb",
    "use_parallel_connections",
    "buffer_engine_enabled",
    "parallel_network_enabled",
    "allow_large_target_buffer",
    "buffer_budget_managed",
    "parallel_connection_count",
    "parallel_chunk_size_mb",
    "parallel_chunk_size_kb",
    "enable_http2",
    "last_playback_diagnostics_json",
    "enable_buffer_logs",
    "resize_mode",
    "min_buffer_ms",
    "max_buffer_ms",
    "buffer_for_playback_ms",
    "buffer_for_playback_after_rebuffer_ms",
    "target_buffer_size_mb",
    "back_buffer_duration_ms",
    "retain_back_buffer_from_keyframe",
    "migration_load_control_defaults_aligned_done",
    "migration_load_control_defaults_retuned_done",
    "migration_load_control_min_buffer_retuned_done",
    "migration_vod_cache_split_done",
    "migration_back_buffer_duration_bumped_done",
    "migration_max_buffer_bumped_done",
    "migration_target_buffer_size_bumped_done",
    "migration_after_rebuffer_lowered_done",
    "migration_back_buffer_duration_reduced_done",
    "migration_target_buffer_size_reduced_done",
    "migration_allow_large_target_buffer_off_done",
    "migration_buffer_budget_managed_exo_done",
    "migration_vod_cache_back_buffer_zeroed_done",
    "nuvio_performance_mode_enabled"
)

// 4K trailers depend on the device's RAM, so a choice made on one device must not reach another.
private val localOnlyTrailerProfileSettingsKeys = setOf(
    "trailer_allow_4k"
)

// Launcher icon overrides can point to files or provider URIs that exist only on one TV.
private val localOnlyInstalledAppSettingsKeys = setOf("installed_apps_custom_icons")

// Library contents and mutation cursors have their own delta-sync service; only its view settings
// belong in the profile settings blob.
private val localOnlyLibrarySyncKeys = setOf(
    "library_items",
    "library_delta_cursor",
    "library_delta_initialized",
    "library_pending_upserts",
    "library_pending_deletes",
    "library_mutation_revision"
)

private val credentialProfileSettingsKeys = mapOf(
    "mdblist_settings" to setOf("mdblist_api_key")
)

internal fun shouldExcludePreferenceFromProfileSettingsSync(feature: String, keyName: String): Boolean {
    return when {
        feature == "layout_settings" && keyName in catalogKeysExcludedFromProfileSettingsBlob -> true
        feature == "layout_settings" && keyName in localOnlyLayoutProfileSettingsKeys -> true
        feature == "layout_settings" && keyName == "search_discover_enabled" -> true
        feature == PLAYER_SETTINGS_FEATURE && keyName in localOnlyPlayerProfileSettingsKeys -> true
        feature == "trailer_settings" && keyName in localOnlyTrailerProfileSettingsKeys -> true
        feature == "installed_apps_row" && keyName in localOnlyInstalledAppSettingsKeys -> true
        feature == "library_preferences" && keyName in localOnlyLibrarySyncKeys -> true
        feature == PluginDataStore.FEATURE && keyName != PluginDataStore.GROUP_STREAMS_BY_REPOSITORY -> true
        keyName in credentialProfileSettingsKeys[feature].orEmpty() -> true
        else -> false
    }
}

@Singleton
@OptIn(kotlinx.coroutines.FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class ProfileSettingsSyncService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authManager: AuthManager,
    private val postgrest: Postgrest,
    private val profileManager: ProfileManager,
    private val profileDataStoreFactory: ProfileDataStoreFactory,
    private val syncClientIdentity: SyncClientIdentity,
    private val providerCredentialSyncService: ProviderCredentialSyncService,
    private val iptvProfileControlSyncService: IptvProfileControlSyncService,
    private val mediaServerSyncService: MediaServerSyncService,
    private val tmdbSettingsDataStore: TmdbSettingsDataStore,
    private val metaRepository: MetaRepository,
    private val cwEnrichmentCache: ContinueWatchingEnrichmentCache,
    private val calendarPreferences: CalendarPreferences,
    private val liveTvPreferences: LiveTvPreferences,
    private val freeboxRepository: FreeboxRepository,
    private val profileDataStore: ProfileDataStore,
    private val sentrySettingsDataStore: SentrySettingsDataStore
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val syncMutex = Mutex()

    @Volatile
    private var applyingRemoteBlob: Boolean = false

    @Volatile
    private var skipNextPushSignature: String? = null
    private var foregroundPullJob: Job? = null
    private var lastForegroundPullAtMs: Long = 0L

    private val dataStoreSyncedFeatures = listOf(
        "theme_settings",
        "layout_settings",
        ExperienceModeDataStore.FEATURE,
        AmbientModeDataStore.FEATURE,
        DiscoverSelectionDataStore.FEATURE,
        PLAYER_SETTINGS_FEATURE,
        StreamBadgeSettingsDataStore.FEATURE,
        PluginDataStore.FEATURE,
        "trailer_settings",
        "tmdb_settings",
        "mdblist_settings",
        "trakt_settings",
        "track_preference",
        "search_history",
        "installed_apps_row",
        "library_preferences"
    )

    private val sharedPreferencesSyncFeatures = listOf(
        SharedPreferencesSyncFeature(APP_LOCALE_FEATURE, APP_LOCALE_FEATURE, profileScoped = false),
        SharedPreferencesSyncFeature(YOUTUBE_SETTINGS_FEATURE, YOUTUBE_SETTINGS_FEATURE, profileScoped = false),
        SharedPreferencesSyncFeature(
            CONTINUE_WATCHING_ARTWORK_FEATURE,
            "continue_watching_hero_artwork",
            profileScoped = true
        ),
        SharedPreferencesSyncFeature(
            HOME_FAVORITE_ARTWORK_FEATURE,
            "home_favorite_artwork_overrides",
            profileScoped = true
        ),
        SharedPreferencesSyncFeature(
            featureName = IPTV_MENU_SETTINGS_FEATURE,
            preferencesName = "nuvio_live_tv_menu",
            profileScoped = false
        )
    )

    private val syncedFeatures = dataStoreSyncedFeatures +
        sharedPreferencesSyncFeatures.map { it.featureName } +
        listOf(
            CALENDAR_SETTINGS_FEATURE,
            SPORTS_SETTINGS_FEATURE,
            FREEBOX_SETTINGS_FEATURE,
            PROFILE_BEHAVIOR_FEATURE,
            SENTRY_SETTINGS_FEATURE,
            CONTINUE_WATCHING_ENRICHMENT_FEATURE
        )

    private val _supplementalSettingsImported =
        MutableSharedFlow<ProfileSettingsSupplementalChange>(extraBufferCapacity = 1)
    val supplementalSettingsImported = _supplementalSettingsImported.asSharedFlow()

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

    suspend fun pushCurrentProfileToRemote(): Result<Unit> {
        val profileId = profileManager.activeProfileId.value
        val result = pushProfileSettingsToRemote(profileId)
        return result.fold(
            onSuccess = {
                iptvProfileControlSyncService.syncFromRemote(profileId).map { Unit }
            },
            onFailure = { Result.failure(it) },
        )
    }

    private suspend fun pushProfileSettingsToRemote(profileId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        syncMutex.withLock {
            try {
                pushProfileToRemote(profileId)
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
                val inheritedPluginSettingsApplied = pullInheritedPluginSettings(profileId)
                val blob = pullProfileFromRemote(profileId)
                lastForegroundPullAtMs = SystemClock.elapsedRealtime()
                if (blob == null) {
                    Log.d(TAG, "No remote profile settings blob for profile $profileId; seeding it from this TV")
                    pushProfileToRemote(profileId)
                    iptvProfileControlSyncService.syncFromRemote(profileId).getOrThrow()
                    return@withLock Result.success(inheritedPluginSettingsApplied)
                }

                val featuresJson = blob["features"]?.jsonObject
                    ?: run {
                        iptvProfileControlSyncService.syncFromRemote(profileId).getOrThrow()
                        return@withLock Result.success(inheritedPluginSettingsApplied)
                    }
                val remoteSignature = buildSettingsSignature(featuresJson)
                val localSignature = buildSettingsSignature(profileId)
                if (remoteSignature == localSignature) {
                    Log.d(TAG, "Remote profile settings already match local for profile $profileId")
                    iptvProfileControlSyncService.syncFromRemote(profileId).getOrThrow()
                    return@withLock Result.success(inheritedPluginSettingsApplied)
                }

                applySettingsBlob(profileId, featuresJson, remoteSignature)
                val mergedLocalSignature = buildSettingsSignature(profileId)
                if (mergedLocalSignature != remoteSignature) {
                    // Preserve newly added settings from an older local blob, then seed those
                    // fields so the next TV can restore them too.
                    pushProfileToRemote(profileId)
                    skipNextPushSignature = mergedLocalSignature
                }
                Log.d(TAG, "Applied remote profile settings blob for profile $profileId")
                iptvProfileControlSyncService.syncFromRemote(profileId).getOrThrow()
                Result.success(true)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to pull profile settings blob", e)
                Result.failure(e)
            }
        }
    }

    suspend fun copyProfileSetup(
        sourceProfileId: Int,
        targetProfileId: Int,
        copyProviderCredentials: Boolean = false
    ): Result<Unit> =
        withContext(Dispatchers.IO) {
            syncMutex.withLock {
                try {
                    require(sourceProfileId != targetProfileId)
                    require(profileManager.profiles.value.any { it.id == sourceProfileId })
                    require(profileManager.profiles.value.any { it.id == targetProfileId })

                    val targetFeatures = if (authManager.isAuthenticated) {
                        if (
                            copyProviderCredentials &&
                            profileManager.activeProfileId.value in setOf(sourceProfileId, targetProfileId)
                        ) {
                            providerCredentialSyncService.syncFromRemote().getOrThrow()
                        }
                        val sourceBlob = if (sourceProfileId == profileManager.activeProfileId.value) {
                            exportSettingsBlob(sourceProfileId).also { blob ->
                                pushProfileToRemote(sourceProfileId, blob)
                            }
                        } else {
                            pullProfileFromRemote(sourceProfileId)
                                ?: exportSettingsBlob(sourceProfileId).also { blob ->
                                    pushProfileToRemote(sourceProfileId, blob)
                                }
                        }
                        require(sourceBlob["features"] is JsonObject)

                        val params = buildJsonObject {
                            put("p_source_profile_id", sourceProfileId)
                            put("p_target_profile_id", targetProfileId)
                            put("p_copy_tv", true)
                            put("p_copy_mobile", false)
                            put("p_copy_desktop", false)
                            put("p_copy_provider_credentials", copyProviderCredentials)
                            put("p_replace_provider_credentials", false)
                            putSyncOriginClientId(syncClientIdentity)
                        }
                        val response = withJwtRefreshRetry {
                            postgrest.rpc("sync_copy_profile_setup", params)
                        }
                        val copyResult = response.decodeList<SupabaseProfileSetupCopyResult>().firstOrNull()
                            ?: error("Profile settings copy returned no result")
                        check(copyResult.sourceProfileId == sourceProfileId)
                        check(copyResult.targetProfileId == targetProfileId)
                        check(copyResult.tvStatus == "copied" || copyResult.tvStatus == "unchanged")
                        if (copyProviderCredentials) {
                            check(
                                copyResult.providerCredentialsStatus in setOf(
                                    "copied",
                                    "copied_partial",
                                    "kept_existing",
                                    "unchanged",
                                    "source_missing"
                                )
                            )
                        }
                        pullProfileFromRemote(targetProfileId)
                            ?.get("features")
                            ?.jsonObject
                            ?: error("Copied TV settings are unavailable")
                    } else {
                        exportSettingsBlob(sourceProfileId)["features"]?.jsonObject
                            ?: error("Source TV settings are unavailable")
                    }

                    applySettingsBlob(
                        profileId = targetProfileId,
                        featuresJson = targetFeatures,
                        signature = buildSettingsSignature(targetFeatures)
                    )
                    if (copyProviderCredentials) {
                        if (authManager.isAuthenticated) {
                            if (profileManager.activeProfileId.value == targetProfileId) {
                                providerCredentialSyncService.syncFromRemote(targetProfileId).getOrThrow()
                            }
                        } else {
                            copyProviderCredentialsLocally(sourceProfileId, targetProfileId)
                        }
                    }
                    iptvProfileControlSyncService.copyProfileConfig(sourceProfileId, targetProfileId)
                        .onFailure { Log.w(TAG, "Could not copy IPTV profile settings", it) }
                    Log.d(TAG, "Copied profile setup from $sourceProfileId to $targetProfileId")
                    Result.success(Unit)
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to copy profile setup from $sourceProfileId to $targetProfileId", e)
                    Result.failure(e)
                }
            }
        }

    fun requestForegroundPull(force: Boolean = false) {
        providerCredentialSyncService.requestForegroundPull(force)
        mediaServerSyncService.requestForegroundPull(force)
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

    private suspend fun pushProfileToRemote(
        profileId: Int,
        settingsJson: JsonObject? = null
    ) {
        val resolvedSettingsJson = settingsJson ?: exportSettingsBlob(profileId)
        val params = buildJsonObject {
            put("p_profile_id", profileId)
            put("p_settings_json", resolvedSettingsJson)
            put("p_platform", SETTINGS_SYNC_PLATFORM)
            putSyncOriginClientId(syncClientIdentity)
        }
        withJwtRefreshRetry {
            postgrest.rpc("sync_push_profile_settings_blob", params)
        }
    }

    private suspend fun pullProfileFromRemote(profileId: Int): JsonObject? {
        val params = buildJsonObject {
            put("p_profile_id", profileId)
            put("p_platform", SETTINGS_SYNC_PLATFORM)
        }
        val response = withJwtRefreshRetry {
            postgrest.rpc("sync_pull_profile_settings_blob", params)
        }
        return response.decodeList<SupabaseProfileSettingsBlob>().firstOrNull()?.settingsJson
    }

    private suspend fun applySettingsBlob(
        profileId: Int,
        featuresJson: JsonObject,
        signature: String
    ) {
        val isActiveProfile = profileManager.activeProfileId.value == profileId
        importSettingsBlob(profileId, featuresJson)
        if (isActiveProfile) {
            skipNextPushSignature = signature
        }
    }

    private suspend fun pullInheritedPluginSettings(profileId: Int): Boolean {
        val profile = profileManager.profiles.value.firstOrNull { it.id == profileId }
        if (profileId == 1 || profile?.usesPrimaryPlugins != true) return false
        val features = pullProfileFromRemote(1)?.get("features") as? JsonObject ?: return false
        val pluginSettings = features[PluginDataStore.FEATURE] as? JsonObject ?: return false
        return importPluginSettings(1, pluginSettings)
    }

    private suspend fun importPluginSettings(profileId: Int, featureJson: JsonObject): Boolean {
        val keyName = PluginDataStore.GROUP_STREAMS_BY_REPOSITORY
        val encoded = featureJson[keyName] as? JsonObject ?: return false
        if ((encoded["type"] as? JsonPrimitive)?.contentOrNull != "boolean") return false
        val enabled = (encoded["value"] as? JsonPrimitive)?.contentOrNull?.toBooleanStrictOrNull()
            ?: return false
        var changed = false
        profileDataStoreFactory.get(profileId, PluginDataStore.FEATURE).edit { prefs ->
            val key = booleanPreferencesKey(keyName)
            changed = prefs[key] != enabled
            prefs[key] = enabled
        }
        return changed
    }

    private suspend fun copyProviderCredentialsLocally(sourceProfileId: Int, targetProfileId: Int) {
        credentialProfileSettingsKeys.forEach { (feature, keyNames) ->
            val sourcePreferences = profileDataStoreFactory.get(sourceProfileId, feature).data.first()
            profileDataStoreFactory.get(targetProfileId, feature).edit { targetPreferences ->
                keyNames.forEach { keyName ->
                    val key = stringPreferencesKey(keyName)
                    val sourceValue = sourcePreferences[key]?.trim().orEmpty()
                    val targetValue = targetPreferences[key]?.trim().orEmpty()
                    if (sourceValue.isNotEmpty() && targetValue.isEmpty()) {
                        targetPreferences[key] = sourceValue
                    }
                }
            }
        }
    }

    private suspend fun exportSettingsBlob(profileId: Int): JsonObject {
        val enrichmentSnapshot = cwEnrichmentCache.exportForDeviceSync(profileId)
        val features = buildJsonObject {
            dataStoreSyncedFeatures.forEach { feature ->
                val prefs = profileDataStoreFactory.get(profileId, feature).data.first()
                val serialized = buildJsonObject {
                    prefs.asMap().forEach { (key, rawValue) ->
                        if (shouldExcludePreferenceFromProfileSettingsSync(feature, key.name)) return@forEach
                        val encoded = encodePreferenceValue(rawValue) ?: return@forEach
                        put(key.name, encoded)
                    }
                }
                put(feature, serialized)
            }
            sharedPreferencesSyncFeatures.forEach { feature ->
                put(feature.featureName, exportSharedPreferencesFeature(profileId, feature))
            }
            put(CALENDAR_SETTINGS_FEATURE, exportCalendarSettingsFeature(profileId))
            put(SPORTS_SETTINGS_FEATURE, exportSportsSettingsFeature(profileId))
            put(FREEBOX_SETTINGS_FEATURE, JsonPrimitive(freeboxRepository.profileSettingsSyncSnapshot()))
            put(PROFILE_BEHAVIOR_FEATURE, encodeProfileBehaviorFeature(profileDataStore.settingsSyncSnapshot(profileId).first()))
            put(SENTRY_SETTINGS_FEATURE, encodeSentrySettingsFeature(sentrySettingsDataStore.enabled.first()))
            if (enrichmentSnapshot != null) {
                put(CONTINUE_WATCHING_ENRICHMENT_FEATURE, enrichmentSnapshot)
            }
        }

        return buildJsonObject {
            put("version", 1)
            put("features", features)
        }
    }

    private suspend fun exportCalendarSettingsFeature(profileId: Int): JsonObject =
        encodeCalendarSettingsFeature(calendarPreferences.profileSyncSnapshot(profileId).first())

    private fun encodeCalendarSettingsFeature(snapshot: CalendarProfileSyncSnapshot): JsonObject =
        buildJsonObject {
            put("calendar_in_sidebar", JsonPrimitive(snapshot.settings.calendarInSidebar))
            put("calendar_in_header", JsonPrimitive(snapshot.settings.calendarInHeader))
            // Kept for older app versions that still have one shared Calendar visibility setting.
            put("calendar_in_menu", JsonPrimitive(snapshot.settings.calendarInSidebar))
            put("calendar_off", JsonPrimitive(snapshot.settings.calendarOff))
            put("calendar_upcoming_row", JsonPrimitive(snapshot.settings.calendarUpcomingRow))
            put("calendar_new_episode_cards", JsonPrimitive(snapshot.settings.calendarNewEpisodeCards))
            put("calendar_hide_spoilers", JsonPrimitive(snapshot.settings.calendarHideSpoilers))
            put("calendar_country", JsonPrimitive(snapshot.settings.calendarCountry))
            put("calendar_skip_news_talk", JsonPrimitive(snapshot.settings.calendarSkipNewsTalk))
            put("calendar_view", JsonPrimitive(snapshot.settings.calendarView))
            put("calendar_hide_watched", JsonPrimitive(snapshot.settings.calendarHideWatched))
            put("use_24h_clock", JsonPrimitive(snapshot.settings.use24HourClock))
            put("calendar_follows", JsonPrimitive(snapshot.follows))
            put("calendar_hidden_shows", JsonPrimitive(snapshot.hiddenShows))
        }

    private fun decodeCalendarSettingsFeature(featureJson: JsonObject): CalendarProfileSyncSnapshot {
        val defaults = CalendarSettings()
        fun boolean(key: String, default: Boolean): Boolean =
            featureJson[key]?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull() ?: default
        fun string(key: String, default: String): String =
            featureJson[key]?.jsonPrimitive?.contentOrNull ?: default
        val legacyCalendarInMenu = boolean("calendar_in_menu", defaults.calendarInSidebar)

        return CalendarProfileSyncSnapshot(
            settings = CalendarSettings(
                calendarInSidebar = boolean("calendar_in_sidebar", legacyCalendarInMenu),
                calendarInHeader = boolean("calendar_in_header", legacyCalendarInMenu),
                calendarOff = boolean("calendar_off", defaults.calendarOff),
                calendarUpcomingRow = boolean("calendar_upcoming_row", defaults.calendarUpcomingRow),
                calendarNewEpisodeCards = boolean("calendar_new_episode_cards", defaults.calendarNewEpisodeCards),
                calendarHideSpoilers = boolean("calendar_hide_spoilers", defaults.calendarHideSpoilers),
                calendarCountry = string("calendar_country", defaults.calendarCountry),
                calendarSkipNewsTalk = boolean("calendar_skip_news_talk", defaults.calendarSkipNewsTalk),
                calendarView = string("calendar_view", defaults.calendarView),
                calendarHideWatched = boolean("calendar_hide_watched", defaults.calendarHideWatched),
                use24HourClock = boolean("use_24h_clock", defaults.use24HourClock)
            ),
            follows = string("calendar_follows", ""),
            hiddenShows = string("calendar_hidden_shows", "")
        )
    }

    private suspend fun exportSportsSettingsFeature(profileId: Int): JsonObject =
        encodeSportsSettingsFeature(liveTvPreferences.sportsProfileSyncSnapshot(profileId).first())

    private fun encodeSportsSettingsFeature(snapshot: SportsProfileSyncSnapshot): JsonObject =
        buildJsonObject {
            put("settings_json", snapshot.settingsJson?.let { JsonPrimitive(it) } ?: JsonNull)
            put("follows_json", snapshot.followsJson?.let { JsonPrimitive(it) } ?: JsonNull)
        }

    private fun decodeSportsSettingsFeature(featureJson: JsonObject): SportsProfileSyncSnapshot =
        SportsProfileSyncSnapshot(
            settingsJson = featureJson["settings_json"]
                ?.takeUnless { it == JsonNull }
                ?.jsonPrimitive
                ?.contentOrNull,
            followsJson = featureJson["follows_json"]
                ?.takeUnless { it == JsonNull }
                ?.jsonPrimitive
                ?.contentOrNull
        )

    private fun encodeProfileBehaviorFeature(snapshot: ProfileBehaviorSyncSnapshot): JsonObject =
        buildJsonObject {
            put("remember_last_profile_enabled", JsonPrimitive(snapshot.rememberLastProfileEnabled))
            put("confirm_exit_enabled", JsonPrimitive(snapshot.confirmExitEnabled))
            put("startup_splash_enabled", JsonPrimitive(snapshot.startupSplashEnabled))
            put("profile_idle_switch_enabled", JsonPrimitive(snapshot.profileIdleSwitchEnabled))
            put("home_presentation", JsonPrimitive(snapshot.homePresentation.name))
        }

    private fun decodeProfileBehaviorFeature(featureJson: JsonObject): ProfileBehaviorSyncSnapshot {
        fun boolean(key: String, default: Boolean): Boolean =
            featureJson[key]?.jsonPrimitive?.contentOrNull?.toBooleanStrictOrNull() ?: default
        val homePresentation = featureJson["home_presentation"]
            ?.jsonPrimitive
            ?.contentOrNull
            ?.let { runCatching { ProfileHomePresentation.valueOf(it) }.getOrNull() }
            ?: ProfileHomePresentation.ORIGINAL
        return ProfileBehaviorSyncSnapshot(
            rememberLastProfileEnabled = boolean("remember_last_profile_enabled", false),
            confirmExitEnabled = boolean("confirm_exit_enabled", false),
            startupSplashEnabled = boolean("startup_splash_enabled", true),
            profileIdleSwitchEnabled = boolean("profile_idle_switch_enabled", true),
            homePresentation = homePresentation
        )
    }

    private fun encodeSentrySettingsFeature(enabled: Boolean): JsonObject = buildJsonObject {
        put("enabled", JsonPrimitive(enabled))
    }

    private fun exportSharedPreferencesFeature(
        profileId: Int,
        feature: SharedPreferencesSyncFeature
    ): JsonObject {
        val preferences = context.getSharedPreferences(feature.preferencesName, Context.MODE_PRIVATE)
        return buildJsonObject {
            preferences.all.toSortedMap().forEach { (storedKey, rawValue) ->
                if (!sharedPreferenceKeyBelongsToProfile(storedKey, profileId, feature)) return@forEach
                val keyName = sharedPreferenceLogicalKey(storedKey, profileId, feature)
                if (keyName in feature.excludedKeys) return@forEach
                if (feature.featureName in artworkPreferenceFeatures && !isShareableArtworkValue(rawValue)) {
                    return@forEach
                }
                val encoded = encodePreferenceValue(rawValue) ?: return@forEach
                put(keyName, encoded)
            }
            put(
                SUPPLEMENTAL_UPDATED_AT_KEY,
                encodePreferenceValue(getSupplementalUpdatedAt(profileId, feature))!!
            )
            put(
                SUPPLEMENTAL_CONTENT_SIGNATURE_KEY,
                encodePreferenceValue(getSupplementalContentSignature(profileId, feature)).let { encoded ->
                    encoded ?: JsonNull
                }
            )
        }
    }

    private fun importSharedPreferencesFeature(
        profileId: Int,
        feature: SharedPreferencesSyncFeature,
        featureJson: JsonObject
    ): Boolean {
        val preferences = context.getSharedPreferences(feature.preferencesName, Context.MODE_PRIVATE)
        val current = preferences.all.mapNotNull { (key, value) ->
            if (
                sharedPreferenceKeyBelongsToProfile(key, profileId, feature) && value != null &&
                sharedPreferenceLogicalKey(key, profileId, feature) !in feature.excludedKeys
            ) key to value else null
        }.toMap()
        val remoteUpdatedAt = (featureJson[SUPPLEMENTAL_UPDATED_AT_KEY]
            ?.let(::decodePreferenceValue) as? Long) ?: 0L
        val localUpdatedAt = getSupplementalUpdatedAt(profileId, feature)
        if (localUpdatedAt > remoteUpdatedAt) return false

        val remoteValuesJson = JsonObject(
            featureJson.filterKeys {
                    it != SUPPLEMENTAL_UPDATED_AT_KEY && it != SUPPLEMENTAL_CONTENT_SIGNATURE_KEY &&
                    it !in feature.excludedKeys
            }
        )
        val hasLocalShareableValues = current.any { (key, value) ->
            key != SUPPLEMENTAL_UPDATED_AT_KEY &&
                (feature.featureName !in artworkPreferenceFeatures || isShareableArtworkValue(value))
        }
        if (remoteValuesJson.isEmpty() && hasLocalShareableValues && remoteUpdatedAt <= localUpdatedAt) {
            return false
        }
        val preservedLocalOnly = if (feature.featureName in artworkPreferenceFeatures) {
            current.filterValues { !isShareableArtworkValue(it) }
        } else {
            emptyMap()
        }
        val imported = remoteValuesJson.mapNotNull { (keyName, encodedValue) ->
            val value = decodePreferenceValue(encodedValue) ?: return@mapNotNull null
            if (feature.featureName in artworkPreferenceFeatures && !isShareableArtworkValue(value)) {
                return@mapNotNull null
            }
            sharedPreferenceStoredKey(keyName, profileId, feature) to value
        }.toMap()
        val target = preservedLocalOnly + imported
        val preferencesChanged = current != target
        val valuesChanged = preferencesChanged
        if (preferencesChanged) {
            val editor = preferences.edit()
            current.keys.filterNot { it in preservedLocalOnly }.forEach { editor.remove(it) }
            target.forEach { (key, value) -> putSharedPreferenceValue(editor, key, value) }
            editor.apply()
        }
        if (remoteUpdatedAt != localUpdatedAt) setSupplementalUpdatedAt(profileId, feature, remoteUpdatedAt)
        setSupplementalContentSignature(
            profileId,
            feature,
            sharedPreferenceFeatureContentSignature(profileId, feature)
        )
        return valuesChanged
    }

    private fun isShareableArtworkValue(value: Any?): Boolean {
        if (value !is String) return false
        val normalized = value.trim().lowercase()
        return normalized.startsWith("https://") || normalized.startsWith("http://")
    }

    private fun putSharedPreferenceValue(
        editor: SharedPreferences.Editor,
        key: String,
        value: Any
    ) {
        when (value) {
            is String -> editor.putString(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Int -> editor.putInt(key, value)
            is Long -> editor.putLong(key, value)
            is Float -> editor.putFloat(key, value)
            is Set<*> -> if (value.all { it is String }) {
                editor.putStringSet(key, value.map { it as String }.toMutableSet())
            }
        }
    }

    private fun sharedPreferenceKeyBelongsToProfile(
        storedKey: String,
        profileId: Int,
        feature: SharedPreferencesSyncFeature
    ): Boolean = when {
        !feature.profileScoped -> true
        feature.profileKeySuffix -> storedKey.endsWith("_$profileId")
        else -> storedKey.startsWith("$profileId:")
    }

    private fun sharedPreferenceLogicalKey(
        storedKey: String,
        profileId: Int,
        feature: SharedPreferencesSyncFeature
    ): String = when {
        !feature.profileScoped -> storedKey
        feature.profileKeySuffix -> storedKey.removeSuffix("_$profileId")
        else -> storedKey.removePrefix("$profileId:")
    }

    private fun sharedPreferenceStoredKey(
        logicalKey: String,
        profileId: Int,
        feature: SharedPreferencesSyncFeature
    ): String = when {
        !feature.profileScoped -> logicalKey
        feature.profileKeySuffix -> "${logicalKey}_$profileId"
        else -> "$profileId:$logicalKey"
    }

    private fun supplementalUpdatedAtKey(profileId: Int, feature: SharedPreferencesSyncFeature): String =
        "$profileId:${feature.featureName}"

    private fun supplementalContentSignatureKey(profileId: Int, feature: SharedPreferencesSyncFeature): String =
        "$profileId:${feature.featureName}:content_signature"

    private fun getSupplementalUpdatedAt(profileId: Int, feature: SharedPreferencesSyncFeature): Long =
        context.getSharedPreferences("profile_settings_sync_metadata", Context.MODE_PRIVATE)
            .getLong(supplementalUpdatedAtKey(profileId, feature), 0L)

    private fun setSupplementalUpdatedAt(
        profileId: Int,
        feature: SharedPreferencesSyncFeature,
        updatedAt: Long
    ) {
        context.getSharedPreferences("profile_settings_sync_metadata", Context.MODE_PRIVATE)
            .edit()
            .putLong(supplementalUpdatedAtKey(profileId, feature), updatedAt)
            .apply()
    }

    private fun getSupplementalContentSignature(
        profileId: Int,
        feature: SharedPreferencesSyncFeature
    ): String? = context.getSharedPreferences("profile_settings_sync_metadata", Context.MODE_PRIVATE)
        .getString(supplementalContentSignatureKey(profileId, feature), null)

    private fun setSupplementalContentSignature(
        profileId: Int,
        feature: SharedPreferencesSyncFeature,
        signature: String
    ) {
        context.getSharedPreferences("profile_settings_sync_metadata", Context.MODE_PRIVATE)
            .edit()
            .putString(supplementalContentSignatureKey(profileId, feature), signature)
            .apply()
    }

    private fun sharedPreferenceFeatureContentSignature(
        profileId: Int,
        feature: SharedPreferencesSyncFeature
    ): String {
        val values = exportSharedPreferencesFeature(profileId, feature)
            .filterKeys { it != SUPPLEMENTAL_UPDATED_AT_KEY && it != SUPPLEMENTAL_CONTENT_SIGNATURE_KEY }
        return buildFeatureSignature(JsonObject(values), feature.featureName)
    }

    private fun recordSharedPreferencesChange(profileId: Int, feature: SharedPreferencesSyncFeature) {
        val contentSignature = sharedPreferenceFeatureContentSignature(profileId, feature)
        val metadata = context.getSharedPreferences("profile_settings_sync_metadata", Context.MODE_PRIVATE)
        val signatureKey = supplementalContentSignatureKey(profileId, feature)
        val priorSignature = metadata.getString(signatureKey, null)
        if (contentSignature == priorSignature) return
        val timeKey = supplementalUpdatedAtKey(profileId, feature)
        val next = maxOf(System.currentTimeMillis(), metadata.getLong(timeKey, 0L) + 1L)
        metadata.edit()
            .putLong(timeKey, next)
            .putString(signatureKey, contentSignature)
            .apply()
    }

    private suspend fun importSettingsBlob(profileId: Int, featuresJson: JsonObject) {
        applyingRemoteBlob = true
        try {
            dataStoreSyncedFeatures.forEach { feature ->
                val featureJson = featuresJson[feature]?.jsonObject ?: return@forEach
                if (feature == PluginDataStore.FEATURE) {
                    importPluginSettings(profileId, featureJson)
                    return@forEach
                }
                profileDataStoreFactory.get(profileId, feature).edit { mutablePrefs ->
                    val preservedEntries = captureLocalOnlyPreferenceEntries(feature, mutablePrefs)
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
                        if (shouldExcludePreferenceFromProfileSettingsSync(feature, keyName)) {
                            if (feature != "layout_settings" || keyName != "search_discover_enabled") return@forEach
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

                    restorePreferenceEntries(mutablePrefs, preservedEntries)
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

            featuresJson[CALENDAR_SETTINGS_FEATURE]?.jsonObject?.takeIf { it.isNotEmpty() }?.let { featureJson ->
                calendarPreferences.applyProfileSyncSnapshot(
                    profileId,
                    decodeCalendarSettingsFeature(featureJson)
                )
            }
            featuresJson[SPORTS_SETTINGS_FEATURE]?.jsonObject?.takeIf { it.isNotEmpty() }?.let { featureJson ->
                liveTvPreferences.applySportsProfileSyncSnapshot(
                    profileId,
                    decodeSportsSettingsFeature(featureJson)
                )
            }
            val freeboxSettingsSnapshot = featuresJson[FREEBOX_SETTINGS_FEATURE]
                ?.jsonPrimitive
                ?.contentOrNull
            if (!freeboxSettingsSnapshot.isNullOrBlank()) {
                freeboxRepository.applyProfileSettingsSyncSnapshot(freeboxSettingsSnapshot)
            }
            featuresJson[PROFILE_BEHAVIOR_FEATURE]?.jsonObject?.takeIf { it.isNotEmpty() }?.let { featureJson ->
                profileDataStore.applySettingsSyncSnapshot(
                    profileId,
                    decodeProfileBehaviorFeature(featureJson)
                )
            }
            val sentryEnabled = featuresJson[SENTRY_SETTINGS_FEATURE]
                ?.jsonObject
                ?.get("enabled")
                ?.jsonPrimitive
                ?.contentOrNull
                ?.toBooleanStrictOrNull()
            if (sentryEnabled != null) {
                sentrySettingsDataStore.setEnabled(sentryEnabled)
            }

            val changedSupplementalFeatures = mutableSetOf<String>()
            sharedPreferencesSyncFeatures.forEach { feature ->
                val featureJson = featuresJson[feature.featureName]?.jsonObject ?: return@forEach
                if (importSharedPreferencesFeature(profileId, feature, featureJson)) {
                    changedSupplementalFeatures += feature.featureName
                }
            }
            val enrichmentSnapshot = featuresJson[CONTINUE_WATCHING_ENRICHMENT_FEATURE]
                ?.jsonPrimitive?.contentOrNull
            if (enrichmentSnapshot != null) {
                // This cache is optional. A malformed or older cache payload must not abort the
                // profile import before IPTV preference change notifications are delivered.
                val enrichmentChanged = runCatching {
                    cwEnrichmentCache.importForDeviceSync(profileId, enrichmentSnapshot)
                }.onFailure { error ->
                    Log.w(TAG, "Failed to import Continue Watching enrichment", error)
                }.getOrDefault(false)
                if (enrichmentChanged) {
                    changedSupplementalFeatures += CONTINUE_WATCHING_ENRICHMENT_FEATURE
                }
            }
            if (changedSupplementalFeatures.isNotEmpty()) {
                if (APP_LOCALE_FEATURE in changedSupplementalFeatures) {
                    com.nuvio.tv.LocaleCache.localeTag = context
                        .getSharedPreferences(APP_LOCALE_FEATURE, Context.MODE_PRIVATE)
                        .getString("locale_tag", null)
                        .orEmpty()
                }
                if (YOUTUBE_SETTINGS_FEATURE in changedSupplementalFeatures) {
                    com.nuvio.tv.youtube.YouTubeSettings.notifySettingsChangedFromSync()
                }
                if (IPTV_MENU_SETTINGS_FEATURE in changedSupplementalFeatures) {
                    com.nuvio.tv.reshaped.livetv.LiveTvPreferences.notifySettingsChangedFromSync(context)
                }
                _supplementalSettingsImported.tryEmit(
                    ProfileSettingsSupplementalChange(profileId, changedSupplementalFeatures)
                )
            }
        } finally {
            applyingRemoteBlob = false
        }
    }

    private fun observeLocalSettingsChangesAndSync() {
        scope.launch {
            profileManager.activeProfileId
                .flatMapLatest { profileId ->
                    val featureFlows = dataStoreSyncedFeatures.map { feature ->
                        profileDataStoreFactory.get(profileId, feature).data
                            .map { prefs ->
                                "$feature={${buildFeatureSignature(prefs, feature)}}"
                            }
                    }
                    val calendarSettingsFlow = calendarPreferences.profileSyncSnapshot(profileId)
                        .map { snapshot ->
                            "$CALENDAR_SETTINGS_FEATURE={${buildFeatureSignature(encodeCalendarSettingsFeature(snapshot), CALENDAR_SETTINGS_FEATURE)}}"
                        }
                    val sportsSettingsFlow = liveTvPreferences.sportsProfileSyncSnapshot(profileId)
                        .map { snapshot ->
                            "$SPORTS_SETTINGS_FEATURE={${buildFeatureSignature(encodeSportsSettingsFeature(snapshot), SPORTS_SETTINGS_FEATURE)}}"
                        }
                    val freeboxSettingsFlow = freeboxRepository.state
                        .map { "$FREEBOX_SETTINGS_FEATURE=${freeboxRepository.profileSettingsSyncSnapshot()}" }
                        .distinctUntilChanged()
                    val profileBehaviorFlow = profileDataStore.settingsSyncSnapshot(profileId)
                        .map { snapshot ->
                            "$PROFILE_BEHAVIOR_FEATURE={${buildFeatureSignature(encodeProfileBehaviorFeature(snapshot), PROFILE_BEHAVIOR_FEATURE)}}"
                        }
                    val sentrySettingsFlow = sentrySettingsDataStore.enabled
                        .map { enabled ->
                            "$SENTRY_SETTINGS_FEATURE={${buildFeatureSignature(encodeSentrySettingsFeature(enabled), SENTRY_SETTINGS_FEATURE)}}"
                        }
                    val supplementalFlows = listOf(
                        sharedPreferencesChanges(profileId),
                        calendarSettingsFlow,
                        sportsSettingsFlow,
                        freeboxSettingsFlow,
                        profileBehaviorFlow,
                        sentrySettingsFlow,
                        enrichmentCacheChanges(profileId)
                    )
                    combine(featureFlows + supplementalFlows) { signatures ->
                        profileId to signatures.joinToString(separator = "||")
                    }
                        .distinctUntilChanged()
                        // The first value is the profile's stored state, not a local edit. On a
                        // profile switch it can be older than the cloud copy that is about to be
                        // pulled, so pushing it would overwrite newer settings.
                        .drop(1)
                }
                .debounce(SETTINGS_PUSH_DEBOUNCE_MS)
                .collect { (profileId, signature) ->
                    if (!authManager.isAuthenticated) return@collect
                    if (applyingRemoteBlob) return@collect
                    if (profileDataStoreFactory.corruptedFileNames.isNotEmpty()) {
                        Log.w(TAG, "DataStore corruption detected (${profileDataStoreFactory.corruptedFileNames}) — pulling from remote instead of pushing")
                        profileDataStoreFactory.corruptedFileNames.clear()
                        pullCurrentProfileFromRemote()
                        return@collect
                    }
                    if (signature == skipNextPushSignature) {
                        skipNextPushSignature = null
                        return@collect
                    }
                    // A change debounced across a profile switch still belongs to the profile it was made on.
                    pushProfileSettingsToRemote(profileId)
                }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun sharedPreferencesChanges(profileId: Int): Flow<String> = callbackFlow {
        val registrations = sharedPreferencesSyncFeatures.map { feature ->
            val preferences = context.getSharedPreferences(feature.preferencesName, Context.MODE_PRIVATE)
            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
                val belongsToProfile = changedKey == null ||
                    sharedPreferenceKeyBelongsToProfile(changedKey, profileId, feature)
                if (!applyingRemoteBlob && belongsToProfile) {
                    recordSharedPreferencesChange(profileId, feature)
                }
                trySend(buildSharedPreferencesSignature(profileId))
            }
            preferences.registerOnSharedPreferenceChangeListener(listener)
            preferences to listener
        }
        trySend(buildSharedPreferencesSignature(profileId))
        awaitClose {
            registrations.forEach { (preferences, listener) ->
                preferences.unregisterOnSharedPreferenceChangeListener(listener)
            }
        }
    }

    private fun buildSharedPreferencesSignature(profileId: Int): String =
        sharedPreferencesSyncFeatures.joinToString(separator = "||") { feature ->
            val value = exportSharedPreferencesFeature(profileId, feature)
            "${feature.featureName}={${buildFeatureSignature(value, feature.featureName)}}"
        }

    private fun enrichmentCacheChanges(profileId: Int): Flow<String> =
        cwEnrichmentCache.snapshotVersion
            .drop(1)
            .sample(CONTINUE_WATCHING_ENRICHMENT_SYNC_INTERVAL_MS)
            .map { enrichmentCacheSignature(profileId) }
            .onStart { emit(enrichmentCacheSignature(profileId)) }

    private suspend fun enrichmentCacheSignature(profileId: Int): String =
        "$CONTINUE_WATCHING_ENRICHMENT_FEATURE=${cwEnrichmentCache.exportForDeviceSync(profileId).orEmpty()}"

    private suspend fun buildSettingsSignature(profileId: Int): String {
        val featuresJson = exportSettingsBlob(profileId)["features"]?.jsonObject ?: JsonObject(emptyMap())
        return buildSettingsSignature(featuresJson)
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
        val hasLocalOnly = featureJson.keys.any { it in localOnlyLayoutProfileSettingsKeys }
        if (!hasLegacy && !hasNewKey && !hasLocalOnly) return featureJson
        val newDiscoverString = extractDiscoverLocationString(featureJson)
        if (!hasLegacy && newDiscoverString != null && !hasLocalOnly) return featureJson
        return buildJsonObject {
            featureJson.forEach { (keyName, encodedValue) ->
                when {
                    keyName == "search_discover_enabled" -> return@forEach
                    keyName == "discover_location" && newDiscoverString == null -> return@forEach
                    keyName in localOnlyLayoutProfileSettingsKeys -> return@forEach
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
        val signatures = ArrayList<String>(syncedFeatures.size)
        dataStoreSyncedFeatures.forEach { feature ->
            val featureJson = featuresJson[feature]?.jsonObject ?: JsonObject(emptyMap())
            val normalized = if (feature == "layout_settings") {
                normalizeLayoutSettingsForSignature(featureJson)
            } else {
                featureJson
            }
            signatures += "$feature={${buildFeatureSignature(normalized, feature)}}"
        }
        sharedPreferencesSyncFeatures.forEach { feature ->
            val featureJson = featuresJson[feature.featureName]?.jsonObject ?: JsonObject(emptyMap())
            signatures += "${feature.featureName}={${buildFeatureSignature(featureJson, feature.featureName)}}"
        }
        val calendarSettings = featuresJson[CALENDAR_SETTINGS_FEATURE]?.jsonObject ?: JsonObject(emptyMap())
        signatures += "$CALENDAR_SETTINGS_FEATURE={${buildFeatureSignature(calendarSettings, CALENDAR_SETTINGS_FEATURE)}}"
        val sportsSettings = featuresJson[SPORTS_SETTINGS_FEATURE]?.jsonObject ?: JsonObject(emptyMap())
        signatures += "$SPORTS_SETTINGS_FEATURE={${buildFeatureSignature(sportsSettings, SPORTS_SETTINGS_FEATURE)}}"
        val freeboxSettings = featuresJson[FREEBOX_SETTINGS_FEATURE]?.jsonPrimitive?.contentOrNull.orEmpty()
        signatures += "$FREEBOX_SETTINGS_FEATURE=$freeboxSettings"
        val profileBehavior = featuresJson[PROFILE_BEHAVIOR_FEATURE]?.jsonObject ?: JsonObject(emptyMap())
        signatures += "$PROFILE_BEHAVIOR_FEATURE={${buildFeatureSignature(profileBehavior, PROFILE_BEHAVIOR_FEATURE)}}"
        val sentrySettings = featuresJson[SENTRY_SETTINGS_FEATURE]?.jsonObject ?: JsonObject(emptyMap())
        signatures += "$SENTRY_SETTINGS_FEATURE={${buildFeatureSignature(sentrySettings, SENTRY_SETTINGS_FEATURE)}}"
        val enrichment = featuresJson[CONTINUE_WATCHING_ENRICHMENT_FEATURE]
            ?.jsonPrimitive?.contentOrNull.orEmpty()
        signatures += "$CONTINUE_WATCHING_ENRICHMENT_FEATURE=$enrichment"
        return signatures.joinToString(separator = "||")
    }

    private fun buildFeatureSignature(prefs: Preferences, feature: String = ""): String {
        return prefs.asMap()
            .entries
            .mapNotNull { (key, rawValue) ->
                if (shouldExcludePreferenceFromProfileSettingsSync(feature, key.name)) return@mapNotNull null
                encodePreferenceValue(rawValue)?.let { encoded ->
                    key.name to encoded.toString()
                }
            }
            .sortedBy { it.first }
            .joinToString(separator = "|") { (key, value) -> "$key=$value" }
    }

    private fun buildFeatureSignature(featureJson: JsonObject, feature: String = ""): String {
        return featureJson.entries
            .filterNot { (key, _) -> shouldExcludePreferenceFromProfileSettingsSync(feature, key) }
            .sortedBy { it.key }
            .joinToString(separator = "|") { (key, value) -> "$key=$value" }
    }

    private fun captureLocalOnlyPreferenceEntries(
        feature: String,
        mutablePrefs: MutablePreferences
    ): Map<Preferences.Key<*>, Any> {
        val keyNames = when (feature) {
            "layout_settings" -> catalogKeysExcludedFromProfileSettingsBlob + localOnlyLayoutProfileSettingsKeys
            PLAYER_SETTINGS_FEATURE -> localOnlyPlayerProfileSettingsKeys
            "trailer_settings" -> localOnlyTrailerProfileSettingsKeys
            "installed_apps_row" -> localOnlyInstalledAppSettingsKeys
            "library_preferences" -> localOnlyLibrarySyncKeys
            else -> credentialProfileSettingsKeys[feature].orEmpty()
        }
        if (keyNames.isEmpty()) return emptyMap()
        val entries = mutableMapOf<Preferences.Key<*>, Any>()
        keyNames.forEach { keyName ->
            val stringKey = stringPreferencesKey(keyName)
            runCatching { mutablePrefs[stringKey] }.getOrNull()?.let { entries[stringKey] = it }
            val booleanKey = booleanPreferencesKey(keyName)
            runCatching { mutablePrefs[booleanKey] }.getOrNull()?.let { entries[booleanKey] = it }
            val intKey = intPreferencesKey(keyName)
            runCatching { mutablePrefs[intKey] }.getOrNull()?.let { entries[intKey] = it }
            val longKey = longPreferencesKey(keyName)
            runCatching { mutablePrefs[longKey] }.getOrNull()?.let { entries[longKey] = it }
            val floatKey = floatPreferencesKey(keyName)
            runCatching { mutablePrefs[floatKey] }.getOrNull()?.let { entries[floatKey] = it }
            val doubleKey = doublePreferencesKey(keyName)
            runCatching { mutablePrefs[doubleKey] }.getOrNull()?.let { entries[doubleKey] = it }
            val stringSetKey = stringSetPreferencesKey(keyName)
            runCatching { mutablePrefs[stringSetKey] }.getOrNull()?.let { entries[stringSetKey] = it }
        }
        return entries
    }

    @Suppress("UNCHECKED_CAST")
    private fun restorePreferenceEntries(
        mutablePrefs: MutablePreferences,
        entries: Map<Preferences.Key<*>, Any>
    ) {
        val gson = com.google.gson.Gson()
        entries.forEach { (key, value) ->
            when (value) {
                is String -> mutablePrefs[key as Preferences.Key<String>] = value
                is Boolean -> mutablePrefs[key as Preferences.Key<Boolean>] = value
                is Int -> mutablePrefs[key as Preferences.Key<Int>] = value
                is Long -> mutablePrefs[key as Preferences.Key<Long>] = value
                is Float -> mutablePrefs[key as Preferences.Key<Float>] = value
                is Double -> mutablePrefs[key as Preferences.Key<Double>] = value
                is Set<*> -> {
                    if (value.all { it is String }) {
                        // Catalog keys should be stored as JSON strings, not Sets.
                        // Convert to prevent ClassCastException on read.
                        if (key.name in catalogKeysExcludedFromProfileSettingsBlob) {
                            val jsonValue = gson.toJson((value as Set<String>).toList())
                            mutablePrefs[stringPreferencesKey(key.name)] = jsonValue
                        } else {
                            mutablePrefs[key as Preferences.Key<Set<String>>] = value as Set<String>
                        }
                    }
                }
            }
        }
    }

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

    private fun decodePreferenceValue(encodedValue: JsonElement): Any? {
        val obj = encodedValue as? JsonObject ?: return null
        val type = obj["type"]?.jsonPrimitive?.contentOrNull ?: return null
        val value = obj["value"] ?: JsonNull
        return when (type) {
            "string" -> value.jsonPrimitive.contentOrNull
            "boolean" -> value.jsonPrimitive.contentOrNull?.toBooleanStrictOrNull()
            "int" -> value.jsonPrimitive.intOrNull
            "long" -> value.jsonPrimitive.longOrNull
            "float" -> value.jsonPrimitive.floatOrNull
            "double" -> value.jsonPrimitive.doubleOrNull
            "string_set" -> value.jsonArray.mapNotNull { it.jsonPrimitive.contentOrNull }.toSet()
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
