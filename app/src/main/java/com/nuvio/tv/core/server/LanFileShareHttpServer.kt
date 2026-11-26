package com.nuvio.tv.core.server

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.util.Base64
import android.webkit.MimeTypeMap
import fi.iki.elonen.NanoHTTPD
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Collections
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

internal class LanFileShareHttpServer(
    private val context: Context,
    private val password: String,
) : NanoHTTPD(PORT) {

    override fun serve(session: IHTTPSession): Response {
        if (!isLocalNetworkClient(session.remoteIpAddress)) {
            return textResponse(Response.Status.FORBIDDEN, "Accès réservé au réseau local.")
        }
        if (session.method == Method.OPTIONS) return handleCorsPreflight(session)

        val cookieAuthorized = isAuthorizedByCookie(session)
        val basicAuthorized = !cookieAuthorized && isAuthorizedByBasic(session)
        if (!cookieAuthorized && !basicAuthorized) {
            return addCorsHeaders(
                session,
                unauthorizedResponse(includeChallenge = !isAllowedDeviceBrowserOrigin(session)),
            )
        }

        val response = when (session.method) {
            Method.GET -> when (session.uri) {
                "/" -> servePage()
                "/api/list" -> listDirectory(session.parameters["path"]?.firstOrNull().orEmpty())
                "/download" -> downloadFile(session.parameters["path"]?.firstOrNull().orEmpty())
                else -> textResponse(Response.Status.NOT_FOUND, "Introuvable.")
            }
            Method.POST -> handlePost(session)
            else -> textResponse(Response.Status.METHOD_NOT_ALLOWED, "Méthode non prise en charge.")
        }

        if (basicAuthorized || cookieAuthorized) {
            response.addHeader("Set-Cookie", createSessionCookieHeader())
        }
        return addCorsHeaders(session, response)
    }

    private fun servePage(): Response = runCatching {
        context.assets.open(PAGE_ASSET).use { input ->
            val bytes = input.readBytes()
            newFixedLengthResponse(
                Response.Status.OK,
                "text/html; charset=utf-8",
                bytes.inputStream(),
                bytes.size.toLong(),
            ).apply {
                addHeader("Cache-Control", "no-store")
                addHeader("X-Content-Type-Options", "nosniff")
            }
        }
    }.getOrElse { textResponse(Response.Status.INTERNAL_ERROR, "Page indisponible.") }

    private fun listDirectory(path: String): Response {
        val result = JSONArray()
        val roots = mountedStorages(context)
        if (path.isBlank()) {
            roots.forEach { root ->
                result.put(
                    JSONObject()
                        .put("name", root.label)
                        .put("path", root.directory.canonicalPath)
                        .put("directory", true)
                        .put("size", 0L),
                )
            }
            return jsonResponse(result)
        }

        val directory = resolveMountedFile(path, roots)
            ?: return textResponse(Response.Status.FORBIDDEN, "Ce chemin n’appartient pas à un stockage monté.")
        if (!directory.isDirectory || !directory.canRead()) {
            return textResponse(Response.Status.FORBIDDEN, "Ce dossier ne peut pas être lu.")
        }
        val children = directory.listFiles()
            ?: return textResponse(Response.Status.FORBIDDEN, "Impossible de lire ce dossier.")
        children.forEach { child ->
            val canonical = runCatching { child.canonicalFile }.getOrNull() ?: return@forEach
            if (child.absoluteFile.path != canonical.path) return@forEach
            if (roots.none { isWithin(canonical, it.directory) } || !canonical.canRead()) return@forEach
            val isDirectory = canonical.isDirectory
            if (!isDirectory && !canonical.isFile) return@forEach
            result.put(
                JSONObject()
                    .put("name", canonical.name)
                    .put("path", canonical.path)
                    .put("directory", isDirectory)
                    .put("size", if (isDirectory) 0L else canonical.length()),
            )
        }
        return jsonResponse(result)
    }

    private fun downloadFile(path: String): Response {
        val file = resolveMountedFile(path, mountedStorages(context))
            ?: return textResponse(Response.Status.FORBIDDEN, "Ce chemin n’appartient pas à un stockage monté.")
        if (!file.isFile || !file.canRead()) {
            return textResponse(Response.Status.NOT_FOUND, "Fichier indisponible.")
        }
        val extension = file.extension.lowercase()
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "application/octet-stream"
        return try {
            newFixedLengthResponse(Response.Status.OK, mimeType, FileInputStream(file), file.length()).apply {
                val asciiName = file.name.replace(Regex("[^A-Za-z0-9._ -]"), "_").replace("\"", "_")
                val encodedName = URLEncoder.encode(file.name, "UTF-8").replace("+", "%20")
                addHeader("Content-Disposition", "attachment; filename=\"$asciiName\"; filename*=UTF-8''$encodedName")
                addHeader("Cache-Control", "no-store")
                addHeader("X-Content-Type-Options", "nosniff")
            }
        } catch (_: Exception) {
            textResponse(Response.Status.NOT_FOUND, "Fichier indisponible.")
        }
    }

    private fun jsonResponse(value: JSONArray): Response =
        newFixedLengthResponse(Response.Status.OK, "application/json; charset=utf-8", value.toString())
            .apply { addHeader("Cache-Control", "no-store") }

    private fun jsonResponse(value: JSONObject): Response =
        newFixedLengthResponse(Response.Status.OK, "application/json; charset=utf-8", value.toString())
            .apply { addHeader("Cache-Control", "no-store") }

    private fun textResponse(status: Response.Status, message: String): Response =
        newFixedLengthResponse(status, "text/plain; charset=utf-8", message)
            .apply { addHeader("Cache-Control", "no-store") }

    private fun unauthorizedResponse(includeChallenge: Boolean = true): Response =
        textResponse(Response.Status.UNAUTHORIZED, "Identifiant et code d’accès requis.")
            .apply {
                if (includeChallenge) addHeader("WWW-Authenticate", "Basic realm=\"JojoTV fichiers\", charset=\"UTF-8\"")
            }

    private fun handlePost(session: IHTTPSession): Response {
        if (!(isSameOrigin(session) || isAllowedDeviceBrowserOrigin(session)) ||
            header(session, "x-jojotv-request") != ACTION_HEADER_VALUE
        ) {
            return textResponse(Response.Status.FORBIDDEN, "Requête non autorisée.")
        }
        val contentType = header(session, "content-type")
            ?.substringBefore(';')
            ?.trim()
            ?.lowercase()
        if (contentType != FORM_CONTENT_TYPE) {
            return textResponse(Response.Status.BAD_REQUEST, "Format de requête non pris en charge.")
        }
        val contentLength = header(session, "content-length")?.toLongOrNull()
        if (contentLength == null || contentLength !in 0..MAX_FORM_BODY_BYTES) {
            return textResponse(Response.Status.BAD_REQUEST, "Taille de requête invalide.")
        }
        val bodyFiles = HashMap<String, String>()
        if (runCatching { session.parseBody(bodyFiles) }.isFailure) {
            return textResponse(Response.Status.BAD_REQUEST, "Requête invalide.")
        }
        val path = session.parameters["path"]?.firstOrNull().orEmpty()
        return when (session.uri) {
            "/api/rename" -> renameEntry(path, session.parameters["name"]?.firstOrNull().orEmpty())
            "/api/delete" -> deleteEntry(path)
            else -> textResponse(Response.Status.NOT_FOUND, "Introuvable.")
        }
    }

    private fun renameEntry(path: String, newName: String): Response {
        val roots = mountedStorages(context)
        val resolved = resolveMutableMountedFile(path, roots)
            ?: return textResponse(Response.Status.FORBIDDEN, "Ce chemin ne peut pas être modifié.")
        val file = resolved.first
        val root = resolved.second
        if (!file.exists() || (!file.isFile && !file.isDirectory)) {
            return textResponse(Response.Status.NOT_FOUND, "Élément introuvable.")
        }
        if (file.canonicalFile == root.canonicalFile) {
            return textResponse(Response.Status.FORBIDDEN, "La racine d’un stockage ne peut pas être renommée.")
        }
        if (!isValidEntryName(newName)) {
            return textResponse(Response.Status.BAD_REQUEST, "Nom invalide.")
        }
        if (newName == file.name) return jsonResponse(JSONObject().put("ok", true).put("name", file.name))

        val parent = file.parentFile?.canonicalFile
            ?: return textResponse(Response.Status.FORBIDDEN, "Dossier parent indisponible.")
        if (!isWithin(parent, root) || !parent.canWrite()) {
            return textResponse(Response.Status.FORBIDDEN, "Ce dossier n’autorise pas le renommage.")
        }
        val target = File(parent, newName)
        val canonicalTarget = runCatching { target.canonicalFile }.getOrNull()
            ?: return textResponse(Response.Status.BAD_REQUEST, "Nom invalide.")
        if (!isWithin(canonicalTarget, root) || canonicalTarget.parentFile?.canonicalPath != parent.canonicalPath) {
            return textResponse(Response.Status.FORBIDDEN, "La destination sort du stockage autorisé.")
        }
        if (target.exists()) {
            return textResponse(Response.Status.BAD_REQUEST, "Un élément porte déjà ce nom.")
        }
        if (!file.renameTo(target)) {
            return textResponse(Response.Status.FORBIDDEN, "Le renommage a échoué. Vérifie les droits d’écriture.")
        }
        return jsonResponse(JSONObject().put("ok", true).put("name", newName))
    }

    private fun deleteEntry(path: String): Response {
        val roots = mountedStorages(context)
        val resolved = resolveMutableMountedFile(path, roots)
            ?: return textResponse(Response.Status.FORBIDDEN, "Ce chemin ne peut pas être supprimé.")
        val file = resolved.first
        val root = resolved.second
        if (!file.exists() || (!file.isFile && !file.isDirectory)) {
            return textResponse(Response.Status.NOT_FOUND, "Élément introuvable.")
        }
        if (file.canonicalFile == root.canonicalFile) {
            return textResponse(Response.Status.FORBIDDEN, "La racine d’un stockage ne peut pas être supprimée.")
        }
        if (!canDeleteTree(file, root)) {
            return textResponse(Response.Status.FORBIDDEN, "Suppression impossible : stockage en lecture seule ou lien symbolique détecté.")
        }
        if (!deleteTree(file, root)) {
            return textResponse(Response.Status.FORBIDDEN, "La suppression est incomplète ou a échoué.")
        }
        return jsonResponse(JSONObject().put("ok", true))
    }

    private fun canDeleteTree(file: File, root: File): Boolean {
        val absolute = file.absoluteFile
        val canonical = runCatching { file.canonicalFile }.getOrNull() ?: return false
        if (absolute.path != canonical.path || !isWithin(canonical, root) || canonical == root.canonicalFile) return false
        val parent = canonical.parentFile ?: return false
        if (!parent.canWrite()) return false
        if (!canonical.isDirectory) return canonical.isFile
        val children = canonical.listFiles() ?: return false
        return children.all { child -> canDeleteTree(child, root) }
    }

    private fun deleteTree(file: File, root: File): Boolean {
        val canonical = runCatching { file.canonicalFile }.getOrNull() ?: return false
        if (file.absoluteFile.path != canonical.path || !isWithin(canonical, root) || canonical == root.canonicalFile) {
            return false
        }
        if (canonical.isDirectory) {
            val children = canonical.listFiles() ?: return false
            for (child in children) if (!deleteTree(child, root)) return false
        }
        return canonical.delete()
    }

    private fun isValidEntryName(name: String): Boolean =
        name.isNotBlank() && name == name.trim() && name.length <= MAX_ENTRY_NAME_LENGTH &&
            name != "." && name != ".." &&
            name.none { it == '/' || it == '\\' || it == '\u0000' || it.isISOControl() }

    private fun resolveMutableMountedFile(path: String, roots: List<MountedStorage>): Pair<File, File>? {
        if (path.isBlank()) return null
        val absolute = runCatching { File(path).absoluteFile }.getOrNull() ?: return null
        val canonical = runCatching { absolute.canonicalFile }.getOrNull() ?: return null
        if (absolute.path != canonical.path) return null
        val root = roots.firstOrNull { isWithin(canonical, it.directory) }?.directory ?: return null
        return canonical to root
    }

    private fun isSameOrigin(session: IHTTPSession): Boolean {
        val origin = header(session, "origin")?.trimEnd('/') ?: return false
        val host = header(session, "host")?.trim()?.takeIf(String::isNotBlank) ?: return false
        return origin.equals("http://$host", ignoreCase = true)
    }

    private fun isAllowedDeviceBrowserOrigin(session: IHTTPSession): Boolean =
        header(session, "origin")?.equals(DEVICE_BROWSER_ORIGIN, ignoreCase = true) == true

    private fun handleCorsPreflight(session: IHTTPSession): Response {
        if (!isAllowedDeviceBrowserOrigin(session)) {
            return textResponse(Response.Status.FORBIDDEN, "Origine non autorisée.")
        }
        if (session.uri !in CORS_PATHS) {
            return textResponse(Response.Status.NOT_FOUND, "Introuvable.")
        }
        val requestedMethod = header(session, "access-control-request-method")
        if (requestedMethod == null ||
            (!requestedMethod.equals("GET", ignoreCase = true) &&
                !requestedMethod.equals("POST", ignoreCase = true))
        ) {
            return textResponse(Response.Status.FORBIDDEN, "Méthode non autorisée.")
        }
        val requestedHeaders = header(session, "access-control-request-headers")
            ?.split(',')
            ?.map(String::trim)
            ?.filter(String::isNotBlank)
            .orEmpty()
        if (requestedHeaders.any { it.lowercase() !in CORS_ALLOWED_HEADERS }) {
            return textResponse(Response.Status.FORBIDDEN, "En-tête non autorisé.")
        }

        return newFixedLengthResponse(Response.Status.NO_CONTENT, "text/plain; charset=utf-8", "")
            .apply {
                addHeader("Access-Control-Allow-Origin", DEVICE_BROWSER_ORIGIN)
                addHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS")
                addHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-JojoTV-Request")
                addHeader("Access-Control-Max-Age", "600")
                addHeader("Vary", "Origin")
                if (header(session, "access-control-request-private-network")
                        ?.equals("true", ignoreCase = true) == true
                ) {
                    addHeader("Access-Control-Allow-Private-Network", "true")
                }
            }
    }

    private fun addCorsHeaders(session: IHTTPSession, response: Response): Response =
        if (isAllowedDeviceBrowserOrigin(session)) {
            response.apply {
                addHeader("Access-Control-Allow-Origin", DEVICE_BROWSER_ORIGIN)
                addHeader("Vary", "Origin")
            }
        } else {
            response
        }

    private fun header(session: IHTTPSession, name: String): String? =
        session.headers.entries.firstOrNull { it.key.equals(name, ignoreCase = true) }?.value

    private fun isAuthorizedByBasic(session: IHTTPSession): Boolean {
        val authorization = header(session, "authorization")?.trim() ?: return false
        if (!authorization.startsWith("Basic ", ignoreCase = true)) return false
        val credentials = runCatching {
            String(
                Base64.decode(authorization.substringAfter(' '), Base64.DEFAULT),
                Charsets.UTF_8,
            )
        }.getOrNull() ?: return false
        val separator = credentials.indexOf(':')
        if (separator <= 0) return false
        // The access code is the secret; accepting a saved username avoids Edge reusing an older label.
        val suppliedPassword = credentials.substring(separator + 1)
        return MessageDigest.isEqual(
            password.toByteArray(Charsets.UTF_8),
            suppliedPassword.toByteArray(Charsets.UTF_8),
        )
    }

    private fun isAuthorizedByCookie(session: IHTTPSession): Boolean {
        val cookieHeader = header(session, "cookie") ?: return false
        val token = cookieHeader.split(';')
            .map(String::trim)
            .firstOrNull { it.startsWith("$SESSION_COOKIE_NAME=") }
            ?.substringAfter('=')
            ?: return false
        val parts = token.split('.', limit = 3)
        if (parts.size != 3) return false
        val expiresAt = parts[0].toLongOrNull() ?: return false
        val now = System.currentTimeMillis()
        if (expiresAt <= now || expiresAt - now > SESSION_DURATION_MS) return false
        val payload = "${parts[0]}.${parts[1]}"
        val expected = runCatching { signSession(payload) }.getOrNull() ?: return false
        val actual = runCatching {
            Base64.decode(parts[2], Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        }.getOrNull() ?: return false
        return MessageDigest.isEqual(expected, actual)
    }

    private fun createSessionCookieHeader(): String {
        val expiresAt = System.currentTimeMillis() + SESSION_DURATION_MS
        val nonce = ByteArray(SESSION_NONCE_BYTES).also(SecureRandom()::nextBytes)
        val nonceText = Base64.encodeToString(nonce, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING)
        val payload = "$expiresAt.$nonceText"
        val signature = Base64.encodeToString(
            signSession(payload),
            Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING,
        )
        val token = "$payload.$signature"
        return "$SESSION_COOKIE_NAME=$token; Path=/; Max-Age=$SESSION_MAX_AGE_SECONDS; HttpOnly; SameSite=Strict"
    }

    private fun signSession(payload: String): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(password.toByteArray(Charsets.UTF_8), "HmacSHA256"))
        return mac.doFinal(payload.toByteArray(Charsets.UTF_8))
    }

    private fun resolveMountedFile(path: String, roots: List<MountedStorage>): File? {
        if (path.isBlank()) return null
        val canonical = runCatching { File(path).canonicalFile }.getOrNull() ?: return null
        return canonical.takeIf { file -> roots.any { isWithin(file, it.directory) } }
    }

    private fun isLocalNetworkClient(remoteAddress: String): Boolean {
        val remote = runCatching { InetAddress.getByName(remoteAddress) }.getOrNull() as? Inet4Address
            ?: return false
        return activeLanInterfaces().any { network ->
            network.interfaceAddresses.any { local ->
                val address = local.address as? Inet4Address ?: return@any false
                address.isSiteLocalAddress && sameSubnet(address.address, remote.address, local.networkPrefixLength.toInt())
            }
        }
    }

    private fun sameSubnet(local: ByteArray, remote: ByteArray, prefixLength: Int): Boolean {
        if (local.size != 4 || remote.size != 4 || prefixLength !in 1..32) return false
        for (index in 0 until 4) {
            val remainingBits = (prefixLength - index * 8).coerceIn(0, 8)
            val mask = if (remainingBits == 0) 0 else (0xff shl (8 - remainingBits)) and 0xff
            if ((local[index].toInt() and 0xff and mask) != (remote[index].toInt() and 0xff and mask)) {
                return false
            }
        }
        return true
    }

    private fun mountedStorages(context: Context): List<MountedStorage> {
        val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as? StorageManager
        val volumes = runCatching { storageManager?.storageVolumes.orEmpty() }.getOrDefault(emptyList())
        val mounted = volumes.mapNotNull { volume ->
            if (
                (volume.state != Environment.MEDIA_MOUNTED &&
                    volume.state != Environment.MEDIA_MOUNTED_READ_ONLY) ||
                (!volume.isPrimary && !volume.isRemovable)
            ) {
                return@mapNotNull null
            }
            val directory = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                runCatching { volume.directory }.getOrNull()
            } else {
                legacyStorageRoot(context, storageManager, volume)
            }?.let { runCatching { it.canonicalFile }.getOrNull() } ?: return@mapNotNull null
            if (!directory.isDirectory || !directory.canRead()) return@mapNotNull null
            val label = if (volume.isPrimary) {
                "Stockage interne"
            } else {
                runCatching { volume.getDescription(context) }.getOrNull()?.trim()?.takeIf(String::isNotBlank)
                    ?: "Stockage USB"
            }
            MountedStorage(directory, label, volume.isPrimary)
        }
        val withPrimary = if (mounted.none(MountedStorage::isPrimary)) {
            @Suppress("DEPRECATION")
            val primary = runCatching { Environment.getExternalStorageDirectory().canonicalFile }.getOrNull()
            if (primary != null && primary.isDirectory && primary.canRead()) {
                mounted + MountedStorage(primary, "Stockage interne", true)
            } else {
                mounted
            }
        } else {
            mounted
        }
        return withPrimary.distinctBy { it.directory.path }
            .sortedWith(compareByDescending<MountedStorage> { it.isPrimary }.thenBy { it.label.lowercase() })
    }

    @Suppress("DEPRECATION")
    private fun legacyStorageRoot(
        context: Context,
        storageManager: StorageManager?,
        volume: StorageVolume,
    ): File? {
        val appDirectory = context.getExternalFilesDirs(null)
            .filterNotNull()
            .firstOrNull { directory ->
                runCatching {
                    val mountedVolume = storageManager?.getStorageVolume(directory)
                    mountedVolume != null &&
                        mountedVolume.uuid == volume.uuid &&
                        mountedVolume.isPrimary == volume.isPrimary
                }.getOrDefault(false)
            }
        val marker = File.separator + "Android" + File.separator + "data" + File.separator
        val appPath = appDirectory?.absolutePath
        val rootPath = appPath?.takeIf { it.contains(marker) }?.substringBefore(marker)?.takeIf(String::isNotBlank)
        if (rootPath != null) return File(rootPath)
        return Environment.getExternalStorageDirectory().takeIf { volume.isPrimary }
    }

    private fun isWithin(file: File, root: File): Boolean {
        val canonicalRoot = runCatching { root.canonicalPath }.getOrNull() ?: return false
        val canonicalFile = runCatching { file.canonicalPath }.getOrNull() ?: return false
        return canonicalFile == canonicalRoot || canonicalFile.startsWith(
            if (canonicalRoot.endsWith(File.separator)) canonicalRoot else canonicalRoot + File.separator,
        )
    }

    private data class MountedStorage(
        val directory: File,
        val label: String,
        val isPrimary: Boolean,
    )

    companion object {
        const val PORT = 1111
        const val BASIC_USERNAME = "jojo"
        private const val DEVICE_BROWSER_ORIGIN = "https://jojotv.pages.dev"
        private const val PAGE_ASSET = "lan_file_share.html"
        private const val SESSION_COOKIE_NAME = "jojo_tv_lan_session"
        private const val SESSION_MAX_AGE_SECONDS = 31_536_000L
        private const val SESSION_DURATION_MS = SESSION_MAX_AGE_SECONDS * 1_000L
        private const val SESSION_NONCE_BYTES = 16
        private const val MAX_FORM_BODY_BYTES = 8_192L
        private const val MAX_ENTRY_NAME_LENGTH = 255
        private const val FORM_CONTENT_TYPE = "application/x-www-form-urlencoded"
        private const val ACTION_HEADER_VALUE = "file-action"
        private val CORS_PATHS = setOf("/api/list", "/api/rename", "/api/delete", "/download")
        private val CORS_ALLOWED_HEADERS = setOf("authorization", "content-type", "x-jojotv-request")

        fun localAddresses(): List<String> = activeLanInterfaces()
            .flatMap { network ->
                network.interfaceAddresses.mapNotNull { local ->
                    val address = local.address as? Inet4Address ?: return@mapNotNull null
                    address.takeIf { it.isSiteLocalAddress && !it.isLoopbackAddress }?.hostAddress
                }
            }
            .distinct()
            .sorted()

        private fun activeLanInterfaces(): List<NetworkInterface> = runCatching {
            val interfaces = NetworkInterface.getNetworkInterfaces() ?: return@runCatching emptyList()
            Collections.list(interfaces).filter { network ->
                network.isUp && !network.isLoopback && isLanInterface(network.name)
            }
        }.getOrDefault(emptyList())

        private fun isLanInterface(name: String): Boolean {
            val value = name.lowercase()
            return value.startsWith("wlan") || value.startsWith("wifi") ||
                value.startsWith("eth") || value.startsWith("en")
        }
    }
}
