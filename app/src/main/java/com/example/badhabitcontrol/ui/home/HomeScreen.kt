package com.example.badhabitcontrol.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.badhabitcontrol.ui.theme.CalmBackground
import com.example.badhabitcontrol.ui.theme.CalmBorder
import com.example.badhabitcontrol.ui.theme.CalmSurface
import com.example.badhabitcontrol.ui.theme.StatusClean
import com.example.badhabitcontrol.ui.theme.TextMuted
import com.example.badhabitcontrol.ui.theme.TextPrimary
import com.example.badhabitcontrol.ui.theme.TextSecondary
import com.example.badhabitcontrol.ui.viewmodel.HabitCardState
import com.example.badhabitcontrol.ui.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onStartUrge: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CalmBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "TODAY",
            color = TextMuted,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "YOUR CONTROL",
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(28.dp))

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TextSecondary)
            }
        } else {
            state.cigarettes?.let { card ->
                HabitSummaryCard(
                    cardState = card,
                    onCardClick = { onNavigateToDetail(1L) },
                    onUrgeClick = { onStartUrge(1L) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            state.masturbation?.let { card ->
                HabitSummaryCard(
                    cardState = card,
                    onCardClick = { onNavigateToDetail(2L) },
                    onUrgeClick = { onStartUrge(2L) }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun HabitSummaryCard(
    cardState: HabitCardState,
    onCardClick: () -> Unit,
    onUrgeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CalmSurface)
            .border(1.dp, CalmBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onCardClick)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = cardState.habit.name.uppercase(),
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.5.sp
            )
            Text(
                text = "DETAILS ›",
                color = TextMuted,
                fontSize = 11.sp,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Large bold streak number
        Text(
            text = cardState.currentStreak.toString(),
            color = TextPrimary,
            fontSize = 64.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 64.sp
        )

        Text(
            text = "DAYS CLEAN",
            color = StatusClean,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 2.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Since ${cardState.sinceDateText}",
            color = TextMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onUrgeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextPrimary
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, CalmBorder)
        ) {
            Text(
                text = "I'M HAVING AN URGE",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }
    }
}
