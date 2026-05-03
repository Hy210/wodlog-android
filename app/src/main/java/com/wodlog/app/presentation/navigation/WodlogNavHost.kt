package com.wodlog.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.wodlog.app.domain.repository.WodlogRepository
import com.wodlog.app.presentation.calendar.CalendarScreen
import com.wodlog.app.presentation.compare.CompareScreen
import com.wodlog.app.presentation.home.HomeScreen
import com.wodlog.app.presentation.lifestyle.LifestyleScreen
import com.wodlog.app.presentation.profile.ProfileScreen
import com.wodlog.app.presentation.profile.ProfileRoute
import com.wodlog.app.presentation.profile.ProfileViewModel
import com.wodlog.app.presentation.profile.ProfileViewModelFactory
import com.wodlog.app.presentation.prompt.PromptScreen
import com.wodlog.app.presentation.report.ReportEditScreen
import com.wodlog.app.presentation.resultedit.ResultEditScreen
import com.wodlog.app.presentation.settings.SettingsScreen
import com.wodlog.app.presentation.woddetail.WodDetailScreen
import com.wodlog.app.presentation.wodedit.WodEditRoute
import com.wodlog.app.presentation.wodedit.WodEditViewModel
import com.wodlog.app.presentation.wodedit.WodEditViewModelFactory

@Composable
fun WodlogNavHost(
    navController: NavHostController,
    repository: WodlogRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = WodlogRoute.startDestination,
        modifier = modifier
    ) {
        composable(WodlogRoute.Home.route) {
            HomeScreen(
                onCreateWodClick = {
                    navController.navigate(WodlogRoute.WodEdit.route)
                }
            )
        }
        composable(WodlogRoute.Calendar.route) {
            CalendarScreen(
                onCreateWodClick = {
                    navController.navigate(WodlogRoute.WodEdit.route)
                },
                onOpenWodClick = {
                    navController.navigate(WodlogRoute.WodDetail.route)
                }
            )
        }
        composable(WodlogRoute.Compare.route) {
            CompareScreen()
        }
        composable(WodlogRoute.Settings.route) {
            SettingsScreen(
                onProfileClick = {
                    navController.navigate(WodlogRoute.Profile.route)
                },
                onLifestyleClick = {
                    navController.navigate(WodlogRoute.Lifestyle.route)
                }
            )
        }
        composable(WodlogRoute.WodEdit.route) {
            val wodEditViewModel: WodEditViewModel = viewModel(
                factory = WodEditViewModelFactory(repository)
            )
            WodEditRoute(viewModel = wodEditViewModel)
        }
        composable(WodlogRoute.WodDetail.route) {
            WodDetailScreen(
                onEditResultClick = {
                    navController.navigate(WodlogRoute.ResultEdit.route)
                },
                onPromptClick = {
                    navController.navigate(WodlogRoute.Prompt.route)
                },
                onReportClick = {
                    navController.navigate(WodlogRoute.ReportEdit.route)
                }
            )
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
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(repository)
            )
            ProfileRoute(viewModel = profileViewModel)
        }
        composable(WodlogRoute.Lifestyle.route) {
            LifestyleScreen()
        }
    }
}
