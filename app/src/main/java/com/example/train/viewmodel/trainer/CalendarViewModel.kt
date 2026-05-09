package com.example.train.viewmodel.trainer

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.model.trainer.AssignedWorkout
import com.example.train.database.DatabaseHelper
import com.example.train.model.trainer.CalendarUiState
import com.example.train.model.trainer.TraineeSlot
import com.example.train.model.trainer.WorkoutOption
import com.example.train.viewmodel.reuseComponent.WorkoutDetailLoader
import com.example.train.viewmodel.reuseComponent.toTraineeSlot
import java.time.LocalDate
import kotlin.math.ceil

class CalendarViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)
    private val workoutDetailLoader = WorkoutDetailLoader(dbHelper)

    var uiState = mutableStateOf(CalendarUiState())
        private set

    init {
        loadWorkoutOptions()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadTraineeSlots(traineeId: Int, date: LocalDate) {
        val slots = mutableListOf<TraineeSlot>()

        dbHelper.getTraineeSlots(traineeId, date).use { cursor ->
            while (cursor.moveToNext()) {
                slots.add(cursor.toTraineeSlot())
            }
        }
        val sortedSlots = slots.sortedBy { it.startTime }
        uiState.value = uiState.value.copy(
            traineeSlots = sortedSlots,
            workoutDetailsBySlotId = workoutDetailLoader.loadWorkoutDetailsBySlotId(sortedSlots)
        )
    }

    fun showAssignDialog(slot: TraineeSlot) {
        loadWorkoutOptions()
        uiState.value = uiState.value.copy(
            showAssignDialog = true,
            editingWorkout = null,
            selectedSlot = slot
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun showEditDialog(slot: TraineeSlot) {
        loadWorkoutOptions()
        uiState.value = uiState.value.copy(
            showAssignDialog = false,
            editingWorkout = AssignedWorkout(
                workoutId = slot.workoutId,
                name = slot.workoutName ?: ""
            ),
            selectedSlot = slot
        )
    }

    fun dismissAssignDialog() {
        uiState.value = uiState.value.copy(showAssignDialog = false, selectedSlot = null)
    }

    fun dismissEditDialog() {
        uiState.value = uiState.value.copy(editingWorkout = null, selectedSlot = null)
    }

    fun clearError() {
        uiState.value = uiState.value.copy(errorMessage = null)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun assignWorkout(assignedWorkout: AssignedWorkout, traineeId: Int, date: LocalDate) {
        val selectedSlot = findSelectedSlot() ?: return dismissAssignDialog()
        val workoutId = assignedWorkout.workoutId ?: return dismissAssignDialog()
        val workout = uiState.value.workoutOptions.firstOrNull { it.id == workoutId } ?: return dismissAssignDialog()
        val slotsToAssign = findAssignableSlots(selectedSlot, workout.duration)
        if (slotsToAssign == null) {
            uiState.value = uiState.value.copy(
                showAssignDialog = false,
                selectedSlot = null,
                errorMessage = "This workout needs consecutive available slots"
            )
            return
        }

        dbHelper.assignWorkoutToTraineeSlots(slotsToAssign.map { it.slotId }, workoutId)
        uiState.value = uiState.value.copy(showAssignDialog = false, selectedSlot = null)
        loadTraineeSlots(traineeId, date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateAssignedWorkout(assignedWorkout: AssignedWorkout, traineeId: Int, date: LocalDate) {
        val selectedSlot = findSelectedSlot() ?: return dismissEditDialog()
        val workoutId = assignedWorkout.workoutId ?: return dismissEditDialog()
        val workout = uiState.value.workoutOptions.firstOrNull { it.id == workoutId } ?: return dismissEditDialog()
        val oldSlots = findAssignedSlotGroup(selectedSlot)
        val anchorSlot = oldSlots.firstOrNull() ?: selectedSlot
        val oldSlotIds = oldSlots.map { it.slotId }
        val slotsToAssign = findAssignableSlots(
            selectedSlot = anchorSlot,
            workoutDurationSeconds = workout.duration,
            reusableSlotIds = oldSlotIds
        )
        if (slotsToAssign == null) {
            uiState.value = uiState.value.copy(
                editingWorkout = null,
                selectedSlot = null,
                errorMessage = "This workout needs consecutive available slots"
            )
            return
        }

        dbHelper.replaceWorkoutOnTraineeSlots(oldSlotIds, slotsToAssign.map { it.slotId }, workoutId)
        uiState.value = uiState.value.copy(editingWorkout = null, selectedSlot = null)
        loadTraineeSlots(traineeId, date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteAssignedWorkout(slot: TraineeSlot, traineeId: Int, date: LocalDate) {
        val slotsToClear = findAssignedSlotGroup(slot).map { it.slotId }
        dbHelper.clearWorkoutFromTraineeSlots(slotsToClear)
        loadTraineeSlots(traineeId, date)
    }

    private fun loadWorkoutOptions() {
        val options = mutableListOf<WorkoutOption>()
        dbHelper.getWorkoutOptions().use { cursor ->
            while (cursor.moveToNext()) {
                options.add(
                    WorkoutOption(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_NAME)),
                        duration = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_DURATION))
                    )
                )
            }
        }
        uiState.value = uiState.value.copy(workoutOptions = options)
    }

    private fun findSelectedSlot(): TraineeSlot? {
        return uiState.value.selectedSlot
    }

    private fun findAssignableSlots(
        selectedSlot: TraineeSlot,
        workoutDurationSeconds: Int,
        reusableSlotIds: List<Int> = emptyList()
    ): List<TraineeSlot>? {
        val requiredSlots = ceil(workoutDurationSeconds / 3600.0).toInt().coerceAtLeast(1)
        val sortedSlots = uiState.value.traineeSlots.sortedBy { it.startTime }
        val selectedIndex = sortedSlots.indexOfFirst { it.slotId == selectedSlot.slotId }
        if (selectedIndex == -1) return null

        val result = mutableListOf<TraineeSlot>()
        for (offset in 0 until requiredSlots) {
            val slot = sortedSlots.getOrNull(selectedIndex + offset) ?: return null
            if (offset > 0) {
                val previousSlot = result.last()
                if (previousSlot.endTime != slot.startTime) return null
            }
            if (slot.status == 2) return null
            if ((slot.workoutId != null || slot.assignmentId != null) && slot.slotId !in reusableSlotIds) return null
            result.add(slot)
        }

        return result
    }

    private fun findAssignedSlotGroup(selectedSlot: TraineeSlot): List<TraineeSlot> {
        val assignmentId = selectedSlot.assignmentId ?: return listOf(selectedSlot)
        val sortedSlots = uiState.value.traineeSlots.sortedBy { it.startTime }
        return sortedSlots.filter { it.assignmentId == assignmentId }.ifEmpty { listOf(selectedSlot) }
    }
}
