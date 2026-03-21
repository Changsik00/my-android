package com.example.myapplication.presentation.screens.calendar

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.presentation.components.TodoListSection
import com.example.myapplication.presentation.viewmodel.TodoUiEvent
import com.example.myapplication.presentation.viewmodel.TodoViewModel
import com.example.myapplication.domain.model.Todo
import java.time.LocalDate

@Composable
fun CalendarScreen(
    viewModel: TodoViewModel = hiltViewModel(),
    onNavigateToDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    CalendarScreenContent(
        selectedDate = uiState.selectedDate,
        todos = uiState.todos,
        isLoading = uiState.isLoading,
        error = uiState.error,
        onDateSelected = { date -> viewModel.onEvent(TodoUiEvent.SelectDate(date)) },
        onToggleTodo = { id -> viewModel.onEvent(TodoUiEvent.ToggleTodo(id)) },
        onDeleteTodo = { id -> viewModel.onEvent(TodoUiEvent.DeleteTodo(id)) },
        onTodoClick = onNavigateToDetail,
        onAddClick = { /* TODO: Open Bottom Sheet (SPEC-404) */ }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreenContent(
    selectedDate: LocalDate,
    todos: List<Todo>,
    isLoading: Boolean,
    error: String?,
    onDateSelected: (LocalDate) -> Unit,
    onToggleTodo: (Long) -> Unit,
    onDeleteTodo: (Long) -> Unit,
    onTodoClick: (Long) -> Unit,
    onAddClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Todo Scheduler") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Todo")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            MonthCalendar(
                selectedDate = selectedDate,
                onDateSelected = onDateSelected,
                modifier = Modifier.padding(16.dp)
            )
            
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            
            // SPEC-403: Todo List Section 추가
            TodoListSection(
                todos = todos,
                isLoading = isLoading,
                error = error,
                onToggleTodo = onToggleTodo,
                onDeleteTodo = onDeleteTodo,
                onTodoClick = onTodoClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
