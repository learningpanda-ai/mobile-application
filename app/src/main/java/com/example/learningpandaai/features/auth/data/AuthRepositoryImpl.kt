package com.example.learningpandaai.features.auth.data

import com.example.learningpandaai.core.data.ProfileLocalCache
import com.example.learningpandaai.core.data.SecurePreferences
import com.example.learningpandaai.core.util.Logger
import com.example.learningpandaai.features.auth.data.remote.AuthApiService
import com.example.learningpandaai.features.auth.data.remote.AuthTokensDto
import com.example.learningpandaai.features.auth.data.remote.GoogleMobileRequest
import com.example.learningpandaai.features.auth.data.remote.LogoutRequest
import com.example.learningpandaai.features.auth.data.remote.SendOtpRequest
import com.example.learningpandaai.features.auth.data.remote.VerifyOtpRequest
import com.example.learningpandaai.features.auth.domain.AuthRepository
import com.example.learningpandaai.features.auth.domain.RateLimitException
import com.example.learningpandaai.features.auth.domain.User
import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApiService: AuthApiService,
    private val securePreferences: SecurePreferences,
    private val profileLocalCache: ProfileLocalCache
) : AuthRepository {

    override suspend fun sendOtp(email: String): Result<String> {
        return try {
            val response = authApiService.sendOtp(SendOtpRequest(email = email.trim()))
            val challengeId = response.challengeId
            if (challengeId.isNullOrBlank()) {
                Logger.e("sendOtp: missing challenge_id in response")
                return Result.failure(Exception("Could not start verification. Please try again."))
            }
            Logger.d("sendOtp: OTP dispatched, challenge acquired")
            Result.success(challengeId)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Logger.e("sendOtp: network failure — ${e.message}", e)
            Result.failure(IOException("No internet connection. Please check your network.", e))
        } catch (e: HttpException) {
            Logger.e("sendOtp: HTTP ${e.code()} — ${e.message()}", e)
            if (e.code() == 429) {
                val retryAfter = parseRetryAfterSeconds(e)
                return Result.failure(RateLimitException(retryAfter, rateLimitMessage(retryAfter)))
            }
            Result.failure(Exception(extractServerError(e), e))
        } catch (e: Exception) {
            Logger.e("sendOtp: unexpected error — ${e.message}", e)
            Result.failure(Exception("An unexpected error occurred while sending the verification code.", e))
        }
    }

    override suspend fun verifyOtp(challengeId: String, otp: String): Result<User> {
        return try {
            val tokens = authApiService.verifyOtp(
                VerifyOtpRequest(challengeId = challengeId, otp = otp.trim())
            )
            if (tokens.accessToken.isBlank()) {
                Logger.e("verifyOtp: missing access token in response")
                return Result.failure(Exception("Verification failed. Please try again."))
            }
            persistSession(tokens)
            Logger.d("verifyOtp: authenticated, isOnboarded=${tokens.user?.isOnboarded == true}, plan=${tokens.user?.plan}")
            Result.success(tokens.toDomainUser())
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Logger.e("verifyOtp: network failure — ${e.message}", e)
            Result.failure(IOException("No internet connection detected.", e))
        } catch (e: HttpException) {
            Logger.e("verifyOtp: HTTP ${e.code()} — ${e.message()}", e)
            Result.failure(Exception(extractServerError(e), e))
        } catch (e: Exception) {
            Logger.e("verifyOtp: unexpected error — ${e.message}", e)
            Result.failure(Exception("Verification failed. Please try again.", e))
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<User> {
        return try {
            val tokens = authApiService.loginWithGoogleMobile(GoogleMobileRequest(idToken = idToken))
            persistSession(tokens)
            Logger.d("loginWithGoogle: success, isOnboarded=${tokens.user?.isOnboarded == true}")
            Result.success(tokens.toDomainUser())
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Logger.e("loginWithGoogle: network failure — ${e.message}", e)
            Result.failure(IOException("Network error: Google connection failed.", e))
        } catch (e: HttpException) {
            Logger.e("loginWithGoogle: HTTP ${e.code()} — ${e.message()}", e)
            Result.failure(Exception(extractServerError(e), e))
        } catch (e: Exception) {
            Logger.e("loginWithGoogle: unexpected error — ${e.message}", e)
            Result.failure(Exception("Google Sign-In failed unexpectedly.", e))
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            authApiService.logout(LogoutRequest(refreshToken = securePreferences.getRefreshToken()))
            securePreferences.clearAllData()
            Logger.d("logout: server session invalidated and local data cleared")
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Logger.e("logout: ${e.message} — clearing local session anyway", e)
            securePreferences.clearAllData()
            Result.success(Unit)
        }
    }

    override suspend fun verifyOtpForSensitiveAction(challengeId: String, otp: String): Result<Unit> {
        return try {
            authApiService.verifyOtp(
                VerifyOtpRequest(challengeId = challengeId, otp = otp.trim())
            )
            Logger.d("verifyOtpForSensitiveAction: OTP verified")
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Logger.e("verifyOtpForSensitiveAction: network failure — ${e.message}", e)
            Result.failure(IOException("No internet connection. Please check your network.", e))
        } catch (e: HttpException) {
            Logger.e("verifyOtpForSensitiveAction: HTTP ${e.code()} — ${e.message()}", e)
            Result.failure(Exception("Invalid or expired code. Please try again.", e))
        } catch (e: Exception) {
            Logger.e("verifyOtpForSensitiveAction: unexpected error — ${e.message}", e)
            Result.failure(Exception("Verification failed. Please try again.", e))
        }
    }

    private fun persistSession(tokens: AuthTokensDto) {
        securePreferences.saveOAuthToken(tokens.accessToken)
        tokens.refreshToken?.takeIf { it.isNotBlank() }?.let { securePreferences.saveRefreshToken(it) }

        val user = tokens.user ?: return
        profileLocalCache.cacheAuthSessionUser(
            email = user.email,
            imageUrl = user.image,
            plan = user.plan,
            firstName = user.firstName,
            grade = user.grade,
            board = user.board
        )
    }

    private fun AuthTokensDto.toDomainUser(): User {
        val u = user
        return User(
            uid = u?.id?.toString().orEmpty(),
            email = u?.email.orEmpty(),
            firstName = u?.firstName.orEmpty(),
            isProfileComplete = u?.isOnboarded == true,
            plan = u?.plan.orEmpty()
        )
    }

    private fun extractServerError(exception: HttpException): String = when (exception.code()) {
        400 -> "Invalid or expired verification code."
        401 -> "Verification code expired or incorrect."
        404 -> "Email address not registered."
        422 -> "Please check your details and try again."
        500 -> "Server maintenance. Please try again shortly."
        else -> "Server error (${exception.code()}). Please try again."
    }

    /**
     * Reads the server's `Retry-After` hint (seconds) from a 429 response,
     * falling back to [DEFAULT_RETRY_AFTER_SECONDS] when it's missing or non-numeric.
     */
    private fun parseRetryAfterSeconds(exception: HttpException): Int {
        val header = exception.response()?.headers()?.get("Retry-After")
        return header?.trim()?.toIntOrNull()?.coerceIn(1, MAX_RETRY_AFTER_SECONDS)
            ?: DEFAULT_RETRY_AFTER_SECONDS
    }

    private fun rateLimitMessage(retryAfterSeconds: Int): String =
        "Too many requests. Please wait $retryAfterSeconds seconds before trying again."

    companion object {
        private const val DEFAULT_RETRY_AFTER_SECONDS = 60
        private const val MAX_RETRY_AFTER_SECONDS = 60 * 60
    }
}
