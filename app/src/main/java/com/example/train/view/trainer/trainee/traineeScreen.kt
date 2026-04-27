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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.train.R
import com.example.train.viewmodel.trainer.TraineesViewModel

data class Trainee(
    val id: Int,
    val name: String,
    val bio: String,
    val tags: List<String>,
    val completionRate: Float = 0f,
    val imageRes: Int? = null
)

@Composable
fun TraineesScreen(
    modifier: Modifier = Modifier,
    viewModel: TraineesViewModel = viewModel(),
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
        onCalendarClick = onCalendarClick
    )
}

@Composable
fun TraineesScreenContent(
    activeCount: Int,
    requestCount: Int,
    modifier: Modifier = Modifier,
    initialTab: String = "Active",
    onCalendarClick: (Int) -> Unit = {}
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    val sampleActiveTrainees = listOf(
        Trainee(
            id = 1,
            name = "Sarah Johnson",
            bio = "Looking to build strength and improve fitness. Love trying new workouts!",
            tags = listOf("strength", "weight-loss", "endurance"),
            imageRes = R.drawable.ic_person
        ),
        Trainee(
            id = 2,
            name = "Mike Wilson",
            bio = "Former athlete getting back into shape. Focused on building muscle and endurance.",
            tags = listOf("muscle-gain", "strength", "endurance"),
            imageRes = R.drawable.ic_person
        )
    )

    val sampleRequests = listOf(
        Trainee(
            id = 3,
            name = "Alex Martinez",
            bio = "Marathon runner seeking cross-training options",
            tags = listOf("endurance", "cardio", "injury-prevention"),
            imageRes = R.drawable.ic_person
        )
    )

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
                trainees = sampleActiveTrainees,
                onCalendarClick = onCalendarClick
            )
        } else {
            TraineeRequestList(
                trainees = sampleRequests
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
