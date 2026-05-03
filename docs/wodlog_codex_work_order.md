# 와드로그 Codex 작업지시서

작성일: 2026-05-03  
대상: Codex  
프로젝트명: **와드로그**  
목표: 서버 없는 Android 크로스핏 WOD 기록 앱 MVP 구현  
배포 방식: GitHub Releases APK  
운영 원칙: 완전 무료, 로컬 저장, OpenAI API 직접 호출 없음

---

## 1. Codex에게 주는 최상위 지시

이 프로젝트는 크로스핏 WOD 기록 앱 **와드로그**의 MVP를 구현하는 작업이다.

반드시 다음 원칙을 지켜라.

1. 서버를 만들지 마라.
2. OpenAI API를 호출하지 마라.
3. Firebase, Supabase, 외부 백엔드, 유료 API를 붙이지 마라.
4. 네이버카페 크롤링, 로그인 자동화, OCR, 텍스트 자동 파싱은 MVP에서 구현하지 마라.
5. 모든 데이터는 Android 기기 내부 Room/SQLite에 저장하라.
6. ChatGPT 분석은 앱이 질문지를 생성하고 사용자가 복사하는 방식으로 구현하라.
7. ChatGPT 답변은 사용자가 앱에 붙여넣어 저장하는 방식으로 구현하라.
8. GitHub APK 배포가 가능하도록 release build를 준비하라.
9. 오픈소스 프로젝트로 관리할 수 있도록 README와 LICENSE를 포함하라.
10. MVP 범위 밖 기능은 코드에 강하게 결합하지 말고 확장 포인트만 남겨라.

---

## 2. 권장 기술 스택

### 필수 또는 권장

- Kotlin
- Jetpack Compose
- Room
- Kotlin Coroutines
- Flow / StateFlow
- AndroidX Navigation Compose
- Material 3
- MVVM 구조

### 사용하지 말 것

- OpenAI API SDK
- Firebase
- Supabase
- 서버 API 클라이언트
- 광고 SDK
- 결제 SDK
- 네이버 로그인 SDK
- 크롤러
- OCR 라이브러리

---

## 3. 저장소 구조 생성

다음 구조를 기준으로 프로젝트를 구성하라.

```text
wodlog-android/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/wodlog/app/
│       │   ├── MainActivity.kt
│       │   ├── WodLogApp.kt
│       │   ├── data/
│       │   │   ├── local/
│       │   │   ├── dao/
│       │   │   ├── entity/
│       │   │   ├── mapper/
│       │   │   └── repository/
│       │   ├── domain/
│       │   │   ├── model/
│       │   │   ├── repository/
│       │   │   ├── usecase/
│       │   │   └── analysis/
│       │   ├── presentation/
│       │   │   ├── navigation/
│       │   │   ├── theme/
│       │   │   ├── home/
│       │   │   ├── calendar/
│       │   │   ├── wodedit/
│       │   │   ├── woddetail/
│       │   │   ├── compare/
│       │   │   ├── prompt/
│       │   │   ├── report/
│       │   │   ├── profile/
│       │   │   ├── lifestyle/
│       │   │   └── settings/
│       │   └── util/
│       └── res/
├── docs/
│   ├── requirements.md
│   └── codex-work-order.md
├── README.md
├── LICENSE
└── .gitignore
```

---

## 4. 구현 단계

## Phase 0. 프로젝트 초기화

### 작업

1. Android Kotlin 프로젝트 생성
2. Jetpack Compose 활성화
3. Material 3 테마 설정
4. Room 의존성 추가
5. Navigation Compose 추가
6. 앱 이름을 `와드로그`로 설정
7. 패키지명은 `com.wodlog.app` 사용 권장
8. 앱 아이콘 적용
9. README 작성
10. LICENSE 작성

### 산출물

- 실행 가능한 빈 앱
- 하단 또는 상단 기본 네비게이션 구조
- 앱 아이콘 반영
- README.md
- LICENSE

### 수용 기준

- Android Studio에서 빌드 성공
- 앱 설치 후 실행 가능
- 앱 이름이 와드로그로 표시
- 서버 관련 코드 없음

---

## Phase 1. Room 데이터 계층 구현

### 작업

다음 Entity, DAO, Repository를 구현하라.

#### Entity

1. UserProfileEntity
2. WodEntity
3. WodSectionEntity
4. MovementEntryEntity
5. WodResultEntity
6. WeeklyLifestyleEntity
7. AiPromptEntity
8. AiReportEntity

#### Enum 또는 TypeConverter 대상

1. WodType
2. MovementCategory
3. ScoreType
4. RxStatus
5. Condition

#### DAO

1. UserProfileDao
2. WodDao
3. WodSectionDao
4. MovementEntryDao
5. WodResultDao
6. WeeklyLifestyleDao
7. AiPromptDao
8. AiReportDao

#### 주요 쿼리

- 날짜별 WOD 조회
- 월별 WOD 조회
- 최근 WOD 3개 조회
- 특정 WOD의 movement 전체 조회
- 특정 WOD의 result 조회
- 특정 WOD의 AI report 조회
- 주 시작일 기준 lifestyle 조회

### 수용 기준

- Room database build 성공
- 기본 CRUD 동작
- 최근 3회 WOD 조회 가능
- 날짜별 WOD 조회 가능

---

## Phase 2. 도메인 모델 및 매퍼 구현

### 작업

1. Entity와 Domain Model 분리
2. Mapper 구현
3. Repository 인터페이스와 구현체 분리
4. 날짜/시간 유틸 구현
5. 입력 검증 유틸 구현

### 주요 도메인 모델

- UserProfile
- Wod
- WodSection
- MovementEntry
- WodResult
- WeeklyLifestyle
- AiPrompt
- AiReport
- WodWithDetails
- RecentWodComparison

### 수용 기준

- UI가 Entity에 직접 의존하지 않음
- Domain model 기준으로 ViewModel 작성 가능

---

## Phase 3. 기본 네비게이션 및 화면 골격

### 작업

다음 화면 route를 구현하라.

1. HomeScreen
2. CalendarScreen
3. WodEditScreen
4. WodDetailScreen
5. ResultEditScreen
6. CompareScreen
7. PromptScreen
8. ReportEditScreen
9. ProfileScreen
10. LifestyleScreen
11. SettingsScreen

### 네비게이션 권장

- Home
- Calendar
- Compare
- Settings

상세/편집 화면은 route parameter로 이동.

### 수용 기준

- 모든 화면 이동 가능
- 빈 상태 UI 제공
- 뒤로가기 동작 정상

---

## Phase 4. 프로필 기능 구현

### 작업

1. 키 입력
2. 몸무게 입력
3. 크로스핏 시작일 입력
4. 저장 / 수정
5. 경력 자동 계산

### 수용 기준

- 프로필 저장 가능
- 앱 재시작 후 유지
- 시작일 기준 경력 표시 가능

---

## Phase 5. WOD 직접 입력 기능 구현

### 작업

WodEditScreen에서 다음 기능을 구현하라.

1. 날짜 선택
2. 제목 입력
3. WOD 유형 선택
4. 원문 입력
5. 섹션 추가/수정/삭제
6. Movement 추가/수정/삭제
7. Movement별 선택 필드
   - 무게 kg
   - reps
   - sets
   - rounds
   - distance meters
   - calories
   - duration seconds
   - category
   - notes
8. 저장
9. 기존 WOD 수정
10. WOD 삭제

### UX 원칙

- 입력 필드는 최대한 단순하게 유지
- 사용자가 모르는 값은 비워둘 수 있어야 함
- 자유 입력을 허용하되, 분석에 필요한 구조화 필드도 제공

### 수용 기준

- 하나의 WOD에 여러 movement 저장 가능
- 저장 후 상세 화면에서 확인 가능
- 수정/삭제 가능

---

## Phase 6. 결과 기록 기능 구현

### 작업

ResultEditScreen에서 다음 기능 구현.

1. ScoreType 선택
2. Time 입력
3. Rounds + reps 입력
4. Total reps 입력
5. Load 입력
6. Distance 입력
7. Calories 입력
8. RxStatus 선택
9. RPE 입력
10. Condition 선택
11. Memo 입력
12. 저장/수정

### 수용 기준

- WOD별 결과 1개 이상 저장 가능
- MVP에서는 WOD당 대표 결과 1개만 허용해도 됨
- WOD 상세 화면에서 결과 확인 가능

---

## Phase 7. 캘린더 기능 구현

### 작업

1. 월간 캘린더 UI 구현
2. 기록 있는 날짜 표시
3. 선택 날짜 상세 표시
4. 선택 날짜에 기록 없으면 새 WOD 작성 버튼 표시
5. 월 이동 기능 구현

### 수용 기준

- 날짜별 WOD 조회 가능
- 기록 있는 날짜 시각 표시
- 날짜 클릭 후 상세 이동 가능

---

## Phase 8. WOD 상세 화면 구현

### 작업

WodDetailScreen에 다음 표시.

1. 날짜
2. 제목
3. WOD 유형
4. 원문
5. 섹션별 movement 목록
6. 결과
7. Rx 여부
8. RPE
9. 컨디션
10. 메모
11. 편집 버튼
12. 결과 입력/수정 버튼
13. ChatGPT 질문지 생성 버튼
14. GPT 답변 저장/보기 버튼
15. 삭제 버튼

### 수용 기준

- 하나의 날짜 기록을 완전하게 확인 가능
- PromptScreen으로 이동 가능
- ReportEditScreen으로 이동 가능

---

## Phase 9. 주간 식단/생활습관 기능 구현

### 작업

1. 주 시작일 선택
2. 식습관 요약 입력
3. 음주 여부 입력
4. 주 평균 주량 입력
5. 흡연 여부 입력
6. 주 평균 흡연량 입력
7. 평균 수면시간 입력 선택 사항
8. 메모 입력
9. 저장/수정

### 수용 기준

- 주 단위 lifestyle 저장 가능
- PromptGenerator에서 최근 주간 lifestyle을 가져올 수 있음

---

## Phase 10. 최근 3회 비교 및 분석 요약 구현

### 작업

`AnalysisSummaryGenerator`를 구현하라.

계산 항목:

1. 총 movement 수
2. 총 reps
3. 총 load volume
4. 총 distance
5. 총 calories
6. WOD type
7. Rx status
8. RPE
9. 카테고리별 movement count
10. 최근 3회 비교 객체 생성

### 주의

- 서로 다른 WOD를 단순 우열 비교하지 마라.
- 앱 내부 분석은 정량 요약 중심으로 유지하라.
- “평균보다 강하다/약하다” 같은 판단은 ChatGPT 질문에서 추정하도록 맡겨라.

### 수용 기준

- CompareScreen에서 최근 3회 카드 표시
- WOD별 요약 지표 표시
- 데이터가 3개 미만이면 가능한 만큼 표시하고 안내 문구 표시

---

## Phase 11. ChatGPT 질문지 생성 기능 구현

### 작업

`PromptGenerator`를 구현하라.

입력:

- UserProfile
- 최근 3개 WODWithDetails
- 각 WOD의 WodResult
- 최근 WeeklyLifestyle
- AnalysisSummary

출력:

- String promptText

PromptScreen 기능:

1. 질문지 생성
2. 질문지 미리보기
3. 클립보드 복사
4. 생성된 prompt 저장 선택 사항
5. ChatGPT 앱/웹 열기 선택 사항

### 필수 프롬프트 요구

프롬프트는 다음을 포함해야 한다.

- 크로스핏 코치이자 운동 데이터 분석가처럼 답변 요청
- 최근 3회 비교 요청
- 종목별 강점/약점 요청
- 근력/심폐/체조/바벨기술/지구력/회복 평가 요청
- 평균적인 크로스핏터 대비 추정 요청
- 잘한 점/부족한 점 요청
- 다음 2주 개선 방향 요청
- 부상 위험/회복 리스크 요청
- 데이터 부족 시 추정이라고 표시하라는 요청
- 답변 형식 지정

### 수용 기준

- 질문지 복사 버튼 동작
- 클립보드에 정확히 저장
- 앱 외부 API 호출 없음

---

## Phase 12. GPT 답변 저장 기능 구현

### 작업

ReportEditScreen에서 다음 기능 구현.

1. 특정 WOD와 연결
2. GPT 답변 붙여넣기 입력창
3. 사용자 메모 입력
4. 저장
5. 수정
6. 삭제
7. 상세 화면에서 저장된 답변 보기

### 수용 기준

- ChatGPT 답변을 앱 내부에 저장 가능
- 앱 재시작 후에도 유지
- 특정 WOD와 연결됨

---

## Phase 13. 백업 / 복구 구현

### 작업

1. 전체 DB 데이터를 JSON으로 Export
2. JSON 파일 저장 또는 공유 Intent 제공
3. JSON Import
4. Import 전 경고 표시
5. Import 후 데이터 갱신

### 백업 대상

- UserProfile
- Wod
- WodSection
- MovementEntry
- WodResult
- WeeklyLifestyle
- AiPrompt
- AiReport

### 수용 기준

- Export한 파일을 다시 Import할 수 있음
- 기본 데이터 손상 없이 복구 가능

---

## Phase 14. 설정 및 앱 정보

### 작업

SettingsScreen 구현.

표시 항목:

1. 앱 이름
2. 앱 버전
3. GitHub 저장소 링크 placeholder
4. 데이터 내보내기
5. 데이터 가져오기
6. MIT License 보기
7. 앱 데이터 초기화

### 수용 기준

- 설정 화면에서 백업/복구 접근 가능
- License 확인 가능

---

## Phase 15. UI 정리 및 테스트 데이터

### 작업

1. 전체 UI 간격/타이포그래피 정리
2. 빈 상태 문구 추가
3. 오류 상태 문구 추가
4. 테스트용 샘플 WOD 입력 기능은 debug build에서만 허용
5. 실제 release에서는 샘플 자동 삽입 금지

### 수용 기준

- 사용자가 앱을 처음 실행해도 다음 행동을 알 수 있음
- 필수 입력 누락 시 오류 안내

---

## Phase 16. Release APK 준비

### 작업

1. release build 설정
2. 앱 버전명/버전코드 설정
3. release APK 생성 방법 README에 작성
4. GitHub Releases 업로드 절차 README에 작성
5. 서명키 관리 주의사항 문서화

### 수용 기준

- 로컬에서 release APK 빌드 가능
- GitHub Releases에 올릴 수 있는 APK 산출 가능

---

## 5. README에 반드시 포함할 내용

README.md는 다음을 포함해야 한다.

1. 프로젝트명: 와드로그
2. 프로젝트 설명
3. 주요 기능
4. MVP 범위
5. 제외된 기능
6. 기술 스택
7. 빌드 방법
8. APK 설치 방법
9. 백업/복구 안내
10. ChatGPT 질문지 사용 방법
11. 라이선스
12. 향후 로드맵

---

## 6. LICENSE

기본값으로 MIT License를 사용하라.

단, 저작권자 이름은 placeholder로 둔다.

예:

```text
Copyright (c) 2026 WodLog Contributors
```

---

## 7. 앱 아이콘 적용 지시

사용자가 제공한 마지막 아이콘 컨셉을 사용한다.

아이콘 패키지 파일이 있다면 다음을 적용하라.

- `adaptive/ic_launcher_foreground.png`
- `adaptive/ic_launcher_background.png`
- `adaptive/ic_launcher_monochrome.png`
- legacy mipmap 아이콘

---

## 8. 금지 사항

Codex는 다음을 하지 마라.

1. 서버 코드 생성 금지
2. OpenAI API 호출 코드 생성 금지
3. 네이버 로그인 또는 크롤링 코드 생성 금지
4. OCR 코드 생성 금지
5. Firebase 연동 금지
6. Supabase 연동 금지
7. 광고 SDK 추가 금지
8. 결제 SDK 추가 금지
9. 사용자의 네이버 ID/PW 저장 기능 구현 금지
10. 개인정보 처리 서버 전송 구현 금지

---

## 9. 품질 기준

### 코드 품질

- 의미 있는 패키지 분리
- ViewModel에 DB 직접 접근 금지
- Repository 경유
- PromptGenerator는 순수 함수 형태로 테스트 가능하게 구현
- AnalysisSummaryGenerator도 테스트 가능하게 구현

### UI 품질

- 입력 중심 앱이므로 입력 흐름이 단순해야 함
- 필수값과 선택값 구분
- 삭제 전 확인 다이얼로그 제공
- 복사 완료 토스트 또는 스낵바 제공

### 데이터 안정성

- 날짜별 데이터 중복 처리 정책 명확화
- WOD 하나에 여러 movement 저장 가능
- WOD 삭제 시 관련 section, movement, result, prompt, report 처리 필요

---

## 10. 데이터 삭제 정책

WOD 삭제 시 관련 데이터 처리:

- WodSection 삭제
- MovementEntry 삭제
- WodResult 삭제
- AiPrompt 삭제 또는 orphan 방지
- AiReport 삭제 또는 사용자 확인 후 삭제

MVP에서는 WOD 삭제 시 연결 데이터 전체 삭제를 기본으로 한다.

---

## 11. 테스트 시나리오

### 시나리오 1. 첫 실행

1. 앱 실행
2. 프로필 미입력 상태 확인
3. 프로필 입력
4. 홈으로 돌아옴
5. 프로필 정보 유지 확인

### 시나리오 2. WOD 기록

1. 오늘 날짜 선택
2. WOD 추가
3. 유형 For Time 선택
4. 원문 입력
5. Thruster movement 추가
6. Pull-up movement 추가
7. 저장
8. 상세 화면에서 확인

### 시나리오 3. 결과 기록

1. WOD 상세 진입
2. 결과 입력
3. Time 기록 입력
4. Rx 선택
5. RPE 입력
6. 저장
7. 상세에서 결과 확인

### 시나리오 4. 최근 3회 비교

1. WOD 3개 이상 입력
2. CompareScreen 진입
3. 최근 3회 카드 확인
4. 총 reps, load volume, category count 확인

### 시나리오 5. 질문지 생성

1. WOD 상세에서 질문지 생성
2. PromptScreen 확인
3. 복사 버튼 클릭
4. 클립보드 확인

### 시나리오 6. GPT 답변 저장

1. ReportEditScreen 진입
2. ChatGPT 답변 붙여넣기
3. 저장
4. WOD 상세에서 답변 다시 열람

### 시나리오 7. 백업/복구

1. 데이터 Export
2. 앱 데이터 초기화
3. Import
4. 데이터 복구 확인

---

## 12. 향후 확장 대비 인터페이스

MVP에서는 구현하지 않지만 다음 인터페이스를 고려해도 된다.

```kotlin
interface WodParser {
    fun parse(input: String): ParsedWod
}
```

구현체는 MVP에서 만들지 않거나, 수동 입력용 placeholder만 둔다.

```kotlin
class ManualWodParser : WodParser
class PlainTextWodParser : WodParser // future
class OcrWodParser : WodParser // future
```

단, OCR 또는 네이버 관련 의존성은 추가하지 않는다.

---

## 13. Codex 최종 목표

최종적으로 Codex는 다음 결과물을 만들어야 한다.

1. 빌드 가능한 Android 프로젝트
2. 서버 없는 로컬 DB 기반 앱
3. 직접 WOD 입력 기능
4. 결과 기록 기능
5. 캘린더 조회 기능
6. 최근 3회 비교 기능
7. ChatGPT 질문지 생성 및 복사 기능
8. GPT 답변 저장 기능
9. 주간 식단/생활습관 입력 기능
10. JSON 백업/복구 기능
11. README.md
12. LICENSE
13. GitHub APK 배포 준비 문서

---

## 14. 구현 우선순위 요약

1. 프로젝트 생성
2. Room DB
3. 기본 화면 네비게이션
4. 프로필
5. WOD 입력
6. 결과 입력
7. 캘린더
8. WOD 상세
9. 최근 3회 비교
10. 질문지 생성/복사
11. GPT 답변 저장
12. 식단/생활습관
13. 백업/복구
14. README/LICENSE/release 정리

---

## 15. 완료 정의

아래 조건을 만족하면 MVP 구현 완료로 본다.

- 앱이 Android 기기에서 오프라인으로 실행된다.
- 사용자가 WOD를 직접 입력할 수 있다.
- 날짜별 기록을 캘린더에서 볼 수 있다.
- 최근 3회 기록 비교가 가능하다.
- ChatGPT 분석 질문지를 생성하고 복사할 수 있다.
- ChatGPT 답변을 붙여넣어 저장할 수 있다.
- 데이터가 Room에 저장된다.
- JSON 백업/복구가 가능하다.
- release APK를 만들 수 있다.
- README와 MIT LICENSE가 포함된다.
