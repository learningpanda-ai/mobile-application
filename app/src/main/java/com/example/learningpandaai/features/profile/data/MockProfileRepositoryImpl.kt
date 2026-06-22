package com.example.learningpandaai.features.profile.data

import com.example.learningpandaai.core.data.ProfileCacheNotifier
import com.example.learningpandaai.features.profile.domain.CachedProfileSnapshot
import com.example.learningpandaai.features.profile.domain.ProfileRepository
import com.example.learningpandaai.features.profile.domain.ProfileUpdateParams
import com.example.learningpandaai.features.profile.domain.UserProfile
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockProfileRepositoryImpl @Inject constructor(
    private val profileCacheNotifier: ProfileCacheNotifier
) : ProfileRepository {

    private var mockProfile = UserProfile(
        id = "mock-user-001",
        email = "alok.kumar@learningpanda.dev",
        plan = "PRO",
        firstName = "Alok",
        lastName = "Kumar",
        grade = "8",
        schoolBoard = "CBSE",
        city = "Patna",
        state = "Bihar",
        parentName = "Rajesh Kumar",
        parentMobile = "9876543210",
        parentEmail = "rajesh.kumar@email.com",
        courses = listOf(
            "Algebra II",
            "Probability & Statistics",
            "Physics - Mechanics",
            "Chemistry - Periodic Table",
            "Biology - Cell Division"
        ),
        currentStreak = 12,
        longestStreak = 18,
        favoriteSubject = "Mathematics",
        studyFeeling = "Curious",
        careerThoughts = "Software Engineer"
    )

    override fun getCachedSnapshot(): CachedProfileSnapshot = CachedProfileSnapshot(
        firstName = mockProfile.firstName,
        gradeLevel = mockProfile.grade,
        plan = mockProfile.plan,
        avatarUrl = mockProfile.avatarUrl,
        favoriteSubject = mockProfile.favoriteSubject,
        parentEmail = mockProfile.parentEmail,
        selectedSubjects = mockProfile.courses.toSet()
    )

    override suspend fun getCurrentProfile(): Result<UserProfile> {
        delay(800)
        return Result.success(mockProfile)
    }

    override suspend fun getAvailableSubjects(): Result<List<String>> {
        delay(300)
        return Result.success(listOf("Mathematics", "Science"))
    }

    override suspend fun updateProfile(params: ProfileUpdateParams): Result<UserProfile> {
        delay(800)
        mockProfile = mockProfile.copy(
            firstName = params.firstName ?: mockProfile.firstName,
            lastName = params.lastName ?: mockProfile.lastName,
            city = params.city ?: mockProfile.city,
            state = params.state ?: mockProfile.state,
            grade = params.grade ?: mockProfile.grade,
            schoolBoard = params.schoolBoard ?: mockProfile.schoolBoard,
            parentMobile = params.parentMobile ?: mockProfile.parentMobile,
            parentEmail = params.parentEmail ?: mockProfile.parentEmail,
            courses = params.courses ?: mockProfile.courses
        )
        profileCacheNotifier.notifyProfileCacheUpdated()
        return Result.success(mockProfile)
    }

    override fun observeProfileCacheUpdates() = profileCacheNotifier.updates

    override suspend fun logout(): Result<Unit> {
        delay(300)
        return Result.success(Unit)
    }

    override suspend fun requestAccountDeletionOtp(email: String): Result<Unit> {
        delay(600)
        return if (email.contains("@")) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Invalid email on file."))
        }
    }

    override suspend fun confirmAccountDeletion(email: String, otpCode: String): Result<Unit> {
        delay(800)
        return if (otpCode.length >= 6) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Enter the 6-digit code from your email."))
        }
    }
}
