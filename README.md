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
- ⚙️ **Settings Popup** — Open the top-right settings popup to configure app options. Theme mode (`System`, `Light`, `Dark`) is one of the settings and is remembered across app restarts.
- 📔 **Daily Journal Capture** — At day rollover (12am), Deckly records the date, luck card, and task cards into a daily journal snapshot, displayed as clean rank+suit values (for example, `Q♠`, `7♥`).
- 📚 **Journal Page Navigation** — Use the `Journal` button on the main page to view archived daily snapshots and `Back` to return.

---

## The Card System

Deckly's cards carry meaning only when you assign it. Before you start, define what each variable means to you. The most important part of Deckly is that the meaning is yours.

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

The Luck Card is **not a task**. Keep it face-down until you're ready. It acts as a mindset modifier for the day — a quiet signl that you reveal at the end of a day or when you've accomplished the tasks.

Here is a baseline for what your daily luck card could mean (though you can obviously indicate anything you want to fit your life):

| Symbol | Luck Theme |
|---|---|
| ♠️ Spades | The Trial |
| ♦️ Diamonds | The Momentum |
| ♣️ Clubs | The Reliability |
| ♥️ Hearts | The Flow |

**Special Modifiers:**
- **Ace** — *Critical Luck*
- **Jokers** — *Total Unpredictability*

Revealing it at the end of a productive day becomes your Victory Lap.

---

## Tech Stack

- **UI:** Jetpack Compose + Material3
- **Architecture:** MVVM (`AndroidViewModel` + `StateFlow`)
- **State Persistence:** `SharedPreferences`
- **Theme System:** Material3 theme with System, Light, and Dark modes via popup selector
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

### Run on a Physical Android Device (USB)

1. Enable Developer Options on your phone:
   - Open `Settings` > `About phone`.
   - Tap `Build number` 7 times.

2. Enable USB debugging:
   - Open `Settings` > `Developer options`.
   - Turn on `USB debugging`.
   - Turn on `USB install`.

3. Connect your phone with a USB data cable.

4. Allow debugging on the phone when prompted:
   - Tap `Allow` on the `Allow USB debugging?` dialog.
   - Optional: enable `Always allow from this computer`.

5. In Android Studio:
   - Select your physical device from the run target dropdown or check if your computer has already detected your physical device.
   - Click `Run` to install and launch Deckly.

6. Optional (terminal install):
   ```bash
   ./gradlew installDebug
   ```

7. Verify device connection (optional):
   ```bash
   adb devices
   ```
   Your phone should appear as `device` (not `unauthorized` or `offline`).

#### USB Troubleshooting

- If the device does not appear, change USB mode to `File Transfer (MTP)`.
- If status is `unauthorized`, reconnect the cable and accept the debug prompt again.
- On Windows, install/update the OEM USB driver (Samsung, Xiaomi, etc.) if needed.
- Try a different USB cable/port; some cables are charge-only and do not transfer data.

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
