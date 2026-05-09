package com.example.train.model.trainer

import java.io.Serializable

data class Workout(
    var id: Int = 0,
    var name: String? = null,
    var description: String? = null,
    var duration: Int = 0,
    var exercises: List<Exercise> = emptyList()
) : Serializable

data class WorkoutExerciseDetail(
    val exerciseId: Long,
    val name: String,
    val reps: Int,
    val timePerRep: Int,
    val description: String = "",
)

data class WorkoutUiItem(
    val workout: Workout,
    val exerciseDetails: List<WorkoutExerciseDetail>,
)
