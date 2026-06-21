package com.example.learningpandaai.features.profile.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.POST

interface StreakApiService {

    @GET("api/v1/streak")
    suspend fun getStreak(): StreakDto

    @POST("api/v1/streak/check-in")
    suspend fun checkIn(): StreakDto
}

data class StreakDto(
    @SerializedName("current_streak") val currentStreak: Int = 0,
    @SerializedName("longest_streak") val longestStreak: Int = 0,
    @SerializedName("total_active_days") val totalActiveDays: Int = 0,
    @SerializedName("last_activity_date") val lastActivityDate: String? = null
)