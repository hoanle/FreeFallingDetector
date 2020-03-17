package com.whoisyari.freefallingdetectorlibrary.data.repo

import android.util.Log
import com.whoisyari.freefallingdetector.FreeFallingSdk
import com.whoisyari.freefallingdetectorlibrary.data.FreeFallingDatabase
import com.whoisyari.freefallingdetectorlibrary.data.model.SensorData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * Concrete class to handle Sensor Data
 * Use coroutines to run async
 */
class SensorDataRepository(val database: FreeFallingDatabase) : CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    fun getAllSensorData(): List<SensorData> {
        return database.sensorDataDao().getAllSensorData()
    }

    fun insertOrUpdateSensorData(data: SensorData): Long {
        return runBlocking {
            database.sensorDataDao().insertOrUpdateSensorData(data)
        }
    }

    fun getSensorById(id: Long): SensorData? {
        return database.sensorDataDao().getSensorById(id)
    }

    fun insertOrUpdateSensorData(list: MutableList<SensorData>): List<Long> {
        return runBlocking {
            val lastRecord = getLastRecordWithFallId()
            val newList = list.map {
                Log.d(FreeFallingSdk.TAG, "time ${list.last().timeStamp - (lastRecord?.timeStamp ?: 0)}")
                if (lastRecord == null) {
                    it.fallId = 1
                } else if (list.last().timeStamp - lastRecord.timeStamp > 10000) {
                    it.fallId = lastRecord.fallId + 1
                } else {
                    it.fallId = lastRecord.fallId
                }
                it
            }
            database.sensorDataDao().insertOrUpdateSensorList(newList)
        }
    }

    fun insertNewFall(list: MutableList<SensorData>)  {
        val lastRecord = getLastRecordWithFallId()
        val newList = list.map {
            it.fallId = (lastRecord?.fallId  ?: 0) + 1
            it
        }
        launch {
            database.sensorDataDao().insertOrUpdateSensorList(newList)
        }
    }

    fun getLastRecordWithFallId(): SensorData? {
        return database.sensorDataDao().getLastRecordWithFallId()
    }

    fun getLastFall(): List<SensorData> {
        val lastRecord = getLastRecordWithFallId()
        Log.d(FreeFallingSdk.TAG, "lastID ${lastRecord?.fallId}")
        return  database.sensorDataDao().getFallById(lastRecord?.fallId ?: 0)
    }

    fun removeInvalidFall(fallId: Long): Int {
        return database.sensorDataDao().deleteInvalidFallById(fallId)
    }

    fun getFallIds(): List<Long> {
        return database.sensorDataDao().getFallIds()
    }

    fun getFallTrailById(fallId: Long): List<SensorData> {
        return database.sensorDataDao().getFallById(fallId)
    }
}