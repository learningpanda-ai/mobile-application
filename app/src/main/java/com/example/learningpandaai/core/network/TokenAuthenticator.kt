package com.example.learningpandaai.core.network

import com.example.learningpandaai.core.session.SessionManager
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp [Authenticator] that rotates JWTs via POST /api/v1/auth/refresh when a request
 * receives HTTP 401, then retries the original call with the new access token.
 */

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenRefreshService: TokenRefreshService,
    private val sessionManager: SessionManager
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if(response.code !=  HTTP_UNAUTHORIZED) return null
        if (unauthorizedCount(response) >= MAX_RETRY_COUNT) {
            sessionManager.expireSession()
            return null
        }
        val path = response.request.url.encodedPath
        if (isAuthEndpoint(path)) return null
        val newAccessToken = tokenRefreshService.refreshAccessTokenBlocking()
        if (newAccessToken.isNullOrBlank()) {
            sessionManager.expireSession()
            return null
        }
        return response.request.newBuilder()
            .header(AUTHORIZATION_HEADER, "Bearer $newAccessToken")
            .build()

    }

    private fun isAuthEndpoint(path: String): Boolean =
        path.contains("/auth/send-otp") ||
                path.contains("/auth/verify-otp") ||
                path.contains("/auth/magic-link") ||
                path.contains("/auth/refresh") ||
                path.contains("/auth/google") ||
                path.contains("/auth/exchange-code") ||
                path.contains("/auth/logout")


    private fun unauthorizedCount(response: Response): Int {
        var count = 0
        var current: Response? = response
        while (current != null) {
            if (current.code == HTTP_UNAUTHORIZED) count++
            current = current.priorResponse
        }
        return count
    }

    private companion object {
        const val HTTP_UNAUTHORIZED = 401
        const val MAX_RETRY_COUNT = 2
        const val AUTHORIZATION_HEADER = "Authorization"
    }
}