package OOP_PROJECT.CatchTheBall.src.models;

import OOP_PROJECT.CatchTheBall.src.enums.Difficulty;

public class GameState {
    private int     score;
    private int     level;
    private int     combo;
    private int     highestCombo;
    private float   comboMultiplier;
    private int     timeLeft;
    private int     lives;
    private int     coinsThisGame;
    private int     ballsCaughtThisGame;
    private boolean shieldActive;
    private boolean magnetActive;
    private boolean doublePointsActive;
    private boolean slowTimeActive;
    private boolean wideBasketActive;
    private boolean frozenActive;
    private int     magnetTimer;
    private int     doubleTimer;
    private int     slowTimer;
    private int     wideTimer;
    private int     frozenTimer;
    private Difficulty difficulty;

  private static final int[] LEVEL_TARGETS = {
    100, 200, 300, 400, 500,
    600, 700, 800, 900, 1000,
    1100, 1200, 1300, 1400, 1500,
    1600, 1700, 1800, 1900, 2000
};

    public GameState(Difficulty difficulty) {
        this.difficulty = difficulty;
        reset();
    }

    public void reset() {
        score               = 0;
        level               = 1;
        combo               = 0;
        highestCombo        = 0;
        comboMultiplier     = 1f;
        timeLeft            = difficulty.getStartingTime();
        lives               = difficulty.getStartingLives();
        coinsThisGame       = 0;
        ballsCaughtThisGame = 0;
        shieldActive        = false;
        magnetActive        = false;
        doublePointsActive  = false;
        slowTimeActive      = false;
        wideBasketActive    = false;
        frozenActive        = false;
        magnetTimer         = 0;
        doubleTimer         = 0;
        slowTimer           = 0;
        wideTimer           = 0;
        frozenTimer         = 0;
    }

    public int getLevelTarget() {
        return LEVEL_TARGETS[Math.min(level - 1, LEVEL_TARGETS.length - 1)];
    }

    public void addScore(int pts)      { score += pts; }
    public void incrementCombo()       { combo++; if (combo > highestCombo) highestCombo = combo; }
    public void resetCombo()           { combo = 0; comboMultiplier = 1f; }
    public void addCoins(int c)        { coinsThisGame += c; }
    public void incrementBallsCaught() { ballsCaughtThisGame++; }
    public void decrementTime()        { if (timeLeft > 0) timeLeft--; }
    public void addTime(int s)         { timeLeft += s; }
    public void levelUp()              { level++; timeLeft += 10; }
    public void addLife()              { lives = Math.min(lives + 1, difficulty.getStartingLives() + 2); }
    public void loseLife()             { lives = Math.max(0, lives - 1); }

    public int  getLives()             { return lives; }

    public int     getScore()               { return score; }
    public int     getLevel()               { return level; }
    public int     getCombo()               { return combo; }
    public int     getHighestCombo()        { return highestCombo; }
    public float   getComboMultiplier()     { return comboMultiplier; }
    public void    setComboMultiplier(float m){ comboMultiplier = m; }
    public int     getTimeLeft()            { return timeLeft; }
    public int     getCoinsThisGame()       { return coinsThisGame; }
    public int     getBallsCaughtThisGame() { return ballsCaughtThisGame; }
    public boolean isShieldActive()         { return shieldActive; }
    public boolean isMagnetActive()         { return magnetActive; }
    public boolean isDoublePointsActive()   { return doublePointsActive; }
    public boolean isSlowTimeActive()       { return slowTimeActive; }
    public boolean isWideBasketActive()     { return wideBasketActive; }
    public boolean isFrozenActive()         { return frozenActive; }
    public int     getMagnetTimer()         { return magnetTimer; }
    public int     getDoubleTimer()         { return doubleTimer; }
    public int     getSlowTimer()           { return slowTimer; }
    public int     getWideTimer()           { return wideTimer; }
    public int     getFrozenTimer()         { return frozenTimer; }
    public Difficulty getDifficulty()       { return difficulty; }

    public void setShieldActive(boolean v)       { shieldActive = v; }
    public void setMagnetActive(boolean v)       { magnetActive = v; }
    public void setDoublePointsActive(boolean v) { doublePointsActive = v; }
    public void setSlowTimeActive(boolean v)     { slowTimeActive = v; }
    public void setWideBasketActive(boolean v)   { wideBasketActive = v; }
    public void setFrozenActive(boolean v)       { frozenActive = v; }
    public void setMagnetTimer(int t)            { magnetTimer = t; }
    public void setDoubleTimer(int t)            { doubleTimer = t; }
    public void setSlowTimer(int t)              { slowTimer = t; }
    public void setWideTimer(int t)              { wideTimer = t; }
    public void setFrozenTimer(int t)            { frozenTimer = t; }
    public void setDifficulty(Difficulty d)      { difficulty = d; }
}