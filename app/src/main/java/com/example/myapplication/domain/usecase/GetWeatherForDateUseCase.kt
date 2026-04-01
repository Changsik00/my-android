package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.WeatherInfo
import com.example.myapplication.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Inject

class GetWeatherForDateUseCase @Inject constructor(
    private val weatherRepository: WeatherRepository
) {
    /**
     * 특정 날짜에 대한 날씨 정보를 가져옵니다.
     * @param date 날씨를 조회할 기준 날짜
     * @param city 조회할 도시 이름 (기본값: "Seoul")
     * @return 날씨 정보 Flow
     */
    operator fun invoke(date: LocalDate, city: String = "Seoul"): Flow<WeatherInfo?> {
        return weatherRepository.getWeather(date, city)
    }
}
