package com.example.pushuplogger

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class PushupWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            views.setOnClickPendingIntent(R.id.widgetButtonAddPushups, pendingIntent)

            val sharedPreferences = context.getSharedPreferences("pushupPrefs", Context.MODE_PRIVATE)
            val totalPushups = sharedPreferences.getInt("totalPushups", 0)
            views.setTextViewText(R.id.widgetTextViewTotalPushups, "Pushups: $totalPushups")

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
