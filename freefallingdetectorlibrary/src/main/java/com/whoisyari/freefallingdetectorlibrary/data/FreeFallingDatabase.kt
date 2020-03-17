package com.whoisyari.freefallingdetectorlibrary.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.whoisyari.freefallingdetectorlibrary.data.dao.SensorDataDao
import com.whoisyari.freefallingdetectorlibrary.data.model.SensorData
import com.whoisyari.freefallingdetectorlibrary.extra.Constant

@Database(
    entities = arrayOf(SensorData::class),
    version = Constant.DATABASE_VERSION,
    exportSchema = false
)
abstract class FreeFallingDatabase : RoomDatabase() {
    abstract fun sensorDataDao(): SensorDataDao

    companion object {

        private var INSTANCE: FreeFallingDatabase? = null

        fun getInstance(context: Context): FreeFallingDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    FreeFallingDatabase::class.java,
                    "free_falling_detector.db"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

}