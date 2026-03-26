package com.example.myapplication.data.repository

import com.example.myapplication.data.db.TodoDao
import com.example.myapplication.data.db.TodoEntity
import com.example.myapplication.domain.model.Todo
import com.example.myapplication.domain.repository.TodoRepository
import com.example.myapplication.domain.repository.BaseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class TodoRepositoryImpl @Inject constructor(
    private val dao: TodoDao
) : BaseRepository(), TodoRepository {

    override fun getTodosByDate(date: LocalDate): Flow<List<Todo>> = safeFlow {
        dao.getTodosByDate(date).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertTodo(todo: Todo): Long {
        return safeCall {
            dao.insertTodo(todo.toEntity())
        } ?: -1L
    }

    override suspend fun updateTodo(todo: Todo) {
        safeCall {
            dao.updateTodo(todo.toEntity())
        }
    }

    override suspend fun deleteTodo(id: Long) {
        safeCall {
            dao.deleteTodo(id)
        }
    }

    override suspend fun getTodoById(id: Long): Todo? {
        return safeCall {
            dao.getTodoById(id)?.toDomain()
        }
    }

    // Mapper extension functions
    private fun TodoEntity.toDomain(): Todo {
        return Todo(
            id = id,
            title = title,
            description = description,
            date = date,
            isCompleted = isCompleted,
            priority = priority
        )
    }

    private fun Todo.toEntity(): TodoEntity {
        return TodoEntity(
            id = id,
            title = title,
            description = description,
            date = date,
            isCompleted = isCompleted,
            priority = priority
        )
    }
}
