package com.example.myapplication.domain.repository

import com.example.myapplication.presentation.viewmodel.AppError
import com.example.myapplication.presentation.viewmodel.AppErrorBus
import kotlinx.coroutines.flow.*

abstract class BaseRepository {

    protected fun <T> safeFlow(block: suspend () -> Flow<T>): Flow<T> =
        flow {
            emitAll(block())
        }.catch { e ->
            AppErrorBus.emit(AppError.DatabaseError(e.message ?: "DB 오류"))
        }

    protected suspend fun <T> safeCall(block: suspend () -> T): T? {
        return try {
            block()
        } catch (e: Exception) {
            AppErrorBus.emit(AppError.DatabaseError(e.message ?: "DB 오류"))
            null
        }
    }
}
