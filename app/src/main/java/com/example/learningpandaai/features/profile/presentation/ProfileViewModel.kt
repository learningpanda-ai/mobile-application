package com.example.learningpandaai.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningpandaai.core.network.ApiErrorMapper
import com.example.learningpandaai.core.network.SessionExpiredException
import com.example.learningpandaai.core.util.Logger
import com.example.learningpandaai.core.util.PlanTier
import com.example.learningpandaai.features.profile.domain.ProfileRepository
import com.example.learningpandaai.features.profile.domain.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import com.example.learningpandaai.core.util.InputValidation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileOverlayState(
    val signOutDialogVisible: Boolean = false,
    val deleteDialog: DeleteAccountDialogState = DeleteAccountDialogState()
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(
        ProfileUiState.Loading(buildLabels().loadingMessage)
    )
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = Channel<ProfileUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private val _overlayState = MutableStateFlow(ProfileOverlayState())
    val overlayState: StateFlow<ProfileOverlayState> = _overlayState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var notificationPrefs: Map<String, Boolean> = defaultNotificationPrefs()

    init {
        loadProfile(showLoading = true)
    }

    fun retry() = loadProfile(showLoading = true)

    /** Pull latest profile from the server (e.g. after website edits or tab focus). */
    fun refreshFromServer() = loadProfile(showLoading = _uiState.value !is ProfileUiState.Success)

    /** Pull-to-refresh on the profile tab. */
    fun refresh() {
        if (_isRefreshing.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            loadProfileAwait(showLoading = false)
            _isRefreshing.value = false
        }
    }

    fun showSignOutDialog() {
        _overlayState.update { it.copy(signOutDialogVisible = true) }
    }

    fun dismissSignOutDialog() {
        _overlayState.update { it.copy(signOutDialogVisible = false) }
    }

    fun confirmSignOut() {
        dismissSignOutDialog()
        logout()
    }

    fun showDeleteAccountDialog(email: String) {
        _overlayState.update {
            it.copy(
                deleteDialog = DeleteAccountDialogState(
                    isVisible = true,
                    step = DeleteAccountStep.Warning,
                    email = email,
                    errorMessage = null
                )
            )
        }
    }

    fun dismissDeleteAccountDialog() {
        _overlayState.update {
            it.copy(deleteDialog = DeleteAccountDialogState())
        }
    }

    fun proceedDeleteToOtpStep() {
        val email = _overlayState.value.deleteDialog.email
        if (email.isBlank()) return
        _overlayState.update {
            it.copy(
                deleteDialog = it.deleteDialog.copy(
                    step = DeleteAccountStep.OtpEntry,
                    isSendingOtp = true,
                    errorMessage = null
                )
            )
        }
        viewModelScope.launch {
            profileRepository.requestAccountDeletionOtp(email)
                .onSuccess {
                    _overlayState.update { state ->
                        state.copy(
                            deleteDialog = state.deleteDialog.copy(
                                isSendingOtp = false,
                                errorMessage = null
                            )
                        )
                    }
                }
                .onFailure { throwable ->
                    _overlayState.update { state ->
                        state.copy(
                            deleteDialog = state.deleteDialog.copy(
                                isSendingOtp = false,
                                step = DeleteAccountStep.Warning,
                                errorMessage = throwable.message
                                    ?: "Could not send verification code."
                            )
                        )
                    }
                }
        }
    }

    fun onDeleteOtpChanged(otp: String) {
        val digits = InputValidation.filterOtpDigits(otp)
        _overlayState.update { state ->
            state.copy(
                deleteDialog = state.deleteDialog.copy(
                    otpInput = digits,
                    errorMessage = null
                )
            )
        }
    }

    fun confirmAccountDeletion() {
        val dialog = _overlayState.value.deleteDialog
        if (!InputValidation.isOtpComplete(dialog.otpInput)) {
            _overlayState.update { state ->
                state.copy(
                    deleteDialog = state.deleteDialog.copy(
                        errorMessage = "Enter the 6-digit code from your email."
                    )
                )
            }
            return
        }
        _overlayState.update { state ->
            state.copy(
                deleteDialog = state.deleteDialog.copy(
                    isDeleting = true,
                    errorMessage = null
                )
            )
        }
        viewModelScope.launch {
            profileRepository.confirmAccountDeletion(dialog.email, dialog.otpInput)
                .onSuccess {
                    dismissDeleteAccountDialog()
                    Logger.d("confirmAccountDeletion: success — routing to Auth")
                    _events.trySend(ProfileUiEvent.AccountDeleted)
                }
                .onFailure { throwable ->
                    _overlayState.update { state ->
                        state.copy(
                            deleteDialog = state.deleteDialog.copy(
                                isDeleting = false,
                                errorMessage = throwable.message
                                    ?: "Could not delete account. Please retry."
                            )
                        )
                    }
                }
        }
    }

    fun logout() {
        viewModelScope.launch {
            profileRepository.logout()
                .onSuccess {
                    Logger.d("logout: session cleared — routing to Auth")
                    _events.trySend(ProfileUiEvent.LoggedOut)
                }
                .onFailure { throwable ->
                    Logger.e("logout: failed — ${throwable.message}", throwable)
                    _events.trySend(ProfileUiEvent.LoggedOut)
                }
        }
    }

    fun onNotificationToggled(id: String, enabled: Boolean) {
        notificationPrefs = notificationPrefs.toMutableMap().apply { put(id, enabled) }
        updateSuccess { state ->
            state.copy(
                notifications = state.notifications.map { item ->
                    if (item.id == id) item.copy(isEnabled = enabled) else item
                }
            )
        }
    }

    private fun loadProfile(showLoading: Boolean) {
        val labels = buildLabels()
        if (showLoading) {
            _uiState.value = ProfileUiState.Loading(labels.loadingMessage)
        }
        viewModelScope.launch { loadProfileAwait(showLoading) }
    }

    private suspend fun loadProfileAwait(showLoading: Boolean) {
        val labels = buildLabels()
        profileRepository.getCurrentProfile()
            .onSuccess { profile ->
                Logger.d("loadProfile: success — uid=${profile.id}")
                _uiState.value = profile.toSuccessState(labels, notificationPrefs)
                _events.trySend(ProfileUiEvent.ProfileDataSynced)
            }
            .onFailure { throwable ->
                Logger.e("loadProfile: failed — ${throwable.message}", throwable)
                if (showLoading || _uiState.value !is ProfileUiState.Success) {
                    val sessionExpired = throwable is SessionExpiredException
                    _uiState.value = ProfileUiState.Error(
                        title = if (sessionExpired) ApiErrorMapper.SESSION_EXPIRED_TITLE else null,
                        message = throwable.message ?: "Failed to load profile. Please retry.",
                        retryLabel = if (sessionExpired) "Sign in" else labels.retryLabel
                    )
                }
            }
    }

    private fun updateSuccess(block: (ProfileUiState.Success) -> ProfileUiState.Success) {
        val current = _uiState.value
        if (current is ProfileUiState.Success) {
            _uiState.value = block(current)
        }
    }

    private fun buildLabels() = ProfileLabels(
        screenTitle = "Profile",
        academicSectionTitle = "Learning details",
        academicClassLabel = "Class",
        educationalBoardLabel = "Board",
        enrolledCoursesTitle = "Your subjects",
        guardianSectionTitle = "Guardian",
        guardianNameLabel = "Guardian name",
        guardianPhoneLabel = "Guardian phone",
        guardianEmailLabel = "Guardian email address",
        notificationsTitle = "Notifications",
        notificationsSubtitle = "Choose what we send you and your guardian.",
        helpSupportLabel = "Help and support",
        accountSectionTitle = "Subscription",
        upgradeAccountLabel = "Upgrade to Pro",
        deleteAccountLabel = "Delete account",
        signOutLabel = "Sign out",
        editProfileActionLabel = "Edit profile",
        learningSummaryTitle = "Your learning",
        signOutDialogTitle = "Sign out?",
        signOutDialogMessage = "You will need to sign in again to access your lessons and progress.",
        signOutConfirmLabel = "Sign out",
        signOutCancelLabel = "Cancel",
        deleteDialogTitle = "Delete account?",
        deleteDialogWarning = "This permanently removes your profile, progress, and chat history. This cannot be undone.",
        deleteDialogContinueLabel = "Continue",
        deleteOtpTitle = "Confirm with email code",
        deleteOtpMessage = "We sent a 6-digit code to your registered email. Enter it to confirm deletion.",
        deleteOtpHint = "Verification code",
        deleteConfirmLabel = "Delete my account",
        deleteCancelLabel = "Cancel",
        editProfileContentDescription = "Edit profile",
        editCoursesContentDescription = "Edit subjects",
        loadingMessage = "Loading your profile…",
        retryLabel = "Retry",
        freeLearnerTitle = "Free learner",
        freeLearnerSubtitle = "Core lessons and Ask Panda"
    )

    private fun UserProfile.toSuccessState(
        labels: ProfileLabels,
        notifications: Map<String, Boolean>
    ): ProfileUiState.Success {
        val fullName = listOf(firstName, lastName).filter { it.isNotBlank() }.joinToString(" ")
        val location = listOf(city, state).filter { it.isNotBlank() }.joinToString(", ")
        val normalizedGrade = normalizeGradeId(grade)
        val gradeLabel = normalizedGrade.takeIf { it.isNotBlank() }?.let { "Class $it" } ?: grade
        val isPremium = PlanTier.isPremium(plan)
        val planName = PlanTier.displayName(plan)
        return ProfileUiState.Success(
            labels = labels,
            id = id,
            email = email,
            plan = plan,
            planDisplay = planName,
            isPremiumPlan = isPremium,
            firstName = firstName,
            lastName = lastName,
            grade = normalizedGrade,
            gradeDisplay = gradeLabel,
            schoolBoard = schoolBoard,
            city = city,
            state = state,
            fullNameDisplay = fullName.ifBlank { email },
            locationDisplay = location,
            mobileDisplay = parentMobile,
            avatarUrl = avatarUrl,
            avatarInitial = firstName.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
            headerStreakDisplay = if (currentStreak == 1) "1 day" else "$currentStreak days",
            enrolledCourseGroups = buildEnrolledCourseGroups(courses),
            guardian = GuardianFormState(
                name = parentName,
                phone = parentMobile,
                email = parentEmail
            ),
            notifications = buildNotificationItems(labels, notifications),
            account = AccountSectionState(
                statusTitle = if (isPremium) "$planName learner" else labels.freeLearnerTitle,
                statusSubtitle = if (isPremium) {
                    "Unlimited Ask Panda, priority tutoring and all premium lessons."
                } else {
                    labels.freeLearnerSubtitle
                },
                upgradeLabel = labels.upgradeAccountLabel,
                deleteAccountLabel = labels.deleteAccountLabel,
                planDisplay = planName,
                isPremiumPlan = isPremium,
                showUpgrade = !isPremium
            ),
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            courses = courses
        )
    }

    companion object {
        private fun defaultNotificationPrefs() = mapOf(
            "study_reminders" to true,
            "weekly_report" to true,
            "new_features" to false
        )

        private fun normalizeGradeId(grade: String): String {
            val digits = grade.filter { it.isDigit() }
            return digits.ifBlank { grade }
        }

        private fun buildEnrolledCourseGroups(courses: List<String>): List<EnrolledCourseGroup> {
            if (courses.isEmpty()) return emptyList()
            val mathTopics = courses.filter { topic ->
                listOf("math", "calculus", "algebra", "probability", "geometry")
                    .any { keyword -> topic.contains(keyword, ignoreCase = true) }
            }
            val scienceTopics = courses.filter { topic ->
                listOf("phys", "chem", "bio", "science")
                    .any { keyword -> topic.contains(keyword, ignoreCase = true) }
            }
            val groups = mutableListOf<EnrolledCourseGroup>()
            if (mathTopics.isNotEmpty()) {
                groups += EnrolledCourseGroup("Mathematics", mathTopics)
            }
            if (scienceTopics.isNotEmpty()) {
                groups += EnrolledCourseGroup("Science", scienceTopics)
            }
            val remaining = courses - mathTopics.toSet() - scienceTopics.toSet()
            if (remaining.isNotEmpty()) {
                groups += EnrolledCourseGroup("Courses", remaining)
            }
            if (groups.isEmpty()) {
                groups += EnrolledCourseGroup("Enrolled", courses)
            }
            return groups
        }

        private fun buildNotificationItems(
            labels: ProfileLabels,
            prefs: Map<String, Boolean>
        ): List<NotificationToggleItem> = listOf(
            NotificationToggleItem(
                id = "study_reminders",
                title = "Study Reminders",
                subtitle = "Daily push notifications for pending lessons",
                isEnabled = prefs["study_reminders"] == true
            ),
            NotificationToggleItem(
                id = "weekly_report",
                title = "Weekly Progress Report",
                subtitle = "Summary of achievements sent to guardian email",
                isEnabled = prefs["weekly_report"] == true
            ),
            NotificationToggleItem(
                id = "new_features",
                title = "New Features & Updates",
                subtitle = "Occasional news about Panda AI",
                isEnabled = prefs["new_features"] == true
            )
        )
    }
}
