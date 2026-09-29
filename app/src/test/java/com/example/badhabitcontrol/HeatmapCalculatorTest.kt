package com.example.badhabitcontrol

import com.example.badhabitcontrol.data.entity.UrgeEvent
import com.example.badhabitcontrol.data.model.TimeBand
import com.example.badhabitcontrol.data.model.UrgeOutcome
import com.example.badhabitcontrol.domain.analytics.HeatmapCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneId

class HeatmapCalculatorTest {

    private val zoneId = ZoneId.of("UTC")

    @Test
    fun testEmptyEventsGivesEmptyHeatmap() {
        val data = HeatmapCalculator.computeHeatmap(emptyList(), zoneId)
        assertEquals(0, data.summary.totalRecordedEvents)
        assertEquals(0, data.maxCount)
    }

    @Test
    fun testHourBandsCategorizeCorrectly() {
        assertEquals(TimeBand.LATE_NIGHT, TimeBand.fromHour(0))
        assertEquals(TimeBand.LATE_NIGHT, TimeBand.fromHour(3))
        assertEquals(TimeBand.EARLY_MORNING, TimeBand.fromHour(4))
        assertEquals(TimeBand.EARLY_MORNING, TimeBand.fromHour(7))
        assertEquals(TimeBand.MORNING, TimeBand.fromHour(8))
        assertEquals(TimeBand.MORNING, TimeBand.fromHour(11))
        assertEquals(TimeBand.AFTERNOON, TimeBand.fromHour(12))
        assertEquals(TimeBand.AFTERNOON, TimeBand.fromHour(15))
        assertEquals(TimeBand.EVENING, TimeBand.fromHour(16))
        assertEquals(TimeBand.EVENING, TimeBand.fromHour(19))
        assertEquals(TimeBand.NIGHT, TimeBand.fromHour(20))
        assertEquals(TimeBand.NIGHT, TimeBand.fromHour(23))
    }

    @Test
    fun testEventsBinningAndPeakDetection() {
        // Create a Friday date: 2026-10-02 is a Friday
        val fridayNight = LocalDateTime.of(2026, 10, 2, 21, 30).atZone(zoneId).toInstant().toEpochMilli()
        val fridayLateNight = LocalDateTime.of(2026, 10, 2, 23, 15).atZone(zoneId).toInstant().toEpochMilli()
        val mondayMorning = LocalDateTime.of(2026, 9, 28, 9, 0).atZone(zoneId).toInstant().toEpochMilli()

        val events = listOf(
            UrgeEvent(habitId = 1L, createdAt = fridayNight, intensity = "HIGH", timerSeconds = 300, trigger = "STRESS", outcome = UrgeOutcome.PASSED.name),
            UrgeEvent(habitId = 1L, createdAt = fridayLateNight, intensity = "HIGH", timerSeconds = 300, trigger = "BOREDOM", outcome = UrgeOutcome.RELAPSE.name),
            UrgeEvent(habitId = 1L, createdAt = mondayMorning, intensity = "LOW", timerSeconds = 300, trigger = "ALONE", outcome = UrgeOutcome.PASSED.name)
        )

        val result = HeatmapCalculator.computeHeatmap(events, zoneId)

        assertEquals(3, result.summary.totalRecordedEvents)
        val fridayNightCell = result.cells[DayOfWeek.FRIDAY to TimeBand.NIGHT]
        assertNotNull(fridayNightCell)
        assertEquals(2, fridayNightCell!!.totalCount)
        assertEquals(1, fridayNightCell.urgeCount)
        assertEquals(1, fridayNightCell.relapseCount)

        val mondayMorningCell = result.cells[DayOfWeek.MONDAY to TimeBand.MORNING]
        assertNotNull(mondayMorningCell)
        assertEquals(1, mondayMorningCell!!.totalCount)
        assertEquals(1, mondayMorningCell.urgeCount)
        assertEquals(0, mondayMorningCell.relapseCount)

        // Peak should be Friday Night
        assertEquals(DayOfWeek.FRIDAY, result.summary.peakDay)
        assertEquals(TimeBand.NIGHT, result.summary.peakTimeBand)
        assertTrue(result.summary.peakWindowDescription.contains("Friday"))
        assertTrue(result.summary.peakWindowDescription.contains("8 PM – 12 AM"))
    }
}
