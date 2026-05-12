package OOP_PROJECT.CatchTheBall.src.renderers;

import java.awt.*;
import java.awt.geom.*;

/**
 * Renders an animated farm-scene background for the main menu.
 * Drop-in call:  MenuBackgroundRenderer.draw(g, tickCount, screenW, screenH);
 *
 * The scene matches the in-game BackgroundRenderer aesthetic:
 *   sky gradient → sun → clouds → mountains → forest line →
 *   rice field · barn · farmhouse · windmill · trees · flowers → ground
 */
public class MenuBackgroundRenderer {

    // ── public API ────────────────────────────────────────────────────────────

    public static void draw(Graphics2D g, int tick, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);

        drawSky(g2, tick, w, h);
        drawSun(g2, tick, w);
        drawClouds(g2, tick, w);
        drawMountains(g2, w, h);
        drawForestLine(g2, tick, w, h);
        drawFarmScene(g2, tick, w, h);
        drawGround(g2, tick, w, h);

        g2.dispose();
    }

    // ── sky ───────────────────────────────────────────────────────────────────

    private static void drawSky(Graphics2D g, int tick, int w, int h) {
        int groundY = h - 100;

        // Bright sunny daytime sky — no dark golden-hour shift
        Color top = new Color(30, 120, 220);   // deep sky blue
        Color mid = new Color(85, 185, 255);   // lighter blue
        Color bot = new Color(178, 228, 255);  // pale horizon blue

        GradientPaint upper = new GradientPaint(0, 0, top, 0, groundY / 2f, mid);
        g.setPaint(upper);
        g.fillRect(0, 0, w, groundY / 2);

        GradientPaint lower = new GradientPaint(0, groundY / 2f, mid, 0, groundY, bot);
        g.setPaint(lower);
        g.fillRect(0, groundY / 2, w, groundY / 2 + 4);
        g.setPaint(null);
    }

    // ── sun ───────────────────────────────────────────────────────────────────

    private static void drawSun(Graphics2D g, int tick, int w) {
        // sun drifts slightly on the x-axis
        int sx = (int)(w * 0.82 + Math.sin(tick * 0.002) * 12);
        int sy = 55;
        int r  = 26;

        // glow halos
        float[] alphas = {12f, 20f, 32f, 50f};
        int[]   pads   = {40,  28,  18,  8};
        for (int i = 0; i < alphas.length; i++) {
            g.setColor(new Color(255, 225, 100, (int) alphas[i]));
            int d = (r + pads[i]) * 2;
            g.fillOval(sx - pads[i], sy - pads[i], d, d);
        }

        // core disc
        GradientPaint disc = new GradientPaint(
                sx, sy, new Color(255, 252, 210),
                sx + r, sy + r, new Color(255, 185, 22));
        g.setPaint(disc);
        g.fillOval(sx, sy, r * 2, r * 2);
        g.setPaint(null);

        // specular
        g.setColor(new Color(255, 255, 255, 115));
        g.fillOval(sx + 5, sy + 5, r / 2, r / 3);

        // animated rays
        g.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (int a = 0; a < 14; a++) {
            double angle = Math.toRadians(a * (360.0 / 14) + tick * 0.2);
            int inner = r + 5;
            int outer = r + 16 + (a % 2) * 8;
            g.setColor(new Color(255, 240, 110, 90));
            g.drawLine(
                (int)(sx + r + Math.cos(angle) * inner),
                (int)(sy + r + Math.sin(angle) * inner),
                (int)(sx + r + Math.cos(angle) * outer),
                (int)(sy + r + Math.sin(angle) * outer));
        }
        g.setStroke(new BasicStroke(1f));
    }

    // ── clouds ────────────────────────────────────────────────────────────────

    private static void drawClouds(Graphics2D g, int tick, int w) {
        // layer 1 – large, medium speed
        int off1 = (int)(tick * 0.3) % (w + 280);
        drawCloud(g, w + 60  - off1, 42,  120, 42, 210);
        drawCloud(g, w + 290 - off1, 30,   82, 30, 205);
        drawCloud(g, w + 510 - off1, 55,  140, 50, 215);
        drawCloud(g, w + 750 - off1, 38,   90, 34, 208);

        // layer 2 – smaller, slower
        int off2 = (int)(tick * 0.14) % (w + 350);
        drawCloud(g, w + 130 - off2, 85,   65, 22, 155);
        drawCloud(g, w + 400 - off2, 70,   88, 28, 148);
        drawCloud(g, w + 640 - off2, 92,   55, 18, 135);
        drawCloud(g, w + 860 - off2, 80,   72, 24, 145);

        // layer 3 – thin wispy, very slow
        int off3 = (int)(tick * 0.06) % (w + 400);
        g.setColor(new Color(255, 255, 255, 55));
        drawWisp(g, w + 80  - off3, 108, 180, 12);
        drawWisp(g, w + 380 - off3, 118, 220, 10);
        drawWisp(g, w + 650 - off3, 100, 160, 14);
    }

    private static void drawCloud(Graphics2D g, int x, int y, int cw, int ch, int alpha) {
        g.setColor(new Color(255, 255, 255, alpha));
        g.fillOval(x,            y,           cw,       ch);
        g.fillOval(x + cw / 4,   y - ch / 3,  cw / 2,   ch);
        g.fillOval(x + cw * 3/5, y - ch / 4,  cw * 2/5, ch - 4);
        g.fillOval(x + cw / 6,   y + ch / 4,  cw * 2/3, ch * 2/3);
        // subtle underside shadow
        g.setColor(new Color(150, 165, 200, alpha / 14));
        g.fillOval(x + cw / 8, y + ch * 2/3, cw * 3/4, ch / 3);
    }

    private static void drawWisp(Graphics2D g, int x, int y, int len, int h) {
        g.fillRoundRect(x, y, len, h, h, h);
        g.fillRoundRect(x + len / 5, y - h / 2, len * 3/5, h, h, h);
    }

    // ── mountains ─────────────────────────────────────────────────────────────

    private static void drawMountains(Graphics2D g, int w, int h) {
        int groundY = h - 100;

        // far range – cool hazy blue
        g.setColor(new Color(105, 148, 192, 85));
        int[] mx1 = {0,    60,  145, 240, 335, 440, 545, 650, 755, 860, w, w, 0};
        int[] my1 = {groundY-170, groundY-272, groundY-210, groundY-315,
                     groundY-255, groundY-295, groundY-225, groundY-285,
                     groundY-240, groundY-198, groundY-188, groundY-90, groundY-90};
        g.fillPolygon(mx1, my1, 13);

        // snow caps
        g.setColor(new Color(235, 248, 255, 115));
        int[] sx = {232, 238, 338, 344, 339, 337};
        int[] sy = {groundY-315, groundY-290, groundY-290, groundY-255, groundY-290, groundY-290};
        g.fillPolygon(sx, sy, 6);
        int[] sx2 = {435, 442, 548, 554, 549, 547};
        int[] sy2 = {groundY-295, groundY-272, groundY-272, groundY-240, groundY-272, groundY-272};
        g.fillPolygon(sx2, sy2, 6);

        // mid range – rolling green hills
        g.setColor(new Color(62, 138, 52, 128));
        int[] mx2 = {0,  95, 200, 310, 420, 530, 640, 750, 860, w, w, 0};
        int[] my2 = {groundY-108, groundY-175, groundY-138, groundY-195,
                     groundY-152, groundY-180, groundY-145, groundY-178,
                     groundY-130, groundY-118, groundY-90, groundY-90};
        g.fillPolygon(mx2, my2, 12);
    }

    // ── forest line ───────────────────────────────────────────────────────────

    private static void drawForestLine(Graphics2D g, int tick, int w, int h) {
        int groundY = h - 100;

        // dark silhouette forest
        g.setColor(new Color(32, 110, 32, 148));
        int spacing = 28;
        for (int tx = -spacing; tx < w + spacing; tx += spacing) {
            int sway = (int)(Math.sin(tick * 0.025 + tx * 0.04) * 2);
            int height = 52 + (int)(Math.sin(tx * 0.07) * 18);
            drawSilhouetteTree(g, tx + sway, groundY - height, 20, height);
        }

        // brighter mid-ground trees
        g.setColor(new Color(44, 138, 44, 110));
        for (int tx = -14; tx < w + 14; tx += 42) {
            int sway = (int)(Math.sin(tick * 0.03 + tx * 0.05) * 2);
            drawSilhouetteTree(g, tx + sway, groundY - 62, 26, 52);
        }
    }

    private static void drawSilhouetteTree(Graphics2D g, int x, int y, int w, int h) {
        g.fillRect(x + w/2 - 4, y + h - 18, 7, 18);
        g.fillOval(x, y + h / 2, w, h / 2);
        g.fillOval(x + w/8, y + h/4, w*3/4, h/2);
        g.fillOval(x + w/5, y, w*3/5, h/2);
    }

    // ── farm scene ────────────────────────────────────────────────────────────

    private static void drawFarmScene(Graphics2D g, int tick, int w, int h) {
        int groundY = h - 100;

        // spread elements across wider menu canvas
        drawRiceField(g, 18,         groundY - 54, 210, 58, tick);
        drawFence(g, 0, groundY - 8, w);
        drawBarn(g, 248, groundY - 105, 90, 105);
        drawFarmHouse(g, w - 290, groundY - 148, 185, 148, tick);
        drawCow(g, w / 2 + 5,  groundY - 46, tick);
        drawCow(g, w / 2 + 95, groundY - 46, tick + 20);  // second cow slightly offset
        drawTallTree(g, 20,      groundY - 92, tick);
        drawTallTree(g, 175,     groundY - 72, tick);
        drawTallTree(g, w - 80,  groundY - 92, tick);
        drawTallTree(g, w - 195, groundY - 72, tick);
        drawWindmill(g, w / 2 - 30, groundY - 125, tick);
        drawFlowers(g, 232,       groundY - 12, tick);
        drawFlowers(g, 420,       groundY - 12, tick);
        drawFlowers(g, w / 2 - 80, groundY - 12, tick);
        drawFlowers(g, w / 2 + 60, groundY - 12, tick);
        drawBush(g, 380, groundY - 22, tick);
        drawBush(g, w - 215, groundY - 22, tick);
        drawBush(g, 140,     groundY - 20, tick);
    }

    // ── rice field ────────────────────────────────────────────────────────────

    private static void drawRiceField(Graphics2D g, int x, int y, int w, int h, int tick) {
        g.setColor(new Color(95, 170, 222, 65));
        g.fillRect(x, y + h / 2, w, h / 2);

        // water shimmer
        g.setColor(new Color(255, 255, 255, 38));
        for (int wx = x + 8; wx < x + w - 10; wx += 18) {
            int off = (int)(Math.sin(tick * 0.08 + wx * 0.3) * 3);
            g.fillOval(wx + off, y + h * 3/4, 10, 3);
        }

        for (int row = 0; row < 5; row++) {
            int ry = y + row * 11;
            for (int col = 0; col < w / 12; col++) {
                int rx = x + col * 12;
                int sway = (int)(Math.sin(tick * 0.05 + col * 0.4) * 2);
                g.setColor(new Color(55, 152, 42));
                g.fillRect(rx + sway, ry, 2, 9);
                g.setColor(new Color(38, 122, 28));
                g.fillRect(rx + 4 + sway, ry + 2, 2, 7);
                g.setColor(new Color(198, 172, 68));
                g.fillOval(rx + sway, ry - 3, 3, 5);
            }
        }
        g.setColor(new Color(68, 42, 14));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x, y, w, h, 4, 4);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("SansSerif", Font.BOLD, 9));
        g.setColor(new Color(38, 112, 24));
        g.drawString("Rice Field", x + 4, y - 3);
    }

    // ── fence ─────────────────────────────────────────────────────────────────

    private static void drawFence(Graphics2D g, int x, int y, int w) {
        g.setColor(new Color(195, 165, 105));
        g.fillRect(x, y + 5, w, 5);
        g.fillRect(x, y + 16, w, 4);
        for (int px = x; px < x + w; px += 30) {
            GradientPaint post = new GradientPaint(
                    px, y, new Color(175, 135, 75),
                    px + 7, y, new Color(215, 180, 110));
            g.setPaint(post);
            g.fillRect(px, y, 7, 30);
            g.setPaint(null);
            g.setColor(new Color(220, 185, 115));
            g.fillOval(px - 1, y - 3, 9, 7);
        }
    }

    // ── farmhouse ─────────────────────────────────────────────────────────────

    private static void drawFarmHouse(Graphics2D g, int x, int y, int w, int h, int tick) {
        int wallTop = y + h / 3;

        GradientPaint wall = new GradientPaint(
                x, wallTop, new Color(248, 232, 198),
                x + w, y + h, new Color(212, 192, 152));
        g.setPaint(wall);
        g.fillRect(x, wallTop, w, h * 2 / 3);
        g.setPaint(null);

        g.setColor(new Color(185, 165, 125, 85));
        for (int row = 0; row < 8; row++)
            g.drawLine(x, wallTop + row * 14, x + w, wallTop + row * 14);

        int[] rx = {x - 14, x + w / 2, x + w + 14};
        int[] ry = {wallTop, y, wallTop};
        GradientPaint roof = new GradientPaint(
                x, y, new Color(192, 70, 50),
                x + w, wallTop, new Color(150, 46, 32));
        g.setPaint(roof);
        g.fillPolygon(rx, ry, 3);
        g.setPaint(null);

        g.setColor(new Color(100, 30, 15, 75));
        for (int row = 0; row < 6; row++) {
            int ry2 = y + row * (h / 3) / 6;
            int indent = row * 8;
            g.drawLine(x - 14 + indent, ry2, x + w + 14 - indent, ry2);
        }

        // chimney
        g.setColor(new Color(158, 108, 68));
        g.fillRect(x + w - 52, y - 26, 22, h / 3 + 16);
        g.setColor(new Color(128, 85, 55));
        g.fillRect(x + w - 55, y - 28, 28, 10);

        // smoke
        for (int s = 0; s < 4; s++) {
            float drift = (float)(Math.sin(tick * 0.03 + s) * 5);
            g.setColor(new Color(215, 215, 215, 68 - s * 14));
            int sz = 14 + s * 5;
            g.fillOval((int)(x + w - 47 + drift), y - 54 - s * 18, sz, sz);
        }

        GradientPaint door = new GradientPaint(
                x + w / 2 - 16, 0, new Color(108, 68, 24),
                x + w / 2 + 16, 0, new Color(85, 50, 18));
        g.setPaint(door);
        g.fillRoundRect(x + w / 2 - 16, y + h * 2 / 3, 32, h / 3, 6, 6);
        g.setPaint(null);
        g.setColor(new Color(205, 165, 85));
        g.fillOval(x + w / 2 + 8, y + h * 5 / 6, 5, 5);

        drawWindow(g, x + 12,       wallTop + h / 5, 36, 28, tick);
        drawWindow(g, x + w - 50,   wallTop + h / 5, 36, 28, tick);

        g.setColor(new Color(122, 92, 54));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRect(x, wallTop, w, h * 2 / 3);
        g.drawPolygon(rx, ry, 3);
        g.setStroke(new BasicStroke(1f));
    }

    private static void drawWindow(Graphics2D g, int x, int y, int w, int h, int tick) {
        g.setColor(new Color(152, 102, 52));
        g.fillRoundRect(x - 3, y - 3, w + 6, h + 6, 5, 5);
        g.setColor(new Color(198, 230, 255, 195));
        g.fillRoundRect(x, y, w, h, 3, 3);
        g.setColor(new Color(100, 80, 50, 155));
        g.drawLine(x + w/2, y, x + w/2, y + h);
        g.drawLine(x, y + h/2, x + w, y + h/2);
        g.setColor(new Color(255, 255, 255, 115));
        g.fillOval(x + 3, y + 2, 10, 7);
    }

    // ── barn ──────────────────────────────────────────────────────────────────

    private static void drawBarn(Graphics2D g, int x, int y, int w, int h) {
        GradientPaint barn = new GradientPaint(
                x, y, new Color(192, 56, 46),
                x + w, y + h, new Color(148, 36, 30));
        g.setPaint(barn);
        g.fillRect(x, y, w, h);
        g.setPaint(null);

        g.setColor(new Color(100, 20, 15, 98));
        for (int row = 1; row < 7; row++)
            g.drawLine(x, y + h * row / 7, x + w, y + h * row / 7);

        int[] rx = {x - 10, x + w / 2, x + w + 10};
        int[] ry = {y, y - 42, y};
        g.setColor(new Color(84, 54, 34));
        g.fillPolygon(rx, ry, 3);

        g.setColor(new Color(54, 34, 18));
        g.setStroke(new BasicStroke(3.5f));
        g.drawLine(x + w / 2, y - 42, x + w / 2, y);
        g.setStroke(new BasicStroke(1f));

        g.setColor(new Color(30, 10, 8, 195));
        g.fillOval(x + w / 2 - 14, y - 34, 28, 22);

        g.setColor(new Color(102, 62, 22));
        g.fillRoundRect(x + w / 2 - 18, y + h / 2, 36, h / 2, 4, 4);
        g.setColor(new Color(82, 52, 16));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(x + w / 2 - 17, y + h / 2, x + w / 2 + 17, y + h - 2);
        g.drawLine(x + w / 2 + 17, y + h / 2, x + w / 2 - 17, y + h - 2);
        g.setStroke(new BasicStroke(1f));

        g.setColor(new Color(82, 32, 22));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRect(x, y, w, h);
        g.setStroke(new BasicStroke(1f));

        g.setFont(new Font("SansSerif", Font.BOLD, 9));
        g.setColor(new Color(255, 220, 185));
        g.drawString("BARN", x + w / 2 - 12, y - 6);
    }

    // ── cow ───────────────────────────────────────────────────────────────────

    private static void drawCow(Graphics2D g, int x, int y, int tick) {
        g.setColor(new Color(242, 232, 215));
        g.fillOval(x, y + 10, 52, 30);
        g.setColor(new Color(48, 38, 28));
        g.fillOval(x + 6, y + 13, 16, 11);
        g.fillOval(x + 28, y + 20, 12, 9);
        g.setColor(new Color(242, 232, 215));
        g.fillOval(x + 40, y + 5, 24, 20);
        g.setColor(new Color(222, 182, 162));
        g.fillOval(x + 50, y + 14, 14, 10);
        g.setColor(new Color(162, 100, 100));
        g.fillOval(x + 52, y + 16, 4, 4);
        g.fillOval(x + 58, y + 16, 4, 4);
        g.setColor(new Color(22, 16, 10));
        g.fillOval(x + 48, y + 8, 5, 5);
        g.setColor(Color.WHITE);
        g.fillOval(x + 49, y + 8, 2, 2);
        g.setColor(new Color(232, 182, 162));
        g.fillOval(x + 42, y + 3, 9, 8);
        g.setColor(new Color(200, 175, 100));
        g.fillOval(x + 44, y, 7, 9);
        g.fillOval(x + 54, y, 7, 9);
        int swing = (int)(Math.sin(tick * 0.08) * 4);
        g.setColor(new Color(230, 222, 204));
        g.fillRect(x + 7,  y + 36, 8, 16 + swing);
        g.fillRect(x + 18, y + 36, 8, 16 - swing);
        g.fillRect(x + 30, y + 36, 8, 16 + swing);
        g.fillRect(x + 41, y + 36, 8, 16 - swing);
        g.setColor(new Color(48, 38, 28));
        g.fillRoundRect(x + 7,  y + 50 + swing, 8, 5, 2, 2);
        g.fillRoundRect(x + 18, y + 50 - swing, 8, 5, 2, 2);
        g.fillRoundRect(x + 30, y + 50 + swing, 8, 5, 2, 2);
        g.fillRoundRect(x + 41, y + 50 - swing, 8, 5, 2, 2);
        g.setColor(new Color(200, 182, 162));
        g.setStroke(new BasicStroke(2f));
        int ts = (int)(Math.sin(tick * 0.1) * 8);
        g.drawArc(x - 14 + ts, y + 18, 22, 22, 0, 200);
        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(232, 182, 182));
        g.fillOval(x + 18, y + 33, 14, 8);
    }

    // ── tall tree ─────────────────────────────────────────────────────────────

    private static void drawTallTree(Graphics2D g, int x, int y, int tick) {
        int sway = (int)(Math.sin(tick * 0.04) * 3);

        GradientPaint trunk = new GradientPaint(
                x + 9, y + 50, new Color(102, 67, 28),
                x + 22, y + 50, new Color(72, 48, 18));
        g.setPaint(trunk);
        g.fillRoundRect(x + 9, y + 50, 16, 48, 5, 5);
        g.setPaint(null);

        g.setColor(new Color(82, 52, 22));
        g.fillOval(x + 2,  y + 90, 14, 8);
        g.fillOval(x + 18, y + 90, 14, 8);

        Color[] greens = {
            new Color(36, 125, 36),
            new Color(48, 155, 44),
            new Color(60, 182, 48)
        };
        int[] yOff = {0, 12, 22};
        for (int i = 0; i < 3; i++) {
            g.setColor(greens[i]);
            g.fillOval(x - 10 + sway, y + 30 - yOff[i], 50, 34);
        }
        g.setColor(new Color(56, 168, 44));
        g.fillOval(x - 2 + sway, y + 12, 42, 30);
        g.setColor(new Color(68, 192, 52));
        g.fillOval(x + 2 + sway, y, 35, 28);
        g.setColor(new Color(118, 228, 82, 82));
        g.fillOval(x + 4 + sway, y + 4, 14, 12);

        g.setColor(new Color(220, 40, 40));
        g.fillOval(x + 6  + sway, y + 28, 9, 9);
        g.fillOval(x + 20 + sway, y + 22, 8, 8);
        g.setColor(new Color(80, 50, 20));
        g.setStroke(new BasicStroke(1f));
        g.drawLine(x + 10 + sway, y + 28, x + 10 + sway, y + 24);
        g.drawLine(x + 24 + sway, y + 22, x + 24 + sway, y + 18);
        g.setStroke(new BasicStroke(1f));
    }

    // ── bush ──────────────────────────────────────────────────────────────────

    private static void drawBush(Graphics2D g, int x, int y, int tick) {
        int sway = (int)(Math.sin(tick * 0.05 + x * 0.02) * 2);
        g.setColor(new Color(40, 125, 34));
        g.fillOval(x + sway, y, 32, 22);
        g.fillOval(x - 8 + sway, y + 5, 28, 20);
        g.fillOval(x + 12 + sway, y + 5, 28, 20);
        g.setColor(new Color(56, 155, 48));
        g.fillOval(x + 4 + sway, y - 5, 24, 18);
        g.setColor(new Color(78, 182, 62, 80));
        g.fillOval(x + 6 + sway, y - 3, 10, 7);
    }

    // ── flowers ───────────────────────────────────────────────────────────────

    private static void drawFlowers(Graphics2D g, int x, int y, int tick) {
        Color[] petal = {
            new Color(255, 100, 100),
            new Color(255, 200, 50),
            new Color(200, 100, 255),
            new Color(100, 200, 255),
            new Color(255, 140, 40)
        };
        for (int f = 0; f < 6; f++) {
            int fx = x + f * 16;
            int sw = (int)(Math.sin(tick * 0.06 + f) * 2);
            g.setColor(new Color(55, 135, 38));
            g.fillRect(fx + sw, y - 13, 2, 15);
            g.setColor(new Color(72, 155, 46));
            g.fillOval(fx - 4 + sw, y - 9, 7, 5);
            g.setColor(petal[f % petal.length]);
            for (int p = 0; p < 5; p++) {
                double angle = Math.toRadians(p * 72);
                g.fillOval(
                    (int)(fx + sw + Math.cos(angle) * 5) - 3,
                    (int)(y - 17 + Math.sin(angle) * 5) - 3,
                    7, 7);
            }
            g.setColor(new Color(255, 242, 58));
            g.fillOval(fx + sw - 3, y - 20, 7, 7);
        }
    }

    // ── windmill ──────────────────────────────────────────────────────────────

    private static void drawWindmill(Graphics2D g, int x, int y, int tick) {
        int[] tx = {x + 6, x + 24, x + 30, x};
        int[] ty = {y, y, y + 82, y + 82};
        GradientPaint tower = new GradientPaint(
                x, y, new Color(228, 208, 168),
                x + 30, y, new Color(185, 165, 125));
        g.setPaint(tower);
        g.fillPolygon(tx, ty, 4);
        g.setPaint(null);

        g.setColor(new Color(105, 65, 25));
        g.fillRoundRect(x + 8, y + 52, 14, 30, 4, 4);

        g.setColor(new Color(155, 105, 55));
        g.fillOval(x + 11, y + 14, 8, 8);
        g.setColor(new Color(228, 192, 85));
        g.fillOval(x + 13, y + 16, 4, 4);

        double angle = tick * 0.045;
        for (int b = 0; b < 4; b++) {
            double ba = angle + b * Math.PI / 2;
            int bx2 = (int)(x + 15 + Math.cos(ba) * 34);
            int by2 = (int)(y + 18 + Math.sin(ba) * 34);
            g.setColor(new Color(180, 150, 92));
            g.setStroke(new BasicStroke(11f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(x + 15, y + 18, bx2, by2);
            g.setColor(new Color(245, 225, 188));
            g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(x + 15, y + 18, bx2, by2);
        }
        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(185, 135, 65));
        g.fillOval(x + 10, y + 13, 10, 10);
        g.setColor(new Color(225, 185, 85));
        g.fillOval(x + 12, y + 15, 6, 6);

        g.setColor(new Color(82, 52, 22));
        g.setStroke(new BasicStroke(1.5f));
        g.drawPolygon(tx, ty, 4);
        g.setStroke(new BasicStroke(1f));
    }

    // ── ground ────────────────────────────────────────────────────────────────

    private static void drawGround(Graphics2D g, int tick, int w, int h) {
        int groundY = h - 100;

        GradientPaint grass = new GradientPaint(
                0, groundY, new Color(60, 138, 44),
                0, h,       new Color(38, 98, 28));
        g.setPaint(grass);
        g.fillRect(0, groundY, w, 100);
        g.setPaint(null);

        // path to farmhouse
        g.setColor(new Color(158, 118, 58, 138));
        int[] px = {w / 2 - 32, w / 2 + 32, w / 2 + 26, w / 2 - 26};
        int[] py = {groundY, groundY, h, h};
        g.fillPolygon(px, py, 4);

        // second path (to barn)
        g.setColor(new Color(148, 110, 52, 100));
        int[] px2 = {265, 312, 308, 260};
        int[] py2  = {groundY, groundY, h, h};
        g.fillPolygon(px2, py2, 4);

        // main grass tuft row
        g.setColor(new Color(74, 168, 54));
        for (int i = 0; i < w; i += 16) {
            int sw = (int)(Math.sin(tick * 0.04 + i * 0.15) * 2);
            g.fillArc(i - 2 + sw, groundY - 10, 16, 15, 0, 180);
        }
        // secondary tufts
        g.setColor(new Color(100, 205, 68, 140));
        for (int i = 6; i < w; i += 22) {
            int sw = (int)(Math.sin(tick * 0.04 + i * 0.2) * 2);
            g.fillArc(i + sw, groundY - 5, 11, 11, 0, 180);
        }

        // ground edge
        g.setColor(new Color(38, 112, 26));
        g.setStroke(new BasicStroke(2.5f));
        g.drawLine(0, groundY, w, groundY);
        g.setStroke(new BasicStroke(1f));

        // scattered pebbles
        g.setColor(new Color(165, 148, 118, 118));
        int[] pebX = {32, 80, 155, 218, 340, 420, 488, 560, 610, 680, 740, 820};
        int[] pebY = {groundY+20, groundY+35, groundY+24, groundY+44,
                      groundY+30, groundY+18, groundY+38, groundY+25,
                      groundY+42, groundY+20, groundY+35, groundY+28};
        for (int i = 0; i < pebX.length; i++)
            g.fillOval(pebX[i], pebY[i], 6, 4);
    }

    // ── utility ───────────────────────────────────────────────────────────────

    private static Color blend(Color a, Color b, float t) {
        int r = (int)(a.getRed()   + (b.getRed()   - a.getRed())   * t);
        int gv = (int)(a.getGreen() + (b.getGreen() - a.getGreen()) * t);
        int bl = (int)(a.getBlue()  + (b.getBlue()  - a.getBlue())  * t);
        return new Color(clamp(r), clamp(gv), clamp(bl));
    }

    private static int clamp(int v) { return Math.max(0, Math.min(255, v)); }

    private MenuBackgroundRenderer() {}
}