package com.example.train.viewmodel.trainer

import android.app.Application
import android.database.Cursor
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.model.Tag
import com.example.train.model.trainer.TraineeDashboardProfile
import com.example.train.model.trainer.TraineeDashboardUiState
import com.example.train.model.trainer.TraineeDashboardWorkout
import java.time.LocalDate
import java.time.LocalTime

class DashboardViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    var uiState = mutableStateOf(TraineeDashboardUiState())
        private set

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadDashboard(traineeId: Int) {
        uiState.value = TraineeDashboardUiState(
            trainee = loadTraineeProfile(traineeId),
            workouts = loadDashboardWorkouts(traineeId)
        )
    }

    private fun loadTraineeProfile(traineeId: Int): TraineeDashboardProfile? {
        val cursor = dbHelper.getTraineeUserById(traineeId)

        return cursor.use {
            if (!it.moveToFirst()) return null

            TraineeDashboardProfile(
                id = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID)),
                name = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME)) ?: "Unknown",
                bio = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_USER_BIO)) ?: "",
                tags = loadUserTags(traineeId)
            )
        }
    }

    private fun loadUserTags(traineeId: Int): List<Tag> {
        return dbHelper.getUserTags(traineeId).use { cursor ->
            cursor.mapToList {
                Tag(
                    tagId = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_TAG_ID)),
                    tagName = getString(getColumnIndexOrThrow(DatabaseHelper.COL_TAG_NAME))
                )
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadDashboardWorkouts(traineeId: Int): List<TraineeDashboardWorkout> {
        return dbHelper.getTraineeDashboardWorkouts(traineeId).use { cursor ->
            cursor.mapToList { toDashboardWorkout() }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Cursor.toDashboardWorkout(): TraineeDashboardWorkout {
        return TraineeDashboardWorkout(
            assignmentId = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_ASSIGNMENT_ID)),
            workoutId = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_WORKOUT_ID)),
            workoutName = getString(getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_NAME)) ?: "Deleted workout",
            date = LocalDate.parse(getString(getColumnIndexOrThrow("first_date"))),
            lastDate = LocalDate.parse(getString(getColumnIndexOrThrow("last_date"))),
            startTime = LocalTime.parse(getString(getColumnIndexOrThrow("start_time"))),
            endTime = LocalTime.parse(getString(getColumnIndexOrThrow("end_time"))),
            completedExerciseTime = getInt(getColumnIndexOrThrow("completed_time")),
            totalExerciseTime = getInt(getColumnIndexOrThrow("total_time")),
            assignmentCount = getInt(getColumnIndexOrThrow("assignment_count"))
        )
    }
}
