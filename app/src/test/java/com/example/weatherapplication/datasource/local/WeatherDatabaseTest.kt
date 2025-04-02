package com.example.weatherapplication.datasource.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weatherapplication.domain.model.AlertData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class WeatherDatabaseTest {

    private lateinit var database: WeatherDatabase
    private lateinit var locationDao: LocationDAO

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, WeatherDatabase::class.java)
            .build()
        locationDao = database.locationDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAlertAndRetrieve() = runBlocking {

        val alert = AlertData(startDate = "2025-04-01", startTime = "12:30 PM")
        locationDao.insertAlert(alert)

        val alerts = locationDao.getAllAlert().first()

        assertNotNull(alerts)
        assertThat(alerts.size, `is`(1))
        assertThat(alerts[0].startDate, `is`(alert.startDate))
        assertThat(alerts[0].startTime, `is`(alert.startTime))
    }

    @Test
    fun getAllAlert_ReturnsInsertedAlerts() = runBlocking {
        val alert = AlertData(startDate = "2025-04-01", startTime = "12:30 PM")
        locationDao.insertAlert(alert)

        val alerts = locationDao.getAllAlert().first()

        assertNotNull(alerts)
        assertThat(alerts.size, `is`(1))
        assertThat(alerts[0].startDate, `is`(alert.startDate))
        assertThat(alerts[0].startTime, `is`(alert.startTime))
    }

    @Test
    fun deleteAlert_RemovesAlert() = runBlocking {
        val alert = AlertData(startDate = "2025-04-01", startTime = "12:30 PM")

        locationDao.deleteAlert(alert)

        val alerts = locationDao.getAllAlert().first()
        assertNotNull(alerts)
        assertThat(alerts.size, `is`(0))
    }

}
