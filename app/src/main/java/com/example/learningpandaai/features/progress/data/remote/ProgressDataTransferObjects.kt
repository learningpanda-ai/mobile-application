package com.example.learningpandaai.features.progress.data.remote

import com.google.gson.annotations.SerializedName

data class ProgressStatsDto(
    @SerializedName("total_questions_answered") val totalQuestionsAnswered: Int,
    @SerializedName("active_learning_ratio_percent") val activeLearningRatioPercent: Int,
    @SerializedName("weekly_activity_hours") val weeklyActivityHours: List<WeeklyActivityDto> = emptyList()
)

data class WeeklyActivityDto(
    @SerializedName("day") val day: String,
    @SerializedName("hours") val hours: Float
)
