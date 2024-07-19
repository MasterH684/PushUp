package com.example.pushuplogger

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

import android.appwidget.AppWidgetManager
import android.content.ComponentName


class PushupAdapter(
    private val context: Context,
    private val pushupList: MutableList<PushupLog>,
    private val updatePushupLog: (PushupLog) -> Unit
) : RecyclerView.Adapter<PushupAdapter.PushupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PushupViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_pushup_log, parent, false)
        return PushupViewHolder(view)
    }

    override fun onBindViewHolder(holder: PushupViewHolder, position: Int) {
        val pushupLog = pushupList[position]
        holder.bind(pushupLog)

        holder.buttonDelete.setOnClickListener {
            removePushupLog(position)
        }
    }

    override fun getItemCount(): Int {
        return pushupList.size
    }

    private fun removePushupLog(position: Int) {
        pushupList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, pushupList.size)
        savePushupLogs()
    }

    private fun savePushupLogs() {
        val sharedPreferences = context.getSharedPreferences("pushupPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(pushupList)
        editor.putString("pushupLogs", json)
        editor.apply()
    }

    private fun updateWidget() {
        val intent = Intent(context, PushupWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(ComponentName(context, PushupWidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        context.sendBroadcast(intent)
    }

    inner class PushupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewPushupCount: TextView = itemView.findViewById(R.id.textViewPushupCount)
        private val textViewPushupDate: TextView = itemView.findViewById(R.id.textViewPushupDate)
        val buttonDelete: Button = itemView.findViewById(R.id.buttonDeletePushup)

        fun bind(pushupLog: PushupLog) {
            textViewPushupCount.text = pushupLog.count.toString()
            textViewPushupDate.text = pushupLog.date
        }
    }
}
