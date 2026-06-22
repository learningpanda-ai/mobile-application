package com.example.learningpandaai.features.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningpandaai.core.data.SecurePreferences
import com.example.learningpandaai.core.network.SessionExpiredException
import com.example.learningpandaai.core.util.Logger
import com.example.learningpandaai.features.profile.domain.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkOnboardingStatus()
    }

    private fun checkOnboardingStatus() {
        viewModelScope.launch {
            delay(2000) // Align with splash animation minimum display time

            val token = securePreferences.getOAuthToken()
            if (token.isNullOrBlank()) {
                _uiState.value = SplashUiState.NavigateToAuth
                return@launch
            }

            // Validate the stored token against the backend rather than trusting it blindly —
            // TokenAuthenticator will attempt a refresh on 401; if that also fails it surfaces
            // as a SessionExpiredException and we route to auth instead of the dashboard.
            profileRepository.getCurrentProfile()
                .onSuccess {
                    _uiState.value = resolveDestination()
                }
                .onFailure { throwable ->
                    if (throwable is SessionExpiredException) {
                        _uiState.value = SplashUiState.NavigateToAuth
                    } else {
                        Logger.e(
                            "checkOnboardingStatus: profile check failed — ${throwable.message}",
                            throwable
                        )
                        // Network/server error — fall back to cached profile rather than stranding
                        // an offline user on the splash screen.
                        _uiState.value = resolveDestination()
                    }
                }
        }
    }

    private fun resolveDestination(): SplashUiState {
        val name = securePreferences.getFirstName()
        return if (name.isNullOrBlank()) SplashUiState.NavigateToOnboarding else SplashUiState.NavigateToDashboard
    }
}
