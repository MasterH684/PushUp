package com.example.pushuplogger

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PushupAdapter(private val context: Context, private var pushupList: MutableList<PushupLog>, private val onUpdate: (PushupLog) -> Unit) : RecyclerView.Adapter<PushupAdapter.PushupViewHolder>() {

    class PushupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewPushupCount: TextView = itemView.findViewById(R.id.textViewPushupCount)
        val textViewDate: TextView = itemView.findViewById(R.id.textViewDate)
        val buttonEdit: Button = itemView.findViewById(R.id.buttonEdit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PushupViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pushup_log, parent, false)
        return PushupViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PushupViewHolder, position: Int) {
        val currentItem = pushupList[position]
        holder.textViewPushupCount.text = "${currentItem.count}"
        holder.textViewDate.text = currentItem.date

        holder.buttonEdit.setOnClickListener {
            showEditDialog(currentItem, position)
        }
    }

    private fun showEditDialog(pushupLog: PushupLog, position: Int) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Bewerk Push-ups")

        val view = LayoutInflater.from(context).inflate(R.layout.dialog_edit_pushup, null)
        val editTextPushups = view.findViewById<EditText>(R.id.editTextDialogPushups)
        editTextPushups.setText(pushupLog.count.toString())

        builder.setView(view)

        builder.setPositiveButton("Opslaan") { _, _ ->
            val newCount = editTextPushups.text.toString().toIntOrNull()
            if (newCount != null) {
                val updatedLog = pushupLog.copy(count = newCount)
                pushupList[position] = updatedLog
                onUpdate(updatedLog)
                notifyDataSetChanged()
            }
        }

        builder.setNegativeButton("Annuleren", null)

        builder.create().show()
    }

    override fun getItemCount() = pushupList.size
}
