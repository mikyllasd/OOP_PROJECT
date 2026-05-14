package OOP_PROJECT.CatchTheBall.src.enums;

public enum Difficulty {
    EASY("Easy",     1.80f, 140, -2,  7),
    NORMAL("Normal", 2.80f, 110, -20, 5),
    HARD("Hard",     5.00f,  80, -35, 4);

    private final String displayName;
    private final float  speedMultiplier;
    private final int    startingTime;
    private final int    penaltyMultiplier;
    private final int    startingLives;
    private final float  bombSpawnChance;

    Difficulty(String displayName, float speedMultiplier,
               int startingTime, int penaltyMultiplier, int startingLives) {
        this.displayName       = displayName;
        this.speedMultiplier   = speedMultiplier;
        this.startingTime      = startingTime;
        this.penaltyMultiplier = penaltyMultiplier;
        this.startingLives     = startingLives;
        this.bombSpawnChance   = switch (displayName) {
            case "Easy"   -> 0.10f;
            case "Normal" -> 0.40f;
            case "Hard"   -> 0.75f;
            default       -> 0.15f;
        };
    }

    public String getDisplayName()       { return displayName; }
    public float  getSpeedMultiplier()   { return speedMultiplier; }
    public int    getStartingTime()      { return startingTime; }
    public int    getPenaltyMultiplier() { return penaltyMultiplier; }
    public int    getStartingLives()     { return startingLives; }
    public float  getBombSpawnChance()   { return bombSpawnChance; }
}