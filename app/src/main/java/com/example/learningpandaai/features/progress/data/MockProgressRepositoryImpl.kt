package com.example.learningpandaai.features.progress.data

import com.example.learningpandaai.features.progress.domain.DailyActivityHours
import com.example.learningpandaai.features.progress.domain.ProgressRepository
import com.example.learningpandaai.features.progress.domain.StreakStats
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockProgressRepositoryImpl @Inject constructor() : ProgressRepository {

    override suspend fun fetchCurrentStats(): Result<StreakStats> =
        Result.success(mockStats())

    override suspend fun recordDailyActivity(): Result<StreakStats> =
        Result.success(
            mockStats().copy(
                currentStreak = 13,
                lastActivityDate = "2026-05-26"
            )
        )

    private fun mockStats() = StreakStats(
        currentStreak = 12,
        longestStreak = 18,
        lastActivityDate = "2026-05-28",
        totalActiveDays = 22,
        memberSinceIso = "2025-11-15",
        courses = listOf(
            "Mathematics",
            "Physics",
            "Chemistry",
            "Biology",
            "Computer Science",
            "English Literature"
        ),
        totalQuestionsAnswered = 342,
        activeLearningRatioPercent = 84,
        modulesCompleted = 12,
        weeklyActivityHours = listOf(
            DailyActivityHours("Mon", 1.2f),
            DailyActivityHours("Tue", 3.5f),
            DailyActivityHours("Wed", 4.2f),
            DailyActivityHours("Thu", 1.8f),
            DailyActivityHours("Fri", 3.8f),
            DailyActivityHours("Sat", 2.5f),
            DailyActivityHours("Sun", 1.0f)
        )
    )
}
