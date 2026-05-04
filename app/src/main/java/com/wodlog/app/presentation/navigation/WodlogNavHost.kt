package com.wodlog.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.wodlog.app.domain.backup.BackupExportUseCase
import com.wodlog.app.domain.backup.BackupImportPreviewUseCase
import com.wodlog.app.domain.repository.WodlogRepository
import com.wodlog.app.presentation.calendar.CalendarRoute
import com.wodlog.app.presentation.calendar.CalendarViewModel
import com.wodlog.app.presentation.calendar.CalendarViewModelFactory
import com.wodlog.app.presentation.compare.CompareRoute
import com.wodlog.app.presentation.compare.CompareViewModel
import com.wodlog.app.presentation.compare.CompareViewModelFactory
import com.wodlog.app.presentation.home.HomeScreen
import com.wodlog.app.presentation.lifestyle.LifestyleRoute
import com.wodlog.app.presentation.lifestyle.LifestyleViewModel
import com.wodlog.app.presentation.lifestyle.LifestyleViewModelFactory
import com.wodlog.app.presentation.profile.ProfileRoute
import com.wodlog.app.presentation.profile.ProfileViewModel
import com.wodlog.app.presentation.profile.ProfileViewModelFactory
import com.wodlog.app.presentation.prompt.PromptRoute
import com.wodlog.app.presentation.prompt.PromptScreen
import com.wodlog.app.presentation.prompt.PromptUiState
import com.wodlog.app.presentation.prompt.PromptViewModel
import com.wodlog.app.presentation.prompt.PromptViewModelFactory
import com.wodlog.app.presentation.report.ReportEditRoute
import com.wodlog.app.presentation.report.ReportEditScreen
import com.wodlog.app.presentation.report.ReportEditUiState
import com.wodlog.app.presentation.report.ReportEditViewModel
import com.wodlog.app.presentation.report.ReportEditViewModelFactory
import com.wodlog.app.presentation.resultedit.ResultEditRoute
import com.wodlog.app.presentation.resultedit.ResultEditScreen
import com.wodlog.app.presentation.resultedit.ResultEditUiState
import com.wodlog.app.presentation.resultedit.ResultEditViewModel
import com.wodlog.app.presentation.resultedit.ResultEditViewModelFactory
import com.wodlog.app.presentation.settings.LicenseScreen
import com.wodlog.app.presentation.settings.SettingsRoute
import com.wodlog.app.presentation.woddetail.WodDetailRoute
import com.wodlog.app.presentation.woddetail.WodDetailScreen
import com.wodlog.app.presentation.woddetail.WodDetailUiState
import com.wodlog.app.presentation.woddetail.WodDetailViewModel
import com.wodlog.app.presentation.woddetail.WodDetailViewModelFactory
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
            val calendarViewModel: CalendarViewModel = viewModel(
                factory = CalendarViewModelFactory(repository)
            )
            CalendarRoute(
                viewModel = calendarViewModel,
                onCreateWod = {
                    navController.navigate(WodlogRoute.WodEdit.route)
                },
                onOpenWod = { wodId ->
                    navController.navigate(WodlogRoute.WodDetail.createRoute(wodId))
                }
            )
        }
        composable(WodlogRoute.Compare.route) {
            val compareViewModel: CompareViewModel = viewModel(
                factory = CompareViewModelFactory(repository)
            )
            CompareRoute(viewModel = compareViewModel)
        }
        composable(WodlogRoute.Settings.route) {
            SettingsRoute(
                backupExportUseCase = BackupExportUseCase(repository),
                backupImportPreviewUseCase = BackupImportPreviewUseCase(),
                onProfileClick = {
                    navController.navigate(WodlogRoute.Profile.route)
                },
                onLifestyleClick = {
                    navController.navigate(WodlogRoute.Lifestyle.route)
                },
                onLicenseClick = {
                    navController.navigate(WodlogRoute.License.route)
                }
            )
        }
        composable(WodlogRoute.WodEdit.route) {
            val wodEditViewModel: WodEditViewModel = viewModel(
                factory = WodEditViewModelFactory(repository)
            )
            WodEditRoute(
                viewModel = wodEditViewModel,
                onSaved = { wodId ->
                    navController.navigate(WodlogRoute.WodDetail.createRoute(wodId))
                }
            )
        }
        composable(WodlogRoute.WodDetail.placeholderRoute) {
            WodDetailScreen(
                state = WodDetailUiState(errorMessage = "Open a saved WOD to view details."),
                onEditResultClick = {
                    navController.navigate(WodlogRoute.ResultEdit.placeholderRoute)
                },
                onPromptClick = {
                    navController.navigate(WodlogRoute.Prompt.placeholderRoute)
                },
                onReportClick = {
                    navController.navigate(WodlogRoute.ReportEdit.placeholderRoute)
                }
            )
        }
        composable(
            route = WodlogRoute.WodDetail.route,
            arguments = listOf(
                navArgument(WodlogRoute.WodDetail.wodIdArgument) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val wodId = backStackEntry.arguments?.getLong(WodlogRoute.WodDetail.wodIdArgument)
            if (wodId == null) {
                WodDetailScreen(
                    state = WodDetailUiState(errorMessage = "Missing WOD id."),
                    onEditResultClick = {
                        navController.navigate(WodlogRoute.ResultEdit.placeholderRoute)
                    },
                    onPromptClick = {
                        navController.navigate(WodlogRoute.Prompt.placeholderRoute)
                    },
                    onReportClick = {
                        navController.navigate(WodlogRoute.ReportEdit.placeholderRoute)
                    }
                )
            } else {
                val wodDetailViewModel: WodDetailViewModel = viewModel(
                    factory = WodDetailViewModelFactory(repository)
                )
                WodDetailRoute(
                    viewModel = wodDetailViewModel,
                    wodId = wodId,
                    onEditResultClick = {
                        navController.navigate(WodlogRoute.ResultEdit.createRoute(wodId))
                    },
                    onPromptClick = {
                        navController.navigate(WodlogRoute.Prompt.createRoute(wodId))
                    },
                    onReportClick = {
                        navController.navigate(WodlogRoute.ReportEdit.createRoute(wodId))
                    }
                )
            }
        }
        composable(WodlogRoute.ResultEdit.placeholderRoute) {
            ResultEditScreen(
                state = ResultEditUiState(message = "Open a saved WOD before entering a result."),
                onScoreTypeChange = {},
                onTimeSecondsChange = {},
                onRoundsChange = {},
                onRepsChange = {},
                onTotalRepsChange = {},
                onLoadChange = {},
                onDistanceChange = {},
                onCaloriesChange = {},
                onRxStatusChange = {},
                onRpeChange = {},
                onConditionChange = {},
                onMemoChange = {},
                onSaveClick = {}
            )
        }
        composable(
            route = WodlogRoute.ResultEdit.route,
            arguments = listOf(
                navArgument(WodlogRoute.ResultEdit.wodIdArgument) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val wodId = backStackEntry.arguments?.getLong(WodlogRoute.ResultEdit.wodIdArgument)
            if (wodId == null) {
                ResultEditScreen(
                    state = ResultEditUiState(message = "Missing WOD id."),
                    onScoreTypeChange = {},
                    onTimeSecondsChange = {},
                    onRoundsChange = {},
                    onRepsChange = {},
                    onTotalRepsChange = {},
                    onLoadChange = {},
                    onDistanceChange = {},
                    onCaloriesChange = {},
                    onRxStatusChange = {},
                    onRpeChange = {},
                    onConditionChange = {},
                    onMemoChange = {},
                    onSaveClick = {}
                )
            } else {
                val resultEditViewModel: ResultEditViewModel = viewModel(
                    factory = ResultEditViewModelFactory(repository)
                )
                ResultEditRoute(
                    viewModel = resultEditViewModel,
                    wodId = wodId,
                    onSaved = {
                        navController.navigate(WodlogRoute.WodDetail.createRoute(wodId)) {
                            popUpTo(WodlogRoute.WodDetail.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
        composable(WodlogRoute.Prompt.placeholderRoute) {
            PromptScreen(
                state = PromptUiState(errorMessage = "Open a saved WOD to generate a prompt."),
                onCopyClick = {}
            )
        }
        composable(
            route = WodlogRoute.Prompt.route,
            arguments = listOf(
                navArgument(WodlogRoute.Prompt.wodIdArgument) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val wodId = backStackEntry.arguments?.getLong(WodlogRoute.Prompt.wodIdArgument)
            if (wodId == null) {
                PromptScreen(
                    state = PromptUiState(errorMessage = "Missing WOD id."),
                    onCopyClick = {}
                )
            } else {
                val promptViewModel: PromptViewModel = viewModel(
                    factory = PromptViewModelFactory(repository)
                )
                PromptRoute(
                    viewModel = promptViewModel,
                    wodId = wodId
                )
            }
        }
        composable(WodlogRoute.ReportEdit.placeholderRoute) {
            ReportEditScreen(
                state = ReportEditUiState(errorMessage = "Open a saved WOD before saving a report."),
                onAnswerChange = {},
                onSelectReport = {},
                onNewReportClick = {},
                onSaveClick = {},
                onDeleteClick = {}
            )
        }
        composable(
            route = WodlogRoute.ReportEdit.route,
            arguments = listOf(
                navArgument(WodlogRoute.ReportEdit.wodIdArgument) {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->
            val wodId = backStackEntry.arguments?.getLong(WodlogRoute.ReportEdit.wodIdArgument)
            if (wodId == null) {
                ReportEditScreen(
                    state = ReportEditUiState(errorMessage = "Missing WOD id."),
                    onAnswerChange = {},
                    onSelectReport = {},
                    onNewReportClick = {},
                    onSaveClick = {},
                    onDeleteClick = {}
                )
            } else {
                val reportEditViewModel: ReportEditViewModel = viewModel(
                    factory = ReportEditViewModelFactory(repository)
                )
                ReportEditRoute(
                    viewModel = reportEditViewModel,
                    wodId = wodId
                )
            }
        }
        composable(WodlogRoute.Profile.route) {
            val profileViewModel: ProfileViewModel = viewModel(
                factory = ProfileViewModelFactory(repository)
            )
            ProfileRoute(viewModel = profileViewModel)
        }
        composable(WodlogRoute.Lifestyle.route) {
            val lifestyleViewModel: LifestyleViewModel = viewModel(
                factory = LifestyleViewModelFactory(repository)
            )
            LifestyleRoute(viewModel = lifestyleViewModel)
        }
        composable(WodlogRoute.License.route) {
            LicenseScreen()
        }
    }
}
