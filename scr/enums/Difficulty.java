package enums;
public enum Difficulty {
    EASY("Easy", 0.7f, 240, -10),
    NORMAL("Normal", 1.0f, 180, -20),
    HARD("Hard", 1.4f, 120, -35);

    private final String displayName;
    private final float speedMultiplier;
    private final int startingTime;
    private final int penaltyMultiplier;

    Difficulty(String displayName, float speedMultiplier,
               int startingTime, int penaltyMultiplier) {
        this.displayName     = displayName;
        this.speedMultiplier = speedMultiplier;
        this.startingTime    = startingTime;
        this.penaltyMultiplier = penaltyMultiplier;
    }
    public String getDisplayName()    { return displayName; }
    public float getSpeedMultiplier() { return speedMultiplier; }
    public int getStartingTime()      { return startingTime; }
    public int getPenaltyMultiplier() { return penaltyMultiplier; }
}