package com.example.learningpandaai.features.auth.data.remote

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Synchronous refresh API for [okhttp3.Authenticator] — must use [retrofit2.Call], not suspend.
 * Uses a dedicated OkHttp client without the token authenticator to avoid refresh loops.
 */
interface AuthTokenRefreshApi {

    @POST("api/v1/auth/refresh")
    fun refreshToken(@Body request: RefreshTokenRequest): Call<AuthTokensDto>
}