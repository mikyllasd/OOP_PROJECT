package entities;
import core.Entity;
import utils.FontManager;
import java.awt.*;
public class Particle extends Entity {
    private float vx, vy;
    private float life, maxLife;
    private Color color;
    private int size;
    private String text;

    public Particle(float x, float y, float vx, float vy,
                    Color color, int size, float life) {
        super(x, y, size, size);
        this.vx = vx; this.vy = vy;
        this.color = color; this.size = size;
        this.life = life; this.maxLife = life;
        this.text = null;
    }
    public Particle(float x, float y, float vx, float vy,
                    String text, Color color, float life) {
        super(x, y, 16, 16);
        this.vx = vx; this.vy = vy;
        this.text = text; this.color = color;
        this.size = 16; this.life = life; this.maxLife = life;
    }
    @Override
    public void update() {
        x += vx; y += vy; vy += 0.08f; life--;
    }
    @Override
    public void draw(Graphics2D g) {
        float alpha = Math.min(1f, life / maxLife);
        if (text != null) {
            g.setFont(FontManager.getBodyBold(size));
            g.setColor(new Color(color.getRed(), color.getGreen(),
                    color.getBlue(), (int)(alpha * 255)));
            g.drawString(text, (int) x, (int) y);
        } else {
            g.setColor(new Color(color.getRed(), color.getGreen(),
                    color.getBlue(), (int)(alpha * 200)));
            g.fillOval((int)(x - size / 2f), (int)(y - size / 2f), size, size);
        }
    }
    public boolean isDead() { return life <= 0; }
}