package com.example.learningpandaai.features.home.presentation

enum class Difficulty { BEGINNER, INTERMEDIATE, ADVANCED }

enum class ModuleStatus { LOCKED, IN_PROGRESS, COMPLETED }

enum class SubjectTagStyle {
    LIGHT_ACCENT,
    CORE_TRACK
}

data class SubjectTag(
    val label: String,
    val style: SubjectTagStyle
)

data class SyllabusScreenLabels(
    val startTopicAction: String,
    val backContentDescription: String
)

data class UserStats(
    val name: String,
    val streakDays: Int,
    val xp: Int,
    val avatarUrl: String? = null,
    /** Subscription tier code (e.g. "FREE", "PRO") driving the avatar's Pro ring. */
    val plan: String? = null,
    val isPro: Boolean = false
)

data class SyllabusModule(
    val id: String,
    val moduleNumber: Int,
    val title: String,
    val description: String,
    val lessonCount: Int,
    val difficulty: Difficulty,
    val status: ModuleStatus,
    val statusHeader: String,
    val unlockHint: String? = null
)

data class ActiveSubject(
    val id: String,
    val title: String,
    val subtitle: String,
    val currentModuleLabel: String,
    val topicsCompleteLabel: String,
    val progressPercentLabel: String,
    val topicsComplete: Int,
    val totalTopics: Int,
    val progressPercent: Float,
    val modules: List<SyllabusModule>,
    val tags: List<SubjectTag> = emptyList(),
    val syllabusLabels: SyllabusScreenLabels? = null
)

/** All user-visible copy for the Courses dashboard — supplied by the ViewModel / API layer. */
data class HomeScreenLabels(
    val greetingPrefix: String,
    val activeSubjectsSectionTitle: String,
    val seeAllAction: String,
    val todaysFocusSectionTitle: String,
    val resumeModuleAction: String,
    val startLessonAction: String,
    val weeklyGoalProgressLabel: String,
    val streakLabel: String,
    val xpLabel: String,
    val emptyTitle: String,
    val emptySubtitle: String,
    val loadingMessage: String,
    val retryAction: String
)

data class FocusTask(
    val title: String,
    val description: String,
    val badgeText: String
)

data class WeeklyGoal(
    val title: String,
    val description: String,
    val current: Int,
    val target: Int
)

sealed interface HomeUiState {
    data class Loading(val message: String) : HomeUiState
    data class Error(
        val title: String,
        val message: String,
        val retryLabel: String
    ) : HomeUiState
    data class Empty(
        val title: String,
        val subtitle: String
    ) : HomeUiState

    data class Success(
        val labels: HomeScreenLabels,
        val userStats: UserStats,
        val activeSubjects: List<ActiveSubject>,
        val todaysFocus: FocusTask,
        val weeklyGoal: WeeklyGoal
    ) : HomeUiState
}
