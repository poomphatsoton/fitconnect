package com.example.train.viewmodel.trainer

import android.app.Application
import android.database.Cursor
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import com.example.train.model.trainer.CalendarUiState
import com.example.train.model.trainer.TraineeSlot
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalTime
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