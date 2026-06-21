package com.example.learningpandaai.features.onboarding.domain

data class OnboardingProfile(
    val firstName: String,
    val lastName: String,
    val state: String,
    val city: String,
    val parentName: String,
    val parentMobile: String,
    val parentEmail: String,
    val gradeLevel: String,
    val board: String,
    val selectedSubjects: Set<String>,
    val favoriteSubject: String,
    val studiesFeeling: String,
    val careerIdea: String,
    val discoverStrengths: String
)