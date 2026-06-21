package com.example.learningpandaai.features.auth.presentation

sealed interface AuthUiEvent {
    object NavigateToDashboard : AuthUiEvent
    object NavigateToOnboarding : AuthUiEvent
}
