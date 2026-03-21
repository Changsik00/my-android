package com.example.myapplication.presentation.viewmodel

import com.example.myapplication.presentation.model.UiText

sealed class TodoUiEffect {
    data class ShowSnackbar(val message: UiText) : TodoUiEffect()
    data class NavigateToDetail(val todoId: Long) : TodoUiEffect()
}
