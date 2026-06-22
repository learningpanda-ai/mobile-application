package com.example.learningpandaai.features.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learningpandaai.core.designsystem.components.DashboardErrorFallback
import com.example.learningpandaai.core.designsystem.components.DashboardPullToRefresh
import com.example.learningpandaai.core.designsystem.components.ErrorStateContent
import com.example.learningpandaai.core.designsystem.components.PlanBadge
import com.example.learningpandaai.core.designsystem.components.UserAvatar
import com.example.learningpandaai.core.designsystem.theme.StatusError
import com.example.learningpandaai.features.profile.presentation.components.AccountManagementSection
import com.example.learningpandaai.features.profile.presentation.components.DeleteAccountDialog
import com.example.learningpandaai.features.profile.presentation.components.EnrolledCoursesSection
import com.example.learningpandaai.features.profile.presentation.components.NotificationToggles
import com.example.learningpandaai.features.profile.presentation.components.SignOutConfirmDialog
import com.example.learningpandaai.features.profile.presentation.components.ProfileReadOnlyField

private val ProfileCardShape = RoundedCornerShape(16.dp)
private val ProfilePadding = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToEdit: () -> Unit,
    onLogout: () -> Unit,
    onProfileDataSynced: () -> Unit = {},
    refreshTrigger: Boolean = false
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val overlayState by viewModel.overlayState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    LaunchedEffect(refreshTrigger) {
        if (refreshTrigger) viewModel.refreshFromServer()
    }

    LifecycleResumeEffect(Unit) {
        viewModel.refreshFromServer()
        onPauseOrDispose { }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ProfileUiEvent.LoggedOut,
                ProfileUiEvent.AccountDeleted -> onLogout()
                ProfileUiEvent.ProfileDataSynced -> onProfileDataSynced()
            }
        }
    }

    when (val state = uiState) {
        is ProfileUiState.Loading -> ProfileLoadingState(message = state.message)
        is ProfileUiState.Error -> {
            val modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
            if (state.title != null) {
                DashboardErrorFallback(
                    title = state.title,
                    message = state.message,
                    retryLabel = state.retryLabel,
                    onRetry = viewModel::retry,
                    modifier = modifier
                )
            } else {
                ErrorStateContent(
                    message = state.message,
                    retryLabel = state.retryLabel,
                    onRetry = viewModel::retry,
                    modifier = modifier
                )
            }
        }
        is ProfileUiState.Success -> {
            DashboardPullToRefresh(
                isRefreshing = isRefreshing,
                onRefresh = viewModel::refresh
            ) {
                ProfileSuccessContent(
                    state = state,
                    onEditProfile = onNavigateToEdit,
                    onNotificationToggle = viewModel::onNotificationToggled,
                    onSignOutClick = viewModel::showSignOutDialog,
                    onDeleteAccountClick = { viewModel.showDeleteAccountDialog(state.email) }
                )
            }

            if (overlayState.signOutDialogVisible) {
                SignOutConfirmDialog(
                    labels = state.labels,
                    onConfirm = viewModel::confirmSignOut,
                    onDismiss = viewModel::dismissSignOutDialog
                )
            }

            if (overlayState.deleteDialog.isVisible) {
                DeleteAccountDialog(
                    labels = state.labels,
                    state = overlayState.deleteDialog,
                    onDismiss = viewModel::dismissDeleteAccountDialog,
                    onContinueToOtp = viewModel::proceedDeleteToOtpStep,
                    onOtpChanged = viewModel::onDeleteOtpChanged,
                    onConfirmDelete = viewModel::confirmAccountDeletion
                )
            }
        }
    }
}

@Composable
private fun ProfileLoadingState(message: String) {
    val colorScheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = colorScheme.secondary)
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun ProfileSuccessContent(
    state: ProfileUiState.Success,
    onEditProfile: () -> Unit,
    onNotificationToggle: (String, Boolean) -> Unit,
    onSignOutClick: () -> Unit,
    onDeleteAccountClick: () -> Unit
) {
    val labels = state.labels
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .then(ProfilePadding),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = labels.screenTitle,
            style = MaterialTheme.typography.headlineMedium,
            color = colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        ProfileHeroCard(
            fullName = state.fullNameDisplay,
            email = state.email,
            locationDisplay = state.locationDisplay,
            avatarUrl = state.avatarUrl,
            avatarInitial = state.avatarInitial,
            plan = state.plan,
            planDisplay = state.planDisplay,
            isPremiumPlan = state.isPremiumPlan,
            streakDisplay = state.headerStreakDisplay,
            editLabel = labels.editProfileActionLabel,
            onEditProfile = onEditProfile
        )

        Text(
            text = labels.learningSummaryTitle,
            style = MaterialTheme.typography.titleSmall,
            color = colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MiniStatCard(
                icon = Icons.Filled.LocalFireDepartment,
                value = state.currentStreak.toString(),
                label = "Current streak",
                modifier = Modifier.weight(1f)
            )
            MiniStatCard(
                icon = Icons.Filled.EmojiEvents,
                value = state.longestStreak.toString(),
                label = "Best streak",
                modifier = Modifier.weight(1f)
            )
            MiniStatCard(
                icon = Icons.Filled.School,
                value = state.gradeDisplay.replace("Class ", ""),
                label = "Class",
                modifier = Modifier.weight(1f)
            )
        }

        ProfileSectionCard {
            ProfileReadOnlyField(label = labels.educationalBoardLabel, value = state.schoolBoard)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileReadOnlyField(label = labels.guardianPhoneLabel, value = state.guardian.phone)
            Spacer(modifier = Modifier.height(10.dp))
            ProfileReadOnlyField(label = labels.guardianEmailLabel, value = state.guardian.email)
        }

        EnrolledCoursesSection(
            sectionTitle = labels.enrolledCoursesTitle,
            courseGroups = state.enrolledCourseGroups,
            editContentDescription = labels.editCoursesContentDescription
        )

        NotificationToggles(
            title = labels.notificationsTitle,
            subtitle = labels.notificationsSubtitle,
            items = state.notifications,
            onToggle = onNotificationToggle
        )

        ProfileActionRow(
            icon = Icons.Filled.Info,
            label = labels.helpSupportLabel,
            onClick = { /* help — future */ }
        )

        AccountManagementSection(
            sectionTitle = labels.accountSectionTitle,
            account = state.account,
            onUpgradeClick = { /* billing — future */ },
            onDeleteAccountClick = onDeleteAccountClick,
            showDeleteAccount = false
        )

        ProfileSectionCard(
            containerColor = colorScheme.errorContainer.copy(alpha = 0.35f)
        ) {
            Text(
                text = "Account actions",
                style = MaterialTheme.typography.titleSmall,
                color = colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            ProfileActionRow(
                icon = Icons.AutoMirrored.Filled.Logout,
                label = labels.signOutLabel,
                labelColor = StatusError,
                iconTint = StatusError,
                onClick = onSignOutClick,
                showChevron = false
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun ProfileHeroCard(
    fullName: String,
    email: String,
    locationDisplay: String,
    avatarUrl: String?,
    avatarInitial: String,
    plan: String,
    planDisplay: String,
    isPremiumPlan: Boolean,
    streakDisplay: String,
    editLabel: String,
    onEditProfile: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    ProfileSectionCard {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            UserAvatar(
                imageUrl = avatarUrl,
                displayName = fullName.ifBlank { avatarInitial },
                size = 72.dp,
                initialTextStyle = MaterialTheme.typography.headlineMedium,
                plan = plan,
                contentDescription = "Profile photo"
            )
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = fullName,
                        style = MaterialTheme.typography.titleLarge,
                        color = colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    PlanBadge(planDisplay = planDisplay, isPremium = isPremiumPlan)
                }
                if (locationDisplay.isNotBlank()) {
                    Text(
                        text = locationDisplay,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = email,
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(colorScheme.secondaryContainer)
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocalFireDepartment,
                        contentDescription = null,
                        tint = colorScheme.secondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = streakDisplay,
                        style = MaterialTheme.typography.labelMedium,
                        color = colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = colorScheme.outline.copy(alpha = 0.35f))
        Spacer(modifier = Modifier.height(10.dp))
        ProfileActionRow(
            icon = Icons.Filled.Edit,
            label = editLabel,
            onClick = onEditProfile,
            showChevron = true
        )
    }
}

@Composable
private fun MiniStatCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme

    ProfileSectionCard(modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = colorScheme.secondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ProfileActionRow(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    labelColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurface,
    iconTint: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.secondary,
    showChevron: Boolean = true
) {
    val colorScheme = MaterialTheme.colorScheme

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = labelColor,
                fontWeight = FontWeight.Medium
            )
        }
        if (showChevron) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfileSectionCard(
    modifier: Modifier = Modifier,
    containerColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.surfaceVariant,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = ProfileCardShape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            content()
        }
    }
}
