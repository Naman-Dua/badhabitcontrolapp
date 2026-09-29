package com.example.badhabitcontrol

import com.example.badhabitcontrol.data.model.FinancialConfig
import com.example.badhabitcontrol.domain.calculator.SavingsCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SavingsCalculatorTest {

    @Test
    fun testDisabledConfigReturnsZeroSavings() {
        val config = FinancialConfig(enabled = false, dailyCost = 15.0)
        val stats = SavingsCalculator.computeSavings(config, totalCleanDays = 10, currentStreak = 5)
        assertEquals(0.0, stats.totalSaved, 0.001)
        assertEquals(0.0, stats.streakSaved, 0.001)
        assertEquals(0, stats.totalUnitsAvoided)
    }

    @Test
    fun testEnabledConfigComputesExactSavings() {
        val config = FinancialConfig(
            enabled = true,
            dailyCost = 12.50,
            currencySymbol = "$",
            unitName = "cigarettes",
            unitsPerDay = 20.0
        )
        val stats = SavingsCalculator.computeSavings(config, totalCleanDays = 10, currentStreak = 4)

        assertEquals(125.0, stats.totalSaved, 0.001)
        assertEquals(50.0, stats.streakSaved, 0.001)
        assertEquals(200, stats.totalUnitsAvoided)
        assertEquals(12.50 * 365.25, stats.projectedAnnualSaved, 0.01)

        // Total saved is $125 -> next milestone is $250 (from milestones [50, 100, 250...])
        assertEquals(250.0, stats.nextMilestoneAmount, 0.001)
        // Progress between 100 and 250: (125 - 100) / (250 - 100) = 25 / 150 = 0.1667
        assertTrue(stats.milestoneProgress in 0.15f..0.18f)
    }

    @Test
    fun testMilestoneProgressionBoundary() {
        val config = FinancialConfig(enabled = true, dailyCost = 50.0)
        val stats = SavingsCalculator.computeSavings(config, totalCleanDays = 1, currentStreak = 1)
        assertEquals(50.0, stats.totalSaved, 0.001)
        assertEquals(100.0, stats.nextMilestoneAmount, 0.001)
        assertEquals(0.0f, stats.milestoneProgress, 0.01f)
    }
}
