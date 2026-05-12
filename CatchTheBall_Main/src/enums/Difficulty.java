package OOP_PROJECT.CatchTheBall.src.enums;

public enum Difficulty {
    EASY("Easy",     0.50f, 120, -3),
    NORMAL("Normal", 0.75f,  90, -15),
    HARD("Hard",     1.05f,  60, -28);

    private final String displayName;
    private final float  speedMultiplier;
    private final int    startingTime;
    private final int    penaltyMultiplier;

    Difficulty(String displayName, float speedMultiplier,
               int startingTime, int penaltyMultiplier) {
        this.displayName       = displayName;
        this.speedMultiplier   = speedMultiplier;
        this.startingTime      = startingTime;
        this.penaltyMultiplier = penaltyMultiplier;
    }

    public String getDisplayName()      { return displayName; }
    public float  getSpeedMultiplier()  { return speedMultiplier; }
    public int    getStartingTime()     { return startingTime; }
    public int    getPenaltyMultiplier(){ return penaltyMultiplier; }
}