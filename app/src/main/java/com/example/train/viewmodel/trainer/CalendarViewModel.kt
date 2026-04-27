package com.example.train.viewmodel.trainer

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.model.trainer.CalendarUiState
import com.example.train.model.trainer.TraineeSlot
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val dbHelper = DatabaseHelper(application)

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val displayFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.US)

    var uiState = mutableStateOf(CalendarUiState())
        private set

    init {
        val today = Calendar.getInstance()
        val selectedDate = dateFormat.format(today.time)
        val displayDate = displayFormat.format(today.time)
        
        uiState.value = uiState.value.copy(
            selectedDate = selectedDate,
            displayDate = displayDate
        )
    }

    fun updateSelectedDate(date: String) {
        try {
            val parsedDate = dateFormat.parse(date)
            val displayDate = if (parsedDate != null) {
                displayFormat.format(parsedDate)
            } else {
                "Date Error"
            }

            uiState.value = uiState.value.copy(
                selectedDate = date,
                displayDate = displayDate
            )

        } catch (e: Exception) {
            uiState.value = uiState.value.copy(
                displayDate = "Date Error"
            )
        }
    }

    fun loadTraineeSlots(traineeId: Int) {
        val slots = mutableListOf<TraineeSlot>()
        val cursor = dbHelper.getTraineeSlots(traineeId)

        if (cursor.moveToFirst()) {
            do {
                val slotId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_ID))
                val workoutIdIndex = cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_ID)
                val workoutId = if (cursor.isNull(workoutIdIndex)) null else cursor.getInt(workoutIdIndex)
                
                val status = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_STATUS))
                val startTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_START_TIME))
                val endTime = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_SLOT_END_TIME))
                val workoutName = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_WORKOUT_NAME))

                slots.add(
                    TraineeSlot(
                        slotId = slotId,
                        workoutId = workoutId,
                        workoutName = if (workoutId != null) workoutName else null,
                        status = status,
                        startTime = startTime,
                        endTime = endTime
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        
        uiState.value = uiState.value.copy(traineeSlots = slots)
    }

    fun formatDateFromCalendar(
        year: Int,
        month: Int,
        day: Int
    ): String {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)

        return dateFormat.format(calendar.time)
    }
}