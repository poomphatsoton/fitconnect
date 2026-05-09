package com.example.train.viewmodel.trainee

import android.app.Application
import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.model.Tag
import com.example.train.model.trainee.TraineeTrainerUiState
import com.example.train.model.trainee.TrainerProfile

class TraineeTrainerViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    private val prefs = application.getSharedPreferences(
        "FitConnect",
        Context.MODE_PRIVATE
    )

    var uiState = mutableStateOf(TraineeTrainerUiState())
        private set

    private val userId: Int
        get() = prefs.getInt("userId", -1)

    private var allOtherTrainers: List<TrainerProfile> = emptyList()

    fun loadTrainers() {
        if (userId == -1) return

        val myTrainerId = dbHelper.getMyTrainerID(userId)
        val pendingTrainerId = dbHelper.getPendingTrainerRequestId(userId)
        val displayedTrainerId = if (myTrainerId != -1) myTrainerId else pendingTrainerId
        val myTrainer = if (displayedTrainerId != -1) {
            loadTrainerById(displayedTrainerId)?.copy(
                requestStatus = if (myTrainerId != -1) DatabaseHelper.STATUS_ACCEPTED else DatabaseHelper.STATUS_PENDING
            )
        } else {
            null
        }

        allOtherTrainers = loadAllTrainers()
            .filterNot { it.id == displayedTrainerId }

        uiState.value = uiState.value.copy(
            myTrainer = myTrainer,
            otherTrainers = filterTrainers(
                trainers = allOtherTrainers,
                query = uiState.value.searchQuery,
                selectedTags = uiState.value.selectedTags
            ),
            availableTags = loadAllTags(),
            hasTrainerOrPendingRequest = displayedTrainerId != -1
        )
    }

    fun requestTrainer(trainerId: Int): Boolean {
        if (userId == -1 || uiState.value.hasTrainerOrPendingRequest) return false

        val isSuccess = dbHelper.requestTrainer(trainerId, userId)
        if (isSuccess) {
            loadTrainers()
        }
        return isSuccess
    }

    fun cancelTrainerRequest(trainerId: Int): Boolean {
        if (userId == -1) return false

        val isSuccess = dbHelper.cancelTrainerRequest(trainerId, userId)
        if (isSuccess) {
            loadTrainers()
        }
        return isSuccess
    }

    fun onSearchQueryChange(query: String) {
        uiState.value = uiState.value.copy(
            searchQuery = query,
            otherTrainers = filterTrainers(
                trainers = allOtherTrainers,
                query = query,
                selectedTags = uiState.value.selectedTags
            )
        )
    }

    fun onTagClick(tag: Tag) {
        val currentTags = uiState.value.selectedTags
        val selectedTags = if (currentTags.any { it.tagId == tag.tagId }) {
            currentTags.filterNot { it.tagId == tag.tagId }
        } else {
            currentTags + tag
        }

        uiState.value = uiState.value.copy(
            selectedTags = selectedTags,
            otherTrainers = filterTrainers(
                trainers = allOtherTrainers,
                query = uiState.value.searchQuery,
                selectedTags = selectedTags
            )
        )
    }

    fun onSelectAllTags() {
        uiState.value = uiState.value.copy(
            selectedTags = emptyList(),
            otherTrainers = filterTrainers(
                trainers = allOtherTrainers,
                query = uiState.value.searchQuery,
                selectedTags = emptyList()
            )
        )
    }

    private fun filterTrainers(
        trainers: List<TrainerProfile>,
        query: String,
        selectedTags: List<Tag>
    ): List<TrainerProfile> {
        val trimmedQuery = query.trim()
        return trainers.filter { trainer ->
            val matchesSearch = trimmedQuery.isEmpty() ||
                trainer.name.contains(trimmedQuery, ignoreCase = true) ||
                trainer.bio.contains(trimmedQuery, ignoreCase = true) ||
                trainer.tags.any { it.tagName.contains(trimmedQuery, ignoreCase = true) }

            val matchesTags = selectedTags.isEmpty() ||
                selectedTags.all { selectedTag ->
                    trainer.tags.any { it.tagId == selectedTag.tagId }
                }

            matchesSearch && matchesTags
        }
    }

    private fun loadAllTrainers(): List<TrainerProfile> {
        val trainers = mutableListOf<TrainerProfile>()
        dbHelper.getAllTrainers().use { cursor ->
            while (cursor.moveToNext()) {
                trainers.add(cursor.toTrainerProfile())
            }
        }
        return trainers
    }

    private fun loadTrainerById(trainerId: Int): TrainerProfile? {
        dbHelper.getTrainerById(trainerId).use { cursor ->
            if (cursor.moveToFirst()) {
                return cursor.toTrainerProfile()
            }
        }
        return null
    }

    private fun android.database.Cursor.toTrainerProfile(): TrainerProfile {
        val trainerId = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_USER_ID))
        val maxTrainees = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_USER_MAX_TRAINEES))
        return TrainerProfile(
            id = trainerId,
            name = getString(getColumnIndexOrThrow(DatabaseHelper.COL_USER_NAME)),
            bio = getString(getColumnIndexOrThrow(DatabaseHelper.COL_USER_BIO)),
            tags = loadUserTags(trainerId),
            activeTrainees = dbHelper.getActiveTraineesCount(trainerId),
            maxTrainees = maxTrainees
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
