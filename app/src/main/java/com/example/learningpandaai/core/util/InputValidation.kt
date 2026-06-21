package com.example.learningpandaai.core.util

import android.util.Patterns

object InputValidation {
    fun hasValidEmailFormat(email: String): Boolean =
        Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun emailShowsError(email: String): Boolean =
        email.isNotEmpty() && !hasValidEmailFormat(email)

    fun phoneShowsError(digitsOnly: String): Boolean =
        digitsOnly.isNotEmpty() && digitsOnly.length != 10

    fun isPhoneComplete(digitsOnly: String): Boolean =
        digitsOnly.length == 10 && digitsOnly.all { it.isDigit() }

    fun filterPhoneDigits(input: String): String =
        input.filter { it.isDigit() }.take(10)

    const val MIN_OTP_LENGTH = 6

    fun isOtpComplete(otp: String): Boolean =
        otp.filter { it.isDigit() }.length >= MIN_OTP_LENGTH

    fun filterOtpDigits(input: String): String =
        input.filter { it.isDigit() }.take(MIN_OTP_LENGTH)
}
