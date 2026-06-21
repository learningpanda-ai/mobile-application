package com.example.learningpandaai.features.auth.data

import com.example.learningpandaai.core.data.SecurePreferences
import com.example.learningpandaai.core.util.InputValidation
import com.example.learningpandaai.features.auth.domain.AuthRepository
import com.example.learningpandaai.features.auth.domain.User
import kotlinx.coroutines.delay
import javax.inject.Inject

class MockAuthRepositoryImpl @Inject constructor(
    private val securePreferences: SecurePreferences
) : AuthRepository {

    /** Remembers the email tied to the latest mock challenge so verify can branch on it. */
    private var lastEmail: String = ""

    override suspend fun sendOtp(email: String): Result<String> {
        delay(800)
        lastEmail = email.trim().lowercase()
        return Result.success("mock-challenge-${System.currentTimeMillis()}")
    }

    override suspend fun verifyOtp(challengeId: String, otp: String): Result<User> {
        delay(800)
        val isReturningUser = lastEmail in RETURNING_USER_EMAILS
        persistMockSession(isReturningUser)
        return Result.success(buildMockUser(email = lastEmail, isReturningUser = isReturningUser))
    }

    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        delay(800)
        // Google sign-in in the mock always simulates a returning user (account already exists).
        persistMockSession(isReturningUser = true)
        return Result.success(
            buildMockUser(
                email = "student@learningpanda.com",
                isReturningUser = true
            )
        )
    }

    override suspend fun logout(): Result<Unit> {
        delay(300)
        securePreferences.clearAllData()
        return Result.success(Unit)
    }

    override suspend fun verifyOtpForSensitiveAction(challengeId: String, otp: String): Result<Unit> {
        delay(400)
        return if (InputValidation.isOtpComplete(otp.trim())) {
            Result.success(Unit)
        } else {
            Result.failure(IllegalArgumentException("Enter the 6-digit code from your email."))
        }
    }

    private fun persistMockSession(isReturningUser: Boolean) {
        securePreferences.saveOAuthToken("mock_jwt_access_token")
        securePreferences.saveRefreshToken("mock_jwt_refresh_token")
        securePreferences.savePlan(if (isReturningUser) "PRO" else "FREE")
        if (isReturningUser) {
            securePreferences.saveStudentProfile(
                firstName = "Alok Kumar",
                gradeLevel = "Class 10",
                board = "CBSE"
            )
        }
    }

    private fun buildMockUser(email: String, isReturningUser: Boolean) = User(
        uid = "mock-user-001",
        email = email,
        firstName = if (isReturningUser) "Alok Kumar" else "",
        isProfileComplete = isReturningUser,
        plan = if (isReturningUser) "PRO" else "FREE"
    )

    companion object {
        // Any email in this set will skip onboarding and route straight to Dashboard.
        // Use "new@test.com" (or any other address) to exercise the onboarding flow.
        private val RETURNING_USER_EMAILS = setOf(
            "returning@test.com",
            "student@learningpanda.com"
        )
    }
}
