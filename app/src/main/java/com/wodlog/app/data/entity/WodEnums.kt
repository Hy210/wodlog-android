package com.wodlog.app.data.entity

enum class WodType {
    FOR_TIME,
    AMRAP,
    EMOM,
    RFT,
    STRENGTH,
    SKILL,
    INTERVAL,
    OTHER
}

enum class WodSourceType {
    MANUAL,
    NAVER_CAFE_WEBVIEW,
    PASTE_TEXT,
    OCR
}

enum class MovementCategory {
    STRENGTH,
    CARDIO,
    GYMNASTICS,
    WEIGHTLIFTING,
    BODYWEIGHT,
    OTHER
}

enum class ScoreType {
    TIME,
    ROUNDS_REPS,
    REPS,
    LOAD,
    DISTANCE,
    CALORIES,
    OTHER
}

enum class RxStatus {
    RX,
    SCALED,
    CUSTOM,
    UNKNOWN
}

enum class Condition {
    GREAT,
    GOOD,
    NORMAL,
    TIRED,
    PAIN,
    UNKNOWN
}
