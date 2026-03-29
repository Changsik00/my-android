package com.example.myapplication.core.network

/**
 * SPEC-702: 네트워크 통신 결과를 래핑하는 Sealed Class
 */
sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val code: Int? = null, val message: String, val exception: Throwable? = null) : NetworkResult<Nothing>()
    data object Loading : NetworkResult<Nothing>()
}
