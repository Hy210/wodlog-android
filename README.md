# 와드로그

와드로그는 크로스핏 WOD 기록을 Android 기기 안에 로컬로 저장하고, 기록 조회와 최근 기록 비교, ChatGPT에 직접 붙여넣을 질문지 생성을 돕는 앱입니다.

## 현재 MVP 상태

- 프로필 입력/저장
- WOD 직접 입력/저장
- WOD 상세 조회
- 결과 기록 입력/저장
- 월간 캘린더 조회
- 최근 3회 WOD 정량 비교
- ChatGPT 붙여넣기용 질문지 생성 및 복사
- 사용자가 붙여넣은 GPT 답변 저장/열람
- 주간 식단/생활습관 기록
- JSON 백업 내보내기/가져오기 미리보기/병합 적용
- Settings와 MIT License 화면

모든 데이터는 Room/SQLite 기반으로 기기 안에 저장됩니다.

## 제외 범위

- 서버 없음
- OpenAI API 직접 호출 없음
- 로그인 없음
- 클라우드 동기화 없음
- Firebase/Supabase 없음
- OCR/크롤링 없음
- 광고/결제 없음
- Google Play 배포 설정 없음

ChatGPT 분석은 앱이 API를 호출하지 않습니다. 앱은 질문지를 생성하고, 사용자가 직접 복사해서 ChatGPT에 붙여넣는 방식만 제공합니다.

## 기술 스택

- Kotlin
- Jetpack Compose
- Material 3
- AndroidX Navigation Compose
- Room / SQLite
- Kotlin Coroutines / StateFlow
- kotlinx.serialization JSON

## 빌드와 테스트

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
.\gradlew.bat assembleDebugAndroidTest
.\gradlew.bat assembleRelease
```

연결된 기기나 에뮬레이터가 있을 때만 instrumented test를 실행합니다.

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

## Release APK 준비

현재 Android Gradle 설정:

- `versionCode = 1`
- `versionName = "1.0"`
- `applicationId = "com.wodlog.app"`

Release APK 빌드:

```powershell
.\gradlew.bat assembleRelease
```

빌드가 성공하면 APK 산출물은 보통 아래 위치에 생성됩니다.

```text
app/build/outputs/apk/release/app-release-unsigned.apk
```

현재 별도 release signing config와 keystore는 저장소에 포함하지 않습니다. GitHub Releases에 배포할 최종 APK는 배포자가 안전하게 관리하는 서명키로 서명한 뒤 업로드해야 합니다. keystore, 비밀번호, 서명 관련 비밀값은 저장소에 커밋하지 않습니다.

## GitHub Releases 업로드 절차

1. `.\gradlew.bat testDebugUnitTest`로 JVM 테스트를 확인합니다.
2. `.\gradlew.bat assembleDebugAndroidTest`로 Android test APK 컴파일을 확인합니다.
3. `.\gradlew.bat assembleRelease`로 release APK를 생성합니다.
4. APK 파일 위치를 확인합니다.
5. 필요한 경우 로컬에서 안전하게 관리하는 keystore로 APK를 서명합니다.
6. GitHub 저장소의 Releases에서 새 tag를 만듭니다.
7. APK를 release asset으로 첨부합니다.
8. release note에 버전명, 주요 변경, 제외 범위, “OpenAI API 직접 호출 없음”을 명시합니다.

## 백업/복구

Settings에서 JSON 내보내기와 가져오기 미리보기/병합 적용을 제공합니다. Import는 기존 데이터를 삭제하지 않고 백업 데이터의 ID를 기준으로 병합/upsert하는 방향입니다. 앱 데이터 초기화 기능은 구현되어 있지 않습니다.

## 라이선스

MIT License. 자세한 내용은 [LICENSE](LICENSE)를 확인하세요.
