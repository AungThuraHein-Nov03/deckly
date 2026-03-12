# CHANGELOG — Deckly

## [0.0.4] — 2026-03-12

### Added
- **Light/Dark mode toggle** — Added a top-right theme toggle on the main screen to switch between light and dark mode instantly.
- **Theme preference persistence** — Theme mode is stored in `SharedPreferences` and restored across app restarts.
- **Daily journal snapshots** — At day rollover, Deckly now archives a daily journal entry containing the date, luck card, and quest cards.
- **Midnight journal scheduler** — Added a background WorkManager job to trigger end-of-day journal archiving around 12am.
- **Journal page button** — Added `Journal` and `Back` buttons to move between the main page and a simple journal list view.

### Changed
- **Material theme architecture** — `DecklyTheme` now supports both light and dark color schemes.
- **Contrast tuning for readability** — Updated dark-mode surface, background, and muted text colors to keep card UI and helper text readable.
- **New-day fallback archiving** — When opening the app after rollover, Deckly archives the previous day before drawing fresh cards.

## -- To fix --
- **Test in progress**

## [0.0.3] — 2026-03-11

### Fixed
- **Cards persist across app restarts** — Deck state (luck card, quest cards, phase, drawn date) is now saved to SharedPreferences. Clearing the app from recents no longer resets the cards; the same hand is restored until a new day begins.

### Changed
- **`MainViewModel`** — Converted from `ViewModel` to `AndroidViewModel` for `SharedPreferences` access. On init, restores saved state if available instead of always drawing fresh cards.

---

## [0.0.2] — 2026-03-10

### Changed
- **Background → white** — Switched `DecklyTheme` from `darkColorScheme` (black) to `lightColorScheme` (white background, black text) for a cleaner, less ominous look.
- **Quest cards start hidden** — All 3 Active Quest cards now draw face-down; each reveals individually on tap via `revealQuestCard(index)`.
- **`PlayingCard` composable** — Added `onClick` parameter alongside `onLongPress`; both wired through `detectTapGestures`.
- **Number card centre** — Removed large suit symbols from the centre of number/ace cards; centre now shows rank label only.

### Added
- **`MainViewModel.revealQuestCard(index)`** — Reveals a single quest card by index.
- **`MainViewModel.refreshIfNewDay()`** — Compares `DeckState.drawnDate` (`LocalDate`) against today; redraws automatically when a new day begins.
- **`DeckState.drawnDate`** — Tracks the `LocalDate` when cards were drawn.
- **`LaunchedEffect` in `DecklyScreen`** — Calls `refreshIfNewDay()` on composition so opening the app on a new day always gives fresh cards.
- "Tap a card to reveal" hint text below the ACTIVE QUESTS header.

### Removed
- **NEW DRAW button** — No longer needed; cards refresh automatically on a new day.
- Unused imports (`Button`, `ButtonDefaults`, `RoundedCornerShape`, `fillMaxWidth`, `RacingRed`) from `DecklyScreen`.

---

## [0.0.1] — 2026-03-10

### Added

#### Architecture & Data Model
- **`Suit` enum** (`model/Suit.kt`) — `SPADES`, `DIAMONDS`, `CLUBS`, `HEARTS` with `symbol`, `initial`, and `isRed` properties.
- **`Card` data class** (`model/Card.kt`) — `rank` (0-13), `suit`, `isRevealed`. Computed properties: `isJoker`, `isFaceCard`, `isAce`, `rankLabel`. 4 Jokers represented as `rank = 0`.
- **`MainViewModel`** (`viewmodel/MainViewModel.kt`) — MVVM ViewModel managing the full 56-card deck (52 standard + 4 Jokers). Exposes a `StateFlow<DeckState>` with:
  - `luckCard` — 1 hidden "Luck" card (face-down by default).
  - `questCards` — 3 revealed "Active Quest" cards.
  - `phase` — `MORNING_DRAW` or `REVIEW`.
  - `drawCards()` — Shuffles deck and redraws.
  - `revealLuckCard()` — Flips the luck card and transitions to `REVIEW` phase.

#### Composables & UI
- **`PlayingCard`** (`ui/components/PlayingCard.kt`) — Modular card composable with:
  - **Flip animation** — 3D `rotationY` tween (600 ms) between face-down and face-up.
  - **Card back** — Racing Red surface with diamond-lattice `Canvas` pattern and "D" watermark.
  - **Number cards (rank 1-10)** — Corner labels (top-left + inverted bottom-right) with rank and suit; large centre suit symbol for Ace, rank + suit for numbers.
  - **Face cards (J / Q / K)** — Tries to load a pre-baked drawable (`ic_face_<rank><suit>`); falls back to styled text if the drawable is absent.
  - **Joker** — Star + "JOKER" label in the suit's colour.
  - **`SuitSymbol` helper** — Loads suit drawable (`ic_suit_<s/d/c/h>`) with `ColorFilter.tint()` for red/black colouring; falls back to Unicode glyph.
  - **`onLongPress`** callback wired via `pointerInput` / `detectTapGestures`.
  - `@Preview` composables for Ace, King, 7, card back, and Joker.
- **`DecklyTheme`** (`ui/theme/Theme.kt`) — Dark `MaterialTheme` with Pure Black (`#000000`) background and Racing Red (`#FF3B30`) primary accent.
- **`DecklyScreen`** (`ui/screens/DecklyScreen.kt`) — Main screen layout:
  - "DECKLY" title with wide letter-spacing.
  - "YOUR LUCK" section with the face-down luck card (long-press to reveal).
  - "ACTIVE QUESTS" section with 3 quest cards in a horizontally scrollable row.
  - "NEW DRAW" button styled in Racing Red.

#### Build Configuration
- Added `kotlin-compose` plugin (`org.jetbrains.kotlin.plugin.compose`) to version catalog and `app/build.gradle.kts`.
- Enabled `buildFeatures { compose = true }`.
- Added Compose BOM, UI, Graphics, Material 3, Foundation, Tooling, Lifecycle Runtime Compose, and ViewModel Compose dependencies.

### Changed
- **`MainActivity`** — Migrated from `AppCompatActivity` + XML layout to `ComponentActivity` + `setContent { DecklyScreen() }`.
- **`libs.versions.toml`** — Added `composeBom`, `lifecycleRuntimeCompose` versions and all Compose library entries.
- **`app/build.gradle.kts`** — Replaced `activity` dependency with `activity-compose`; added all Compose dependencies.
- **`colors.xml`** — Added `racing_red` and `pure_black` colour resources.

### Notes
- SVG assets in `deck_of_cards/` (face, suit, rank, back) have **not** been converted to Android vector drawables yet. The composable includes runtime resolution (`getIdentifier`) that gracefully falls back to text rendering until `ic_face_*` and `ic_suit_*` drawables are added to `res/drawable/`.
- The existing XML layout (`activity_main.xml`) and `card_ace_of_hearts.xml` drawable remain in the project but are no longer used by the Compose-based `MainActivity`.

---

## [0.1.0] — Initial scaffold

- Android project created with `AppCompatActivity`, ConstraintLayout, and a single `card_ace_of_hearts.xml` vector drawable.
- Raw SVG card assets placed in `deck_of_cards/` (face, suit, rank, back).
