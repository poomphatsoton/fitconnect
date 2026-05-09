package com.example.train.model.trainee

import com.example.train.model.trainer.TraineeSlot

data class TraineeCalendarUiState(
    val slots: List<TraineeSlot> = emptyList(),
    val errorMessage: String? = null
)
