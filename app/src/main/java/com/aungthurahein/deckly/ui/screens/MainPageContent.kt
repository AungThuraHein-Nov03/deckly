package com.aungthurahein.deckly.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aungthurahein.deckly.ui.components.PlayingCard
import com.aungthurahein.deckly.viewmodel.AppPhase
import com.aungthurahein.deckly.viewmodel.DeckState

@Composable
fun MainPageContent(
    state: DeckState,
    showRevealHints: Boolean,
    onRevealLuck: () -> Unit,
    onRevealQuest: (Int) -> Unit
) {
    Spacer(modifier = Modifier.weight(1f))

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
                onLongPress = if (state.phase == AppPhase.MORNING_DRAW) onRevealLuck else null
            )
        }

        if (state.phase == AppPhase.MORNING_DRAW && showRevealHints) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Long press to reveal",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
            )
        }
    }

    Spacer(modifier = Modifier.height(48.dp))

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "ACTIVE QUESTS",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            letterSpacing = 4.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (showRevealHints) {
            Text(
                text = "Tap a card to reveal",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
            )
        }

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
                        { onRevealQuest(index) }
                    } else null
                )
            }
        }
    }

    Spacer(modifier = Modifier.weight(1f))
}
