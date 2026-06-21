package com.example.learningpandaai.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.learningpandaai.features.auth.presentation.AuthScreen
import com.example.learningpandaai.features.auth.presentation.AuthViewModel
import com.example.learningpandaai.features.splash.presentation.SplashScreen
import com.example.learningpandaai.features.splash.presentation.SplashViewModel

@Composable
fun SetupNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route,
        modifier = Modifier
    ) {
        composable(route = Screen.Splash.route) {
            val splashViewModel: SplashViewModel = hiltViewModel()
            SplashScreen(
                viewModel = splashViewModel,
                onNavigate = { targetRoute ->
                    navController.navigate(targetRoute) {
                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(route = Screen.Auth.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            AuthScreen(
                viewModel = authViewModel,
                onNavigateToOnboarding = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.Onboarding.route) {
            PlaceholderScreen("Onboarding — coming next")
        }
        composable(route = Screen.Dashboard.route) {
            PlaceholderScreen("Dashboard — coming next")
        }


    }
}


@Composable
private fun PlaceholderScreen(label: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(label)
    }
}
