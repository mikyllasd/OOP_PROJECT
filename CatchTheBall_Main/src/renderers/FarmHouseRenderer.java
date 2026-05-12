package OOP_PROJECT.CatchTheBall.src.renderers;

import java.awt.*;
import java.awt.geom.*;

public class FarmHouseRenderer {

    public static void draw(Graphics2D g, int stage, int x, int y, int w, int h) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        switch (getHouseType(stage)) {
            case 0: drawOldShack(g, x, y, w, h);    break;
            case 1: drawSimpleHouse(g, x, y, w, h); break;
            case 2: drawFarmHouse(g, x, y, w, h);   break;
            case 3: drawVillage(g, x, y, w, h);     break;
            case 4: drawGrandFarm(g, x, y, w, h);   break;
            case 5: drawCastleFarm(g, x, y, w, h);  break;
            case 6: drawRoyalPalace(g, x, y, w, h); break;
            case 7: drawLegendMansion(g, x, y, w, h); break;
        }
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

    // ── STAGE 1-2: Old Shack ──
    private static void drawOldShack(Graphics2D g, int x, int y, int w, int h) {
        int bx = x + w/6, by = y + h/2, bw = w*2/3, bh = h/2;

        // Ground shadow
        g.setColor(new Color(0, 0, 0, 40));
        g.fillOval(bx - 5, by + bh - 5, bw + 10, 14);

        // Walls — old weathered wood
        GradientPaint wall = new GradientPaint(bx, by, new Color(160, 120, 70), bx + bw, by + bh, new Color(120, 85, 45));
        g.setPaint(wall); g.fillRect(bx, by, bw, bh); g.setPaint(null);

        // Wood plank lines
        g.setColor(new Color(90, 60, 30, 150));
        g.setStroke(new BasicStroke(1.5f));
        for (int i = 1; i <= 4; i++) g.drawLine(bx, by + bh * i / 5, bx + bw, by + bh * i / 5);
        g.setStroke(new BasicStroke(1f));

        // Crooked roof
        int[] rx = {bx - 8, bx + bw / 2, bx + bw + 8};
        int[] ry = {by, by - h / 3, by};
        GradientPaint roof = new GradientPaint(bx, by - h/3, new Color(100, 70, 40), bx + bw, by, new Color(70, 45, 20));
        g.setPaint(roof); g.fillPolygon(rx, ry, 3); g.setPaint(null);

        // Roof shingles
        g.setColor(new Color(50, 30, 10, 100));
        for (int row = 0; row < 3; row++) {
            int indent = row * 6;
            g.drawLine(bx - 8 + indent, by - h/3 + row * (h/3)/3,
                       bx + bw + 8 - indent, by - h/3 + row * (h/3)/3);
        }

        // Broken door
        g.setColor(new Color(80, 50, 20));
        g.fillRoundRect(bx + bw/2 - 10, by + bh/2, 20, bh/2, 4, 4);
        // Door crack
        g.setColor(new Color(40, 20, 5));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(bx + bw/2, by + bh/2, bx + bw/2 - 3, by + bh);
        g.setStroke(new BasicStroke(1f));

        // Small window
        g.setColor(new Color(180, 220, 255, 160));
        g.fillRect(bx + 6, by + 6, 14, 12);
        g.setColor(new Color(100, 80, 50));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(bx + 6, by + 6, 14, 12);
        g.setStroke(new BasicStroke(1f));

        // Chimney (crooked)
        g.setColor(new Color(130, 90, 60));
        int[] chx = {bx + bw - 20, bx + bw - 12, bx + bw - 10, bx + bw - 18};
        int[] chy = {by - 5, by - 5, by - h/3 + 10, by - h/3 + 10};
        g.fillPolygon(chx, chy, 4);

        // Smoke puff
        g.setColor(new Color(200, 200, 200, 80));
        g.fillOval(bx + bw - 20, by - h/3 - 12, 10, 10);
        g.setColor(new Color(200, 200, 200, 50));
        g.fillOval(bx + bw - 16, by - h/3 - 20, 14, 14);

        // Outline
        g.setColor(new Color(60, 40, 15));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(bx, by, bw, bh);
        g.drawPolygon(rx, ry, 3);
        g.setStroke(new BasicStroke(1f));

        // Star badge
        drawStarBadge(g, x + w - 18, y + 4, 1);
    }

    // ── STAGE 3-5: Simple House ──
    private static void drawSimpleHouse(Graphics2D g, int x, int y, int w, int h) {
        int bx = x + w/8, by = y + h*2/5, bw = w*3/4, bh = h*3/5;

        // Ground shadow
        g.setColor(new Color(0, 0, 0, 40));
        g.fillOval(bx, by + bh - 4, bw, 12);

        // Walls
        GradientPaint wall = new GradientPaint(bx, by, new Color(245, 225, 185), bx, by + bh, new Color(210, 185, 145));
        g.setPaint(wall); g.fillRect(bx, by, bw, bh); g.setPaint(null);

        // Roof
        int[] rx = {bx - 10, bx + bw/2, bx + bw + 10};
        int[] ry = {by, by - h*2/5, by};
        GradientPaint roof = new GradientPaint(bx, by - h*2/5, new Color(200, 80, 60), bx + bw, by, new Color(155, 50, 35));
        g.setPaint(roof); g.fillPolygon(rx, ry, 3); g.setPaint(null);

        // Roof highlight
        g.setColor(new Color(255, 120, 100, 60));
        g.fillPolygon(new int[]{bx-10, bx+bw/2, bx+bw/4}, new int[]{by, by-h*2/5, by}, 3);

        // Chimney
        g.setColor(new Color(160, 100, 70));
        g.fillRect(bx + bw - 28, by - h*2/5 + 5, 16, h*2/5 + 5);
        g.setColor(new Color(140, 85, 55));
        g.fillRect(bx + bw - 31, by - h*2/5 + 3, 22, 7);
        // Smoke
        g.setColor(new Color(200, 200, 200, 70));
        g.fillOval(bx + bw - 24, by - h*2/5 - 12, 12, 12);
        g.setColor(new Color(200, 200, 200, 45));
        g.fillOval(bx + bw - 20, by - h*2/5 - 22, 16, 16);

        // Door
        GradientPaint door = new GradientPaint(bx+bw/2-12, 0, new Color(120, 70, 30), bx+bw/2+12, 0, new Color(85, 50, 18));
        g.setPaint(door); g.fillRoundRect(bx + bw/2 - 12, by + bh/2, 24, bh/2, 6, 6); g.setPaint(null);
        // Doorknob
        g.setColor(new Color(200, 170, 80));
        g.fillOval(bx + bw/2 + 5, by + bh*3/4, 5, 5);

        // Windows
        drawCartoonWindow(g, bx + 8, by + 8, 28, 22);
        drawCartoonWindow(g, bx + bw - 36, by + 8, 28, 22);

        // Fence posts
        g.setColor(new Color(200, 170, 110));
        for (int i = 0; i <= 4; i++) {
            int fx = bx - 10 + i * (bw + 20) / 4;
            g.fillRoundRect(fx, by + bh - 5, 6, 18, 3, 3);
        }
        g.fillRect(bx - 10, by + bh + 2, bw + 20, 4);

        // Outline
        g.setColor(new Color(120, 90, 50));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(bx, by, bw, bh);
        g.drawPolygon(rx, ry, 3);
        g.setStroke(new BasicStroke(1f));

        drawStarBadge(g, x + w - 18, y + 4, 2);
    }

    // ── STAGE 6-8: Farm House ──
    private static void drawFarmHouse(Graphics2D g, int x, int y, int w, int h) {
        int bx = x + w/10, by = y + h/3, bw = w*4/5, bh = h*2/3;

        // Ground
        g.setColor(new Color(0, 0, 0, 40));
        g.fillOval(bx, by + bh - 4, bw, 14);

        // Main house walls
        GradientPaint wall = new GradientPaint(bx, by, new Color(250, 230, 190), bx, by+bh, new Color(215, 190, 150));
        g.setPaint(wall); g.fillRect(bx, by, bw, bh); g.setPaint(null);

        // Brick pattern
        g.setColor(new Color(180, 140, 100, 60));
        for (int row = 0; row < 6; row++) {
            int offsetX = (row % 2 == 0) ? 0 : 14;
            for (int col = -1; col < bw/28 + 1; col++) {
                int brickX = bx + col * 28 + offsetX;
                int brickY = by + row * (bh/6);
                g.drawRect(brickX, brickY, 26, bh/6 - 2);
            }
        }

        // Side barn wing
        int wingW = bw / 4, wingH = bh * 3 / 4;
        GradientPaint barn = new GradientPaint(bx - wingW, by + bh - wingH, new Color(190, 55, 45),
                bx, by + bh, new Color(145, 35, 28));
        g.setPaint(barn); g.fillRect(bx - wingW, by + bh - wingH, wingW, wingH); g.setPaint(null);
        // Barn roof
        int[] brx = {bx - wingW - 5, bx - wingW/2, bx + 5};
        int[] bry = {by + bh - wingH, by + bh - wingH - 25, by + bh - wingH};
        g.setColor(new Color(80, 55, 30)); g.fillPolygon(brx, bry, 3);

        // Main roof
        int[] rx = {bx - 12, bx + bw/2, bx + bw + 12};
        int[] ry = {by, by - h/3 + 5, by};
        GradientPaint roof = new GradientPaint(bx, by - h/3, new Color(185, 65, 45), bx+bw, by, new Color(140, 42, 28));
        g.setPaint(roof); g.fillPolygon(rx, ry, 3); g.setPaint(null);
        // Shingles
        g.setColor(new Color(100, 35, 18, 100));
        for (int i = 0; i < 4; i++) {
            int indent = i * 8;
            g.setStroke(new BasicStroke(1.5f));
            g.drawLine(bx - 12 + indent, by - (h/3 - 5) * i / 3,
                       bx + bw + 12 - indent, by - (h/3 - 5) * i / 3);
        }
        g.setStroke(new BasicStroke(1f));

        // Chimney
        g.setColor(new Color(155, 105, 65));
        g.fillRect(bx + bw - 35, by - h/3 - 15, 18, h/3 + 18);
        g.setColor(new Color(130, 85, 50));
        g.fillRect(bx + bw - 38, by - h/3 - 18, 24, 8);
        // Smoke
        for (int s = 0; s < 3; s++) {
            g.setColor(new Color(200, 200, 210, 60 - s * 15));
            g.fillOval(bx + bw - 30 + s * 2, by - h/3 - 28 - s * 12, 12 + s * 4, 12 + s * 4);
        }

        // Door
        g.setPaint(new GradientPaint(bx+bw/2-14, 0, new Color(110, 65, 22), bx+bw/2+14, 0, new Color(80, 45, 15)));
        g.fillRoundRect(bx + bw/2 - 14, by + bh/2, 28, bh/2, 6, 6); g.setPaint(null);
        g.setColor(new Color(200, 160, 70)); g.fillOval(bx + bw/2 + 6, by + bh*3/4, 5, 5);

        // Windows
        drawCartoonWindow(g, bx + 10, by + 8, 32, 26);
        drawCartoonWindow(g, bx + bw - 42, by + 8, 32, 26);

        // Flower boxes under windows
        g.setColor(new Color(150, 80, 40));
        g.fillRect(bx + 6, by + 36, 40, 8);
        g.fillRect(bx + bw - 46, by + 36, 40, 8);
        // Flowers
        Color[] fc = {new Color(255, 80, 80), new Color(255, 200, 50), new Color(200, 100, 255)};
        for (int fi = 0; fi < 3; fi++) {
            g.setColor(fc[fi]);
            g.fillOval(bx + 10 + fi * 12, by + 28, 8, 8);
            g.fillOval(bx + bw - 42 + fi * 12, by + 28, 8, 8);
        }

        // Outline
        g.setColor(new Color(100, 70, 35));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(bx, by, bw, bh);
        g.drawPolygon(rx, ry, 3);
        g.setStroke(new BasicStroke(1f));

        drawStarBadge(g, x + w - 18, y + 4, 3);
    }

    // ── STAGE 9-11: Village ──
    private static void drawVillage(Graphics2D g, int x, int y, int w, int h) {
        // Draw two houses side by side like a village
        int bx = x + 5, by = y + h/4, bw = w - 10, bh = h*3/4;

        // Ground shadow
        g.setColor(new Color(0, 0, 0, 40));
        g.fillOval(bx, by + bh - 4, bw, 14);

        // Back house (smaller, behind)
        int backW = bw * 2 / 5, backH = bh * 3 / 4;
        int backX = bx + bw / 2 - backW / 2;
        GradientPaint backWall = new GradientPaint(backX, by, new Color(200, 210, 230), backX, by + backH, new Color(160, 170, 195));
        g.setPaint(backWall); g.fillRect(backX, by, backW, backH); g.setPaint(null);
        int[] brx2 = {backX - 8, backX + backW/2, backX + backW + 8};
        int[] bry2 = {by, by - h/5, by};
        g.setColor(new Color(80, 60, 140)); g.fillPolygon(brx2, bry2, 3);
        drawCartoonWindow(g, backX + 8, by + 8, 22, 18);
        drawCartoonWindow(g, backX + backW - 30, by + 8, 22, 18);

        // Left house
        int lw = bw * 2 / 5, lh = bh * 4 / 5;
        int lx = bx;
        GradientPaint lWall = new GradientPaint(lx, by + bh - lh, new Color(240, 220, 180), lx, by + bh, new Color(205, 180, 140));
        g.setPaint(lWall); g.fillRect(lx, by + bh - lh, lw, lh); g.setPaint(null);
        int[] lrx = {lx - 8, lx + lw/2, lx + lw + 4};
        int[] lry = {by + bh - lh, by + bh - lh - h/4, by + bh - lh};
        g.setColor(new Color(190, 70, 50)); g.fillPolygon(lrx, lry, 3);
        drawCartoonWindow(g, lx + 8, by + bh - lh + 8, 26, 20);
        g.setPaint(new GradientPaint(lx+lw/2-10, 0, new Color(100,60,20), lx+lw/2+10, 0, new Color(70,40,10)));
        g.fillRoundRect(lx + lw/2 - 10, by + bh - lh/2, 20, lh/2, 5, 5); g.setPaint(null);

        // Right house
        int rw = bw * 2 / 5, rh = bh * 4 / 5;
        int rx2 = bx + bw - rw;
        GradientPaint rWall = new GradientPaint(rx2, by + bh - rh, new Color(230, 215, 200), rx2, by + bh, new Color(195, 175, 155));
        g.setPaint(rWall); g.fillRect(rx2, by + bh - rh, rw, rh); g.setPaint(null);
        int[] rrx = {rx2 - 4, rx2 + rw/2, rx2 + rw + 8};
        int[] rry = {by + bh - rh, by + bh - rh - h/4, by + bh - rh};
        g.setColor(new Color(60, 100, 160)); g.fillPolygon(rrx, rry, 3);
        drawCartoonWindow(g, rx2 + rw - 34, by + bh - rh + 8, 26, 20);
        g.setPaint(new GradientPaint(rx2+rw/2-10, 0, new Color(100,60,20), rx2+rw/2+10, 0, new Color(70,40,10)));
        g.fillRoundRect(rx2 + rw/2 - 10, by + bh - rh/2, 20, rh/2, 5, 5); g.setPaint(null);

        // Path between houses
        g.setColor(new Color(180, 155, 110, 140));
        int[] px2 = {bx + bw/2 - 15, bx + bw/2 + 15, bx + bw/2 + 12, bx + bw/2 - 12};
        int[] py2 = {by + bh - lh/2, by + bh - lh/2, by + bh, by + bh};
        g.fillPolygon(px2, py2, 4);

        // Outlines
        g.setColor(new Color(80, 60, 40));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRect(lx, by + bh - lh, lw, lh);
        g.drawPolygon(lrx, lry, 3);
        g.drawRect(rx2, by + bh - rh, rw, rh);
        g.drawPolygon(rrx, rry, 3);
        g.setStroke(new BasicStroke(1f));

        drawStarBadge(g, x + w - 18, y + 4, 4);
    }

    // ── STAGE 12-14: Grand Farm ──
    private static void drawGrandFarm(Graphics2D g, int x, int y, int w, int h) {
        int bx = x + 4, by = y + h/5, bw = w - 8, bh = h*4/5;

        g.setColor(new Color(0, 0, 0, 45));
        g.fillOval(bx, by + bh - 4, bw, 14);

        // Main large house
        int mw = bw * 3 / 5, mh = bh;
        int mx = bx + bw/2 - mw/2;
        GradientPaint mWall = new GradientPaint(mx, by, new Color(250, 235, 200), mx, by+mh, new Color(215, 195, 158));
        g.setPaint(mWall); g.fillRect(mx, by, mw, mh); g.setPaint(null);

        // Stone base
        g.setColor(new Color(170, 155, 130));
        g.fillRect(mx, by + mh - 14, mw, 14);
        g.setColor(new Color(140, 125, 105, 120));
        for (int i = 0; i < 6; i++) g.drawRect(mx + 2 + i * (mw/6), by + mh - 14, mw/6 - 2, 12);

        // Main roof
        int[] rx = {mx - 14, mx + mw/2, mx + mw + 14};
        int[] ry = {by, by - h/4, by};
        GradientPaint roof = new GradientPaint(mx, by - h/4, new Color(180, 60, 40), mx+mw, by, new Color(140, 40, 25));
        g.setPaint(roof); g.fillPolygon(rx, ry, 3); g.setPaint(null);
        // Roof ridge
        g.setColor(new Color(100, 30, 15));
        g.setStroke(new BasicStroke(3f));
        g.drawLine(mx + mw/2, by - h/4, mx + mw/2, by);
        g.setStroke(new BasicStroke(1f));

        // Side towers
        for (int side = 0; side <= 1; side++) {
            int tx = side == 0 ? bx : bx + bw - bw/5;
            int tw = bw / 5, th = bh * 3 / 4;
            GradientPaint tWall = new GradientPaint(tx, by + bh - th, new Color(220, 200, 165),
                    tx + tw, by + bh, new Color(185, 165, 130));
            g.setPaint(tWall); g.fillRect(tx, by + bh - th, tw, th); g.setPaint(null);
            // Tower cone roof
            int[] trx = {tx - 5, tx + tw/2, tx + tw + 5};
            int[] try2 = {by + bh - th, by + bh - th - h/5, by + bh - th};
            g.setColor(new Color(60, 100, 160)); g.fillPolygon(trx, try2, 3);
            // Tower window
            drawCartoonWindow(g, tx + tw/2 - 10, by + bh - th + 8, 20, 16);
            g.setColor(new Color(80, 60, 40));
            g.setStroke(new BasicStroke(1.5f));
            g.drawRect(tx, by + bh - th, tw, th);
            g.setStroke(new BasicStroke(1f));
        }

        // Chimney pair
        g.setColor(new Color(160, 110, 70));
        g.fillRect(mx + mw - 40, by - h/4 + 5, 14, h/4 + 8);
        g.fillRect(mx + mw - 25, by - h/4 + 10, 12, h/4 + 3);
        g.setColor(new Color(135, 90, 55));
        g.fillRect(mx + mw - 43, by - h/4 + 3, 20, 7);
        g.fillRect(mx + mw - 28, by - h/4 + 8, 18, 6);

        // Windows
        drawCartoonWindow(g, mx + 10, by + 10, 34, 26);
        drawCartoonWindow(g, mx + mw - 44, by + 10, 34, 26);
        drawCartoonWindow(g, mx + mw/2 - 14, by + 10, 28, 22);

        // Front door double
        g.setColor(new Color(100, 60, 20));
        g.fillRoundRect(mx + mw/2 - 18, by + mh/2, 16, mh/2, 5, 5);
        g.fillRoundRect(mx + mw/2 + 2, by + mh/2, 16, mh/2, 5, 5);
        g.setColor(new Color(195, 160, 70));
        g.fillOval(mx + mw/2 - 5, by + mh*3/4, 5, 5);
        g.fillOval(mx + mw/2 + 4, by + mh*3/4, 5, 5);

        // Columns
        g.setColor(new Color(240, 230, 210));
        for (int c = 0; c < 2; c++) {
            int colX = mx + mw/2 - 22 + c * 30;
            g.fillRect(colX, by + mh/2 - 10, 6, mh/2 + 10);
            g.setColor(new Color(200, 190, 170)); g.fillOval(colX - 3, by + mh/2 - 14, 12, 8);
            g.setColor(new Color(240, 230, 210));
        }

        // Outline
        g.setColor(new Color(100, 75, 40));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(mx, by, mw, mh);
        g.drawPolygon(rx, ry, 3);
        g.setStroke(new BasicStroke(1f));

        drawStarBadge(g, x + w - 18, y + 4, 5);
    }

    // ── STAGE 15-17: Castle Farm ──
    private static void drawCastleFarm(Graphics2D g, int x, int y, int w, int h) {
        int bx = x + 2, by = y + h/6, bw = w - 4, bh = h*5/6;

        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(bx, by + bh - 4, bw, 16);

        // Castle wall — stone grey
        GradientPaint stone = new GradientPaint(bx, by, new Color(185, 185, 195), bx, by+bh, new Color(145, 145, 160));
        g.setPaint(stone); g.fillRect(bx, by, bw, bh); g.setPaint(null);

        // Stone block pattern
        g.setColor(new Color(120, 120, 135, 100));
        g.setStroke(new BasicStroke(1f));
        for (int row = 0; row < 7; row++) {
            int offsetX = (row % 2 == 0) ? 0 : 16;
            for (int col = -1; col < bw/32 + 1; col++) {
                g.drawRect(bx + col * 32 + offsetX, by + row * (bh/7), 30, bh/7 - 1);
            }
        }
        g.setStroke(new BasicStroke(1f));

        // Battlements (crenellations) on top
        g.setColor(new Color(170, 170, 182));
        for (int cr = 0; cr < bw/16; cr++) {
            if (cr % 2 == 0) g.fillRect(bx + cr * 16, by - 14, 14, 14);
        }

        // Corner towers
        int[] towerPositions = {bx, bx + bw - 22};
        for (int tp : towerPositions) {
            GradientPaint tWall = new GradientPaint(tp, by - 20, new Color(170, 170, 185),
                    tp + 22, by + bh, new Color(130, 130, 148));
            g.setPaint(tWall); g.fillRect(tp, by - 20, 22, bh + 20); g.setPaint(null);
            // Tower crenellations
            g.setColor(new Color(155, 155, 170));
            for (int ct = 0; ct < 3; ct++) {
                if (ct % 2 == 0) g.fillRect(tp + ct * 8, by - 32, 7, 13);
            }
            // Tower windows (arrow slits)
            g.setColor(new Color(40, 40, 60));
            g.fillRect(tp + 8, by - 5, 5, 14);
            g.fillRect(tp + 8, by + 30, 5, 14);
            g.setColor(new Color(80, 60, 40));
            g.setStroke(new BasicStroke(1.5f));
            g.drawRect(tp, by - 20, 22, bh + 20);
            g.setStroke(new BasicStroke(1f));
        }

        // Central tower taller
        int ctW = bw / 3, ctX = bx + bw/2 - ctW/2;
        GradientPaint ctWall = new GradientPaint(ctX, by - 35, new Color(175, 175, 188),
                ctX + ctW, by, new Color(135, 135, 152));
        g.setPaint(ctWall); g.fillRect(ctX, by - 35, ctW, 35); g.setPaint(null);
        // Central tower cone
        int[] crx = {ctX - 5, ctX + ctW/2, ctX + ctW + 5};
        int[] cry = {by - 35, by - 35 - h/5, by - 35};
        g.setColor(new Color(50, 120, 60)); g.fillPolygon(crx, cry, 3);
        // Flag
        g.setColor(new Color(200, 50, 50));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(ctX + ctW/2, by - 35 - h/5, ctX + ctW/2, by - 35 - h/5 + 20);
        g.setStroke(new BasicStroke(1f));
        int[] flagX = {ctX + ctW/2, ctX + ctW/2 + 12, ctX + ctW/2};
        int[] flagY = {by - 35 - h/5, by - 35 - h/5 + 6, by - 35 - h/5 + 12};
        g.fillPolygon(flagX, flagY, 3);

        // Gate arch
        g.setColor(new Color(40, 35, 50));
        g.fillArc(bx + bw/2 - 20, by + bh/3, 40, 40, 0, 180);
        g.fillRect(bx + bw/2 - 20, by + bh/3 + 20, 40, bh*2/3 - 20);
        // Portcullis bars
        g.setColor(new Color(80, 70, 90));
        g.setStroke(new BasicStroke(2f));
        for (int bar = 0; bar < 3; bar++)
            g.drawLine(bx + bw/2 - 16 + bar * 10, by + bh/3 + 5,
                       bx + bw/2 - 16 + bar * 10, by + bh - 2);
        g.drawLine(bx + bw/2 - 18, by + bh/3 + 20, bx + bw/2 + 18, by + bh/3 + 20);
        g.setStroke(new BasicStroke(1f));

        // Main windows
        drawCartoonWindow(g, bx + 28, by + 12, 28, 22);
        drawCartoonWindow(g, bx + bw - 56, by + 12, 28, 22);

        // Moat hint
        g.setPaint(new GradientPaint(bx, by + bh, new Color(80, 130, 200, 180),
                bx, by + bh + 10, new Color(50, 100, 180, 100)));
        g.fillRect(bx, by + bh, bw, 10); g.setPaint(null);

        // Outline
        g.setColor(new Color(80, 80, 100));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(bx, by, bw, bh);
        g.setStroke(new BasicStroke(1f));

        drawStarBadge(g, x + w - 18, y + 4, 6);
    }

    // ── STAGE 18-19: Royal Palace ──
    private static void drawRoyalPalace(Graphics2D g, int x, int y, int w, int h) {
        int bx = x + 2, by = y + h/8, bw = w - 4, bh = h*7/8;

        g.setColor(new Color(0, 0, 0, 55));
        g.fillOval(bx, by + bh - 4, bw, 18);

        // Grand marble walls
        GradientPaint marble = new GradientPaint(bx, by, new Color(245, 240, 220), bx + bw, by + bh, new Color(210, 205, 185));
        g.setPaint(marble); g.fillRect(bx, by, bw, bh); g.setPaint(null);

        // Marble veins
        g.setColor(new Color(190, 185, 165, 60));
        g.setStroke(new BasicStroke(1f));
        for (int v = 0; v < 5; v++) {
            g.drawLine(bx + v * (bw/5), by, bx + v * (bw/5) + 10, by + bh);
        }
        g.setStroke(new BasicStroke(1f));

        // Gold trim
        g.setColor(new Color(220, 180, 40));
        g.fillRect(bx, by + bh - 12, bw, 12);
        g.fillRect(bx, by, bw, 8);

        // Five spire towers
        int[] spireX = {bx, bx + bw/4, bx + bw/2 - 15, bx + bw*3/4, bx + bw - 22};
        int[] spireH = {bh*2/3, bh*3/5, bh*4/5, bh*3/5, bh*2/3};
        Color[] spireColors = {
            new Color(60, 110, 180), new Color(80, 160, 80),
            new Color(180, 50, 50),
            new Color(80, 160, 80), new Color(60, 110, 180)
        };
        for (int sp = 0; sp < 5; sp++) {
            int sw = 22, sh = spireH[sp];
            int sx = spireX[sp];
            GradientPaint sWall = new GradientPaint(sx, by + bh - sh, new Color(230, 225, 210),
                    sx + sw, by + bh, new Color(195, 190, 175));
            g.setPaint(sWall); g.fillRect(sx, by + bh - sh, sw, sh); g.setPaint(null);
            // Spire cone
            int[] srx = {sx - 4, sx + sw/2, sx + sw + 4};
            int[] sry = {by + bh - sh, by + bh - sh - h/4, by + bh - sh};
            g.setColor(spireColors[sp]); g.fillPolygon(srx, sry, 3);
            // Gold tip
            g.setColor(new Color(255, 215, 0));
            g.fillOval(sx + sw/2 - 3, by + bh - sh - h/4 - 4, 6, 6);
            // Spire window
            drawCartoonWindow(g, sx + 4, by + bh - sh + 6, 14, 12);
            g.setColor(new Color(100, 90, 70));
            g.setStroke(new BasicStroke(1.5f));
            g.drawRect(sx, by + bh - sh, sw, sh);
            g.setStroke(new BasicStroke(1f));
        }

        // Grand arched entrance
        g.setColor(new Color(180, 145, 60));
        g.fillRect(bx + bw/2 - 22, by + bh/2 - 10, 44, 8); // arch header
        g.setColor(new Color(110, 75, 25));
        g.fillArc(bx + bw/2 - 22, by + bh/3, 44, 44, 0, 180);
        g.fillRect(bx + bw/2 - 22, by + bh/3 + 22, 44, bh*2/3 - 22);
        // Door gold knobs
        g.setColor(new Color(220, 180, 40));
        g.fillOval(bx + bw/2 - 5, by + bh*2/3, 7, 7);
        g.fillOval(bx + bw/2 + 2, by + bh*2/3, 7, 7);

        // Columns
        for (int col = 0; col < 4; col++) {
            int colX = bx + bw/2 - 45 + col * 28;
            g.setColor(new Color(240, 235, 215));
            g.fillRect(colX, by + bh/3 + 20, 8, bh*2/3 - 20);
            g.setColor(new Color(220, 180, 40));
            g.fillOval(colX - 3, by + bh/3 + 18, 14, 6);
            g.fillOval(colX - 3, by + bh - 8, 14, 6);
        }

        // Ornate windows
        drawCartoonWindow(g, bx + 28, by + 10, 32, 26);
        drawCartoonWindow(g, bx + bw - 60, by + 10, 32, 26);
        drawCartoonWindow(g, bx + 65, by + 10, 28, 22);
        drawCartoonWindow(g, bx + bw - 93, by + 10, 28, 22);

        // Crown on top center
        g.setColor(new Color(220, 180, 30));
        int[] crownX = {bx+bw/2-16, bx+bw/2-16, bx+bw/2-8, bx+bw/2-4,
                         bx+bw/2, bx+bw/2+4, bx+bw/2+8, bx+bw/2+16, bx+bw/2+16};
        int[] crownY = {by+8, by-4, by-10, by-4, by-14, by-4, by-10, by-4, by+8};
        g.fillPolygon(crownX, crownY, 9);
        g.setColor(new Color(200, 50, 50)); g.fillOval(bx+bw/2-4, by-12, 8, 8);
        g.setColor(new Color(50, 100, 200)); g.fillOval(bx+bw/2-14, by-6, 6, 6);
        g.fillOval(bx+bw/2+8, by-6, 6, 6);

        g.setColor(new Color(150, 120, 60));
        g.setStroke(new BasicStroke(2f));
        g.drawRect(bx, by, bw, bh);
        g.setStroke(new BasicStroke(1f));

        drawStarBadge(g, x + w - 18, y + 4, 7);
    }

    // ── STAGE 20: Legend Mansion ──
    private static void drawLegendMansion(Graphics2D g, int x, int y, int w, int h) {
        // Golden glowing mansion
        int bx = x + 1, by = y + h/10, bw = w - 2, bh = h*9/10;

        // Glow aura
        g.setColor(new Color(255, 215, 0, 30));
        g.fillOval(bx - 10, by - 10, bw + 20, bh + 20);
        g.setColor(new Color(255, 215, 0, 18));
        g.fillOval(bx - 20, by - 20, bw + 40, bh + 40);

        g.setColor(new Color(0, 0, 0, 55));
        g.fillOval(bx, by + bh - 4, bw, 18);

        // Golden walls
        GradientPaint gold = new GradientPaint(bx, by, new Color(255, 235, 150), bx+bw, by+bh, new Color(200, 160, 60));
        g.setPaint(gold); g.fillRect(bx, by, bw, bh); g.setPaint(null);

        // Diamond pattern on walls
        g.setColor(new Color(220, 180, 50, 80));
        for (int drow = 0; drow < 6; drow++) {
            for (int dcol = 0; dcol < bw/20; dcol++) {
                int dx = bx + dcol * 20 + (drow % 2) * 10;
                int dy = by + drow * (bh/6);
                g.drawLine(dx, dy + bh/12, dx + 10, dy);
                g.drawLine(dx + 10, dy, dx + 20, dy + bh/12);
                g.drawLine(dx + 20, dy + bh/12, dx + 10, dy + bh/6);
                g.drawLine(dx + 10, dy + bh/6, dx, dy + bh/12);
            }
        }

        // Seven golden spires
        for (int sp = 0; sp < 7; sp++) {
            int sw = 18;
            int sx = bx + sp * (bw / 6) - sw/2;
            if (sx < bx) sx = bx;
            if (sx + sw > bx + bw) sx = bx + bw - sw;
            int sh = (sp == 3) ? bh : bh * 4 / 5;
            GradientPaint spW = new GradientPaint(sx, by+bh-sh, new Color(255,240,170), sx+sw, by+bh, new Color(200,160,55));
            g.setPaint(spW); g.fillRect(sx, by+bh-sh, sw, sh); g.setPaint(null);
            // Gold cone
            int[] srx = {sx-5, sx+sw/2, sx+sw+5};
            int[] sry = {by+bh-sh, by+bh-sh-h/4, by+bh-sh};
            GradientPaint cone = new GradientPaint(sx, by+bh-sh-h/4, new Color(255,215,0),
                    sx+sw, by+bh-sh, new Color(180,130,10));
            g.setPaint(cone); g.fillPolygon(srx, sry, 3); g.setPaint(null);
            // Star on tip
            g.setColor(new Color(255, 255, 100));
            g.fillOval(sx+sw/2-4, by+bh-sh-h/4-5, 8, 8);
            // Spire border
            g.setColor(new Color(180, 130, 20));
            g.setStroke(new BasicStroke(1.5f));
            g.drawRect(sx, by+bh-sh, sw, sh);
            g.drawPolygon(srx, sry, 3);
            g.setStroke(new BasicStroke(1f));
        }

        // Grand double door with arch
        g.setColor(new Color(150, 100, 20));
        g.fillArc(bx+bw/2-24, by+bh/3-10, 48, 48, 0, 180);
        g.setColor(new Color(120, 80, 15));
        g.fillRect(bx+bw/2-24, by+bh/3+14, 24, bh*2/3-14);
        g.fillRect(bx+bw/2, by+bh/3+14, 24, bh*2/3-14);
        // Gold door trim
        g.setColor(new Color(255, 215, 0));
        g.setStroke(new BasicStroke(2f));
        g.drawArc(bx+bw/2-24, by+bh/3-10, 48, 48, 0, 180);
        g.drawLine(bx+bw/2, by+bh/3+14, bx+bw/2, by+bh);
        g.setStroke(new BasicStroke(1f));
        g.fillOval(bx+bw/2-6, by+bh*2/3, 8, 8);
        g.fillOval(bx+bw/2+2, by+bh*2/3, 8, 8);

        // Gem encrusted columns
        Color[] gemColors = {new Color(200,50,50), new Color(50,100,255), new Color(50,200,50)};
        for (int col = 0; col < 3; col++) {
            int colX = bx+bw/2-42+col*30;
            g.setColor(new Color(255,240,180));
            g.fillRect(colX, by+bh/3+10, 8, bh*2/3-10);
            g.setColor(new Color(220,180,40));
            g.fillOval(colX-3, by+bh/3+8, 14, 7);
            g.fillOval(colX-3, by+bh-8, 14, 7);
            // Gems on column
            g.setColor(gemColors[col]);
            g.fillOval(colX+1, by+bh/2, 6, 6);
        }

        // Ornate windows
        drawCartoonWindow(g, bx+22, by+10, 32, 26);
        drawCartoonWindow(g, bx+bw-54, by+10, 32, 26);
        drawCartoonWindow(g, bx+58, by+10, 26, 22);
        drawCartoonWindow(g, bx+bw-84, by+10, 26, 22);

        // Rainbow sparkles
        Color[] sparkColors = {new Color(255,80,80), new Color(255,180,50),
                new Color(100,255,80), new Color(80,160,255), new Color(200,80,255)};
        int[][] sparkPos = {{bx+15,by+60},{bx+bw-20,by+50},{bx+bw/4,by+bh/3},
                           {bx+bw*3/4,by+bh/3},{bx+bw/2,by+20}};
        for (int sp = 0; sp < 5; sp++) {
            g.setColor(sparkColors[sp]);
            int spx = sparkPos[sp][0], spy = sparkPos[sp][1];
            g.drawLine(spx-5, spy, spx+5, spy);
            g.drawLine(spx, spy-5, spx, spy+5);
            g.drawLine(spx-3, spy-3, spx+3, spy+3);
            g.drawLine(spx+3, spy-3, spx-3, spy+3);
        }

        // Gold outline
        g.setColor(new Color(180, 130, 10));
        g.setStroke(new BasicStroke(2.5f));
        g.drawRect(bx, by, bw, bh);
        g.setStroke(new BasicStroke(1f));

        // LEGEND text banner
        g.setPaint(new GradientPaint(bx+bw/2-35, by+bh-22, new Color(200,160,10),
                bx+bw/2+35, by+bh-8, new Color(255,215,0)));
        g.fillRoundRect(bx+bw/2-35, by+bh-22, 70, 18, 6, 6); g.setPaint(null);
        g.setFont(new Font("SansSerif", Font.BOLD, 9));
        g.setColor(new Color(60, 30, 0));
        FontMetrics fm = g.getFontMetrics();
        g.drawString("LEGEND", bx+bw/2-fm.stringWidth("LEGEND")/2, by+bh-8);

        drawStarBadge(g, x + w - 18, y + 4, 8);
    }

    // ── Shared helpers ──

    private static void drawCartoonWindow(Graphics2D g, int x, int y, int w, int h) {
        // Frame
        g.setColor(new Color(140, 100, 50));
        g.fillRoundRect(x - 3, y - 3, w + 6, h + 6, 5, 5);
        // Glass
        GradientPaint glass = new GradientPaint(x, y, new Color(200, 230, 255, 200),
                x + w, y + h, new Color(150, 200, 255, 180));
        g.setPaint(glass); g.fillRoundRect(x, y, w, h, 3, 3); g.setPaint(null);
        // Cross panes
        g.setColor(new Color(100, 80, 50, 160));
        g.drawLine(x + w/2, y, x + w/2, y + h);
        g.drawLine(x, y + h/2, x + w, y + h/2);
        // Shine
        g.setColor(new Color(255, 255, 255, 120));
        g.fillOval(x + 2, y + 2, w/3, h/3);
    }

    private static void drawStarBadge(Graphics2D g, int x, int y, int count) {
        // Small gold star badge showing stage tier
        g.setColor(new Color(255, 215, 0, 200));
        g.setFont(new Font("SansSerif", Font.BOLD, 9));
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < Math.min(count, 5); i++) stars.append("\u2605");
        g.drawString(stars.toString(), x - stars.length() * 5, y + 10);
    }

    private FarmHouseRenderer() {}
}