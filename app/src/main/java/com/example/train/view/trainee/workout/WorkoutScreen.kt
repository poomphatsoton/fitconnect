package com.example.train.view.trainee.workout

import android.net.Uri
import android.os.Build
import android.widget.MediaController
import android.widget.VideoView
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.train.model.trainer.WorkoutExerciseDetail
import com.example.train.model.trainee.TraineeAssignedWorkout
import com.example.train.view.reuseComponent.WorkoutDetailCard
import com.example.train.viewmodel.trainee.TraineeWorkoutViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TraineeWorkoutScreen(
    modifier: Modifier = Modifier,
    viewModel: TraineeWorkoutViewModel = viewModel()
) {
    val uiState by viewModel.uiState
    val selectedWorkout = remember { mutableStateOf<TraineeAssignedWorkout?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadTodayWorkouts()
    }

    selectedWorkout.value?.let { workout ->
        WorkoutStartScreen(
            assignedWorkout = workout,
            onMarkComplete = { slotId, exerciseId ->
                viewModel.markExerciseComplete(slotId, exerciseId)
            },
            onBackToWorkoutList = {
                selectedWorkout.value = null
            }
        )
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        WorkoutTodayHeader(currentTime = uiState.currentTime)

        if (uiState.workouts.isEmpty()) {
            EmptyTodayWorkoutMessage()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.workouts) { assignedWorkout ->
                    AssignedWorkoutSection(
                        assignedWorkout = assignedWorkout,
                        onStartClick = {
                            selectedWorkout.value = assignedWorkout
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun WorkoutTodayHeader(
    currentTime: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "My Workout Today",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            modifier = Modifier.weight(1f)
        )

        Text(
            text = currentTime,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6C757D)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun AssignedWorkoutSection(
    assignedWorkout: TraineeAssignedWorkout,
    onStartClick: () -> Unit
) {
    val canStart = assignedWorkout.canStartNow()

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "${assignedWorkout.startTime.toDisplayTime()} - ${assignedWorkout.endTime.toDisplayTime()}",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        WorkoutDetailCard(item = assignedWorkout.workoutItem)

        if (assignedWorkout.isComplete) {
            Text(
                text = "Status: Complete",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF15803D)
            )
        }

        Button(
            onClick = onStartClick,
            enabled = canStart,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0F172A),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFFE5E7EB),
                disabledContentColor = Color(0xFF9CA3AF)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Start Workout", fontSize = 15.sp)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun WorkoutStartScreen(
    assignedWorkout: TraineeAssignedWorkout,
    onMarkComplete: (slotId: Int, exerciseId: Long) -> Boolean,
    onBackToWorkoutList: () -> Unit
) {
    val exerciseDetails = assignedWorkout.workoutItem.exerciseDetails
    val currentIndex = remember(assignedWorkout) { mutableIntStateOf(0) }
    val completedExerciseIds = remember(assignedWorkout) { mutableStateOf(assignedWorkout.completedExerciseIds) }
    val currentExercise = exerciseDetails.getOrNull(currentIndex.intValue)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onBackToWorkoutList,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE5E7EB),
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Back to workouts")
        }

        Text(
            text = assignedWorkout.workoutItem.workout.name ?: "Workout",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Text(
            text = "${assignedWorkout.startTime.toDisplayTime()} - ${assignedWorkout.endTime.toDisplayTime()}",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6C757D)
        )

        if (currentExercise == null) {
            Text(
                text = "No exercises in this workout",
                fontSize = 16.sp,
                color = Color(0xFF6C757D)
            )
        } else {
            val isCurrentComplete = completedExerciseIds.value.contains(currentExercise.exerciseId)

            ExerciseStartDetail(
                detail = currentExercise,
                currentStep = currentIndex.intValue + 1,
                totalSteps = exerciseDetails.size,
                isComplete = isCurrentComplete
            )

            Button(
                onClick = {
                    val isSuccess = onMarkComplete(assignedWorkout.slotId, currentExercise.exerciseId)
                    if (isSuccess) {
                        completedExerciseIds.value = completedExerciseIds.value + currentExercise.exerciseId
                        if (currentIndex.intValue < exerciseDetails.lastIndex) {
                            currentIndex.intValue += 1
                        }
                    }
                },
                enabled = !isCurrentComplete,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFE5E7EB),
                    disabledContentColor = Color(0xFF9CA3AF)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isCurrentComplete) "Completed" else "Mark as Complete",
                    fontSize = 15.sp
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (currentIndex.intValue > 0) {
                            currentIndex.intValue -= 1
                        }
                    },
                    enabled = currentIndex.intValue > 0,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE5E7EB),
                        contentColor = Color.Black,
                        disabledContainerColor = Color(0xFFE5E7EB),
                        disabledContentColor = Color(0xFF9CA3AF)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Back")
                }

                Button(
                    onClick = {
                        if (currentIndex.intValue < exerciseDetails.lastIndex) {
                            currentIndex.intValue += 1
                        }
                    },
                    enabled = currentIndex.intValue < exerciseDetails.lastIndex &&
                        isCurrentComplete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F172A),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFFE5E7EB),
                        disabledContentColor = Color(0xFF9CA3AF)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "Next")
                }
            }
        }
    }
}

@Composable
private fun ExerciseStartDetail(
    detail: WorkoutExerciseDetail,
    currentStep: Int,
    totalSteps: Int,
    isComplete: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!detail.videoUrl.isNullOrBlank()) {
            ExerciseVideo(
                videoUrl = detail.videoUrl,
                videoName = detail.videoName
            )
        }

        Text(
            text = "Exercise $currentStep / $totalSteps",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF6C757D)
        )

        Text(
            text = detail.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Text(
            text = "${detail.reps} reps",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF212121)
        )

        Text(
            text = "${detail.timePerRep}s / rep",
            fontSize = 16.sp,
            color = Color(0xFF495057)
        )

        Text(
            text = detail.description,
            fontSize = 15.sp,
            color = Color(0xFF6C757D),
            lineHeight = 22.sp
        )

        if (isComplete) {
            Text(
                text = "Completed",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF15803D)
            )
        }
    }
}

@Composable
private fun ExerciseVideo(
    videoUrl: String,
    videoName: String?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = videoName ?: "Exercise video",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF495057)
        )

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            factory = { context ->
                VideoView(context).apply {
                    val controller = MediaController(context)
                    controller.setAnchorView(this)
                    setMediaController(controller)
                    setVideoURI(Uri.parse(videoUrl))
                }
            }
        )
    }
}

@Composable
private fun EmptyTodayWorkoutMessage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "No workouts today",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF6C757D)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun LocalTime.toDisplayTime(): String {
    return format(DateTimeFormatter.ofPattern("HH.mm"))
}

@RequiresApi(Build.VERSION_CODES.O)
private fun TraineeAssignedWorkout.canStartNow(): Boolean {
    val now = LocalTime.now()
    return !now.isBefore(startTime.minusMinutes(10)) && !now.isAfter(endTime)
}
