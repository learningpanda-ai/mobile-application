package com.example.learningpandaai.features.profile.presentation

import com.example.learningpandaai.core.util.InputValidation

data class EditProfileUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val loadError: String? = null,
    val saveError: String? = null,
    val firstName: String = "",
    val lastName: String = "",
    val grade: String = "",
    val schoolBoard: String = "",
    val city: String = "",
    val state: String = "",
    val parentName: String = "",
    val parentMobile: String = "",
    val parentEmail: String = "",
    val gradeOptions: List<SelectableChip> = emptyList(),
    val boardOptions: List<SelectableChip> = emptyList(),
    val mobileError: Boolean = false,
    val emailError: Boolean = false,
) {
    val canSave: Boolean
        get() = firstName.isNotBlank() &&
            lastName.isNotBlank() &&
            grade.isNotBlank() &&
            schoolBoard.isNotBlank() &&
            InputValidation.isPhoneComplete(parentMobile) &&
            parentEmail.isNotBlank() &&
            InputValidation.hasValidEmailFormat(parentEmail) &&
            !mobileError &&
            !emailError &&
            !isLoading &&
            !isSaving
}
