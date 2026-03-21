package com.example.myapplication.presentation.navigation

sealed class Screen(val route: String) {
    object Calendar : Screen("calendar_screen")
    object TodoDetail : Screen("todo_detail_screen/{todoId}") {
        fun createRoute(todoId: Long) = "todo_detail_screen/$todoId"
    }
}
