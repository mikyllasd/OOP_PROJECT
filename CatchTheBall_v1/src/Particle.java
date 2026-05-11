import java.awt.*;

public class Particle {
    private float x, y;
    private float vx, vy;
    private float life;
    private float maxLife;
    private Color color;
    private int size;
    private String text;

    public Particle(float x, float y, float vx, float vy, Color color, int size, float life) {
        this.x = x; this.y = y;
        this.vx = vx; this.vy = vy;
        this.color = color; this.size = size;
        this.life = life; this.maxLife = life;
        this.text = null;
    }

    public Particle(float x, float y, float vx, float vy, String text, float life) {
        this.x = x; this.y = y;
        this.vx = vx; this.vy = vy;
        this.text = text;
        this.color = Color.WHITE;
        this.size = 16;
        this.life = life; this.maxLife = life;
    }

    public void update() {
        x += vx;
        y += vy;
        vy += 0.08f; // gravity
        life--;
    }

    public void draw(Graphics2D g) {
        float alpha = Math.min(1f, life / maxLife);
        if (text != null) {
            g.setFont(new Font("Arial", Font.BOLD, size));
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 255)));
            g.drawString(text, (int)x, (int)y);
        } else {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)(alpha * 200)));
            g.fillOval((int)(x - size/2f), (int)(y - size/2f), size, size);
        }
    }

    public boolean isDead() { return life <= 0; }
}
