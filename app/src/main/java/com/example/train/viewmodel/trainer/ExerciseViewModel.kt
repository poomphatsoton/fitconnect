package com.example.train.viewmodel.trainer

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.train.database.DatabaseHelper
import com.example.train.database.helper.VideoStorageHelper
import com.example.train.model.Tag
import com.example.train.model.trainer.Exercise
import kotlinx.coroutines.launch

class ExercisesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val logTag = "ExercisesViewModel"
    private val dbHelper = DatabaseHelper(application)
    private val videoStorage = VideoStorageHelper(dbHelper)

    val exercises = mutableStateListOf<Exercise>()
    var exerciseTagsMap by mutableStateOf<Map<Int, List<Tag>>>(emptyMap())
        private set
    var availableTags by mutableStateOf<List<Tag>>(emptyList())
        private set
    var isUploadingVideo by mutableStateOf(false)
        private set

    fun loadExercises() {
        exercises.clear()
        val newTagsMap = mutableMapOf<Int, List<Tag>>()
        availableTags = loadAllTags()

        dbHelper.getAllExercises().use { cursor ->
            while (cursor.moveToNext()) {
                val videoUrl = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_VIDEO_URL)
                )
                val videoName = cursor.getString(
                    cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_VIDEO_NAME)
                )

                val exercise = Exercise(
                    id = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_ID)
                    ),
                    name = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_NAME)
                    ),
                    description = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_DESC)
                    ),
                    timePerRep = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_TIME_PER_REP)
                    ),
                    videoUrl = videoUrl,
                    videoName = videoName.takeUnless { it.isNullOrBlank() } ?: videoNameFromUrl(videoUrl)
                )

                exercises.add(exercise)
                newTagsMap[exercise.id] = getTagsByExerciseId(exercise.id)
            }
        }

        exerciseTagsMap = newTagsMap
    }

    private fun loadAllTags(): List<Tag> {
        val tags = mutableListOf<Tag>()

        dbHelper.getAllTags().use { cursor ->
            while (cursor.moveToNext()) {
                tags.add(
                    Tag(
                        tagId = cursor.getInt(
                            cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAG_ID)
                        ),
                        tagName = cursor.getString(
                            cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAG_NAME)
                        )
                    )
                )
            }
        }

        return tags
    }

    private fun getTagsByExerciseId(exerciseId: Int): List<Tag> {
        val tags = mutableListOf<Tag>()

        val cursor = dbHelper.getExerciseTags(exerciseId.toLong())

        cursor.use {
            while (it.moveToNext()) {
                tags.add(
                    Tag(
                        tagId = it.getInt(
                            it.getColumnIndexOrThrow(DatabaseHelper.COL_TAG_ID)
                        ),
                        tagName = it.getString(
                            it.getColumnIndexOrThrow(DatabaseHelper.COL_TAG_NAME)
                        )
                    )
                )
            }
        }

        return tags
    }

    fun createExercise(
        name: String,
        description: String,
        timePerRepText: String,
        tags: List<Tag>,
        videoUri: Uri? = null,
        onFinished: (Boolean) -> Unit = {}
    ): String? {
        val trimmedName = name.trim()
        val trimmedDescription = description.trim()
        val trimmedTime = timePerRepText.trim()

        if (trimmedName.isEmpty() || trimmedTime.isEmpty()) {
            return "Please fill required fields"
        }

        val timePerRep = parseMinutesToSeconds(trimmedTime)
            ?: return "Time must be a whole number of minutes"

        val exerciseId = dbHelper.insertExercise(
            name = trimmedName,
            desc = trimmedDescription,
            timePerRep = timePerRep,
            tags = tags
        )

        if (exerciseId == -1L) {
            return "Could not save exercise"
        }

        saveVideoIfSelected(
            exerciseId = exerciseId.toInt(),
            videoUri = videoUri,
            videoName = videoUri?.getFileName(),
            onFinished = onFinished
        )

        return null
    }

    fun deleteExercise(exerciseId: Int) {
        dbHelper.deleteExercise(exerciseId)
        loadExercises()
    }

    fun updateExercise(
        id: Int,
        name: String,
        description: String,
        timePerRepText: String,
        tags: List<Tag>,
        videoUri: Uri? = null,
        onFinished: (Boolean) -> Unit = {}
    ): String? {
        val trimmedName = name.trim()
        val trimmedDescription = description.trim()
        val trimmedTime = timePerRepText.trim()

        if (trimmedName.isEmpty() || trimmedTime.isEmpty()) {
            return "Please fill required fields"
        }

        val timePerRep = parseMinutesToSeconds(trimmedTime)
            ?: return "Time must be a whole number of minutes"

        dbHelper.updateExercise(
            id = id,
            name = trimmedName,
            desc = trimmedDescription,
            timePerRep = timePerRep,
            tags = tags
        )

        saveVideoIfSelected(
            exerciseId = id,
            videoUri = videoUri,
            videoName = videoUri?.getFileName(),
            onFinished = onFinished
        )

        return null
    }

    private fun saveVideoIfSelected(
        exerciseId: Int,
        videoUri: Uri?,
        videoName: String?,
        onFinished: (Boolean) -> Unit
    ) {
        loadExercises()

        if (videoUri == null) {
            onFinished(true)
            return
        }

        isUploadingVideo = true
        viewModelScope.launch {
            try {
                videoStorage.uploadExerciseVideo(exerciseId, videoUri, videoName.orEmpty())
                loadExercises()
                onFinished(true)
            } catch (e: Exception) {
                Log.e(logTag, "Video upload failed", e)
                onFinished(false)
            } finally {
                isUploadingVideo = false
            }
        }
    }

    private fun Uri.getFileName(): String {
        val resolver = getApplication<Application>().contentResolver

        resolver.query(this, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && cursor.moveToFirst()) {
                return cursor.getString(nameIndex)
            }
        }

        return lastPathSegment ?: "video.mp4"
    }

    private fun videoNameFromUrl(url: String?): String? {
        if (url.isNullOrBlank()) return null
        return url.substringAfterLast('/').substringBefore('?').ifBlank { null }
    }

    private fun parseMinutesToSeconds(value: String): Int? {
        val minutes = value.toIntOrNull() ?: return null
        if (minutes <= 0) return null
        return minutes * 60
    }
}
