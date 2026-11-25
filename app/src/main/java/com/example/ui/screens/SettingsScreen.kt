package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.ViewAgenda
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.CurrencyFormatter
import com.example.domain.model.CurrencyStyle
import com.example.ui.components.ConfirmDestructiveDialog
import com.example.ui.components.InitialBalanceEditDialog
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.MonochromeAccent
import com.example.ui.theme.MonochromeAccentBright
import com.example.ui.theme.MonochromeBackground
import com.example.ui.theme.MonochromeDivider
import com.example.ui.theme.MonochromeOutline
import com.example.ui.theme.MonochromeSurface
import com.example.ui.theme.MonochromeSurfaceSecondary
import com.example.ui.theme.MonochromeTextPrimary
import com.example.ui.theme.MonochromeTextSecondary
import com.example.ui.viewmodel.SettingsUiState

@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    onUpdateInitialBalance: (Long) -> Unit,
    onUpdateCurrencyStyle: (CurrencyStyle) -> Unit,
    onSetBalanceHidden: (Boolean) -> Unit,
    onSetCompactList: (Boolean) -> Unit,
    onSetShowTimeInList: (Boolean) -> Unit,
    onClearAllData: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showInitialBalanceDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showClearAllConfirm by remember { mutableStateOf(false) }

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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineSmall,
                color = MonochromeTextPrimary
            )

            // Section 1: Keuangan
            SettingsSection(title = stringResource(R.string.settings_section_finance)) {
                SettingsActionRow(
                    icon = Icons.Outlined.AccountBalanceWallet,
                    title = stringResource(R.string.settings_initial_balance),
                    subtitle = CurrencyFormatter.format(
                        uiState.settings.initialBalance,
                        uiState.currencyStyle
                    ),
                    onClick = { showInitialBalanceDialog = true },
                    testTag = "settings_initial_balance_row"
                )
                HorizontalDivider(color = MonochromeDivider, modifier = Modifier.padding(start = 52.dp))
                SettingsActionRow(
                    icon = Icons.Outlined.AttachMoney,
                    title = stringResource(R.string.settings_currency_format),
                    subtitle = "${uiState.currencyStyle.displayName} (${uiState.currencyStyle.samplePreview})",
                    onClick = { showCurrencyDialog = true },
                    testTag = "settings_currency_row"
                )
            }

            // Section 2: Preferensi Tampilan
            SettingsSection(title = stringResource(R.string.settings_section_display)) {
                SettingsToggleRow(
                    icon = Icons.Outlined.VisibilityOff,
                    title = stringResource(R.string.settings_hide_balance_default),
                    subtitle = "Mask nominal saldo di dashboard dan riwayat",
                    checked = uiState.settings.isBalanceHidden,
                    onCheckedChange = onSetBalanceHidden,
                    testTag = "settings_toggle_hide_balance"
                )
                HorizontalDivider(color = MonochromeDivider, modifier = Modifier.padding(start = 52.dp))
                SettingsToggleRow(
                    icon = Icons.Outlined.ViewAgenda,
                    title = stringResource(R.string.settings_compact_list),
                    subtitle = "Tampilkan baris transaksi lebih ringkas",
                    checked = uiState.settings.isCompactList,
                    onCheckedChange = onSetCompactList,
                    testTag = "settings_toggle_compact"
                )
                HorizontalDivider(color = MonochromeDivider, modifier = Modifier.padding(start = 52.dp))
                SettingsToggleRow(
                    icon = Icons.Outlined.Schedule,
                    title = stringResource(R.string.settings_show_time),
                    subtitle = "Sertakan jam transaksi di daftar histori",
                    checked = uiState.settings.showTimeInList,
                    onCheckedChange = onSetShowTimeInList,
                    testTag = "settings_toggle_show_time"
                )
            }

            // Section 3: Data & Penyimpanan
            SettingsSection(title = stringResource(R.string.settings_section_data)) {
                SettingsActionRow(
                    icon = Icons.Outlined.DeleteSweep,
                    iconTint = ExpenseRed,
                    title = stringResource(R.string.settings_clear_all_data),
                    titleColor = ExpenseRed,
                    subtitle = "${uiState.totalTransactionCount} transaksi tersimpan secara lokal",
                    onClick = { showClearAllConfirm = true },
                    testTag = "settings_clear_all_row"
                )
            }

            // Section 4: Informasi Aplikasi
            SettingsSection(title = stringResource(R.string.settings_section_about)) {
                SettingsInfoRow(
                    icon = Icons.Outlined.Info,
                    title = stringResource(R.string.app_name),
                    value = "v1.0.0"
                )
                HorizontalDivider(color = MonochromeDivider, modifier = Modifier.padding(start = 52.dp))
                SettingsInfoRow(
                    icon = Icons.Outlined.Storage,
                    title = "Database Lokal",
                    value = "Room Offline"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showInitialBalanceDialog) {
        InitialBalanceEditDialog(
            currentInitialBalance = uiState.settings.initialBalance,
            onSave = { newBalance ->
                onUpdateInitialBalance(newBalance)
                showInitialBalanceDialog = false
            },
            onDismiss = { showInitialBalanceDialog = false }
        )
    }

    if (showCurrencyDialog) {
        CurrencyStyleDialog(
            selectedStyle = uiState.currencyStyle,
            onSelect = { style ->
                onUpdateCurrencyStyle(style)
                showCurrencyDialog = false
            },
            onDismiss = { showCurrencyDialog = false }
        )
    }

    if (showClearAllConfirm) {
        ConfirmDestructiveDialog(
            title = stringResource(R.string.clear_all_title),
            message = stringResource(R.string.clear_all_message),
            confirmText = stringResource(R.string.action_delete),
            onConfirm = {
                onClearAllData()
                showClearAllConfirm = false
            },
            onDismiss = { showClearAllConfirm = false }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MonochromeTextSecondary,
            modifier = Modifier.padding(start = 6.dp)
        )
        Surface(
            color = MonochromeSurface,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(0.8.dp, MonochromeOutline.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    testTag: String,
    iconTint: Color = MonochromeAccent,
    titleColor: Color = MonochromeTextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = titleColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MonochromeTextSecondary
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = MonochromeTextSecondary
        )
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MonochromeAccent,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MonochromeTextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MonochromeTextSecondary
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MonochromeBackground,
                checkedTrackColor = MonochromeAccentBright,
                uncheckedThumbColor = MonochromeAccent,
                uncheckedTrackColor = MonochromeSurfaceSecondary,
                uncheckedBorderColor = MonochromeOutline
            )
        )
    }
}

@Composable
private fun SettingsInfoRow(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MonochromeAccent,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = MonochromeTextPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MonochromeTextSecondary
        )
    }
}

@Composable
private fun CurrencyStyleDialog(
    selectedStyle: CurrencyStyle,
    onSelect: (CurrencyStyle) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MonochromeSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = stringResource(R.string.settings_currency_format),
                style = MaterialTheme.typography.titleLarge,
                color = MonochromeTextPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CurrencyStyle.entries.forEach { style ->
                    val isSelected = style == selectedStyle
                    Surface(
                        color = if (isSelected) MonochromeSurfaceSecondary else MonochromeBackground,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                0.8.dp,
                                if (isSelected) MonochromeAccentBright else MonochromeOutline,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable { onSelect(style) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = style.displayName,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MonochromeTextPrimary
                                )
                                Text(
                                    text = style.samplePreview,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MonochromeTextSecondary
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = "Dipilih",
                                    tint = MonochromeAccentBright
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Tutup", color = MonochromeAccentBright)
            }
        }
    )
}
