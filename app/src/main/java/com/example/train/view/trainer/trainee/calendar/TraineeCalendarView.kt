package com.example.train.ui

import android.os.Build
import android.widget.CalendarView
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.time.LocalDate
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TraineeCalendarView(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onSelectedDate: (LocalDate) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        AndroidView(
            factory = { context ->
                CalendarView(context).apply {
                    // set วันที่เริ่มต้นให้ CalendarView
                    date = selectedDate
                        .atStartOfDay(ZoneId.systemDefault())
                        .toInstant()
                        .toEpochMilli()

                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        val newDate = LocalDate.of(
                            year,
                            month + 1, // CalendarView month เริ่มจาก 0
                            dayOfMonth
                        )

                        onSelectedDate(newDate)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}
