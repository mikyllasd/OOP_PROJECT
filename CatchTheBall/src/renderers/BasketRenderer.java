package OOP_PROJECT.CatchTheBall.src.renderers;

import OOP_PROJECT.CatchTheBall.src.enums.BasketSkin;
import java.awt.*;
import java.awt.geom.*;

/**
 * BasketRenderer — pixel-art style with rich detail for all 10 basket skins.
 * Rendered at native size then scaled; each basket has unique silhouette,
 * materials, and decorative elements.
 */
public class BasketRenderer {

    public static void draw(Graphics2D g, BasketSkin skin, int x, int y, int w, int h) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);
        switch (skin) {
            case WOVEN:     drawWoven    (g2, x, y, w, h); break;
            case METAL:     drawMetal    (g2, x, y, w, h); break;
            case GOLDEN:    drawGolden   (g2, x, y, w, h); break;
            case CART:      drawCart     (g2, x, y, w, h); break;
            case DIAMOND:   drawDiamond  (g2, x, y, w, h); break;
            case BAMBOO:    drawBamboo   (g2, x, y, w, h); break;
            case CLAY:      drawClay     (g2, x, y, w, h); break;
            case CRYSTAL:   drawCrystal  (g2, x, y, w, h); break;
            case MAGIC:     drawMagic    (g2, x, y, w, h); break;
            case LEGENDARY: drawLegendary(g2, x, y, w, h); break;
        }
        g2.dispose();
    }

    // ── shared helpers ────────────────────────────────────────────────────────

    private static void fillTrap(Graphics2D g, int x, int y, int w, int h, int inset) {
        int[] bx = {x, x + w, x + w - inset, x + inset};
        int[] by = {y, y, y + h, y + h};
        g.fillPolygon(bx, by, 4);
    }
    private static void drawTrap(Graphics2D g, int x, int y, int w, int h, int inset) {
        int[] bx = {x, x + w, x + w - inset, x + inset};
        int[] by = {y, y, y + h, y + h};
        g.drawPolygon(bx, by, 4);
    }

    private static void drawRim(Graphics2D g, int x, int y, int w,
                                 Color top, Color bot, Color outline) {
        GradientPaint gp = new GradientPaint(x, y - 5, top, x + w, y + 5, bot);
        g.setPaint(gp);
        g.fillRoundRect(x - 5, y - 6, w + 10, 12, 8, 8);
        g.setPaint(null);
        g.setColor(outline);
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x - 5, y - 6, w + 10, 12, 8, 8);
        g.setStroke(new BasicStroke(1f));
        // rim top highlight
        g.setColor(new Color(255, 255, 255, 60));
        g.fillRoundRect(x - 4, y - 5, w + 8, 4, 6, 6);
    }

    private static void drawHandle(Graphics2D g, int x, int y, int w, int h,
                                    Color col, float strokeW) {
        g.setColor(col);
        g.setStroke(new BasicStroke(strokeW, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(x + w / 5, y - h / 2, w * 3 / 5, h / 2, 0, 180);
        g.setStroke(new BasicStroke(1f));
    }

    // ── WOVEN BASKET ─────────────────────────────────────────────────────────
    private static void drawWoven(Graphics2D g, int x, int y, int w, int h) {
        // body gradient
        GradientPaint gp = new GradientPaint(x, y, new Color(215, 162, 84),
                x + w, y + h, new Color(138, 85, 28));
        g.setPaint(gp); fillTrap(g, x, y, w, h, 10); g.setPaint(null);

        // pixel-style weave pattern
        int cellW = Math.max(4, w / 10), cellH = Math.max(3, h / 8);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 10; col++) {
                boolean horiz = (row + col) % 2 == 0;
                int cx2 = x + col * cellW + 2;
                int cy2 = y + row * cellH + 2;
                if (cx2 + cellW > x + w - 2) continue;
                if (cy2 + cellH > y + h - 2) continue;
                if (horiz) {
                    g.setColor(new Color(165, 110, 42, 140));
                    g.fillRect(cx2, cy2, cellW - 1, cellH / 2);
                } else {
                    g.setColor(new Color(100, 62, 18, 120));
                    g.fillRect(cx2, cy2, cellW / 2, cellH - 1);
                }
            }
        }

        // horizontal weft lines
        g.setColor(new Color(92, 55, 16, 160));
        g.setStroke(new BasicStroke(1.5f));
        for (int i = 1; i <= 6; i++) g.drawLine(x + 2, y + h * i / 7, x + w - 2, y + h * i / 7);
        g.setStroke(new BasicStroke(1f));

        // left highlight
        g.setColor(new Color(255, 222, 158, 65));
        g.fillPolygon(new int[]{x, x + w / 3, x + 10}, new int[]{y, y, y + h}, 3);

        // inner shadow
        g.setColor(new Color(80, 44, 10, 55));
        g.fillOval(x + 5, y, w - 10, 10);

        // dark border at sides for 3D depth
        g.setColor(new Color(88, 52, 14, 120));
        g.fillPolygon(new int[]{x, x + 8, x + 5, x}, new int[]{y, y, y + h, y + h}, 4);
        g.fillPolygon(new int[]{x+w, x+w-8, x+w-5, x+w}, new int[]{y, y, y+h, y+h}, 4);

        drawRim(g, x, y, w, new Color(228, 182, 98), new Color(188, 142, 60),
                new Color(78, 46, 12));
        drawHandle(g, x, y, w, h, new Color(138, 88, 26), 6f);
        drawHandle(g, x, y, w, h, new Color(198, 152, 70), 3.5f);
        drawHandle(g, x, y, w, h, new Color(245, 210, 145, 120), 1.5f);

        g.setColor(new Color(78, 46, 12));
        g.setStroke(new BasicStroke(1.8f));
        drawTrap(g, x, y, w, h, 10);
        g.setStroke(new BasicStroke(1f));
    }

    // ── METAL BUCKET ──────────────────────────────────────────────────────────
    private static void drawMetal(Graphics2D g, int x, int y, int w, int h) {
        // brushed steel – three-band gradient
        GradientPaint gp = new GradientPaint(x, y, new Color(155, 172, 195),
                x + w * 2/3, y, new Color(228, 240, 252));
        g.setPaint(gp); fillTrap(g, x, y, w, h, 7); g.setPaint(null);

        // right dark shading
        GradientPaint shade = new GradientPaint(x + w * 2/3, y,
                new Color(0,0,0,0), x + w, y + h, new Color(0,0,0,55));
        g.setPaint(shade); fillTrap(g, x, y, w, h, 7); g.setPaint(null);

        // brushed horizontal lines
        g.setColor(new Color(128, 148, 175, 55));
        g.setStroke(new BasicStroke(0.7f));
        for (int i = 0; i < 12; i++) g.drawLine(x+2, y + h*i/12, x+w-2, y + h*i/12);
        g.setStroke(new BasicStroke(1f));

        // ring seams
        g.setColor(new Color(92, 112, 138, 180));
        g.setStroke(new BasicStroke(2.5f));
        for (int i = 1; i <= 3; i++) g.drawLine(x+2, y+h*i/4, x+w-2, y+h*i/4);
        g.setStroke(new BasicStroke(1f));
        // ring highlight above each seam
        g.setColor(new Color(255, 255, 255, 50));
        g.setStroke(new BasicStroke(1f));
        for (int i = 1; i <= 3; i++) g.drawLine(x+2, y+h*i/4-1, x+w-2, y+h*i/4-1);
        g.setStroke(new BasicStroke(1f));

        // rivets
        for (int i = 1; i <= 3; i++) {
            int ry2 = y + h * i / 4;
            int[] rx2 = {x + 5, x + w - 12};
            for (int rx3 : rx2) {
                g.setColor(new Color(105, 122, 148));
                g.fillOval(rx3, ry2 - 5, 8, 8);
                g.setColor(new Color(195, 212, 232));
                g.fillOval(rx3 + 1, ry2 - 4, 3, 3);
                g.setColor(new Color(78, 95, 120));
                g.setStroke(new BasicStroke(0.8f));
                g.drawOval(rx3, ry2 - 5, 8, 8);
                g.setStroke(new BasicStroke(1f));
            }
        }

        // left specular highlight
        g.setColor(new Color(255, 255, 255, 65));
        g.fillPolygon(new int[]{x+3, x+w/4, x+8}, new int[]{y, y, y+h}, 3);

        drawRim(g, x, y, w, new Color(200, 215, 235), new Color(148, 165, 188),
                new Color(82, 98, 122));

        // double-stroke handle
        g.setColor(new Color(130, 148, 172));
        g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(x+w/5, y-h/2, w*3/5, h/2, 0, 180);
        g.setColor(new Color(200, 215, 232));
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(x+w/5, y-h/2, w*3/5, h/2, 0, 180);
        g.setStroke(new BasicStroke(1f));

        g.setColor(new Color(85, 100, 122));
        g.setStroke(new BasicStroke(1.8f));
        drawTrap(g, x, y, w, h, 7);
        g.setStroke(new BasicStroke(1f));
    }

    // ── GOLDEN BASKET ────────────────────────────────────────────────────────
    private static void drawGolden(Graphics2D g, int x, int y, int w, int h) {
        // rich gold body
        GradientPaint gp = new GradientPaint(x, y, new Color(255, 225, 85),
                x + w/2, y, new Color(218, 175, 28));
        g.setPaint(gp); fillTrap(g, x, y, w, h, 10); g.setPaint(null);

        // right shade
        GradientPaint shade = new GradientPaint(x+w*2/3, y, new Color(0,0,0,0),
                x+w, y+h, new Color(0,0,0,65));
        g.setPaint(shade); fillTrap(g, x, y, w, h, 10); g.setPaint(null);

        // filigree oval patterns
        g.setColor(new Color(255, 245, 148, 100));
        for (int i = 0; i < 3; i++) g.drawOval(x+8+i*18, y+4, 16, h-10);

        // engraved horizontal lines
        g.setColor(new Color(138, 98, 8, 130));
        g.setStroke(new BasicStroke(1.2f));
        for (int i = 1; i <= 4; i++) g.drawLine(x+2, y+h*i/5, x+w-2, y+h*i/5);
        g.setStroke(new BasicStroke(1f));

        // dot rivets on lines
        g.setColor(new Color(255, 235, 100));
        for (int i = 1; i <= 4; i++) {
            int ry2 = y+h*i/5;
            for (int d = 0; d < 5; d++) g.fillOval(x+6+d*(w-12)/4, ry2-2, 4, 4);
        }

        // left specular
        g.setColor(new Color(255, 255, 225, 95));
        g.fillPolygon(new int[]{x, x+w/2, x+10}, new int[]{y, y, y+h}, 3);

        // sparkles
        g.setColor(new Color(255, 255, 210, 200));
        g.setStroke(new BasicStroke(1.5f));
        int[][] sp = {{x+8,y+5},{x+w-14,y+8},{x+w/2,y+h/2},{x+w-8,y+h-10}};
        for (int[] s : sp) {
            g.drawLine(s[0]-4,s[1],s[0]+4,s[1]);
            g.drawLine(s[0],s[1]-4,s[0],s[1]+4);
        }
        g.setStroke(new BasicStroke(1f));

        drawRim(g, x, y, w, new Color(255, 235, 105), new Color(202, 160, 22),
                new Color(148, 108, 5));
        drawHandle(g, x, y, w, h, new Color(172, 126, 10), 6f);
        drawHandle(g, x, y, w, h, new Color(255, 220, 72), 3.5f);
        drawHandle(g, x, y, w, h, new Color(255, 255, 210, 120), 1.5f);

        g.setColor(new Color(148, 105, 5));
        g.setStroke(new BasicStroke(1.8f));
        drawTrap(g, x, y, w, h, 10);
        g.setStroke(new BasicStroke(1f));
    }

    // ── FARM CART ─────────────────────────────────────────────────────────────
    private static void drawCart(Graphics2D g, int x, int y, int w, int h) {
        int boxH = h - 18, wheelR = 14;

        // drop shadow
        g.setColor(new Color(0,0,0,35));
        g.fillOval(x+2, y+boxH+wheelR*2-4, w-4, 10);

        // cart box
        GradientPaint gp = new GradientPaint(x, y, new Color(208, 148, 65),
                x, y+boxH, new Color(138, 88, 22));
        g.setPaint(gp); g.fillRoundRect(x, y, w, boxH, 6, 6); g.setPaint(null);

        // plank horizontal lines
        g.setColor(new Color(92, 52, 12, 130));
        g.setStroke(new BasicStroke(1f));
        for (int i = 1; i <= 3; i++) g.drawLine(x, y+boxH*i/4, x+w, y+boxH*i/4);
        // plank vertical lines
        for (int i = 1; i <= 4; i++) g.drawLine(x+w*i/5, y, x+w*i/5, y+boxH);
        g.setStroke(new BasicStroke(1f));

        // wood grain texture
        g.setColor(new Color(158, 98, 32, 40));
        for (int i = 0; i < 5; i++)
            g.drawLine(x+i*12, y, x+i*12+4, y+boxH);

        // interior shadow
        g.setColor(new Color(78, 42, 8, 58));
        g.fillOval(x+4, y+2, w-8, 14);
        // highlight
        g.setColor(new Color(255, 205, 122, 55));
        g.fillPolygon(new int[]{x,x+w/3,x+6}, new int[]{y,y,y+boxH}, 3);

        // metal rim strip
        GradientPaint rimG = new GradientPaint(x, y-4, new Color(155,158,175),
                x+w, y+4, new Color(188,192,210));
        g.setPaint(rimG); g.fillRoundRect(x-3, y-4, w+6, 8, 5, 5); g.setPaint(null);
        g.setColor(new Color(212, 215, 232));
        g.fillRect(x, y-2, w, 3);
        g.setColor(new Color(95,98,118)); g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(x-3, y-4, w+6, 8, 5, 5); g.setStroke(new BasicStroke(1f));

        // wheels
        int[] wheelX = {x + wheelR + 6, x + w - wheelR - 6};
        for (int wx : wheelX) {
            int wy = y + boxH + wheelR - 2;
            // tyre
            g.setColor(new Color(28, 20, 8));
            g.setStroke(new BasicStroke(6f));
            g.drawOval(wx-wheelR, wy-wheelR, wheelR*2, wheelR*2);
            // inner rim
            g.setColor(new Color(85, 58, 18));
            g.setStroke(new BasicStroke(3f));
            g.drawOval(wx-wheelR+4, wy-wheelR+4, (wheelR-4)*2, (wheelR-4)*2);
            // spokes
            g.setColor(new Color(108, 72, 26));
            g.setStroke(new BasicStroke(2f));
            for (int sp = 0; sp < 8; sp++) {
                double a = Math.PI/4*sp;
                g.drawLine(wx, wy,
                    wx+(int)(Math.cos(a)*(wheelR-5)),
                    wy+(int)(Math.sin(a)*(wheelR-5)));
            }
            // hub
            g.setColor(new Color(78, 48, 12));
            g.fillOval(wx-6, wy-6, 12, 12);
            g.setColor(new Color(135, 98, 38));
            g.fillOval(wx-4, wy-4, 8, 8);
            g.setColor(new Color(188, 148, 68));
            g.fillOval(wx-2, wy-2, 4, 4);
            g.setStroke(new BasicStroke(1f));
            // wheel highlight
            g.setColor(new Color(255,255,255,40));
            g.drawArc(wx-wheelR+2, wy-wheelR+2, wheelR, wheelR, 45, 90);
        }
        // axle
        g.setColor(new Color(88, 62, 20));
        g.fillRect(wheelX[0], y+boxH+wheelR-4, wheelX[1]-wheelX[0], 6);
        g.setColor(new Color(120, 88, 38));
        g.fillRect(wheelX[0], y+boxH+wheelR-3, wheelX[1]-wheelX[0], 2);

        // box outline
        g.setColor(new Color(92, 55, 12));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(x, y, w, boxH, 6, 6);
        g.setStroke(new BasicStroke(1f));
    }

    // ── DIAMOND ───────────────────────────────────────────────────────────────
    private static void drawDiamond(Graphics2D g, int x, int y, int w, int h) {
        int cx2 = x + w / 2;

        // outer glow layers
        for (int layer = 3; layer >= 1; layer--) {
            int pad = layer * 4;
            g.setColor(new Color(140, 215, 255, 15 * (4 - layer)));
            g.fillOval(x-pad, y-pad, w+pad*2, h+pad*2);
        }

        // main body polygon
        int[] px2 = {cx2-w/2, cx2, cx2+w/2, cx2+w/2-7, cx2, cx2-w/2+7};
        int[] py2 = {y+h/4, y, y+h/4, y+h, y+h, y+h};
        GradientPaint gp = new GradientPaint(x, y, new Color(215, 242, 255),
                x+w, y+h, new Color(78, 165, 228));
        g.setPaint(gp); g.fillPolygon(px2, py2, 6); g.setPaint(null);

        // bright top crown facet
        g.setColor(new Color(242, 254, 255, 210));
        g.fillPolygon(new int[]{cx2, cx2+w/4, cx2}, new int[]{y, y+h/4, y+h/3}, 3);
        // left dark facet
        g.setColor(new Color(142, 202, 245, 165));
        g.fillPolygon(new int[]{cx2-w/2, cx2, cx2-w/2+7},
                      new int[]{y+h/4, y+h/4, y+h}, 3);
        // right medium facet
        g.setColor(new Color(185, 228, 255, 100));
        g.fillPolygon(new int[]{cx2, cx2+w/2, cx2+w/2-7},
                      new int[]{y+h/4, y+h/4, y+h}, 3);
        // central gleam
        g.setColor(new Color(255, 255, 255, 220));
        g.fillPolygon(new int[]{cx2-3, cx2+8, cx2-2}, new int[]{y+2, y+h/5, y+h/4}, 3);

        // facet outline lines
        g.setColor(new Color(48, 128, 198, 160));
        g.setStroke(new BasicStroke(0.8f));
        g.drawLine(cx2, y, cx2, y+h);
        g.drawLine(cx2-w/2, y+h/4, cx2+w/2, y+h/4);
        g.setStroke(new BasicStroke(1f));

        // sparkle crosses
        g.setColor(new Color(200, 242, 255, 215));
        g.setStroke(new BasicStroke(1.8f));
        int[][] sparks = {{cx2-w/4,y+h*2/3},{cx2+w/5,y+h*3/4},{cx2,y+h/2}};
        for (int[] s : sparks) {
            g.drawLine(s[0]-6,s[1],s[0]+6,s[1]);
            g.drawLine(s[0],s[1]-6,s[0],s[1]+6);
            g.drawLine(s[0]-3,s[1]-3,s[0]+3,s[1]+3);
            g.drawLine(s[0]+3,s[1]-3,s[0]-3,s[1]+3);
        }
        g.setStroke(new BasicStroke(1f));

        g.setColor(new Color(48, 125, 195));
        g.setStroke(new BasicStroke(1.5f));
        g.drawPolygon(px2, py2, 6);
        g.setStroke(new BasicStroke(1f));

        drawRim(g, x, y, w, new Color(195, 232, 255), new Color(132, 188, 238),
                new Color(48, 125, 195));
        drawHandle(g, x, y, w, h, new Color(88, 155, 212), 4.5f);
        drawHandle(g, x, y, w, h, new Color(205, 238, 255), 2.5f);
    }

    // ── BAMBOO BASKET ─────────────────────────────────────────────────────────
    private static void drawBamboo(Graphics2D g, int x, int y, int w, int h) {
        GradientPaint gp = new GradientPaint(x, y, new Color(132, 198, 72),
                x+w, y, new Color(78, 148, 28));
        g.setPaint(gp); fillTrap(g, x, y, w, h, 10); g.setPaint(null);

        // bamboo section highlights between nodes
        for (int i = 0; i < 5; i++) {
            GradientPaint seg = new GradientPaint(x, y+h*i/5+2, new Color(158, 228, 98, 80),
                    x, y+h*(i+1)/5-2, new Color(68, 128, 22, 40));
            g.setPaint(seg);
            g.fillRect(x+2, y+h*i/5+2, w-4, h/5-4);
            g.setPaint(null);
        }

        // node rings (thick)
        g.setColor(new Color(52, 108, 16, 180));
        g.setStroke(new BasicStroke(3.5f));
        for (int i = 1; i <= 4; i++) g.drawLine(x+3, y+h*i/5, x+w-3, y+h*i/5);
        g.setStroke(new BasicStroke(1f));
        // node bulge
        g.setColor(new Color(162, 235, 92, 85));
        for (int i = 1; i <= 4; i++) g.fillOval(x+2, y+h*i/5-4, w-4, 9);
        // node dark underline
        g.setColor(new Color(38, 88, 10, 100));
        g.setStroke(new BasicStroke(1f));
        for (int i = 1; i <= 4; i++) g.drawLine(x+3, y+h*i/5+2, x+w-3, y+h*i/5+2);
        g.setStroke(new BasicStroke(1f));

        // vertical stave lines
        g.setColor(new Color(65, 128, 26, 100));
        g.setStroke(new BasicStroke(1.2f));
        for (int col = 1; col <= 6; col++)
            g.drawLine(x+w*col/7, y+2, x+w*col/7+3, y+h-2);
        g.setStroke(new BasicStroke(1f));

        // left highlight
        g.setColor(new Color(202, 252, 152, 65));
        g.fillPolygon(new int[]{x,x+w/3,x+10}, new int[]{y,y,y+h}, 3);

        // special rim – dark green band
        g.setColor(new Color(44, 108, 16));
        g.fillRoundRect(x-5, y-7, w+10, 14, 8, 8);
        g.setColor(new Color(82, 162, 40));
        g.fillRect(x-3, y-5, w+6, 8);
        g.setColor(new Color(35, 88, 10));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x-5, y-7, w+10, 14, 8, 8);
        g.setStroke(new BasicStroke(1f));

        drawHandle(g, x, y, w, h, new Color(68, 128, 20), 6.5f);
        drawHandle(g, x, y, w, h, new Color(135, 198, 58), 3.5f);
        drawHandle(g, x, y, w, h, new Color(185, 242, 118, 120), 1.5f);

        g.setColor(new Color(40, 95, 12));
        g.setStroke(new BasicStroke(1.8f));
        drawTrap(g, x, y, w, h, 10);
        g.setStroke(new BasicStroke(1f));
    }

    // ── CLAY POT ──────────────────────────────────────────────────────────────
    private static void drawClay(Graphics2D g, int x, int y, int w, int h) {
        int neck = w / 5;
        GradientPaint gp = new GradientPaint(x, y, new Color(215, 122, 72),
                x+w, y+h, new Color(155, 72, 28));
        g.setPaint(gp);
        g.fillOval(x+neck/2, y, w-neck, h+4);
        g.setPaint(null);

        // coil lines
        g.setColor(new Color(122, 52, 18, 115));
        g.setStroke(new BasicStroke(1.5f));
        for (int i = 1; i <= 6; i++) {
            int ly = y+h*i/7;
            int ww = (int)(Math.sin(Math.PI*i/7.0)*w*0.48);
            g.drawLine(x+w/2-ww, ly, x+w/2+ww, ly);
        }
        g.setStroke(new BasicStroke(1f));

        // texture detail – small dimples
        g.setColor(new Color(130, 58, 22, 60));
        for (int r = 1; r <= 3; r++)
            for (int c = 0; c < 4; c++) {
                int dx = x+w/2-18+c*12;
                int dy = y+h*r/4;
                g.fillOval(dx, dy, 4, 3);
            }

        // highlight
        g.setColor(new Color(248, 168, 115, 95));
        g.fillOval(x+neck/2+6, y+5, (w-neck)/2, h/3);

        // neck band
        g.setColor(new Color(78, 38, 10));
        g.fillRect(x+neck, y+h/3, w-neck*2, 6);
        g.setColor(new Color(228, 152, 88));
        g.fillRect(x+neck, y+h/3+1, w-neck*2, 3);

        // flared rim
        g.setColor(new Color(222, 140, 80));
        g.fillOval(x, y-7, w, 16);
        GradientPaint rimG = new GradientPaint(x, y-7, new Color(235, 162, 100),
                x+w, y+9, new Color(178, 98, 42));
        g.setPaint(rimG); g.fillOval(x+2, y-5, w-4, 12); g.setPaint(null);
        // rim highlight
        g.setColor(new Color(255, 200, 158, 100));
        g.fillOval(x+4, y-4, w/3, 5);
        g.setColor(new Color(105, 55, 16));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(x, y-7, w, 16);
        g.setStroke(new BasicStroke(1f));

        // rope handle
        g.setColor(new Color(155, 85, 35));
        g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(x+w/5, y-h/2, w*3/5, h/2, 0, 180);
        g.setColor(new Color(208, 138, 75));
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(x+w/5, y-h/2, w*3/5, h/2, 0, 180);
        // rope twist lines
        g.setColor(new Color(128, 65, 25, 160));
        g.setStroke(new BasicStroke(1f));
        for (int r = 0; r < 5; r++) {
            int ra = r * 36;
            g.drawArc(x+w/5+r*4, y-h/2+r*2, w*3/5-r*8, h/2-r*4, ra, 36);
        }
        g.setStroke(new BasicStroke(1f));

        g.setColor(new Color(102, 52, 14));
        g.setStroke(new BasicStroke(1.8f));
        g.drawOval(x+neck/2, y, w-neck, h+4);
        g.setStroke(new BasicStroke(1f));
    }

    // ── CRYSTAL BOWL ──────────────────────────────────────────────────────────
    private static void drawCrystal(Graphics2D g, int x, int y, int w, int h) {
        // glow
        for (int layer = 3; layer >= 1; layer--) {
            int pad = layer*4;
            g.setColor(new Color(180, 222, 255, 12*(4-layer)));
            g.fillOval(x-pad, y-pad, w+pad*2, h+pad*2);
        }

        // translucent body
        GradientPaint gp = new GradientPaint(x, y, new Color(198, 232, 255, 205),
                x+w, y+h, new Color(108, 175, 248, 190));
        g.setPaint(gp); fillTrap(g, x, y, w, h, 8); g.setPaint(null);

        // inner facet planes
        g.setColor(new Color(222, 245, 255, 162));
        g.fillPolygon(new int[]{x, x+w/2, x+10}, new int[]{y, y, y+h}, 3);
        g.setColor(new Color(152, 208, 252, 85));
        g.fillPolygon(new int[]{x+w/2, x+w, x+w-10}, new int[]{y, y, y+h}, 3);

        // refraction bands
        g.setColor(new Color(202, 240, 255, 90));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(x+w/4, y+4, x+4, y+h-4);
        g.drawLine(x+w*3/4, y+4, x+w-4, y+h-4);
        g.setStroke(new BasicStroke(1f));

        // rainbow caustic sheen
        Color[] causticC = {new Color(255,120,120,60), new Color(255,255,80,50),
                             new Color(120,255,120,50), new Color(120,180,255,60)};
        for (int ci = 0; ci < 4; ci++) {
            g.setColor(causticC[ci]);
            g.fillRect(x+ci*(w/4), y+h*2/3, w/4, h/3);
        }

        // sparkle stars
        g.setColor(new Color(255, 255, 255, 235));
        g.setStroke(new BasicStroke(2f));
        int[][] sparks = {{x+8,y+6},{x+w-14,y+10},{x+w/2,y+h-10}};
        for (int[] s : sparks) {
            g.drawLine(s[0]-6,s[1],s[0]+6,s[1]);
            g.drawLine(s[0],s[1]-6,s[0],s[1]+6);
            g.drawLine(s[0]-4,s[1]-4,s[0]+4,s[1]+4);
            g.drawLine(s[0]+4,s[1]-4,s[0]-4,s[1]+4);
        }
        g.setStroke(new BasicStroke(1f));

        drawRim(g, x, y, w, new Color(218, 245, 255), new Color(158, 208, 255),
                new Color(88, 158, 225));
        drawHandle(g, x, y, w, h, new Color(122, 182, 242, 205), 5f);
        drawHandle(g, x, y, w, h, new Color(218, 242, 255, 225), 2.5f);

        g.setColor(new Color(95, 162, 228));
        g.setStroke(new BasicStroke(1.5f));
        drawTrap(g, x, y, w, h, 8);
        g.setStroke(new BasicStroke(1f));
    }

    // ── MAGIC CAULDRON ────────────────────────────────────────────────────────
    private static void drawMagic(Graphics2D g, int x, int y, int w, int h) {
        int legH = 9;

        // outer glow
        for (int layer = 3; layer >= 1; layer--) {
            int pad = layer*4;
            g.setColor(new Color(48, 255, 78, 12*(4-layer)));
            g.fillOval(x-pad, y-pad, w+pad*2, h+pad*2);
        }

        // cauldron body – dark iron
        GradientPaint gp = new GradientPaint(x, y, new Color(58, 55, 68),
                x+w, y+h, new Color(18, 16, 26));
        g.setPaint(gp); g.fillOval(x-5, y, w+10, h+3); g.setPaint(null);

        // iron rivets around body
        g.setColor(new Color(88, 85, 105));
        for (int ri = 0; ri < 8; ri++) {
            double a = Math.PI*ri/8 + 0.2;
            int rx2 = x + w/2 + (int)(Math.cos(a)*(w/2+1));
            int ry2 = y + h/2 + (int)(Math.sin(a)*h/2*0.7);
            g.fillOval(rx2-3, ry2-3, 6, 6);
            g.setColor(new Color(112, 108, 132));
            g.fillOval(rx2-1, ry2-1, 2, 2);
            g.setColor(new Color(88, 85, 105));
        }

        // iron arc highlight (curved scratch lines)
        g.setColor(new Color(78, 75, 90, 85));
        g.setStroke(new BasicStroke(1f));
        for (int i = 0; i < 5; i++)
            g.drawArc(x-4+i*3, y+i*3, w+8-i*6, h-i*4, 8, 165);
        g.setStroke(new BasicStroke(1f));

        // cauldron left highlight
        g.setColor(new Color(115, 110, 132, 85));
        g.fillOval(x+2, y+4, w/3, h/2);

        // potion surface
        GradientPaint potG = new GradientPaint(x, y-4, new Color(55, 222, 82, 228),
                x, y+12, new Color(18, 158, 48, 155));
        g.setPaint(potG); g.fillOval(x+1, y-8, w-2, 18); g.setPaint(null);
        // foam ring
        g.setColor(new Color(88, 255, 120, 120));
        g.setStroke(new BasicStroke(2f));
        g.drawOval(x+3, y-6, w-6, 14);
        g.setStroke(new BasicStroke(1f));

        // bubbles
        int[][] bubbles = {{x+8,y-14,10},{x+w-20,y-12,8},{x+w/2-2,y-18,12},{x+w/3,y-10,6}};
        for (int[] b : bubbles) {
            g.setColor(new Color(118, 255, 138, 185));
            g.fillOval(b[0], b[1], b[2], b[2]);
            g.setColor(new Color(200, 255, 210, 180));
            g.fillOval(b[0]+1, b[1]+1, b[2]/3, b[2]/3);
            g.setColor(new Color(48, 195, 68, 120));
            g.setStroke(new BasicStroke(0.8f));
            g.drawOval(b[0], b[1], b[2], b[2]);
            g.setStroke(new BasicStroke(1f));
        }

        // legs
        g.setColor(new Color(35, 32, 46));
        g.fillRect(x+6, y+h-3, 9, legH);
        g.fillRect(x+w-15, y+h-3, 9, legH);
        g.fillRect(x+w/2-5, y+h-1, 9, legH-2);
        // leg feet
        g.setColor(new Color(28, 25, 38));
        g.fillRect(x+5, y+h+legH-4, 11, 4);
        g.fillRect(x+w-16, y+h+legH-4, 11, 4);

        // brim ring – double stroke
        g.setColor(new Color(70, 67, 85));
        g.setStroke(new BasicStroke(4.5f));
        g.drawOval(x+1, y-8, w-2, 18);
        g.setColor(new Color(98, 95, 118));
        g.setStroke(new BasicStroke(2.5f));
        g.drawOval(x+1, y-8, w-2, 18);
        // brim highlight
        g.setColor(new Color(145, 140, 165, 80));
        g.setStroke(new BasicStroke(1f));
        g.drawArc(x+3, y-6, w/3, 8, 180, 160);
        g.setStroke(new BasicStroke(1f));

        // handle
        g.setColor(new Color(82, 80, 98));
        g.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(x+w/5, y-h/2, w*3/5, h/2, 0, 180);
        g.setColor(new Color(108, 105, 130));
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(x+w/5, y-h/2, w*3/5, h/2, 0, 180);
        g.setStroke(new BasicStroke(1f));

        // star sparkles
        g.setColor(new Color(255, 222, 52, 185));
        g.setStroke(new BasicStroke(1.8f));
        g.drawLine(x-6, y+10, x-12, y+10); g.drawLine(x-9, y+6, x-9, y+14);
        g.drawLine(x+w+4, y+14, x+w+12, y+14); g.drawLine(x+w+8, y+10, x+w+8, y+18);
        g.setStroke(new BasicStroke(1f));
    }

    // ── LEGENDARY ARK ─────────────────────────────────────────────────────────
    private static void drawLegendary(Graphics2D g, int x, int y, int w, int h) {
        // aura glow layers
        for (int layer = 4; layer >= 1; layer--) {
            int pad = layer * 6;
            g.setColor(new Color(255, 215, 0, 14 * (5 - layer)));
            g.fillRoundRect(x-pad, y-pad, w+pad*2, h+pad*2, 10, 10);
        }

        // body – ornate chest
        GradientPaint gp = new GradientPaint(x, y, new Color(192, 152, 25),
                x+w, y+h, new Color(100, 70, 4));
        g.setPaint(gp); g.fillRect(x, y, w, h); g.setPaint(null);

        // wood grain
        g.setColor(new Color(138, 98, 8, 95));
        g.setStroke(new BasicStroke(0.9f));
        for (int i = 1; i <= 5; i++) g.drawLine(x, y+h*i/6, x+w, y+h*i/6);
        g.setStroke(new BasicStroke(1f));

        // right dark shading
        GradientPaint shade = new GradientPaint(x+w*2/3, y, new Color(0,0,0,0),
                x+w, y+h, new Color(0,0,0,72));
        g.setPaint(shade); g.fillRect(x, y, w, h); g.setPaint(null);

        // gold corner brackets
        g.setColor(new Color(228, 188, 35));
        int bS = 12;
        int[][] corners = {{x,y},{x+w-bS,y},{x,y+h-bS},{x+w-bS,y+h-bS}};
        for (int[] c : corners) {
            g.fillRect(c[0], c[1], bS, bS);
            // bracket highlight
            g.setColor(new Color(255, 228, 68));
            g.fillRect(c[0]+1, c[1]+1, bS/2, 2);
            g.fillRect(c[0]+1, c[1]+1, 2, bS/2);
            g.setColor(new Color(228, 188, 35));
        }
        // bracket rivets
        g.setColor(new Color(255, 225, 65));
        for (int[] c : corners) g.fillOval(c[0]+3, c[1]+3, 6, 6);
        g.setColor(new Color(178, 138, 10));
        for (int[] c : corners) {
            g.setStroke(new BasicStroke(0.8f));
            g.drawOval(c[0]+3, c[1]+3, 6, 6);
            g.setStroke(new BasicStroke(1f));
        }

        // metal bands (horizontal)
        g.setColor(new Color(222, 182, 30));
        g.fillRect(x, y+h/3, w, 6);
        g.fillRect(x, y+h*2/3, w, 6);
        g.setColor(new Color(255, 232, 82, 130));
        g.fillRect(x, y+h/3, w, 2);
        g.fillRect(x, y+h*2/3, w, 2);
        g.setColor(new Color(145, 108, 4));
        g.setStroke(new BasicStroke(0.8f));
        g.drawRect(x, y+h/3, w, 6);
        g.drawRect(x, y+h*2/3, w, 6);
        g.setStroke(new BasicStroke(1f));

        // central lock medallion
        g.setColor(new Color(222, 182, 30));
        g.fillOval(x+w/2-12, y+h/2-12, 24, 24);
        GradientPaint lockG = new GradientPaint(x+w/2-10, y+h/2-10,
                new Color(255, 225, 62), x+w/2+10, y+h/2+10, new Color(175, 132, 8));
        g.setPaint(lockG); g.fillOval(x+w/2-10, y+h/2-10, 20, 20); g.setPaint(null);
        // lock face detail
        g.setColor(new Color(255, 245, 155, 120));
        g.fillOval(x+w/2-6, y+h/2-6, 8, 8);
        // keyhole
        g.setColor(new Color(108, 78, 7));
        g.fillOval(x+w/2-3, y+h/2-6, 7, 7);
        g.fillPolygon(new int[]{x+w/2-2,x+w/2+3,x+w/2+4,x+w/2-3},
                      new int[]{y+h/2,y+h/2,y+h/2+7,y+h/2+7}, 4);
        g.setColor(new Color(178, 142, 22));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(x+w/2-12, y+h/2-12, 24, 24);
        g.setStroke(new BasicStroke(1f));

        // star corner ornaments
        g.setColor(new Color(255, 215, 52, 195));
        drawStar(g, x+7, y+7, 5);
        drawStar(g, x+w-7, y+7, 5);
        drawStar(g, x+7, y+h-7, 5);
        drawStar(g, x+w-7, y+h-7, 5);

        // golden glow overlay
        g.setColor(new Color(255, 215, 0, 25));
        g.fillRect(x, y, w, h);

        drawRim(g, x, y, w, new Color(255, 232, 82), new Color(208, 165, 20),
                new Color(148, 108, 5));

        // thick handle
        drawHandle(g, x, y, w, h, new Color(172, 128, 8), 7f);
        drawHandle(g, x, y, w, h, new Color(255, 222, 62), 4f);
        drawHandle(g, x, y, w, h, new Color(255, 255, 205, 105), 2f);

        g.setColor(new Color(142, 102, 4));
        g.setStroke(new BasicStroke(2.2f));
        g.drawRect(x, y, w, h);
        g.setStroke(new BasicStroke(1f));
    }

    // ── utilities ─────────────────────────────────────────────────────────────

    private static void drawStar(Graphics2D g, int cx, int cy, int r) {
        int[] sx = new int[10], sy = new int[10];
        for (int i = 0; i < 10; i++) {
            double a = Math.PI/5*i - Math.PI/2;
            int rad = (i%2==0) ? r : r/2;
            sx[i] = cx + (int)(Math.cos(a)*rad);
            sy[i] = cy + (int)(Math.sin(a)*rad);
        }
        g.fillPolygon(sx, sy, 10);
    }

    private BasketRenderer() {}
}