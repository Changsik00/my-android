package com.example.myapplication.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * SPEC-703: API 호출 결과를 로컬에 임시 저장하는 캐시 엔티티.
 */
@Entity(tableName = "weather_cache")
data class WeatherCacheEntity(
    @PrimaryKey
    val dateKey: String,        // 조회 대상 날짜 문자열, ex: "2024-03-30"
    val condition: String,
    val tempCelsius: Float,
    val minTemp: Float,
    val maxTemp: Float,
    val iconCode: String,
    val cachedAt: Long = System.currentTimeMillis()   // 만료(1시간) 체크용 타임스탬프
)
