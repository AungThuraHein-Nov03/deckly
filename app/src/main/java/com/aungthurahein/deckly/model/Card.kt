package com.aungthurahein.deckly.model

/**
 * @param rank 0 = Joker, 1 = Ace, 2-10 = number, 11 = Jack, 12 = Queen, 13 = King
 */
data class Card(
    val rank: Int,
    val suit: Suit,
    val isRevealed: Boolean = false
) {
    val isJoker: Boolean get() = rank == 0
    val isFaceCard: Boolean get() = rank in 11..13
    val isAce: Boolean get() = rank == 1

    val rankLabel: String
        get() = when (rank) {
            0 -> "★"
            1 -> "A"
            in 2..10 -> rank.toString()
            11 -> "J"
            12 -> "Q"
            13 -> "K"
            else -> ""
        }
}
