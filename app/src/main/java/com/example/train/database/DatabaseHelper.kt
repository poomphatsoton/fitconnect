package com.example.train.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FitConnect.db"
        private const val DATABASE_VERSION = 3

        // Users
        const val TABLE_USERS = "users"
        const val COL_USER_ID = "id"
        const val COL_USER_USERNAME = "username"
        const val COL_USER_PASSWORD = "password"
        const val COL_USER_ROLE = "role"
        const val COL_USER_NAME = "name"
        const val COL_USER_BIO = "bio"

        // Exercises
        const val TABLE_EXERCISES = "exercises"
        const val COL_EXERCISE_ID = "id"
        const val COL_EXERCISE_NAME = "name"
        const val COL_EXERCISE_DESC = "description"
        const val COL_EXERCISE_CATEGORY1 = "category1"
        const val COL_EXERCISE_CATEGORY2 = "category2"
        const val COL_EXERCISE_TIME_PER_REP = "time_per_rep"

        // Workouts
        const val TABLE_WORKOUTS = "workouts"
        const val COL_WORKOUT_ID = "id"
        const val COL_WORKOUT_NAME = "name"
        const val COL_WORKOUT_DESC = "description"
        const val COL_WORKOUT_DURATION = "duration"

        // Workout_Exercises
        const val TABLE_WORKOUT_EXERCISES = "workout_exercises"
        const val COL_WE_WORKOUT_ID = "workout_id"
        const val COL_WE_EXERCISE_ID = "exercise_id"
        const val COL_WE_REPS = "reps"

        // Trainee Requests
        const val TABLE_TRAINEE_REQUESTS = "trainee_requests"
        const val COL_REQUEST_ID = "id"
        const val COL_REQUEST_TRAINER_ID = "trainer_id"
        const val COL_REQUEST_TRAINEE_ID = "trainee_id"
        const val COL_REQUEST_STATUS = "status"

        // Schedules
        const val TABLE_WORKOUT_SCHEDULES = "workout_schedules"
        const val COL_SCHEDULE_ID = "id"
        const val COL_SCHEDULE_WORKOUT_ID = "workout_id"
        const val COL_SCHEDULE_DATE = "date"
        const val COL_SCHEDULE_TRAINEE_ID = "trainee_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_USERS)
        db.execSQL(CREATE_TABLE_EXERCISES)
        db.execSQL(CREATE_TABLE_WORKOUTS)
        db.execSQL(CREATE_TABLE_WORKOUT_EXERCISES)
        db.execSQL(CREATE_TABLE_TRAINEE_REQUESTS)
        db.execSQL(CREATE_TABLE_WORKOUT_SCHEDULES)
        insertDemoData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUT_SCHEDULES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRAINEE_REQUESTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUT_EXERCISES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXERCISES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    private val CREATE_TABLE_USERS = """
        CREATE TABLE $TABLE_USERS (
            $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_USER_USERNAME TEXT UNIQUE NOT NULL,
            $COL_USER_PASSWORD TEXT NOT NULL,
            $COL_USER_ROLE TEXT NOT NULL,
            $COL_USER_NAME TEXT,
            $COL_USER_BIO TEXT
        )
    """.trimIndent()

    private val CREATE_TABLE_EXERCISES = """
        CREATE TABLE $TABLE_EXERCISES (
            $COL_EXERCISE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_EXERCISE_NAME TEXT NOT NULL,
            $COL_EXERCISE_DESC TEXT,
            $COL_EXERCISE_CATEGORY1 TEXT,
            $COL_EXERCISE_CATEGORY2 TEXT,
            $COL_EXERCISE_TIME_PER_REP INTEGER
        )
    """.trimIndent()

    private val CREATE_TABLE_WORKOUTS = """
        CREATE TABLE $TABLE_WORKOUTS (
            $COL_WORKOUT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_WORKOUT_NAME TEXT NOT NULL,
            $COL_WORKOUT_DESC TEXT,
            $COL_WORKOUT_DURATION INTEGER
        )
    """.trimIndent()

    private val CREATE_TABLE_WORKOUT_EXERCISES = """
        CREATE TABLE $TABLE_WORKOUT_EXERCISES (
            $COL_WE_WORKOUT_ID INTEGER,
            $COL_WE_EXERCISE_ID INTEGER,
            $COL_WE_REPS INTEGER,
            PRIMARY KEY($COL_WE_WORKOUT_ID, $COL_WE_EXERCISE_ID),
            FOREIGN KEY($COL_WE_WORKOUT_ID) REFERENCES $TABLE_WORKOUTS($COL_WORKOUT_ID),
            FOREIGN KEY($COL_WE_EXERCISE_ID) REFERENCES $TABLE_EXERCISES($COL_EXERCISE_ID)
        )
    """.trimIndent()

    private val CREATE_TABLE_TRAINEE_REQUESTS = """
        CREATE TABLE $TABLE_TRAINEE_REQUESTS (
            $COL_REQUEST_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_REQUEST_TRAINER_ID INTEGER,
            $COL_REQUEST_TRAINEE_ID INTEGER,
            $COL_REQUEST_STATUS TEXT DEFAULT 'pending',
            FOREIGN KEY($COL_REQUEST_TRAINER_ID) REFERENCES $TABLE_USERS($COL_USER_ID),
            FOREIGN KEY($COL_REQUEST_TRAINEE_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
        )
    """.trimIndent()

    private val CREATE_TABLE_WORKOUT_SCHEDULES = """
        CREATE TABLE $TABLE_WORKOUT_SCHEDULES (
            $COL_SCHEDULE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_SCHEDULE_WORKOUT_ID INTEGER,
            $COL_SCHEDULE_DATE TEXT NOT NULL,
            $COL_SCHEDULE_TRAINEE_ID INTEGER,
            FOREIGN KEY($COL_SCHEDULE_WORKOUT_ID) REFERENCES $TABLE_WORKOUTS($COL_WORKOUT_ID),
            FOREIGN KEY($COL_SCHEDULE_TRAINEE_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
        )
    """.trimIndent()

    private fun insertDemoData(db: SQLiteDatabase) {
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_PASSWORD, $COL_USER_ROLE, $COL_USER_NAME, $COL_USER_BIO) VALUES ('trainer', 'trainer123', 'trainer', 'John Smith', 'Certified personal trainer with 10 years of experience')")
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_PASSWORD, $COL_USER_ROLE) VALUES ('trainee', 'trainee123', 'trainee')")

        db.execSQL("INSERT INTO $TABLE_EXERCISES ($COL_EXERCISE_NAME, $COL_EXERCISE_DESC, $COL_EXERCISE_CATEGORY1, $COL_EXERCISE_CATEGORY2, $COL_EXERCISE_TIME_PER_REP) VALUES ('Push-ups', 'Standard push-ups with proper form. Keep body straight, lower chest to ground.', 'strength', 'upper-body', 3)")
        db.execSQL("INSERT INTO $TABLE_EXERCISES ($COL_EXERCISE_NAME, $COL_EXERCISE_DESC, $COL_EXERCISE_CATEGORY1, $COL_EXERCISE_CATEGORY2, $COL_EXERCISE_TIME_PER_REP) VALUES ('Squats', 'Bodyweight squats. Keep back straight, lower until thighs are parallel to ground.', 'strength', 'lower-body', 4)")
        db.execSQL("INSERT INTO $TABLE_EXERCISES ($COL_EXERCISE_NAME, $COL_EXERCISE_DESC, $COL_EXERCISE_CATEGORY1, $COL_EXERCISE_CATEGORY2, $COL_EXERCISE_TIME_PER_REP) VALUES ('Jumping Jacks', 'Cardio exercise. Jump while spreading legs and raising arms overhead.', 'cardio', 'full-body', 2)")
        db.execSQL("INSERT INTO $TABLE_EXERCISES ($COL_EXERCISE_NAME, $COL_EXERCISE_DESC, $COL_EXERCISE_CATEGORY1, $COL_EXERCISE_CATEGORY2, $COL_EXERCISE_TIME_PER_REP) VALUES ('Plank', 'Hold plank position with forearms on ground, body straight.', 'strength', 'core', 1)")

        db.execSQL("INSERT INTO $TABLE_WORKOUTS ($COL_WORKOUT_NAME, $COL_WORKOUT_DESC, $COL_WORKOUT_DURATION) VALUES ('Full Body Strength', 'Complete full body workout targeting all major muscle groups', 245)")

        db.execSQL("INSERT INTO $TABLE_WORKOUT_EXERCISES ($COL_WE_WORKOUT_ID, $COL_WE_EXERCISE_ID, $COL_WE_REPS) VALUES (1, 1, 15)")
        db.execSQL("INSERT INTO $TABLE_WORKOUT_EXERCISES ($COL_WE_WORKOUT_ID, $COL_WE_EXERCISE_ID, $COL_WE_REPS) VALUES (1, 2, 20)")
        db.execSQL("INSERT INTO $TABLE_WORKOUT_EXERCISES ($COL_WE_WORKOUT_ID, $COL_WE_EXERCISE_ID, $COL_WE_REPS) VALUES (1, 3, 30)")
        db.execSQL("INSERT INTO $TABLE_WORKOUT_EXERCISES ($COL_WE_WORKOUT_ID, $COL_WE_EXERCISE_ID, $COL_WE_REPS) VALUES (1, 4, 60)")

        db.execSQL("INSERT INTO $TABLE_TRAINEE_REQUESTS ($COL_REQUEST_TRAINER_ID, $COL_REQUEST_TRAINEE_ID) VALUES (1, 2)")
    }

    fun getActiveTraineesCount(trainerId: Int): Int {
        val db = readableDatabase
        val selection = "$COL_REQUEST_TRAINER_ID = ? AND $COL_REQUEST_STATUS = 'accepted'"
        val cursor = db.query(TABLE_TRAINEE_REQUESTS, null, selection, arrayOf(trainerId.toString()), null, null, null)
        val count = cursor.count
        cursor.close()
        return count
    }

    fun getExercisesCount(): Int {
        val db = readableDatabase
        val cursor = db.query(TABLE_EXERCISES, null, null, null, null, null, null)
        val count = cursor.count
        cursor.close()
        return count
    }

    fun getWorkoutsCount(): Int {
        val db = readableDatabase
        val cursor = db.query(TABLE_WORKOUTS, null, null, null, null, null, null)
        val count = cursor.count
        cursor.close()
        return count
    }

    fun getPendingRequestsCount(trainerId: Int): Int {
        val db = readableDatabase
        val selection = "$COL_REQUEST_TRAINER_ID = ? AND $COL_REQUEST_STATUS = 'pending'"
        val cursor = db.query(TABLE_TRAINEE_REQUESTS, null, selection, arrayOf(trainerId.toString()), null, null, null)
        val count = cursor.count
        cursor.close()
        return count
    }

    fun insertExercise(name: String, desc: String, category1: String, category2: String, timePerRep: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_EXERCISE_NAME, name)
            put(COL_EXERCISE_DESC, desc)
            put(COL_EXERCISE_CATEGORY1, category1)
            put(COL_EXERCISE_CATEGORY2, category2)
            put(COL_EXERCISE_TIME_PER_REP, timePerRep)
        }
        val insertId = db.insert(TABLE_EXERCISES, null, values)
        return insertId
    }

    fun insertWorkout(name: String, desc: String, duration: Int): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_WORKOUT_NAME, name)
            put(COL_WORKOUT_DESC, desc)
            put(COL_WORKOUT_DURATION, duration)
        }
        val insertId = db.insert(TABLE_WORKOUTS, null, values)
        return insertId
    }

    fun addExerciseToWorkout(workoutId: Long, exerciseId: Long, reps: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_WE_WORKOUT_ID, workoutId)
            put(COL_WE_EXERCISE_ID, exerciseId)
            put(COL_WE_REPS, reps)
        }
        db.insert(TABLE_WORKOUT_EXERCISES, null, values)
    }

    fun getAllExercises(): Cursor {
        val db = readableDatabase
        return db.query(TABLE_EXERCISES, null, null, null, null, null, COL_EXERCISE_NAME)
    }

    fun calculateWorkoutTotalDuration(workoutId: Long): Int {
        val db = readableDatabase
        val query = """
            SELECT SUM(e.$COL_EXERCISE_TIME_PER_REP * we.$COL_WE_REPS)
            FROM $TABLE_WORKOUT_EXERCISES we
            JOIN $TABLE_EXERCISES e ON we.$COL_WE_EXERCISE_ID = e.$COL_EXERCISE_ID
            WHERE we.$COL_WE_WORKOUT_ID = ?
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(workoutId.toString()))
        var total = 0
        if (cursor.moveToFirst()) total = cursor.getInt(0)
        cursor.close()
        return total
    }

    fun updateWorkoutDuration(workoutId: Long, duration: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_WORKOUT_DURATION, duration)
        }
        db.update(TABLE_WORKOUTS, values, "$COL_WORKOUT_ID=?", arrayOf(workoutId.toString()))
    }

    fun getWorkoutExerciseDetails(workoutId: Long): Cursor {
        val db = readableDatabase
        val query = """
            SELECT e.$COL_EXERCISE_NAME, we.$COL_WE_REPS, e.$COL_EXERCISE_TIME_PER_REP,
            e.$COL_EXERCISE_CATEGORY1, e.$COL_EXERCISE_CATEGORY2
            FROM $TABLE_WORKOUT_EXERCISES we
            JOIN $TABLE_EXERCISES e ON we.$COL_WE_EXERCISE_ID = e.$COL_EXERCISE_ID
            WHERE we.$COL_WE_WORKOUT_ID = ?
        """.trimIndent()
        return db.rawQuery(query, arrayOf(workoutId.toString()))
    }
}