package com.nuvio.tv.data.storage

import jcifs.CIFSContext
import jcifs.config.PropertyConfiguration
import jcifs.context.BaseContext
import jcifs.smb.NtlmPasswordAuthenticator
import jcifs.smb.SmbFile as JcifsSmbFile
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.Properties

data class StorageCapacity(
    val freeBytes: Long,
    val totalBytes: Long,
)

/** Reads a share's free and total bytes without blocking the browser or server hub UI. */
object SmbStorageCapacity {
    private const val TIMEOUT_MS = 8_000

    suspend fun read(address: String, username: String, password: String): StorageCapacity? =
        withContext(Dispatchers.IO) {
            try {
                val provider = jcifs.util.Crypto.getProvider()
                MessageDigest.getInstance("MD4", provider)
                val file = JcifsSmbFile(normalizeAddress(address), cifsContext(username, password))
                val freeBytes = file.diskFreeSpace
                val sizeField = JcifsSmbFile::class.java.getDeclaredField("size").apply { isAccessible = true }
                val totalBytes = sizeField.getLong(file)
                StorageCapacity(freeBytes, totalBytes)
            } catch (error: CancellationException) {
                throw error
            } catch (_: Exception) {
                null
            }
        }

    private fun normalizeAddress(address: String): String {
        val normalized = address.trim().replace('\\', '/').trimEnd('/')
        return if (normalized.startsWith("smb://", ignoreCase = true)) "$normalized/" else "smb://$normalized/"
    }

    private fun cifsContext(username: String, password: String): CIFSContext {
        val properties = Properties().apply {
            setProperty("jcifs.smb.client.minVersion", "SMB202")
            setProperty("jcifs.smb.client.maxVersion", "SMB311")
            setProperty("jcifs.smb.client.responseTimeout", TIMEOUT_MS.toString())
            setProperty("jcifs.smb.client.soTimeout", TIMEOUT_MS.toString())
            setProperty("jcifs.smb.client.connTimeout", TIMEOUT_MS.toString())
        }
        val base = BaseContext(PropertyConfiguration(properties))
        if (username.isBlank()) return base

        val domainSeparator = username.indexOf('\\')
        val domain = if (domainSeparator > 0) username.substring(0, domainSeparator) else ""
        val user = if (domainSeparator > 0) username.substring(domainSeparator + 1) else username
        return base.withCredentials(NtlmPasswordAuthenticator(domain, user, password))
    }
}
