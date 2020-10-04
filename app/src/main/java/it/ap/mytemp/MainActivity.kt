package it.ap.mytemp

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import it.ap.mytemp.models.Temperature
import java.util.*

class MainActivity : AppCompatActivity() {
    private val newTemperatureActivityRequestCode = 1
    private lateinit var temperatureViewModel: TemperatureViewModel
    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var builder: Notification.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview)
        val adapter = TemperatureListAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        temperatureViewModel = ViewModelProvider(this).get(TemperatureViewModel::class.java)
        temperatureViewModel.allTemperatures.observe(this, Observer { temps ->
            // Update the cached copy of the temperatures in the adapter.
            temps?.let { adapter.setTemperatures(it) }
        })

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, NewTemperatureActivity::class.java)
            startActivityForResult(intent, newTemperatureActivityRequestCode)
        }

        NotificationUtils().setNotification(Calendar.getInstance().timeInMillis + 5000, this@MainActivity)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newTemperatureActivityRequestCode && resultCode == Activity.RESULT_OK) {
            data?.getStringExtra(NewTemperatureActivity.EXTRA_REPLY)?.let {
                val temperature = Temperature(
                    0,
                    Calendar.getInstance().get(Calendar.LONG_FORMAT).toString(),
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toString(),
                    it.toDouble()
                )
                temperatureViewModel.insert(temperature)
            }
        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun createNotification() {
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(this, NewTemperatureActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val contentView = RemoteViews(packageName, R.layout.activity_new_temperature)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = NotificationChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_title),
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(false)
            notificationManager.createNotificationChannel(notificationChannel)

            builder = Notification.Builder(this, getString(R.string.notification_channel_id))
                .setChannelId(getString(R.string.notification_channel_id))
        } else {
            builder = Notification.Builder(this)
        }

        builder = builder
            .setContentTitle(getString(R.string.notification_title))
            .setContentText(getString(R.string.notification_title))
            .setSmallIcon(R.drawable.ic_baseline_add_24)
            .setLargeIcon(
                BitmapFactory.decodeResource(
                    this.resources,
                    R.drawable.ic_launcher_background
                )
            )
            .setContentIntent(pendingIntent)
        notificationManager.notify(1234, builder.build())
    }
}