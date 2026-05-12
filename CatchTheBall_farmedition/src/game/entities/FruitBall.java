package game.entities;

import java.awt.*;
import java.awt.geom.*;

/**
 * A falling fruit/item drawn entirely with filled rectangles,
 * matching the pixel-art style of Character.drawPixelBody().
 *
 * Each fruit uses a palette of (dark / mid / highlight / shine) colours,
 * and the circular body is built row-by-row exactly like the character's
 * head and limbs.
 */
public class FruitBall extends Entity {

    private final BallType type;

    // Physics
    private float vy;

    // Animation
    private int   bobTimer;
    private int   sparkleTimer;   // rare shimmer cycle
    private float warningPulse;   // bad-item red glow phase
    private int   catchFlashTimer;
    private float squishX, squishY;

    // â”€â”€ Constructor â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public FruitBall(float x, float y, BallType type) {
        super(x, y, radius(type) * 2, radius(type) * 2);
        this.type      = type;
        this.vy        = 1.8f + (float)(Math.random() * 1.8f);
        this.squishX   = 1f;
        this.squishY   = 1f;
        this.bobTimer  = (int)(Math.random() * 60);
    }

    // â”€â”€ Entity overrides â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Override
    public void setTarget(float tx, float ty) { /* not used for balls */ }

    @Override
    public void update() {
        y += vy;
        bobTimer++;
        sparkleTimer++;
        warningPulse = (float)(Math.sin(bobTimer * 0.12) * 0.5 + 0.5);

        if (catchFlashTimer > 0) {
            catchFlashTimer--;
            float t = catchFlashTimer / 10f;
            squishX = 1.25f - 0.25f * t;
            squishY = 0.75f + 0.25f * t;
        } else {
            squishX += (1f - squishX) * 0.2f;
            squishY += (1f - squishY) * 0.2f;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,    RenderingHints.VALUE_RENDER_SPEED);

        int r  = radius(type);
        int cx = (int)(x + r);
        int cy = (int)(y + r);

        // Shadow
        drawShadow(g2, cx, (int)(y + r * 2 + 3), r);

        // Bad-item warning glow (pixel border)
        if (type.isBad()) {
            int alpha = (int)(warningPulse * 80 + 30);
            g2.setColor(new Color(200, 20, 20, alpha));
            g2.fillRect(cx - r - 4, cy - r - 4, (r + 4) * 2, (r + 4) * 2);
        }

        // Rare sparkles (before body so they sit behind)
        if (type.isRare()) drawSparkles(g2, cx, cy, r);

        // Squish pivot at centre
        g2.translate(cx, cy);
        g2.scale(squishX, squishY);
        g2.translate(-cx, -cy);

        // Pixel body
        switch (type) {
            case APPLE:      drawApple(g2, cx, cy, r);      break;
            case ORANGE:     drawOrange(g2, cx, cy, r);     break;
            case STRAWBERRY: drawStrawberry(g2, cx, cy, r); break;
            case MUSHROOM:   drawMushroom(g2, cx, cy, r);   break;
            case EGGPLANT:   drawEggplant(g2, cx, cy, r);   break;
        }

        g2.dispose();
    }

    // â”€â”€ Fruit pixel bodies â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private void drawApple(Graphics2D g2, int cx, int cy, int r) {
        // Body: row-by-row circle
        for (int row = -r; row <= r; row++) {
            int rw = circleRowWidth(r, row);
            if (rw <= 0) continue;
            float t = (float)(row + r) / (r * 2f);
            Color c = t < 0.35f ? new Color(0xFF7070) :
                      t < 0.70f ? new Color(0xEE4040) :
                                  new Color(0xAA1010);
            px(g2, c, cx - rw / 2, cy + row, rw, 1);
        }
        // Shine patch
        px(g2, new Color(0xFFCCCC), cx - r / 2,     cy - r / 2,     r / 3,     r / 5);
        px(g2, new Color(0xFFEEEE), cx - r / 2 + 1, cy - r / 2 + 1, r / 5,     r / 8);
        // Dark right edge
        for (int row = -r / 2; row <= r / 2; row++) {
            px(g2, new Color(0x881010), cx + r / 2, cy + row, r / 6, 1);
        }
        // Stem
        px(g2, new Color(0x5A3010), cx - 1, cy - r - 5, 3, 6);
        // Leaf
        px(g2, new Color(0x28A028), cx + 1, cy - r - 4, 7, 4);
        px(g2, new Color(0x50C050), cx + 2, cy - r - 4, 5, 2);
        px(g2, new Color(0x28A028), cx + 1, cy - r - 2, 3, 2); // leaf base curve
        // Outline pixel border
        drawCircleOutline(g2, cx, cy, r, new Color(0x881010, true));
    }

    private void drawOrange(Graphics2D g2, int cx, int cy, int r) {
        for (int row = -r; row <= r; row++) {
            int rw = circleRowWidth(r, row);
            if (rw <= 0) continue;
            float t = (float)(row + r) / (r * 2f);
            Color c = t < 0.35f ? new Color(0xFFAA50) :
                      t < 0.70f ? new Color(0xF08020) :
                                  new Color(0xB04010);
            px(g2, c, cx - rw / 2, cy + row, rw, 1);
        }
        // Texture vertical lines
        g2.setColor(new Color(0, 0, 0, 30));
        for (int i = -1; i <= 1; i++) {
            int lx = cx + i * (r / 3);
            g2.drawLine(lx, cy - r * 2 / 3, lx, cy + r * 2 / 3);
        }
        // Shine
        px(g2, new Color(0xFFCC90), cx - r / 2,     cy - r / 2,     r / 3, r / 5);
        px(g2, new Color(0xFFEECC), cx - r / 2 + 1, cy - r / 2 + 1, r / 5, r / 8);
        // Dark right
        for (int row = -r / 2; row <= r / 2; row++) {
            px(g2, new Color(0xA03800), cx + r / 2, cy + row, r / 6, 1);
        }
        // Stem + leaf
        px(g2, new Color(0x5A3010), cx - 1, cy - r - 4, 3, 5);
        px(g2, new Color(0x30A030), cx + 1, cy - r - 3, 6, 4);
        px(g2, new Color(0x50C050), cx + 2, cy - r - 3, 4, 2);
        drawCircleOutline(g2, cx, cy, r, new Color(0xA03800, true));
    }

    private void drawStrawberry(Graphics2D g2, int cx, int cy, int r) {
        int h = (int)(r * 2.1f);
        // Body: tapered pixel shape (wide at top, pointed bottom)
        for (int row = 0; row < h; row++) {
            float t = (float) row / h;
            int rw;
            if (t < 0.35f)      rw = (int)(r * 2f * (t / 0.35f));
            else                 rw = (int)(r * 2f * (1f - (t - 0.35f) / 0.65f));
            rw = Math.max(rw, 0);
            Color c = t < 0.5f ? new Color(0xEE2040) : new Color(0xCC1030);
            px(g2, c, cx - rw / 2, cy - r + row, rw, 1);
        }
        // Seeds (tiny yellow pixels)
        g2.setColor(new Color(0xFFEE88));
        int[][] seeds = {{-4,-5},{3,-6},{7,-1},{-6,2},{1,2},{5,5},{-3,8},{3,10},{-7,6},{1,-2},{6,8}};
        for (int[] s : seeds) {
            int sx = cx + s[0], sy = cy + s[1];
            int dist = (int) Math.sqrt(s[0] * s[0] + s[1] * s[1]);
            if (dist < r + 4) px(g2, new Color(0xFFEE88), sx - 1, sy - 1, 2, 2);
        }
        // Shine
        px(g2, new Color(0xFFAAAA), cx - r / 3, cy - r / 2, r / 4, r / 6);
        // Calyx leaves
        px(g2, new Color(0x40AA20), cx - 7, cy - r - 4, 6, 6);
        px(g2, new Color(0x40AA20), cx + 2, cy - r - 4, 6, 6);
        px(g2, new Color(0x40AA20), cx - 3, cy - r - 7, 5, 7);
        px(g2, new Color(0x70CC40), cx - 6, cy - r - 3, 4, 3);
        px(g2, new Color(0x70CC40), cx + 3, cy - r - 3, 4, 3);
        // Outline (dark red)
        g2.setColor(new Color(0x880020));
        g2.setStroke(new BasicStroke(1f));
        // simple bounding hint at widest point
        g2.drawLine(cx - r, cy, cx + r, cy);
    }

    private void drawMushroom(Graphics2D g2, int cx, int cy, int r) {
        // Stem
        int sw = (int)(r * 0.9f);
        px(g2, new Color(0xC8C0A0), cx - sw / 2,     cy,          sw,         r + 3);
        px(g2, new Color(0xE8E0B8), cx - sw / 2 + 2, cy + 2,      sw / 2,     r - 2);
        px(g2, new Color(0x908060), cx + sw / 5,      cy + 2,      sw / 4,     r - 2);

        // Cap: semi-circle, row by row
        for (int row = -r; row <= 0; row++) {
            int rw = circleRowWidth((int)(r * 1.2f), row);
            if (rw <= 0) continue;
            float t = (float)(-row) / r;
            Color c = t > 0.7f ? new Color(0xCC3020) : new Color(0xAA2010);
            px(g2, c, cx - rw / 2, cy + row, rw, 1);
        }
        // Cap underside rim
        px(g2, new Color(0xF0E0C8), cx - sw / 2 - 4, cy,     sw + 8, 4);
        px(g2, new Color(0xD8C8A0), cx - sw / 2 - 4, cy + 3, sw + 8, 2);

        // White spots â€” each is a small pixel rectangle with highlight
        int[][] spotPos = {{0, (int)(-r * 0.6f)}, {(int)(r * 0.5f), (int)(-r * 0.3f)}, {-(int)(r * 0.5f), (int)(-r * 0.3f)}};
        for (int[] sp : spotPos) {
            px(g2, new Color(0xFFE8D0), cx + sp[0] - 4, cy + sp[1] - 4, 8, 8);
            px(g2, new Color(0xFFFAF4), cx + sp[0] - 2, cy + sp[1] - 2, 4, 4);
        }

        // Outline
        g2.setColor(new Color(0x601010));
        g2.setStroke(new BasicStroke(1f));
        g2.drawArc(cx - (int)(r * 1.2f), cy - r, (int)(r * 2.4f), r * 2, 0, 180);
    }

    private void drawEggplant(Graphics2D g2, int cx, int cy, int r) {
        int h = (int)(r * 2.3f);
        int topY = cy - (int)(r * 1.1f);

        // Body: oval/teardrop shape, narrow at top, wide in middle, taper at bottom
        for (int row = 0; row < h; row++) {
            float t = (float) row / h;
            int rw;
            if (t < 0.15f)      rw = (int)(r * 0.7f * (t / 0.15f));
            else if (t < 0.55f) rw = (int)(r * 0.7f + (r * 1.3f - r * 0.7f) * ((t - 0.15f) / 0.4f));
            else                 rw = (int)(r * 1.3f * (1f - (t - 0.55f) / 0.55f));
            rw = Math.max(rw, 0);
            Color c = t < 0.5f ? new Color(0x7030A8) : new Color(0x5A1E90);
            px(g2, c, cx - rw / 2, topY + row, rw, 1);
        }
        // Shine strip
        for (int row = (int)(h * 0.1f); row < (int)(h * 0.6f); row++) {
            float t = (float) row / h;
            int rw = (int)(r * 0.25f);
            px(g2, new Color(0xA858E0), cx - r / 2, topY + row, rw, 1);
        }
        // Calyx
        px(g2, new Color(0x40AA20), cx - 7, topY - 4, 6, 7);
        px(g2, new Color(0x40AA20), cx + 2, topY - 4, 6, 7);
        px(g2, new Color(0x40AA20), cx - 3, topY - 7, 5, 7);
        px(g2, new Color(0x70CC40), cx - 6, topY - 3, 4, 4);
        px(g2, new Color(0x70CC40), cx + 3, topY - 3, 4, 4);
        // Outline
        g2.setColor(new Color(0x3A0E60));
        g2.setStroke(new BasicStroke(1f));
        g2.drawOval(cx - r, topY + (int)(h * 0.15f), r * 2, (int)(h * 0.7f));
    }

    // â”€â”€ Effects â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    private void drawShadow(Graphics2D g2, int cx, int groundY, int r) {
        int sw = (int)(r * 1.6f);
        int sh = (int)(r * 0.4f);
        g2.setColor(new Color(0, 0, 0, 45));
        g2.fillOval(cx - sw / 2, groundY - sh / 2, sw, sh);
    }

    private void drawSparkles(Graphics2D g2, int cx, int cy, int r) {
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int count  = 6;
        float rad  = r + 10 + (float)(Math.sin(sparkleTimer * 0.05f) * 4);
        int   alpha = 200;
        for (int i = 0; i < count; i++) {
            double angle = i * Math.PI * 2 / count + sparkleTimer * 0.04;
            int sx = (int)(cx + Math.cos(angle) * rad);
            int sy = (int)(cy + Math.sin(angle) * rad * 0.6f);
            int ex = (int)(cx + Math.cos(angle) * (rad + 6));
            int ey = (int)(cy + Math.sin(angle) * (rad + 6) * 0.6f);
            g2.setColor(new Color(255, 240, 60, alpha));
            g2.drawLine(sx, sy, ex, ey);
            px(g2, new Color(255, 255, 180, alpha), sx - 1, sy - 1, 3, 3);
        }
    }

    private void drawCircleOutline(Graphics2D g2, int cx, int cy, int r, Color c) {
        // Pixel-art style outline: darken the outermost row of each circle row
        g2.setColor(c);
        for (int row = -r; row <= r; row++) {
            int rw = circleRowWidth(r, row);
            if (rw <= 0) continue;
            // left and right edge pixels only
            g2.fillRect(cx - rw / 2, cy + row, 1, 1);
            g2.fillRect(cx + rw / 2 - 1, cy + row, 1, 1);
        }
    }

    // â”€â”€ Utility â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    /**
     * Returns the pixel width of a circle row at the given row offset from centre.
     * Same maths used in Character's head drawing, just extracted here.
     */
    private static int circleRowWidth(int r, int row) {
        return (int)(Math.sqrt(Math.max(0, (double)r * r - (double)row * row)) * 2);
    }

    private void px(Graphics2D g2, Color c, int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) return;
        g2.setColor(c);
        g2.fillRect(x, y, w, h);
    }

    public static int radius(BallType t) {
        return t.isRare() ? 20 : 16;
    }

    // â”€â”€ Public API â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€

    public BallType getType()            { return type; }
    public float    getVy()              { return vy;   }
    public void     setVy(float vy)      { this.vy = vy; }
    public void     triggerCatch()       { catchFlashTimer = 10; }
    public boolean  isOffScreen(int h)   { return y > h + height + 20; }
}
