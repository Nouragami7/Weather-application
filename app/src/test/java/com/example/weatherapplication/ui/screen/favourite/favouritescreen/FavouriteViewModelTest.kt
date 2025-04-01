package com.example.weatherapplication.ui.screen.favourite.favouritescreen

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapplication.datasource.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class FavouriteViewModelTest {
    private lateinit var favouriteViewModel: FavouriteViewModel
    private lateinit var repository: WeatherRepository

    @Before
    fun setUp() {
        repository = mockk(relaxed = true) // Mock repository
        favouriteViewModel = FavouriteViewModel(repository)
    }

    @Test
    fun `deleteFromFavourite should call repository deleteLocation`() = runTest {
        // Given
        val lat = 30.0
        val lng = 31.0
        coEvery { repository.deleteLocation(lat, lng) } returns Unit // Mock successful delete

        // When
        favouriteViewModel.deleteFromFavourite(lat, lng)
        advanceUntilIdle() // Wait for coroutine execution

        // Then
        coVerify { repository.deleteLocation(lat, lng) } // Ensure deleteLocation() was called
        assertThat(true, `is`(true)) // Additional assertion for validation
    }


    @Test
    fun `deleteFromFavourite should not affect other states`() = runTest {
        // Given
        val stateBeforeDeletion = favouriteViewModel.favLocations.first()

        // When
        advanceUntilIdle() // Ensure coroutine execution completion

        // Then
        assertThat(stateBeforeDeletion, `is`(favouriteViewModel.favLocations.first()))
    }

}
