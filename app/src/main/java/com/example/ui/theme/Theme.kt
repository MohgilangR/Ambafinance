package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val MonochromeDarkColorScheme = darkColorScheme(
    primary = MonochromeAccentBright,
    onPrimary = MonochromeBackground,
    primaryContainer = MonochromeSurfaceSecondary,
    onPrimaryContainer = MonochromeTextPrimary,
    secondary = MonochromeAccent,
    onSecondary = MonochromeBackground,
    secondaryContainer = MonochromeSurfaceSecondary,
    onSecondaryContainer = MonochromeTextPrimary,
    tertiary = IncomeGreen,
    onTertiary = MonochromeBackground,
    background = MonochromeBackground,
    onBackground = MonochromeTextPrimary,
    surface = MonochromeSurface,
    onSurface = MonochromeTextPrimary,
    surfaceVariant = MonochromeSurfaceSecondary,
    onSurfaceVariant = MonochromeTextSecondary,
    surfaceContainerLowest = Color(0xFF101010),
    surfaceContainerLow = Color(0xFF1B1B1B),
    surfaceContainer = MonochromeSurface,
    surfaceContainerHigh = MonochromeSurfaceElevated,
    surfaceContainerHighest = MonochromeSurfaceSecondary,
    outline = MonochromeOutline,
    outlineVariant = MonochromeDivider,
    error = ExpenseRed,
    onError = MonochromeBackground,
    errorContainer = ExpenseRedContainer,
    onErrorContainer = ExpenseRed
)

val MonochromeShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun MonochromeFinanceTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MonochromeDarkColorScheme,
        typography = MonochromeTypography,
        shapes = MonochromeShapes,
        content = content
    )
}
