package com.example.learningpandaai.features.progress.presentation

/** User-visible copy for the Progress screen — supplied by ViewModel / API layer. */
data class ProgressLabels(
    val screenTitle: String,
    val dayStreakSuffix: String,
    val longestStreakTitle: String,
    val chatSessionsLabel: String,
    val accuracyLabel: String,
    val activeDaysLabel: String,
    val weeklyCheckInsTitle: String,
    val weeklyCheckInsSubtitle: String,
    val streakCalendarTitle: String,
    val activeDayCheckedIn: String,
    val activeDayMissed: String,
    val emptyTitle: String,
    val emptySubtitle: String,
    val emptyActionHint: String,
    val loadingMessage: String,
    val retryLabel: String
)

data class WeeklyCheckInDay(
    val dayShort: String,
    val dateShort: String,
    val fullLabel: String,
    val isActive: Boolean
)

enum class CalendarStreakMark {
    BLANK,
    INACTIVE,
    STREAK,
    TODAY,
    TODAY_STREAK,
    FUTURE
}

data class CalendarDayCell(
    val dayOfMonth: Int?,
    val streakMark: CalendarStreakMark
)

sealed interface ProgressUiState {
    data class Loading(val message: String) : ProgressUiState

    data class Error(
        val title: String? = null,
        val message: String,
        val retryLabel: String
    ) : ProgressUiState

    data class Success(
        val labels: ProgressLabels,
        val currentStreak: Int,
        val longestStreak: Int,
        val chatSessionCount: Int,
        val accuracyPercent: Int,
        val activeDaysThisWeek: Int,
        val totalActiveDays: Int,
        val weeklyCheckIns: List<WeeklyCheckInDay>,
        val calendarMonthLabel: String,
        val calendarWeekHeaders: List<String>,
        val calendarDays: List<CalendarDayCell>,
        val canGoToPreviousMonth: Boolean,
        val canGoToNextMonth: Boolean
    ) : ProgressUiState
}

sealed interface ProgressUiEvent {
    data object ActivityRecorded : ProgressUiEvent
}
