package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.Todo
import com.example.myapplication.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetTodosForDateUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    operator fun invoke(date: LocalDate): Flow<List<Todo>> {
        return repository.getTodosByDate(date)
    }
}
