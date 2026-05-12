package OOP_PROJECT.CatchTheBall.src.renderers;

import java.awt.*;
import java.awt.geom.*;

public class BackgroundRenderer {

    // ── palette helpers ────────────────────────────────────────────────────────

    /** Sky top colours per era */
    private static Color skyTop(int level) {
        if (level <= 3)  return new Color(30, 120, 220);
        if (level <= 6)  return new Color(220, 90,  20);
        if (level <= 9)  return new Color(160, 30,  20);
        return                   new Color(5,   8,  35);
    }
    private static Color skyMid(int level) {
        if (level <= 3)  return new Color(90, 180, 255);
        if (level <= 6)  return new Color(255, 170, 60);
        if (level <= 9)  return new Color(230, 100, 50);
        return                   new Color(20,  30, 70);
    }
    private static Color skyBot(int level) {
        if (level <= 3)  return new Color(180, 225, 255);
        if (level <= 6)  return new Color(255, 220, 140);
        if (level <= 9)  return new Color(255, 160, 80);
        return                   new Color(38,  55, 100);
    }

    // ── public entry point ─────────────────────────────────────────────────────

    public static void drawSky(Graphics2D g, int level, int tickCount,
                               int arenaW, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);

        drawGradientSky(g2, level, arenaW, h);
        if (level <= 6) drawSun(g2, arenaW, tickCount, level);
        else            drawMoon(g2, arenaW, tickCount);
        drawStars(g2, arenaW, level, tickCount);
        drawClouds(g2, arenaW, tickCount, level);
        drawDistantHaze(g2, arenaW, h, level);
        drawMountains(g2, arenaW, h, level);
        drawForestLayer(g2, arenaW, h, tickCount, level);
        drawFarmScene(g2, arenaW, h, tickCount, level);
        drawGround(g2, arenaW, h, tickCount, level);

        g2.dispose();
    }

    // ── sky ───────────────────────────────────────────────────────────────────

    private static void drawGradientSky(Graphics2D g, int level, int arenaW, int h) {
        Color top = skyTop(level), mid = skyMid(level), bot = skyBot(level);
        int groundY = h - 80;

        // three-stop gradient: top → mid-point → horizon
        GradientPaint upper = new GradientPaint(0, 0, top, 0, groundY / 2f, mid);
        g.setPaint(upper);
        g.fillRect(0, 0, arenaW, groundY / 2);

        GradientPaint lower = new GradientPaint(0, groundY / 2f, mid, 0, groundY, bot);
        g.setPaint(lower);
        g.fillRect(0, groundY / 2, arenaW, groundY / 2 + 2);

        g.setPaint(null);
    }

    private static void drawDistantHaze(Graphics2D g, int arenaW, int h, int level) {
        int groundY = h - 80;
        Color haze;
        if      (level <= 3) haze = new Color(200, 230, 255, 55);
        else if (level <= 6) haze = new Color(255, 200, 120, 60);
        else if (level <= 9) haze = new Color(255, 130, 60,  50);
        else                 haze = new Color(30,  50, 100,  45);

        GradientPaint gp = new GradientPaint(0, groundY - 110, transparent(haze), 0, groundY, haze);
        g.setPaint(gp);
        g.fillRect(0, groundY - 110, arenaW, 115);
        g.setPaint(null);
    }

    // ── celestial ─────────────────────────────────────────────────────────────

    private static void drawSun(Graphics2D g, int arenaW, int tick, int level) {
        int sx = arenaW - 90, sy = 48;
        int r = 20;

        // outer glow rings
        for (int ring = 5; ring >= 1; ring--) {
            int alpha = ring * 9;
            Color col = level <= 3
                    ? new Color(255, 230, 100, alpha)
                    : new Color(255, 170, 60, alpha);
            g.setColor(col);
            int pad = ring * 10;
            g.fillOval(sx - pad, sy - pad, (r + pad) * 2, (r + pad) * 2);
        }

        // core disc gradient
        GradientPaint disc = new GradientPaint(sx, sy,
                new Color(255, 250, 200),
                sx + r * 2, sy + r * 2,
                new Color(255, 190, 30));
        g.setPaint(disc);
        g.fillOval(sx, sy, r * 2, r * 2);
        g.setPaint(null);

        // specular highlight
        g.setColor(new Color(255, 255, 255, 100));
        g.fillOval(sx + 4, sy + 4, r / 2, r / 2);

        // animated rays
        g.setColor(new Color(255, 235, 100, 100));
        for (int a = 0; a < 12; a++) {
            double angle = Math.toRadians(a * 30 + tick * 0.25);
            int inner = r + 5, outer = r + 14 + (a % 2) * 6;
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(
                (int)(sx + r + Math.cos(angle) * inner),
                (int)(sy + r + Math.sin(angle) * inner),
                (int)(sx + r + Math.cos(angle) * outer),
                (int)(sy + r + Math.sin(angle) * outer));
        }
        g.setStroke(new BasicStroke(1f));
    }

    private static void drawMoon(Graphics2D g, int arenaW, int tick) {
        int mx = arenaW - 85, my = 44;
        // soft glow
        for (int ring = 4; ring >= 1; ring--) {
            g.setColor(new Color(200, 215, 255, ring * 8));
            int pad = ring * 8;
            g.fillOval(mx - pad, my - pad, 32 + pad * 2, 32 + pad * 2);
        }
        g.setColor(new Color(228, 238, 255));
        g.fillOval(mx, my, 32, 32);
        // crater shadow
        g.setColor(new Color(60, 82, 140));
        g.fillOval(mx + 9, my - 2, 20, 20);
        g.setColor(new Color(200, 215, 255, 120));
        g.fillOval(mx + 2, my + 2, 10, 8);
        g.fillOval(mx + 18, my + 18, 6, 5);
    }

    private static void drawStars(Graphics2D g, int arenaW, int level, int tick) {
        if (level <= 6) return;
        int[][] stars = {
            {45,18},{88,8},{155,28},{220,12},{290,22},{380,10},
            {450,30},{510,15},{580,24},{630,8},{350,40},{120,42}
        };
        float twinkle = 0.6f + 0.4f * (float)Math.sin(tick * 0.05);
        for (int[] s : stars) {
            int alpha = (int)(180 * twinkle);
            g.setColor(new Color(255, 255, 220, alpha));
            g.fillOval(s[0], s[1], 2, 2);
        }
        // shooting star
        if (level > 9) {
            int sx = (int)((tick * 2.5) % (arenaW + 120)) - 60;
            g.setColor(new Color(255, 255, 200, 150));
            g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(sx, 16, sx + 38, 10);
            g.setStroke(new BasicStroke(1f));
        }
    }

    // ── clouds ────────────────────────────────────────────────────────────────

    private static void drawClouds(Graphics2D g, int arenaW, int tick, int level) {
        if (level > 9) return;
        int alpha = level <= 3 ? 215 : 160;

        // layer 1 – large, faster
        int off1 = (int)(tick * 0.38) % (arenaW + 240);
        drawCloud(g, 70  - off1 + arenaW, 48,  100, 36, alpha);
        drawCloud(g, 290 - off1 + arenaW, 38,   72, 26, alpha);
        drawCloud(g, 490 - off1 + arenaW, 60,  118, 42, alpha);
        drawCloud(g, 700 - off1 + arenaW, 44,   82, 30, alpha);

        // layer 2 – smaller, slower
        int off2 = (int)(tick * 0.18) % (arenaW + 320);
        drawCloud(g, 140 - off2 + arenaW, 90,   58, 20, alpha - 50);
        drawCloud(g, 390 - off2 + arenaW, 76,   74, 24, alpha - 50);
        drawCloud(g, 570 - off2 + arenaW, 98,   50, 18, alpha - 60);

        // during sunset add warm tint
        if (level > 3 && level <= 6) {
            int off3 = (int)(tick * 0.12) % (arenaW + 200);
            drawCloud(g, 200 - off3 + arenaW, 112, 64, 20, 80, new Color(255, 160, 80));
            drawCloud(g, 480 - off3 + arenaW, 100, 88, 28, 80, new Color(255, 140, 60));
        }
    }

    private static void drawCloud(Graphics2D g, int x, int y, int w, int h, int alpha) {
        drawCloud(g, x, y, w, h, alpha, Color.WHITE);
    }

    private static void drawCloud(Graphics2D g, int x, int y, int w, int h, int alpha, Color tint) {
        g.setColor(new Color(tint.getRed(), tint.getGreen(), tint.getBlue(), alpha));
        g.fillOval(x,            y,           w,       h);
        g.fillOval(x + w / 4,   y - h / 3,   w / 2,   h);
        g.fillOval(x + w * 3/5, y - h / 4,   w * 2/5, h - 4);
        g.fillOval(x + w / 6,   y + h / 4,   w * 2/3, h * 2/3);
        // subtle shadow underside
        g.setColor(new Color(0, 0, 0, alpha / 12));
        g.fillOval(x + w / 8,   y + h * 2/3, w * 3/4, h / 3);
    }

    // ── mountains ─────────────────────────────────────────────────────────────

    private static void drawMountains(Graphics2D g, int arenaW, int h, int level) {
        int groundY = h - 80;

        // far range – cool blue-grey
        Color farCol;
        if      (level <= 3) farCol = new Color(110, 155, 195, 90);
        else if (level <= 6) farCol = new Color(160, 100,  60, 80);
        else if (level <= 9) farCol = new Color(140,  60,  40, 85);
        else                 farCol = new Color( 25,  35,  75, 100);

        int[] mx1 = {0,   60,  140, 230, 320, 410, 500, 590, arenaW, arenaW, 0};
        int[] my1 = {groundY-160, groundY-260, groundY-200, groundY-300,
                     groundY-240, groundY-280, groundY-215, groundY-270,
                     groundY-180, groundY-80, groundY-80};
        g.setColor(farCol);
        g.fillPolygon(mx1, my1, 11);

        // snow caps on day levels
        if (level <= 3) {
            g.setColor(new Color(240, 248, 255, 120));
            int[] sx = {225, 230, 315, 320, 316, 314};
            int[] sy = {groundY-300, groundY-278, groundY-278, groundY-240, groundY-278, groundY-278};
            g.fillPolygon(sx, sy, 6);
        }

        // mid range – green hills
        Color midCol;
        if      (level <= 3) midCol = new Color(70, 145, 55, 130);
        else if (level <= 6) midCol = new Color(95, 115, 45, 110);
        else if (level <= 9) midCol = new Color(60,  80, 30, 100);
        else                 midCol = new Color(15,  30, 15, 110);

        int[] mx2 = {0, 90, 190, 290, 390, 490, arenaW, arenaW, 0};
        int[] my2 = {groundY-100, groundY-168, groundY-128, groundY-182,
                     groundY-140, groundY-165, groundY-118, groundY-80, groundY-80};
        g.setColor(midCol);
        g.fillPolygon(mx2, my2, 9);
    }

    // ── forest layer (behind farm) ─────────────────────────────────────────────

    private static void drawForestLayer(Graphics2D g, int arenaW, int h, int tick, int level) {
        int groundY = h - 80;
        Color treeCol;
        if      (level <= 3) treeCol = new Color(40, 120, 40, 140);
        else if (level <= 6) treeCol = new Color(65, 110, 30, 120);
        else if (level <= 9) treeCol = new Color(35,  70, 20, 110);
        else                 treeCol = new Color(10,  22, 10, 120);

        g.setColor(treeCol);
        int[] treeTops = {
            10, 35, 60, 85, 110, 135, 160, 185,
            arenaW-10, arenaW-35, arenaW-60, arenaW-85, arenaW-110, arenaW-135
        };
        for (int tx : treeTops) {
            int sway = (int)(Math.sin(tick * 0.03 + tx * 0.05) * 2);
            drawDistantTree(g, tx + sway, groundY - 68, 22, 42);
        }
    }

    private static void drawDistantTree(Graphics2D g, int x, int y, int w, int h) {
        // trunk
        g.fillRect(x + w/2 - 3, y + h - 18, 6, 18);
        // canopy layers
        g.fillOval(x,          y + h/2,     w,     h/2);
        g.fillOval(x + w/8,    y + h/4,     w*3/4, h/2);
        g.fillOval(x + w/5,    y,           w*3/5, h/2);
    }

    // ── farm scene ────────────────────────────────────────────────────────────

    private static void drawFarmScene(Graphics2D g, int arenaW, int h, int tick, int level) {
        int groundY = h - 80;

        drawRiceField(g, 8,           groundY - 46, 180, 52, tick, level);
        drawFence(g, 0, groundY - 8, arenaW);
        drawBarn(g, 148, groundY - 98, 82, 98, level);
        drawFarmHouse(g, arenaW - 242, groundY - 130, 168, 130, tick, level);
        if (level <= 6)  drawCow(g, arenaW / 2 + 10, groundY - 44, tick);
        drawTallTree(g, 22,          groundY - 80, tick);
        drawTallTree(g, arenaW - 68, groundY - 80, tick);
        drawTallTree(g, 255,         groundY - 64, tick);
        drawWindmill(g, arenaW / 2 - 24, groundY - 108, tick);
        drawFlowers(g, 208, groundY - 10, tick);
        drawFlowers(g, 388, groundY - 10, tick);
        drawFlowers(g, arenaW / 2 - 60, groundY - 10, tick);
        drawBush(g, 335, groundY - 20, tick);
        drawBush(g, arenaW - 175, groundY - 20, tick);
    }

    // ── rice field ────────────────────────────────────────────────────────────

    private static void drawRiceField(Graphics2D g, int x, int y, int w, int h,
                                       int tick, int level) {
        // water reflection
        Color waterCol = level <= 3
                ? new Color(100, 175, 225, 70)
                : new Color(80, 130, 160, 55);
        g.setColor(waterCol);
        g.fillRect(x, y + h / 2, w, h / 2);

        // glimmer on water
        g.setColor(new Color(255, 255, 255, 40));
        for (int wx = x + 8; wx < x + w - 10; wx += 18) {
            int off = (int)(Math.sin(tick * 0.08 + wx * 0.3) * 3);
            g.fillOval(wx + off, y + h * 3/4, 10, 3);
        }

        // rice stalks
        for (int row = 0; row < 4; row++) {
            int ry = y + row * 11;
            for (int col = 0; col < w / 12; col++) {
                int rx = x + col * 12;
                int sway = (int)(Math.sin(tick * 0.05 + col * 0.4) * 2);
                g.setColor(new Color(60, 155, 45));
                g.fillRect(rx + sway, ry, 2, 9);
                g.setColor(new Color(40, 125, 30));
                g.fillRect(rx + 4 + sway, ry + 2, 2, 7);
                // grain tip
                g.setColor(new Color(200, 175, 70));
                g.fillOval(rx + sway, ry - 3, 3, 5);
            }
        }

        // border + label
        g.setColor(new Color(70, 45, 15));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x, y, w, h, 4, 4);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("SansSerif", Font.BOLD, 8));
        g.setColor(new Color(40, 115, 25));
        g.drawString("Rice Field", x + 4, y - 3);
    }

    // ── fence ─────────────────────────────────────────────────────────────────

    private static void drawFence(Graphics2D g, int x, int y, int w) {
        // rails
        g.setColor(new Color(195, 165, 105));
        g.fillRect(x, y + 5, w, 5);
        g.fillRect(x, y + 16, w, 4);
        // posts
        for (int px = x; px < x + w; px += 30) {
            GradientPaint post = new GradientPaint(
                    px, y, new Color(175, 135, 75),
                    px + 7, y, new Color(215, 180, 110));
            g.setPaint(post);
            g.fillRect(px, y, 7, 30);
            g.setPaint(null);
            // post cap
            g.setColor(new Color(220, 185, 115));
            g.fillOval(px - 1, y - 3, 9, 7);
        }
    }

    // ── farmhouse ─────────────────────────────────────────────────────────────

    private static void drawFarmHouse(Graphics2D g, int x, int y, int w, int h,
                                       int tick, int level) {
        int wallTop = y + h / 3;

        // wall
        GradientPaint wall = new GradientPaint(
                x, wallTop, new Color(248, 230, 195),
                x + w, y + h, new Color(210, 190, 150));
        g.setPaint(wall);
        g.fillRect(x, wallTop, w, h * 2 / 3);
        g.setPaint(null);

        // horizontal siding lines
        g.setColor(new Color(185, 165, 125, 90));
        for (int row = 0; row < 7; row++)
            g.drawLine(x, wallTop + row * 13, x + w, wallTop + row * 13);

        // roof
        int[] rx = {x - 12, x + w / 2, x + w + 12};
        int[] ry = {wallTop, y, wallTop};
        GradientPaint roof = new GradientPaint(
                x, y, new Color(188, 68, 48),
                x + w, wallTop, new Color(148, 44, 30));
        g.setPaint(roof);
        g.fillPolygon(rx, ry, 3);
        g.setPaint(null);

        // roof tiles hint
        g.setColor(new Color(100, 30, 15, 80));
        for (int row = 0; row < 6; row++) {
            int ry2 = y + row * (h / 3) / 6;
            int indent = row * 7;
            g.drawLine(x - 12 + indent, ry2, x + w + 12 - indent, ry2);
        }

        // chimney
        g.setColor(new Color(158, 108, 68));
        g.fillRect(x + w - 44, y - 24, 20, h / 3 + 14);
        g.setColor(new Color(128, 85, 55));
        g.fillRect(x + w - 47, y - 26, 26, 9);

        // smoke puffs (day only)
        if (level <= 6) {
            for (int s = 0; s < 4; s++) {
                float drift = (float)(Math.sin(tick * 0.03 + s) * 5);
                int alpha = 70 - s * 15;
                g.setColor(new Color(210, 210, 210, alpha));
                int sz = 12 + s * 5;
                g.fillOval((int)(x + w - 40 + drift), y - 50 - s * 16, sz, sz);
            }
        }
        // glow from windows at night
        if (level > 6) {
            g.setColor(new Color(255, 200, 60, 30));
            g.fillOval(x - 15, wallTop + h / 6, w + 30, h / 2);
        }

        // door
        GradientPaint door = new GradientPaint(
                x + w / 2 - 14, 0, new Color(105, 65, 22),
                x + w / 2 + 14, 0, new Color(82, 48, 18));
        g.setPaint(door);
        g.fillRoundRect(x + w / 2 - 14, y + h * 2 / 3, 28, h / 3, 6, 6);
        g.setPaint(null);
        g.setColor(new Color(205, 165, 85));
        g.fillOval(x + w / 2 + 6, y + h * 5 / 6, 5, 5);

        // windows
        drawWindow(g, x + 10,       wallTop + h / 6, 32, 26, tick, level);
        drawWindow(g, x + w - 44,   wallTop + h / 6, 32, 26, tick, level);

        // outline
        g.setColor(new Color(120, 90, 52));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRect(x, wallTop, w, h * 2 / 3);
        g.drawPolygon(rx, ry, 3);
        g.setStroke(new BasicStroke(1f));
    }

    private static void drawWindow(Graphics2D g, int x, int y, int w, int h,
                                    int tick, int level) {
        // frame
        g.setColor(new Color(152, 102, 52));
        g.fillRoundRect(x - 3, y - 3, w + 6, h + 6, 5, 5);
        // glass
        Color glass = level <= 6
                ? new Color(195, 228, 255, 190)
                : new Color(255, 235, 140, 200);
        g.setColor(glass);
        g.fillRoundRect(x, y, w, h, 3, 3);
        // cross divider
        g.setColor(new Color(100, 80, 50, 160));
        g.drawLine(x + w / 2, y, x + w / 2, y + h);
        g.drawLine(x, y + h / 2, x + w, y + h / 2);
        // specular
        g.setColor(new Color(255, 255, 255, 110));
        g.fillOval(x + 2, y + 2, 9, 6);
        // night glow
        if (level > 6) {
            g.setColor(new Color(255, 200, 80, 50));
            g.fillOval(x - 6, y - 6, w + 12, h + 12);
        }
    }

    // ── barn ──────────────────────────────────────────────────────────────────

    private static void drawBarn(Graphics2D g, int x, int y, int w, int h, int level) {
        // body
        GradientPaint barn = new GradientPaint(
                x, y, new Color(188, 54, 44),
                x + w, y + h, new Color(144, 34, 28));
        g.setPaint(barn);
        g.fillRect(x, y, w, h);
        g.setPaint(null);

        // plank lines
        g.setColor(new Color(100, 20, 15, 100));
        for (int row = 1; row < 7; row++)
            g.drawLine(x, y + h * row / 7, x + w, y + h * row / 7);

        // gambrel roof
        int[] rx = {x - 10, x + w / 2, x + w + 10};
        int[] ry = {y, y - 38, y};
        g.setColor(new Color(84, 54, 34));
        g.fillPolygon(rx, ry, 3);
        g.setColor(new Color(54, 34, 18));
        g.setStroke(new BasicStroke(3f));
        g.drawLine(x + w / 2, y - 38, x + w / 2, y);
        g.setStroke(new BasicStroke(1f));

        // loft door opening
        g.setColor(new Color(30, 10, 8, 200));
        g.fillOval(x + w / 2 - 12, y - 30, 24, 20);

        // main door
        g.setColor(new Color(102, 62, 22));
        g.fillRoundRect(x + w / 2 - 15, y + h / 2, 30, h / 2, 4, 4);

        // X cross bracing on door
        g.setColor(new Color(82, 52, 16));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(x + w / 2 - 14, y + h / 2, x + w / 2 + 14, y + h - 2);
        g.drawLine(x + w / 2 + 14, y + h / 2, x + w / 2 - 14, y + h - 2);
        g.setStroke(new BasicStroke(1f));

        // outline
        g.setColor(new Color(82, 32, 22));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRect(x, y, w, h);
        g.setStroke(new BasicStroke(1f));

        // label
        g.setFont(new Font("SansSerif", Font.BOLD, 8));
        g.setColor(new Color(255, 220, 185));
        g.drawString("BARN", x + w / 2 - 10, y - 4);
    }

    // ── cow ───────────────────────────────────────────────────────────────────

    private static void drawCow(Graphics2D g, int x, int y, int tick) {
        // body
        g.setColor(new Color(242, 232, 215));
        g.fillOval(x, y + 10, 52, 30);
        // patches
        g.setColor(new Color(48, 38, 28));
        g.fillOval(x + 6, y + 13, 16, 11);
        g.fillOval(x + 28, y + 20, 12, 9);
        // head
        g.setColor(new Color(242, 232, 215));
        g.fillOval(x + 40, y + 5, 24, 20);
        // muzzle
        g.setColor(new Color(222, 182, 162));
        g.fillOval(x + 50, y + 14, 14, 10);
        g.setColor(new Color(162, 100, 100));
        g.fillOval(x + 52, y + 16, 4, 4);
        g.fillOval(x + 58, y + 16, 4, 4);
        // eye
        g.setColor(new Color(22, 16, 10));
        g.fillOval(x + 48, y + 8, 5, 5);
        g.setColor(Color.WHITE);
        g.fillOval(x + 49, y + 8, 2, 2);
        // ear
        g.setColor(new Color(232, 182, 162));
        g.fillOval(x + 42, y + 3, 9, 8);
        g.setColor(new Color(200, 175, 100));
        g.fillOval(x + 44, y, 7, 9);
        g.fillOval(x + 54, y, 7, 9);
        // legs
        int swing = (int)(Math.sin(tick * 0.08) * 4);
        g.setColor(new Color(230, 222, 204));
        g.fillRect(x + 7,  y + 36, 8, 16 + swing);
        g.fillRect(x + 18, y + 36, 8, 16 - swing);
        g.fillRect(x + 30, y + 36, 8, 16 + swing);
        g.fillRect(x + 41, y + 36, 8, 16 - swing);
        // hooves
        g.setColor(new Color(48, 38, 28));
        g.fillRoundRect(x + 7,  y + 50 + swing, 8, 5, 2, 2);
        g.fillRoundRect(x + 18, y + 50 - swing, 8, 5, 2, 2);
        g.fillRoundRect(x + 30, y + 50 + swing, 8, 5, 2, 2);
        g.fillRoundRect(x + 41, y + 50 - swing, 8, 5, 2, 2);
        // tail
        g.setColor(new Color(200, 182, 162));
        g.setStroke(new BasicStroke(2f));
        int ts = (int)(Math.sin(tick * 0.1) * 8);
        g.drawArc(x - 14 + ts, y + 18, 22, 22, 0, 200);
        g.setStroke(new BasicStroke(1f));
        // udder
        g.setColor(new Color(232, 182, 182));
        g.fillOval(x + 18, y + 34, 16, 9);
    }

    // ── tall tree ─────────────────────────────────────────────────────────────

    private static void drawTallTree(Graphics2D g, int x, int y, int tick) {
        int sway = (int)(Math.sin(tick * 0.04) * 3);

        // trunk with taper
        GradientPaint trunk = new GradientPaint(
                x + 9, y + 50, new Color(102, 67, 28),
                x + 22, y + 50, new Color(72, 48, 18));
        g.setPaint(trunk);
        g.fillRoundRect(x + 9, y + 50, 15, 45, 5, 5);
        g.setPaint(null);

        // roots
        g.setColor(new Color(82, 52, 22));
        g.fillOval(x + 2, y + 88, 14, 8);
        g.fillOval(x + 20, y + 88, 14, 8);

        // canopy layers (bottom to top)
        int[] offsets = {0, 12, 22};
        Color[] greens;
        if (tick >= 0) { // always true – just used to avoid static init warning
            greens = new Color[]{
                new Color(38, 128, 38),
                new Color(50, 158, 45),
                new Color(62, 185, 50)
            };
        } else {
            greens = new Color[]{ Color.GREEN, Color.GREEN, Color.GREEN };
        }
        for (int i = 0; i < 3; i++) {
            g.setColor(greens[i]);
            g.fillOval(x - 10 + sway, y + 28 - offsets[i], 50, 32);
        }
        g.setColor(new Color(58, 172, 45));
        g.fillOval(x - 2 + sway, y + 10, 40, 28);
        g.setColor(new Color(70, 195, 55));
        g.fillOval(x + 2 + sway, y, 34, 26);

        // highlight
        g.setColor(new Color(120, 228, 85, 80));
        g.fillOval(x + 4 + sway, y + 4, 14, 11);

        // apples
        g.setColor(new Color(220, 40, 40));
        g.fillOval(x + 6  + sway, y + 28, 9, 9);
        g.fillOval(x + 20 + sway, y + 22, 8, 8);
        // stems
        g.setColor(new Color(80, 50, 20));
        g.setStroke(new BasicStroke(1f));
        g.drawLine(x + 10 + sway, y + 28, x + 10 + sway, y + 24);
        g.drawLine(x + 24 + sway, y + 22, x + 24 + sway, y + 18);
        g.setStroke(new BasicStroke(1f));
    }

    // ── bush ──────────────────────────────────────────────────────────────────

    private static void drawBush(Graphics2D g, int x, int y, int tick) {
        int sway = (int)(Math.sin(tick * 0.05 + x * 0.02) * 2);
        g.setColor(new Color(42, 128, 36));
        g.fillOval(x + sway,       y,      30, 20);
        g.fillOval(x - 8 + sway,   y + 4,  26, 18);
        g.fillOval(x + 12 + sway,  y + 4,  26, 18);
        g.setColor(new Color(58, 158, 50));
        g.fillOval(x + 4 + sway,   y - 4,  22, 16);
        g.setColor(new Color(80, 185, 65, 80));
        g.fillOval(x + 6 + sway,   y - 3,  8, 6);
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
        for (int f = 0; f < 5; f++) {
            int fx = x + f * 16;
            int sway = (int)(Math.sin(tick * 0.06 + f) * 2);
            // stem
            g.setColor(new Color(58, 138, 40));
            g.fillRect(fx + sway, y - 12, 2, 14);
            // leaf
            g.setColor(new Color(75, 158, 48));
            g.fillOval(fx - 4 + sway, y - 8, 6, 4);
            // petals
            g.setColor(petal[f % petal.length]);
            for (int p = 0; p < 5; p++) {
                double angle = Math.toRadians(p * 72);
                g.fillOval(
                    (int)(fx + sway + Math.cos(angle) * 5) - 3,
                    (int)(y - 16 + Math.sin(angle) * 5) - 3,
                    6, 6);
            }
            // centre
            g.setColor(new Color(255, 240, 55));
            g.fillOval(fx + sway - 2, y - 18, 6, 6);
        }
    }

    // ── windmill ──────────────────────────────────────────────────────────────

    private static void drawWindmill(Graphics2D g, int x, int y, int tick) {
        // tower (tapered)
        int[] tx = {x + 6, x + 24, x + 30, x};
        int[] ty = {y, y, y + 72, y + 72};
        GradientPaint tower = new GradientPaint(
                x, y, new Color(225, 205, 165),
                x + 30, y, new Color(182, 162, 122));
        g.setPaint(tower);
        g.fillPolygon(tx, ty, 4);
        g.setPaint(null);

        // door opening
        g.setColor(new Color(102, 62, 22));
        g.fillRoundRect(x + 8, y + 46, 14, 26, 4, 4);

        // hub disc
        g.setColor(new Color(152, 104, 52));
        g.fillOval(x + 11, y + 12, 8, 8);
        g.setColor(new Color(225, 188, 82));
        g.fillOval(x + 13, y + 14, 4, 4);

        // blades with depth
        double angle = tick * 0.045;
        for (int b = 0; b < 4; b++) {
            double ba = angle + b * Math.PI / 2;
            int bx2 = (int)(x + 15 + Math.cos(ba) * 30);
            int by2 = (int)(y + 16 + Math.sin(ba) * 30);
            // blade shadow
            g.setColor(new Color(180, 148, 90));
            g.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(x + 15, y + 16, bx2, by2);
            // blade highlight
            g.setColor(new Color(242, 222, 185));
            g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(x + 15, y + 16, bx2, by2);
        }
        g.setStroke(new BasicStroke(1f));

        // centre cap
        g.setColor(new Color(182, 132, 62));
        g.fillOval(x + 10, y + 11, 10, 10);
        g.setColor(new Color(222, 182, 82));
        g.fillOval(x + 12, y + 13, 6, 6);

        // outline
        g.setColor(new Color(82, 52, 22));
        g.setStroke(new BasicStroke(1.5f));
        g.drawPolygon(tx, ty, 4);
        g.setStroke(new BasicStroke(1f));
    }

    // ── ground ────────────────────────────────────────────────────────────────

    private static void drawGround(Graphics2D g, int arenaW, int h, int tick, int level) {
        int groundY = h - 80;

        // base grass gradient
        Color grassTop, grassBot;
        if      (level <= 3) { grassTop = new Color(58, 135, 42); grassBot = new Color(38, 98, 28); }
        else if (level <= 6) { grassTop = new Color(72, 128, 38); grassBot = new Color(48, 98, 24); }
        else if (level <= 9) { grassTop = new Color(48, 100, 30); grassBot = new Color(28, 70, 18); }
        else                  { grassTop = new Color(22, 55, 15); grassBot = new Color(12, 38, 8); }

        GradientPaint gp = new GradientPaint(0, groundY, grassTop, 0, h, grassBot);
        g.setPaint(gp);
        g.fillRect(0, groundY, arenaW, 80);
        g.setPaint(null);

        // dirt path
        g.setColor(new Color(158, 118, 58, 140));
        int[] pathX = {arenaW / 2 - 28, arenaW / 2 + 28, arenaW / 2 + 22, arenaW / 2 - 22};
        int[] pathY = {groundY, groundY, h, h};
        g.fillPolygon(pathX, pathY, 4);
        // path texture
        g.setColor(new Color(178, 142, 82, 60));
        for (int px = arenaW / 2 - 20; px < arenaW / 2 + 20; px += 8) {
            for (int py = groundY + 8; py < h; py += 10) {
                g.fillOval(px + (int)(Math.sin(py * 0.3) * 3), py, 5, 3);
            }
        }

        // grass tufts – main row
        Color tGrass = level <= 6 ? new Color(72, 165, 52) : new Color(42, 108, 28);
        g.setColor(tGrass);
        for (int i = 0; i < arenaW; i += 16) {
            int sw = (int)(Math.sin(tick * 0.04 + i * 0.15) * 2);
            g.fillArc(i - 2 + sw, groundY - 8, 15, 14, 0, 180);
        }
        // secondary tufts
        g.setColor(new Color(tGrass.getRed() + 28, tGrass.getGreen() + 35,
                             tGrass.getBlue() + 12, 145));
        for (int i = 6; i < arenaW; i += 22) {
            int sw = (int)(Math.sin(tick * 0.04 + i * 0.2) * 2);
            g.fillArc(i + sw, groundY - 4, 10, 10, 0, 180);
        }

        // ground edge line
        g.setColor(new Color(38, 108, 25));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(0, groundY, arenaW, groundY);
        g.setStroke(new BasicStroke(1f));

        // pebbles scattered on ground
        g.setColor(new Color(165, 148, 118, 120));
        int[] px = {30, 75, 140, 200, 310, 400, 460, 520};
        int[] py2 = {groundY + 18, groundY + 32, groundY + 22, groundY + 40,
                     groundY + 28, groundY + 15, groundY + 35, groundY + 22};
        for (int i = 0; i < px.length; i++)
            g.fillOval(px[i], py2[i], 5, 3);
    }

    // ── utility ───────────────────────────────────────────────────────────────

    private static Color transparent(Color c) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), 0);
    }

    private BackgroundRenderer() {}
}