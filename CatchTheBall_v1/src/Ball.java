import java.awt.*;

public class Ball extends Entity {
    private BallType type;
    private float speed;
    private float wobble;
    private int wobbleTimer;

    public Ball(float x, float y, BallType type, float speed) {
        super(x, y, 40, 40);
        this.type = type;
        this.speed = speed;
        this.wobble = 0;
        this.wobbleTimer = 0;
    }

    @Override
    public void update() {
        y += speed;
        wobbleTimer++;
        wobble = (float)(Math.sin(wobbleTimer * 0.15) * 3);
    }

    @Override
    public void draw(Graphics2D g) {
        if (!active) return;
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 32);
        g.setFont(emojiFont);
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(type.getEmoji());
        g.drawString(type.getEmoji(), (int)(x + wobble + (width - tw) / 2f), (int)(y + height - 4));

        // rare glow
        if (type.isRare()) {
            g.setColor(new Color(255, 215, 0, 60));
            g.fillOval((int)x - 4, (int)y - 4, width + 8, height + 8);
        }
    }

    public BallType getType() { return type; }
    public float getSpeed() { return speed; }
    public void setSpeed(float speed) { this.speed = speed; }
}
