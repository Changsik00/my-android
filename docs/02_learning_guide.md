# 단계별 학습 가이드 — TodoScheduler로 현대 Android 배우기

> 각 Phase는 독립적으로 실행 가능하며, 코드를 직접 짜면서 배웁니다.
> 각 Phase가 끝나면 앱이 실제로 동작해야 합니다.

---

## Phase 0 — 환경 이해 (현재 위치)

**목표:** 기존 코드가 뭘 하는지 이해하고, 새 기술 스택을 준비한다.

**배우는 것:**
- Jetpack Compose 기본 구조 (`MainActivity`, `setContent`, `@Composable`)
- `build.gradle.kts` + `libs.versions.toml` Version Catalog 구조
- `@Preview` 어노테이션 활용

**체크포인트:**
- [ ] 현재 `MainActivity.kt`를 열어 `setContent { }` 블록을 분석한다
- [ ] `gradle/libs.versions.toml`을 열어 라이브러리 버전 관리 방식을 이해한다
- [ ] `@Preview` 어노테이션이 달린 Composable을 Android Studio에서 미리 본다

---

## Phase 1 — 데이터 레이어 (Room + Repository + Flow)

**목표:** 앱의 데이터 기반을 만든다. UI 없이 DB만 동작하면 성공.

**배우는 것:**
- Room Entity, Dao, Database 설정
- TypeConverter (LocalDate ↔ Long 변환)
- Flow를 반환하는 Dao 쿼리
- Repository 패턴 (인터페이스 + 구현체 분리)
- Hilt 기본 설정 (`@HiltAndroidApp`, `@Module`, `@Provides`)

**핵심 개념:**
```
Room Dao → Flow<List<Todo>> → Repository → ViewModel
         ↑
       DB 변경 시 자동으로 새 값 emit (리액티브 DB!)
```

**체크포인트:**
- [ ] `TodoEntity` 생성 후 `Room` DB 컴파일 성공
- [ ] `Flow<List<TodoEntity>>` 반환하는 Dao 메서드 작성
- [ ] `TodoRepository` 인터페이스와 `TodoRepositoryImpl` 구현
- [ ] (선택) Unit Test: Room in-memory DB로 insert/query 테스트

---

## Phase 2 — 도메인 레이어 (UseCase + Model)

**목표:** 비즈니스 로직을 Domain 레이어에 격리한다.

**배우는 것:**
- Clean Architecture의 Domain 레이어 역할
- UseCase(= Interactor) 패턴
- Entity(DB) ↔ Domain Model 변환 (Mapper)

**핵심 개념:**
```kotlin
// UseCase는 단 하나의 책임만
class GetTodosForDateUseCase(private val repo: TodoRepository) {
    operator fun invoke(date: LocalDate): Flow<List<Todo>> = repo.getTodosForDate(date)
}
```

**체크포인트:**
- [ ] `Todo` Domain Model 정의 (Entity와 별도)
- [ ] `GetTodosForDateUseCase`, `AddTodoUseCase`, `ToggleTodoUseCase` 작성
- [ ] Mapper 함수 작성: `TodoEntity.toDomain()`, `Todo.toEntity()`

---

## Phase 3 — ViewModel (MVVM + MVI 하이브리드)

**목표:** UI 상태를 관리하는 ViewModel을 만든다. 이 앱의 핵심 아키텍처 포인트.

**배우는 것:**
- `StateFlow` vs `SharedFlow` 차이와 사용 시점
- `UiState` sealed class로 단일 상태 관리 (MVI)
- `UiEvent` sealed class로 Intent 처리 (MVI)
- `viewModelScope` + Coroutines
- Hilt `@HiltViewModel`

**핵심 구조:**
```kotlin
// UiState — MVI 방식의 단일 상태
data class TodoUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// UiEvent — 사용자 인텐트
sealed class TodoUiEvent {
    data class SelectDate(val date: LocalDate) : TodoUiEvent()
    data class AddTodo(val title: String) : TodoUiEvent()
    data class ToggleTodo(val id: Long) : TodoUiEvent()
    data class DeleteTodo(val id: Long) : TodoUiEvent()
}

// ViewModel — MVVM의 ViewModel + MVI의 이벤트 처리
class TodoViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TodoUiState())
    val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

    fun onEvent(event: TodoUiEvent) { /* 이벤트 처리 */ }
}
```

**체크포인트:**
- [ ] `TodoUiState`, `TodoUiEvent` 정의
- [ ] ViewModel에서 `StateFlow` 수집 및 상태 업데이트
- [ ] 날짜 변경 시 해당 날짜의 Todo 목록이 Flow로 자동 갱신

---

## Phase 4 — UI 레이어 (Jetpack Compose)

**목표:** Compose로 화면을 만들고 ViewModel과 연결한다.

**배우는 것:**
- `collectAsStateWithLifecycle()` — Flow를 Compose 상태로 수집
- Stateful vs Stateless Composable 분리
- `LazyColumn` — RecyclerView 대체
- `MaterialDatePicker` 또는 커스텀 달력 UI
- Compose Navigation 설정

**핵심 패턴 (Stateful/Stateless 분리):**
```kotlin
// Stateful — ViewModel 알고 있음 (화면 진입점)
@Composable
fun TodoScreenRoute(viewModel: TodoViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    TodoScreen(uiState = uiState, onEvent = viewModel::onEvent)
}

// Stateless — ViewModel 모름 (테스트/Preview 용이)
@Composable
fun TodoScreen(uiState: TodoUiState, onEvent: (TodoUiEvent) -> Unit) {
    // 순수 UI
}
```

**체크포인트:**
- [ ] `CalendarScreen`: 날짜 선택 UI 구현
- [ ] `TodoListScreen`: 선택된 날짜의 할 일 목록
- [ ] `AddTodoDialog`: 할 일 추가 다이얼로그
- [ ] `@Preview` 로 각 Composable 미리보기 동작 확인

---

## Phase 5 — 통합 & 폴리싱

**목표:** 전체 앱을 통합하고 사용성을 개선한다.

**배우는 것:**
- 앱 전체 Navigation Graph 설계
- Material3 디자인 컴포넌트 활용
- 애니메이션 (`AnimatedVisibility`, `animateColorAsState`)
- 에러 처리 및 로딩 상태 UI

**체크포인트:**
- [ ] 날짜 선택 → 해당 날짜 Todo 목록 표시 E2E 동작
- [ ] Todo 추가/완료/삭제 전체 플로우
- [ ] 앱 재시작 후 데이터 유지 (Room persistence)
- [ ] 로딩/에러 상태 UI 처리

---

## Phase 6 — 테스트 (선택, 권장)

**목표:** 각 레이어를 독립적으로 테스트하는 방법을 익힌다.

**배우는 것:**
- Room in-memory DB 테스트
- ViewModel 테스트 (`Turbine` 라이브러리로 Flow 테스트)
- Compose UI 테스트 (`ComposeTestRule`)

---

## 학습 참고 자료

| 주제 | 링크 |
|------|------|
| Compose 공식 | https://developer.android.com/jetpack/compose |
| Now in Android (구글 공식 예제) | https://github.com/android/nowinandroid |
| Compose 상태 관리 | https://developer.android.com/jetpack/compose/state |
| MVVM + MVI 패턴 | https://medium.com/androiddevelopers/a-guide-to-android-architecture-2024 |
| Hilt | https://developer.android.com/training/dependency-injection/hilt-android |
| Flow | https://developer.android.com/kotlin/flow |
