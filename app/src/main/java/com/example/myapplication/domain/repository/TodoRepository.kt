package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Todo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface TodoRepository {
    fun getTodosByDate(date: LocalDate): Flow<List<Todo>>
    suspend fun insertTodo(todo: Todo): Long
    suspend fun updateTodo(todo: Todo)
    suspend fun deleteTodo(id: Long)
    suspend fun getTodoById(id: Long): Todo?
}
