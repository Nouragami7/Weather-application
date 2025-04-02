package com.example.weatherapplication.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.weatherapplication.datasource.local.IWeatherLocalDataSource
import com.example.weatherapplication.datasource.local.LocationDAO
import com.example.weatherapplication.datasource.local.WeatherLocalDataSource
import com.example.weatherapplication.domain.model.LocationData
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk

import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class WeatherLocalDataSourceTest{

    private lateinit var localDataSource: IWeatherLocalDataSource
    private lateinit var locationDAO: LocationDAO

    @Before
    fun setUp() {
        locationDAO = mockk(relaxed = true)
        localDataSource = WeatherLocalDataSource(locationDAO)
    }

    @Test
    fun localDataSource_insertLocation_calls_locationDAO_insertLocation() = runTest {
        // Given
        val locationData = LocationData(30.0, 31.0, mockk(), mockk(), "Egypt", "Cairo")

        // When
        localDataSource.insertLocation(locationData)

        // Then
        coVerify { locationDAO.insertLocation(locationData) }

    }

    @Test
    fun localDataSource_deleteLocation_calls_locationDAO_deleteLocation_and_removes_location() = runTest {
        // Given
        val lat = 30.0
        val lng = 31.0
        val locationData = LocationData(lat, lng, mockk(), mockk(), "Egypt", "Cairo")

        locationDAO.insertLocation(locationData)
        val locationFlow = flowOf(listOf(locationData))
        every { locationDAO.getAllLocations() } returns locationFlow

        // When
        localDataSource.deleteLocation(lat, lng)

        val emptyLocationFlow = flowOf(emptyList<LocationData>())
        every { locationDAO.getAllLocations() } returns emptyLocationFlow

        //Then
        val locations = localDataSource.getAllLocations().firstOrNull()
        assertThat(locations?.isEmpty(), `is`(true))

    }

}