package com.example.train.database.helper

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.train.database.DatabaseHelper
import com.example.train.model.Tag

class ExerciseHelper(private val dbHelper: DatabaseHelper) {

    fun insertExercise(name: String, desc: String, timePerRep: Int, tags: List<Tag>): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_EXERCISE_NAME, name)
            put(DatabaseHelper.COL_EXERCISE_DESC, desc)
            put(DatabaseHelper.COL_EXERCISE_TIME_PER_REP, timePerRep)
        }
        val exerciseId = db.insert(DatabaseHelper.TABLE_EXERCISES, null, values)

        tags.forEach { tag ->
            val valueTag = ContentValues().apply {
                put(DatabaseHelper.COL_EXERCISE_ID, exerciseId)
                put(DatabaseHelper.COL_TAG_ID, tag.tagId)
            }

            db.insert(DatabaseHelper.TABLE_EXERCISE_TAGS, null, valueTag)
        }

        return exerciseId
    }

    fun getAllExercises(): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_EXERCISES,
            null,
            null,
            null,
            null,
            null,
            DatabaseHelper.COL_EXERCISE_NAME
        )
    }

    fun getExerciseById(exerciseId: Int): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_EXERCISES,
            null,
            "${DatabaseHelper.COL_EXERCISE_ID} = ?",
            arrayOf(exerciseId.toString()),
            null,
            null,
            null
        )
    }

    fun deleteExercise(exerciseId: Int) {
        val db = dbHelper.writableDatabase
        val affectedWorkoutIds = getWorkoutIdsForExercise(db, exerciseId)
        db.delete(DatabaseHelper.TABLE_EXERCISE_TAGS, "${DatabaseHelper.COL_EXERCISE_ID} = ?", arrayOf(exerciseId.toString()))
        db.delete(DatabaseHelper.TABLE_WORKOUT_EXERCISES, "${DatabaseHelper.COL_WE_EXERCISE_ID} = ?", arrayOf(exerciseId.toString()))
        db.delete(DatabaseHelper.TABLE_EXERCISES, "${DatabaseHelper.COL_EXERCISE_ID} = ?", arrayOf(exerciseId.toString()))
        affectedWorkoutIds.forEach { workoutId ->
            dbHelper.updateWorkoutDuration(workoutId, dbHelper.calculateWorkoutTotalDuration(workoutId))
        }
    }

    fun updateExercise(id: Int, name: String, desc: String, timePerRep: Int, tags: List<Tag>) {
        val db = dbHelper.writableDatabase
        val affectedWorkoutIds = getWorkoutIdsForExercise(db, id)
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_EXERCISE_NAME, name)
            put(DatabaseHelper.COL_EXERCISE_DESC, desc)
            put(DatabaseHelper.COL_EXERCISE_TIME_PER_REP, timePerRep)
        }
        db.update(DatabaseHelper.TABLE_EXERCISES, values, "${DatabaseHelper.COL_EXERCISE_ID} = ?", arrayOf(id.toString()))

        db.delete(DatabaseHelper.TABLE_EXERCISE_TAGS, "${DatabaseHelper.COL_EXERCISE_ID} = ?", arrayOf(id.toString()))
        tags.forEach { tag ->
            val valueTag = ContentValues().apply {
                put(DatabaseHelper.COL_EXERCISE_ID, id)
                put(DatabaseHelper.COL_TAG_ID, tag.tagId)
            }
            db.insert(DatabaseHelper.TABLE_EXERCISE_TAGS, null, valueTag)
        }
        affectedWorkoutIds.forEach { workoutId ->
            dbHelper.updateWorkoutDuration(workoutId, dbHelper.calculateWorkoutTotalDuration(workoutId))
        }
    }

    private fun getWorkoutIdsForExercise(db: SQLiteDatabase, exerciseId: Int): List<Long> {
        val workoutIds = mutableListOf<Long>()
        val cursor = db.query(
            DatabaseHelper.TABLE_WORKOUT_EXERCISES,
            arrayOf(DatabaseHelper.COL_WE_WORKOUT_ID),
            "${DatabaseHelper.COL_WE_EXERCISE_ID} = ?",
            arrayOf(exerciseId.toString()),
            null,
            null,
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                workoutIds.add(it.getLong(it.getColumnIndexOrThrow(DatabaseHelper.COL_WE_WORKOUT_ID)))
            }
        }

        return workoutIds
    }
}
