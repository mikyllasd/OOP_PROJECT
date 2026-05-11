package models;
import enums.Difficulty;
public class GameState {
    private int score;
    private int level;
    private int combo;
    private int highestCombo;
    private float comboMultiplier;
    private int timeLeft;
    private int coinsThisGame;
    private boolean shieldActive;
    private boolean magnetActive;
    private boolean doublePointsActive;
    private boolean slowTimeActive;
    private boolean wideBasketActive;
    private int magnetTimer;
    private int doubleTimer;
    private int slowTimer;
    private int wideTimer;
    private Difficulty difficulty;

    private static final int[] LEVEL_TARGETS = {
        200, 400, 700, 1100, 1600,
        2200, 2900, 3700, 4600, 5600
    };

    public GameState(Difficulty difficulty) {
        this.difficulty = difficulty;
        reset();
    }
    public void reset() {
        score              = 0;
        level              = 1;
        combo              = 0;
        highestCombo       = 0;
        comboMultiplier    = 1f;
        timeLeft           = difficulty.getStartingTime();
        coinsThisGame      = 0;
        shieldActive       = false;
        magnetActive       = false;
        doublePointsActive = false;
        slowTimeActive     = false;
        wideBasketActive   = false;
        magnetTimer        = 0;
        doubleTimer        = 0;
        slowTimer          = 0;
        wideTimer          = 0;
    }
    public int getLevelTarget() {
        return LEVEL_TARGETS[Math.min(level - 1, LEVEL_TARGETS.length - 1)];
    }
    public void addScore(int points)  { score += points; }
    public void incrementCombo()      { combo++; if (combo > highestCombo) highestCombo = combo; }
    public void resetCombo()          { combo = 0; comboMultiplier = 1f; }
    public void addCoins(int coins)   { coinsThisGame += coins; }
    public void decrementTime()       { if (timeLeft > 0) timeLeft--; }
    public void addTime(int seconds)  { timeLeft += seconds; }
    public void levelUp()             { level++; timeLeft += 45; }

    public int getScore()                   { return score; }
    public int getLevel()                   { return level; }
    public int getCombo()                   { return combo; }
    public int getHighestCombo()            { return highestCombo; }
    public float getComboMultiplier()       { return comboMultiplier; }
    public void setComboMultiplier(float m) { comboMultiplier = m; }
    public int getTimeLeft()                { return timeLeft; }
    public int getCoinsThisGame()           { return coinsThisGame; }
    public boolean isShieldActive()         { return shieldActive; }
    public boolean isMagnetActive()         { return magnetActive; }
    public boolean isDoublePointsActive()   { return doublePointsActive; }
    public boolean isSlowTimeActive()       { return slowTimeActive; }
    public boolean isWideBasketActive()     { return wideBasketActive; }
    public int getMagnetTimer()             { return magnetTimer; }
    public int getDoubleTimer()             { return doubleTimer; }
    public int getSlowTimer()               { return slowTimer; }
    public int getWideTimer()               { return wideTimer; }
    public Difficulty getDifficulty()       { return difficulty; }
    public void setShieldActive(boolean v)       { shieldActive = v; }
    public void setMagnetActive(boolean v)       { magnetActive = v; }
    public void setDoublePointsActive(boolean v) { doublePointsActive = v; }
    public void setSlowTimeActive(boolean v)     { slowTimeActive = v; }
    public void setWideBasketActive(boolean v)   { wideBasketActive = v; }
    public void setMagnetTimer(int t)            { magnetTimer = t; }
    public void setDoubleTimer(int t)            { doubleTimer = t; }
    public void setSlowTimer(int t)              { slowTimer = t; }
    public void setWideTimer(int t)              { wideTimer = t; }
    public void setDifficulty(Difficulty d)      { difficulty = d; }
}