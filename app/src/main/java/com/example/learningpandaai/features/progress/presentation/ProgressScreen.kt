package com.example.learningpandaai.features.progress.presentation

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.learningpandaai.core.designsystem.components.DashboardErrorFallback
import com.example.learningpandaai.core.designsystem.components.DashboardPullToRefresh
import com.example.learningpandaai.core.designsystem.components.ErrorStateContent
import com.example.learningpandaai.core.designsystem.components.shimmerPlaceholder
import com.example.learningpandaai.core.designsystem.layout.ResponsiveContentWidth
import com.example.learningpandaai.core.designsystem.theme.AppTheme
import com.example.learningpandaai.core.designsystem.theme.LearningPandaAITheme
import com.example.learningpandaai.core.designsystem.theme.Spacing
import com.example.learningpandaai.features.progress.presentation.components.ProgressStatsRow
import com.example.learningpandaai.features.progress.presentation.components.ProgressStreakCalendar
import com.example.learningpandaai.features.progress.presentation.components.ProgressStreakHero
import com.example.learningpandaai.features.progress.presentation.components.ProgressWeeklyCheckInsSection

private val ProgressCardShape = RoundedCornerShape(16.dp)

@Composable
fun ProgressScreen(viewModel: ProgressViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

    ResponsiveContentWidth {
        when (val state = uiState) {
            is ProgressUiState.Loading -> ProgressLoadingSkeleton()
            is ProgressUiState.Error -> {
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
            is ProgressUiState.Success -> DashboardPullToRefresh(
                isRefreshing = isRefreshing,
                onRefresh = viewModel::refresh
            ) {
                ProgressSuccessContent(
                    state = state,
                    onPreviousMonth = viewModel::goToPreviousMonth,
                    onNextMonth = viewModel::goToNextMonth
                )
            }
        }
    }
}

@Composable
private fun ProgressSuccessContent(
    state: ProgressUiState.Success,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val labels = state.labels
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.screen, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.section)
    ) {
        Text(
            text = labels.screenTitle,
            style = MaterialTheme.typography.headlineMedium,
            color = colorScheme.onBackground,
            fontWeight = FontWeight.Bold
        )

        ProgressStreakHero(
            labels = labels,
            currentStreak = state.currentStreak,
            longestStreak = state.longestStreak
        )

        ProgressStatsRow(
            chatSessionCount = state.chatSessionCount,
            accuracyPercent = state.accuracyPercent,
            activeDaysThisWeek = state.activeDaysThisWeek,
            chatSessionsLabel = labels.chatSessionsLabel,
            accuracyLabel = labels.accuracyLabel,
            activeDaysLabel = labels.activeDaysLabel
        )

        ProgressWeeklyCheckInsSection(
            title = labels.weeklyCheckInsTitle,
            subtitle = labels.weeklyCheckInsSubtitle,
            days = state.weeklyCheckIns,
            activeDayCheckedIn = labels.activeDayCheckedIn,
            activeDayMissed = labels.activeDayMissed
        )

        ProgressStreakCalendar(
            title = labels.streakCalendarTitle,
            monthLabel = state.calendarMonthLabel,
            weekHeaders = state.calendarWeekHeaders,
            days = state.calendarDays,
            canGoToPreviousMonth = state.canGoToPreviousMonth,
            canGoToNextMonth = state.canGoToNextMonth,
            onPreviousMonth = onPreviousMonth,
            onNextMonth = onNextMonth
        )

        if (state.currentStreak == 0 && state.totalActiveDays == 0) {
            ProgressEmptyHint(labels = labels)
        }

        Spacer(modifier = Modifier.height(Spacing.lg))
    }
}

@Composable
private fun ProgressEmptyHint(labels: ProgressLabels) {
    val colorScheme = MaterialTheme.colorScheme
    val appColors = AppTheme.colors

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ProgressCardShape)
            .background(appColors.amberContainer.copy(alpha = 0.45f))
            .padding(Spacing.lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Icon(
            imageVector = Icons.Filled.LocalFireDepartment,
            contentDescription = null,
            tint = appColors.amber,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = labels.emptySubtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Text(
            text = labels.emptyActionHint,
            style = MaterialTheme.typography.labelLarge,
            color = appColors.amber,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ProgressLoadingSkeleton() {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.screen, vertical = Spacing.lg),
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.45f)
                .height(28.dp)
                .shimmerPlaceholder(RoundedCornerShape(6.dp))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .shimmerPlaceholder(ProgressCardShape)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            repeat(3) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(88.dp)
                        .shimmerPlaceholder(ProgressCardShape)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .shimmerPlaceholder(ProgressCardShape)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .shimmerPlaceholder(ProgressCardShape)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProgressSuccessPreview() {
    LearningPandaAITheme {
        ProgressSuccessContent(
            state = ProgressUiState.Success(
                labels = ProgressLabels(
                    screenTitle = "Your Streak",
                    dayStreakSuffix = "day streak!",
                    longestStreakTitle = "Best streak",
                    chatSessionsLabel = "Chat sessions",
                    accuracyLabel = "Accuracy",
                    activeDaysLabel = "Active days",
                    weeklyCheckInsTitle = "This week",
                    weeklyCheckInsSubtitle = "Tap a day to see if you checked in",
                    streakCalendarTitle = "Streak calendar",
                    activeDayCheckedIn = "You checked in — streak kept!",
                    activeDayMissed = "No check-in on this day",
                    emptyTitle = "",
                    emptySubtitle = "Ask Panda a doubt to start your streak.",
                    emptyActionHint = "Go to Ask Panda",
                    loadingMessage = "",
                    retryLabel = "Retry"
                ),
                currentStreak = 4,
                longestStreak = 10,
                chatSessionCount = 12,
                accuracyPercent = 0,
                activeDaysThisWeek = 4,
                totalActiveDays = 22,
                weeklyCheckIns = listOf(
                    WeeklyCheckInDay("Mon", "16 Jun", "Monday, 16 Jun", true),
                    WeeklyCheckInDay("Tue", "17 Jun", "Tuesday, 17 Jun", true),
                    WeeklyCheckInDay("Wed", "18 Jun", "Wednesday, 18 Jun", false),
                    WeeklyCheckInDay("Thu", "19 Jun", "Thursday, 19 Jun", true),
                    WeeklyCheckInDay("Fri", "20 Jun", "Friday, 20 Jun", true),
                    WeeklyCheckInDay("Sat", "21 Jun", "Saturday, 21 Jun", false),
                    WeeklyCheckInDay("Sun", "22 Jun", "Sunday, 22 Jun", false)
                ),
                calendarMonthLabel = "June 2026",
                calendarWeekHeaders = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa"),
                calendarDays = buildPreviewCalendarDays(),
                canGoToPreviousMonth = true,
                canGoToNextMonth = false
            ),
            onPreviousMonth = {},
            onNextMonth = {}
        )
    }
}

private fun buildPreviewCalendarDays(): List<CalendarDayCell> {
    val marks = listOf(
        CalendarStreakMark.BLANK, CalendarStreakMark.BLANK,
        CalendarStreakMark.INACTIVE, CalendarStreakMark.STREAK,
        CalendarStreakMark.STREAK, CalendarStreakMark.STREAK,
        CalendarStreakMark.INACTIVE, CalendarStreakMark.INACTIVE,
        CalendarStreakMark.INACTIVE, CalendarStreakMark.STREAK,
        CalendarStreakMark.STREAK, CalendarStreakMark.TODAY,
        CalendarStreakMark.FUTURE, CalendarStreakMark.FUTURE
    )
    val days = mutableListOf<CalendarDayCell>()
    repeat(6) { days += CalendarDayCell(null, CalendarStreakMark.BLANK) }
    marks.forEachIndexed { index, mark ->
        days += CalendarDayCell(index + 1, mark)
    }
    return days
}
