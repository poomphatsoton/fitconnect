package com.example.train.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.model.Exercise

class ExercisesViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    val exercises = mutableStateListOf<Exercise>()

    fun loadExercises() {
        exercises.clear()

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
                    category1 = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_CATEGORY1)
                    ),
                    category2 = cursor.getString(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_CATEGORY2)
                    ),
                    timePerRep = cursor.getInt(
                        cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_TIME_PER_REP)
                    )
                )

                exercises.add(exercise)
            } while (cursor.moveToNext())
        }

        cursor.close()
    }

    fun createExercise(
        name: String,
        description: String,
        category1: String,
        category2: String,
        timePerRepText: String
    ): String? {
        val trimmedName = name.trim()
        val trimmedDescription = description.trim()
        val trimmedCategory1 = category1.trim()
        val trimmedCategory2 = category2.trim()
        val trimmedTime = timePerRepText.trim()

        if (trimmedName.isEmpty() || trimmedTime.isEmpty()) {
            return "Please fill required fields"
        }

        val timePerRep = trimmedTime.toIntOrNull()
            ?: return "Time must be a number"

        dbHelper.insertExercise(
            name = trimmedName,
            desc = trimmedDescription,
            category1 = trimmedCategory1,
            category2 = trimmedCategory2,
            timePerRep = timePerRep
        )

        loadExercises()

        return null
    }
}