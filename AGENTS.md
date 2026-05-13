# AGENTS.md (공통)

이 파일은 Codex 또는 AI 코딩 도구가 이 프로젝트를 이해하고 일관된 방식으로 개발할 수 있도록 제공하는 **프로젝트 공통 가이드**.
백엔드와 프론트엔드 양쪽이 모두 알아야 하는 정보가 담겨 있다.

- 백엔드 작업 시에는 이 파일과 함께 `backend/AGENTS.md`도 자동 로드된다.
- 프론트엔드 작업 시에는 이 파일과 함께 `front/AGENTS.md`도 자동 로드된다.

## 프로젝트 개요

배드민턴 모임에서 사용할 **코트 관리 웹 어플리케이션**

관리자(admin)가 로그인 후 플레이어들을 등록/관리하고, 활성 코트(1~4) 또는 대기 코트(W1, W2)에 배치하여 게임을 운영할 수 있다. 게임은 시작/종료 시각이 기록되어 추후 통계나 매칭 분석에 활용 가능하다.

초기 목표는 로컬 환경에서 Spring Boot + React + MySQL 기반으로 핵심 기능을 구현하는 것. 추후 AWS(EC2, CloudFront)에 배포한다.

## 개발 철학

- 완벽한 설계보다 동작하는 핵심 기능을 우선한다.
- 각 기능은 백엔드 API → 프론트 UI까지 **vertical slice**로 한 번에 완성한 뒤 다음 단계로 넘어간다.
- 완성된 단계는 브라우저에서 직접 동작 확인이 가능해야 한다.
- 학습 목적과 실제 모임 운영 목적을 함께 가진다. 따라서 처음부터 완벽한 설계를 추구하기보다, 동작 확인 후 리팩토링한다.

## 도메인 모델 핵심 개념

이 프로젝트의 데이터 모델은 다음 5개의 테이블로 구성됨.

- `admin` : 관리자 계정
- `player` : 플레이어 정보 (참석 여부 포함)
- `court` : 코트 (활성/대기 구분) — **대기열은 별도 테이블이 아니라 `court_type='WAITING'`인 코트로 표현됨**
- `game` : 코트 위에서 진행되는 한 판의 게임 (BEFORE → PLAYING → FINISHED)
- `court_assignment` : 게임에 배정된 플레이어 (game_id × player_id)

### 왜 이렇게 설계했는가

- **Court와 Queue를 분리하지 않은 이유**
  대기열도 결국 "사람이 모여 기다리는 공간"이므로 코트와 같은 추상으로 다루는 것이 더 단순함. UI에서는 `court_type`으로 분기해 W1, W2를 별도 영역에 표시.

- **Game 테이블을 둔 이유**
  같은 코트 위에서 여러 판의 게임이 시간순으로 진행됨. "현재 진행 중인 게임"과 "지난 게임 기록"을 분리해서 보관하려면 Game이라는 별도 엔티티가 필요. `started_at`, `ended_at`으로 기록을 남김.

- **CourtAssignment가 Court가 아닌 Game을 참조하는 이유**
  한 코트에서 여러 게임이 진행되므로, 어떤 게임에 누가 들어갔는지를 추적하려면 game_id를 거쳐야 함. 즉 Game이 "지금 이 코트에 있는 사람들"의 묶음 역할을 한다.

- **현재 코트에 누가 있는지 조회하는 흐름**
  `Court → status != 'FINISHED'인 Game → 그 Game의 CourtAssignment → Player`

## 주요 기능

### 1. 플레이어 관리
- 플레이어 등록 / 전체 조회 / 단건 조회 / 수정 / 삭제
- 성별, 급수 기준 필터링 (체크박스)
- 참석 여부(`is_attended`) 토글

### 2. 코트 관리
- 코트 목록 조회 (활성 코트 + 대기 코트 한꺼번에)
- 코트별 현재 게임에 배정된 플레이어 조회

### 3. 게임 관리
- 코트에 게임 생성 (status=BEFORE)
- 게임 시작 (status=PLAYING, started_at 기록)
- 게임 종료 (status=FINISHED, ended_at 기록)
- 플레이어를 게임에 배정 / 제거
- 코트 간 플레이어 이동 (이전 코트의 현재 게임에서 제거 → 새 코트의 현재 게임에 추가)

### 4. 웹 UI
- 플레이어 목록 표시 (오른쪽 사이드)
- 코트 보드 표시 (1, 2, 3, 4, W1, W2)
- 클릭 또는 드래그 앤 드롭으로 플레이어 배치
- 반응형 UI 구성

### 5. 인증과 권한
- 관리자 로그인 (이름 + 비밀번호) → 모든 기능 사용 가능
- LOGIN AS PLAYER (보기 모드) → 코트 관리 화면을 readOnly로만 볼 수 있음

## 데이터베이스 설계

> 실제 적용된 schema.sql 기준. 모든 테이블은 InnoDB / utf8mb4.

### admin
관리자 계정 정보를 저장한다.

```sql
CREATE TABLE `admin` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);
```

- 학습 단계에서는 평문으로 저장하지만, 인증 단계 도입 시 BCrypt 등으로 해시 저장으로 전환할 것.

### player
플레이어 정보를 저장한다.

```sql
CREATE TABLE `player` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(50) NOT NULL,
  `sex` ENUM('M','W') NOT NULL,
  `level` VARCHAR(10) NOT NULL,
  `is_attended` BIT NOT NULL DEFAULT 0,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);
```

- `sex` : ENUM으로 'M' 또는 'W'만 허용. DB 차원의 유효성 검사 효과.
- `level` : 'S', 'A', 'B' 같은 급수 문자열.
- `is_attended` : 모임 당일 출석 여부. 출석한 사람만 코트 배정 대상으로 본다.

### court
코트 정보를 저장한다. **활성 코트와 대기 코트가 하나의 테이블에 함께 존재.**

```sql
CREATE TABLE `court` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(20) NOT NULL,
  `court_type` ENUM('ACTIVE','WAITING') NOT NULL,
  PRIMARY KEY (`id`)
);
```

- `court_type='ACTIVE'` : 실제 게임이 진행되는 코트 (1, 2, 3, 4)
- `court_type='WAITING'` : 대기 공간 (W1, W2)

### game
코트 위에서 진행되는 한 판의 게임 정보를 저장한다.

```sql
CREATE TABLE `game` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `court_id` INT NOT NULL,
  `status` ENUM('BEFORE','PLAYING','FINISHED') NOT NULL DEFAULT 'BEFORE',
  `started_at` DATETIME DEFAULT NULL,
  `ended_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `FK_court_id` FOREIGN KEY (`court_id`) REFERENCES `court` (`id`)
);
```

- `status` 흐름 : `BEFORE` (선수 배치 중) → `PLAYING` (시작 후) → `FINISHED` (종료)
- 한 코트에서 여러 게임이 시간순으로 일어나므로, 현재 진행 중인 게임을 찾을 때는 `status != 'FINISHED'` 조건으로 조회한다.
- **불변 조건 (Service 레벨에서 보장)** : 한 코트당 `status != 'FINISHED'`인 게임은 최대 1개여야 한다.

### court_assignment
게임에 배정된 플레이어 정보를 저장한다. **Court가 아닌 Game을 참조함에 주의.**

```sql
CREATE TABLE `court_assignment` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `game_id` INT NOT NULL,
  `player_id` INT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UQ_game_player` (`game_id`, `player_id`),
  CONSTRAINT `FK_game_id` FOREIGN KEY (`game_id`) REFERENCES `game` (`id`),
  CONSTRAINT `FK_player_id` FOREIGN KEY (`player_id`) REFERENCES `player` (`id`)
);
```

- `UNIQUE(game_id, player_id)` : 한 게임에 같은 플레이어가 중복 배정되는 것을 DB 레벨에서 차단.
- "코트에서 플레이어 제거"는 곧 해당 게임의 court_assignment 레코드를 삭제하는 것.

## API 설계

### Admin API

```http
POST /api/admin/login
```

### Player API

```http
GET    /api/players                  # 전체 조회 (필터 query param: sex, level, attended)
GET    /api/players/{id}             # 단건 조회
POST   /api/players                  # 등록
PUT    /api/players/{id}             # 수정
PATCH  /api/players/{id}/attendance  # 참석 여부 토글
DELETE /api/players/{id}             # 삭제
```

### Court API

```http
GET /api/courts          # 전체 코트 조회 (활성+대기, 각 코트의 현재 게임 + 배정 플레이어 포함)
GET /api/courts/{id}     # 코트 단건 조회
```

### Game API

```http
POST   /api/courts/{courtId}/games                              # 해당 코트에 새 게임 생성 (BEFORE)
PATCH  /api/games/{gameId}/start                                # 게임 시작 (PLAYING, started_at 기록)
PATCH  /api/games/{gameId}/end                                  # 게임 종료 (FINISHED, ended_at 기록)
POST   /api/games/{gameId}/players/{playerId}                   # 게임에 플레이어 배정
DELETE /api/games/{gameId}/players/{playerId}                   # 게임에서 플레이어 제거
PATCH  /api/games/{gameId}/players/{playerId}/move/{toGameId}   # 다른 게임으로 이동
```

> **헬퍼 API (UI 단순화용)**
> 프론트는 보통 "코트에 사람 넣기"처럼 사고하므로, 내부적으로 현재 게임을 찾아주는 단축 API를 둘 수 있다.
> ```http
> POST   /api/courts/{courtId}/players/{playerId}     # 해당 코트의 현재 게임에 플레이어 추가 (없으면 BEFORE 상태로 자동 생성)
> DELETE /api/courts/{courtId}/players/{playerId}     # 해당 코트의 현재 게임에서 플레이어 제거
> ```

## 응답 형식 예시

### PlayerResponseDto

```json
{
  "id": 1,
  "name": "이재웅",
  "sex": "M",
  "level": "A",
  "attended": true
}
```

### CourtResponseDto

```json
{
  "id": 1,
  "name": "1",
  "courtType": "ACTIVE",
  "currentGame": {
    "id": 12,
    "status": "PLAYING",
    "startedAt": "2025-05-09T10:30:00",
    "players": [
      { "id": 1, "name": "이재웅", "sex": "M", "level": "A" },
      { "id": 3, "name": "김범근", "sex": "M", "level": "A" }
    ]
  }
}
```

### GameResponseDto

```json
{
  "id": 12,
  "courtId": 1,
  "status": "PLAYING",
  "startedAt": "2025-05-09T10:30:00",
  "endedAt": null,
  "players": [
    { "id": 1, "name": "이재웅", "sex": "M", "level": "A" }
  ]
}
```

## 개발 우선순위 (Vertical Slicing)

각 단계는 백엔드 API → 프론트 UI까지 한 번에 완성한다.
완성된 단계는 브라우저에서 직접 동작 확인이 가능해야 한다.

### 1단계: Player
- 백엔드: Player CRUD + 참석 토글 API
- 프론트: 플레이어 리스트 표시 / [+] 등록 / 항목 클릭 → 수정·삭제

### 2단계: Court
- 백엔드: Court 전체/단건 조회 API
- 프론트: 코트 보드(1~4, W1, W2) 빈 상태로 렌더링

### 3단계: 코트에 플레이어 배치
- 백엔드: Game 자동 생성 + CourtAssignment 추가/제거 API
- 프론트: 플레이어 클릭 → 코트 선택 시 배치, 코트의 플레이어 클릭 → 제거
- (이 단계에서 코트 간 이동도 함께)

### 4단계: 게임 라이프사이클
- 백엔드: 게임 시작/종료 API (started_at, ended_at)
- 프론트: 코트별 시작/종료 버튼, 진행 시간 표시

### 5단계: 폴리싱
- 드래그 앤 드롭으로 배치 방식 업그레이드
- 반응형 UI 다듬기
- 필터(성별/급수) 추가

### 6단계: 인증 추가
- 백엔드: Admin 로그인 API + 쓰기 API에 권한 체크
- 프론트: 로그인 화면 + LOGIN AS PLAYER(읽기 전용) 분기
- 1~5단계 동안엔 인증 없이 바로 코트 관리 화면 진입 (임시 진입점)

## 공통 코딩 규칙

이 규칙은 백엔드/프론트 양쪽에 공통으로 적용된다. 언어별 추가 규칙은 각 폴더의 `AGENTS.md`에 있다.

- 불필요하게 복잡한 구조를 만들지 않는다.
- 기능이 작을 때는 단순한 구조를 우선한다.
- Controller, Service, Repository 역할을 명확히 분리한다. (백엔드)
- Entity를 API 응답으로 직접 반환하지 않는다.
- Request DTO, Response DTO를 사용한다.
- 예외 메시지는 원인을 알 수 있게 작성한다.
- 프론트엔드와 백엔드의 필드명은 최대한 일치시킨다.

## 공통 주의할 점

- **Game을 거치지 않고 Court에 직접 플레이어를 배정할 수 없다.** 항상 `Court → 현재 Game → CourtAssignment`의 경로로 접근한다.
- **한 코트당 진행 중인 게임은 최대 1개.** `status != 'FINISHED'`인 Game을 한 코트에서 둘 이상 만들지 않도록 보장한다.
- CORS 설정은 React와 Spring Boot를 분리 실행할 때 필요하다.
- 비밀번호는 학습 단계에서는 평문이지만, 인증 단계 도입 시 BCrypt 등으로 해시 저장으로 전환한다.

## 기술 스택 요약

### Backend (어느 정도 할 줄 앎)
- Java 17, Spring Boot 3, Spring Web, Spring Data JPA, MySQL, Lombok, Gradle
- 자세한 가이드는 `backend/AGENTS.md` 참조

### Frontend (기초만 아는 상태, React는 기초부터 배우는 중)
- React, JavaScript, Axios, CSS / Bootstrap / Tailwind 중 선택
- 자세한 가이드는 `front/AGENTS.md` 참조

### Database
- MySQL (로컬 개발 환경 기준)

### 배포 (추후)
- AWS EC2 : Spring Boot 서버 호스팅
- AWS CloudFront : React 빌드 결과물(정적 파일) 배포
