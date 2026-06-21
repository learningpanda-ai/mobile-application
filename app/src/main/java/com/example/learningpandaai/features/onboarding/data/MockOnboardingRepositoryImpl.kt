package com.example.learningpandaai.features.onboarding.data

import com.example.learningpandaai.core.data.SecurePreferences
import com.example.learningpandaai.core.util.Logger
import com.example.learningpandaai.features.onboarding.domain.OnboardingProfile
import com.example.learningpandaai.features.onboarding.domain.OnboardingRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockOnboardingRepositoryImpl @Inject constructor(
    private val securePreferences: SecurePreferences
) : OnboardingRepository {

    override suspend fun syncOnboardingData(profile: OnboardingProfile): Result<Unit> {
        delay(1000)

        val fullName = "${profile.firstName.trim()} ${profile.lastName.trim()}"
        securePreferences.saveStudentProfile(
            firstName = fullName,
            gradeLevel = profile.gradeLevel,
            board = profile.board
        )
        securePreferences.saveParentProfile(
            parentName = profile.parentName.trim(),
            parentMobile = profile.parentMobile.trim(),
            parentEmail = profile.parentEmail.trim()
        )
        securePreferences.saveSelectedSubjects(profile.selectedSubjects)
        securePreferences.saveLearningMindset(
            favoriteSub = profile.favoriteSubject,
            feeling = profile.studiesFeeling,
            career = profile.careerIdea,
            strengths = profile.discoverStrengths
        )

        Logger.d("MockOnboardingRepositoryImpl: syncOnboardingData success for '$fullName'")
        return Result.success(Unit)
    }
}