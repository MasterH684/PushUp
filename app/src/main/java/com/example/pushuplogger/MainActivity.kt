package com.example.pushuplogger

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*
import android.appwidget.AppWidgetManager
import android.content.ComponentName

class MainActivity : AppCompatActivity() {

    private lateinit var editTextPushups: EditText
    private lateinit var textViewDate: TextView
    private lateinit var buttonSelectDate: Button
    private lateinit var buttonSavePushups: Button
    private lateinit var buttonWeekOverview: Button
    private lateinit var textViewTotalPushups: TextView
    private lateinit var recyclerViewPushups: RecyclerView
    private lateinit var pushupAdapter: PushupAdapter
    private val pushupList = mutableListOf<PushupLog>()
    private var selectedDate: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextPushups = findViewById(R.id.editTextPushups)
        textViewDate = findViewById(R.id.textViewDate)
        buttonSelectDate = findViewById(R.id.buttonSelectDate)
        buttonSavePushups = findViewById(R.id.buttonSavePushups)
        buttonWeekOverview = findViewById(R.id.buttonWeekOverview)
        textViewTotalPushups = findViewById(R.id.textViewTotalPushups)
        recyclerViewPushups = findViewById(R.id.recyclerViewPushups)

        pushupAdapter = PushupAdapter(this, pushupList, ::updatePushupLog)
        recyclerViewPushups.layoutManager = LinearLayoutManager(this)
        recyclerViewPushups.adapter = pushupAdapter

        loadPushupLogs()
        updateDate()
        updateTotalPushups()

        buttonSelectDate.setOnClickListener {
            showDatePickerDialog()
        }

        buttonSavePushups.setOnClickListener {
            savePushups()
        }

        buttonWeekOverview.setOnClickListener {
            val intent = Intent(this, WeekOverviewActivity::class.java)
            val gson = Gson()
            val pushupListJson = gson.toJson(pushupList)
            intent.putExtra("pushupList", pushupListJson)
            startActivity(intent)
        }
    }

    private fun updateDate(calendar: Calendar = selectedDate) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        textViewDate.text = dateFormat.format(calendar.time)
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                selectedDate.set(year, month, dayOfMonth)
                updateDate(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun savePushups() {
        val pushups = editTextPushups.text.toString().toIntOrNull()
        val date = textViewDate.text.toString()

        if (pushups != null && pushups > 0) {
            val pushupLog = PushupLog(pushups, date)
            pushupList.add(pushupLog)
            sortPushupList()
            updateTotalPushups()
            pushupAdapter.notifyDataSetChanged()
            savePushupLogs()
            updateWidget()

            editTextPushups.text.clear()
            updateDate()
        }
    }

    private fun updateWidget() {
        val intent = Intent(this, PushupWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(this).getAppWidgetIds(ComponentName(this, PushupWidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }
    private fun updatePushupLog(updatedLog: PushupLog) {
        val index = pushupList.indexOfFirst { it.date == updatedLog.date }
        if (index != -1) {
            pushupList[index] = updatedLog
            sortPushupList()
            updateTotalPushups()
            pushupAdapter.notifyDataSetChanged()
            savePushupLogs()
        }
    }

    private fun sortPushupList() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        pushupList.sortByDescending { dateFormat.parse(it.date) }
    }

    private fun updateTotalPushups() {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val filteredList = pushupList.filter { dateFormat.parse(it.date).after(calendar.time) || dateFormat.parse(it.date) == calendar.time }

        val totalPushups = filteredList.sumOf { it.count }
        textViewTotalPushups.text = "Totaal Push-ups (laatste 7 dagen): $totalPushups"
    }

    private fun savePushupLogs() {
        val sharedPreferences = getSharedPreferences("pushupPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val gson = Gson()
        val json = gson.toJson(pushupList)
        editor.putString("pushupLogs", json)
        editor.apply()
    }

    private fun loadPushupLogs() {
        val sharedPreferences = getSharedPreferences("pushupPrefs", Context.MODE_PRIVATE)
        val gson = Gson()
        val json = sharedPreferences.getString("pushupLogs", null)
        if (json != null) {
            val type = object : TypeToken<MutableList<PushupLog>>() {}.type
            val logs: MutableList<PushupLog> = gson.fromJson(json, type)
            pushupList.addAll(logs)
            sortPushupList()
        }
    }
}
