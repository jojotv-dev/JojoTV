package com.nuvio.tv.ui.screens.folderpicker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Build
import android.os.CancellationSignal
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.provider.DocumentsProvider
import android.util.Base64
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.Locale

data class NuvioStorageRoot(
    val id: String,
    val label: String,
    val directory: File,
    val isPrimary: Boolean,
)

/**
 * Exposes mounted local and USB storage through SAF tree URIs. URI grants are issued only
 * after a user chooses a folder in [NuvioFolderPickerActivity].
 */
class NuvioDocumentsProvider : DocumentsProvider() {
    override fun onCreate(): Boolean = true

    override fun queryRoots(projection: Array<out String>?): Cursor {
        val columns = projection ?: DEFAULT_ROOT_PROJECTION
        val cursor = MatrixCursor(columns)
        storageRoots(providerContext).forEach { root ->
            val rootDocumentId = documentId(root, root.directory)
            cursor.addProjectedRow(columns, mapOf(
                DocumentsContract.Root.COLUMN_ROOT_ID to root.id,
                DocumentsContract.Root.COLUMN_DOCUMENT_ID to rootDocumentId,
                DocumentsContract.Root.COLUMN_TITLE to root.label,
                DocumentsContract.Root.COLUMN_SUMMARY to "Stockage local ou USB",
                DocumentsContract.Root.COLUMN_FLAGS to (
                    DocumentsContract.Root.FLAG_LOCAL_ONLY or
                        DocumentsContract.Root.FLAG_SUPPORTS_CREATE
                    ),
                DocumentsContract.Root.COLUMN_MIME_TYPES to "*/*",
                DocumentsContract.Root.COLUMN_AVAILABLE_BYTES to root.directory.usableSpace,
                DocumentsContract.Root.COLUMN_ICON to android.R.drawable.ic_menu_save,
            ))
        }
        return cursor
    }

    override fun queryDocument(documentId: String, projection: Array<out String>?): Cursor {
        val columns = projection ?: DEFAULT_DOCUMENT_PROJECTION
        val cursor = MatrixCursor(columns)
        val (root, file) = resolveDocument(documentId)
        if (!file.exists()) throw FileNotFoundException("Ce dossier ou fichier n’existe plus.")
        cursor.addProjectedRow(columns, documentValues(root, file))
        return cursor
    }

    override fun queryChildDocuments(
        parentDocumentId: String,
        projection: Array<out String>?,
        sortOrder: String?,
    ): Cursor {
        val columns = projection ?: DEFAULT_DOCUMENT_PROJECTION
        val cursor = MatrixCursor(columns)
        val (root, parent) = resolveDocument(parentDocumentId)
        if (!parent.isDirectory || !parent.canRead()) {
            throw FileNotFoundException("Ce dossier ne peut pas être lu.")
        }
        val children = parent.listFiles()
            ?: throw FileNotFoundException("Impossible de lire le contenu de ce dossier.")
        children.asSequence()
            .mapNotNull { child -> runCatching { child.canonicalFile }.getOrNull() }
            .filter { isWithinRoot(it, root.directory) && !isProtectedAndroidArea(root, it) }
            .filter { it.exists() && it.canRead() }
            .sortedWith(compareByDescending<File> { it.isDirectory }.thenBy { it.name.lowercase(Locale.ROOT) })
            .forEach { child -> cursor.addProjectedRow(columns, documentValues(root, child)) }
        return cursor
    }

    override fun openDocument(
        documentId: String,
        mode: String,
        signal: CancellationSignal?,
    ): ParcelFileDescriptor {
        signal?.throwIfCanceled()
        val (root, file) = resolveDocument(documentId)
        if (file.isDirectory || isProtectedAndroidArea(root, file)) {
            throw FileNotFoundException("Ce fichier ne peut pas être ouvert.")
        }
        if (mode.contains('w') && !file.parentFile?.canWrite().orFalse()) {
            throw FileNotFoundException("JojoTV ne peut pas écrire dans ce dossier.")
        }
        if (!mode.contains('w') && !file.canRead()) {
            throw FileNotFoundException("Ce fichier ne peut pas être lu.")
        }
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.parseMode(mode))
    }

    override fun createDocument(parentDocumentId: String, mimeType: String, displayName: String): String {
        val (root, parent) = resolveDocument(parentDocumentId)
        if (!parent.isDirectory || !parent.canWrite()) {
            throw FileNotFoundException("JojoTV ne peut pas écrire dans ce dossier.")
        }
        val safeName = validateDisplayName(displayName)
        val target = File(parent, safeName).canonicalFile
        if (!isWithinRoot(target, root.directory) || isProtectedAndroidArea(root, target)) {
            throw FileNotFoundException("Ce dossier n’est pas accessible.")
        }
        val created = if (mimeType == DocumentsContract.Document.MIME_TYPE_DIR) {
            target.mkdir()
        } else {
            target.createNewFile()
        }
        if (!created) throw FileNotFoundException("Impossible de créer « $safeName ».")
        notifyChanged(parentDocumentId, documentId(root, target))
        return documentId(root, target)
    }

    override fun deleteDocument(documentId: String) {
        val (root, file) = resolveDocument(documentId)
        if (file == root.directory) throw FileNotFoundException("La racine du stockage ne peut pas être supprimée.")
        if (isProtectedAndroidArea(root, file) || !file.deleteRecursively()) {
            throw FileNotFoundException("Impossible de supprimer cet élément.")
        }
        notifyChanged(documentId(root, file.parentFile ?: root.directory), documentId)
    }

    override fun renameDocument(documentId: String, displayName: String): String {
        val (root, source) = resolveDocument(documentId)
        if (source == root.directory || isProtectedAndroidArea(root, source)) {
            throw FileNotFoundException("Cet élément ne peut pas être renommé.")
        }
        val safeName = validateDisplayName(displayName)
        val parent = source.parentFile ?: throw FileNotFoundException("Dossier parent introuvable.")
        val destination = File(parent, safeName).canonicalFile
        if (!isWithinRoot(destination, root.directory) || isProtectedAndroidArea(root, destination) || destination.exists()) {
            throw FileNotFoundException("Le nouveau nom n’est pas disponible.")
        }
        if (!source.renameTo(destination)) throw FileNotFoundException("Impossible de renommer cet élément.")
        notifyChanged(documentId(root, parent), documentId)
        return documentId(root, destination)
    }

    override fun isChildDocument(parentDocumentId: String, documentId: String): Boolean {
        val (parentRoot, parent) = runCatching { resolveDocument(parentDocumentId) }.getOrNull() ?: return false
        val (childRoot, child) = runCatching { resolveDocument(documentId) }.getOrNull() ?: return false
        return parentRoot.id == childRoot.id && parent != child && isWithinRoot(child, parent)
    }

    private fun documentValues(root: NuvioStorageRoot, file: File): Map<String, Any?> {
        val isDirectory = file.isDirectory
        val isRoot = file == root.directory
        var flags = 0
        if (isDirectory) {
            if (file.canWrite()) flags = flags or DocumentsContract.Document.FLAG_DIR_SUPPORTS_CREATE
            if (!isRoot && file.parentFile?.canWrite() == true) {
                flags = flags or DocumentsContract.Document.FLAG_SUPPORTS_DELETE or
                    DocumentsContract.Document.FLAG_SUPPORTS_RENAME
            }
        } else {
            if (file.parentFile?.canWrite() == true) {
                flags = flags or DocumentsContract.Document.FLAG_SUPPORTS_DELETE or
                    DocumentsContract.Document.FLAG_SUPPORTS_RENAME
            }
            if (file.canWrite()) flags = flags or DocumentsContract.Document.FLAG_SUPPORTS_WRITE
        }
        return mapOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID to documentId(root, file),
            DocumentsContract.Document.COLUMN_DISPLAY_NAME to if (isRoot) root.label else file.name,
            DocumentsContract.Document.COLUMN_MIME_TYPE to if (isDirectory) {
                DocumentsContract.Document.MIME_TYPE_DIR
            } else {
                mimeType(file)
            },
            DocumentsContract.Document.COLUMN_FLAGS to flags,
            DocumentsContract.Document.COLUMN_SIZE to if (isDirectory) 0L else file.length(),
            DocumentsContract.Document.COLUMN_LAST_MODIFIED to file.lastModified(),
        )
    }

    private fun resolveDocument(id: String): Pair<NuvioStorageRoot, File> {
        val separator = id.indexOf(':')
        if (separator <= 0) throw FileNotFoundException("Identifiant de dossier invalide.")
        val rootId = id.substring(0, separator)
        val relativePath = runCatching {
            val encoded = id.substring(separator + 1)
            if (encoded.isBlank()) "" else String(Base64.decode(encoded, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING))
        }.getOrElse { throw FileNotFoundException("Identifiant de dossier invalide.") }
        if (relativePath.startsWith('/') || relativePath.split('/').any { it == ".." }) {
            throw FileNotFoundException("Chemin de dossier invalide.")
        }
        val root = storageRoots(providerContext).firstOrNull { it.id == rootId }
            ?: throw FileNotFoundException("Ce stockage n’est plus disponible.")
        val file = runCatching { File(root.directory, relativePath).canonicalFile }
            .getOrElse { throw FileNotFoundException("Chemin de dossier invalide.") }
        if (!isWithinRoot(file, root.directory) || isProtectedAndroidArea(root, file)) {
            throw FileNotFoundException("Ce dossier n’est pas accessible.")
        }
        return root to file
    }

    private fun notifyChanged(parentDocumentId: String, documentId: String) {
        val resolver = providerContext.contentResolver
        resolver.notifyChange(DocumentsContract.buildDocumentUri(authority, documentId), null)
        resolver.notifyChange(DocumentsContract.buildChildDocumentsUri(authority, parentDocumentId), null)
    }

    private val providerContext: Context
        get() = context ?: throw IllegalStateException("JojoTV provider is not attached.")

    private val authority: String get() = documentsAuthority(providerContext)

    companion object {
        private val DEFAULT_ROOT_PROJECTION = arrayOf(
            DocumentsContract.Root.COLUMN_ROOT_ID,
            DocumentsContract.Root.COLUMN_DOCUMENT_ID,
            DocumentsContract.Root.COLUMN_TITLE,
            DocumentsContract.Root.COLUMN_SUMMARY,
            DocumentsContract.Root.COLUMN_FLAGS,
            DocumentsContract.Root.COLUMN_MIME_TYPES,
            DocumentsContract.Root.COLUMN_AVAILABLE_BYTES,
            DocumentsContract.Root.COLUMN_ICON,
        )
        private val DEFAULT_DOCUMENT_PROJECTION = arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
            DocumentsContract.Document.COLUMN_FLAGS,
            DocumentsContract.Document.COLUMN_SIZE,
            DocumentsContract.Document.COLUMN_LAST_MODIFIED,
        )

        fun documentsAuthority(context: Context): String = "${context.packageName}.documents"

        fun hasStorageAccess(context: Context): Boolean = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.R -> Environment.isExternalStorageManager()
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ->
                context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            else -> true
        }

        fun storageRoots(context: Context): List<NuvioStorageRoot> {
            if (!hasStorageAccess(context)) return emptyList()
            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as? StorageManager
            val volumes = runCatching { storageManager?.storageVolumes.orEmpty() }.getOrDefault(emptyList())
            val roots = volumes.mapNotNull { volume ->
                if (volume.state != Environment.MEDIA_MOUNTED || (!volume.isPrimary && !volume.isRemovable)) {
                    return@mapNotNull null
                }
                val directory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    runCatching { volume.directory }.getOrNull()
                } else {
                    legacyVolumeRoot(context, storageManager, volume)
                }?.let { runCatching { it.canonicalFile }.getOrNull() } ?: return@mapNotNull null
                if (!directory.isDirectory || !directory.canRead() || !directory.canWrite()) return@mapNotNull null
                val id = if (volume.isPrimary) "primary" else "volume_${encode(directory.path)}"
                val label = if (volume.isPrimary) {
                    "Stockage interne"
                } else {
                    runCatching { volume.getDescription(context) }.getOrNull()?.trim()?.takeIf(String::isNotBlank)
                        ?: "Stockage USB"
                }
                NuvioStorageRoot(id, label, directory, volume.isPrimary)
            }
            val withPrimaryFallback = if (roots.none(NuvioStorageRoot::isPrimary)) {
                val primary = runCatching { Environment.getExternalStorageDirectory().canonicalFile }.getOrNull()
                if (primary != null && primary.isDirectory && primary.canRead() && primary.canWrite()) {
                    roots + NuvioStorageRoot("primary", "Stockage interne", primary, true)
                } else roots
            } else roots
            return withPrimaryFallback.distinctBy { it.directory.path }
                .sortedWith(compareByDescending<NuvioStorageRoot> { it.isPrimary }.thenBy { it.label.lowercase(Locale.ROOT) })
        }

        fun documentIdFor(context: Context, file: File): String {
            val canonicalFile = file.canonicalFile
            val root = storageRoots(context).firstOrNull { isWithinRoot(canonicalFile, it.directory) }
                ?: throw FileNotFoundException("Ce dossier n’appartient pas à un stockage accessible.")
            if (!canonicalFile.isDirectory || !canonicalFile.canRead() || !canonicalFile.canWrite() ||
                isProtectedAndroidArea(root, canonicalFile)
            ) {
                throw FileNotFoundException("Ce dossier ne peut pas être sélectionné.")
            }
            return documentId(root, canonicalFile)
        }

        private fun documentId(root: NuvioStorageRoot, file: File): String {
            val relativePath = if (file == root.directory) "" else {
                file.relativeTo(root.directory).path.replace(File.separatorChar, '/')
            }
            return "${root.id}:${if (relativePath.isBlank()) "" else encode(relativePath)}"
        }

        private fun encode(value: String): String = Base64.encodeToString(
            value.toByteArray(Charsets.UTF_8),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING,
        )

        private fun isWithinRoot(file: File, root: File): Boolean {
            val rootPath = runCatching { root.canonicalPath }.getOrNull() ?: return false
            val filePath = runCatching { file.canonicalPath }.getOrNull() ?: return false
            return filePath == rootPath || filePath.startsWith(
                if (rootPath.endsWith(File.separator)) rootPath else "$rootPath${File.separator}",
            )
        }

        private fun isProtectedAndroidArea(root: NuvioStorageRoot, file: File): Boolean {
            val relative = runCatching { file.relativeTo(root.directory).path }
                .getOrDefault("")
                .replace('\\', '/')
                .trim('/')
                .lowercase(Locale.ROOT)
            return relative == "android/data" || relative.startsWith("android/data/") ||
                relative == "android/obb" || relative.startsWith("android/obb/")
        }

        private fun legacyVolumeRoot(
            context: Context,
            storageManager: StorageManager?,
            volume: android.os.storage.StorageVolume,
        ): File? {
            val appDirectory = context.getExternalFilesDirs(null).filterNotNull().firstOrNull { directory ->
                runCatching {
                    val mountedVolume = storageManager?.getStorageVolume(directory)
                    mountedVolume?.uuid == volume.uuid && mountedVolume?.isPrimary == volume.isPrimary
                }.getOrDefault(false)
            }
            val volumePath = appDirectory?.absolutePath
                ?.substringBefore("${File.separator}Android${File.separator}data${File.separator}")
                ?.takeIf(String::isNotBlank)
            if (volumePath != null) return File(volumePath)
            return Environment.getExternalStorageDirectory().takeIf { volume.isPrimary }
        }

        private fun mimeType(file: File): String {
            val extension = file.extension.lowercase(Locale.ROOT)
            return android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
                ?: "application/octet-stream"
        }

        private fun validateDisplayName(value: String): String {
            val name = value.trim()
            if (name.isBlank() || name == "." || name == ".." || name.contains('/') || name.contains('\\')) {
                throw IOException("Nom de fichier invalide.")
            }
            return name
        }

    }
}

private fun MatrixCursor.addProjectedRow(columns: Array<out String>, values: Map<String, Any?>) {
    addRow(columns.map { values[it] }.toTypedArray())
}

private fun Boolean?.orFalse(): Boolean = this == true
