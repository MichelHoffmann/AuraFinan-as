package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = Color(0xFF1E0A3C),
    primaryContainer = PurpleTertiary,
    onPrimaryContainer = Color(0xFFF3E8FF),
    secondary = PurpleSecondary,
    onSecondary = Color(0xFF2E1065),
    secondaryContainer = Color(0xFF3B1D6B),
    onSecondaryContainer = Color(0xFFE9D5FF),
    tertiary = IncomeGreen,
    onTertiary = Color.Black,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceDarkElevated,
    onSurfaceVariant = TextSecondary,
    outline = SurfaceBorder,
    error = ExpenseRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    // The user explicitly specified: "O app deverá ser em dark mode com a cor roxa como cor principal de destaque."
    // We enforce the customized Dark Purple theme everywhere.
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
