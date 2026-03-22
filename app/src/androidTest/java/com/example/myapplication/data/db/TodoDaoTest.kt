package com.example.myapplication.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.data.db.TodoDao
import com.example.myapplication.data.db.TodoDatabase
import com.example.myapplication.data.db.TodoEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.LocalDate

@RunWith(AndroidJUnit4::class)
class TodoDaoTest {
    private lateinit var todoDao: TodoDao
    private lateinit var db: TodoDatabase

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        // In-memory 데이터베이스는 메모리에 상주하므로 프로세스가 죽으면 날아갑니다 (테스트용으로 완벽)
        db = Room.inMemoryDatabaseBuilder(
            context,
            TodoDatabase::class.java
        ).allowMainThreadQueries().build()
        todoDao = db.todoDao()
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTodosForDate() = runBlocking {
        // Given
        val todo = TodoEntity(
            id = 1L,
            title = "Test Todo",
            description = "Description",
            date = LocalDate.ofEpochDay(1000L),
            isCompleted = false,
            createdAt = 100L
        )
        todoDao.insertTodo(todo)

        // When
        val todos = todoDao.getTodosByDate(LocalDate.ofEpochDay(1000L)).first()

        // Then
        assertEquals(1, todos.size)
        assertEquals("Test Todo", todos[0].title)
    }

    @Test
    fun deleteTodo() = runBlocking {
        // Given
        val todo = TodoEntity(1L, "Delete Me", "", LocalDate.ofEpochDay(1000L), false, 100L)
        todoDao.insertTodo(todo)
        
        // When
        todoDao.deleteTodo(1L)
        val todos = todoDao.getTodosByDate(LocalDate.ofEpochDay(1000L)).first()
        
        // Then
        assertTrue(todos.isEmpty())
    }

    @Test
    fun updateTodoCompletion() = runBlocking {
        // Given
        val todo = TodoEntity(1L, "Toggle", "", LocalDate.ofEpochDay(1000L), false, 100L)
        todoDao.insertTodo(todo)
        
        // When
        val inserted = todoDao.getTodosByDate(LocalDate.ofEpochDay(1000L)).first().first()
        val updated = inserted.copy(isCompleted = true)
        todoDao.updateTodo(updated)
        
        // Then
        val result = todoDao.getTodosByDate(LocalDate.ofEpochDay(1000L)).first().first()
        assertTrue(result.isCompleted)
    }
}
