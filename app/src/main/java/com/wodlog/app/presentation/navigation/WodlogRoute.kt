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
    data object WodDetail : WodlogRoute("wod-detail/{wodId}", "WOD Detail") {
        const val wodIdArgument = "wodId"
        const val placeholderRoute = "wod-detail"

        fun createRoute(wodId: Long): String = "wod-detail/$wodId"
    }
    data object ResultEdit : WodlogRoute("result-edit/{wodId}", "Result Edit") {
        const val wodIdArgument = "wodId"
        const val placeholderRoute = "result-edit"

        fun createRoute(wodId: Long): String = "result-edit/$wodId"
    }
    data object Prompt : WodlogRoute("prompt/{wodId}", "Prompt") {
        const val wodIdArgument = "wodId"
        const val placeholderRoute = "prompt"

        fun createRoute(wodId: Long): String = "prompt/$wodId"
    }
    data object ReportEdit : WodlogRoute("report-edit/{wodId}", "Report Edit") {
        const val wodIdArgument = "wodId"
        const val placeholderRoute = "report-edit"

        fun createRoute(wodId: Long): String = "report-edit/$wodId"
    }
    data object Profile : WodlogRoute("profile", "Profile")
    data object Lifestyle : WodlogRoute("lifestyle", "Lifestyle")
    data object License : WodlogRoute("license", "License")

    companion object {
        val startDestination = Home.route
        val topLevelRoutes: List<WodlogRoute>
            get() = listOf(Home, Calendar, Compare, Settings)
        val secondaryRoutes: List<WodlogRoute>
            get() = listOf(
                WodEdit,
                WodDetail,
                ResultEdit,
                Prompt,
                ReportEdit,
                Profile,
                Lifestyle,
                License
            )
        val allRoutes: List<WodlogRoute>
            get() = topLevelRoutes + secondaryRoutes
    }
}
