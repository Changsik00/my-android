package com.example.myapplication.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * SPEC-701: 5일 예보 API 응답 DTO
 * OpenWeatherMap /data/2.5/forecast (3시간 간격)
 */
@Serializable
data class ForecastResponseDto(
    val list: List<ForecastItemDto>,
    val city: CityDto
)

@Serializable
data class ForecastItemDto(
    val dt: Long,
    val main: MainDto,
    val weather: List<WeatherDto>,
    @SerialName("dt_txt") val dtTxt: String   // "2024-03-21 12:00:00"
)

@Serializable
data class CityDto(
    val id: Int,
    val name: String,
    val country: String
)
