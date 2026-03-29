package com.example.myapplication.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * SPEC-701: 현재 날씨 API 응답 DTO
 * OpenWeatherMap /data/2.5/weather
 */
@Serializable
data class WeatherResponseDto(
    val weather: List<WeatherDto>,
    val main: MainDto,
    val name: String,   // 도시 이름
    val dt: Long        // Unix timestamp
)

@Serializable
data class WeatherDto(
    val id: Int,
    val main: String,         // "Clear", "Rain", "Clouds" ...
    val description: String,
    val icon: String          // "01d", "02n" ...
)

@Serializable
data class MainDto(
    val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    @SerialName("temp_min") val tempMin: Double,
    @SerialName("temp_max") val tempMax: Double,
    val humidity: Int
)
