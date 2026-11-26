package com.nuvio.tv.core.launcher

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.os.UserManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.nuvio.tv.MainActivity
import com.nuvio.tv.R
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

/** Fire OS 6 does not consistently bind third-party AccessibilityServices for the remote HOME key. */
class FireOsHomeButtonService : Service() {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var readerJob: Job? = null
    @Volatile private var logcatProcess: Process? = null
    private var registeredWakeReceiver = false
    private var screenWasOff = false
    private var lastWakeLaunchAtMs = 0L
    @Volatile private var lastHomeEventAtMs = 0L
    @Volatile private var homeEventPendingUserUnlock = false
    @Volatile private var includeRecentBootHomeEventOnNextReader = false

    private val wakeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_SCREEN_OFF, Intent.ACTION_DREAMING_STARTED -> screenWasOff = true
                Intent.ACTION_USER_UNLOCKED -> {
                    if (homeEventPendingUserUnlock) {
                        homeEventPendingUserUnlock = false
                        dispatchHomeRequest(SystemClock.elapsedRealtime())
                    }
                }
                Intent.ACTION_SCREEN_ON, Intent.ACTION_DREAMING_STOPPED -> {
                    if (screenWasOff) {
                        screenWasOff = false
                        scheduleWakeRecovery()
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                getString(R.string.settings_fire_os_home_notification_channel),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.settings_fire_os_home_notification_description)
                setShowBadge(false)
                setSound(null, null)
            }
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
        runCatching {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_OFF)
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_DREAMING_STARTED)
                addAction(Intent.ACTION_DREAMING_STOPPED)
                addAction(Intent.ACTION_USER_UNLOCKED)
            }
            registerReceiver(wakeReceiver, filter)
            registeredWakeReceiver = true
        }.onFailure { Log.w(TAG, "Could not observe Fire TV standby", it) }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.getBooleanExtra(EXTRA_INCLUDE_RECENT_BOOT_HOME_EVENT, false) == true) {
            includeRecentBootHomeEventOnNextReader = true
        }
        if (!isSupported(this) || !isEnabled(this) || !hasReadLogsPermission(this)) {
            stopSelf(startId)
            return START_NOT_STICKY
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.settings_fire_os_home_notification_title))
            .setContentText(getString(R.string.settings_fire_os_home_notification_text))
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        val foregroundStarted = runCatching {
            startForeground(NOTIFICATION_ID, notification)
            true
        }.onFailure {
            Log.e(TAG, "Could not keep the Fire OS Home watcher in the foreground", it)
        }.getOrDefault(false)
        if (!foregroundStarted) {
            stopSelf(startId)
            return START_NOT_STICKY
        }
        Log.i(TAG, "Fire OS Home watcher is running")
        startLogcatReader()
        return START_STICKY
    }

    private fun startLogcatReader() {
        if (readerJob?.isActive == true) return
        val includeRecentBootHomeEvent = includeRecentBootHomeEventOnNextReader
        includeRecentBootHomeEventOnNextReader = false
        readerJob = serviceScope.launch {
            var includeBootLookbackOnRetry = includeRecentBootHomeEvent
            while (coroutineContext.isActive) {
                val readerStartedAt = System.currentTimeMillis()
                val logcatStartTime = if (includeBootLookbackOnRetry) {
                    String.format(
                        Locale.US,
                        "%.3f",
                        (readerStartedAt - BOOT_HOME_EVENT_LOOKBACK_MS) / 1_000.0
                    )
                } else "1"
                val process = runCatching {
                    ProcessBuilder(
                        "logcat",
                        "-b", "main",
                        "-b", "system",
                        "-b", "events",
                        "-v", "epoch",
                        "-T", logcatStartTime,
                        "-e", LOGCAT_FILTER
                    ).redirectErrorStream(true).start()
                }.onFailure { Log.w(TAG, "Could not start filtered logcat", it) }.getOrNull()

                if (process == null) {
                    delay(RESTART_DELAY_MS)
                    continue
                }
                val acceptBufferedBootEvent = includeBootLookbackOnRetry
                includeBootLookbackOnRetry = false
                logcatProcess = process
                operational.value = true
                Log.i(
                    TAG,
                    if (acceptBufferedBootEvent) {
                        "Filtered Fire TV Home log reader attached with boot-event catch-up"
                    } else {
                        "Filtered Fire TV Home log reader attached"
                    }
                )
                try {
                    process.inputStream.bufferedReader().use { reader ->
                        while (coroutineContext.isActive) {
                            val line = reader.readLine() ?: break
                            homeEventUptime(line, readerStartedAt, acceptBufferedBootEvent)?.let(::handleHomeEvent)
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    Log.w(TAG, "Filtered logcat reader stopped", e)
                } finally {
                    if (logcatProcess === process) logcatProcess = null
                    process.destroy()
                }
                operational.value = false
                if (!hasReadLogsPermission(this@FireOsHomeButtonService)) {
                    stopSelf()
                    return@launch
                }
                if (coroutineContext.isActive) delay(RESTART_DELAY_MS)
            }
        }
    }

    private fun homeEventUptime(
        line: String,
        readerStartedAtWallMs: Long,
        acceptBufferedBootEvent: Boolean
    ): Long? {
        if (line.contains("JojoFireHome", ignoreCase = true) || line.contains(packageName, ignoreCase = true)) return null

        val isOzdcsHomePress = line.contains("OZ-DCS", ignoreCase = true) &&
            line.contains("HomePressReceiver", ignoreCase = true)
        val isHomeListenerPress = line.contains("HomeListener", ignoreCase = true) &&
            HOME_LISTENER_PRESS.containsMatchIn(line)
        val isHomeActivityLaunch = line.contains("ActivityManager", ignoreCase = true) &&
            line.contains("START", ignoreCase = true) && line.contains("com.amazon", ignoreCase = true) &&
            (line.contains("cat=[android.intent.category.HOME]", ignoreCase = true) ||
                line.contains("launcher", ignoreCase = true) || line.contains("tv.home", ignoreCase = true))
        val isEventBufferHomeLaunch = line.contains("am_create_activity", ignoreCase = true) &&
            line.contains("com.amazon", ignoreCase = true) &&
            (line.contains("launcher", ignoreCase = true) || line.contains("tv.home", ignoreCase = true))
        if (!isOzdcsHomePress && !isHomeListenerPress && !isHomeActivityLaunch && !isEventBufferHomeLaunch) return null

        // Fire OS pads logcat timestamps with spaces; startup catch-up intentionally accepts
        // matching HOME entries from the recent boot window.
        val epochSeconds = line.trimStart().substringBefore(' ').toDoubleOrNull() ?: return null
        val eventWallMs = (epochSeconds * 1_000.0).toLong()
        val nowWallMs = System.currentTimeMillis()
        val allowedLookbackMs = if (acceptBufferedBootEvent) BOOT_HOME_EVENT_LOOKBACK_MS else BUFFER_TOLERANCE_MS
        if (eventWallMs < readerStartedAtWallMs - allowedLookbackMs || eventWallMs > nowWallMs + BUFFER_TOLERANCE_MS) {
            return null
        }
        val ageMs = (nowWallMs - eventWallMs).coerceAtLeast(0L)
        return (SystemClock.elapsedRealtime() - ageMs).coerceAtLeast(0L)
    }

    private fun handleHomeEvent(eventUptimeMs: Long) {
        if (LauncherSetup.isDefaultHome(this)) {
            if (isUserUnlocked(this)) LauncherSetup.observeNativeHome(this, true)
            else preferences(this).edit().putBoolean(PREF_ENABLED, false).apply()
            stopSelf()
            return
        }
        if (!isEnabled(this)) return
        val previous = lastHomeEventAtMs
        if (previous != 0L && eventUptimeMs - previous < HomeKeyGesture.DEFAULT_MIN_GAP_MS) return
        lastHomeEventAtMs = eventUptimeMs
        val eventAgeMs = (SystemClock.elapsedRealtime() - eventUptimeMs).coerceAtLeast(0L)
        Log.i(TAG, "Detected Fire TV Home event (age=${eventAgeMs}ms)")

        if (!isUserUnlocked(this)) {
            homeEventPendingUserUnlock = true
            return
        }
        dispatchHomeRequest(eventUptimeMs)
    }

    private fun dispatchHomeRequest(eventUptimeMs: Long) {
        mainHandler.post {
            if (!isEnabled(this) || LauncherSetup.isDefaultHome(this)) return@post
            Log.i(TAG, "Redirecting Fire TV Home to Nuvio")
            val deliveredInForeground = HomeForegroundDelivery.deliver(eventUptimeMs)
            val launched = if (!deliveredInForeground) {
                val launch = Intent(this, MainActivity::class.java).apply {
                    putExtra(LauncherHomeRequest.EXTRA_GO_HOME, true)
                    putExtra(LauncherHomeRequest.EXTRA_HOME_PRESS_UPTIME_MS, eventUptimeMs)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                runCatching { startActivity(launch) }
                    .onFailure { Log.w(TAG, "Could not return to Nuvio after Fire TV HOME", it) }
                    .isSuccess
            } else true
            if (launched) LauncherBootLaunch.cancelPending(this)
        }
    }

    private fun scheduleWakeRecovery() {
        mainHandler.removeCallbacks(wakeRecoveryRunnable)
        mainHandler.postDelayed(wakeRecoveryRunnable, WAKE_RECOVERY_DELAY_MS)
    }

    private val wakeRecoveryRunnable = Runnable {
        val now = SystemClock.elapsedRealtime()
        if (now - lastWakeLaunchAtMs < WAKE_DUPLICATE_WINDOW_MS) return@Runnable
        if (lastHomeEventAtMs != 0L && now - lastHomeEventAtMs in 0..WAKE_DUPLICATE_WINDOW_MS) return@Runnable
        if (!isEnabled(this) || LauncherSetup.isDefaultHome(this)) {
            return@Runnable
        }
        if (!isUserUnlocked(this)) {
            homeEventPendingUserUnlock = true
            return@Runnable
        }
        if (HomeForegroundDelivery.isResumed) return@Runnable
        lastWakeLaunchAtMs = now
        Log.d(TAG, "Restoring Nuvio after Fire TV standby")
        val launch = Intent(this, MainActivity::class.java).apply {
            putExtra(LauncherHomeRequest.EXTRA_GO_HOME, true)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        runCatching { startActivity(launch) }
            .onFailure { Log.w(TAG, "Could not restore Nuvio after Fire TV standby", it) }
    }

    override fun onDestroy() {
        operational.value = false
        mainHandler.removeCallbacks(wakeRecoveryRunnable)
        if (registeredWakeReceiver) runCatching { unregisterReceiver(wakeReceiver) }
        logcatProcess?.destroy()
        logcatProcess = null
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val TAG = "FireOsHomeButton"
        private const val PREF_ENABLED = "fire_os_home_redirect_enabled"
        private const val CHANNEL_ID = "fire_os_home_override"
        private const val NOTIFICATION_ID = 7342
        private const val RESTART_DELAY_MS = 1_500L
        private const val BUFFER_TOLERANCE_MS = 250L
        private const val BOOT_HOME_EVENT_LOOKBACK_MS = 60_000L
        private const val WAKE_RECOVERY_DELAY_MS = 1_200L
        private const val WAKE_DUPLICATE_WINDOW_MS = 5_000L
        private const val EXTRA_INCLUDE_RECENT_BOOT_HOME_EVENT = "include_recent_boot_home_event"
        private const val LOGCAT_FILTER = "OZ-DCS|HomePressReceiver|HomeListener|android.intent.category.HOME|am_create_activity|com\\.amazon.*launcher|com\\.amazon.*tv\\.home"
        private val HOME_LISTENER_PRESS = Regex("(?:home.*(?:press|key)|(?:press|key).*home|HOME_PRESSED)", RegexOption.IGNORE_CASE)
        private val operational = kotlinx.coroutines.flow.MutableStateFlow(false)

        /** The targeted Fire OS 6 generation is Android 7.1/API 25 on Amazon AFT devices. */
        fun isSupported(context: Context): Boolean = Build.MANUFACTURER.equals("Amazon", ignoreCase = true) &&
            Build.MODEL.startsWith("AFT", ignoreCase = true) && Build.VERSION.SDK_INT == 25

        fun hasReadLogsPermission(context: Context): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            context.checkSelfPermission(android.Manifest.permission.READ_LOGS) == PackageManager.PERMISSION_GRANTED

        fun isEnabled(context: Context): Boolean {
            val prefs = preferences(context)
            if (!prefs.contains(PREF_ENABLED) && isUserUnlocked(context)) {
                val wasEnabledBeforeDirectBoot = LauncherSetup.preferences(context).getBoolean(PREF_ENABLED, false)
                if (wasEnabledBeforeDirectBoot) prefs.edit().putBoolean(PREF_ENABLED, true).apply()
            }
            return prefs.getBoolean(PREF_ENABLED, false) && hasReadLogsPermission(context)
        }

        internal val isOperational = operational

        fun setEnabled(context: Context, enabled: Boolean): Boolean {
            if (enabled && (!isSupported(context) || !hasReadLogsPermission(context))) return false
            preferences(context).edit().putBoolean(PREF_ENABLED, enabled).apply()
            if (isUserUnlocked(context)) {
                LauncherSetup.preferences(context).edit().putBoolean(PREF_ENABLED, enabled).apply()
            }
            if (enabled) {
                LauncherSetup.allowAccessibilityFallback(context)
                startIfConfigured(context)
            } else {
                runCatching { context.stopService(Intent(context, FireOsHomeButtonService::class.java)) }
                operational.value = false
            }
            return true
        }

        fun startIfConfigured(context: Context, includeRecentBootHomeEvent: Boolean = false) {
            if (operational.value || !isEnabled(context) || !isSupported(context)) return
            Log.i(TAG, "Starting configured Fire OS Home watcher")
            val serviceIntent = Intent(context, FireOsHomeButtonService::class.java).apply {
                putExtra(EXTRA_INCLUDE_RECENT_BOOT_HOME_EVENT, includeRecentBootHomeEvent)
            }
            runCatching { context.startService(serviceIntent) }
                .onFailure { Log.w(TAG, "Could not start the Fire OS Home watcher", it) }
        }

        internal fun stopForNativeHome(context: Context) {
            preferences(context).edit().putBoolean(PREF_ENABLED, false).apply()
            if (isUserUnlocked(context)) {
                LauncherSetup.preferences(context).edit().putBoolean(PREF_ENABLED, false).apply()
            }
            runCatching { context.stopService(Intent(context, FireOsHomeButtonService::class.java)) }
            operational.value = false
        }

        private fun preferences(context: Context) = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.createDeviceProtectedStorageContext()
                .getSharedPreferences("fire_os_home_override", Context.MODE_PRIVATE)
        } else {
            context.getSharedPreferences("launcher_capabilities", Context.MODE_PRIVATE)
        }

        private fun isUserUnlocked(context: Context): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.N ||
            runCatching { context.getSystemService(UserManager::class.java)?.isUserUnlocked == true }.getOrDefault(false)
    }
}
