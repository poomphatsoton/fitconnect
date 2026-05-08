package com.example.train.ui

import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.train.R
import com.example.train.ui.components.BottomNavigationBar
import com.example.train.view.trainer.exercise.ExercisesScreen
import com.example.train.view.trainer.workout.WorkoutsScreen

object MainRoutes {
    const val HOME = "home"
    const val EXERCISES = "exercises"
    const val WORKOUTS = "workouts"
    const val TRAINEES = "trainees"
    const val TRAINEE_CALENDAR = "trainee_calendar"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    @DrawableRes val icon: Int
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem(
            route = MainRoutes.HOME,
            label = "Home",
            icon = R.drawable.ic_home
        ),
        BottomNavItem(
            route = MainRoutes.EXERCISES,
            label = "Exercises",
            icon = R.drawable.ic_fitness
        ),
        BottomNavItem(
            route = MainRoutes.WORKOUTS,
            label = "Workouts",
            icon = R.drawable.ic_list
        ),
        BottomNavItem(
            route = MainRoutes.TRAINEES,
            label = "Trainees",
            icon = R.drawable.ic_group
        )
    )

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        bottomBar = {
            BottomNavigationBar(
                items = bottomNavItems,
                navController = navController
            )
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = MainRoutes.HOME,
            modifier = Modifier
                .padding(innerPadding)
                .background(Color(0xFFF5F5F5))
        ) {
            composable(MainRoutes.HOME) {
                HomeScreen(
                    onLogout = onLogout
                )
            }

            composable(MainRoutes.EXERCISES) {
                ExercisesScreen()
            }

            composable(MainRoutes.WORKOUTS) {
                WorkoutsScreen()
            }

            composable(MainRoutes.TRAINEES) {
                TraineesScreen(
                    onCalendarClick = { traineeId ->
                        navController.navigate(MainRoutes.TRAINEE_CALENDAR)
                    }
                )
            }

            composable(MainRoutes.TRAINEE_CALENDAR) {
                TraineeCalendarScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}