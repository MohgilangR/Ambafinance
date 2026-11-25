package com.example.ui.components

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.CurrencyFormatter
import com.example.domain.model.CurrencyStyle
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedContainer
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.IncomeGreenContainer
import com.example.ui.theme.MonochromeAccent
import com.example.ui.theme.MonochromeAccentBright
import com.example.ui.theme.MonochromeBackground
import com.example.ui.theme.MonochromeOutline
import com.example.ui.theme.MonochromeSurface
import com.example.ui.theme.MonochromeSurfaceSecondary
import com.example.ui.theme.MonochromeTextPrimary
import com.example.ui.theme.MonochromeTextSecondary
import com.example.ui.theme.WarningAmber

@Composable
fun ConfirmDestructiveDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MonochromeSurface,
        titleContentColor = MonochromeTextPrimary,
        textContentColor = MonochromeTextSecondary,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(text = title, style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ExpenseRed,
                    contentColor = MonochromeBackground
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("dialog_confirm_destructive")
            ) {
                Text(confirmText, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.testTag("dialog_cancel_destructive")
            ) {
                Text(stringResource(R.string.action_cancel), color = MonochromeAccent)
            }
        }
    )
}

@Composable
fun OverdraftWarningDialog(
    expenseAmount: Long,
    availableBalance: Long,
    currencyStyle: CurrencyStyle,
    onConfirmSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MonochromeSurface,
        shape = RoundedCornerShape(24.dp),
        icon = {
            Icon(
                imageVector = Icons.Rounded.WarningAmber,
                contentDescription = null,
                tint = WarningAmber,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = stringResource(R.string.overdraft_warning_title),
                style = MaterialTheme.typography.titleLarge,
                color = MonochromeTextPrimary
            )
        },
        text = {
            val formattedExpense = CurrencyFormatter.format(expenseAmount, currencyStyle)
            val formattedBalance = CurrencyFormatter.format(availableBalance, currencyStyle)
            Text(
                text = "Pengeluaran sebesar $formattedExpense melebihi saldo tersedia ($formattedBalance). Saldo akhir akan menjadi negatif jika dilanjutkan.",
                style = MaterialTheme.typography.bodyMedium,
                color = MonochromeTextSecondary
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmSave,
                colors = ButtonDefaults.buttonColors(
                    containerColor = WarningAmber,
                    contentColor = MonochromeBackground
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("overdraft_confirm_button")
            ) {
                Text(stringResource(R.string.overdraft_confirm_continue), fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel), color = MonochromeAccent)
            }
        }
    )
}

@Composable
fun InitialBalanceEditDialog(
    currentInitialBalance: Long,
    onSave: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    var rawDigits by remember {
        mutableStateOf(if (currentInitialBalance > 0L) currentInitialBalance.toString() else "")
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MonochromeSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = stringResource(R.string.settings_initial_balance),
                style = MaterialTheme.typography.titleLarge,
                color = MonochromeTextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(R.string.initial_balance_helper),
                    style = MaterialTheme.typography.bodySmall,
                    color = MonochromeTextSecondary
                )
                OutlinedTextField(
                    value = rawDigits,
                    onValueChange = { input ->
                        rawDigits = sanitizeNumericInput(input)
                        errorMessage = null
                    },
                    label = { Text(stringResource(R.string.initial_balance_input_label)) },
                    prefix = { Text("Rp ", color = MonochromeAccent) },
                    placeholder = { Text("0") },
                    singleLine = true,
                    visualTransformation = ThousandSeparatorVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { msg -> { Text(msg, color = ExpenseRed) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MonochromeAccentBright,
                        unfocusedBorderColor = MonochromeOutline,
                        focusedTextColor = MonochromeTextPrimary,
                        unfocusedTextColor = MonochromeTextPrimary,
                        focusedLabelColor = MonochromeAccentBright,
                        unfocusedLabelColor = MonochromeTextSecondary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("initial_balance_dialog_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val value = if (rawDigits.isBlank()) 0L else rawDigits.toLongOrNull()
                    if (value == null || value < 0L) {
                        errorMessage = "Masukkan nominal saldo awal yang valid."
                    } else {
                        onSave(value)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MonochromeAccentBright,
                    contentColor = MonochromeBackground
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.testTag("initial_balance_dialog_save")
            ) {
                Text("Simpan", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel), color = MonochromeAccent)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddActionSheet(
    onAddExpense: () -> Unit,
    onAddIncome: () -> Unit,
    onEditInitialBalance: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MonochromeSurface,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Catat Keuangan",
                style = MaterialTheme.typography.titleLarge,
                color = MonochromeTextPrimary
            )
            Text(
                text = "Pilih jenis pencatatan yang ingin dilakukan",
                style = MaterialTheme.typography.bodySmall,
                color = MonochromeTextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))

            ActionSheetRow(
                icon = Icons.Rounded.Remove,
                iconTint = ExpenseRed,
                iconBg = ExpenseRedContainer,
                title = "Catat Pengeluaran",
                subtitle = "Makan, minuman, transportasi, jajan, atau custom",
                onClick = onAddExpense,
                testTag = "sheet_add_expense"
            )

            ActionSheetRow(
                icon = Icons.Rounded.Add,
                iconTint = IncomeGreen,
                iconBg = IncomeGreenContainer,
                title = "Tambah Pemasukan",
                subtitle = "Gaji, bonus, freelance, atau pemasukan lainnya",
                onClick = onAddIncome,
                testTag = "sheet_add_income"
            )

            ActionSheetRow(
                icon = Icons.Rounded.AccountBalanceWallet,
                iconTint = MonochromeAccentBright,
                iconBg = MonochromeSurfaceSecondary,
                title = "Atur Saldo Awal",
                subtitle = "Tetapkan saldo awal sebagai dasar perhitungan",
                onClick = onEditInitialBalance,
                testTag = "sheet_initial_balance"
            )
        }
    }
}

@Composable
private fun ActionSheetRow(
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    iconBg: androidx.compose.ui.graphics.Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        color = MonochromeSurfaceSecondary.copy(alpha = 0.65f),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(0.8.dp, MonochromeOutline, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MonochromeTextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MonochromeTextSecondary
                )
            }
        }
    }
}
