package com.example.weatherapplication.datasource.repository

import com.example.weatherapplication.datasource.local.IWeatherLocalDataSource
import com.example.weatherapplication.datasource.remote.IWeatherRemoteDataSource
import com.example.weatherapplication.domain.model.CurrentWeather
import com.example.weatherapplication.domain.model.LocationData
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class WeatherRepositoryTest {

    private lateinit var remoteDataSource: IWeatherRemoteDataSource
    private lateinit var localDataSource: IWeatherLocalDataSource
    private lateinit var weatherRepository: IRepository

    @Before
    fun setUp() {
        remoteDataSource = mockk()
        localDataSource = mockk()
        weatherRepository = WeatherRepository.getInstance(remoteDataSource, localDataSource)
    }

    @Test
    fun getCurrentWeather_callsRemoteDataSource_returnsCurrentWeather() = runTest {
        //Given
        val expectedWeather = mockk<CurrentWeather>()
        coEvery {
            remoteDataSource.getCurrentWeather(30.0, 31.0, "en", "metric", "api_key")
        } returns flowOf(expectedWeather)

        //When
        val result = weatherRepository.getCurrentWeather(30.0, 31.0, "en", "metric", "api_key").first()

        //Then
        assertEquals(expectedWeather, result)
        coVerify { remoteDataSource.getCurrentWeather(30.0, 31.0, "en", "metric", "api_key") }

    }

    @Test
    fun insertLocation_callsLocalDataSourceInsertLocation() = runTest {
        //Given
        val location = LocationData(30.0, 31.0, mockk(relaxed = true), mockk(relaxed = true), "Egypt", "Cairo")
        coEvery { localDataSource.insertLocation(location) } returns Unit

        //When
        weatherRepository.insertLocation(location)

        //Then
        coVerify { localDataSource.insertLocation(location) }

    }
}
