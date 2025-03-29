import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.example.weatherapplication.MainActivity
import com.example.weatherapplication.R
import com.example.weatherapplication.domain.model.CurrentWeather

fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "message_channel"
        val name = "Messages"
        val descriptionText = "Notification for new messages"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}


fun showNotification(context: Context, weather: CurrentWeather?) {
    val channelId = "message_channel"

    val replyIntent = Intent(context, MainActivity::class.java).apply {
        action = Intent.ACTION_VIEW
        data = "favDetails".toUri()
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }

    val replyPendingIntent = PendingIntent.getActivity(
        context, 0, replyIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )


    val notification =
        NotificationCompat.Builder(context, channelId).setSmallIcon(R.drawable.broken_clouds)
            .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.notification))
            .setContentTitle("${weather?.name}")
            .setContentText("${weather?.weather?.firstOrNull()?.description} ")
            .setPriority(NotificationCompat.PRIORITY_HIGH).addAction(
                R.drawable.clear_night,
                "Open",
                replyPendingIntent
            )
            .addAction(
                R.drawable.snowy,
                "Cancel",
                replyPendingIntent
            )
            .build()

    with(NotificationManagerCompat.from(context)) {
        notify(1, notification)
    }
}