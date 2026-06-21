package com.example.learningpandaai.features.progress.domain

data class DailyActivityHours(
    val dayLabel: String,
    val hours: Float
)

data class StreakStats(
    val currentStreak: Int,
    val longestStreak: Int,
    val lastActivityDate: String?,
    val totalActiveDays: Int = 0,
    val memberSinceIso: String? = null,
    val courses: List<String>,
    val totalQuestionsAnswered: Int = 0,
    val activeLearningRatioPercent: Int = 0,
    val weeklyActivityHours: List<DailyActivityHours> = emptyList(),
    val modulesCompleted: Int = 0
)