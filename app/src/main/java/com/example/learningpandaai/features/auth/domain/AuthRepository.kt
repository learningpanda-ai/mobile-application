package com.example.learningpandaai.features.auth.domain

data class User(
    val uid: String,
    val email: String,
    val firstName: String,
    val isProfileComplete: Boolean,
    val plan: String = ""
)

class RateLimitException(
    val retryAfterSeconds: Int,
    message: String
) : Exception(message)

interface AuthRepository {

    suspend fun sendOtp(email: String): Result<String>

    suspend fun verifyOtp(challengeId: String, otp: String): Result<User>

    suspend fun loginWithGoogle(idToken: String): Result<User>

    suspend fun logout(): Result<Unit>

    suspend fun verifyOtpForSensitiveAction(challengeId: String, otp: String): Result<Unit>
}