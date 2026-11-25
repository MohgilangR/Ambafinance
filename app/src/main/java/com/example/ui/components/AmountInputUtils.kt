package com.example.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class ThousandSeparatorVisualTransformation : VisualTransformation {
    private val symbols = DecimalFormatSymbols(Locale("id", "ID")).apply {
        groupingSeparator = '.'
    }
    private val formatter = DecimalFormat("#,###", symbols)

    override fun filter(text: AnnotatedString): TransformedText {
        val raw = text.text
        if (raw.isEmpty()) {
            return TransformedText(AnnotatedString(""), OffsetMapping.Identity)
        }
        val parsed = raw.toLongOrNull() ?: return TransformedText(text, OffsetMapping.Identity)
        val formatted = formatter.format(parsed)

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clamped = offset.coerceIn(0, raw.length)
                if (clamped == 0) return 0
                val sub = raw.substring(0, clamped).toLongOrNull() ?: return formatted.length
                val subFormattedLength = formatter.format(sub).length
                // Map proportional trailing digits when leading zeros or full string
                val totalDots = formatted.count { it == '.' }
                val digitsAfter = raw.length - clamped
                val dotsAfter = if (digitsAfter == 0) 0 else (digitsAfter - 1) / 3
                val dotsBefore = (totalDots - dotsAfter).coerceAtLeast(0)
                return (clamped + dotsBefore).coerceIn(0, formatted.length)
            }

            override fun transformedToOriginal(offset: Int): Int {
                val clamped = offset.coerceIn(0, formatted.length)
                val digitsBefore = formatted.substring(0, clamped).count { it.isDigit() }
                return digitsBefore.coerceIn(0, raw.length)
            }
        }

        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

fun sanitizeNumericInput(input: String, maxDigits: Int = 14): String {
    val digits = input.filter { it.isDigit() }
    val trimmed = digits.trimStart('0').ifEmpty { if (digits.isNotEmpty()) "0" else "" }
    return trimmed.take(maxDigits)
}
