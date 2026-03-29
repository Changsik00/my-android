package com.example.myapplication.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.myapplication.data.local.entity.WeatherCacheEntity

@Dao
interface WeatherCacheDao {

    /**
     * 캐시 데이터 갱신 (기존에 있으면 덮어쓰기)
     */
    @Upsert
    suspend fun upsert(weatherCache: WeatherCacheEntity)

    /**
     * 특정 날짜의 캐시 데이터 조회
     */
    @Query("SELECT * FROM weather_cache WHERE dateKey = :dateKey LIMIT 1")
    suspend fun getWeatherCache(dateKey: String): WeatherCacheEntity?
}
