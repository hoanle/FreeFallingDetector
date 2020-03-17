package com.whoisyari.freefallingdetectorlibrary.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.whoisyari.freefallingdetectorlibrary.data.model.SensorData

/**
 * Interface to define actions on SensorData
 */
@Dao
interface SensorDataDao {
    @Query("SELECT * FROM SensorData")
    fun getAllSensorData(): List<SensorData>

    @Query("SELECT * FROM SensorData ORDER BY fallId DESC LIMIT 1")
    fun getLastRecordWithFallId(): SensorData?

    @Query("SELECT * From SensorData where id = :id limit 1")
    fun getSensorById(id: Long): SensorData?

    @Query("SELECT * FROM SensorData where fallId = :fallId")
    fun getLastFallById(fallId: Long): List<SensorData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateSensorData(data: SensorData) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateSensorList(list: List<SensorData>): List<Long>

    @Query("DELETE FROM sensordata WHERE fallId = :fallId")
    fun deleteInvalidFallById(fallId: Long) : Int
}