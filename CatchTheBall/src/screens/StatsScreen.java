package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.models.PlayerProfile;
import OOP_PROJECT.CatchTheBall.src.renderers.FarmBackgroundRenderer;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class StatsScreen extends Screen {
    private Rectangle backBtn;
    private int hovered = -1;

    public StatsScreen(GamePanel panel) { super(panel); }

    @Override public void onEnter() { super.onEnter(); }
    @Override public void update()  { tickCount++; }

    @Override
    public void draw(Graphics2D g) {
        FarmBackgroundRenderer.draw(g, GamePanel.W, GamePanel.H, tickCount);

        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);

        RenderUtils.drawHeaderBar(g, GamePanel.W, "Player Stats");

        PlayerProfile p = panel.getPlayerData().getProfile();
        int px = 40, py = 100, pw = GamePanel.W - 80, ph = GamePanel.H - 170;
        RenderUtils.drawGradientPanel(g, px, py, pw, ph,
                new Color(22, 52, 16, 220), new Color(12, 34, 8, 220),
                new Color(88, 172, 62), 2f, 16);

        int col1 = px + 30, col2 = px + pw / 2 + 20, sy = py + 30;

        // ── Left column ─────────────────────────────────────────────────────
        drawStatRow(g, col1, sy, StatIcon.MEDAL,    "Games Played",  "" + p.getTotalGamesPlayed());  sy += 32;
        drawStatRow(g, col1, sy, StatIcon.TROPHY,   "Best Score",    "" + p.getAllTimeBestScore());   sy += 32;
        drawStatRow(g, col1, sy, StatIcon.FLAME,    "Best Combo",    "x" + p.getAllTimeBestCombo());  sy += 32;
        drawStatRow(g, col1, sy, StatIcon.APPLE,    "Balls Caught",  "" + p.getTotalBallsCaught());   sy += 32;
        drawStatRow(g, col1, sy, StatIcon.COIN,     "Total Earned",  "" + p.getTotalCoinsEarned());   sy += 32;
        drawStatRow(g, col1, sy, StatIcon.BASKET,   "Total Spent",   "" + p.getTotalCoinsSpent());

        // ── Right column ────────────────────────────────────────────────────
        sy = py + 30;
        String acc = panel.getAccountManager().getActiveAccountName();
        drawStatRow(g, col2, sy, StatIcon.PERSON,   "Account",       acc != null ? acc : "Guest");   sy += 32;
        drawStatRow(g, col2, sy, StatIcon.HOUSE,    "Farm Stage",    panel.getPlayerData().getFarmStage() + "/20"); sy += 32;
        String streak = p.getConsecutiveLoginDays() > 0
                ? p.getConsecutiveLoginDays() + " day streak" : "No streak";
        drawStatRow(g, col2, sy, StatIcon.CALENDAR, "Login Streak",  streak);                         sy += 32;
        drawStatRow(g, col2, sy, StatIcon.LEAF,     "Farmer Name",   p.getFarmerName());              sy += 32;
        drawStatRow(g, col2, sy, StatIcon.SHIRT,    "Skin",          p.getEquippedSkin().getDisplayName()); sy += 32;
        drawStatRow(g, col2, sy, StatIcon.BUCKET,   "Basket",        p.getEquippedBasket().getDisplayName());

        int chartY = py + 230, chartH = 140;
        drawScoreHistoryChart(g, px + 30, chartY, pw - 60, chartH, p);

        backBtn = new Rectangle(20, GamePanel.H - 52, 140, 36);
        RenderUtils.drawButton(g, backBtn, "< Back", hovered == 0, FontManager.getBold(14));
    }

    // ── Icon enum ────────────────────────────────────────────────────────────

    private enum StatIcon {
        MEDAL, TROPHY, FLAME, APPLE, COIN, BASKET,
        PERSON, HOUSE, CALENDAR, LEAF, SHIRT, BUCKET
    }

    // ── Stat row with drawn icon ─────────────────────────────────────────────

    private void drawStatRow(Graphics2D g, int x, int y, StatIcon icon, String label, String val) {
        // Small icon drawn in a 16×16 area, vertically centred on the text baseline
        int iconSize = 15;
        int iconTop  = y - iconSize + 3;
        drawStatIcon(g, icon, x, iconTop, iconSize);

        // Label
        g.setFont(FontManager.getBody(13));
        g.setColor(new Color(155, 195, 135));
        g.drawString(label, x + iconSize + 6, y);

        // Value
        g.setFont(FontManager.getBodyBold(14));
        g.setColor(Color.WHITE);
        g.drawString(val, x + 220, y);
    }

    /**
     * Draws a small stat icon using pure Java2D — no Unicode emoji dependency.
     * All icons fit within a square of (size × size) starting at (x, y).
     */
    private void drawStatIcon(Graphics2D g, StatIcon icon, int x, int y, int size) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int cx = x + size / 2;
        int cy = y + size / 2;
        int r  = size / 2 - 1;

        switch (icon) {

            case MEDAL: {
                // Circle with ribbon
                g2.setColor(new Color(255, 195, 20));
                g2.fillOval(cx - r, cy - r + 3, r * 2, r * 2);
                g2.setColor(new Color(195, 140, 10));
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawOval(cx - r, cy - r + 3, r * 2, r * 2);
                g2.setStroke(new BasicStroke(1f));
                // Ribbon top
                g2.setColor(new Color(70, 130, 220));
                g2.fillRect(cx - 2, y, 4, r);
                break;
            }

            case TROPHY: {
                // Cup shape
                int tw = (int)(r * 1.6), th = (int)(r * 1.2);
                int tx = cx - tw / 2, ty = cy - r + 1;
                g2.setColor(new Color(255, 205, 20));
                g2.fillRoundRect(tx, ty, tw, th, 4, 4);
                // Handles
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(tx - 3, ty + 2, 6, th / 2, 90, 180);
                g2.drawArc(tx + tw - 3, ty + 2, 6, th / 2, 270, 180);
                g2.setStroke(new BasicStroke(1f));
                // Base stem
                g2.setColor(new Color(195, 155, 10));
                g2.fillRect(cx - 2, ty + th, 4, r / 2);
                g2.fillRect(cx - r / 2, ty + th + r / 2 - 1, r, 3);
                break;
            }

            case FLAME: {
                // Teardrop flame using Path2D
                Path2D.Float flame = new Path2D.Float();
                flame.moveTo(cx, y + size - 1);
                flame.curveTo(cx - r * 0.9f, cy + r * 0.3f,
                              cx - r * 0.6f, cy - r * 0.5f,
                              cx, y + 1);
                flame.curveTo(cx + r * 0.6f, cy - r * 0.5f,
                              cx + r * 0.9f, cy + r * 0.3f,
                              cx, y + size - 1);
                g2.setColor(new Color(255, 80, 10));
                g2.fill(flame);
                // Inner yellow core
                Path2D.Float inner = new Path2D.Float();
                int is = (int)(r * 0.55f);
                inner.moveTo(cx, y + size - 2);
                inner.curveTo(cx - is * 0.8f, cy + is * 0.3f,
                              cx - is * 0.5f, cy - is * 0.5f,
                              cx, cy - is);
                inner.curveTo(cx + is * 0.5f, cy - is * 0.5f,
                              cx + is * 0.8f, cy + is * 0.3f,
                              cx, y + size - 2);
                g2.setColor(new Color(255, 210, 20));
                g2.fill(inner);
                break;
            }

            case APPLE: {
                // Red circle with green leaf + stem
                g2.setColor(new Color(220, 50, 40));
                g2.fillOval(cx - r, cy - r + 2, r * 2, r * 2);
                // Highlight
                g2.setColor(new Color(255, 140, 130, 100));
                g2.fillOval(cx - r / 2, cy - r / 2 + 2, r / 2, r / 3);
                // Stem
                g2.setColor(new Color(100, 60, 20));
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(cx, cy - r + 2, cx + r / 2, cy - r - 2);
                // Leaf
                g2.setColor(new Color(50, 175, 20));
                g2.fillOval(cx, cy - r - 2, r / 2 + 1, r / 3 + 1);
                g2.setStroke(new BasicStroke(1f));
                break;
            }

            case COIN: {
                // Gold circle with $ inside
                g2.setColor(new Color(255, 195, 20));
                g2.fillOval(cx - r, cy - r, r * 2, r * 2);
                g2.setColor(new Color(195, 145, 8));
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawOval(cx - r, cy - r, r * 2, r * 2);
                g2.setStroke(new BasicStroke(1f));
                g2.setFont(FontManager.getBold(size - 4));
                g2.setColor(new Color(140, 100, 5));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("$", cx - fm.stringWidth("$") / 2, cy + fm.getAscent() / 2 - 1);
                break;
            }

            case BASKET: {
                // Simple basket/cart outline
                int bw = (int)(r * 1.6), bh = (int)(r * 1.1);
                int bx = cx - bw / 2, by2 = cy - bh / 2 + 2;
                g2.setColor(new Color(200, 140, 60));
                g2.fillRoundRect(bx, by2, bw, bh, 4, 4);
                // Weave lines
                g2.setColor(new Color(160, 100, 30));
                g2.setStroke(new BasicStroke(1f));
                for (int lx = bx + 3; lx < bx + bw - 1; lx += 4)
                    g2.drawLine(lx, by2, lx, by2 + bh);
                g2.drawLine(bx, by2 + bh / 2, bx + bw, by2 + bh / 2);
                // Handle arc
                g2.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(160, 100, 30));
                g2.drawArc(bx + bw / 4, by2 - r / 2 - 1, bw / 2, r, 0, 180);
                g2.setStroke(new BasicStroke(1f));
                break;
            }

            case PERSON: {
                // Head circle + body arc
                int hr = r / 2;
                g2.setColor(new Color(120, 200, 100));
                g2.fillOval(cx - hr, y + 1, hr * 2, hr * 2);
                // Body
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawArc(cx - r, cy, r * 2, r, 0, 180);
                g2.setStroke(new BasicStroke(1f));
                break;
            }

            case HOUSE: {
                // Roof triangle + house rectangle
                int hw = (int)(r * 1.7);
                int houseY = cy - r / 4;
                int houseH = (int)(r * 1.1);
                // Walls
                g2.setColor(new Color(200, 140, 70));
                g2.fillRect(cx - hw / 2, houseY, hw, houseH);
                // Roof
                g2.setColor(new Color(180, 50, 40));
                int[] rx = {cx - hw / 2 - 2, cx + hw / 2 + 2, cx};
                int[] ry = {houseY, houseY, y + 1};
                g2.fillPolygon(rx, ry, 3);
                // Door
                g2.setColor(new Color(120, 75, 30));
                g2.fillRect(cx - 2, houseY + houseH / 2, 5, houseH / 2);
                break;
            }

            case CALENDAR: {
                // Rectangle with grid lines
                int cw = (int)(r * 1.7), ch = (int)(r * 1.5);
                int calX = cx - cw / 2, calY = cy - ch / 2 + 1;
                g2.setColor(new Color(80, 140, 220));
                g2.fillRoundRect(calX, calY, cw, ch, 3, 3);
                // Header strip
                g2.setColor(new Color(50, 100, 190));
                g2.fillRoundRect(calX, calY, cw, ch / 3, 3, 3);
                g2.fillRect(calX, calY + ch / 3 - 2, cw, 4);
                // Grid dots
                g2.setColor(new Color(200, 225, 255));
                int dotSize = 2;
                for (int row = 0; row < 2; row++)
                    for (int col = 0; col < 3; col++)
                        g2.fillRect(calX + 3 + col * (cw / 3),
                                    calY + ch / 3 + 3 + row * (ch / 3),
                                    dotSize, dotSize);
                break;
            }

            case LEAF: {
                // Oval leaf with center vein
                g2.setColor(new Color(60, 185, 50));
                Ellipse2D.Float leaf = new Ellipse2D.Float(cx - r, cy - r / 2, r * 2, r + 2);
                AffineTransform old = g2.getTransform();
                g2.rotate(Math.toRadians(-30), cx, cy);
                g2.fill(leaf);
                g2.setColor(new Color(30, 120, 20));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawLine(cx - r + 2, cy + r / 2, cx + r - 2, cy - r / 2);
                g2.setStroke(new BasicStroke(1f));
                g2.setTransform(old);
                break;
            }

            case SHIRT: {
                // Simple T-shirt silhouette
                // Sleeves
                g2.setColor(new Color(80, 160, 220));
                int[] slx = {cx - r, cx - r / 2, cx - r / 2};
                int[] sly = {cy - r / 2, cy - r / 3, cy + r / 3};
                g2.fillPolygon(slx, sly, 3);
                int[] srx = {cx + r, cx + r / 2, cx + r / 2};
                int[] sry = {cy - r / 2, cy - r / 3, cy + r / 3};
                g2.fillPolygon(srx, sry, 3);
                // Body
                g2.fillRoundRect(cx - r / 2, cy - r / 3, r, (int)(r * 1.4), 3, 3);
                // Collar notch
                g2.setColor(new Color(50, 120, 180));
                g2.fillArc(cx - r / 4, cy - r / 3 - 1, r / 2, r / 3, 0, 180);
                break;
            }

            case BUCKET: {
                // Trapezoid bucket
                int topW = (int)(r * 1.2), botW = (int)(r * 1.6), bucketH = (int)(r * 1.3);
                int bucketY = cy - bucketH / 2 + 2;
                int[] bux = {cx - botW / 2, cx + botW / 2, cx + topW / 2, cx - topW / 2};
                int[] buy = {bucketY + bucketH, bucketY + bucketH, bucketY, bucketY};
                g2.setColor(new Color(70, 155, 200));
                g2.fillPolygon(bux, buy, 4);
                g2.setColor(new Color(40, 110, 160));
                g2.setStroke(new BasicStroke(1.3f));
                g2.drawPolygon(bux, buy, 4);
                // Handle arc
                g2.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(new Color(40, 110, 160));
                g2.drawArc(cx - topW / 2, bucketY - r / 2, topW, r / 2 + 2, 0, 180);
                g2.setStroke(new BasicStroke(1f));
                break;
            }
        }

        g2.dispose();
    }

    // ── Score history chart ──────────────────────────────────────────────────

    private void drawScoreHistoryChart(Graphics2D g, int x, int y, int w, int h, PlayerProfile p) {
        g.setFont(FontManager.getBodyBold(12));
        g.setColor(ColorPalette.TEXT_GOLD);
        g.drawString("Score History (last 10 games)", x, y - 6);
        g.setColor(new Color(0, 0, 0, 80));
        g.fillRoundRect(x, y, w, h, 8, 8);
        g.setColor(new Color(60, 110, 45));
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(x, y, w, h, 8, 8);
        g.setStroke(new BasicStroke(1f));

        int cnt = p.getScoreHistoryCount();
        if (cnt == 0) {
            RenderUtils.drawCenteredText(g, "No games played yet", x + w / 2, y + h / 2 + 5,
                    FontManager.getBody(13, Font.ITALIC), new Color(130, 170, 120));
            return;
        }
        int[] scores = p.getLastTenScores();
        int maxScore = 1;
        for (int i = 0; i < cnt; i++) if (scores[i] > maxScore) maxScore = scores[i];

        int barW      = Math.max(10, (w - 20) / Math.max(cnt, 1) - 4);
        int totalUsed = (barW + 4) * cnt;
        int startX    = x + (w - totalUsed) / 2;

        for (int i = 0; i < cnt; i++) {
            float ratio = (float) scores[i] / maxScore;
            int bh2 = (int) ((h - 30) * ratio);
            int bx  = startX + i * (barW + 4);
            int by  = y + h - bh2 - 12;
            if (bh2 > 0) {
                GradientPaint gp = new GradientPaint(bx, by, new Color(100, 220, 80),
                        bx, by + bh2, new Color(50, 140, 40));
                g.setPaint(gp);
                g.fillRoundRect(bx, by, barW, bh2, 4, 4);
                g.setPaint(null);
                g.setColor(new Color(150, 240, 100));
                g.setStroke(new BasicStroke(1f));
                g.drawRoundRect(bx, by, barW, bh2, 4, 4);
                g.setStroke(new BasicStroke(1f));
            }
            if (scores[i] > 0) {
                g.setFont(FontManager.getBody(9));
                g.setColor(new Color(200, 235, 170));
                String sv = scores[i] >= 1000 ? (scores[i] / 1000) + "k" : "" + scores[i];
                FontMetrics fm = g.getFontMetrics();
                g.drawString(sv, bx + (barW - fm.stringWidth(sv)) / 2, by - 2);
            }
            g.setFont(FontManager.getBody(9));
            g.setColor(new Color(130, 170, 120));
            String idx = "" + (i + 1);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(idx, bx + (barW - fm.stringWidth(idx)) / 2, y + h - 2);
        }
    }

    // ── Input handling ───────────────────────────────────────────────────────

    @Override
    public void onMouseMoved(MouseEvent e) {
        hovered = (backBtn != null && backBtn.contains(e.getX(), e.getY())) ? 0 : -1;
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (backBtn != null && backBtn.contains(e.getX(), e.getY()))
            panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }
}