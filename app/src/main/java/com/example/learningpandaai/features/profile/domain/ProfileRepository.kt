package com.example.learningpandaai.features.profile.domain

import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getCachedSnapshot(): CachedProfileSnapshot
    fun observeProfileCacheUpdates(): Flow<Unit>
    suspend fun getCurrentProfile(): Result<UserProfile>
    suspend fun getAvailableSubjects(): Result<List<String>>
    suspend fun updateProfile(params: ProfileUpdateParams): Result<UserProfile>
    suspend fun logout(): Result<Unit>
    suspend fun requestAccountDeletionOtp(email: String): Result<Unit>
    suspend fun confirmAccountDeletion(email: String, otpCode: String): Result<Unit>
}