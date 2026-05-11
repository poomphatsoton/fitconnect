package com.example.train.viewmodel.reuseComponent

import android.database.Cursor
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.train.database.DatabaseHelper
import com.example.train.model.trainer.Exercise
import com.example.train.model.trainer.TraineeSlot
import com.example.train.model.trainer.Workout
import com.example.train.model.trainer.WorkoutExerciseDetail
import com.example.train.model.trainer.WorkoutTagPercent
import com.example.train.model.trainer.WorkoutUiItem
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.roundToInt

class WorkoutDetailLoader(private val dbHelper: DatabaseHelper) {

    fun loadWorkoutDetailsBySlotId(slots: List<TraineeSlot>): Map<Int, WorkoutUiItem> {
        return slots.mapNotNull { slot ->
            val workoutId = slot.workoutId ?: return@mapNotNull null
            val detail = loadWorkoutDetail(workoutId, slot.assignmentId) ?: return@mapNotNull null
            slot.slotId to detail
        }.toMap()
    }

    fun loadWorkoutDetail(workoutId: Int, assignmentId: Int?): WorkoutUiItem? {
        val cursor = dbHelper.getWorkoutById(workoutId)

        cursor.use {
            if (!it.moveToFirst()) return null

            val snapshotDetails = assignmentId?.let { id -> loadSnapshotWorkoutExerciseDetails(id) }.orEmpty()
            val exerciseDetails = snapshotDetails.ifEmpty { loadWorkoutExerciseDetails(workoutId.toLong()) }
            val workout = Workout(
                id = it.getInt(it.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_ID)),
                name = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_NAME)),
                description = it.getString(it.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_DESC)),
                duration = exerciseDetails.sumOf { detail -> detail.reps * detail.timePerRep },
                exercises = exerciseDetails.map { detail ->
                    Exercise(
                        id = detail.exerciseId.toInt(),
                        name = detail.name,
                        description = detail.description,
                        timePerRep = detail.timePerRep
                    )
                }
            )

            return WorkoutUiItem(
                workout = workout,
                exerciseDetails = exerciseDetails,
                tagPercents = calculateWorkoutTagPercents(workoutId.toLong(), exerciseDetails)
            )
        }
    }

    fun loadWorkoutExerciseDetails(workoutId: Long): List<WorkoutExerciseDetail> {
        val details = mutableListOf<WorkoutExerciseDetail>()
        dbHelper.getWorkoutExerciseDetails(workoutId).use { cursor ->
            while (cursor.moveToNext()) {
                details.add(cursor.toWorkoutExerciseDetail())
            }
        }
        return details
    }

    fun loadSnapshotWorkoutExerciseDetails(assignmentId: Int): List<WorkoutExerciseDetail> {
        val details = mutableListOf<WorkoutExerciseDetail>()
        dbHelper.getSnapshotWorkoutExerciseDetails(assignmentId).use { cursor ->
            while (cursor.moveToNext()) {
                details.add(cursor.toWorkoutExerciseDetail())
            }
        }
        return details
    }

    fun calculateWorkoutTagPercents(
        workoutId: Long,
        exerciseDetails: List<WorkoutExerciseDetail>
    ): List<WorkoutTagPercent> {
        val totalTime = exerciseDetails.sumOf { it.reps * it.timePerRep }
        if (totalTime <= 0) return emptyList()

        val tagsByExercise = mutableMapOf<Long, MutableList<String>>()
        dbHelper.getWorkoutExerciseTagTimes(workoutId).use { cursor ->
            while (cursor.moveToNext()) {
                val exerciseId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_ID))
                val tagName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TAG_NAME))
                tagsByExercise.getOrPut(exerciseId) { mutableListOf() }.add(tagName)
            }
        }

        val tagTimes = mutableMapOf<String, Double>()
        exerciseDetails.forEach { detail ->
            val tags = tagsByExercise[detail.exerciseId].orEmpty()
            if (tags.isNotEmpty()) {
                val sharedTime = (detail.reps * detail.timePerRep).toDouble() / tags.size
                tags.forEach { tagName ->
                    tagTimes[tagName] = (tagTimes[tagName] ?: 0.0) + sharedTime
                }
            }
        }

        return tagTimes.map { (tagName, tagTime) ->
            WorkoutTagPercent(
                tagName = tagName,
                percent = (tagTime / totalTime * 100).roundToInt()
            )
        }.sortedByDescending { it.percent }
    }
}

fun Cursor.toWorkoutExerciseDetail(): WorkoutExerciseDetail {
    return WorkoutExerciseDetail(
        exerciseId = getLong(getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_ID)),
        name = getString(getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_NAME)),
        reps = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_WE_REPS)),
        timePerRep = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_TIME_PER_REP)),
        description = getString(getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_DESC)),
        videoUrl = getString(getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_VIDEO_URL)),
        videoName = getString(getColumnIndexOrThrow(DatabaseHelper.COL_EXERCISE_VIDEO_NAME))
    )
}

@RequiresApi(Build.VERSION_CODES.O)
fun Cursor.toTraineeSlot(): TraineeSlot {
    val workoutIdIndex = getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_WORKOUT_ID)
    val assignmentIdIndex = getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_ASSIGNMENT_ID)
    val workoutNameIndex = getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_NAME)

    return TraineeSlot(
        slotId = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_ID)),
        workoutId = if (isNull(workoutIdIndex)) null else getInt(workoutIdIndex),
        assignmentId = if (isNull(assignmentIdIndex)) null else getInt(assignmentIdIndex),
        workoutName = if (isNull(workoutNameIndex)) null else getString(workoutNameIndex),
        status = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_STATUS)),
        startTime = LocalTime.parse(getString(getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_START_TIME))),
        endTime = LocalTime.parse(getString(getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_END_TIME))),
        date = LocalDate.parse(getString(getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_DATE)))
    )
}
