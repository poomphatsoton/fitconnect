package com.example.train.viewmodel.trainee

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.model.trainer.Exercise
import com.example.train.model.trainer.Workout
import com.example.train.model.trainer.WorkoutExerciseDetail
import com.example.train.model.trainer.WorkoutTagPercent
import com.example.train.model.trainer.WorkoutUiItem
import com.example.train.model.trainee.TraineeAssignedWorkout
import com.example.train.model.trainee.TraineeWorkoutUiState
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

class TraineeWorkoutViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    private val prefs = application.getSharedPreferences(
        "FitConnect",
        Context.MODE_PRIVATE
    )

    var uiState = mutableStateOf(TraineeWorkoutUiState())
        private set

    private val userId: Int
        get() = prefs.getInt("userId", -1)

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadTodayWorkouts() {
        if (userId == -1) return

        val workouts = mutableListOf<TraineeAssignedWorkout>()
        dbHelper.getTraineeSlots(userId, LocalDate.now()).use { cursor ->
            while (cursor.moveToNext()) {
                val workoutIdIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_WORKOUT_ID)
                if (!cursor.isNull(workoutIdIndex)) {
                    val workoutId = cursor.getInt(workoutIdIndex)
                    val assignmentIdIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_ASSIGNMENT_ID)
                    val assignmentId = if (cursor.isNull(assignmentIdIndex)) null else cursor.getInt(assignmentIdIndex)
                    val workout = loadWorkoutById(workoutId, assignmentId)
                    if (workout != null) {
                        workouts.add(
                            TraineeAssignedWorkout(
                                slotId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_ID)),
                                startTime = LocalTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_START_TIME))),
                                endTime = LocalTime.parse(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_END_TIME))),
                                workoutItem = workout,
                                completedExerciseIds = loadCompletedExerciseIds(
                                    cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_ID))
                                )
                            )
                        )
                    }
                }
            }
        }

        uiState.value = TraineeWorkoutUiState(
            currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH.mm")),
            workouts = workouts.sortedBy { it.startTime }
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun markExerciseComplete(slotId: Int, exerciseId: Long): Boolean {
        val isSuccess = dbHelper.markWorkoutExerciseComplete(slotId, exerciseId)
        if (isSuccess) {
            loadTodayWorkouts()
        }
        return isSuccess
    }

    private fun loadCompletedExerciseIds(slotId: Int): Set<Long> {
        val completedExerciseIds = mutableSetOf<Long>()
        dbHelper.getCompletedExerciseIdsForSlot(slotId).use { cursor ->
            while (cursor.moveToNext()) {
                completedExerciseIds.add(
                    cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COMPLETION_EXERCISE_ID))
                )
            }
        }
        return completedExerciseIds
    }

    private fun loadWorkoutById(workoutId: Int, assignmentId: Int?): WorkoutUiItem? {
        val cursor = dbHelper.getWorkoutById(workoutId)

        cursor.use {
            if (!it.moveToFirst()) return null

            val snapshotDetails = assignmentId?.let { id -> loadSnapshotWorkoutExerciseDetails(id) }.orEmpty()
            val exerciseDetails = snapshotDetails.ifEmpty { loadWorkoutExerciseDetails(workoutId.toLong()) }

            val workout = Workout(
                id = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_ID)),
                name = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_NAME)),
                description = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_DESC)),
                duration = exerciseDetails.sumOf { detail -> detail.reps * detail.timePerRep },
                exercises = exerciseDetails.map { detail ->
                    Exercise(
                        id = detail.exerciseId.toInt(),
                        name = detail.name,
                        description = detail.description,
                        timePerRep = detail.timePerRep
                    )
                }
            )

            return WorkoutUiItem(
                workout = workout,
                exerciseDetails = exerciseDetails,
                tagPercents = calculateWorkoutTagPercents(workoutId.toLong(), exerciseDetails)
            )
        }
    }

    private fun loadWorkoutExerciseDetails(workoutId: Long): List<WorkoutExerciseDetail> {
        val details = mutableListOf<WorkoutExerciseDetail>()
        dbHelper.getWorkoutExerciseDetails(workoutId).use { cursor ->
            while (cursor.moveToNext()) {
                details.add(cursor.toWorkoutExerciseDetail())
            }
        }
        return details
    }

    private fun loadSnapshotWorkoutExerciseDetails(assignmentId: Int): List<WorkoutExerciseDetail> {
        val details = mutableListOf<WorkoutExerciseDetail>()
        dbHelper.getSnapshotWorkoutExerciseDetails(assignmentId).use { cursor ->
            while (cursor.moveToNext()) {
                details.add(cursor.toWorkoutExerciseDetail())
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

    private fun Cursor.toWorkoutExerciseDetail(): WorkoutExerciseDetail {
        return WorkoutExerciseDetail(
            exerciseId = getLong(getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_ID)),
            name = getString(getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_NAME)),
            reps = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_WE_REPS)),
            timePerRep = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_TIME_PER_REP)),
            description = getString(getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_DESC)),
            videoUrl = getString(getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_VIDEO_URL)),
            videoName = getString(getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_VIDEO_NAME))
        )
    }
}
