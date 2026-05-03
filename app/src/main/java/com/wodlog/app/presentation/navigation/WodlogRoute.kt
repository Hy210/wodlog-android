package com.wodlog.app.presentation.navigation

sealed class WodlogRoute(
    val route: String,
    val label: String
) {
    data object Home : WodlogRoute("home", "홈")
    data object Calendar : WodlogRoute("calendar", "캘린더")
    data object Compare : WodlogRoute("compare", "비교")
    data object Settings : WodlogRoute("settings", "설정")

    companion object {
        val startDestination = Home.route
        val topLevelRoutes = listOf(Home, Calendar, Compare, Settings)
    }
}
