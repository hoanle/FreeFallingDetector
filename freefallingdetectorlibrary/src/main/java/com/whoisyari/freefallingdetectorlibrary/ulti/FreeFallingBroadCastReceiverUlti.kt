package com.whoisyari.freefallingdetector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager

internal class FreeFallingBroadCastReceiverUlti {
    companion object {

        fun registerLocalBroadcastReceiverForForegroundService(context: Context, receiver: BroadcastReceiver) {
            val filter = IntentFilter(FreeFallingSdk.BROADCAST_SERVICE_RUNNING)
            filter.addAction(FreeFallingSdk.SHOULD_CHECK_IF_MONITORING_SERVICE_RUNNING)
            LocalBroadcastManager.getInstance(context).registerReceiver(
                receiver, filter
            );
        }
        /**
         * Register action to link with external client
         */
        fun registerBroadCastReceiverForExternal(context: Context, receiver: BroadcastReceiver) {
            val filter = IntentFilter(FreeFallingSdk.BROADCAST_SERVICE_RUNNING)
            filter.addAction(FreeFallingSdk.BROADCAST_SERVICE_UNAVAILABLE)
            filter.addAction(FreeFallingSdk.BROADCAST_POSSIBLE_FALLING)
            filter.addAction(FreeFallingSdk.SHOULD_START_SERVICE_BACKGROUND)
            filter.addAction(FreeFallingSdk.BROADCAST_FALLING_DETECTED)
            LocalBroadcastManager.getInstance(context).registerReceiver(
                receiver, filter
            )
        }

        /**
         * Register action to local receiver
         */
        fun registerBroadCastReceiverLocal(context: Context, receiver: BroadcastReceiver) {
            val filter = IntentFilter(FreeFallingSdk.BROADCAST_IS_SERVICE_RUNNING)
            filter.addAction(FreeFallingSdk.BROADCAST_POSSIBLE_FALLING)
            filter.addAction(FreeFallingSdk.BROADCAST_SERVICE_UNAVAILABLE)
            filter.addAction(FreeFallingSdk.SHOULD_START_SERVICE_FOREGROUND)
            LocalBroadcastManager
                .getInstance(context)
                .registerReceiver(
                    receiver,
                    filter
                )
        }
    }
}