package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.ui.components.EmptyTransactionsCard
import com.example.ui.components.InitialBalanceEditDialog
import com.example.ui.components.QuickAddActionSheet
import com.example.ui.components.SummaryMetricsRow
import com.example.ui.components.TotalBalanceHeroCard
import com.example.ui.components.TransactionItemRow
import com.example.ui.theme.MonochromeAccent
import com.example.ui.theme.MonochromeAccentBright
import com.example.ui.theme.MonochromeBackground
import com.example.ui.theme.MonochromeOutline
import com.example.ui.theme.MonochromeSurface
import com.example.ui.theme.MonochromeTextPrimary
import com.example.ui.theme.MonochromeTextSecondary
import com.example.ui.viewmodel.DashboardUiState

@Composable
fun DashboardScreen(
    uiState: DashboardUiState,
    onToggleBalanceVisibility: () -> Unit,
    onUpdateInitialBalance: (Long) -> Unit,
    onNavigateToAddIncome: () -> Unit,
    onNavigateToAddExpense: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onTransactionClick: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showQuickAddSheet by remember { mutableStateOf(false) }
    var showInitialBalanceDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MonochromeBackground),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 16.dp,
                bottom = 104.dp
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MonochromeTextPrimary
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.dashboard_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MonochromeTextSecondary
                        )
                    }

                    Surface(
                        color = MonochromeSurface,
                        shape = CircleShape,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .border(1.dp, MonochromeOutline, CircleShape)
                            .clickable { showQuickAddSheet = true }
                            .testTag("header_add_button")
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = stringResource(R.string.add_transaction_fab),
                                tint = MonochromeAccentBright
                            )
                        }
                    }
                }
            }

            item {
                TotalBalanceHeroCard(
                    summary = uiState.summary,
                    currencyStyle = uiState.currencyStyle,
                    isBalanceHidden = uiState.settings.isBalanceHidden,
                    onToggleVisibility = onToggleBalanceVisibility,
                    onEditInitialBalance = { showInitialBalanceDialog = true },
                    onQuickAddIncome = onNavigateToAddIncome,
                    onQuickAddExpense = onNavigateToAddExpense
                )
            }

            item {
                SummaryMetricsRow(
                    totalIncome = uiState.summary.totalIncome,
                    totalExpense = uiState.summary.totalExpense,
                    currencyStyle = uiState.currencyStyle,
                    isBalanceHidden = uiState.settings.isBalanceHidden
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.recent_transactions_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MonochromeTextPrimary
                    )

                    if (uiState.recentTransactions.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(onClick = onNavigateToHistory)
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                .testTag("see_all_history_button"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.see_all_action),
                                style = MaterialTheme.typography.labelLarge,
                                color = MonochromeAccent
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                                contentDescription = null,
                                tint = MonochromeAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            if (uiState.recentTransactions.isEmpty()) {
                item {
                    EmptyTransactionsCard(
                        title = stringResource(R.string.empty_transactions_title),
                        subtitle = stringResource(R.string.empty_transactions_subtitle),
                        modifier = Modifier.testTag("dashboard_empty_state")
                    )
                }
            } else {
                items(
                    items = uiState.recentTransactions,
                    key = { it.id }
                ) { transaction ->
                    TransactionItemRow(
                        transaction = transaction,
                        currencyStyle = uiState.currencyStyle,
                        isBalanceHidden = uiState.settings.isBalanceHidden,
                        isCompact = uiState.settings.isCompactList,
                        showTime = uiState.settings.showTimeInList,
                        onClick = { onTransactionClick(transaction) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { showQuickAddSheet = true },
            containerColor = MonochromeAccentBright,
            contentColor = MonochromeBackground,
            shape = CircleShape,
            elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 20.dp)
                .size(58.dp)
                .testTag("fab_add_transaction")
        ) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = stringResource(R.string.add_transaction_fab),
                modifier = Modifier.size(28.dp)
            )
        }
    }

    if (showQuickAddSheet) {
        QuickAddActionSheet(
            onAddExpense = {
                showQuickAddSheet = false
                onNavigateToAddExpense()
            },
            onAddIncome = {
                showQuickAddSheet = false
                onNavigateToAddIncome()
            },
            onEditInitialBalance = {
                showQuickAddSheet = false
                showInitialBalanceDialog = true
            },
            onDismiss = { showQuickAddSheet = false }
        )
    }

    if (showInitialBalanceDialog) {
        InitialBalanceEditDialog(
            currentInitialBalance = uiState.summary.initialBalance,
            onSave = { newBalance ->
                onUpdateInitialBalance(newBalance)
                showInitialBalanceDialog = false
            },
            onDismiss = { showInitialBalanceDialog = false }
        )
    }
}
