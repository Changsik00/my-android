package com.example.myapplication.data.remote.interceptor

import com.example.myapplication.BuildConfig
import com.example.myapplication.data.remote.api.ApiEndpoints
import okhttp3.Interceptor
import okhttp3.Response

/**
 * 모든 날씨 API 요청에 공통으로 필요한 쿼리파라미터(AppId, 단위, 언어)를 
 * 통신 직전에 강제 삽입해 주는 인터셉터입니다.
 */
class WeatherAuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val originalUrl = originalRequest.url

        // 기존 URL에 공통 파라미터를 추가하여 새 URL 생성
        val newUrl = originalUrl.newBuilder()
            .addQueryParameter(ApiEndpoints.QueryParam.API_KEY, BuildConfig.WEATHER_API_KEY)
            .addQueryParameter(ApiEndpoints.QueryParam.UNITS, ApiEndpoints.Defaults.UNITS_METRIC)
            .addQueryParameter(ApiEndpoints.QueryParam.LANGUAGE, ApiEndpoints.Defaults.LANG_KR)
            .build()

        // 새 URL을 담은 Request로 교체 후 통신 진행
        val newRequest = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }
}
