package com.example.badhabitcontrol.domain.streak

import com.example.badhabitcontrol.data.entity.DayRecord
import com.example.badhabitcontrol.data.entity.UrgeEvent
import com.example.badhabitcontrol.data.model.DayStatus
import com.example.badhabitcontrol.data.model.HabitStats
import com.example.badhabitcontrol.data.model.UrgeOutcome
import com.example.badhabitcontrol.data.model.UrgeTrigger
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object StreakCalculator {

    private val DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE

    fun parseDate(dateStr: String): LocalDate? {
        return try {
            LocalDate.parse(dateStr, DATE_FORMATTER)
        } catch (_: Exception) {
            null
        }
    }

    fun formatDate(date: LocalDate): String {
        return date.format(DATE_FORMATTER)
    }

    fun isCleanStatus(status: DayStatus): Boolean {
        return status == DayStatus.CLEAN || status == DayStatus.URGE_MANAGED
    }

    /**
     * Calculates the current clean streak in days.
     *
     * Rules:
     * - If today has a RELAPSE, current streak is 0.
     * - If today is clean (CLEAN or URGE_MANAGED), streak starts at 1 and counts backwards consecutive clean days.
     * - If today has no record yet: streak starts counting from yesterday. If yesterday was clean, streak is
     *   consecutive clean days through yesterday. If yesterday was relapse or before habit creation, streak is 0.
     * - A missed/untracked day or a RELAPSE halts the streak.
     */
    fun calculateCurrentStreak(
        habitCreatedDate: LocalDate,
        recordsByDate: Map<LocalDate, DayStatus>,
        today: LocalDate
    ): Int {
        val todayStatus = recordsByDate[today]
        if (todayStatus == DayStatus.RELAPSE) {
            return 0
        }

        var streak = 0
        val startDate = if (todayStatus != null && isCleanStatus(todayStatus)) {
            today
        } else {
            today.minusDays(1)
        }

        var checkDate = startDate
        while (!checkDate.isBefore(habitCreatedDate)) {
            val status = recordsByDate[checkDate]
            if (status != null && isCleanStatus(status)) {
                streak++
                checkDate = checkDate.minusDays(1)
            } else {
                break
            }
        }

        return streak
    }

    /**
     * Calculates the longest consecutive clean streak across all history up to [today].
     */
    fun calculateLongestStreak(
        habitCreatedDate: LocalDate,
        recordsByDate: Map<LocalDate, DayStatus>,
        today: LocalDate
    ): Int {
        if (recordsByDate.isEmpty()) {
            return 0
        }

        val earliestDate = recordsByDate.keys.minOrNull() ?: habitCreatedDate
        val start = if (earliestDate.isBefore(habitCreatedDate)) earliestDate else habitCreatedDate

        var maxStreak = 0
        var currentRun = 0

        var date = start
        while (!date.isAfter(today)) {
            val status = recordsByDate[date]
            if (status != null && isCleanStatus(status)) {
                currentRun++
                if (currentRun > maxStreak) {
                    maxStreak = currentRun
                }
            } else {
                currentRun = 0
            }
            date = date.plusDays(1)
        }

        return maxStreak
    }

    /**
     * Calculates total unique dates marked as clean (CLEAN or URGE_MANAGED).
     */
    fun calculateTotalCleanDays(recordsByDate: Map<LocalDate, DayStatus>): Int {
        return recordsByDate.values.count { isCleanStatus(it) }
    }

    /**
     * Calculates urge management rate: (managed / total completed) * 100.
     */
    fun calculateUrgeManagementRate(events: List<UrgeEvent>): Float {
        val completedEvents = events.filter {
            it.outcome == UrgeOutcome.PASSED.name ||
                it.outcome == UrgeOutcome.STILL_URGE.name ||
                it.outcome == UrgeOutcome.RELAPSE.name
        }
        if (completedEvents.isEmpty()) return 0f

        val managedCount = completedEvents.count { it.outcome == UrgeOutcome.PASSED.name }
        return (managedCount.toFloat() / completedEvents.size.toFloat()) * 100f
    }

    /**
     * Compiles full statistics for a habit.
     */
    fun computeStats(
        habitCreatedDate: LocalDate,
        dayRecords: List<DayRecord>,
        urgeEvents: List<UrgeEvent>,
        today: LocalDate
    ): HabitStats {
        val recordMap = mutableMapOf<LocalDate, DayStatus>()
        for (r in dayRecords) {
            val d = parseDate(r.localDate) ?: continue
            val status = try {
                DayStatus.valueOf(r.status)
            } catch (_: Exception) {
                continue
            }
            recordMap[d] = status
        }

        val currentStreak = calculateCurrentStreak(habitCreatedDate, recordMap, today)
        val longestStreak = calculateLongestStreak(habitCreatedDate, recordMap, today)
        val totalCleanDays = calculateTotalCleanDays(recordMap)

        val totalUrges = urgeEvents.size
        val managedUrges = urgeEvents.count { it.outcome == UrgeOutcome.PASSED.name }
        val relapses = dayRecords.count { it.status == DayStatus.RELAPSE.name }
        val rate = calculateUrgeManagementRate(urgeEvents)

        val triggerCounts = mutableMapOf<UrgeTrigger, Int>()
        for (event in urgeEvents) {
            val trig = try {
                UrgeTrigger.valueOf(event.trigger)
            } catch (_: Exception) {
                null
            }
            if (trig != null) {
                triggerCounts[trig] = (triggerCounts[trig] ?: 0) + 1
            }
        }

        val mostFrequent = triggerCounts.maxByOrNull { it.value }?.key

        return HabitStats(
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            totalCleanDays = totalCleanDays,
            totalUrges = totalUrges,
            managedUrges = managedUrges,
            relapses = relapses,
            urgeManagementRate = rate,
            triggerCounts = triggerCounts,
            mostFrequentTrigger = mostFrequent
        )
    }
}
