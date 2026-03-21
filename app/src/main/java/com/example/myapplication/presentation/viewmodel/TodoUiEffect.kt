package com.example.myapplication.presentation.viewmodel

sealed class TodoUiEffect {
    data class ShowSnackbar(val message: String) : TodoUiEffect()
    data class NavigateToDetail(val todoId: Long) : TodoUiEffect()
}
