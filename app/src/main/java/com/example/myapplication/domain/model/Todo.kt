package com.example.myapplication.domain.model

import java.time.LocalDate

data class Todo(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: LocalDate,
    val isCompleted: Boolean = false,
    val priority: TodoPriority = TodoPriority.LOW
)
