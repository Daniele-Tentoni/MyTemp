package it.ap.mytemp

import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

class NotificationUtils {
    fun setRecurrentNotification(activity: Activity) {
        val pref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
        val notification =
            pref.getBoolean(activity.getString(R.string.notification_already_set), false)
        if (!notification) { // If
            val alarmManager = activity.getSystemService(Activity.ALARM_SERVICE) as AlarmManager
            val alarmIntent = Intent(activity.applicationContext, AlarmReceiver::class.java)

            // Set the recurrent job at 9am and each half day.
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 9)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                activity,
                0,
                alarmIntent,
                PendingIntent.FLAG_CANCEL_CURRENT
            )

            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_HALF_DAY,
                pendingIntent
            )
        }
    }
}