package com.example.learningpandaai.features.askpanda.presentation

/**
 * Active top-level tab on the Ask Panda screen.
 */
enum class AskPandaTab {
    CHAT,
    VOICE,
    PANDA_CHAT;

    fun toMode(): AskPandaMode = when (this) {
        CHAT -> AskPandaMode.CHAT
        VOICE -> AskPandaMode.VOICE
        PANDA_CHAT -> AskPandaMode.VIDEO
    }

    companion object {
        fun fromMode(mode: AskPandaMode): AskPandaTab = when (mode) {
            AskPandaMode.CHAT -> CHAT
            AskPandaMode.VOICE -> VOICE
            AskPandaMode.VIDEO -> PANDA_CHAT
        }
    }
}
