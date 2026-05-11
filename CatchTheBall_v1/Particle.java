import java.awt.*;

/**
 * A single visual particle emitted on ball catch / explosion.
 * Demonstrates Encapsulation — all physics state is private.
 */
public class Particle {

    private double x, y;
    private double vx, vy;
    private float  life;
    private final float  decay;
    private final Color  color;
    private final int    size;
    private final boolean square;
    private final double gravity;

    /**
     * @param isBomb true → larger, square, slower-fading particles
     */
    public Particle(double x, double y, Color color, boolean isBomb) {
        this.x      = x;
        this.y      = y;
        this.color  = color;
        this.life   = 1f;

        double angle = Math.random() * Math.PI * 2;
        double speed = isBomb ? (4 + Math.random() * 5) : (2 + Math.random() * 3);
        this.vx      = Math.cos(angle) * speed;
        this.vy      = Math.sin(angle) * speed - (isBomb ? 0 : 2);
        this.decay   = isBomb ? 0.022f : 0.032f;
        this.size    = isBomb ? (int)(5 + Math.random() * 6) : (int)(3 + Math.random() * 4);
        this.square  = isBomb;
        this.gravity = isBomb ? 0.14 : 0.08;
    }

    public void update() {
        life -= decay;
        vy   += gravity;
        x    += vx;
        y    += vy;
        vx   *= 0.97;
    }

    public void draw(Graphics2D g) {
        if (life <= 0) return;
        int alpha = Math.max(0, Math.min(255, (int)(life * 255)));
        Color c = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
        g.setColor(c);
        int s = Math.max(1, (int)(size * life));
        if (square) {
            g.fillRect((int) x - s / 2, (int) y - s / 2, s, s);
        } else {
            g.fillOval((int) x - s / 2, (int) y - s / 2, s, s);
        }
    }

    public boolean isDead() { return life <= 0; }
}
