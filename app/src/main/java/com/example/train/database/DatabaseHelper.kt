package com.example.train.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.train.database.helper.CalendarHelper
import com.example.train.database.helper.DashboardHelper
import com.example.train.database.helper.ExerciseHelper
import com.example.train.database.helper.TagHelper
import com.example.train.database.helper.TrainerTraineeHelper
import com.example.train.database.helper.UserHelper
import com.example.train.database.helper.WorkoutHelper
import com.example.train.model.Tag
import java.time.LocalDate

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "FitConnect.db"
        private const val DATABASE_VERSION = 39

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
        const val COL_EXERCISE_VIDEO_URL = "video_url"
        const val COL_EXERCISE_VIDEO_NAME = "video_name"

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

        const val TABLE_SNAPSHOT_WORKOUT = "snapshotWorkout"
        const val COL_SNAPSHOT_ID = "snapshot_id"
        const val COL_SNAPSHOT_ASSIGNMENT_ID = "assignment_id"
        const val COL_SNAPSHOT_WORKOUT_ID = "workout_id"
        const val COL_SNAPSHOT_EXERCISE_ID = "exercise_id"
        const val COL_SNAPSHOT_EXERCISE_NAME = "exercise_name"
        const val COL_SNAPSHOT_EXERCISE_DESC = "exercise_description"
        const val COL_SNAPSHOT_TIME_PER_REP = "time_per_rep"
        const val COL_SNAPSHOT_REPS = "reps"
        const val COL_SNAPSHOT_EXERCISE_TOTAL_TIME = "exercise_total_time"

        const val TABLE_WORKOUT_ASSIGNMENT_PROGRESS = "workoutAssignmentProgress"
        const val COL_PROGRESS_ASSIGNMENT_ID = "assignment_id"
        const val COL_PROGRESS_WORKOUT_ID = "workout_id"
        const val COL_PROGRESS_COMPLETED_EXERCISE_TIME = "completed_exercise_time"
        const val COL_PROGRESS_TOTAL_EXERCISE_TIME = "total_exercise_time"

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
        db.execSQL(CREATE_TABLE_SNAPSHOT_WORKOUT)
        db.execSQL(CREATE_TABLE_WORKOUT_ASSIGNMENT_PROGRESS)
        db.execSQL(CREATE_TABLE_TRAINEE_REQUESTS)
        db.execSQL(CREATE_TABLE_WORKOUT_SCHEDULES)
        db.execSQL(CREATE_TABLE_TRAINEE_CALENDAR_SLOT)
        db.execSQL(CREATE_TABLE_TRAINER_TRAINEES)
        db.execSQL(CREATE_TABLE_TRAINEE_TRAINER)
        db.execSQL(CREATE_TABLE_TAGS)
        db.execSQL(CREATE_TABLE_USER_TAGS)
        db.execSQL(CREATE_TABLE_EXERCISE_TAGS)
        CreateDeomo.insertDemoData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS_TAGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXERCISE_TAGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRAINEE_TRAINER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRAINER_TRAINEES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRAINEE_CALENDAR_SLOT")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUT_SCHEDULES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRAINEE_REQUESTS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WORKOUT_ASSIGNMENT_PROGRESS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SNAPSHOT_WORKOUT")
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
            $COL_EXERCISE_TIME_PER_REP INTEGER,
            $COL_EXERCISE_VIDEO_URL TEXT,
            $COL_EXERCISE_VIDEO_NAME TEXT
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

    private val CREATE_TABLE_SNAPSHOT_WORKOUT = """
        CREATE TABLE IF NOT EXISTS $TABLE_SNAPSHOT_WORKOUT (
            $COL_SNAPSHOT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COL_SNAPSHOT_ASSIGNMENT_ID INTEGER NOT NULL,
            $COL_SNAPSHOT_WORKOUT_ID INTEGER NOT NULL,
            $COL_SNAPSHOT_EXERCISE_ID INTEGER NOT NULL,
            $COL_SNAPSHOT_EXERCISE_NAME TEXT NOT NULL,
            $COL_SNAPSHOT_EXERCISE_DESC TEXT,
            $COL_SNAPSHOT_TIME_PER_REP INTEGER NOT NULL,
            $COL_SNAPSHOT_REPS INTEGER NOT NULL,
            $COL_SNAPSHOT_EXERCISE_TOTAL_TIME INTEGER NOT NULL,
            FOREIGN KEY($COL_SNAPSHOT_WORKOUT_ID) REFERENCES $TABLE_WORKOUTS($COL_WORKOUT_ID)
        )
    """.trimIndent()

    private val CREATE_TABLE_WORKOUT_ASSIGNMENT_PROGRESS = """
        CREATE TABLE IF NOT EXISTS $TABLE_WORKOUT_ASSIGNMENT_PROGRESS (
            $COL_PROGRESS_ASSIGNMENT_ID INTEGER PRIMARY KEY,
            $COL_PROGRESS_WORKOUT_ID INTEGER NOT NULL,
            $COL_PROGRESS_COMPLETED_EXERCISE_TIME INTEGER NOT NULL DEFAULT 0,
            $COL_PROGRESS_TOTAL_EXERCISE_TIME INTEGER NOT NULL DEFAULT 0
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

    private val dashboardHelper by lazy { DashboardHelper(this) }
    private val userHelper by lazy { UserHelper(this) }
    private val exerciseHelper by lazy { ExerciseHelper(this) }
    private val workoutHelper by lazy { WorkoutHelper(this) }
    private val trainerTraineeHelper by lazy { TrainerTraineeHelper(this) }
    private val calendarHelper by lazy { CalendarHelper(this) }
    private val tagHelper by lazy { TagHelper(this) }

    fun getActiveTraineesCount(trainerId: Int): Int = dashboardHelper.getActiveTraineesCount(trainerId)

    fun getExercisesCount(): Int = dashboardHelper.getExercisesCount()

    fun getWorkoutsCount(): Int = dashboardHelper.getWorkoutsCount()

    fun getPendingRequestsCount(trainerId: Int): Int = dashboardHelper.getPendingRequestsCount(trainerId)

    fun getTraineeDashboardWorkouts(traineeId: Int): Cursor = dashboardHelper.getTraineeDashboardWorkouts(traineeId)

    fun getUserByUsername(username: String): Cursor = userHelper.getUserByUsername(username)

    fun getUserById(userId: Int): Cursor = userHelper.getUserById(userId)

    fun getTraineeUserById(traineeId: Int): Cursor = userHelper.getTraineeUserById(traineeId)

    fun insertUser(username: String, password: String, role: String, name: String, bio: String): Long =
        userHelper.insertUser(username, password, role, name, bio)

    fun updateUserProfile(userId: Int, name: String, bio: String, maxTrainees: Int? = null, password: String?) =
        userHelper.updateUserProfile(userId, name, bio, maxTrainees, password)

    fun insertExercise(name: String, desc: String, timePerRep: Int, tags: List<Tag>): Long =
        exerciseHelper.insertExercise(name, desc, timePerRep, tags)

    fun getAllExercises(): Cursor = exerciseHelper.getAllExercises()

    fun getExerciseById(exerciseId: Int): Cursor = exerciseHelper.getExerciseById(exerciseId)

    fun deleteExercise(exerciseId: Int) = exerciseHelper.deleteExercise(exerciseId)

    fun updateExercise(id: Int, name: String, desc: String, timePerRep: Int, tags: List<Tag>) =
        exerciseHelper.updateExercise(id, name, desc, timePerRep, tags)

    fun insertWorkout(name: String, desc: String, duration: Int): Long = workoutHelper.insertWorkout(name, desc, duration)

    fun addExerciseToWorkout(workoutId: Long, exerciseId: Long, reps: Int) =
        workoutHelper.addExerciseToWorkout(workoutId, exerciseId, reps)

    fun getAllWorkouts(): Cursor = workoutHelper.getAllWorkouts()

    fun getWorkoutById(workoutId: Int): Cursor = workoutHelper.getWorkoutById(workoutId)

    fun getWorkoutExercises(workoutId: Int): Cursor = workoutHelper.getWorkoutExercises(workoutId)

    fun replaceWorkoutExercises(workoutId: Int, name: String, desc: String, exercises: List<Pair<Long, Int>>) =
        workoutHelper.replaceWorkoutExercises(workoutId, name, desc, exercises)

    fun calculateWorkoutTotalDuration(workoutId: Long): Int = workoutHelper.calculateWorkoutTotalDuration(workoutId)

    fun updateWorkoutDuration(workoutId: Long, duration: Int) = workoutHelper.updateWorkoutDuration(workoutId, duration)

    fun getWorkoutExerciseDetails(workoutId: Long): Cursor = workoutHelper.getWorkoutExerciseDetails(workoutId)

    fun getWorkoutExerciseTagTimes(workoutId: Long): Cursor = workoutHelper.getWorkoutExerciseTagTimes(workoutId)

    fun getWorkoutOptions(): Cursor = workoutHelper.getWorkoutOptions()

    fun deleteWorkout(workoutId: Int) = workoutHelper.deleteWorkout(workoutId)

    fun getPendingRequest(trainerId: Int): Cursor = trainerTraineeHelper.getPendingRequest(trainerId)

    fun getAllTraineesForTrainer(trainerId: Int): Cursor = trainerTraineeHelper.getAllTraineesForTrainer(trainerId)

    fun getAllTrainers(): Cursor = trainerTraineeHelper.getAllTrainers()

    fun getTrainerById(trainerId: Int): Cursor = trainerTraineeHelper.getTrainerById(trainerId)

    fun getMyTrainerID(traineeUserId: Int): Int = trainerTraineeHelper.getMyTrainerID(traineeUserId)

    fun getPendingTrainerRequestId(traineeUserId: Int): Int =
        trainerTraineeHelper.getPendingTrainerRequestId(traineeUserId)

    fun requestTrainer(trainerId: Int, traineeId: Int): Boolean = trainerTraineeHelper.requestTrainer(trainerId, traineeId)

    fun cancelTrainerRequest(trainerId: Int, traineeId: Int): Boolean =
        trainerTraineeHelper.cancelTrainerRequest(trainerId, traineeId)

    fun unrollTrainer(trainerId: Int, traineeId: Int): Boolean = trainerTraineeHelper.unrollTrainer(trainerId, traineeId)

    fun getTrainerMaxTrainees(trainerId: Int): Int = trainerTraineeHelper.getTrainerMaxTrainees(trainerId)

    fun acceptTrainee(trainerId: Int, traineeId: Int): Boolean = trainerTraineeHelper.acceptTrainee(trainerId, traineeId)

    fun denyTrainee(trainerId: Int, traineeId: Int): Boolean = trainerTraineeHelper.denyTrainee(trainerId, traineeId)

    fun getSnapshotWorkoutExerciseDetails(assignmentId: Int): Cursor =
        calendarHelper.getSnapshotWorkoutExerciseDetails(assignmentId)

    fun getTraineeSlots(traineeId: Int, date: LocalDate): Cursor = calendarHelper.getTraineeSlots(traineeId, date)

    fun assignWorkoutToTraineeSlots(slotIds: List<Int>, workoutId: Int): Boolean =
        calendarHelper.assignWorkoutToTraineeSlots(slotIds, workoutId)

    fun replaceWorkoutOnTraineeSlots(oldSlotIds: List<Int>, newSlotIds: List<Int>, workoutId: Int): Boolean =
        calendarHelper.replaceWorkoutOnTraineeSlots(oldSlotIds, newSlotIds, workoutId)

    fun clearWorkoutFromTraineeSlots(slotIds: List<Int>): Boolean = calendarHelper.clearWorkoutFromTraineeSlots(slotIds)

    fun addTraineeCalendarSlot(
        traineeId: Int,
        date: LocalDate,
        startTime: String,
        endTime: String,
        status: Int
    ): Boolean = calendarHelper.addTraineeCalendarSlot(traineeId, date, startTime, endTime, status)

    fun updateTraineeCalendarSlot(
        slotId: Int,
        traineeId: Int,
        date: LocalDate,
        startTime: String,
        endTime: String,
        status: Int
    ): Boolean = calendarHelper.updateTraineeCalendarSlot(slotId, traineeId, date, startTime, endTime, status)

    fun deleteTraineeCalendarSlot(slotId: Int): Boolean = calendarHelper.deleteTraineeCalendarSlot(slotId)

    fun getCompletedExerciseIdsForSlot(slotId: Int): Cursor = calendarHelper.getCompletedExerciseIdsForSlot(slotId)

    fun markWorkoutExerciseComplete(slotId: Int, exerciseId: Long): Boolean =
        calendarHelper.markWorkoutExerciseComplete(slotId, exerciseId)

    fun getUserTags(userId: Int): Cursor = tagHelper.getUserTags(userId)

    fun getAllTags(): Cursor = tagHelper.getAllTags()

    fun updateUserTags(userId: Int, tags: List<Tag>) = tagHelper.updateUserTags(userId, tags)

    fun getExerciseTags(exerciseId: Long): Cursor = tagHelper.getExerciseTags(exerciseId)
}
