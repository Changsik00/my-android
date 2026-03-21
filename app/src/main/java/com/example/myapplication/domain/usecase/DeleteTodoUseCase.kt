package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.repository.TodoRepository
import javax.inject.Inject

class DeleteTodoUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.deleteTodo(id)
    }
}
