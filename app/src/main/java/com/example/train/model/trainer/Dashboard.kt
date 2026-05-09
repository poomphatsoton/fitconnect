package com.example.train.model.trainer

import com.example.train.model.Tag
import java.time.LocalDate
import java.time.LocalTime

data class TraineeDashboardUiState(
    val trainee: TraineeDashboardProfile? = null,
    val workouts: List<TraineeDashboardWorkout> = emptyList()
)

data class TraineeDashboardProfile(
    val id: Int,
    val name: String,
    val bio: String,
    val tags: List<Tag> = emptyList(),
    val imageRes: Int? = null
)

data class TraineeDashboardWorkout(
    val assignmentId: Int,
    val workoutId: Int,
    val workoutName: String,
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val completedExerciseTime: Int,
    val totalExerciseTime: Int
) {
    val completionPercent: Float
        get() = if (totalExerciseTime <= 0) 0f else
            (completedExerciseTime.toFloat() / totalExerciseTime.toFloat() * 100f).coerceIn(0f, 100f)
}
