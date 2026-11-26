package com.nuvio.tv.reshaped.livetv

import android.content.Context
import android.content.SharedPreferences
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.util.UUID
import org.json.JSONArray
import org.json.JSONObject

/**
 * Live TV's saved sources, favorites, hidden categories and channel history, per profile. Small
 * values live in their own preferences file (read once, off the startup path); an imported
 * playlist is a file per source, because it can be megabytes and preferences keep everything in
 * memory and rewrite it on each save.
 */
internal class LiveTvStorage(context: Context, private val profileId: Int) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    private val playlistDir = File(context.applicationContext.filesDir, "live_tv")

    private fun key(base: String) = "${base}_$profileId"
    private fun string(base: String): String? = prefs.getString(key(base), null)?.takeIf(String::isNotBlank)
    private fun SharedPreferences.Editor.putOrRemove(base: String, value: String?): SharedPreferences.Editor =
        apply { if (value.isNullOrBlank()) remove(key(base)) else putString(key(base), value) }

    // region Sources

    /** The saved sources, in the order they were added. A single source saved by an older version is moved over. */
    fun sources(): List<LiveTvSource> {
        val saved = string(SOURCES) ?: return migrateLegacySource()
        return runCatching {
            val array = JSONArray(saved)
            (0 until array.length()).mapNotNull { index -> array.optJSONObject(index)?.toSource() }
        }.getOrDefault(emptyList())
    }

    fun saveSources(sources: List<LiveTvSource>) {
        val array = JSONArray()
        sources.forEach { array.put(it.toJson()) }
        // An empty list is saved too, so an older single source is not moved over again.
        prefs.edit().putString(key(SOURCES), array.toString()).apply()
    }

    /** Provider-backed sources can move between devices; imported M3U files stay on this device. */
    fun synchronizedSources(): List<LiveTvSource> = sources().filter(::isSynchronizedSource)

    fun isSynchronizedSource(source: LiveTvSource): Boolean =
        source.type != LiveTvSourceType.M3u ||
            source.url.startsWith("http://", ignoreCase = true) ||
            source.url.startsWith("https://", ignoreCase = true)

    fun encodeSources(sources: List<LiveTvSource>): String {
        val array = JSONArray()
        sources.forEach { array.put(it.toJson()) }
        return array.toString()
    }

    fun decodeSources(json: String): List<LiveTvSource> {
        val array = JSONArray(json)
        return (0 until array.length()).mapNotNull { index -> array.optJSONObject(index)?.toSource() }
    }

    fun newSourceId(): String = UUID.randomUUID().toString().replace("-", "").take(12)

    private fun JSONObject.toSource(): LiveTvSource? {
        val id = optString("id").takeIf(String::isNotBlank) ?: return null
        val type = LiveTvSourceType.entries.firstOrNull { it.name == optString("type") } ?: return null
        return LiveTvSource(
            id = id,
            type = type,
            url = optString("url"),
            stalker = LiveTvStalkerSettings(
                portalUrl = optString("portal"), macAddress = optString("mac"),
                username = optString("stalkerUser"), password = optString("stalkerPassword"),
                serialNumber = optString("stalkerSn"), deviceId = optString("stalkerDeviceId"),
                deviceId2 = optString("stalkerDeviceId2"), deviceVersion = optString("stalkerVersion"),
                deviceModel = optString("stalkerModel"), timezone = optString("stalkerTimezone"),
                httpProxy = optString("stalkerProxy"), httpHeaders = optString("stalkerHeaders"),
                httpCookie = optString("stalkerCookie"), stbUserAgent = optString("stalkerStbUa"),
                playerUserAgent = optString("stalkerPlayerUa"), requestMethod = optString("stalkerMethod", "GET"),
            ),
            xtream = LiveTvXtreamSettings(optString("server"), optString("xtreamUser"), optString("xtreamPassword")),
            epgUrl = optString("epg"),
            name = optString("name"),
        )
    }

    private fun LiveTvSource.toJson(): JSONObject = JSONObject().apply {
        put("id", id)
        put("type", type.name)
        put("url", url)
        if (epgUrl.isNotBlank()) put("epg", epgUrl)
        if (name.isNotBlank()) put("name", name)
        when (type) {
            LiveTvSourceType.M3u -> Unit
            LiveTvSourceType.Stalker -> {
                put("portal", stalker.portalUrl)
                put("mac", stalker.macAddress)
                put("stalkerUser", stalker.username)
                put("stalkerPassword", stalker.password)
                put("stalkerSn", stalker.serialNumber)
                put("stalkerDeviceId", stalker.deviceId)
                put("stalkerDeviceId2", stalker.deviceId2)
                put("stalkerVersion", stalker.deviceVersion)
                put("stalkerModel", stalker.deviceModel)
                put("stalkerTimezone", stalker.timezone)
                put("stalkerProxy", stalker.httpProxy)
                put("stalkerHeaders", stalker.httpHeaders)
                put("stalkerCookie", stalker.httpCookie)
                put("stalkerStbUa", stalker.stbUserAgent)
                put("stalkerPlayerUa", stalker.playerUserAgent)
                put("stalkerMethod", stalker.requestMethod)
            }
            LiveTvSourceType.Xtream -> {
                put("server", xtream.serverUrl)
                put("xtreamUser", xtream.username)
                put("xtreamPassword", xtream.password)
            }
        }
    }

    /** Versions before multiple sources kept one active source in separate keys. */
    private fun migrateLegacySource(): List<LiveTvSource> {
        val type = LiveTvSourceType.entries.firstOrNull { it.name == string(LEGACY_SOURCE_TYPE) }
        val url = string(LEGACY_SOURCE_URL).orEmpty()
        val id = "main"
        val source = when (type) {
            LiveTvSourceType.M3u -> {
                val legacyFile = File(playlistDir, "playlist_$profileId.m3u")
                if (legacyFile.isFile) legacyFile.renameTo(playlistFileFor(id))
                LiveTvSource(id, LiveTvSourceType.M3u, url).takeIf { url.isNotBlank() }
            }
            LiveTvSourceType.Xtream -> LiveTvSource(
                id, LiveTvSourceType.Xtream, url,
                xtream = LiveTvXtreamSettings(string(XTREAM_SERVER).orEmpty(), string(XTREAM_USER).orEmpty(), string(XTREAM_PASSWORD).orEmpty()),
            ).takeIf { it.xtream.isConfigured }
            LiveTvSourceType.Stalker -> LiveTvSource(
                id, LiveTvSourceType.Stalker, url,
                stalker = LiveTvStalkerSettings(string(STALKER_PORTAL).orEmpty(), string(STALKER_MAC).orEmpty(), string(STALKER_USER).orEmpty(), string(STALKER_PASSWORD).orEmpty()),
            ).takeIf { it.stalker.isConfigured }
            null -> null
        }
        val sources = listOfNotNull(source)
        saveSources(sources)
        prefs.edit().apply {
            listOf(
                LEGACY_SOURCE_TYPE, LEGACY_SOURCE_URL, STALKER_PORTAL, STALKER_MAC, STALKER_USER, STALKER_PASSWORD,
                XTREAM_SERVER, XTREAM_USER, XTREAM_PASSWORD,
            ).forEach { remove(key(it)) }
        }.apply()
        return sources
    }

    // endregion

    // region Imported playlists

    private fun playlistFileFor(sourceId: String) = File(playlistDir, "playlist_${profileId}_$sourceId.m3u")

    /** The imported playlist of [sourceId], or null. */
    fun playlistFile(sourceId: String): File? = playlistFileFor(sourceId).takeIf { it.isFile && it.length() > 0L }

    /** Saves an imported playlist for [sourceId] from [write]; call off the main thread. */
    fun savePlaylistFile(sourceId: String, write: (File) -> Unit): File {
        val target = playlistFileFor(sourceId)
        playlistDir.mkdirs()
        val temp = File(target.path + ".tmp")
        write(temp)
        if (!temp.renameTo(target)) {
            target.delete()
            temp.renameTo(target)
        }
        return target
    }

    fun deletePlaylistFile(sourceId: String) {
        playlistFileFor(sourceId).delete()
    }

    // endregion

    // Category names are saved one per line; the blank "Uncategorised" key gets a marker line.
    private fun decodeGroups(base: String): Sequence<String> =
        string(base)?.lineSequence()?.filter(String::isNotEmpty)?.map { if (it == UNGROUPED_LINE) LIVE_TV_UNGROUPED else it }
            ?: emptySequence()

    private fun encodeGroups(groups: Collection<String>): String =
        groups.joinToString("\n") { if (it == LIVE_TV_UNGROUPED) UNGROUPED_LINE else it }

    fun hiddenGroups(): Set<String> = decodeGroups(HIDDEN_GROUPS).toHashSet()

    fun saveHiddenGroups(groups: Set<String>) {
        prefs.edit().putOrRemove(HIDDEN_GROUPS, encodeGroups(groups)).apply()
    }

    /** Categories the viewer hid in the movie and series catalogs. */
    fun hiddenVodCategoryKeys(): Set<String> = runCatching {
        val array = JSONArray(string(HIDDEN_VOD_CATEGORIES) ?: return emptySet())
        (0 until array.length()).mapNotNull { array.optString(it).takeIf(String::isNotBlank) }.toSet()
    }.getOrDefault(emptySet())

    fun saveHiddenVodCategoryKeys(keys: Set<String>) {
        val array = JSONArray()
        keys.forEach(array::put)
        prefs.edit().putOrRemove(HIDDEN_VOD_CATEGORIES, if (keys.isEmpty()) null else array.toString()).apply()
    }

    /** Remembers equivalent IDs when an account sync replaces a local copy of the same source. */
    fun rememberVodSourceIdAlias(oldSourceId: String, canonicalSourceId: String) = synchronized(prefs) {
        if (oldSourceId.isBlank() || canonicalSourceId.isBlank() || oldSourceId == canonicalSourceId) {
            return@synchronized
        }
        val aliases = vodSourceIdAliasesLocked().toMutableMap()
        aliases.keys.toList().forEach { key ->
            if (resolveVodSourceIdAlias(key, aliases) == oldSourceId) {
                aliases[key] = canonicalSourceId
            }
        }
        aliases[oldSourceId] = canonicalSourceId
        aliases.remove(canonicalSourceId)
        aliases.keys.toList().forEach { key ->
            val resolved = resolveVodSourceIdAlias(key, aliases)
            if (key == resolved) aliases.remove(key) else aliases[key] = resolved
        }
        val json = JSONObject()
        aliases.forEach { (oldId, newId) -> json.put(oldId, newId) }
        prefs.edit().putString(key(VOD_SOURCE_ID_ALIASES), json.toString()).apply()
    }

    fun remapVodFavoriteKeys(keys: Set<String>): Set<String> = synchronized(prefs) {
        remapVodSourceKeys(keys, vodSourceIdAliasesLocked())
    }

    fun remapStoredVodFavoriteKeys(): Boolean = synchronized(prefs) {
        val current = favoriteVodKeys()
        val remapped = remapVodFavoriteKeys(current)
        if (current == remapped) return@synchronized false
        saveFavoriteVodKeys(remapped)
        true
    }

    fun remapVodCategoryKeys(keys: Set<String>): Set<String> = synchronized(prefs) {
        val aliases = vodSourceIdAliasesLocked()
        keys.mapTo(LinkedHashSet()) { categoryKey ->
            val parts = categoryKey.split('\u001f', limit = 3)
            if (parts.size != 3) return@mapTo categoryKey
            val canonicalSourceId = aliases[parts[1]] ?: return@mapTo categoryKey
            "${parts[0]}\u001f$canonicalSourceId\u001f${parts[2]}"
        }
    }

    fun remapStoredVodCategoryKeys(): Boolean = synchronized(prefs) {
        val current = hiddenVodCategoryKeys()
        val remapped = remapVodCategoryKeys(current)
        if (current == remapped) return@synchronized false
        saveHiddenVodCategoryKeys(remapped)
        true
    }

    private fun remapVodSourceKeys(
        keys: Set<String>,
        aliases: Map<String, String>,
    ): Set<String> = keys.mapTo(LinkedHashSet()) { value ->
        val sourceSeparator = value.indexOf(':')
        if (sourceSeparator <= 0) return@mapTo value
        val sourceId = value.substring(0, sourceSeparator)
        val afterSource = value.substring(sourceSeparator + 1)
        val typeSeparator = afterSource.indexOf(':')
        if (typeSeparator <= 0) return@mapTo value
        val type = afterSource.substring(0, typeSeparator)
        val itemId = afterSource.substring(typeSeparator + 1)
        val canonicalSourceId = aliases[sourceId] ?: sourceId

        // VOD IDs include the source ID too (source:type:source:type:providerId). When a local
        // source is reconciled with the account-wide ID, update both parts of the favorite key.
        val nestedSourceSeparator = itemId.indexOf(':')
        val nestedTypeSeparator = itemId.indexOf(':', nestedSourceSeparator + 1)
        val canonicalItemId = if (nestedSourceSeparator > 0 && nestedTypeSeparator > nestedSourceSeparator) {
            val nestedSourceId = itemId.substring(0, nestedSourceSeparator)
            val nestedType = itemId.substring(nestedSourceSeparator + 1, nestedTypeSeparator)
            val canonicalNestedSourceId = aliases[nestedSourceId]
            if (nestedType == type && canonicalNestedSourceId != null) {
                "$canonicalNestedSourceId:${itemId.substring(nestedSourceSeparator + 1)}"
            } else {
                itemId
            }
        } else {
            itemId
        }
        "$canonicalSourceId:$type:$canonicalItemId"
    }

    private fun vodSourceIdAliasesLocked(): Map<String, String> {
        val raw = prefs.getString(key(VOD_SOURCE_ID_ALIASES), null) ?: return emptyMap()
        val json = runCatching { JSONObject(raw) }.getOrNull() ?: return emptyMap()
        return json.keys().asSequence().mapNotNull { oldId ->
            json.optString(oldId).takeIf(String::isNotBlank)?.let { oldId to it }
        }.toMap()
    }

    private fun resolveVodSourceIdAlias(sourceId: String, aliases: Map<String, String>): String {
        var current = sourceId
        repeat(32) {
            val next = aliases[current]?.takeIf(String::isNotBlank) ?: return current
            if (next == current) return current
            current = next
        }
        return current
    }

    // region Hidden channels

    private val hiddenChannelsFile get() = File(playlistDir, "hidden_channels_$profileId.bin")

    /**
     * Hidden channels, as [LiveTvChannel.hideKey]s in a small file of their own (8 bytes each), so
     * hiding a large category does not grow the preferences every other save rewrites. Call off
     * the main thread.
     */
    fun hiddenChannelKeys(): Set<Long> {
        // Older builds kept them as links in the preferences; those can't be matched to keys.
        if (prefs.contains(key(LEGACY_HIDDEN_CHANNELS))) prefs.edit().remove(key(LEGACY_HIDDEN_CHANNELS)).apply()
        val file = hiddenChannelsFile
        if (!file.isFile) return emptySet()
        return runCatching {
            DataInputStream(file.inputStream().buffered()).use { input ->
                val count = file.length().toInt() / Long.SIZE_BYTES
                HashSet<Long>(count * 2).apply { repeat(count) { add(input.readLong()) } }
            }
        }.getOrDefault(emptySet())
    }

    fun saveHiddenChannelKeys(keys: Set<Long>) {
        val target = hiddenChannelsFile
        if (keys.isEmpty()) {
            target.delete()
            markHiddenChannelKeysChanged()
            return
        }
        playlistDir.mkdirs()
        val temp = File(target.path + ".tmp")
        val saved = runCatching {
            DataOutputStream(temp.outputStream().buffered()).use { out -> keys.forEach(out::writeLong) }
            if (!temp.renameTo(target)) {
                target.delete()
                check(temp.renameTo(target))
            }
            true
        }.getOrDefault(false)
        if (saved) markHiddenChannelKeysChanged()
    }

    private fun markHiddenChannelKeysChanged() {
        val marker = key(HIDDEN_CHANNEL_KEYS_SYNC_MARKER)
        prefs.edit().putLong(marker, prefs.getLong(marker, 0L) + 1L).apply()
    }

    // endregion

    /** Names the viewer gave categories, by the playlist's name ("" is Uncategorised). */
    fun groupNames(): Map<String, String> = runCatching {
        val json = JSONObject(string(GROUP_NAMES) ?: return emptyMap())
        json.keys().asSequence().associateWith(json::getString).filterValues(String::isNotBlank)
    }.getOrDefault(emptyMap())

    fun saveGroupNames(names: Map<String, String>) {
        prefs.edit().putOrRemove(GROUP_NAMES, if (names.isEmpty()) null else JSONObject(names).toString()).apply()
    }

    /** Categories in the order the viewer put them; ones not in it follow, A to Z. */
    fun groupOrder(): List<String> = decodeGroups(GROUP_ORDER).toList()

    fun saveGroupOrder(groups: List<String>) {
        prefs.edit().putOrRemove(GROUP_ORDER, encodeGroups(groups)).apply()
    }

    /** Categories in the sidebar rail in the viewer's custom order. */
    fun categoryRailOrder(): List<String> = decodeGroups(CATEGORY_RAIL_ORDER).toList()

    fun saveCategoryRailOrder(order: List<String>) {
        prefs.edit().putOrRemove(CATEGORY_RAIL_ORDER, encodeGroups(order)).apply()
    }

    /** The viewer's own playlists, oldest first. */
    fun customLists(): List<LiveTvCustomList> = runCatching {
        val array = JSONArray(string(CUSTOM_LISTS) ?: return emptyList())
        (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            val id = item.optString("id").takeIf(String::isNotBlank) ?: return@mapNotNull null
            val urls = item.optJSONArray("urls")
            LiveTvCustomList(id, item.optString("name"), (0 until (urls?.length() ?: 0)).mapNotNull { urls?.optString(it)?.takeIf(String::isNotBlank) })
        }.sortedBy { it.id }
    }.getOrDefault(emptyList())

    fun saveCustomLists(lists: List<LiveTvCustomList>) {
        val array = JSONArray()
        lists.forEach { list ->
            array.put(JSONObject().put("id", list.id).put("name", list.name).put("urls", JSONArray(list.urls)))
        }
        prefs.edit().putOrRemove(CUSTOM_LISTS, if (lists.isEmpty()) null else array.toString()).apply()
    }

    fun favoriteUrls(): Set<String> =
        string(FAVORITES)?.lineSequence()?.map(String::trim)?.filter(String::isNotBlank)?.toHashSet().orEmpty()

    fun saveFavoriteUrls(urls: Set<String>) {
        prefs.edit().putOrRemove(FAVORITES, urls.joinToString("\n")).apply()
    }

    /** Stable channel identities synchronized between devices; unlike URLs these contain no credentials. */
    fun favoriteChannelKeys(): Set<Long> =
        string(FAVORITE_CHANNEL_KEYS)?.lineSequence()?.mapNotNull(String::toLongOrNull)?.toSet().orEmpty()

    /** True once a device has imported or migrated the stable favorite identity snapshot. */
    fun hasFavoriteChannelKeysSnapshot(): Boolean = prefs.contains(key(FAVORITE_CHANNEL_KEYS))

    fun saveFavoriteChannelKeys(keys: Set<Long>) {
        // Keep an empty value too: it means the user has no channel favorites, rather than that
        // the app should migrate stale stream URLs from an older local-only snapshot.
        val value = keys.sorted().joinToString("\n")
        synchronized(prefs) {
            val editor = prefs.edit()
            if (!prefs.getBoolean(key(CHANNEL_FAVORITES_SYNC_PENDING), false)) {
                editor.putString(key(CHANNEL_FAVORITES_SYNC_BASE), prefs.getString(key(FAVORITE_CHANNEL_KEYS), "").orEmpty())
            }
            editor.putString(key(FAVORITE_CHANNEL_KEYS), value)
                .putBoolean(key(CHANNEL_FAVORITES_SYNC_PENDING), true)
                .putString(key(CHANNEL_FAVORITES_SYNC_VALUE), value)
                .apply()
        }
    }

    fun applyRemoteFavoriteChannelKeys(keys: Set<Long>): Boolean = synchronized(prefs) {
        if (prefs.getBoolean(key(CHANNEL_FAVORITES_SYNC_PENDING), false)) return@synchronized false
        prefs.edit()
            .putString(key(FAVORITE_CHANNEL_KEYS), keys.sorted().joinToString("\n"))
            .remove(key(CHANNEL_FAVORITES_SYNC_BASE))
            .apply()
        true
    }

    fun pendingFavoriteChannelEdit(): PendingFavoriteKeysEdit? = synchronized(prefs) {
        if (!prefs.getBoolean(key(CHANNEL_FAVORITES_SYNC_PENDING), false)) return@synchronized null
        val target = favoriteKeySet(prefs.getString(key(CHANNEL_FAVORITES_SYNC_VALUE), ""))
        val baseKey = key(CHANNEL_FAVORITES_SYNC_BASE)
        val base = if (prefs.contains(baseKey)) favoriteKeySet(prefs.getString(baseKey, "")) else null
        PendingFavoriteKeysEdit(baseKeys = base, targetKeys = target)
    }

    fun clearPendingFavoriteChannelEdit(expected: PendingFavoriteKeysEdit): Boolean = synchronized(prefs) {
        if (!prefs.getBoolean(key(CHANNEL_FAVORITES_SYNC_PENDING), false)) return@synchronized false
        if (pendingFavoriteChannelEdit() != expected) return@synchronized false
        prefs.edit()
            .remove(key(CHANNEL_FAVORITES_SYNC_PENDING))
            .remove(key(CHANNEL_FAVORITES_SYNC_VALUE))
            .remove(key(CHANNEL_FAVORITES_SYNC_BASE))
            .apply()
        true
    }

    fun favoriteVodKeys(): Set<String> =
        string(VOD_FAVORITES)?.lineSequence()?.map(String::trim)?.filter(String::isNotBlank)?.toHashSet().orEmpty()

    data class PendingFavoriteKeysEdit(
        val baseKeys: Set<String>?,
        val targetKeys: Set<String>,
    ) {
        fun applyTo(remoteKeys: Set<String>): Set<String> {
            val base = baseKeys ?: return targetKeys
            val removed = base - targetKeys
            val added = targetKeys - base
            return (remoteKeys - removed) + added
        }
    }

    fun saveFavoriteVodKeys(keys: Set<String>) {
        val normalized = keys.map(String::trim).filter(String::isNotBlank).toSortedSet()
        val value = normalized.joinToString("\n")
        synchronized(prefs) {
            val editor = prefs.edit()
            if (!prefs.getBoolean(key(VOD_FAVORITES_SYNC_PENDING), false)) {
                editor.putString(key(VOD_FAVORITES_SYNC_BASE), prefs.getString(key(VOD_FAVORITES), "").orEmpty())
            }
            if (value.isBlank()) editor.remove(key(VOD_FAVORITES))
            else editor.putString(key(VOD_FAVORITES), value)
            editor.putBoolean(key(VOD_FAVORITES_SYNC_PENDING), true)
                .putString(key(VOD_FAVORITES_SYNC_VALUE), value)
                .apply()
        }
    }

    /** Applies a remote snapshot only when no newer local VOD-favourite edit is waiting to sync. */
    fun applyRemoteFavoriteVodKeys(keys: Set<String>): Boolean = synchronized(prefs) {
        if (prefs.getBoolean(key(VOD_FAVORITES_SYNC_PENDING), false)) return@synchronized false
        val normalizedKeys = remapVodFavoriteKeys(keys.map(String::trim).filter(String::isNotBlank).toSet())
        val value = normalizedKeys.toSortedSet().joinToString("\n")
        if (normalizedKeys != keys) {
            val baseValue = keys.map(String::trim).filter(String::isNotBlank).toSortedSet().joinToString("\n")
            val editor = prefs.edit()
                .putString(key(VOD_FAVORITES_SYNC_BASE), baseValue)
                .putBoolean(key(VOD_FAVORITES_SYNC_PENDING), true)
                .putString(key(VOD_FAVORITES_SYNC_VALUE), value)
            if (value.isBlank()) editor.remove(key(VOD_FAVORITES)) else editor.putString(key(VOD_FAVORITES), value)
            editor.apply()
            return@synchronized false
        }
        val editor = prefs.edit()
        if (value.isBlank()) editor.remove(key(VOD_FAVORITES))
        else editor.putString(key(VOD_FAVORITES), value)
        editor.remove(key(VOD_FAVORITES_SYNC_BASE))
        editor.apply()
        true
    }

    /** Null means no local edit is pending; an empty set is a deliberate clear. */
    fun pendingFavoriteVodKeys(): Set<String>? = synchronized(prefs) {
        if (!prefs.getBoolean(key(VOD_FAVORITES_SYNC_PENDING), false)) return@synchronized null
        prefs.getString(key(VOD_FAVORITES_SYNC_VALUE), "").orEmpty()
            .lineSequence().map(String::trim).filter(String::isNotBlank).toSet()
    }

    fun pendingFavoriteVodEdit(): PendingFavoriteKeysEdit? = synchronized(prefs) {
        if (!prefs.getBoolean(key(VOD_FAVORITES_SYNC_PENDING), false)) return@synchronized null
        val target = prefs.getString(key(VOD_FAVORITES_SYNC_VALUE), "").orEmpty()
            .lineSequence().map(String::trim).filter(String::isNotBlank).toSet()
        val baseKey = key(VOD_FAVORITES_SYNC_BASE)
        val base = if (prefs.contains(baseKey)) {
            prefs.getString(baseKey, "").orEmpty()
                .lineSequence().map(String::trim).filter(String::isNotBlank).toSet()
        } else {
            null
        }
        PendingFavoriteKeysEdit(baseKeys = base, targetKeys = target)
    }

    fun clearPendingFavoriteVodEdit(expected: PendingFavoriteKeysEdit): Boolean = synchronized(prefs) {
        if (!prefs.getBoolean(key(VOD_FAVORITES_SYNC_PENDING), false)) return@synchronized false
        val current = pendingFavoriteVodEdit() ?: return@synchronized false
        if (current != expected) return@synchronized false
        prefs.edit()
            .remove(key(VOD_FAVORITES_SYNC_PENDING))
            .remove(key(VOD_FAVORITES_SYNC_VALUE))
            .remove(key(VOD_FAVORITES_SYNC_BASE))
            .apply()
        true
    }

    fun clearPendingFavoriteVodKeys(expected: Set<String>): Boolean = synchronized(prefs) {
        val normalizedExpected = expected.map(String::trim).filter(String::isNotBlank).toSet()
        val current = pendingFavoriteVodEdit() ?: return@synchronized false
        if (current.targetKeys != normalizedExpected) return@synchronized false
        clearPendingFavoriteVodEdit(current)
    }

    private fun favoriteKeySet(raw: String?): Set<String> = raw.orEmpty()
        .lineSequence().map(String::trim).filter(String::isNotBlank).toSet()

    fun recentChannel(): LiveTvRecentChannel? {
        val url = string(RECENT_URL) ?: return null
        val name = string(RECENT_NAME) ?: return null
        return LiveTvRecentChannel(
            streamUrl = url,
            name = name,
            logoUrl = string(RECENT_LOGO),
            group = string(RECENT_GROUP).orEmpty(),
            tvgId = string(RECENT_TVG_ID),
        )
    }

    fun saveRecentChannel(channel: LiveTvRecentChannel) {
        prefs.edit().apply {
            putOrRemove(RECENT_URL, channel.streamUrl)
            putOrRemove(RECENT_NAME, channel.name)
            putOrRemove(RECENT_LOGO, channel.logoUrl)
            putOrRemove(RECENT_GROUP, channel.group)
            putOrRemove(RECENT_TVG_ID, channel.tvgId)
        }.apply()
    }

    /** Most recently played channels, newest first. Older installs seed this from the last channel. */
    fun channelHistory(): List<LiveTvRecentChannel> {
        val saved = string(CHANNEL_HISTORY) ?: return listOfNotNull(recentChannel())
        return runCatching {
            val array = JSONArray(saved)
            (0 until array.length()).mapNotNull { index ->
                array.optJSONObject(index)?.let { json ->
                    val url = json.optString("url").takeIf(String::isNotBlank) ?: return@let null
                    val name = json.optString("name").takeIf(String::isNotBlank) ?: return@let null
                    LiveTvRecentChannel(
                        streamUrl = url,
                        name = name,
                        logoUrl = json.optString("logo").takeUnless { it.isBlank() || it == "null" },
                        group = json.optString("group"),
                        tvgId = json.optString("tvgId").takeUnless { it.isBlank() || it == "null" },
                    )
                }
            }.take(LIVE_TV_CHANNEL_HISTORY_LIMIT)
        }.getOrElse { listOfNotNull(recentChannel()) }
    }

    fun saveChannelHistory(channels: List<LiveTvRecentChannel>) {
        val array = JSONArray()
        channels.distinctBy(LiveTvRecentChannel::streamUrl).take(LIVE_TV_CHANNEL_HISTORY_LIMIT).forEach { channel ->
            array.put(
                JSONObject()
                    .put("url", channel.streamUrl)
                    .put("name", channel.name)
                    .put("logo", channel.logoUrl)
                    .put("group", channel.group)
                    .put("tvgId", channel.tvgId),
            )
        }
        prefs.edit().putOrRemove(CHANNEL_HISTORY, array.toString().takeIf { array.length() > 0 }).apply()
    }

    companion object {
        const val PREFS = "nuvio_live_tv"
        private const val SOURCES = "sources"
        private const val HIDDEN_GROUPS = "hidden_groups"
        private const val HIDDEN_VOD_CATEGORIES = "hidden_vod_categories"
        private const val GROUP_ORDER = "group_order"
        private const val CATEGORY_RAIL_ORDER = "category_rail_order"
        private const val GROUP_NAMES = "group_names"
        private const val LEGACY_HIDDEN_CHANNELS = "hidden_channel_urls"
        private const val HIDDEN_CHANNEL_KEYS_SYNC_MARKER = "hidden_channel_keys_sync_marker"
        private const val UNGROUPED_LINE = "\uE000"
        private const val LEGACY_SOURCE_TYPE = "source_type"
        private const val LEGACY_SOURCE_URL = "source_url"
        private const val STALKER_PORTAL = "stalker_portal_url"
        private const val STALKER_MAC = "stalker_mac_address"
        private const val STALKER_USER = "stalker_username"
        private const val STALKER_PASSWORD = "stalker_password"
        private const val XTREAM_SERVER = "xtream_server_url"
        private const val XTREAM_USER = "xtream_username"
        private const val XTREAM_PASSWORD = "xtream_password"
        private const val FAVORITES = "favorite_channel_urls"
        private const val FAVORITE_CHANNEL_KEYS = "favorite_channel_keys"
        private const val CHANNEL_FAVORITES_SYNC_PENDING = "favorite_channel_keys_sync_pending"
        private const val CHANNEL_FAVORITES_SYNC_VALUE = "favorite_channel_keys_sync_value"
        private const val CHANNEL_FAVORITES_SYNC_BASE = "favorite_channel_keys_sync_base"
        private const val VOD_FAVORITES = "favorite_vod_keys"
        private const val VOD_FAVORITES_SYNC_PENDING = "favorite_vod_keys_sync_pending"
        private const val VOD_FAVORITES_SYNC_VALUE = "favorite_vod_keys_sync_value"
        private const val VOD_FAVORITES_SYNC_BASE = "favorite_vod_keys_sync_base"
        private const val VOD_SOURCE_ID_ALIASES = "vod_source_id_aliases"
        private const val CUSTOM_LISTS = "custom_lists"
        private const val RECENT_URL = "recent_channel_url"
        private const val RECENT_NAME = "recent_channel_name"
        private const val RECENT_LOGO = "recent_channel_logo"
        private const val RECENT_GROUP = "recent_channel_group"
        private const val RECENT_TVG_ID = "recent_channel_tvg_id"
        private const val CHANNEL_HISTORY = "channel_history"
    }
}
