package com.example.train.model.trainer

data class TraineesUiState(
    val activeCount: Int = 0,
    val requestCount: Int = 0,
    val allActiveTrainees: List<Trainee> = emptyList()
)

data class Trainee(
    val id: Int,
    val name: String,
    val bio: String,
    val tags: List<String> = emptyList(),
    val completionRate: Float = 0f,
    val imageRes: Int? = null
)
