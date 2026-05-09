package com.example.train.view.trainee.home

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.train.view.reuseComponent.EditUserProfileScreen
import com.example.train.view.reuseComponent.UserProfileCard
import com.example.train.viewmodel.trainee.TraineeHomeViewModel

@Composable
fun TraineeHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: TraineeHomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState
    var showEditProfile by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadHomeData()
    }

    if (showEditProfile) {
        EditUserProfileScreen(
            initialName = uiState.traineeName,
            initialBio = uiState.traineeBio,
            initialTags = uiState.traineeTags,
            availableTags = uiState.availableTags,
            isTrainer = false,
            modifier = modifier,
            onCancel = {
                showEditProfile = false
            },
            onSave = { name, bio, _, password, tags ->
                val error = viewModel.updateTraineeProfile(
                    name = name,
                    bio = bio,
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
            modifier = modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            UserProfileCard(
                name = uiState.traineeName,
                bio = uiState.traineeBio,
                tags = uiState.traineeTags,
                isTrainer = false,
                onEditClick = {
                    showEditProfile = true
                }
            )
        }
    }
}
