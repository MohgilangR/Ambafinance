package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.MonochromeDatabase
import com.example.data.local.TransactionEntity
import com.example.data.local.TransactionType
import com.example.data.repository.FinanceRepository
import com.example.domain.model.CurrencyFormatter
import com.example.domain.model.CurrencyStyle
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: MonochromeDatabase
    private lateinit var repository: FinanceRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MonochromeDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = FinanceRepository(
            transactionDao = database.transactionDao(),
            settingsDao = database.accountSettingsDao()
        )
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `read app name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Ambafinance", appName)
    }

    @Test
    fun `balance calculation updates accurately across insert edit and delete`() = runTest {
        repository.updateInitialBalance(1_000_000L)

        val incomeId = repository.insertTransaction(
            TransactionEntity(
                title = "Gaji Tambahan",
                amount = 500_000L,
                type = TransactionType.INCOME,
                category = "Gaji",
                timestamp = 1_700_000_000_000L
            )
        )

        val expenseId = repository.insertTransaction(
            TransactionEntity(
                title = "Beli Kopi Susu",
                amount = 35_000L,
                type = TransactionType.EXPENSE,
                category = "Minuman",
                timestamp = 1_700_000_100_000L
            )
        )

        var summary = repository.financialSummary.first()
        assertEquals(1_000_000L, summary.initialBalance)
        assertEquals(500_000L, summary.totalIncome)
        assertEquals(35_000L, summary.totalExpense)
        assertEquals(1_465_000L, summary.finalBalance)

        // Update expense amount
        val existing = repository.getTransactionById(expenseId).first()!!
        repository.updateTransaction(existing.copy(amount = 50_000L))

        summary = repository.financialSummary.first()
        assertEquals(1_450_000L, summary.finalBalance)

        // Delete income
        repository.deleteTransactionById(incomeId)
        summary = repository.financialSummary.first()
        assertEquals(0L, summary.totalIncome)
        assertEquals(950_000L, summary.finalBalance)

        // Clear all data
        repository.clearAllData()
        summary = repository.financialSummary.first()
        assertEquals(0L, summary.initialBalance)
        assertEquals(0L, summary.finalBalance)
        assertEquals(0, repository.allTransactions.first().size)
    }

    @Test
    fun `rupiah currency formatter formats thousand separators cleanly`() {
        assertEquals("Rp 1.500.000", CurrencyFormatter.format(1_500_000L, CurrencyStyle.IDR_STANDARD))
        assertEquals("Rp1.500.000", CurrencyFormatter.format(1_500_000L, CurrencyStyle.IDR_COMPACT))
        assertEquals("IDR 1.500.000", CurrencyFormatter.format(1_500_000L, CurrencyStyle.IDR_WITH_CODE))
        assertEquals("Rp 1.500.000,00", CurrencyFormatter.format(1_500_000L, CurrencyStyle.IDR_DECIMAL))
    }
}
