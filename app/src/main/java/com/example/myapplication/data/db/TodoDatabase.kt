package com.example.myapplication.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.myapplication.data.db.converter.LocalDateConverter
import com.example.myapplication.data.db.converter.TodoPriorityConverter

/**
 * SPEC-605: MIGRATION_1_2 예제
 * priority 컬럼 추가 (이미 Entity에 존재하므로 신규 설치 시에는 자동 생성,
 * 기존 DB(v1) 업그레이드 시에는 이 Migration이 실행됨)
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // priority INTEGER: 0=LOW, 1=MEDIUM, 2=HIGH (TodoPriority ordinal)
        database.execSQL(
            "ALTER TABLE todos ADD COLUMN priority INTEGER NOT NULL DEFAULT 0"
        )
    }
}

/**
 * SPEC-703: MIGRATION_2_3
 * 날씨 캐싱용 테이블 생성
 */
val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "CREATE TABLE IF NOT EXISTS `weather_cache` (`dateKey` TEXT NOT NULL, `condition` TEXT NOT NULL, `tempCelsius` REAL NOT NULL, `minTemp` REAL NOT NULL, `maxTemp` REAL NOT NULL, `iconCode` TEXT NOT NULL, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`dateKey`))"
        )
    }
}

@Database(
    entities = [TodoEntity::class, com.example.myapplication.data.local.entity.WeatherCacheEntity::class],
    views = [TodoSummaryView::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(LocalDateConverter::class, TodoPriorityConverter::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun todoSummaryDao(): TodoSummaryDao
    abstract fun weatherCacheDao(): WeatherCacheDao // SPEC-703
}
