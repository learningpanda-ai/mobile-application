package com.example.learningpandaai.core.util

object ProfileFieldFormatter {

    fun gradeToChipId(raw: String): String {
        val digits = raw.filter { it.isDigit() }
        return digits.ifBlank { raw.trim() }
    }

    fun gradeToApi(raw: String): String {
        val digits = raw.filter { it.isDigit() }
        if (digits.isNotEmpty()) return "Class-$digits"
        val trimmed = raw.trim()
        if (trimmed.startsWith("Class", ignoreCase = true)) {
            val fromLabel = trimmed.filter { it.isDigit() }
            if (fromLabel.isNotEmpty()) return "Class-$fromLabel"
        }
        return trimmed
    }

    fun mobileToApi(digitsOnly: String): String {
        val digits = digitsOnly.filter { it.isDigit() }.take(10)
        return if (digits.length == 10) "+91 $digits" else digitsOnly.trim()
    }
}