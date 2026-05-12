package game.entities;

public enum BallType {
    APPLE("ðŸŽ", 10, false, false),
    ORANGE("ðŸŠ", 10, false, false),
    STRAWBERRY("ðŸ“", 50, false, true),
    MUSHROOM("ðŸ„", -30, true, false),
    EGGPLANT("ðŸ†", -20, true, false);

    private final String emoji;
    private final int points;
    private final boolean isBad;
    private final boolean isRare;

    BallType(String emoji, int points, boolean isBad, boolean isRare) {
        this.emoji = emoji;
        this.points = points;
        this.isBad = isBad;
        this.isRare = isRare;
    }

    public String getEmoji() { return emoji; }
    public int getPoints() { return points; }
    public boolean isBad() { return isBad; }
    public boolean isRare() { return isRare; }
}

