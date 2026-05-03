package com.wodlog.app.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WodlogNavigationTest {
    @Test
    fun topLevelRoutes_includePhaseZeroScreens() {
        val routes = WodlogRoute.topLevelRoutes.map { it.route }

        assertTrue(routes.contains("home"))
        assertTrue(routes.contains("calendar"))
        assertTrue(routes.contains("compare"))
        assertTrue(routes.contains("settings"))
    }

    @Test
    fun topLevelRoutes_areUnique() {
        val routes = WodlogRoute.topLevelRoutes.map { it.route }

        assertEquals(routes.size, routes.toSet().size)
    }

    @Test
    fun startDestination_isHome() {
        assertEquals(WodlogRoute.Home.route, WodlogRoute.startDestination)
    }
}
