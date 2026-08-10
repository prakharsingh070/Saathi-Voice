package com.example.saathi.ui

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
    object Conversation : Screen("conversation")
    object SchemeResults : Screen("scheme_results")
    object SchemeDetails : Screen("scheme_details/{schemeId}") {
        fun createRoute(schemeId: String) = "scheme_details/$schemeId"
    }
    object History : Screen("history")
    object Profile : Screen("profile")
    object Privacy : Screen("privacy")
}
