# Modern Android Development Trends (2015 → 2024+)

> 2015년까지 안드로이드 개발 경험이 있는 개발자를 위한 "무엇이 달라졌는가" 가이드

---

## 1. UI 패러다임 — XML View → Jetpack Compose

### 2015년 방식
```xml
<!-- activity_main.xml -->
<LinearLayout ...>
    <TextView android:id="@+id/tvTitle" android:text="Hello" />
    <Button android:id="@+id/btnSubmit" android:text="Submit" />
</LinearLayout>
```
```kotlin
// MainActivity.kt
val tvTitle = findViewById<TextView>(R.id.tvTitle)
tvTitle.text = "World"
```

### 2024+ 방식 (Jetpack Compose)
```kotlin
@Composable
fun TodoScreen(title: String) {
    Column {
        Text(text = title)
        Button(onClick = { /* ... */ }) { Text("Submit") }
    }
}
```

**핵심 차이점:**
| 항목 | XML View | Jetpack Compose |
|------|----------|-----------------|
| UI 정의 | XML + Kotlin 분리 | Kotlin 단일 코드 |
| 상태 관리 | 수동 (`setText`, `setVisibility`) | 선언적 (`state` → 자동 리컴포지션) |
| Preview | 에뮬레이터 필요 | `@Preview` 어노테이션으로 즉시 확인 |
| 애니메이션 | ObjectAnimator, ValueAnimator | `AnimatedVisibility`, `animateAsState` |
| RecyclerView | Adapter 보일러플레이트 | `LazyColumn { items(list) { ... } }` |

---

## 2. 아키텍처 패턴 — MVC → MVVM / MVI

### 2015년 방식 (대부분 MVC)
- Activity/Fragment가 UI + 비즈니스 로직 모두 담당
- God Activity 문제 (수천 줄짜리 Activity)
- 테스트 거의 불가

### 2024+ 방식: MVVM + MVI 하이브리드

```
┌─────────────┐     Intent/Event     ┌─────────────┐
│   UI Layer  │ ─────────────────►  │  ViewModel  │
│  (Compose)  │ ◄─────────────────  │  (MVVM+MVI) │
└─────────────┘     UiState(Flow)   └──────┬──────┘
                                           │ UseCase 호출
                                    ┌──────▼──────┐
                                    │  Domain     │
                                    │  (UseCase)  │
                                    └──────┬──────┘
                                           │
                                    ┌──────▼──────┐
                                    │    Data     │
                                    │ (Repository)│
                                    └─────────────┘
```

**MVVM vs MVI 차이:**
| | MVVM | MVI |
|--|------|-----|
| 상태 | 여러 LiveData/Flow | 단일 UiState sealed class |
| 이벤트 | 함수 직접 호출 | Intent(Event) 객체 |
| 디버깅 | 상태 추적 어려움 | 단방향 데이터 흐름으로 명확 |
| 적합한 화면 | 단순 CRUD | 복잡한 인터랙션 |

**하이브리드 전략 (이 프로젝트 채택):**
- ViewModel에 `UiState` (단일 상태, MVI 방식)
- 이벤트는 `UiEvent` sealed class (MVI 방식)
- Repository 패턴으로 데이터 추상화 (MVVM 방식)

---

## 3. 비동기 처리 — AsyncTask → Coroutines + Flow

### 2015년 방식
```kotlin
// AsyncTask (deprecated in API 30!)
class LoadTodosTask : AsyncTask<Void, Void, List<Todo>>() {
    override fun doInBackground(vararg params: Void?) = db.loadAll()
    override fun onPostExecute(result: List<Todo>) { adapter.submitList(result) }
}
```

### 2024+ 방식 (Coroutines + Flow)
```kotlin
// ViewModel
viewModelScope.launch {
    repository.getTodosForDate(date)
        .collect { todos -> _uiState.update { it.copy(todos = todos) } }
}
```

**Flow 타입 비교:**
| 타입 | 설명 | 언제 사용 |
|------|------|----------|
| `Flow<T>` | Cold Stream, 구독 시작 시 실행 | Repository → ViewModel |
| `StateFlow<T>` | Hot Stream, 항상 최신 값 보유 | ViewModel → UI |
| `SharedFlow<T>` | Hot Stream, 히스토리 없음 | 일회성 이벤트 (Toast, Navigate) |

---

## 4. 의존성 주입 — Dagger → Hilt

### 2015년 방식
```kotlin
// Dagger 2 - 복잡한 Component/Module 설정
@Component(modules = [AppModule::class])
interface AppComponent { fun inject(activity: MainActivity) }
```

### 2024+ 방식 (Hilt)
```kotlin
@HiltAndroidApp
class MyApp : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity() // 자동 주입

@HiltViewModel
class TodoViewModel @Inject constructor(
    private val repository: TodoRepository
) : ViewModel()
```

---

## 5. 로컬 데이터베이스 — SQLiteOpenHelper → Room

### 2015년 방식
```kotlin
// SQLiteOpenHelper 직접 구현 - 수십 줄의 반복 코드
class DbHelper : SQLiteOpenHelper(...) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE todos (...)")
    }
}
```

### 2024+ 방식 (Room)
```kotlin
@Entity
data class TodoEntity(@PrimaryKey val id: Long, val title: String, val date: LocalDate)

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos WHERE date = :date")
    fun getTodosByDate(date: LocalDate): Flow<List<TodoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(todo: TodoEntity)
}
```

---

## 6. 네비게이션 — Intent → Navigation Compose

### 2015년 방식
```kotlin
val intent = Intent(this, DetailActivity::class.java)
intent.putExtra("id", todoId)
startActivity(intent)
```

### 2024+ 방식
```kotlin
// NavHost 정의 (sealed class로 타입 안전)
NavHost(navController, startDestination = Screen.Calendar.route) {
    composable(Screen.Calendar.route) { CalendarScreen(navController) }
    composable("detail/{id}") { DetailScreen(navController) }
}
// 이동
navController.navigate("detail/$todoId")
```

---

## 7. 빌드 시스템 — Groovy Gradle → Kotlin DSL + Version Catalog

### 2015년 방식 (`build.gradle`)
```groovy
dependencies {
    implementation 'androidx.room:room-runtime:2.6.1'
    kapt 'androidx.room:room-compiler:2.6.1'
}
```

### 2024+ 방식 (`build.gradle.kts` + `libs.versions.toml`)
```toml
# gradle/libs.versions.toml
[versions]
room = "2.6.1"

[libraries]
room-runtime = { group = "androidx.room", name = "room-runtime", version.ref = "room" }
```
```kotlin
// build.gradle.kts
dependencies {
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
}
```

---

## 8. 테스트 전략

| 영역 | 2015년 | 2024+ |
|------|--------|-------|
| ViewModel | 테스트 어려움 | `viewModelScope` mock, `Turbine` (Flow 테스트) |
| UI | Espresso (느림, 불안정) | Compose Testing (`composeTestRule`) |
| DB | 실 DB에 의존 | Room in-memory DB |
| 전체 | 거의 수동 | Unit + Integration + UI 계층 분리 |

---

## 9. 2024+ 기술 스택 한눈에 보기

```
언어:          Kotlin (Coroutines, Flow, Extension Functions)
UI:            Jetpack Compose + Material3
아키텍처:      MVVM + MVI 하이브리드
DI:            Hilt
DB:            Room (with Flow)
비동기:        Coroutines + StateFlow/SharedFlow
네비게이션:    Navigation Compose
빌드:          Kotlin DSL + Version Catalog
테스트:        JUnit5, MockK, Turbine, Compose Testing
```

---

## 10. 마인드셋 전환 포인트

> 2015 → 2024로 넘어올 때 가장 헷갈리는 부분

1. **"어떻게 그릴지"가 아닌 "상태가 무엇인지"를 먼저 생각** — Compose는 상태 → UI 방향
2. **Activity/Fragment는 최소화** — UI 진입점일 뿐, 로직은 ViewModel로
3. **Flow는 LiveData보다 강력** — cold/hot 개념 이해 필수
4. **Hilt가 마법처럼 동작** — 어노테이션 기반 DI, Dagger 지식 없어도 OK
5. **Room + Flow = 리액티브 DB** — DB 변경 → UI 자동 업데이트
