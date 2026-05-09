package com.example.train.view.trainee

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.train.R
import com.example.train.ui.BottomNavItem
import com.example.train.ui.components.BottomNavigationBar
import com.example.train.ui.components.NavigationBar
import com.example.train.view.trainee.home.TraineeHomeScreen

private object TraineeRoutes {
    const val HOME = "trainee_home"
    const val WORKOUT = "trainee_workout"
    const val CALENDAR = "trainee_calendar"
    const val TRAINER = "trainee_trainer"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TraineeMainScreen(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem(
            route = TraineeRoutes.HOME,
            label = "Home",
            icon = R.drawable.ic_home
        ),
        BottomNavItem(
            route = TraineeRoutes.WORKOUT,
            label = "Workout",
            icon = R.drawable.ic_fitness
        ),
        BottomNavItem(
            route = TraineeRoutes.CALENDAR,
            label = "Calendar",
            icon = R.drawable.ic_calendar
        ),
        BottomNavItem(
            route = TraineeRoutes.TRAINER,
            label = "Trainer",
            icon = R.drawable.ic_group
        )
    )

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            NavigationBar(
                title = "FitConnect",
                onLogout = onLogout
            )
        },
        bottomBar = {
            BottomNavigationBar(
                items = bottomNavItems,
                navController = navController
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TraineeRoutes.HOME,
            modifier = Modifier
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
        ) {
            composable(TraineeRoutes.HOME) {
                TraineeHomeScreen()
            }

            composable(TraineeRoutes.WORKOUT) {
                TraineePlaceholderScreen(title = "Workout")
            }

            composable(TraineeRoutes.CALENDAR) {
                TraineePlaceholderScreen(title = "Calendar")
            }

            composable(TraineeRoutes.TRAINER) {
                TraineePlaceholderScreen(title = "Trainer")
            }
        }
    }
}

@Composable
private fun TraineePlaceholderScreen(
    title: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
    }
}
