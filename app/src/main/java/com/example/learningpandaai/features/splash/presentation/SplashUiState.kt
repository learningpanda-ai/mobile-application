package com.example.learningpandaai.features.splash.presentation

/**
 * Represents the absolute states of the Splash Screen.
 * The UI observes this to decide when and where to trigger navigation.
 */
sealed interface SplashUiState {
    object Loading : SplashUiState
    object NavigateToAuth : SplashUiState
    object NavigateToOnboarding : SplashUiState
    object  NavigateToDashboard : SplashUiState
}