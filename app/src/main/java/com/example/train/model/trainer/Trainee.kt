package com.example.train.model.trainer

import com.example.train.model.Tag

data class TraineesUiState(
    val activeCount: Int = 0,
    val requestCount: Int = 0,
    val allActiveTrainees: List<Trainee> = emptyList(),
    val allRequestTrainees: List<Trainee> = emptyList(),
    val trainerId: Int = 0
)

data class Trainee(
    val id: Int,
    val name: String,
    val bio: String,
    val tags: List<Tag> = emptyList(),
    val completionRate: Float = 0f,
    val imageRes: Int? = null
)
