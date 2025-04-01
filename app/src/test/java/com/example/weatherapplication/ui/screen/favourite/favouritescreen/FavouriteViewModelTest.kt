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
    fun `deleteFromFavourite should call repository deleteLocation and emit toast message`() = runTest {
        // Given
        val lat = 30.0
        val lng = 31.0
        coEvery { repository.deleteLocation(lat, lng) } returns Unit // Mock successful delete

        // When
        favouriteViewModel.deleteFromFavourite(lat, lng)
        advanceUntilIdle() // Ensure coroutine execution completion

        // Then
        coVerify { repository.deleteLocation(lat, lng) } // Verify deleteLocation is called

        val toastMessage = favouriteViewModel.toastEvent.first()
        assertThat(toastMessage, `is`("Location deleted from favourites")) // Check toast message
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


    @Test
    fun `deleteFromFavourite should emit error message on failure`() = runTest {
        // Given
        val lat = 30.0
        val lng = 31.0
        val errorMessage = "Failed to delete location"
        coEvery { repository.deleteLocation(lat, lng) } throws Exception(errorMessage) // Simulate failure

        // When
        favouriteViewModel.deleteFromFavourite(lat, lng)
        advanceUntilIdle() // Ensure coroutine execution completion

        // Then
        coVerify { repository.deleteLocation(lat, lng) } // Verify deleteLocation was attempted

        val toastMessage = favouriteViewModel.toastEvent.first()
        assertThat(toastMessage, `is`("An error occurred: $errorMessage")) // Check error message
    }

}
