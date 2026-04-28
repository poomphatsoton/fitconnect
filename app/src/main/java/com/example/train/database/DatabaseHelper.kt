package com.example.train.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FitConnect.db"
        private const val DATABASE_VERSION = 14

        // Users
        const val TABLE_USERS = "users"
        const val COL_USER_ID = "id"
        const val COL_USER_USERNAME = "username"
        const val COL_USER_PASSWORD = "password"
        const val COL_USER_ROLE = "role"
        const val COL_USER_NAME = "name"
        const val COL_USER_BIO = "bio"

        //Tags
        const val TABLE_TAGS = "tags"
        const val COL_TAG_ID = "tag_id"
        const val COL_TAG_NAME = "tag_name"

        // User tags
        const val TABLE_USERS_TAGS = "user_tags"

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

        // Trainee slot
        const val TABLE_TRAINEE_CALENDAR_SLOT = "trainee_calendar_slot"
        const val COL_SLOT_ID = "slot_id"
        const val COL_TRAINEE_ID = "trainee_id"
        const val COL_SLOT_WORKOUT_ID = "workout_id"
        const val COL_SLOT_STATUS = "slot_status"
        const val COL_SLOT_START_TIME = "start_time"
        const val COL_SLOT_END_TIME = "end_time"
        const val COL_SLOT_DATE = "slot_date"

        // Trainer-Trainee mappings
        const val TABLE_TRAINER_TRAINEES = "trainer_trainees"
        const val COL_TT_ID = "id"
        const val COL_TT_TRAINER_ID = "trainer_id"
        const val COL_TT_TRAINEE_ID = "trainee_id"

        const val TABLE_TRAINEE_TRAINER = "trainee_trainer"
        const val COL_TTR_ID = "id"
        const val COL_TTR_TRAINEE_ID = "trainee_id"
        const val COL_TTR_TRAINER_ID = "trainer_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_TABLE_USERS)
        db.execSQL(CREATE_TABLE_EXERCISES)
        db.execSQL(CREATE_TABLE_WORKOUTS)
        db.execSQL(CREATE_TABLE_WORKOUT_EXERCISES)
        db.execSQL(CREATE_TABLE_TRAINEE_REQUESTS)
        db.execSQL(CREATE_TABLE_WORKOUT_SCHEDULES)
        db.execSQL(CREATE_TABLE_TRAINEE_CALENDAR_SLOT)
        db.execSQL(CREATE_TABLE_TRAINER_TRAINEES)
        db.execSQL(CREATE_TABLE_TRAINEE_TRAINER)
        db.execSQL(CREATE_TABLE_TAGS)
        db.execSQL(CREATE_TABLE_USER_TAGS)
        insertDemoData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS_TAGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRAINEE_TRAINER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRAINER_TRAINEES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRAINEE_CALENDAR_SLOT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUT_SCHEDULES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRAINEE_REQUESTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUT_EXERCISES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXERCISES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TAGS")
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

    private val CREATE_TABLE_USER_TAGS = """
    CREATE TABLE $TABLE_USERS_TAGS (
        $COL_USER_ID INTEGER,
        $COL_TAG_ID INTEGER,
        PRIMARY KEY($COL_USER_ID, $COL_TAG_ID),
        FOREIGN KEY($COL_USER_ID) REFERENCES $TABLE_USERS($COL_USER_ID),
        FOREIGN KEY($COL_TAG_ID) REFERENCES $TABLE_TAGS($COL_TAG_ID)
    )
""".trimIndent()

    private val CREATE_TABLE_TAGS = """
        CREATE TABLE $TABLE_TAGS (
            $COL_TAG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_TAG_NAME TEXT NOT NULL
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

    private val CREATE_TABLE_TRAINEE_CALENDAR_SLOT = """
        CREATE TABLE $TABLE_TRAINEE_CALENDAR_SLOT (
            $COL_SLOT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_TRAINEE_ID INTEGER,
            $COL_SLOT_WORKOUT_ID INTEGER,
            $COL_SLOT_STATUS INTEGER,
            $COL_SLOT_START_TIME TEXT,
            $COL_SLOT_END_TIME TEXT,
            $COL_SLOT_DATE TEXT,
            FOREIGN KEY($COL_TRAINEE_ID) REFERENCES $TABLE_USERS($COL_USER_ID),
            FOREIGN KEY($COL_SLOT_WORKOUT_ID) REFERENCES $TABLE_WORKOUTS($COL_WORKOUT_ID)
        )
    """.trimIndent()

    private val CREATE_TABLE_TRAINER_TRAINEES = """
        CREATE TABLE $TABLE_TRAINER_TRAINEES (
            $COL_TT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_TT_TRAINER_ID INTEGER,
            $COL_TT_TRAINEE_ID INTEGER,
            FOREIGN KEY($COL_TT_TRAINER_ID) REFERENCES $TABLE_USERS($COL_USER_ID),
            FOREIGN KEY($COL_TT_TRAINEE_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
        )
    """.trimIndent()

    private val CREATE_TABLE_TRAINEE_TRAINER = """
        CREATE TABLE $TABLE_TRAINEE_TRAINER (
            $COL_TTR_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_TTR_TRAINEE_ID INTEGER UNIQUE,
            $COL_TTR_TRAINER_ID INTEGER,
            FOREIGN KEY($COL_TTR_TRAINEE_ID) REFERENCES $TABLE_USERS($COL_USER_ID),
            FOREIGN KEY($COL_TTR_TRAINER_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
        )
    """.trimIndent()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertDemoData(db: SQLiteDatabase) {
        // Trainer (ID 1)
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_PASSWORD, $COL_USER_ROLE, $COL_USER_NAME, $COL_USER_BIO) VALUES ('trainer', 'trainer123', 'trainer', 'John Smith', 'Certified personal trainer with 10 years of experience')")

        // 4 Active Trainees (IDs 2, 3, 4, 5)
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_PASSWORD, $COL_USER_ROLE, $COL_USER_NAME, $COL_USER_BIO) VALUES ('trainee1', 'pass123', 'trainee', 'Sarah Johnson', 'Looking to build strength')")
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_PASSWORD, $COL_USER_ROLE, $COL_USER_NAME, $COL_USER_BIO) VALUES ('trainee2', 'pass123', 'trainee', 'Mike Wilson', 'Former athlete getting back into shape')")
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_PASSWORD, $COL_USER_ROLE, $COL_USER_NAME, $COL_USER_BIO) VALUES ('trainee3', 'pass123', 'trainee', 'Emily Davis', 'Focusing on weight loss')")
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_PASSWORD, $COL_USER_ROLE, $COL_USER_NAME, $COL_USER_BIO) VALUES ('trainee4', 'pass123', 'trainee', 'Chris Brown', 'Improving endurance')")

        db.execSQL("INSERT INTO $TABLE_EXERCISES ($COL_EXERCISE_NAME, $COL_EXERCISE_DESC, $COL_EXERCISE_CATEGORY1, $COL_EXERCISE_CATEGORY2, $COL_EXERCISE_TIME_PER_REP) VALUES ('Push-ups', 'Standard push-ups with proper form. Keep body straight, lower chest to ground.', 'strength', 'upper-body', 3)")
        db.execSQL("INSERT INTO $TABLE_EXERCISES ($COL_EXERCISE_NAME, $COL_EXERCISE_DESC, $COL_EXERCISE_CATEGORY1, $COL_EXERCISE_CATEGORY2, $COL_EXERCISE_TIME_PER_REP) VALUES ('Squats', 'Bodyweight squats. Keep back straight, lower until thighs are parallel to ground.', 'strength', 'lower-body', 4)")
        db.execSQL("INSERT INTO $TABLE_EXERCISES ($COL_EXERCISE_NAME, $COL_EXERCISE_DESC, $COL_EXERCISE_CATEGORY1, $COL_EXERCISE_CATEGORY2, $COL_EXERCISE_TIME_PER_REP) VALUES ('Jumping Jacks', 'Cardio exercise. Jump while spreading legs and raising arms overhead.', 'cardio', 'full-body', 2)")
        db.execSQL("INSERT INTO $TABLE_EXERCISES ($COL_EXERCISE_NAME, $COL_EXERCISE_DESC, $COL_EXERCISE_CATEGORY1, $COL_EXERCISE_CATEGORY2, $COL_EXERCISE_TIME_PER_REP) VALUES ('Plank', 'Hold plank position with forearms on ground, body straight.', 'strength', 'core', 1)")

        db.execSQL("INSERT INTO $TABLE_WORKOUTS ($COL_WORKOUT_NAME, $COL_WORKOUT_DESC, $COL_WORKOUT_DURATION) VALUES ('Full Body Strength', 'Complete full body workout targeting all major muscle groups', 245)")

        db.execSQL("INSERT INTO $TABLE_WORKOUT_EXERCISES ($COL_WE_WORKOUT_ID, $COL_WE_EXERCISE_ID, $COL_WE_REPS) VALUES (1, 1, 15)")
        db.execSQL("INSERT INTO $TABLE_WORKOUT_EXERCISES ($COL_WE_WORKOUT_ID, $COL_WE_EXERCISE_ID, $COL_WE_REPS) VALUES (1, 2, 20)")
        db.execSQL("INSERT INTO $TABLE_WORKOUT_EXERCISES ($COL_WE_WORKOUT_ID, $COL_WE_EXERCISE_ID, $COL_WE_REPS) VALUES (1, 3, 30)")
        db.execSQL("INSERT INTO $TABLE_WORKOUT_EXERCISES ($COL_WE_WORKOUT_ID, $COL_WE_EXERCISE_ID, $COL_WE_REPS) VALUES (1, 4, 60)")

        // Active Requests
        db.execSQL("INSERT INTO $TABLE_TRAINEE_REQUESTS ($COL_REQUEST_TRAINER_ID, $COL_REQUEST_TRAINEE_ID, $COL_REQUEST_STATUS) VALUES (1, 2, 'accepted')")
        db.execSQL("INSERT INTO $TABLE_TRAINEE_REQUESTS ($COL_REQUEST_TRAINER_ID, $COL_REQUEST_TRAINEE_ID, $COL_REQUEST_STATUS) VALUES (1, 3, 'accepted')")
        db.execSQL("INSERT INTO $TABLE_TRAINEE_REQUESTS ($COL_REQUEST_TRAINER_ID, $COL_REQUEST_TRAINEE_ID, $COL_REQUEST_STATUS) VALUES (1, 4, 'accepted')")
        db.execSQL("INSERT INTO $TABLE_TRAINEE_REQUESTS ($COL_REQUEST_TRAINER_ID, $COL_REQUEST_TRAINEE_ID, $COL_REQUEST_STATUS) VALUES (1, 5, 'accepted')")

        // Active Mappings (Trainee Table)
        db.execSQL("INSERT INTO $TABLE_TRAINER_TRAINEES ($COL_TT_TRAINER_ID, $COL_TT_TRAINEE_ID) VALUES (1, 2)")
        db.execSQL("INSERT INTO $TABLE_TRAINER_TRAINEES ($COL_TT_TRAINER_ID, $COL_TT_TRAINEE_ID) VALUES (1, 3)")
        db.execSQL("INSERT INTO $TABLE_TRAINER_TRAINEES ($COL_TT_TRAINER_ID, $COL_TT_TRAINEE_ID) VALUES (1, 4)")
        db.execSQL("INSERT INTO $TABLE_TRAINER_TRAINEES ($COL_TT_TRAINER_ID, $COL_TT_TRAINEE_ID) VALUES (1, 5)")

        db.execSQL("INSERT INTO $TABLE_TRAINEE_TRAINER ($COL_TTR_TRAINEE_ID, $COL_TTR_TRAINER_ID) VALUES (2, 1)")
        db.execSQL("INSERT INTO $TABLE_TRAINEE_TRAINER ($COL_TTR_TRAINEE_ID, $COL_TTR_TRAINER_ID) VALUES (3, 1)")
        db.execSQL("INSERT INTO $TABLE_TRAINEE_TRAINER ($COL_TTR_TRAINEE_ID, $COL_TTR_TRAINER_ID) VALUES (4, 1)")
        db.execSQL("INSERT INTO $TABLE_TRAINEE_TRAINER ($COL_TTR_TRAINEE_ID, $COL_TTR_TRAINER_ID) VALUES (5, 1)")

        // Mock Trainee Slots for testing for Sarah (ID 2)
        val today = java.time.LocalDate.now().toString()

        // 1. IDEAL with Workout
        db.execSQL("INSERT INTO $TABLE_TRAINEE_CALENDAR_SLOT ($COL_TRAINEE_ID, $COL_SLOT_WORKOUT_ID, $COL_SLOT_STATUS, $COL_SLOT_DATE, $COL_SLOT_START_TIME, $COL_SLOT_END_TIME) VALUES (2, 1, 0, '$today', '08:00', '09:00')")

        // 2. MAYBE without Workout (Available)
        db.execSQL("INSERT INTO $TABLE_TRAINEE_CALENDAR_SLOT ($COL_TRAINEE_ID, $COL_SLOT_WORKOUT_ID, $COL_SLOT_STATUS, $COL_SLOT_DATE, $COL_SLOT_START_TIME, $COL_SLOT_END_TIME) VALUES (2, NULL, 1, '$today', '10:00', '11:00')")

        // 3. BUSY without Workout (Unavailable)
        db.execSQL("INSERT INTO $TABLE_TRAINEE_CALENDAR_SLOT ($COL_TRAINEE_ID, $COL_SLOT_WORKOUT_ID, $COL_SLOT_STATUS, $COL_SLOT_DATE, $COL_SLOT_START_TIME, $COL_SLOT_END_TIME) VALUES (2, NULL, 2, '$today', '13:00', '14:00')")

        // 4. IDEAL without Workout (Available)
        db.execSQL("INSERT INTO $TABLE_TRAINEE_CALENDAR_SLOT ($COL_TRAINEE_ID, $COL_SLOT_WORKOUT_ID, $COL_SLOT_STATUS, $COL_SLOT_DATE, $COL_SLOT_START_TIME, $COL_SLOT_END_TIME) VALUES (2, NULL, 0, '$today', '15:00', '16:00')")

        // 5. MAYBE with Workout
        db.execSQL("INSERT INTO $TABLE_TRAINEE_CALENDAR_SLOT ($COL_TRAINEE_ID, $COL_SLOT_WORKOUT_ID, $COL_SLOT_STATUS, $COL_SLOT_DATE, $COL_SLOT_START_TIME, $COL_SLOT_END_TIME) VALUES (2, 1, 1, '$today', '17:00', '18:00')")

        // Mock Tags
        db.execSQL("INSERT INTO $TABLE_TAGS ($COL_TAG_NAME) VALUES ('strength')") // ID 1
        db.execSQL("INSERT INTO $TABLE_TAGS ($COL_TAG_NAME) VALUES ('weight-loss')") // ID 2
        db.execSQL("INSERT INTO $TABLE_TAGS ($COL_TAG_NAME) VALUES ('endurance')") // ID 3
        db.execSQL("INSERT INTO $TABLE_TAGS ($COL_TAG_NAME) VALUES ('cardio')") // ID 4
        db.execSQL("INSERT INTO $TABLE_TAGS ($COL_TAG_NAME) VALUES ('muscle-gain')") // ID 5

        // Assign tags to Sarah (User ID 2)
        db.execSQL("INSERT INTO $TABLE_USERS_TAGS ($COL_USER_ID, $COL_TAG_ID) VALUES (2, 1)")
        db.execSQL("INSERT INTO $TABLE_USERS_TAGS ($COL_USER_ID, $COL_TAG_ID) VALUES (2, 2)")
        db.execSQL("INSERT INTO $TABLE_USERS_TAGS ($COL_USER_ID, $COL_TAG_ID) VALUES (2, 3)")

        // Assign tags to Mike (User ID 3)
        db.execSQL("INSERT INTO $TABLE_USERS_TAGS ($COL_USER_ID, $COL_TAG_ID) VALUES (3, 5)")
        db.execSQL("INSERT INTO $TABLE_USERS_TAGS ($COL_USER_ID, $COL_TAG_ID) VALUES (3, 1)")
        db.execSQL("INSERT INTO $TABLE_USERS_TAGS ($COL_USER_ID, $COL_TAG_ID) VALUES (3, 3)")
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

    fun getAllTrainees(trainerId: Int): Cursor {
        val db = readableDatabase
        val query = """
            SELECT u.* 
            FROM $TABLE_USERS u
            JOIN $TABLE_TRAINER_TRAINEES tt ON u.$COL_USER_ID = tt.$COL_TT_TRAINEE_ID
            WHERE tt.$COL_TT_TRAINER_ID = ?
        """.trimIndent()
        return db.rawQuery(query, arrayOf(trainerId.toString()))
    }

    fun getTrainerID(traineeUserId: Int): Int {
        val db = readableDatabase
        val query = "SELECT $COL_TTR_TRAINER_ID FROM $TABLE_TRAINEE_TRAINER WHERE $COL_TTR_TRAINEE_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(traineeUserId.toString()))
        var trainerId = -1
        if (cursor.moveToFirst()) {
            trainerId = cursor.getInt(0)
        }
        cursor.close()
        return trainerId
    }

    fun getTraineeID(userId: Int): Int {
        val db = readableDatabase
        val query = "SELECT $COL_TTR_TRAINEE_ID FROM $TABLE_TRAINEE_TRAINER WHERE $COL_TTR_TRAINEE_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
        var traineeId = -1
        if (cursor.moveToFirst()) {
            traineeId = cursor.getInt(0)
        }
        cursor.close()
        return traineeId
    }

    fun getTraineeSlots(traineeId: Int, date: LocalDate): Cursor {
        val db = readableDatabase

        val query = """
        SELECT s.*, w.$COL_WORKOUT_NAME
        FROM $TABLE_TRAINEE_CALENDAR_SLOT s
        LEFT JOIN $TABLE_WORKOUTS w 
            ON s.$COL_SLOT_WORKOUT_ID = w.$COL_WORKOUT_ID
        WHERE s.$COL_TRAINEE_ID = ?
        AND s.$COL_SLOT_DATE = ?
    """.trimIndent()

        return db.rawQuery(
            query,
            arrayOf(
                traineeId.toString(),
                date.toString()
            )
        )
    }

    fun getUserTags(userId: Int): Cursor {
        val db = readableDatabase

        val query = """
            SELECT s.$COL_USER_ID, w.$COL_TAG_ID, w.$COL_TAG_NAME
            FROM $TABLE_USERS_TAGS s
            LEFT JOIN $TABLE_TAGS w
                ON s.$COL_TAG_ID = w.$COL_TAG_ID
            WHERE s.$COL_USER_ID = ?
            
        """.trimIndent()

        return db.rawQuery(
            query,
            arrayOf(
                userId.toString()
            )
        )
    }
}