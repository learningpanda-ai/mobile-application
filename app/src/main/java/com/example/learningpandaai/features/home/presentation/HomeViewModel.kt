package com.example.learningpandaai.features.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningpandaai.core.network.ApiErrorMapper
import com.example.learningpandaai.core.network.SessionExpiredException
import com.example.learningpandaai.features.profile.domain.ProfileRepository
import com.example.learningpandaai.features.profile.domain.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(
        HomeUiState.Loading("Loading your home…")
    )
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadHome(showFullLoading = true)
    }

    fun retry() = loadHome(showFullLoading = true)

    fun refresh() {
        if (_isRefreshing.value) return
        viewModelScope.launch {
            _isRefreshing.value = true
            refreshProfile(showFullLoading = false)
            _isRefreshing.value = false
        }
    }

    private fun loadHome(showFullLoading: Boolean) {
        viewModelScope.launch {
            val cached = buildFromCache()
            if (showFullLoading) {
                _uiState.value = cached ?: HomeUiState.Loading("Loading your home…")
            } else if (cached != null && _uiState.value !is HomeUiState.Success) {
                _uiState.value = cached
            }
            refreshProfile(showFullLoading = showFullLoading && cached == null)
        }
    }

    private suspend fun refreshProfile(showFullLoading: Boolean) {
        profileRepository.getCurrentProfile()
            .onSuccess { profile ->
                _uiState.value = profile.toUiState()
            }
            .onFailure { throwable ->
                if (_uiState.value is HomeUiState.Success) return
                val sessionExpired = throwable is SessionExpiredException
                _uiState.value = HomeUiState.Error(
                    title = if (sessionExpired) {
                        ApiErrorMapper.SESSION_EXPIRED_TITLE
                    } else {
                        HOME_ERROR_TITLE
                    },
                    message = throwable.message
                        ?: "Something went wrong while loading your profile. Please try again.",
                    retryLabel = if (sessionExpired) "Sign in" else "Retry"
                )
            }
    }

    private fun buildFromCache(): HomeUiState.Success? {
        val cached = profileRepository.getCachedSnapshot()
        val firstName = cached.firstName
            .trim()
            .substringBefore(" ")
            .ifBlank { return null }
        return placeholderHomeState(
            displayName = firstName,
            streakDays = 0,
            favoriteSubject = cached.favoriteSubject,
            courses = cached.selectedSubjects.toList(),
            avatarUrl = cached.avatarUrl,
            plan = cached.plan
        )
    }

    private fun UserProfile.toUiState(): HomeUiState.Success {
        val displayName = listOf(firstName, lastName)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString(" ")
            .ifBlank { "Student" }
        return placeholderHomeState(
            displayName = displayName,
            streakDays = currentStreak,
            favoriteSubject = favoriteSubject,
            courses = courses,
            avatarUrl = avatarUrl ?: profileRepository.getCachedSnapshot().avatarUrl,
            plan = plan.ifBlank { profileRepository.getCachedSnapshot().plan.orEmpty() }
        )
    }

    private fun placeholderHomeState(
        displayName: String,
        streakDays: Int,
        favoriteSubject: String,
        courses: List<String>,
        avatarUrl: String?,
        plan: String?
    ): HomeUiState.Success {
        val focusSubject = favoriteSubject.ifBlank {
            courses.firstOrNull() ?: "your subjects"
        }
        val labels = HomeScreenLabels(
            greetingPrefix = timeBasedGreeting(),
            activeSubjectsSectionTitle = "Continue Learning",
            seeAllAction = "See all",
            todaysFocusSectionTitle = "Today's Focus",
            resumeModuleAction = "Resume Module",
            startLessonAction = "Start Lesson",
            weeklyGoalProgressLabel = "Progress",
            streakLabel = if (streakDays == 1) "1 Day Streak" else "$streakDays Day Streak",
            xpLabel = "",
            emptyTitle = "No active subjects yet",
            emptySubtitle = "Enroll in a course to see your learning path here.",
            loadingMessage = "Loading your home…",
            retryAction = "Retry"
        )
        return HomeUiState.Success(
            labels = labels,
            userStats = UserStats(
                name = displayName,
                streakDays = streakDays,
                xp = 0,
                avatarUrl = avatarUrl,
                plan = plan,
                isPro = com.example.learningpandaai.core.util.PlanTier.isPremium(plan)
            ),
            activeSubjects = emptyList(),
            todaysFocus = FocusTask(
                title = "Ready to learn?",
                description = "Open a subject and keep building momentum in $focusSubject.",
                badgeText = if (streakDays > 0) "$streakDays day streak" else "Let's go"
            ),
            weeklyGoal = WeeklyGoal(
                title = "Daily habit",
                description = "Study a little each day to grow your streak.",
                current = streakDays.coerceAtMost(7),
                target = 7
            )
        )
    }

    companion object {
        private const val HOME_ERROR_TITLE = "Oops, we couldn't load your profile"

        private fun timeBasedGreeting(): String {
            return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
                in 5..11 -> "Good morning"
                in 12..16 -> "Good afternoon"
                in 17..20 -> "Good evening"
                else -> "Welcome back"
            }
        }
    }
}
