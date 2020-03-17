package com.whoisyari.freefallingdetector

/*
 *
 * Create by hoanle@xtaypro.com
 * Created at 15/3/20
 *
 */

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.whoisyari.freefallingdetectorlibrary.data.model.SensorData
import com.whoisyari.freefallingdetectorlibrary.data.repo.SensorDataRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch


/**
 * The listener to receive update when device has accelerated activities
 */

internal class FreeFallingSensorEventListener(
    private val context: Context,
    private val repository: SensorDataRepository,
    private val intervalTimeOfDetecting: Int,
    private val minimumFallingSpeed: Int
) : SensorEventListener2 {

    private val channel = BroadcastChannel<Boolean>(1)

    // Last time an accelerated activity happens
    var lastUpdate = 0L

    // Last recorded record of Axis X
    var lastX = 0f

    // Last recorded record of Axis Y
    var lastY = 0f

    // Last recorded record of Axis Z
    var lastZ = 0f

    // Last recorded record of calculated gravity value in Axis X
    var gravityX = 0f

    // Last recorded record of calculated gravity value in Axis Y
    var gravityY = 0f

    // Last recorded record of calculated gravity value in Axis Z
    var gravityZ = 0f

    // Last recorded record of calculated value of device in Axis X
    var linearAccelerationX = 0f

    // Last recorded record of calculated value of device in Axis Y
    var linearAccelerationY = 0f

    // Last recorded record of calculated value of device in Axis Z
    var linearAccelerationZ = 0f

    var records = arrayListOf<SensorData>()

    init {
        observer()
    }

    /**
     * Receive the event and handle the event of accelerometer.
     * After handling, if a free falling happened, broadcast the event
     * @param event: The sensor event received from the device system
     */
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_ACCELEROMETER) {
            val curTime = System.currentTimeMillis()
            var values = event.values;

            if (curTime - lastUpdate > intervalTimeOfDetecting) {
                val diffTime: Long = curTime - lastUpdate
                lastUpdate = curTime
                var x = values[0]
                var y = values[1]
                var z = values[2]
                val speed: Float =
                    Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000

                val alpha = 0.8f;

                var gravityX = alpha * gravityX + (1 - alpha) * event.values[0];
                var gravityY = alpha * gravityY + (1 - alpha) * event.values[1];
                var gravityZ = alpha * gravityZ + (1 - alpha) * event.values[2];

                linearAccelerationX = event.values[0] - gravityX
                linearAccelerationY = event.values[1] - gravityY
                linearAccelerationZ = event.values[2] - gravityZ;

                if (speed > minimumFallingSpeed) {
                    Log.d(FreeFallingSdk.TAG, "Z " + linearAccelerationZ)

                    records.add(
                        SensorData(
                            0,
                            x,
                            y,
                            z,
                            gravityX,
                            gravityY,
                            gravityZ,
                            linearAccelerationX,
                            linearAccelerationY,
                            linearAccelerationZ,
                            System.currentTimeMillis(),
                            0
                        )
                    )
                    GlobalScope.launch {
                        channel.send(true)
                    }
                }

                lastX = x
                lastY = y
                lastZ = z
            }
        }
    }

    private fun observer() {
        GlobalScope.launch {
            channel.asFlow().debounce(2000).collect {
                repository.insertNewFall(records)
                records.clear()
                sentToLocalBroadCast(context)
            }
        }
    }

    /**
     * Only use local broadcast for now
     */
    private fun sentToLocalBroadCast(context: Context) {
        GlobalScope.launch {
            Log.d(FreeFallingSdk.TAG, "sentToLocalBroadCast")
            delay(2000)  //debounce timeOut

            Log.d(FreeFallingSdk.TAG, "really sentToLocalBroadCast")
            LocalBroadcastManager
                .getInstance(context)
                .sendBroadcastSync(Intent(FreeFallingSdk.BROADCAST_POSSIBLE_FALLING))
        }

    }

    /**
     * Simply ignore unless some settings must be considered
     */
    override fun onFlushCompleted(p0: Sensor?) {

    }

    /**
     * Simply ignore unless some settings must be considered
     */
    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

}