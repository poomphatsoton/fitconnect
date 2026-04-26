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
    val name: String,
    val reps: Int,
    val timePerRep: Int,
    val category1: String?,
    val category2: String?
)

data class WorkoutCategoryPercent(
    val category: String,
    val percent: Int
)

data class WorkoutUiItem(
    val workout: Workout,
    val exerciseDetails: List<WorkoutExerciseDetail>,
    val categoryPercents: List<WorkoutCategoryPercent>
)
