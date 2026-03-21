package com.example.myapplication.presentation.viewmodel

import com.example.myapplication.domain.model.Todo
import java.time.LocalDate

data class TodoUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
