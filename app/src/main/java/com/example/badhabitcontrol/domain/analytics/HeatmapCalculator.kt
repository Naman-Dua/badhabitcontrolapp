package com.example.badhabitcontrol.domain.analytics

import com.example.badhabitcontrol.data.entity.UrgeEvent
import com.example.badhabitcontrol.data.model.HeatmapCell
import com.example.badhabitcontrol.data.model.HeatmapData
import com.example.badhabitcontrol.data.model.TimeBand
import com.example.badhabitcontrol.data.model.UrgeOutcome
import com.example.badhabitcontrol.data.model.VulnerabilitySummary
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

object HeatmapCalculator {

    fun computeHeatmap(
        events: List<UrgeEvent>,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): HeatmapData {
        if (events.isEmpty()) {
            return HeatmapData()
        }

        val allDays = DayOfWeek.entries
        val allBands = TimeBand.entries

        // Initialize cell map with zeroes
        val cellMap = mutableMapOf<Pair<DayOfWeek, TimeBand>, HeatmapCell>()
        for (day in allDays) {
            for (band in allBands) {
                cellMap[day to band] = HeatmapCell(dayOfWeek = day, timeBand = band)
            }
        }

        var totalEvents = 0
        for (event in events) {
            val zdt = Instant.ofEpochMilli(event.createdAt).atZone(zoneId)
            val day = zdt.dayOfWeek
            val band = TimeBand.fromHour(zdt.hour)
            val key = day to band
            val existing = cellMap[key] ?: HeatmapCell(day, band)

            val isRelapse = event.outcome == UrgeOutcome.RELAPSE.name
            val updated = existing.copy(
                urgeCount = existing.urgeCount + (if (isRelapse) 0 else 1),
                relapseCount = existing.relapseCount + (if (isRelapse) 1 else 0),
                totalCount = existing.totalCount + 1
            )
            cellMap[key] = updated
            totalEvents++
        }

        val maxCount = cellMap.values.maxOfOrNull { it.totalCount } ?: 0

        // Find peak vulnerability cell (weighted: relapses count as 2x risk)
        val peakEntry = cellMap.maxByOrNull { (_, cell) ->
            cell.urgeCount + (cell.relapseCount * 2)
        }

        val summary = if (peakEntry != null && peakEntry.value.totalCount > 0) {
            val peakDay = peakEntry.key.first
            val peakBand = peakEntry.key.second
            val dayName = peakDay.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
            val desc = "$dayName, ${peakBand.label} (${peakEntry.value.totalCount} events)"
            VulnerabilitySummary(
                peakDay = peakDay,
                peakTimeBand = peakBand,
                peakWindowDescription = desc,
                totalRecordedEvents = totalEvents
            )
        } else {
            VulnerabilitySummary(totalRecordedEvents = totalEvents)
        }

        return HeatmapData(
            cells = cellMap,
            summary = summary,
            maxCount = maxCount
        )
    }
}
