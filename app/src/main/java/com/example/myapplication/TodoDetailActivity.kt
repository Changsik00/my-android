package com.example.myapplication

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.myapplication.presentation.screens.detail.TodoDetailScreen
import com.example.myapplication.presentation.viewmodel.TodoDetailUiEffect
import com.example.myapplication.presentation.viewmodel.TodoDetailUiEvent
import com.example.myapplication.presentation.viewmodel.TodoDetailViewModel
import com.example.myapplication.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * SPEC-603: TodoDetailActivity
 * - MainAcitivity로부터 EXTRA_TODO_ID를 Intent로 전달받아 상세 화면을 표시
 * - 삭제/수정 후 RESULT_OK를 반환하여 CalendarScreen이 목록을 새로고침하도록 함
 */
@AndroidEntryPoint
class TodoDetailActivity : ComponentActivity() {

    private val viewModel: TodoDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val todoId = intent.getLongExtra(EXTRA_TODO_ID, -1L)
        if (todoId == -1L) {
            finish()
            return
        }

        // 최초 로드
        viewModel.onEvent(TodoDetailUiEvent.LoadTodo(todoId))

        setContent {
            MyApplicationTheme {
                val uiState by viewModel.uiState.collectAsState()

                // Effect 처리 — NavigateBack 시 RESULT_OK 반환
                LaunchedEffect(viewModel.effect) {
                    viewModel.effect.collect { effect ->
                        when (effect) {
                            is TodoDetailUiEffect.NavigateBack -> {
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                            is TodoDetailUiEffect.ShowSnackbar -> {
                                // Snackbar는 AppScaffold가 없으므로 여기서 직접 처리 불필요
                                // AppErrorBus를 통해 MainActivity의 AppScaffold에서 표시됨
                            }
                        }
                    }
                }

                TodoDetailScreen(
                    uiState = uiState,
                    onEvent = viewModel::onEvent,
                    onBackClick = { finish() }
                )
            }
        }
    }

    companion object {
        const val EXTRA_TODO_ID = "extra_todo_id"
    }
}
