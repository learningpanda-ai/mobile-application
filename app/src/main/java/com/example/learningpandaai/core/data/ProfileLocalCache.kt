package com.example.learningpandaai.core.data

import javax.inject.Inject
import javax.inject.Singleton
import com.example.learningpandaai.core.util.ProfileAvatarResolver
import com.example.learningpandaai.features.profile.data.remote.ProfileResponseDto

@Singleton
class ProfileLocalCache @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val profileCacheNotifier: ProfileCacheNotifier
) {

    fun cacheAuthSessionUser(
        email : String?,
        imageUrl : String?,
        plan : String?,
        firstName : String?,
        grade : String?,
        board : String?
    ) {
        email?.takeIf { it.isNotBlank() }?.let { securePreferences.saveEmail(it) }
        imageUrl?.takeIf { it.isNotBlank() }?.let { securePreferences.saveProfileImageUrl(it) }
        securePreferences.savePlan(plan)
        val resolvedFirstName = firstName.orEmpty()
        if (resolvedFirstName.isNotBlank()) {
            securePreferences.saveStudentProfile(
                firstName = resolvedFirstName,
                gradeLevel = grade.orEmpty(),
                board = board.orEmpty()
            )
        }
        profileCacheNotifier.notifyProfileCacheUpdated()
    }

    fun cacheProfileResponse(response: ProfileResponseDto) {
        ProfileAvatarResolver.fromProfileResponse(response)
            ?.let { securePreferences.saveProfileImageUrl(it) }
        val details = response.profileDetails ?: return
        val firstName = details.firstName.orEmpty()
        if (firstName.isNotBlank()) {
            securePreferences.saveStudentProfile(
                firstName = firstName,
                gradeLevel = details.grade.orEmpty(),
                board = details.board.orEmpty()
            )
        }
        securePreferences.saveParentProfile(
            parentName = details.parentGuardianName.orEmpty(),
            parentMobile = details.parentGuardianMobile.orEmpty(),
            parentEmail = details.parentGuardianEmail.orEmpty()
        )
        profileCacheNotifier.notifyProfileCacheUpdated()
    }

}