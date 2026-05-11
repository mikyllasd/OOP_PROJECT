# 🌾 Catch the Ball — Farm Edition
*A Java Swing desktop game demonstrating OOP principles*

---

## 📋 Requirements
- **JDK 17 or later** (not just JRE — you need the compiler!)
- Download: https://adoptium.net/ (recommended) or https://www.oracle.com/java/

## 🚀 How to Build & Run

### Windows
1. Open a Command Prompt in this folder
2. Run: `build.bat`
3. After build: double-click `run.bat` or run `java -jar CatchTheBall.jar`

### Linux / macOS
```bash
chmod +x build.sh
./build.sh
java -jar CatchTheBall.jar
```

### Manual (any platform)
```bash
mkdir out
javac -encoding UTF-8 -d out src/*.java
jar cfm CatchTheBall.jar MANIFEST.MF -C out .
java -jar CatchTheBall.jar
```

---

## 🎮 Game Controls
| Key | Action |
|-----|--------|
| Mouse Move | Move basket |
| ← → or A / D | Move basket |
| P | Pause / Resume |
| M | Mute / Unmute |
| ESC | Back to menu |
| Enter | Confirm name input |

---

## 🌾 Gameplay
- Catch **🍎 Apples** and **🍊 Oranges** (+10 pts)
- Catch rare **🍓 Strawberries** for +50 pts and bonus coins!
- **Avoid** 🍄 Poisonous Mushrooms (-30 pts, screen shake!)
- **Avoid** 🍆 Rotten Eggplants (-20 pts)
- Build **combos** for multipliers: 3x=2, 5x=3, 8x=4, 11x=5
- Grab **power-ups**: 🧲 Magnet, ⏰ Time+, 🛡️ Shield, 2️⃣ Double Points
- Reach the **target score** each level to advance
- Timer resets to 3 minutes on level up

---

## 🏗️ OOP Structure

### Abstraction
- `Entity` — abstract base class with `update()` and `draw(Graphics2D)` methods

### Inheritance
- `Ball extends Entity` — falling fruits and hazards
- `Basket extends Entity` — player-controlled catcher
- `Character extends Entity` — farmer sprite with animations

### Encapsulation
- `PlayerData` — private fields, public getters/setters, file persistence
- `ScoreManager` — encapsulates leaderboard loading/saving/sorting
- `AchievementManager` — manages achievement state and toast notifications

### Polymorphism
- All `Entity` subclasses override `update()` and `draw()` differently
- `ScoreEntry implements Comparable<ScoreEntry>` for natural ordering

### Enums
- `BallType` — fruit types with points, bad/rare flags
- `GameScreen` — application state machine
- `SkinType` — farmer skins with costs
- `BasketSkin` — basket skins with costs
- `PowerUp.PowerUpType` — power-up types (inner enum)

---

## 💾 Save Files
- `scores.txt` — top 10 leaderboard (auto-created)
- `player.txt` — coins, owned skins, farm stage, achievements (auto-created)

---

## 📁 Source Files
```
src/
├── Main.java              — Entry point
├── GameFrame.java         — JFrame window setup
├── GamePanel.java         — Main game loop, all screens, rendering
├── Entity.java            — Abstract base entity
├── Ball.java              — Falling fruit entity
├── Basket.java            — Player basket entity
├── Character.java         — Farmer character entity
├── Particle.java          — Visual particle effects
├── PowerUp.java           — Power-up drops
├── BallType.java          — Enum: fruit types
├── GameScreen.java        — Enum: screen states
├── SkinType.java          — Enum: farmer skins
├── BasketSkin.java        — Enum: basket skins
├── Achievement.java       — Achievement data class
├── AchievementManager.java — Achievement logic + toasts
├── ScoreEntry.java        — Comparable score record
├── ScoreManager.java      — Leaderboard file I/O
├── PlayerData.java        — Player persistence
├── SoundManager.java      — Synthesized audio (javax.sound)
└── FarmProgression.java   — Farm upgrade system
```

---

## 🎨 Features
- **9 Screens**: Main Menu, Character Creation, Game, Pause, Wardrobe, Farm Upgrade, Achievements, Game Over, Leaderboard
- **Dynamic sky**: Sunny → Golden Noon → Sunset → Night Farm (based on level)
- **Rain effect** at level 7+
- **Animated clouds** drifting across sky
- **Screen shake** on bomb catches
- **Combo flash** animations
- **Toast notifications** for achievements and events
- **Particle effects** on catches
- **60 FPS** game loop
- **Synthesized audio** — no external files needed

---

Made with ❤️ using Java Swing & Graphics2D. No external libraries required.
