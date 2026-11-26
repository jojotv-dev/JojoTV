package com.nuvio.tv.core.server

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.nuvio.tv.R
import com.nuvio.tv.core.profile.ProtectedProfilePreferences
import java.security.SecureRandom

class LanFileShareService : Service() {
    private var server: LanFileShareHttpServer? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            setEnabledPreference(this, false)
            stopServer()
            stopSelf(startId)
            return START_NOT_STICKY
        }
        if (!isSupported() || !isEnabled(this)) {
            stopServer()
            stopSelf(startId)
            return START_NOT_STICKY
        }
        if (server?.isAlive == true) return START_STICKY

        startForeground(NOTIFICATION_ID, buildNotification())
        lastStartError = null
        try {
            val password = accessCode(this)
            server = LanFileShareHttpServer(applicationContext, password).also {
                it.start(SOCKET_READ_TIMEOUT_MS, false)
            }
            serverActive = true
        } catch (failure: Exception) {
            lastStartError = failure.localizedMessage ?: getString(R.string.lan_share_start_failed)
            stopServer()
            stopForeground(true)
            stopSelf(startId)
            return START_NOT_STICKY
        }
        return START_STICKY
    }

    override fun onDestroy() {
        stopServer()
        stopForeground(true)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun stopServer() {
        runCatching { server?.stop() }
        server = null
        serverActive = false
    }

    private fun buildNotification() = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle(getString(R.string.lan_share_notification_title))
        .setContentText(getString(R.string.lan_share_notification_text))
        .setSmallIcon(android.R.drawable.ic_menu_share)
        .setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .addAction(
            0,
            getString(R.string.lan_share_notification_stop),
            PendingIntent.getService(
                this,
                NOTIFICATION_ID,
                Intent(this, LanFileShareService::class.java).setAction(ACTION_STOP),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            ),
        )
        .build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.lan_share_notification_channel),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = getString(R.string.lan_share_notification_text)
            setShowBadge(false)
            setSound(null, null)
        }
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "lan_file_share"
        private const val NOTIFICATION_ID = 4817
        private const val ACTION_STOP = "com.nuvio.tv.action.STOP_LAN_FILE_SHARE"
        private const val PREFS_NAME = "lan_file_share"
        private const val PREF_ENABLED = "enabled"
        private const val PASSWORD_PROFILE_ID = 1
        private const val PASSWORD_PREFS = "lan_file_share_secret"
        private const val PASSWORD_KEY_ALIAS = "nuvio.lan.file.share.secret"
        private const val SOCKET_READ_TIMEOUT_MS = 5_000
        private const val PASSWORD_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789"

        @Volatile
        private var serverActive = false

        @Volatile
        private var lastStartError: String? = null

        fun isSupported(): Boolean =
            Build.VERSION.SDK_INT <= Build.VERSION_CODES.S

        fun isEnabled(context: Context): Boolean =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(PREF_ENABLED, false)

        fun isRunning(): Boolean = serverActive

        fun startError(): String? = lastStartError

        fun setEnabled(context: Context, enabled: Boolean) {
            setEnabledPreference(context, enabled)
            if (enabled) {
                startIfEnabled(context)
            } else {
                lastStartError = null
                context.stopService(Intent(context, LanFileShareService::class.java))
            }
        }

        fun startIfEnabled(context: Context) {
            if (!isEnabled(context) || !isSupported()) return
            val intent = Intent(context, LanFileShareService::class.java)
            runCatching {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(context, intent)
                } else {
                    context.startService(intent)
                }
            }.onFailure {
                lastStartError = it.localizedMessage ?: context.getString(R.string.lan_share_start_failed)
            }
        }

        fun accessCode(context: Context): String {
            val protected = ProtectedProfilePreferences(
                context.applicationContext,
                PASSWORD_PREFS,
                PASSWORD_KEY_ALIAS,
            )
            protected.read(PASSWORD_PROFILE_ID)?.let { return it }
            val random = SecureRandom()
            val generated = buildString(16) {
                repeat(16) { append(PASSWORD_ALPHABET[random.nextInt(PASSWORD_ALPHABET.length)]) }
            }
            protected.write(PASSWORD_PROFILE_ID, generated)
            return generated
        }

        private fun setEnabledPreference(context: Context, enabled: Boolean) {
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(PREF_ENABLED, enabled)
                .commit()
        }
    }
}
