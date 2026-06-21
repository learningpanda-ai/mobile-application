package com.example.learningpandaai.features.profile.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface ProfileApiService {

    @GET("api/v1/profile")
    suspend fun getProfile(): ProfileResponseDto

    @POST("api/v1/profile/onboarding")
    suspend fun completeOnboarding(@Body request: OnboardingRequestDto): ProfileResponseDto

    @PATCH("api/v1/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequestDto): ProfileResponseDto

    @GET("api/v1/profile/subjects")
    suspend fun getSubjects(): SubjectsResponseDto
}