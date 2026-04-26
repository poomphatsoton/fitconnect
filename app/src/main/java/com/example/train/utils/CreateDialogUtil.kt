package com.example.train.utils

import android.content.Context
import android.database.Cursor
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.train.R
import com.example.train.database.DatabaseHelper

object CreateDialogUtil {

    fun interface OnCreateSuccessListener {
        fun onCreateSuccess()
    }

    fun showCreateExerciseDialog(context: Context, listener: OnCreateSuccessListener?) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_create_exercise, null)
        val dialog = AlertDialog.Builder(context).setView(view).setCancelable(true).create()

        val etName = view.findViewById<EditText>(R.id.et_name)
        val etDesc = view.findViewById<EditText>(R.id.et_desc)
        val etC1 = view.findViewById<EditText>(R.id.et_category1)
        val etC2 = view.findViewById<EditText>(R.id.et_category2)
        val etTime = view.findViewById<EditText>(R.id.et_time)

        view.findViewById<View>(R.id.btn_cancel).setOnClickListener { dialog.dismiss() }
        view.findViewById<View>(R.id.btn_confirm).setOnClickListener {
            val name = etName.text.toString().trim()
            val desc = etDesc.text.toString().trim()
            val c1 = etC1.text.toString().trim()
            val c2 = etC2.text.toString().trim()
            val timeStr = etTime.text.toString().trim()

            if (name.isEmpty() || timeStr.isEmpty()) {
                Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val time = timeStr.toInt()
            val db = DatabaseHelper(context)
            db.insertExercise(name, desc, c1, c2, time)
            Toast.makeText(context, "Created successfully", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            listener?.onCreateSuccess()
        }

        dialog.show()
        dialog.window?.setLayout((context.resources.displayMetrics.widthPixels * 0.9).toInt(), -2)
    }

    fun showCreateWorkoutDialog(context: Context, listener: OnCreateSuccessListener?) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_create_workout, null)
        val dialog = AlertDialog.Builder(context).setView(view).setCancelable(true).create()

        val etName = view.findViewById<EditText>(R.id.et_name)
        val etDesc = view.findViewById<EditText>(R.id.et_desc)
        val rvExercises = view.findViewById<RecyclerView>(R.id.rv_exercises)

        val dbHelper = DatabaseHelper(context)
        val cursor = dbHelper.getAllExercises()
        val list = mutableListOf<ExerciseSelectItem>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_ID))
                val name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_NAME))
                list.add(ExerciseSelectItem(id, name, false, 0))
            } while (cursor.moveToNext())
        }
        cursor.close()

        val adapter = ExerciseSelectAdapter(list)
        rvExercises.layoutManager = LinearLayoutManager(context)
        rvExercises.adapter = adapter

        view.findViewById<View>(R.id.btn_cancel).setOnClickListener { dialog.dismiss() }
        view.findViewById<View>(R.id.btn_confirm).setOnClickListener {
            val name = etName.text.toString().trim()
            val desc = etDesc.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(context, "Please enter workout name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selected = adapter.selectedItems
            if (selected.isEmpty()) {
                Toast.makeText(context, "Please select at least one exercise", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val db = DatabaseHelper(context)
            val workoutId = db.insertWorkout(name, desc, 0)
            selected.forEach { item ->
                db.addExerciseToWorkout(workoutId, item.id, item.reps)
            }

            val total = db.calculateWorkoutTotalDuration(workoutId)
            db.updateWorkoutDuration(workoutId, total)

            Toast.makeText(context, "Workout created successfully", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
            listener?.onCreateSuccess()
        }

        dialog.show()
        dialog.window?.setLayout((context.resources.displayMetrics.widthPixels * 0.9).toInt(), -2)
    }

    private data class ExerciseSelectItem(
        var id: Long,
        var name: String,
        var isSelected: Boolean,
        var reps: Int
    )

    private class ExerciseSelectAdapter(private val list: MutableList<ExerciseSelectItem>) :
        RecyclerView.Adapter<ExerciseSelectAdapter.VH>() {

        val selectedItems: List<ExerciseSelectItem>
            get() = list.filter { it.isSelected && it.reps > 0 }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_exercise_select, parent, false)
            return VH(v)
        }

        override fun onBindViewHolder(holder: VH, position: Int) {
            val item = list[position]
            holder.tvName.text = item.name
            holder.cb.isChecked = item.isSelected
            holder.etReps.setText(if (item.reps > 0) item.reps.toString() else "")

            holder.cb.setOnCheckedChangeListener { _, isChecked ->
                item.isSelected = isChecked
            }

            holder.etReps.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
                override fun afterTextChanged(s: Editable?) {
                    item.reps = try {
                        s.toString().toInt()
                    } catch (e: Exception) {
                        0
                    }
                }
            })
        }

        override fun getItemCount() = list.size

        class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val cb: CheckBox = itemView.findViewById(R.id.cb_exercise)
            val tvName: TextView = itemView.findViewById(R.id.tv_exercise_name)
            val etReps: EditText = itemView.findViewById(R.id.et_reps)
        }
    }
}