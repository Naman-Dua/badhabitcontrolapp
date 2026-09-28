package com.example.badhabitcontrol

import com.example.badhabitcontrol.data.entity.DayRecord
import com.example.badhabitcontrol.data.model.DayStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class DayRecordTransitionTest {

    private fun applyUrgeManaged(
        existing: DayRecord?,
        habitId: Long,
        date: String,
        nowEpoch: Long
    ): DayRecord? {
        if (existing?.status == DayStatus.RELAPSE.name) return existing
        return existing?.copy(status = DayStatus.URGE_MANAGED.name) ?: DayRecord(
            habitId = habitId,
            localDate = date,
            status = DayStatus.URGE_MANAGED.name,
            createdAt = nowEpoch
        )
    }

    private fun applyRelapse(
        existing: DayRecord?,
        habitId: Long,
        date: String,
        nowEpoch: Long
    ): DayRecord {
        return existing?.copy(status = DayStatus.RELAPSE.name) ?: DayRecord(
            habitId = habitId,
            localDate = date,
            status = DayStatus.RELAPSE.name,
            createdAt = nowEpoch
        )
    }

    @Test
    fun testUrgeManagedTransitionsFromEmpty() {
        val result = applyUrgeManaged(null, 1L, "2026-09-28", 1000L)
        assertEquals(DayStatus.URGE_MANAGED.name, result?.status)
    }

    @Test
    fun testRelapseOverridesUrgeManagedOnSameDay() {
        val managed = applyUrgeManaged(null, 1L, "2026-09-28", 1000L)
        val relapsed = applyRelapse(managed, 1L, "2026-09-28", 2000L)

        assertEquals(DayStatus.RELAPSE.name, relapsed.status)
    }

    @Test
    fun testUrgeManagedDoesNotOverwriteRelapseOnSameDay() {
        val relapsed = applyRelapse(null, 1L, "2026-09-28", 1000L)
        val managedAttempt = applyUrgeManaged(relapsed, 1L, "2026-09-28", 2000L)

        assertEquals(DayStatus.RELAPSE.name, managedAttempt?.status)
    }

    @Test
    fun testHabitIsolation() {
        val habit1Record = applyRelapse(null, 1L, "2026-09-28", 1000L)
        val habit2Record = applyUrgeManaged(null, 2L, "2026-09-28", 1000L)

        assertEquals(1L, habit1Record.habitId)
        assertEquals(DayStatus.RELAPSE.name, habit1Record.status)

        assertEquals(2L, habit2Record?.habitId)
        assertEquals(DayStatus.URGE_MANAGED.name, habit2Record?.status)
    }
}
