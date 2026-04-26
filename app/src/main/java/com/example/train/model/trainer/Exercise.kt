package com.example.train.model.trainer

data class Exercise(
    var id: Int = 0,
    var name: String? = null,
    var description: String? = null,
    var category1: String? = null,
    var category2: String? = null,
    var timePerRep: Int = 0
)

data class ExerciseSelectUiItem(
    val id: Long,
    val name: String,
    val isSelected: Boolean = false,
    val reps: String = ""
)