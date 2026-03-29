package com.example.myapplication.data.remote.api

/**
 * API 호출 상수 관리 객체
 */
object ApiEndpoints {
    const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    const val CURRENT_WEATHER = "weather"
    const val FORECAST = "forecast"

    object QueryParam {
        const val API_KEY = "appid"
        const val CITY = "q"
        const val UNITS = "units"
        const val LANGUAGE = "lang"
    }

    object Defaults {
        const val UNITS_METRIC = "metric"
        const val LANG_KR = "kr"
    }
}
