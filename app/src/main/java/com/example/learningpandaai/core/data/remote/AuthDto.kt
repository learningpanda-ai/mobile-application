package com.example.learningpandaai.features.auth.data.remote

import com.google.gson.annotations.SerializedName

data class RefreshTokenRequest(
    @SerializedName("refresh_token") val refreshToken: String? = null
)

data class AuthUserDto(
    @SerializedName("id") val id: Number? = null,
    @SerializedName("email") val email: String?,
    @SerializedName("email_verified") val emailVerified: Boolean = false,
    @SerializedName("is_onboarded") val isOnboarded: Boolean = false,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("role") val role: String? = null,
    @SerializedName("plan") val plan: String? = null,
    @SerializedName("name") val name: String? = null,
    @SerializedName("image") val image: String? = null,
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("grade") val grade: String? = null,
    @SerializedName("board") val board: String? = null
)

data class AuthTokensDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("token_type") val tokenType: String? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("expires_in") val expiresIn: Long? = null,
    @SerializedName("refresh_expires_in") val refreshExpiresIn: Long? = null,
    @SerializedName("user") val user: AuthUserDto? = null
)