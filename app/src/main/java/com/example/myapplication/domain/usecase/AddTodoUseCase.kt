package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Todo
import com.example.myapplication.domain.model.TodoPriority
import com.example.myapplication.domain.repository.TodoRepository
import java.time.LocalDate
import javax.inject.Inject

class AddTodoUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        date: LocalDate,
        priority: TodoPriority = TodoPriority.LOW
    ): Long {
        if (title.isBlank()) {
            throw IllegalArgumentException("Title cannot be blank")
        }
        val todo = Todo(
            title = title,
            description = description,
            date = date,
            priority = priority
        )
        return repository.insertTodo(todo)
    }
}
