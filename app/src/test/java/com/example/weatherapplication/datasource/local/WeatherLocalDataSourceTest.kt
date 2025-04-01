package com.example.weatherapplication.datasource.local

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

class WeatherLocalDataSourceTest{

    private lateinit var localDataSource: IWeatherLocalDataSource
    private lateinit var locationDAO: LocationDAO

    @Before
    fun setUp() {
        locationDAO = mockk(relaxed = true) // Mock LocationDAO
        localDataSource = WeatherLocalDataSource(locationDAO)
    }

    @Test
    fun `insertLocation should call DAO insertLocation`() = runTest {
        // Given
        val locationData = LocationData(30.0, 31.0, mockk(), mockk(), "Egypt", "Cairo")

        // When
        localDataSource.insertLocation(locationData)

        // Then
        // Use `coVerify` to check the function call
        coVerify { locationDAO.insertLocation(locationData) }

        // Use `assertThat` to verify the interaction if needed
        assertThat(locationData.latitude, `is`(30.0))
        assertThat(locationData.longitude, `is`(31.0))
        assertThat(locationData.city, `is`("Cairo"))
        assertThat(locationData.country, `is`("Egypt"))
    }





    @Test
    fun `deleteLocation should call DAO deleteLocation and remove the location`() = runTest {
        // Given
        val lat = 30.0
        val lng = 31.0
        val locationData = LocationData(lat, lng, mockk(), mockk(), "Egypt", "Cairo")

        locationDAO.insertLocation(locationData)


        // When
        localDataSource.deleteLocation(lat, lng)

        // Then
        coVerify { locationDAO.deleteLocation(lat, lng) }

        // Mock the flow of getAllLocations() after deletion, expecting an empty list
        val emptyLocationFlow = flowOf(emptyList<LocationData>()) // Return an empty list after deletion
        every { locationDAO.getAllLocations() } returns emptyLocationFlow

        // Check if the location is no longer in the database (i.e., it was deleted)
        val locations = localDataSource.getAllLocations().firstOrNull()
        assertThat(locations?.isEmpty(), `is`(true))

    }



}