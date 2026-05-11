package com.example.train.model.trainee

import com.example.train.model.trainer.TraineeSlot
import com.example.train.model.trainer.WorkoutUiItem

data class TraineeCalendarUiState(
    val slots: List<TraineeSlot> = emptyList(),
    val workoutDetailsBySlotId: Map<Int, WorkoutUiItem> = emptyMap(),
    val errorMessage: String? = null
)
