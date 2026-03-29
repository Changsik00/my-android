package com.example.myapplication.core.network

import android.util.Log

/**
 * 에러 리포터 유틸리티
 * 향후 Firebase Crashlytics 등으로 전송할 때 여기서 공통 처리합니다.
 */
object ApiErrorReporter {
    private const val TAG = "NetworkError"

    fun report(exception: Throwable?, customMessage: String) {
        // 현재는 Logcat 에 출력
        Log.e(TAG, "[$customMessage] - ${exception?.message}", exception)
        
        // TODO: Crashlytics.logException(exception) 등 추가 가능
    }
}
