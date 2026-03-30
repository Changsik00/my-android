package com.example.myapplication.data.repository

import com.example.myapplication.core.network.NetworkResult
import com.example.myapplication.data.db.WeatherCacheDao
import com.example.myapplication.data.local.entity.WeatherCacheEntity
import com.example.myapplication.data.remote.source.WeatherRemoteDataSource
import com.example.myapplication.domain.model.WeatherInfo
import com.example.myapplication.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.time.LocalDate
import javax.inject.Inject

/**
 * SPEC-703: Offline-First 캐싱 로직이 적용된 통신 레포지토리
 */
class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: WeatherRemoteDataSource,
    private val cacheDao: WeatherCacheDao
) : WeatherRepository {

    override fun getWeather(date: LocalDate, city: String): Flow<WeatherInfo?> = flow {
        val dateKey = date.toString()
        
        // 1. 로컬 캐시 조회 및 즉시 방출
        val cachedEntity = cacheDao.getWeatherCache(dateKey)
        if (cachedEntity != null) {
            emit(cachedEntity.toDomain(date))
        }

        // 2. 만료 시간 체크 (1시간 = 3,600,000 ms)
        val isCacheValid = cachedEntity != null && 
            (System.currentTimeMillis() - cachedEntity.cachedAt) < 3_600_000L

        // 3. 만료되었거나 캐시가 아예 없으면 API 요청
        if (!isCacheValid) {
            val result = remoteDataSource.getCurrentWeather(city)
            
            if (result is NetworkResult.Success) {
                // 성공 시 DB 업데이트 및 최신 값 방출
                val newEntity = result.data.toEntity(dateKey)
                cacheDao.upsert(newEntity)
                emit(result.data)
            } else if (result is NetworkResult.Error) {
                // 백그라운드 갱신 실패: 만약 캐시가 아예 없었을 경우 null을 한 번 쏴줄 수 있으나,
                // 여기선 기존 캐시가 없을 때만 조용히 넘어가거나 추가 처리 가능
                if (cachedEntity == null) {
                    emit(null)
                }
            }
        }
    }

    private fun WeatherCacheEntity.toDomain(date: LocalDate): WeatherInfo {
        return WeatherInfo(
            date = date,
            condition = condition,
            tempCelsius = tempCelsius,
            minTemp = minTemp,
            maxTemp = maxTemp,
            iconCode = iconCode
        )
    }

    private fun WeatherInfo.toEntity(dateKey: String): WeatherCacheEntity {
        return WeatherCacheEntity(
            dateKey = dateKey,
            condition = condition,
            tempCelsius = tempCelsius,
            minTemp = minTemp,
            maxTemp = maxTemp,
            iconCode = iconCode,
            cachedAt = System.currentTimeMillis()
        )
    }
}
