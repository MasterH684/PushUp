package com.example.pushuplogger

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class WeekOverviewActivity : AppCompatActivity() {

    private lateinit var textViewWeekOverview: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_week_overview)

        textViewWeekOverview = findViewById(R.id.textViewWeekOverview)

        val pushupListJson = intent.getStringExtra("pushupList")
        val pushupList: List<PushupLog> = if (pushupListJson != null) {
            val type = object : TypeToken<List<PushupLog>>() {}.type
            Gson().fromJson(pushupListJson, type)
        } else {
            emptyList()
        }

        displayWeekOverview(pushupList)
    }

    private fun displayWeekOverview(pushupList: List<PushupLog>) {
        val weekMap = HashMap<Int, Int>()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        for (log in pushupList) {
            val date = dateFormat.parse(log.date)
            val calendar = Calendar.getInstance()
            calendar.time = date
            val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)

            weekMap[weekOfYear] = weekMap.getOrDefault(weekOfYear, 0) + log.count
        }

        val weekOverview = StringBuilder()
        for ((week, total) in weekMap) {
            weekOverview.append("Week $week: $total push-ups\n")
        }

        textViewWeekOverview.text = weekOverview.toString()
    }
}
