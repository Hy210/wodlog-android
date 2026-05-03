package com.wodlog.app.domain.model

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
