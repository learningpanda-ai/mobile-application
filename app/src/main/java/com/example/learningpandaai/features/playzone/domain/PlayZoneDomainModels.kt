package com.example.learningpandaai.features.playzone.domain

data class PlayerStats(
    val level: Int,
    val xp: Int,
    val gamesPlayed: Int,
    val currentStreak: Int
)