package com.example.train.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.component1
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.train.R
import com.example.train.model.trainer.Trainee
import com.example.train.view.trainer.trainee.activeAndRequest.TraineeTabSelector
import com.example.train.viewmodel.trainer.TraineesViewModel

@Composable
fun TraineesScreen(
    modifier: Modifier = Modifier,
    viewModel: TraineesViewModel = viewModel(),
    onDashboardClick: (Int) -> Unit = {},
    onCalendarClick: (Int) -> Unit = {}
) {
    val uiState by viewModel.uiState

    LaunchedEffect(Unit) {
        viewModel.loadTabData()
    }

    TraineesScreenContent(
        modifier = modifier,
        activeCount = uiState.activeCount,
        requestCount = uiState.requestCount,
        activeTrainees = uiState.allActiveTrainees,
        requestTrainees = uiState.allRequestTrainees,
        onDashboardClick = onDashboardClick,
        onCalendarClick = onCalendarClick,
        onApproveClick = { trainerId, traineeId -> viewModel.onApproveClick(trainerId, traineeId) },
        onDenyClick = { trainerId, traineeId -> viewModel.onDenyClick(trainerId, traineeId) },
        trainerId = uiState.trainerId,
    )
}

@Composable
fun TraineesScreenContent(
    activeCount: Int,
    requestCount: Int,
    activeTrainees: List<Trainee>,
    requestTrainees: List<Trainee>,
    modifier: Modifier = Modifier,
    initialTab: String = "Active",
    onDashboardClick: (Int) -> Unit = {},
    onCalendarClick: (Int) -> Unit = {},
    onApproveClick: (Int, Int) -> Boolean,
    onDenyClick: (Int, Int) -> Boolean,
    trainerId: Int
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .padding(16.dp)
    ) {
        Text(
            text = "Manage Trainees",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        TraineeTabSelector(
            selectedTab = selectedTab,
            onTabSelected = { selectedTab = it },
            tabs = listOf(
                "Active ($activeCount)" to "Active",
                "Requests ($requestCount)" to "Requests"
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (selectedTab == "Active") {
            TraineeActiveList(
                trainees = activeTrainees,
                onDashboardClick = onDashboardClick,
                onCalendarClick = onCalendarClick
            )
        } else {
            TraineeRequestList(
                trainees = requestTrainees,
                trainerId = trainerId,
                onApproveClick = onApproveClick,
                onDenyClick = onDenyClick
            )
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = message, color = Color.Gray)
    }
}
