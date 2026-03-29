package com.example.myapplication.presentation.viewmodel

import com.example.myapplication.domain.model.Todo
import com.example.myapplication.domain.model.TodoSummary
import java.time.LocalDate

data class TodoUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    // SPEC-605: DatabaseView — 달력 날짜 셀 인디케이터용 월별 통계
    val todoSummaries: Map<LocalDate, TodoSummary> = emptyMap()
)
