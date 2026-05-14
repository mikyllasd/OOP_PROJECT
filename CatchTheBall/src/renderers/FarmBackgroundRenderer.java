package OOP_PROJECT.CatchTheBall.src.renderers;

import java.awt.*;
import java.awt.geom.*;

/**
 * Shared farm-scene background renderer.
 *
 * Matches the MainMenu look: sky gradient, animated sun, distant mountains,
 * rolling green hills, fence, barn, farmhouse, apple trees, and wildflowers.
 *
 * Usage (any Screen's draw method):
 *   FarmBackgroundRenderer.draw(g, GamePanel.W, GamePanel.H, tickCount);
 */
public final class FarmBackgroundRenderer {

    // ── palette (sampled from main-menu screenshot) ───────────────────────────
    private static final Color SKY_TOP      = new Color(0x5BB8E8);
    private static final Color SKY_BOT      = new Color(0xA8DCEF);
    private static final Color MTN_FAR      = new Color(0x8BAFC4);   // distant mountains
    private static final Color MTN_MID      = new Color(0x5E8C6A);   // mid hills
    private static final Color HILL_LIGHT   = new Color(0x5DB84A);   // bright hilltop
    private static final Color GRASS_TOP    = new Color(0x4CAF35);   // ground surface
    private static final Color GRASS_BOT    = new Color(0x2E7D1E);   // ground shadow
    private static final Color SUN_CORE     = new Color(0xFFD740);
    private static final Color SUN_GLOW     = new Color(0xFFE57A);

    // barn
    private static final Color BARN_WALL    = new Color(0x8B2020);
    private static final Color BARN_ROOF    = new Color(0x6B1515);
    private static final Color BARN_DOOR    = new Color(0x5A1510);
    private static final Color BARN_TRIM    = new Color(0xFFFFFF);

    // farmhouse
    private static final Color HOUSE_WALL   = new Color(0xF5DEB3);
    private static final Color HOUSE_ROOF   = new Color(0x8B2020);
    private static final Color HOUSE_WIN    = new Color(0xADD8E6);
    private static final Color HOUSE_TRIM   = new Color(0xDEB887);

    // tree / foliage
    private static final Color TREE_TRUNK   = new Color(0x6B4226);
    private static final Color LEAF_DARK    = new Color(0x2E7D1E);
    private static final Color LEAF_MID     = new Color(0x3E9C35);
    private static final Color LEAF_LIGHT   = new Color(0x5DB84A);
    private static final Color APPLE_RED    = new Color(0xE53935);

    // fence
    private static final Color FENCE_COL    = new Color(0xE8D8A8);
    private static final Color FENCE_SHADOW = new Color(0xC4B07A);

    // flower colours
    private static final Color[] FLOWER_COLS = {
        new Color(0xFF6B6B), new Color(0xFFD93D), new Color(0xC678DD),
        new Color(0xFF9F43), new Color(0xFF6BAA)
    };
    private static final Color FLOWER_CENTER = new Color(0xFFE57A);
    private static final Color STEM_GREEN    = new Color(0x4CAF35);

    // ── public entry point ────────────────────────────────────────────────────

    /**
     * Draws the complete farm background.
     *
     * @param g         active Graphics2D context
     * @param w         panel width  (GamePanel.W)
     * @param h         panel height (GamePanel.H)
     * @param tick      animation tick (use 0 for static screens)
     */
    public static void draw(Graphics2D g, int w, int h, int tick) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);

        int groundY = (int)(h * 0.76);   // horizon line

        drawSky(g2, w, h, groundY);
        drawSun(g2, w, tick);
        drawDistantMountains(g2, w, groundY);
        drawMidHills(g2, w, groundY);
        drawGround(g2, w, h, groundY);
        drawFence(g2, w, groundY);
        drawBarn(g2, (int)(w * 0.30), groundY);
        drawFarmhouse(g2, (int)(w * 0.68), groundY);
        drawAppleTree(g2, (int)(w * 0.09),  groundY, 0.85f);
        drawAppleTree(g2, (int)(w * 0.19),  groundY, 1.0f);
        drawAppleTree(g2, (int)(w * 0.78),  groundY, 0.90f);
        drawAppleTree(g2, (int)(w * 0.88),  groundY, 1.05f);
        drawAppleTree(g2, (int)(w * 0.96),  groundY, 0.80f);
        drawFlowerBed(g2, w, groundY, tick);
        drawGroundOverlay(g2, w, h, groundY);   // subtle shading on top of everything

        g2.dispose();
    }

    // ── sky ───────────────────────────────────────────────────────────────────

    private static void drawSky(Graphics2D g, int w, int h, int groundY) {
        g.setPaint(new GradientPaint(0, 0, SKY_TOP, 0, groundY, SKY_BOT));
        g.fillRect(0, 0, w, groundY + 10);
        g.setPaint(null);
    }

    // ── sun ───────────────────────────────────────────────────────────────────

    private static void drawSun(Graphics2D g, int w, int tick) {
        int sx = (int)(w * 0.86);
        int sy = (int)(w * 0.07);
        int r  = 44;

        // slow pulsing glow
        float pulse = (float)(Math.sin(tick * 0.03) * 0.15 + 0.85);
        int glowR = (int)(r * 1.55 * pulse);

        // outer glow ring
        g.setColor(new Color(255, 230, 100, 45));
        g.fillOval(sx - glowR, sy - glowR, glowR * 2, glowR * 2);
        g.setColor(new Color(255, 220, 80, 70));
        g.fillOval(sx - r - 10, sy - r - 10, (r + 10) * 2, (r + 10) * 2);

        // sun body
        g.setPaint(new RadialGradientPaint(
                new Point2D.Float(sx, sy), r,
                new float[]{ 0f, 0.6f, 1f },
                new Color[]{ Color.WHITE, SUN_CORE, SUN_GLOW }
        ));
        g.fillOval(sx - r, sy - r, r * 2, r * 2);
        g.setPaint(null);

        // rays
        g.setColor(new Color(255, 230, 80, 180));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int rays = 12;
        for (int i = 0; i < rays; i++) {
            double angle = Math.toRadians(i * 360.0 / rays + tick * 0.4);
            int x1 = (int)(sx + Math.cos(angle) * (r + 6));
            int y1 = (int)(sy + Math.sin(angle) * (r + 6));
            int x2 = (int)(sx + Math.cos(angle) * (r + 22));
            int y2 = (int)(sy + Math.sin(angle) * (r + 22));
            g.drawLine(x1, y1, x2, y2);
        }
        g.setStroke(new BasicStroke(1f));
    }

    // ── distant mountains ─────────────────────────────────────────────────────

    private static void drawDistantMountains(Graphics2D g, int w, int groundY) {
        // two overlapping mountain ranges
        drawMountainRange(g, w, groundY, MTN_FAR,
                new int[]{ 0, 80, 180, 280, 380, 480, 560, 660, 760, 880, w },
                new int[]{ groundY - 50, groundY - 140, groundY - 90,
                           groundY - 170, groundY - 110, groundY - 155,
                           groundY - 95,  groundY - 165, groundY - 100,
                           groundY - 140, groundY - 50 });

        drawMountainRange(g, w, groundY, new Color(0x6E9E7B),
                new int[]{ 0, 120, 230, 320, 440, 550, 650, 780, 900, w },
                new int[]{ groundY - 30, groundY - 105, groundY - 65,
                           groundY - 130, groundY - 75, groundY - 120,
                           groundY - 60,  groundY - 115, groundY - 45,
                           groundY - 30 });
    }

    private static void drawMountainRange(Graphics2D g, int w, int groundY,
                                           Color col, int[] xs, int[] ys) {
        int n = xs.length;
        int[] fullXs = new int[n + 2];
        int[] fullYs = new int[n + 2];
        fullXs[0] = 0; fullYs[0] = groundY + 5;
        System.arraycopy(xs, 0, fullXs, 1, n);
        System.arraycopy(ys, 0, fullYs, 1, n);
        fullXs[n + 1] = w; fullYs[n + 1] = groundY + 5;

        g.setColor(col);
        g.fillPolygon(fullXs, fullYs, n + 2);
    }

    // ── mid rolling hills ─────────────────────────────────────────────────────

    private static void drawMidHills(Graphics2D g, int w, int groundY) {
        // large sweeping hill arcs that sit just above the ground line
        g.setPaint(new GradientPaint(0, groundY - 80, HILL_LIGHT, 0, groundY, MTN_MID));
        GeneralPath hills = new GeneralPath();
        hills.moveTo(0, groundY + 5);
        hills.curveTo(w * 0.1, groundY - 60, w * 0.25, groundY - 80, w * 0.35, groundY - 30);
        hills.curveTo(w * 0.45, groundY + 10, w * 0.55, groundY - 50, w * 0.65, groundY - 40);
        hills.curveTo(w * 0.75, groundY - 30, w * 0.85, groundY - 65, w, groundY - 20);
        hills.lineTo(w, groundY + 5);
        hills.closePath();
        g.fill(hills);
        g.setPaint(null);
    }

    // ── flat ground ───────────────────────────────────────────────────────────

    private static void drawGround(Graphics2D g, int w, int h, int groundY) {
        g.setPaint(new GradientPaint(0, groundY, GRASS_TOP, 0, h, GRASS_BOT));
        g.fillRect(0, groundY, w, h - groundY);
        g.setPaint(null);
    }

    private static void drawGroundOverlay(Graphics2D g, int w, int h, int groundY) {
        // very subtle dark strip right at horizon for depth
        g.setPaint(new GradientPaint(0, groundY, new Color(0, 0, 0, 40),
                0, groundY + 18, new Color(0, 0, 0, 0)));
        g.fillRect(0, groundY, w, 18);
        g.setPaint(null);
    }

    // ── fence ─────────────────────────────────────────────────────────────────

    private static void drawFence(Graphics2D g, int w, int groundY) {
        int y  = groundY - 20;
        int fh = 36;   // fence height
        int postW = 8;
        int gap   = 48;

        for (int x = 0; x < w; x += gap) {
            // post shadow
            g.setColor(FENCE_SHADOW);
            g.fillRoundRect(x + 3, y + 3, postW, fh, 3, 3);
            // post
            g.setColor(FENCE_COL);
            g.fillRoundRect(x, y, postW, fh, 3, 3);
            // post cap
            g.setColor(Color.WHITE);
            g.fillRoundRect(x - 1, y - 4, postW + 2, 6, 3, 3);
        }

        // two horizontal rails
        int[] railYs = { y + 8, y + 22 };
        for (int ry : railYs) {
            g.setColor(FENCE_SHADOW);
            g.fillRoundRect(0, ry + 2, w, 5, 2, 2);
            g.setColor(FENCE_COL);
            g.fillRoundRect(0, ry, w, 5, 2, 2);
        }
    }

    // ── barn ──────────────────────────────────────────────────────────────────

    private static void drawBarn(Graphics2D g, int cx, int groundY) {
        int bw = 130, bh = 110;
        int bx = cx - bw / 2;
        int by = groundY - bh;

        // main wall
        g.setPaint(new GradientPaint(bx, by, BARN_WALL, bx + bw, by + bh,
                BARN_WALL.darker()));
        g.fillRect(bx, by, bw, bh);
        g.setPaint(null);

        // vertical board lines
        g.setColor(new Color(0, 0, 0, 30));
        g.setStroke(new BasicStroke(1.5f));
        for (int lx = bx + 18; lx < bx + bw; lx += 18) {
            g.drawLine(lx, by, lx, by + bh);
        }
        g.setStroke(new BasicStroke(1f));

        // roof (triangle + overhang)
        int roofH = 55;
        int[] rx = { bx - 12, cx, bx + bw + 12 };
        int[] ry = { by, by - roofH, by };
        g.setColor(BARN_ROOF);
        g.fillPolygon(rx, ry, 3);
        // roof shading
        g.setColor(new Color(0, 0, 0, 35));
        int[] shadeX = { cx, bx + bw + 12, bx + bw + 12 - 10 };
        int[] shadeY = { by - roofH, by, by - 10 };
        g.fillPolygon(shadeX, shadeY, 3);

        // roof ridge
        g.setColor(new Color(80, 10, 10));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(bx - 12, by, cx, by - roofH);
        g.drawLine(cx, by - roofH, bx + bw + 12, by);
        g.setStroke(new BasicStroke(1f));

        // chimney
        g.setColor(new Color(0x8B5E3C));
        g.fillRect(cx + 20, by - roofH + 15, 16, 30);
        g.setColor(new Color(0x6B4226));
        g.fillRect(cx + 18, by - roofH + 12, 20, 6);

        // big door (double)
        int dw = 50, dh = 65;
        int dx = cx - dw / 2, dy = by + bh - dh;
        g.setColor(BARN_DOOR);
        g.fillRect(dx, dy, dw, dh);
        // door frame
        g.setColor(BARN_TRIM);
        g.setStroke(new BasicStroke(2f));
        g.drawRect(dx, dy, dw, dh);
        // door split line
        g.drawLine(cx, dy, cx, dy + dh);
        g.setStroke(new BasicStroke(1f));
        // door arch
        g.setColor(BARN_DOOR.darker());
        g.fillArc(dx, dy - 12, dw, 24, 0, 180);
        g.setColor(BARN_TRIM);
        g.setStroke(new BasicStroke(1.5f));
        g.drawArc(dx, dy - 12, dw, 24, 0, 180);
        g.setStroke(new BasicStroke(1f));

        // window
        int wx = bx + 14, wy = by + 18;
        g.setColor(new Color(0xADD8E6, false));
        g.fillRoundRect(wx, wy, 24, 22, 4, 4);
        g.setColor(BARN_TRIM);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(wx, wy, 24, 22, 4, 4);
        g.drawLine(wx + 12, wy, wx + 12, wy + 22);
        g.drawLine(wx, wy + 11, wx + 24, wy + 11);
        g.setStroke(new BasicStroke(1f));

        // "BARN" sign
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        FontMetrics fm = g.getFontMetrics();
        String sign = "BARN";
        int signX = cx - fm.stringWidth(sign) / 2;
        g.setColor(new Color(255, 255, 255, 200));
        g.drawString(sign, signX, by + 30);
    }

    // ── farmhouse ─────────────────────────────────────────────────────────────

    private static void drawFarmhouse(Graphics2D g, int cx, int groundY) {
        int hw = 115, hh = 95;
        int hx = cx - hw / 2;
        int hy = groundY - hh;

        // wall
        g.setPaint(new GradientPaint(hx, hy, HOUSE_WALL, hx + hw, hy + hh,
                new Color(0xDEC898)));
        g.fillRect(hx, hy, hw, hh);
        g.setPaint(null);

        // wall shadow (right side)
        g.setColor(new Color(0, 0, 0, 22));
        g.fillRect(hx + hw - 18, hy, 18, hh);

        // roof
        int roofH = 48;
        int[] rx = { hx - 10, cx, hx + hw + 10 };
        int[] ry = { hy, hy - roofH, hy };
        g.setColor(HOUSE_ROOF);
        g.fillPolygon(rx, ry, 3);
        // roof shadow
        g.setColor(new Color(0, 0, 0, 28));
        int[] shadeX = { cx, hx + hw + 10, hx + hw + 10 - 8 };
        int[] shadeY = { hy - roofH, hy, hy - 10 };
        g.fillPolygon(shadeX, shadeY, 3);

        // chimney
        g.setColor(new Color(0xB07850));
        g.fillRect(cx - 30, hy - roofH + 8, 14, 28);
        g.setColor(new Color(0x8B5E3C));
        g.fillRect(cx - 32, hy - roofH + 5, 18, 5);

        // front door
        int dw = 28, dh = 48;
        int dx = cx - dw / 2, dy = hy + hh - dh;
        g.setPaint(new GradientPaint(dx, dy, new Color(0x6B4226), dx + dw, dy + dh,
                new Color(0x4A2C18)));
        g.fillRoundRect(dx, dy, dw, dh, 4, 4);
        g.setPaint(null);
        g.setColor(HOUSE_TRIM);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(dx, dy, dw, dh, 4, 4);
        // door knob
        g.setColor(new Color(0xFFD740));
        g.fillOval(dx + dw - 8, dy + dh / 2, 5, 5);
        // door arch
        g.setColor(new Color(0x5A3518));
        g.fillArc(dx, dy - 10, dw, 20, 0, 180);
        g.setColor(HOUSE_TRIM);
        g.drawArc(dx, dy - 10, dw, 20, 0, 180);
        g.setStroke(new BasicStroke(1f));

        // two windows
        int[][] wins = { {hx + 10, hy + 14}, {hx + hw - 38, hy + 14} };
        for (int[] wn : wins) {
            g.setColor(new Color(0xADD8E6));
            g.fillRoundRect(wn[0], wn[1], 28, 26, 4, 4);
            g.setColor(Color.WHITE);
            g.setStroke(new BasicStroke(1.5f));
            g.drawRoundRect(wn[0], wn[1], 28, 26, 4, 4);
            g.drawLine(wn[0] + 14, wn[1], wn[0] + 14, wn[1] + 26);
            g.drawLine(wn[0], wn[1] + 13, wn[0] + 28, wn[1] + 13);
            g.setStroke(new BasicStroke(1f));
        }

        // porch overhang
        g.setColor(HOUSE_TRIM);
        g.fillRect(dx - 14, dy - 8, dw + 28, 6);
        g.setColor(new Color(0xC4A87A));
        g.fillRect(dx - 10, dy - 2, 6, dh + 2);
        g.fillRect(dx + dw + 4, dy - 2, 6, dh + 2);
    }

    // ── apple tree ────────────────────────────────────────────────────────────

    private static void drawAppleTree(Graphics2D g, int cx, int groundY, float scale) {
        int trunkH = (int)(55 * scale);
        int trunkW = (int)(14 * scale);
        int canopyR = (int)(38 * scale);
        int tx = cx - trunkW / 2;
        int ty = groundY - trunkH;

        // trunk shadow
        g.setColor(new Color(0, 0, 0, 30));
        g.fillRoundRect(tx + 3, ty + 3, trunkW, trunkH, 4, 4);
        // trunk
        g.setPaint(new GradientPaint(tx, ty, TREE_TRUNK, tx + trunkW, ty + trunkH,
                TREE_TRUNK.darker()));
        g.fillRoundRect(tx, ty, trunkW, trunkH, 4, 4);
        g.setPaint(null);

        // canopy (three overlapping circles for volume)
        int canopyY = ty - canopyR + 10;
        drawLeafCircle(g, cx - (int)(canopyR * 0.45), canopyY + (int)(canopyR * 0.3), canopyR, scale);
        drawLeafCircle(g, cx + (int)(canopyR * 0.45), canopyY + (int)(canopyR * 0.3), canopyR, scale);
        drawLeafCircle(g, cx,                          canopyY - (int)(canopyR * 0.1), (int)(canopyR * 1.1), scale);

        // apples
        int appleR = Math.max(4, (int)(6 * scale));
        int[][] applePos = {
            { cx - (int)(canopyR * 0.4), canopyY + (int)(canopyR * 0.4) },
            { cx + (int)(canopyR * 0.35),canopyY + (int)(canopyR * 0.45) },
            { cx - (int)(canopyR * 0.1), canopyY + (int)(canopyR * 0.5) },
            { cx + (int)(canopyR * 0.15),canopyY + (int)(canopyR * 0.2) },
        };
        for (int[] ap : applePos) {
            g.setColor(new Color(0, 0, 0, 25));
            g.fillOval(ap[0] - appleR + 2, ap[1] - appleR + 2, appleR * 2, appleR * 2);
            g.setPaint(new RadialGradientPaint(
                    new Point2D.Float(ap[0] - appleR / 3f, ap[1] - appleR / 3f),
                    appleR,
                    new float[]{ 0f, 1f },
                    new Color[]{ new Color(0xFF6B6B), APPLE_RED }));
            g.fillOval(ap[0] - appleR, ap[1] - appleR, appleR * 2, appleR * 2);
            g.setPaint(null);
            // stem
            g.setColor(TREE_TRUNK);
            g.drawLine(ap[0], ap[1] - appleR, ap[0], ap[1] - appleR - 4);
        }
    }

    private static void drawLeafCircle(Graphics2D g, int cx, int cy, int r, float scale) {
        // shadow
        g.setColor(new Color(0, 0, 0, 22));
        g.fillOval(cx - r + 3, cy - r + 3, r * 2, r * 2);
        // main fill
        g.setPaint(new RadialGradientPaint(
                new Point2D.Float(cx - r * 0.2f, cy - r * 0.25f), r,
                new float[]{ 0f, 0.55f, 1f },
                new Color[]{ LEAF_LIGHT, LEAF_MID, LEAF_DARK }));
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        g.setPaint(null);
    }

    // ── wildflower bed ────────────────────────────────────────────────────────

    private static void drawFlowerBed(Graphics2D g, int w, int groundY, int tick) {
        // dense band of flowers along the ground line
        int flowerY  = groundY + 4;
        int spacing  = 28;
        int colCount = FLOWER_COLS.length;

        for (int x = 20; x < w - 20; x += spacing) {
            // slight vertical stagger for a natural look
            int yOff = (int)(Math.sin(x * 0.3) * 5);
            // sway animation
            float sway = (float)(Math.sin(tick * 0.04 + x * 0.07) * 1.5);
            int colorIdx = (x / spacing) % colCount;
            drawFlower(g, x, flowerY + yOff, 9, FLOWER_COLS[colorIdx], sway);
        }
    }

    private static void drawFlower(Graphics2D g, int cx, int cy,
                                    int r, Color petCol, float sway) {
        // stem with sway
        g.setColor(STEM_GREEN);
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(cx, cy + r + 8, (int)(cx + sway), cy + r);
        g.setStroke(new BasicStroke(1f));

        // petals (5-petal radial)
        g.setColor(petCol);
        int petR = r;
        for (int i = 0; i < 5; i++) {
            double angle = Math.toRadians(i * 72 + sway * 4);
            int px = (int)(cx + Math.cos(angle) * petR);
            int py = (int)(cy + Math.sin(angle) * petR);
            g.fillOval(px - petR / 2, py - petR / 2, petR, petR);
        }

        // centre
        g.setColor(FLOWER_CENTER);
        g.fillOval(cx - r / 2, cy - r / 2, r, r);
        g.setColor(new Color(255, 200, 50));
        g.fillOval(cx - r / 3, cy - r / 3, r * 2 / 3, r * 2 / 3);
    }

    private FarmBackgroundRenderer() {}
}