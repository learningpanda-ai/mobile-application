package com.example.learningpandaai.features.profile.presentation

/** User-visible copy for Profile — supplied by ViewModel / API layer. */
data class ProfileLabels(
    val screenTitle: String,
    val academicSectionTitle: String,
    val academicClassLabel: String,
    val educationalBoardLabel: String,
    val enrolledCoursesTitle: String,
    val guardianSectionTitle: String,
    val guardianNameLabel: String,
    val guardianPhoneLabel: String,
    val guardianEmailLabel: String,
    val notificationsTitle: String,
    val notificationsSubtitle: String,
    val helpSupportLabel: String,
    val accountSectionTitle: String,
    val upgradeAccountLabel: String,
    val deleteAccountLabel: String,
    val signOutLabel: String,
    val editProfileActionLabel: String,
    val learningSummaryTitle: String,
    val signOutDialogTitle: String,
    val signOutDialogMessage: String,
    val signOutConfirmLabel: String,
    val signOutCancelLabel: String,
    val deleteDialogTitle: String,
    val deleteDialogWarning: String,
    val deleteDialogContinueLabel: String,
    val deleteOtpTitle: String,
    val deleteOtpMessage: String,
    val deleteOtpHint: String,
    val deleteConfirmLabel: String,
    val deleteCancelLabel: String,
    val editProfileContentDescription: String,
    val editCoursesContentDescription: String,
    val loadingMessage: String,
    val retryLabel: String,
    val freeLearnerTitle: String,
    val freeLearnerSubtitle: String
)

data class SelectableChip(
    val id: String,
    val label: String,
    val isSelected: Boolean
)

data class EnrolledCourseGroup(
    val categoryName: String,
    val topics: List<String>
)

data class NotificationToggleItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val isEnabled: Boolean
)

data class GuardianFormState(
    val name: String,
    val phone: String,
    val email: String = ""
)

data class AccountSectionState(
    val statusTitle: String,
    val statusSubtitle: String,
    val upgradeLabel: String,
    val deleteAccountLabel: String,
    /** Human-friendly plan label shown in the account card (e.g. "Free", "Pro"). */
    val planDisplay: String = "Free",
    val isPremiumPlan: Boolean = false,
    /** Whether to show the upgrade CTA (hidden for users already on a paid plan). */
    val showUpgrade: Boolean = true
)

sealed interface ProfileUiState {
    data class Loading(val message: String) : ProfileUiState

    data class Error(
        val title: String? = null,
        val message: String,
        val retryLabel: String
    ) : ProfileUiState

    data class Success(
        val labels: ProfileLabels,
        val id: String,
        val email: String,
        /** Subscription tier code (e.g. "FREE", "PRO"). */
        val plan: String,
        /** Human-friendly plan name for chips/badges (e.g. "Free", "Pro"). */
        val planDisplay: String,
        /** True when the user is on a paid tier (drives the avatar Pro ring + badge). */
        val isPremiumPlan: Boolean,
        val firstName: String,
        val lastName: String,
        val grade: String,
        val gradeDisplay: String,
        val schoolBoard: String,
        val mobileDisplay: String,
        val city: String,
        val state: String,
        val fullNameDisplay: String,
        val locationDisplay: String,
        val avatarUrl: String? = null,
        val avatarInitial: String,
        val headerStreakDisplay: String,
        val enrolledCourseGroups: List<EnrolledCourseGroup>,
        val guardian: GuardianFormState,
        val notifications: List<NotificationToggleItem>,
        val account: AccountSectionState,
        val currentStreak: Int,
        val longestStreak: Int,
        val courses: List<String>
    ) : ProfileUiState
}

sealed interface ProfileUiEvent {
    data object LoggedOut : ProfileUiEvent
    data object AccountDeleted : ProfileUiEvent
    /** Emitted after a successful GET /auth/me — local cache and shell UI can refresh. */
    data object ProfileDataSynced : ProfileUiEvent
}

data class DeleteAccountDialogState(
    val isVisible: Boolean = false,
    val step: DeleteAccountStep = DeleteAccountStep.Warning,
    val email: String = "",
    val otpInput: String = "",
    val isSendingOtp: Boolean = false,
    val isDeleting: Boolean = false,
    val errorMessage: String? = null
)

enum class DeleteAccountStep {
    Warning,
    OtpEntry
}
