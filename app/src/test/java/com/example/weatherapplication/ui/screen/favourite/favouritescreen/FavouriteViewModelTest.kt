package com.example.weatherapplication.ui.screen.favourite.favouritescreen

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapplication.R
import com.example.weatherapplication.datasource.repository.WeatherRepository
import com.example.weatherapplication.ui.screen.favourite.favouritescreen.viewmodel.FavouriteViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
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
    private lateinit var context: Context

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        favouriteViewModel = FavouriteViewModel(repository)
        context = mockk()
    }

    @Test
    fun deleteFromFavourite_callsRepositoryDeleteLocation_emitsToastMessage() = runTest {
        // Given
        val lat = 30.0
        val lng = 31.0

        every { context.getString(R.string.location_deleted_from_favourites) } returns "Location deleted from favourites"

        // When
        favouriteViewModel.deleteFromFavourite(lat, lng , context)
        advanceUntilIdle()

        // Then
        coVerify { repository.deleteLocation(lat, lng) }

        val toastMessage = favouriteViewModel.toastEvent.first()
        assertThat(toastMessage, `is`(context.getString(R.string.location_deleted_from_favourites)))
    }

    @Test
    fun deleteFromFavourite_emitsErrorMessage_onFailure() = runTest {
        // Given
        val lat = 30.0
        val lng = 31.0
        val errorMessage = "Failed to delete location"
        coEvery { repository.deleteLocation(lat, lng) } throws Exception(errorMessage)

        // When
        favouriteViewModel.deleteFromFavourite(lat, lng,context)
        advanceUntilIdle()
        // Then
        coVerify { repository.deleteLocation(lat, lng) }

        val toastMessage = favouriteViewModel.toastEvent.first()
        assertThat(toastMessage, `is`("An error occurred: $errorMessage"))
    }

}
