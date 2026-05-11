package com.example.train.view.reuseComponent

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.train.R
import com.example.train.model.trainer.WorkoutExerciseDetail
import com.example.train.model.trainer.WorkoutTagPercent
import com.example.train.model.trainer.WorkoutUiItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkoutDetailCard(
    item: WorkoutUiItem,
    modifier: Modifier = Modifier,
    actions: (@Composable RowScope.() -> Unit)? = null
) {
    val workout = item.workout
    val totalSec = workout.duration

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFFEEEEEE),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = workout.name ?: "",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121),
                modifier = Modifier.weight(1f)
            )

            if (actions != null) {
                actions()
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = workout.description ?: "",
            fontSize = 16.sp,
            color = Color(0xFF757575)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.fitness),
                    contentDescription = "Exercise count",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${workout.exercises.size} exercises",
                    fontSize = 16.sp,
                    color = Color(0xFF757575)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.time),
                    contentDescription = "Duration",
                    modifier = Modifier.size(20.dp),
                    tint = Color(0xFF757575)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${totalSec / 60}m ${totalSec % 60}s",
                    fontSize = 16.sp,
                    color = Color(0xFF757575)
                )
            }
        }

        if (item.tagPercents.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
            ) {
                item.tagPercents.forEach { tag ->
                    WorkoutTagPercentChip(tag)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFEEEEEE))
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Exercises:",
            fontSize = 16.sp,
            color = Color(0xFF757575)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Column {
            item.exerciseDetails.forEach { detail ->
                WorkoutExerciseRow(detail = detail)
            }
        }
    }
}

@Composable
private fun WorkoutTagPercentChip(tag: WorkoutTagPercent) {
    Text(
        text = "${tag.tagName} ${tag.percent}%",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF374151),
        modifier = Modifier
            .background(
                color = Color(0xFFF3F4F6),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}

@Composable
fun WorkoutExerciseRow(
    detail: WorkoutExerciseDetail
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            text = detail.name,
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "${detail.reps} reps",
            fontSize = 14.sp
        )
    }
}
