# Deckly 🃏

> A daily card-drawing Android app inspired by **Sonny Hayes** from the *F1* (2025) film, where he draws a card from a deck before each race and keeps it hidden — a personal ritual for focus and luck.

Each day, draw a **Luck Card** and three **Active Quest** cards from a 56-card deck (52 standard + 4 Jokers). Cards persist throughout the day and automatically refresh at midnight.

---

## Features

- 🃏 **Daily Draw** — A fresh hand is dealt once per day; opening the app on a new day automatically redraws.
- 🎴 **Luck Card** — Drawn face-down each morning. Long-press to reveal your luck for the day.
- 🎯 **Active Quests** — Three face-down quest cards dealt alongside the luck card. Tap each one individually to reveal.
- 💾 **Persistent State** — Your current hand survives app restarts and is restored exactly as you left it.
- 🗃️ **Full Deck** — 52 standard cards plus 4 Jokers (one per suit), with accurate rank labels, suit symbols, and face-card artwork.
- ✨ **Flip Animation** — Smooth 3D card-flip animation (600 ms) when revealing any card.

---

## The Card System

Deckly's cards carry meaning only when you assign it. Before you start, define what each variable means to you.

### Suits — What are you doing?

Suits represent **domains** of your life. Each suit channels your energy into a specific area.

| Suit | Domain | Examples |
|---|---|---|
| ♠️ Spades — *The Grit* | Hard, avoided tasks | Technical deep dives, bug hunting, things you've been procrastinating on |
| ♦️ Diamonds — *The Loot* | Progress & value | High-impact features, portfolio work, finishing a project milestone |
| ♣️ Clubs — *The Foundation* | Daily grind | Cleaning, organizing files, documentation, routine chores |
| ♥️ Hearts — *The Flow* | Well-being & creativity | Brainstorming, gaming, checking in with friends and family |

### Rank — How hard is the push?

The rank sets the **weight** of the quest — map it to time, complexity, or priority.

| Rank | Name | Meaning |
|---|---|---|
| A | *The Critical Hit* | A small but vital task — a quick win with massive momentum impact |
| 2–10 | *The Sprint* | Most users map these to time (e.g. a 4 = 40-minute focused sprint, a 10 = 100-minute deep-work session) |
| J/Q/K | *Boss Fights* | Major objectives that define the success of your day |
| Joker | *The Chaos* | Total wildcard — "drop everything and take a walk" or "switch tasks for a new perspective" |

### The Luck Card — Your Daily Aura

The Luck Card is **not a task**. Keep it face-down until you're ready. It acts as a mindset modifier for the day — a quiet signal that shapes how you approach everything else.

Revealing it at the end of a productive day becomes your Victory Lap.

---

## Tech Stack

- **UI:** Jetpack Compose + Material3
- **Architecture:** MVVM (`AndroidViewModel` + `StateFlow`)
- **State Persistence:** `SharedPreferences`
- **Language:** Kotlin
- **Min SDK:** 26 (Android 8.0 Oreo)
- **Target SDK:** 36

## Project Structure

```
app/src/main/
├── java/com/aungthurahein/deckly/
│   ├── MainActivity.kt          # Entry point
│   ├── model/
│   │   ├── Card.kt              # Card data class (rank 0–13, suit, isRevealed)
│   │   └── Suit.kt              # Suit enum (SPADES, DIAMONDS, CLUBS, HEARTS)
│   ├── viewmodel/
│   │   └── MainViewModel.kt     # Deck state, draw logic, persistence
│   └── ui/
│       ├── screens/
│       │   └── DecklyScreen.kt  # Main screen composable
│       ├── components/
│       │   └── PlayingCard.kt   # Card composable with flip animation
│       └── theme/               # Material3 theme
└── res/
    ├── drawable/                # Suit SVGs, face-card images, card back assets
    └── ...
```

## Getting Started

### Prerequisites

- Android Studio Meerkat or later
- JDK 11+
- Android device or emulator running API 26+

### Build & Run

1. Clone the repository:
   ```bash
   git clone https://github.com/aungthurahein/deckly.git
   cd deckly
   ```

2. Open the project in Android Studio.

3. Sync Gradle and run on a device or emulator:
   ```bash
   ./gradlew installDebug
   ```

## How It Works

```
App open
   │
   ├─ Same day?  ──Yes──► Restore saved hand from SharedPreferences
   │
   └─ New day?   ──Yes──► Shuffle 56-card deck → deal 1 Luck + 3 Quest cards
                                                        │
                                           All cards start face-down
                                                        │
                                  Long-press Luck card to reveal
                                  Tap each Quest card to reveal
```

## License

This project is for personal use. No license is currently specified.
