package com.example.myapplication.domain.model

import java.time.LocalDate

/**
 * SPEC-702, 703: UI에서 필요한 순수 날씨 상태 모델
 * 불필요한 서버 응답 필드 없이, 앱 비즈니스 로직에 필요한 속성만 가집니다.
 */
data class WeatherInfo(
    val date: LocalDate,
    val condition: String,    // "맑음", "흐림" 등
    val tempCelsius: Float,
    val minTemp: Float,
    val maxTemp: Float,
    val iconCode: String      // OpenWeatherMap 아이콘 상태값 ("01d")
) {
    // UI 표시용 Emoji (가짜 확장 프로퍼티 예제)
    val iconEmoji: String
        get() = when {
            iconCode.contains("01") -> "☀️"
            iconCode.contains("02") || iconCode.contains("03") || iconCode.contains("04") -> "☁️"
            iconCode.contains("09") || iconCode.contains("10") -> "🌧️"
            iconCode.contains("11") -> "⛈️"
            iconCode.contains("13") -> "❄️"
            iconCode.contains("50") -> "🌫️"
            else -> "🌈"
        }
}
