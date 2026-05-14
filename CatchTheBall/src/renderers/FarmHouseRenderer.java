package OOP_PROJECT.CatchTheBall.src.renderers;

import java.awt.*;
import java.awt.geom.*;

/**
 * FarmHouseRenderer — fully redrawn with pixel-art-inspired style,
 * rich gradients, layered detail, and 8 visually distinct tiers.
 * Each tier has unique architectural character.
 */
public class FarmHouseRenderer {

    public static void draw(Graphics2D g, int stage, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
        switch (getHouseType(stage)) {
            case 0: drawOldShack     (g2, x, y, w, h); break;
            case 1: drawSimpleHouse  (g2, x, y, w, h); break;
            case 2: drawFarmHouse    (g2, x, y, w, h); break;
            case 3: drawVillage      (g2, x, y, w, h); break;
            case 4: drawGrandFarm    (g2, x, y, w, h); break;
            case 5: drawCastleFarm   (g2, x, y, w, h); break;
            case 6: drawRoyalPalace  (g2, x, y, w, h); break;
            case 7: drawLegendMansion(g2, x, y, w, h); break;
        }
        g2.dispose();
    }

    private static int getHouseType(int stage) {
        if (stage <= 2)  return 0;
        if (stage <= 5)  return 1;
        if (stage <= 8)  return 2;
        if (stage <= 11) return 3;
        if (stage <= 14) return 4;
        if (stage <= 17) return 5;
        if (stage <= 19) return 6;
        return 7;
    }

    // ── shared helpers ────────────────────────────────────────────────────────

    private static void drawWindow(Graphics2D g, int x, int y, int w, int h, Color glass) {
        // drop shadow
        g.setColor(new Color(0,0,0,45));
        g.fillRoundRect(x+2, y+2, w, h, 5, 5);
        // outer frame
        GradientPaint fp = new GradientPaint(x-4, y-4, new Color(140, 100, 50),
                x+w+4, y+h+4, new Color(90, 60, 25));
        g.setPaint(fp); g.fillRoundRect(x-5, y-5, w+10, h+10, 8, 8); g.setPaint(null);
        // inner reveal shadow
        g.setColor(new Color(55, 35, 12));
        g.fillRoundRect(x-2, y-2, w+4, h+4, 6, 6);
        // glass pane
        GradientPaint gp = new GradientPaint(x, y, lighter(glass, 55), x+w, y+h, glass);
        g.setPaint(gp); g.fillRoundRect(x, y, w, h, 4, 4); g.setPaint(null);
        // cross dividers
        g.setColor(new Color(85, 65, 35, 195));
        g.setStroke(new BasicStroke(1.8f));
        g.drawLine(x+w/2, y, x+w/2, y+h);
        g.drawLine(x, y+h/2, x+w, y+h/2);
        g.setStroke(new BasicStroke(1f));
        // gleam
        g.setColor(new Color(255, 255, 255, 145));
        g.fillOval(x+2, y+2, w/3, h/3);
        g.setColor(new Color(255, 255, 255, 60));
        g.fillOval(x+4, y+3, 3, 2);
        // sill
        GradientPaint sillG = new GradientPaint(x-7, y+h, new Color(190,158,90),
                x+w+7, y+h+5, new Color(148,112,55));
        g.setPaint(sillG); g.fillRoundRect(x-7, y+h, w+14, 7, 4, 4); g.setPaint(null);
        // frame outline
        g.setColor(new Color(68, 45, 16));
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(x-5, y-5, w+10, h+10, 8, 8);
        g.setStroke(new BasicStroke(1f));
    }

    private static void drawDoor(Graphics2D g, int x, int y, int w, int h, Color col) {
        // shadow
        g.setColor(new Color(0,0,0,45));
        g.fillRoundRect(x+2, y+2, w, h+2, 7, 7);
        // door body gradient
        GradientPaint dp = new GradientPaint(x, y, lighter(col, 35), x+w, y+h, darker(col, 30));
        g.setPaint(dp); g.fillRoundRect(x, y, w, h, 7, 7); g.setPaint(null);
        // panel recesses
        int ph2 = (h-14)/2;
        g.setColor(darker(col, 20));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x+4, y+5, w-8, ph2, 4, 4);
        g.drawRoundRect(x+4, y+8+ph2, w-8, ph2, 4, 4);
        g.setStroke(new BasicStroke(1f));
        // panel inner highlight
        g.setColor(new Color(255,255,255,28));
        g.fillRoundRect(x+5, y+6, (w-8)/2, ph2/2, 3, 3);
        // door highlight strip
        g.setColor(lighter(col, 60));
        g.fillRect(x+3, y+3, 3, h-6);
        // knob
        g.setColor(new Color(215, 178, 58));
        g.fillOval(x+w-11, y+h/2-4, 8, 8);
        g.setColor(new Color(255, 222, 85));
        g.fillOval(x+w-10, y+h/2-3, 3, 3);
        g.setColor(new Color(155, 122, 22));
        g.setStroke(new BasicStroke(0.8f));
        g.drawOval(x+w-11, y+h/2-4, 8, 8);
        g.setStroke(new BasicStroke(1f));
        // outline
        g.setColor(darker(col, 50));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x, y, w, h, 7, 7);
        g.setStroke(new BasicStroke(1f));
    }

    private static void drawChimney(Graphics2D g, int x, int y, int w, int h,
                                     Color brickC, Color topC) {
        GradientPaint cp = new GradientPaint(x, y, lighter(brickC, 25),
                x+w, y+h, darker(brickC, 20));
        g.setPaint(cp); g.fillRect(x, y, w, h); g.setPaint(null);
        // brick courses
        g.setColor(darker(brickC, 30));
        g.setStroke(new BasicStroke(0.8f));
        for (int r = 0; r < h/5; r++) g.drawLine(x, y+r*5, x+w, y+r*5);
        g.setStroke(new BasicStroke(1f));
        // cap
        g.setColor(topC);
        g.fillRect(x-3, y, w+6, 6);
        g.setColor(darker(topC, 25));
        g.setStroke(new BasicStroke(1f));
        g.drawRect(x-3, y, w+6, 6);
        g.setStroke(new BasicStroke(1f));
        // mortar highlight
        g.setColor(lighter(brickC, 50));
        g.fillRect(x+1, y+1, w-2, 2);
    }

    private static void drawSmoke(Graphics2D g, int cx, int startY, int count) {
        for (int s = 0; s < count; s++) {
            float alpha = 70f - s * 18f;
            if (alpha <= 0) break;
            int r = 6 + s*5;
            g.setColor(new Color(195, 192, 188, (int)alpha));
            g.fillOval(cx - r/2 + s*2, startY - r - s*10, r, r);
        }
    }

    private static void drawStarBadge(Graphics2D g, int x, int y, int count) {
        g.setColor(new Color(0,0,0,80));
        g.fillRoundRect(x-40, y, 40, 16, 6, 6);
        g.setColor(new Color(255, 222, 35, 215));
        g.setFont(new Font("SansSerif", Font.BOLD, 9));
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < Math.min(count, 8); i++) s.append("\u2605");
        g.drawString(s.toString(), x - 36, y + 11);
    }

    // ── 0: OLD SHACK (stages 1-2) ─────────────────────────────────────────────
    private static void drawOldShack(Graphics2D g, int x, int y, int w, int h) {
        int bx = x+w/6, by = y+h/2, bw = w*2/3, bh = h/2;

        g.setColor(new Color(0,0,0,38));
        g.fillOval(bx-2, by+bh-2, bw+4, 10);

        // aged wood walls
        GradientPaint wall = new GradientPaint(bx, by, new Color(172, 132, 82),
                bx+bw, by+bh, new Color(115, 80, 40));
        g.setPaint(wall); g.fillRect(bx, by, bw, bh); g.setPaint(null);

        // wood plank horizontal lines
        g.setColor(new Color(88, 58, 26, 155));
        g.setStroke(new BasicStroke(1.5f));
        for (int i = 1; i <= 5; i++) g.drawLine(bx, by+bh*i/6, bx+bw, by+bh*i/6);
        g.setStroke(new BasicStroke(1f));
        // plank gap highlight
        g.setColor(new Color(215, 185, 128, 35));
        for (int i = 1; i <= 5; i++) g.drawLine(bx, by+bh*i/6+1, bx+bw, by+bh*i/6+1);

        // left highlight
        g.setColor(new Color(255, 225, 162, 38));
        g.fillRect(bx, by, bw/4, bh);
        // right shadow
        g.setColor(new Color(55, 32, 10, 55));
        g.fillRect(bx+bw*3/4, by, bw/4, bh);

        // slightly-sagging roof
        int[] rx2 = {bx-10, bx+bw/2+4, bx+bw+10};
        int[] ry2 = {by, by-h/3, by};
        GradientPaint roof = new GradientPaint(bx, by-h/3, new Color(98, 68, 36),
                bx+bw, by, new Color(58, 38, 16));
        g.setPaint(roof); g.fillPolygon(rx2, ry2, 3); g.setPaint(null);
        // shingle rows
        g.setColor(new Color(42, 26, 8, 120));
        g.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < 4; i++) {
            int indent = i*8;
            int ry3 = by - (h/3)*(4-i)/4;
            g.drawLine(bx-10+indent, ry3, bx+bw+10-indent, ry3);
        }
        g.setStroke(new BasicStroke(1f));
        // roof highlight
        g.setColor(new Color(135, 100, 58, 60));
        g.fillPolygon(new int[]{bx-10, bx+bw/2+4, bx+bw/4}, new int[]{by, by-h/3, by}, 3);
        // moss
        g.setColor(new Color(58, 132, 38, 85));
        g.fillOval(bx+bw/2-14, by-12, 28, 10);
        g.fillOval(bx+bw/3-6, by-8, 18, 7);

        // crooked chimney
        Graphics2D gc = (Graphics2D) g.create();
        gc.rotate(Math.toRadians(4), bx+bw-18, by-10);
        drawChimney(gc, bx+bw-24, by-h/3+4, 16, h/3+10, new Color(132, 92, 60),
                    new Color(112, 74, 46));
        gc.dispose();
        drawSmoke(g, bx+bw-16, by-h/3, 3);

        // cracked window
        drawWindow(g, bx+6, by+7, 20, 16, new Color(155, 198, 218, 175));
        // crack on window glass
        g.setColor(new Color(38, 28, 18, 160));
        g.setStroke(new BasicStroke(1f));
        g.drawLine(bx+8, by+8, bx+22, by+20);
        g.setStroke(new BasicStroke(1f));

        // broken door
        drawDoor(g, bx+bw/2-10, by+bh/2, 20, bh/2, new Color(78, 48, 18));
        // door crack
        g.setColor(new Color(32, 16, 4, 170));
        g.setStroke(new BasicStroke(1.2f));
        g.drawLine(bx+bw/2, by+bh/2, bx+bw/2-5, by+bh);
        g.setStroke(new BasicStroke(1f));

        // outline
        g.setColor(new Color(55, 36, 12));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(bx, by, bw, bh);
        g.drawPolygon(rx2, ry2, 3);
        g.setStroke(new BasicStroke(1f));

        drawStarBadge(g, x+w-2, y+6, 1);
    }

    // ── 1: SIMPLE HOUSE (stages 3-5) ─────────────────────────────────────────
    private static void drawSimpleHouse(Graphics2D g, int x, int y, int w, int h) {
        int bx = x+w/8, by = y+h*2/5, bw = w*3/4, bh = h*3/5;

        g.setColor(new Color(0,0,0,38));
        g.fillOval(bx-2, by+bh-2, bw+4, 12);

        // cream walls with horizontal siding
        GradientPaint wall = new GradientPaint(bx, by, new Color(252, 238, 202),
                bx, by+bh, new Color(220, 195, 158));
        g.setPaint(wall); g.fillRect(bx, by, bw, bh); g.setPaint(null);
        g.setColor(new Color(185, 162, 118, 75));
        g.setStroke(new BasicStroke(0.8f));
        for (int i = 1; i < 8; i++) g.drawLine(bx, by+bh*i/8, bx+bw, by+bh*i/8);
        g.setStroke(new BasicStroke(1f));

        // left highlight / right shadow
        g.setColor(new Color(255,245,215,42));
        g.fillRect(bx, by, bw/5, bh);
        g.setColor(new Color(55,35,12,42));
        g.fillRect(bx+bw*4/5, by, bw/5, bh);

        // terracotta roof
        int[] rx2 = {bx-12, bx+bw/2, bx+bw+12};
        int[] ry2 = {by, by-h*2/5, by};
        GradientPaint roof = new GradientPaint(bx, by-h*2/5, new Color(212, 92, 68),
                bx+bw, by, new Color(158, 52, 32));
        g.setPaint(roof); g.fillPolygon(rx2, ry2, 3); g.setPaint(null);
        // shingles
        g.setColor(new Color(138, 42, 22, 80));
        g.setStroke(new BasicStroke(1.2f));
        for (int i = 0; i < 5; i++) {
            int indent = i*7;
            g.drawLine(bx-12+indent, by-(h*2/5)*(4-i)/4, bx+bw+12-indent, by-(h*2/5)*(4-i)/4);
        }
        g.setStroke(new BasicStroke(1f));
        // roof left highlight
        g.setColor(new Color(255, 155, 135, 58));
        g.fillPolygon(new int[]{bx-12, bx+bw/2, bx+bw/4}, new int[]{by, by-h*2/5, by}, 3);
        // ridge cap
        g.setColor(new Color(135, 40, 20));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(bx+bw/2, by-h*2/5, bx+bw/2, by);
        g.setStroke(new BasicStroke(1f));

        // chimney
        drawChimney(g, bx+bw-32, by-h*2/5+6, 16, h*2/5+6,
                    new Color(162, 112, 72), new Color(142, 90, 55));
        drawSmoke(g, bx+bw-24, by-h*2/5, 3);

        // windows with flower boxes
        drawWindow(g, bx+10, by+10, 30, 24, new Color(182, 222, 255, 205));
        drawWindow(g, bx+bw-40, by+10, 30, 24, new Color(182, 222, 255, 205));
        // flower boxes
        g.setColor(new Color(142, 82, 36));
        g.fillRoundRect(bx+3, by+36, 44, 8, 4, 4);
        g.fillRoundRect(bx+bw-47, by+36, 44, 8, 4, 4);
        Color[] fc = {new Color(255,85,85), new Color(255,205,55), new Color(205,85,225)};
        for (int fi = 0; fi < 3; fi++) {
            // flower stem
            g.setColor(new Color(48,142,28));
            g.fillRect(bx+13+fi*14, by+28, 2, 10);
            g.fillRect(bx+bw-45+fi*14, by+28, 2, 10);
            // petals
            g.setColor(fc[fi]);
            g.fillOval(bx+11+fi*14, by+22, 9, 9);
            g.fillOval(bx+bw-47+fi*14, by+22, 9, 9);
            // center
            g.setColor(new Color(255,235,80));
            g.fillOval(bx+13+fi*14, by+24, 4, 4);
            g.fillOval(bx+bw-45+fi*14, by+24, 4, 4);
        }

        // door
        drawDoor(g, bx+bw/2-14, by+bh/2, 28, bh/2, new Color(102, 64, 22));

        // picket fence
        g.setColor(new Color(242, 232, 210));
        for (int i = 0; i <= 7; i++) {
            int fx = bx-10+i*(bw+20)/7;
            g.fillRoundRect(fx-2, by+bh+2, 7, 20, 3, 3);
            // pointed tip
            g.fillPolygon(new int[]{fx, fx+3, fx+7},
                          new int[]{by+bh+2, by+bh-4, by+bh+2}, 3);
        }
        g.setColor(new Color(218, 208, 185));
        g.fillRect(bx-10, by+bh+10, bw+20, 4);
        g.fillRect(bx-10, by+bh+15, bw+20, 3);

        // outline
        g.setColor(new Color(118, 88, 48));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(bx, by, bw, bh);
        g.drawPolygon(rx2, ry2, 3);
        g.setStroke(new BasicStroke(1f));

        drawStarBadge(g, x+w-2, y+6, 2);
    }

    // ── 2: FARM HOUSE (stages 6-8) ────────────────────────────────────────────
    private static void drawFarmHouse(Graphics2D g, int x, int y, int w, int h) {
        int bx = x+w/10, by = y+h/3, bw = w*4/5, bh = h*2/3;

        g.setColor(new Color(0,0,0,42));
        g.fillOval(bx-2, by+bh-2, bw+4, 14);

        // main walls – warm white with subtle brick texture
        GradientPaint wall = new GradientPaint(bx, by, new Color(252, 244, 228),
                bx, by+bh, new Color(218, 204, 175));
        g.setPaint(wall); g.fillRect(bx, by, bw, bh); g.setPaint(null);
        // brick pattern
        g.setColor(new Color(192, 155, 112, 58));
        g.setStroke(new BasicStroke(0.7f));
        for (int row = 0; row < 7; row++) {
            int offX = (row%2==0) ? 0 : 15;
            for (int col = -1; col < bw/30+1; col++)
                g.drawRoundRect(bx+col*30+offX, by+row*(bh/7), 28, bh/7-1, 2, 2);
        }
        g.setStroke(new BasicStroke(1f));

        // side barn wing
        int wgW = bw/4, wgH = bh*3/4;
        GradientPaint barnG = new GradientPaint(bx-wgW, by+bh-wgH,
                new Color(198, 60, 48), bx, by+bh, new Color(148, 36, 26));
        g.setPaint(barnG); g.fillRect(bx-wgW, by+bh-wgH, wgW, wgH); g.setPaint(null);
        // barn plank lines
        g.setColor(new Color(100, 22, 14, 105));
        g.setStroke(new BasicStroke(1.2f));
        for (int i = 1; i < 5; i++) g.drawLine(bx-wgW, by+bh-wgH+wgH*i/5, bx, by+bh-wgH+wgH*i/5);
        g.setStroke(new BasicStroke(1f));
        // X brace on barn
        g.setColor(new Color(125, 38, 22, 140));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(bx-wgW+2, by+bh-wgH, bx-2, by+bh);
        g.drawLine(bx-2, by+bh-wgH, bx-wgW+2, by+bh);
        g.setStroke(new BasicStroke(1f));
        // barn wing roof
        int[] brx2 = {bx-wgW-6, bx-wgW/2, bx+6};
        int[] bry2 = {by+bh-wgH, by+bh-wgH-28, by+bh-wgH};
        GradientPaint bRoof = new GradientPaint(bx-wgW, by+bh-wgH-28,
                new Color(80, 55, 28), bx, by+bh-wgH, new Color(50, 30, 12));
        g.setPaint(bRoof); g.fillPolygon(brx2, bry2, 3); g.setPaint(null);
        // loft window
        g.setColor(new Color(28, 10, 6, 210));
        g.fillOval(bx-wgW/2-9, by+bh-wgH-20, 18, 16);
        g.setColor(new Color(155, 195, 215, 120));
        g.fillOval(bx-wgW/2-7, by+bh-wgH-18, 14, 12);

        // main roof – deep red
        int[] rx2 = {bx-14, bx+bw/2, bx+bw+14};
        int[] ry2 = {by, by-h/3+6, by};
        GradientPaint roofG = new GradientPaint(bx, by-h/3, new Color(192, 65, 46),
                bx+bw, by, new Color(138, 40, 24));
        g.setPaint(roofG); g.fillPolygon(rx2, ry2, 3); g.setPaint(null);
        // shingle rows
        g.setColor(new Color(100, 32, 16, 105));
        g.setStroke(new BasicStroke(1.4f));
        for (int i = 0; i < 6; i++) {
            int indent = i*9;
            int ry3 = by-(h/3-6)*(5-i)/5;
            g.drawLine(bx-14+indent, ry3, bx+bw+14-indent, ry3);
        }
        g.setStroke(new BasicStroke(1f));
        // ridge cap
        g.setColor(new Color(115, 35, 18));
        g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(bx+bw/2, by-h/3+6, bx+bw/2, by);
        g.setStroke(new BasicStroke(1f));

        // chimney pair
        drawChimney(g, bx+bw-38, by-h/3-10, 15, h/3+14,
                    new Color(160, 110, 70), new Color(132, 85, 50));
        drawChimney(g, bx+bw-22, by-h/3-5, 13, h/3+9,
                    new Color(155, 105, 65), new Color(128, 82, 48));
        drawSmoke(g, bx+bw-30, by-h/3, 3);

        // windows
        drawWindow(g, bx+10, by+12, 34, 28, new Color(198, 230, 255, 205));
        drawWindow(g, bx+bw-46, by+12, 34, 28, new Color(198, 230, 255, 205));

        // porch gable
        g.setColor(new Color(138, 95, 40));
        g.fillPolygon(new int[]{bx+bw/2-20, bx+bw/2, bx+bw/2+20},
                      new int[]{by+bh/2-6, by+bh/2-16, by+bh/2-6}, 3);
        g.setColor(new Color(175, 128, 65));
        g.fillRect(bx+bw/2-18, by+bh/2-6, 36, 4);

        drawDoor(g, bx+bw/2-15, by+bh/2, 30, bh/2, new Color(112, 68, 24));

        // outline
        g.setColor(new Color(98, 68, 32));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(bx, by, bw, bh);
        g.drawPolygon(rx2, ry2, 3);
        g.setStroke(new BasicStroke(1f));

        drawStarBadge(g, x+w-2, y+6, 3);
    }

    // ── 3: VILLAGE (stages 9-11) ──────────────────────────────────────────────
    private static void drawVillage(Graphics2D g, int x, int y, int w, int h) {
        int bx = x+4, by = y+h/4, bw = w-8, bh = h*3/4;
        Color glass = new Color(198, 230, 255, 205);

        g.setColor(new Color(0,0,0,42));
        g.fillOval(bx-2, by+bh-2, bw+4, 14);

        // back centre house
        int backW = bw*2/5, backH = bh*3/4;
        int backX = bx+bw/2-backW/2;
        GradientPaint bkW = new GradientPaint(backX, by, new Color(212, 220, 240),
                backX, by+backH, new Color(165, 175, 205));
        g.setPaint(bkW); g.fillRect(backX, by, backW, backH); g.setPaint(null);
        int[] bkrx = {backX-10, backX+backW/2, backX+backW+10};
        int[] bkry = {by, by-h/5, by};
        GradientPaint bkRoof = new GradientPaint(backX, by-h/5, new Color(88, 65, 152),
                backX+backW, by, new Color(62, 42, 118));
        g.setPaint(bkRoof); g.fillPolygon(bkrx, bkry, 3); g.setPaint(null);
        drawWindow(g, backX+7, by+9, 24, 19, glass);
        drawWindow(g, backX+backW-31, by+9, 24, 19, glass);
        drawDoor(g, backX+backW/2-9, by+backH-28, 18, 28, new Color(95, 58, 18));
        g.setColor(new Color(62, 42, 118));
        g.setStroke(new BasicStroke(1.5f));
        g.drawPolygon(bkrx, bkry, 3);
        g.setStroke(new BasicStroke(1f));

        // left house
        int lw2 = bw*2/5, lh = bh*4/5, lx = bx;
        GradientPaint lW = new GradientPaint(lx, by+bh-lh, new Color(248, 228, 188),
                lx, by+bh, new Color(210, 185, 145));
        g.setPaint(lW); g.fillRect(lx, by+bh-lh, lw2, lh); g.setPaint(null);
        int[] lrx = {lx-10, lx+lw2/2, lx+lw2+5};
        int[] lry = {by+bh-lh, by+bh-lh-h/4, by+bh-lh};
        GradientPaint lRoof = new GradientPaint(lx, by+bh-lh-h/4, new Color(198, 75, 52),
                lx+lw2, by+bh-lh, new Color(152, 48, 30));
        g.setPaint(lRoof); g.fillPolygon(lrx, lry, 3); g.setPaint(null);
        drawWindow(g, lx+9, by+bh-lh+9, 28, 22, glass);
        drawDoor(g, lx+lw2/2-11, by+bh-lh+lh/2, 22, lh/2, new Color(102, 62, 18));
        g.setColor(new Color(152, 48, 30));
        g.setStroke(new BasicStroke(1.5f));
        g.drawPolygon(lrx, lry, 3); g.drawRect(lx, by+bh-lh, lw2, lh);
        g.setStroke(new BasicStroke(1f));
        // left chimney
        drawChimney(g, lx+lw2-20, by+bh-lh-h/4+5, 12, h/4+8,
                    new Color(158, 108, 68), new Color(130, 85, 52));
        drawSmoke(g, lx+lw2-14, by+bh-lh-h/4, 2);

        // right house
        int rw2 = bw*2/5, rh = bh*4/5, rx3 = bx+bw-rw2;
        GradientPaint rW = new GradientPaint(rx3, by+bh-rh, new Color(238, 220, 208),
                rx3, by+bh, new Color(200, 180, 160));
        g.setPaint(rW); g.fillRect(rx3, by+bh-rh, rw2, rh); g.setPaint(null);
        int[] rrx = {rx3-5, rx3+rw2/2, rx3+rw2+10};
        int[] rry = {by+bh-rh, by+bh-rh-h/4, by+bh-rh};
        GradientPaint rRoof = new GradientPaint(rx3, by+bh-rh-h/4, new Color(60, 108, 168),
                rx3+rw2, by+bh-rh, new Color(38, 80, 135));
        g.setPaint(rRoof); g.fillPolygon(rrx, rry, 3); g.setPaint(null);
        drawWindow(g, rx3+rw2-38, by+bh-rh+9, 28, 22, glass);
        drawDoor(g, rx3+rw2/2-11, by+bh-rh+rh/2, 22, rh/2, new Color(102, 62, 18));
        g.setColor(new Color(38, 80, 135));
        g.setStroke(new BasicStroke(1.5f));
        g.drawPolygon(rrx, rry, 3); g.drawRect(rx3, by+bh-rh, rw2, rh);
        g.setStroke(new BasicStroke(1f));

        // cobblestone path
        g.setColor(new Color(175, 158, 118, 158));
        int[] pathX = {bx+bw/2-15, bx+bw/2+15, bx+bw/2+12, bx+bw/2-12};
        int[] pathY = {by+bh-lh/2, by+bh-lh/2, by+bh, by+bh};
        g.fillPolygon(pathX, pathY, 4);
        g.setColor(new Color(148, 130, 92, 78));
        for (int ci = 0; ci < 5; ci++)
            g.fillOval(bx+bw/2-6+ci*3, by+bh-18+ci*4, 6, 4);

        drawStarBadge(g, x+w-2, y+6, 4);
    }

    // ── 4: GRAND FARM (stages 12-14) ─────────────────────────────────────────
    private static void drawGrandFarm(Graphics2D g, int x, int y, int w, int h) {
        int bx = x+4, by = y+h/5, bw = w-8, bh = h*4/5;
        Color glass = new Color(198, 230, 255, 205);

        g.setColor(new Color(0,0,0,46));
        g.fillOval(bx-2, by+bh-2, bw+4, 16);

        // main body
        int mw = bw*3/5, mx = bx+bw/2-mw/2;
        GradientPaint mW = new GradientPaint(mx, by, new Color(252, 240, 215),
                mx, by+bh, new Color(218, 198, 162));
        g.setPaint(mW); g.fillRect(mx, by, mw, bh); g.setPaint(null);

        // quoin stones at corners
        g.setColor(new Color(178, 155, 120));
        for (int qi = 0; qi < 6; qi++) {
            g.fillRect(mx-3, by+qi*(bh/6), 9, bh/12);
            g.fillRect(mx+mw-6, by+qi*(bh/6), 9, bh/12);
            // quoin highlight
            g.setColor(new Color(212, 195, 162));
            g.fillRect(mx-2, by+qi*(bh/6), 2, bh/12);
            g.setColor(new Color(178, 155, 120));
        }

        // stone base course
        g.setColor(new Color(168, 152, 122));
        g.fillRect(mx, by+bh-16, mw, 16);
        g.setColor(new Color(142, 125, 98, 135));
        g.setStroke(new BasicStroke(0.8f));
        for (int i = 0; i < 7; i++) g.drawRect(mx+2+i*(mw/7), by+bh-16, mw/7-2, 14);
        g.setStroke(new BasicStroke(1f));

        // side towers
        int twW = bw/5;
        for (int side = 0; side <= 1; side++) {
            int tx = side==0 ? bx : bx+bw-twW;
            int twH = bh*3/4;
            GradientPaint tW = new GradientPaint(tx, by+bh-twH, new Color(230, 210, 175),
                    tx+twW, by+bh, new Color(192, 170, 135));
            g.setPaint(tW); g.fillRect(tx, by+bh-twH, twW, twH); g.setPaint(null);
            // tower cone roof
            int[] trx = {tx-6, tx+twW/2, tx+twW+6};
            int[] try2 = {by+bh-twH, by+bh-twH-h/5, by+bh-twH};
            GradientPaint tRoof = new GradientPaint(tx, by+bh-twH-h/5, new Color(62, 105, 168),
                    tx+twW, by+bh-twH, new Color(40, 78, 132));
            g.setPaint(tRoof); g.fillPolygon(trx, try2, 3); g.setPaint(null);
            // weather vane
            g.setColor(new Color(182, 142, 32));
            g.setStroke(new BasicStroke(1.5f));
            g.drawLine(tx+twW/2, by+bh-twH-h/5, tx+twW/2, by+bh-twH-h/5-14);
            g.setStroke(new BasicStroke(1f));
            g.fillOval(tx+twW/2-3, by+bh-twH-h/5-16, 6, 6);
            // arrow on vane
            g.setColor(new Color(218, 178, 38));
            g.fillPolygon(new int[]{tx+twW/2, tx+twW/2+8, tx+twW/2},
                          new int[]{by+bh-twH-h/5-8, by+bh-twH-h/5-11, by+bh-twH-h/5-14}, 3);
            drawWindow(g, tx+twW/2-11, by+bh-twH+7, 22, 17, glass);
            g.setColor(new Color(80, 58, 28));
            g.setStroke(new BasicStroke(1.5f));
            g.drawRect(tx, by+bh-twH, twW, twH);
            g.drawPolygon(trx, try2, 3);
            g.setStroke(new BasicStroke(1f));
        }

        // main roof
        int[] rx2 = {mx-16, mx+mw/2, mx+mw+16};
        int[] ry2 = {by, by-h/4, by};
        GradientPaint roofG = new GradientPaint(mx, by-h/4, new Color(185, 62, 40),
                mx+mw, by, new Color(140, 38, 22));
        g.setPaint(roofG); g.fillPolygon(rx2, ry2, 3); g.setPaint(null);
        // shingles
        g.setColor(new Color(105, 30, 14, 110));
        g.setStroke(new BasicStroke(1.3f));
        for (int i = 0; i < 5; i++) {
            int indent = i*9;
            g.drawLine(mx-16+indent, by-(h/4)*(5-i)/5, mx+mw+16-indent, by-(h/4)*(5-i)/5);
        }
        g.setStroke(new BasicStroke(1f));
        // ridge tile row
        g.setColor(new Color(105, 30, 14));
        g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(mx+mw/2, by-h/4, mx+mw/2, by);
        g.drawLine(mx-16, by, mx+mw+16, by);
        g.setStroke(new BasicStroke(1f));

        // chimney pair
        drawChimney(g, mx+mw-44, by-h/4, 15, h/4+10, new Color(160, 110, 70), new Color(130, 85, 52));
        drawChimney(g, mx+mw-28, by-h/4+5, 13, h/4+5, new Color(155, 105, 65), new Color(128, 80, 50));
        drawSmoke(g, mx+mw-36, by-h/4, 3);

        // windows
        drawWindow(g, mx+12, by+12, 36, 30, glass);
        drawWindow(g, mx+mw-50, by+12, 36, 30, glass);
        drawWindow(g, mx+mw/2-15, by+12, 30, 24, glass);

        // columns
        g.setColor(new Color(242, 235, 218));
        g.fillRect(mx+mw/2-24, by+bh/2-14, 7, bh/2+14);
        g.fillRect(mx+mw/2+17, by+bh/2-14, 7, bh/2+14);
        // column caps
        g.setColor(new Color(200, 192, 170));
        g.fillRoundRect(mx+mw/2-28, by+bh/2-16, 15, 7, 4, 4);
        g.fillRoundRect(mx+mw/2+13, by+bh/2-16, 15, 7, 4, 4);
        // column bases
        g.fillRoundRect(mx+mw/2-28, by+bh-12, 15, 7, 4, 4);
        g.fillRoundRect(mx+mw/2+13, by+bh-12, 15, 7, 4, 4);

        drawDoor(g, mx+mw/2-18, by+bh/2, 17, bh/2, new Color(102, 62, 18));
        drawDoor(g, mx+mw/2+1,  by+bh/2, 17, bh/2, new Color(102, 62, 18));

        // outline
        g.setColor(new Color(98, 72, 36));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(mx, by, mw, bh);
        g.drawPolygon(rx2, ry2, 3);
        g.setStroke(new BasicStroke(1f));

        drawStarBadge(g, x+w-2, y+6, 5);
    }

    // ── 5: CASTLE FARM (stages 15-17) ─────────────────────────────────────────
    private static void drawCastleFarm(Graphics2D g, int x, int y, int w, int h) {
        int bx = x+2, by = y+h/6, bw = w-4, bh = h*5/6;
        Color glass = new Color(198, 228, 255, 205);

        g.setColor(new Color(0,0,0,52));
        g.fillOval(bx-2, by+bh-2, bw+4, 18);

        // stone wall
        GradientPaint stone = new GradientPaint(bx, by, new Color(195, 192, 205),
                bx+bw, by+bh, new Color(150, 148, 165));
        g.setPaint(stone); g.fillRect(bx, by, bw, bh); g.setPaint(null);
        // stone block grid
        g.setColor(new Color(118, 115, 132, 100));
        g.setStroke(new BasicStroke(0.9f));
        for (int row = 0; row < 8; row++) {
            int offX = (row%2==0) ? 0 : 19;
            for (int col = -1; col < bw/38+1; col++)
                g.drawRoundRect(bx+col*38+offX, by+row*(bh/8), 36, bh/8-1, 2, 2);
        }
        g.setStroke(new BasicStroke(1f));

        // crenellations
        g.setColor(new Color(178, 175, 192));
        for (int cr = 0; cr < bw/20; cr++)
            if (cr%2==0) g.fillRect(bx+cr*20, by-15, 18, 15);

        // corner towers
        int[] tPos = {bx, bx+bw-26};
        for (int tp : tPos) {
            GradientPaint tW = new GradientPaint(tp, by-24, new Color(178, 175, 192),
                    tp+26, by+bh, new Color(135, 132, 152));
            g.setPaint(tW); g.fillRect(tp, by-24, 26, bh+24); g.setPaint(null);
            // tower crenellations
            g.setColor(new Color(162, 158, 178));
            for (int ct = 0; ct < 4; ct++)
                if (ct%2==0) g.fillRect(tp+ct*7, by-36, 7, 14);
            // arrow slits
            g.setColor(new Color(38, 35, 55));
            g.fillRoundRect(tp+10, by-10, 5, 18, 2, 2);
            g.fillRoundRect(tp+10, by+35, 5, 18, 2, 2);
            // tower outline
            g.setColor(new Color(82, 80, 102));
            g.setStroke(new BasicStroke(1.8f));
            g.drawRect(tp, by-24, 26, bh+24);
            g.setStroke(new BasicStroke(1f));
        }

        // central tower – taller
        int ctW = bw/3, ctX = bx+bw/2-ctW/2;
        GradientPaint ctW2 = new GradientPaint(ctX, by-42, new Color(182, 180, 198),
                ctX+ctW, by, new Color(140, 138, 158));
        g.setPaint(ctW2); g.fillRect(ctX, by-42, ctW, 42); g.setPaint(null);
        // conical roof
        int[] crx2 = {ctX-7, ctX+ctW/2, ctX+ctW+7};
        int[] cry2 = {by-42, by-42-h/4, by-42};
        GradientPaint coneG2 = new GradientPaint(ctX, by-42-h/4, new Color(50, 125, 62),
                ctX+ctW, by-42, new Color(32, 95, 44));
        g.setPaint(coneG2); g.fillPolygon(crx2, cry2, 3); g.setPaint(null);
        g.setColor(new Color(28, 90, 38));
        g.setStroke(new BasicStroke(1.2f)); g.drawPolygon(crx2, cry2, 3); g.setStroke(new BasicStroke(1f));
        // flag
        g.setColor(new Color(190, 42, 42));
        g.setStroke(new BasicStroke(1.8f));
        g.drawLine(ctX+ctW/2, by-42-h/4, ctX+ctW/2, by-42-h/4+24);
        g.setStroke(new BasicStroke(1f));
        g.fillPolygon(new int[]{ctX+ctW/2, ctX+ctW/2+16, ctX+ctW/2},
                      new int[]{by-42-h/4, by-42-h/4+8, by-42-h/4+16}, 3);

        // portcullis gate
        g.setColor(new Color(33, 30, 48));
        g.fillArc(bx+bw/2-24, by+bh/3, 48, 48, 0, 180);
        g.fillRect(bx+bw/2-24, by+bh/3+24, 48, bh*2/3-24);
        // portcullis bars
        g.setColor(new Color(78, 72, 95));
        g.setStroke(new BasicStroke(2.8f));
        for (int bar = 0; bar < 4; bar++)
            g.drawLine(bx+bw/2-19+bar*12, by+bh/3+5, bx+bw/2-19+bar*12, by+bh-2);
        g.drawLine(bx+bw/2-22, by+bh/3+24, bx+bw/2+22, by+bh/3+24);
        g.drawLine(bx+bw/2-22, by+bh/3+38, bx+bw/2+22, by+bh/3+38);
        g.setStroke(new BasicStroke(1f));

        drawWindow(g, bx+32, by+14, 30, 24, glass);
        drawWindow(g, bx+bw-62, by+14, 30, 24, glass);

        // moat
        GradientPaint moat = new GradientPaint(bx, by+bh, new Color(80, 128, 200, 185),
                bx, by+bh+12, new Color(50, 100, 180, 105));
        g.setPaint(moat); g.fillRect(bx, by+bh, bw, 12); g.setPaint(null);
        // moat ripple
        g.setColor(new Color(120, 175, 230, 80));
        g.setStroke(new BasicStroke(1f));
        g.drawArc(bx+10, by+bh+2, bw/3, 8, 0, 180);
        g.drawArc(bx+bw/2, by+bh+3, bw/3, 6, 0, 180);
        g.setStroke(new BasicStroke(1f));

        // wall outline
        g.setColor(new Color(80, 78, 100));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(bx, by, bw, bh);
        g.setStroke(new BasicStroke(1f));

        drawStarBadge(g, x+w-2, y+6, 6);
    }

    // ── 6: ROYAL PALACE (stages 18-19) ───────────────────────────────────────
    private static void drawRoyalPalace(Graphics2D g, int x, int y, int w, int h) {
        int bx = x+2, by = y+h/8, bw = w-4, bh = h*7/8;
        Color glass = new Color(255, 248, 185, 215);

        g.setColor(new Color(0,0,0,58));
        g.fillOval(bx-2, by+bh-2, bw+4, 18);

        // marble walls
        GradientPaint marble = new GradientPaint(bx, by, new Color(250, 246, 232),
                bx+bw, by+bh, new Color(215, 210, 194));
        g.setPaint(marble); g.fillRect(bx, by, bw, bh); g.setPaint(null);
        // marble veins
        g.setColor(new Color(195, 188, 168, 60));
        g.setStroke(new BasicStroke(0.9f));
        for (int v = 0; v < 7; v++)
            g.drawLine(bx+v*(bw/7), by, bx+v*(bw/7)+14, by+bh);
        g.setStroke(new BasicStroke(1f));

        // gold trim bands
        Color gold = new Color(220, 180, 40);
        g.setColor(gold);
        g.fillRect(bx, by, bw, 9);
        g.fillRect(bx, by+bh-14, bw, 14);
        g.fillRect(bx, by+bh/3-3, bw, 6);
        g.setColor(new Color(255, 215, 65, 85));
        g.fillRect(bx+1, by+1, bw-2, 5);
        g.fillRect(bx+1, by+bh/3-2, bw-2, 3);

        // five spire towers
        int[] spireXs = {bx, bx+bw/4, bx+bw/2-14, bx+bw*3/4-14, bx+bw-25};
        int[] spireHs = {bh*2/3, bh*3/5, bh*4/5, bh*3/5, bh*2/3};
        Color[] spireRoofs = {new Color(60,112,185), new Color(80,162,80),
                               new Color(182,50,50), new Color(80,162,80), new Color(60,112,185)};
        for (int sp = 0; sp < 5; sp++) {
            int sw = 25, sh = spireHs[sp];
            int sx = spireXs[sp];
            GradientPaint sW = new GradientPaint(sx, by+bh-sh, new Color(238, 232, 218),
                    sx+sw, by+bh, new Color(200, 194, 180));
            g.setPaint(sW); g.fillRect(sx, by+bh-sh, sw, sh); g.setPaint(null);
            // cone
            int[] srx = {sx-6, sx+sw/2, sx+sw+6};
            int[] sry = {by+bh-sh, by+bh-sh-h/4, by+bh-sh};
            GradientPaint coneG = new GradientPaint(sx, by+bh-sh-h/4,
                    lighter(spireRoofs[sp], 40), sx+sw, by+bh-sh, darker(spireRoofs[sp], 20));
            g.setPaint(coneG); g.fillPolygon(srx, sry, 3); g.setPaint(null);
            // gold finial ball
            g.setColor(new Color(255, 215, 0));
            g.fillOval(sx+sw/2-5, by+bh-sh-h/4-7, 10, 10);
            g.setColor(new Color(255, 245, 120));
            g.fillOval(sx+sw/2-3, by+bh-sh-h/4-5, 4, 4);
            // spire window
            drawWindow(g, sx+6, by+bh-sh+8, 14, 12, glass);
            // outline
            g.setColor(new Color(100, 88, 65));
            g.setStroke(new BasicStroke(1.5f));
            g.drawRect(sx, by+bh-sh, sw, sh);
            g.drawPolygon(srx, sry, 3);
            g.setStroke(new BasicStroke(1f));
        }

        // grand arched entrance
        g.setColor(new Color(182, 146, 60));
        g.fillRect(bx+bw/2-26, by+bh/2-12, 52, 8);
        g.setColor(new Color(110, 74, 24));
        g.fillArc(bx+bw/2-26, by+bh/3, 52, 52, 0, 180);
        g.fillRect(bx+bw/2-26, by+bh/3+26, 26, bh*2/3-26);
        g.fillRect(bx+bw/2, by+bh/3+26, 26, bh*2/3-26);
        // arch gold moulding
        g.setColor(new Color(222, 182, 40));
        g.setStroke(new BasicStroke(3f));
        g.drawArc(bx+bw/2-26, by+bh/3, 52, 52, 0, 180);
        g.setStroke(new BasicStroke(1f));
        g.drawLine(bx+bw/2, by+bh/3+26, bx+bw/2, by+bh);
        // door knobs
        g.setColor(new Color(255, 215, 0));
        g.fillOval(bx+bw/2-7, by+bh*2/3, 8, 8);
        g.fillOval(bx+bw/2+3, by+bh*2/3, 8, 8);

        // columns (4)
        for (int col = 0; col < 4; col++) {
            int colX = bx+bw/2-52+col*33;
            g.setColor(new Color(244, 240, 225));
            g.fillRect(colX, by+bh/3+22, 10, bh*2/3-22);
            g.setColor(new Color(222, 182, 40));
            g.fillRoundRect(colX-5, by+bh/3+20, 20, 7, 4, 4);
            g.fillRoundRect(colX-5, by+bh-12, 20, 8, 4, 4);
            // fluting
            g.setColor(new Color(200, 195, 175, 85));
            g.fillRect(colX+3, by+bh/3+28, 4, bh*2/3-36);
        }

        // ornate windows
        drawWindow(g, bx+28, by+13, 34, 28, glass);
        drawWindow(g, bx+bw-62, by+13, 34, 28, glass);
        drawWindow(g, bx+68, by+14, 28, 24, glass);
        drawWindow(g, bx+bw-96, by+14, 28, 24, glass);

        // crown on rooftop
        g.setColor(new Color(222, 182, 32));
        int[] crownX = {bx+bw/2-18, bx+bw/2-18, bx+bw/2-9, bx+bw/2-5,
                         bx+bw/2,    bx+bw/2+5, bx+bw/2+9, bx+bw/2+18, bx+bw/2+18};
        int[] crownY = {by+9, by-5, by-14, by-5, by-18, by-5, by-14, by-5, by+9};
        g.fillPolygon(crownX, crownY, 9);
        g.setColor(new Color(188, 148, 18));
        g.setStroke(new BasicStroke(1.2f)); g.drawPolygon(crownX, crownY, 9); g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(200, 50, 50));  g.fillOval(bx+bw/2-4, by-16, 9, 9);
        g.setColor(new Color(50, 100, 205)); g.fillOval(bx+bw/2-15, by-9, 8, 8);
        g.setColor(new Color(50, 100, 205)); g.fillOval(bx+bw/2+9, by-9, 8, 8);

        g.setColor(new Color(150, 120, 60));
        g.setStroke(new BasicStroke(2.2f));
        g.drawRect(bx, by, bw, bh);
        g.setStroke(new BasicStroke(1f));

        drawStarBadge(g, x+w-2, y+6, 7);
    }

    // ── 7: LEGEND MANSION (stage 20) ──────────────────────────────────────────
    private static void drawLegendMansion(Graphics2D g, int x, int y, int w, int h) {
        int bx = x+1, by = y+h/10, bw = w-2, bh = h*9/10;
        Color glass = new Color(255, 252, 205, 228);

        // pulsing golden aura
        for (int layer = 4; layer >= 1; layer--) {
            int pad = layer*8;
            g.setColor(new Color(255, 215, 0, 16*(5-layer)));
            g.fillRoundRect(bx-pad, by-pad, bw+pad*2, bh+pad*2, 14, 14);
        }

        g.setColor(new Color(0,0,0,58));
        g.fillOval(bx-2, by+bh-2, bw+4, 20);

        // gold + ivory walls
        GradientPaint goldWall = new GradientPaint(bx, by, new Color(255, 245, 178),
                bx+bw, by+bh, new Color(208, 170, 65));
        g.setPaint(goldWall); g.fillRect(bx, by, bw, bh); g.setPaint(null);

        // diamond lattice
        g.setColor(new Color(222, 182, 50, 72));
        g.setStroke(new BasicStroke(0.8f));
        for (int drow = 0; drow < 8; drow++) {
            for (int dcol = 0; dcol < bw/22; dcol++) {
                int dx = bx+dcol*22+(drow%2)*11;
                int dy = by+drow*(bh/8);
                g.drawLine(dx, dy+bh/16, dx+11, dy);
                g.drawLine(dx+11, dy, dx+22, dy+bh/16);
                g.drawLine(dx+22, dy+bh/16, dx+11, dy+bh/8);
                g.drawLine(dx+11, dy+bh/8, dx, dy+bh/16);
            }
        }
        g.setStroke(new BasicStroke(1f));

        // gold trim bands
        g.setColor(new Color(222, 182, 32));
        g.fillRect(bx, by, bw, 9);
        g.fillRect(bx, by+bh-14, bw, 14);
        g.fillRect(bx, by+bh/3, bw, 7);
        g.setColor(new Color(255, 228, 65, 105));
        g.fillRect(bx+1, by+1, bw-2, 5);
        g.fillRect(bx+1, by+bh/3+1, bw-2, 3);

        // seven golden spires
        for (int sp = 0; sp < 7; sp++) {
            int sw = 22;
            int sx = bx + sp*(bw/6) - sw/2;
            sx = Math.max(bx, Math.min(bx+bw-sw, sx));
            int sh = (sp==3) ? bh : bh*4/5;
            GradientPaint spW = new GradientPaint(sx, by+bh-sh, new Color(255, 248, 192),
                    sx+sw, by+bh, new Color(205, 165, 55));
            g.setPaint(spW); g.fillRect(sx, by+bh-sh, sw, sh); g.setPaint(null);
            // cone
            int[] srx = {sx-7, sx+sw/2, sx+sw+7};
            int[] sry = {by+bh-sh, by+bh-sh-h/4, by+bh-sh};
            GradientPaint coneG = new GradientPaint(sx, by+bh-sh-h/4,
                    new Color(255, 225, 0), sx+sw, by+bh-sh, new Color(182, 132, 8));
            g.setPaint(coneG); g.fillPolygon(srx, sry, 3); g.setPaint(null);
            // star tip
            g.setColor(new Color(255, 255, 125));
            drawStar(g, sx+sw/2, by+bh-sh-h/4-2, 7);
            g.setColor(new Color(182, 132, 12));
            g.setStroke(new BasicStroke(1.8f));
            g.drawRect(sx, by+bh-sh, sw, sh);
            g.drawPolygon(srx, sry, 3);
            g.setStroke(new BasicStroke(1f));
            // spire window
            drawWindow(g, sx+5, by+bh-sh+9, 14, 11, glass);
        }

        // grand arched double door
        g.setColor(new Color(152, 105, 20));
        g.fillArc(bx+bw/2-28, by+bh/3-14, 56, 56, 0, 180);
        g.setColor(new Color(120, 80, 14));
        g.fillRect(bx+bw/2-28, by+bh/3+14, 28, bh*2/3-14);
        g.fillRect(bx+bw/2, by+bh/3+14, 28, bh*2/3-14);
        // door gold arch
        g.setColor(new Color(255, 215, 0));
        g.setStroke(new BasicStroke(3f));
        g.drawArc(bx+bw/2-28, by+bh/3-14, 56, 56, 0, 180);
        g.drawLine(bx+bw/2, by+bh/3+14, bx+bw/2, by+bh);
        g.setStroke(new BasicStroke(1f));
        // door knobs
        g.fillOval(bx+bw/2-9, by+bh*2/3, 10, 10);
        g.fillOval(bx+bw/2+4, by+bh*2/3, 10, 10);

        // gem-encrusted columns (3)
        Color[] gemColors = {new Color(205, 50, 50), new Color(50, 100, 255), new Color(50, 200, 50)};
        for (int col = 0; col < 3; col++) {
            int colX = bx+bw/2-48+col*36;
            g.setColor(new Color(255, 248, 205));
            g.fillRect(colX, by+bh/3+12, 12, bh*2/3-12);
            g.setColor(new Color(222, 182, 40));
            g.fillRoundRect(colX-5, by+bh/3+10, 22, 8, 4, 4);
            g.fillRoundRect(colX-5, by+bh-12, 22, 9, 4, 4);
            // gems at mid-column
            g.setColor(gemColors[col]);
            g.fillOval(colX+2, by+bh/2, 9, 9);
            g.setColor(new Color(255, 255, 255, 155));
            g.fillOval(colX+3, by+bh/2+1, 3, 3);
        }

        // ornate windows
        drawWindow(g, bx+24, by+13, 35, 28, glass);
        drawWindow(g, bx+bw-59, by+13, 35, 28, glass);
        drawWindow(g, bx+65, by+14, 29, 24, glass);
        drawWindow(g, bx+bw-94, by+14, 29, 24, glass);

        // rainbow sparkles
        Color[] spkC = {new Color(255,80,80), new Color(255,185,52),
                         new Color(102,255,82), new Color(82,162,255), new Color(205,82,255)};
        int[][] spkPos = {{bx+16,by+52},{bx+bw-22,by+45},{bx+bw/4,by+bh/3},
                          {bx+bw*3/4,by+bh/3},{bx+bw/2,by+24}};
        for (int sp = 0; sp < 5; sp++) {
            g.setColor(spkC[sp]);
            g.setStroke(new BasicStroke(1.8f));
            int spx = spkPos[sp][0], spy = spkPos[sp][1];
            g.drawLine(spx-7,spy,spx+7,spy); g.drawLine(spx,spy-7,spx,spy+7);
            g.drawLine(spx-5,spy-5,spx+5,spy+5); g.drawLine(spx+5,spy-5,spx-5,spy+5);
            g.setStroke(new BasicStroke(1f));
        }

        // gold outline
        g.setColor(new Color(178, 130, 8));
        g.setStroke(new BasicStroke(2.8f));
        g.drawRect(bx, by, bw, bh);
        g.setStroke(new BasicStroke(1f));

        // LEGEND banner
        GradientPaint banner = new GradientPaint(bx+bw/2-40, by+bh-24,
                new Color(202, 162, 10), bx+bw/2+40, by+bh-8, new Color(255, 218, 0));
        g.setPaint(banner); g.fillRoundRect(bx+bw/2-40, by+bh-24, 80, 20, 8, 8); g.setPaint(null);
        g.setColor(new Color(100, 70, 0));
        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        FontMetrics fm = g.getFontMetrics();
        String legend = "\u2605 LEGEND \u2605";
        g.drawString(legend, bx+bw/2-fm.stringWidth(legend)/2, by+bh-8);

        drawStarBadge(g, x+w-2, y+6, 8);
    }

    // ── utilities ─────────────────────────────────────────────────────────────

    private static void drawStar(Graphics2D g, int cx, int cy, int r) {
        int[] sx = new int[10], sy = new int[10];
        for (int i = 0; i < 10; i++) {
            double a = Math.PI/5*i - Math.PI/2;
            int rad = (i%2==0) ? r : r/2;
            sx[i] = cx+(int)(Math.cos(a)*rad);
            sy[i] = cy+(int)(Math.sin(a)*rad);
        }
        g.fillPolygon(sx, sy, 10);
    }

    private static Color lighter(Color c, int amt) {
        return new Color(Math.min(255, c.getRed()+amt),
                         Math.min(255, c.getGreen()+amt),
                         Math.min(255, c.getBlue()+amt), c.getAlpha());
    }
    private static Color darker(Color c, int amt) {
        return new Color(Math.max(0, c.getRed()-amt),
                         Math.max(0, c.getGreen()-amt),
                         Math.max(0, c.getBlue()-amt), c.getAlpha());
    }

    private FarmHouseRenderer() {}
}