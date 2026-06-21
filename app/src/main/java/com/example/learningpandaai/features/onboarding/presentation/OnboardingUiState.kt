package com.example.learningpandaai.features.onboarding.presentation

import com.example.learningpandaai.core.util.InputValidation

data class OnboardingUiState(
    val currentStep: Int = 1,
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val errorMessage: String? = null,

    val firstName: String = "",
    val lastName: String = "",
    val appLanguage: String = "English",
    val selectedState: String = "",
    val cityInput: String = "",
    val parentName: String = "",
    val parentMobile: String = "",
    val parentEmail: String = "",

    val parentEmailError: Boolean = false,
    val parentPhoneError: Boolean = false,

    val selectedClass: String = "",
    val selectedBoard: String = "",

    val selectedSubjects: Set<String> = emptySet(),

    val favoriteSubject: String = "",
    val studiesFeeling: String = "",
    val careerIdea: String = "",
    val discoverStrengths: String = ""
) {
    val isStep1Valid: Boolean
        get() = firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                appLanguage.isNotBlank() &&
                selectedState.isNotBlank() &&
                cityInput.isNotBlank() &&
                parentName.isNotBlank() &&
                InputValidation.isPhoneComplete(parentMobile) &&
                parentEmail.isNotBlank() &&
                InputValidation.hasValidEmailFormat(parentEmail) &&
                !parentEmailError &&
                !parentPhoneError

    val isStep2Valid: Boolean
        get() = selectedClass.isNotBlank() && selectedBoard.isNotBlank()

    val isStep3Valid: Boolean
        get() = selectedSubjects.isNotEmpty()

    val isStep4Valid: Boolean
        get() = favoriteSubject.isNotBlank() &&
                studiesFeeling.isNotBlank() &&
                careerIdea.isNotBlank() &&
                discoverStrengths.isNotBlank()

    val canContinueCurrentStep: Boolean
        get() = when (currentStep) {
            1 -> isStep1Valid
            2 -> isStep2Valid
            3 -> isStep3Valid
            4 -> isStep4Valid
            else -> false
        }
}