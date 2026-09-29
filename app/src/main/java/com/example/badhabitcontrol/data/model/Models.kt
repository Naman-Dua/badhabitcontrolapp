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

enum class TimeBand(val label: String, val shortLabel: String, val startHour: Int, val endHour: Int) {
    LATE_NIGHT("12 AM – 4 AM", "12a-4a", 0, 3),
    EARLY_MORNING("4 AM – 8 AM", "4a-8a", 4, 7),
    MORNING("8 AM – 12 PM", "8a-12p", 8, 11),
    AFTERNOON("12 PM – 4 PM", "12p-4p", 12, 15),
    EVENING("4 PM – 8 PM", "4p-8p", 16, 19),
    NIGHT("8 PM – 12 AM", "8p-12a", 20, 23);

    companion object {
        fun fromHour(hour: Int): TimeBand = entries.firstOrNull { hour in it.startHour..it.endHour } ?: NIGHT
    }
}

data class HeatmapCell(
    val dayOfWeek: java.time.DayOfWeek,
    val timeBand: TimeBand,
    val urgeCount: Int = 0,
    val relapseCount: Int = 0,
    val totalCount: Int = 0
)

data class VulnerabilitySummary(
    val peakDay: java.time.DayOfWeek? = null,
    val peakTimeBand: TimeBand? = null,
    val peakWindowDescription: String = "No data yet",
    val totalRecordedEvents: Int = 0
)

data class HeatmapData(
    val cells: Map<Pair<java.time.DayOfWeek, TimeBand>, HeatmapCell> = emptyMap(),
    val summary: VulnerabilitySummary = VulnerabilitySummary(),
    val maxCount: Int = 0
)

data class FinancialConfig(
    val enabled: Boolean = false,
    val dailyCost: Double = 0.0,
    val currencySymbol: String = "$",
    val unitName: String = "items",
    val unitsPerDay: Double = 0.0
)

data class FinancialStats(
    val totalSaved: Double = 0.0,
    val streakSaved: Double = 0.0,
    val projectedAnnualSaved: Double = 0.0,
    val totalUnitsAvoided: Int = 0,
    val currencySymbol: String = "$",
    val nextMilestoneAmount: Double = 50.0,
    val milestoneProgress: Float = 0f
)
