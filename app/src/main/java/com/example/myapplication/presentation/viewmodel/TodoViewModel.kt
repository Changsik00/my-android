package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.*
import com.example.myapplication.presentation.model.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val getTodosForDateUseCase: GetTodosForDateUseCase,
    private val addTodoUseCase: AddTodoUseCase,
    private val toggleTodoUseCase: ToggleTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<TodoUiEffect>()
    val effect: SharedFlow<TodoUiEffect> = _effect.asSharedFlow()

    // 별도의 date flow를 유지하여 flatMapLatest로 연결 (Reactive Architecture)
    private val dateFlow = MutableStateFlow(LocalDate.now())

    init {
        observeTodos()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTodos() {
        dateFlow
            .flatMapLatest { date ->
                _uiState.update { it.copy(isLoading = true, error = null) }
                getTodosForDateUseCase(date)
                    .map { todos -> Result.success(todos) }
                    .catch { emit(Result.failure(it)) }
            }
            .onEach { result ->
                result.onSuccess { todos ->
                    _uiState.update { it.copy(todos = todos, isLoading = false) }
                }.onFailure { exception ->
                    _uiState.update { it.copy(error = exception.message ?: "Unknown error", isLoading = false) }
                    _effect.emit(TodoUiEffect.ShowSnackbar(UiText.DynamicString("데이터를 불러오지 못했습니다.")))
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: TodoUiEvent) {
        when (event) {
            is TodoUiEvent.SelectDate -> {
                _uiState.update { it.copy(selectedDate = event.date) }
                dateFlow.value = event.date
            }
            is TodoUiEvent.AddTodo -> {
                viewModelScope.launch {
                    try {
                        addTodoUseCase(
                            title = event.title,
                            description = event.description,
                            date = uiState.value.selectedDate,
                            priority = event.priority
                        )
                        _effect.emit(TodoUiEffect.ShowSnackbar(UiText.DynamicString("할 일이 추가되었습니다.")))
                    } catch (e: Exception) {
                        _effect.emit(TodoUiEffect.ShowSnackbar(UiText.DynamicString("할 일 추가 실패: ${e.message}")))
                    }
                }
            }
            is TodoUiEvent.ToggleTodo -> {
                viewModelScope.launch {
                    try {
                        toggleTodoUseCase(event.id)
                    } catch (e: Exception) {
                        _effect.emit(TodoUiEffect.ShowSnackbar(UiText.DynamicString("상태 변경 실패")))
                    }
                }
            }
            is TodoUiEvent.ToggleTodoCompletion -> {
                viewModelScope.launch {
                    try {
                        toggleTodoUseCase(event.id)
                    } catch (e: Exception) {
                        _effect.emit(TodoUiEffect.ShowSnackbar(UiText.DynamicString("상태 변경 실패")))
                    }
                }
            }
            is TodoUiEvent.DeleteTodo -> {
                viewModelScope.launch {
                    try {
                        deleteTodoUseCase(event.id)
                        _effect.emit(TodoUiEffect.ShowSnackbar(UiText.DynamicString("할 일이 삭제되었습니다.")))
                    } catch (e: Exception) {
                        _effect.emit(TodoUiEffect.ShowSnackbar(UiText.DynamicString("할 일 삭제 실패")))
                    }
                }
            }
        }
    }
}
