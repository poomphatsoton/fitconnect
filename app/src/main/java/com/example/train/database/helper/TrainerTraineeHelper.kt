package com.example.train.database.helper

import android.content.ContentValues
import android.database.Cursor
import com.example.train.database.DatabaseHelper

class TrainerTraineeHelper(private val dbHelper: DatabaseHelper) {

    fun getPendingRequest(trainerId: Int): Cursor {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT w.*
            FROM ${DatabaseHelper.TABLE_TRAINEE_REQUESTS} s
                LEFT JOIN ${DatabaseHelper.TABLE_USERS} w
                ON s.${DatabaseHelper.COL_REQUEST_TRAINEE_ID} = w.${DatabaseHelper.COL_USER_ID}
            WHERE s.${DatabaseHelper.COL_REQUEST_TRAINER_ID} = ?
            AND s.${DatabaseHelper.COL_REQUEST_STATUS} = ?
        """.trimIndent()
        return db.rawQuery(query, arrayOf(trainerId.toString(), DatabaseHelper.STATUS_PENDING))
    }

    fun getAllTraineesForTrainer(trainerId: Int): Cursor {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT u.* 
            FROM ${DatabaseHelper.TABLE_USERS} u
            JOIN ${DatabaseHelper.TABLE_TRAINER_TRAINEES} tt ON u.${DatabaseHelper.COL_USER_ID} = tt.${DatabaseHelper.COL_TT_TRAINEE_ID}
            WHERE tt.${DatabaseHelper.COL_TT_TRAINER_ID} = ?
        """.trimIndent()
        return db.rawQuery(query, arrayOf(trainerId.toString()))
    }

    fun getAllTrainers(): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_USERS,
            null,
            "${DatabaseHelper.COL_USER_ROLE} = ?",
            arrayOf("trainer"),
            null,
            null,
            DatabaseHelper.COL_USER_NAME
        )
    }

    fun getTrainerById(trainerId: Int): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_USERS,
            null,
            "${DatabaseHelper.COL_USER_ID} = ? AND ${DatabaseHelper.COL_USER_ROLE} = ?",
            arrayOf(trainerId.toString(), "trainer"),
            null,
            null,
            null
        )
    }

    fun getMyTrainerID(traineeUserId: Int): Int {
        val db = dbHelper.readableDatabase
        val query = "SELECT ${DatabaseHelper.COL_TTR_TRAINER_ID} FROM ${DatabaseHelper.TABLE_TRAINEE_TRAINER} WHERE ${DatabaseHelper.COL_TTR_TRAINEE_ID} = ?"
        val cursor = db.rawQuery(query, arrayOf(traineeUserId.toString()))
        var trainerId = -1
        if (cursor.moveToFirst()) {
            trainerId = cursor.getInt(0)
        }
        cursor.close()
        return trainerId
    }

    fun getPendingTrainerRequestId(traineeUserId: Int): Int {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT ${DatabaseHelper.COL_REQUEST_TRAINER_ID}
            FROM ${DatabaseHelper.TABLE_TRAINEE_REQUESTS}
            WHERE ${DatabaseHelper.COL_REQUEST_TRAINEE_ID} = ?
            AND ${DatabaseHelper.COL_REQUEST_STATUS} = ?
            LIMIT 1
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(traineeUserId.toString(), DatabaseHelper.STATUS_PENDING))
        val trainerId = if (cursor.moveToFirst()) {
            cursor.getInt(0)
        } else {
            -1
        }
        cursor.close()
        return trainerId
    }

    fun requestTrainer(trainerId: Int, traineeId: Int): Boolean {
        if (getMyTrainerID(traineeId) != -1 || getPendingTrainerRequestId(traineeId) != -1) {
            return false
        }

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_REQUEST_TRAINER_ID, trainerId)
            put(DatabaseHelper.COL_REQUEST_TRAINEE_ID, traineeId)
            put(DatabaseHelper.COL_REQUEST_STATUS, DatabaseHelper.STATUS_PENDING)
        }
        return db.insert(DatabaseHelper.TABLE_TRAINEE_REQUESTS, null, values) != -1L
    }

    fun cancelTrainerRequest(trainerId: Int, traineeId: Int): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_REQUEST_STATUS, DatabaseHelper.STATUS_REJECTED)
        }
        val condition = """
            ${DatabaseHelper.COL_REQUEST_TRAINER_ID} = ?
            AND ${DatabaseHelper.COL_REQUEST_TRAINEE_ID} = ?
            AND ${DatabaseHelper.COL_REQUEST_STATUS} = ?
        """.trimIndent()
        return db.update(
            DatabaseHelper.TABLE_TRAINEE_REQUESTS,
            values,
            condition,
            arrayOf(trainerId.toString(), traineeId.toString(), DatabaseHelper.STATUS_PENDING)
        ) > 0
    }

    fun unrollTrainer(trainerId: Int, traineeId: Int): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            db.beginTransaction()

            db.delete(
                DatabaseHelper.TABLE_TRAINER_TRAINEES,
                "${DatabaseHelper.COL_TT_TRAINER_ID} = ? AND ${DatabaseHelper.COL_TT_TRAINEE_ID} = ?",
                arrayOf(trainerId.toString(), traineeId.toString())
            )

            db.delete(
                DatabaseHelper.TABLE_TRAINEE_TRAINER,
                "${DatabaseHelper.COL_TTR_TRAINER_ID} = ? AND ${DatabaseHelper.COL_TTR_TRAINEE_ID} = ?",
                arrayOf(trainerId.toString(), traineeId.toString())
            )

            db.delete(
                DatabaseHelper.TABLE_TRAINEE_REQUESTS,
                "${DatabaseHelper.COL_REQUEST_TRAINER_ID} = ? AND ${DatabaseHelper.COL_REQUEST_TRAINEE_ID} = ?",
                arrayOf(trainerId.toString(), traineeId.toString())
            )

            db.delete(
                DatabaseHelper.TABLE_WORKOUT_SCHEDULES,
                "${DatabaseHelper.COL_SCHEDULE_TRAINEE_ID} = ?",
                arrayOf(traineeId.toString())
            )

            db.delete(
                DatabaseHelper.TABLE_WORKOUT_EXERCISE_COMPLETIONS,
                "${DatabaseHelper.COL_COMPLETION_SLOT_ID} IN (SELECT ${DatabaseHelper.COL_SLOT_ID} FROM ${DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT} WHERE ${DatabaseHelper.COL_TRAINEE_ID} = ?)",
                arrayOf(traineeId.toString())
            )

            db.delete(
                DatabaseHelper.TABLE_SNAPSHOT_WORKOUT,
                "${DatabaseHelper.COL_SNAPSHOT_ASSIGNMENT_ID} IN (SELECT ${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID} FROM ${DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT} WHERE ${DatabaseHelper.COL_TRAINEE_ID} = ? AND ${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID} IS NOT NULL)",
                arrayOf(traineeId.toString())
            )
            db.delete(
                DatabaseHelper.TABLE_WORKOUT_ASSIGNMENT_PROGRESS,
                "${DatabaseHelper.COL_PROGRESS_ASSIGNMENT_ID} IN (SELECT ${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID} FROM ${DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT} WHERE ${DatabaseHelper.COL_TRAINEE_ID} = ? AND ${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID} IS NOT NULL)",
                arrayOf(traineeId.toString())
            )

            val slotValues = ContentValues().apply {
                putNull(DatabaseHelper.COL_SLOT_WORKOUT_ID)
                putNull(DatabaseHelper.COL_SLOT_ASSIGNMENT_ID)
            }
            db.update(
                DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT,
                slotValues,
                "${DatabaseHelper.COL_TRAINEE_ID} = ?",
                arrayOf(traineeId.toString())
            )

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.endTransaction()
        }
    }

    fun getTrainerMaxTrainees(trainerId: Int): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_USERS,
            arrayOf(DatabaseHelper.COL_USER_MAX_TRAINEES),
            "${DatabaseHelper.COL_USER_ID} = ?",
            arrayOf(trainerId.toString()),
            null,
            null,
            null
        )

        val max = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USER_MAX_TRAINEES))
        } else {
            0
        }
        cursor.close()
        return max
    }

    fun acceptTrainee(trainerId: Int, traineeId: Int): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            db.beginTransaction()

            val requestValues = ContentValues().apply {
                put(DatabaseHelper.COL_REQUEST_STATUS, DatabaseHelper.STATUS_ACCEPTED)
            }
            db.update(
                DatabaseHelper.TABLE_TRAINEE_REQUESTS,
                requestValues,
                "${DatabaseHelper.COL_REQUEST_TRAINER_ID} = ? AND ${DatabaseHelper.COL_REQUEST_TRAINEE_ID} = ?",
                arrayOf(trainerId.toString(), traineeId.toString())
            )

            val trainerTrainee = ContentValues().apply {
                put(DatabaseHelper.COL_TT_TRAINER_ID, trainerId)
                put(DatabaseHelper.COL_TT_TRAINEE_ID, traineeId)
            }
            db.insert(DatabaseHelper.TABLE_TRAINER_TRAINEES, null, trainerTrainee)

            val traineeTrainer = ContentValues().apply {
                put(DatabaseHelper.COL_TTR_TRAINEE_ID, traineeId)
                put(DatabaseHelper.COL_TTR_TRAINER_ID, trainerId)
            }
            db.insert(DatabaseHelper.TABLE_TRAINEE_TRAINER, null, traineeTrainer)

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.endTransaction()
        }
    }

    fun denyTrainee(trainerId: Int, traineeId: Int): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            db.beginTransaction()

            val query = ContentValues().apply {
                put(DatabaseHelper.COL_REQUEST_STATUS, DatabaseHelper.STATUS_REJECTED)
            }
            val condition = "${DatabaseHelper.COL_REQUEST_TRAINER_ID} = ? AND ${DatabaseHelper.COL_REQUEST_TRAINEE_ID} = ?"
            db.update(DatabaseHelper.TABLE_TRAINEE_REQUESTS, query, condition, arrayOf(trainerId.toString(), traineeId.toString()))

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.endTransaction()
        }
    }
}
