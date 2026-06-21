package com.example.learningpandaai.features.onboarding.domain

interface OnboardingRepository {
    suspend fun syncOnboardingData(profile: OnboardingProfile): Result<Unit>
}