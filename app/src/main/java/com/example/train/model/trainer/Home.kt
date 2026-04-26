package com.example.train.model.trainer

data class TrainerHomeUiState(
    val trainerName: String = "John Smith",
    val trainerBio: String = "Certified personal trainer with 10 years of experience",
    val activeTrainees: Int = 0,
    val exercises: Int = 0,
    val workouts: Int = 0,
    val pendingRequests: Int = 0
)


