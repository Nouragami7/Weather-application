package com.example.weatherapplication.datasource.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapplication.domain.model.LocationData

@Database(entities = [LocationData::class], version = 1)
abstract class WeatherDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDAO

    companion object {
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        fun getDatabase(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "product_database"
                ).
                fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }


}