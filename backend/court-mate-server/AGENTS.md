# AGENTS.md (Backend)

이 파일은 **백엔드 작업 시에만 적용되는 가이드**다.
프로젝트 공통 정보(도메인 모델, DB 스키마, API 설계, 개발 우선순위 등)는 상위 디렉토리의 `cc/AGENTS.md`를 먼저 참조할 것.

## 개발자 컨텍스트

- 백엔드 숙련도: **중급** (Java Spring Boot, MySQL은 어느 정도 다룰 줄 앎)
- 따라서 기본 개념 설명보다는 **"왜 이 구조를 쓰는가"**, **"실무에서는 어떻게 다루는가"** 중심으로 설명한다.
- 학습 의도가 있으므로, 흔히 빠지는 함정(N+1, 트랜잭션 경계 누락, Enum Ordinal 사용 등)이 있으면 짚고 간다.

## 기술 스택

- Java 17
- Spring Boot 3
- Spring Web
- Spring Data JPA
- MySQL
- Lombok
- Gradle (빌드 도구)

## 패키지 구조 권장

```text
src/main/java/com/minton/courtmanager
├── CourtManagerApplication.java
├── controller
│   ├── AdminController.java
│   ├── PlayerController.java
│   ├── CourtController.java
│   └── GameController.java
├── service
│   ├── AdminService.java
│   ├── PlayerService.java
│   ├── CourtService.java
│   └── GameService.java
├── repository
│   ├── AdminRepository.java
│   ├── PlayerRepository.java
│   ├── CourtRepository.java
│   ├── GameRepository.java
│   └── CourtAssignmentRepository.java
├── domain
│   ├── Admin.java
│   ├── Player.java
│   ├── Court.java
│   ├── Game.java
│   └── CourtAssignment.java
├── dto
│   ├── request
│   └── response
└── exception
```

## 빌드 / 실행 명령어

```bash
./gradlew bootRun     # 개발 서버 실행
./gradlew build       # 빌드
./gradlew test        # 테스트 실행
./gradlew clean       # 빌드 결과물 정리
```

## 백엔드 코딩 규칙

### 계층 책임
- **Controller**: 요청과 응답 처리만 담당. 비즈니스 로직 금지.
- **Service**: 비즈니스 로직 작성. 트랜잭션 경계는 여기서 잡는다.
- **Repository**: DB 접근만 처리.

### 트랜잭션
- 조회 메서드는 `@Transactional(readOnly = true)`를 사용한다.
- 등록, 수정, 삭제 메서드는 `@Transactional`을 사용한다.

### Optional 처리
- `Optional`을 Controller까지 그대로 반환하지 않는다.
- Service에서 `.orElseThrow(() -> new XxxNotFoundException(...))` 형태로 풀어준다.

### HTTP 응답
- 삭제 API는 성공 시 `204 No Content` 응답을 우선 고려한다.
- 등록 API는 `201 Created` + Location 헤더를 고려한다 (선택).

### JPA 매핑
- Enum 컬럼은 `@Enumerated(EnumType.STRING)`을 사용한다.
  → `ORDINAL`은 enum 순서가 바뀌면 데이터가 깨지므로 **절대 사용 금지**.
- `BIT(1)` 컬럼은 Java에서 `boolean`으로 매핑한다.

### Entity 보호
- Entity를 API 응답으로 직접 반환하지 않는다.
- Request DTO, Response DTO를 반드시 사용한다.
- DTO를 그대로 저장하지 말고, Service에서 Entity로 변환해서 저장한다.

## 백엔드 주의할 점

- `@PathVariable`을 사용할 때는 URL 경로와 메서드 파라미터를 일치시킨다.
- `/api/players/{id}` 형태에서는 `@RequestParam`이 아니라 `@PathVariable`을 사용한다.
- 삭제 API는 실제 삭제 전에 해당 데이터가 존재하는지 확인한다 (`existsById` 또는 `findById` 후 분기).
- **Game을 거치지 않고 Court에 직접 플레이어를 배정할 수 없다.** 항상 `Court → 현재 Game → CourtAssignment`의 경로로 접근한다.
- **한 코트당 진행 중인 게임은 최대 1개.** `status != 'FINISHED'`인 Game을 한 코트에서 둘 이상 만들지 않도록 Service 레벨에서 보장한다.
- 비밀번호는 학습 단계에서는 평문이지만, 인증 단계 도입 시 BCrypt 등으로 해시 저장으로 전환한다.
- CORS 설정은 React 개발 서버(보통 `localhost:3000` 또는 `:5173`)와 Spring Boot(`:8080`)를 분리 실행할 때 필요하다. `@CrossOrigin` 또는 `WebMvcConfigurer`로 설정.

## 자주 마주칠 함정 (학습 포인트)

- **N+1 쿼리**: `Court → Game → CourtAssignment → Player`로 join이 깊으니, 전체 조회 시 `@EntityGraph` 또는 `JOIN FETCH`를 고려.
- **양방향 연관관계의 무한 루프**: JSON 직렬화 시 순환 참조 발생 가능. DTO로 변환해 응답하면 자연스럽게 해결됨.
- **트랜잭션 누락**: `@Transactional` 없는 Service 메서드에서 lazy loading 호출 시 `LazyInitializationException` 발생.
