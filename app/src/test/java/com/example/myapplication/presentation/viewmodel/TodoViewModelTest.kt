package com.example.myapplication.presentation.viewmodel

import com.example.myapplication.domain.model.Todo
import com.example.myapplication.domain.repository.TodoRepository
import com.example.myapplication.domain.usecase.AddTodoUseCase
import com.example.myapplication.domain.usecase.DeleteTodoUseCase
import com.example.myapplication.domain.usecase.GetTodosForDateUseCase
import com.example.myapplication.domain.usecase.ToggleTodoUseCase
import com.example.myapplication.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TodoViewModel
    private lateinit var fakeRepository: FakeTodoRepository

    @Before
    fun setUp() {
        fakeRepository = FakeTodoRepository()
        viewModel = TodoViewModel(
            getTodosForDateUseCase = GetTodosForDateUseCase(fakeRepository),
            addTodoUseCase = AddTodoUseCase(fakeRepository),
            toggleTodoUseCase = ToggleTodoUseCase(fakeRepository),
            deleteTodoUseCase = DeleteTodoUseCase(fakeRepository)
        )
    }

    @Test
    fun selectDate_updatesSelectedDateInUiState() = runTest {
        // Given
        val targetDate = LocalDate.of(2025, 1, 1)

        // When
        viewModel.onEvent(TodoUiEvent.SelectDate(targetDate))

        // Then
        val currentState = viewModel.uiState.value
        assertEquals(targetDate, currentState.selectedDate)
    }

    @Test
    fun addTodo_insertsIntoRepositoryAndClearsState() = runTest {
        // Given
        val initialSize = fakeRepository.todos.size
        
        // When
        viewModel.onEvent(TodoUiEvent.AddTodo(title = "test1", description = "desc"))
        
        // Then
        assertEquals(initialSize + 1, fakeRepository.todos.size)
        val addedTodo = fakeRepository.todos.last()
        assertEquals("test1", addedTodo.title)
        assertEquals("desc", addedTodo.description)
        // AddTodo 시에 ViewModel 내부의 selectedDate가 할당되는지 (현재 선택 날짜 기반) 파악
        assertEquals(viewModel.uiState.value.selectedDate, addedTodo.date)
    }

    @Test
    fun toggleTodo_updatesRepository() = runTest {
        // Given
        val todo = Todo(1L, "Test", "", LocalDate.now(), false)
        fakeRepository.todos.add(todo)
        
        // When
        viewModel.onEvent(TodoUiEvent.ToggleTodo(1L))
        
        // Then
        val updated = fakeRepository.todos.find { it.id == 1L }
        assertNotNull(updated)
        assertEquals(true, updated?.isCompleted)
    }
}

class FakeTodoRepository : TodoRepository {
    var todos = mutableListOf<Todo>()

    override fun getTodosByDate(date: LocalDate): Flow<List<Todo>> {
        return flowOf(todos.filter { it.date == date })
    }

    override suspend fun insertTodo(todo: Todo): Long {
        val newId = if (todo.id == 0L) (todos.size + 1).toLong() else todo.id
        todos.add(todo.copy(id = newId))
        return newId
    }

    override suspend fun updateTodo(todo: Todo) {
        val index = todos.indexOfFirst { it.id == todo.id }
        if (index != -1) {
            todos[index] = todo
        }
    }

    override suspend fun deleteTodo(id: Long) {
        todos.removeIf { it.id == id }
    }

    override suspend fun getTodoById(id: Long): Todo? {
        return todos.find { it.id == id }
    }
}
