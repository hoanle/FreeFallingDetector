package com.whoisyari.freefallingdetector

import android.content.Context
import android.content.Intent
import com.whoisyari.freefallingdetectorlibrary.core.ForegroundService
import com.whoisyari.freefallingdetectorlibrary.core.MonitoringService

internal class FreeFallingServiceUtil {

    companion object {

        /**
         * Stop the running service
         */
        fun stopMonitoringService(context: Context) {
            val i = Intent(context, MonitoringService::class.java)
            context.stopService(i)
        }

        /**
         * Stop the foreground service
         */
        fun stopForegroundService(context: Context) {
            val i = Intent(context, ForegroundService::class.java)
            context.stopService(i)
        }

        /**
         * Start the service with option to run background or foreground
         */
        fun startMonitoringService(context: Context, foreGround: Boolean) {
            val i = Intent(context, MonitoringService::class.java)
            i.putExtra(MonitoringService.IS_FOREGROUND, foreGround)
            context.startService(i)
        }

        /**
         * Start the service foreground, it will control the bg service and make sure it alive
         */
        fun startForegroundService(context: Context) {
            val i = Intent(context, ForegroundService::class.java)
            context.startService(i)
        }
    }
}