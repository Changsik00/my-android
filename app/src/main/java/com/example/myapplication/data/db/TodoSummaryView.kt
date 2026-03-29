package com.example.myapplication.data.db

import androidx.room.DatabaseView

/**
 * SPEC-605: DatabaseView — 날짜별 Todo 통계를 미리 집계하는 읽기 전용 뷰.
 * date는 LocalDate → Long(epochDay) 변환된 값으로 저장됨.
 */
@DatabaseView(
    value = """
        SELECT date, 
               COUNT(*) AS totalCount,
               SUM(CASE WHEN isCompleted = 1 THEN 1 ELSE 0 END) AS completedCount
        FROM todos
        GROUP BY date
    """,
    viewName = "todo_summary_view"
)
data class TodoSummaryView(
    val date: Long,           // LocalDate.toEpochDay() 값
    val totalCount: Int,
    val completedCount: Int
)
