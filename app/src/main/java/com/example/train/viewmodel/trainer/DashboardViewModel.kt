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
        val cursor = dbHelper.readableDatabase.query(
            DatabaseHelper.TABLE_USERS,
            null,
            "${DatabaseHelper.COL_USER_ID} = ? AND ${DatabaseHelper.COL_USER_ROLE} = ?",
            arrayOf(traineeId.toString(), "trainee"),
            null,
            null,
            null
        )

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
        val query = """
            SELECT
                s.${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID},
                s.${DatabaseHelper.COL_SLOT_WORKOUT_ID},
                w.${DatabaseHelper.COL_WORKOUT_NAME},
                s.${DatabaseHelper.COL_SLOT_DATE},
                MIN(s.${DatabaseHelper.COL_SLOT_START_TIME}) AS start_time,
                MAX(s.${DatabaseHelper.COL_SLOT_END_TIME}) AS end_time,
                COALESCE(p.${DatabaseHelper.COL_PROGRESS_COMPLETED_EXERCISE_TIME}, 0) AS completed_time,
                COALESCE(p.${DatabaseHelper.COL_PROGRESS_TOTAL_EXERCISE_TIME}, 0) AS total_time
            FROM ${DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT} s
            LEFT JOIN ${DatabaseHelper.TABLE_WORKOUTS} w
                ON s.${DatabaseHelper.COL_SLOT_WORKOUT_ID} = w.${DatabaseHelper.COL_WORKOUT_ID}
            LEFT JOIN ${DatabaseHelper.TABLE_WORKOUT_ASSIGNMENT_PROGRESS} p
                ON s.${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID} = p.${DatabaseHelper.COL_PROGRESS_ASSIGNMENT_ID}
            WHERE s.${DatabaseHelper.COL_TRAINEE_ID} = ?
            AND s.${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID} IS NOT NULL
            GROUP BY s.${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID}
            ORDER BY s.${DatabaseHelper.COL_SLOT_DATE} ASC, start_time ASC
        """.trimIndent()

        return dbHelper.readableDatabase.rawQuery(query, arrayOf(traineeId.toString())).use { cursor ->
            cursor.mapToList { toDashboardWorkout() }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Cursor.toDashboardWorkout(): TraineeDashboardWorkout {
        return TraineeDashboardWorkout(
            assignmentId = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_ASSIGNMENT_ID)),
            workoutId = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_WORKOUT_ID)),
            workoutName = getString(getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_NAME)) ?: "Deleted workout",
            date = LocalDate.parse(getString(getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_DATE))),
            startTime = LocalTime.parse(getString(getColumnIndexOrThrow("start_time"))),
            endTime = LocalTime.parse(getString(getColumnIndexOrThrow("end_time"))),
            completedExerciseTime = getInt(getColumnIndexOrThrow("completed_time")),
            totalExerciseTime = getInt(getColumnIndexOrThrow("total_time"))
        )
    }
}
