package com.example.myapplication.domain.model

/**
 * SPEC-605: TodoSummary — DatabaseView에서 매핑되는 Domain 모델.
 * 달력 날짜 셀의 인디케이터 뱃지 표시에 사용.
 */
data class TodoSummary(
    val totalCount: Int,
    val completedCount: Int
)
