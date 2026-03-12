package com.aungthurahein.deckly.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val RacingRed = Color(0xFFFF3B30)
val PureBlack = Color(0xFF000000)
val CardWhite = Color(0xFFFFFFFF)

private val DecklyLightColorScheme = lightColorScheme(
    primary = RacingRed,
    onPrimary = CardWhite,
    background = CardWhite,
    onBackground = PureBlack,
    surface = CardWhite,
    onSurface = PureBlack,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF666666)
)

private val DecklyDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFF6B62),
    onPrimary = Color(0xFF2A0805),
    background = Color(0xFF121212),
    onBackground = Color(0xFFEAEAEA),
    surface = Color(0xFF1A1A1A),
    onSurface = Color(0xFFEAEAEA),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFB5B5B5)
)

@Composable
fun DecklyTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DecklyDarkColorScheme else DecklyLightColorScheme,
        content = content
    )
}
