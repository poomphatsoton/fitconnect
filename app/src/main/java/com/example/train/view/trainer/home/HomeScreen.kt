package com.example.train.ui

import com.example.train.view.trainer.exercise.CreateExerciseDialog
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
import com.example.train.ui.components.NavigationBar
import com.example.train.view.reuseComponent.EditUserProfileScreen
import com.example.train.view.reuseComponent.UserProfileCard
import com.example.train.view.trainer.exercise.CreateWorkoutDialogHost
import com.example.train.viewmodel.trainer.ExercisesViewModel
import com.example.train.viewmodel.trainer.TrainerHomeViewModel
import com.example.train.viewmodel.trainer.WorkoutsViewModel

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
    var showEditProfile by remember { mutableStateOf(false) }

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

        if (showEditProfile) {
            EditUserProfileScreen(
                initialName = uiState.trainerName,
                initialBio = uiState.trainerBio,
                initialMaxTrainees = uiState.maxTrainees,
                initialTags = uiState.trainerTags,
                availableTags = uiState.availableTags,
                isTrainer = true,
                modifier = Modifier.padding(innerPadding),
                onCancel = {
                    showEditProfile = false
                },
                onSave = { name, bio, maxTrainees, password, tags ->
                    val error = viewModel.updateTrainerProfile(
                        name = name,
                        bio = bio,
                        maxTraineesText = maxTrainees,
                        password = password,
                        tags = tags
                    )

                    if (error == null) {
                        Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                        showEditProfile = false
                    } else {
                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                    }
                }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                UserProfileCard(
                    name = uiState.trainerName,
                    bio = uiState.trainerBio,
                    tags = uiState.trainerTags,
                    isTrainer = true,
                    activeTrainees = uiState.activeTrainees,
                    maxTrainees = uiState.maxTrainees,
                    onEditClick = {
                        showEditProfile = true
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                CreateWorkoutDialogHost(
                    viewModel = workoutsViewModel,
                    onCreated = {
                        viewModel.loadOverviewData()
                    }
                ) { openCreateWorkoutDialog, _ ->

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
            }
        }
    }

    if (showCreateExerciseDialog) {
        CreateExerciseDialog(
            onDismiss = {
                showCreateExerciseDialog = false
            },
            onConfirm = { name, description, time, tags ->
                val error = exercisesViewModel.createExercise(
                    name = name,
                    description = description,
                    timePerRepText = time,
                    tags = tags
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
