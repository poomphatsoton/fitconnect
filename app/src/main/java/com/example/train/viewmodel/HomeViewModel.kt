package com.example.train.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper

data class TrainerHomeUiState(
    val trainerName: String = "John Smith",
    val trainerBio: String = "Certified personal trainer with 10 years of experience",
    val activeTrainees: Int = 0,
    val exercises: Int = 0,
    val workouts: Int = 0,
    val pendingRequests: Int = 0
)

class TrainerHomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    private val prefs = application.getSharedPreferences(
        "FitConnect",
        Context.MODE_PRIVATE
    )

    var uiState = androidx.compose.runtime.mutableStateOf(TrainerHomeUiState())
        private set

    private val userId: Int
        get() = prefs.getInt("userId", -1)

    fun loadHomeData() {
        loadTrainerProfile()
        loadOverviewData()
    }

    fun loadTrainerProfile() {
        if (userId == -1) return

        val projection = arrayOf(
            DatabaseHelper.COL_USER_NAME,
            DatabaseHelper.COL_USER_BIO
        )

        val selection = "${DatabaseHelper.COL_USER_ID} = ?"
        val args = arrayOf(userId.toString())

        val cursor = dbHelper.readableDatabase.query(
            DatabaseHelper.TABLE_USERS,
            projection,
            selection,
            args,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val name = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME)
            )

            val bio = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_BIO)
            )

            uiState.value = uiState.value.copy(
                trainerName = name,
                trainerBio = bio
            )
        }

        cursor.close()
    }

    fun loadOverviewData() {
        if (userId == -1) return

        val active = dbHelper.getActiveTraineesCount(userId)
        val exercises = dbHelper.getExercisesCount()
        val workouts = dbHelper.getWorkoutsCount()
        val pending = dbHelper.getPendingRequestsCount(userId)

        uiState.value = uiState.value.copy(
            activeTrainees = active,
            exercises = exercises,
            workouts = workouts,
            pendingRequests = pending
        )
    }
}