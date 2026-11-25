package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
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
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.local.TransactionEntity
import com.example.data.local.TransactionType
import com.example.domain.model.CurrencyFormatter
import com.example.domain.model.CurrencyStyle
import com.example.domain.model.DateFormatter
import com.example.domain.model.FinancialSummary
import com.example.domain.model.TransactionCategories
import com.example.ui.components.ThousandSeparatorVisualTransformation
import com.example.ui.components.sanitizeNumericInput
import com.example.ui.theme.ExpenseRed
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
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun IncomeAndBalanceScreen(
    summary: FinancialSummary,
    currencyStyle: CurrencyStyle,
    editingTransaction: TransactionEntity? = null,
    onSaveIncome: (title: String, amount: Long, category: String, note: String, timestamp: Long) -> Unit,
    onSaveInitialBalance: (Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val isEditing = editingTransaction != null
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Pemasukan, 1 = Saldo Awal

    // Income form states
    var rawAmount by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(TransactionCategories.incomeCategories.first().name) }
    var note by remember { mutableStateOf("") }
    var selectedTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var amountError by remember { mutableStateOf<String?>(null) }
    var titleError by remember { mutableStateOf<String?>(null) }

    // Initial balance form state
    var rawInitialBalance by remember {
        mutableStateOf(if (summary.initialBalance > 0L) summary.initialBalance.toString() else "")
    }
    var initialBalanceError by remember { mutableStateOf<String?>(null) }

    // Date & time pickers
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(editingTransaction) {
        if (editingTransaction != null) {
            rawAmount = editingTransaction.amount.toString()
            title = editingTransaction.title
            selectedCategory = editingTransaction.category
            note = editingTransaction.note
            selectedTimestamp = editingTransaction.timestamp
            selectedTab = 0
        }
    }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MonochromeAccentBright,
        unfocusedBorderColor = MonochromeOutline,
        focusedContainerColor = MonochromeSurface,
        unfocusedContainerColor = MonochromeSurface,
        focusedTextColor = MonochromeTextPrimary,
        unfocusedTextColor = MonochromeTextPrimary,
        focusedLabelColor = MonochromeAccentBright,
        unfocusedLabelColor = MonochromeTextSecondary,
        cursorColor = MonochromeAccentBright
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MonochromeBackground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .navigationBarsPadding()
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 640.dp)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(48.dp)
                    .testTag("income_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Kembali",
                    tint = MonochromeTextPrimary
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isEditing) {
                    stringResource(R.string.edit_transaction_title)
                } else {
                    stringResource(R.string.income_screen_title)
                },
                style = MaterialTheme.typography.titleLarge,
                color = MonochromeTextPrimary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 640.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isEditing) {
                // iOS-style segmented control
                Surface(
                    color = MonochromeSurface,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MonochromeOutline, RoundedCornerShape(16.dp))
                ) {
                    Row(modifier = Modifier.padding(4.dp)) {
                        listOf(
                            stringResource(R.string.tab_add_income),
                            stringResource(R.string.tab_initial_balance)
                        ).forEachIndexed { index, label ->
                            val selected = selectedTab == index
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (selected) MonochromeSurfaceSecondary else MonochromeSurface
                                    )
                                    .clickable { selectedTab = index }
                                    .testTag("income_tab_$index"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (selected) MonochromeTextPrimary else MonochromeTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            AnimatedContent(
                targetState = selectedTab,
                label = "income_tab_content"
            ) { tabIndex ->
                if (tabIndex == 0) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        // Amount Input
                        OutlinedTextField(
                            value = rawAmount,
                            onValueChange = {
                                rawAmount = sanitizeNumericInput(it)
                                amountError = null
                            },
                            label = { Text(stringResource(R.string.amount_label)) },
                            prefix = {
                                Text(
                                    "Rp ",
                                    color = IncomeGreen,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            placeholder = { Text(stringResource(R.string.amount_placeholder)) },
                            singleLine = true,
                            visualTransformation = ThousandSeparatorVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = amountError != null,
                            supportingText = amountError?.let { err -> { Text(err, color = ExpenseRed) } },
                            colors = textFieldColors,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("income_amount_input")
                        )

                        // Quick suggestions for income name
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "Saran Cepat",
                                style = MaterialTheme.typography.labelMedium,
                                color = MonochromeTextSecondary
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TransactionCategories.quickIncomeSuggestions.forEach { suggestion ->
                                    Surface(
                                        color = MonochromeSurfaceSecondary,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                title = suggestion
                                                titleError = null
                                            }
                                    ) {
                                        Text(
                                            text = suggestion,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MonochromeAccentBright,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Custom Income Title
                        OutlinedTextField(
                            value = title,
                            onValueChange = {
                                title = it
                                titleError = null
                            },
                            label = { Text(stringResource(R.string.income_name_label)) },
                            placeholder = { Text(stringResource(R.string.income_name_placeholder)) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences
                            ),
                            isError = titleError != null,
                            supportingText = titleError?.let { err -> { Text(err, color = ExpenseRed) } },
                            colors = textFieldColors,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("income_title_input")
                        )

                        // Category selector
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = stringResource(R.string.category_label),
                                style = MaterialTheme.typography.labelMedium,
                                color = MonochromeTextSecondary
                            )
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                TransactionCategories.incomeCategories.forEach { category ->
                                    val selected = selectedCategory == category.name
                                    FilterChip(
                                        selected = selected,
                                        onClick = { selectedCategory = category.name },
                                        label = { Text(category.name) },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = category.icon,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        },
                                        colors = FilterChipDefaults.filterChipColors(
                                            containerColor = MonochromeSurface,
                                            labelColor = MonochromeTextSecondary,
                                            iconColor = MonochromeTextSecondary,
                                            selectedContainerColor = IncomeGreenContainer,
                                            selectedLabelColor = IncomeGreen,
                                            selectedLeadingIconColor = IncomeGreen
                                        ),
                                        border = FilterChipDefaults.filterChipBorder(
                                            enabled = true,
                                            selected = selected,
                                            borderColor = MonochromeOutline,
                                            selectedBorderColor = IncomeGreen.copy(alpha = 0.5f)
                                        ),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                }
                            }
                        }

                        // Date & Time Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            DateTimeSelectorCard(
                                label = stringResource(R.string.date_label),
                                value = DateFormatter.formatShortDate(selectedTimestamp),
                                icon = Icons.Rounded.CalendarMonth,
                                onClick = { showDatePicker = true },
                                modifier = Modifier.weight(1.3f)
                            )
                            DateTimeSelectorCard(
                                label = stringResource(R.string.time_label),
                                value = DateFormatter.formatTime(selectedTimestamp),
                                icon = Icons.Rounded.AccessTime,
                                onClick = { showTimePicker = true },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Optional Note
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            label = { Text(stringResource(R.string.note_label)) },
                            placeholder = { Text(stringResource(R.string.note_placeholder)) },
                            minLines = 2,
                            maxLines = 4,
                            keyboardOptions = KeyboardOptions(
                                capitalization = KeyboardCapitalization.Sentences
                            ),
                            colors = textFieldColors,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("income_note_input")
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Button(
                            onClick = {
                                val parsedAmount = rawAmount.toLongOrNull()
                                var valid = true
                                if (rawAmount.isBlank()) {
                                    amountError = "Nominal tidak boleh kosong."
                                    valid = false
                                } else if (parsedAmount == null || parsedAmount <= 0L) {
                                    amountError = "Nominal harus lebih dari 0."
                                    valid = false
                                }
                                if (title.isBlank()) {
                                    titleError = "Nama pemasukan wajib diisi."
                                    valid = false
                                }
                                if (valid && parsedAmount != null) {
                                    onSaveIncome(
                                        title.trim(),
                                        parsedAmount,
                                        selectedCategory,
                                        note.trim(),
                                        selectedTimestamp
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MonochromeAccentBright,
                                contentColor = MonochromeBackground
                            ),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("save_income_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEditing) {
                                    stringResource(R.string.save_changes_button)
                                } else {
                                    stringResource(R.string.save_income_button)
                                },
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                } else {
                    // Tab 2: Initial Balance
                    val previewInitial = rawInitialBalance.toLongOrNull() ?: 0L
                    val previewFinal = previewInitial + summary.totalIncome - summary.totalExpense

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Surface(
                            color = MonochromeSurface,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MonochromeOutline, RoundedCornerShape(20.dp))
                        ) {
                            Column(
                                modifier = Modifier.padding(18.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Pratinjau Saldo Akhir",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MonochromeTextSecondary
                                )
                                Text(
                                    text = CurrencyFormatter.format(previewFinal, currencyStyle),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (previewFinal < 0L) ExpenseRed else MonochromeTextPrimary
                                )
                                Text(
                                    text = stringResource(R.string.initial_balance_helper),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MonochromeTextSecondary
                                )
                            }
                        }

                        OutlinedTextField(
                            value = rawInitialBalance,
                            onValueChange = {
                                rawInitialBalance = sanitizeNumericInput(it)
                                initialBalanceError = null
                            },
                            label = { Text(stringResource(R.string.initial_balance_input_label)) },
                            prefix = { Text("Rp ", color = MonochromeAccentBright) },
                            placeholder = { Text("0") },
                            singleLine = true,
                            visualTransformation = ThousandSeparatorVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            isError = initialBalanceError != null,
                            supportingText = initialBalanceError?.let { err -> { Text(err, color = ExpenseRed) } },
                            colors = textFieldColors,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("initial_balance_tab_input")
                        )

                        Button(
                            onClick = {
                                val value = if (rawInitialBalance.isBlank()) 0L else rawInitialBalance.toLongOrNull()
                                if (value == null || value < 0L) {
                                    initialBalanceError = "Nominal saldo awal tidak valid."
                                } else {
                                    onSaveInitialBalance(value)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MonochromeAccentBright,
                                contentColor = MonochromeBackground
                            ),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("save_initial_balance_button")
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.save_initial_balance_button),
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedTimestamp)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { pickedDate ->
                            selectedTimestamp = mergeDateKeepingTime(pickedDate, selectedTimestamp)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Pilih", color = MonochromeAccentBright)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.action_cancel), color = MonochromeTextSecondary)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val cal = remember(selectedTimestamp) {
            Calendar.getInstance().apply { timeInMillis = selectedTimestamp }
        }
        val timePickerState = rememberTimePickerState(
            initialHour = cal.get(Calendar.HOUR_OF_DAY),
            initialMinute = cal.get(Calendar.MINUTE),
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            containerColor = MonochromeSurface,
            title = { Text(stringResource(R.string.time_label), color = MonochromeTextPrimary) },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updatedCal = Calendar.getInstance().apply {
                            timeInMillis = selectedTimestamp
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                        }
                        selectedTimestamp = updatedCal.timeInMillis
                        showTimePicker = false
                    }
                ) {
                    Text("Pilih", color = MonochromeAccentBright)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text(stringResource(R.string.action_cancel), color = MonochromeTextSecondary)
                }
            }
        )
    }
}

@Composable
fun DateTimeSelectorCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MonochromeSurface,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, MonochromeOutline, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MonochromeAccent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MonochromeTextSecondary
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    color = MonochromeTextPrimary
                )
            }
        }
    }
}

fun mergeDateKeepingTime(pickedUtcDateMillis: Long, currentTimestampMillis: Long): Long {
    val pickedCal = Calendar.getInstance(java.util.TimeZone.getTimeZone("UTC")).apply {
        timeInMillis = pickedUtcDateMillis
    }
    val currentCal = Calendar.getInstance().apply {
        timeInMillis = currentTimestampMillis
    }
    return Calendar.getInstance().apply {
        set(Calendar.YEAR, pickedCal.get(Calendar.YEAR))
        set(Calendar.MONTH, pickedCal.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, pickedCal.get(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, currentCal.get(Calendar.HOUR_OF_DAY))
        set(Calendar.MINUTE, currentCal.get(Calendar.MINUTE))
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}
