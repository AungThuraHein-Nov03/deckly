package com.aungthurahein.deckly.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val RacingRed = Color(0xFFFF3B30)
val PureBlack = Color(0xFF000000)
val CardWhite = Color(0xFFFFFFFF)

private val DecklyColorScheme = lightColorScheme(
    primary = RacingRed,
    onPrimary = CardWhite,
    background = CardWhite,
    onBackground = PureBlack,
    surface = CardWhite,
    onSurface = PureBlack,
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF666666)
)

@Composable
fun DecklyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DecklyColorScheme,
        content = content
    )
}
