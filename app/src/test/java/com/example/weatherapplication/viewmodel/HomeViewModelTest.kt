package com.example.weatherapplication.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapplication.datasource.remote.ResponseState
import com.example.weatherapplication.datasource.repository.WeatherRepository
import com.example.weatherapplication.domain.model.HomeData
import com.example.weatherapplication.ui.viewmodel.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config


@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class HomeViewModelTest{
    lateinit var homeViewModel: HomeViewModel
    lateinit var repository: WeatherRepository

    @Before
    fun setUp(){
        repository = mockk(relaxed = true)
        homeViewModel = HomeViewModel(repository)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `insertHomeData should update homeData state`() = runTest {
        // Given
        val homeData: HomeData = mockk()

        // Mock repository response
        coEvery { repository.getHomeData() } returns flowOf(homeData)

        // When
        homeViewModel.insertHomeDate(homeData)
        homeViewModel.getHomeData() // Ensure we trigger data update

        advanceUntilIdle() // Wait for coroutines to complete

        // Then
        val result = homeViewModel.homeData.first() // Get the first emitted value
        assertThat(result, `is`(ResponseState.Success(homeData)))
    }

}