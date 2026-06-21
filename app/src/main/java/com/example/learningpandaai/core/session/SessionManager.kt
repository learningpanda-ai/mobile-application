package com.example.learningpandaai.core.session

import com.example.learningpandaai.core.data.SecurePreferences
import com.example.learningpandaai.core.util.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(
    private val securePreferences: SecurePreferences
) {
    private val _sessionExpired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpired : SharedFlow<Unit> = _sessionExpired.asSharedFlow()

    fun expireSession()
    {
        // Clear everything so stale profile/onboarding data can't leak to next sign-in.
        securePreferences.clearAllData()
        Logger.d("expireSession: local session data cleared — navigating to sign-in")
        _sessionExpired.tryEmit(Unit)
    }
}