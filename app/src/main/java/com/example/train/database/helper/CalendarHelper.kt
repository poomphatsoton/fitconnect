package com.example.train.database.helper

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.example.train.database.DatabaseHelper
import java.time.LocalDate

class CalendarHelper(private val dbHelper: DatabaseHelper) {

    fun getSnapshotWorkoutExerciseDetails(assignmentId: Int): Cursor {
        val db = dbHelper.readableDatabase
        val query = """
            SELECT
                ${DatabaseHelper.COL_SNAPSHOT_EXERCISE_ID} AS ${DatabaseHelper.COL_EXERCISE_ID},
                ${DatabaseHelper.COL_SNAPSHOT_EXERCISE_NAME} AS ${DatabaseHelper.COL_EXERCISE_NAME},
                ${DatabaseHelper.COL_SNAPSHOT_REPS} AS ${DatabaseHelper.COL_WE_REPS},
                ${DatabaseHelper.COL_SNAPSHOT_TIME_PER_REP} AS ${DatabaseHelper.COL_EXERCISE_TIME_PER_REP},
                ${DatabaseHelper.COL_SNAPSHOT_EXERCISE_DESC} AS ${DatabaseHelper.COL_EXERCISE_DESC}
            FROM ${DatabaseHelper.TABLE_SNAPSHOT_WORKOUT}
            WHERE ${DatabaseHelper.COL_SNAPSHOT_ASSIGNMENT_ID} = ?
            ORDER BY ${DatabaseHelper.COL_SNAPSHOT_ID} ASC
        """.trimIndent()
        return db.rawQuery(query, arrayOf(assignmentId.toString()))
    }

    fun getTraineeSlots(traineeId: Int, date: LocalDate): Cursor {
        val db = dbHelper.readableDatabase

        val query = """
            SELECT s.*, w.${DatabaseHelper.COL_WORKOUT_NAME}
            FROM ${DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT} s
            LEFT JOIN ${DatabaseHelper.TABLE_WORKOUTS} w 
                ON s.${DatabaseHelper.COL_SLOT_WORKOUT_ID} = w.${DatabaseHelper.COL_WORKOUT_ID}
            WHERE s.${DatabaseHelper.COL_TRAINEE_ID} = ?
            AND s.${DatabaseHelper.COL_SLOT_DATE} = ?
            ORDER BY s.${DatabaseHelper.COL_SLOT_START_TIME} ASC, s.${DatabaseHelper.COL_SLOT_END_TIME} ASC
        """.trimIndent()

        return db.rawQuery(query, arrayOf(traineeId.toString(), date.toString()))
    }

    fun assignWorkoutToTraineeSlots(slotIds: List<Int>, workoutId: Int): Boolean {
        if (slotIds.isEmpty()) return false

        val db = dbHelper.writableDatabase
        return try {
            db.beginTransaction()
            val assignmentId = getNextAssignmentId(db)
            createSnapshotWorkout(db, assignmentId, workoutId)
            createWorkoutAssignmentProgress(db, assignmentId, workoutId)
            slotIds.forEach { slotId ->
                val values = ContentValues().apply {
                    put(DatabaseHelper.COL_SLOT_WORKOUT_ID, workoutId)
                    put(DatabaseHelper.COL_SLOT_ASSIGNMENT_ID, assignmentId)
                }
                db.update(
                    DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT,
                    values,
                    "${DatabaseHelper.COL_SLOT_ID} = ?",
                    arrayOf(slotId.toString())
                )
            }
            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.endTransaction()
        }
    }

    fun replaceWorkoutOnTraineeSlots(oldSlotIds: List<Int>, newSlotIds: List<Int>, workoutId: Int): Boolean {
        if (oldSlotIds.isEmpty() || newSlotIds.isEmpty()) return false

        val db = dbHelper.writableDatabase
        return try {
            db.beginTransaction()
            val assignmentId = getAssignmentIdForSlots(db, oldSlotIds) ?: getNextAssignmentId(db)
            deleteSnapshotsForAssignments(db, listOf(assignmentId))
            createSnapshotWorkout(db, assignmentId, workoutId)
            createWorkoutAssignmentProgress(db, assignmentId, workoutId)

            oldSlotIds.forEach { slotId ->
                val values = ContentValues().apply {
                    putNull(DatabaseHelper.COL_SLOT_WORKOUT_ID)
                    putNull(DatabaseHelper.COL_SLOT_ASSIGNMENT_ID)
                }
                db.update(DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT, values, "${DatabaseHelper.COL_SLOT_ID} = ?", arrayOf(slotId.toString()))
            }

            newSlotIds.forEach { slotId ->
                val values = ContentValues().apply {
                    put(DatabaseHelper.COL_SLOT_WORKOUT_ID, workoutId)
                    put(DatabaseHelper.COL_SLOT_ASSIGNMENT_ID, assignmentId)
                }
                db.update(DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT, values, "${DatabaseHelper.COL_SLOT_ID} = ?", arrayOf(slotId.toString()))
            }

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.endTransaction()
        }
    }

    fun clearWorkoutFromTraineeSlots(slotIds: List<Int>): Boolean {
        if (slotIds.isEmpty()) return false

        val db = dbHelper.writableDatabase
        return try {
            db.beginTransaction()
            val assignmentIds = getAssignmentIdsForSlots(db, slotIds)
            slotIds.forEach { slotId ->
                val values = ContentValues().apply {
                    putNull(DatabaseHelper.COL_SLOT_WORKOUT_ID)
                    putNull(DatabaseHelper.COL_SLOT_ASSIGNMENT_ID)
                }
                db.update(DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT, values, "${DatabaseHelper.COL_SLOT_ID} = ?", arrayOf(slotId.toString()))
            }
            deleteSnapshotsForAssignments(db, assignmentIds)
            deleteProgressForAssignments(db, assignmentIds)
            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.endTransaction()
        }
    }

    fun addTraineeCalendarSlot(
        traineeId: Int,
        date: LocalDate,
        startTime: String,
        endTime: String,
        status: Int
    ): Boolean {
        if (hasTraineeSlotAtTime(traineeId, date, startTime, endTime)) return false

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_TRAINEE_ID, traineeId)
            put(DatabaseHelper.COL_SLOT_DATE, date.toString())
            put(DatabaseHelper.COL_SLOT_START_TIME, startTime)
            put(DatabaseHelper.COL_SLOT_END_TIME, endTime)
            put(DatabaseHelper.COL_SLOT_STATUS, status)
        }
        return db.insert(DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT, null, values) != -1L
    }

    fun updateTraineeCalendarSlot(
        slotId: Int,
        traineeId: Int,
        date: LocalDate,
        startTime: String,
        endTime: String,
        status: Int
    ): Boolean {
        if (hasTraineeSlotAtTime(traineeId, date, startTime, endTime, slotId)) return false

        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_SLOT_DATE, date.toString())
            put(DatabaseHelper.COL_SLOT_START_TIME, startTime)
            put(DatabaseHelper.COL_SLOT_END_TIME, endTime)
            put(DatabaseHelper.COL_SLOT_STATUS, status)
        }
        return db.update(
            DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT,
            values,
            "${DatabaseHelper.COL_SLOT_ID} = ? AND ${DatabaseHelper.COL_TRAINEE_ID} = ?",
            arrayOf(slotId.toString(), traineeId.toString())
        ) > 0
    }

    fun deleteTraineeCalendarSlot(slotId: Int): Boolean {
        val db = dbHelper.writableDatabase
        val assignmentIds = getAssignmentIdsForSlots(db, listOf(slotId))
        val deleted = db.delete(DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT, "${DatabaseHelper.COL_SLOT_ID} = ?", arrayOf(slotId.toString())) > 0
        deleteSnapshotsForAssignments(db, assignmentIds)
        deleteProgressForAssignments(db, assignmentIds)
        return deleted
    }

    fun getCompletedExerciseIdsForSlot(slotId: Int): Cursor {
        val db = dbHelper.readableDatabase
        return db.query(
            DatabaseHelper.TABLE_WORKOUT_EXERCISE_COMPLETIONS,
            arrayOf(DatabaseHelper.COL_COMPLETION_EXERCISE_ID),
            "${DatabaseHelper.COL_COMPLETION_SLOT_ID} = ?",
            arrayOf(slotId.toString()),
            null,
            null,
            null
        )
    }

    fun markWorkoutExerciseComplete(slotId: Int, exerciseId: Long): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            db.beginTransaction()

            val assignmentId = getAssignmentIdForSlots(db, listOf(slotId))
            val shouldUpdateProgress = assignmentId == null ||
                !hasCompletedExerciseForAssignment(db, assignmentId, exerciseId)

            val values = ContentValues().apply {
                put(DatabaseHelper.COL_COMPLETION_SLOT_ID, slotId)
                put(DatabaseHelper.COL_COMPLETION_EXERCISE_ID, exerciseId)
                put(DatabaseHelper.COL_COMPLETION_COMPLETED_AT, System.currentTimeMillis().toString())
            }
            val isInserted = db.insertWithOnConflict(
                DatabaseHelper.TABLE_WORKOUT_EXERCISE_COMPLETIONS,
                null,
                values,
                SQLiteDatabase.CONFLICT_IGNORE
            ) != -1L

            if (isInserted && shouldUpdateProgress && assignmentId != null) {
                addCompletedExerciseTime(db, assignmentId, exerciseId)
            }

            db.setTransactionSuccessful()
            isInserted
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.endTransaction()
        }
    }

    private fun getNextAssignmentId(db: SQLiteDatabase): Int {
        val cursor = db.rawQuery(
            "SELECT COALESCE(MAX(${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID}), 0) + 1 FROM ${DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT}",
            null
        )
        val assignmentId = if (cursor.moveToFirst()) cursor.getInt(0) else 1
        cursor.close()
        return assignmentId
    }

    private fun getAssignmentIdForSlots(db: SQLiteDatabase, slotIds: List<Int>): Int? {
        if (slotIds.isEmpty()) return null
        val placeholders = slotIds.joinToString(",") { "?" }
        val cursor = db.rawQuery(
            """
                SELECT ${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID}
                FROM ${DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT}
                WHERE ${DatabaseHelper.COL_SLOT_ID} IN ($placeholders)
                AND ${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID} IS NOT NULL
                LIMIT 1
            """.trimIndent(),
            slotIds.map { it.toString() }.toTypedArray()
        )
        val assignmentId = if (cursor.moveToFirst()) cursor.getInt(0) else null
        cursor.close()
        return assignmentId
    }

    private fun getAssignmentIdsForSlots(db: SQLiteDatabase, slotIds: List<Int>): List<Int> {
        if (slotIds.isEmpty()) return emptyList()
        val placeholders = slotIds.joinToString(",") { "?" }
        val assignmentIds = mutableListOf<Int>()
        val cursor = db.rawQuery(
            """
                SELECT DISTINCT ${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID}
                FROM ${DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT}
                WHERE ${DatabaseHelper.COL_SLOT_ID} IN ($placeholders)
                AND ${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID} IS NOT NULL
            """.trimIndent(),
            slotIds.map { it.toString() }.toTypedArray()
        )
        cursor.use {
            while (it.moveToNext()) {
                assignmentIds.add(it.getInt(0))
            }
        }
        return assignmentIds
    }

    private fun createSnapshotWorkout(db: SQLiteDatabase, assignmentId: Int, workoutId: Int) {
        db.delete(DatabaseHelper.TABLE_SNAPSHOT_WORKOUT, "${DatabaseHelper.COL_SNAPSHOT_ASSIGNMENT_ID} = ?", arrayOf(assignmentId.toString()))
        val query = """
            SELECT e.${DatabaseHelper.COL_EXERCISE_ID}, e.${DatabaseHelper.COL_EXERCISE_NAME}, e.${DatabaseHelper.COL_EXERCISE_DESC},
                   e.${DatabaseHelper.COL_EXERCISE_TIME_PER_REP}, we.${DatabaseHelper.COL_WE_REPS},
                   (e.${DatabaseHelper.COL_EXERCISE_TIME_PER_REP} * we.${DatabaseHelper.COL_WE_REPS}) AS exercise_total_time
            FROM ${DatabaseHelper.TABLE_WORKOUT_EXERCISES} we
            JOIN ${DatabaseHelper.TABLE_EXERCISES} e
                ON we.${DatabaseHelper.COL_WE_EXERCISE_ID} = e.${DatabaseHelper.COL_EXERCISE_ID}
            WHERE we.${DatabaseHelper.COL_WE_WORKOUT_ID} = ?
        """.trimIndent()
        db.rawQuery(query, arrayOf(workoutId.toString())).use { cursor ->
            while (cursor.moveToNext()) {
                val values = ContentValues().apply {
                    put(DatabaseHelper.COL_SNAPSHOT_ASSIGNMENT_ID, assignmentId)
                    put(DatabaseHelper.COL_SNAPSHOT_WORKOUT_ID, workoutId)
                    put(DatabaseHelper.COL_SNAPSHOT_EXERCISE_ID, cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_ID)))
                    put(DatabaseHelper.COL_SNAPSHOT_EXERCISE_NAME, cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_NAME)))
                    put(DatabaseHelper.COL_SNAPSHOT_EXERCISE_DESC, cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_DESC)))
                    put(DatabaseHelper.COL_SNAPSHOT_TIME_PER_REP, cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_TIME_PER_REP)))
                    put(DatabaseHelper.COL_SNAPSHOT_REPS, cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WE_REPS)))
                    put(DatabaseHelper.COL_SNAPSHOT_EXERCISE_TOTAL_TIME, cursor.getInt(cursor.getColumnIndexOrThrow("exercise_total_time")))
                }
                db.insert(DatabaseHelper.TABLE_SNAPSHOT_WORKOUT, null, values)
            }
        }
    }

    private fun createWorkoutAssignmentProgress(db: SQLiteDatabase, assignmentId: Int, workoutId: Int) {
        val totalTime = getSnapshotTotalExerciseTime(db, assignmentId)
        val values = ContentValues().apply {
            put(DatabaseHelper.COL_PROGRESS_ASSIGNMENT_ID, assignmentId)
            put(DatabaseHelper.COL_PROGRESS_WORKOUT_ID, workoutId)
            put(DatabaseHelper.COL_PROGRESS_COMPLETED_EXERCISE_TIME, 0)
            put(DatabaseHelper.COL_PROGRESS_TOTAL_EXERCISE_TIME, totalTime)
        }
        db.insertWithOnConflict(
            DatabaseHelper.TABLE_WORKOUT_ASSIGNMENT_PROGRESS,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
    }

    private fun getSnapshotTotalExerciseTime(db: SQLiteDatabase, assignmentId: Int): Int {
        val cursor = db.query(
            DatabaseHelper.TABLE_SNAPSHOT_WORKOUT,
            arrayOf("SUM(${DatabaseHelper.COL_SNAPSHOT_EXERCISE_TOTAL_TIME})"),
            "${DatabaseHelper.COL_SNAPSHOT_ASSIGNMENT_ID} = ?",
            arrayOf(assignmentId.toString()),
            null,
            null,
            null
        )
        val total = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        return total
    }

    private fun deleteSnapshotsForAssignments(db: SQLiteDatabase, assignmentIds: List<Int>) {
        if (assignmentIds.isEmpty()) return
        val placeholders = assignmentIds.joinToString(",") { "?" }
        db.delete(
            DatabaseHelper.TABLE_SNAPSHOT_WORKOUT,
            "${DatabaseHelper.COL_SNAPSHOT_ASSIGNMENT_ID} IN ($placeholders)",
            assignmentIds.map { it.toString() }.toTypedArray()
        )
    }

    private fun deleteProgressForAssignments(db: SQLiteDatabase, assignmentIds: List<Int>) {
        if (assignmentIds.isEmpty()) return
        val placeholders = assignmentIds.joinToString(",") { "?" }
        db.delete(
            DatabaseHelper.TABLE_WORKOUT_ASSIGNMENT_PROGRESS,
            "${DatabaseHelper.COL_PROGRESS_ASSIGNMENT_ID} IN ($placeholders)",
            assignmentIds.map { it.toString() }.toTypedArray()
        )
    }

    private fun hasTraineeSlotAtTime(
        traineeId: Int,
        date: LocalDate,
        startTime: String,
        endTime: String,
        excludedSlotId: Int? = null
    ): Boolean {
        val db = dbHelper.readableDatabase
        val selection = buildString {
            append("${DatabaseHelper.COL_TRAINEE_ID} = ? AND ${DatabaseHelper.COL_SLOT_DATE} = ? AND ${DatabaseHelper.COL_SLOT_START_TIME} = ? AND ${DatabaseHelper.COL_SLOT_END_TIME} = ?")
            if (excludedSlotId != null) {
                append(" AND ${DatabaseHelper.COL_SLOT_ID} != ?")
            }
        }
        val args = mutableListOf(traineeId.toString(), date.toString(), startTime, endTime)
        if (excludedSlotId != null) {
            args.add(excludedSlotId.toString())
        }
        val cursor = db.query(
            DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT,
            arrayOf(DatabaseHelper.COL_SLOT_ID),
            selection,
            args.toTypedArray(),
            null,
            null,
            null
        )
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    private fun hasCompletedExerciseForAssignment(
        db: SQLiteDatabase,
        assignmentId: Int,
        exerciseId: Long
    ): Boolean {
        val query = """
            SELECT c.${DatabaseHelper.COL_COMPLETION_EXERCISE_ID}
            FROM ${DatabaseHelper.TABLE_WORKOUT_EXERCISE_COMPLETIONS} c
            JOIN ${DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT} s
                ON c.${DatabaseHelper.COL_COMPLETION_SLOT_ID} = s.${DatabaseHelper.COL_SLOT_ID}
            WHERE s.${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID} = ?
            AND c.${DatabaseHelper.COL_COMPLETION_EXERCISE_ID} = ?
            LIMIT 1
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(assignmentId.toString(), exerciseId.toString()))
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    private fun addCompletedExerciseTime(
        db: SQLiteDatabase,
        assignmentId: Int,
        exerciseId: Long
    ) {
        val cursor = db.query(
            DatabaseHelper.TABLE_SNAPSHOT_WORKOUT,
            arrayOf(DatabaseHelper.COL_SNAPSHOT_EXERCISE_TOTAL_TIME),
            "${DatabaseHelper.COL_SNAPSHOT_ASSIGNMENT_ID} = ? AND ${DatabaseHelper.COL_SNAPSHOT_EXERCISE_ID} = ?",
            arrayOf(assignmentId.toString(), exerciseId.toString()),
            null,
            null,
            null
        )
        val exerciseTime = if (cursor.moveToFirst()) cursor.getInt(0) else 0
        cursor.close()
        if (exerciseTime <= 0) return

        db.execSQL(
            """
                UPDATE ${DatabaseHelper.TABLE_WORKOUT_ASSIGNMENT_PROGRESS}
                SET ${DatabaseHelper.COL_PROGRESS_COMPLETED_EXERCISE_TIME} =
                    MIN(${DatabaseHelper.COL_PROGRESS_COMPLETED_EXERCISE_TIME} + ?, ${DatabaseHelper.COL_PROGRESS_TOTAL_EXERCISE_TIME})
                WHERE ${DatabaseHelper.COL_PROGRESS_ASSIGNMENT_ID} = ?
            """.trimIndent(),
            arrayOf(exerciseTime, assignmentId)
        )
    }
}
