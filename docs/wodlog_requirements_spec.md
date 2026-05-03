# 와드로그 요구사항명세서

작성일: 2026-05-03  
대상: Codex / Android 개발자 / 기획 검토자  
앱 이름: **와드로그**  
목표 플랫폼: **Android 우선**  
운영 원칙: **완전 무료, 서버 없음, 로컬 저장, GitHub APK 배포**

---

## 1. 프로젝트 개요

**와드로그**는 크로스핏 사용자가 오늘 수행한 WOD를 기록하고, 이전 기록과 비교하여 성장 방향성을 확인할 수 있도록 돕는 Android 앱이다.

초기 버전에서는 네이버카페 자동 수집, OpenAI API 직접 연동, 서버 동기화, OCR 기능을 제외한다. 사용자는 WOD를 직접 입력하고, 앱은 기록을 구조화하여 로컬 DB에 저장한다. 이후 앱은 최근 기록을 바탕으로 ChatGPT에 붙여넣을 수 있는 고품질 분석 질문지를 자동 생성한다. 사용자는 ChatGPT의 답변을 복사하여 앱 내부에 저장할 수 있다.

---

## 2. 핵심 목표

1. 사용자가 매일 크로스핏 WOD를 쉽게 기록할 수 있어야 한다.
2. 기록은 날짜별로 캘린더에서 확인할 수 있어야 한다.
3. 최근 3회 이상의 기록을 비교할 수 있어야 한다.
4. 앱 내부에서 WOD 데이터를 구조화해야 한다.
5. ChatGPT 분석용 질문지를 자동 생성하고 복사할 수 있어야 한다.
6. ChatGPT 답변을 앱에 다시 붙여넣어 저장할 수 있어야 한다.
7. 모든 기능은 서버 없이 로컬에서 동작해야 한다.
8. 구현, 배포, 운영 비용은 무료여야 한다.
9. GitHub Releases를 통해 APK 파일로 배포한다.
10. 오픈소스로 공개할 수 있는 구조여야 한다.

---

## 3. 확정된 의사결정

| 항목 | 결정 |
|---|---|
| 앱 이름 | 와드로그 |
| 플랫폼 | Android 우선 |
| UI 우선순위 | 모바일 중심 |
| PC 지원 | MVP 제외, 장기 확장 가능성만 고려 |
| 서버 | 없음 |
| 데이터 저장 | 로컬 DB |
| 추천 DB | Room / SQLite |
| 추천 언어 | Kotlin |
| 추천 UI | Jetpack Compose |
| 추천 아키텍처 | MVVM + Repository |
| OpenAI API 직접 호출 | 사용하지 않음 |
| ChatGPT 분석 방식 | 질문지 생성 후 사용자가 복사하여 ChatGPT에 붙여넣기 |
| ChatGPT 답변 저장 | 사용자가 답변 복사 후 앱에 붙여넣어 저장 |
| WOD 입력 v1 | 직접 입력 |
| 네이버카페 자동 크롤링 | MVP 제외 |
| 네이버 로그인 기반 크롤링 | 일단 제외 |
| 텍스트 복사 파싱 | 추후 확장 |
| 스크린샷 OCR 파싱 | 추후 확장 |
| 배포 | GitHub Releases APK |
| 오픈소스 | 진행 |
| 라이선스 | MIT License 권장. 필요 시 무라이선스 가능 |
| 개인정보 처리 안내 | MVP 범위에서 제외 |
| 아이콘 | 마지막 선택 아이콘: 어두운 배경 + 흰색 W + 바벨 + 붉은 원판 |

---

## 4. MVP 포함 기능

### 4.1 사용자 프로필

- 키 입력
- 몸무게 입력
- 크로스핏 시작일 입력
- 위 값들은 고정 분석값으로 사용되지만 사용자가 언제든 변경 가능해야 한다.
- 앱은 크로스핏 시작일 기준 경력을 자동 계산해야 한다.

### 4.2 WOD 직접 입력

- 날짜 선택
- WOD 제목 입력
- WOD 유형 선택
- WOD 원문 입력
- 섹션별 입력 가능
  - Warm-up
  - Strength
  - Skill
  - Metcon
  - Accessory
  - 기타
- 운동 종목 입력
- 종목별 필요한 정보 입력
  - 무게
  - 횟수
  - 세트
  - 라운드
  - 거리
  - 칼로리
  - 시간
  - 난이도 또는 Scaled 옵션
  - 메모
- 사용자가 직접 수정 가능해야 한다.
- 입력값을 모르는 경우 비워둘 수 있어야 한다.

### 4.3 WOD 결과 기록

- 결과 유형 선택
  - Time
  - AMRAP rounds + reps
  - Reps
  - Load
  - Distance
  - Calories
  - 기타
- Rx 여부
  - Rx
  - Scaled
  - Custom
  - Unknown
- 기록값 입력
- RPE 입력
- 컨디션 입력
- 자유 메모 입력

### 4.4 캘린더 UI

- 월간 캘린더 제공
- 운동 기록이 있는 날짜 표시
- 날짜 선택 시 해당 날짜의 WOD와 결과 표시
- 날짜별 GPT 분석 답변 저장 여부 표시 가능

### 4.5 최근 기록 비교

- 최소 최근 3회 WOD 비교
- 전전 WOD / 전 WOD / 현재 WOD 형태로 표시
- 동일 WOD 반복 여부를 판단할 수 있으면 표시
- 서로 다른 WOD의 경우 종목 카테고리, 볼륨, 결과 유형, 메모를 중심으로 비교

### 4.6 식단 및 생활습관 입력

- 1주 식습관 요약 입력
- 음주 여부 입력
- 1주 평균 주량 입력
- 흡연 여부 입력
- 1주 평균 흡연량 입력
- 평균 수면 시간 입력은 선택 사항
- MVP에서는 자유 텍스트 + 간단 수치 입력 중심으로 구현

### 4.7 ChatGPT 질문지 생성

앱이 저장된 데이터를 바탕으로 ChatGPT용 질문지를 자동 생성해야 한다.

질문지에는 다음 정보가 포함되어야 한다.

- 사용자 프로필
- 크로스핏 경력
- 최근 3회 WOD
- 각 WOD의 구조화 데이터
- 결과 기록
- Rx/Scaled 여부
- RPE
- 사용자 메모
- 식단/음주/흡연 정보
- 앱 내부 계산 요약 지표
- 원하는 답변 형식

기능:

- 질문지 미리보기
- 질문지 복사 버튼
- ChatGPT 앱 또는 웹으로 이동 버튼은 선택 사항

### 4.8 GPT 답변 저장

- 사용자가 ChatGPT 답변을 복사해서 앱에 붙여넣을 수 있어야 한다.
- 답변은 특정 날짜 또는 특정 WOD에 연결되어 저장되어야 한다.
- 저장된 답변은 다시 열람 가능해야 한다.
- 질문지 원문과 답변을 함께 저장할 수 있으면 좋다.

### 4.9 백업 / 복구

- 로컬 데이터 JSON Export
- 로컬 데이터 JSON Import
- CSV Export는 선택 사항

---

## 5. MVP 제외 기능

다음 기능은 MVP에서 구현하지 않는다.

1. 네이버카페 로그인 기반 자동 크롤링
2. 네이버카페 공개글 검색 API 연동
3. OCR 기반 스크린샷 파싱
4. 클립보드 WOD 텍스트 자동 파싱
5. OpenAI API 직접 호출
6. 서버 저장
7. 계정 로그인
8. 클라우드 동기화
9. Google Play 배포
10. 박스별 랭킹
11. 친구 기능
12. Wear OS 연동
13. 심박수, 수면, 헬스 데이터 연동
14. 유료 결제 기능
15. 광고
16. 개인정보 서버 전송

---

## 6. 향후 확장 설계

### 6.1 텍스트 복사 기반 WOD 파싱

향후 사용자가 네이버카페나 다른 앱에서 WOD 텍스트를 복사하여 붙여넣으면 앱이 자동으로 구조화하는 기능을 추가할 수 있다.

확장 포인트:

- `WodParser` 인터페이스 정의
- `ManualWodParser`
- `PlainTextWodParser`
- `OcrWodParser`

MVP에서는 직접 입력만 구현하되, 데이터 모델은 자동 파싱 결과를 수용할 수 있게 설계한다.

### 6.2 OCR 기반 파싱

향후 사용자가 스크린샷을 선택하면 OCR로 WOD 텍스트를 추출하고 파싱할 수 있다. MVP에서는 제외한다.

### 6.3 네이버카페 크롤링

현재 요구사항에서는 구현하지 않는다. 향후에도 기본 기능으로 의존하지 않는다.

주의사항:

- 네이버 로그인 정보 저장은 보안 리스크가 큼
- 약관 및 정책 이슈 가능성이 있음
- 차단, CAPTCHA, HTML 변경에 취약함
- 자동 우회 또는 차단 회피 기능은 구현하지 않는다.

### 6.4 로컬 LLM

완전 무료를 유지하면서 앱 내부 AI 분석을 강화하고 싶다면, 장기적으로 온디바이스 LLM을 검토할 수 있다. 단, MVP에서는 제외한다.

---

## 7. 데이터 모델 초안

### 7.1 UserProfile

```kotlin
data class UserProfile(
    val id: Long,
    val heightCm: Double?,
    val weightKg: Double?,
    val crossfitStartDate: LocalDate?,
    val createdAt: Instant,
    val updatedAt: Instant
)
```

### 7.2 Wod

```kotlin
data class Wod(
    val id: Long,
    val date: LocalDate,
    val title: String,
    val type: WodType,
    val rawText: String?,
    val notes: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)
```

### 7.3 WodType

```kotlin
enum class WodType {
    FOR_TIME,
    AMRAP,
    EMOM,
    RFT,
    STRENGTH,
    SKILL,
    INTERVAL,
    CHIPPER,
    TABATA,
    OTHER
}
```

### 7.4 WodSection

```kotlin
data class WodSection(
    val id: Long,
    val wodId: Long,
    val name: String,
    val orderIndex: Int
)
```

### 7.5 MovementEntry

```kotlin
data class MovementEntry(
    val id: Long,
    val wodId: Long,
    val sectionId: Long?,
    val movementName: String,
    val category: MovementCategory?,
    val weightKg: Double?,
    val reps: Int?,
    val sets: Int?,
    val rounds: Int?,
    val distanceMeters: Double?,
    val calories: Double?,
    val durationSeconds: Int?,
    val orderIndex: Int,
    val notes: String?
)
```

### 7.6 MovementCategory

```kotlin
enum class MovementCategory {
    STRENGTH,
    CARDIO,
    GYMNASTICS,
    WEIGHTLIFTING,
    BARBELL,
    DUMBBELL,
    KETTLEBELL,
    BODYWEIGHT,
    ENGINE,
    GRIP,
    CORE,
    LOWER_BODY,
    UPPER_BODY,
    OTHER
}
```

### 7.7 WodResult

```kotlin
data class WodResult(
    val id: Long,
    val wodId: Long,
    val scoreType: ScoreType,
    val timeSeconds: Int?,
    val rounds: Int?,
    val extraReps: Int?,
    val totalReps: Int?,
    val loadKg: Double?,
    val distanceMeters: Double?,
    val calories: Double?,
    val rxStatus: RxStatus,
    val rpe: Int?,
    val condition: Condition?,
    val memo: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)
```

### 7.8 ScoreType

```kotlin
enum class ScoreType {
    TIME,
    ROUNDS_REPS,
    REPS,
    LOAD,
    DISTANCE,
    CALORIES,
    NONE,
    OTHER
}
```

### 7.9 RxStatus

```kotlin
enum class RxStatus {
    RX,
    SCALED,
    CUSTOM,
    UNKNOWN
}
```

### 7.10 Condition

```kotlin
enum class Condition {
    GREAT,
    GOOD,
    NORMAL,
    TIRED,
    PAIN,
    UNKNOWN
}
```

### 7.11 WeeklyLifestyle

```kotlin
data class WeeklyLifestyle(
    val id: Long,
    val weekStartDate: LocalDate,
    val mealSummary: String?,
    val alcohol: Boolean?,
    val alcoholAmountPerWeek: String?,
    val smoking: Boolean?,
    val smokingAmountPerWeek: String?,
    val sleepAverageHours: Double?,
    val notes: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)
```

### 7.12 AiPrompt

```kotlin
data class AiPrompt(
    val id: Long,
    val targetWodId: Long,
    val promptText: String,
    val createdAt: Instant
)
```

### 7.13 AiReport

```kotlin
data class AiReport(
    val id: Long,
    val targetWodId: Long,
    val promptId: Long?,
    val reportText: String,
    val userMemo: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)
```

---

## 8. 화면 요구사항

### 8.1 홈 화면

- 오늘 날짜
- 오늘 기록 여부
- 오늘 WOD 추가 버튼
- 최근 3회 기록 카드
- ChatGPT 질문지 생성 바로가기

### 8.2 캘린더 화면

- 월간 캘린더
- 기록 있는 날짜 표시
- 날짜 선택 시 상세 화면 이동
- 기록 없는 날짜는 새 WOD 작성 버튼 표시

### 8.3 WOD 작성 / 편집 화면

- 날짜
- 제목
- WOD 유형
- 원문 텍스트
- 섹션 추가
- 종목 추가
- 각 종목별 필드 입력
- 저장 버튼

### 8.4 결과 입력 화면

- 결과 유형
- 기록값
- Rx/Scaled/Custom
- RPE
- 컨디션
- 메모
- 저장 버튼

### 8.5 WOD 상세 화면

- WOD 정보
- 종목 리스트
- 결과
- 메모
- GPT 질문지 생성 버튼
- GPT 답변 저장/보기 버튼
- 편집 버튼
- 삭제 버튼

### 8.6 비교 분석 화면

- 전전 WOD / 전 WOD / 현재 WOD 카드
- WOD 유형 비교
- 종목 수 비교
- 총 반복수 비교
- 총 중량 볼륨 비교
- 거리/칼로리 비교
- 카테고리 비중 비교
- 간단한 앱 내부 분석 요약

### 8.7 ChatGPT 질문지 화면

- 생성된 질문지 미리보기
- 복사 버튼
- ChatGPT 열기 버튼 선택 사항
- 질문지 저장 여부 선택 사항

### 8.8 GPT 답변 저장 화면

- 연결된 WOD 정보
- 질문지 원문 보기 선택 사항
- GPT 답변 붙여넣기 영역
- 사용자 메모
- 저장 버튼

### 8.9 프로필 설정 화면

- 키
- 몸무게
- 크로스핏 시작일
- 저장 버튼

### 8.10 주간 식단/생활습관 화면

- 주 시작일
- 식습관 요약
- 음주 여부
- 주 평균 주량
- 흡연 여부
- 주 평균 흡연량
- 평균 수면시간 선택 사항
- 메모

### 8.11 설정 화면

- 앱 이름 / 버전
- GitHub 저장소 링크
- 데이터 내보내기
- 데이터 가져오기
- MIT License 보기
- 앱 데이터 초기화

---

## 9. 앱 내부 분석 요구사항

MVP에서 앱은 ChatGPT 수준의 자연어 분석을 직접 만들 필요는 없다. 다만 질문지를 좋게 만들기 위해 간단한 계산 요약은 생성해야 한다.

계산 항목:

- 최근 3개 WOD 목록
- WOD별 총 movement 수
- WOD별 총 reps
- WOD별 총 load volume: `weightKg * reps * sets`
- 거리 합계
- 칼로리 합계
- 시간 기록
- Rx/Scaled 여부
- RPE
- 카테고리별 movement 개수
- 카테고리별 대략적 비중

비교 원칙:

- 동일한 WOD 제목 또는 동일한 구조가 반복되면 직접 기록 비교 가능
- 서로 다른 WOD는 단순 우열 비교하지 않음
- 서로 다른 WOD는 종목 카테고리, 볼륨, 결과 유형, 메모를 바탕으로 병목 추정만 제공
- 평균적인 크로스핏터 대비 평가는 앱 내부에서 단정하지 않음
- 평균 대비 평가는 ChatGPT 프롬프트에서 “추정”으로 요청

---

## 10. ChatGPT 질문지 템플릿 요구사항

앱은 다음 구조로 질문지를 생성해야 한다.

```text
너는 크로스핏 코치이자 운동 데이터 분석가처럼 답변해줘.
아래 사용자의 최근 WOD 3회 기록과 신체 정보, 식습관 정보를 바탕으로 분석해줘.

목표:
1. 전전 WOD, 전 WOD, 현재 WOD를 비교
2. 종목별 강점/약점 분석
3. 근력, 심폐, 체조, 바벨 테크닉, 지구력 관점 평가
4. 평균적인 크로스핏터 대비 충분한 부분과 부족한 부분 추정
5. 현재 WOD에서 잘한 점과 부족한 점
6. 다음 2주간 개선 방향
7. 부상 위험이나 회복 리스크가 있다면 알려줘
8. 과도하게 단정하지 말고, 데이터가 부족한 부분은 “추정”이라고 표시해줘

사용자 정보:
- 키:
- 몸무게:
- 크로스핏 시작일:
- 경력:

최근 1주 생활 정보:
- 식습관:
- 음주 여부 및 주량:
- 흡연 여부 및 흡연량:
- 평균 수면시간:

최근 3회 WOD:

[전전 WOD]
- 날짜:
- 유형:
- 원문:
- 구조화된 종목:
- 결과:
- Rx 여부:
- RPE:
- 메모:
- 앱 계산 요약:

[전 WOD]
...

[현재 WOD]
...

답변 형식:
- 한줄 총평
- 최근 3회 비교표
- 종목별 분석
- 능력 영역별 점수: 근력/심폐/체조/바벨기술/지구력/회복
- 잘한 점
- 부족한 점
- 다음 훈련 추천
- 다음에 기록하면 좋은 추가 데이터
```

---

## 11. 무료 운영 제약

1. 서버를 사용하지 않는다.
2. 유료 API를 사용하지 않는다.
3. OpenAI API를 호출하지 않는다.
4. Firebase, Supabase 등 무료 티어라도 운영 의존성을 만들지 않는다.
5. 광고 SDK를 사용하지 않는다.
6. 결제 SDK를 사용하지 않는다.
7. 계정 시스템을 만들지 않는다.
8. 데이터는 사용자 기기 내부에 저장한다.
9. 백업은 사용자가 직접 파일로 내보내고 가져오는 방식으로 한다.
10. APK는 GitHub Releases에서 제공한다.

---

## 12. 오픈소스 및 라이선스

오픈소스로 공개한다.

라이선스는 MIT License를 권장한다. 사용자가 원할 경우 라이선스를 두지 않을 수도 있으나, 오픈소스 배포 목적이라면 MIT License를 포함하는 편이 명확하다.

Codex 작업 시 기본값:

- `LICENSE` 파일 생성
- MIT License 사용
- 앱 이름은 `와드로그`
- 패키지명 예시: `com.wodlog.app`

---

## 13. 아이콘

앱 아이콘은 대화 중 생성된 마지막 컨셉을 사용한다.

컨셉:

- 어두운 rounded square 배경
- 중앙에 흰색 W
- 바벨 모티프
- 양쪽에 붉은 원판
- 크로스핏, 기록, WOD 느낌을 전달

아이콘 패키지 파일:

- `wodlog_icon_master_1024.png`
- `adaptive/ic_launcher_foreground.png`
- `adaptive/ic_launcher_background.png`
- `adaptive/ic_launcher_monochrome.png`
- Android legacy mipmap 파일들

---

## 14. 수용 기준

MVP 완료 기준:

1. 앱을 설치하면 서버 없이 실행된다.
2. 사용자는 프로필을 입력하고 수정할 수 있다.
3. 사용자는 날짜별 WOD를 직접 입력할 수 있다.
4. 사용자는 WOD 결과를 저장할 수 있다.
5. 캘린더에서 기록한 날짜를 확인할 수 있다.
6. 날짜를 선택하면 해당 기록을 확인할 수 있다.
7. 최근 3회 기록을 비교할 수 있다.
8. ChatGPT 질문지가 자동 생성된다.
9. 질문지를 클립보드에 복사할 수 있다.
10. ChatGPT 답변을 붙여넣어 저장할 수 있다.
11. 저장된 GPT 답변을 다시 볼 수 있다.
12. 데이터는 Room/SQLite에 저장된다.
13. JSON 백업 내보내기와 가져오기가 가능하다.
14. 앱은 GitHub APK로 배포 가능한 release build를 생성할 수 있다.
15. 오픈소스 저장소에 README와 LICENSE가 포함된다.

---

## 15. 비기능 요구사항

### 성능

- 일반 Android 기기에서 입력, 조회, 캘린더 이동이 지연 없이 동작해야 한다.
- 서버 통신이 없으므로 오프라인에서 모든 MVP 기능이 동작해야 한다.

### 안정성

- 앱 종료 후에도 데이터가 보존되어야 한다.
- WOD 삭제 시 연결 데이터 처리 정책이 명확해야 한다.

### 보안

- 민감한 계정 정보 저장 없음
- 외부 서버 전송 없음
- 로컬 데이터는 사용자가 직접 백업 파일로 내보내기 전까지 기기 내부에만 존재

### 유지보수

- 기능별 패키지 분리
- ViewModel, Repository, DAO 분리
- PromptGenerator는 독립 클래스로 분리하여 테스트 가능하게 설계

---

## 16. 권장 프로젝트 구조

```text
wodlog-android/
├── app/
│   ├── src/main/java/com/wodlog/app/
│   │   ├── data/
│   │   │   ├── local/
│   │   │   ├── dao/
│   │   │   ├── entity/
│   │   │   └── repository/
│   │   ├── domain/
│   │   │   ├── model/
│   │   │   ├── usecase/
│   │   │   └── analysis/
│   │   ├── presentation/
│   │   │   ├── home/
│   │   │   ├── calendar/
│   │   │   ├── wodedit/
│   │   │   ├── woddetail/
│   │   │   ├── compare/
│   │   │   ├── prompt/
│   │   │   ├── report/
│   │   │   ├── profile/
│   │   │   ├── lifestyle/
│   │   │   └── settings/
│   │   └── util/
│   └── src/main/res/
├── README.md
├── LICENSE
└── docs/
    ├── requirements.md
    └── codex-work-order.md
```

---

## 17. 최종 요약

와드로그 MVP는 “무료 운영 가능한 서버 없는 Android 크로스핏 WOD 기록 앱”이다. 사용자는 WOD를 직접 입력하고, 앱은 기록을 구조화하여 캘린더와 최근 3회 비교를 제공한다. ChatGPT 분석은 API가 아니라 질문지 자동 생성과 복사 방식으로 해결한다. 사용자는 ChatGPT 답변을 다시 앱에 저장할 수 있다. 이후 텍스트 파싱, OCR, 네이버카페 연동은 확장 후보로만 남긴다.
