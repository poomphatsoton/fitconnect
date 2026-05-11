package com.example.train.viewmodel.trainer

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.model.trainer.Exercise
import com.example.train.model.trainer.ExerciseSelectUiItem
import com.example.train.model.trainer.Workout
import com.example.train.model.trainer.WorkoutExerciseDetail
import com.example.train.model.trainer.WorkoutTagPercent
import com.example.train.model.trainer.WorkoutUiItem
import kotlin.math.roundToInt

class WorkoutsViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)
    private val prefs = application.getSharedPreferences("FitConnect", android.content.Context.MODE_PRIVATE)
    private val trainerId: Int
        get() = prefs.getInt("userId", -1)

    val workouts = mutableStateListOf<WorkoutUiItem>()

    fun loadWorkouts() {
        workouts.clear()

        dbHelper.getWorkoutsByTrainer(trainerId).use { cursor ->
            while (cursor.moveToNext()) {
                val workout = Workout().apply {
                    id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_ID)
                    )

                    name = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_NAME)
                    )

                    description = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_DESC)
                    )

                    duration = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_DURATION)
                    )
                }

                workout.exercises = loadExercisesForWorkout(workout.id)
                val exerciseDetails = loadWorkoutExerciseDetails(workout.id.toLong())

                workouts.add(
                    WorkoutUiItem(
                        workout = workout,
                        exerciseDetails = exerciseDetails,
                        tagPercents = calculateWorkoutTagPercents(workout.id.toLong(), exerciseDetails)
                    )
                )
            }
        }
    }

    private fun loadExercisesForWorkout(workoutId: Int): List<Exercise> {
        val exercises = mutableListOf<Exercise>()

        dbHelper.getWorkoutExercises(workoutId).use { cursor ->
            while (cursor.moveToNext()) {
                val exerciseId = cursor.getInt(
                    cursor.getColumnIndexOrThrow(
                        DatabaseHelper.COL_WE_EXERCISE_ID
                    )
                )

                loadExerciseById(exerciseId)?.let { exercises.add(it) }
            }
        }

        return exercises
    }

    private fun loadExerciseById(exerciseId: Int): Exercise? {
        return dbHelper.getExerciseById(exerciseId).use { cursor ->
            if (!cursor.moveToFirst()) return null

            Exercise().apply {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_ID))
                name = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_NAME)
                )
                description = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_DESC)
                )
                timePerRep = cursor.getInt(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_TIME_PER_REP)
                )
            }
        }
    }

    private fun loadWorkoutExerciseDetails(workoutId: Long): List<WorkoutExerciseDetail> {
        val details = mutableListOf<WorkoutExerciseDetail>()

        dbHelper.getWorkoutExerciseDetails(workoutId).use { cursor ->
            while (cursor.moveToNext()) {
                details.add(
                    WorkoutExerciseDetail(
                        exerciseId = cursor.getLong(0),
                        name = cursor.getString(1),
                        reps = cursor.getInt(2),
                        timePerRep = cursor.getInt(3),
                        description = cursor.getString(4),
                        videoUrl = cursor.getString(5),
                        videoName = cursor.getString(6)
                    )
                )
            }
        }

        return details
    }

    private fun calculateWorkoutTagPercents(
        workoutId: Long,
        exerciseDetails: List<WorkoutExerciseDetail>
    ): List<WorkoutTagPercent> {
        val totalTime = exerciseDetails.sumOf { it.reps * it.timePerRep }
        if (totalTime <= 0) return emptyList()

        val tagsByExercise = mutableMapOf<Long, MutableList<String>>()
        dbHelper.getWorkoutExerciseTagTimes(workoutId).use { cursor ->
            while (cursor.moveToNext()) {
                val exerciseId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_ID))
                val tagName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAG_NAME))
                tagsByExercise.getOrPut(exerciseId) { mutableListOf() }.add(tagName)
            }
        }

        val tagTimes = mutableMapOf<String, Double>()
        exerciseDetails.forEach { detail ->
            val tags = tagsByExercise[detail.exerciseId].orEmpty()
            if (tags.isNotEmpty()) {
                val sharedTime = (detail.reps * detail.timePerRep).toDouble() / tags.size
                tags.forEach { tagName ->
                    tagTimes[tagName] = (tagTimes[tagName] ?: 0.0) + sharedTime
                }
            }
        }

        return tagTimes.map { (tagName, tagTime) ->
            WorkoutTagPercent(
                tagName = tagName,
                percent = (tagTime / totalTime * 100).roundToInt()
            )
        }.sortedByDescending { it.percent }
    }

    val availableExercises = mutableStateListOf<ExerciseSelectUiItem>()

    fun loadAvailableExercises() {
        availableExercises.clear()

        dbHelper.getExercisesByTrainer(trainerId).use { cursor ->
            while (cursor.moveToNext()) {
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
            }
        }
    }

    fun updateExerciseSelected(id: Long, selected: Boolean) {
        val index = availableExercises.indexOfFirst { it.id == id }

        if (index != -1) {
            availableExercises[index] = availableExercises[index].copy(
                isSelected = selected
            )
        }
    }

    fun updateExerciseReps(id: Long, reps: String) {
        val index = availableExercises.indexOfFirst { it.id == id }

        if (index != -1) {
            availableExercises[index] = availableExercises[index].copy(
                reps = reps
            )
        }
    }

    fun createWorkout(name: String, description: String): String? {
        val trimmedName = name.trim()
        val trimmedDescription = description.trim()

        if (trimmedName.isEmpty()) {
            return "Please enter workout name"
        }

        val selectedExercises = selectedExercisesWithReps()
        if (selectedExercises.isEmpty()) {
            return "Please select at least one exercise"
        }

        val workoutId = dbHelper.insertWorkout(
            name = trimmedName,
            desc = trimmedDescription,
            duration = 0,
            trainerId = trainerId
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

    fun deleteWorkout(workoutId: Int) {
        dbHelper.deleteWorkout(workoutId)
        loadWorkouts()
    }

    fun loadWorkoutForEdit(workoutId: Int) {
        loadAvailableExercises()

        val workoutUiItem = workouts.find { it.workout.id == workoutId } ?: return
        availableExercises.forEachIndexed { index, selectItem ->
            val existingExercise = workoutUiItem.exerciseDetails.find {
                it.exerciseId == selectItem.id
            }
            if (existingExercise != null) {
                availableExercises[index] = selectItem.copy(
                    isSelected = true,
                    reps = existingExercise.reps.toString()
                )
            } else {
                availableExercises[index] = selectItem.copy(
                    isSelected = false,
                    reps = ""
                )
            }
        }
    }

    fun updateWorkout(workoutId: Int, name: String, description: String): String? {
        val trimmedName = name.trim()
        val trimmedDescription = description.trim()

        if (trimmedName.isEmpty()) {
            return "Please enter workout name"
        }

        val selectedExercises = selectedExercisesWithReps()
        if (selectedExercises.isEmpty()) {
            return "Please select at least one exercise"
        }

        dbHelper.replaceWorkoutExercises(
            workoutId = workoutId,
            name = trimmedName,
            desc = trimmedDescription,
            exercises = selectedExercises.map { item ->
                item.id to (item.reps.toIntOrNull() ?: 0)
            }
        )

        val totalDuration = dbHelper.calculateWorkoutTotalDuration(workoutId.toLong())
        dbHelper.updateWorkoutDuration(workoutId.toLong(), totalDuration)

        loadWorkouts()
        return null
    }

    private fun selectedExercisesWithReps(): List<ExerciseSelectUiItem> {
        return availableExercises.filter { item ->
            item.isSelected && (item.reps.toIntOrNull() ?: 0) > 0
        }
    }
}
