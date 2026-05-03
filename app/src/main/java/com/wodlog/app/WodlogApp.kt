package com.wodlog.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.wodlog.app.presentation.navigation.WodlogNavHost
import com.wodlog.app.presentation.navigation.WodlogRoute

@Composable
fun WodlogApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: WodlogRoute.startDestination
    val topLevelRoutes = WodlogRoute.topLevelRoutes

    Scaffold(
        bottomBar = {
            NavigationBar {
                topLevelRoutes.forEach { route ->
                    NavigationBarItem(
                        selected = currentRoute == route.route,
                        onClick = {
                            navController.navigate(route.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(route.label) },
                        icon = {}
                    )
                }
            }
        }
    ) { innerPadding ->
        WodlogNavHost(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
