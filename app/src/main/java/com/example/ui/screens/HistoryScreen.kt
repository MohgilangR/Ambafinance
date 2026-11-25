package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.TransactionEntity
import com.example.domain.model.CurrencyFormatter
import com.example.domain.model.DateFormatter
import com.example.domain.model.HistoryPeriodFilter
import com.example.domain.model.HistoryTypeFilter
import com.example.ui.components.EmptyTransactionsCard
import com.example.ui.components.TransactionItemRow
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.MonochromeAccent
import com.example.ui.theme.MonochromeAccentBright
import com.example.ui.theme.MonochromeBackground
import com.example.ui.theme.MonochromeOutline
import com.example.ui.theme.MonochromeSurface
import com.example.ui.theme.MonochromeSurfaceSecondary
import com.example.ui.theme.MonochromeTextPrimary
import com.example.ui.theme.MonochromeTextSecondary
import com.example.ui.viewmodel.HistoryUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    uiState: HistoryUiState,
    onSearchQueryChange: (String) -> Unit,
    onTypeFilterChange: (HistoryTypeFilter) -> Unit,
    onPeriodFilterChange: (HistoryPeriodFilter) -> Unit,
    onCustomDateRangeSelected: (Long?, Long?) -> Unit,
    onResetFilters: () -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDateRangeDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MonochromeBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.history_title),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MonochromeTextPrimary
                    )
                    Text(
                        text = "${uiState.filteredCount} transaksi",
                        style = MaterialTheme.typography.labelMedium,
                        color = MonochromeTextSecondary
                    )
                }

                // Search bar
                OutlinedTextField(
                    value = uiState.filterParams.searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text(stringResource(R.string.search_transactions_placeholder)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = MonochromeTextSecondary
                        )
                    },
                    trailingIcon = {
                        if (uiState.filterParams.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = "Hapus pencarian",
                                    tint = MonochromeAccent
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MonochromeAccentBright,
                        unfocusedBorderColor = MonochromeOutline,
                        focusedContainerColor = MonochromeSurface,
                        unfocusedContainerColor = MonochromeSurface,
                        focusedTextColor = MonochromeTextPrimary,
                        unfocusedTextColor = MonochromeTextPrimary,
                        cursorColor = MonochromeAccentBright
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("history_search_input")
                )

                // Type filter segmented chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HistoryTypeFilter.entries.forEach { typeFilter ->
                        val selected = uiState.filterParams.typeFilter == typeFilter
                        Surface(
                            color = if (selected) MonochromeAccentBright else MonochromeSurface,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    0.8.dp,
                                    if (selected) MonochromeAccentBright else MonochromeOutline,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { onTypeFilterChange(typeFilter) }
                                .testTag("history_type_${typeFilter.name.lowercase()}")
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = typeFilter.label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selected) MonochromeBackground else MonochromeTextSecondary
                                )
                            }
                        }
                    }
                }

                // Period filter chips (scrollable)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HistoryPeriodFilter.entries.forEach { period ->
                        val selected = uiState.filterParams.periodFilter == period
                        val labelText = if (
                            period == HistoryPeriodFilter.CUSTOM &&
                            selected &&
                            uiState.filterParams.customStartMillis != null &&
                            uiState.filterParams.customEndMillis != null
                        ) {
                            "${DateFormatter.formatShortDate(uiState.filterParams.customStartMillis)} - " +
                                DateFormatter.formatShortDate(uiState.filterParams.customEndMillis)
                        } else {
                            period.label
                        }

                        FilterChip(
                            selected = selected,
                            onClick = {
                                if (period == HistoryPeriodFilter.CUSTOM) {
                                    showDateRangeDialog = true
                                } else {
                                    onPeriodFilterChange(period)
                                }
                            },
                            label = { Text(labelText) },
                            leadingIcon = if (period == HistoryPeriodFilter.CUSTOM) {
                                {
                                    Icon(
                                        imageVector = Icons.Rounded.CalendarMonth,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = MonochromeSurface,
                                labelColor = MonochromeTextSecondary,
                                iconColor = MonochromeTextSecondary,
                                selectedContainerColor = MonochromeSurfaceSecondary,
                                selectedLabelColor = MonochromeTextPrimary,
                                selectedLeadingIconColor = MonochromeAccentBright
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = MonochromeOutline,
                                selectedBorderColor = MonochromeAccent
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                // Filtered Summary Strip when there are transactions
                if (uiState.filteredCount > 0) {
                    Surface(
                        color = MonochromeSurface,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.8.dp, MonochromeOutline.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Masuk",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MonochromeTextSecondary
                                )
                                Text(
                                    text = if (uiState.settings.isBalanceHidden) "Rp ••••"
                                    else CurrencyFormatter.format(uiState.filteredIncomeTotal, uiState.currencyStyle, "+"),
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = IncomeGreen
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Keluar",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MonochromeTextSecondary
                                )
                                Text(
                                    text = if (uiState.settings.isBalanceHidden) "Rp ••••"
                                    else CurrencyFormatter.format(uiState.filteredExpenseTotal, uiState.currencyStyle, "-"),
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                                    color = ExpenseRed
                                )
                            }
                        }
                    }
                }
            }

            if (uiState.groupedTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        EmptyTransactionsCard(
                            title = if (uiState.totalAllTransactions == 0) {
                                stringResource(R.string.empty_transactions_title)
                            } else {
                                "Tidak ada hasil"
                            },
                            subtitle = if (uiState.totalAllTransactions == 0) {
                                stringResource(R.string.empty_transactions_subtitle)
                            } else {
                                stringResource(R.string.history_empty_filtered)
                            }
                        )
                        if (uiState.totalAllTransactions > 0) {
                            Spacer(modifier = Modifier.height(12.dp))
                            TextButton(onClick = onResetFilters) {
                                Text("Reset Filter", color = MonochromeAccentBright)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = 20.dp,
                        end = 20.dp,
                        top = 4.dp,
                        bottom = 28.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    uiState.groupedTransactions.forEach { group ->
                        item(key = "header_${group.dateMillis}") {
                            Text(
                                text = group.dateHeader,
                                style = MaterialTheme.typography.labelLarge,
                                color = MonochromeTextSecondary,
                                modifier = Modifier.padding(top = 8.dp, bottom = 2.dp, start = 2.dp)
                            )
                        }
                        items(
                            items = group.items,
                            key = { it.id }
                        ) { tx ->
                            TransactionItemRow(
                                transaction = tx,
                                currencyStyle = uiState.currencyStyle,
                                isBalanceHidden = uiState.settings.isBalanceHidden,
                                isCompact = uiState.settings.isCompactList,
                                showTime = uiState.settings.showTimeInList,
                                onClick = { onTransactionClick(tx) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDateRangeDialog) {
        val rangeState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = uiState.filterParams.customStartMillis,
            initialSelectedEndDateMillis = uiState.filterParams.customEndMillis
        )
        DatePickerDialog(
            onDismissRequest = { showDateRangeDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onCustomDateRangeSelected(
                            rangeState.selectedStartDateMillis,
                            rangeState.selectedEndDateMillis ?: rangeState.selectedStartDateMillis
                        )
                        showDateRangeDialog = false
                    }
                ) {
                    Text("Terapkan", color = MonochromeAccentBright)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangeDialog = false }) {
                    Text(stringResource(R.string.action_cancel), color = MonochromeTextSecondary)
                }
            }
        ) {
            DateRangePicker(
                state = rangeState,
                modifier = Modifier.height(420.dp)
            )
        }
    }
}
