package com.example.train.viewmodel.trainer

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.model.trainer.Exercise
import com.example.train.model.trainer.ExerciseSelectUiItem
import com.example.train.model.trainer.Workout
import com.example.train.model.trainer.WorkoutCategoryPercent
import com.example.train.model.trainer.WorkoutExerciseDetail
import com.example.train.model.trainer.WorkoutUiItem

class WorkoutsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    val workouts = mutableStateListOf<WorkoutUiItem>()

    fun loadWorkouts() {
        workouts.clear()

        val workoutCursor = dbHelper.readableDatabase.query(
            DatabaseHelper.TABLE_WORKOUTS,
            null,
            null,
            null,
            null,
            null,
            null
        )

        if (workoutCursor.moveToFirst()) {
            do {
                val workout = Workout().apply {
                    id = workoutCursor.getInt(
                        workoutCursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_ID)
                    )

                    name = workoutCursor.getString(
                        workoutCursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_NAME)
                    )

                    description = workoutCursor.getString(
                        workoutCursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_DESC)
                    )

                    duration = workoutCursor.getInt(
                        workoutCursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_DURATION)
                    )
                }

                val exercises = loadExercisesForWorkout(workout.id)
                workout.exercises = exercises

                val exerciseDetails = loadWorkoutExerciseDetails(workout.id.toLong())

                val categoryPercents = calculateCategoryPercents(
                    details = exerciseDetails,
                    totalDuration = workout.duration
                )

                workouts.add(
                    WorkoutUiItem(
                        workout = workout,
                        exerciseDetails = exerciseDetails,
                        categoryPercents = categoryPercents
                    )
                )

            } while (workoutCursor.moveToNext())
        }

        workoutCursor.close()
    }

    private fun loadExercisesForWorkout(
        workoutId: Int
    ): List<Exercise> {
        val exercises = mutableListOf<Exercise>()

        val selection = "${DatabaseHelper.COL_WE_WORKOUT_ID} = ?"
        val args = arrayOf(workoutId.toString())

        val workoutExerciseCursor = dbHelper.readableDatabase.query(
            DatabaseHelper.TABLE_WORKOUT_EXERCISES,
            null,
            selection,
            args,
            null,
            null,
            null
        )

        if (workoutExerciseCursor.moveToFirst()) {
            do {
                val exerciseId = workoutExerciseCursor.getInt(
                    workoutExerciseCursor.getColumnIndexOrThrow(
                        DatabaseHelper.COL_WE_EXERCISE_ID
                    )
                )

                val exercise = loadExerciseById(exerciseId)

                if (exercise != null) {
                    exercises.add(exercise)
                }

            } while (workoutExerciseCursor.moveToNext())
        }

        workoutExerciseCursor.close()

        return exercises
    }

    private fun loadExerciseById(
        exerciseId: Int
    ): Exercise? {
        val selection = "${DatabaseHelper.COL_EXERCISE_ID} = ?"
        val args = arrayOf(exerciseId.toString())

        val cursor = dbHelper.readableDatabase.query(
            DatabaseHelper.TABLE_EXERCISES,
            null,
            selection,
            args,
            null,
            null,
            null
        )

        var exercise: Exercise? = null

        if (cursor.moveToFirst()) {
            exercise = Exercise().apply {
                id = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_ID)
                )

                name = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_NAME)
                )

                description = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_DESC)
                )

                category1 = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_CATEGORY1)
                )

                category2 = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_CATEGORY2)
                )

                timePerRep = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_TIME_PER_REP)
                )
            }
        }

        cursor.close()

        return exercise
    }

    private fun loadWorkoutExerciseDetails(
        workoutId: Long
    ): List<WorkoutExerciseDetail> {
        val details = mutableListOf<WorkoutExerciseDetail>()

        val cursor = dbHelper.getWorkoutExerciseDetails(workoutId)

        if (cursor.moveToFirst()) {
            do {
                val detail = WorkoutExerciseDetail(
                    name = cursor.getString(0),
                    reps = cursor.getInt(1),
                    timePerRep = cursor.getInt(2),
                    category1 = cursor.getString(3),
                    category2 = cursor.getString(4)
                )

                details.add(detail)

            } while (cursor.moveToNext())
        }

        cursor.close()

        return details
    }

    private fun calculateCategoryPercents(
        details: List<WorkoutExerciseDetail>,
        totalDuration: Int
    ): List<WorkoutCategoryPercent> {
        val categoryTimeMap = mutableMapOf<String, Int>()
        val safeTotalDuration = if (totalDuration <= 0) 1 else totalDuration

        details.forEach { detail ->
            val itemTotalTime = detail.reps * detail.timePerRep

            val category1 = detail.category1
            val category2 = detail.category2

            if (!category1.isNullOrBlank()) {
                categoryTimeMap[category1] =
                    (categoryTimeMap[category1] ?: 0) + itemTotalTime
            }

            if (!category2.isNullOrBlank() && category2 != category1) {
                categoryTimeMap[category2] =
                    (categoryTimeMap[category2] ?: 0) + itemTotalTime
            }
        }

        return categoryTimeMap.map { entry ->
            WorkoutCategoryPercent(
                category = entry.key,
                percent = (entry.value * 100) / safeTotalDuration
            )
        }
    }
// ---------------------- For Workout Dialog ----------------------
    val availableExercises = mutableStateListOf<ExerciseSelectUiItem>()

    fun loadAvailableExercises() {
        availableExercises.clear()

        val cursor = dbHelper.getAllExercises()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_ID)
                )

                val name = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_NAME)
                )

                availableExercises.add(
                    ExerciseSelectUiItem(
                        id = id,
                        name = name
                    )
                )

            } while (cursor.moveToNext())
        }

        cursor.close()
    }

    fun updateExerciseSelected(
        id: Long,
        selected: Boolean
    ) {
        val index = availableExercises.indexOfFirst { it.id == id }

        if (index != -1) {
            availableExercises[index] = availableExercises[index].copy(
                isSelected = selected
            )
        }
    }

    fun updateExerciseReps(
        id: Long,
        reps: String
    ) {
        val index = availableExercises.indexOfFirst { it.id == id }

        if (index != -1) {
            availableExercises[index] = availableExercises[index].copy(
                reps = reps
            )
        }
    }

    fun createWorkout(
        name: String,
        description: String
    ): String? {
        val trimmedName = name.trim()
        val trimmedDescription = description.trim()

        if (trimmedName.isEmpty()) {
            return "Please enter workout name"
        }

        val selectedExercises = availableExercises.filter {
            it.isSelected && (it.reps.toIntOrNull() ?: 0) > 0
        }

        if (selectedExercises.isEmpty()) {
            return "Please select at least one exercise"
        }

        val workoutId = dbHelper.insertWorkout(
            name = trimmedName,
            desc = trimmedDescription,
            duration = 0
        )

        selectedExercises.forEach { item ->
            dbHelper.addExerciseToWorkout(
                workoutId = workoutId,
                exerciseId = item.id,
                reps = item.reps.toIntOrNull() ?: 0
            )
        }

        val totalDuration = dbHelper.calculateWorkoutTotalDuration(workoutId)

        dbHelper.updateWorkoutDuration(
            workoutId = workoutId,
            duration = totalDuration
        )

        loadWorkouts()

        return null
    }
}