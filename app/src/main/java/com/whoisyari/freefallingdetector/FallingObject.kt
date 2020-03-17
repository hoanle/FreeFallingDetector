package com.whoisyari.freefallingdetector

import com.whoisyari.freefallingdetectorlibrary.data.model.SensorData
import kotlin.math.absoluteValue

class FallingObject(val list: List<SensorData>) {

    var totalZ = 0.0
    var gravityZ = 0.0
    var accelerationZ = 0.0

    init {
        totalZ = list.sumByDouble {
            it.gravityZ.toDouble()
        }
        gravityZ = list.sumByDouble {
            it.gravityZ.toDouble()
        }
        accelerationZ = list.sumByDouble {
            it.linearAccelerationZ.toDouble()
        }
    }
}