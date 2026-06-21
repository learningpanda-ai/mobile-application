package com.example.learningpandaai.features.profile.domain

data class UserProfile(
    val id: String,
    val email: String,
    val plan: String = "FREE",
    val avatarUrl: String? = null,
    val firstName: String,
    val lastName: String,
    val grade: String,
    val schoolBoard: String,
    val city: String,
    val state: String,
    val parentName: String,
    val parentMobile: String,
    val parentEmail: String,
    val courses: List<String>,
    val currentStreak: Int,
    val longestStreak: Int,
    val favoriteSubject: String,
    val studyFeeling: String,
    val careerThoughts: String
)

data class ProfileUpdateParams(
    val firstName: String? = null,
    val lastName: String? = null,
    val city: String? = null,
    val state: String? = null,
    val grade: String? = null,
    val schoolBoard: String? = null,
    val parentName: String? = null,
    val parentMobile: String? = null,
    val parentEmail: String? = null,
    val courses: List<String>? = null
)