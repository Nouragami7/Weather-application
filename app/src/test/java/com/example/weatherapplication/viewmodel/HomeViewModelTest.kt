package com.example.weatherapplication.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapplication.datasource.remote.ResponseState
import com.example.weatherapplication.datasource.repository.WeatherRepository
import com.example.weatherapplication.domain.model.HomeData
import com.example.weatherapplication.ui.screen.homescreen.viewmodel.HomeViewModel
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
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var repository: WeatherRepository

    @Before
    fun setUp(){
        repository = mockk(relaxed = true)
        homeViewModel = HomeViewModel(repository)

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertHomeData_updatesHomeDataState() = runTest {
        // Given
        val homeData: HomeData = mockk()

        coEvery { repository.getHomeData() } returns flowOf(homeData)

        // When
        homeViewModel.insertHomeDate(homeData)
        homeViewModel.getHomeData()

        advanceUntilIdle()

        // Then
        val result = homeViewModel.homeData.first()
        assertThat(result, `is`(ResponseState.Success(homeData)))
    }

}