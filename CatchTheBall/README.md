# 🎮 Catch the Ball — Java OOP Game

A simple yet polished Java Swing game where the player moves a basket
left and right to catch falling balls and earn points.

---

## 📁 File Structure

| File               | Role                                                         |
|--------------------|--------------------------------------------------------------|
| `Main.java`        | Entry point — launches the game window                       |
| `GameFrame.java`   | Main JFrame; starts the game loop thread                     |
| `GamePanel.java`   | Core game panel: loop, rendering, input, all screens         |
| `Entity.java`      | Abstract base class for game objects (Ball, Basket)          |
| `Ball.java`        | Falling ball entity — extends Entity                         |
| `Basket.java`      | Player-controlled basket — extends Entity                    |
| `Particle.java`    | Visual particle effects on catch/explosion                   |
| `BallType.java`    | Enum: NORMAL, STAR, BOMB, ROTTEN — each with weight & color  |
| `GameScreen.java`  | Enum: MENU, PLAYING, PAUSED, GAME_OVER, LEADERBOARD          |
| `ScoreEntry.java`  | Immutable leaderboard entry (Comparable)                     |
| `ScoreManager.java`| Saves/loads top-10 scores to scores.txt                      |
| `SoundManager.java`| Synthesised sound effects (no external audio files needed)   |

---

## 🏗️ OOP Concepts Demonstrated

| Concept           | Where                                                   |
|-------------------|---------------------------------------------------------|
| **Abstraction**   | `Entity` abstract class with `update()` / `draw()`      |
| **Inheritance**   | `Ball extends Entity`, `Basket extends Entity`          |
| **Polymorphism**  | `Entity` list; each subclass draws differently          |
| **Encapsulation** | Private fields + getters in every class                 |
| **Enum**          | `BallType`, `GameScreen` with methods/fields            |
| **Interface**     | `Runnable` (game loop), `Comparable` (ScoreEntry)       |
| **Composition**   | `GamePanel` contains `Basket`, `List<Ball>`, etc.       |

---

## 🎮 Controls

| Input               | Action               |
|---------------------|----------------------|
| Mouse move          | Move basket          |
| ← → / A D keys     | Move basket          |
| **P**               | Pause / Resume       |
| **ESC**             | Quit to main menu    |
| **M**               | Mute / Unmute sound  |
| Type + Enter (Game Over) | Submit name to leaderboard |

---

## 🏆 Ball Types

| Ball   | Points | Rarity  | Note                  |
|--------|--------|---------|-----------------------|
| ● Green | +10   | Common  | Catch for points      |
| ★ Star  | +50   | Rare    | Bonus points + coins  |
| ✦ Bomb  | -30   | Uncommon| Avoid! Screen shake   |
| ✕ Rotten| -20  | Uncommon| Avoid!                |

---

## 🏠 Level System

- Each level requires `level × 100 + (level - 1) × 50` points to clear.
- On level-up the timer resets to 3 minutes.
- Ball fall speed increases with every level.
- Sky background changes: **day → dusk → night → deep night**.

---

## 🔧 How to Compile & Run

```bash
# 1. Compile (requires JDK 11+)
javac *.java

# 2. Run
java Main
```

Or on Windows double-click `run.bat`.
On Linux/macOS run `./run.sh`.

Scores are saved to `scores.txt` in the same folder.
