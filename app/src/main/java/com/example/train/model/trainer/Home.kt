package com.example.train.model.trainer

import com.example.train.model.Tag

data class TrainerHomeUiState(
    val trainerName: String = "John Smith",
    val trainerBio: String = "Certified personal trainer with 10 years of experience",
    val trainerPassword: String = "",
    val trainerTags: List<Tag> = emptyList(),
    val availableTags: List<Tag> = emptyList(),
    val maxTrainees: Int = 10,
    val activeTrainees: Int = 0
)


