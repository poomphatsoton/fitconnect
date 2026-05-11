package com.example.train.model.trainer

import com.example.train.model.Tag

data class Exercise(
    var id: Int = 0,
    var name: String? = null,
    var description: String? = null,
    var timePerRep: Int = 0,
    var videoUrl: String? = null,
    var videoName: String? = null,
    var exerciseTags: List<Tag> = emptyList(),
)

data class ExerciseSelectUiItem(
    val id: Long,
    val name: String,
    val isSelected: Boolean = false,
    val reps: String = ""
)
