package com.aungthurahein.deckly.data

import android.content.Context
import java.time.LocalDate

/**
 * Stores end-of-day snapshots in SharedPreferences.
 *
 * Journal format (one line per day):
 * yyyy-MM-dd;<luckCardSerialized>;<questCardSerialized|questCardSerialized|questCardSerialized>
 */
object DailyJournalStore {

    private const val PREFS_NAME = "deckly_state"
    private const val KEY_DRAWN_DATE = "drawnDate"
    private const val KEY_LUCK_CARD = "luckCard"
    private const val KEY_QUEST_CARDS = "questCards"
    private const val KEY_JOURNAL_ENTRIES = "journalEntries"
    private const val KEY_LAST_JOURNALED_DATE = "lastJournaledDate"

    data class JournalEntry(
        val date: LocalDate,
        val luckCardSerialized: String,
        val questCardsSerialized: List<String>
    )

    fun archiveIfDayEnded(context: Context, today: LocalDate = LocalDate.now()): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val drawnDateString = prefs.getString(KEY_DRAWN_DATE, null) ?: return false
        val drawnDate = runCatching { LocalDate.parse(drawnDateString) }.getOrNull() ?: return false

        // Only archive after the day has rolled over.
        if (!drawnDate.isBefore(today)) return false

        val lastJournaledDate = prefs.getString(KEY_LAST_JOURNALED_DATE, null)
        if (lastJournaledDate == drawnDateString) return false

        val luckCard = prefs.getString(KEY_LUCK_CARD, null) ?: return false
        val questCards = prefs.getString(KEY_QUEST_CARDS, null) ?: return false

        val entryLine = "$drawnDateString;$luckCard;$questCards"
        val existing = prefs.getString(KEY_JOURNAL_ENTRIES, "").orEmpty()
        val updated = if (existing.isBlank()) entryLine else "$existing\n$entryLine"

        prefs.edit()
            .putString(KEY_JOURNAL_ENTRIES, updated)
            .putString(KEY_LAST_JOURNALED_DATE, drawnDateString)
            .apply()

        return true
    }

    fun loadEntries(context: Context): List<JournalEntry> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_JOURNAL_ENTRIES, "").orEmpty().trim()
        if (raw.isBlank()) return emptyList()

        return raw
            .lineSequence()
            .mapNotNull { line ->
                val parts = line.split(";")
                if (parts.size != 3) return@mapNotNull null

                val date = runCatching { LocalDate.parse(parts[0]) }.getOrNull() ?: return@mapNotNull null
                val luckCard = parts[1]
                val questCards = parts[2].split("|").filter { it.isNotBlank() }

                JournalEntry(
                    date = date,
                    luckCardSerialized = luckCard,
                    questCardsSerialized = questCards
                )
            }
            .toList()
            .sortedByDescending { it.date }
    }
}
