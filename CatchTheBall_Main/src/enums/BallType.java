package OOP_PROJECT.CatchTheBall.src.enums;

public enum BallType {
    APPLE(10, false, false),
    ORANGE(10, false, false),
    STRAWBERRY(50, false, true),
    MUSHROOM(-30, true, false),
    EGGPLANT(-20, true, false),
    GOLDEN_APPLE(100, false, true),
    BOMB(-50, true, false),
    MYSTERY(0, false, false),
    RAINBOW(500, false, true),
    FROZEN(30, false, false),
    GIANT(150, false, false),
    TINY(200, false, false);

    private final int points;
    private final boolean isBad;
    private final boolean isRare;

    BallType(int points, boolean isBad, boolean isRare) {
        this.points = points;
        this.isBad  = isBad;
        this.isRare = isRare;
    }

    public int getPoints()  { return points; }
    public boolean isBad()  { return isBad; }
    public boolean isRare() { return isRare; }
}