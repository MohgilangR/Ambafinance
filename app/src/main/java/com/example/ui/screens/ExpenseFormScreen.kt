package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.rounded.WarningAmber
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
import com.example.domain.model.TransactionCategories
import com.example.ui.components.OverdraftWarningDialog
import com.example.ui.components.ThousandSeparatorVisualTransformation
import com.example.ui.components.sanitizeNumericInput
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.ExpenseRedContainer
import com.example.ui.theme.MonochromeAccentBright
import com.example.ui.theme.MonochromeBackground
import com.example.ui.theme.MonochromeOutline
import com.example.ui.theme.MonochromeSurface
import com.example.ui.theme.MonochromeSurfaceSecondary
import com.example.ui.theme.MonochromeTextPrimary
import com.example.ui.theme.MonochromeTextSecondary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ExpenseFormScreen(
    availableBalance: Long,
    currencyStyle: CurrencyStyle,
    editingTransaction: TransactionEntity? = null,
    onSaveExpense: (title: String, amount: Long, category: String, note: String, timestamp: Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val isEditing = editingTransaction != null

    val effectiveAvailableBalance = remember(availableBalance, editingTransaction) {
        when {
            editingTransaction == null -> availableBalance
            editingTransaction.type == TransactionType.EXPENSE -> availableBalance + editingTransaction.amount
            else -> availableBalance - editingTransaction.amount
        }
    }

    var rawAmount by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(TransactionCategories.expenseCategories.first().name) }
    var note by remember { mutableStateOf("") }
    var selectedTimestamp by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var amountError by remember { mutableStateOf<String?>(null) }
    var titleError by remember { mutableStateOf<String?>(null) }
    var showOverdraftDialog by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(editingTransaction) {
        if (editingTransaction != null) {
            rawAmount = editingTransaction.amount.toString()
            title = editingTransaction.title
            selectedCategory = editingTransaction.category
            note = editingTransaction.note
            selectedTimestamp = editingTransaction.timestamp
        }
    }

    val typedAmount = rawAmount.toLongOrNull() ?: 0L
    val isOverdraft = typedAmount > 0L && typedAmount > effectiveAvailableBalance

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
        // Header
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
                    .testTag("expense_back_button")
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
                    stringResource(R.string.expense_screen_title)
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
            // Available Balance Context Strip
            Surface(
                color = MonochromeSurface,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, MonochromeOutline, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Saldo Tersedia",
                        style = MaterialTheme.typography.bodySmall,
                        color = MonochromeTextSecondary
                    )
                    Text(
                        text = CurrencyFormatter.format(effectiveAvailableBalance, currencyStyle),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (effectiveAvailableBalance < 0L) ExpenseRed else MonochromeTextPrimary
                    )
                }
            }

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
                        color = ExpenseRed,
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
                    .testTag("expense_amount_input")
            )

            // Overdraft Inline Warning
            AnimatedVisibility(visible = isOverdraft) {
                Surface(
                    color = WarningAmberContainer,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.8.dp, WarningAmber.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .testTag("overdraft_inline_warning")
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.WarningAmber,
                            contentDescription = null,
                            tint = WarningAmber,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = stringResource(R.string.overdraft_warning_inline),
                            style = MaterialTheme.typography.bodySmall,
                            color = WarningAmber
                        )
                    }
                }
            }

            // Quick suggestions (never restricting free-form custom names!)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Contoh Cepat (atau ketik bebas di bawah)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MonochromeTextSecondary
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TransactionCategories.quickExpenseSuggestions.forEach { suggestion ->
                        Surface(
                            color = MonochromeSurfaceSecondary,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    title = suggestion
                                    titleError = null
                                    selectedCategory = when (suggestion) {
                                        "Makan" -> "Makan"
                                        "Beli minuman" -> "Minuman"
                                        "Transportasi" -> "Transportasi"
                                        "Beli barang" -> "Belanja"
                                        "Jajan" -> "Jajan"
                                        else -> selectedCategory
                                    }
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

            // Custom Expense Name Input
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    titleError = null
                },
                label = { Text(stringResource(R.string.expense_name_label)) },
                placeholder = { Text(stringResource(R.string.expense_name_placeholder)) },
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
                    .testTag("expense_title_input")
            )

            // Expense Category Chips
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
                    TransactionCategories.expenseCategories.forEach { category ->
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
                                selectedContainerColor = ExpenseRedContainer,
                                selectedLabelColor = ExpenseRed,
                                selectedLeadingIconColor = ExpenseRed
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = selected,
                                borderColor = MonochromeOutline,
                                selectedBorderColor = ExpenseRed.copy(alpha = 0.5f)
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
                    .testTag("expense_note_input")
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
                        titleError = "Nama pengeluaran wajib diisi."
                        valid = false
                    }
                    if (valid && parsedAmount != null) {
                        if (parsedAmount > effectiveAvailableBalance) {
                            showOverdraftDialog = true
                        } else {
                            onSaveExpense(
                                title.trim(),
                                parsedAmount,
                                selectedCategory,
                                note.trim(),
                                selectedTimestamp
                            )
                        }
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
                    .testTag("save_expense_button")
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
                        stringResource(R.string.save_expense_button)
                    },
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showOverdraftDialog) {
        OverdraftWarningDialog(
            expenseAmount = typedAmount,
            availableBalance = effectiveAvailableBalance,
            currencyStyle = currencyStyle,
            onConfirmSave = {
                showOverdraftDialog = false
                onSaveExpense(
                    title.trim(),
                    typedAmount,
                    selectedCategory,
                    note.trim(),
                    selectedTimestamp
                )
            },
            onDismiss = { showOverdraftDialog = false }
        )
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
