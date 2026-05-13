# CLAUDE.md (Frontend)

이 파일은 **프론트엔드 작업 시에만 적용되는 가이드**다.
프로젝트 공통 정보(도메인 모델, DB 스키마, API 설계, 개발 우선순위 등)는 상위 디렉토리의 `cc/CLAUDE.md`를 먼저 참조할 것.

## 개발자 컨텍스트

- 프론트엔드 숙련도: **초중급** (HTML, CSS, JS는 기초 이상, **React는 기초부터 배우는 중**)
- 따라서 코드를 작성할 때는 **개념 설명을 함께** 제공해야 한다.
- 코드 자체보다 "왜 이렇게 쓰는가"를 함께 설명하는 게 더 중요하다.

## 학습 단계 가이드 (중요)

이 프로젝트는 학습 목적도 함께 가지므로, AI 도구는 다음 규칙을 따른다.

- 새로운 React 개념(useState, useEffect, props, 조건부 렌더링, key, 이벤트 핸들러 등)을 **처음 사용할 때**는 그 개념이 무엇이고 왜 쓰는지 주석으로 설명한다.
- API 호출, 상태 변화, 조건부 렌더링이 일어나는 줄에는 한 줄짜리 주석을 붙인다.
- **이미 설명한 개념은 반복 설명하지 않는다.** (코드가 지저분해짐)
- 한 컴포넌트에 처음 등장하는 패턴이라도, 같은 프로젝트 안에서 이미 다른 파일에 설명이 있다면 "X.jsx 참고" 식으로 짧게 처리한다.
- 너무 일반적인 JavaScript 문법(const, =>, .map() 등)은 설명하지 않는다. 사용자가 이미 아는 부분이다.

## 기술 스택

- React (CRA 또는 Vite)
- JavaScript (TypeScript는 추후 도입 검토)
- Axios (HTTP 클라이언트)
- CSS / Bootstrap / Tailwind 중 선택

## 폴더 구조 권장

```text
src
├── api
│   ├── adminApi.js
│   ├── playerApi.js
│   ├── courtApi.js
│   └── gameApi.js
├── components
│   ├── PlayerList.jsx
│   ├── CourtBoard.jsx
│   ├── CourtCard.jsx
│   └── WaitingArea.jsx
├── pages
│   ├── LoginPage.jsx
│   └── CourtManagerPage.jsx
├── styles
└── App.jsx
```

## 빌드 / 실행 명령어

```bash
npm install           # 의존성 설치
npm start             # 개발 서버 실행 (CRA 기준, http://localhost:3000)
npm run build         # 프로덕션 빌드 (build/ 또는 dist/ 생성)
npm test              # 테스트 실행
```

> Vite를 쓰는 경우: `npm run dev`로 개발 서버 실행.

## 프론트엔드 코딩 규칙

### 폴더 분리
- API 호출 코드는 `api` 폴더에 분리한다. 컴포넌트 안에서 직접 `axios.get(...)`을 호출하지 않는다.
- 컴포넌트는 역할별로 작게 나눈다.

### 상태 관리
- 상태는 처음에는 React 기본 상태 관리(`useState`)로 충분하다.
- 전역 상태 관리 라이브러리(Redux, Zustand 등)는 정말 필요해질 때 도입한다.

### UX 우선순위
- 드래그 앤 드롭은 핵심 CRUD 기능이 안정화된 뒤 적용한다 (개발 우선순위 5단계 참조).
- 클릭 기반 배치를 먼저 안정시킨 후 드래그로 업그레이드.

### 백엔드와의 정합성
- 필드명은 백엔드 응답과 최대한 일치시킨다.
- `enum` 값(`'M'/'W'`, `'ACTIVE'/'WAITING'`, `'BEFORE'/'PLAYING'/'FINISHED'`)은 화면 표시 직전에만 한국어로 변환한다.

## 자주 마주칠 함정 (학습 포인트)

- **state를 직접 수정하면 안 됨**: `players.push(...)` 대신 `setPlayers([...players, newPlayer])`를 써야 React가 변화를 감지한다.
- **useEffect 의존성 배열 누락**: 상태나 props를 사용하는데 의존성에 안 넣으면 stale closure 문제 발생. ESLint 경고를 무시하지 말 것.
- **key 누락**: `.map()`으로 리스트 렌더링할 때 `key`를 빠뜨리면 React가 비효율적으로 동작하고 경고가 뜸. `key={player.id}` 형태로 고유값을 줄 것.
- **CORS 에러**: 백엔드가 분리 실행 중이면 발생. 백엔드 측 CORS 설정으로 해결한다 (백엔드 가이드 참조).
- **비동기 처리 누락**: `axios.get()`은 Promise를 반환. `await` 또는 `.then()`을 빼먹으면 데이터가 안 나옴.
