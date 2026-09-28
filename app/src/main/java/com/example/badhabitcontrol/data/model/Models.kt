package com.example.badhabitcontrol.data.model

enum class DayStatus {
    CLEAN,
    URGE_MANAGED,
    RELAPSE
}

enum class UrgeIntensity(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High")
}

enum class UrgeTrigger(val label: String) {
    STRESS("Stress"),
    BOREDOM("Boredom"),
    ALONE("Alone"),
    SOCIAL("Social"),
    HABIT_ROUTINE("Habit/Routine"),
    RANDOM_OTHER("Random/Other")
}

enum class UrgeOutcome(val label: String) {
    PASSED("Urge Passed"),
    STILL_URGE("Still an Urge"),
    RELAPSE("Relapse")
}

data class HabitStats(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val totalCleanDays: Int = 0,
    val totalUrges: Int = 0,
    val managedUrges: Int = 0,
    val relapses: Int = 0,
    val urgeManagementRate: Float = 0f,
    val triggerCounts: Map<UrgeTrigger, Int> = emptyMap(),
    val mostFrequentTrigger: UrgeTrigger? = null
)
