package com.example.train.viewmodel.trainee

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.model.Tag
import com.example.train.model.trainee.TraineeHomeUiState

class TraineeHomeViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    private val prefs = application.getSharedPreferences(
        "FitConnect",
        Context.MODE_PRIVATE
    )

    var uiState = mutableStateOf(TraineeHomeUiState())
        private set

    private val userId: Int
        get() = prefs.getInt("userId", -1)

    fun loadHomeData() {
        if (userId == -1) return

        val projection = arrayOf(
            DatabaseHelper.COL_USER_NAME,
            DatabaseHelper.COL_USER_BIO
        )

        val cursor = dbHelper.readableDatabase.query(
            DatabaseHelper.TABLE_USERS,
            projection,
            "${DatabaseHelper.COL_USER_ID} = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        cursor.use {
            if (it.moveToFirst()) {
                uiState.value = uiState.value.copy(
                    traineeName = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME)),
                    traineeBio = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_USER_BIO)),
                    traineeTags = loadUserTags(userId),
                    availableTags = loadAllTags()
                )
            }
        }
    }

    fun updateTraineeProfile(
        name: String,
        bio: String,
        password: String,
        tags: List<Tag>
    ): String? {
        if (userId == -1) return "User not found"

        val trimmedName = name.trim()
        val trimmedBio = bio.trim()
        val trimmedPassword = password.trim()

        if (trimmedName.isEmpty()) {
            return "Name cannot be empty"
        }

        dbHelper.updateUserProfile(
            userId = userId,
            name = trimmedName,
            bio = trimmedBio,
            password = trimmedPassword.ifEmpty { null }
        )
        dbHelper.updateUserTags(userId, tags)
        loadHomeData()

        return null
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
