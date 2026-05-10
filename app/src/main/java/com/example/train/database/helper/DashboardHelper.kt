package com.example.train.database.helper

import android.database.Cursor
import com.example.train.database.DatabaseHelper

class DashboardHelper(private val dbHelper: DatabaseHelper) {

    fun getActiveTraineesCount(trainerId: Int): Int {
        val db = dbHelper.readableDatabase
        val selection = "${DatabaseHelper.COL_REQUEST_TRAINER_ID} = ? AND ${DatabaseHelper.COL_REQUEST_STATUS} = 'accepted'"
        val cursor = db.query(
            DatabaseHelper.TABLE_TRAINEE_REQUESTS,
            null,
            selection,
            arrayOf(trainerId.toString()),
            null,
            null,
            null
        )
        val count = cursor.count
        cursor.close()
        return count
    }

    fun getExercisesCount(): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_EXERCISES, null, null, null, null, null, null)
        val count = cursor.count
        cursor.close()
        return count
    }

    fun getWorkoutsCount(): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.query(DatabaseHelper.TABLE_WORKOUTS, null, null, null, null, null, null)
        val count = cursor.count
        cursor.close()
        return count
    }

    fun getPendingRequestsCount(trainerId: Int): Int {
        val db = dbHelper.readableDatabase
        val selection = "${DatabaseHelper.COL_REQUEST_TRAINER_ID} = ? AND ${DatabaseHelper.COL_REQUEST_STATUS} = 'pending'"
        val cursor = db.query(
            DatabaseHelper.TABLE_TRAINEE_REQUESTS,
            null,
            selection,
            arrayOf(trainerId.toString()),
            null,
            null,
            null
        )
        val count = cursor.count
        cursor.close()
        return count
    }

    fun getTraineeDashboardWorkouts(traineeId: Int): Cursor {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT
                a.${DatabaseHelper.COL_SLOT_WORKOUT_ID},
                w.${DatabaseHelper.COL_WORKOUT_NAME},
                SUM(COALESCE(p.${DatabaseHelper.COL_PROGRESS_COMPLETED_EXERCISE_TIME}, 0)) AS completed_time,
                SUM(COALESCE(p.${DatabaseHelper.COL_PROGRESS_TOTAL_EXERCISE_TIME}, 0)) AS total_time
            FROM (
                SELECT
                    ${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID},
                    ${DatabaseHelper.COL_SLOT_WORKOUT_ID}
                FROM ${DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT}
                WHERE ${DatabaseHelper.COL_TRAINEE_ID} = ?
                AND ${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID} IS NOT NULL
                AND ${DatabaseHelper.COL_SLOT_WORKOUT_ID} IS NOT NULL
                GROUP BY ${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID}, ${DatabaseHelper.COL_SLOT_WORKOUT_ID}
            ) a
            LEFT JOIN ${DatabaseHelper.TABLE_WORKOUTS} w
                ON a.${DatabaseHelper.COL_SLOT_WORKOUT_ID} = w.${DatabaseHelper.COL_WORKOUT_ID}
            LEFT JOIN ${DatabaseHelper.TABLE_WORKOUT_ASSIGNMENT_PROGRESS} p
                ON a.${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID} = p.${DatabaseHelper.COL_PROGRESS_ASSIGNMENT_ID}
            GROUP BY a.${DatabaseHelper.COL_SLOT_WORKOUT_ID}, w.${DatabaseHelper.COL_WORKOUT_NAME}
            ORDER BY w.${DatabaseHelper.COL_WORKOUT_NAME} ASC
        """.trimIndent()
        return db.rawQuery(query, arrayOf(traineeId.toString()))
    }
}
