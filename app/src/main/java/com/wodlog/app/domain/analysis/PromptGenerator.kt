package com.wodlog.app.domain.analysis

import com.wodlog.app.domain.model.LifestyleLog
import com.wodlog.app.domain.model.Movement
import com.wodlog.app.domain.model.UserProfile
import com.wodlog.app.domain.model.Wod
import com.wodlog.app.domain.model.WodResult
import com.wodlog.app.domain.model.WodSection
import java.util.Locale

object PromptGenerator {
    fun generate(input: PromptInput): String {
        return buildString {
            appendLine("너는 CrossFit 코치이자 운동 기록 분석가다.")
            appendLine("아래 WOD 기록을 바탕으로 사용자가 다음 훈련을 더 잘 준비할 수 있게 분석해줘.")
            appendLine()
            appendLine("ChatGPT 붙여넣기용 운동 기록 분석 질문지")
            appendLine()
            appendLine("## 목적")
            appendLine("- 아래 운동 기록을 바탕으로 회복, 페이스, 운동 구성 관점에서 분석해 주세요.")
            appendLine("- 의학적 진단이 아니라 일반적인 운동 기록 분석으로 답변해 주세요.")
            appendLine("- 서로 다른 WOD는 단순히 더 좋다/나쁘다로 판단하지 말고 정량 지표 중심으로 요약해 주세요.")
            appendLine()

            appendProfile(input.profile)
            appendCurrentWod(input.currentWod)
            appendSections(input.sections)
            appendMovements("## Movement 목록", input.movements)
            appendResult("## 결과 기록", input.result)
            appendRecentSummary(input.recentSummary)
            appendLifestyle(input.lifestyleLog)
            appendAdditionalMemo(input.additionalUserMemo)
            appendQuestions()
            appendAnswerFormat()
        }.trim()
    }

    private fun StringBuilder.appendProfile(profile: UserProfile?) {
        appendLine("## 사용자 프로필")
        if (profile == null) {
            appendLine("- 프로필: 미입력")
        } else {
            appendLine("- 키: ${profile.heightCm.format("cm")}")
            appendLine("- 몸무게: ${profile.weightKg.format("kg")}")
            appendLine("- 크로스핏 시작일: ${profile.crossfitStartDate.display()}")
        }
        appendLine()
    }

    private fun StringBuilder.appendCurrentWod(wod: Wod) {
        appendLine("## 현재 WOD")
        appendLine("- 날짜: ${wod.date}")
        appendLine("- 제목: ${wod.title}")
        appendLine("- WOD 유형: ${wod.type.name}")
        appendLine("- 원문: ${wod.rawText.display()}")
        appendLine("- 메모: ${wod.notes.display()}")
        appendLine()
    }

    private fun StringBuilder.appendSections(sections: List<WodSection>) {
        appendLine("## Section 목록")
        if (sections.isEmpty()) {
            appendLine("- 미입력")
        } else {
            sections.sortedBy { it.orderIndex }.forEachIndexed { index, section ->
                appendLine("${index + 1}. ${section.name}")
            }
        }
        appendLine()
    }

    private fun StringBuilder.appendMovements(title: String, movements: List<Movement>) {
        appendLine(title)
        if (movements.isEmpty()) {
            appendLine("- 미입력")
        } else {
            movements.sortedBy { it.orderIndex }.forEachIndexed { index, movement ->
                appendMovement(index, movement)
            }
        }
        appendLine()
    }

    private fun StringBuilder.appendMovement(index: Int, movement: Movement) {
        appendLine("${index + 1}. ${movement.name}")
        appendLine("   - 카테고리: ${movement.category?.name.display()}")
        appendLine("   - 무게: ${movement.weightKg.format("kg")}")
        appendLine("   - 횟수: ${movement.reps.display()}")
        appendLine("   - 세트: ${movement.sets.display()}")
        appendLine("   - 라운드: ${movement.rounds.display()}")
        appendLine("   - 거리: ${movement.distanceMeters.format("m")}")
        appendLine("   - 칼로리: ${movement.calories.format("cal")}")
        appendLine("   - 시간: ${movement.durationSeconds.format("sec")}")
        appendLine("   - 메모: ${movement.notes.display()}")
    }

    private fun StringBuilder.appendResult(title: String, result: WodResult?) {
        appendLine(title)
        if (result == null) {
            appendLine("- 결과: 미입력")
        } else {
            appendResultFields(result)
        }
        appendLine()
    }

    private fun StringBuilder.appendResultFields(result: WodResult) {
        appendLine("- Score type: ${result.scoreType.name}")
        appendLine("- Time: ${result.timeSeconds.format("sec")}")
        appendLine("- Rounds/Reps: ${result.rounds.display()} rounds + ${result.extraReps.display()} reps")
        appendLine("- Total reps: ${result.totalReps.display()}")
        appendLine("- Load: ${result.loadKg.format("kg")}")
        appendLine("- Distance: ${result.distanceMeters.format("m")}")
        appendLine("- Calories: ${result.calories.format("cal")}")
        appendLine("- Rx status: ${result.rxStatus.name}")
        appendLine("- RPE: ${result.rpe.display()}")
        appendLine("- Condition: ${result.condition?.name.display()}")
        appendLine("- Memo: ${result.memo.display()}")
    }

    private fun StringBuilder.appendRecentSummary(summary: AnalysisSummary?) {
        appendLine("## 최근 3회 비교 요약")
        if (summary == null) {
            appendLine("- 최근 비교 데이터 없음")
        } else {
            summary.items.forEach { item ->
                appendLine("### ${item.label.displayName()}")
                appendLine("- 날짜: ${item.date}")
                appendLine("- 제목: ${item.title}")
                appendLine("- WOD 유형: ${item.wodType.name}")
                appendLine("- 원문: ${item.rawText.display()}")
                appendLine("- 메모: ${item.notes.display()}")
                appendLine("- Section:")
                if (item.sections.isEmpty()) {
                    appendLine("  - 미입력")
                } else {
                    item.sections.sortedBy { it.orderIndex }.forEachIndexed { index, section ->
                        appendLine("  ${index + 1}. ${section.name}")
                    }
                }
                appendLine("- Movement:")
                if (item.movements.isEmpty()) {
                    appendLine("  - 미입력")
                } else {
                    item.movements.sortedBy { it.orderIndex }.forEachIndexed { index, movement ->
                        appendLine("  ${index + 1}. ${movement.name}")
                        appendLine("     - 카테고리: ${movement.category?.name.display()}")
                        appendLine("     - 무게: ${movement.weightKg.format("kg")}")
                        appendLine("     - 횟수: ${movement.reps.display()}")
                        appendLine("     - 세트: ${movement.sets.display()}")
                        appendLine("     - 라운드: ${movement.rounds.display()}")
                        appendLine("     - 거리: ${movement.distanceMeters.format("m")}")
                        appendLine("     - 칼로리: ${movement.calories.format("cal")}")
                        appendLine("     - 시간: ${movement.durationSeconds.format("sec")}")
                        appendLine("     - 메모: ${movement.notes.display()}")
                    }
                }
                appendLine("- Result:")
                if (item.result == null) {
                    appendLine("  - 결과: 미입력")
                } else {
                    appendLine("  - Score type: ${item.result.scoreType.name}")
                    appendLine("  - Time: ${item.result.timeSeconds.format("sec")}")
                    appendLine("  - Rounds/Reps: ${item.result.rounds.display()} rounds + ${item.result.extraReps.display()} reps")
                    appendLine("  - Total reps: ${item.result.totalReps.display()}")
                    appendLine("  - Load: ${item.result.loadKg.format("kg")}")
                    appendLine("  - Distance: ${item.result.distanceMeters.format("m")}")
                    appendLine("  - Calories: ${item.result.calories.format("cal")}")
                    appendLine("  - Rx status: ${item.result.rxStatus.name}")
                    appendLine("  - RPE: ${item.result.rpe.display()}")
                    appendLine("  - Condition: ${item.result.condition?.name.display()}")
                    appendLine("  - Memo: ${item.result.memo.display()}")
                }
                appendLine("- 자동 계산 참고:")
                appendLine("  - Total reps: ${item.totalReps}")
                appendLine("  - Load volume: ${item.totalLoadVolume.formatNumber()}")
                appendLine("  - Distance: ${item.totalDistance.formatNumber()}")
                appendLine("  - Calories: ${item.totalCalories.formatNumber()}")
                appendLine("  - Rx status: ${item.rxStatus?.name.display()}")
                appendLine("  - RPE: ${item.rpe.display()}")
                appendLine("  - Category count: ${item.movementCategoryCounts.displayCategoryCounts()}")
            }
            appendLine("- 카테고리 비중:")
            if (summary.categoryBreakdown.isEmpty()) {
                appendLine("  - 미입력")
            } else {
                summary.categoryBreakdown.forEach { share ->
                    appendLine("  - ${share.category.name}: ${share.count}개, ${share.ratio.formatPercent()}")
                }
            }
            appendLine("- 중립 요약:")
            summary.neutralSummary.forEach { line ->
                appendLine("  - $line")
            }
        }
        appendLine()
    }

    private fun StringBuilder.appendLifestyle(log: LifestyleLog?) {
        appendLine("## 주간 식단/생활습관")
        if (log == null) {
            appendLine("- 생활습관 기록: 미입력")
        } else {
            appendLine("- 주 시작일: ${log.weekStartDate}")
            appendLine("- 식습관 요약: ${log.mealSummary.display()}")
            appendLine("- 음주 여부/주량: ${log.alcohol.displayBoolean()} / ${log.alcoholAmountPerWeek.display()}")
            appendLine("- 흡연 여부/흡연량: ${log.smoking.displayBoolean()} / ${log.smokingAmountPerWeek.display()}")
            appendLine("- 평균 수면시간: ${log.sleepAverageHours.format("hours")}")
            appendLine("- 메모: ${log.notes.display()}")
        }
        appendLine()
    }

    private fun StringBuilder.appendAdditionalMemo(memo: String?) {
        appendLine("## 사용자 추가 메모")
        appendLine("- ${memo.display()}")
        appendLine()
    }

    private fun StringBuilder.appendQuestions() {
        appendLine("## 질문")
        appendLine("1. 회복 관점에서 현재 기록을 어떻게 해석할 수 있나요?")
        appendLine("2. 페이스 조절 관점에서 다음 훈련 때 참고할 점은 무엇인가요?")
        appendLine("3. 운동 구성 관점에서 과도하게 반복되거나 부족해 보이는 영역이 있나요?")
        appendLine("4. 다음 훈련에서 주의할 점을 정리해 주세요.")
        appendLine("5. WOD가 서로 다르면 직접적인 우열 판단 대신 total reps, load volume, distance, calories, Rx, RPE, 카테고리 비중 중심으로 설명해 주세요.")
        appendLine()
    }

    private fun StringBuilder.appendAnswerFormat() {
        appendLine("## 답변 형식")
        appendLine("- 답변은 너무 길게 늘어뜨리지 말고 핵심 위주로 작성해 주세요.")
        appendLine("- 각 항목의 핵심 포인트 앞에는 적당한 이모티콘(예: ✅, ⚠️, 🎯)을 사용해 가독성을 높여 주세요.")
        appendLine("- 이모티콘은 과하게 쓰지 말고, 강조가 필요한 핵심 문장에만 사용해 주세요.")
        appendLine("- 마지막에는 \"요약\" 섹션을 두고 답변의 핵심을 3~5줄로 정리해 주세요.")
    }

    private fun ComparisonLabel.displayName(): String {
        return when (this) {
            ComparisonLabel.Older -> "전전 WOD"
            ComparisonLabel.Previous -> "전 WOD"
            ComparisonLabel.Current -> "현재 WOD"
        }
    }

    private fun Map<*, Int>.displayCategoryCounts(): String {
        return if (isEmpty()) {
            EMPTY_VALUE
        } else {
            entries.joinToString { (category, count) -> "$category: ${count}개" }
        }
    }

    private fun String?.display(): String {
        return this?.trim()?.takeIf { it.isNotEmpty() } ?: EMPTY_VALUE
    }

    private fun Any?.display(): String {
        return this?.toString() ?: EMPTY_VALUE
    }

    private fun Boolean?.displayBoolean(): String {
        return when (this) {
            true -> "예"
            false -> "아니오"
            null -> EMPTY_VALUE
        }
    }

    private fun Double?.format(unit: String): String {
        return this?.let { "${it.formatNumber()} $unit" } ?: EMPTY_VALUE
    }

    private fun Int?.format(unit: String): String {
        return this?.let { "$it $unit" } ?: EMPTY_VALUE
    }

    private fun Double.formatNumber(): String {
        return if (this % 1.0 == 0.0) {
            toLong().toString()
        } else {
            String.format(Locale.US, "%.1f", this)
        }
    }

    private fun Double.formatPercent(): String {
        return String.format(Locale.US, "%.0f%%", this * 100.0)
    }

    private const val EMPTY_VALUE = "미입력"
}
