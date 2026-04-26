package com.example.train.ui

import android.widget.CalendarView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.train.viewmodel.trainer.CalendarViewModel

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = viewModel()
) {
    val uiState by viewModel.uiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Workout Calendar",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        AndroidView(
            factory = { context ->
                CalendarView(context).apply {
                    setOnDateChangeListener { _, year, month, dayOfMonth ->
                        val selectedDate = viewModel.formatDateFromCalendar(
                            year = year,
                            month = month,
                            day = dayOfMonth
                        )

                        viewModel.updateSelectedDate(selectedDate)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(bottom = 16.dp)
        )

        DateStatusCard(
            selectedDate = uiState.displayDate,
            statusText = uiState.statusText
        )

        Spacer(modifier = Modifier.height(16.dp))

        LegendCard()
    }
}

@Composable
fun DateStatusCard(
    selectedDate: String,
    statusText: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Text(
            text = selectedDate,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = statusText,
            fontSize = 16.sp,
            color = Color(0xFF666666)
        )
    }
}

@Composable
fun LegendCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Text(
            text = "Legend",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LegendRow(
            color = Color(0xFF4CAF50),
            text = "Scheduled Workout"
        )

        Spacer(modifier = Modifier.height(8.dp))

        LegendRow(
            color = Color(0xFF9E9E9E),
            text = "No Workout Scheduled"
        )
    }
}

@Composable
fun LegendRow(
    color: Color,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .background(color)
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = text,
            fontSize = 16.sp
        )
    }
}