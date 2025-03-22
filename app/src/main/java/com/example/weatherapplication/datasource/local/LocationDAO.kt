package com.example.weatherapplication.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapplication.domain.model.LocationData
import kotlinx.coroutines.flow.Flow


@Dao
interface LocationDAO{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationData)

    @Query("SELECT * FROM Weather")
     fun getAllLocations(): Flow<List<LocationData>>

    @Query("DELETE FROM Weather WHERE latitude = :lat AND longitude = :lng")
    suspend fun deleteLocation(lat: Double, lng: Double)

}