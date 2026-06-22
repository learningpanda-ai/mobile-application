package com.example.learningpandaai.features.profile.data

import com.example.learningpandaai.core.data.ProfileLocalCache
import com.example.learningpandaai.core.data.ProfileCacheNotifier
import com.example.learningpandaai.core.data.SecurePreferences
import com.example.learningpandaai.core.network.ApiErrorMapper
import com.example.learningpandaai.core.util.InputValidation
import com.example.learningpandaai.core.util.Logger
import com.example.learningpandaai.core.util.ProfileAvatarResolver
import com.example.learningpandaai.core.util.ProfileFieldFormatter
import com.example.learningpandaai.features.auth.domain.AuthRepository
import com.example.learningpandaai.features.profile.data.remote.ProfileApiService
import com.example.learningpandaai.features.profile.data.remote.ProfileResponseDto
import com.example.learningpandaai.features.profile.data.remote.StreakApiService
import com.example.learningpandaai.features.profile.data.remote.UpdateProfileRequestDto
import com.example.learningpandaai.features.profile.domain.CachedProfileSnapshot
import com.example.learningpandaai.features.profile.domain.ProfileRepository
import com.example.learningpandaai.features.profile.domain.ProfileUpdateParams
import com.example.learningpandaai.features.profile.domain.UserProfile
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val profileApiService: ProfileApiService,
    private val streakApiService: StreakApiService,
    private val authRepository: AuthRepository,
    private val profileLocalCache: ProfileLocalCache,
    private val securePreferences: SecurePreferences,
    private val profileCacheNotifier: ProfileCacheNotifier
) : ProfileRepository {

    /** challenge_id for the in-progress account-deletion verification. */
    private var deletionChallengeId: String? = null

    override fun getCachedSnapshot(): CachedProfileSnapshot {
        return CachedProfileSnapshot(
            firstName = securePreferences.getFirstName().orEmpty(),
            gradeLevel = securePreferences.getGradeLevel().orEmpty(),
            plan = securePreferences.getPlan(),
            avatarUrl = securePreferences.getProfileImageUrl(),
            favoriteSubject = securePreferences.getFavoriteSubject().orEmpty(),
            parentEmail = securePreferences.getParentEmail().orEmpty(),
            selectedSubjects = securePreferences.getSelectedSubjects()
        )
    }

    override fun observeProfileCacheUpdates() = profileCacheNotifier.updates

    override suspend fun getCurrentProfile(): Result<UserProfile> {
        return try {
            val profile = profileApiService.getProfile()
            val streak = runCatching { streakApiService.getStreak() }.getOrNull()
            Logger.d("getCurrentProfile: success — grade=${profile.profileDetails?.grade}")
            profileLocalCache.cacheProfileResponse(profile)
            Result.success(profile.toDomain(streak?.currentStreak ?: 0, streak?.longestStreak ?: 0))
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Logger.e("getCurrentProfile: network failure — ${e.message}", e)
            Result.failure(IOException("No internet connection. Please check your network.", e))
        } catch (e: HttpException) {
            Logger.e("getCurrentProfile: HTTP ${e.code()} — ${e.message()}", e)
            Result.failure(ApiErrorMapper.mapHttpException(e))
        } catch (e: Exception) {
            Logger.e("getCurrentProfile: unexpected error — ${e.message}", e)
            Result.failure(ApiErrorMapper.mapThrowable(e, "Failed to load profile. Please retry."))
        }
    }

    override suspend fun getAvailableSubjects(): Result<List<String>> {
        return try {
            val subjects = profileApiService.getSubjects().subjects.orEmpty()
            Logger.d("getAvailableSubjects: success — count=${subjects.size}")
            Result.success(subjects)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Logger.e("getAvailableSubjects: network failure — ${e.message}", e)
            Result.failure(IOException("No internet connection. Please check your network.", e))
        } catch (e: HttpException) {
            Logger.e("getAvailableSubjects: HTTP ${e.code()} — ${e.message()}", e)
            Result.failure(ApiErrorMapper.mapHttpException(e))
        } catch (e: Exception) {
            Logger.e("getAvailableSubjects: unexpected error — ${e.message}", e)
            Result.failure(ApiErrorMapper.mapThrowable(e, "Failed to load subjects."))
        }
    }

    override suspend fun updateProfile(params: ProfileUpdateParams): Result<UserProfile> {
        return try {
            val request = UpdateProfileRequestDto(
                firstName = params.firstName?.trim()?.takeIf { it.isNotEmpty() },
                lastName = params.lastName?.trim()?.takeIf { it.isNotEmpty() },
                cityTown = params.city?.trim()?.takeIf { it.isNotEmpty() },
                state = params.state?.trim()?.takeIf { it.isNotEmpty() },
                parentGuardianName = params.parentName?.trim()?.takeIf { it.isNotEmpty() },
                parentGuardianMobile = params.parentMobile
                    ?.let { ProfileFieldFormatter.mobileToApi(it) }
                    ?.takeIf { it.isNotEmpty() },
                parentGuardianEmail = params.parentEmail?.trim()?.takeIf { it.isNotEmpty() },
                grade = params.grade?.let { ProfileFieldFormatter.gradeToApi(it) },
                board = params.schoolBoard?.trim()?.takeIf { it.isNotEmpty() }
            )
            val profile = profileApiService.updateProfile(request)
            Logger.d("updateProfile: success")
            profileLocalCache.cacheProfileResponse(profile)
            val streak = runCatching { streakApiService.getStreak() }.getOrNull()
            Result.success(profile.toDomain(streak?.currentStreak ?: 0, streak?.longestStreak ?: 0))
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Logger.e("updateProfile: network failure — ${e.message}", e)
            Result.failure(IOException("No internet connection. Please check your network.", e))
        } catch (e: HttpException) {
            Logger.e("updateProfile: HTTP ${e.code()} — ${e.message()}", e)
            Result.failure(ApiErrorMapper.mapHttpException(e))
        } catch (e: Exception) {
            Logger.e("updateProfile: unexpected error — ${e.message}", e)
            Result.failure(Exception("Failed to update profile. Please retry.", e))
        }
    }

    override suspend fun requestAccountDeletionOtp(email: String): Result<Unit> {
        return authRepository.sendOtp(email.trim())
            .map { challengeId ->
                deletionChallengeId = challengeId
                Logger.d("requestAccountDeletionOtp: OTP sent to registered email")
            }
    }

    override suspend fun confirmAccountDeletion(email: String, otpCode: String): Result<Unit> {
        if (!InputValidation.isOtpComplete(otpCode.trim())) {
            return Result.failure(IllegalArgumentException("Enter the 6-digit code from your email."))
        }
        val challengeId = deletionChallengeId
        if (challengeId.isNullOrBlank()) {
            return Result.failure(IllegalStateException("Please request a fresh verification code."))
        }
        return authRepository.verifyOtpForSensitiveAction(challengeId, otpCode.trim())
            .onSuccess {
                authRepository.logout()
                deletionChallengeId = null
                Logger.d("confirmAccountDeletion: verified and local session cleared")
            }
    }

    override suspend fun logout(): Result<Unit> = authRepository.logout()

    private fun ProfileResponseDto.toDomain(currentStreak: Int, longestStreak: Int): UserProfile {
        val details = profileDetails
        val about = aboutYou
        val cached = getCachedSnapshot()
        val avatarUrl = ProfileAvatarResolver.fromProfileResponse(this) ?: cached.avatarUrl
        return UserProfile(
            id = "",
            email = securePreferences.getEmail().orEmpty(),
            plan = cached.plan.orEmpty().ifBlank { "FREE" },
            avatarUrl = avatarUrl,
            firstName = details?.firstName.orEmpty(),
            lastName = details?.lastName.orEmpty(),
            grade = details?.grade.orEmpty(),
            schoolBoard = details?.board.orEmpty(),
            city = details?.cityTown.orEmpty(),
            state = details?.state.orEmpty(),
            parentName = details?.parentGuardianName.orEmpty(),
            parentMobile = details?.parentGuardianMobile.orEmpty(),
            parentEmail = details?.parentGuardianEmail.orEmpty(),
            courses = cached.selectedSubjects.toList(),
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            favoriteSubject = cached.favoriteSubject,
            studyFeeling = about?.feelingOnStudies.orEmpty(),
            careerThoughts = about?.futureCareer.orEmpty()
        )
    }
}
