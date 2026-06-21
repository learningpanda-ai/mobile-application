package com.example.learningpandaai.features.dashboard.presentation

import androidx.lifecycle.ViewModel
import com.example.learningpandaai.features.profile.domain.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class DashboardUiState(
    val firstName: String = "Student",
    val selectedSubjects: Set<String> = emptySet()
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(readShellState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    fun refreshShellFromCache() {
        _uiState.value = readShellState()
    }

    private fun readShellState(): DashboardUiState {
        val cached = profileRepository.getCachedSnapshot()
        val displayFirstName = cached.firstName.substringBefore(" ").ifBlank { "Student" }
        return DashboardUiState(
            firstName = displayFirstName,
            selectedSubjects = cached.selectedSubjects
        )
    }
}