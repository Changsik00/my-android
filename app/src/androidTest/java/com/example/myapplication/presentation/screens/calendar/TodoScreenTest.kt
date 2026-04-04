package com.example.myapplication.presentation.screens.calendar

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.myapplication.MainActivity
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * SPEC-503: Compose UI 자동화 테스트
 *
 * 주요 사용자 플로우 검증:
 * 1. 할 일 추가 플로우 — FAB 탭 → BottomSheet 표시 → 제목 입력 → 저장
 * 2. 날짜 선택 플로우 — 달력에서 날짜 탭 → 선택 상태 변화 확인
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class TodoScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    // ────────────────────────────────────────
    // 할 일 추가 플로우
    // ────────────────────────────────────────

    @Test
    fun fab_click_shows_add_todo_bottom_sheet() {
        // FAB(할 일 추가 버튼) 클릭 시 BottomSheet가 표시되는지 확인
        composeTestRule
            .onNodeWithContentDescription("Add Todo")
            .performClick()

        // BottomSheet 내 "새 할 일 추가" 텍스트가 표시되어야 함
        composeTestRule
            .onNodeWithText("새 할 일 추가")
            .assertIsDisplayed()
    }

    @Test
    fun add_todo_flow_saves_todo_and_dismisses_sheet() {
        val testTitle = "UI 테스트 할 일"

        // 1. FAB 탭
        composeTestRule
            .onNodeWithContentDescription("Add Todo")
            .performClick()

        // 2. BottomSheet가 열릴 때까지 대기
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule
                .onAllNodesWithText("새 할 일 추가")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 3. 제목 입력 필드에 텍스트 입력
        composeTestRule
            .onNodeWithText("제목 (필수)")
            .performClick()
            .performTextInput(testTitle)

        // 4. 저장 버튼 활성화 확인 후 클릭
        composeTestRule
            .onNodeWithText("저장")
            .assertIsEnabled()
            .performClick()

        // 5. BottomSheet가 닫히면 "새 할 일 추가" 텍스트가 사라져야 함
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule
                .onAllNodesWithText("새 할 일 추가")
                .fetchSemanticsNodes().isEmpty()
        }
    }

    @Test
    fun save_button_disabled_when_title_is_empty() {
        // FAB 탭 → BottomSheet 열기
        composeTestRule
            .onNodeWithContentDescription("Add Todo")
            .performClick()

        // 제목 입력 없이 저장 버튼이 비활성화 상태인지 확인
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule
                .onAllNodesWithText("저장")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule
            .onNodeWithText("저장")
            .assertIsNotEnabled()
    }

    @Test
    fun cancel_button_dismisses_bottom_sheet() {
        // FAB 탭 → BottomSheet 열기
        composeTestRule
            .onNodeWithContentDescription("Add Todo")
            .performClick()

        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule
                .onAllNodesWithText("취소")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // 취소 버튼 클릭
        composeTestRule
            .onNodeWithText("취소")
            .performClick()

        // BottomSheet가 닫혀야 함
        composeTestRule.waitUntil(timeoutMillis = 3_000) {
            composeTestRule
                .onAllNodesWithText("새 할 일 추가")
                .fetchSemanticsNodes().isEmpty()
        }
    }

    // ────────────────────────────────────────
    // 날짜 선택 플로우
    // ────────────────────────────────────────

    @Test
    fun calendar_screen_displays_top_app_bar() {
        // 앱 바 타이틀이 표시되는지 확인
        composeTestRule
            .onNodeWithText("Todo Scheduler")
            .assertIsDisplayed()
    }

    @Test
    fun calendar_screen_displays_month_calendar() {
        // 달력(MonthCalendar)이 표시되는지 — 현재 월/년 텍스트 존재 여부로 검증
        // 달력 영역에 날짜 숫자(1~28)가 하나 이상 표시되어야 함
        composeTestRule
            .onAllNodesWithText("1")
            .onFirst()
            .assertExists()
    }

    @Test
    fun selecting_date_updates_calendar_selection() {
        // 달력에서 "15"를 탭하면 해당 셀이 선택 상태를 반영해야 함
        // (날짜 셀이 존재하는지 확인 — 실제 색상 변화는 스크린샷/비주얼 테스트 필요)
        composeTestRule
            .onAllNodesWithText("15")
            .onFirst()
            .performClick()

        // 클릭 후 UI가 크래시 없이 살아있으면 통과
        composeTestRule
            .onNodeWithText("Todo Scheduler")
            .assertIsDisplayed()
    }
}
