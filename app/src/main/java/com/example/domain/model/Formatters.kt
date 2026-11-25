package com.example.domain.model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.abs

object CurrencyFormatter {
    private val indonesianSymbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }

    private val integerFormatter = DecimalFormat("#,##0", indonesianSymbols)

    fun format(
        amount: Long,
        style: CurrencyStyle = CurrencyStyle.IDR_STANDARD,
        withSign: String? = null
    ): String {
        val isNegative = amount < 0
        val formattedNumber = integerFormatter.format(abs(amount))
        val prefix = when {
            withSign != null -> withSign
            isNegative -> "-"
            else -> ""
        }
        return when (style) {
            CurrencyStyle.IDR_STANDARD -> "${prefix}Rp $formattedNumber"
            CurrencyStyle.IDR_COMPACT -> "${prefix}Rp$formattedNumber"
            CurrencyStyle.IDR_WITH_CODE -> "${prefix}IDR $formattedNumber"
            CurrencyStyle.IDR_DECIMAL -> "${prefix}Rp $formattedNumber,00"
        }
    }

    fun formatThousandsOnly(digitsOnly: String): String {
        val parsed = digitsOnly.toLongOrNull() ?: return ""
        return integerFormatter.format(parsed)
    }
}

object DateFormatter {
    private val idLocale = Locale("id", "ID")
    private val fullDateFormat = SimpleDateFormat("EEEE, dd MMM yyyy", idLocale)
    private val shortDateFormat = SimpleDateFormat("dd MMM yyyy", idLocale)
    private val timeFormat = SimpleDateFormat("HH:mm", idLocale)
    private val dateTimeFormat = SimpleDateFormat("dd MMM yyyy • HH:mm", idLocale)

    fun formatGroupHeader(timestampMillis: Long): String {
        val target = Calendar.getInstance().apply { timeInMillis = timestampMillis }
        val today = Calendar.getInstance()
        val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        return when {
            isSameDay(target, today) -> "Hari Ini"
            isSameDay(target, yesterday) -> "Kemarin"
            else -> fullDateFormat.format(Date(timestampMillis))
        }
    }

    fun formatShortDate(timestampMillis: Long): String = shortDateFormat.format(Date(timestampMillis))

    fun formatTime(timestampMillis: Long): String = timeFormat.format(Date(timestampMillis))

    fun formatDateTime(timestampMillis: Long): String = dateTimeFormat.format(Date(timestampMillis))

    fun startOfDay(timestampMillis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestampMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun endOfDay(timestampMillis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = timestampMillis
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }.timeInMillis
    }

    fun startOfCurrentMonth(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun daysAgoStart(days: Int): Long {
        return Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -days)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    private fun isSameDay(a: Calendar, b: Calendar): Boolean {
        return a.get(Calendar.YEAR) == b.get(Calendar.YEAR) &&
            a.get(Calendar.DAY_OF_YEAR) == b.get(Calendar.DAY_OF_YEAR)
    }
}
