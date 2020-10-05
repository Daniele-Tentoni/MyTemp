package it.ap.mytemp.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import it.ap.mytemp.NewTemperatureActivity
import it.ap.mytemp.R

class NotificationService : IntentService("NotificationService") {
    private lateinit var builder: Notification.Builder
    private lateinit var mNotification: Notification
    private val mNotificationId: Int = 1000

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val context = this.applicationContext
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_title),
                importance
            )
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.description = getString(R.string.notification_channel_description)
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        val context = this.applicationContext
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notifyIntent = Intent(this, NewTemperatureActivity::class.java)

        val title = getString(R.string.notification_title)
        val message = getString(R.string.notification_message)

        notifyIntent.putExtra("title", title)
        notifyIntent.putExtra("message", message)
        notifyIntent.putExtra("notification", true)
        notifyIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
            val channelId = getString(R.string.notification_channel_id)
            Notification.Builder(this, channelId)
                .setChannelId(channelId)
        } else {
            Notification.Builder(this)
        }

        mNotification = builder
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.ic_baseline_add_24)
            .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))
            .setAutoCancel(true)
            .setContentTitle(title)
            .setStyle(Notification.BigTextStyle().bigText(message))
            .setContentText(message).build()
        notificationManager.notify(mNotificationId, mNotification)
    }
}