package com.example.train.model.trainee

import com.example.train.model.trainer.WorkoutUiItem
import java.time.LocalTime

data class TraineeWorkoutUiState(
    val currentTime: String = "",
    val workouts: List<TraineeAssignedWorkout> = emptyList()
)

data class TraineeAssignedWorkout(
    val slotId: Int,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val workoutItem: WorkoutUiItem,
    val completedExerciseIds: Set<Long> = emptySet()
) {
    val isComplete: Boolean
        get() = workoutItem.exerciseDetails.isNotEmpty() &&
            workoutItem.exerciseDetails.all { completedExerciseIds.contains(it.exerciseId) }
}
