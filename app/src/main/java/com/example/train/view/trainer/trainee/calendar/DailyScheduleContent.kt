package com.example.train.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.train.R
import com.example.train.model.trainer.TraineeSlot

@Composable
fun DailyScheduleContent(
    slots: List<TraineeSlot>,
    onAddClick: () -> Unit,
    onEditClick: (TraineeSlot) -> Unit,
    onDeleteClick: (TraineeSlot) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!slots.isEmpty()) {
            slots.forEach { slot ->
                ScheduleRow(
                    slot = slot,
                    onAddClick = onAddClick,
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
@Composable
fun Tag(idTag: Int) {
    val color = when(idTag) {
        0 -> Color(0xFFD1FAE5)
        1 -> Color(0xFFFEF3C7)
        else -> Color(0xFFF3F4F6)
    }
    val text = when(idTag) {
        0 -> "IDEAL"
        1 -> "MAYBE"
        else -> "BUSY"
    }
    Surface(
        color = color,
        shape = RoundedCornerShape(4.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun ScheduleRow(
    slot: TraineeSlot,
    onAddClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF3F4F6))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFF9FAFB),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "${slot.startTime} - ${slot.endTime}",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            color = Color.Gray
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Tag(slot.status)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val title = when {
                    slot.status == 2 -> "Unavailable booking"
                    slot.workoutId != null -> slot.workoutName ?: "Strength Training"
                    else -> "Available for Booking"
                }
                
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (slot.status == 2 || slot.workoutId == null) Color.Gray else Color.Black
                )
            }

            if (slot.status != 2) {
                if (slot.workoutId != null) {
                    Row (
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        IconButton(onClick = onEditClick) {
                            Image(
                                painter = painterResource(id = R.drawable.edit),
                                contentDescription = "Delete",
                                modifier = Modifier
                                    .size(25.dp)
                            )
                        }
                        IconButton(onClick = onDeleteClick) {
                            Image(
                                painter = painterResource(id = R.drawable.delete),
                                contentDescription = "Delete",
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                } else {
                    IconButton(onClick = onAddClick) {
                        Image(
                            painter = painterResource(id = R.drawable.add),
                            contentDescription = "Add",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }
            }
        }
    }
}
