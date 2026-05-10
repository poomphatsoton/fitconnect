package com.example.train.viewmodel.trainer

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.model.Tag
import com.example.train.model.trainer.TrainerHomeUiState

class TrainerHomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    private val prefs = application.getSharedPreferences(
        "FitConnect",
        Context.MODE_PRIVATE
    )

    var uiState = mutableStateOf(TrainerHomeUiState())
        private set

    private val userId: Int
        get() = prefs.getInt("userId", -1)

    fun loadHomeData() {
        loadTrainerProfile()
        loadOverviewData()
    }

    fun loadTrainerProfile() {
        if (userId == -1) return

        val cursor = dbHelper.getUserById(userId)

        if (cursor.moveToFirst()) {
            val name = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME)
            )

            val bio = cursor.getString(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_BIO)
            )

            val maxTrainees = cursor.getInt(
                cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_MAX_TRAINEES)
            )

            uiState.value = uiState.value.copy(
                trainerName = name,
                trainerBio = bio,
                trainerPassword = "",
                trainerTags = loadUserTags(userId),
                availableTags = loadAllTags(),
                maxTrainees = maxTrainees
            )
        }

        cursor.close()
    }

    fun updateTrainerProfile(
        name: String,
        bio: String,
        maxTraineesText: String,
        password: String,
        tags: List<Tag>
    ): String? {
        if (userId == -1) return "User not found"

        val trimmedName = name.trim()
        val trimmedBio = bio.trim()
        val trimmedPassword = password.trim()
        val maxTrainees = maxTraineesText.trim().toIntOrNull()
            ?: return "Max trainees must be a number"

        if (trimmedName.isEmpty()) {
            return "Name cannot be empty"
        }

        if (maxTrainees < 0) {
            return "Max trainees cannot be negative"
        }

        dbHelper.updateUserProfile(
            userId = userId,
            name = trimmedName,
            bio = trimmedBio,
            maxTrainees = maxTrainees,
            password = trimmedPassword.ifEmpty { null }
        )
        dbHelper.updateUserTags(userId, tags)
        loadTrainerProfile()

        return null
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

    private fun loadUserTags(userId: Int): List<Tag> {
        val tags = mutableListOf<Tag>()
        dbHelper.getUserTags(userId).use { cursor ->
            while (cursor.moveToNext()) {
                tags.add(
                    Tag(
                        tagId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAG_ID)),
                        tagName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAG_NAME))
                    )
                )
            }
        }
        return tags
    }

    private fun loadAllTags(): List<Tag> {
        val tags = mutableListOf<Tag>()
        dbHelper.getAllTags().use { cursor ->
            while (cursor.moveToNext()) {
                tags.add(
                    Tag(
                        tagId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAG_ID)),
                        tagName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAG_NAME))
                    )
                )
            }
        }
        return tags
    }
}
