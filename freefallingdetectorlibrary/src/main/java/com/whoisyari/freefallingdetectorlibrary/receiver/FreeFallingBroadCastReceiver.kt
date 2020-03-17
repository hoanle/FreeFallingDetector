package com.whoisyari.freefallingdetector

/*
 *
 * Create by hoanle@xtaypro.com
 * Created at 15/3/20
 *
 */

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.whoisyari.freefallingdetectorlibrary.R
import com.whoisyari.freefallingdetectorlibrary.data.FreeFallingDatabase
import com.whoisyari.freefallingdetectorlibrary.data.repo.SensorDataRepository
import kotlin.math.abs


/**
 * The BroadCast Receiver that receives the event of the falling.
 * It then invoke FreeFallingCallback functions
 */

internal class FreeFallingBroadCastReceiver : BroadcastReceiver() {

    /**
     * Receive the broadcast, let the callback handle next
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if (FreeFallingSdk.BROADCAST_POSSIBLE_FALLING == intent.action) {
                context?.apply {
                    checkValidFallingTrait(context)
                }
            } else if (FreeFallingSdk.BROADCAST_SERVICE_UNAVAILABLE == intent.action) {

            } else if (FreeFallingSdk.BROADCAST_IS_SERVICE_RUNNING == intent.action) {
                context?.apply {
                    LocalBroadcastManager
                        .getInstance(context)
                        .sendBroadcastSync(Intent(FreeFallingSdk.BROADCAST_SERVICE_RUNNING));
                }
            } else if (FreeFallingSdk.SHOULD_START_SERVICE_FOREGROUND == intent.action) {
                context?.apply {
                    FreeFallingServiceUtil.stopMonitoringService(context)
                    FreeFallingServiceUtil.startForegroundService(context)
                }
            } else {
                //Do nothing for now
            }
        }
    }

    /**
     * Get last falling records
     * Check if this is a valid fall
     * If not, remove them to lighten the database
     */
    private fun checkValidFallingTrait(context: Context) {
        val dao = SensorDataRepository(FreeFallingDatabase.getInstance(context))
        val list = dao.getLastFall()
        var totalZ = 0f
        var totalGravityZ = 0f
        var totalLinearAccelerationZ = 0f

        for (sensor in list) {
            Log.d(FreeFallingSdk.TAG, "record ${+sensor.fallId}")
            totalZ = totalZ + sensor.sensorZ
            totalGravityZ = totalGravityZ + sensor.gravityZ
            totalLinearAccelerationZ =
                totalLinearAccelerationZ + sensor.linearAccelerationZ
        }
        Log.d(FreeFallingSdk.TAG, "total $totalZ")
        Log.d(FreeFallingSdk.TAG, "total $totalGravityZ")
        Log.d(FreeFallingSdk.TAG, "total $totalLinearAccelerationZ")

        // This is an intuitive observation. For several tests, when absolute of totalGravityZ
        // is < 10, most likely a fall happens.
        // More refine actions need to be done though
        if (abs(totalGravityZ) < 10) {
            FreeFallingNotificationUlti.createLocalNotification(
                context,
                context.getString(R.string.notification_channel_id)
            )
            LocalBroadcastManager
                .getInstance(context)
                .sendBroadcastSync(Intent(FreeFallingSdk.BROADCAST_FALLING_DETECTED));
        } else {
            // Remove records if they are not a valid fall
            if (list.size > 0) {
                dao.removeInvalidFall(list.get(0).fallId)
            }
        }
    }
}