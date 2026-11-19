package com.nuvio.tv.data.local

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

data class ServerBrowserShortcut(
    val sourceId: String,
    val path: String,
    val name: String,
    val isDirectory: Boolean,
    val isPlayable: Boolean,
    val thumbnailUri: String? = null,
)

@Singleton
class ServerBrowserShortcutStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val preferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    private val mutableShortcuts = MutableStateFlow(readShortcuts())
    val shortcuts: StateFlow<List<ServerBrowserShortcut>> = mutableShortcuts.asStateFlow()

    @Synchronized
    fun add(shortcut: ServerBrowserShortcut) {
        val next = mutableShortcuts.value.filterNot {
            it.sourceId == shortcut.sourceId && it.path == shortcut.path
        } + shortcut
        write(next)
    }

    @Synchronized
    fun remove(sourceId: String, path: String) {
        write(mutableShortcuts.value.filterNot { it.sourceId == sourceId && it.path == path })
    }

    @Synchronized
    fun removeTree(sourceId: String, path: String) {
        write(mutableShortcuts.value.filterNot {
            it.sourceId == sourceId && (it.path == path || it.path.startsWith("${path.trimEnd('/')}/"))
        })
    }

    @Synchronized
    fun renameTree(sourceId: String, oldPath: String, newPath: String, newName: String) {
        write(mutableShortcuts.value.map { shortcut ->
            if (shortcut.sourceId != sourceId) return@map shortcut
            when {
                shortcut.path == oldPath -> shortcut.copy(path = newPath, name = newName)
                shortcut.path.startsWith("${oldPath.trimEnd('/')}/") ->
                    shortcut.copy(path = newPath.trimEnd('/') + shortcut.path.removePrefix(oldPath.trimEnd('/')))
                else -> shortcut
            }
        })
    }

    private fun write(shortcuts: List<ServerBrowserShortcut>) {
        val json = JSONArray()
        shortcuts.forEach { shortcut ->
            json.put(
                JSONObject()
                    .put("sourceId", shortcut.sourceId)
                    .put("path", shortcut.path)
                    .put("name", shortcut.name)
                    .put("isDirectory", shortcut.isDirectory)
                    .put("isPlayable", shortcut.isPlayable)
                    .put("thumbnailUri", shortcut.thumbnailUri)
            )
        }
        preferences.edit().putString(KEY_SHORTCUTS, json.toString()).apply()
        mutableShortcuts.value = shortcuts
    }

    private fun readShortcuts(): List<ServerBrowserShortcut> = runCatching {
        val json = JSONArray(preferences.getString(KEY_SHORTCUTS, "[]"))
        (0 until json.length()).mapNotNull { index ->
            val item = json.optJSONObject(index) ?: return@mapNotNull null
            val sourceId = item.optString("sourceId").takeIf(String::isNotBlank) ?: return@mapNotNull null
            val path = item.optString("path").takeIf(String::isNotBlank) ?: return@mapNotNull null
            val name = item.optString("name").takeIf(String::isNotBlank) ?: return@mapNotNull null
            ServerBrowserShortcut(
                sourceId = sourceId,
                path = path,
                name = name,
                isDirectory = item.optBoolean("isDirectory"),
                isPlayable = item.optBoolean("isPlayable"),
                thumbnailUri = item.optString("thumbnailUri").takeIf(String::isNotBlank),
            )
        }
    }.getOrDefault(emptyList())

    companion object {
        private const val PREFERENCES = "server_browser_shortcuts"
        private const val KEY_SHORTCUTS = "items"
        private const val FOLDER_ID_PREFIX = "server-browser-folder:"
        private const val FILE_ID_PREFIX = "server-browser-file:"

        fun folderShortcutId(sourceId: String, path: String): String =
            FOLDER_ID_PREFIX + Uri.encode(sourceId) + "|" + Uri.encode(path)

        fun decodeFolderShortcutId(id: String): Pair<String, String>? {
            if (!id.startsWith(FOLDER_ID_PREFIX)) return null
            val parts = id.removePrefix(FOLDER_ID_PREFIX).split('|', limit = 2)
            if (parts.size != 2) return null
            return Uri.decode(parts[0]) to Uri.decode(parts[1])
        }

        fun fileShortcutId(sourceId: String, path: String): String =
            FILE_ID_PREFIX + Uri.encode(sourceId) + "|" + Uri.encode(path)

        fun decodeFileShortcutId(id: String): Pair<String, String>? {
            if (!id.startsWith(FILE_ID_PREFIX)) return null
            val parts = id.removePrefix(FILE_ID_PREFIX).split('|', limit = 2)
            if (parts.size != 2) return null
            return Uri.decode(parts[0]) to Uri.decode(parts[1])
        }
    }
}
