package com.wodlog.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wodlog.app.presentation.calendar.CalendarScreen
import com.wodlog.app.presentation.compare.CompareScreen
import com.wodlog.app.presentation.home.HomeScreen
import com.wodlog.app.presentation.settings.SettingsScreen

@Composable
fun WodlogNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = WodlogRoute.startDestination,
        modifier = modifier
    ) {
        composable(WodlogRoute.Home.route) {
            HomeScreen()
        }
        composable(WodlogRoute.Calendar.route) {
            CalendarScreen()
        }
        composable(WodlogRoute.Compare.route) {
            CompareScreen()
        }
        composable(WodlogRoute.Settings.route) {
            SettingsScreen()
        }
    }
}
