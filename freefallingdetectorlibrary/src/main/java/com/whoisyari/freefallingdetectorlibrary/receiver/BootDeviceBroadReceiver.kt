package com.whoisyari.freefallingdetector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.whoisyari.freefallingdetectorlibrary.R

/**
 * When device reboot, show notification to user to ask them reactivate the app
 */
internal class BootDeviceBroadReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(FreeFallingSdk.TAG, "BootDeviceBroadReceiver")
        if (Intent.ACTION_BOOT_COMPLETED == intent?.action) {
            context?.let {
                val component = FreeFallingPreferenceUtil.getApplicationComponentName(context)
                if (component.isNotEmpty()) {
                    FreeFallingNotificationUlti.createNotificationToRestartService(context, context.getString(R.string.notification_channel_id))
                }
            }
        }
    }
}