package com.nuvio.tv.core.network

import com.nuvio.tv.data.local.IptvSettingsDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Dns
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.SocketTimeoutException
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

data class IptvDnsProvider(
    val id: String,
    val name: String,
    val primary: String?,
    val secondary: String?,
    val note: String
)

object IptvDnsProviders {
    const val SYSTEM_ID = "system"

    val all: List<IptvDnsProvider> = listOf(
        IptvDnsProvider(
            id = SYSTEM_ID,
            name = "DNS automatique",
            primary = null,
            secondary = null,
            note = "Utilise le DNS configure sur l'appareil ou la box."
        ),
        IptvDnsProvider(
            id = "cloudflare",
            name = "Cloudflare",
            primary = "1.1.1.1",
            secondary = "1.0.0.1",
            note = "Rapide, generaliste, avec DNSSEC."
        ),
        IptvDnsProvider(
            id = "controld",
            name = "Control D",
            primary = "76.76.2.0",
            secondary = "76.76.10.0",
            note = "DNS public rapide de Control D."
        ),
        IptvDnsProvider(
            id = "google",
            name = "Google Public DNS",
            primary = "8.8.8.8",
            secondary = "8.8.4.4",
            note = "Tres compatible, souvent utile en depannage."
        ),
        IptvDnsProvider(
            id = "quad9",
            name = "Quad9",
            primary = "9.9.9.9",
            secondary = "149.112.112.112",
            note = "DNS axe securite, blocage de domaines malveillants."
        ),
        IptvDnsProvider(
            id = "opendns",
            name = "OpenDNS",
            primary = "208.67.222.222",
            secondary = "208.67.220.220",
            note = "DNS public Cisco/OpenDNS."
        ),
        IptvDnsProvider(
            id = "gcore",
            name = "Gcore",
            primary = "95.85.95.85",
            secondary = "2.56.220.2",
            note = "DNS public oriente performance."
        ),
        IptvDnsProvider(
            id = "nextdns",
            name = "NextDNS",
            primary = "45.90.28.0",
            secondary = "45.90.30.0",
            note = "DNS public NextDNS sans profil personnalise."
        ),
        IptvDnsProvider(
            id = "cleanbrowsing",
            name = "CleanBrowsing",
            primary = "185.228.168.9",
            secondary = "185.228.169.9",
            note = "Filtrage securite contre domaines dangereux."
        ),
        IptvDnsProvider(
            id = "fdn",
            name = "FDN",
            primary = "80.67.169.12",
            secondary = "80.67.169.40",
            note = "DNS associatif FDN, avec validation DNSSEC."
        )
    )

    fun byId(id: String?): IptvDnsProvider =
        all.firstOrNull { it.id == id } ?: all.first()
}

@Singleton
class IptvDnsResolver @Inject constructor(
    private val settingsDataStore: IptvSettingsDataStore
) : Dns {
    private val fallback = IPv4FirstDns()

    override fun lookup(hostname: String): List<InetAddress> {
        val selected = runCatching {
            runBlocking { settingsDataStore.dnsSettings.first().providerId }
        }.getOrDefault(IptvDnsProviders.SYSTEM_ID)
        val provider = IptvDnsProviders.byId(selected)
        if (provider.id == IptvDnsProviders.SYSTEM_ID) {
            return fallback.lookup(hostname)
        }

        val resolved = provider.servers()
            .asSequence()
            .flatMap { server ->
                sequence {
                    yieldAll(query(server, hostname, DNS_TYPE_A))
                    yieldAll(query(server, hostname, DNS_TYPE_AAAA))
                }
            }
            .distinctBy { it.hostAddress }
            .sortedBy { if (it is Inet4Address) 0 else 1 }
            .toList()

        return resolved.ifEmpty { fallback.lookup(hostname) }
    }

    private fun IptvDnsProvider.servers(): List<String> =
        listOfNotNull(primary, secondary).filter { it.isNotBlank() }

    private fun query(server: String, hostname: String, type: Int): List<InetAddress> {
        val transactionId = random.nextInt(0x10000)
        val query = buildQuery(transactionId, hostname, type)
        return try {
            DatagramSocket().use { socket ->
                socket.soTimeout = DNS_TIMEOUT_MS
                val serverAddress = InetAddress.getByName(server)
                socket.send(DatagramPacket(query, query.size, serverAddress, DNS_PORT))

                val buffer = ByteArray(1500)
                val response = DatagramPacket(buffer, buffer.size)
                socket.receive(response)
                parseResponse(buffer.copyOf(response.length), transactionId, type)
            }
        } catch (_: SocketTimeoutException) {
            emptyList()
        } catch (_: IOException) {
            emptyList()
        } catch (_: RuntimeException) {
            emptyList()
        }
    }

    private fun buildQuery(transactionId: Int, hostname: String, type: Int): ByteArray {
        val labels = hostname.trimEnd('.').split('.').filter { it.isNotBlank() }
        val size = DNS_HEADER_SIZE + labels.sumOf { it.toByteArray(Charsets.UTF_8).size + 1 } + 1 + 4
        val bytes = ByteArray(size)
        bytes[0] = (transactionId ushr 8).toByte()
        bytes[1] = transactionId.toByte()
        bytes[2] = 0x01
        bytes[5] = 0x01
        var offset = DNS_HEADER_SIZE
        labels.forEach { label ->
            val labelBytes = label.toByteArray(Charsets.UTF_8)
            bytes[offset++] = labelBytes.size.toByte()
            labelBytes.copyInto(bytes, offset)
            offset += labelBytes.size
        }
        bytes[offset++] = 0
        bytes[offset++] = (type ushr 8).toByte()
        bytes[offset++] = type.toByte()
        bytes[offset++] = 0
        bytes[offset] = DNS_CLASS_IN.toByte()
        return bytes
    }

    private fun parseResponse(bytes: ByteArray, transactionId: Int, requestedType: Int): List<InetAddress> {
        if (bytes.size < DNS_HEADER_SIZE) return emptyList()
        val id = readUShort(bytes, 0)
        if (id != transactionId) return emptyList()
        val responseCode = bytes[3].toInt() and 0x0F
        if (responseCode != 0) return emptyList()

        val questionCount = readUShort(bytes, 4)
        val answerCount = readUShort(bytes, 6)
        var offset = DNS_HEADER_SIZE
        repeat(questionCount) {
            offset = skipName(bytes, offset)
            offset += 4
            if (offset > bytes.size) return emptyList()
        }

        val addresses = mutableListOf<InetAddress>()
        repeat(answerCount) {
            offset = skipName(bytes, offset)
            if (offset + 10 > bytes.size) return@repeat
            val type = readUShort(bytes, offset)
            val clazz = readUShort(bytes, offset + 2)
            val dataLength = readUShort(bytes, offset + 8)
            offset += 10
            if (offset + dataLength > bytes.size) return@repeat
            if (clazz == DNS_CLASS_IN && type == requestedType) {
                when {
                    type == DNS_TYPE_A && dataLength == 4 -> {
                        addresses += Inet4Address.getByAddress(bytes.copyOfRange(offset, offset + 4)) as Inet4Address
                    }
                    type == DNS_TYPE_AAAA && dataLength == 16 -> {
                        addresses += Inet6Address.getByAddress(bytes.copyOfRange(offset, offset + 16)) as Inet6Address
                    }
                }
            }
            offset += dataLength
        }
        return addresses
    }

    private fun skipName(bytes: ByteArray, start: Int): Int {
        var offset = start
        var jumps = 0
        while (offset < bytes.size) {
            val length = bytes[offset].toInt() and 0xFF
            if (length == 0) return offset + 1
            if ((length and 0xC0) == 0xC0) return offset + 2
            offset += length + 1
            jumps++
            if (jumps > 64) break
        }
        return bytes.size
    }

    private fun readUShort(bytes: ByteArray, offset: Int): Int =
        ((bytes[offset].toInt() and 0xFF) shl 8) or (bytes[offset + 1].toInt() and 0xFF)

    private companion object {
        const val DNS_PORT = 53
        const val DNS_TIMEOUT_MS = 1800
        const val DNS_HEADER_SIZE = 12
        const val DNS_CLASS_IN = 1
        const val DNS_TYPE_A = 1
        const val DNS_TYPE_AAAA = 28
        val random = SecureRandom()
    }
}
