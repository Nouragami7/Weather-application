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
    private lateinit var weatherRepository: WeatherRepository

    @Before
    fun setUp() {
        remoteDataSource = mockk()
        localDataSource = mockk()
        weatherRepository = WeatherRepository.getInstance(remoteDataSource, localDataSource)
    }

    @Test
    fun `getCurrentWeather should call remoteDataSource and return current weather`() = runTest {
        val expectedWeather = mockk<CurrentWeather>()
        coEvery {
            remoteDataSource.getCurrentWeather(30.0, 31.0, "en", "metric", "api_key")
        } returns flowOf(expectedWeather)

        val result = weatherRepository.getCurrentWeather(30.0, 31.0, "en", "metric", "api_key").first()

        assertEquals(expectedWeather, result)
        coVerify { remoteDataSource.getCurrentWeather(30.0, 31.0, "en", "metric", "api_key") }

    }

    @Test
    fun `insertLocation should call localDataSource insertLocation`() = runTest {
        val location = LocationData(30.0, 31.0, mockk(), mockk(), "Egypt", "Cairo")

        weatherRepository.insertLocation(location)

        coVerify { localDataSource.insertLocation(location) }
    }
}
