package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AccountSettingsEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.TransactionType
import com.example.data.repository.FinanceRepository
import com.example.domain.model.CurrencyStyle
import com.example.domain.model.DateFormatter
import com.example.domain.model.FinancialSummary
import com.example.domain.model.HistoryPeriodFilter
import com.example.domain.model.HistoryTypeFilter
import com.example.domain.model.TransactionGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val summary: FinancialSummary = FinancialSummary(),
    val recentTransactions: List<TransactionEntity> = emptyList(),
    val settings: AccountSettingsEntity = AccountSettingsEntity(),
    val currencyStyle: CurrencyStyle = CurrencyStyle.IDR_STANDARD,
    val isLoading: Boolean = true
)

data class HistoryFilterParams(
    val searchQuery: String = "",
    val typeFilter: HistoryTypeFilter = HistoryTypeFilter.ALL,
    val periodFilter: HistoryPeriodFilter = HistoryPeriodFilter.ALL,
    val customStartMillis: Long? = null,
    val customEndMillis: Long? = null
)

data class HistoryUiState(
    val filterParams: HistoryFilterParams = HistoryFilterParams(),
    val groupedTransactions: List<TransactionGroup> = emptyList(),
    val totalAllTransactions: Int = 0,
    val filteredCount: Int = 0,
    val filteredIncomeTotal: Long = 0L,
    val filteredExpenseTotal: Long = 0L,
    val settings: AccountSettingsEntity = AccountSettingsEntity(),
    val currencyStyle: CurrencyStyle = CurrencyStyle.IDR_STANDARD
)

data class SettingsUiState(
    val settings: AccountSettingsEntity = AccountSettingsEntity(),
    val currencyStyle: CurrencyStyle = CurrencyStyle.IDR_STANDARD,
    val totalTransactionCount: Int = 0
)

class FinanceViewModel(
    private val repository: FinanceRepository
) : ViewModel() {

    private val historyFilterParams = MutableStateFlow(HistoryFilterParams())

    val dashboardUiState: StateFlow<DashboardUiState> = combine(
        repository.financialSummary,
        repository.getRecentTransactions(limit = 6),
        repository.settingsFlow
    ) { summary, recent, settings ->
        DashboardUiState(
            summary = summary,
            recentTransactions = recent,
            settings = settings,
            currencyStyle = CurrencyStyle.fromKey(settings.currencyStyle),
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState()
    )

    val historyUiState: StateFlow<HistoryUiState> = combine(
        repository.allTransactions,
        repository.settingsFlow,
        historyFilterParams
    ) { allTransactions, settings, params ->
        val filtered = filterTransactions(allTransactions, params)
        val grouped = filtered
            .groupBy { DateFormatter.startOfDay(it.timestamp) }
            .entries
            .sortedByDescending { it.key }
            .map { (dayStart, items) ->
                TransactionGroup(
                    dateHeader = DateFormatter.formatGroupHeader(dayStart),
                    dateMillis = dayStart,
                    items = items.sortedWith(
                        compareByDescending<TransactionEntity> { it.timestamp }
                            .thenByDescending { it.id }
                    )
                )
            }

        val incomeSum = filtered.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expenseSum = filtered.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        HistoryUiState(
            filterParams = params,
            groupedTransactions = grouped,
            totalAllTransactions = allTransactions.size,
            filteredCount = filtered.size,
            filteredIncomeTotal = incomeSum,
            filteredExpenseTotal = expenseSum,
            settings = settings,
            currencyStyle = CurrencyStyle.fromKey(settings.currencyStyle)
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HistoryUiState()
    )

    val settingsUiState: StateFlow<SettingsUiState> = combine(
        repository.settingsFlow,
        repository.allTransactions
    ) { settings, transactions ->
        SettingsUiState(
            settings = settings,
            currencyStyle = CurrencyStyle.fromKey(settings.currencyStyle),
            totalTransactionCount = transactions.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SettingsUiState()
    )

    fun getTransactionById(id: Long): Flow<TransactionEntity?> =
        repository.getTransactionById(id)

    fun toggleBalanceVisibility() {
        viewModelScope.launch {
            repository.toggleBalanceHidden()
        }
    }

    fun setSearchQuery(query: String) {
        historyFilterParams.update { it.copy(searchQuery = query) }
    }

    fun setTypeFilter(filter: HistoryTypeFilter) {
        historyFilterParams.update { it.copy(typeFilter = filter) }
    }

    fun setPeriodFilter(filter: HistoryPeriodFilter) {
        historyFilterParams.update { it.copy(periodFilter = filter) }
    }

    fun setCustomDateRange(startMillis: Long?, endMillis: Long?) {
        historyFilterParams.update {
            it.copy(
                periodFilter = if (startMillis != null && endMillis != null) {
                    HistoryPeriodFilter.CUSTOM
                } else {
                    HistoryPeriodFilter.ALL
                },
                customStartMillis = startMillis,
                customEndMillis = endMillis
            )
        }
    }

    fun resetHistoryFilters() {
        historyFilterParams.value = HistoryFilterParams()
    }

    fun wouldExceedAvailableBalance(
        expenseAmount: Long,
        existingTransaction: TransactionEntity? = null
    ): Boolean {
        val currentBalance = dashboardUiState.value.summary.finalBalance
        val adjustedAvailableBalance = when {
            existingTransaction == null -> currentBalance
            existingTransaction.type == TransactionType.EXPENSE ->
                currentBalance + existingTransaction.amount
            else -> currentBalance - existingTransaction.amount
        }
        return expenseAmount > adjustedAvailableBalance
    }

    fun saveTransaction(
        existingId: Long? = null,
        title: String,
        amount: Long,
        type: TransactionType,
        category: String,
        note: String,
        timestamp: Long,
        createdAt: Long? = null,
        onComplete: () -> Unit = {}
    ) {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty() || amount <= 0L) return

        viewModelScope.launch {
            val entity = TransactionEntity(
                id = existingId ?: 0L,
                title = cleanTitle,
                amount = amount,
                type = type,
                category = category.ifBlank { "Lainnya" },
                note = note.trim(),
                timestamp = timestamp,
                createdAt = createdAt ?: System.currentTimeMillis()
            )
            if (existingId != null && existingId > 0L) {
                repository.updateTransaction(entity)
            } else {
                repository.insertTransaction(entity)
            }
            onComplete()
        }
    }

    fun deleteTransaction(id: Long, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteTransactionById(id)
            onComplete()
        }
    }

    fun updateInitialBalance(amount: Long, onComplete: () -> Unit = {}) {
        if (amount < 0L) return
        viewModelScope.launch {
            repository.updateInitialBalance(amount)
            onComplete()
        }
    }

    fun updateCurrencyStyle(style: CurrencyStyle) {
        viewModelScope.launch {
            repository.updateCurrencyStyle(style)
        }
    }

    fun setBalanceHidden(hidden: Boolean) {
        viewModelScope.launch {
            repository.setBalanceHidden(hidden)
        }
    }

    fun setCompactList(compact: Boolean) {
        viewModelScope.launch {
            repository.setCompactList(compact)
        }
    }

    fun setShowTimeInList(show: Boolean) {
        viewModelScope.launch {
            repository.setShowTimeInList(show)
        }
    }

    fun clearAllData(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            repository.clearAllData()
            historyFilterParams.value = HistoryFilterParams()
            onComplete()
        }
    }

    private fun filterTransactions(
        transactions: List<TransactionEntity>,
        params: HistoryFilterParams
    ): List<TransactionEntity> {
        val now = System.currentTimeMillis()
        val todayStart = DateFormatter.startOfDay(now)
        val todayEnd = DateFormatter.endOfDay(now)
        val weekStart = DateFormatter.daysAgoStart(6)
        val monthStart = DateFormatter.startOfCurrentMonth()

        return transactions.filter { tx ->
            val matchesQuery = params.searchQuery.isBlank() ||
                tx.title.contains(params.searchQuery.trim(), ignoreCase = true) ||
                tx.category.contains(params.searchQuery.trim(), ignoreCase = true) ||
                tx.note.contains(params.searchQuery.trim(), ignoreCase = true)

            val matchesType = when (params.typeFilter) {
                HistoryTypeFilter.ALL -> true
                HistoryTypeFilter.INCOME -> tx.type == TransactionType.INCOME
                HistoryTypeFilter.EXPENSE -> tx.type == TransactionType.EXPENSE
            }

            val matchesPeriod = when (params.periodFilter) {
                HistoryPeriodFilter.ALL -> true
                HistoryPeriodFilter.TODAY -> tx.timestamp in todayStart..todayEnd
                HistoryPeriodFilter.LAST_7_DAYS -> tx.timestamp in weekStart..todayEnd
                HistoryPeriodFilter.THIS_MONTH -> tx.timestamp in monthStart..todayEnd
                HistoryPeriodFilter.CUSTOM -> {
                    val start = params.customStartMillis?.let { DateFormatter.startOfDay(it) }
                    val end = params.customEndMillis?.let { DateFormatter.endOfDay(it) }
                    when {
                        start != null && end != null -> tx.timestamp in start..end
                        start != null -> tx.timestamp >= start
                        end != null -> tx.timestamp <= end
                        else -> true
                    }
                }
            }

            matchesQuery && matchesType && matchesPeriod
        }
    }
}

class FinanceViewModelFactory(
    private val repository: FinanceRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            return FinanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
