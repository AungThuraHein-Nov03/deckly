package com.aungthurahein.deckly.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.aungthurahein.deckly.model.Card
import com.aungthurahein.deckly.model.Suit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

enum class AppPhase {
    MORNING_DRAW,
    REVIEW
}

data class DeckState(
    val luckCard: Card? = null,
    val questCards: List<Card> = emptyList(),
    val phase: AppPhase = AppPhase.MORNING_DRAW,
    val drawnDate: LocalDate? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("deckly_state", Context.MODE_PRIVATE)

    private val _state = MutableStateFlow(DeckState())
    val state: StateFlow<DeckState> = _state.asStateFlow()

    init {
        val restored = loadState()
        if (restored != null) {
            _state.value = restored
        } else {
            drawCards()
        }
    }

    /**
     * Checks whether the current draw belongs to a previous day.
     * If so, shuffles a fresh hand. Call this from the screen's
     * composition so a new day always means new cards.
     */
    fun refreshIfNewDay() {
        val today = LocalDate.now()
        if (_state.value.drawnDate != today) {
            drawCards()
        }
    }

    /**
     * Builds a full 56-card deck: 52 standard + 4 Jokers.
     * Jokers are assigned alternating suits for red/black colour variety.
     */
    private fun createDeck(): List<Card> {
        val deck = mutableListOf<Card>()

        for (suit in Suit.entries) {
            for (rank in 1..13) {
                deck.add(Card(rank = rank, suit = suit))
            }
        }

        // 4 Jokers — cycle through suits so 2 are red, 2 are black
        Suit.entries.forEachIndexed { index, suit ->
            deck.add(Card(rank = 0, suit = suit))
        }

        return deck
    }

    /**
     * Shuffles the deck and draws 4 cards:
     *   • 1 hidden "Luck" card (face-down)
     *   • 3 revealed "Active Quest" cards
     * Resets the phase to MORNING_DRAW.
     */
    fun drawCards() {
        val deck = createDeck().shuffled()
        val drawn = deck.take(4)

        _state.value = DeckState(
            luckCard = drawn[0].copy(isRevealed = false),
            questCards = drawn.drop(1).map { it.copy(isRevealed = false) },
            phase = AppPhase.MORNING_DRAW,
            drawnDate = LocalDate.now()
        )
        saveState()
    }

    /**
     * Reveals the hidden Luck card and transitions to the REVIEW phase.
     * Called on a LongPress gesture at the end of the day.
     */
    fun revealLuckCard() {
        _state.update { current ->
            current.copy(
                luckCard = current.luckCard?.copy(isRevealed = true),
                phase = AppPhase.REVIEW
            )
        }
        saveState()
    }

    /**
     * Reveals a single quest card by index when the user taps it.
     */
    fun revealQuestCard(index: Int) {
        _state.update { current ->
            current.copy(
                questCards = current.questCards.mapIndexed { i, card ->
                    if (i == index) card.copy(isRevealed = true) else card
                }
            )
        }
        saveState()
    }

    private fun serializeCard(card: Card): String =
        "${card.rank},${card.suit.name},${card.isRevealed}"

    private fun deserializeCard(s: String): Card? {
        val parts = s.split(",")
        if (parts.size != 3) return null
        val rank = parts[0].toIntOrNull() ?: return null
        val suit = try { Suit.valueOf(parts[1]) } catch (_: Exception) { return null }
        val isRevealed = parts[2].toBooleanStrictOrNull() ?: return null
        return Card(rank = rank, suit = suit, isRevealed = isRevealed)
    }

    private fun saveState() {
        val current = _state.value
        prefs.edit()
            .putString("luckCard", current.luckCard?.let { serializeCard(it) })
            .putString("questCards", current.questCards.joinToString("|") { serializeCard(it) })
            .putString("phase", current.phase.name)
            .putString("drawnDate", current.drawnDate?.toString())
            .apply()
    }

    private fun loadState(): DeckState? {
        val dateStr = prefs.getString("drawnDate", null) ?: return null
        val drawnDate = try { LocalDate.parse(dateStr) } catch (_: Exception) { return null }
        val luckCard = prefs.getString("luckCard", null)?.let { deserializeCard(it) } ?: return null
        val questCardsStr = prefs.getString("questCards", null) ?: return null
        val questCards = questCardsStr.split("|").map { deserializeCard(it) ?: return null }
        val phase = try {
            AppPhase.valueOf(prefs.getString("phase", null) ?: return null)
        } catch (_: Exception) { return null }
        return DeckState(
            luckCard = luckCard,
            questCards = questCards,
            phase = phase,
            drawnDate = drawnDate
        )
    }
}
