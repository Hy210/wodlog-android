# 와드로그

와드로그는 크로스핏 사용자가 WOD 기록을 로컬에 저장하고, 이후 기록 비교와 ChatGPT 질문지 생성을 목표로 하는 Android 앱입니다.

## 현재 상태

이 저장소는 Phase 0 기준의 앱 뼈대와 Phase 1 Step 1의 Room 데이터 계층 기초 설정을 포함합니다. 앱 이름, Compose Material 3 테마, 기본 Navigation, 빈 화면 구조, Room Entity/Converter/Database 골격만 준비되어 있으며 실제 WOD 저장 기능은 아직 구현되지 않았습니다.

## MVP 목표

- 날짜별 WOD 직접 입력
- WOD 결과 기록
- 캘린더 기반 기록 조회
- 최근 3회 WOD 비교
- ChatGPT에 붙여넣을 질문지 생성 및 복사
- 사용자가 붙여넣은 ChatGPT 답변 저장
- Room/SQLite 기반 로컬 저장
- JSON 백업 및 복구

## 제외 범위

- 서버 없음
- OpenAI API 직접 호출 없음
- 로그인 없음
- 클라우드 동기화 없음
- 네이버 크롤링 없음
- OCR 없음
- 결제 및 광고 없음

## 기술 스택

- Kotlin
- Jetpack Compose
- Material 3
- AndroidX Navigation Compose
- Room 예정
- SQLite 로컬 DB 예정

## 빌드

```powershell
.\gradlew.bat assembleDebug
```

## 테스트

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebugAndroidTest
```

## 라이선스

MIT License
