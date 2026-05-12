package OOP_PROJECT.CatchTheBall.src.enums;

public enum Difficulty {
    EASY("Easy",     0.45f, 140, -2, 7),
    NORMAL("Normal", 0.75f, 110, -15, 5),
    HARD("Hard",     1.10f,  80, -28, 4);

    private final String displayName;
    private final float  speedMultiplier;
    private final int    startingTime;
    private final int    penaltyMultiplier;
    private final int    startingLives;

    Difficulty(String displayName, float speedMultiplier,
               int startingTime, int penaltyMultiplier, int startingLives) {
        this.displayName       = displayName;
        this.speedMultiplier   = speedMultiplier;
        this.startingTime      = startingTime;
        this.penaltyMultiplier = penaltyMultiplier;
        this.startingLives     = startingLives;
    }

    public String getDisplayName()      { return displayName; }
    public float  getSpeedMultiplier()  { return speedMultiplier; }
    public int    getStartingTime()     { return startingTime; }
    public int    getPenaltyMultiplier(){ return penaltyMultiplier; }
    public int    getStartingLives()    { return startingLives; }
}