package com.example.learningpandaai.features.auth.presentation

import com.example.learningpandaai.core.util.InputValidation

/**
 * Models the complete, immutable state of the Authentication Screen.
 * Adapts reactively to the modern passwordless Email + OTP state machine.
 */
data class AuthUiState(
    val emailInput: String = "",
    val otpInput: String = "",
    val isOtpSent: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emailError: Boolean = false,
    val otpError: Boolean = false,
    /** Seconds left before the user can resend OTP; 0 means resend is allowed. */
    val resendCooldownSeconds: Int = 0,
) {
    val canResendOtp: Boolean
        get() = isOtpSent && resendCooldownSeconds == 0 && !isLoading

    val isEmailStepValid: Boolean
        get() = emailInput.isNotBlank() &&
            InputValidation.hasValidEmailFormat(emailInput) &&
            !emailError

    val isOtpStepValid: Boolean
        get() = otpInput.length >= InputValidation.MIN_OTP_LENGTH && !otpError

    val canSubmit: Boolean
        get() = if (isOtpSent) isOtpStepValid else isEmailStepValid
}
