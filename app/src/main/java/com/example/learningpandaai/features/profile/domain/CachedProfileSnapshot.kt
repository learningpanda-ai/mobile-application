package com.example.learningpandaai.features.profile.domain

data class CachedProfileSnapshot(
    val firstName: String,
    val gradeLevel: String,
    val plan: String?,
    val avatarUrl: String?,
    val favoriteSubject: String,
    val parentEmail: String,
    val selectedSubjects: Set<String>
)