package OOP_PROJECT.CatchTheBall.src.entities;

import OOP_PROJECT.CatchTheBall.src.core.Entity;
import OOP_PROJECT.CatchTheBall.src.enums.BallType;
import OOP_PROJECT.CatchTheBall.src.utils.FontManager;
import OOP_PROJECT.CatchTheBall.src.utils.MathUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Ball extends Entity {
    private BallType      type;
    private float         speed;
    private int           animTimer;
    private float         wobbleOffset;
    private int           rainbowHue;
    private List<float[]> trail;
    private static final int TRAIL_LEN = 4;
    private int           squishTimer;
    private boolean       caught;

    public Ball(float x, float y, BallType type, float speed) {
        super(x, y,
              type == BallType.GIANT ? 60 : type == BallType.TINY ? 20 : 36,
              type == BallType.GIANT ? 60 : type == BallType.TINY ? 20 : 36);
        this.type        = type;
        this.speed       = speed;
        this.animTimer   = 0;
        this.trail       = new ArrayList<>();
        this.squishTimer = 0;
        this.caught      = false;
    }

    @Override
    public void update() {
        if (caught) { squishTimer--; return; }
        trail.add(new float[]{x + width / 2f, y + height / 2f});
        if (trail.size() > TRAIL_LEN) trail.remove(0);
        y += speed;
        animTimer++;
        if (type == BallType.RAINBOW) rainbowHue = (rainbowHue + 5) % 360;
        if (type == BallType.MUSHROOM || type == BallType.EGGPLANT || type == BallType.BOMB) {
            wobbleOffset = (float)(Math.sin(animTimer * 0.15) * 3);
            x += wobbleOffset * 0.1f;
        }
    }

    public void triggerCatch() {
        caught      = true;
        squishTimer = 10;
        active      = false;
    }

    public boolean isCaughtAnimDone() { return caught && squishTimer <= 0; }

    @Override
    public void draw(Graphics2D g) {
        if (!active && !caught) return;
        drawTrail(g);

        int cx = (int)(x + width / 2f);
        int cy = (int)(y + height / 2f);
        int r  = width / 2;

        float sx = 1f, sy = 1f;
        if (caught && squishTimer > 0) {
            float t = squishTimer / 10f;
            sx = 1f + 0.4f * (1f - t);
            sy = 1f - 0.3f * (1f - t);
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(cx, cy);
        g2.scale(sx, sy);
        g2.translate(-cx, -cy);

        switch (type) {
            case APPLE:        drawApple(g2, cx, cy, r);       break;
            case ORANGE:       drawOrange(g2, cx, cy, r);      break;
            case STRAWBERRY:   drawStrawberry(g2, cx, cy, r);  break;
            case MUSHROOM:     drawMushroom(g2, cx, cy, r);    break;
            case EGGPLANT:     drawEggplant(g2, cx, cy, r);    break;
            case GOLDEN_APPLE: drawGoldenApple(g2, cx, cy, r); break;
            case BOMB:         drawBomb(g2, cx, cy, r);        break;
            case MYSTERY:      drawMystery(g2, cx, cy, r);     break;
            case RAINBOW:      drawRainbow(g2, cx, cy, r);     break;
            case FROZEN:       drawFrozen(g2, cx, cy, r);      break;
            case GIANT:        drawGiant(g2, cx, cy, r);       break;
            case TINY:         drawTiny(g2, cx, cy, r);        break;
        }
        drawShadow(g2, cx, r);

        if (type.isRare() && type != BallType.RAINBOW) {
            float pulse = MathUtils.pulse(animTimer, 0.15f);
            g2.setColor(new Color(255, 215, 0, (int)(80 * pulse)));
            g2.setStroke(new BasicStroke(3f));
            g2.drawOval(cx - r - 5, cy - r - 5, (r + 5) * 2, (r + 5) * 2);
            g2.setStroke(new BasicStroke(1f));
        }
        g2.dispose();
    }

    private void drawTrail(Graphics2D g) {
        for (int i = 0; i < trail.size(); i++) {
            float alpha = (float) i / trail.size() * 0.45f;
            int sz = (int)(width * ((float) i / trail.size()) * 0.55f);
            if (sz < 2) continue;
            Color tc;
            if      (type == BallType.RAINBOW)      tc = Color.getHSBColor((rainbowHue + i * 20) % 360 / 360f, 1f, 1f);
            else if (type == BallType.GOLDEN_APPLE) tc = new Color(255, 215, 0);
            else if (type == BallType.FROZEN)       tc = new Color(150, 220, 255);
            else                                    tc = new Color(255, 255, 255);
            g.setColor(new Color(tc.getRed(), tc.getGreen(), tc.getBlue(), (int)(alpha * 180)));
            float[] pt = trail.get(i);
            g.fillOval((int)(pt[0] - sz / 2f), (int)(pt[1] - sz / 2f), sz, sz);
        }
    }

    private void drawShadow(Graphics2D g, int cx, int r) {
        g.setColor(new Color(0, 0, 0, 40));
        g.fillOval(cx - r + 4, (int)(y + height) - 4, (r - 4) * 2, 6);
    }

    private void drawApple(Graphics2D g, int cx, int cy, int r) {
        g.setColor(new Color(200, 20, 20));
        g.fillOval(cx - r, cy - r + 3, r * 2, r * 2 - 2);
        g.setColor(new Color(240, 80, 80));
        g.fillOval(cx - r + 2, cy - r + 5, r * 2 - 4, r - 4);
        g.setColor(new Color(255, 255, 255, 70));
        g.fillOval(cx - r / 2, cy - r / 2, r / 2, r / 3);
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
        g.setColor(new Color(255, 255, 255, 60));
        g.fillOval(cx - r / 2, cy - r / 2, r / 2, r / 3);
        g.setColor(new Color(80, 50, 10));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(cx, cy - r, cx, cy - r - 5);
        g.setStroke(new BasicStroke(1f));
    }

    private void drawStrawberry(Graphics2D g, int cx, int cy, int r) {
        g.setColor(new Color(180, 15, 30));
        g.fillArc(cx - r, cy - r / 2, r * 2, r * 2, 180, 180);
        g.fillRect(cx - r, cy, r * 2, r / 2);
        g.setColor(new Color(230, 40, 60));
        g.fillArc(cx - r + 2, cy - r / 2 + 2, r * 2 - 4, r * 2 - 4, 180, 180);
        g.setColor(new Color(255, 210, 80));
        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 3; col++)
                g.fillOval(cx - r / 2 + 1 + col * (r / 2), cy - r / 4 + row * (r / 3), 3, 3);
        g.setColor(new Color(50, 150, 30));
        g.fillArc(cx - r + 2, cy - r - 2, r - 2, r / 2, 0, 180);
        g.fillArc(cx, cy - r - 2, r - 2, r / 2, 0, 180);
    }

    private void drawMushroom(Graphics2D g, int cx, int cy, int r) {
        g.setColor(new Color(230, 240, 210));
        g.fillRoundRect(cx - r / 3, cy - r / 6, r / 3 * 2 + 2, r / 2 + 6, 5, 5);
        g.setColor(new Color(130, 20, 20));
        g.fillArc(cx - r, cy - r, r * 2, (int)(r * 1.4), 0, 180);
        g.setColor(new Color(180, 30, 30));
        g.fillArc(cx - r + 2, cy - r + 2, r * 2 - 4, (int)(r * 1.4) - 4, 0, 180);
        g.setColor(new Color(255, 255, 255, 220));
        g.fillOval(cx - r / 2 - 2, cy - r + 4, 9, 8);
        g.fillOval(cx + r / 4 - 2, cy - r + 7, 7, 7);
        g.setColor(new Color(220, 50, 50));
        g.setStroke(new BasicStroke(2.5f));
        g.drawLine(cx - 6, cy + 2, cx + 6, cy + 11);
        g.drawLine(cx + 6, cy + 2, cx - 6, cy + 11);
        g.setStroke(new BasicStroke(1f));
    }

    private void drawEggplant(Graphics2D g, int cx, int cy, int r) {
        g.setColor(new Color(55, 5, 90));
        g.fillOval(cx - r * 3 / 4 + 1, cy - r + 1, r * 3 / 2 - 2, r * 2 - 2);
        g.setColor(new Color(110, 35, 160));
        g.fillOval(cx - r * 3 / 4, cy - r, r * 3 / 2, r * 2);
        g.setColor(new Color(170, 90, 220));
        g.fillOval(cx - r / 2, cy - r + 4, r / 3, r / 4);
        g.setColor(new Color(60, 130, 30));
        g.fillRoundRect(cx - 3, cy - r - 9, 7, 12, 3, 3);
        g.fillOval(cx - r / 2 + 2, cy - r - 5, r / 2, 9);
        g.fillOval(cx + 2, cy - r - 5, r / 2, 9);
    }

    private void drawGoldenApple(Graphics2D g, int cx, int cy, int r) {
        g.setColor(new Color(180, 130, 0));
        g.fillOval(cx - r, cy - r + 3, r * 2, r * 2 - 2);
        g.setColor(new Color(255, 210, 50));
        g.fillOval(cx - r + 2, cy - r + 5, r * 2 - 4, r - 4);
        g.setColor(new Color(255, 255, 200, 120));
        g.fillOval(cx - r / 2, cy - r / 2, r / 2, r / 3);
        float pulse = MathUtils.pulse(animTimer, 0.12f);
        g.setColor(new Color(255, 240, 100, (int)(60 + 60 * pulse)));
        g.fillOval(cx - r - 4, cy - r - 4, (r + 4) * 2, (r + 4) * 2);
        g.setColor(new Color(80, 50, 10));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(cx, cy - r + 3, cx + 3, cy - r - 6);
        g.setStroke(new BasicStroke(1f));
    }

    private void drawBomb(Graphics2D g, int cx, int cy, int r) {
        g.setColor(new Color(30, 30, 30));
        g.fillOval(cx - r, cy - r + 4, r * 2, r * 2 - 4);
        g.setColor(new Color(70, 70, 70));
        g.fillOval(cx - r + 3, cy - r + 6, r - 4, r - 6);
        g.setColor(new Color(120, 80, 20));
        g.setStroke(new BasicStroke(2.5f));
        g.drawLine(cx + r / 2, cy - r + 4, cx + r / 2 + 4, cy - r - 6);
        g.setStroke(new BasicStroke(1f));
        float pulse = MathUtils.pulse(animTimer, 0.2f);
        g.setColor(new Color(255, 120, 0, (int)(180 * pulse)));
        g.fillOval(cx + r / 2 + 2, cy - r - 8, 6, 6);
    }

    private void drawMystery(Graphics2D g, int cx, int cy, int r) {
        int hue = (animTimer * 3) % 360;
        g.setColor(Color.getHSBColor(hue / 360f, 0.8f, 0.6f));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(Color.getHSBColor(hue / 360f, 0.6f, 1f));
        g.fillOval(cx - r + 3, cy - r + 3, r * 2 - 6, r * 2 - 6);
        g.setFont(FontManager.getBold(r));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        g.drawString("?", cx - fm.stringWidth("?") / 2, cy + fm.getAscent() / 2 - 2);
    }

    private void drawRainbow(Graphics2D g, int cx, int cy, int r) {
        for (int i = 0; i < 6; i++) {
            float hue = (rainbowHue + i * 60) % 360 / 360f;
            g.setColor(Color.getHSBColor(hue, 1f, 1f));
            g.fillArc(cx - r + i, cy - r + i, (r - i) * 2, (r - i) * 2, 0, 360);
        }
        g.setColor(new Color(255, 255, 255, 180));
        g.fillOval(cx - r / 3, cy - r / 3, r / 2, r / 3);
    }

    private void drawFrozen(Graphics2D g, int cx, int cy, int r) {
        g.setColor(new Color(180, 230, 255));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(120, 200, 255));
        g.fillOval(cx - r + 3, cy - r + 3, r * 2 - 6, r * 2 - 6);
        g.setColor(new Color(200, 240, 255, 200));
        g.setStroke(new BasicStroke(2f));
        for (int i = 0; i < 6; i++) {
            double a = Math.toRadians(i * 60);
            g.drawLine(cx, cy, (int)(cx + Math.cos(a) * r), (int)(cy + Math.sin(a) * r));
        }
        g.setStroke(new BasicStroke(1f));
        g.setFont(FontManager.getEmoji(r));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String e = "\u2744";
        g.drawString(e, cx - fm.stringWidth(e) / 2, cy + fm.getAscent() / 2 - 2);
    }

    private void drawGiant(Graphics2D g, int cx, int cy, int r) {
        g.setColor(new Color(255, 180, 0));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(255, 220, 80));
        g.fillOval(cx - r + 4, cy - r + 4, r * 2 - 8, r - 6);
        g.setFont(FontManager.getEmoji(r));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String e = "\uD83C\uDF1F";
        g.drawString(e, cx - fm.stringWidth(e) / 2, cy + fm.getAscent() / 2 - 2);
    }

    private void drawTiny(Graphics2D g, int cx, int cy, int r) {
        g.setColor(new Color(255, 100, 200));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setColor(new Color(255, 180, 230));
        g.fillOval(cx - r + 2, cy - r + 2, r * 2 - 4, r - 2);
    }

    public BallType getType()         { return type; }
    public float    getSpeed()        { return speed; }
    public void     setSpeed(float s) { this.speed = s; }
}