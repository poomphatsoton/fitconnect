package com.example.train.model.trainer

data class CalendarUiState(
    val selectedDate: String = "",
    val displayDate: String = "",
    val statusText: String = "No workouts scheduled for this day",
    val traineeSlots: List<TraineeSlot> = emptyList()
)

data class TraineeSlot(
    val slotId: Int,
    val workoutId: Int?,
    val workoutName: String?,
    val status: Int, // 0: IDEAL, 1: MAYBE, 2: BUSY
    val startTime: String,
    val endTime: String
)

data class AssignedWorkout(
    val name: String,
    val startTime: String,
    val endTime: String,
    val tag: String
)
