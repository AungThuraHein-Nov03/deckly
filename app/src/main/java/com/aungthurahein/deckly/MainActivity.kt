package com.aungthurahein.deckly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aungthurahein.deckly.ui.screens.DecklyScreen
import com.aungthurahein.deckly.ui.theme.DecklyTheme
import com.aungthurahein.deckly.viewmodel.MainViewModel
import com.aungthurahein.deckly.viewmodel.ThemeMode
import com.aungthurahein.deckly.worker.DailyJournalScheduler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        DailyJournalScheduler.schedule(applicationContext)
        setContent {
            val viewModel: MainViewModel = viewModel()
            val state by viewModel.state.collectAsStateWithLifecycle()
            val systemDarkMode = isSystemInDarkTheme()

            val useDarkTheme = when (state.themeMode) {
                ThemeMode.DARK -> true
                ThemeMode.LIGHT -> false
                ThemeMode.SYSTEM -> systemDarkMode
            }

            DecklyTheme(darkTheme = useDarkTheme) {
                DecklyScreen(viewModel = viewModel)
            }
        }
    }
}