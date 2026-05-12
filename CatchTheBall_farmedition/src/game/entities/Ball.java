package game.entities;

import java.awt.*;
import java.awt.geom.*;

public class Ball extends Entity {
    private BallType type;
    private float speed;
    private int animTimer;

    public Ball(float x, float y, BallType type, float speed) {
        super(x, y, 36, 36);
        this.type = type;
        this.speed = speed;
        this.animTimer = 0;
    }

    @Override
    public void update() {
        y += speed;
        animTimer++;
    }

    @Override
    public void draw(Graphics2D g) {
        if (!active) return;
        int cx = (int)(x + width / 2f);
        int cy = (int)(y + height / 2f);
        int r = width / 2;

        switch (type) {
            case APPLE:      drawApple(g, cx, cy, r);      break;
            case ORANGE:     drawOrange(g, cx, cy, r);     break;
            case STRAWBERRY: drawStrawberry(g, cx, cy, r); break;
            case MUSHROOM:   drawMushroom(g, cx, cy, r);   break;
            case EGGPLANT:   drawEggplant(g, cx, cy, r);   break;
        }

        if (type.isRare()) {
            float pulse = (float)(0.5 + 0.5 * Math.sin(animTimer * 0.15));
            g.setColor(new Color(255, 215, 0, (int)(80 * pulse)));
            g.setStroke(new BasicStroke(3f));
            g.drawOval(cx - r - 5, cy - r - 5, (r + 5) * 2, (r + 5) * 2);
            g.setStroke(new BasicStroke(1f));
        }
    }

    private void drawApple(Graphics2D g, int cx, int cy, int r) {
        g.setColor(new Color(200, 20, 20));
        g.fillOval(cx - r, cy - r + 3, r * 2, r * 2 - 2);
        g.setColor(new Color(240, 80, 80));
        g.fillOval(cx - r + 2, cy - r + 5, r * 2 - 4, r - 4);
        g.setColor(new Color(255, 255, 255, 70));
        g.fillOval(cx - r/2, cy - r/2, r/2, r/3);
        g.setColor(new Color(80, 50, 10));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(cx, cy - r + 3, cx + 3, cy - r - 6);
        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(50, 140, 30));
        int[] lx = {cx + 2, cx + 11, cx + 5};
        int[] ly = {cy - r - 2, cy - r - 9, cy - r - 9};
        g.fillPolygon(lx, ly, 3);
    }

    private void drawOrange(Graphics2D g, int cx, int cy, int r) {
        g.setColor(new Color(210, 100, 10));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(255, 165, 40));
        g.fillOval(cx - r + 2, cy - r + 2, r * 2 - 4, r * 2 - 4);
        g.setColor(new Color(200, 80, 0, 60));
        for (int i = 0; i < 6; i++) {
            int sx = cx + (int)(Math.cos(i * Math.PI / 3) * r * 0.55);
            int sy = cy + (int)(Math.sin(i * Math.PI / 3) * r * 0.55);
            g.fillOval(sx - 2, sy - 2, 5, 5);
        }
        g.setColor(new Color(255, 255, 255, 60));
        g.fillOval(cx - r/2, cy - r/2, r/2, r/3);
        g.setColor(new Color(80, 50, 10));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(cx, cy - r, cx, cy - r - 5);
        g.setStroke(new BasicStroke(1f));
    }

    private void drawStrawberry(Graphics2D g, int cx, int cy, int r) {
        g.setColor(new Color(180, 15, 30));
        g.fillArc(cx - r, cy - r/2, r * 2, r * 2, 180, 180);
        g.fillRect(cx - r, cy, r * 2, r/2);
        g.setColor(new Color(230, 40, 60));
        g.fillArc(cx - r + 2, cy - r/2 + 2, r * 2 - 4, r * 2 - 4, 180, 180);
        g.setColor(new Color(255, 210, 80));
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int sx = cx - r/2 + 1 + col * (r / 2);
                int sy = cy - r/4 + row * (r / 3);
                g.fillOval(sx, sy, 3, 3);
            }
        }
        g.setColor(new Color(50, 150, 30));
        g.fillArc(cx - r + 2, cy - r - 2, r - 2, r/2, 0, 180);
        g.fillArc(cx, cy - r - 2, r - 2, r/2, 0, 180);
        g.setColor(new Color(70, 180, 40));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(cx, cy - r, cx, cy - r - 7);
        g.setStroke(new BasicStroke(1f));
    }

    private void drawMushroom(Graphics2D g, int cx, int cy, int r) {
        g.setColor(new Color(230, 240, 210));
        g.fillRoundRect(cx - r/3, cy - r/6, r/3 * 2 + 2, r/2 + 6, 5, 5);
        g.setColor(new Color(130, 20, 20));
        g.fillArc(cx - r, cy - r, r * 2, (int)(r * 1.4), 0, 180);
        g.setColor(new Color(180, 30, 30));
        g.fillArc(cx - r + 2, cy - r + 2, r * 2 - 4, (int)(r * 1.4) - 4, 0, 180);
        g.setColor(new Color(255, 255, 255, 220));
        g.fillOval(cx - r/2 - 2, cy - r + 4, 9, 8);
        g.fillOval(cx + r/4 - 2, cy - r + 7, 7, 7);
        g.fillOval(cx - r/5, cy - r/2 - 4, 6, 5);
        g.setColor(new Color(240, 200, 160));
        g.fillArc(cx - r, cy - r/5, r * 2, r/2, 180, 180);
        g.setColor(new Color(220, 50, 50));
        g.setStroke(new BasicStroke(2.5f));
        g.drawLine(cx - 6, cy + 2, cx + 6, cy + 11);
        g.drawLine(cx + 6, cy + 2, cx - 6, cy + 11);
        g.setStroke(new BasicStroke(1f));
    }

    private void drawEggplant(Graphics2D g, int cx, int cy, int r) {
        g.setColor(new Color(55, 5, 90));
        g.fillOval(cx - r * 3/4 + 1, cy - r + 1, r * 3/2 - 2, r * 2 - 2);
        g.setColor(new Color(110, 35, 160));
        g.fillOval(cx - r * 3/4, cy - r, r * 3/2, r * 2);
        g.setColor(new Color(170, 90, 220));
        g.fillOval(cx - r/2, cy - r + 4, r/3, r/4);
        g.setColor(new Color(60, 130, 30));
        g.fillRoundRect(cx - 3, cy - r - 9, 7, 12, 3, 3);
        g.fillOval(cx - r/2 + 2, cy - r - 5, r/2, 9);
        g.fillOval(cx + 2, cy - r - 5, r/2, 9);
        g.setColor(new Color(60, 20, 5, 140));
        g.fillOval(cx - 7, cy + 4, 8, 6);
        g.fillOval(cx + 3, cy - 5, 7, 5);
    }

    public BallType getType() { return type; }
    public float getSpeed() { return speed; }
    public void setSpeed(float s) { this.speed = s; }
}
