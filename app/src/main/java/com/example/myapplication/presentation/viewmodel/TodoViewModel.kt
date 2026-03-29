package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.*
import com.example.myapplication.presentation.model.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val getTodosForDateUseCase: GetTodosForDateUseCase,
    private val addTodoUseCase: AddTodoUseCase,
    private val toggleTodoUseCase: ToggleTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
    private val getTodoSummaryForMonthUseCase: GetTodoSummaryForMonthUseCase  // SPEC-605
) : BaseViewModel<TodoUiEvent, TodoUiState, TodoUiEffect>(TodoUiState()) {

    private val dateFlow = MutableStateFlow(LocalDate.now())

    // SPEC-605: 현재 표시 중인 월 Flow (달력 월 이동 시 통계도 갱신)
    private val monthFlow = MutableStateFlow(YearMonth.now())

    init {
        observeTodos()
        observeSummary()  // SPEC-605
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

    // SPEC-605: 월별 Summary Flow 구독
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSummary() {
        monthFlow
            .flatMapLatest { yearMonth ->
                getTodoSummaryForMonthUseCase(yearMonth)
            }
            .onEach { summaries ->
                updateState { it.copy(todoSummaries = summaries) }
            }
            .catch { /* 통계 오류는 UI를 막지 않음 */ }
            .launchIn(viewModelScope)
    }

    override fun onEvent(event: TodoUiEvent) {
        when (event) {
            is TodoUiEvent.SelectDate -> {
                updateState { it.copy(selectedDate = event.date) }
                dateFlow.value = event.date
                // 날짜가 속한 월로 통계도 갱신
                monthFlow.value = YearMonth.from(event.date)
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
                dateFlow.value = uiState.value.selectedDate
            }
        }
    }
}
