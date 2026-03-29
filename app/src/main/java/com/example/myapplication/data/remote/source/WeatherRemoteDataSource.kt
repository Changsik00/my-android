package com.example.myapplication.data.remote.source

import com.example.myapplication.core.network.NetworkResult
import com.example.myapplication.core.network.safeApiCall
import com.example.myapplication.data.remote.api.WeatherApi
import com.example.myapplication.domain.model.WeatherInfo
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

/**
 * 외부(Retrofit) 의존성을 숨기고, API 원본 DTO 응답 중 
 * 앱 구동(Domain)에 필요한 정보만 파싱하여 넘겨주는 DataSource 계층입니다.
 */
interface WeatherRemoteDataSource {
    suspend fun getCurrentWeather(city: String): NetworkResult<WeatherInfo>
}

class WeatherRemoteDataSourceImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRemoteDataSource {

    override suspend fun getCurrentWeather(city: String): NetworkResult<WeatherInfo> {
        return safeApiCall {
            val response = api.getCurrentWeather(city)
            
            // DTO -> Domain Model 매핑 로직 (Data 필터링)
            WeatherInfo(
                date = Instant.ofEpochSecond(response.dt)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate(),
                condition = response.weather.firstOrNull()?.description ?: "알 수 없음",
                tempCelsius = response.main.temp.toFloat(),
                minTemp = response.main.tempMin.toFloat(),
                maxTemp = response.main.tempMax.toFloat(),
                iconCode = response.weather.firstOrNull()?.icon ?: "01d"
            )
        }
    }
}
