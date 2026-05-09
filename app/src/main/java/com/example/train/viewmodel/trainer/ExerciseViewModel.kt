package com.example.train.viewmodel.trainer

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.model.Tag
import com.example.train.model.trainer.Exercise
import kotlin.math.roundToInt

class ExercisesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    val exercises = mutableStateListOf<Exercise>()
    var exerciseTagsMap by mutableStateOf<Map<Int, List<Tag>>>(emptyMap())
        private set

    fun loadExercises() {
        exercises.clear()
        val newTagsMap = mutableMapOf<Int, List<Tag>>()

        val cursor = dbHelper.readableDatabase.query(
            DatabaseHelper.TABLE_EXERCISES,
            null,
            null,
            null,
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            do {
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
                    )
                )

                exercises.add(exercise)
                newTagsMap[exercise.id] = getTagsByExerciseId(exercise.id)
            } while (cursor.moveToNext())
        }

        exerciseTagsMap = newTagsMap
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
        tags: List<Tag>
    ): String? {
        val trimmedName = name.trim()
        val trimmedDescription = description.trim()
        val trimmedTime = timePerRepText.trim()

        if (trimmedName.isEmpty() || trimmedTime.isEmpty()) {
            return "Please fill required fields"
        }

        val timePerRep = parseMinutesToSeconds(trimmedTime)
            ?: return "Time must be a number of minutes"

        dbHelper.insertExercise(
            name = trimmedName,
            desc = trimmedDescription,
            timePerRep = timePerRep,
            tags = tags
        )

        loadExercises()

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
        tags: List<Tag>
    ): String? {
        val trimmedName = name.trim()
        val trimmedDescription = description.trim()
        val trimmedTime = timePerRepText.trim()

        if (trimmedName.isEmpty() || trimmedTime.isEmpty()) {
            return "Please fill required fields"
        }

        val timePerRep = parseMinutesToSeconds(trimmedTime)
            ?: return "Time must be a number of minutes"

        dbHelper.updateExercise(
            id = id,
            name = trimmedName,
            desc = trimmedDescription,
            timePerRep = timePerRep,
            tags = tags
        )

        loadExercises()

        return null
    }

    private fun parseMinutesToSeconds(value: String): Int? {
        val minutes = value.toDoubleOrNull() ?: return null
        if (minutes <= 0.0) return null
        return (minutes * 60).roundToInt()
    }
}
