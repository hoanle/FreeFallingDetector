package com.whoisyari.freefallingdetectorlibrary.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * SensorData
 * @param id: local id, primary key
 * @param sensorX: sensor axis X
 * @param sensorY: sensor axis Y
 * @param sensorZ: sensor axis Z
 * @param gravityX: gravity data axiz X, calculated by: alpha * gravityX + (1 - alpha) * event.values[0];
 * @param gravityY: gravity data axiz Y, calculated by: alpha * gravityY + (1 - alpha) * event.values[1]
 * @param gravityZ: gravity data axiz Z, calculated by: alpha * gravityZ + (1 - alpha) * event.values[2]
 * @param linearAccelerationX: acceleration data axiz X, calculated by: event.values[0] - gravityX
 * @param linearAccelerationX: acceleration data axiz Y, calculated by: event.values[1] - gravityY
 * @param linearAccelerationX: acceleration data axiz Z, calculated by: event.values[2] - gravityZ
 * @param timeStamp: Time when event happens
 * @param fallId: Id of the falling event
 */
@Entity
class SensorData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Long = 0,
    @ColumnInfo(name = "sensorX") var sensorX: Float = 0f,
    @ColumnInfo(name = "sensorY") var sensorY: Float = 0f,
    @ColumnInfo(name = "sensorZ") var sensorZ: Float = 0f,
    @ColumnInfo(name = "gravityX") var gravityX: Float = 0f,
    @ColumnInfo(name = "gravityY") var gravityY: Float = 0f,
    @ColumnInfo(name = "gravityZ") var gravityZ: Float = 0f,
    @ColumnInfo(name = "linearAccelerationX") var linearAccelerationX: Float = 0f,
    @ColumnInfo(name = "linearAccelerationY") var linearAccelerationY: Float = 0f,
    @ColumnInfo(name = "linearAccelerationZ") var linearAccelerationZ: Float = 0f,
    @ColumnInfo(name = "timestamp") var timeStamp: Long = 0,
    @ColumnInfo(name = "fallId") var fallId: Long = 0
)