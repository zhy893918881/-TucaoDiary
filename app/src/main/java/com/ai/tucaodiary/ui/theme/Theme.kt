package com.ai.tucaodiary.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val DarkColorScheme = darkColorScheme(
    primary = Orange, secondary = Cyan, tertiary = Pink,
    background = DarkBg, surface = CardBg,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onBackground = TextMain, onSurface = TextMain,
    outlineVariant = CardStroke
)

val AppTypography = Typography(
    headlineLarge = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 16.sp, lineHeight = 26.sp),
    bodyMedium = TextStyle(fontSize = 14.sp, color = TextSub),
    labelLarge = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
)

@Composable
fun TucaoTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = AppTypography,
        content = content
    )
}
