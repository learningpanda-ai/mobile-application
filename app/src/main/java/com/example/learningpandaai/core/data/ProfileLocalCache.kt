package com.example.learningpandaai.core.data

import javax.inject.Inject
import javax.inject.Singleton

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

}