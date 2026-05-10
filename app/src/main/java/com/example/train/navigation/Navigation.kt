package com.example.train.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.train.ui.MainScreen
import com.example.train.view.authentication.LoginScreen
import com.example.train.view.authentication.RegistrationScreen
import com.example.train.view.trainee.TraineeMainScreen
import com.example.train.viewmodel.authentication.LoginViewModel

object Routes {
    const val LOGIN = "login"
    const val HOME = "home"
    const val CREATE_ACCOUNT = "create_account"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()

    val startDestination =
        if (loginViewModel.isLoggedIn()) Routes.HOME else Routes.LOGIN

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginClick = { username, password ->
                    loginViewModel.login(username, password) {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) {
                                inclusive = true
                            }
                        }
                    }
                },
                onCreateAccountClick = {
                    navController.navigate(Routes.CREATE_ACCOUNT)
                }
            )
        }

        composable(Routes.CREATE_ACCOUNT) {
            RegistrationScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.CREATE_ACCOUNT) {
                            inclusive = true
                        }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(Routes.HOME) {
            val onLogout = {
                loginViewModel.logout()

                navController.navigate(Routes.LOGIN) {
                    popUpTo(Routes.HOME) {
                        inclusive = true
                    }
                }
            }

            if (loginViewModel.getUserRole() == "trainee") {
                TraineeMainScreen(onLogout = onLogout)
            } else {
                MainScreen(onLogout = onLogout)
            }
        }

    }
}
