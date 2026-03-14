package com.aungthurahein.deckly.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aungthurahein.deckly.data.DailyJournalStore
import com.aungthurahein.deckly.model.Suit

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
fun JournalPageContent(journalEntries: List<DailyJournalStore.JournalEntry>) {
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
