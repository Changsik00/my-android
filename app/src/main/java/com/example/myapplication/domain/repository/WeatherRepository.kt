package com.example.myapplication.domain.repository

import com.example.myapplication.domain.model.WeatherInfo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * SPEC-703: 날씨 정보 제공을 위한 Repository 인터페이스
 */
interface WeatherRepository {
    /**
     * 특정 날짜/도시에 대한 날씨 반환 Flow
     */
    fun getWeather(date: LocalDate, city: String): Flow<WeatherInfo?>
}
