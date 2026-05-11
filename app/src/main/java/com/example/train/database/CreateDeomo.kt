package com.example.train.database

import android.database.sqlite.SQLiteDatabase
import com.example.train.security.PasswordHasher
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object CreateDeomo {

    fun insertDemoData(db: SQLiteDatabase) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val now = System.currentTimeMillis().toString()

        fun insertTag(name: String) {
            db.execSQL(
                """
                INSERT INTO ${DatabaseHelper.TABLE_TAGS}
                (${DatabaseHelper.COL_TAG_NAME})
                VALUES ('$name')
                """.trimIndent()
            )
        }

        fun insertTrainer(
            username: String,
            name: String,
            bio: String,
            maxTrainees: Int
        ) {
            db.execSQL(
                """
                INSERT INTO ${DatabaseHelper.TABLE_USERS}
                (
                    ${DatabaseHelper.COL_USER_USERNAME},
                    ${DatabaseHelper.COL_USER_PASSWORD},
                    ${DatabaseHelper.COL_USER_ROLE},
                    ${DatabaseHelper.COL_USER_NAME},
                    ${DatabaseHelper.COL_USER_BIO},
                    ${DatabaseHelper.COL_USER_MAX_TRAINEES}
                )
                VALUES
                (
                    '$username',
                    '${PasswordHasher.hash("trainer123")}',
                    'trainer',
                    '$name',
                    '$bio',
                    $maxTrainees
                )
                """.trimIndent()
            )
        }

        fun insertTrainee(
            username: String,
            password: String,
            name: String,
            bio: String
        ) {
            db.execSQL(
                """
                INSERT INTO ${DatabaseHelper.TABLE_USERS}
                (
                    ${DatabaseHelper.COL_USER_USERNAME},
                    ${DatabaseHelper.COL_USER_PASSWORD},
                    ${DatabaseHelper.COL_USER_ROLE},
                    ${DatabaseHelper.COL_USER_NAME},
                    ${DatabaseHelper.COL_USER_BIO}
                )
                VALUES
                (
                    '$username',
                    '${PasswordHasher.hash(password)}',
                    'trainee',
                    '$name',
                    '$bio'
                )
                """.trimIndent()
            )
        }

        fun insertUserTag(userId: Int, tagId: Int) {
            db.execSQL(
                """
                INSERT INTO ${DatabaseHelper.TABLE_USERS_TAGS}
                (
                    ${DatabaseHelper.COL_USER_ID},
                    ${DatabaseHelper.COL_TAG_ID}
                )
                VALUES ($userId, $tagId)
                """.trimIndent()
            )
        }

        fun insertExercise(
            name: String,
            description: String,
            timePerRep: Int,
            trainerId: Int
        ) {
            db.execSQL(
                """
                INSERT INTO ${DatabaseHelper.TABLE_EXERCISES}
                (
                    ${DatabaseHelper.COL_EXERCISE_NAME},
                    ${DatabaseHelper.COL_EXERCISE_DESC},
                    ${DatabaseHelper.COL_EXERCISE_TIME_PER_REP},
                    ${DatabaseHelper.COL_EXERCISE_TRAINER_ID}
                )
                VALUES
                (
                    '$name',
                    '$description',
                    $timePerRep,
                    $trainerId
                )
                """.trimIndent()
            )
        }

        fun insertExerciseTag(exerciseId: Int, tagId: Int) {
            db.execSQL(
                """
                INSERT INTO ${DatabaseHelper.TABLE_EXERCISE_TAGS}
                (
                    ${DatabaseHelper.COL_EXERCISE_ID},
                    ${DatabaseHelper.COL_TAG_ID}
                )
                VALUES ($exerciseId, $tagId)
                """.trimIndent()
            )
        }

        fun insertWorkout(
            name: String,
            description: String,
            duration: Int,
            trainerId: Int
        ) {
            db.execSQL(
                """
                INSERT INTO ${DatabaseHelper.TABLE_WORKOUTS}
                (
                    ${DatabaseHelper.COL_WORKOUT_NAME},
                    ${DatabaseHelper.COL_WORKOUT_DESC},
                    ${DatabaseHelper.COL_WORKOUT_DURATION},
                    ${DatabaseHelper.COL_WORKOUT_TRAINER_ID}
                )
                VALUES
                (
                    '$name',
                    '$description',
                    $duration,
                    $trainerId
                )
                """.trimIndent()
            )
        }

        fun insertWorkoutExercise(
            workoutId: Int,
            exerciseId: Int,
            reps: Int
        ) {
            db.execSQL(
                """
                INSERT INTO ${DatabaseHelper.TABLE_WORKOUT_EXERCISES}
                (
                    ${DatabaseHelper.COL_WE_WORKOUT_ID},
                    ${DatabaseHelper.COL_WE_EXERCISE_ID},
                    ${DatabaseHelper.COL_WE_REPS}
                )
                VALUES ($workoutId, $exerciseId, $reps)
                """.trimIndent()
            )
        }

        fun insertRequest(
            trainerId: Int,
            traineeId: Int,
            status: String
        ) {
            db.execSQL(
                """
                INSERT INTO ${DatabaseHelper.TABLE_TRAINEE_REQUESTS}
                (
                    ${DatabaseHelper.COL_REQUEST_TRAINER_ID},
                    ${DatabaseHelper.COL_REQUEST_TRAINEE_ID},
                    ${DatabaseHelper.COL_REQUEST_STATUS}
                )
                VALUES ($trainerId, $traineeId, '$status')
                """.trimIndent()
            )
        }

        fun insertTrainerTrainee(
            trainerId: Int,
            traineeId: Int
        ) {
            db.execSQL(
                """
                INSERT INTO ${DatabaseHelper.TABLE_TRAINER_TRAINEES}
                (
                    ${DatabaseHelper.COL_TT_TRAINER_ID},
                    ${DatabaseHelper.COL_TT_TRAINEE_ID}
                )
                VALUES ($trainerId, $traineeId)
                """.trimIndent()
            )
        }

        fun insertTraineeTrainer(
            traineeId: Int,
            trainerId: Int
        ) {
            db.execSQL(
                """
                INSERT INTO ${DatabaseHelper.TABLE_TRAINEE_TRAINER}
                (
                    ${DatabaseHelper.COL_TTR_TRAINEE_ID},
                    ${DatabaseHelper.COL_TTR_TRAINER_ID}
                )
                VALUES ($traineeId, $trainerId)
                """.trimIndent()
            )
        }

        fun insertSlot(
            traineeId: Int,
            workoutId: Int?,
            assignmentId: Int?,
            status: Int,
            startTime: String,
            endTime: String
        ) {
            val workoutValue = workoutId?.toString() ?: "NULL"
            val assignmentValue = assignmentId?.toString() ?: "NULL"

            db.execSQL(
                """
                INSERT INTO ${DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT}
                (
                    ${DatabaseHelper.COL_TRAINEE_ID},
                    ${DatabaseHelper.COL_SLOT_WORKOUT_ID},
                    ${DatabaseHelper.COL_SLOT_ASSIGNMENT_ID},
                    ${DatabaseHelper.COL_SLOT_STATUS},
                    ${DatabaseHelper.COL_SLOT_START_TIME},
                    ${DatabaseHelper.COL_SLOT_END_TIME},
                    ${DatabaseHelper.COL_SLOT_DATE}
                )
                VALUES
                (
                    $traineeId,
                    $workoutValue,
                    $assignmentValue,
                    $status,
                    '$startTime',
                    '$endTime',
                    '$today'
                )
                """.trimIndent()
            )
        }

        fun insertCompletion(
            traineeId: Int,
            startTime: String,
            exerciseId: Int
        ) {
            db.execSQL(
                """
                INSERT INTO ${DatabaseHelper.TABLE_WORKOUT_EXERCISE_COMPLETIONS}
                (
                    ${DatabaseHelper.COL_COMPLETION_SLOT_ID},
                    ${DatabaseHelper.COL_COMPLETION_EXERCISE_ID},
                    ${DatabaseHelper.COL_COMPLETION_COMPLETED_AT}
                )
                SELECT
                    ${DatabaseHelper.COL_SLOT_ID},
                    $exerciseId,
                    '$now'
                FROM ${DatabaseHelper.TABLE_TRAINEE_CALENDAR_SLOT}
                WHERE ${DatabaseHelper.COL_TRAINEE_ID} = $traineeId
                AND ${DatabaseHelper.COL_SLOT_DATE} = '$today'
                AND ${DatabaseHelper.COL_SLOT_START_TIME} = '$startTime'
                """.trimIndent()
            )
        }

        // ─── TAGS ───────────────────────────────────────────────
        for (i in 1..10) {
            insertTag("tag$i")
        }

        // ─── USERS ───────────────────────────────────────────────
        insertTrainer("trainer1", "trainer1", "bio1", 10)
        insertTrainer("trainer2", "trainer2", "bio2", 8)
        insertTrainer("trainer3", "trainer3", "bio3", 12)

        insertTrainee("trainee1", "pass123", "trainee1", "bio4")
        insertTrainee("trainee2", "pass123", "trainee2", "bio5")
        insertTrainee("trainee3", "pass123", "trainee3", "bio6")
        insertTrainee("trainee4", "pass123", "trainee4", "bio7")
        insertTrainee("trainee5", "pass123", "trainee5", "bio8")
        insertTrainee("trainee6", "pass123", "trainee6", "bio9")

        // Test login account
        insertTrainee("trainee", "123456", "trainee", "bio10")

        // ─── USER TAGS ───────────────────────────────────────────
        insertUserTag(1, 1)
        insertUserTag(1, 5)
        insertUserTag(1, 3)

        insertUserTag(2, 6)
        insertUserTag(2, 9)
        insertUserTag(2, 10)

        insertUserTag(3, 8)
        insertUserTag(3, 2)
        insertUserTag(3, 4)

        insertUserTag(4, 1)
        insertUserTag(4, 4)
        insertUserTag(4, 7)

        insertUserTag(5, 5)
        insertUserTag(5, 1)
        insertUserTag(5, 3)

        insertUserTag(6, 4)
        insertUserTag(6, 2)
        insertUserTag(6, 8)

        insertUserTag(7, 3)
        insertUserTag(7, 2)

        insertUserTag(8, 8)
        insertUserTag(8, 6)

        insertUserTag(9, 5)
        insertUserTag(9, 7)

        insertUserTag(10, 2)
        insertUserTag(10, 4)

        // ─── EXERCISES ───────────────────────────────────────────
        // trainer1 (id=1) owns exercises 1-7
        insertExercise("exercise1", "description1", 60, trainerId = 1)
        insertExercise("exercise2", "description2", 60, trainerId = 1)
        insertExercise("exercise3", "description3", 120, trainerId = 1)
        insertExercise("exercise4", "description4", 60, trainerId = 1)
        insertExercise("exercise5", "description5", 120, trainerId = 1)
        insertExercise("exercise6", "description6", 60, trainerId = 1)
        insertExercise("exercise7", "description7", 60, trainerId = 1)
        // trainer2 (id=2) owns exercises 8-10
        insertExercise("exercise8", "description8", 180, trainerId = 2)
        insertExercise("exercise9", "description9", 120, trainerId = 2)
        insertExercise("exercise10", "description10", 180, trainerId = 2)

        // ─── EXERCISE TAGS ───────────────────────────────────────
        insertExerciseTag(1, 1)
        insertExerciseTag(1, 5)
        insertExerciseTag(1, 7)

        insertExerciseTag(2, 1)
        insertExerciseTag(2, 5)
        insertExerciseTag(2, 4)

        insertExerciseTag(3, 2)
        insertExerciseTag(3, 3)
        insertExerciseTag(3, 4)

        insertExerciseTag(4, 7)
        insertExerciseTag(4, 1)

        insertExerciseTag(5, 8)
        insertExerciseTag(5, 2)
        insertExerciseTag(5, 4)

        insertExerciseTag(6, 1)
        insertExerciseTag(6, 5)
        insertExerciseTag(6, 6)

        insertExerciseTag(7, 8)
        insertExerciseTag(7, 7)
        insertExerciseTag(7, 2)

        insertExerciseTag(8, 1)
        insertExerciseTag(8, 5)

        insertExerciseTag(9, 6)
        insertExerciseTag(9, 9)
        insertExerciseTag(9, 10)

        insertExerciseTag(10, 7)
        insertExerciseTag(10, 1)

        // ─── WORKOUTS ────────────────────────────────────────────
        // trainer1 (id=1) owns all demo workouts
        insertWorkout("workout1", "description1", 235, trainerId = 1)
        insertWorkout("workout2", "description2", 150, trainerId = 1)
        insertWorkout("workout3", "description3", 3900, trainerId = 1)
        insertWorkout("workout4", "description4", 360, trainerId = 1)
        insertWorkout("workout5", "description5", 275, trainerId = 1)

        // ─── WORKOUT EXERCISES ──────────────────────────────────
        insertWorkoutExercise(1, 1, 15)
        insertWorkoutExercise(1, 2, 12)
        insertWorkoutExercise(1, 4, 15)
        insertWorkoutExercise(1, 8, 10)

        insertWorkoutExercise(2, 3, 15)
        insertWorkoutExercise(2, 5, 10)
        insertWorkoutExercise(2, 7, 12)

        insertWorkoutExercise(3, 4, 15)
        insertWorkoutExercise(3, 3, 15)
        insertWorkoutExercise(3, 2, 12)

        insertWorkoutExercise(4, 4, 15)
        insertWorkoutExercise(4, 10, 12)
        insertWorkoutExercise(4, 9, 5)
        insertWorkoutExercise(4, 6, 15)

        insertWorkoutExercise(5, 5, 15)
        insertWorkoutExercise(5, 7, 12)
        insertWorkoutExercise(5, 3, 15)
        insertWorkoutExercise(5, 1, 12)

        // ─── TRAINEE REQUESTS ───────────────────────────────────
        insertRequest(1, 4, DatabaseHelper.STATUS_ACCEPTED)
        insertRequest(1, 5, DatabaseHelper.STATUS_ACCEPTED)
        insertRequest(1, 6, DatabaseHelper.STATUS_ACCEPTED)
        insertRequest(1, 7, DatabaseHelper.STATUS_ACCEPTED)
        insertRequest(1, 10, DatabaseHelper.STATUS_ACCEPTED)

        insertRequest(1, 8, DatabaseHelper.STATUS_PENDING)
        insertRequest(1, 9, DatabaseHelper.STATUS_PENDING)

        // ─── ACTIVE MAPPINGS ────────────────────────────────────
        insertTrainerTrainee(1, 4)
        insertTrainerTrainee(1, 5)
        insertTrainerTrainee(1, 6)
        insertTrainerTrainee(1, 7)
        insertTrainerTrainee(1, 10)

        insertTraineeTrainer(4, 1)
        insertTraineeTrainer(5, 1)
        insertTraineeTrainer(6, 1)
        insertTraineeTrainer(7, 1)
        insertTraineeTrainer(10, 1)

        // ─── CALENDAR SLOTS: trainee ID 10 ──────────────────────
        insertSlot(10, 1, 1, 0, "08:00", "09:00")
        insertSlot(10, 3, 2, 0, "09:00", "10:00")
        insertSlot(10, 3, 2, 1, "10:00", "11:00")
        insertSlot(10, null, null, 1, "13:00", "14:00")
        insertSlot(10, null, null, 2, "15:00", "16:00")
        insertSlot(10, 5, 3, 0, "17:00", "18:00")

        // ─── CALENDAR SLOTS: trainee ID 4 ───────────────────────
        insertSlot(4, 2, 4, 0, "07:00", "08:00")
        insertSlot(4, null, null, 1, "10:00", "11:00")
        insertSlot(4, 4, 5, 0, "14:00", "15:00")
        insertSlot(4, null, null, 2, "16:00", "17:00")

        // ─── SNAPSHOTS ──────────────────────────────────────────
        createSnapshotWorkout(db, assignmentId = 1, workoutId = 1)
        createSnapshotWorkout(db, assignmentId = 2, workoutId = 3)
        createSnapshotWorkout(db, assignmentId = 3, workoutId = 5)
        createSnapshotWorkout(db, assignmentId = 4, workoutId = 2)
        createSnapshotWorkout(db, assignmentId = 5, workoutId = 4)

        // ─── PROGRESS ───────────────────────────────────────────
        createWorkoutAssignmentProgress(
            db = db,
            assignmentId = 1,
            workoutId = 1,
            completedExerciseIds = listOf(1, 2)
        )

        createWorkoutAssignmentProgress(
            db = db,
            assignmentId = 2,
            workoutId = 3
        )

        createWorkoutAssignmentProgress(
            db = db,
            assignmentId = 3,
            workoutId = 5,
            completedExerciseIds = listOf(5, 7, 3, 1)
        )

        createWorkoutAssignmentProgress(
            db = db,
            assignmentId = 4,
            workoutId = 2,
            completedExerciseIds = listOf(3)
        )

        createWorkoutAssignmentProgress(
            db = db,
            assignmentId = 5,
            workoutId = 4
        )

        // ─── COMPLETIONS ────────────────────────────────────────
        insertCompletion(traineeId = 10, startTime = "08:00", exerciseId = 1)
        insertCompletion(traineeId = 10, startTime = "08:00", exerciseId = 2)

        insertCompletion(traineeId = 10, startTime = "17:00", exerciseId = 5)
        insertCompletion(traineeId = 10, startTime = "17:00", exerciseId = 7)
        insertCompletion(traineeId = 10, startTime = "17:00", exerciseId = 3)
        insertCompletion(traineeId = 10, startTime = "17:00", exerciseId = 1)

        insertCompletion(traineeId = 4, startTime = "07:00", exerciseId = 3)
    }

    private fun createSnapshotWorkout(
        db: SQLiteDatabase,
        assignmentId: Int,
        workoutId: Int
    ) {
        db.execSQL(
            """
            INSERT INTO ${DatabaseHelper.TABLE_SNAPSHOT_WORKOUT}
            (
                ${DatabaseHelper.COL_SNAPSHOT_ASSIGNMENT_ID},
                ${DatabaseHelper.COL_SNAPSHOT_WORKOUT_ID},
                ${DatabaseHelper.COL_SNAPSHOT_EXERCISE_ID},
                ${DatabaseHelper.COL_SNAPSHOT_EXERCISE_NAME},
                ${DatabaseHelper.COL_SNAPSHOT_EXERCISE_DESC},
                ${DatabaseHelper.COL_SNAPSHOT_TIME_PER_REP},
                ${DatabaseHelper.COL_SNAPSHOT_REPS},
                ${DatabaseHelper.COL_SNAPSHOT_EXERCISE_TOTAL_TIME}
            )
            SELECT
                $assignmentId,
                $workoutId,
                e.${DatabaseHelper.COL_EXERCISE_ID},
                e.${DatabaseHelper.COL_EXERCISE_NAME},
                e.${DatabaseHelper.COL_EXERCISE_DESC},
                e.${DatabaseHelper.COL_EXERCISE_TIME_PER_REP},
                we.${DatabaseHelper.COL_WE_REPS},
                e.${DatabaseHelper.COL_EXERCISE_TIME_PER_REP} * we.${DatabaseHelper.COL_WE_REPS}
            FROM ${DatabaseHelper.TABLE_WORKOUT_EXERCISES} we
            JOIN ${DatabaseHelper.TABLE_EXERCISES} e
            ON we.${DatabaseHelper.COL_WE_EXERCISE_ID} = e.${DatabaseHelper.COL_EXERCISE_ID}
            WHERE we.${DatabaseHelper.COL_WE_WORKOUT_ID} = $workoutId
            """.trimIndent()
        )
    }

    private fun createWorkoutAssignmentProgress(
        db: SQLiteDatabase,
        assignmentId: Int,
        workoutId: Int,
        completedExerciseIds: List<Int> = emptyList()
    ) {
        val completedTimeSql = if (completedExerciseIds.isEmpty()) {
            "0"
        } else {
            val completedIds = completedExerciseIds.joinToString(",")

            """
            (
                SELECT COALESCE(SUM(${DatabaseHelper.COL_SNAPSHOT_EXERCISE_TOTAL_TIME}), 0)
                FROM ${DatabaseHelper.TABLE_SNAPSHOT_WORKOUT}
                WHERE ${DatabaseHelper.COL_SNAPSHOT_ASSIGNMENT_ID} = $assignmentId
                AND ${DatabaseHelper.COL_SNAPSHOT_EXERCISE_ID} IN ($completedIds)
            )
            """.trimIndent()
        }

        db.execSQL(
            """
            INSERT INTO ${DatabaseHelper.TABLE_WORKOUT_ASSIGNMENT_PROGRESS}
            (
                ${DatabaseHelper.COL_PROGRESS_ASSIGNMENT_ID},
                ${DatabaseHelper.COL_PROGRESS_WORKOUT_ID},
                ${DatabaseHelper.COL_PROGRESS_COMPLETED_EXERCISE_TIME},
                ${DatabaseHelper.COL_PROGRESS_TOTAL_EXERCISE_TIME}
            )
            VALUES
            (
                $assignmentId,
                $workoutId,
                $completedTimeSql,
                (
                    SELECT COALESCE(SUM(${DatabaseHelper.COL_SNAPSHOT_EXERCISE_TOTAL_TIME}), 0)
                    FROM ${DatabaseHelper.TABLE_SNAPSHOT_WORKOUT}
                    WHERE ${DatabaseHelper.COL_SNAPSHOT_ASSIGNMENT_ID} = $assignmentId
                )
            )
            """.trimIndent()
        )
    }
}
