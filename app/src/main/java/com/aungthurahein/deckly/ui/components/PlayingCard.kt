package com.aungthurahein.deckly.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aungthurahein.deckly.model.Card
import com.aungthurahein.deckly.model.Suit
import com.aungthurahein.deckly.ui.theme.DecklyTheme
import com.aungthurahein.deckly.ui.theme.RacingRed
import androidx.compose.foundation.Image

// ---------------------------------------------------------------------------
// Public API
// ---------------------------------------------------------------------------

/**
 * Modular playing-card composable.
 *
 * Rendering rules:
 *  • Face-down  → [CardBack] (red diamond-pattern).
 *  • Joker      → Special joker face.
 *  • rank ≥ 11  → Pre-baked face-card image (SVG from `face/` folder),
 *                  falling back to a styled letter when the drawable is absent.
 *  • rank < 11  → Dynamically assembled card:
 *                  corners = small rank + suit (top-left & inverted bottom-right),
 *                  centre  = large suit symbol (Ace) or rank + suit (numbers).
 *
 * Suit SVGs loaded via [painterResource] are tinted red / black with
 * [ColorFilter.tint] based on [Suit.isRed].
 *
 * @param card       The [Card] to render.
 * @param modifier   Caller controls the card size (default 160 × 224 dp).
 * @param onLongPress Optional callback for the "Luck" card long-press reveal.
 * @param onClick    Optional callback for tap-to-reveal (quest cards).
 */
@Composable
fun PlayingCard(
    card: Card,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val rotation by animateFloatAsState(
        targetValue = if (card.isRevealed) 0f else 180f,
        animationSpec = tween(durationMillis = 600),
        label = "cardFlip"
    )

    val gestureModifier = if (onLongPress != null || onClick != null) {
        Modifier.pointerInput(Unit) {
            detectTapGestures(
                onLongPress = onLongPress?.let { { _ -> it() } },
                onTap = onClick?.let { { _ -> it() } }
            )
        }
    } else {
        Modifier
    }

    Surface(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
            .then(gestureModifier),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, Color(0xFFCCCCCC))
    ) {
        if (rotation > 90f) {
            // Card is face-down — counter-rotate content to prevent mirroring
            Box(modifier = Modifier.graphicsLayer { rotationY = 180f }) {
                CardBack()
            }
        } else {
            when {
                card.isJoker -> JokerFace(card)
                card.isFaceCard -> FaceCardContent(card)
                else -> NumberCardContent(card)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Card Back
// ---------------------------------------------------------------------------

@Composable
private fun CardBack() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(RacingRed)
            .padding(6.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(8.dp),
            color = RacingRed,
            border = BorderStroke(2.dp, Color.White.copy(alpha = 0.6f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Diamond lattice pattern
                Canvas(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    val spacing = 20.dp.toPx()
                    val diamondSize = 5.dp.toPx()
                    val cols = (size.width / spacing).toInt() + 1
                    val rows = (size.height / spacing).toInt() + 1

                    for (col in 0..cols) {
                        for (row in 0..rows) {
                            val cx = col * spacing + if (row % 2 == 0) 0f else spacing / 2
                            val cy = row * spacing
                            val path = Path().apply {
                                moveTo(cx, cy - diamondSize)
                                lineTo(cx + diamondSize, cy)
                                lineTo(cx, cy + diamondSize)
                                lineTo(cx - diamondSize, cy)
                                close()
                            }
                            drawPath(path, Color.White.copy(alpha = 0.15f))
                        }
                    }
                }
                // Subtle watermark
                Text(
                    text = "D",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White.copy(alpha = 0.18f),
                    fontFamily = FontFamily.Serif
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Number / Ace Card (rank 1 – 10)
// ---------------------------------------------------------------------------

@Composable
private fun NumberCardContent(card: Card) {
    val suitColor = suitColor(card.suit)

    Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        // Top-left corner
        CornerLabel(card, suitColor, Modifier.align(Alignment.TopStart))

        // Centre — rank number only
        Text(
            text = card.rankLabel,
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = suitColor,
            modifier = Modifier.align(Alignment.Center)
        )

        // Bottom-right corner (inverted)
        CornerLabel(card, suitColor, Modifier.align(Alignment.BottomEnd).rotate(180f))
    }
}

// ---------------------------------------------------------------------------
// Face Card (J, Q, K — rank 11 – 13)
// ---------------------------------------------------------------------------

@Composable
private fun FaceCardContent(card: Card) {
    val color = suitColor(card.suit)
    val facePainter = resolveFaceCardDrawable(card)

    Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
        CornerLabel(card, color, Modifier.align(Alignment.TopStart))

        Box(modifier = Modifier.align(Alignment.Center)) {
            if (facePainter != null) {
                // Pre-baked face-card SVG from the face/ folder
                Image(
                    painter = facePainter,
                    contentDescription = "${card.rankLabel} of ${card.suit.name}",
                    modifier = Modifier.size(100.dp, 140.dp)
                )
            } else {
                // Fallback when SVG drawable has not been converted yet
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = card.rankLabel,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        fontFamily = FontFamily.Serif
                    )
                    SuitSymbol(card.suit, fontSize = 28.sp)
                }
            }
        }

        CornerLabel(card, color, Modifier.align(Alignment.BottomEnd).rotate(180f))
    }
}

// ---------------------------------------------------------------------------
// Joker
// ---------------------------------------------------------------------------

@Composable
private fun JokerFace(card: Card) {
    val color = suitColor(card.suit)

    Box(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "★", fontSize = 48.sp, color = color)
            Text(
                text = "JOKER",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                letterSpacing = 4.sp
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Shared Helpers
// ---------------------------------------------------------------------------

/**
 * Small rank label + suit symbol used in the top-left and (rotated) bottom-right
 * corners of every non-Joker card.
 */
@Composable
private fun CornerLabel(card: Card, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = card.rankLabel,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        SuitSymbol(card.suit, fontSize = 14.sp)
    }
}

/**
 * Renders a suit icon.
 *
 * Tries to load an Android vector drawable named `ic_suit_<x>` (where x = s/d/c/h).
 * When found it applies [ColorFilter.tint] to colour the single-colour SVG red or black.
 * Falls back to a Unicode text glyph when the drawable is not present.
 */
@Composable
private fun SuitSymbol(
    suit: Suit,
    fontSize: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier
) {
    val color = suitColor(suit)
    val context = LocalContext.current
    val resName = "ic_suit_${suit.initial.lowercaseChar()}"
    val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)

    if (resId != 0) {
        Image(
            painter = painterResource(resId),
            contentDescription = suit.name,
            modifier = modifier.size(fontSize.value.dp),
            colorFilter = ColorFilter.tint(color)
        )
    } else {
        Text(
            text = suit.symbol,
            fontSize = fontSize,
            color = color,
            modifier = modifier
        )
    }
}

/**
 * Tries to resolve a pre-baked face-card drawable (e.g. `ic_face_jh` for Jack of Hearts).
 * Returns `null` when the drawable has not been added to `res/drawable/` yet.
 */
@Composable
private fun resolveFaceCardDrawable(card: Card): androidx.compose.ui.graphics.painter.Painter? {
    val rankChar = when (card.rank) {
        11 -> 'j'; 12 -> 'q'; 13 -> 'k'; else -> return null
    }
    val suitChar = card.suit.initial.lowercaseChar()
    val resName = "ic_face_${rankChar}${suitChar}"

    val context = LocalContext.current
    val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
    return if (resId != 0) painterResource(resId) else null
}

/** Maps a [Suit] to Racing Red (#FF3B30) or Black. */
private fun suitColor(suit: Suit): Color =
    if (suit.isRed) RacingRed else Color.Black

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PreviewAceOfHearts() {
    DecklyTheme {
        PlayingCard(
            card = Card(rank = 1, suit = Suit.HEARTS, isRevealed = true),
            modifier = Modifier.size(160.dp, 224.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PreviewKingOfSpades() {
    DecklyTheme {
        PlayingCard(
            card = Card(rank = 13, suit = Suit.SPADES, isRevealed = true),
            modifier = Modifier.size(160.dp, 224.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PreviewSevenOfDiamonds() {
    DecklyTheme {
        PlayingCard(
            card = Card(rank = 7, suit = Suit.DIAMONDS, isRevealed = true),
            modifier = Modifier.size(160.dp, 224.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PreviewCardBack() {
    DecklyTheme {
        PlayingCard(
            card = Card(rank = 5, suit = Suit.CLUBS, isRevealed = false),
            modifier = Modifier.size(160.dp, 224.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun PreviewJoker() {
    DecklyTheme {
        PlayingCard(
            card = Card(rank = 0, suit = Suit.HEARTS, isRevealed = true),
            modifier = Modifier.size(160.dp, 224.dp)
        )
    }
}
