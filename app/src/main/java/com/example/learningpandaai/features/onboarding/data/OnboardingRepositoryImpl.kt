package com.example.learningpandaai.features.onboarding.data

import com.example.learningpandaai.core.data.ProfileLocalCache
import com.example.learningpandaai.core.data.SecurePreferences
import com.example.learningpandaai.core.network.ApiErrorMapper
import com.example.learningpandaai.core.util.Logger
import com.example.learningpandaai.core.util.ProfileFieldFormatter
import com.example.learningpandaai.features.onboarding.domain.OnboardingProfile
import com.example.learningpandaai.features.onboarding.domain.OnboardingRepository
import com.example.learningpandaai.features.profile.data.remote.AboutYouDto
import com.example.learningpandaai.features.profile.data.remote.OnboardingRequestDto
import com.example.learningpandaai.features.profile.data.remote.ProfileDetailsDto
import com.example.learningpandaai.features.profile.data.remote.ProfileApiService
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor(
    private val profileApiService: ProfileApiService,
    private val profileLocalCache: ProfileLocalCache,
    private val securePreferences: SecurePreferences
) : OnboardingRepository {

    override suspend fun syncOnboardingData(profile: OnboardingProfile): Result<Unit> {
        return try {
            val response = profileApiService.completeOnboarding(profile.toRequest())
            profileLocalCache.cacheProfileResponse(response)
            Logger.d("syncOnboardingData: server sync successful")
            persistLocally(profile)
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Logger.e("syncOnboardingData: network failure — ${e.message}", e)
            Result.failure(IOException("No internet connection. Please check your network.", e))
        } catch (e: HttpException) {
            val url = e.response()?.raw()?.request?.url?.toString().orEmpty()
            Logger.e("syncOnboardingData: HTTP ${e.code()} — $url — ${e.message()}", e)
            Result.failure(ApiErrorMapper.mapHttpException(e))
        } catch (e: Exception) {
            Logger.e("syncOnboardingData: unexpected error — ${e.message}", e)
            Result.failure(Exception("Profile setup failed. Please try again.", e))
        }
    }

    private fun persistLocally(profile: OnboardingProfile) {
        securePreferences.saveStudentProfile(
            firstName = profile.firstName.trim(),
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
        Logger.d("persistLocally: onboarding profile cached for '${profile.firstName.trim()}'")
    }

    private fun OnboardingProfile.toRequest() = OnboardingRequestDto(
        profileDetails = ProfileDetailsDto(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            cityTown = city.trim(),
            state = state,
            parentGuardianName = parentName.trim(),
            parentGuardianMobile = ProfileFieldFormatter.mobileToApi(parentMobile.trim()),
            parentGuardianEmail = parentEmail.trim(),
            grade = ProfileFieldFormatter.gradeToApi(gradeLevel),
            board = board
        ),
        aboutYou = AboutYouDto(
            feelingOnStudies = studiesFeeling,
            futureCareer = careerIdea,
            futureSkills = discoverStrengths
        )
    )
}