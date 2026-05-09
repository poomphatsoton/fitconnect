package com.example.train.model.trainer

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class CalendarUiState(
    val traineeSlots: List<TraineeSlot> = emptyList(),
    val workoutOptions: List<WorkoutOption> = emptyList(),
    val showAssignDialog: Boolean = false,
    val editingWorkout: AssignedWorkout? = null,
    val selectedSlot: TraineeSlot? = null
)

data class WorkoutOption(
    val id: Int,
    val name: String
)

data class TraineeSlot(
    val slotId: Int,
    val workoutId: Int?,
    val workoutName: String?,
    val status: Int, // 0: IDEAL, 1: MAYBE, 2: BUSY
    val startTime: LocalTime,
    val endTime: LocalTime,
    val date: LocalDate
)

data class AssignedWorkout(
    val workoutId: Int?,
    val name: String,
    val startTime: String,
    val endTime: String,
    val tag: String,
    val datetime: LocalDateTime
)

