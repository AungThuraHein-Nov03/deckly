package com.aungthurahein.deckly.model

enum class Suit(val symbol: String, val initial: Char, val isRed: Boolean) {
    SPADES("♠", 'S', false),
    DIAMONDS("♦", 'D', true),
    CLUBS("♣", 'C', false),
    HEARTS("♥", 'H', true)
}
