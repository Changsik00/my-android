package com.example.myapplication.presentation.viewmodel

import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.myapplication.domain.model.Todo
import com.example.myapplication.domain.usecase.DeleteTodoUseCase
import com.example.myapplication.domain.usecase.GetTodoByIdUseCase
import com.example.myapplication.domain.usecase.ToggleTodoUseCase
import com.example.myapplication.presentation.model.UiText
import javax.inject.Inject

// UiState
data class TodoDetailUiState(
    val todo: Todo? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

// UiEvent
sealed class TodoDetailUiEvent {
    data class LoadTodo(val id: Long) : TodoDetailUiEvent()
    data class ToggleTodo(val id: Long) : TodoDetailUiEvent()
    data class DeleteTodo(val id: Long) : TodoDetailUiEvent()
}

// UiEffect
sealed class TodoDetailUiEffect {
    object NavigateBack : TodoDetailUiEffect()
    data class ShowSnackbar(val message: UiText) : TodoDetailUiEffect()
}

/**
 * SPEC-603: TodoDetailViewModel
 * - GetTodoByIdUseCase로 상세 데이터 로드
 * - 수정(토글) / 삭제 이벤트 처리
 */
@HiltViewModel
class TodoDetailViewModel @Inject constructor(
    private val getTodoByIdUseCase: GetTodoByIdUseCase,
    private val toggleTodoUseCase: ToggleTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase
) : BaseViewModel<TodoDetailUiEvent, TodoDetailUiState, TodoDetailUiEffect>(TodoDetailUiState()) {

    override fun onEvent(event: TodoDetailUiEvent) {
        when (event) {
            is TodoDetailUiEvent.LoadTodo -> loadTodo(event.id)
            is TodoDetailUiEvent.ToggleTodo -> toggleTodo(event.id)
            is TodoDetailUiEvent.DeleteTodo -> deleteTodo(event.id)
        }
    }

    private fun loadTodo(id: Long) {
        launchWithLoading(
            setLoading = { loading -> updateState { it.copy(isLoading = loading) } }
        ) {
            val todo = getTodoByIdUseCase(id)
            updateState { it.copy(todo = todo) }
        }
    }

    private fun toggleTodo(id: Long) {
        launchWithLoading(setLoading = {}) {
            toggleTodoUseCase(id)
            // 상태 반영을 위해 다시 로드
            val updated = getTodoByIdUseCase(id)
            updateState { it.copy(todo = updated) }
            sendEffect(TodoDetailUiEffect.ShowSnackbar(UiText.DynamicString("완료 상태가 변경되었습니다.")))
        }
    }

    private fun deleteTodo(id: Long) {
        launchWithLoading(
            setLoading = { loading -> updateState { it.copy(isLoading = loading) } }
        ) {
            deleteTodoUseCase(id)
            sendEffect(TodoDetailUiEffect.ShowSnackbar(UiText.DynamicString("할 일이 삭제되었습니다.")))
            sendEffect(TodoDetailUiEffect.NavigateBack)
        }
    }
}
