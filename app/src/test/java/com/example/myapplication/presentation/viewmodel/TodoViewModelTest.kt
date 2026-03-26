package com.example.myapplication.presentation.viewmodel

import app.cash.turbine.test
import com.example.myapplication.domain.model.Todo
import com.example.myapplication.domain.model.TodoPriority
import com.example.myapplication.domain.usecase.AddTodoUseCase
import com.example.myapplication.domain.usecase.DeleteTodoUseCase
import com.example.myapplication.domain.usecase.GetTodosForDateUseCase
import com.example.myapplication.domain.usecase.ToggleTodoUseCase
import com.example.myapplication.presentation.model.UiText
import com.example.myapplication.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class TodoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: TodoViewModel
    private val getTodosForDateUseCase: GetTodosForDateUseCase = mockk()
    private val addTodoUseCase: AddTodoUseCase = mockk()
    private val toggleTodoUseCase: ToggleTodoUseCase = mockk()
    private val deleteTodoUseCase: DeleteTodoUseCase = mockk()

    @Before
    fun setUp() {
        // Default behavior for getTodosForDate to avoid crash on init
        every { getTodosForDateUseCase(any()) } returns flowOf(emptyList())
        
        viewModel = TodoViewModel(
            getTodosForDateUseCase = getTodosForDateUseCase,
            addTodoUseCase = addTodoUseCase,
            toggleTodoUseCase = toggleTodoUseCase,
            deleteTodoUseCase = deleteTodoUseCase
        )
    }

    @Test
    fun `Initial state has current date and empty todos`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals(LocalDate.now(), state.selectedDate)
            assertEquals(emptyList<Todo>(), state.todos)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `SelectDate updates selectedDate and triggers todos update`() = runTest {
        val targetDate = LocalDate.of(2025, 1, 1)
        val expectedTodos = listOf(
            Todo(1L, "Test", "Desc", targetDate, false, TodoPriority.LOW)
        )
        
        every { getTodosForDateUseCase(targetDate) } returns flowOf(expectedTodos)

        viewModel.uiState.test {
            // 1. Initial state
            awaitItem()
            
            viewModel.onEvent(TodoUiEvent.SelectDate(targetDate))
            
            // 2. updated selectedDate
            val stateWithDate = awaitItem()
            assertEquals(targetDate, stateWithDate.selectedDate)
            
            // 3. isLoading = true (from flatMapLatest)
            val stateLoading = awaitItem()
            assertEquals(true, stateLoading.isLoading)
            
            // 4. todos update and isLoading = false
            val stateWithTodos = awaitItem()
            assertEquals(expectedTodos, stateWithTodos.todos)
            assertEquals(false, stateWithTodos.isLoading)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `AddTodo should call usecase and show snackbar`() = runTest {
        val title = "New Job"
        val desc = "Description"
        val priority = TodoPriority.MEDIUM
        val date = viewModel.uiState.value.selectedDate
        
        coEvery { 
            addTodoUseCase(title, desc, date, priority) 
        } returns 1L

        viewModel.effect.test {
            viewModel.onEvent(TodoUiEvent.AddTodo(title, desc, priority))
            
            val effect = awaitItem()
            assertEquals(TodoUiEffect.ShowSnackbar(UiText.DynamicString("할 일이 추가되었습니다.")), effect)
            
            coVerify { addTodoUseCase(title, desc, date, priority) }
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ToggleTodo should call usecase`() = runTest {
        val todoId = 123L
        coEvery { toggleTodoUseCase(todoId) } returns Unit
        
        viewModel.onEvent(TodoUiEvent.ToggleTodo(todoId))
        
        coVerify { toggleTodoUseCase(todoId) }
    }

    @Test
    fun `DeleteTodo should call usecase and show snackbar`() = runTest {
        val todoId = 456L
        coEvery { deleteTodoUseCase(todoId) } returns Unit
        
        viewModel.effect.test {
            viewModel.onEvent(TodoUiEvent.DeleteTodo(todoId))
            
            val effect = awaitItem()
            assertEquals(TodoUiEffect.ShowSnackbar(UiText.DynamicString("할 일이 삭제되었습니다.")), effect)
            
            coVerify { deleteTodoUseCase(todoId) }
            cancelAndIgnoreRemainingEvents()
        }
    }
}
