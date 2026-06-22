package com.example.learningpandaai.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningpandaai.core.data.ProfileCacheNotifier
import com.example.learningpandaai.core.data.SecurePreferences
import com.example.learningpandaai.core.util.InputValidation
import com.example.learningpandaai.core.util.Logger
import com.example.learningpandaai.core.util.ProfileFieldFormatter
import com.example.learningpandaai.features.profile.domain.ProfileRepository
import com.example.learningpandaai.features.profile.domain.ProfileUpdateParams
import com.example.learningpandaai.features.profile.domain.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val securePreferences: SecurePreferences,
    private val profileCacheNotifier: ProfileCacheNotifier
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    private val _events = Channel<EditProfileUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadProfile()
    }

    fun retryLoad() = loadProfile()

    fun onFirstNameChanged(value: String) {
        _uiState.value = _uiState.value.copy(firstName = value, saveError = null)
    }

    fun onLastNameChanged(value: String) {
        _uiState.value = _uiState.value.copy(lastName = value, saveError = null)
    }

    fun onGradeSelected(gradeId: String) {
        _uiState.value = _uiState.value.copy(
            grade = gradeId,
            gradeOptions = _uiState.value.gradeOptions.map {
                it.copy(isSelected = it.id == gradeId)
            },
            saveError = null
        )
    }

    fun onBoardSelected(boardId: String) {
        _uiState.value = _uiState.value.copy(
            schoolBoard = boardId,
            boardOptions = _uiState.value.boardOptions.map {
                it.copy(isSelected = it.id == boardId)
            },
            saveError = null
        )
    }

    fun onParentMobileChanged(mobile: String) {
        val digitsOnly = InputValidation.filterPhoneDigits(mobile)
        _uiState.value = _uiState.value.copy(
            parentMobile = digitsOnly,
            mobileError = InputValidation.phoneShowsError(digitsOnly),
            saveError = null
        )
    }

    fun onParentEmailChanged(email: String) {
        _uiState.value = _uiState.value.copy(
            parentEmail = email,
            emailError = InputValidation.emailShowsError(email),
            saveError = null
        )
    }

    fun saveProfile() {
        val state = _uiState.value
        if (!state.canSave) {
            _uiState.value = state.copy(
                mobileError = InputValidation.phoneShowsError(state.parentMobile),
                emailError = InputValidation.emailShowsError(state.parentEmail),
                saveError = "Please fix the highlighted fields before saving."
            )
            return
        }

        _uiState.value = state.copy(isSaving = true, saveError = null)

        viewModelScope.launch {
            profileRepository.updateProfile(
                ProfileUpdateParams(
                    firstName = state.firstName.trim(),
                    lastName = state.lastName.trim(),
                    city = state.city.takeIf { it.isNotBlank() },
                    state = state.state.takeIf { it.isNotBlank() },
                    parentName = state.parentName.takeIf { it.isNotBlank() },
                    grade = state.grade,
                    schoolBoard = state.schoolBoard,
                    parentMobile = state.parentMobile,
                    parentEmail = state.parentEmail.trim()
                )
            ).onSuccess { profile ->
                persistLocal(profile, state)
                Logger.d("saveProfile: success — uid=${profile.id}")
                _uiState.value = _uiState.value.copy(isSaving = false)
                _events.trySend(EditProfileUiEvent.Saved)
            }.onFailure { throwable ->
                Logger.e("saveProfile: failed — ${throwable.message}", throwable)
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    saveError = throwable.message ?: "Failed to save profile. Please retry."
                )
            }
        }
    }

    private fun persistLocal(profile: UserProfile, state: EditProfileUiState) {
        securePreferences.saveStudentProfile(
            firstName = state.firstName.trim(),
            gradeLevel = state.grade,
            board = state.schoolBoard
        )
        securePreferences.saveParentProfile(
            parentName = profile.parentName,
            parentMobile = state.parentMobile,
            parentEmail = state.parentEmail.trim()
        )
        profileCacheNotifier.notifyProfileCacheUpdated()
    }

    private fun loadProfile() {
        _uiState.value = EditProfileUiState(isLoading = true)
        viewModelScope.launch {
            profileRepository.getCurrentProfile()
                .onSuccess { profile ->
                    val normalizedGrade = ProfileFieldFormatter.gradeToChipId(profile.grade)
                    _uiState.value = EditProfileUiState(
                        isLoading = false,
                        firstName = profile.firstName,
                        lastName = profile.lastName,
                        grade = normalizedGrade,
                        schoolBoard = profile.schoolBoard,
                        city = profile.city,
                        state = profile.state,
                        parentName = profile.parentName,
                        parentMobile = InputValidation.filterPhoneDigits(profile.parentMobile),
                        parentEmail = profile.parentEmail.ifBlank {
                            securePreferences.getParentEmail().orEmpty()
                        },
                        gradeOptions = gradeChipOptions(normalizedGrade),
                        boardOptions = boardChipOptions(profile.schoolBoard)
                    )
                }
                .onFailure { throwable ->
                    _uiState.value = EditProfileUiState(
                        isLoading = false,
                        loadError = throwable.message ?: "Failed to load profile."
                    )
                }
        }
    }

    private fun normalizeGradeId(grade: String): String = ProfileFieldFormatter.gradeToChipId(grade)

    companion object {
        private val gradeIds = listOf("8", "9", "10", "11", "12")
        private val boardIds = listOf("CBSE", "ICSE", "IGCSE", "IB", "State Board")

        private fun gradeChipOptions(selectedGrade: String): List<SelectableChip> =
            gradeIds.map { id ->
                SelectableChip(
                    id = id,
                    label = "Class $id",
                    isSelected = id == selectedGrade || selectedGrade == "class-$id"
                )
            }

        private fun boardChipOptions(selectedBoard: String): List<SelectableChip> =
            boardIds.map { id ->
                SelectableChip(
                    id = id,
                    label = id,
                    isSelected = id.equals(selectedBoard, ignoreCase = true)
                )
            }
    }
}

sealed interface EditProfileUiEvent {
    data object Saved : EditProfileUiEvent
}
