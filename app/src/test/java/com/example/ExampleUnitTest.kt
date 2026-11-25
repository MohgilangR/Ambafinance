package com.example

import com.example.domain.model.CurrencyFormatter
import com.example.domain.model.CurrencyStyle
import com.example.domain.model.FinancialSummary
import com.example.ui.components.sanitizeNumericInput
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `financial summary calculates final balance accurately`() {
        val summary = FinancialSummary(
            initialBalance = 1_500_000L,
            totalIncome = 750_000L,
            totalExpense = 425_000L
        )
        assertEquals(1_825_000L, summary.finalBalance)
    }

    @Test
    fun `rupiah currency formatter handles positive negative and signed formats`() {
        assertEquals("Rp 1.500.000", CurrencyFormatter.format(1_500_000L, CurrencyStyle.IDR_STANDARD))
        assertEquals("Rp1.500.000", CurrencyFormatter.format(1_500_000L, CurrencyStyle.IDR_COMPACT))
        assertEquals("IDR 1.500.000", CurrencyFormatter.format(1_500_000L, CurrencyStyle.IDR_WITH_CODE))
        assertEquals("Rp 1.500.000,00", CurrencyFormatter.format(1_500_000L, CurrencyStyle.IDR_DECIMAL))
        assertEquals("-Rp 250.000", CurrencyFormatter.format(-250_000L, CurrencyStyle.IDR_STANDARD))
        assertEquals("+Rp 500.000", CurrencyFormatter.format(500_000L, CurrencyStyle.IDR_STANDARD, "+"))
    }

    @Test
    fun `sanitize numeric input strips non-digits and leading zeros`() {
        assertEquals("150000", sanitizeNumericInput("Rp 150.000"))
        assertEquals("0", sanitizeNumericInput("0000"))
        assertEquals("500", sanitizeNumericInput("00500"))
        assertEquals("", sanitizeNumericInput("abc"))
    }
}
