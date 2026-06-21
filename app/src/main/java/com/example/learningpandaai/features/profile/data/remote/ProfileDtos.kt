package com.example.learningpandaai.features.profile.data.remote

import com.google.gson.annotations.SerializedName

data class ProfileDetailsDto(
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("city_town") val cityTown: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("parent_guardian_name") val parentGuardianName: String? = null,
    @SerializedName("parent_guardian_mobile") val parentGuardianMobile: String? = null,
    @SerializedName("parent_guardian_email") val parentGuardianEmail: String? = null,
    @SerializedName("grade") val grade: String? = null,
    @SerializedName("board") val board: String? = null,
    @SerializedName("profile_link") val profileLink: String? = null
)

data class AboutYouDto(
    @SerializedName("feeling_on_studies") val feelingOnStudies: String? = null,
    @SerializedName("future_career") val futureCareer: String? = null,
    @SerializedName("future_skills") val futureSkills: String? = null
)

data class ProfileResponseDto(
    @SerializedName("email") val email: String? = null,
    @SerializedName("is_onboarded") val isOnboarded: Boolean? = null,
    @SerializedName("profile_details") val profileDetails: ProfileDetailsDto? = null,
    @SerializedName("about_you") val aboutYou: AboutYouDto? = null,
    @SerializedName("image") val image: String? = null
)

data class SubjectsResponseDto(
    @SerializedName("subjects") val subjects: List<String>? = null
)

data class OnboardingRequestDto(
    @SerializedName("profile_details") val profileDetails: ProfileDetailsDto,
    @SerializedName("about_you") val aboutYou: AboutYouDto
)

data class UpdateProfileRequestDto(
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    @SerializedName("city_town") val cityTown: String? = null,
    @SerializedName("state") val state: String? = null,
    @SerializedName("parent_guardian_name") val parentGuardianName: String? = null,
    @SerializedName("parent_guardian_mobile") val parentGuardianMobile: String? = null,
    @SerializedName("parent_guardian_email") val parentGuardianEmail: String? = null,
    @SerializedName("grade") val grade: String? = null,
    @SerializedName("board") val board: String? = null
)