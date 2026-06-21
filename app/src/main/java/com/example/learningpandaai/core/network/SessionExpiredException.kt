package com.example.learningpandaai.core.network

/**
 * Thrown when the access token is invalid and refresh failed (or no refresh token exists).
 * UI should prompt the user to sign in again — not show a generic server error.
 */
class SessionExpiredException(
    message: String = ApiErrorMapper.SESSION_EXPIRED_MESSAGE,
    cause: Throwable? = null
) : Exception(message, cause)