package com.example.badhabitcontrol.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.badhabitcontrol.data.repository.HabitRepository
import com.example.badhabitcontrol.ui.habit.HabitDetailScreen
import com.example.badhabitcontrol.ui.home.HomeScreen
import com.example.badhabitcontrol.ui.history.HistoryScreen
import com.example.badhabitcontrol.ui.settings.SettingsScreen
import com.example.badhabitcontrol.ui.theme.CalmBackground
import com.example.badhabitcontrol.ui.theme.CalmBorder
import com.example.badhabitcontrol.ui.theme.CalmSurface
import com.example.badhabitcontrol.ui.theme.TextMuted
import com.example.badhabitcontrol.ui.theme.TextPrimary
import com.example.badhabitcontrol.ui.theme.TextSecondary
import com.example.badhabitcontrol.ui.urge.UrgeFlowScreen
import com.example.badhabitcontrol.ui.viewmodel.AppViewModelFactory
import com.example.badhabitcontrol.ui.viewmodel.HabitViewModel
import com.example.badhabitcontrol.ui.viewmodel.HistoryViewModel
import com.example.badhabitcontrol.ui.viewmodel.HomeViewModel
import com.example.badhabitcontrol.ui.viewmodel.SettingsViewModel
import com.example.badhabitcontrol.ui.viewmodel.UrgeViewModel

sealed class Screen(val route: String, val title: String, val icon: ImageVector? = null) {
    data object Home : Screen("home", "HOME", Icons.Default.Home)
    data object History : Screen("history", "HISTORY", Icons.Default.DateRange)
    data object Settings : Screen("settings", "SETTINGS", Icons.Default.Settings)
    data object Detail : Screen("detail/{habitId}", "DETAIL") {
        fun createRoute(habitId: Long) = "detail/$habitId"
    }
    data object Urge : Screen("urge/{habitId}", "URGE") {
        fun createRoute(habitId: Long) = "urge/$habitId"
    }
}

@Composable
fun AppNavigation(
    repository: HabitRepository,
    navController: NavHostController = rememberNavController(),
    directUrgeHabitId: Long? = null,
    onUrgeHandled: () -> Unit = {}
) {
    androidx.compose.runtime.LaunchedEffect(directUrgeHabitId) {
        if (directUrgeHabitId != null && directUrgeHabitId > 0) {
            navController.navigate(Screen.Urge.createRoute(directUrgeHabitId))
            onUrgeHandled()
        }
    }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(Screen.Home, Screen.History, Screen.Settings)
    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        containerColor = CalmBackground,
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = CalmSurface,
                    tonalElevation = 0.dp
                ) {
                    bottomNavItems.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                screen.icon?.let {
                                    Icon(
                                        imageVector = it,
                                        contentDescription = screen.title
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = screen.title,
                                    fontSize = 10.sp,
                                    letterSpacing = 1.sp
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = TextPrimary,
                                unselectedIconColor = TextMuted,
                                selectedTextColor = TextPrimary,
                                unselectedTextColor = TextMuted,
                                indicatorColor = CalmBorder
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.Home.route) {
                val homeViewModel: HomeViewModel = viewModel(
                    factory = AppViewModelFactory(repository)
                )
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToDetail = { habitId ->
                        navController.navigate(Screen.Detail.createRoute(habitId))
                    },
                    onStartUrge = { habitId ->
                        navController.navigate(Screen.Urge.createRoute(habitId))
                    }
                )
            }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("habitId") { type = NavType.LongType })
            ) { backStackEntry ->
                val habitId = backStackEntry.arguments?.getLong("habitId") ?: 1L
                val habitViewModel: HabitViewModel = viewModel(
                    factory = AppViewModelFactory(repository, habitId = habitId)
                )
                HabitDetailScreen(
                    viewModel = habitViewModel,
                    onBack = { navController.popBackStack() },
                    onStartUrge = { id ->
                        navController.navigate(Screen.Urge.createRoute(id))
                    }
                )
            }

            composable(
                route = Screen.Urge.route,
                arguments = listOf(navArgument("habitId") { type = NavType.LongType })
            ) { backStackEntry ->
                val habitId = backStackEntry.arguments?.getLong("habitId") ?: 1L
                val habitName = if (habitId == 1L) "Cigarettes" else "Masturbation"
                val urgeViewModel: UrgeViewModel = viewModel(
                    factory = AppViewModelFactory(repository, habitId = habitId)
                )
                UrgeFlowScreen(
                    habitName = habitName,
                    viewModel = urgeViewModel,
                    onDismiss = { navController.popBackStack() }
                )
            }

            composable(Screen.History.route) {
                val historyViewModel: HistoryViewModel = viewModel(
                    factory = AppViewModelFactory(repository)
                )
                HistoryScreen(viewModel = historyViewModel)
            }

            composable(Screen.Settings.route) {
                val settingsViewModel: SettingsViewModel = viewModel(
                    factory = AppViewModelFactory(repository)
                )
                SettingsScreen(viewModel = settingsViewModel)
            }
        }
    }
}
