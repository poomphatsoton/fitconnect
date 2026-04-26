package com.example.train.adapter

import android.content.Context
import android.database.Cursor
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.train.R
import com.example.train.database.DatabaseHelper
import com.example.train.databinding.ItemWorkoutBinding
import com.example.train.model.Workout

class WorkoutAdapter(
    private val context: Context,
    private val workoutList: List<Workout>
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    private val dbHelper = DatabaseHelper(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workoutList[position]
        holder.binding.tvWorkoutName.text = workout.name
        holder.binding.tvWorkoutDesc.text = workout.description
        holder.binding.tvExerciseCount.text = "${workout.exercises.size} exercises"

        val totalSec = workout.duration
        holder.binding.tvDuration.text = "${totalSec / 60}m ${totalSec % 60}s"

        // 训练项目列表
        holder.binding.layoutExerciseList.removeAllViews()
        val exerciseCursor = dbHelper.getWorkoutExerciseDetails(workout.id.toLong())
        if (exerciseCursor.moveToFirst()) {
            do {
                val exerciseName = exerciseCursor.getString(0)
                val reps = exerciseCursor.getInt(1)

                val itemLayout = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }

                val tvName = TextView(context).apply {
                    text = exerciseName
                    textSize = 14f
                    setPadding(0, 4, 0, 4)
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val tvReps = TextView(context).apply {
                    text = "$reps reps"
                    textSize = 14f
                }

                itemLayout.addView(tvName)
                itemLayout.addView(tvReps)
                holder.binding.layoutExerciseList.addView(itemLayout)
            } while (exerciseCursor.moveToNext())
        }
        exerciseCursor.close()

        // 分类占比
        holder.binding.layoutCategoryTags.removeAllViews()
        val categoryTimeMap = mutableMapOf<String, Int>()
        val totalDuration = if (workout.duration <= 0) 1 else workout.duration

        val categoryCursor = dbHelper.getWorkoutExerciseDetails(workout.id.toLong())
        if (categoryCursor.moveToFirst()) {
            do {
                val reps = categoryCursor.getInt(1)
                val timePerRep = categoryCursor.getInt(2)
                val category1 = categoryCursor.getString(3)
                val category2 = categoryCursor.getString(4)

                val itemTotalTime = reps * timePerRep
                categoryTimeMap[category1] = (categoryTimeMap[category1] ?: 0) + itemTotalTime

                if (category2 != null && category2 != category1) {
                    categoryTimeMap[category2] = (categoryTimeMap[category2] ?: 0) + itemTotalTime
                }
            } while (categoryCursor.moveToNext())
        }
        categoryCursor.close()

        // 自动换行
        var currentRow = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        holder.binding.layoutCategoryTags.addView(currentRow)

        val maxWidth = context.resources.displayMetrics.widthPixels - 120
        var usedWidth = 0

        categoryTimeMap.forEach { (category, value) ->
            val percent = (value * 100) / totalDuration
            val tvTag = TextView(context).apply {
                text = String.format("%s: %d%%", category, percent)
                setBackgroundResource(R.drawable.tag_bg)
                setTextColor(context.getColor(android.R.color.black))
                setPadding(25, 10, 25, 10)
                gravity = Gravity.CENTER
            }

            val params = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(8, 8, 8, 8)
            }
            tvTag.layoutParams = params

            tvTag.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val width = tvTag.measuredWidth

            if (usedWidth + width > maxWidth) {
                currentRow = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                }
                holder.binding.layoutCategoryTags.addView(currentRow)
                usedWidth = 0
            }

            currentRow.addView(tvTag)
            usedWidth += width + 60
        }
    }

    override fun getItemCount() = workoutList.size

    class WorkoutViewHolder(val binding: ItemWorkoutBinding) :
        RecyclerView.ViewHolder(binding.root)
}