package com.aungthurahein.deckly.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aungthurahein.deckly.ui.components.PlayingCard
import com.aungthurahein.deckly.ui.theme.DecklyTheme
import com.aungthurahein.deckly.viewmodel.AppPhase
import com.aungthurahein.deckly.viewmodel.MainViewModel

@Composable
fun DecklyScreen(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Automatically redraw when the app is opened on a new day
    LaunchedEffect(Unit) {
        viewModel.refreshIfNewDay()
    }

    DecklyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp, vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // ── Title ──────────────────────────────────────────────
                Text(
                    text = "DECKLY",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Thin,
                    letterSpacing = 12.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

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
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
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
            }
        }
    }
}
