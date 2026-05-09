package com.example.train.viewmodel.trainee

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.model.trainee.TraineeCalendarUiState
import com.example.train.viewmodel.reuseComponent.WorkoutDetailLoader
import com.example.train.viewmodel.reuseComponent.toTraineeSlot
import java.time.LocalDate

class TraineeCalendarViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)
    private val workoutDetailLoader = WorkoutDetailLoader(dbHelper)

    private val prefs = application.getSharedPreferences(
        "FitConnect",
        Context.MODE_PRIVATE
    )

    var uiState = mutableStateOf(TraineeCalendarUiState())
        private set

    private val userId: Int
        get() = prefs.getInt("userId", -1)

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadSlots(date: LocalDate) {
        if (userId == -1) return

        val slots = mutableListOf<com.example.train.model.trainer.TraineeSlot>()
        dbHelper.getTraineeSlots(userId, date).use { cursor ->
            while (cursor.moveToNext()) {
                slots.add(cursor.toTraineeSlot())
            }
        }

        val sortedSlots = slots.sortedBy { it.startTime }
        uiState.value = uiState.value.copy(
            slots = sortedSlots,
            workoutDetailsBySlotId = workoutDetailLoader.loadWorkoutDetailsBySlotId(sortedSlots),
            errorMessage = null
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addSlot(date: LocalDate, startTime: String, endTime: String, status: Int) {
        if (userId == -1) return

        val isSuccess = dbHelper.addTraineeCalendarSlot(
            traineeId = userId,
            date = date,
            startTime = startTime,
            endTime = endTime,
            status = status
        )

        if (isSuccess) {
            loadSlots(date)
        } else {
            uiState.value = uiState.value.copy(errorMessage = "Slot time already exists")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSlot(slotId: Int, date: LocalDate, startTime: String, endTime: String, status: Int) {
        if (userId == -1) return

        val isSuccess = dbHelper.updateTraineeCalendarSlot(
            slotId = slotId,
            traineeId = userId,
            date = date,
            startTime = startTime,
            endTime = endTime,
            status = status
        )

        if (isSuccess) {
            loadSlots(date)
        } else {
            uiState.value = uiState.value.copy(errorMessage = "Slot time already exists")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun deleteSlot(slotId: Int, date: LocalDate) {
        dbHelper.deleteTraineeCalendarSlot(slotId)
        loadSlots(date)
    }

    fun clearError() {
        uiState.value = uiState.value.copy(errorMessage = null)
    }
}
