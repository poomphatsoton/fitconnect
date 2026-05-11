package com.example.train.view.trainee.calendar

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.train.model.trainer.TraineeSlot
import com.example.train.view.reuseComponent.FitCalendarView
import com.example.train.view.reuseComponent.ScheduleSlotCard
import com.example.train.view.reuseComponent.ScheduleSlotCardMode
import com.example.train.viewmodel.trainee.TraineeCalendarViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TraineeCalendarTabScreen(
    modifier: Modifier = Modifier,
    viewModel: TraineeCalendarViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var editingSlot by remember { mutableStateOf<TraineeSlot?>(null) }
    var showCreateSlotDialog by remember { mutableStateOf(false) }

    LaunchedEffect(selectedDate) {
        viewModel.loadSlots(selectedDate)
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FitCalendarView(
            selectedDate = selectedDate,
            onSelectedDate = { date ->
                selectedDate = date
                editingSlot = null
                showCreateSlotDialog = false
            }
        )
        Button(
            onClick = { showCreateSlotDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0F172A),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Create Slot", fontSize = 16.sp)
        }
        Text(
            text = "Slot Time",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        if (uiState.slots.isEmpty()) {
            Text(
                text = "No slot time added",
                fontSize = 16.sp,
                color = Color.Gray
            )
        } else {
            uiState.slots.forEach { slot ->
                ScheduleSlotCard(
                    slot = slot,
                    mode = ScheduleSlotCardMode.TraineeManage,
                    workoutDetail = uiState.workoutDetailsBySlotId[slot.slotId],
                    showWorkoutDetail = true,
                    onEditClick = {
                        editingSlot = slot
                    },
                    onDeleteClick = {
                        viewModel.deleteSlot(slot.slotId, selectedDate)
                    }
                )
            }
        }
    }

    val dialogSlot = editingSlot
    val showSlotDialog = showCreateSlotDialog || dialogSlot != null
    if (showSlotDialog) {
        SlotDialog(
            title = if (dialogSlot == null) "Create Slot" else "Edit Slot Time",
            initialTime = dialogSlot?.let {
                "${it.startTime.toDisplayTime()} - ${it.endTime.toDisplayTime()}"
            },
            initialStatus = dialogSlot?.status,
            submitText = if (dialogSlot == null) "Create Slot" else "Update",
            onDismiss = {
                showCreateSlotDialog = false
                editingSlot = null
            },
            onSubmit = { time, status ->
                val range = time.toSlotTimeRange()
                if (dialogSlot == null) {
                    viewModel.addSlot(selectedDate, range.first, range.second, status)
                    showCreateSlotDialog = false
                } else {
                    viewModel.updateSlot(dialogSlot.slotId, selectedDate, range.first, range.second, status)
                    editingSlot = null
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SlotEditor(
    selectedTime: String?,
    selectedStatus: Int?,
    buttonText: String,
    onTimeSelected: (String) -> Unit,
    onStatusSelected: (Int) -> Unit,
    onSubmit: () -> Unit
) {
    var expandedTime by remember { mutableStateOf(false) }
    var expandedStatus by remember { mutableStateOf(false) }
    val timeOptions = (5..22).map { hour ->
        String.format("%02d:00 - %02d:00", hour, hour + 1)
    }
    val statuses = listOf(0 to "IDEAL", 1 to "MAYBE", 2 to "BUSY")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expandedTime,
                onExpandedChange = { expandedTime = !expandedTime }
            ) {
                OutlinedTextField(
                    value = selectedTime ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Slot Time") },
                    placeholder = { Text("Select slot time") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTime) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedTime,
                    onDismissRequest = { expandedTime = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    timeOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onTimeSelected(option)
                                expandedTime = false
                            },
                            colors = MenuDefaults.itemColors(textColor = Color.Black)
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = expandedStatus,
                onExpandedChange = { expandedStatus = !expandedStatus }
            ) {
                OutlinedTextField(
                    value = selectedStatus?.let { selected ->
                        statuses.first { it.first == selected }.second
                    } ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Status") },
                    placeholder = { Text("Select status") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedStatus) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedStatus,
                    onDismissRequest = { expandedStatus = false },
                    modifier = Modifier.background(Color.White)
                ) {
                    statuses.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(status.second) },
                            onClick = {
                                onStatusSelected(status.first)
                                expandedStatus = false
                            },
                            colors = MenuDefaults.itemColors(textColor = Color.Black)
                        )
                    }
                }
            }
            Button(
                onClick = onSubmit,
                enabled = selectedTime != null && selectedStatus != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFE5E7EB),
                    disabledContentColor = Color(0xFF9CA3AF)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = buttonText, fontSize = 16.sp)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SlotDialog(
    title: String,
    initialTime: String?,
    initialStatus: Int?,
    submitText: String,
    onDismiss: () -> Unit,
    onSubmit: (time: String, status: Int) -> Unit
) {
    var editTime by remember(initialTime) { mutableStateOf(initialTime) }
    var editStatus by remember(initialStatus) { mutableStateOf(initialStatus) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                SlotEditor(
                    selectedTime = editTime,
                    selectedStatus = editStatus,
                    buttonText = submitText,
                    onTimeSelected = { editTime = it },
                    onStatusSelected = { editStatus = it },
                    onSubmit = {
                        val time = editTime ?: return@SlotEditor
                        val status = editStatus ?: return@SlotEditor
                        onSubmit(time, status)
                    }
                )
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE5E7EB),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Cancel")
                }
            }
        }
    }
}

private fun String.toSlotTimeRange(): Pair<String, String> {
    val parts = split(" - ")
    return parts[0] to parts[1]
}

private fun LocalTime.toDisplayTime(): String {
    return format(DateTimeFormatter.ofPattern("HH:mm"))
}
