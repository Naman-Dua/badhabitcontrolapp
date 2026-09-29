package com.example.badhabitcontrol.domain.calculator

import com.example.badhabitcontrol.data.model.FinancialConfig
import com.example.badhabitcontrol.data.model.FinancialStats
import kotlin.math.roundToInt

object SavingsCalculator {

    private val MILESTONES = listOf(50.0, 100.0, 250.0, 500.0, 1000.0, 2500.0, 5000.0, 10000.0)

    fun computeSavings(
        config: FinancialConfig,
        totalCleanDays: Int,
        currentStreak: Int
    ): FinancialStats {
        if (!config.enabled || config.dailyCost <= 0.0) {
            return FinancialStats(currencySymbol = config.currencySymbol)
        }

        val totalSaved = totalCleanDays * config.dailyCost
        val streakSaved = currentStreak * config.dailyCost
        val projectedAnnual = config.dailyCost * 365.25
        val unitsAvoided = (totalCleanDays * config.unitsPerDay).roundToInt()

        // Milestone calculation
        var prevMilestone = 0.0
        var nextMilestone = MILESTONES.firstOrNull { it > totalSaved } ?: (totalSaved + 5000.0)
        for (m in MILESTONES) {
            if (m <= totalSaved) {
                prevMilestone = m
            } else {
                nextMilestone = m
                break
            }
        }

        val range = (nextMilestone - prevMilestone).coerceAtLeast(1.0)
        val progress = ((totalSaved - prevMilestone) / range).toFloat().coerceIn(0f, 1f)

        return FinancialStats(
            totalSaved = totalSaved,
            streakSaved = streakSaved,
            projectedAnnualSaved = projectedAnnual,
            totalUnitsAvoided = unitsAvoided,
            currencySymbol = config.currencySymbol,
            nextMilestoneAmount = nextMilestone,
            milestoneProgress = progress
        )
    }
}
