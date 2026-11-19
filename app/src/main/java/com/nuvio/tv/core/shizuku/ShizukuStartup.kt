package com.nuvio.tv.core.shizuku

import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import rikka.shizuku.Shizuku
import java.io.DataInputStream
import java.io.IOException
import java.math.BigInteger
import java.net.ConnectException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.Signature
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAKeyGenParameterSpec
import java.util.concurrent.atomic.AtomicBoolean

/** Device-local Shizuku startup, authorized once through this TV's loopback ADB. */
internal object ShizukuStartup {
    const val PERMISSION_REQUEST_CODE = 6431

    private const val PREFERENCES_NAME = "nuvio_shizuku_startup"
    private const val AUTO_START_KEY = "auto_start"
    private const val SHIZUKU_PACKAGE = "moe.shizuku.privileged.api"
    private const val ADB_PORT = 5555

    enum class Status {
        DISABLED,
        NOT_RUNNING,
        CONNECTING,
        STARTING_SERVICE,
        READY,
        PERMISSION_REQUIRED,
        SHIZUKU_NOT_INSTALLED,
        ADB_PORT_UNAVAILABLE,
        ADB_AUTHORIZATION_REQUIRED,
        ADB_AUTHORIZATION_PENDING,
        STARTUP_TIMEOUT,
        FAILED
    }

    private enum class Availability { READY, PERMISSION_REQUIRED, UNAVAILABLE }

    private val startupRunning = AtomicBoolean(false)
    private val startupLock = Any()
    private val startupCallbacks = ArrayList<() -> Unit>()
    private val mainHandler = Handler(Looper.getMainLooper())
    private val mutableStatus = MutableStateFlow(Status.DISABLED)
    val status: StateFlow<Status> = mutableStatus.asStateFlow()

    fun autoStartEnabled(context: Context): Boolean = context.applicationContext
        .getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
        .getBoolean(AUTO_START_KEY, false)

    fun setAutoStartEnabled(context: Context, enabled: Boolean) {
        context.applicationContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(AUTO_START_KEY, enabled)
            .apply()
        refreshStatus(context)
    }

    fun refreshStatus(context: Context) {
        if (startupRunning.get()) return
        mutableStatus.value = when (availability()) {
            Availability.READY -> Status.READY
            Availability.PERMISSION_REQUIRED -> Status.PERMISSION_REQUIRED
            Availability.UNAVAILABLE -> if (autoStartEnabled(context)) Status.NOT_RUNNING else Status.DISABLED
        }
    }

    /** Called when Nuvio becomes foreground after the TV starts or wakes. */
    fun startIfEnabled(context: Context) {
        val application = context.applicationContext
        if (!autoStartEnabled(application)) {
            refreshStatus(application)
            return
        }
        when (availability()) {
            Availability.READY -> mutableStatus.value = Status.READY
            Availability.PERMISSION_REQUIRED -> mutableStatus.value = Status.PERMISSION_REQUIRED
            Availability.UNAVAILABLE -> startWithAdb(application, allowAuthorization = false, finished = null)
        }
    }

    /** Explicit settings action: the first use may ask the user to trust Nuvio's ADB key. */
    fun configure(context: Context, finished: () -> Unit) {
        when (availability()) {
            Availability.READY -> {
                mutableStatus.value = Status.READY
                mainHandler.post { finished() }
            }
            Availability.PERMISSION_REQUIRED -> {
                mutableStatus.value = Status.PERMISSION_REQUIRED
                mainHandler.post {
                    runCatching { Shizuku.requestPermission(PERMISSION_REQUEST_CODE) }
                    finished()
                }
            }
            Availability.UNAVAILABLE -> startWithAdb(
                context.applicationContext,
                allowAuthorization = true,
                finished = {
                    if (availability() == Availability.PERMISSION_REQUIRED) {
                        runCatching { Shizuku.requestPermission(PERMISSION_REQUEST_CODE) }
                    }
                    finished()
                }
            )
        }
    }

    private fun startWithAdb(context: Context, allowAuthorization: Boolean, finished: (() -> Unit)?) {
        synchronized(startupLock) {
            if (finished != null) startupCallbacks += finished
            if (!startupRunning.compareAndSet(false, true)) return
            mutableStatus.value = Status.CONNECTING
        }

        Thread({
            var authenticated = false
            var cancelled = false
            try {
                val manager = context.packageManager.getApplicationInfo(SHIZUKU_PACKAGE, 0)
                var connected = false
                for (attempt in 0..3) {
                    if (!allowAuthorization && !autoStartEnabled(context)) {
                        mutableStatus.value = Status.DISABLED
                        cancelled = true
                        break
                    }
                    try {
                        Socket().use { socket ->
                            socket.connect(InetSocketAddress("127.0.0.1", ADB_PORT), 3_000)
                            socket.soTimeout = if (allowAuthorization) 60_000 else 8_000
                            socket.tcpNoDelay = true
                            val adb = LocalAdb(socket)
                            adb.authenticate(allowAuthorization)
                            authenticated = true

                            if (!allowAuthorization && !autoStartEnabled(context)) {
                                mutableStatus.value = Status.DISABLED
                                cancelled = true
                            } else if (availability() == Availability.UNAVAILABLE) {
                                mutableStatus.value = Status.STARTING_SERVICE
                                socket.soTimeout = 20_000
                                val binary = shellQuote("${manager.nativeLibraryDir}/libshizuku.so")
                                val startScript = shellQuote("/sdcard/Android/data/$SHIZUKU_PACKAGE/start.sh")
                                val command = "if [ -x $binary ]; then $binary " +
                                    "${shellQuote("--apk=${manager.sourceDir}")}; else sh $startScript; fi; " +
                                    "printf '\\nNUVIO_SHIZUKU_EXIT:%s\\n' \"\$?\""
                                if (!adb.shell(command).contains("NUVIO_SHIZUKU_EXIT:0")) {
                                    throw IOException("Shizuku starter failed")
                                }
                            }
                        }
                        if (cancelled) break
                        connected = true
                        break
                    } catch (error: ConnectException) {
                        if (attempt == 3) throw error
                        SystemClock.sleep(2_000)
                    }
                }
                if (cancelled) return@Thread
                if (!connected) throw ConnectException("ADB loopback connection failed")

                val deadline = SystemClock.elapsedRealtime() + 8_000
                while (availability() == Availability.UNAVAILABLE && SystemClock.elapsedRealtime() < deadline) {
                    SystemClock.sleep(200)
                }
                when (availability()) {
                    Availability.READY -> mutableStatus.value = Status.READY
                    Availability.PERMISSION_REQUIRED -> mutableStatus.value = Status.PERMISSION_REQUIRED
                    Availability.UNAVAILABLE -> mutableStatus.value = Status.STARTUP_TIMEOUT
                }
            } catch (_: PackageManager.NameNotFoundException) {
                mutableStatus.value = Status.SHIZUKU_NOT_INSTALLED
            } catch (_: SocketTimeoutException) {
                mutableStatus.value = if (authenticated) Status.STARTUP_TIMEOUT else Status.ADB_AUTHORIZATION_PENDING
            } catch (_: AdbAuthorizationException) {
                mutableStatus.value = Status.ADB_AUTHORIZATION_REQUIRED
            } catch (_: ConnectException) {
                mutableStatus.value = Status.ADB_PORT_UNAVAILABLE
            } catch (_: Exception) {
                mutableStatus.value = Status.FAILED
            } finally {
                val callbacks = synchronized(startupLock) {
                    startupRunning.set(false)
                    startupCallbacks.toList().also { startupCallbacks.clear() }
                }
                callbacks.forEach { callback -> mainHandler.post { callback() } }
            }
        }, "NuvioShizukuStartup").start()
    }

    private fun availability(): Availability = try {
        if (!Shizuku.pingBinder()) {
            Availability.UNAVAILABLE
        } else if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
            Availability.READY
        } else {
            Availability.PERMISSION_REQUIRED
        }
    } catch (_: Throwable) {
        Availability.UNAVAILABLE
    }

    private fun adbKey(): KeyStore.PrivateKeyEntry {
        val alias = "nuvio_shizuku_adb"
        val store = KeyStore.getInstance("AndroidKeyStore")
        store.load(null)
        if (!store.containsAlias(alias)) {
            val generator = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore")
            generator.initialize(
                KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN)
                    .setAlgorithmParameterSpec(RSAKeyGenParameterSpec(2048, RSAKeyGenParameterSpec.F4))
                    .setDigests(KeyProperties.DIGEST_NONE, KeyProperties.DIGEST_SHA256)
                    .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                    .build()
            )
            generator.generateKeyPair()
        }
        return store.getEntry(alias, null) as KeyStore.PrivateKeyEntry
    }

    private fun adbPublicKey(key: RSAPublicKey): ByteArray {
        val modulus = key.modulus
        val word = BigInteger.ONE.shiftLeft(32)
        val buffer = ByteBuffer.allocate(524).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putInt(64).putInt(modulus.mod(word).modInverse(word).negate().toInt())
        val numbers = arrayOf(modulus, BigInteger.ONE.shiftLeft(4096).mod(modulus))
        numbers.forEach { initial ->
            var number = initial
            repeat(64) {
                buffer.putInt(number.toInt())
                number = number.shiftRight(32)
            }
        }
        buffer.putInt(key.publicExponent.toInt())
        return (Base64.encodeToString(buffer.array(), Base64.NO_WRAP) + " Nuvio@localhost\u0000")
            .toByteArray(StandardCharsets.UTF_8)
    }

    private class AdbAuthorizationException : IOException()

    /** A loopback-only ADB shell session, matching Jojo Home's opt-in startup flow. */
    private class LocalAdb(socket: Socket) {
        private val input = DataInputStream(socket.getInputStream())
        private val output = socket.getOutputStream()

        fun authenticate(allowAuthorization: Boolean) {
            write(CNXN, 0x01000000, 4096, "host::Nuvio\u0000".toByteArray(StandardCharsets.UTF_8))
            var reply = read()
            if (reply.command == AUTH && reply.arg0 == 1) {
                if (reply.data.size != 20) throw IOException("Invalid ADB token")
                val key = adbKey()
                val signer = Signature.getInstance("NONEwithRSA")
                signer.initSign(key.privateKey)
                signer.update(
                    byteArrayOf(
                        0x30, 0x21, 0x30, 0x09, 0x06, 0x05, 0x2b, 0x0e,
                        0x03, 0x02, 0x1a, 0x05, 0x00, 0x04, 0x14
                    )
                )
                signer.update(reply.data)
                write(AUTH, 2, 0, signer.sign())
                reply = read()
                if (reply.command == AUTH && reply.arg0 == 1) {
                    if (!allowAuthorization) throw AdbAuthorizationException()
                    write(AUTH, 3, 0, adbPublicKey(key.certificate.publicKey as RSAPublicKey))
                    reply = read()
                }
            }
            if (reply.command != CNXN) throw AdbAuthorizationException()
        }

        fun shell(command: String): String {
            write(OPEN, 1, 0, "shell:$command\u0000".toByteArray(StandardCharsets.UTF_8))
            var reply = read()
            if (reply.command != OKAY || reply.arg1 != 1) throw IOException("ADB shell unavailable")
            val remoteId = reply.arg0
            val result = StringBuilder()
            while (true) {
                reply = read()
                if (reply.arg0 != remoteId || reply.arg1 != 1) throw IOException("Invalid ADB stream")
                if (reply.command == CLSE) {
                    write(CLSE, 1, remoteId, byteArrayOf())
                    return result.toString()
                }
                if (reply.command != WRTE) throw IOException("Invalid ADB shell packet")
                if (result.length > 16_384) throw IOException("ADB startup output too long")
                result.append(String(reply.data, StandardCharsets.UTF_8))
                write(OKAY, 1, remoteId, byteArrayOf())
            }
        }

        private fun write(command: Int, arg0: Int, arg1: Int, data: ByteArray) {
            var checksum = 0
            data.forEach { checksum += it.toInt() and 0xff }
            val header = ByteBuffer.allocate(24).order(ByteOrder.LITTLE_ENDIAN)
                .putInt(command)
                .putInt(arg0)
                .putInt(arg1)
                .putInt(data.size)
                .putInt(checksum)
                .putInt(command.inv())
                .array()
            output.write(header)
            output.write(data)
            output.flush()
        }

        private fun read(): Packet {
            val bytes = ByteArray(24)
            input.readFully(bytes)
            val header = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
            val command = header.int
            val arg0 = header.int
            val arg1 = header.int
            val length = header.int
            val checksum = header.int
            val magic = header.int
            if (magic != command.inv() || length < 0 || length > 65_536) throw IOException("Invalid ADB header")
            val data = ByteArray(length)
            input.readFully(data)
            var actualChecksum = 0
            data.forEach { actualChecksum += it.toInt() and 0xff }
            if (checksum != actualChecksum) throw IOException("Invalid ADB checksum")
            return Packet(command, arg0, arg1, data)
        }
    }

    private data class Packet(val command: Int, val arg0: Int, val arg1: Int, val data: ByteArray)

    private fun shellQuote(value: String): String = "'" + value.replace("'", "'\"'\"'") + "'"

    private const val CNXN = 0x4e584e43
    private const val AUTH = 0x48545541
    private const val OPEN = 0x4e45504f
    private const val OKAY = 0x59414b4f
    private const val WRTE = 0x45545257
    private const val CLSE = 0x45534c43
}
