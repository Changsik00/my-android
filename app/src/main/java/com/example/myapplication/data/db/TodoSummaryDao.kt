package com.example.myapplication.data.db

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * SPEC-605: TodoSummaryDao — DatabaseView 쿼리 인터페이스.
 * 특정 월의 모든 날짜 통계를 Flow로 반환.
 */
@Dao
interface TodoSummaryDao {

    /**
     * 특정 월의 날짜별 Todo 통계 조회.
     * KSP 기간에 [startEpoch, endEpoch) 범위로 필터링.
     */
    @Query(
        """
        SELECT * FROM todo_summary_view
        WHERE date >= :startEpoch AND date < :endEpoch
        """
    )
    fun getSummaryForMonth(startEpoch: Long, endEpoch: Long): Flow<List<TodoSummaryView>>
}
