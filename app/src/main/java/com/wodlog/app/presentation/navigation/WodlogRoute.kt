package com.wodlog.app.presentation.navigation

sealed class WodlogRoute(
    val route: String,
    val label: String
) {
    data object Home : WodlogRoute("home", "홈")
    data object Calendar : WodlogRoute("calendar", "캘린더")
    data object Compare : WodlogRoute("compare", "비교")
    data object Settings : WodlogRoute("settings", "설정")
    data object WodEdit : WodlogRoute("wod-edit", "WOD Edit")
    data object WodDetail : WodlogRoute("wod-detail", "WOD Detail")
    data object ResultEdit : WodlogRoute("result-edit", "Result Edit")
    data object Prompt : WodlogRoute("prompt", "Prompt")
    data object ReportEdit : WodlogRoute("report-edit", "Report Edit")
    data object Profile : WodlogRoute("profile", "Profile")
    data object Lifestyle : WodlogRoute("lifestyle", "Lifestyle")

    companion object {
        val startDestination = Home.route
        val topLevelRoutes = listOf(Home, Calendar, Compare, Settings)
        val secondaryRoutes = listOf(
            WodEdit,
            WodDetail,
            ResultEdit,
            Prompt,
            ReportEdit,
            Profile,
            Lifestyle
        )
        val allRoutes = topLevelRoutes + secondaryRoutes
    }
}
