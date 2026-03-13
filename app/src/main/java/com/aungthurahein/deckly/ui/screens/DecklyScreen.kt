package com.aungthurahein.deckly.ui.screens

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aungthurahein.deckly.data.DailyJournalStore
import com.aungthurahein.deckly.model.Suit
import com.aungthurahein.deckly.ui.components.PlayingCard
import com.aungthurahein.deckly.viewmodel.AppPhase
import com.aungthurahein.deckly.viewmodel.MainViewModel
import com.aungthurahein.deckly.viewmodel.ThemeMode

private enum class DecklyPage {
    MAIN,
    JOURNAL
}

private fun rankLabel(rank: Int): String = when (rank) {
    0 -> "★"
    1 -> "A"
    in 2..10 -> rank.toString()
    11 -> "J"
    12 -> "Q"
    13 -> "K"
    else -> "?"
}

private fun formatSerializedCard(serialized: String): String {
    val parts = serialized.split(",")
    if (parts.size < 2) return serialized

    val rank = parts[0].toIntOrNull() ?: return serialized
    val suit = runCatching { Suit.valueOf(parts[1]) }.getOrNull() ?: return rankLabel(rank)
    return "${rankLabel(rank)}${suit.symbol}"
}

@Composable
fun DecklyScreen(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isSystemDark = isSystemInDarkTheme()
    val context = LocalContext.current
    var currentPage by remember { mutableStateOf(DecklyPage.MAIN) }
    val journalEntries by remember(state.drawnDate, state.phase, state.luckCard, state.questCards) {
        mutableStateOf(DailyJournalStore.loadEntries(context))
    }

    // Automatically redraw when the app is opened on a new day
    LaunchedEffect(Unit) {
        viewModel.refreshIfNewDay()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Header ──────────────────────────────────────────────
            Box(modifier = Modifier.fillMaxWidth()) {
                if (currentPage == DecklyPage.MAIN) {
                    FilledTonalIconButton(
                        onClick = { currentPage = DecklyPage.JOURNAL },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Text(text = "📔")
                    }
                } else {
                    FilledTonalIconButton(
                        onClick = { currentPage = DecklyPage.MAIN },
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Text(text = "←")
                    }
                }

                Text(
                    text = if (currentPage == DecklyPage.MAIN) "DECKLY" else "JOURNAL",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Thin,
                    letterSpacing = 12.sp,
                    modifier = Modifier.align(Alignment.Center)
                )

                val toggleContentDescription = when (state.themeMode) {
                    ThemeMode.DARK -> "Switch to light mode"
                    ThemeMode.LIGHT -> "Switch to dark mode"
                    ThemeMode.SYSTEM -> if (isSystemDark) "Switch to light mode" else "Switch to dark mode"
                }

                FilledTonalIconButton(
                    onClick = { viewModel.toggleThemeMode(isSystemDark) },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .semantics { contentDescription = toggleContentDescription }
                ) {
                    Text(
                        text = if (state.themeMode == ThemeMode.DARK || (state.themeMode == ThemeMode.SYSTEM && isSystemDark)) "☀️" else "🌙",
                        fontSize = 18.sp
                    )
                }
            }

            if (currentPage == DecklyPage.MAIN) {
                Spacer(modifier = Modifier.weight(1f))

                // ── Luck Card Section ──────────────────────────────────
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (state.phase == AppPhase.REVIEW) "YOUR LUCK — REVEALED" else "YOUR LUCK",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 4.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    state.luckCard?.let { card ->
                        PlayingCard(
                            card = card,
                            modifier = Modifier.size(width = 180.dp, height = 252.dp),
                            onLongPress = if (state.phase == AppPhase.MORNING_DRAW) {
                                { viewModel.revealLuckCard() }
                            } else null
                        )
                    }

                    if (state.phase == AppPhase.MORNING_DRAW) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Long press to reveal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                // ── Quest Cards Section ────────────────────────────────
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ACTIVE QUESTS",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 4.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Tap a card to reveal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.horizontalScroll(rememberScrollState())
                    ) {
                        state.questCards.forEachIndexed { index, card ->
                            PlayingCard(
                                card = card,
                                modifier = Modifier.size(width = 120.dp, height = 168.dp),
                                onClick = if (!card.isRevealed) {
                                    { viewModel.revealQuestCard(index) }
                                } else null
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))
            } else {
                Spacer(modifier = Modifier.height(20.dp))
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (journalEntries.isEmpty()) {
                        Text(
                            text = "No journal entries yet.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        journalEntries.forEach { entry ->
                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = entry.date.toString(),
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Luck: ${formatSerializedCard(entry.luckCardSerialized)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Tasks: ${entry.questCardsSerialized.joinToString { formatSerializedCard(it) }}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
