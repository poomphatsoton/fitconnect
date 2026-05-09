package com.example.train.ui

import com.example.train.view.trainer.exercise.CreateExerciseDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.train.model.Tag
import com.example.train.ui.components.CreateButtons
import com.example.train.ui.components.NavigationBar
import com.example.train.ui.components.TrainerProfileCard
import com.example.train.view.trainer.exercise.CreateWorkoutDialogHost
import com.example.train.view.trainer.exercise.TagDropdown
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
            EditTrainerProfileScreen(
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
                TrainerProfileCard(
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

@Composable
fun EditTrainerProfileScreen(
    initialName: String,
    initialBio: String,
    initialTags: List<Tag>,
    availableTags: List<Tag>,
    isTrainer: Boolean,
    modifier: Modifier = Modifier,
    initialMaxTrainees: Int = 0,
    onCancel: () -> Unit,
    onSave: (name: String, bio: String, maxTrainees: String, password: String, tags: List<Tag>) -> Unit
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    var bio by remember(initialBio) { mutableStateOf(initialBio) }
    var maxTrainees by remember(initialMaxTrainees) { mutableStateOf(initialMaxTrainees.toString()) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedTags by remember(initialTags) { mutableStateOf(initialTags) }
    val isChangingPassword = password.isNotBlank() || confirmPassword.isNotBlank()
    val passwordsDoNotMatch = isChangingPassword && password != confirmPassword

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Edit Profile",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (isTrainer) {
            OutlinedTextField(
                value = maxTrainees,
                onValueChange = { value ->
                    maxTrainees = value.filter { it.isDigit() }
                },
                label = { Text("Max Trainees") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        Text(
            text = "Tags",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        TagDropdown(
            selectedTags = selectedTags,
            items = availableTags,
            onTagSelected = { tag ->
                selectedTags = if (selectedTags.any { it.tagId == tag.tagId }) {
                    selectedTags.filterNot { it.tagId == tag.tagId }
                } else {
                    selectedTags + tag
                }
            },
            onTagRemoved = { tag ->
                selectedTags = selectedTags.filterNot { it.tagId == tag.tagId }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = passwordsDoNotMatch,
            visualTransformation = PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp)
        )

        if (passwordsDoNotMatch) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Passwords do not match",
                color = Color(0xFFDC2626),
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE9ECEF),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(text = "Cancel")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = {
                    if (!passwordsDoNotMatch) {
                        onSave(name, bio, maxTrainees, password.trim(), selectedTags)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text(text = "Save")
            }
        }
    }
}
