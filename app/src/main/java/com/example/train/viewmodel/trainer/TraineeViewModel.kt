package com.example.train.viewmodel.trainer

import android.app.Application
import android.content.Context
import android.database.Cursor
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.model.Tag
import com.example.train.model.trainer.Trainee
import com.example.train.model.trainer.TraineesUiState

class TraineesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    private val prefs = application.getSharedPreferences(
        "FitConnect",
        Context.MODE_PRIVATE
    )

    var uiState = mutableStateOf(TraineesUiState())
        private set

    private val userId: Int
        get() = prefs.getInt("userId", -1)

    fun onApproveClick(trainerId: Int, traineeId: Int): Boolean {
        if (userId == -1) return false
        val isSuccess = dbHelper.acceptTrainee(trainerId, traineeId)
        if (isSuccess) {
            loadTabData()
        }

        return isSuccess
    }

    fun onDenyClick(trainerId: Int, traineeId: Int): Boolean {
        if (userId == -1) return false
        val isSuccess = dbHelper.denyTrainee(trainerId, traineeId)
        if (isSuccess) {
            loadTabData()
        }

        return isSuccess
    }

    fun loadTabData() {
        if (userId == -1) return

        val activeTrainees = loadTraineesFromCursor(
            cursor = dbHelper.getAllTrainees(userId)
        )

        val requestTrainees = loadTraineesFromCursor(
            cursor = dbHelper.getPendingRequest(userId)
        )

        uiState.value = TraineesUiState(
            activeCount = activeTrainees.size,
            requestCount = requestTrainees.size,
            allActiveTrainees = activeTrainees,
            allRequestTrainees = requestTrainees,
            trainerId= userId
        )
    }

    private fun loadTraineesFromCursor(cursor: Cursor): List<Trainee> {
        return cursor.use {
            it.mapToList {
                val traineeId = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID))

                Trainee(
                    id = traineeId,
                    name = getString(getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME)) ?: "Unknown",
                    bio = getString(getColumnIndexOrThrow(DatabaseHelper.COL_USER_BIO)) ?: "",
                    tags = loadUserTags(traineeId)
                )
            }
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
}
