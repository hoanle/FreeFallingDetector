package com.whoisyari.freefallingdetectorlibrary.core

/*
 *
 * Create by hoanle@xtaypro.com
 * Created at 15/3/20
 *
 */

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.whoisyari.freefallingdetector.*
import com.whoisyari.freefallingdetectorlibrary.R
import com.whoisyari.freefallingdetectorlibrary.data.FreeFallingDatabase
import com.whoisyari.freefallingdetectorlibrary.data.repo.SensorDataRepository

/**
 * Service that should run and detect the falling of the device.
 * - It should run even when the app is not in active mode or even shutdown
 * - It should automatically re-created after being destroy by user, unless user decide to force close
 *      from the settings of the device
 * - It should automatically start if user restart the app
 */

internal class MonitoringService : Service() {

    // Instance of device sensor manager
    private var sensorManager: SensorManager? = null

    // Instance of the accelerometer sensor
    private var sensor: Sensor? = null

    // Instance of FreeFallingListener, for unregister later
    private var fallingSensorEventListener: FreeFallingSensorEventListener? = null

    // Service should have its receiver to make sure all action receive even when app is killed
    private val receiver: FreeFallingBroadCastReceiver by lazy {
        FreeFallingBroadCastReceiver();
    }

    // Servive is foreground or not
    private var isForegorund = false

    init {
        instance = this
    }

    companion object {
        const val IS_FOREGROUND = "is_foreground"

        lateinit var instance: MonitoringService

        fun terminateService() {
            instance.stopSelf()
        }
    }

    /**
     * Register private broadcast for detecting whether service is running
     */
    override fun onCreate() {
        super.onCreate()
        Log.d(FreeFallingSdk.TAG, "onCreate ${android.os.Process.myPid()}")
        FreeFallingBroadCastReceiverUlti.registerBroadCastReceiverLocal(this, receiver)
    }

    /**
     * In order to run long term, the service should be run as STICKY service
     * This will consume the batter unless further optimization is done
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(FreeFallingSdk.TAG, "onStartCommand")
        startFallingService(baseContext);

        isForegorund = intent?.extras?.getBoolean(IS_FOREGROUND) ?: false

        // Based on the extra when start the service, service could be run foreground or background
        if (isForegorund) {
            startForeground(
                1,
                FreeFallingNotificationUlti.createForegroundNotification(
                    baseContext,
                    baseContext.getString(R.string.notification_channel_id)
                )
            )
        }

        return START_STICKY;
    }

    /**
     * Receive a context, use it to get possible sensor manager instance
     * Check if the device has accelerometer, throw exception it no sensor found
     * Register the listener to start receiving updates
     */
    private fun startFallingService(context: Context) {
        val intervalTimeOfDetecting = FreeFallingPreferenceUtil.getIntervalTime(context)
        val minimumFallingSpeed = FreeFallingPreferenceUtil.getMinimumSpeed(context)

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        fallingSensorEventListener =
            FreeFallingSensorEventListener(
                context,
                SensorDataRepository(FreeFallingDatabase.getInstance(context)),
                intervalTimeOfDetecting,
                minimumFallingSpeed
            )
        if (sensorManager != null) {
            val sensors =
                sensorManager!!.getSensorList(Sensor.TYPE_ACCELEROMETER)
            if (sensors.size > 0) {
                sensor = sensors[0]
                Log.d(FreeFallingSdk.TAG, "registerListener sensors")
                sensorManager!!.registerListener(
                    fallingSensorEventListener,
                    sensor,
                    SensorManager.SENSOR_DELAY_GAME
                )
            } else {
                notifyServiceUnavailable(context)
            }
        } else {
            notifyServiceUnavailable(context)
        }
    }

    /**
     * Let client know that device does not support this sensor
     */
    private fun notifyServiceUnavailable(context: Context) {
        LocalBroadcastManager
            .getInstance(context)
            .sendBroadcastSync(Intent(FreeFallingSdk.BROADCAST_SERVICE_UNAVAILABLE));
    }

    /**
     * Unregister the listener to avoid memory leak
     */
    override fun onDestroy() {
        Log.d(FreeFallingSdk.TAG, "onDestroy ${android.os.Process.myPid()}")
        sensorManager?.unregisterListener(fallingSensorEventListener)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
        super.onDestroy()
    }

    /**
     * When app is closed / hide / swiped, onTrimMemory will invoke.
     * Run service as foreground to keep it alive
     */
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        Log.d(FreeFallingSdk.TAG, "onTrimMemory")
        LocalBroadcastManager
            .getInstance(baseContext)
            .sendBroadcastSync(Intent(FreeFallingSdk.SHOULD_START_SERVICE_FOREGROUND))
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }
}