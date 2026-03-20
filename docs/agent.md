# Agent Guide — TodoScheduler Android Project

> 이 문서는 AI 에이전트(Antigravity)가 이 프로젝트에서 작업할 때 따라야 할 규칙과 프로세스를 정의합니다.
> 개발자도 참조하여 일관된 개발 방식을 유지할 수 있습니다.

---

## 1. 프로젝트 개요

| 항목 | 값 |
|------|-----|
| 앱 이름 | TodoScheduler |
| 패키지 | `com.example.myapplication` |
| 언어 | Kotlin |
| UI | Jetpack Compose + Material3 |
| 아키텍처 | MVVM + MVI 하이브리드 |
| DI | Hilt |
| DB | Room |
| 비동기 | Coroutines + Flow |
| 네비게이션 | Navigation Compose |

---

## 2. 디렉토리 구조

```
app/src/main/java/com/example/myapplication/
├── data/
│   ├── db/
│   │   ├── TodoDatabase.kt
│   │   ├── TodoDao.kt
│   │   ├── TodoEntity.kt
│   │   └── converter/
│   │       └── LocalDateConverter.kt
│   └── repository/
│       └── TodoRepositoryImpl.kt
├── domain/
│   ├── model/
│   │   └── Todo.kt
│   ├── repository/
│   │   └── TodoRepository.kt
│   └── usecase/
│       ├── GetTodosForDateUseCase.kt
│       ├── AddTodoUseCase.kt
│       ├── ToggleTodoUseCase.kt
│       └── DeleteTodoUseCase.kt
├── presentation/
│   ├── screen/
│   │   ├── calendar/
│   │   │   ├── CalendarScreen.kt
│   │   │   └── components/
│   │   │       └── MonthCalendar.kt
│   │   └── todo/
│   │       ├── TodoListSection.kt
│   │       ├── TodoItem.kt
│   │       └── AddTodoBottomSheet.kt
│   ├── viewmodel/
│   │   ├── TodoViewModel.kt
│   │   ├── TodoUiState.kt
│   │   ├── TodoUiEvent.kt
│   │   └── TodoUiEffect.kt
│   └── navigation/
│       └── NavGraph.kt
├── di/
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
├── ui/theme/
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt
└── MyApplication.kt
```

---

## 3. Spec-Driven Development (SDD) 프로세스

모든 코드 작업은 아래 프로세스를 따릅니다.

### Step 1: 스펙 확인
```
BACKLOG.md에서 대상 SPEC-XXX을 찾아 완료 조건을 확인한다
```

### Step 2: 작업 범위 분석
```
해당 스펙에서 변경/생성할 파일 목록을 먼저 나열한다
의존하는 다른 스펙이 완료되었는지 확인한다
```

### Step 3: 구현
```
파일 하나씩 작성한다. 절대 완료 조건을 벗어나는 범위를 작업하지 않는다
완료 조건에 없는 기능을 추가하지 않는다 (Scope Creep 방지)
```

### Step 4: 검증
```
Gradle build 성공 확인 (./gradlew assembleDebug)
스펙의 완료 조건 하나씩 체크
```

### Step 5: 백로그 업데이트
```
BACKLOG.md에서 완료된 태스크 [x]로 마킹
```

---

## 4. 코딩 컨벤션

### Composable 네이밍
```kotlin
// ✅ Route (Stateful) — ViewModel 알고 있음
@Composable
fun CalendarScreenRoute(viewModel: TodoViewModel = hiltViewModel())

// ✅ Screen (Stateless) — 순수 UI, Preview 가능
@Composable  
fun CalendarScreen(uiState: TodoUiState, onEvent: (TodoUiEvent) -> Unit)

// ✅ 재사용 컴포넌트
@Composable
fun TodoItem(todo: Todo, onToggle: () -> Unit, onDelete: () -> Unit)
```

### ViewModel 패턴
```kotlin
// ✅ StateFlow for UI state
val uiState: StateFlow<TodoUiState> = _uiState.asStateFlow()

// ✅ SharedFlow for one-time effects
val effect: SharedFlow<TodoUiEffect> = _effect.asSharedFlow()

// ✅ 단일 이벤트 진입점
fun onEvent(event: TodoUiEvent)
```

### Repository 패턴
```kotlin
// ✅ Domain 레이어에 인터페이스 정의
interface TodoRepository {
    fun getTodosForDate(date: LocalDate): Flow<List<Todo>>
    suspend fun addTodo(todo: Todo): Long
}

// ✅ Data 레이어에 구현체
class TodoRepositoryImpl @Inject constructor(
    private val dao: TodoDao
) : TodoRepository
```

### Flow 수집 방법
```kotlin
// ✅ Compose에서 StateFlow 수집
val uiState by viewModel.uiState.collectAsStateWithLifecycle()

// ✅ ViewModel에서 Flow 수집 (날짜 변경 연동)
selectedDate.flatMapLatest { date ->
    getTodosForDateUseCase(date)
}.onEach { todos ->
    _uiState.update { it.copy(todos = todos) }
}.launchIn(viewModelScope)
```

---

## 5. 금지 사항

- ❌ Activity/Fragment에 비즈니스 로직 작성 금지
- ❌ ViewModel에서 Context 사용 금지 (AndroidViewModel 사용 자제)
- ❌ Composable에서 직접 ViewModel 생성 금지 (`hiltViewModel()` 사용)
- ❌ Data 레이어에서 Domain 모델 직접 사용 금지 (Mapper 사용)
- ❌ `runBlocking` 사용 금지 (테스트 코드 제외)
- ❌ `GlobalScope` 사용 금지

---

## 6. 주요 라이브러리 버전 참조

> 정확한 버전은 `gradle/libs.versions.toml` 참조

| 라이브러리 | 역할 |
|-----------|------|
| `androidx.room` | 로컬 DB |
| `com.google.dagger.hilt` | 의존성 주입 |
| `androidx.navigation.compose` | 화면 이동 |
| `androidx.lifecycle.viewmodel.compose` | Compose ViewModel |
| `org.jetbrains.kotlinx.coroutines` | 비동기 |
| `com.squareup.turbine` | Flow 테스트 (테스트만) |
| `io.mockk` | Mock 라이브러리 (테스트만) |

---

## 7. 자주 쓰는 명령어

```bash
# 빌드
./gradlew assembleDebug

# 단위 테스트
./gradlew test

# 인스트루멘트 테스트
./gradlew connectedAndroidTest

# Clean 빌드
./gradlew clean assembleDebug

# Lint 검사
./gradlew lint
```

---

## 8. 작업 시작 전 체크리스트

에이전트 또는 개발자가 새 스펙 작업을 시작하기 전 반드시 확인:

- [ ] `BACKLOG.md`에서 대상 SPEC-XXX 확인
- [ ] 의존 스펙(선행 스펙)이 완료되었는지 확인
- [ ] 기존 파일 구조와 충돌이 없는지 확인
- [ ] `./gradlew assembleDebug`로 현재 빌드 상태 확인 (깨진 상태로 시작 금지)
