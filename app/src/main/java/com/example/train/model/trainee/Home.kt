package com.example.train.model.trainee

import com.example.train.model.Tag

data class TraineeHomeUiState(
    val traineeName: String = "",
    val traineeBio: String = "",
    val traineeTags: List<Tag> = emptyList(),
    val availableTags: List<Tag> = emptyList()
)
