package com.example.myapplication.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.myapplication.presentation.navigation.Screen
import com.example.myapplication.presentation.screens.calendar.CalendarScreen

@Composable
fun TodoNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Calendar.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Calendar.route) {
            CalendarScreen(
                onNavigateToDetail = { todoId ->
                    navController.navigate(Screen.TodoDetail.createRoute(todoId))
                }
            )
        }
        composable(
            route = Screen.TodoDetail.route,
            arguments = listOf(navArgument("todoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val todoId = backStackEntry.arguments?.getLong("todoId") ?: -1L
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Todo 상세 화면 (ID: $todoId) - 구현 준비 중")
            }
        }
    }
}
