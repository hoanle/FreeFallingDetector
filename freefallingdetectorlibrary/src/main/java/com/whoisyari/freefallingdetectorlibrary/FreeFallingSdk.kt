package com.whoisyari.freefallingdetector

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.whoisyari.freefallingdetectorlibrary.core.MonitoringService
import com.whoisyari.freefallingdetectorlibrary.data.FreeFallingDatabase
import com.whoisyari.freefallingdetectorlibrary.data.repo.SensorDataRepository

/**
 * The entrance to the lib.
 * Client should access only this class. All the necessary data and actions should be from this class
 */
class FreeFallingSdk {

    companion object {
        const val TAG = "FreeFallingSdk"
        const val BROADCAST_IS_SERVICE_RUNNING = "falling.detector.is_running"
        const val BROADCAST_SERVICE_RUNNING = "falling.detector.running"
        const val BROADCAST_SERVICE_UNAVAILABLE = "falling.detector.unavailable"
        const val BROADCAST_POSSIBLE_FALLING = "falling.detector.possible_falling"
        const val BROADCAST_FALLING_DETECTED = "falling.detector.falling_detected"
        const val SHOULD_START_SERVICE_FOREGROUND = "falling.detector.start.service.foreground"
        const val SHOULD_START_SERVICE_BACKGROUND = "falling.detector.start.service.background"
        const val SHOULD_CHECK_IF_MONITORING_SERVICE_RUNNING = "falling.detector.should.check.monitoring.service.running"

        private var INSTANCE: FreeFallingSdk? = null
        private var created = false;

        fun getInstance(): FreeFallingSdk {

            if (INSTANCE == null) {
                INSTANCE = FreeFallingSdk()
            }

            return INSTANCE!!
        }
    }

    private constructor()

    // Callback from client
    private var freeFallingCallback: FreeFallingCallback? = null

    // To check whether service is running or not
    private var isRunning = false

    /**
     * This receiver to receive local broadcase and then use callback from client to proceed.
     * Client can implements method of this callbacks to perform accordingly
     */
    private var echoToExternal = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (BROADCAST_SERVICE_RUNNING == intent?.action) {
                isRunning = true
            } else if (BROADCAST_POSSIBLE_FALLING == intent?.action) {
                freeFallingCallback?.onPossibleFalling()
            } else if (BROADCAST_SERVICE_UNAVAILABLE == intent?.action) {
                freeFallingCallback?.onSensorUnavailable()
            } else if (SHOULD_START_SERVICE_BACKGROUND == intent?.action) {
                context?.let {
                    FreeFallingServiceUtil.stopForegroundService(context)
                    FreeFallingServiceUtil.stopMonitoringService(context)
                    FreeFallingServiceUtil.startMonitoringService(it, false)
                }
            } else if (BROADCAST_FALLING_DETECTED == intent?.action) {
                freeFallingCallback?.onFallingDetected()
            }
        }
    }

    /**
     * Start the service to collect sensor data
     * @param: context: From client, either application or activity context
     * @param options: FreeFalling option
     * @param callback: provided by client, to receive update from FreeFalling
     */
    fun startService(
        context: Context,
        options: FreeFallingOptions,
        callback: FreeFallingCallback?
    ) {

        this.freeFallingCallback = callback

        if (!isServiceRunning(context)) {
            initializeSDK(context, options)
            isRunning = false
        } else {
            Log.d(TAG, "service is running")
        }
        LocalBroadcastManager.getInstance(context).sendBroadcastSync(
            Intent(SHOULD_START_SERVICE_BACKGROUND)
        )
        created = true
    }

    /**
     * Setup all settings
     * - Save settings to Preference for others to access
     * - Register Broadcast to pass callback to client
     * - Register notification channel
     * - Since app is foreground, service should be started as background
     *
     * @param context:
     * @param options: Options to run the sdk, some settings have default values already
     */
    private fun initializeSDK(context: Context, options: FreeFallingOptions) {
        saveOptionsToPreference(context, options)
        createDatabase(context)
        FreeFallingBroadCastReceiverUlti.registerBroadCastReceiverForExternal(
            context,
            echoToExternal
        )
        FreeFallingNotificationUlti.registerNotificationChannel(context)
    }

    /**
     *  Stop all actions, service
     *  @param context:
     */
    fun destroy(context: Context) {
        assertSdkCreated()
        LocalBroadcastManager.getInstance(context).unregisterReceiver(echoToExternal)
        MonitoringService.terminateService()
        INSTANCE = null
    }

    /**
     * Access repository for records
     * @param context:
     */
    fun getFreeFallRepository(context: Context): SensorDataRepository {
        assertSdkCreated()
        return SensorDataRepository(FreeFallingDatabase.getInstance(context))
    }

    /**
     * All settings should be save to preferene for service to access
     * @param context:
     * @param options:
     */
    private fun saveOptionsToPreference(context: Context, options: FreeFallingOptions) {
        FreeFallingPreferenceUtil.setAllowedForeground(context, options.allowForeground)
        FreeFallingPreferenceUtil.setIntervalTime(context, options.interval)
        FreeFallingPreferenceUtil.setMinimumSpeed(context, options.minimumFallingSpeed)
        FreeFallingPreferenceUtil.setApplicationComponentName(context, context.packageName)
    }

    /**
     * Create FreeFalling database. Room make sure this happens only when no db found
     * @param context:
     */
    private fun createDatabase(context: Context) {
        FreeFallingDatabase.getInstance(context);
    }

    /**
     * Check if service is running or not.
     * This uses a trick to ping a receiver inside service. If service started, it would receive the signal and reply
     * isRunning then updated to true.
     */
    private fun isServiceRunning(context: Context): Boolean {
        LocalBroadcastManager.getInstance(context).sendBroadcastSync(
            Intent(BROADCAST_IS_SERVICE_RUNNING)
        )
        return isRunning
    }

    /**
     * Make sure all settings are correctly setup
     */
    private fun assertSdkCreated() {
        if (!created) throw AssertionError("startService must be called first");
    }
}