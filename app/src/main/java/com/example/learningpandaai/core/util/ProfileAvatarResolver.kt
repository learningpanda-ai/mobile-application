package com.example.learningpandaai.core.util

import com.example.learningpandaai.features.profile.data.remote.ProfileResponseDto

object ProfileAvatarResolver {

    fun fromProfileResponse(response: ProfileResponseDto): String? {
        val candidates = listOfNotNull(
            response.profileDetails?.profileLink,
            response.image
        )
        return candidates
            .mapNotNull { ProfileImageUrlResolver.resolve(it) }
            .firstOrNull()
    }
}