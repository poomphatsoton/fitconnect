package com.example.train.ui

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.train.R
import com.example.train.model.trainer.AssignedWorkout
import com.example.train.model.trainer.WorkoutOption
import com.example.train.view.reuseComponent.FitCalendarView
import com.example.train.viewmodel.trainer.CalendarViewModel
import java.time.LocalDate
import java.time.LocalDate.now

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TraineeCalendarScreen(
    traineeId: Int = 2,
    onBackClick: () -> Unit = {},
    viewModel: CalendarViewModel = viewModel(),
    calendarDate: LocalDate = now()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState
    var selectedDate by remember { mutableStateOf(calendarDate) }

    LaunchedEffect(traineeId, selectedDate) {
        if (traineeId != -1) {
            viewModel.loadTraineeSlots(traineeId, selectedDate)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
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
            FitCalendarView(
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
                workoutDetailsBySlotId = uiState.workoutDetailsBySlotId,
                onAddClick = viewModel::showAssignDialog,
                onEditClick = viewModel::showEditDialog,
                onDeleteClick = { slot ->
                    viewModel.deleteAssignedWorkout(slot, traineeId, selectedDate)
                }
            )
        }
    }

    if (uiState.showAssignDialog) {
        AssignWorkoutDialog(
            workoutOptions = uiState.workoutOptions,
            onDismiss = viewModel::dismissAssignDialog,
            onAssign = { assignedWorkout ->
                viewModel.assignWorkout(assignedWorkout, traineeId, selectedDate)
            }
        )
    }

    if (uiState.editingWorkout != null) {
        AssignWorkoutDialog(
            initialWorkout = uiState.editingWorkout,
            workoutOptions = uiState.workoutOptions,
            onDismiss = viewModel::dismissEditDialog,
            onAssign = { assignedWorkout ->
                viewModel.updateAssignedWorkout(assignedWorkout, traineeId, selectedDate)
            }
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
                painter = painterResource(id = R.drawable.back),
                contentDescription = "Back",
                modifier = Modifier.size(20.dp)
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
    workoutOptions: List<WorkoutOption> = emptyList(),
    onDismiss: () -> Unit,
    onAssign: (AssignedWorkout) -> Unit
) {
    val selectedInitialWorkout = workoutOptions.firstOrNull { it.id == initialWorkout?.workoutId }
        ?: workoutOptions.firstOrNull { it.name == initialWorkout?.name }
        ?: workoutOptions.firstOrNull()
    var selectedWorkout by remember(initialWorkout, workoutOptions) {
        mutableStateOf(selectedInitialWorkout)
    }
    var expandedWorkout by remember { mutableStateOf(false) }

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
                            value = selectedWorkout?.name ?: "No workouts available",
                            onValueChange = {},
                            readOnly = true,
                            enabled = workoutOptions.isNotEmpty(),
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedWorkout)
                            },
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
                                    text = { Text(text = option.name) },
                                    onClick = {
                                        selectedWorkout = option
                                        expandedWorkout = false
                                    },
                                    colors = MenuDefaults.itemColors(textColor = Color.Black)
                                )
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
                        onClick = {
                            val workout = selectedWorkout ?: return@Button
                            onAssign(
                                AssignedWorkout(
                                    workoutId = workout.id,
                                    name = workout.name
                                )
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                        enabled = selectedWorkout != null,
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
