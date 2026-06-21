package com.example.learningpandaai.core.network

import com.example.learningpandaai.core.data.SecurePreferences
import com.example.learningpandaai.core.util.Logger
import com.example.learningpandaai.features.auth.data.remote.AuthTokenRefreshApi
import com.example.learningpandaai.features.auth.data.remote.AuthTokensDto
import com.example.learningpandaai.features.auth.data.remote.RefreshTokenRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenRefreshService @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val authTokenRefreshApi: AuthTokenRefreshApi
){
    private val refreshLock = Any()

    /**
     * @return new access token, or null if refresh cannot proceed
     */
    fun refreshAccessTokenBlocking(): String? = synchronized(refreshLock) {
        val refreshToken = securePreferences.getRefreshToken()
        if (refreshToken.isNullOrBlank()) {
            Logger.d("refreshAccessTokenBlocking: no refresh token in storage")
            return null
        }
        return try {
            val response = authTokenRefreshApi
                .refreshToken(RefreshTokenRequest(refreshToken = refreshToken))
                .execute()
            if (!response.isSuccessful) {
                Logger.e(
                    "refreshAccessTokenBlocking: HTTP ${response.code()} — ${response.message()}"
                )
                return null
            }
            val body = response.body()
            if (body == null) {
                Logger.e("refreshAccessTokenBlocking: empty response body")
                return null
            }
            persistTokens(body)
            Logger.d("refreshAccessTokenBlocking: tokens rotated successfully")
            body.accessToken
        } catch (e: Exception) {
            Logger.e("refreshAccessTokenBlocking: failed — ${e.message}", e)
            null
        }
    }

    private fun persistTokens(response: AuthTokensDto) {
        securePreferences.saveOAuthToken(response.accessToken)
        response.refreshToken?.takeIf { it.isNotBlank() }?.let {
            securePreferences.saveRefreshToken(it)
        }
        response.user?.plan?.takeIf { it.isNotBlank() }?.let { securePreferences.savePlan(it) }
    }
}