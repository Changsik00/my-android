package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.repository.TodoRepository
import javax.inject.Inject

class ToggleTodoUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(id: Long) {
        val todo = repository.getTodoById(id)
        if (todo != null) {
            val updatedTodo = todo.copy(isCompleted = !todo.isCompleted)
            repository.updateTodo(updatedTodo)
        }
    }
}
