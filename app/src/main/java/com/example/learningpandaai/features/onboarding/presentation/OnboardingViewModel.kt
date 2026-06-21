package com.example.learningpandaai.features.onboarding.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningpandaai.core.util.InputValidation
import com.example.learningpandaai.core.util.Logger
import com.example.learningpandaai.features.onboarding.domain.OnboardingProfile
import com.example.learningpandaai.features.onboarding.domain.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onFirstNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(firstName = name, errorMessage = null)
    }

    fun onLastNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(lastName = name, errorMessage = null)
    }

    fun onAppLanguageChanged(language: String) {
        _uiState.value = _uiState.value.copy(appLanguage = language, errorMessage = null)
    }

    fun onStateChanged(state: String) {
        _uiState.value = _uiState.value.copy(
            selectedState = state,
            cityInput = "",
            errorMessage = null
        )
    }

    fun onCityChanged(city: String) {
        _uiState.value = _uiState.value.copy(cityInput = city)
    }

    fun onParentNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(parentName = name, errorMessage = null)
    }

    fun onParentMobileChanged(mobile: String) {
        val digitsOnly = InputValidation.filterPhoneDigits(mobile)
        _uiState.value = _uiState.value.copy(
            parentMobile = digitsOnly,
            parentPhoneError = InputValidation.phoneShowsError(digitsOnly),
            errorMessage = null
        )
    }

    fun onParentEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(
            parentEmail = email,
            parentEmailError = InputValidation.emailShowsError(email),
            errorMessage = null
        )
    }

    fun onClassSelected(className: String) {
        _uiState.value = _uiState.value.copy(selectedClass = className)
    }

    fun onBoardSelected(boardName: String) {
        _uiState.value = _uiState.value.copy(selectedBoard = boardName)
    }

    fun onSubjectToggled(subject: String) {
        val current = _uiState.value.selectedSubjects
        val updated = if (current.contains(subject)) current - subject else current + subject
        _uiState.value = _uiState.value.copy(selectedSubjects = updated)
    }

    fun onFavoriteSubjectSelected(subject: String) {
        _uiState.value = _uiState.value.copy(favoriteSubject = subject)
    }

    fun onStudiesFeelingSelected(feeling: String) {
        _uiState.value = _uiState.value.copy(studiesFeeling = feeling)
    }

    fun onCareerIdeaSelected(idea: String) {
        _uiState.value = _uiState.value.copy(careerIdea = idea)
    }

    fun onDiscoverStrengthsSelected(choice: String) {
        _uiState.value = _uiState.value.copy(discoverStrengths = choice)
    }

    fun nextStep() {
        val state = _uiState.value
        if (!state.canContinueCurrentStep) {
            _uiState.value = state.copy(
                errorMessage = when (state.currentStep) {
                    1 -> "Please complete all fields with valid email and 10-digit phone."
                    2 -> "Please select your class and board."
                    3 -> "Please select at least one subject."
                    else -> state.errorMessage
                }
            )
            return
        }
        val targetStep = when (state.currentStep) {
            1 -> 2
            2 -> 3
            3 -> 4
            else -> state.currentStep
        }
        _uiState.value = state.copy(currentStep = targetStep, errorMessage = null)
    }

    fun previousStep() {
        val state = _uiState.value
        if (state.currentStep > 1) {
            _uiState.value = state.copy(
                currentStep = state.currentStep - 1,
                errorMessage = null
            )
        }
    }

    fun completeSetup() {
        val state = _uiState.value
        if (!state.isStep4Valid) {
            _uiState.value = state.copy(
                errorMessage = "Please complete all selections to personalize your sanctuary."
            )
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val profile = OnboardingProfile(
                firstName = state.firstName.trim(),
                lastName = state.lastName.trim(),
                state = state.selectedState,
                city = state.cityInput,
                parentName = state.parentName.trim(),
                parentMobile = state.parentMobile.trim(),
                parentEmail = state.parentEmail.trim(),
                gradeLevel = state.selectedClass,
                board = state.selectedBoard,
                selectedSubjects = state.selectedSubjects,
                favoriteSubject = state.favoriteSubject,
                studiesFeeling = state.studiesFeeling,
                careerIdea = state.careerIdea,
                discoverStrengths = state.discoverStrengths
            )

            onboardingRepository.syncOnboardingData(profile)
                .onSuccess {
                    Logger.d("completeSetup: onboarding synced — routing to success gateway")
                    _uiState.value = _uiState.value.copy(isLoading = false, currentStep = 5)
                }
                .onFailure { throwable ->
                    Logger.e("completeSetup: sync failed — ${throwable.message}", throwable)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Profile setup failed. Please try again."
                    )
                }
        }
    }

    fun goToDashboard() {
        if (_uiState.value.currentStep == 5) {
            _uiState.value = _uiState.value.copy(isComplete = true)
        }
    }
}