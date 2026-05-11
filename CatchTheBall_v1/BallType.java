import java.awt.Color;

/**
 * Enum representing the different types of balls in the game.
 * Each type has a point value, spawn weight, color, and label.
 * 
 * Demonstrates: Enum with fields and methods (OOP principle)
 */
public enum BallType {
    NORMAL  (10,  40, new Color(100, 200, 100), "●"),
    STAR    (50,   5, new Color(255, 215,   0), "★"),
    BOMB    (-30, 12, new Color( 50,  50,  50), "✦"),
    ROTTEN  (-20, 15, new Color(130, 160,  40), "✕");

    private final int points;
    private final int weight;   // higher weight = more likely to spawn
    private final Color color;
    private final String label;

    BallType(int points, int weight, Color color, String label) {
        this.points = points;
        this.weight = weight;
        this.color  = color;
        this.label  = label;
    }

    public int    getPoints() { return points; }
    public int    getWeight() { return weight; }
    public Color  getColor()  { return color;  }
    public String getLabel()  { return label;  }

    /**
     * Returns a random BallType weighted by each type's spawn probability.
     */
    public static BallType random() {
        int total = 0;
        for (BallType t : values()) total += t.weight;
        int r = (int)(Math.random() * total);
        for (BallType t : values()) {
            r -= t.weight;
            if (r <= 0) return t;
        }
        return NORMAL;
    }
}
