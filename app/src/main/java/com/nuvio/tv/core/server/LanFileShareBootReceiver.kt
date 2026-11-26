package com.nuvio.tv.core.server

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class LanFileShareBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> LanFileShareService.startIfEnabled(context)
        }
    }
}
