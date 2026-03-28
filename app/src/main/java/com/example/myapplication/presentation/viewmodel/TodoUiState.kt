package com.example.myapplication.presentation.viewmodel

import com.example.myapplication.domain.model.Todo
import java.time.LocalDate

data class TodoUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = false,      // 초기 로딩 (목록이 비어 있을 때)
    val isRefreshing: Boolean = false,   // SPEC-604: 날짜 전환 시 목록 위 오버레이 로딩
    val error: String? = null
)
