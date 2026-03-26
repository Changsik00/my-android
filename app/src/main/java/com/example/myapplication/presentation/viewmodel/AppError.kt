package com.example.myapplication.presentation.viewmodel

import com.example.myapplication.presentation.model.UiText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

sealed class AppError {
    data class NetworkError(val message: String) : AppError()
    data class DatabaseError(val message: String) : AppError()
    data class UnknownError(val message: String) : AppError()

    fun toUiText(): UiText {
        val s = when (this) {
            is NetworkError -> "네트워크 오류: $message"
            is DatabaseError -> "저장 오류: $message"
            is UnknownError -> message
        }
        return UiText.DynamicString(s)
    }
}

object AppErrorBus {
    private val _errors = MutableSharedFlow<AppError>()
    val errors: SharedFlow<AppError> = _errors.asSharedFlow()

    suspend fun emit(error: AppError) {
        _errors.emit(error)
    }
}
