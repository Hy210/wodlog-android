package com.wodlog.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wodlog.app.presentation.calendar.CalendarScreen
import com.wodlog.app.presentation.compare.CompareScreen
import com.wodlog.app.presentation.home.HomeScreen
import com.wodlog.app.presentation.lifestyle.LifestyleScreen
import com.wodlog.app.presentation.profile.ProfileScreen
import com.wodlog.app.presentation.prompt.PromptScreen
import com.wodlog.app.presentation.report.ReportEditScreen
import com.wodlog.app.presentation.resultedit.ResultEditScreen
import com.wodlog.app.presentation.settings.SettingsScreen
import com.wodlog.app.presentation.woddetail.WodDetailScreen
import com.wodlog.app.presentation.wodedit.WodEditScreen

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
        composable(WodlogRoute.WodEdit.route) {
            WodEditScreen()
        }
        composable(WodlogRoute.WodDetail.route) {
            WodDetailScreen()
        }
        composable(WodlogRoute.ResultEdit.route) {
            ResultEditScreen()
        }
        composable(WodlogRoute.Prompt.route) {
            PromptScreen()
        }
        composable(WodlogRoute.ReportEdit.route) {
            ReportEditScreen()
        }
        composable(WodlogRoute.Profile.route) {
            ProfileScreen()
        }
        composable(WodlogRoute.Lifestyle.route) {
            LifestyleScreen()
        }
    }
}
