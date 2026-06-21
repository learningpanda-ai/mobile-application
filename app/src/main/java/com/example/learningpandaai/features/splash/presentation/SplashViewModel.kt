package com.example.learningpandaai.features.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningpandaai.core.data.SecurePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val securePreferences: SecurePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState : StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    private fun checkSession()
    {
        viewModelScope.launch {
            delay(2000) // Match splash minimum display time

            val token = securePreferences.getOAuthToken()

            if(token.isNullOrBlank())
            {
                _uiState.value = SplashUiState.NavigateToAuth
                return@launch
            }

            // Phase 1: trust cached profile data.
            // Phase 2 (with ProfileRepository): validate token via GET /profile
            _uiState.value = resolveDestination()
        }
    }

    private fun resolveDestination() : SplashUiState {
        val name = securePreferences.getFirstName()
        return if (name.isNullOrBlank()) {
            SplashUiState.NavigateToOnboarding
        }else {
            SplashUiState.NavigateToOnboarding
        }
    }

}