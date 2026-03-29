package com.example.myapplication.data.remote.api

import com.example.myapplication.BuildConfig
import com.example.myapplication.data.remote.dto.ForecastResponseDto
import com.example.myapplication.data.remote.dto.WeatherResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * SPEC-701: WeatherApi — Retrofit 인터페이스
 * Base URL: https://api.openweathermap.org/data/2.5/
 */
interface WeatherApi {

    /**
     * 현재 날씨 조회 (당일)
     */
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String = BuildConfig.WEATHER_API_KEY,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "kr"
    ): WeatherResponseDto

    /**
     * 5일 예보 조회 (3시간 간격, 미래 날짜용)
     */
    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String = BuildConfig.WEATHER_API_KEY,
        @Query("units") units: String = "metric",
        @Query("lang") lang: String = "kr"
    ): ForecastResponseDto
}
