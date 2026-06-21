package com.example.learningpandaai.core.navigation

/**
 * Defines the navigation destinations for our app.
 * Using a sealed class with predefined routes prevents typo bugs or navigation
 * crashes that happen when hardcoding strings directly inside screens.
 */
sealed class Screen(val route : String) {

    object Splash : Screen("splash_screen")
    object Auth : Screen("auth_screen")
    object Onboarding : Screen("onboarding_screen")
    object Dashboard : Screen("dashboard_screen")

    object Syllabus : Screen("syllabus/{subjectId}") {
        const val SUBJECT_ID_ARG = "subjectId"
        fun createRoute(subjectId: String): String = "syllabus/$subjectId"
    }
    // Dashboard Bottom Tabs Sub-Routes
    object Home : Screen("dashboard_home")
    object Progress : Screen("dashboard_progress")
    object AskPanda : Screen("dashboard_ask_panda")
    object PlayZone : Screen("dashboard_play_zone")
    object Profile : Screen("dashboard_profile")
    object EditProfile : Screen("edit_profile")
}