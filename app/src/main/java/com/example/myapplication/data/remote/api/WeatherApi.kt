package com.example.myapplication.data.remote.api

import com.example.myapplication.data.remote.dto.ForecastResponseDto
import com.example.myapplication.data.remote.dto.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * SPEC-701, 702: WeatherApi — Retrofit 인터페이스
 * 엔드포인트는 ApiEndpoints 로 관리되며,
 * 공통 QueryParameter (appid, units, lang)는 WeatherAuthInterceptor 에서 일괄 주입됩니다.
 */
interface WeatherApi {

    /**
     * 현재 날씨 조회 (당일)
     */
    @GET(ApiEndpoints.CURRENT_WEATHER)
    suspend fun getCurrentWeather(
        @Query(ApiEndpoints.QueryParam.CITY) city: String
    ): WeatherResponseDto

    /**
     * 5일 예보 조회 (3시간 간격, 미래 날짜용)
     */
    @GET(ApiEndpoints.FORECAST)
    suspend fun getWeatherForecast(
        @Query(ApiEndpoints.QueryParam.CITY) city: String
    ): ForecastResponseDto
}
