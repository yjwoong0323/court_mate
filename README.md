# CourtMate

배드민턴 모임을 위한 코트 관리 웹 서비스

관리자는 플레이어를 등록하고, 참석자를 실제 코트 또는 대기 코트에 배치해 게임 운영 가능

## Tech Stack

- Backend: Java 17, Spring Boot 3, Spring Data JPA, MySQL, Gradle
- Frontend: React, JavaScript, CSS

## 주요 기능

- 플레이어 등록, 수정, 삭제, 참석 여부 관리
- 활성 코트와 대기 코트 조회
- 코트별 플레이어 배치 및 제거
- 게임 시작/종료 시간 기록
- 관리자 모드와 플레이어 보기 모드

## 프로젝트 구조

```text
CourtMate/
├── backend/
│   └── court-mate-server/
└── frontend/
    └── court-mate-web/
```

## 실행

백엔드:

```bash
cd backend/court-mate-server
./gradlew bootRun
```

프론트엔드:

```bash
cd frontend/court-mate-web
npm install
npm run dev
```
