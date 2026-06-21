package com.example.learningpandaai.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.learningpandaai.core.util.InputValidation
import com.example.learningpandaai.core.util.Logger
import com.example.learningpandaai.features.auth.domain.AuthRepository
import com.example.learningpandaai.features.auth.domain.RateLimitException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = Channel<AuthUiEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var resendCooldownJob: Job? = null

    /** challenge_id from the latest send-otp; required by verify-otp. */
    private var challengeId: String? = null

    fun onShowEmailForm() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun onEmailChanged(newEmail: String) {
        _uiState.value = _uiState.value.copy(
            emailInput = newEmail,
            emailError = InputValidation.emailShowsError(newEmail),
            errorMessage = null
        )
    }

    fun onOtpChanged(newOtp: String) {
        val digitsOnly = InputValidation.filterOtpDigits(newOtp)
        _uiState.value = _uiState.value.copy(
            otpInput = digitsOnly,
            otpError = digitsOnly.isNotEmpty() && digitsOnly.length < InputValidation.MIN_OTP_LENGTH,
            errorMessage = null
        )
    }

    fun onDismissEmailForm() {
        clearResendCooldown()
        _uiState.value = _uiState.value.copy(
            isOtpSent = false,
            otpInput = "",
            otpError = false,
            errorMessage = null,
            isLoading = false
        )
    }

    fun onChangeEmail() {
        clearResendCooldown()
        _uiState.value = _uiState.value.copy(
            isOtpSent = false,
            otpInput = "",
            otpError = false,
            errorMessage = null
        )
    }

    fun resendOtpCode() {
        val state = _uiState.value
        if (!state.canResendOtp) return
        sendOtpCode()
    }

    fun sendOtpCode() {
        val state = _uiState.value
        val email = state.emailInput.trim()

        if (!state.isEmailStepValid) {
            _uiState.value = state.copy(
                emailError = InputValidation.emailShowsError(email),
                errorMessage = "Please enter a valid educational email address."
            )
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            authRepository.sendOtp(email)
                .onSuccess { newChallengeId ->
                    Logger.d("sendOtpCode: OTP sent successfully for $email")
                    challengeId = newChallengeId
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isOtpSent = true,
                        otpInput = "",
                        otpError = false
                    )
                    startResendCooldown()
                }
                .onFailure { throwable ->
                    Logger.e("sendOtpCode: failed — ${throwable.message}", throwable)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Failed to send verification code. Please retry."
                    )
                    if (throwable is RateLimitException && _uiState.value.isOtpSent) {
                        startResendCooldown(throwable.retryAfterSeconds)
                    }
                }
        }
    }

    fun verifyOtpAndLogin() {
        val state = _uiState.value
        val otp = state.otpInput.trim()

        if (!state.isOtpStepValid) {
            _uiState.value = state.copy(
                otpError = otp.isNotEmpty() && otp.length < InputValidation.MIN_OTP_LENGTH,
                errorMessage = "Please enter the complete verification code."
            )
            return
        }

        val currentChallengeId = challengeId
        if (currentChallengeId.isNullOrBlank()) {
            _uiState.value = state.copy(
                errorMessage = "Your code expired. Please request a new one."
            )
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            authRepository.verifyOtp(currentChallengeId, otp)
                .onSuccess { user ->
                    Logger.d("verifyOtpAndLogin: authenticated uid=${user.uid} — routing to ${if (user.isProfileComplete) "Dashboard" else "Onboarding"}")
                    clearResendCooldown()
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    val event = if (user.isProfileComplete) AuthUiEvent.NavigateToDashboard else AuthUiEvent.NavigateToOnboarding
                    _events.trySend(event)
                }
                .onFailure { throwable ->
                    Logger.e("verifyOtpAndLogin: failed — ${throwable.message}", throwable)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Invalid or expired code."
                    )
                }
        }
    }

    fun onGoogleError(message: String) {
        _uiState.value = _uiState.value.copy(errorMessage = message)
    }

    fun authenticateWithGoogle(idToken: String) {
        if (idToken.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Google login aborted: Empty ID token."
            )
            return
        }

        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            authRepository.loginWithGoogle(idToken)
                .onSuccess { user ->
                    Logger.d("authenticateWithGoogle: success uid=${user.uid} — routing to ${if (user.isProfileComplete) "Dashboard" else "Onboarding"}")
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    val event = if (user.isProfileComplete) AuthUiEvent.NavigateToDashboard else AuthUiEvent.NavigateToOnboarding
                    _events.trySend(event)
                }
                .onFailure { throwable ->
                    Logger.e("authenticateWithGoogle: failed — ${throwable.message}", throwable)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Google login failed."
                    )
                }
        }
    }

    private fun startResendCooldown(totalSeconds: Int = RESEND_COOLDOWN_SECONDS) {
        resendCooldownJob?.cancel()
        resendCooldownJob = viewModelScope.launch {
            for (seconds in totalSeconds downTo 1) {
                _uiState.value = _uiState.value.copy(resendCooldownSeconds = seconds)
                delay(ONE_SECOND_MS)
            }
            _uiState.value = _uiState.value.copy(resendCooldownSeconds = 0)
        }
    }

    private fun clearResendCooldown() {
        resendCooldownJob?.cancel()
        resendCooldownJob = null
        if (_uiState.value.resendCooldownSeconds != 0) {
            _uiState.value = _uiState.value.copy(resendCooldownSeconds = 0)
        }
    }

    override fun onCleared() {
        resendCooldownJob?.cancel()
        super.onCleared()
    }

    companion object {
        const val RESEND_COOLDOWN_SECONDS = 30
        private const val ONE_SECOND_MS = 1_000L
    }
}
