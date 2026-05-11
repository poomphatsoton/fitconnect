package com.example.train.view.trainer.workout

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.train.R
import com.example.train.model.trainer.WorkoutUiItem
import com.example.train.view.reuseComponent.WorkoutDetailCard
import com.example.train.view.trainer.exercise.CreateWorkoutDialogHost
import com.example.train.viewmodel.trainer.WorkoutsViewModel

@Composable
fun WorkoutsScreen(
    viewModel: WorkoutsViewModel = viewModel()
) {
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
        ) { openCreateWorkoutDialog, openEditWorkoutDialog ->

            WorkoutHeader(
                onCreateWorkoutClick = openCreateWorkoutDialog
            )

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
                        WorkoutCard(
                            item = item,
                            onEditClick = {
                                openEditWorkoutDialog(item.workout.id)
                            },
                            onDeleteClick = {
                                viewModel.deleteWorkout(item.workout.id)
                            }
                        )
                    }
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
            Text(
                text = "+",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(text = "Create")
        }
    }
}

@Composable
fun WorkoutCard(
    item: WorkoutUiItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    WorkoutDetailCard(item = item) {
            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "Edit Workout",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.delete),
                    contentDescription = "Delete Workout",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
    }
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
