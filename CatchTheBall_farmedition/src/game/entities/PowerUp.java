package game.entities;

import java.awt.*;

public class PowerUp extends Entity {
    public enum PowerUpType {
        MAGNET("ðŸ§²", "Magnet", new Color(255, 100, 100)),
        TIME_PLUS("â°", "Time+", new Color(100, 200, 255)),
        SHIELD("ðŸ›¡ï¸", "Shield", new Color(100, 255, 150)),
        DOUBLE_POINTS("2ï¸âƒ£", "2x Points", new Color(255, 220, 50));

        public final String emoji;
        public final String label;
        public final Color color;
        PowerUpType(String e, String l, Color c) { emoji=e; label=l; color=c; }
    }

    private PowerUpType type;
    private float speed;
    private int glowTimer;

    public PowerUp(float x, float y, PowerUpType type) {
        super(x, y, 40, 40);
        this.type = type;
        this.speed = 2.0f;
        this.glowTimer = 0;
    }

    @Override
    public void update() {
        y += speed;
        glowTimer++;
    }

    @Override
    public void draw(Graphics2D g) {
        if (!active) return;
        float pulse = (float)(0.7 + 0.3 * Math.sin(glowTimer * 0.12));
        int alpha = (int)(pulse * 180);
        g.setColor(new Color(type.color.getRed(), type.color.getGreen(), type.color.getBlue(), alpha));
        g.fillOval((int)x - 5, (int)y - 5, width + 10, height + 10);

        g.setColor(new Color(255, 255, 255, 120));
        g.setStroke(new BasicStroke(2f));
        g.drawOval((int)x - 5, (int)y - 5, width + 10, height + 10);
        g.setStroke(new BasicStroke(1f));

        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 28);
        g.setFont(emojiFont);
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(type.emoji);
        g.drawString(type.emoji, (int)(x + (width - tw) / 2f), (int)(y + height - 5));
    }

    public PowerUpType getType() { return type; }
}

