package com.example.myapplication.presentation.viewmodel

import com.example.myapplication.domain.model.TodoPriority
import java.time.LocalDate

sealed class TodoUiEvent {
    data class SelectDate(val date: LocalDate) : TodoUiEvent()
    data class AddTodo(
        val title: String,
        val description: String,
        val priority: TodoPriority = TodoPriority.LOW
    ) : TodoUiEvent()
    data class ToggleTodo(val id: Long) : TodoUiEvent()
    data class DeleteTodo(val id: Long) : TodoUiEvent()
    data class ToggleTodoCompletion(val id: Long) : TodoUiEvent() // Explicit action
    object RefreshList : TodoUiEvent() // SPEC-603: TodoDetailActivity에서 돌아올 때 목록 새로고침
}
