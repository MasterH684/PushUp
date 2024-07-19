package com.example.pushuplogger

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var textViewTotalPushups: TextView
    private var totalPushups = 0
    private val sharedPreferences by lazy { getSharedPreferences("pushupPrefs", Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewTotalPushups = findViewById(R.id.textViewTotalPushups)
        val buttonAddPushups: Button = findViewById(R.id.buttonAddPushups)

        totalPushups = sharedPreferences.getInt("totalPushups", 0)
        textViewTotalPushups.text = "Total Pushups: $totalPushups"

        buttonAddPushups.setOnClickListener {
            totalPushups++
            textViewTotalPushups.text = "Total Pushups: $totalPushups"
            sharedPreferences.edit().putInt("totalPushups", totalPushups).apply()
        }

        scheduleWeeklyNotification()
    }

    private fun scheduleWeeklyNotification() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            set(Calendar.HOUR_OF_DAY, 9)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY * 7,
            pendingIntent
        )
    }
}
