package com.example.train.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.train.model.trainer.TraineeSlot
import com.example.train.model.trainer.WorkoutUiItem
import com.example.train.view.reuseComponent.ScheduleSlotCard
import com.example.train.view.reuseComponent.ScheduleSlotCardMode

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DailyScheduleContent(
    slots: List<TraineeSlot>,
    workoutDetailsBySlotId: Map<Int, WorkoutUiItem>,
    onAddClick: (TraineeSlot) -> Unit,
    onEditClick: (TraineeSlot) -> Unit,
    onDeleteClick: (TraineeSlot) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (slots.isNotEmpty()) {
            slots.forEach { slot ->
                ScheduleSlotCard(
                    slot = slot,
                    mode = ScheduleSlotCardMode.TrainerAssign,
                    workoutDetail = workoutDetailsBySlotId[slot.slotId],
                    onAddClick = { onAddClick(slot) },
                    onEditClick = { onEditClick(slot) },
                    onDeleteClick = { onDeleteClick(slot) }
                )
            }
        } else {
            Text(
                text = "No workouts scheduled for this day.",
                fontSize = 20.sp,
                color = Color.Gray
            )
        }
    }
}
