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
    val videoUrl: String? = null,
    val videoName: String? = null
)

data class WorkoutTagPercent(
    val tagName: String,
    val percent: Int
)

data class WorkoutUiItem(
    val workout: Workout,
    val exerciseDetails: List<WorkoutExerciseDetail>,
    val tagPercents: List<WorkoutTagPercent> = emptyList()
)
