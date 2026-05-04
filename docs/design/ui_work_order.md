# 와드로그 UI/UX 고도화 작업지시서

이 문서는 기능 구현 완료 후 와드로그 UI/UX를 어떤 순서와 범위로 고도화할지 정의한다. 목적은 Kotlin/Jetpack Compose 코드를 즉시 수정하는 것이 아니라, 이후 Design Phase에서 안전하게 UI 작업을 진행하기 위한 기준을 제공하는 것이다.

## 1. Design Phase 전체 원칙

- 기능 로직, DB, DAO, Repository, ViewModel, Navigation 계약은 변경하지 않는다.
- OpenAI API 직접 호출, 서버, Firebase, Supabase, OCR, 네이버 관련 기능은 추가하지 않는다.
- Material 3 기본 컴포넌트를 우선 사용한다.
- 입력 속도, 엄지 접근성, 명확한 데이터 표현을 우선한다.
- 모든 앱 문구는 한국어를 기본으로 하며 CrossFit 용어는 영어 표기를 유지한다.
- 삭제, 초기화, Import 덮어쓰기 등 위험 액션은 확인 다이얼로그를 사용한다.
- UI 변경 후에는 작은 화면, 다크 테마, 라이트 테마, 긴 텍스트, 빈 상태를 확인한다.

## 2. 권장 진행 순서

1. Design Phase 1: Theme Foundation
2. Design Phase 2: Shared Components
3. Design Phase 3: Main Navigation & Home
4. Design Phase 4: WOD Input & Result Input
5. Design Phase 5: Detail, Compare, Prompt, Report
6. Design Phase 6: Profile, Lifestyle, Settings, Backup
7. Design Phase 7: QA Polish

## 3. Design Phase 1: Theme Foundation

### 목표

- Material 3 theme의 색상, 타입, shape 적용 기준을 정리한다.
- 다크 스포츠 감성과 라이트 테마 대응 기준을 만든다.

### 수정 가능 범위

- Theme, ColorScheme, Typography, Shape 관련 UI 파일
- 기존 화면이 참조하는 스타일 계층

### 수정 금지 범위

- 데이터 모델, DB, DAO, Repository, ViewModel
- Navigation route와 화면 인자 계약
- 기능 동작 및 저장 로직
- Gradle 의존성 추가

### 검증 방법

- 앱이 기존 기능 흐름대로 실행되는지 확인한다.
- 다크/라이트 테마에서 주요 텍스트와 버튼 대비를 확인한다.
- 기존 화면 이동과 저장 동작에 변화가 없는지 확인한다.

### 완료 기준

- Theme 적용 기준이 일관된다.
- 주요 화면에서 색상, 타입, shape가 튀지 않는다.
- 기능 테스트 결과가 Theme 변경 전과 동일하다.

## 4. Design Phase 2: Shared Components

### 목표

- 공통 Scaffold, TopBar, BottomBar, Card, Button, TextField, Chip, Dialog 기준을 정리한다.
- 반복 UI를 공통 컴포넌트로 정돈하되 화면별 기능 흐름은 유지한다.

### 수정 가능 범위

- 공통 presentation component 파일
- 화면 내부의 단순 UI 치환
- EmptyState, ConfirmDialog, MetricCard 같은 표시 전용 컴포넌트

### 수정 금지 범위

- 저장, 삭제, 복사, Import/Export 실행 로직
- ViewModel state 구조 변경
- Navigation 목적지 변경
- 외부 디자인 라이브러리 추가

### 검증 방법

- 모든 주요 버튼이 기존 액션을 그대로 호출하는지 확인한다.
- 삭제/초기화 액션이 확인 다이얼로그를 거치는지 확인한다.
- TalkBack 라벨이 주요 컴포넌트에 적용 가능한지 점검한다.

### 완료 기준

- 화면 간 버튼, 카드, 입력 필드, Chip 스타일이 일관된다.
- 공통 컴포넌트 도입 후 기존 기능 흐름이 유지된다.

## 5. Design Phase 3: Main Navigation & Home

### 목표

- Home, Calendar, 주요 진입 흐름을 빠른 기록 중심으로 고도화한다.
- 하단 네비게이션과 주요 액션의 엄지 접근성을 개선한다.

### 수정 가능 범위

- Home 화면 정보 구조
- Calendar의 날짜 상태 표시
- BottomBar, 빠른 추가 액션 배치

### 수정 금지 범위

- Navigation route 이름과 인자 계약
- 날짜별 조회 쿼리, 최근 3회 조회 로직
- 데이터 저장 정책

### 검증 방법

- 오늘 WOD 추가 흐름이 1-2회 탭으로 시작되는지 확인한다.
- 기록이 있는 날짜와 없는 날짜가 명확히 구분되는지 확인한다.
- 기록 없는 날짜에서 새 WOD 작성 흐름이 보이는지 확인한다.

### 완료 기준

- Home에서 오늘 기록, 최근 기록, 빠른 추가 액션이 즉시 보인다.
- Calendar에서 날짜 상태와 다음 행동이 명확하다.

## 6. Design Phase 4: WOD Input & Result Input

### 목표

- WOD 작성과 결과 기록 화면을 운동 직후 빠르게 입력할 수 있게 정리한다.
- WOD Type, Score Type에 따라 필요한 필드만 드러내는 UX를 적용한다.

### 수정 가능 범위

- 입력 필드 순서와 그룹핑
- WOD Type, Score Type, RxStatus Chip 표현
- 저장 버튼의 하단 고정 또는 접근성 개선
- 오류/빈 값 안내 문구

### 수정 금지 범위

- 입력 데이터 모델
- 저장/수정/삭제 로직
- 자동 파싱, OCR, 클립보드 자동 분석 기능 추가

### 검증 방법

- 필수 입력 누락 시 한국어 오류 문구가 보이는지 확인한다.
- Score Type 선택에 따라 관련 필드만 보이는지 확인한다.
- 사용자가 모르는 선택값을 비워둘 수 있는지 확인한다.

### 완료 기준

- 입력 부담이 줄고 핵심 저장 액션이 명확하다.
- 결과 유형별 입력 UI가 불필요한 필드를 과하게 노출하지 않는다.

## 7. Design Phase 5: Detail, Compare, Prompt, Report

### 목표

- WOD 상세, 최근 3회 비교, ChatGPT 질문지, GPT 답변 저장/조회 화면의 읽기 경험을 개선한다.

### 수정 가능 범위

- WOD Detail 카드 구조
- Compare의 MetricCard, ComparisonRow 구성
- Prompt 미리보기, 복사 버튼, 복사 완료 피드백
- Report 긴 글 표시와 섹션 구조

### 수정 금지 범위

- AnalysisSummary 계산 로직
- PromptGenerator 출력 내용의 기능 계약
- GPT 답변 저장 방식
- OpenAI API 호출 추가

### 검증 방법

- 서로 다른 WOD를 좋다/나쁘다로 단정하지 않는지 확인한다.
- 질문지 복사 완료 피드백이 제공되는지 확인한다.
- 긴 GPT 답변이 스크롤과 섹션으로 읽히는지 확인한다.

### 완료 기준

- 상세 화면에서 WOD 원문, 구조화 정보, 결과, 메모, GPT 연결 상태가 명확하다.
- Prompt와 Report는 긴 텍스트를 다루기 편하다.

## 8. Design Phase 6: Profile, Lifestyle, Settings, Backup

### 목표

- 프로필, 주간 생활습관, 설정, 백업/복구 흐름을 단순하고 안전하게 정리한다.

### 수정 가능 범위

- Profile 입력 구조와 저장 피드백
- Lifestyle의 주간 단위 안내
- Settings의 앱 정보, 백업/복구, 초기화 액션 배치
- 백업/복구 위험 안내 문구와 다이얼로그

### 수정 금지 범위

- JSON Export/Import 데이터 형식
- 백업/복구 실행 로직
- 계정 로그인, 클라우드 동기화, Google Play 배포 기능 추가

### 검증 방법

- 프로필 저장 상태가 명확한지 확인한다.
- Lifestyle이 주간 단위 입력임을 화면에서 알 수 있는지 확인한다.
- Import 전 데이터 덮어쓰기 위험이 명시되는지 확인한다.

### 완료 기준

- 설정 화면이 앱 정보와 데이터 관리 중심으로 단순하게 구성된다.
- 데이터 손실 가능 액션이 일반 액션과 구분된다.

## 9. Design Phase 7: QA Polish

### 목표

- 작은 화면, 다크 테마, 라이트 테마, 한국어 문구, 접근성, 빈 상태, 오류 상태를 최종 점검한다.

### 수정 가능 범위

- 여백, 줄바꿈, 문구, 빈 상태, 오류 상태
- TalkBack 라벨과 터치 영역
- 긴 텍스트 표시 안정성

### 수정 금지 범위

- 기능 로직 변경
- 데이터 저장 정책 변경
- 새로운 MVP 제외 기능 추가

### 검증 방법

- `qa_checklist.md`의 수동 QA 항목을 확인한다.
- 긴 WOD 제목, 긴 WOD 원문, 긴 GPT 답변을 입력해 본다.
- 다크/라이트 테마와 글자 크기 확대에서 확인한다.

### 완료 기준

- 사용자가 첫 실행, 기록, 비교, 질문지 복사, GPT 답변 저장, 백업/복구 흐름을 막힘 없이 진행할 수 있다.
- 문구와 접근성의 큰 결함이 없다.
