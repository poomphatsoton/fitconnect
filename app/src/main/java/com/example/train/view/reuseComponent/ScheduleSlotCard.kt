package com.example.train.view.reuseComponent

import android.graphics.Paint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.train.R
import com.example.train.model.trainer.TraineeSlot
import com.example.train.model.trainer.WorkoutExerciseDetail
import com.example.train.model.trainer.WorkoutTagPercent
import com.example.train.model.trainer.WorkoutUiItem
import java.time.LocalTime
import java.time.format.DateTimeFormatter

enum class ScheduleSlotCardMode {
    TrainerAssign,
    TraineeManage
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleSlotCard(
    slot: TraineeSlot,
    mode: ScheduleSlotCardMode,
    modifier: Modifier = Modifier,
    workoutDetail: WorkoutUiItem? = null,
    showWorkoutDetail: Boolean = true,
    onAddClick: (() -> Unit)? = null,
    onEditClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    val hasWorkout = slot.workoutId != null
    val title = when {
        slot.status == 2 -> "Unavailable booking"
        hasWorkout -> slot.workoutName ?: workoutDetail?.workout?.name ?: "Assigned Workout"
        else -> "Available for Booking"
    }
    var isShowWorkoutDetail by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        TimeRangeTag(slot.startTime, slot.endTime)
                        Spacer(modifier = Modifier.width(8.dp))
                        SlotStatusTag(status = slot.status)
                    }

                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (slot.status == 2 || !hasWorkout) Color.Gray else Color.Black
                    )
                }

                Column()
                {
                    SlotActions(
                        slot = slot,
                        mode = mode,
                        onAddClick = onAddClick,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick
                    )
                    if (hasWorkout) {
                        IconButton(
                            onClick = { isShowWorkoutDetail = !isShowWorkoutDetail },
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.down),
                                contentDescription = null,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }


            if (hasWorkout && showWorkoutDetail && workoutDetail != null && isShowWorkoutDetail) {
                WorkoutSlotDetail(workoutDetail)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun TimeRangeTag(startTime: LocalTime, endTime: LocalTime) {
    Surface(
        color = Color(0xFFF9FAFB),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = "${startTime.toDisplayTime()} - ${endTime.toDisplayTime()}",
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun SlotActions(
    slot: TraineeSlot,
    mode: ScheduleSlotCardMode,
    onAddClick: (() -> Unit)?,
    onEditClick: (() -> Unit)?,
    onDeleteClick: (() -> Unit)?
) {
    val hasWorkout = slot.workoutId != null
    when (mode) {
        ScheduleSlotCardMode.TrainerAssign -> {
            if (slot.status != 2) {
                if (hasWorkout) {
                    EditDeleteActions(onEditClick = onEditClick, onDeleteClick = onDeleteClick)
                } else {
                    AddAction(onAddClick = onAddClick)
                }
            }
        }

        ScheduleSlotCardMode.TraineeManage -> {
            if (hasWorkout) {
                AssignedWorkoutTag()
            } else {
                EditDeleteActions(onEditClick = onEditClick, onDeleteClick = onDeleteClick)
            }
        }
    }
}

@Composable
private fun AddAction(onAddClick: (() -> Unit)?) {
    IconButton(onClick = { onAddClick?.invoke() }, enabled = onAddClick != null) {
        Text(
            text = "+",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
private fun EditDeleteActions(
    onEditClick: (() -> Unit)?,
    onDeleteClick: (() -> Unit)?
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = { onEditClick?.invoke() }, enabled = onEditClick != null) {
            Image(
                painter = painterResource(id = R.drawable.edit),
                contentDescription = "Edit",
                modifier = Modifier.size(24.dp)
            )
        }
        IconButton(onClick = { onDeleteClick?.invoke() }, enabled = onDeleteClick != null) {
            Image(
                painter = painterResource(id = R.drawable.delete),
                contentDescription = "Delete",
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun AssignedWorkoutTag() {
    Surface(
        color = Color(0xFFDBEAFE),
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = "ASSIGNED WORKOUT",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1D4ED8),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun WorkoutSlotDetail(workout: WorkoutUiItem) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        workout.workout.description?.takeIf { it.isNotBlank() }?.let { description ->
            Text(
                text = description,
                fontSize = 13.sp,
                color = Color(0xFF6B7280),
                lineHeight = 18.sp
            )
        }

        Text(
            text = "${workout.exerciseDetails.size} exercises - ${workout.workout.duration / 60}m ${workout.workout.duration % 60}s",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF374151)
        )

        if (workout.tagPercents.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                workout.tagPercents.forEach { tag ->
                    WorkoutTagPercentChip(tag)
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
            workout.exerciseDetails.forEach { detail ->
                WorkoutExerciseLine(detail)
            }
        }
    }
}

@Composable
private fun WorkoutTagPercentChip(tag: WorkoutTagPercent) {
    Surface(
        color = Color(0xFFF3F4F6),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = "${tag.tagName} ${tag.percent}%",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF374151),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun WorkoutExerciseLine(detail: WorkoutExerciseDetail) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = detail.name,
            fontSize = 12.sp,
            color = Color(0xFF4B5563),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${detail.reps} reps",
            fontSize = 12.sp,
            color = Color(0xFF4B5563)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun LocalTime.toDisplayTime(): String {
    return format(DateTimeFormatter.ofPattern("HH:mm"))
}
