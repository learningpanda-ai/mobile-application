package com.example.learningpandaai.features.auth.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/v1/auth/send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): SendOtpResponseDto

    @POST("api/v1/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): AuthTokensDto

    @POST("api/v1/auth/magic-link/verify")
    suspend fun verifyMagicLink(@Body request: MagicLinkVerifyRequest): OneTimeCodeDto

    @POST("api/v1/auth/exchange-code")
    suspend fun exchangeCode(@Body request: ExchangeCodeRequest): AuthTokensDto

    @POST("api/v1/auth/google/mobile")
    suspend fun loginWithGoogleMobile(@Body request: GoogleMobileRequest): AuthTokensDto

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): AuthTokensDto

    @POST("api/v1/auth/logout")
    suspend fun logout(@Body request: LogoutRequest)
}