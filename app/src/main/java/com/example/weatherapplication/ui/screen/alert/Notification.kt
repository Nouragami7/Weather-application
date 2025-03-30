
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.weatherapplication.R
import com.example.weatherapplication.domain.model.CurrentWeather
import com.example.weatherapplication.ui.screen.alert.StopSoundReceiver

private var mediaPlayer: MediaPlayer? = null

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "message_channel"
        val name = "Messages"
        val descriptionText = "Notification for new messages"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val soundUri = Uri.parse("android.resource://${context.packageName}/raw/loud_notification")
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
            setSound(soundUri, audioAttributes)
            enableVibration(true)
            vibrationPattern = longArrayOf(500, 1000, 500, 1000)
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

fun showNotification(context: Context, weather: CurrentWeather?) {
    val channelId = "message_channel"
    val soundUri = Uri.parse("android.resource://${context.packageName}/raw/loud_notification")

    playNotificationSound(context) // Start alarm sound

    // Intent to stop sound and open MainActivity
    val openIntent = Intent(context, StopSoundReceiver::class.java).apply {
        putExtra("open_main", true) // Flag to indicate opening MainActivity
    }

    // Intent to only stop sound when clicking "Cancel"
    val stopSoundIntent = Intent(context, StopSoundReceiver::class.java)
    val stopSoundPendingIntent = PendingIntent.getBroadcast(
        context, 0, stopSoundIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )


    val openPendingIntent = PendingIntent.getBroadcast(
        context, 1, openIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.broken_clouds)
        .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.notification))
        .setContentTitle(weather?.name ?: "Weather Update")
        .setContentText(weather?.weather?.firstOrNull()?.description ?: "No details available")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setSound(soundUri)
        .setVibrate(longArrayOf(500, 1000, 500, 1000))
        .addAction(R.drawable.clear_night, "Open", openPendingIntent) // Now stops sound before opening MainActivity
        .addAction(R.drawable.snowy, "Cancel", stopSoundPendingIntent) // Stops sound only
        .setAutoCancel(true)
        .build()

    with(NotificationManagerCompat.from(context)) {
        notify(1, notification)
    }
}


fun playNotificationSound(context: Context) {
    if (mediaPlayer == null) {
        mediaPlayer = MediaPlayer.create(context, R.raw.notification).apply {
            isLooping = true
            start()
        }
    }
}

fun stopNotificationSound() {
    mediaPlayer?.stop()
    mediaPlayer?.release()
    mediaPlayer = null
}


