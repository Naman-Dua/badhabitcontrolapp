package com.example.badhabitcontrol

import com.example.badhabitcontrol.data.entity.DayRecord
import com.example.badhabitcontrol.data.entity.UrgeEvent
import com.example.badhabitcontrol.data.model.DayStatus
import com.example.badhabitcontrol.data.model.UrgeIntensity
import com.example.badhabitcontrol.data.model.UrgeOutcome
import com.example.badhabitcontrol.data.model.UrgeTrigger
import com.example.badhabitcontrol.domain.streak.StreakCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class StreakCalculatorTest {

    private val today = LocalDate.of(2026, 9, 28)
    private val habitCreatedDate = LocalDate.of(2026, 9, 1)

    @Test
    fun testFirstDayCleanGivesStreakOne() {
        val records = mapOf(today to DayStatus.CLEAN)
        val streak = StreakCalculator.calculateCurrentStreak(today, records, today)
        assertEquals(1, streak)
    }

    @Test
    fun testFirstDayNoRecordGivesStreakZero() {
        val records = emptyMap<LocalDate, DayStatus>()
        val streak = StreakCalculator.calculateCurrentStreak(today, records, today)
        assertEquals(0, streak)
    }

    @Test
    fun testFirstDayRelapseGivesStreakZero() {
        val records = mapOf(today to DayStatus.RELAPSE)
        val streak = StreakCalculator.calculateCurrentStreak(today, records, today)
        assertEquals(0, streak)
    }

    @Test
    fun testConsecutiveCleanDaysCountsProperly() {
        val records = mapOf(
            today.minusDays(4) to DayStatus.CLEAN,
            today.minusDays(3) to DayStatus.CLEAN,
            today.minusDays(2) to DayStatus.URGE_MANAGED,
            today.minusDays(1) to DayStatus.CLEAN,
            today to DayStatus.URGE_MANAGED
        )
        val streak = StreakCalculator.calculateCurrentStreak(habitCreatedDate, records, today)
        assertEquals(5, streak)
    }

    @Test
    fun testTodayNoRecordYetCountsThroughYesterday() {
        val records = mapOf(
            today.minusDays(3) to DayStatus.CLEAN,
            today.minusDays(2) to DayStatus.URGE_MANAGED,
            today.minusDays(1) to DayStatus.CLEAN
            // today has no entry yet
        )
        val streak = StreakCalculator.calculateCurrentStreak(habitCreatedDate, records, today)
        assertEquals(3, streak)
    }

    @Test
    fun testRelapseTodayBreaksStreakToZeroWhilePreservingLongestStreak() {
        val records = mapOf(
            today.minusDays(4) to DayStatus.CLEAN,
            today.minusDays(3) to DayStatus.CLEAN,
            today.minusDays(2) to DayStatus.CLEAN,
            today.minusDays(1) to DayStatus.CLEAN,
            today to DayStatus.RELAPSE
        )
        val currentStreak = StreakCalculator.calculateCurrentStreak(habitCreatedDate, records, today)
        val longestStreak = StreakCalculator.calculateLongestStreak(habitCreatedDate, records, today)

        assertEquals(0, currentStreak)
        assertEquals(4, longestStreak)
    }

    @Test
    fun testStreakResumesAfterRelapse() {
        val records = mapOf(
            today.minusDays(5) to DayStatus.CLEAN,
            today.minusDays(4) to DayStatus.CLEAN,
            today.minusDays(3) to DayStatus.RELAPSE,
            today.minusDays(2) to DayStatus.CLEAN,
            today.minusDays(1) to DayStatus.URGE_MANAGED,
            today to DayStatus.CLEAN
        )
        val currentStreak = StreakCalculator.calculateCurrentStreak(habitCreatedDate, records, today)
        val longestStreak = StreakCalculator.calculateLongestStreak(habitCreatedDate, records, today)

        assertEquals(3, currentStreak)
        assertEquals(3, longestStreak)
    }

    @Test
    fun testTotalCleanDaysCountsOnlyCleanAndUrgeManaged() {
        val records = mapOf(
            today.minusDays(4) to DayStatus.CLEAN,
            today.minusDays(3) to DayStatus.RELAPSE,
            today.minusDays(2) to DayStatus.URGE_MANAGED,
            today.minusDays(1) to DayStatus.CLEAN,
            today to DayStatus.RELAPSE
        )
        val totalClean = StreakCalculator.calculateTotalCleanDays(records)
        assertEquals(3, totalClean)
    }

    @Test
    fun testUrgeManagementRate() {
        val events = listOf(
            UrgeEvent(1, 1L, 1000L, UrgeIntensity.LOW.name, 180, UrgeTrigger.STRESS.name, UrgeOutcome.PASSED.name),
            UrgeEvent(2, 1L, 2000L, UrgeIntensity.MEDIUM.name, 300, UrgeTrigger.BOREDOM.name, UrgeOutcome.PASSED.name),
            UrgeEvent(3, 1L, 3000L, UrgeIntensity.HIGH.name, 600, UrgeTrigger.ALONE.name, UrgeOutcome.STILL_URGE.name),
            UrgeEvent(4, 1L, 4000L, UrgeIntensity.HIGH.name, 900, UrgeTrigger.SOCIAL.name, UrgeOutcome.RELAPSE.name)
        )
        // 2 passed out of 4 completed = 50%
        val rate = StreakCalculator.calculateUrgeManagementRate(events)
        assertEquals(50f, rate, 0.01f)
    }

    @Test
    fun testEmptyEventsGivesZeroRate() {
        val rate = StreakCalculator.calculateUrgeManagementRate(emptyList())
        assertEquals(0f, rate, 0.01f)
    }

    @Test
    fun testComputeStatsAggregation() {
        val dayRecords = listOf(
            DayRecord(1, 1L, "2026-09-26", DayStatus.CLEAN.name, 1000L),
            DayRecord(2, 1L, "2026-09-27", DayStatus.URGE_MANAGED.name, 2000L),
            DayRecord(3, 1L, "2026-09-28", DayStatus.CLEAN.name, 3000L)
        )
        val urgeEvents = listOf(
            UrgeEvent(1, 1L, 2000L, UrgeIntensity.HIGH.name, 300, UrgeTrigger.STRESS.name, UrgeOutcome.PASSED.name),
            UrgeEvent(2, 1L, 2500L, UrgeIntensity.MEDIUM.name, 300, UrgeTrigger.STRESS.name, UrgeOutcome.PASSED.name)
        )

        val stats = StreakCalculator.computeStats(
            habitCreatedDate = LocalDate.of(2026, 9, 20),
            dayRecords = dayRecords,
            urgeEvents = urgeEvents,
            today = today
        )

        assertEquals(3, stats.currentStreak)
        assertEquals(3, stats.longestStreak)
        assertEquals(3, stats.totalCleanDays)
        assertEquals(2, stats.totalUrges)
        assertEquals(2, stats.managedUrges)
        assertEquals(0, stats.relapses)
        assertEquals(100f, stats.urgeManagementRate, 0.01f)
        assertEquals(UrgeTrigger.STRESS, stats.mostFrequentTrigger)
    }
}
