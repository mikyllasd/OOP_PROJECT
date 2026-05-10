import java.awt.*;
import java.awt.geom.*;

/**
 * Represents the player-controlled basket at the bottom of the screen.
 * Extends Entity — demonstrates Inheritance.
 *
 * Smoothly follows mouse position; leans when moving left/right.
 */
public class Basket extends Entity {

    private double targetX;
    private double leanAngle;          // degrees; positive = lean right
    private static final int SPEED = 9;

    public Basket(double x, double y) {
        super(x, y, 72, 32);
        this.targetX   = x;
        this.leanAngle = 0;
    }

    // ── Movement ─────────────────────────────────────────────────────

    /** Snap target directly (mouse control). */
    public void setTargetX(double tx, int arenaWidth) {
        this.targetX = Math.max(0, Math.min(tx - width / 2.0, arenaWidth - width));
    }

    /** Move left via keyboard. */
    public void moveLeft() {
        targetX = Math.max(0, targetX - SPEED);
    }

    /** Move right via keyboard. */
    public void moveRight(int arenaWidth) {
        targetX = Math.min(arenaWidth - width, targetX + SPEED);
    }

    @Override
    public void update() {
        double prev = x;
        x += (targetX - x) * 0.18;                 // smooth lerp
        double dx = x - prev;
        leanAngle += (dx * 2.2 - leanAngle) * 0.28;
        leanAngle  = Math.max(-18, Math.min(18, leanAngle));
    }

    @Override
    public void draw(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Rotate around the centre
        g2.translate(x + width / 2.0, y + height / 2.0);
        g2.rotate(Math.toRadians(leanAngle));
        g2.translate(-width / 2.0, -height / 2.0);

        // ── Shadow ───────────────────────────────────────────────────
        g2.setColor(new Color(0, 0, 0, 35));
        g2.fillOval(6, height + 3, width - 12, 9);

        // ── Basket body (trapezoid) ───────────────────────────────────
        int[] px = { 0, width, width - 8, 8 };
        int[] py = { 0, 0, height, height };

        GradientPaint gp = new GradientPaint(
            0, 0, new Color(210, 150, 70),
            0, height, new Color(160, 100, 35)
        );
        g2.setPaint(gp);
        g2.fillPolygon(px, py, 4);

        // ── Weave lines ───────────────────────────────────────────────
        g2.setColor(new Color(130, 80, 25, 160));
        g2.setStroke(new BasicStroke(1.2f));

        // Horizontal
        for (int i = 1; i <= 2; i++) {
            int hy = height * i / 3;
            g2.drawLine(0, hy, width, hy);
        }
        // Vertical
        for (int i = 1; i <= 4; i++) {
            g2.drawLine(width / 5 * i, 0, width / 5 * i, height);
        }

        // ── Rim (top edge) ────────────────────────────────────────────
        g2.setColor(new Color(235, 180, 90));
        g2.setStroke(new BasicStroke(3.5f));
        g2.drawLine(0, 0, width, 0);

        // ── Shine ─────────────────────────────────────────────────────
        g2.setColor(new Color(255, 230, 160, 70));
        g2.fillRect(4, 2, width / 3, height / 2);

        g2.dispose();
    }

    // ── Getters ──────────────────────────────────────────────────────

    public int getCatchCenterX() { return (int)(x + width / 2.0); }
    public int getCatchY()       { return (int) y; }
}
