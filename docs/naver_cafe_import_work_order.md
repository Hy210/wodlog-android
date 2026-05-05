# 네이버카페 WOD 가져오기 Codex 작업지시서

작성일: 2026-05-05  
대상 앱: 와드로그 Android  
대상 기능: 네이버카페 WOD 가져오기  
릴리즈 후보: v0.2.0  
주의: 이 문서는 향후 구현을 위한 작업지시서이며, 현재 Phase 0에서는 문서만 작성한다.

---

## 1. 전체 구현 원칙

- WebView 기반 사용자 주도 가져오기 방식으로 구현한다.
- 코드 변경은 Phase 단위로 진행한다.
- 기능 로직과 UI를 과도하게 한 번에 수정하지 않는다.
- 각 Phase 후 `assembleDebug`를 확인한다.
- 각 Phase 후 가상 디바이스에서 수동 테스트한다.
- git commit은 사용자가 직접 수행한다.
- 서버를 추가하지 않는다.
- OpenAI API를 추가하지 않는다.
- 네이버 ID/PW를 저장하거나 자동 로그인하지 않는다.

---

## 2. 구현 Phase 계획

### Naver Cafe Import Phase 0. 설계 문서 작성

- `docs/naver_cafe_import_spec.md`
- `docs/naver_cafe_import_work_order.md`
- 코드 수정 없음

완료 기준:

- 제품/UX/데이터/WebView/보안/테스트 설계가 문서화되어 있다.
- 향후 Phase별 구현 범위가 분리되어 있다.

### Naver Cafe Import Phase 1. 데이터 모델 및 DB 확장

- `CafeSourceEntity`
- `CafeSourceDao`
- Repository
- `WodSourceType` 추가 검토
- 기존 Wod source 필드 확장 여부 결정
- Migration 필요 여부 확인

완료 기준:

- 카페 소스 등록 정보를 로컬 DB에 저장할 수 있다.
- 기존 WOD 데이터와 호환된다.
- 네이버 계정 정보는 어떤 DB 테이블에도 저장하지 않는다.

### Naver Cafe Import Phase 2. 카페 소스 설정 화면

- `CafeSourceSettingsScreen`
- 카페 URL 추가/수정/삭제
- `box name`
- `board URL`
- `keywords`
- `prefer mobile URL`
- Settings 화면에서 진입

완료 기준:

- 사용자가 하나 이상의 네이버카페 게시판 URL을 등록할 수 있다.
- 등록한 값은 앱 재실행 후에도 유지된다.
- 잘못된 URL과 빈 URL에 대한 한국어 안내 문구가 표시된다.

### Naver Cafe Import Phase 3. 홈 화면 WOD 불러오기 진입점 추가

- HomeScreen에서 `CafeSource` 등록 여부 확인
- `CafeSource`가 1개 이상 있으면 **WOD 추가** 버튼 아래에 초록색 **WOD 불러오기** 버튼 표시
- `CafeSource`가 없으면 버튼 숨김 또는 비활성화
- 버튼 클릭 시 가져오기 흐름으로 이동
- `CafeSource`가 여러 개면 선택 UI 표시
- 기존 **WOD 추가** 수동 입력 흐름은 유지
- 사용자 문구는 한국어
- WOD 용어는 영어 유지

완료 기준:

- **WOD 추가**는 수동 입력 진입점으로 유지된다.
- **WOD 불러오기**는 설정된 네이버카페 URL 기반 가져오기 진입점으로 구분된다.
- 버튼은 초록색 계열의 Success 또는 Import 액션으로 보인다.

### Naver Cafe Import Phase 4. WebView 가져오기 화면

- `CafeImportScreen`
- `CafeWebViewScreen`
- WebView URL 로딩
- 뒤로가기
- 새로고침
- 현재 URL 표시
- 로딩 상태
- 오류 상태

완료 기준:

- 등록된 URL을 앱 내 WebView로 열 수 있다.
- 사용자는 WebView 안에서 직접 로그인할 수 있다.
- 앱은 ID/PW를 읽거나 저장하지 않는다.

### Naver Cafe Import Phase 5. 게시글 후보 추출

- 현재 WebView 페이지에서 텍스트/링크 추출
- `CafePostCandidate` 생성
- 키워드/날짜 기반 confidence 계산
- 후보 목록 표시
- 사용자가 후보 선택

완료 기준:

- 사용자가 **현재 목록에서 WOD 찾기**를 눌렀을 때만 추출한다.
- 후보 confidence는 정렬용으로만 사용한다.
- 후보가 없으면 수동 입력 fallback을 안내한다.

### Naver Cafe Import Phase 6. 게시글 본문 추출

- 선택 게시글 WebView로 열기
- 현재 페이지 본문 텍스트 추출
- 불필요한 텍스트 정리
- `ImportedWodPreviewScreen` 표시

완료 기준:

- 사용자가 **본문 가져오기**를 눌렀을 때만 본문 추출을 수행한다.
- 추출 결과는 저장 전에 미리보기로 확인할 수 있다.
- 실패 시 복사/붙여넣기 또는 수동 입력을 안내한다.

### Naver Cafe Import Phase 7. WOD Edit 연동

- `ImportedWodText`를 WOD Edit 화면으로 전달
- `rawText` 자동 채움
- `title` 자동 채움
- `sourceType`/`sourceUrl` 저장
- 사용자가 확인/수정 후 저장

완료 기준:

- 가져온 본문이 WOD Edit 화면의 원문 입력란에 채워진다.
- 기본 날짜는 오늘로 설정된다.
- 사용자는 날짜, title, rawText, WOD type, movement를 직접 수정할 수 있다.

### Naver Cafe Import Phase 8. 실패 UX / QA / 안정화

- 실패 시나리오 문구
- 수동 입력 fallback
- WebView 오류 처리
- 수동 테스트
- README 또는 사용법 문서 업데이트

완료 기준:

- 주요 실패 상황에서 앱이 crash 없이 동작한다.
- 사용자는 실패 후에도 자연스럽게 수동 입력으로 이어갈 수 있다.
- 실험적 기능의 한계가 사용자에게 명확히 안내된다.

---

## 3. 각 Phase별 금지 사항

공통 금지:

- 네이버 ID/PW 저장 금지
- 자동 로그인 금지
- CAPTCHA 우회 금지
- 백그라운드 주기 수집 금지
- 대량 크롤링 금지
- 서버 추가 금지
- OpenAI API 추가 금지
- Firebase/Supabase 추가 금지
- 네이버 API 키 하드코딩 금지
- git commit 금지

Phase 0 금지:

- Kotlin/Compose 코드 수정 금지
- Gradle 설정 수정 금지
- DB/DAO/Repository/ViewModel/Navigation 코드 수정 금지
- 실제 네이버카페 가져오기 기능 구현 금지

---

## 4. Phase별 Codex 지시문 작성 기준

앞으로 각 Phase를 Codex에게 시킬 때 지시문에는 반드시 다음 섹션이 있어야 한다.

### 작업 목표

- 이번 Phase에서 달성할 기능 목표를 한두 문단으로 작성한다.
- 이전 Phase와 다음 Phase의 경계를 명확히 적는다.

### 참고 문서

- `docs/naver_cafe_import_spec.md`
- `docs/naver_cafe_import_work_order.md`
- 필요한 경우 기존 설계 문서:
  - `docs/wodlog_requirements_spec.md`
  - `docs/wodlog_codex_work_order.md`
  - `docs/design/design_spec.md`
  - `docs/design/ui_work_order.md`
  - `docs/design/screen_ux_spec.md`
  - `docs/design/component_spec.md`
  - `docs/design/copywriting_guide.md`
  - `docs/design/qa_checklist.md`

### 작업 범위

- 이번 Phase에서 수정 가능한 범위를 파일 또는 모듈 단위로 적는다.
- 이번 Phase에서 하지 않을 일을 명확히 적는다.

### 수정 예상 파일

- 실제 파일 경로를 가능한 한 구체적으로 적는다.
- 예상 파일 외 수정이 필요하면 Codex가 보고하도록 요구한다.

### UI/UX 원칙

- 사용자에게 보이는 문구는 한국어로 작성한다.
- WOD type, CrossFit 용어, 운동 이름은 영어 표기를 유지한다.
- 최신 Material 3 스타일을 따른다.
- 운동 직후 빠른 입력을 우선한다.
- 실패 시 수동 입력 fallback을 자연스럽게 제공한다.

### 보안/개인정보 원칙

- 네이버 ID/PW 저장 금지
- 자동 로그인 금지
- 사용자가 직접 접근 가능한 현재 페이지에서만 가져오기
- 서버 전송 없음
- DB에 계정 정보 저장 없음

### 금지 사항

- Phase 공통 금지 사항을 포함한다.
- 해당 Phase별 추가 금지 사항을 적는다.

### 구현 세부 요구

- 필요한 데이터 모델, 화면 상태, 이벤트, 에러 처리, 테스트 요구를 구체적으로 적는다.
- 기존 앱 아키텍처와 코드 스타일을 따르도록 명시한다.

### 완료 후 보고 형식

Codex는 완료 후 다음을 보고해야 한다.

1. 수정한 파일 목록
2. 구현 요약
3. UI/UX 변경 요약
4. 보안/개인정보 원칙 준수 여부
5. 실행한 검증 명령어와 결과
6. 수동 테스트 필요 항목
7. 커밋 메시지 제안

### 빌드/검증 명령어

- Windows PowerShell 기준 명령어를 적는다.
- 기본 검증은 `.\gradlew.bat assembleDebug`, `git status`, `git diff --stat`이다.

### 수동 테스트 체크리스트

- 가상 디바이스에서 확인해야 할 시나리오를 적는다.
- 성공 흐름과 실패 흐름을 모두 포함한다.

### 커밋 메시지 제안

- 사용자가 직접 commit할 수 있도록 한 줄 메시지를 제안한다.
- Codex는 직접 commit하지 않는다.

---

## 5. 검증 명령어

Windows PowerShell 기준:

```powershell
.\gradlew.bat assembleDebug
git status
git diff --stat
```

Phase 0 문서 작업 검증:

```powershell
git status
git diff --stat
git diff -- docs/naver_cafe_import_spec.md
git diff -- docs/naver_cafe_import_work_order.md
```

---

## 6. 수동 QA 전략

- 가상 디바이스 기준 테스트
- `CafeSource`가 없는 상태의 홈 화면 테스트
- `CafeSource`가 있는 상태의 홈 화면 **WOD 불러오기** 버튼 테스트
- 공개 네이버카페 URL 테스트
- 로그인 필요한 카페는 사용자가 직접 WebView에서 로그인 후 테스트
- 실패 시나리오 테스트
- WOD 입력 화면 연동 테스트
- 앱 재실행 후 `CafeSource` 유지 확인
- 네이버 ID/PW가 DB에 저장되지 않는지 확인
- WebView 로딩 실패, 네트워크 없음, 권한 없음 상태 확인

---

## 7. 릴리즈 전략

- 이 기능은 v0.2.0 후보로 문서화한다.
- 첫 구현은 실험적 기능으로 취급한다.
- 사용자 안내 문구에 `카페 구조나 권한에 따라 가져오기가 실패할 수 있음`을 명시한다.
- 수동 입력 fallback을 유지한다.
- 카페별 구조 차이와 네이버 정책 변경 가능성을 릴리즈 노트에 짧게 안내한다.

---

## 8. 언어/문구 정책

- 사용자에게 보이는 안내 문구는 한국어로 작성한다.
- WOD type, CrossFit 용어, 운동 이름은 영어 표기를 유지한다.
- 예: `For Time`, `AMRAP`, `EMOM`, `RFT`, `Chipper`, `Strength`, `Skill`, `Metcon`, `Rx`, `Scaled`, `Thruster`, `Pull-up`
- 홈 화면 버튼 문구는 **WOD 불러오기**로 작성한다.

---

## 9. UI/UX 원칙

- 사용자가 운동 직후 빠르게 WOD를 가져와 입력할 수 있어야 한다.
- 홈 화면에서 수동 입력과 카페 가져오기 진입점을 명확히 구분한다.
- **WOD 추가**는 수동 입력이다.
- **WOD 불러오기**는 설정된 네이버카페 URL 기반 가져오기 기능이다.
- **WOD 불러오기** 버튼은 초록색 계열로 표현해 가져오기/성공 액션 느낌을 준다.
- 가져오기 실패 시 좌절하지 않도록 수동 입력으로 자연스럽게 이어져야 한다.
- WebView 화면에서는 현재 상태를 명확히 표시한다.
- 가져온 본문은 저장 전 반드시 미리보기로 확인하게 한다.
- 위험하거나 혼동되는 액션은 확인을 거친다.
- 최신 Material 3 스타일을 따르되, Phase 0 문서 작업에서는 코드를 수정하지 않는다.

---

## 10. Phase별 수동 테스트 초안

### Phase 1

- [ ] `CafeSource` 추가/조회/수정/삭제가 DB 수준에서 동작한다.
- [ ] 기존 WOD 데이터 migration이 안전하다.
- [ ] 네이버 ID/PW 저장 필드가 없다.

### Phase 2

- [ ] Settings에서 카페 소스 설정 화면으로 진입한다.
- [ ] box name, board URL, keywords를 저장한다.
- [ ] 빈 URL과 잘못된 URL에 한국어 오류가 표시된다.

### Phase 3

- [ ] `CafeSource`가 없으면 HomeScreen에 **WOD 불러오기**가 숨김 또는 비활성화된다.
- [ ] `CafeSource`가 있으면 **WOD 추가** 아래에 초록색 **WOD 불러오기**가 표시된다.
- [ ] `CafeSource`가 1개면 바로 가져오기 흐름으로 진입한다.
- [ ] `CafeSource`가 여러 개면 선택 UI가 표시된다.

### Phase 4

- [ ] WebView가 게시판 URL을 연다.
- [ ] 뒤로가기와 새로고침이 동작한다.
- [ ] 현재 URL과 로딩 상태가 표시된다.
- [ ] 로그인 필요한 페이지에서 사용자가 직접 로그인할 수 있다.

### Phase 5

- [ ] 현재 목록에서 WOD 후보를 추출한다.
- [ ] 키워드와 날짜 기반 정렬이 동작한다.
- [ ] 후보가 없을 때 수동 입력 안내가 표시된다.

### Phase 6

- [ ] 후보 선택 시 게시글이 열린다.
- [ ] **본문 가져오기**가 현재 페이지에서만 동작한다.
- [ ] 미리보기 화면에 추출 본문이 표시된다.

### Phase 7

- [ ] WOD Edit 화면에 title과 rawText가 채워진다.
- [ ] sourceType/sourceUrl이 저장된다.
- [ ] 저장 전 사용자가 수정할 수 있다.

### Phase 8

- [ ] 주요 실패 시나리오에서 crash가 발생하지 않는다.
- [ ] 수동 입력 fallback이 항상 접근 가능하다.
- [ ] 안내 문구가 한국어이며 CrossFit 용어는 영어 표기를 유지한다.

---

## 11. 완료 후 보고 형식

Phase 0 문서 작업이 끝나면 다음 형식으로 보고한다.

1. 생성한 파일 목록
2. 각 문서 요약
3. 설계에서 선택한 핵심 구현 방식
4. 홈 화면 **WOD 불러오기** 버튼 설계 요약
5. 보안/개인정보 관련 금지 사항 요약
6. 다음 구현 Phase 제안
7. 이번 작업에서 코드 수정이 없었는지 여부

---

## 12. Phase 0 커밋 메시지 제안

```text
Add Naver Cafe import design documents
```
