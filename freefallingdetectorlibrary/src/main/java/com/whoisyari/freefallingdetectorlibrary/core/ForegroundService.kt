package com.whoisyari.freefallingdetectorlibrary.core

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.whoisyari.freefallingdetector.FreeFallingBroadCastReceiverUlti
import com.whoisyari.freefallingdetector.FreeFallingNotificationUlti
import com.whoisyari.freefallingdetector.FreeFallingSdk
import com.whoisyari.freefallingdetector.FreeFallingServiceUtil
import com.whoisyari.freefallingdetectorlibrary.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Instead of running a foreground service that does lots of things
 * run a foreground service just to control the background service
 */
class ForegroundService : Service() {

    private var isMonitorServiceRunning = false

    /**
     * The receiver for check service action, also for checking MonitoringService is running or not
     */
    private var receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (FreeFallingSdk.BROADCAST_SERVICE_RUNNING == intent?.action) {
                isMonitorServiceRunning = true
            } else if (FreeFallingSdk.SHOULD_CHECK_IF_MONITORING_SERVICE_RUNNING == intent?.action) {
                Log.d(FreeFallingSdk.TAG, "SHOULD_CHECK_IF_MONITORING_SERVICE_RUNNING")
                context?.let {
                    checkMonitoringService(it)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        FreeFallingBroadCastReceiverUlti.registerLocalBroadcastReceiverForForegroundService(
            baseContext,
            receiver
        )
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        // A foreground notification
        startForeground(
            1,
            FreeFallingNotificationUlti.createForegroundNotification(
                baseContext,
                baseContext.getString(R.string.notification_channel_id)
            )
        )

        LocalBroadcastManager
            .getInstance(baseContext)
            .sendBroadcastSync(Intent(FreeFallingSdk.SHOULD_CHECK_IF_MONITORING_SERVICE_RUNNING))

        return START_STICKY
    }

    /**
     * Check if service is running or not, if not, restart it
     * After that, run a timer to recheck after 50 seconds
     * 50s: Because system normally destroy service after 60s
     */
    private fun checkMonitoringService(context: Context) {
        if (!isMonitoringServiceRunning(context)) {
            Log.d(FreeFallingSdk.TAG, "checkMonitoringService service is not running, restart it")
            FreeFallingServiceUtil.startMonitoringService(context, false)
        } else {
            Log.d(FreeFallingSdk.TAG, "checkMonitoringService service is running")
        }

        // Delay 50s for next checking
        GlobalScope.launch {
            delay(50000)
            LocalBroadcastManager
                .getInstance(baseContext)
                .sendBroadcastSync(Intent(FreeFallingSdk.SHOULD_CHECK_IF_MONITORING_SERVICE_RUNNING))
        }
    }

    /**
     * Send signal to check if Monitoring Service is running or not
     */
    private fun isMonitoringServiceRunning(context: Context): Boolean {
        LocalBroadcastManager.getInstance(context).sendBroadcastSync(
            Intent(FreeFallingSdk.BROADCAST_IS_SERVICE_RUNNING)
        )
        return isMonitorServiceRunning
    }
}