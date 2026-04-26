package com.example.train.ui

import com.example.train.ui.components.CreateExerciseDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.train.ui.components.CreateButtons
import com.example.train.ui.components.CreateWorkoutDialogHost
import com.example.train.ui.components.NavigationBar
import com.example.train.ui.components.NoTraineesCard
import com.example.train.ui.components.OverviewCard
import com.example.train.ui.components.TrainerProfileCard
import com.example.train.viewmodel.ExercisesViewModel
import com.example.train.viewmodel.TrainerHomeViewModel
import com.example.train.viewmodel.WorkoutsViewModel

@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    viewModel: TrainerHomeViewModel = viewModel(),
    exercisesViewModel: ExercisesViewModel = viewModel(),
    workoutsViewModel: WorkoutsViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState

    var showCreateExerciseDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
    }

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadOverviewData()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        topBar = {
            NavigationBar(
                title = "FitConnect",
                onLogout = onLogout
            )
        },
        containerColor = Color(0xFFF8F9FA)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            TrainerProfileCard(
                name = uiState.trainerName,
                bio = uiState.trainerBio
            )

            Spacer(modifier = Modifier.height(16.dp))

            CreateWorkoutDialogHost(
                viewModel = workoutsViewModel,
                onCreated = {
                    viewModel.loadOverviewData()
                }
            ) { openCreateWorkoutDialog ->

                CreateButtons(
                    onCreateExercise = {
                        showCreateExerciseDialog = true
                    },
                    onCreateWorkout = {
                        openCreateWorkoutDialog()
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OverviewCard(
                activeTrainees = uiState.activeTrainees,
                exercises = uiState.exercises,
                workouts = uiState.workouts,
                pendingRequests = uiState.pendingRequests,
                onPendingClick = {
                    Toast.makeText(
                        context,
                        "Pending Requests Clicked!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            NoTraineesCard()
        }
    }

    if (showCreateExerciseDialog) {
        CreateExerciseDialog(
            onDismiss = {
                showCreateExerciseDialog = false
            },
            onConfirm = { name, description, category1, category2, time ->
                val error = exercisesViewModel.createExercise(
                    name = name,
                    description = description,
                    category1 = category1,
                    category2 = category2,
                    timePerRepText = time
                )

                if (error == null) {
                    Toast.makeText(
                        context,
                        "Created successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                    showCreateExerciseDialog = false
                    viewModel.loadOverviewData()
                } else {
                    Toast.makeText(
                        context,
                        error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}