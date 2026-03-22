package com.example.myapplication.data.repository

import com.example.myapplication.data.db.TodoDao
import com.example.myapplication.data.db.TodoEntity
import com.example.myapplication.domain.model.Todo
import com.example.myapplication.domain.model.TodoPriority
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class TodoRepositoryImplTest {

    private lateinit var repository: TodoRepositoryImpl
    private lateinit var fakeDao: FakeTodoDao

    @Before
    fun setUp() {
        fakeDao = FakeTodoDao()
        repository = TodoRepositoryImpl(fakeDao)
    }

    @Test
    fun getTodosForDate_returnsDomainModels() = runBlocking {
        // Given
        val date = LocalDate.of(2023, 10, 24)
        fakeDao.insertTodo(TodoEntity(1L, "test", "", date, false, 0L))

        // When
        val result = repository.getTodosByDate(date).first()

        // Then
        assertEquals(1, result.size)
        assertEquals("test", result[0].title)
        assertFalse(result[0].isCompleted)
    }

    @Test
    fun addTodo_insertsMappedEntity() = runBlocking {
        // Given
        val todo = Todo(
            id = 2L,
            title = "New Todo",
            description = "Desc",
            date = LocalDate.now(),
            isCompleted = false,
            priority = TodoPriority.HIGH
        )

        // When
        repository.insertTodo(todo)

        // Then
        assertEquals(1, fakeDao.db.size)
        assertEquals("New Todo", fakeDao.db.first().title)
    }

    // Fake Dao Implementation for Local JVM Test
    class FakeTodoDao : TodoDao {
        val db = mutableListOf<TodoEntity>()

        override fun getTodosByDate(date: LocalDate): Flow<List<TodoEntity>> {
            return flowOf(db.filter { it.date == date })
        }

        override suspend fun insertTodo(todo: TodoEntity): Long {
            val existing = db.find { it.id == todo.id }
            if (existing != null) {
                db.remove(existing)
            }
            val newId = if (todo.id == 0L) db.size + 1L else todo.id
            db.add(todo.copy(id = newId))
            return newId
        }

        override suspend fun updateTodo(todo: TodoEntity) {
            val index = db.indexOfFirst { it.id == todo.id }
            if (index != -1) {
                db[index] = todo
            }
        }

        override suspend fun deleteTodo(id: Long) {
            db.removeIf { it.id == id }
        }
        
        override suspend fun getTodoById(id: Long): TodoEntity? {
            return db.find { it.id == id }
        }
    }
}
