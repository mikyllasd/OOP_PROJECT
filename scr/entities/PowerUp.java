package entities;
import core.Entity;
import enums.PowerUpType;
import utils.FontManager;
import utils.MathUtils;
import java.awt.*;
public class PowerUp extends Entity {
    private PowerUpType type;
    private float speed;
    private int glowTimer;

    public PowerUp(float x, float y, PowerUpType type) {
        super(x, y, 40, 40);
        this.type      = type;
        this.speed     = 2.0f;
        this.glowTimer = 0;
    }
    @Override
    public void update() { y += speed; glowTimer++; }
    @Override
    public void draw(Graphics2D g) {
        if (!active) return;
        float pulse = MathUtils.pulse(glowTimer, 0.12f);
        int alpha   = (int)(pulse * 180);
        Color c = type.getColor();
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
        g.fillOval((int) x - 5, (int) y - 5, width + 10, height + 10);
        g.setColor(new Color(255, 255, 255, 120));
        g.setStroke(new BasicStroke(2f));
        g.drawOval((int) x - 5, (int) y - 5, width + 10, height + 10);
        g.setStroke(new BasicStroke(1f));
        g.setFont(FontManager.getEmoji(28));
        FontMetrics fm = g.getFontMetrics();
        String emoji   = getEmoji();
        int tw         = fm.stringWidth(emoji);
        g.setColor(Color.WHITE);
        g.drawString(emoji, (int)(x + (width - tw) / 2f), (int)(y + height - 5));
    }
    private String getEmoji() {
        switch (type) {
            case MAGNET:        return "\uD83E\uDDF2";
            case TIME_PLUS:     return "\u23F0";
            case SHIELD:        return "\uD83D\uDEE1\uFE0F";
            case DOUBLE_POINTS: return "2\uFE0F\u20E3";
            case SLOW_TIME:     return "\uD83D\uDD5B";
            case WIDE_BASKET:   return "\uD83E\uDDF3";
            default:            return "?";
        }
    }
    public PowerUpType getType() { return type; }
}