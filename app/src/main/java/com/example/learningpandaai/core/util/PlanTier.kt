package com.example.learningpandaai.core.util

/**
 * Single source of truth for interpreting the backend subscription `plan` code
 * (e.g. "FREE", "PRO", "PREMIUM"). Keeps the "is this a paid tier?" rule and the
 * display formatting consistent across every screen (avatar ring, badges, profile).
 */
object PlanTier {

    const val FREE = "FREE"

    /** A plan counts as premium when it's known and not the FREE tier. */
    fun isPremium(planCode: String?): Boolean =
        !planCode.isNullOrBlank() && !planCode.trim().equals(FREE, ignoreCase = true)

    /** Human-friendly plan name, e.g. "FREE" -> "Free", "PRO" -> "Pro". */
    fun displayName(planCode: String?): String {
        val code = planCode?.trim().orEmpty().ifBlank { FREE }
        return code.lowercase().replaceFirstChar { it.uppercaseChar() }
    }
}
