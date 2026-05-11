package com.example.train.database.helper

import android.content.ContentValues
import android.database.Cursor
import com.example.train.database.DatabaseHelper

class WorkoutHelper(private val dbHelper: DatabaseHelper) {

    fun insertWorkout(name: String, desc: String, duration: Int, trainerId: Int): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_WORKOUT_NAME, name)
            put(DatabaseHelper.COL_WORKOUT_DESC, desc)
            put(DatabaseHelper.COL_WORKOUT_DURATION, duration)
            put(DatabaseHelper.COL_WORKOUT_TRAINER_ID, trainerId)
        }
        return db.insert(DatabaseHelper.TABLE_WORKOUTS, null, values)
    }

    fun addExerciseToWorkout(workoutId: Long, exerciseId: Long, reps: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_WE_WORKOUT_ID, workoutId)
            put(DatabaseHelper.COL_WE_EXERCISE_ID, exerciseId)
            put(DatabaseHelper.COL_WE_REPS, reps)
        }
        db.insert(DatabaseHelper.TABLE_WORKOUT_EXERCISES, null, values)
    }

    fun getWorkoutsByTrainer(trainerId: Int): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_WORKOUTS,
            null,
            "${DatabaseHelper.COL_WORKOUT_TRAINER_ID} = ?",
            arrayOf(trainerId.toString()),
            null,
            null,
            null
        )
    }

    fun getWorkoutById(workoutId: Int): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_WORKOUTS,
            null,
            "${DatabaseHelper.COL_WORKOUT_ID} = ?",
            arrayOf(workoutId.toString()),
            null,
            null,
            null
        )
    }

    fun getWorkoutExercises(workoutId: Int): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_WORKOUT_EXERCISES,
            null,
            "${DatabaseHelper.COL_WE_WORKOUT_ID} = ?",
            arrayOf(workoutId.toString()),
            null,
            null,
            null
        )
    }

    fun replaceWorkoutExercises(workoutId: Int, name: String, desc: String, exercises: List<Pair<Long, Int>>) {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val values = ContentValues().apply {
                put(DatabaseHelper.COL_WORKOUT_NAME, name)
                put(DatabaseHelper.COL_WORKOUT_DESC, desc)
            }
            db.update(DatabaseHelper.TABLE_WORKOUTS, values, "${DatabaseHelper.COL_WORKOUT_ID} = ?", arrayOf(workoutId.toString()))

            db.delete(DatabaseHelper.TABLE_WORKOUT_EXERCISES, "${DatabaseHelper.COL_WE_WORKOUT_ID} = ?", arrayOf(workoutId.toString()))

            exercises.forEach { (exerciseId, reps) ->
                val exerciseValues = ContentValues().apply {
                    put(DatabaseHelper.COL_WE_WORKOUT_ID, workoutId)
                    put(DatabaseHelper.COL_WE_EXERCISE_ID, exerciseId)
                    put(DatabaseHelper.COL_WE_REPS, reps)
                }
                db.insert(DatabaseHelper.TABLE_WORKOUT_EXERCISES, null, exerciseValues)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun calculateWorkoutTotalDuration(workoutId: Long): Int {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT SUM(e.${DatabaseHelper.COL_EXERCISE_TIME_PER_REP} * we.${DatabaseHelper.COL_WE_REPS})
            FROM ${DatabaseHelper.TABLE_WORKOUT_EXERCISES} we
            JOIN ${DatabaseHelper.TABLE_EXERCISES} e ON we.${DatabaseHelper.COL_WE_EXERCISE_ID} = e.${DatabaseHelper.COL_EXERCISE_ID}
            WHERE we.${DatabaseHelper.COL_WE_WORKOUT_ID} = ?
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(workoutId.toString()))
        var total = 0
        if (cursor.moveToFirst()) total = cursor.getInt(0)
        cursor.close()
        return total
    }

    fun updateWorkoutDuration(workoutId: Long, duration: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_WORKOUT_DURATION, duration)
        }
        db.update(DatabaseHelper.TABLE_WORKOUTS, values, "${DatabaseHelper.COL_WORKOUT_ID}=?", arrayOf(workoutId.toString()))
    }

    fun getWorkoutExerciseDetails(workoutId: Long): Cursor {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT e.${DatabaseHelper.COL_EXERCISE_ID}, e.${DatabaseHelper.COL_EXERCISE_NAME}, we.${DatabaseHelper.COL_WE_REPS}, e.${DatabaseHelper.COL_EXERCISE_TIME_PER_REP}, e.${DatabaseHelper.COL_EXERCISE_DESC}, e.${DatabaseHelper.COL_EXERCISE_VIDEO_URL}, e.${DatabaseHelper.COL_EXERCISE_VIDEO_NAME}
            FROM ${DatabaseHelper.TABLE_WORKOUT_EXERCISES} we
            JOIN ${DatabaseHelper.TABLE_EXERCISES} e ON we.${DatabaseHelper.COL_WE_EXERCISE_ID} = e.${DatabaseHelper.COL_EXERCISE_ID}
            WHERE we.${DatabaseHelper.COL_WE_WORKOUT_ID} = ?
        """.trimIndent()
        return db.rawQuery(query, arrayOf(workoutId.toString()))
    }

    fun getWorkoutExerciseTagTimes(workoutId: Long): Cursor {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT
                e.${DatabaseHelper.COL_EXERCISE_ID},
                (we.${DatabaseHelper.COL_WE_REPS} * e.${DatabaseHelper.COL_EXERCISE_TIME_PER_REP}) AS exercise_time,
                t.${DatabaseHelper.COL_TAG_NAME}
            FROM ${DatabaseHelper.TABLE_WORKOUT_EXERCISES} we
            JOIN ${DatabaseHelper.TABLE_EXERCISES} e
                ON we.${DatabaseHelper.COL_WE_EXERCISE_ID} = e.${DatabaseHelper.COL_EXERCISE_ID}
            JOIN ${DatabaseHelper.TABLE_EXERCISE_TAGS} et
                ON e.${DatabaseHelper.COL_EXERCISE_ID} = et.${DatabaseHelper.COL_EXERCISE_ID}
            JOIN ${DatabaseHelper.TABLE_TAGS} t
                ON et.${DatabaseHelper.COL_TAG_ID} = t.${DatabaseHelper.COL_TAG_ID}
            WHERE we.${DatabaseHelper.COL_WE_WORKOUT_ID} = ?
        """.trimIndent()
        return db.rawQuery(query, arrayOf(workoutId.toString()))
    }

    fun getWorkoutOptionsByTrainer(trainerId: Int): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_WORKOUTS,
            arrayOf(
                DatabaseHelper.COL_WORKOUT_ID,
                DatabaseHelper.COL_WORKOUT_NAME,
                DatabaseHelper.COL_WORKOUT_DURATION
            ),
            "${DatabaseHelper.COL_WORKOUT_TRAINER_ID} = ?",
            arrayOf(trainerId.toString()),
            null,
            null,
            DatabaseHelper.COL_WORKOUT_NAME
        )
    }

    fun deleteWorkout(workoutId: Int) {
        val db = dbHelper.writableDatabase
        db.delete(DatabaseHelper.TABLE_WORKOUT_EXERCISES, "${DatabaseHelper.COL_WE_WORKOUT_ID} = ?", arrayOf(workoutId.toString()))
        db.delete(DatabaseHelper.TABLE_WORKOUT_SCHEDULES, "${DatabaseHelper.COL_SCHEDULE_WORKOUT_ID} = ?", arrayOf(workoutId.toString()))
        db.delete(DatabaseHelper.TABLE_WORKOUTS, "${DatabaseHelper.COL_WORKOUT_ID} = ?", arrayOf(workoutId.toString()))
    }
}
