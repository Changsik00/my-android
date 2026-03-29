package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.Todo
import com.example.myapplication.domain.model.TodoSummary
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth

interface TodoRepository {
    fun getTodosByDate(date: LocalDate): Flow<List<Todo>>
    suspend fun insertTodo(todo: Todo): Long
    suspend fun updateTodo(todo: Todo)
    suspend fun deleteTodo(id: Long)
    suspend fun getTodoById(id: Long): Todo?

    // SPEC-605: DatabaseView를 통한 월별 통계
    fun getTodoSummaryForMonth(yearMonth: YearMonth): Flow<Map<LocalDate, TodoSummary>>
}
