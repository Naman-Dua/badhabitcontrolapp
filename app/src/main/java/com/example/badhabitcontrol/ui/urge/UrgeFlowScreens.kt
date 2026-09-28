package com.example.badhabitcontrol.ui.urge

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.badhabitcontrol.data.model.UrgeIntensity
import com.example.badhabitcontrol.data.model.UrgeOutcome
import com.example.badhabitcontrol.data.model.UrgeTrigger
import com.example.badhabitcontrol.ui.components.UrgeWaveCanvas
import com.example.badhabitcontrol.ui.theme.CalmBackground
import com.example.badhabitcontrol.ui.theme.CalmBorder
import com.example.badhabitcontrol.ui.theme.CalmSurface
import com.example.badhabitcontrol.ui.theme.StatusClean
import com.example.badhabitcontrol.ui.theme.StatusRelapse
import com.example.badhabitcontrol.ui.theme.StatusUrgeManaged
import com.example.badhabitcontrol.ui.theme.TextMuted
import com.example.badhabitcontrol.ui.theme.TextPrimary
import com.example.badhabitcontrol.ui.theme.TextSecondary
import com.example.badhabitcontrol.ui.viewmodel.UrgeStep
import com.example.badhabitcontrol.ui.viewmodel.UrgeViewModel
import java.util.Locale

@Composable
fun UrgeFlowScreen(
    habitName: String,
    viewModel: UrgeViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.step) {
        if (state.step == UrgeStep.DONE) {
            onDismiss()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CalmBackground)
    ) {
        when (state.step) {
            UrgeStep.SETUP -> {
                UrgeSetupView(
                    habitName = habitName,
                    selectedIntensity = state.intensity,
                    selectedDurationSec = state.timerSeconds,
                    selectedTrigger = state.trigger,
                    onSelectIntensity = viewModel::setIntensity,
                    onSelectDuration = viewModel::setTimerDuration,
                    onSelectTrigger = viewModel::setTrigger,
                    onStart = viewModel::startTimer,
                    onCancel = onDismiss
                )
            }
            UrgeStep.TIMER -> {
                UrgeTimerView(
                    habitName = habitName,
                    remainingSeconds = state.remainingSeconds,
                    onFinishEarly = viewModel::finishTimerEarly,
                    onCancel = onDismiss
                )
            }
            UrgeStep.OUTCOME -> {
                UrgeOutcomeView(
                    habitName = habitName,
                    onSelectOutcome = viewModel::recordOutcome
                )
            }
            UrgeStep.DONE -> {
                // Automatically dismissed via LaunchedEffect
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun UrgeSetupView(
    habitName: String,
    selectedIntensity: UrgeIntensity,
    selectedDurationSec: Int,
    selectedTrigger: UrgeTrigger,
    onSelectIntensity: (UrgeIntensity) -> Unit,
    onSelectDuration: (Int) -> Unit,
    onSelectTrigger: (UrgeTrigger) -> Unit,
    onStart: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onCancel, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = TextSecondary
                )
            }
        }

        Text(
            text = "I'M HAVING AN URGE",
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = habitName.uppercase(),
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Intensity
        Text(
            text = "HOW STRONG?",
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            UrgeIntensity.entries.forEach { intensity ->
                val isSelected = (intensity == selectedIntensity)
                SelectableChip(
                    text = intensity.label.uppercase(),
                    isSelected = isSelected,
                    onClick = { onSelectIntensity(intensity) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Wait Duration
        Text(
            text = "WAIT BEFORE ACTING",
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val durations = listOf(180 to "3 MIN", 300 to "5 MIN", 600 to "10 MIN", 900 to "15 MIN")
            durations.forEach { (sec, label) ->
                val isSelected = (sec == selectedDurationSec)
                SelectableChip(
                    text = label,
                    isSelected = isSelected,
                    onClick = { onSelectDuration(sec) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Trigger selection
        Text(
            text = "TRIGGER",
            color = TextMuted,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            UrgeTrigger.entries.forEach { trigger ->
                val isSelected = (trigger == selectedTrigger)
                SelectableChip(
                    text = trigger.label,
                    isSelected = isSelected,
                    onClick = { onSelectTrigger(trigger) }
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        Button(
            onClick = onStart,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CalmSurface,
                contentColor = TextPrimary
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, CalmBorder)
        ) {
            Text(
                text = "START",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun UrgeTimerView(
    habitName: String,
    remainingSeconds: Int,
    onFinishEarly: () -> Unit,
    onCancel: () -> Unit
) {
    val minutes = remainingSeconds / 60
    val seconds = remainingSeconds % 60
    val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            IconButton(onClick = onCancel, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Cancel",
                    tint = TextSecondary
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = timeFormatted,
                color = TextPrimary,
                fontSize = 64.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "URGE WAVE",
                color = TextMuted,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            UrgeWaveCanvas()

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = habitName.uppercase(),
                color = TextSecondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = onFinishEarly,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CalmSurface,
                    contentColor = TextPrimary
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, CalmBorder)
            ) {
                Text(
                    text = "I'M READY / RECORD OUTCOME",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextMuted
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, CalmBorder.copy(alpha = 0.5f))
            ) {
                Text(
                    text = "CANCEL",
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

@Composable
private fun UrgeOutcomeView(
    habitName: String,
    onSelectOutcome: (UrgeOutcome) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = habitName.uppercase(),
            color = TextMuted,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "HOW ARE YOU NOW?",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Urge Passed (Managed)
        OutcomeButton(
            text = "URGE PASSED",
            color = StatusClean,
            subtitle = "Managed successfully — streak continues",
            onClick = { onSelectOutcome(UrgeOutcome.PASSED) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Still an Urge
        OutcomeButton(
            text = "STILL AN URGE",
            color = StatusUrgeManaged,
            subtitle = "Paused and endured — streak remains intact",
            onClick = { onSelectOutcome(UrgeOutcome.STILL_URGE) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Relapse
        OutcomeButton(
            text = "RELAPSE",
            color = StatusRelapse,
            subtitle = "Recorded neutrally — previous history preserved",
            onClick = { onSelectOutcome(UrgeOutcome.RELAPSE) }
        )
    }
}

@Composable
private fun OutcomeButton(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CalmSurface)
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 18.dp, horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            color = TextMuted,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun SelectableChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) TextPrimary else CalmSurface)
            .border(
                1.dp,
                if (isSelected) TextPrimary else CalmBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 14.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSelected) CalmBackground else TextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}
