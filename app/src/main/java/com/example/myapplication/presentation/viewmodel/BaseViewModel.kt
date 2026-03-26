package com.example.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

abstract class BaseViewModel<Event, State, Effect>(
    initialState: State
) : ViewModel() {

    protected val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    protected val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    abstract fun onEvent(event: Event)

    protected fun updateState(block: (State) -> State) {
        _uiState.update(block)
    }

    protected fun sendEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.emit(effect)
        }
    }

    protected fun launchWithLoading(
        setLoading: (Boolean) -> Unit,
        block: suspend () -> Unit
    ) {
        viewModelScope.launch {
            try {
                setLoading(true)
                block()
            } catch (e: Exception) {
                AppErrorBus.emit(AppError.UnknownError(e.message ?: "오류가 발생했습니다"))
            } finally {
                setLoading(false)
            }
        }
    }
}
