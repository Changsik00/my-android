# TodoScheduler — 개발 백로그

> MVVM + MVI 하이브리드 | Jetpack Compose | Room + Flow | Hilt
> 
> **스펙 단위로 태스크가 분리**되어 있습니다. 각 태스크는 독립적으로 실행 가능하며, `agent.md`의 Spec-Driven Development 프로세스에 따라 진행합니다.

---

## 상태 표기

- `[ ]` 미완료
- `[/]` 진행 중
- `[x]` 완료
- `[~]` 보류/스킵

---

## Epic 0 — 프로젝트 기반 설정

### ✅ SPEC-001: 의존성 및 플러그인 설정
```
목표: 필요한 모든 라이브러리를 build.gradle.kts에 추가한다
완료 조건: Gradle sync 성공, 컴파일 에러 없음
```
- [x] `gradle/libs.versions.toml`에 Room, Hilt, Navigation, Coroutines, Lifecycle 버전 추가
- [x] `app/build.gradle.kts`에 KSP 플러그인, Hilt 플러그인 추가
- [x] `app/build.gradle.kts`에 모든 의존성 추가
- [x] `build.gradle.kts` (root)에 Hilt 플러그인 선언
- [x] Gradle sync 성공 확인

### ✅ SPEC-002: Hilt 애플리케이션 설정
```
목표: 앱 전체에 Hilt DI를 활성화한다
완료 조건: @HiltAndroidApp 앱 실행 성공
```
- [x] `MyApplication.kt` 생성 (`@HiltAndroidApp`)
- [x] `AndroidManifest.xml`에 `application name` 등록
- [x] `MainActivity.kt`에 `@AndroidEntryPoint` 적용

### ✅ SPEC-003: 앱 패키지 구조 생성
```
목표: Clean Architecture 레이어별 패키지 구조를 만든다
완료 조건: 패키지 구조가 docs/03_architecture.md와 일치
```
- [x] `data/` 패키지 생성 (db, repository)
- [x] `domain/` 패키지 생성 (model, repository, usecase)
- [x] `presentation/` 패키지 생성 (screens, viewmodel, components)
- [x] `di/` 패키지 생성

---

## Epic 1 — 데이터 레이어

### ✅ SPEC-101: Room 데이터베이스 설정
```
목표: 로컬 영구 저장소를 설정한다
완료 조건: DB 생성 성공, Dao 호출 시 데이터 반환
```
- [x] `TodoEntity.kt` 작성 (id, title, description, date, isCompleted, createdAt)
- [x] `LocalDateConverter.kt` 작성 (TypeConverter: LocalDate ↔ Long)
- [x] `TodoDao.kt` 인터페이스 작성
  - [x] `getTodosByDate(date: LocalDate): Flow<List<TodoEntity>>`
  - [x] `insertTodo(todo: TodoEntity): Long`
  - [x] `updateTodo(todo: TodoEntity)`
  - [x] `deleteTodo(id: Long)`
  - [x] `getTodoById(id: Long): TodoEntity?`
- [x] `TodoDatabase.kt` 작성 (`@Database`)
- [x] `DatabaseModule.kt` (Hilt) 작성

### ✅ SPEC-102: Repository 레이어 구현
```
목표: 데이터 소스를 추상화하는 Repository를 구현한다
완료 조건: ViewModel이 Repository 인터페이스만 의존하도록 구성
```
- [x] `TodoRepository.kt` 인터페이스 정의 (domain 레이어)
- [x] `TodoRepositoryImpl.kt` 구현 (data 레이어)
- [x] `RepositoryModule.kt` (Hilt) 바인딩 작성
- [x] Mapper 함수: `TodoEntity.toDomain()`, `Todo.toEntity()`

---

## Epic 2 — 도메인 레이어

### ✅ SPEC-201: 도메인 모델 정의
```
목표: UI와 DB에 독립적인 비즈니스 모델을 정의한다
완료 조건: Todo domain model이 Entity와 분리되어 존재
```
- [x] `Todo.kt` data class 작성 (domain model)
- [x] `TodoPriority.kt` enum 작성 (LOW, MEDIUM, HIGH — 선택사항)

### ✅ SPEC-202: UseCase 구현
```
목표: 각 비즈니스 액션을 UseCase로 캡슐화한다
완료 조건: 각 UseCase가 단일 책임을 가지고 독립적으로 테스트 가능
```
- [x] `GetTodosForDateUseCase.kt`
- [x] `AddTodoUseCase.kt`
- [x] `ToggleTodoUseCase.kt` (완료/미완료 토글)
- [x] `DeleteTodoUseCase.kt`
- [x] `GetTodoByIdUseCase.kt`

---

## Epic 3 — Presentation 레이어 (ViewModel)

### ✅ SPEC-301: Calendar ViewModel
```
목표: 날짜 선택과 Todo 목록을 관리하는 ViewModel
완료 조건: 날짜 변경 시 StateFlow에서 해당 날짜의 Todo 목록 emit
```
- [x] `TodoUiState.kt` 정의
  ```kotlin
  data class TodoUiState(
      val selectedDate: LocalDate,
      val todos: List<Todo>,
      val isLoading: Boolean,
      val error: String?
  )
  ```
- [x] `TodoUiEvent.kt` sealed class 정의
  ```kotlin
  sealed class TodoUiEvent {
      data class SelectDate(val date: LocalDate) : TodoUiEvent()
      data class AddTodo(val title: String, val description: String) : TodoUiEvent()
      data class ToggleTodo(val id: Long) : TodoUiEvent()
      data class DeleteTodo(val id: Long) : TodoUiEvent()
  }
  ```
- [x] `TodoUiEffect.kt` sealed class 정의 (일회성 이벤트: 스낵바, 네비게이션)
- [x] `TodoViewModel.kt` 구현 (`@HiltViewModel`)
  - [x] `uiState: StateFlow<TodoUiState>` 노출
  - [x] `effect: SharedFlow<TodoUiEffect>` 노출
  - [x] `onEvent(event: TodoUiEvent)` 처리
  - [x] Flow 수집: 날짜 변경 시 Todo 목록 자동 갱신

---

## Epic 4 — UI 레이어 (Jetpack Compose)

### ✅ SPEC-401: 앱 기본 구조 (NavHost, Theming)
```
목표: 앱 테마와 네비게이션 구조를 설정한다
완료 조건: 빈 화면이라도 앱 실행 및 네비게이션 동작
```
- [x] Material3 색상 테마 설정 (`Color.kt`, `Theme.kt`)
- [x] `NavGraph.kt` 작성 (NavHost, Screen sealed class)
- [x] `MainActivity.kt`에 NavHost 설정

### ✅ SPEC-402: Calendar Screen UI
```
목표: 날짜 선택 화면을 Compose로 구현한다
완료 조건: 날짜 탭하면 선택 상태가 변하고, 해당 날짜의 Todo 수가 표시됨
```
- [x] `CalendarScreen.kt` (Stateful Route + Stateless Screen 분리)
- [x] 월 단위 달력 컴포넌트 (`MonthCalendar.kt`)
  - [x] 현재 월 표시, 이전/다음 월 이동
  - [x] 날짜 셀 — 선택 상태 하이라이트
  - [x] 날짜 셀 — Todo 존재 시 인디케이터 표시
- [x] 선택된 날짜의 Todo 요약 표시 (몇 개 완료/전체)
- [x] FAB — 할 일 추가 버튼

### ✅ SPEC-403: Todo List UI
```
목표: 선택된 날짜의 할 일 목록을 표시한다
완료 조건: LazyColumn으로 Todo 목록 표시, 스와이프 삭제, 탭으로 완료 토글
```
- [x] `TodoListSection.kt` Composable
- [x] `TodoItem.kt` Composable
  - [x] 완료 상태 체크박스
  - [x] 제목, 설명 표시
  - [x] 완료 시 취소선 스타일
- [x] 스와이프 삭제 (`SwipeToDismiss`)
- [x] 빈 목록 상태 UI (Empty State)
- [x] 로딩 중 UI (CircularProgressIndicator)

### ✅ SPEC-404: Todo 추가 Dialog/Sheet
```
목표: 새 할 일을 추가하는 입력 UI
완료 조건: 제목 입력 후 저장 시 목록에 즉시 반영
```
- [x] `AddTodoBottomSheet.kt` (ModalBottomSheet)
  - [x] 제목 입력 (필수)
  - [x] 메모 입력 (선택)
  - [x] 저장 버튼 — 유효성 검사 (빈 제목 방지)
  - [x] 취소 버튼

### ✅ SPEC-405: 애니메이션 & UX 폴리싱
```
목표: 자연스러운 전환 애니메이션으로 완성도를 높인다
완료 조건: Todo 추가/삭제 시 애니메이션 동작
```
- [x] Todo 아이템 추가 시 `AnimatedVisibility`
- [x] 완료 토글 시 `animateColorAsState`
- [x] 화면 전환 애니메이션 (Navigation Compose)

---

## Epic 5 — 테스트 (권장)

### ✅ SPEC-501: 데이터 레이어 테스트
```
목표: Room DB와 Repository를 격리된 환경에서 테스트한다
완료 조건: 모든 Dao 쿼리 및 Repository 메서드가 테스트 통과
```
- [x] `TodoDaoTest.kt` (Room in-memory DB)
- [x] `TodoRepositoryImplTest.kt` (fake DB 사용)

### ✅ SPEC-502: ViewModel 테스트
```
목표: ViewModel의 상태 전환 로직을 테스트한다
완료 조건: 모든 UiEvent에 대한 UiState 변화 검증
```
- [x] `TodoViewModelTest.kt` (Turbine + MockK)
- [x] 날짜 변경 이벤트 → 상태 변화 검증
- [x] Todo 추가/삭제/완료 이벤트 검증

### SPEC-503: UI 테스트 (선택)
```
목표: Compose UI를 자동화 테스트한다
완료 조건: 주요 사용자 플로우가 Compose Test로 검증
```
- [ ] `TodoScreenTest.kt` (ComposeTestRule)
- [ ] 할 일 추가 플로우 테스트
- [ ] 날짜 선택 플로우 테스트

---

## Epic 6 — 고급 패턴 (v2 추가 요구사항)

### ✅ SPEC-601: Backbone 기반 클래스 구현
```
목표: BaseViewModel / BaseRepository 보일러플레이트를 만들어 모든 기능의 뼈대를 제공한다
완료 조건: TodoViewModel이 BaseViewModel을 상속하고 공통 로직이 제거됨
참조: docs/04_advanced_patterns.md > Section 5
```
- [x] `BaseViewModel.kt` 작성 (제네릭: Event, State, Effect)
  - [x] `updateState()` 헬퍼
  - [x] `sendEffect()` 헬퍼
  - [x] `launchWithLoading()` 헬퍼 (에러 자동 전달)
- [x] `BaseRepository.kt` 작성
  - [x] `safeFlow()` 헬퍼 (Flow 에러 자동 catch)
  - [x] `safeCall()` 헬퍼 (suspend 에러 자동 catch)
- [x] `TodoViewModel`이 `BaseViewModel` 상속하도록 리팩토링
- [x] `TodoRepositoryImpl`이 `BaseRepository` 상속하도록 리팩토링

### SPEC-602: 전역 에러 핸들러
```
목표: 어느 레이어에서 발생한 에러도 최상위 UI에서 Snackbar로 표시된다
완료 조건: 의도적 DB 에러 발생 시 AppScaffold가 Snackbar 표시
참조: docs/04_advanced_patterns.md > Section 3
```
- [ ] `AppError.kt` sealed class 작성 (NetworkError, DatabaseError, UnknownError)
- [ ] `AppErrorBus.kt` (object, SharedFlow) 작성
- [ ] `AppScaffold.kt` Composable 작성
  - [ ] `LaunchedEffect`로 `AppErrorBus.errors` 수집
  - [ ] `SnackbarHost`로 메시지 표시
- [ ] `MainActivity.setContent`에서 `AppScaffold`로 감싸기
- [ ] `BaseViewModel.launchWithLoading()`이 `AppErrorBus.emit()` 호출하는지 확인

### SPEC-603: 멀티 Activity — TodoDetailActivity
```
목표: Todo 상세/편집 화면을 별도 Activity로 분리하여 화면 전환을 경험한다
완료 조건: Todo 아이템 탭 → TodoDetailActivity 슬라이드 전환 → 수정 후 Back → 목록 자동 갱신
참조: docs/04_advanced_patterns.md > Section 1
```
- [ ] `TodoDetailActivity.kt` 생성 (`@AndroidEntryPoint`)
  - [ ] `EXTRA_TODO_ID` companion 상수
  - [ ] Compose로 상세 화면 렌더링
- [ ] `TodoDetailViewModel.kt` 작성
  - [ ] `getTodoByIdUseCase` 호출 → 상세 데이터 로드
  - [ ] 수정 / 삭제 이벤트 처리
- [ ] `TodoDetailScreen.kt` (Stateless Composable)
- [ ] `AndroidManifest.xml`에 `TodoDetailActivity` 등록
- [ ] `CalendarScreen`에서 `ActivityResultLauncher` 설정
  - [ ] Todo 탭 → Intent + `EXTRA_TODO_ID` 전달
  - [ ] `RESULT_OK` 반환 시 목록 새로고침 이벤트 발행
- [ ] Activity 전환 슬라이드 애니메이션 설정

### SPEC-604: 로딩 애니메이션 강화
```
목표: 초기 로딩과 새로고침 로딩을 시각적으로 구분하고, Shimmer UI를 적용한다
완료 조건: 날짜 변경 시 Shimmer → 실제 목록 전환 애니메이션 동작
참조: docs/04_advanced_patterns.md > Section 2
```
- [ ] `TodoUiState`에 `isRefreshing: Boolean` 필드 추가
- [ ] `ShimmerTodoItem.kt` Composable 작성 (무한 alpha 애니메이션)
- [ ] `TodoListSection`에서 `isLoading` 시 Shimmer 목록 표시
- [ ] `isRefreshing` 시 `LinearProgressIndicator` 오버레이 표시
- [ ] ViewModel에서 `flatMapLatest` 날짜 전환 시 로딩 상태 순서 보장

### SPEC-605: Room 심화 — DatabaseView + Migration
```
목표: DatabaseView로 달력 인디케이터용 집계 데이터를 제공하고, Migration 패턴을 적용한다
완료 조건: 달력 날짜 셀에 Todo 완료/전체 수가 DatabaseView에서 표시됨
참조: docs/04_advanced_patterns.md > Section 4
```
- [ ] `TodoSummaryView.kt` (`@DatabaseView`) 작성
- [ ] `TodoSummaryDao.kt` — `Flow<TodoSummaryView?>` 반환
- [ ] `TodoDatabase`에 `TodoSummaryView` 등록
- [ ] `GetTodoSummaryForMonthUseCase.kt` 작성
- [ ] `CalendarScreen`의 날짜 셀에 인디케이터 뱃지 표시
- [ ] `MIGRATION_1_2` 예제 작성 (priority 필드 추가)

---

## Epic 7 — 날씨 API 연동 (OpenWeatherMap)

### SPEC-701: 네트워크 레이어 기반 설정
```
목표: Retrofit + OkHttp + kotlinx.serialization로 네트워크 레이어를 구성한다
완료 조건: WeatherApi.kt 인터페이스 호출 시 실제 JSON 응답 수신
참조: docs/05_api_integration.md > Section 2~4
```
- [ ] `libs.versions.toml`에 Retrofit, OkHttp, kotlinx.serialization 버전 추가
- [ ] `app/build.gradle.kts`에 의존성 추가
- [ ] `local.properties`에 `WEATHER_API_KEY` 추가 (OpenWeatherMap 가입 후 발급)
- [ ] `app/build.gradle.kts`에 `buildConfigField` 설정
- [ ] `WeatherResponseDto.kt`, `ForecastResponseDto.kt` (@Serializable) 작성
- [ ] `WeatherApi.kt` Retrofit 인터페이스 작성
  - [ ] `getCurrentWeather()` - 당일 날씨
  - [ ] `getWeatherForecast()` - 5일 예보
- [ ] `NetworkModule.kt` (Hilt `@Singleton`) 작성
  - [ ] OkHttp (LoggingInterceptor, timeout)
  - [ ] Retrofit (baseUrl, Json converter)
  - [ ] `WeatherApi` binding

### SPEC-702: NetworkResult + safeApiCall
```
목표: API 호출 성공/실패/로딩을 타입 안전하게 처리하는 공통 래퍼를 만든다
완료 조건: safeApiCall이 HttpException과 IOException을 각각 다르게 처리
참조: docs/05_api_integration.md > Section 5
```
- [ ] `NetworkResult.kt` sealed class 작성 (Success, Error, Loading)
- [ ] `safeApiCall()` suspend 확장 함수 작성
- [ ] `AppError`에 `NetworkError` 포함 확인 (SPEC-602 선행 필요)
- [ ] `BaseRepository.safeApiCall()` 호출 통합

### SPEC-703: Weather Repository + Offline-First 캐싱
```
목표: API 결과를 Room에 캐싱하고 오프라인에서도 최근 날씨를 표시한다
완료 조건: 비행기 모드에서도 캐시된 날씨 데이터가 표시됨
참조: docs/05_api_integration.md > Section 6
```
- [ ] `WeatherCacheEntity.kt` Room 엔티티 작성 (dateKey, condition, tempCelsius, iconCode, cachedAt)
- [ ] `WeatherCacheDao.kt` 작성 (upsert, getWeather)
- [ ] `TodoDatabase`에 `WeatherCacheEntity` 추가 + Migration
- [ ] `WeatherInfo.kt` Domain 모델 작성
- [ ] `WeatherRepository.kt` 인터페이스 (domain 레이어)
- [ ] `WeatherRepositoryImpl.kt` 구현
  - [ ] 캐시 즉시 emit → API 호출 → 캐시 갱신 → 새 값 emit (Flow)
  - [ ] 캐시 유효기간 1시간 체크
- [ ] `RepositoryModule.kt`에 WeatherRepository 바인딩 추가

### SPEC-704: Weather UseCase + ViewModel 통합
```
목표: TodoViewModel에 날씨 정보를 통합하여 날짜 선택 시 날씨를 함께 표시한다
완료 조건: 캘린더에서 날짜 선택 시 해당 날의 날씨와 기온이 표시됨
참조: docs/05_api_integration.md > Section 7
```
- [ ] `GetWeatherForDateUseCase.kt` 작성
- [ ] `TodoUiState`에 `weather: WeatherInfo?`, `isWeatherLoading: Boolean` 추가
- [ ] `TodoViewModel`에 날짜 변경 시 날씨 조회 Flow 연동
  - [ ] `combine(todosFlow, weatherFlow)` 로 단일 UiState 업데이트
- [ ] `WeatherBadge.kt` Composable 작성 (기온 + 날씨 조건 + 아이콘 emoji)
- [ ] `CalendarScreen` 상단에 `WeatherBadge` 배치
- [ ] `isWeatherLoading` 시 Badge 자리에 shimmer 표시

---

## 완료 기준 (Definition of Done)

각 스펙 태스크는 다음 조건을 모두 충족해야 완료로 간주합니다:
1. 코드 컴파일 성공 (Gradle build)
2. 해당 스펙의 완료 조건 충족
3. `docs/agent.md`의 스펙 완료 체크리스트 업데이트
