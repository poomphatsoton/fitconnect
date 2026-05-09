package com.example.train.viewmodel.trainer

import android.app.Application
import android.database.Cursor
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.model.trainer.AssignedWorkout
import com.example.train.database.DatabaseHelper
import com.example.train.model.trainer.CalendarUiState
import com.example.train.model.trainer.TraineeSlot
import com.example.train.model.trainer.WorkoutOption
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Calendar
import java.util.Locale

class CalendarViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

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
        uiState.value = uiState.value.copy(
            traineeSlots = slots
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
                name = slot.workoutName ?: "",
                startTime = slot.startTime.toString(),
                endTime = slot.endTime.toString(),
                tag = "",
                datetime = LocalDateTime.of(slot.date, slot.startTime)
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun assignWorkout(assignedWorkout: AssignedWorkout, traineeId: Int, date: LocalDate) {
        val selectedSlot = findSelectedSlot() ?: return dismissAssignDialog()
        val workoutId = assignedWorkout.workoutId ?: return dismissAssignDialog()

        dbHelper.assignWorkoutToTraineeSlot(
            slotId = selectedSlot.slotId,
            workoutId = workoutId,
            startTime = assignedWorkout.startTime,
            endTime = assignedWorkout.endTime
        )
        uiState.value = uiState.value.copy(showAssignDialog = false, selectedSlot = null)
        loadTraineeSlots(traineeId, date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateAssignedWorkout(assignedWorkout: AssignedWorkout, traineeId: Int, date: LocalDate) {
        val selectedSlot = findSelectedSlot() ?: return dismissEditDialog()
        val workoutId = assignedWorkout.workoutId ?: return dismissEditDialog()

        dbHelper.assignWorkoutToTraineeSlot(
            slotId = selectedSlot.slotId,
            workoutId = workoutId,
            startTime = assignedWorkout.startTime,
            endTime = assignedWorkout.endTime
        )
        uiState.value = uiState.value.copy(editingWorkout = null, selectedSlot = null)
        loadTraineeSlots(traineeId, date)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteAssignedWorkout(slot: TraineeSlot, traineeId: Int, date: LocalDate) {
        dbHelper.clearWorkoutFromTraineeSlot(slot.slotId)
        loadTraineeSlots(traineeId, date)
    }

    private fun loadWorkoutOptions() {
        val options = mutableListOf<WorkoutOption>()
        dbHelper.getWorkoutOptions().use { cursor ->
            while (cursor.moveToNext()) {
                options.add(
                    WorkoutOption(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_ID)),
                        name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_NAME))
                    )
                )
            }
        }
        uiState.value = uiState.value.copy(workoutOptions = options)
    }

    private fun findSelectedSlot(): TraineeSlot? {
        return uiState.value.selectedSlot
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun Cursor.toTraineeSlot(): TraineeSlot {
        val workoutIdIndex = getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_WORKOUT_ID)
        val workoutNameIndex = getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_NAME)

        return TraineeSlot(
            slotId = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_ID)),
            workoutId = if (isNull(workoutIdIndex)) null else getInt(workoutIdIndex),
            workoutName = if (isNull(workoutNameIndex)) null else getString(workoutNameIndex),
            status = getInt(getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_STATUS)),
            startTime = LocalTime.parse(getString(getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_START_TIME))),
            endTime = LocalTime.parse(getString(getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_END_TIME))),
            date = LocalDate.parse(getString(getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_DATE)))
        )
    }
}
