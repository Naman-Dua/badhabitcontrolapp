package com.example.badhabitcontrol.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.badhabitcontrol.data.model.HeatmapCell
import com.example.badhabitcontrol.data.model.HeatmapData
import com.example.badhabitcontrol.data.model.TimeBand
import com.example.badhabitcontrol.ui.theme.CalmBorder
import com.example.badhabitcontrol.ui.theme.CalmSurface
import com.example.badhabitcontrol.ui.theme.StatusRelapse
import com.example.badhabitcontrol.ui.theme.StatusUrgeManaged
import com.example.badhabitcontrol.ui.theme.TextMuted
import com.example.badhabitcontrol.ui.theme.TextPrimary
import com.example.badhabitcontrol.ui.theme.TextSecondary
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun HeatmapView(
    data: HeatmapData,
    modifier: Modifier = Modifier
) {
    var selectedCell by remember { mutableStateOf<HeatmapCell?>(null) }
    val days = DayOfWeek.entries
    val timeBands = TimeBand.entries

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CalmSurface)
            .border(1.dp, CalmBorder, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        // Section Title
        Text(
            text = "VULNERABILITY HEATMAP",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "Urge & Relapse Frequency by Time & Day",
            color = TextSecondary,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Peak Vulnerability Summary banner
        if (data.summary.totalRecordedEvents > 0 && data.summary.peakDay != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(StatusUrgeManaged.copy(alpha = 0.12f))
                    .border(1.dp, StatusUrgeManaged.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "⚠ PEAK RISK WINDOW",
                            color = StatusUrgeManaged,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = data.summary.peakWindowDescription,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Time band header labels
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(36.dp)) // Offset for Day names
            for (band in timeBands) {
                Text(
                    text = band.shortLabel,
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Matrix Grid Rows (Mon-Sun)
        for (day in days) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Day name
                Text(
                    text = day.getDisplayName(TextStyle.SHORT, Locale.ENGLISH).take(3),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.width(36.dp)
                )

                for (band in timeBands) {
                    val cell = data.cells[day to band] ?: HeatmapCell(day, band)
                    val isSelected = (selectedCell == cell && cell.totalCount > 0)

                    val cellColor = when {
                        cell.relapseCount > 0 -> {
                            val alpha = (0.4f + (cell.relapseCount.toFloat() / data.maxCount.coerceAtLeast(1)) * 0.6f)
                                .coerceIn(0.4f, 1.0f)
                            StatusRelapse.copy(alpha = alpha)
                        }
                        cell.urgeCount > 0 -> {
                            val alpha = (0.35f + (cell.urgeCount.toFloat() / data.maxCount.coerceAtLeast(1)) * 0.65f)
                                .coerceIn(0.35f, 1.0f)
                            StatusUrgeManaged.copy(alpha = alpha)
                        }
                        else -> CalmBorder.copy(alpha = 0.25f)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.2f)
                            .padding(2.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(cellColor)
                            .then(
                                if (isSelected) {
                                    Modifier.border(1.5.dp, TextPrimary, RoundedCornerShape(4.dp))
                                } else {
                                    Modifier
                                }
                            )
                            .clickable {
                                selectedCell = if (selectedCell == cell) null else cell
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (cell.totalCount > 0) {
                            Text(
                                text = cell.totalCount.toString(),
                                color = if (cell.relapseCount > 0) Color.White else TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Selected Cell Tooltip
        selectedCell?.let { cell ->
            if (cell.totalCount > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                val dayStr = cell.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(CalmBorder.copy(alpha = 0.5f))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "$dayStr (${cell.timeBand.label}): ${cell.urgeCount} urges managed, ${cell.relapseCount} relapses",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(CalmBorder.copy(alpha = 0.3f))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "None", color = TextMuted, fontSize = 10.sp)

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(StatusUrgeManaged.copy(alpha = 0.7f))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Urge Managed", color = TextMuted, fontSize = 10.sp)

            Spacer(modifier = Modifier.width(12.dp))

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(StatusRelapse.copy(alpha = 0.8f))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Relapse", color = TextMuted, fontSize = 10.sp)
        }
    }
}
