package com.wodlog.app.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WodlogNavigationTest {
    @Test
    fun allRoutes_includePhaseThreeScreens() {
        val routes = WodlogRoute.allRoutes.map { it.route }

        assertTrue(routes.contains("home"))
        assertTrue(routes.contains("calendar"))
        assertTrue(routes.contains("compare"))
        assertTrue(routes.contains("settings"))
        assertTrue(routes.contains("wod-edit"))
        assertTrue(routes.contains("wod-detail/{wodId}"))
        assertTrue(routes.contains("result-edit"))
        assertTrue(routes.contains("prompt"))
        assertTrue(routes.contains("report-edit"))
        assertTrue(routes.contains("profile"))
        assertTrue(routes.contains("lifestyle"))
    }

    @Test
    fun allRoutes_areUnique() {
        val routes = WodlogRoute.allRoutes.map { it.route }

        assertEquals(routes.size, routes.toSet().size)
    }

    @Test
    fun startDestination_isHome() {
        assertEquals(WodlogRoute.Home.route, WodlogRoute.startDestination)
    }

    @Test
    fun wodDetailRoute_createsRouteWithWodId() {
        assertEquals("wod-detail/7", WodlogRoute.WodDetail.createRoute(7L))
    }

    @Test
    fun topLevelRoutes_areOnlyBottomNavigationDestinations() {
        val routes = WodlogRoute.topLevelRoutes.map { it.route }

        assertEquals(listOf("home", "calendar", "compare", "settings"), routes)
    }

    @Test
    fun secondaryRoutes_areNotInBottomNavigation() {
        val topLevelRoutes = WodlogRoute.topLevelRoutes.map { it.route }.toSet()

        WodlogRoute.secondaryRoutes.forEach { route ->
            assertFalse(topLevelRoutes.contains(route.route))
        }
    }
}
