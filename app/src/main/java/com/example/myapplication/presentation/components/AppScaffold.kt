package com.example.myapplication.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.myapplication.presentation.viewmodel.AppError
import com.example.myapplication.presentation.viewmodel.AppErrorBus
import kotlinx.coroutines.launch

/**
 * SPEC-602: 전역 에러 핸들러
 *
 * AppErrorBus의 에러 흐름을 구독하여 어느 레이어에서 발생한 에러도
 * 이 컴포저블의 Snackbar를 통해 사용자에게 표시합니다.
 *
 * CalendarScreen이 자체 Scaffold를 가지므로, AppScaffold는 Scaffold 없이
 * SnackbarHost만 오버레이 방식으로 표시합니다.
 */
@Composable
fun AppScaffold(
    content: @Composable () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 전역 에러 수집 — AppErrorBus를 구독하여 Snackbar로 표시
    LaunchedEffect(Unit) {
        AppErrorBus.errors.collect { error ->
            val message = when (error) {
                is AppError.NetworkError -> "네트워크 오류: ${error.message}"
                is AppError.DatabaseError -> "저장 오류: ${error.message}"
                is AppError.UnknownError -> error.message
            }
            scope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 하위 화면 콘텐츠 (각 화면이 자체 Scaffold를 가짐)
        content()

        // 전역 Snackbar — 화면 최상단에 오버레이로 표시
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(snackbarData = data)
        }
    }
}
