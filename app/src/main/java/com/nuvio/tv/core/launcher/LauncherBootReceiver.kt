package com.nuvio.tv.core.launcher

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.util.Log
import com.nuvio.tv.MainActivity

/** Re-enables the Home accessibility service and brings Nuvio up after Fire TV finishes booting. */
class LauncherBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> {
                AccessibilityWatchdog.ensureEnabled(context)
                LauncherBootLaunch.onBootCompleted(context)
            }

            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                AccessibilityWatchdog.ensureEnabled(context)
                LauncherBootLaunch.openIfAllowed(context)
            }
        }
    }
}

/** Internal one-shot alarm receiver so Fire TV's own HOME launch cannot cover Nuvio afterward. */
class LauncherDelayedBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != LauncherBootLaunch.action(context)) return
        LauncherBootLaunch.openIfAllowed(context)
    }
}

private object LauncherBootLaunch {
    private const val TAG = "LauncherBootLaunch"
    private const val REQUEST_CODE = 2417
    private const val STARTUP_DELAY_MS = 30_000L
    private const val ACTION_SUFFIX = ".action.LAUNCH_AFTER_BOOT"

    fun action(context: Context): String = context.packageName + ACTION_SUFFIX

    fun onBootCompleted(context: Context) {
        if (LauncherSetup.isDefaultHome(context)) {
            openIfAllowed(context)
            return
        }
        if (!LauncherSetup.accessibilityFallbackAllowed(context)) return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            Intent(context, LauncherDelayedBootReceiver::class.java).setAction(action(context)),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val launchAt = SystemClock.elapsedRealtime() + STARTUP_DELAY_MS

        runCatching {
            val exactAllowed = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
            if (exactAllowed) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    launchAt,
                    pendingIntent
                )
            } else {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    launchAt,
                    pendingIntent
                )
            }
        }.onFailure { Log.w(TAG, "Could not schedule the delayed boot launch", it) }
    }

    fun openIfAllowed(context: Context) {
        if (!LauncherSetup.accessibilityFallbackAllowed(context) && !LauncherSetup.isDefaultHome(context)) return

        val launchIntent = Intent(context, MainActivity::class.java).apply {
            putExtra(LauncherHomeRequest.EXTRA_GO_HOME, true)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        runCatching { context.startActivity(launchIntent) }
            .onFailure { Log.w(TAG, "Could not open Nuvio after boot", it) }
    }
}
