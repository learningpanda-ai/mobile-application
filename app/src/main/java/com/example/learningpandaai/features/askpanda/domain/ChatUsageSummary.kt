package com.example.learningpandaai.features.askpanda.domain

data class ChatUsageSummary(
    val planName: String,
    val dailyUsed: Int,
    val dailyLimit: Int?,
    val monthlyUsed: Int,
    val monthlyLimit: Int?
) {
    fun dailyLabel(): String? {
        if (dailyLimit == null || dailyLimit <= 0) return null
        return "$dailyUsed/$dailyLimit today"
    }

    fun monthlyLabel(): String? {
        if (monthlyLimit == null || monthlyLimit <= 0) return null
        return "$monthlyUsed/$monthlyLimit this month"
    }

    fun quotaLabel(): String? = dailyLabel() ?: monthlyLabel()
}
