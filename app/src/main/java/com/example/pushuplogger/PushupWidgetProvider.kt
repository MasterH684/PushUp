package com.example.pushuplogger

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

class PushupWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        private const val SHARED_PREFS = "pushupPrefs"
        private const val PUSHUP_LOGS_KEY = "pushupLogs"

        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            // Load push-up logs from SharedPreferences
            val sharedPreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE)
            val gson = Gson()
            val json = sharedPreferences.getString(PUSHUP_LOGS_KEY, null)
            val pushupList: List<PushupLog> = if (json != null) {
                val type = object : TypeToken<List<PushupLog>>() {}.type
                gson.fromJson(json, type)
            } else {
                emptyList()
            }

            // Calculate push-ups for the last 7 days and the 7 days before that
            val (last7DaysPushups, prev7DaysPushups) = calculatePushups(pushupList)

            // Update the widget text
            views.setTextViewText(R.id.textViewLast7DaysPushups, "Last 8 days: $last7DaysPushups")
            views.setTextViewText(R.id.textViewPrev7DaysPushups, "Prev 8 days: $prev7DaysPushups")

            // Set up a pending intent to launch the app when the widget is clicked
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widgetLayout, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun calculatePushups(pushupList: List<PushupLog>): Pair<Int, Int> {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()

            // Calculate the end date for the last 7 days period
            val endDate = calendar.time
            // Calculate the start date for the last 7 days period
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val startDate = calendar.time
            val last7DaysPushups = pushupList.filter {
                val date = dateFormat.parse(it.date)
                date.after(startDate) && date.before(endDate)
            }.sumOf { it.count }

            // Calculate the end date for the previous 7 days period
            calendar.add(Calendar.DAY_OF_YEAR, -7)
            val prevStartDate = calendar.time
            val prev7DaysPushups = pushupList.filter {
                val date = dateFormat.parse(it.date)
                date.after(prevStartDate) && date.before(startDate)
            }.sumOf { it.count }

            return Pair(last7DaysPushups, prev7DaysPushups)
        }
    }
}
