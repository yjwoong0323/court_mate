# Court Mate · Backend API

배드민턴 코트 관리 서비스의 REST API 서버

---

## Tech Stack

- **Language**: Java 17
- **Framework**: Spring Boot 3.x
- **Database**: MySQL 8.x
- **ORM**: Spring Data JPA (Hibernate)
- **Auth**: Spring Security + JWT
- **Build**: Gradle

## Getting Started

### Prerequisites

- JDK 17 이상
- MySQL 8.x
- Gradle 8.x

### Setup

```bash
# 1. 저장소 클론
git clone https://github.com/your/court-mate-backend.git
cd court-mate-backend

# 2. DB 생성
mysql -u root -p
> CREATE DATABASE court_mate CHARACTER SET utf8mb4;

# 3. 환경 변수 설정 (application-local.yml 또는 환경 변수)
# - DB_URL, DB_USERNAME, DB_PASSWORD, JWT_SECRET

# 4. 빌드 & 실행
./gradlew bootRun
```

서버는 `http://localhost:8080` 에서 동작.

## Project Structure

```
src/main/java/com/courtmate/
├── auth/              # 로그인, JWT 발급/검증
├── member/            # 멤버 관리
├── court/             # 코트 + 게임 진행
├── common/            # 공통 응답 포맷, 예외 처리
│   ├── exception/
│   └── response/
└── config/            # Security, CORS 등 설정
```

---

## API 공통 사항

| 항목       | 값                                            |
|----------|----------------------------------------------|
| Base URL | `http://localhost:8080/api`                  |
| 인증       | `Authorization: Bearer {JWT}` 헤더              |
| 콘텐츠 타입   | `application/json; charset=UTF-8`            |
| 날짜 포맷    | ISO 8601 (`2026-05-13T10:30:00+09:00`)       |

### 공통 응답 포맷

**성공 응답**
```json
{
  "success": true,
  "data": { ... }
}
```

**에러 응답**
```json
{
  "success": false,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "이름 또는 비밀번호가 일치하지 않습니다"
  }
}
```

### HTTP Status Code 컨벤션

| 코드  | 의미                              |
|-----|---------------------------------|
| 200 | 조회/수정 성공                        |
| 201 | 생성 성공                           |
| 204 | 삭제 성공 (응답 본문 없음)                |
| 400 | 잘못된 요청 (validation 실패 등)        |
| 401 | 인증되지 않음 (로그인 필요)                |
| 403 | 권한 부족 (Player가 admin API 호출 등)  |
| 404 | 리소스 없음                          |
| 409 | 충돌 (이미 진행 중인 코트를 또 시작하려는 경우 등)  |
| 500 | 서버 내부 에러                        |

---

## API Endpoints

### 🔐 Authentication

#### `POST /api/auth/login/admin`
관리자 로그인

**Request**
```json
{
  "name": "Jaewoong",
  "password": "your_password"
}
```

**Response 200**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 3600,
    "user": {
      "id": 1,
      "name": "Jaewoong",
      "role": "ADMIN"
    }
  }
}
```

**Response 401**
```json
{
  "success": false,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "이름 또는 비밀번호가 일치하지 않습니다"
  }
}
```

---

#### `POST /api/auth/login/player`
일반 멤버 로그인

**Request**
```json
{
  "name": "박지원"
}
```

**Response 200**
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 5,
      "name": "박지원",
      "role": "PLAYER",
      "gender": "M",
      "level": "ADVANCED"
    }
  }
}
```

---

#### `POST /api/auth/logout`
로그아웃 (서버에서 토큰 무효화)

**Header**: `Authorization: Bearer {token}`

**Response 204** (No Content)

---

### 👥 Members

#### `GET /api/members`
멤버 목록 조회 (검색 + 필터)

**Query Parameters**

| 이름     | 타입      | 필수 | 설명                                         |
|--------|---------|----|--------------------------------------------|
| search | string  | ✗  | 이름 부분 검색                                   |
| gender | string  | ✗  | `M` \| `F`                                 |
| level  | string  | ✗  | `BEGINNER` \| `INTERMEDIATE` \| `ADVANCED` |
| status | string  | ✗  | `PLAYING` \| `WAITING` \| `REST`           |
| page   | integer | ✗  | 페이지 번호 (default: 0)                        |
| size   | integer | ✗  | 페이지 크기 (default: 20)                       |

**Example**: `GET /api/members?gender=M&level=ADVANCED&page=0&size=20`

**Response 200**
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "name": "박지원",
        "gender": "M",
        "level": "ADVANCED",
        "status": "PLAYING"
      }
    ],
    "totalElements": 24,
    "totalPages": 2,
    "currentPage": 0
  }
}
```

---

#### `GET /api/members/{id}`
멤버 단건 조회

**Response 200**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "박지원",
    "gender": "M",
    "level": "ADVANCED",
    "status": "PLAYING",
    "totalGames": 47,
    "createdAt": "2025-12-01T09:00:00+09:00"
  }
}
```

---

#### `POST /api/members`  🔒 ADMIN
멤버 생성

**Request**
```json
{
  "name": "신규멤버",
  "gender": "F",
  "level": "BEGINNER"
}
```

**Response 201**
```json
{
  "success": true,
  "data": {
    "id": 25,
    "name": "신규멤버",
    "gender": "F",
    "level": "BEGINNER",
    "status": "REST"
  }
}
```

---

#### `PUT /api/members/{id}`  🔒 ADMIN
멤버 정보 수정

#### `DELETE /api/members/{id}`  🔒 ADMIN
멤버 삭제

---

### 🏸 Courts

#### `GET /api/courts`
전체 코트 상태 조회 (대시보드 메인 데이터)

**Response 200**
```json
{
  "success": true,
  "data": {
    "activeCourts": [
      {
        "id": 1,
        "name": "Court 1",
        "type": "ACTIVE",
        "status": "PLAYING",
        "players": [
          { "id": 1, "name": "박지원", "gender": "M" },
          { "id": 4, "name": "최민준", "gender": "M" },
          { "id": 2, "name": "김선영", "gender": "F" },
          { "id": 3, "name": "이현주", "gender": "F" }
        ],
        "startedAt": "2026-05-13T10:15:00+09:00"
      },
      {
        "id": 2,
        "name": "Court 2",
        "type": "ACTIVE",
        "status": "IDLE",
        "players": [],
        "startedAt": null
      }
    ],
    "waitingCourts": [
      {
        "id": 5,
        "name": "Court W1",
        "type": "WAITING",
        "players": [ ... ]
      }
    ]
  }
}
```

---

#### `POST /api/courts/{id}/start`  🔒 ADMIN
코트에서 게임 시작 (status: IDLE → PLAYING)

**Response 200**
```json
{
  "success": true,
  "data": {
    "id": 1,
    "status": "PLAYING",
    "startedAt": "2026-05-13T10:30:00+09:00"
  }
}
```

**Response 409**
```json
{
  "success": false,
  "error": {
    "code": "COURT_ALREADY_PLAYING",
    "message": "이미 진행 중인 코트입니다"
  }
}
```

---

#### `POST /api/courts/{id}/end`  🔒 ADMIN
코트 게임 종료 (status: PLAYING → IDLE)

**Response 200**: 종료된 게임의 요약 정보 반환 (소요 시간 등)

---

#### `PUT /api/courts/{id}/players`  🔒 ADMIN
코트의 선수 4명 지정/변경

**Request**
```json
{
  "playerIds": [1, 4, 2, 3]
}
```

**Response 200**

**Response 400** (4명이 아닐 때)
```json
{
  "success": false,
  "error": {
    "code": "INVALID_PLAYER_COUNT",
    "message": "코트에는 정확히 4명의 선수가 배치되어야 합니다"
  }
}
```

---

#### `POST /api/courts/waiting/{waitingId}/move`  🔒 ADMIN
대기열의 코트를 활성 코트로 이동

**Request**
```json
{
  "targetCourtId": 3
}
```

**Response 200**: 이동 결과 반환

---

### 📊 Statistics (선택 기능)

#### `GET /api/stats/dashboard`
대시보드 우측 상단 요약 데이터

**Response 200**
```json
{
  "success": true,
  "data": {
    "totalMembers": 24,
    "activeCourts": 2,
    "totalCourts": 4,
    "waitingMembers": 8,
    "todayGames": 12
  }
}
```

---

## TODO (추가 예정 엔드포인트)

- [ ] `POST /api/games/auto-match` — 자동 매칭 알고리즘 (레벨/성별 균형)
- [ ] `GET /api/games/history` — 게임 기록 조회
- [ ] `GET /api/members/{id}/stats` — 개인별 통계
- [ ] WebSocket 실시간 코트 상태 동기화

---

## 컨벤션 메모 (학습용)

이 명세서는 다음 REST 컨벤션을 따름:

1. **명사 사용**: `/api/courts` (O) / `/api/getCourts` (X)
2. **복수형**: `/api/members` (O) / `/api/member` (X)
3. **계층 구조 표현**: `/api/courts/{id}/players` — 코트의 선수들
4. **동작은 sub-resource로**: `/api/courts/{id}/start` (POST) — 동사가 필요한 액션
5. **HTTP 메서드로 의도 표현**: GET (조회), POST (생성/액션), PUT (전체 수정), PATCH (부분 수정), DELETE (삭제)
6. **쿼리 파라미터로 필터링**: `?gender=M&level=ADVANCED`
7. **응답은 항상 공통 포맷**: `{ success, data }` 또는 `{ success, error }` — 프론트엔드에서 일관되게 처리 가능

`🔒 ADMIN` 표시는 관리자 권한이 필요한 엔드포인트.

---

## License

MIT
