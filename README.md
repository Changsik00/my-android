# MyAndroid (할 일 관리 & 캘린더 앱)

Modern Android Development(MAD) 스펙에 따라 구현된 할 일 관리(Todo) 및 캘린더 안드로이드 애플리케이션입니다.

## 🎯 목표 (Goal)
이 프로젝트는 **최신 안드로이드 UI 툴킷과 아키텍처 패턴을 학습하고 적용하는 것**을 목표로 합니다.
- 직관적이고 부드러운 캘린더 기반의 할 일 기록 및 관리 경험 제공
- 선언형 UI, 양방향 데이터 바인딩 없는 단방향 데이터 흐름(UDF) 체득
- 데이터 독립성을 위한 Repository 및 모듈 분리 패턴 구현

## 🏗 아키텍처 및 기술 스택 (Architecture & Tech Stack)
본 앱은 **MVVM 패턴과 MVI 아키텍처의 장점을 혼합**하여 설계되었습니다.
- **UI (Presentation)**: Jetpack Compose (Material 3), Jetpack Navigation Compose
- **State Management**: ViewModel (`StateFlow` 관측 및 `UiEvent` 기반 단방향 상태 갱신)
- **Local Database (Data)**: Room Database (SQLite)
- **Asynchronous & Threading**: Kotlin Coroutines & Flow
- **Dependency Injection**: Hilt (Dagger)
- **API Compatibility**: Java Time API Core Library Desugaring 도입 (minSdk 24 지원)

## 💻 실행 및 설치 환경 (Environment)
* **minSdkVersion**: 24 (Android 7.0)
* **targetSdkVersion**: 34
* **Kotlin Version**: 1.9.0
* **Compose BOM**: 2024.09.00

## 🚀 앱 빌드 및 실행 (Build & Run)
1. 리포지토리를 클론합니다.
2. Android Studio (Ladybug 이상 권장)에서 프로젝트를 엽니다.
3. Gradle Sync가 완료될 때까지 기다립니다.
4. **로컬 에뮬레이터 또는 실기기를 연결**한 뒤, 상단의 **Run 'app' (Shift + F10)** 버튼을 클릭하거나 다음 명령어를 터미널에 입력하여 디버그 빌드를 진행합니다.
   ```bash
   ./gradlew assembleDebug
   ```

## 🧪 테스트 방법 (Test)
앱의 각 계층이 정상적으로 작동하는지 자동화된 테스트를 통해 확인할 수 있습니다.
- **로컬 단위 테스트 (JVM)**: 데이터 로직 및 레포지토리에 대한 테스트
  ```bash
  ./gradlew testDebugUnitTest
  ```
- **안드로이드 통합 테스트 (Instrumented)**: Room DB(SQLite) 인메모리 테스트 및 기기 환경 점검
  *주의: 구동 중인 안드로이드 에뮬레이터나 실기기가 반드시 필요합니다.*
  ```bash
  ./gradlew connectedAndroidTest
  ```

## 📚 관련 문서 가이드 (Docs)
프로젝트 진행 시 참고했던 배경 지식이나 기술 스펙 문서들은 `docs/` 폴더 내에 정리되어 있습니다.
- [01. Modern Android Trends](docs/01_modern_android_trends.md) - 최근 안드로이드 개발 트렌드 분석
- [02. Learning Guide](docs/02_learning_guide.md) - 프로젝트 개발 학습 가이드
- [03. Architecture](docs/03_architecture.md) - 권장 아키텍처 및 구조 가이드
- [04. Advanced Patterns](docs/04_advanced_patterns.md) - MVI 등 안드로이드 고급 패턴 적용 안내
- [05. API Integration](docs/05_api_integration.md) - 외부 API 연동 설계 가이드
- [06. Why Repository Pattern](docs/06_why_repository_pattern.md) - 리포지토리 패턴 도입에 대한 논의
- [Agent Role / Overview](docs/agent.md) - 개발 진행 Agent 개요 문서
