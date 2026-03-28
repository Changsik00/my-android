package com.example.myapplication.presentation.screens.calendar

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.myapplication.TodoDetailActivity
import com.example.myapplication.presentation.components.AddTodoBottomSheet
import com.example.myapplication.presentation.components.TodoListSection
import com.example.myapplication.presentation.viewmodel.TodoUiEvent
import com.example.myapplication.presentation.viewmodel.TodoViewModel
import com.example.myapplication.domain.model.Todo
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: TodoViewModel = hiltViewModel(),
    onNavigateToDetail: (Long) -> Unit  // 사용하지 않음 (Activity 전환으로 대체), 인터페이스 유지
) {
    val uiState by viewModel.uiState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // SPEC-603: ActivityResultLauncher — RESULT_OK 반환 시 목록 새로고침
    val detailLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onEvent(TodoUiEvent.RefreshList)
        }
    }

    CalendarScreenContent(
        selectedDate = uiState.selectedDate,
        todos = uiState.todos,
        isLoading = uiState.isLoading,
        isRefreshing = uiState.isRefreshing,
        error = uiState.error,
        onDateSelected = { date -> viewModel.onEvent(TodoUiEvent.SelectDate(date)) },
        onToggleTodo = { id -> viewModel.onEvent(TodoUiEvent.ToggleTodo(id)) },
        onDeleteTodo = { id -> viewModel.onEvent(TodoUiEvent.DeleteTodo(id)) },
        onTodoClick = { todoId ->
            // Intent + EXTRA_TODO_ID로 TodoDetailActivity 이동
            val intent = Intent(context, TodoDetailActivity::class.java).apply {
                putExtra(TodoDetailActivity.EXTRA_TODO_ID, todoId)
            }
            detailLauncher.launch(intent)
        },
        onAddClick = { showBottomSheet = true }
    )

    if (showBottomSheet) {
        AddTodoBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            onSaveTodo = { title, description, priority ->
                viewModel.onEvent(TodoUiEvent.AddTodo(title, description, priority))
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreenContent(
    selectedDate: LocalDate,
    todos: List<Todo>,
    isLoading: Boolean,
    isRefreshing: Boolean = false,
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
            
            // SPEC-403/604: Todo List Section (Shimmer + RefreshOverlay)
            TodoListSection(
                todos = todos,
                isLoading = isLoading,
                isRefreshing = isRefreshing,
                error = error,
                onToggleTodo = onToggleTodo,
                onDeleteTodo = onDeleteTodo,
                onTodoClick = onTodoClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
