package com.example.train.viewmodel.trainee

import android.app.Application
import android.content.Context
import android.database.Cursor
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.model.trainee.TraineeCalendarUiState
import com.example.train.model.trainer.TraineeSlot
import java.time.LocalDate
import java.time.LocalTime

class TraineeCalendarViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

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

        val slots = mutableListOf<TraineeSlot>()
        dbHelper.getTraineeSlots(userId, date).use { cursor ->
            while (cursor.moveToNext()) {
                slots.add(cursor.toTraineeSlot())
            }
        }

        uiState.value = uiState.value.copy(
            slots = slots.sortedBy { it.startTime },
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
