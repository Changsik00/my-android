# 아키텍처 가이드 06 — 왜 Repository를 인터페이스로 분리할까? (결합도와 테스트 관점)

> **Q. Repository 구조를 보니 실제 DB와 연결되어 있어서 결합도가 높은 것 아닌가요? 사용하는 쪽에서는 어디에서 데이터를 가져오는지 모르게 사용해야 하는 것 아닌가요?**
>
> **Q. UI 테스트 같은 걸 할 때 정보를 가져오려면, 테스트 방법을 고려한 설계나 대비가 있어야 할 것 같은데 현재 구조는 어떻게 처리되나요?**

위와 같은 훌륭한 질문에서 출발하여, 현재 TodoScheduler의 데이터 레이어가 왜 지금의 구조를 띄는지, 그리고 이것이 앱의 안정성과 테스트에 어떤 영향을 미치는지 정리한 문서입니다.

---

## 1. 사용하는 쪽 (Endpoint)의 완벽한 분리 (Decoupling)

**클린 아키텍처(Clean Architecture)** 모델에서 가장 중요한 것은 의존성(Dependency)의 방향입니다. UI나 비즈니스 로직(UseCase)은 **"데이터가 어디서 오는지"** 몰라야만 유연하게 살아남을 수 있습니다.

### 사용하는 쪽 (Domain / Presentation Layer)
사용하는 쪽에서는 오직 `domain/repository`에 정의된 **`TodoRepository` 인터페이스**만 바라봅니다.

```kotlin
// GetTodosForDateUseCase.kt (사용하는 쪽)
class GetTodosForDateUseCase @Inject constructor(
    private val repository: TodoRepository // 구체적인 구현체(Room, Retrofit)를 모름!
) {
    operator fun invoke(date: LocalDate): Flow<List<Todo>> {
        return repository.getTodosByDate(date)
    }
}
```

### 구현하는 쪽 (Data Layer)
실제로 데이터를 가져오는 책임은 `data/repository`의 **`TodoRepositoryImpl`**이 가집니다. 프로젝트 내에서 **오직 이 클래스만** Room DB(Dao)와 결합되어 있습니다.

```kotlin
// TodoRepositoryImpl.kt (구현하는 쪽)
class TodoRepositoryImpl @Inject constructor(
    private val dao: TodoDao // 여기서 실물 데이터소스(Room)를 앎
) : TodoRepository {
    override fun getTodosByDate(date: LocalDate): Flow<List<Todo>> {
        // Room의 Entity를 순수 코틀린 Domain 객체인 Todo로 매핑해서 반환
        return dao.getTodosByDate(date).map { entities -> 
            entities.map { it.toDomain() } 
        }
    }
}
```

---

## 2. 결합을 끊어주는 마법사: Hilt (DI)

"그러면 어떻게 인터페이스가 실제 DB에서 값을 가져오도록 연결하나요?"
바로 그 결합(Wiring) 역할을 의존성 주입(DI) 도구인 **Hilt**가 담당합니다.

앱 전체에서 Hilt 모듈(`RepositoryModule.kt`) 단 한 곳에서만 **"이 인터페이스를 요청하면, 저 구현체를 줘라"**라고 묶어줍니다.

```kotlin
// RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTodoRepository(
        impl: TodoRepositoryImpl // 사용자가 TodoRepository를 요구하면 이걸(Room DB 연동) 줘라!
    ): TodoRepository
}
```

만약 추후에 로컬 DB 방식에서 **"온라인 API(서버 통신) 기능"**으로 변경해야 한다고 가정한다면?
1. `TodoRemoteRepositoryImpl`(서버통신용) 클래스를 새로 만듭니다.
2. `RepositoryModule.kt`에서 `bindTodoRepository`의 대상만 `impl: TodoRemoteRepositoryImpl` 로 갈아끼웁니다.
3. **사용하는 쪽(UI, UseCase) 코드는 단 한 줄도 수정할 필요가 없습니다!**

이것이 리포지토리 패턴을 사용하는 가장 강력한 이유입니다.

---

## 3. 테스트 설계에 대한 완벽한 대비 (Testability)

UI 테스트(Compose Test)나 뷰모델 단위 테스트(Unit Test)를 할 때 데이터가 필요합니다. 하지만 **테스트를 위해 매번 안드로이드 에뮬레이터를 켜고, 실제 물리 DB(Room)에 값을 넣다 뺐다 하면 테스트 속도가 너무 느리고 실패 확률도 높습니다.**

저희가 `TodoRepository`를 **인터페이스**로 분리해 두었기 때문에 이런 문제를 완벽히 해결할 수 있습니다. 어떻게 대비되어 있을까요?

### 가짜(Fake) 객체 주입

테스트 환경에서는 실제 `TodoRepositoryImpl` 대신, 메모리에서만 동작하는 "가짜 리포지토리"를 만들어 아주 빠르게 테스트할 수 있습니다.

```kotlin
// 테스트 환경에서만 사용하는 가짜 리포지토리
class FakeTodoRepository : TodoRepository {
    private val fakeTodos = mutableListOf<Todo>()
    
    override fun getTodosByDate(date: LocalDate): Flow<List<Todo>> = flow {
        // DB를 안 거치고 List 메모리에서 바로 반환 (0.01초 만에 실행)
        emit(fakeTodos.filter { it.date == date })
    }

    override suspend fun insertTodo(todo: Todo): Long {
        fakeTodos.add(todo)
        return todo.id
    }
}
```

### 어떻게 갈아끼우나요? (Hilt Test)

안드로이드 UI 테스트를 수행할 때 로컬 DB 대신 이 가짜(`FakeTodoRepository`) 데이터를 UI에 띄워서 테스트하도록 Hilt가 모듈을 바꿔치기 해줍니다.

```kotlin
@UninstallModules(RepositoryModule::class) // 앱 실행 시 주입되던 '진짜 DB 모듈' 비활성화
@InstallIn(SingletonComponent::class)
class TestRepositoryModule {               // 테스트용 '가짜 모듈' 활성화
    @Provides
    fun provideFakeRepo(): TodoRepository = FakeTodoRepository()
}
```

결론적으로, 현재 설계된 아키텍처는 **1) 확장에 열려 있고(무중단 플랫폼 교체 가능) 2) 테스트를 완벽하게 대비**한 상태라고 할 수 있습니다!
