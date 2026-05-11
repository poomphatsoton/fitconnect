package com.example.train.model.trainer

import com.example.train.model.Tag

data class TrainerHomeUiState(
    val trainerName: String = "",
    val trainerBio: String = "",
    val trainerPassword: String = "",
    val trainerTags: List<Tag> = emptyList(),
    val availableTags: List<Tag> = emptyList(),
    val maxTrainees: Int = 10,
    val activeTrainees: Int = 0
)


