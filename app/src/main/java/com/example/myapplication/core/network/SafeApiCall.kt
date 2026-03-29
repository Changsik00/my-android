package com.example.myapplication.core.network

import retrofit2.HttpException
import java.io.IOException

/**
 * SPEC-702: Retrofit API 호출 시 발생하는 각종 예외를 
 * 안전하게 캐치하여 [NetworkResult]로 변환해주는 래퍼 함수입니다.
 */
suspend fun <T> safeApiCall(apiCall: suspend () -> T): NetworkResult<T> {
    return try {
        val result = apiCall()
        NetworkResult.Success(result)
    } catch (e: HttpException) {
        val errorMessage = "서버 통신 오류 (HTTP ${e.code()})"
        ApiErrorReporter.report(e, errorMessage)
        NetworkResult.Error(code = e.code(), message = errorMessage, exception = e)
    } catch (e: IOException) {
        val errorMessage = "인터넷 접속 범위를 벗어났거나 응답이 없습니다"
        ApiErrorReporter.report(e, errorMessage)
        NetworkResult.Error(code = null, message = errorMessage, exception = e)
    } catch (e: Exception) {
        val errorMessage = e.localizedMessage ?: "알 수 없는 오류가 발생했습니다"
        ApiErrorReporter.report(e, errorMessage)
        NetworkResult.Error(code = null, message = errorMessage, exception = e)
    }
}
