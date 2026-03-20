---
description: SPEC 단위 태스크를 Spec-Driven Development로 실행하는 워크플로우
---

# SDD 워크플로우 — Spec 단위 개발

이 워크플로우는 `BACKLOG.md`에 정의된 SPEC-XXX 단위 태스크를 실행할 때 사용합니다.

## 사전 요건

- `BACKLOG.md`에서 작업할 SPEC-XXX를 식별한다
- `docs/agent.md`를 읽어 프로젝트 컨벤션을 이해한다

## 워크플로우 단계

### Step 1: 스펙 확인
`BACKLOG.md`에서 대상 SPEC-XXX를 읽고 다음을 파악한다:
- 목표 (한 줄 설명)
- 완료 조건
- 세부 태스크 목록

### Step 2: 선행 의존성 확인
해당 SPEC이 의존하는 다른 SPEC이 `[x]` 완료 상태인지 확인한다.
미완료 선행 스펙이 있으면 사용자에게 알린다.

### Step 3: 현재 빌드 상태 확인
// turbo
```bash
cd /Users/ck/Project/Android/MyAndroid && ./gradlew assembleDebug 2>&1 | tail -20
```
빌드가 깨진 상태면 사전에 수정한다.

### Step 4: 구현
`docs/agent.md`의 디렉토리 구조와 컨벤션에 따라 파일을 작성한다.
- 스펙의 완료 조건만 구현한다 (Scope Creep 금지)
- 레이어 의존 방향 준수: `presentation` → `domain` ← `data`

### Step 5: 빌드 검증
// turbo
```bash
cd /Users/ck/Project/Android/MyAndroid && ./gradlew assembleDebug 2>&1 | tail -30
```

### Step 6: 테스트 실행 (해당 SPEC에 테스트가 있는 경우)
// turbo
```bash
cd /Users/ck/Project/Android/MyAndroid && ./gradlew test 2>&1 | tail -30
```

### Step 7: 백로그 업데이트
`BACKLOG.md`에서 완료된 세부 태스크를 `[x]`로 마킹한다.
SPEC 전체가 완료되면 SPEC 헤더 옆에 ✅ 표기한다.

### Step 8: 완료 보고
구현된 내용, 파일 목록, 완료 조건 달성 여부를 사용자에게 보고한다.
