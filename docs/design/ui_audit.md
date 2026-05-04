# 와드로그 UI 감사 문서

작성일: 2026-05-04  
범위: Design Phase 0 - 현재 UI 감사  
대상 코드: `app/src/main/java/com/wodlog/app/presentation`, `app/src/main/java/com/wodlog/app/WodlogApp.kt`

## 1. 현재 UI 구조 요약

현재 앱은 Jetpack Compose와 Material 3 기본 컴포넌트로 구성되어 있다. `MainActivity`에서 `WodlogTheme`을 적용하고, `WodlogApp`이 `Scaffold`와 `NavigationBar`를 제공한다. 최상위 탭은 홈, 캘린더, 비교, 설정 4개이며, 상세/작성/결과/질문지/리포트/프로필/생활습관/라이선스 화면은 Navigation route로 진입한다.

화면별 UI는 대부분 각 Screen 파일 내부에 직접 작성되어 있다. 공통 Scaffold, TopBar, Card, Button, TextField, Chip, EmptyState, ConfirmDialog 같은 와드로그 전용 공통 컴포넌트는 아직 없다. 일부 화면에서 `EnumSelector`, `MetricRow`, `SectionHeader` 같은 private helper가 반복 구현되어 있으나 화면 간 재사용되지는 않는다.

테마는 `WodlogTheme`에서 light/dark `ColorScheme`만 간단히 지정한다. Typography와 Shape는 Material 기본값을 그대로 사용한다. 디자인 문서의 "다크 스포츠 감성", 카드 반경, Primary/Error 역할, Surface 계층, 라이트 테마 대비 같은 세부 기준은 아직 충분히 반영되지 않았다.

전체적으로 현재 UI는 기능 Phase 완료 후의 기초 화면 상태다. 화면 이동과 입력 기능은 존재하지만, 디자인 문서가 요구하는 빠른 기록 중심 정보 구조, 한국어 문구, 하단 주요 액션, 위험 액션 확인, 긴 텍스트 읽기 경험, 접근성 상태 표현은 Design Phase에서 다시 정리해야 한다.

## 2. 화면별 구현 상태

| 화면 | 현재 상태 | 디자인 문서 대비 평가 |
|---|---|---|
| Home | `홈`, `Phase 0 placeholder`, `WOD 작성` 버튼만 제공한다. 오늘 날짜, 오늘 기록 여부, 최근 3회 기록, 질문지 진입점은 없다. | 기능 진입점 수준이다. Home UX 명세와 가장 큰 차이가 있다. |
| Calendar | 월 이동, 월간 그리드, 기록 날짜 표시, 선택 날짜 WOD 목록, WOD 생성/상세 진입을 제공한다. | 캘린더 기능은 있으나 `Prev`, `Next`, `Selected`, `Create WOD`, `No WOD recorded...` 등 영어 문구가 많고 선택 날짜 요약 UI가 카드화되어 있지 않다. |
| WOD Edit | 날짜, 제목, WOD Type, 원문, 메모, 섹션, Movement, 저장을 제공한다. | 입력 기능은 넓게 구현되어 있으나 영어 라벨이 많고 필드가 길게 나열된다. 저장 버튼은 하단 고정이 아니며 Movement 카드/접기 구조가 없다. |
| Result Edit | Score Type, 모든 결과 수치 필드, Rx Status, Condition, 메모, 저장을 제공한다. | Score Type별 필요한 필드만 노출해야 한다는 UX와 다르다. 영어 라벨과 오류 문구가 많고 저장 버튼 하단 접근성이 약하다. |
| WOD Detail | WOD 제목/날짜/유형, 원문, 메모, 섹션, Movement, 결과, GPT report 상태, Result/Prompt/GPT report 액션을 표시한다. | 정보는 표시되지만 카드/섹션 위계가 약하고, 편집/삭제 액션이 없다. 결과 없음/GPT 답변 없음 문구가 영어다. |
| Compare | 최근 3회 비교 카드, 카테고리 비중, 중립 요약, 새로고침을 제공한다. | 핵심 기능은 가장 많이 반영되어 있다. 다만 MetricCard/ComparisonRow 공통화, 단위 표기, 영어 라벨(`WOD type`, `Total reps`, `Load volume`) 개선이 필요하다. |
| Prompt | 질문지 설명, 로컬/API 미사용 안내, 질문지 미리보기, 복사 버튼, 복사 메시지를 제공한다. | 운영 원칙 안내는 좋으나 화면 문구가 영어다. 복사 버튼은 하단 고정이 아니고 긴 질문지 미리보기 전용 카드 구조가 약하다. |
| Report | GPT 답변 붙여넣기, 저장된 답변 목록, 새 리포트, 저장/삭제를 제공한다. | 기능은 있으나 영어 문구가 많다. 삭제 확인 다이얼로그가 없고, 삭제 버튼이 일반 OutlinedButton으로 배치되어 위험 액션 구분이 약하다. |
| Profile | 키, 몸무게, 크로스핏 시작일, 운동 기간, 저장을 제공한다. | 비교적 한국어화되어 있다. 다만 저장 버튼 하단 접근성, 날짜 입력 안내, 경력 표현은 정돈 여지가 있다. |
| Lifestyle | 주 시작일, 식습관, 음주/흡연 토글, 주량/흡연량, 수면, 메모, 저장을 제공한다. | 기능은 있으나 화면 제목/상태/라벨/오류/버튼이 대부분 영어다. 음주/흡연 세부 입력은 비활성 처리만 하고 화면 밀도 최적화는 부족하다. |
| Settings | 프로필/생활습관, 앱 정보, 데이터 내보내기/가져오기, 라이선스, 초기화 placeholder를 제공한다. | 한국어 문구가 비교적 많고 그룹도 있다. Import/초기화 위험 다이얼로그, 위험 액션 시각 분리, 가져오기 결과의 한국어화가 필요하다. |
| License | MIT License 원문을 표시한다. | 라이선스 화면 특성상 영어 원문 표시는 허용 가능하다. TopBar/뒤로가기 구조는 별도 정리가 필요하다. |

## 3. 디자인 문서 대비 부족한 점

1. 공통 화면 구조가 부족하다.  
   `WodLogScaffold`, `WodLogTopBar`, `WodLogBottomBar` 기준이 아직 구현되지 않아 화면별 패딩, 제목, 하단 액션 위치가 제각각이다.

2. 주요 액션의 엄지 접근성이 부족하다.  
   저장, 복사, 추가 버튼이 화면 하단 고정 영역이 아니라 일반 Column 흐름 끝이나 우측 정렬로 배치되어 있다.

3. 카드 기반 정보 구조가 약하다.  
   Calendar 선택 날짜, WOD Detail, Prompt, Report, Settings 그룹 대부분이 단순 Text/Column 중심이다. Compare 일부만 `OutlinedCard`를 사용한다.

4. 위험 액션 처리가 부족하다.  
   Report 삭제는 확인 다이얼로그 없이 바로 ViewModel delete로 연결된다. Settings 초기화는 비활성 placeholder지만, 향후 DangerActionButton과 ConfirmDialog 기준이 필요하다.

5. 입력 화면의 정보 밀도가 높다.  
   WOD Edit와 Result Edit는 모든 필드를 한 화면에 길게 나열한다. Result Edit는 Score Type 선택과 무관하게 Time, rounds, reps, load, distance, calories를 모두 노출한다.

6. 상태 UI가 일관되지 않다.  
   로딩, 빈 상태, 오류 상태가 화면마다 단순 Text로 처리된다. EmptyState, ErrorState, LoadingState 컴포넌트로 정리할 필요가 있다.

7. 접근성 기준 반영이 부족하다.  
   BottomBar 아이콘이 비어 있고, Chip/Enum 선택 상태는 시각적 Button/OutlinedButton 차이에 의존한다. 아이콘 버튼 라벨, 선택됨 상태 설명, 위험 액션 라벨은 아직 체계화되어 있지 않다.

8. 테마 Foundation이 얕다.  
   ColorScheme 일부만 지정되어 있고 Shape, Typography, Surface 계층, Error/Warning/Success 역할, 카드 radius 기준이 명시적으로 반영되지 않았다.

## 4. 한국어 문구 정책 위반 여부

위반 항목이 많다. 앱 표시 문구는 한국어가 기본이어야 하나, 주요 화면에 영어 문구가 그대로 노출된다.

대표 예시:

- Home: `Phase 0 placeholder`
- Calendar: `Prev`, `Next`, `Selected`, `No WOD recorded for this date.`, `Create WOD`
- WOD Edit: `WOD Edit`, `Basic`, `Date yyyy-MM-dd`, `Title`, `Raw text`, `Add section`, `Remove movement`, `Save WOD`
- Result Edit: `Result Edit`, `Score Type`, `Time seconds`, `Save Result`, 영어 오류 문구
- WOD Detail: `Loading WOD...`, `No result recorded yet.`, `GPT reports`, `Prompt`
- Prompt: `Copy this prompt and paste it into ChatGPT yourself.`, `Copy prompt`
- Report: `Paste the answer...`, `New report`, `Pasted GPT answer`, `Delete`
- Lifestyle: `Lifestyle`, `Saved weekly log`, `Diet summary`, `Save lifestyle`
- Settings 일부: `Imported WOD`, `Imported Movement`, preview 결과 일부 영어

프로필과 설정 일부는 한국어 기준을 어느 정도 따르고 있다. 다음 Design Phase에서는 화면별 문구를 `copywriting_guide.md` 기준으로 먼저 정리해야 한다.

## 5. CrossFit 용어 표기 정책 위반 여부

CrossFit 핵심 용어를 영어로 유지한다는 원칙은 큰 방향에서 지켜지고 있다. `WOD`, `AMRAP`, `EMOM`, `RFT`, `Rx`, `Scaled`, `RPE`, `GPT`, `ChatGPT` 등은 영어로 표시된다.

다만 enum 값을 그대로 노출하는 방식은 개선이 필요하다.

- `FOR_TIME`, `ROUNDS_REPS`, `LOWER_BODY`처럼 내부 enum name이 그대로 보일 가능성이 있다.
- `WOD type: ${item.wodType.name}`, `Rx status: ${item.rxStatus?.name}`처럼 개발자 친화 표기가 남아 있다.
- `MovementCategory`도 `WEIGHTLIFTING`, `LOWER_BODY` 등 내부 이름이 노출된다.

정책상 영어 유지가 맞더라도 사용자 표기는 `For Time`, `Rounds + reps`, `Lower body`처럼 읽기 좋은 display label로 변환해야 한다. CrossFit 용어를 한국어로 억지 번역한 문제는 거의 없지만, 내부 enum 표기 노출은 정책 미준수로 본다.

## 6. 공통 컴포넌트화가 필요한 부분

우선 공통화 후보:

| 컴포넌트 | 필요 이유 | 현재 반복/대상 |
|---|---|---|
| `WodLogScaffold` | 안전 영역, 패딩, TopBar/BottomBar, 화면 상태를 일관화 | 모든 화면 |
| `WodLogTopBar` | 하위 화면 뒤로가기, 제목, 보조 액션 정리 | WOD Edit, Detail, Result, Prompt, Report, Profile, Lifestyle, License |
| `WodLogBottomBar` | 아이콘/라벨/선택 상태/접근성 정리 | `WodlogApp` |
| `WodLogCard` | 카드 기반 정보 묶음 일관화 | Home, Calendar, Detail, Compare, Prompt, Report, Settings |
| `SectionHeader` | 화면 내 섹션 제목과 보조 액션 통일 | WOD Edit, Detail, Report, Settings |
| `PrimaryActionButton` | 저장/추가/복사 하단 액션 통일 | WOD Edit, Result, Prompt, Report, Profile, Lifestyle |
| `DangerActionButton` | 삭제/초기화/가져오기 위험 액션 구분 | Report, Settings, 향후 WOD Detail |
| `ConfirmDialog` | 위험 액션 전 확인 처리 | Report 삭제, 향후 WOD 삭제/초기화/Import |
| `WodLogTextField` | 라벨, 단위, 오류, 선택 입력 안내 통일 | WOD Edit, Result, Profile, Lifestyle, Report |
| `WodTypeChip` | WOD Type display label과 선택 상태 통일 | WOD Edit, Detail, Compare |
| `ScoreTypeChip` | Score Type별 필드 노출 UX 연결 | Result Edit |
| `RxScaledChip` | Rx/Scaled/Custom/Unknown 표시 통일 | Result Edit, Detail, Compare |
| `EmptyState` | 빈 상태 문구와 다음 행동 통일 | Home, Calendar, Compare, Prompt, Report |
| `MetricCard` | 수치와 단위 표기 통일 | Compare, Detail 결과, Home 최근 기록 |
| `ComparisonRow` | 최근 3회 병렬 비교 표현 | Compare |
| `PromptPreviewCard` | 긴 질문지 읽기/복사 구조 | Prompt |
| `ReportCard` | 긴 GPT 답변 조회 구조 | WOD Detail, Report |

## 7. 화면별 개선 우선순위

1. Home  
   현재 placeholder 수준이므로 첫 Design 화면 작업으로 적합하다. 오늘 기록, 빠른 WOD 추가, 최근 기록, 질문지 진입을 구성해야 한다.

2. WOD Edit / Result Edit  
   앱의 핵심 사용 흐름이다. 입력 속도, 하단 저장, 한국어 라벨, Score Type별 필드 노출이 사용자 체감에 가장 크다.

3. Shared Components / Theme  
   화면을 개별로 고치기 전에 Theme와 공통 컴포넌트를 먼저 잡아야 중복 수정을 줄일 수 있다.

4. WOD Detail  
   기록 확인과 후속 액션의 중심 화면이다. 원문, Movement, 결과, GPT 연결 상태를 카드와 섹션으로 분리해야 한다.

5. Prompt / Report  
   긴 텍스트 읽기, 복사/저장 피드백, OpenAI API 미사용 안내를 한국어로 명확히 정리해야 한다.

6. Calendar / Compare  
   기능은 어느 정도 구현되어 있으므로 시각 구조, 문구, 단위, EmptyState 중심으로 정리한다.

7. Profile / Lifestyle / Settings  
   Profile과 Settings는 일부 한국어화되어 있어 후순위 가능하다. Lifestyle은 영어 문구가 많아 Phase 6에서 함께 정리한다.

## 8. Design Phase별 작업 제안

### Design Phase 1: Theme Foundation

- `WodlogTheme`의 ColorScheme, Typography, Shape 기준을 정리한다.
- 다크 테마를 기본 경험으로 보강하고 라이트 테마 대비를 함께 확인한다.
- 카드 radius, Surface/Background 계층, Error 색상 역할을 명확히 한다.
- 기능 로직, ViewModel, Navigation 계약은 변경하지 않는다.

### Design Phase 2: Shared Components

- `WodLogScaffold`, `WodLogTopBar`, `WodLogBottomBar`, `WodLogCard`를 만든다.
- `PrimaryActionButton`, `SecondaryActionButton`, `DangerActionButton`, `ConfirmDialog`를 정의한다.
- `EmptyState`, `MetricCard`, `WodTypeChip`, `ScoreTypeChip`, `RxScaledChip` display label 기준을 만든다.
- 화면 내부 helper로 반복된 `EnumSelector`, `MetricRow`, `SectionHeader`를 공통화 후보로 치환한다.

### Design Phase 3: Main Navigation & Home

- BottomBar에 아이콘과 접근성 라벨을 추가한다.
- Home을 placeholder에서 실제 정보 구조로 전환한다.
- Calendar의 영어 문구를 한국어화하고 선택 날짜 카드/빈 상태를 개선한다.
- 오늘 WOD 추가와 기록 없는 날짜 WOD 추가 흐름을 더 선명하게 만든다.

### Design Phase 4: WOD Input & Result Input

- WOD Edit의 기본 정보, WOD 원문, Movement 입력을 섹션/카드 구조로 나눈다.
- Movement 입력은 반복 카드 또는 접기 가능한 구조를 검토한다.
- Result Edit는 Score Type별 관련 필드만 보여준다.
- 저장 버튼을 하단 액션 영역으로 정리하고 오류 문구를 한국어로 바꾼다.

### Design Phase 5: Detail, Compare, Prompt, Report

- WOD Detail을 원문, Movement, 결과, 메모, GPT 상태 카드로 재구성한다.
- Compare는 MetricCard/ComparisonRow를 적용하고 단위를 명확히 표시한다.
- Prompt는 질문지 미리보기와 복사 버튼을 하단 중심으로 정리한다.
- Report는 GPT 답변 입력/목록/읽기 구조를 분리하고 삭제 확인 다이얼로그를 적용한다.

### Design Phase 6: Profile, Lifestyle, Settings, Backup

- Profile 문구와 저장 피드백을 다듬는다.
- Lifestyle을 한국어화하고 주간 단위 입력, 음주/흡연 세부 입력 노출을 정리한다.
- Settings의 데이터 관리, 라이선스, 위험 구역을 카드/섹션으로 정리한다.
- Import 적용 전 확인 문구와 초기화 위험 액션 기준을 준비한다.

### Design Phase 7: QA Polish

- `qa_checklist.md` 기준으로 작은 화면, 긴 텍스트, 다크/라이트 테마, 글자 크기 확대, 빈 상태, 오류 상태를 점검한다.
- 한국어 문구와 CrossFit 용어 표기를 전체 화면에서 재검수한다.
- 위험 액션 다이얼로그와 TalkBack 라벨을 확인한다.

