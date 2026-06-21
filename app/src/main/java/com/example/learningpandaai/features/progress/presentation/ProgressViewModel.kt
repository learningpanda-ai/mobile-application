package com.example.learningpandaai.features.progress.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningpandaai.core.network.ApiErrorMapper
import com.example.learningpandaai.core.network.SessionExpiredException
import com.example.learningpandaai.core.util.Logger
import com.example.learningpandaai.core.util.StreakCalendarMarks
import com.example.learningpandaai.features.askpanda.domain.AgentRepository
import com.example.learningpandaai.features.progress.domain.ProgressRepository
import com.example.learningpandaai.features.progress.domain.StreakStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val agentRepository: AgentRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProgressUiState>(
        ProgressUiState.Loading(buildLabels().loadingMessage)
    )
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var cachedStats: StreakStats? = null
    private var cachedChatSessions: Int = 0
    private val calendarMonth = MutableStateFlow(YearMonth.now())

    init {
        loadStats(showFullLoading = true)
    }

    fun retry() = loadStats(showFullLoading = true)

    fun refresh() {
        if (_isRefreshing.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchStats(showFullLoading = false)
            _isRefreshing.value = false
        }
    }

    fun goToPreviousMonth() {
        calendarMonth.value = calendarMonth.value.minusMonths(1)
        publishSuccessFromCache()
    }

    fun goToNextMonth() {
        val next = calendarMonth.value.plusMonths(1)
        if (!next.isAfter(YearMonth.now())) {
            calendarMonth.value = next
            publishSuccessFromCache()
        }
    }

    private fun loadStats(showFullLoading: Boolean) {
        val labels = buildLabels()
        if (showFullLoading) {
            _uiState.value = ProgressUiState.Loading(labels.loadingMessage)
        }
        viewModelScope.launch {
            fetchStats(showFullLoading)
        }
    }

    private suspend fun fetchStats(showFullLoading: Boolean) {
        val labels = buildLabels()
        val sessionCount = agentRepository.getSessions()
            .getOrNull()
            ?.size
            ?: cachedChatSessions
        cachedChatSessions = sessionCount

        progressRepository.fetchCurrentStats()
            .onSuccess { stats ->
                Logger.d("loadStats: success — streak=${stats.currentStreak}")
                cachedStats = stats
                calendarMonth.value = YearMonth.now()
                _uiState.value = stats.toSuccessState(
                    labels = labels,
                    chatSessionCount = sessionCount,
                    month = calendarMonth.value
                )
            }
            .onFailure { throwable ->
                Logger.e("loadStats: failed — ${throwable.message}", throwable)
                val sessionExpired = throwable is SessionExpiredException
                _uiState.value = ProgressUiState.Error(
                    title = if (sessionExpired) ApiErrorMapper.SESSION_EXPIRED_TITLE else null,
                    message = throwable.message ?: "Failed to load progress. Please retry.",
                    retryLabel = if (sessionExpired) "Sign in" else labels.retryLabel
                )
            }
    }

    private fun publishSuccessFromCache() {
        val stats = cachedStats ?: return
        val current = _uiState.value as? ProgressUiState.Success ?: return
        _uiState.value = stats.toSuccessState(
            labels = current.labels,
            chatSessionCount = current.chatSessionCount,
            month = calendarMonth.value
        )
    }

    private fun buildLabels() = ProgressLabels(
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
        emptyTitle = "Start your streak",
        emptySubtitle = "Ask Panda a doubt or study today to begin.",
        emptyActionHint = "Go to Ask Panda to start",
        loadingMessage = "Loading your streak…",
        retryLabel = "Retry"
    )

    private fun StreakStats.toSuccessState(
        labels: ProgressLabels,
        chatSessionCount: Int,
        month: YearMonth
    ): ProgressUiState.Success {
        val weeklyDays = StreakCalendarMarks.buildWeeklyDays(lastActivityDate, currentStreak)
        val activeDaysThisWeek = weeklyDays.count { it.isActive }
        val monthCells = StreakCalendarMarks.buildMonthGrid(month, lastActivityDate, currentStreak)
        val earliestNavMonth = YearMonth.now().minusMonths(11)

        return ProgressUiState.Success(
            labels = labels,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            chatSessionCount = chatSessionCount,
            accuracyPercent = 0,
            activeDaysThisWeek = activeDaysThisWeek,
            totalActiveDays = totalActiveDays,
            weeklyCheckIns = weeklyDays.map { day ->
                WeeklyCheckInDay(
                    dayShort = day.dayShort,
                    dateShort = day.dateShort,
                    fullLabel = day.fullLabel,
                    isActive = day.isActive
                )
            },
            calendarMonthLabel = StreakCalendarMarks.monthTitle(month),
            calendarWeekHeaders = StreakCalendarMarks.weekDayHeaders(),
            calendarDays = monthCells.map { cell ->
                CalendarDayCell(
                    dayOfMonth = cell.dayOfMonth,
                    streakMark = cell.mark.toUiMark()
                )
            },
            canGoToPreviousMonth = month.isAfter(earliestNavMonth),
            canGoToNextMonth = month.isBefore(YearMonth.now())
        )
    }

    private fun StreakCalendarMarks.DayMark.toUiMark(): CalendarStreakMark = when (this) {
        StreakCalendarMarks.DayMark.BLANK -> CalendarStreakMark.BLANK
        StreakCalendarMarks.DayMark.INACTIVE -> CalendarStreakMark.INACTIVE
        StreakCalendarMarks.DayMark.STREAK -> CalendarStreakMark.STREAK
        StreakCalendarMarks.DayMark.TODAY -> CalendarStreakMark.TODAY
        StreakCalendarMarks.DayMark.TODAY_STREAK -> CalendarStreakMark.TODAY_STREAK
        StreakCalendarMarks.DayMark.FUTURE -> CalendarStreakMark.FUTURE
    }
}
