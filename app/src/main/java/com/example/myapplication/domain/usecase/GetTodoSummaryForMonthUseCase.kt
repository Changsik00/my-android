package com.example.myapplication.domain.usecase

import com.example.myapplication.domain.model.TodoSummary
import com.example.myapplication.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

/**
 * SPEC-605: GetTodoSummaryForMonthUseCase
 * 특정 월의 날짜별 Todo 완료/전체 통계를 Flow로 반환.
 * CalendarScreen의 날짜 셀 인디케이터 뱃지에 사용.
 */
class GetTodoSummaryForMonthUseCase @Inject constructor(
    private val repository: TodoRepository
) {
    operator fun invoke(yearMonth: YearMonth): Flow<Map<LocalDate, TodoSummary>> {
        return repository.getTodoSummaryForMonth(yearMonth)
    }
}
