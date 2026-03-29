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

@Database(
    entities = [TodoEntity::class],
    views = [TodoSummaryView::class],   // SPEC-605: DatabaseView 등록
    version = 2,                         // SPEC-605: v1 → v2 (MIGRATION_1_2)
    exportSchema = false
)
@TypeConverters(LocalDateConverter::class, TodoPriorityConverter::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun todoSummaryDao(): TodoSummaryDao  // SPEC-605: Summary DAO 추가
}
