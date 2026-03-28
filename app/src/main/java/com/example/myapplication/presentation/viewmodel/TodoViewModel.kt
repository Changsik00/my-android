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
) : BaseViewModel<TodoUiEvent, TodoUiState, TodoUiEffect>(TodoUiState()) {

    // 별도의 date flow를 유지하여 flatMapLatest로 연결 (Reactive Architecture)
    private val dateFlow = MutableStateFlow(LocalDate.now())

    init {
        observeTodos()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeTodos() {
        dateFlow
            .flatMapLatest { date ->
                // SPEC-604: 기존 목록이 있으면 isRefreshing(오버레이), 없으면 isLoading(전체화면)
                val hasTodos = uiState.value.todos.isNotEmpty()
                if (hasTodos) {
                    updateState { it.copy(isRefreshing = true, error = null) }
                } else {
                    updateState { it.copy(isLoading = true, error = null) }
                }
                getTodosForDateUseCase(date)
                    .map { todos -> Result.success(todos) }
                    .catch { emit(Result.failure(it)) }
            }
            .onEach { result ->
                result.onSuccess { todos ->
                    updateState { it.copy(todos = todos, isLoading = false, isRefreshing = false) }
                }.onFailure { exception ->
                    updateState { it.copy(error = exception.message ?: "Unknown error", isLoading = false, isRefreshing = false) }
                    sendEffect(TodoUiEffect.ShowSnackbar(UiText.DynamicString("데이터를 불러오지 못했습니다.")))
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: TodoUiEvent) {
        when (event) {
            is TodoUiEvent.SelectDate -> {
                updateState { it.copy(selectedDate = event.date) }
                dateFlow.value = event.date
            }
            is TodoUiEvent.AddTodo -> {
                launchWithLoading(
                    setLoading = { loading -> updateState { it.copy(isLoading = loading) } }
                ) {
                    addTodoUseCase(
                        title = event.title,
                        description = event.description,
                        date = uiState.value.selectedDate,
                        priority = event.priority
                    )
                    sendEffect(TodoUiEffect.ShowSnackbar(UiText.DynamicString("할 일이 추가되었습니다.")))
                }
            }
            is TodoUiEvent.ToggleTodo -> {
                launchWithLoading(
                    setLoading = { /* Toggle doesn't necessarily need a global loading spinner */ }
                ) {
                    toggleTodoUseCase(event.id)
                }
            }
            is TodoUiEvent.ToggleTodoCompletion -> {
                launchWithLoading(setLoading = {}) {
                    toggleTodoUseCase(event.id)
                }
            }
            is TodoUiEvent.DeleteTodo -> {
                launchWithLoading(
                    setLoading = { loading -> updateState { it.copy(isLoading = loading) } }
                ) {
                    deleteTodoUseCase(event.id)
                    sendEffect(TodoUiEffect.ShowSnackbar(UiText.DynamicString("할 일이 삭제되었습니다.")))
                }
            }
            is TodoUiEvent.RefreshList -> {
                // SPEC-603: Detail Activity 복귀 시 현재 날짜의 Todo 목록 갱신
                // dateFlow에 동일 값을 emit하면 flatMapLatest가 재실행됨
                dateFlow.value = uiState.value.selectedDate
            }
        }
    }
}
