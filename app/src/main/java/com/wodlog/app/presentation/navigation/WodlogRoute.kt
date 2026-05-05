package com.wodlog.app.presentation.navigation

sealed class WodlogRoute(
    val route: String,
    val label: String
) {
    data object Home : WodlogRoute("home", "홈")
    data object Calendar : WodlogRoute("calendar", "캘린더")
    data object Compare : WodlogRoute("compare", "비교")
    data object Settings : WodlogRoute("settings", "설정")
    data object WodEdit : WodlogRoute("wod-edit", "WOD 작성")
    data object WodEditFromImport : WodlogRoute("wod-edit/imported", "WOD 작성")
    data object WodDetail : WodlogRoute("wod-detail/{wodId}", "WOD 상세") {
        const val wodIdArgument = "wodId"
        const val placeholderRoute = "wod-detail"

        fun createRoute(wodId: Long): String = "wod-detail/$wodId"
    }
    data object ResultEdit : WodlogRoute("result-edit/{wodId}", "결과 입력") {
        const val wodIdArgument = "wodId"
        const val placeholderRoute = "result-edit"

        fun createRoute(wodId: Long): String = "result-edit/$wodId"
    }
    data object Prompt : WodlogRoute("prompt/{wodId}", "질문지") {
        const val wodIdArgument = "wodId"
        const val placeholderRoute = "prompt"

        fun createRoute(wodId: Long): String = "prompt/$wodId"
    }
    data object ReportEdit : WodlogRoute("report-edit/{wodId}", "GPT 답변") {
        const val wodIdArgument = "wodId"
        const val placeholderRoute = "report-edit"

        fun createRoute(wodId: Long): String = "report-edit/$wodId"
    }
    data object CafeImport : WodlogRoute("cafe-import/{cafeSourceId}", "WOD 불러오기") {
        const val cafeSourceIdArgument = "cafeSourceId"
        const val placeholderRoute = "cafe-import"

        fun createRoute(cafeSourceId: Long): String = "cafe-import/$cafeSourceId"
    }
    data object ImportedWodPreview : WodlogRoute("imported-wod-preview", "가져온 WOD 미리보기")
    data object Profile : WodlogRoute("profile", "프로필")
    data object Lifestyle : WodlogRoute("lifestyle", "생활습관")
    data object CafeSourceSettings : WodlogRoute("cafe-source-settings", "카페 소스 설정")
    data object License : WodlogRoute("license", "라이선스")

    companion object {
        val startDestination = Home.route
        val topLevelRoutes: List<WodlogRoute>
            get() = listOf(Home, Calendar, Compare, Settings)
        val secondaryRoutes: List<WodlogRoute>
            get() = listOf(
                WodEdit,
                WodEditFromImport,
                WodDetail,
                ResultEdit,
                Prompt,
                ReportEdit,
                CafeImport,
                ImportedWodPreview,
                Profile,
                Lifestyle,
                CafeSourceSettings,
                License
            )
        val allRoutes: List<WodlogRoute>
            get() = topLevelRoutes + secondaryRoutes
    }
}
