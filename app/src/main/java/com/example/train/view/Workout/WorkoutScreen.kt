package com.example.train.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.train.R
import com.example.train.ui.components.CreateWorkoutDialogHost
import com.example.train.viewmodel.WorkoutCategoryPercent
import com.example.train.viewmodel.WorkoutExerciseDetail
import com.example.train.viewmodel.WorkoutUiItem
import com.example.train.viewmodel.WorkoutsViewModel

@Composable
fun WorkoutsScreen(
    viewModel: WorkoutsViewModel = viewModel()
) {
    val context = LocalContext.current
    val workouts = viewModel.workouts

    LaunchedEffect(Unit) {
        viewModel.loadWorkouts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        CreateWorkoutDialogHost(
            viewModel = viewModel
        ) { openCreateWorkoutDialog ->

            WorkoutHeader(
                onCreateWorkoutClick = openCreateWorkoutDialog
            )
        }
        if (workouts.isEmpty()) {
            EmptyWorkoutMessage()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = workouts,
                    key = { it.workout.id }
                ) { item ->
                    WorkoutCard(item = item)
                }
            }
        }
    }
}

@Composable
fun WorkoutHeader(
    onCreateWorkoutClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Workout Library",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = onCreateWorkoutClick,
            modifier = Modifier.height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0F172A),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "Create Workout",
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(text = "Create")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkoutCard(
    item: WorkoutUiItem
) {
    val workout = item.workout
    val totalSec = workout.duration

    Column(
        modifier = Modifier
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
        Text(
            text = workout.name ?: "",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF212121)
        )

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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_fitness),
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

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_time),
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

        if (item.categoryPercents.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item.categoryPercents.forEach { tag ->
                    CategoryPercentTag(tag = tag)
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

@Composable
fun CategoryPercentTag(
    tag: WorkoutCategoryPercent
) {
    Text(
        text = "${tag.category}: ${tag.percent}%",
        color = Color.Black,
        modifier = Modifier
            .background(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(99.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFFE0E0E0),
                shape = RoundedCornerShape(99.dp)
            )
            .padding(
                horizontal = 25.dp,
                vertical = 10.dp
            )
    )
}

@Composable
fun EmptyWorkoutMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No workouts yet",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6C757D)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Create your first workout",
            fontSize = 14.sp,
            color = Color(0xFF9CA3AF)
        )
    }
}