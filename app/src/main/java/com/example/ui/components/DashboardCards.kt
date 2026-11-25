package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowDownward
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.TransactionEntity
import com.example.data.local.TransactionType
import com.example.domain.model.CurrencyFormatter
import com.example.domain.model.CurrencyStyle
import com.example.domain.model.DateFormatter
import com.example.domain.model.FinancialSummary
import com.example.domain.model.TransactionCategories
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedContainer
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenContainer
import com.example.ui.theme.MonochromeAccent
import com.example.ui.theme.MonochromeOutline
import com.example.ui.theme.MonochromeSurface
import com.example.ui.theme.MonochromeSurfaceSecondary
import com.example.ui.theme.MonochromeTextMuted
import com.example.ui.theme.MonochromeTextPrimary
import com.example.ui.theme.MonochromeTextSecondary

@Composable
fun TotalBalanceHeroCard(
    summary: FinancialSummary,
    currencyStyle: CurrencyStyle,
    isBalanceHidden: Boolean,
    onToggleVisibility: () -> Unit,
    onEditInitialBalance: () -> Unit,
    onQuickAddIncome: () -> Unit,
    onQuickAddExpense: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cardShape = RoundedCornerShape(26.dp)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF282828),
                        Color(0xFF1F1F1F),
                        Color(0xFF191919)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(900f, 900f)
                )
            )
            .border(width = 1.dp, color = Color(0xFF343434), shape = cardShape)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.06f),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.88f, size.height * 0.12f),
                        radius = size.width * 0.65f
                    ),
                    radius = size.width * 0.65f,
                    center = Offset(size.width * 0.88f, size.height * 0.12f)
                )
            }
            .padding(22.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.total_balance_label),
                        style = MaterialTheme.typography.titleSmall,
                        color = MonochromeTextSecondary
                    )
                    Surface(
                        color = MonochromeSurfaceSecondary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "IDR",
                            style = MaterialTheme.typography.labelSmall,
                            color = MonochromeAccent,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onToggleVisibility,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("toggle_balance_visibility")
                ) {
                    Icon(
                        imageVector = if (isBalanceHidden) {
                            Icons.Outlined.VisibilityOff
                        } else {
                            Icons.Outlined.Visibility
                        },
                        contentDescription = stringResource(R.string.toggle_balance_visibility),
                        tint = MonochromeAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            AnimatedContent(
                targetState = isBalanceHidden,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "balance_mask_anim"
            ) { hidden ->
                val displayBalance = if (hidden) {
                    "Rp •••••••"
                } else {
                    CurrencyFormatter.format(summary.finalBalance, currencyStyle)
                }
                Text(
                    text = displayBalance,
                    style = MaterialTheme.typography.displaySmall,
                    color = if (!hidden && summary.finalBalance < 0L) ExpenseRed else MonochromeTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.testTag("total_balance_amount")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Initial balance pill (tappable to edit initial balance)
            Surface(
                color = MonochromeSurfaceSecondary.copy(alpha = 0.72f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable(onClick = onEditInitialBalance)
                    .testTag("edit_initial_balance_chip")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${stringResource(R.string.initial_balance_chip_label)}: ${
                            if (isBalanceHidden) "••••••"
                            else CurrencyFormatter.format(summary.initialBalance, currencyStyle)
                        }",
                        style = MaterialTheme.typography.labelMedium,
                        color = MonochromeTextSecondary
                    )
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = stringResource(R.string.settings_initial_balance),
                        tint = MonochromeAccent,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Income & Expense Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    color = IncomeGreenContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(0.8.dp, IncomeGreen.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .clickable(onClick = onQuickAddIncome)
                        .testTag("quick_add_income_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            tint = IncomeGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.quick_income),
                            style = MaterialTheme.typography.labelLarge,
                            color = IncomeGreen
                        )
                    }
                }

                Surface(
                    color = ExpenseRedContainer,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .border(0.8.dp, ExpenseRed.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                        .clickable(onClick = onQuickAddExpense)
                        .testTag("quick_add_expense_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Remove,
                            contentDescription = null,
                            tint = ExpenseRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.quick_expense),
                            style = MaterialTheme.typography.labelLarge,
                            color = ExpenseRed
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SummaryMetricsRow(
    totalIncome: Long,
    totalExpense: Long,
    currencyStyle: CurrencyStyle,
    isBalanceHidden: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryStatCard(
            title = stringResource(R.string.total_income_label),
            amount = totalIncome,
            currencyStyle = currencyStyle,
            isHidden = isBalanceHidden,
            isIncome = true,
            modifier = Modifier
                .weight(1f)
                .testTag("summary_income_card")
        )
        SummaryStatCard(
            title = stringResource(R.string.total_expense_label),
            amount = totalExpense,
            currencyStyle = currencyStyle,
            isHidden = isBalanceHidden,
            isIncome = false,
            modifier = Modifier
                .weight(1f)
                .testTag("summary_expense_card")
        )
    }
}

@Composable
private fun SummaryStatCard(
    title: String,
    amount: Long,
    currencyStyle: CurrencyStyle,
    isHidden: Boolean,
    isIncome: Boolean,
    modifier: Modifier = Modifier
) {
    val accentColor = if (isIncome) IncomeGreen else ExpenseRed
    val badgeBg = if (isIncome) IncomeGreenContainer else ExpenseRedContainer
    val icon = if (isIncome) Icons.Rounded.ArrowDownward else Icons.Rounded.ArrowUpward

    Surface(
        color = MonochromeSurface,
        shape = RoundedCornerShape(22.dp),
        modifier = modifier.border(1.dp, MonochromeOutline.copy(alpha = 0.6f), RoundedCornerShape(22.dp))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MonochromeTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = if (isHidden) {
                    "Rp ••••••"
                } else {
                    CurrencyFormatter.format(amount, currencyStyle)
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontFeatureSettings = "tnum"
                ),
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TransactionItemRow(
    transaction: TransactionEntity,
    currencyStyle: CurrencyStyle,
    isBalanceHidden: Boolean,
    isCompact: Boolean,
    showTime: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isIncome = transaction.type == TransactionType.INCOME
    val amountColor = if (isIncome) IncomeGreen else ExpenseRed
    val signPrefix = if (isIncome) "+" else "-"
    val categoryIcon = TransactionCategories.iconFor(transaction.category, transaction.type)

    val formattedDate = if (showTime) {
        DateFormatter.formatDateTime(transaction.timestamp)
    } else {
        DateFormatter.formatShortDate(transaction.timestamp)
    }

    val verticalPadding = if (isCompact) 10.dp else 14.dp

    Surface(
        color = MonochromeSurface,
        shape = RoundedCornerShape(18.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(0.8.dp, MonochromeOutline.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag("transaction_item_${transaction.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = verticalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(if (isCompact) 38.dp else 44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MonochromeSurfaceSecondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = transaction.category,
                    tint = if (isIncome) IncomeGreen else MonochromeAccent,
                    modifier = Modifier.size(if (isCompact) 18.dp else 21.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                    color = MonochromeTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${transaction.category} • $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MonochromeTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!isCompact && transaction.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = transaction.note,
                        style = MaterialTheme.typography.labelSmall,
                        color = MonochromeTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = if (isBalanceHidden) {
                    "$signPrefix Rp ••••"
                } else {
                    CurrencyFormatter.format(
                        amount = transaction.amount,
                        style = currencyStyle,
                        withSign = signPrefix
                    )
                },
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontFeatureSettings = "tnum"
                ),
                color = amountColor,
                maxLines = 1
            )
        }
    }
}

@Composable
fun EmptyTransactionsCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MonochromeSurface,
        shape = RoundedCornerShape(22.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MonochromeOutline.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MonochromeSurfaceSecondary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    tint = MonochromeAccent,
                    modifier = Modifier.size(26.dp)
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MonochromeTextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MonochromeTextSecondary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}
