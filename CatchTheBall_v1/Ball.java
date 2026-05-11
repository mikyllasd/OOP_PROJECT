import java.awt.*;
import java.awt.geom.*;

/**
 * Represents a falling ball in the game.
 * Extends Entity — demonstrates Inheritance and Polymorphism.
 *
 * Each BallType has different visuals, speed, and point value.
 */
public class Ball extends Entity {

    private final BallType type;
    private double speed;
    private double drift;          // horizontal drift per frame
    private boolean active;
    private int arenaWidth;

    // Trail effect
    private final double[] trailX = new double[7];
    private final double[] trailY = new double[7];
    private int trailCount = 0;

    // Wobble animation
    private double wobble    = 0;
    private double wobbleDir = 1;

    public Ball(double x, double y, BallType type, int level, int arenaWidth) {
        super(x, y, 32, 32);
        this.type       = type;
        this.active     = true;
        this.arenaWidth = arenaWidth;
        this.speed      = 2.0 + level * 0.38 + Math.random() * 1.2;
        this.drift      = (Math.random() - 0.5) * 0.55 * Math.min(level, 6);
    }

    @Override
    public void update() {
        // Shift trail buffer
        System.arraycopy(trailX, 0, trailX, 1, trailX.length - 1);
        System.arraycopy(trailY, 0, trailY, 1, trailY.length - 1);
        trailX[0] = x + width  / 2.0;
        trailY[0] = y + height / 2.0;
        if (trailCount < trailX.length) trailCount++;

        // Move
        y += speed;
        x += drift;

        // Wobble angle for star / visual flair
        wobble += wobbleDir * 3.5;
        if (Math.abs(wobble) > 14) wobbleDir = -wobbleDir;

        // Bounce off side walls
        if (x < 0)                  { x = 0;                  drift =  Math.abs(drift); }
        if (x > arenaWidth - width) { x = arenaWidth - width; drift = -Math.abs(drift); }
    }

    @Override
    public void draw(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        drawTrail(g2);

        switch (type) {
            case BOMB:   drawBomb(g2);   break;
            case STAR:   drawStar(g2);   break;
            case ROTTEN: drawRotten(g2); break;
            default:     drawNormal(g2); break;
        }

        g2.dispose();
    }

    // ── Trail ─────────────────────────────────────────────────────────

    private void drawTrail(Graphics2D g) {
        Color base = type.getColor();
        for (int i = 0; i < trailCount; i++) {
            float alpha = (float)(trailCount - i) / (trailCount * 3.0f);
            int   size  = (int)(width * 0.55 * (1.0 - (double) i / trailCount));
            g.setColor(new Color(
                base.getRed() / 255f,
                base.getGreen() / 255f,
                base.getBlue() / 255f,
                alpha
            ));
            g.fillOval((int) trailX[i] - size / 2, (int) trailY[i] - size / 2, size, size);
        }
    }

    // ── Ball Visuals ──────────────────────────────────────────────────

    private void drawNormal(Graphics2D g) {
        // Gradient sphere
        float cx = (float) x + width * 0.35f;
        float cy = (float) y + height * 0.3f;
        RadialGradientPaint rg = new RadialGradientPaint(
            new Point2D.Float(cx, cy), width * 0.55f,
            new float[]{ 0f, 1f },
            new Color[]{ type.getColor().brighter().brighter(), type.getColor().darker() }
        );
        g.setPaint(rg);
        g.fillOval((int) x, (int) y, width, height);

        // Outline
        g.setColor(type.getColor().darker().darker());
        g.setStroke(new BasicStroke(1.8f));
        g.drawOval((int) x, (int) y, width, height);

        // Shine
        g.setColor(new Color(255, 255, 255, 90));
        g.fillOval((int) x + 6, (int) y + 5, 10, 7);
    }

    private void drawBomb(Graphics2D g) {
        int bx = (int) x, by = (int) y + 4;
        int bw = width,   bh = height - 4;

        // Body
        RadialGradientPaint rg = new RadialGradientPaint(
            new Point2D.Float(bx + bw * 0.35f, by + bh * 0.3f), bw * 0.55f,
            new float[]{ 0f, 1f },
            new Color[]{ new Color(80, 80, 80), new Color(20, 20, 20) }
        );
        g.setPaint(rg);
        g.fillOval(bx, by, bw, bh);

        g.setColor(new Color(100, 100, 100));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(bx, by, bw, bh);

        // Fuse
        g.setColor(new Color(160, 110, 40));
        g.setStroke(new BasicStroke(2.5f));
        g.drawLine(bx + bw / 2, by, bx + bw / 2 + 5, by - 6);

        // Spark
        g.setColor(new Color(255, 180, 0));
        g.fillOval(bx + bw / 2 + 2, by - 10, 7, 7);
        g.setColor(new Color(255, 100, 0, 180));
        g.fillOval(bx + bw / 2 + 0, by - 8, 11, 11);

        // Shine
        g.setColor(new Color(255, 255, 255, 55));
        g.fillOval(bx + 7, by + 5, 9, 6);
    }

    private void drawStar(Graphics2D g) {
        int   cx    = (int) x + width / 2;
        int   cy    = (int) y + height / 2;
        int[] starX = new int[10];
        int[] starY = new int[10];
        double baseAngle = Math.toRadians(wobble - 90);

        for (int i = 0; i < 10; i++) {
            double angle = baseAngle + i * Math.PI / 5;
            int    r     = (i % 2 == 0) ? width / 2 : width / 4;
            starX[i] = cx + (int)(r * Math.cos(angle));
            starY[i] = cy + (int)(r * Math.sin(angle));
        }

        // Glow
        g.setColor(new Color(255, 240, 80, 60));
        g.fillPolygon(starX, starY, 10);

        // Fill
        g.setColor(new Color(255, 230, 20));
        Polygon star = new Polygon(starX, starY, 10);
        g.fill(star);

        // Outline
        g.setColor(new Color(220, 160, 0));
        g.setStroke(new BasicStroke(1.2f));
        g.draw(star);
    }

    private void drawRotten(Graphics2D g) {
        // Body
        RadialGradientPaint rg = new RadialGradientPaint(
            new Point2D.Float((float) x + width * 0.35f, (float) y + height * 0.3f), width * 0.55f,
            new float[]{ 0f, 1f },
            new Color[]{ new Color(160, 190, 60), new Color(80, 110, 20) }
        );
        g.setPaint(rg);
        g.fillOval((int) x, (int) y, width, height);

        g.setColor(new Color(70, 100, 15));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval((int) x, (int) y, width, height);

        // X mark
        g.setColor(new Color(50, 80, 10, 180));
        g.setStroke(new BasicStroke(2.5f));
        g.drawLine((int) x + 9, (int) y + 9, (int) x + width - 9, (int) y + height - 9);
        g.drawLine((int) x + width - 9, (int) y + 9, (int) x + 9, (int) y + height - 9);
    }

    // ── Getters ──────────────────────────────────────────────────────

    public BallType getType()   { return type;   }
    public boolean  isActive()  { return active;  }
    public void     setActive(boolean v) { active = v; }
}
