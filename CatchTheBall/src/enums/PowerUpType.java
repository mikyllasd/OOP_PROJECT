package OOP_PROJECT.CatchTheBall.src.enums;

import java.awt.Color;

public enum PowerUpType {
    MAGNET("Magnet",        new Color(255, 100, 100)),
    TIME_PLUS("Time+",      new Color(100, 200, 255)),
    SHIELD("Shield",        new Color(100, 255, 150)),
    DOUBLE_POINTS("2x Pts", new Color(255, 220,  50)),
    SLOW_TIME("Slow Time",  new Color(180, 100, 255)),
    WIDE_BASKET("Wide",     new Color(100, 255, 220));

    private final String label;
    private final Color  color;

    PowerUpType(String label, Color color) {
        this.label = label;
        this.color = color;
    }

    public String getLabel() { return label; }
    public Color  getColor() { return color; }
}