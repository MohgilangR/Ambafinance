package com.example.data.repository

import com.example.data.local.AccountSettingsDao
import com.example.data.local.AccountSettingsEntity
import com.example.data.local.TransactionDao
import com.example.data.local.TransactionEntity
import com.example.data.local.TransactionType
import com.example.domain.model.CurrencyStyle
import com.example.domain.model.FinancialSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val settingsDao: AccountSettingsDao
) {
    val settingsFlow: Flow<AccountSettingsEntity> = settingsDao.observeSettings()
        .map { it ?: AccountSettingsEntity() }

    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()

    fun getRecentTransactions(limit: Int = 5): Flow<List<TransactionEntity>> =
        transactionDao.getRecentTransactions(limit)

    val financialSummary: Flow<FinancialSummary> = combine(
        settingsFlow,
        transactionDao.getTotalByType(TransactionType.INCOME),
        transactionDao.getTotalByType(TransactionType.EXPENSE)
    ) { settings, totalIncome, totalExpense ->
        FinancialSummary(
            initialBalance = settings.initialBalance,
            totalIncome = totalIncome,
            totalExpense = totalExpense
        )
    }

    fun getTransactionById(id: Long): Flow<TransactionEntity?> =
        transactionDao.getTransactionById(id)

    suspend fun insertTransaction(transaction: TransactionEntity): Long =
        transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: TransactionEntity) =
        transactionDao.updateTransaction(transaction)

    suspend fun deleteTransactionById(id: Long) =
        transactionDao.deleteTransactionById(id)

    suspend fun updateInitialBalance(newInitialBalance: Long) {
        val current = settingsDao.getSettingsOnce() ?: AccountSettingsEntity()
        settingsDao.upsertSettings(current.copy(initialBalance = newInitialBalance))
    }

    suspend fun updateCurrencyStyle(style: CurrencyStyle) {
        val current = settingsDao.getSettingsOnce() ?: AccountSettingsEntity()
        settingsDao.upsertSettings(current.copy(currencyStyle = style.name))
    }

    suspend fun toggleBalanceHidden() {
        val current = settingsDao.getSettingsOnce() ?: AccountSettingsEntity()
        settingsDao.upsertSettings(current.copy(isBalanceHidden = !current.isBalanceHidden))
    }

    suspend fun setBalanceHidden(hidden: Boolean) {
        val current = settingsDao.getSettingsOnce() ?: AccountSettingsEntity()
        settingsDao.upsertSettings(current.copy(isBalanceHidden = hidden))
    }

    suspend fun setCompactList(compact: Boolean) {
        val current = settingsDao.getSettingsOnce() ?: AccountSettingsEntity()
        settingsDao.upsertSettings(current.copy(isCompactList = compact))
    }

    suspend fun setShowTimeInList(showTime: Boolean) {
        val current = settingsDao.getSettingsOnce() ?: AccountSettingsEntity()
        settingsDao.upsertSettings(current.copy(showTimeInList = showTime))
    }

    suspend fun clearAllData() {
        transactionDao.deleteAllTransactions()
        val current = settingsDao.getSettingsOnce() ?: AccountSettingsEntity()
        settingsDao.upsertSettings(current.copy(initialBalance = 0L))
    }
}
