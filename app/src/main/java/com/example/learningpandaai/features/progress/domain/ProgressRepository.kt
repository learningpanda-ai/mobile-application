package com.example.learningpandaai.features.progress.domain

interface ProgressRepository {
    suspend fun fetchCurrentStats(): Result<StreakStats>
    suspend fun recordDailyActivity(): Result<StreakStats>
}