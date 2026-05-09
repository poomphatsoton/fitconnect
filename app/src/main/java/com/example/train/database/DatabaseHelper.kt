package com.example.train.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.train.model.Tag
import java.time.LocalDate

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FitConnect.db"
        private const val DATABASE_VERSION = 26

        // Users
        const val TABLE_USERS = "users"
        const val COL_USER_ID = "id"
        const val COL_USER_USERNAME = "username"
        const val COL_USER_PASSWORD = "password"
        const val COL_USER_ROLE = "role"
        const val COL_USER_NAME = "name"
        const val COL_USER_BIO = "bio"
        const val COL_USER_MAX_TRAINEES = "max_trainees"

        //Tags
        const val TABLE_TAGS = "tags"
        const val COL_TAG_ID = "tag_id"
        const val COL_TAG_NAME = "tag_name"

        // User tags
        const val TABLE_USERS_TAGS = "user_tags"

        // Exercise tags
        const val TABLE_EXERCISE_TAGS = "exercise_tags"

        // Exercises
        const val TABLE_EXERCISES = "exercises"
        const val COL_EXERCISE_ID = "id"
        const val COL_EXERCISE_NAME = "name"
        const val COL_EXERCISE_DESC = "description"
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

        const val TABLE_WORKOUT_EXERCISE_COMPLETIONS = "workout_exercise_completions"
        const val COL_COMPLETION_SLOT_ID = "slot_id"
        const val COL_COMPLETION_EXERCISE_ID = "exercise_id"
        const val COL_COMPLETION_COMPLETED_AT = "completed_at"

        // Request Status
        const val STATUS_PENDING = "pending"
        const val STATUS_ACCEPTED = "accepted"
        const val STATUS_REJECTED = "rejected"

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
        const val COL_SLOT_ASSIGNMENT_ID = "assignment_id"
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
        db.execSQL(CREATE_TABLE_WORKOUT_EXERCISE_COMPLETIONS)
        db.execSQL(CREATE_TABLE_TRAINEE_REQUESTS)
        db.execSQL(CREATE_TABLE_WORKOUT_SCHEDULES)
        db.execSQL(CREATE_TABLE_TRAINEE_CALENDAR_SLOT)
        db.execSQL(CREATE_TABLE_TRAINER_TRAINEES)
        db.execSQL(CREATE_TABLE_TRAINEE_TRAINER)
        db.execSQL(CREATE_TABLE_TAGS)
        db.execSQL(CREATE_TABLE_USER_TAGS)
        db.execSQL(CREATE_TABLE_EXERCISE_TAGS)
        insertDemoData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS_TAGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXERCISE_TAGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRAINEE_TRAINER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRAINER_TRAINEES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRAINEE_CALENDAR_SLOT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUT_SCHEDULES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRAINEE_REQUESTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUT_EXERCISE_COMPLETIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUT_EXERCISES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXERCISES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TAGS")
        onCreate(db)
    }

    private val CREATE_TABLE_USERS = """
        CREATE TABLE IF NOT EXISTS $TABLE_USERS (
            $COL_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_USER_USERNAME TEXT UNIQUE NOT NULL,
            $COL_USER_PASSWORD TEXT NOT NULL,
            $COL_USER_ROLE TEXT NOT NULL,
            $COL_USER_NAME TEXT,
            $COL_USER_BIO TEXT,
            $COL_USER_MAX_TRAINEES INTEGER DEFAULT 10
        )
    """.trimIndent()

    private val CREATE_TABLE_USER_TAGS = """
        CREATE TABLE IF NOT EXISTS $TABLE_USERS_TAGS (
            $COL_USER_ID INTEGER,
            $COL_TAG_ID INTEGER,
            PRIMARY KEY($COL_USER_ID, $COL_TAG_ID),
            FOREIGN KEY($COL_USER_ID) REFERENCES $TABLE_USERS($COL_USER_ID),
            FOREIGN KEY($COL_TAG_ID) REFERENCES $TABLE_TAGS($COL_TAG_ID)
        )
    """.trimIndent()

    private val CREATE_TABLE_EXERCISE_TAGS = """
        CREATE TABLE IF NOT EXISTS $TABLE_EXERCISE_TAGS (
            $COL_EXERCISE_ID INTEGER,
            $COL_TAG_ID INTEGER,
            PRIMARY KEY($COL_EXERCISE_ID, $COL_TAG_ID),
            FOREIGN KEY($COL_EXERCISE_ID) REFERENCES $TABLE_EXERCISES($COL_EXERCISE_ID),
            FOREIGN KEY($COL_TAG_ID) REFERENCES $TABLE_TAGS($COL_TAG_ID)
        )
    """.trimIndent()

    private val CREATE_TABLE_TAGS = """
        CREATE TABLE IF NOT EXISTS $TABLE_TAGS (
            $COL_TAG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_TAG_NAME TEXT NOT NULL
        )
    """.trimIndent()

    private val CREATE_TABLE_EXERCISES = """
        CREATE TABLE IF NOT EXISTS $TABLE_EXERCISES (
            $COL_EXERCISE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_EXERCISE_NAME TEXT NOT NULL,
            $COL_EXERCISE_DESC TEXT,
            $COL_EXERCISE_TIME_PER_REP INTEGER
        )
    """.trimIndent()

    private val CREATE_TABLE_WORKOUTS = """
        CREATE TABLE IF NOT EXISTS $TABLE_WORKOUTS (
            $COL_WORKOUT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_WORKOUT_NAME TEXT NOT NULL,
            $COL_WORKOUT_DESC TEXT,
            $COL_WORKOUT_DURATION INTEGER
        )
    """.trimIndent()

    private val CREATE_TABLE_WORKOUT_EXERCISES = """
        CREATE TABLE IF NOT EXISTS $TABLE_WORKOUT_EXERCISES (
            $COL_WE_WORKOUT_ID INTEGER,
            $COL_WE_EXERCISE_ID INTEGER,
            $COL_WE_REPS INTEGER,
            PRIMARY KEY($COL_WE_WORKOUT_ID, $COL_WE_EXERCISE_ID),
            FOREIGN KEY($COL_WE_WORKOUT_ID) REFERENCES $TABLE_WORKOUTS($COL_WORKOUT_ID),
            FOREIGN KEY($COL_WE_EXERCISE_ID) REFERENCES $TABLE_EXERCISES($COL_EXERCISE_ID)
        )
    """.trimIndent()

    private val CREATE_TABLE_WORKOUT_EXERCISE_COMPLETIONS = """
        CREATE TABLE IF NOT EXISTS $TABLE_WORKOUT_EXERCISE_COMPLETIONS (
            $COL_COMPLETION_SLOT_ID INTEGER,
            $COL_COMPLETION_EXERCISE_ID INTEGER,
            $COL_COMPLETION_COMPLETED_AT TEXT NOT NULL,
            PRIMARY KEY($COL_COMPLETION_SLOT_ID, $COL_COMPLETION_EXERCISE_ID),
            FOREIGN KEY($COL_COMPLETION_SLOT_ID) REFERENCES $TABLE_TRAINEE_CALENDAR_SLOT($COL_SLOT_ID),
            FOREIGN KEY($COL_COMPLETION_EXERCISE_ID) REFERENCES $TABLE_EXERCISES($COL_EXERCISE_ID)
        )
    """.trimIndent()

    private val CREATE_TABLE_TRAINEE_REQUESTS = """
        CREATE TABLE IF NOT EXISTS $TABLE_TRAINEE_REQUESTS (
            $COL_REQUEST_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_REQUEST_TRAINER_ID INTEGER,
            $COL_REQUEST_TRAINEE_ID INTEGER,
            $COL_REQUEST_STATUS TEXT NOT NULL DEFAULT '$STATUS_PENDING'
                CHECK($COL_REQUEST_STATUS IN ('$STATUS_PENDING', '$STATUS_ACCEPTED', '$STATUS_REJECTED')),
            FOREIGN KEY($COL_REQUEST_TRAINER_ID) REFERENCES $TABLE_USERS($COL_USER_ID),
            FOREIGN KEY($COL_REQUEST_TRAINEE_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
        )
    """.trimIndent()

    private val CREATE_TABLE_WORKOUT_SCHEDULES = """
        CREATE TABLE IF NOT EXISTS $TABLE_WORKOUT_SCHEDULES (
            $COL_SCHEDULE_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_SCHEDULE_WORKOUT_ID INTEGER,
            $COL_SCHEDULE_DATE TEXT NOT NULL,
            $COL_SCHEDULE_TRAINEE_ID INTEGER,
            FOREIGN KEY($COL_SCHEDULE_WORKOUT_ID) REFERENCES $TABLE_WORKOUTS($COL_WORKOUT_ID),
            FOREIGN KEY($COL_SCHEDULE_TRAINEE_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
        )
    """.trimIndent()

    private val CREATE_TABLE_TRAINEE_CALENDAR_SLOT = """
        CREATE TABLE IF NOT EXISTS $TABLE_TRAINEE_CALENDAR_SLOT (
            $COL_SLOT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_TRAINEE_ID INTEGER,
            $COL_SLOT_WORKOUT_ID INTEGER,
            $COL_SLOT_ASSIGNMENT_ID INTEGER,
            $COL_SLOT_STATUS INTEGER,
            $COL_SLOT_START_TIME TEXT,
            $COL_SLOT_END_TIME TEXT,
            $COL_SLOT_DATE TEXT,
            FOREIGN KEY($COL_TRAINEE_ID) REFERENCES $TABLE_USERS($COL_USER_ID),
            FOREIGN KEY($COL_SLOT_WORKOUT_ID) REFERENCES $TABLE_WORKOUTS($COL_WORKOUT_ID)
        )
    """.trimIndent()

    private val CREATE_TABLE_TRAINER_TRAINEES = """
        CREATE TABLE IF NOT EXISTS $TABLE_TRAINER_TRAINEES (
            $COL_TT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_TT_TRAINER_ID INTEGER,
            $COL_TT_TRAINEE_ID INTEGER,
            FOREIGN KEY($COL_TT_TRAINER_ID) REFERENCES $TABLE_USERS($COL_USER_ID),
            FOREIGN KEY($COL_TT_TRAINEE_ID) REFERENCES $TABLE_USERS($COL_USER_ID)
        )
    """.trimIndent()

    private val CREATE_TABLE_TRAINEE_TRAINER = """
        CREATE TABLE IF NOT EXISTS $TABLE_TRAINEE_TRAINER (
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
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_PASSWORD, $COL_USER_ROLE, $COL_USER_NAME, $COL_USER_BIO, $COL_USER_MAX_TRAINEES) VALUES ('trainer', 'trainer123', 'trainer', 'John Smith', 'Certified personal trainer with 10 years of experience', 10)")

        // 4 Active Trainees (IDs 2, 3, 4, 5)
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_PASSWORD, $COL_USER_ROLE, $COL_USER_NAME, $COL_USER_BIO) VALUES ('trainee1', 'pass123', 'trainee', 'Sarah Johnson', 'Looking to build strength')")
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_PASSWORD, $COL_USER_ROLE, $COL_USER_NAME, $COL_USER_BIO) VALUES ('trainee2', 'pass123', 'trainee', 'Mike Wilson', 'Former athlete getting back into shape')")
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_PASSWORD, $COL_USER_ROLE, $COL_USER_NAME, $COL_USER_BIO) VALUES ('trainee3', 'pass123', 'trainee', 'Emily Davis', 'Focusing on weight loss')")
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_PASSWORD, $COL_USER_ROLE, $COL_USER_NAME, $COL_USER_BIO) VALUES ('trainee4', 'pass123', 'trainee', 'Chris Brown', 'Improving endurance')")

        db.execSQL("INSERT INTO $TABLE_EXERCISES ($COL_EXERCISE_NAME, $COL_EXERCISE_DESC, $COL_EXERCISE_TIME_PER_REP) VALUES ('Push-ups', 'Standard push-ups with proper form. Keep body straight, lower chest to ground.', 3)")
        db.execSQL("INSERT INTO $TABLE_EXERCISES ($COL_EXERCISE_NAME, $COL_EXERCISE_DESC, $COL_EXERCISE_TIME_PER_REP) VALUES ('Squats', 'Bodyweight squats. Keep back straight, lower until thighs are parallel to ground.', 4)")
        db.execSQL("INSERT INTO $TABLE_EXERCISES ($COL_EXERCISE_NAME, $COL_EXERCISE_DESC, $COL_EXERCISE_TIME_PER_REP) VALUES ('Jumping Jacks', 'Cardio exercise. Jump while spreading legs and raising arms overhead.', 2)")
        db.execSQL("INSERT INTO $TABLE_EXERCISES ($COL_EXERCISE_NAME, $COL_EXERCISE_DESC, $COL_EXERCISE_TIME_PER_REP) VALUES ('Plank', 'Hold plank position with forearms on ground, body straight.', 1)")

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
        db.execSQL("INSERT INTO $TABLE_TRAINEE_CALENDAR_SLOT ($COL_TRAINEE_ID, $COL_SLOT_WORKOUT_ID, $COL_SLOT_ASSIGNMENT_ID, $COL_SLOT_STATUS, $COL_SLOT_DATE, $COL_SLOT_START_TIME, $COL_SLOT_END_TIME) VALUES (2, 1, 1, 0, '$today', '08:00', '09:00')")

        // 2. MAYBE without Workout (Available)
        db.execSQL("INSERT INTO $TABLE_TRAINEE_CALENDAR_SLOT ($COL_TRAINEE_ID, $COL_SLOT_WORKOUT_ID, $COL_SLOT_STATUS, $COL_SLOT_DATE, $COL_SLOT_START_TIME, $COL_SLOT_END_TIME) VALUES (2, NULL, 1, '$today', '10:00', '11:00')")

        // 3. BUSY without Workout (Unavailable)
        db.execSQL("INSERT INTO $TABLE_TRAINEE_CALENDAR_SLOT ($COL_TRAINEE_ID, $COL_SLOT_WORKOUT_ID, $COL_SLOT_STATUS, $COL_SLOT_DATE, $COL_SLOT_START_TIME, $COL_SLOT_END_TIME) VALUES (2, NULL, 2, '$today', '13:00', '14:00')")

        // 4. IDEAL without Workout (Available)
        db.execSQL("INSERT INTO $TABLE_TRAINEE_CALENDAR_SLOT ($COL_TRAINEE_ID, $COL_SLOT_WORKOUT_ID, $COL_SLOT_STATUS, $COL_SLOT_DATE, $COL_SLOT_START_TIME, $COL_SLOT_END_TIME) VALUES (2, NULL, 0, '$today', '15:00', '16:00')")

        // 5. MAYBE with Workout
        db.execSQL("INSERT INTO $TABLE_TRAINEE_CALENDAR_SLOT ($COL_TRAINEE_ID, $COL_SLOT_WORKOUT_ID, $COL_SLOT_ASSIGNMENT_ID, $COL_SLOT_STATUS, $COL_SLOT_DATE, $COL_SLOT_START_TIME, $COL_SLOT_END_TIME) VALUES (2, 1, 2, 1, '$today', '17:00', '18:00')")

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

        // Request data
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_PASSWORD, $COL_USER_ROLE, $COL_USER_NAME, $COL_USER_BIO) VALUES ('trainee5', 'pass123', 'trainee', 'Anna Lee', 'Interested in cardio training')")
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_PASSWORD, $COL_USER_ROLE, $COL_USER_NAME, $COL_USER_BIO) VALUES ('trainee6', 'pass123', 'trainee', 'David Kim', 'Wants to improve flexibility')")

        // Additional mock trainers (IDs 8, 9)
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_PASSWORD, $COL_USER_ROLE, $COL_USER_NAME, $COL_USER_BIO, $COL_USER_MAX_TRAINEES) VALUES ('trainer2', 'trainer123', 'trainer', 'Maya Chen', 'Strength and mobility coach focused on sustainable progress', 8)")
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_PASSWORD, $COL_USER_ROLE, $COL_USER_NAME, $COL_USER_BIO, $COL_USER_MAX_TRAINEES) VALUES ('trainer3', 'trainer123', 'trainer', 'Alex Rivera', 'Cardio and weight-loss specialist for beginner-friendly programs', 12)")
        db.execSQL("INSERT INTO $TABLE_USERS_TAGS ($COL_USER_ID, $COL_TAG_ID) VALUES (8, 1)")
        db.execSQL("INSERT INTO $TABLE_USERS_TAGS ($COL_USER_ID, $COL_TAG_ID) VALUES (8, 5)")
        db.execSQL("INSERT INTO $TABLE_USERS_TAGS ($COL_USER_ID, $COL_TAG_ID) VALUES (9, 2)")
        db.execSQL("INSERT INTO $TABLE_USERS_TAGS ($COL_USER_ID, $COL_TAG_ID) VALUES (9, 4)")

        // Login test trainee account
        db.execSQL("INSERT INTO $TABLE_USERS ($COL_USER_USERNAME, $COL_USER_PASSWORD, $COL_USER_ROLE, $COL_USER_NAME, $COL_USER_BIO) VALUES ('trainee', '123456', 'trainee', 'Test Trainee', 'Ready to find a trainer')")

        // Test trainee is enrolled with Alex Rivera (trainer ID 9, trainee ID 10)
        db.execSQL("INSERT INTO $TABLE_TRAINEE_REQUESTS ($COL_REQUEST_TRAINER_ID, $COL_REQUEST_TRAINEE_ID, $COL_REQUEST_STATUS) VALUES (9, 10, '$STATUS_ACCEPTED')")
        db.execSQL("INSERT INTO $TABLE_TRAINER_TRAINEES ($COL_TT_TRAINER_ID, $COL_TT_TRAINEE_ID) VALUES (9, 10)")
        db.execSQL("INSERT INTO $TABLE_TRAINEE_TRAINER ($COL_TTR_TRAINEE_ID, $COL_TTR_TRAINER_ID) VALUES (10, 9)")
        db.execSQL("INSERT INTO $TABLE_TRAINEE_CALENDAR_SLOT ($COL_TRAINEE_ID, $COL_SLOT_WORKOUT_ID, $COL_SLOT_ASSIGNMENT_ID, $COL_SLOT_STATUS, $COL_SLOT_DATE, $COL_SLOT_START_TIME, $COL_SLOT_END_TIME) VALUES (10, NULL, NULL, 0, '$today', '08:00', '09:00')")
        db.execSQL("INSERT INTO $TABLE_TRAINEE_CALENDAR_SLOT ($COL_TRAINEE_ID, $COL_SLOT_WORKOUT_ID, $COL_SLOT_ASSIGNMENT_ID, $COL_SLOT_STATUS, $COL_SLOT_DATE, $COL_SLOT_START_TIME, $COL_SLOT_END_TIME) VALUES (10, NULL, NULL, 1, '$today', '09:00', '10:00')")
        db.execSQL("INSERT INTO $TABLE_TRAINEE_CALENDAR_SLOT ($COL_TRAINEE_ID, $COL_SLOT_WORKOUT_ID, $COL_SLOT_ASSIGNMENT_ID, $COL_SLOT_STATUS, $COL_SLOT_DATE, $COL_SLOT_START_TIME, $COL_SLOT_END_TIME) VALUES (10, NULL, NULL, 0, '$today', '13:00', '14:00')")
        db.execSQL("INSERT INTO $TABLE_TRAINEE_CALENDAR_SLOT ($COL_TRAINEE_ID, $COL_SLOT_WORKOUT_ID, $COL_SLOT_ASSIGNMENT_ID, $COL_SLOT_STATUS, $COL_SLOT_DATE, $COL_SLOT_START_TIME, $COL_SLOT_END_TIME) VALUES (10, NULL, NULL, 2, '$today', '15:00', '16:00')")

        db.execSQL("INSERT INTO $TABLE_TRAINEE_REQUESTS ($COL_REQUEST_TRAINER_ID, $COL_REQUEST_TRAINEE_ID, $COL_REQUEST_STATUS) VALUES (1, 6, '$STATUS_PENDING')")
        db.execSQL("INSERT INTO $TABLE_TRAINEE_REQUESTS ($COL_REQUEST_TRAINER_ID, $COL_REQUEST_TRAINEE_ID, $COL_REQUEST_STATUS) VALUES (1, 7, '$STATUS_PENDING')")
        db.execSQL("INSERT INTO $TABLE_EXERCISE_TAGS ($COL_EXERCISE_ID, $COL_TAG_ID) VALUES (1, 1)") // strength
        db.execSQL("INSERT INTO $TABLE_EXERCISE_TAGS ($COL_EXERCISE_ID, $COL_TAG_ID) VALUES (1, 5)") // muscle-gain

// Exercise 2: Squats
        db.execSQL("INSERT INTO $TABLE_EXERCISE_TAGS ($COL_EXERCISE_ID, $COL_TAG_ID) VALUES (2, 1)") // strength
        db.execSQL("INSERT INTO $TABLE_EXERCISE_TAGS ($COL_EXERCISE_ID, $COL_TAG_ID) VALUES (2, 2)") // weight-loss
        db.execSQL("INSERT INTO $TABLE_EXERCISE_TAGS ($COL_EXERCISE_ID, $COL_TAG_ID) VALUES (2, 5)") // muscle-gain

// Exercise 3: Jumping Jacks
        db.execSQL("INSERT INTO $TABLE_EXERCISE_TAGS ($COL_EXERCISE_ID, $COL_TAG_ID) VALUES (3, 3)") // endurance
        db.execSQL("INSERT INTO $TABLE_EXERCISE_TAGS ($COL_EXERCISE_ID, $COL_TAG_ID) VALUES (3, 4)") // cardio
        db.execSQL("INSERT INTO $TABLE_EXERCISE_TAGS ($COL_EXERCISE_ID, $COL_TAG_ID) VALUES (3, 2)") // weight-loss

// Exercise 4: Plank
        db.execSQL("INSERT INTO $TABLE_EXERCISE_TAGS ($COL_EXERCISE_ID, $COL_TAG_ID) VALUES (4, 1)") // strength
        db.execSQL("INSERT INTO $TABLE_EXERCISE_TAGS ($COL_EXERCISE_ID, $COL_TAG_ID) VALUES (4, 3)") // endurance
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

    fun getPendingRequest(trainerId: Int): Cursor {
        val db = readableDatabase
        val query = """
            SELECT w.*
            FROM $TABLE_TRAINEE_REQUESTS s
                LEFT JOIN $TABLE_USERS w
                ON s.$COL_REQUEST_TRAINEE_ID = w.$COL_USER_ID
            WHERE s.$COL_REQUEST_TRAINER_ID = ?
            AND s.$COL_REQUEST_STATUS = ?
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(trainerId.toString(), STATUS_PENDING))
        return cursor
    }

    fun getPendingRequestsCount(trainerId: Int): Int {
        val db = readableDatabase
        val selection = "$COL_REQUEST_TRAINER_ID = ? AND $COL_REQUEST_STATUS = 'pending'"
        val cursor = db.query(TABLE_TRAINEE_REQUESTS, null, selection, arrayOf(trainerId.toString()), null, null, null)
        val count = cursor.count
        cursor.close()
        return count
    }

    fun insertExercise(name: String, desc: String, timePerRep: Int, tags: List<Tag>) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_EXERCISE_NAME, name)
            put(COL_EXERCISE_DESC, desc)
            put(COL_EXERCISE_TIME_PER_REP, timePerRep)
        }
        val exerciseId = db.insert(TABLE_EXERCISES, null, values)

        tags.forEach { tag ->
            val valueTag = ContentValues().apply {
                put(COL_EXERCISE_ID, exerciseId)
                put(COL_TAG_ID, tag.tagId)
            }

            db.insert(TABLE_EXERCISE_TAGS, null, valueTag)
        }
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
            SELECT e.$COL_EXERCISE_ID, e.$COL_EXERCISE_NAME, we.$COL_WE_REPS, e.$COL_EXERCISE_TIME_PER_REP, e.$COL_EXERCISE_DESC
            FROM $TABLE_WORKOUT_EXERCISES we
            JOIN $TABLE_EXERCISES e ON we.$COL_WE_EXERCISE_ID = e.$COL_EXERCISE_ID
            WHERE we.$COL_WE_WORKOUT_ID = ?
        """.trimIndent()
        return db.rawQuery(query, arrayOf(workoutId.toString()))
    }

    fun getAllTraineesForTrainer(trainerId: Int): Cursor {
        val db = readableDatabase
        val query = """
            SELECT u.* 
            FROM $TABLE_USERS u
            JOIN $TABLE_TRAINER_TRAINEES tt ON u.$COL_USER_ID = tt.$COL_TT_TRAINEE_ID
            WHERE tt.$COL_TT_TRAINER_ID = ?
        """.trimIndent()
        return db.rawQuery(query, arrayOf(trainerId.toString()))
    }

    fun getAllTrainers(): Cursor {
        val db = readableDatabase
        return db.query(
            TABLE_USERS,
            null,
            "$COL_USER_ROLE = ?",
            arrayOf("trainer"),
            null,
            null,
            COL_USER_NAME
        )
    }

    fun getTrainerById(trainerId: Int): Cursor {
        val db = readableDatabase
        return db.query(
            TABLE_USERS,
            null,
            "$COL_USER_ID = ? AND $COL_USER_ROLE = ?",
            arrayOf(trainerId.toString(), "trainer"),
            null,
            null,
            null
        )
    }

    fun getMyTrainerID(traineeUserId: Int): Int {
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

    fun getPendingTrainerRequestId(traineeUserId: Int): Int {
        val db = readableDatabase
        val query = """
            SELECT $COL_REQUEST_TRAINER_ID
            FROM $TABLE_TRAINEE_REQUESTS
            WHERE $COL_REQUEST_TRAINEE_ID = ?
            AND $COL_REQUEST_STATUS = ?
            LIMIT 1
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(traineeUserId.toString(), STATUS_PENDING))
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

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_REQUEST_TRAINER_ID, trainerId)
            put(COL_REQUEST_TRAINEE_ID, traineeId)
            put(COL_REQUEST_STATUS, STATUS_PENDING)
        }
        return db.insert(TABLE_TRAINEE_REQUESTS, null, values) != -1L
    }

    fun cancelTrainerRequest(trainerId: Int, traineeId: Int): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_REQUEST_STATUS, STATUS_REJECTED)
        }
        val condition = """
            $COL_REQUEST_TRAINER_ID = ?
            AND $COL_REQUEST_TRAINEE_ID = ?
            AND $COL_REQUEST_STATUS = ?
        """.trimIndent()
        return db.update(
            TABLE_TRAINEE_REQUESTS,
            values,
            condition,
            arrayOf(trainerId.toString(), traineeId.toString(), STATUS_PENDING)
        ) > 0
    }

    fun unrollTrainer(trainerId: Int, traineeId: Int): Boolean {
        val db = writableDatabase
        return try {
            db.beginTransaction()

            db.delete(
                TABLE_TRAINER_TRAINEES,
                "$COL_TT_TRAINER_ID = ? AND $COL_TT_TRAINEE_ID = ?",
                arrayOf(trainerId.toString(), traineeId.toString())
            )

            db.delete(
                TABLE_TRAINEE_TRAINER,
                "$COL_TTR_TRAINER_ID = ? AND $COL_TTR_TRAINEE_ID = ?",
                arrayOf(trainerId.toString(), traineeId.toString())
            )

            db.delete(
                TABLE_TRAINEE_REQUESTS,
                "$COL_REQUEST_TRAINER_ID = ? AND $COL_REQUEST_TRAINEE_ID = ?",
                arrayOf(trainerId.toString(), traineeId.toString())
            )

            db.delete(
                TABLE_WORKOUT_SCHEDULES,
                "$COL_SCHEDULE_TRAINEE_ID = ?",
                arrayOf(traineeId.toString())
            )

            db.delete(
                TABLE_WORKOUT_EXERCISE_COMPLETIONS,
                "$COL_COMPLETION_SLOT_ID IN (SELECT $COL_SLOT_ID FROM $TABLE_TRAINEE_CALENDAR_SLOT WHERE $COL_TRAINEE_ID = ?)",
                arrayOf(traineeId.toString())
            )

            val slotValues = ContentValues().apply {
                putNull(COL_SLOT_WORKOUT_ID)
                putNull(COL_SLOT_ASSIGNMENT_ID)
            }
            db.update(
                TABLE_TRAINEE_CALENDAR_SLOT,
                slotValues,
                "$COL_TRAINEE_ID = ?",
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
        ORDER BY s.$COL_SLOT_START_TIME ASC, s.$COL_SLOT_END_TIME ASC
    """.trimIndent()

        return db.rawQuery(
            query,
            arrayOf(
                traineeId.toString(),
                date.toString()
            )
        )
    }

    fun getWorkoutOptions(): Cursor {
        val db = readableDatabase
        return db.query(
            TABLE_WORKOUTS,
            arrayOf(COL_WORKOUT_ID, COL_WORKOUT_NAME, COL_WORKOUT_DURATION),
            null,
            null,
            null,
            null,
            COL_WORKOUT_NAME
        )
    }

    fun assignWorkoutToTraineeSlot(slotId: Int, workoutId: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_SLOT_WORKOUT_ID, workoutId)
            put(COL_SLOT_ASSIGNMENT_ID, getNextAssignmentId(db))
        }
        db.update(TABLE_TRAINEE_CALENDAR_SLOT, values, "$COL_SLOT_ID = ?", arrayOf(slotId.toString()))
        db.delete(TABLE_WORKOUT_EXERCISE_COMPLETIONS, "$COL_COMPLETION_SLOT_ID = ?", arrayOf(slotId.toString()))
    }

    fun assignWorkoutToTraineeSlots(slotIds: List<Int>, workoutId: Int): Boolean {
        if (slotIds.isEmpty()) return false

        val db = writableDatabase
        return try {
            db.beginTransaction()
            val assignmentId = getNextAssignmentId(db)
            slotIds.forEach { slotId ->
                val values = ContentValues().apply {
                    put(COL_SLOT_WORKOUT_ID, workoutId)
                    put(COL_SLOT_ASSIGNMENT_ID, assignmentId)
                }
                db.update(TABLE_TRAINEE_CALENDAR_SLOT, values, "$COL_SLOT_ID = ?", arrayOf(slotId.toString()))
                db.delete(TABLE_WORKOUT_EXERCISE_COMPLETIONS, "$COL_COMPLETION_SLOT_ID = ?", arrayOf(slotId.toString()))
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
        if (newSlotIds.isEmpty()) return false

        val db = writableDatabase
        return try {
            db.beginTransaction()
            val assignmentId = getAssignmentIdForSlots(db, oldSlotIds) ?: getNextAssignmentId(db)
            oldSlotIds.forEach { slotId ->
                val values = ContentValues().apply {
                    putNull(COL_SLOT_WORKOUT_ID)
                    putNull(COL_SLOT_ASSIGNMENT_ID)
                }
                db.update(TABLE_TRAINEE_CALENDAR_SLOT, values, "$COL_SLOT_ID = ?", arrayOf(slotId.toString()))
                db.delete(TABLE_WORKOUT_EXERCISE_COMPLETIONS, "$COL_COMPLETION_SLOT_ID = ?", arrayOf(slotId.toString()))
            }
            newSlotIds.forEach { slotId ->
                val values = ContentValues().apply {
                    put(COL_SLOT_WORKOUT_ID, workoutId)
                    put(COL_SLOT_ASSIGNMENT_ID, assignmentId)
                }
                db.update(TABLE_TRAINEE_CALENDAR_SLOT, values, "$COL_SLOT_ID = ?", arrayOf(slotId.toString()))
                db.delete(TABLE_WORKOUT_EXERCISE_COMPLETIONS, "$COL_COMPLETION_SLOT_ID = ?", arrayOf(slotId.toString()))
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

        val db = writableDatabase
        return try {
            db.beginTransaction()
            slotIds.forEach { slotId ->
                val values = ContentValues().apply {
                    putNull(COL_SLOT_WORKOUT_ID)
                    putNull(COL_SLOT_ASSIGNMENT_ID)
                }
                db.update(TABLE_TRAINEE_CALENDAR_SLOT, values, "$COL_SLOT_ID = ?", arrayOf(slotId.toString()))
                db.delete(TABLE_WORKOUT_EXERCISE_COMPLETIONS, "$COL_COMPLETION_SLOT_ID = ?", arrayOf(slotId.toString()))
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

    fun clearWorkoutFromTraineeSlot(slotId: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            putNull(COL_SLOT_WORKOUT_ID)
            putNull(COL_SLOT_ASSIGNMENT_ID)
        }
        db.update(TABLE_TRAINEE_CALENDAR_SLOT, values, "$COL_SLOT_ID = ?", arrayOf(slotId.toString()))
        db.delete(TABLE_WORKOUT_EXERCISE_COMPLETIONS, "$COL_COMPLETION_SLOT_ID = ?", arrayOf(slotId.toString()))
    }

    private fun getNextAssignmentId(db: SQLiteDatabase): Int {
        val cursor = db.rawQuery(
            "SELECT COALESCE(MAX($COL_SLOT_ASSIGNMENT_ID), 0) + 1 FROM $TABLE_TRAINEE_CALENDAR_SLOT",
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
                SELECT $COL_SLOT_ASSIGNMENT_ID
                FROM $TABLE_TRAINEE_CALENDAR_SLOT
                WHERE $COL_SLOT_ID IN ($placeholders)
                AND $COL_SLOT_ASSIGNMENT_ID IS NOT NULL
                LIMIT 1
            """.trimIndent(),
            slotIds.map { it.toString() }.toTypedArray()
        )
        val assignmentId = if (cursor.moveToFirst()) cursor.getInt(0) else null
        cursor.close()
        return assignmentId
    }

    fun addTraineeCalendarSlot(
        traineeId: Int,
        date: LocalDate,
        startTime: String,
        endTime: String,
        status: Int
    ): Boolean {
        if (hasTraineeSlotAtTime(traineeId, date, startTime, endTime)) {
            return false
        }

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_TRAINEE_ID, traineeId)
            putNull(COL_SLOT_WORKOUT_ID)
            putNull(COL_SLOT_ASSIGNMENT_ID)
            put(COL_SLOT_STATUS, status)
            put(COL_SLOT_DATE, date.toString())
            put(COL_SLOT_START_TIME, startTime)
            put(COL_SLOT_END_TIME, endTime)
        }
        return db.insert(TABLE_TRAINEE_CALENDAR_SLOT, null, values) != -1L
    }

    fun updateTraineeCalendarSlot(
        slotId: Int,
        traineeId: Int,
        date: LocalDate,
        startTime: String,
        endTime: String,
        status: Int
    ): Boolean {
        if (hasTraineeSlotAtTime(traineeId, date, startTime, endTime, excludedSlotId = slotId)) {
            return false
        }

        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_SLOT_STATUS, status)
            put(COL_SLOT_START_TIME, startTime)
            put(COL_SLOT_END_TIME, endTime)
        }
        return db.update(TABLE_TRAINEE_CALENDAR_SLOT, values, "$COL_SLOT_ID = ?", arrayOf(slotId.toString())) > 0
    }

    fun deleteTraineeCalendarSlot(slotId: Int): Boolean {
        val db = writableDatabase
        db.delete(TABLE_WORKOUT_EXERCISE_COMPLETIONS, "$COL_COMPLETION_SLOT_ID = ?", arrayOf(slotId.toString()))
        return db.delete(TABLE_TRAINEE_CALENDAR_SLOT, "$COL_SLOT_ID = ?", arrayOf(slotId.toString())) > 0
    }

    private fun hasTraineeSlotAtTime(
        traineeId: Int,
        date: LocalDate,
        startTime: String,
        endTime: String,
        excludedSlotId: Int? = null
    ): Boolean {
        val db = readableDatabase
        val selection = buildString {
            append("$COL_TRAINEE_ID = ? AND $COL_SLOT_DATE = ? AND $COL_SLOT_START_TIME = ? AND $COL_SLOT_END_TIME = ?")
            if (excludedSlotId != null) {
                append(" AND $COL_SLOT_ID != ?")
            }
        }
        val args = mutableListOf(traineeId.toString(), date.toString(), startTime, endTime)
        if (excludedSlotId != null) {
            args.add(excludedSlotId.toString())
        }
        val cursor = db.query(
            TABLE_TRAINEE_CALENDAR_SLOT,
            arrayOf(COL_SLOT_ID),
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

    fun getCompletedExerciseIdsForSlot(slotId: Int): Cursor {
        val db = readableDatabase
        return db.query(
            TABLE_WORKOUT_EXERCISE_COMPLETIONS,
            arrayOf(COL_COMPLETION_EXERCISE_ID),
            "$COL_COMPLETION_SLOT_ID = ?",
            arrayOf(slotId.toString()),
            null,
            null,
            null
        )
    }

    fun markWorkoutExerciseComplete(slotId: Int, exerciseId: Long): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_COMPLETION_SLOT_ID, slotId)
            put(COL_COMPLETION_EXERCISE_ID, exerciseId)
            put(COL_COMPLETION_COMPLETED_AT, System.currentTimeMillis().toString())
        }
        return db.insertWithOnConflict(
            TABLE_WORKOUT_EXERCISE_COMPLETIONS,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE
        ) != -1L
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

    fun getAllTags(): Cursor {
        val db = readableDatabase
        return db.query(
            TABLE_TAGS,
            null,
            null,
            null,
            null,
            null,
            COL_TAG_NAME
        )
    }

    fun updateUserProfile(userId: Int, name: String, bio: String, maxTrainees: Int? = null, password: String?) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_USER_NAME, name)
            put(COL_USER_BIO, bio)
            if (maxTrainees != null) {
                put(COL_USER_MAX_TRAINEES, maxTrainees)
            }
            if (password != null) {
                put(COL_USER_PASSWORD, password)
            }
        }
        db.update(TABLE_USERS, values, "$COL_USER_ID = ?", arrayOf(userId.toString()))
    }

    fun updateUserTags(userId: Int, tags: List<Tag>) {
        val db = writableDatabase
        db.delete(TABLE_USERS_TAGS, "$COL_USER_ID = ?", arrayOf(userId.toString()))

        tags.forEach { tag ->
            val values = ContentValues().apply {
                put(COL_USER_ID, userId)
                put(COL_TAG_ID, tag.tagId)
            }
            db.insert(TABLE_USERS_TAGS, null, values)
        }
    }

    fun getTrainerMaxTrainees(trainerId: Int): Int {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_USERS,
            arrayOf(COL_USER_MAX_TRAINEES),
            "$COL_USER_ID = ?",
            arrayOf(trainerId.toString()),
            null,
            null,
            null
        )

        val maxTrainees = if (cursor.moveToFirst()) {
            cursor.getInt(cursor.getColumnIndexOrThrow(COL_USER_MAX_TRAINEES))
        } else {
            0
        }
        cursor.close()
        return maxTrainees
    }

    fun acceptTrainee(trainerId: Int, traineeId: Int): Boolean {
        val db = writableDatabase
        val maxTrainees = getTrainerMaxTrainees(trainerId)
        if (maxTrainees > 0 && getActiveTraineesCount(trainerId) >= maxTrainees) {
            return false
        }

        return try {
            db.beginTransaction()
            val query = ContentValues().apply {
                put(COL_TT_TRAINER_ID, trainerId)
                put(COL_TT_TRAINEE_ID, traineeId)
            }
            db.insert(TABLE_TRAINER_TRAINEES, null, query)

            val query2 = ContentValues().apply {
                put(COL_TTR_TRAINER_ID, trainerId)
                put(COL_TTR_TRAINEE_ID, traineeId)
            }
            db.insert(TABLE_TRAINEE_TRAINER, null, query2)

            val query3 = ContentValues().apply {
                put(COL_REQUEST_STATUS, STATUS_ACCEPTED)
            }
            val condition = "$COL_REQUEST_TRAINER_ID = ? AND $COL_REQUEST_TRAINEE_ID = ?"
            db.update(TABLE_TRAINEE_REQUESTS, query3, condition, arrayOf(trainerId.toString(), traineeId.toString()))

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.endTransaction()
        }
    }

    fun denyTrainee(trainerId: Int, traineeId: Int): Boolean  {
        val db = writableDatabase
        return try {
            db.beginTransaction()
            val query = ContentValues().apply {
                put(COL_REQUEST_STATUS, STATUS_REJECTED)
            }
            val condition = "$COL_REQUEST_TRAINER_ID = ? AND $COL_REQUEST_TRAINEE_ID = ?"
            db.update(TABLE_TRAINEE_REQUESTS, query, condition, arrayOf(trainerId.toString(), traineeId.toString()))

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            db.endTransaction()
        }
    }

    fun getExerciseTags(exerciseId: Long): Cursor {
        val db = readableDatabase
        val query = """
            SELECT w.$COL_TAG_ID, w.$COL_TAG_NAME
            FROM $TABLE_EXERCISE_TAGS s
            LEFT JOIN $TABLE_TAGS w
                ON s.$COL_TAG_ID = w.$COL_TAG_ID
            WHERE s.$COL_EXERCISE_ID = ?
        """.trimIndent()

        return db.rawQuery(
            query,
            arrayOf(exerciseId.toString())
        )
    }

    fun deleteWorkout(workoutId: Int) {
        val db = writableDatabase
        db.delete(TABLE_WORKOUT_EXERCISES, "$COL_WE_WORKOUT_ID = ?", arrayOf(workoutId.toString()))
        db.delete(TABLE_WORKOUT_SCHEDULES, "$COL_SCHEDULE_WORKOUT_ID = ?", arrayOf(workoutId.toString()))
        db.delete(TABLE_TRAINEE_CALENDAR_SLOT, "$COL_SLOT_WORKOUT_ID = ?", arrayOf(workoutId.toString()))
        db.delete(TABLE_WORKOUTS, "$COL_WORKOUT_ID = ?", arrayOf(workoutId.toString()))
    }

    fun deleteExercise(exerciseId: Int) {
        val db = writableDatabase
        val affectedWorkoutIds = getWorkoutIdsForExercise(db, exerciseId)
        db.delete(TABLE_EXERCISE_TAGS, "$COL_EXERCISE_ID = ?", arrayOf(exerciseId.toString()))
        db.delete(TABLE_WORKOUT_EXERCISES, "$COL_WE_EXERCISE_ID = ?", arrayOf(exerciseId.toString()))
        db.delete(TABLE_EXERCISES, "$COL_EXERCISE_ID = ?", arrayOf(exerciseId.toString()))
        affectedWorkoutIds.forEach { workoutId ->
            updateWorkoutDuration(workoutId, calculateWorkoutTotalDuration(workoutId))
        }
    }

    fun updateExercise(id: Int, name: String, desc: String, timePerRep: Int, tags: List<Tag>) {
        val db = writableDatabase
        val affectedWorkoutIds = getWorkoutIdsForExercise(db, id)
        val values = ContentValues().apply {
            put(COL_EXERCISE_NAME, name)
            put(COL_EXERCISE_DESC, desc)
            put(COL_EXERCISE_TIME_PER_REP, timePerRep)
        }
        db.update(TABLE_EXERCISES, values, "$COL_EXERCISE_ID = ?", arrayOf(id.toString()))

        // Update tags: clear and re-add
        db.delete(TABLE_EXERCISE_TAGS, "$COL_EXERCISE_ID = ?", arrayOf(id.toString()))
        tags.forEach { tag ->
            val valueTag = ContentValues().apply {
                put(COL_EXERCISE_ID, id)
                put(COL_TAG_ID, tag.tagId)
            }
            db.insert(TABLE_EXERCISE_TAGS, null, valueTag)
        }
        affectedWorkoutIds.forEach { workoutId ->
            updateWorkoutDuration(workoutId, calculateWorkoutTotalDuration(workoutId))
        }
    }

    private fun getWorkoutIdsForExercise(db: SQLiteDatabase, exerciseId: Int): List<Long> {
        val workoutIds = mutableListOf<Long>()
        val cursor = db.query(
            TABLE_WORKOUT_EXERCISES,
            arrayOf(COL_WE_WORKOUT_ID),
            "$COL_WE_EXERCISE_ID = ?",
            arrayOf(exerciseId.toString()),
            null,
            null,
            null
        )

        cursor.use {
            while (it.moveToNext()) {
                workoutIds.add(it.getLong(it.getColumnIndexOrThrow(COL_WE_WORKOUT_ID)))
            }
        }

        return workoutIds
    }
}
