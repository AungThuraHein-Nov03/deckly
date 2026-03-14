package com.aungthurahein.deckly.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aungthurahein.deckly.data.DailyJournalStore
import com.aungthurahein.deckly.viewmodel.MainViewModel
import com.aungthurahein.deckly.viewmodel.ThemeMode

private enum class DecklyPage {
    MAIN,
    JOURNAL
}

private fun themeModeLabel(themeMode: ThemeMode): String = when (themeMode) {
    ThemeMode.SYSTEM -> "System"
    ThemeMode.LIGHT -> "Light"
    ThemeMode.DARK -> "Dark"
}

@Composable
fun DecklyScreen(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var currentPage by remember { mutableStateOf(DecklyPage.MAIN) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var showRevealHints by remember { mutableStateOf(true) }
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

                FilledTonalIconButton(
                    onClick = { showSettingsDialog = true },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .semantics { contentDescription = "Open settings" }
                ) {
                    Text(
                        text = "⚙️",
                        fontSize = 18.sp
                    )
                }
            }

            if (showSettingsDialog) {
                AlertDialog(
                    onDismissRequest = { showSettingsDialog = false },
                    title = { Text(text = "Settings") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { showThemeDialog = true }
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Theme",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = themeModeLabel(state.themeMode),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(text = "Change")
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "Show reveal hints",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "Show card reveal helper text on main page",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Switch(
                                    checked = showRevealHints,
                                    onCheckedChange = { showRevealHints = it }
                                )
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showSettingsDialog = false }) {
                            Text(text = "Done")
                        }
                    }
                )
            }

            if (showThemeDialog) {
                AlertDialog(
                    onDismissRequest = { showThemeDialog = false },
                    title = { Text(text = "Theme") },
                    text = {
                        Column {
                            ThemeMode.entries.forEach { mode ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = state.themeMode == mode,
                                        onClick = { viewModel.setThemeMode(mode) }
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = themeModeLabel(mode))
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showThemeDialog = false }) {
                            Text(text = "Done")
                        }
                    }
                )
            }

            if (currentPage == DecklyPage.MAIN) {
                MainPageContent(
                    state = state,
                    showRevealHints = showRevealHints,
                    onRevealLuck = { viewModel.revealLuckCard() },
                    onRevealQuest = { index -> viewModel.revealQuestCard(index) }
                )
            } else {
                JournalPageContent(journalEntries = journalEntries)
            }
        }
    }
}
