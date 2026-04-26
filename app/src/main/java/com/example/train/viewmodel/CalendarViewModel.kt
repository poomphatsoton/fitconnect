package com.example.train.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.train.database.DatabaseHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class CalendarUiState(
    val selectedDate: String = "",
    val displayDate: String = "",
    val statusText: String = "No workouts scheduled for this day"
)

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
        updateSelectedDate(selectedDate)
    }

    fun updateSelectedDate(date: String) {
        try {
            val parsedDate = dateFormat.parse(date)
            val displayDate = if (parsedDate != null) {
                displayFormat.format(parsedDate)
            } else {
                "Date Error"
            }

            val hasWorkout = hasWorkoutOnDate(date)

            uiState.value = CalendarUiState(
                selectedDate = date,
                displayDate = displayDate,
                statusText = if (hasWorkout) {
                    "Scheduled workouts found for this day"
                } else {
                    "No workouts scheduled for this day"
                }
            )

        } catch (e: Exception) {
            uiState.value = uiState.value.copy(
                displayDate = "Date Error",
                statusText = "No workouts scheduled for this day"
            )
        }
    }

    private fun hasWorkoutOnDate(date: String): Boolean {
        val selection = "${DatabaseHelper.COL_SCHEDULE_DATE} = ?"
        val args = arrayOf(date)

        val cursor = dbHelper.readableDatabase.query(
            DatabaseHelper.TABLE_WORKOUT_SCHEDULES,
            null,
            selection,
            args,
            null,
            null,
            null
        )

        val hasWorkout = cursor.count > 0
        cursor.close()

        return hasWorkout
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