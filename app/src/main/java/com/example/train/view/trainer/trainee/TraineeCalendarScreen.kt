package com.example.train.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.train.R
import com.example.train.model.trainer.AssignedWorkout
import com.example.train.viewmodel.trainer.CalendarViewModel
import java.time.LocalDate
import java.time.LocalDate.*
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TraineeCalendarScreen(
    traineeId: Int = 2,
    onBackClick: () -> Unit = {},
    viewModel: CalendarViewModel = viewModel(),
    calendarDate: LocalDate = now()
) {
    val uiState by viewModel.uiState
    var selectedDate by remember { mutableStateOf(calendarDate) }
    var showAssignDialog by remember { mutableStateOf(false) }
    var editingWorkout by remember { mutableStateOf<AssignedWorkout?>(null) }

    LaunchedEffect(traineeId, selectedDate) {
        if (traineeId != -1) {
            viewModel.loadTraineeSlots(traineeId, selectedDate)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
    ) {
        TraineeCalendarHeader(onBackClick)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            TraineeCalendarView(
                selectedDate = selectedDate,
                onSelectedDate = { newDate ->
                    selectedDate = newDate
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Schedule",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            DailyScheduleContent(
                slots = uiState.traineeSlots,
                onAddClick = { showAssignDialog = true },
                onEditClick = { slot ->
                    editingWorkout = AssignedWorkout(
                        name = slot.workoutName ?: "",
                        startTime = slot.startTime.toString(),
                        endTime = slot.endTime.toString(),
                        tag = "",
                        datetime = LocalDateTime.now()
                    )
                },
                onDeleteClick = { /* Handle Delete */ }
            )
        }
    }

    if (showAssignDialog) {
        AssignWorkoutDialog(
            onDismiss = { showAssignDialog = false },
            onAssign = { showAssignDialog = false }
        )
    }

    if (editingWorkout != null) {
        AssignWorkoutDialog(
            initialWorkout = editingWorkout,
            onDismiss = { editingWorkout = null },
            onAssign = { editingWorkout = null }
        )
    }
}

@Composable
fun TraineeCalendarHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back"
            )
        }
        Text(
            text = "Workout Calendar",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssignWorkoutDialog(
    initialWorkout: AssignedWorkout? = null,
    onDismiss: () -> Unit,
    onAssign: () -> Unit
) {
    var workoutName by remember { mutableStateOf(initialWorkout?.name ?: "Strength & Power") }
    var startTime by remember { mutableStateOf(initialWorkout?.startTime ?: "08:00") }
    var endTime by remember { mutableStateOf(initialWorkout?.endTime ?: "09:00") }
    
    var expandedWorkout by remember { mutableStateOf(false) }
    var expandedStart by remember { mutableStateOf(false) }
    var expandedEnd by remember { mutableStateOf(false) }

    val workoutOptions = listOf("Strength & Power", "Yoga Flow", "HIIT Session", "Endurance Run")
    val timeOptions = (5..22).flatMap { hour -> 
        listOf(
            String.format("%02d:00", hour),
            String.format("%02d:30", hour)
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = if (initialWorkout == null) "Assign Workout" else "Edit Workout",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = "Monday, May 12 • 08:00 Window",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Column {
                    Text(
                        text = "Workout Library",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    ExposedDropdownMenuBox(
                        expanded = expandedWorkout,
                        onExpandedChange = { expandedWorkout = !expandedWorkout }
                    ) {
                        OutlinedTextField(
                            value = workoutName,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedWorkout) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expandedWorkout,
                            onDismissRequest = { expandedWorkout = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            workoutOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(text = option) },
                                    onClick = {
                                        workoutName = option
                                        expandedWorkout = false
                                    },
                                    colors = MenuDefaults.itemColors(
                                        textColor = Color.Black
                                    )
                                )
                            }
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Start Time",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        ExposedDropdownMenuBox(
                            expanded = expandedStart,
                            onExpandedChange = { expandedStart = !expandedStart }
                        ) {
                            OutlinedTextField(
                                value = startTime,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStart) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedStart,
                                onDismissRequest = { expandedStart = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                timeOptions.forEach { time ->
                                    DropdownMenuItem(
                                        text = { Text(text = time) },
                                        onClick = {
                                            startTime = time
                                            expandedStart = false
                                        },
                                        colors = MenuDefaults.itemColors(textColor = Color.Black)
                                    )
                                }
                            }
                        }
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "End Time",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        ExposedDropdownMenuBox(
                            expanded = expandedEnd,
                            onExpandedChange = { expandedEnd = !expandedEnd }
                        ) {
                            OutlinedTextField(
                                value = endTime,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedEnd) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = expandedEnd,
                                onDismissRequest = { expandedEnd = false },
                                modifier = Modifier.background(Color.White)
                            ) {
                                timeOptions.forEach { time ->
                                    DropdownMenuItem(
                                        text = { Text(text = time) },
                                        onClick = {
                                            endTime = time
                                            expandedEnd = false
                                        },
                                        colors = MenuDefaults.itemColors(textColor = Color.Black)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF3F4F6),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(text = "Cancel", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onAssign,
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF0F172A),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(28.dp)
                    ) {
                        Text(
                            text = if (initialWorkout == null) "Assign" else "Update",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
