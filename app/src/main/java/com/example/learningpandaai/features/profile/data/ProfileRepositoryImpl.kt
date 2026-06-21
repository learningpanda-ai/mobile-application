package com.example.learningpandaai.features.profile.data

import com.example.learningpandaai.core.data.ProfileCacheNotifier
import com.example.learningpandaai.core.data.SecurePreferences
import com.example.learningpandaai.features.profile.domain.CachedProfileSnapshot
import com.example.learningpandaai.features.profile.domain.ProfileRepository
import com.example.learningpandaai.features.profile.domain.ProfileUpdateParams
import com.example.learningpandaai.features.profile.domain.UserProfile
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val profileCacheNotifier: ProfileCacheNotifier
) : ProfileRepository {

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

    override suspend fun getCurrentProfile(): Result<UserProfile> =
        notImplemented()

    override suspend fun getAvailableSubjects(): Result<List<String>> =
        notImplemented()

    override suspend fun updateProfile(params: ProfileUpdateParams): Result<UserProfile> =
        notImplemented()

    override suspend fun logout(): Result<Unit> =
        notImplemented()

    override suspend fun requestAccountDeletionOtp(email: String): Result<Unit> =
        notImplemented()

    override suspend fun confirmAccountDeletion(email: String, otpCode: String): Result<Unit> =
        notImplemented()

    private fun <T> notImplemented(): Result<T> =
        Result.failure(UnsupportedOperationException("Profile API — coming in profile feature step"))
}