package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.models.GameState;
import OOP_PROJECT.CatchTheBall.src.renderers.FarmBackgroundRenderer;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class GameOverScreen extends Screen {
    private GameState     lastState;
    private StringBuilder nameInput = new StringBuilder();
    private Rectangle[]   buttons;
    private int           hovered   = -1;

    public GameOverScreen(GamePanel panel) { super(panel); }

    @Override
    public void onEnter() {
        super.onEnter();
        GameScreen gs = panel.getScreenManager().getGameScreen();
        if (gs != null && gs.getState() != null) {
            lastState = gs.getState();
            nameInput.setLength(0);
            String pn = gs.getPlayerName();
            nameInput.append(pn != null && !pn.isEmpty() ? pn : "Farmer");

            panel.getPlayerData().getProfile().updateBestScore(lastState.getScore());
            panel.getPlayerData().getProfile().updateBestCombo(lastState.getHighestCombo());
            panel.getPlayerData().getProfile().incrementGamesPlayed();
            panel.getPlayerData().getProfile().addBallsCaught(lastState.getBallsCaughtThisGame());
            panel.getPlayerData().save();

            // Auto-save to leaderboard immediately using the character's name
            panel.getScoreManager().addScore(
                    nameInput.toString().trim(),
                    lastState.getScore(),
                    lastState.getLevel(),
                    lastState.getDifficulty().getDisplayName());
        }
    }

    @Override public void update() { tickCount++; }

    @Override
    public void draw(Graphics2D g) {
        // ── 1. Farm background (same renderer as other screens) ──────────────
        FarmBackgroundRenderer.draw(g, GamePanel.W, GamePanel.H, tickCount);

        // ── 2. Dark red-tinted dramatic overlay ──────────────────────────────
        // Gives a "dusk/end-of-day" feel that's different from the cheerful
        // character creation screen but still consistent with the farm theme.
        g.setPaint(new GradientPaint(
                0, 0,           new Color(60, 10, 5, 170),
                0, GamePanel.H, new Color(10, 20, 5, 200)));
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);
        g.setPaint(null);

        // ── 3. Header bar ────────────────────────────────────────────────────
        RenderUtils.drawHeaderBar(g, GamePanel.W, "Game Over");

        // ── 4. Main panel ────────────────────────────────────────────────────
        int pw = 500, ph = 470;
        int px = (GamePanel.W - pw) / 2;
        int py = 90;   // pushed down to clear the header bar

        RenderUtils.drawGradientPanel(g, px, py, pw, ph,
                new Color(22, 52, 16, 245), new Color(12, 34, 8, 245),
                new Color(100, 210, 70), 2.5f, 20);

        // ── 5. Title row with scythe icon ────────────────────────────────────
        drawScytheIcon(g, px + pw / 2 - 105, py + 28);
        RenderUtils.drawCenteredText(g, "Game Over!",
                px + pw / 2, py + 52,
                FontManager.getBold(34), ColorPalette.TEXT_GOLD);

        // ── 6. New best / best score badge ───────────────────────────────────
        int allTimeBest = panel.getPlayerData().getProfile().getAllTimeBestScore();
        boolean isNewBest = lastState != null
                && lastState.getScore() >= allTimeBest
                && lastState.getScore() > 0;

        if (isNewBest) {
            drawNewBestBadge(g, px + pw / 2, py + 78);
        } else {
            drawCrownIcon(g, px + pw / 2 - 60, py + 66);
            RenderUtils.drawCenteredText(g, "Best: " + allTimeBest,
                    px + pw / 2, py + 78,
                    FontManager.getBodyBold(13), new Color(180, 220, 150));
        }

        // ── 7. Stats ─────────────────────────────────────────────────────────
        if (lastState != null) {
            int sy = py + 102;
            int lx = px + 30;
            int vx = lx + 210;

            drawStatRow(g, lx, sy, StatIcon.SCORE,   "Final Score",   "" + lastState.getScore());       sy += 36;
            drawStatRow(g, lx, sy, StatIcon.LEVEL,   "Level Reached", "" + lastState.getLevel());        sy += 36;
            drawStatRow(g, lx, sy, StatIcon.DIFF,    "Difficulty",    lastState.getDifficulty().getDisplayName()); sy += 36;
            drawStatRow(g, lx, sy, StatIcon.COMBO,   "Best Combo",    "x" + lastState.getHighestCombo()); sy += 36;
            drawStatRow(g, lx, sy, StatIcon.LIVES,   "Lives Left",    "" + lastState.getLives());         sy += 36;
            drawStatRow(g, lx, sy, StatIcon.COINS,   "Coins Earned",  "+" + lastState.getCoinsThisGame()); sy += 36;
            drawStatRow(g, lx, sy, StatIcon.BALLS,   "Balls Caught",  "" + lastState.getBallsCaughtThisGame()); sy += 52;

            // ── Buttons: Menu | Retry | Exit ─────────────────────────────────
            buttons = new Rectangle[3];
            int bw = 140, bh = 46, gap = 12;
            int totalBW = 3 * bw + 2 * gap;
            int bsx = px + (pw - totalBW) / 2;
            buttons[0] = new Rectangle(bsx,            sy, bw, bh);  // Menu
            buttons[1] = new Rectangle(bsx + bw + gap, sy, bw, bh);  // Retry
            buttons[2] = new Rectangle(bsx + 2*(bw+gap), sy, bw, bh); // Exit

            drawActionButton(g, buttons[0], ButtonType.MENU,  hovered == 0);
            drawActionButton(g, buttons[1], ButtonType.RETRY, hovered == 1);
            drawActionButton(g, buttons[2], ButtonType.EXIT,  hovered == 2);
            sy += bh + 16;

            // ── Small "View Leaderboard" text link ───────────────────────────
            g.setFont(FontManager.getBodyBold(12));
            g.setColor(new Color(160, 220, 130, 210));
            String lb = "View Leaderboard >";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(lb, px + (pw - fm.stringWidth(lb)) / 2, sy);
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  STAT ROW ICONS
    // ════════════════════════════════════════════════════════════════════════

    private enum StatIcon { SCORE, LEVEL, DIFF, COMBO, LIVES, COINS, BALLS }
    private enum ButtonType { RETRY, EXIT, MENU }

    private void drawStatRow(Graphics2D g, int x, int y, StatIcon icon, String label, String val) {
        // Icon
        switch (icon) {
            case SCORE:  drawStarIcon(g, x, y - 11, new Color(255, 220, 50)); break;
            case LEVEL:  drawArrowUpIcon(g, x, y - 11); break;
            case DIFF:   drawSwordIcon(g, x, y - 11); break;
            case COMBO:  drawLightningIcon(g, x, y - 11); break;
            case LIVES:  drawHeartIcon(g, x, y - 11, new Color(255, 80, 80)); break;
            case COINS:  drawCoinIcon(g, x, y - 11); break;
            case BALLS:  drawBallIcon(g, x, y - 11); break;
        }
        // Label
        g.setFont(FontManager.getBody(15));
        g.setColor(new Color(155, 198, 135));
        g.drawString(label + ":", x + 20, y);
        // Value
        g.setFont(FontManager.getBodyBold(15));
        g.setColor(Color.WHITE);
        g.drawString(val, x + 220, y);
    }

    /** Draws the three action buttons with custom icons painted inside. */
    private void drawActionButton(Graphics2D g, Rectangle r, ButtonType type, boolean hov) {
        RenderUtils.drawButton(g, r, "", hov, FontManager.getBold(14));

        int ix = r.x + 14;
        int iy = r.y + r.height / 2 - 8;

        String label;
        switch (type) {
            case RETRY:
                drawRetryIcon(g, ix, iy);
                label = "Retry";
                break;
            case EXIT:
                drawExitIcon(g, ix, iy);
                label = "Exit";
                break;
            default: // MENU
                drawHomeIcon(g, ix, iy);
                label = "Menu";
                break;
        }
        RenderUtils.drawCenteredText(g, label,
                r.x + r.width / 2 + 10, r.y + r.height / 2 + 6,
                FontManager.getBold(14), hov ? Color.WHITE : new Color(220, 255, 180));
    }

    // ════════════════════════════════════════════════════════════════════════
    //  DRAWN ICONS  (no emoji — all pure Graphics2D shapes)
    // ════════════════════════════════════════════════════════════════════════

    /** ★ five-point star */
    private void drawStarIcon(Graphics2D g, int x, int y, Color c) {
        g.setColor(c);
        int[] px = new int[10], py = new int[10];
        for (int i = 0; i < 10; i++) {
            double a = Math.toRadians(-90 + i * 36);
            double r = (i % 2 == 0) ? 7 : 3.5;
            px[i] = x + 7 + (int)(Math.cos(a) * r);
            py[i] = y + 7 + (int)(Math.sin(a) * r);
        }
        g.fillPolygon(px, py, 10);
    }

    /** ↑ up-arrow (level) */
    private void drawArrowUpIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(100, 220, 255));
        int[] px = {x+7, x+14, x+10, x+10, x+4, x+4, x};
        int[] py = {y,   y+7,  y+7,  y+14, y+14, y+7, y+7};
        g.fillPolygon(px, py, 7);
    }

    /** ⚔ sword (difficulty) */
    private void drawSwordIcon(Graphics2D g, int x, int y) {
        Stroke s = g.getStroke();
        g.setColor(new Color(200, 200, 220));
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(x + 7, y, x + 7, y + 12);          // blade
        g.setColor(new Color(180, 130, 60));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(x + 3, y + 8, x + 11, y + 8);      // crossguard
        g.setColor(new Color(140, 100, 40));
        g.fillRoundRect(x + 5, y + 11, 4, 5, 2, 2);   // grip
        g.setStroke(s);
    }

    /** ⚡ lightning bolt (combo) */
    private void drawLightningIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(255, 220, 50));
        int[] px = {x+9, x+4, x+8, x+3, x+11, x+7, x+13};
        int[] py = {y,   y+7, y+7, y+14, y+7,  y+7, y};
        g.fillPolygon(px, py, 7);
    }

    /** ♥ heart (lives) */
    private void drawHeartIcon(Graphics2D g, int x, int y, Color c) {
        g.setColor(c);
        // Two overlapping circles + triangle
        g.fillOval(x,     y + 2, 8, 8);
        g.fillOval(x + 5, y + 2, 8, 8);
        int[] px = {x + 1, x + 13, x + 7};
        int[] py = {y + 7,  y + 7,  y + 15};
        g.fillPolygon(px, py, 3);
    }

    /** ○ coin (coins) */
    private void drawCoinIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(255, 210, 50));
        g.fillOval(x + 1, y + 1, 13, 13);
        g.setColor(new Color(200, 160, 20));
        Stroke s = g.getStroke();
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(x + 1, y + 1, 13, 13);
        g.setStroke(s);
        g.setColor(new Color(220, 180, 40));
        g.setFont(new Font("Arial", Font.BOLD, 9));
        g.drawString("$", x + 4, y + 11);
    }

    /** ● ball */
    private void drawBallIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(255, 120, 60));
        g.fillOval(x + 1, y + 1, 13, 13);
        g.setColor(new Color(255, 200, 170, 120));
        g.fillOval(x + 3, y + 3, 5, 5);
    }

    /** ↺ retry circle arrow */
    private void drawRetryIcon(Graphics2D g, int x, int y) {
        Stroke s = g.getStroke();
        g.setColor(new Color(100, 220, 130));
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(x, y, 14, 14, 30, 280);
        // Arrowhead
        g.fillPolygon(new int[]{x+12, x+16, x+12}, new int[]{y+3, y+7, y+10}, 3);
        g.setStroke(s);
    }

    /** 🏆 trophy */
    private void drawTrophyIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(255, 210, 50));
        // Cup body
        int[] px = {x+3, x+1, x+1, x+3, x+11, x+13, x+13, x+11};
        int[] py = {y,   y+2, y+7, y+10, y+10, y+7, y+2, y};
        g.fillPolygon(px, py, 8);
        // Handles
        Stroke s = g.getStroke();
        g.setStroke(new BasicStroke(2f));
        g.setColor(new Color(200, 160, 30));
        g.drawArc(x - 2, y + 2, 6, 6, 90, 180);
        g.drawArc(x + 10, y + 2, 6, 6, 270, 180);
        // Stem
        g.setColor(new Color(255, 210, 50));
        g.fillRect(x + 5, y + 10, 4, 4);
        g.fillRect(x + 3, y + 13, 8, 2);
        g.setStroke(s);
    }

    /** 🏠 home icon */
    private void drawHomeIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(200, 230, 160));
        // Roof
        int[] rx = {x + 7, x, x + 14};
        int[] ry = {y,     y + 7, y + 7};
        g.fillPolygon(rx, ry, 3);
        // Walls
        g.fillRect(x + 2, y + 7, 10, 8);
        // Door
        g.setColor(new Color(140, 90, 40));
        g.fillRect(x + 5, y + 10, 4, 5);
    }

    /** ✦ decorative scythe-like icon for the title */
    private void drawScytheIcon(Graphics2D g, int x, int y) {
        Stroke s = g.getStroke();
        g.setColor(new Color(255, 215, 50, 180));
        g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(x, y + 4, 22, 22, 0, 270);
        g.drawLine(x + 22, y + 15, x + 22, y + 30);
        g.setStroke(s);
    }

    /** ★ new-best badge with a glowing background */
    private void drawNewBestBadge(Graphics2D g, int cx, int y) {
        int bw = 190, bh = 22;
        int bx = cx - bw / 2;
        // Glow
        g.setColor(new Color(255, 200, 0, 40));
        g.fillRoundRect(bx - 4, y - bh, bw + 8, bh + 4, 12, 12);
        g.setColor(new Color(255, 215, 50));
        g.fillRoundRect(bx, y - bh + 2, bw, bh, 8, 8);
        // Stars on either side
        drawStarIcon(g, bx + 4,      y - bh + 3, new Color(255, 255, 100));
        drawStarIcon(g, bx + bw - 18, y - bh + 3, new Color(255, 255, 100));
        // Text
        RenderUtils.drawCenteredText(g, "NEW BEST SCORE!",
                cx, y - 4, FontManager.getBold(13), new Color(80, 40, 0));
    }

    /** Small tag / label icon */
    private void drawTagIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(180, 220, 150));
        g.fillRoundRect(x, y, 14, 10, 4, 4);
        g.setColor(new Color(24, 58, 16));
        g.fillOval(x + 2, y + 2, 3, 3);
        Stroke s = g.getStroke();
        g.setColor(new Color(180, 220, 150));
        g.setStroke(new BasicStroke(1f));
        g.drawLine(x + 6, y + 5, x + 12, y + 5);
        g.setStroke(s);
    }

    /** ✕ exit / door icon */
    private void drawExitIcon(Graphics2D g, int x, int y) {
        // Door frame
        g.setColor(new Color(200, 230, 160));
        g.fillRoundRect(x, y + 2, 10, 13, 2, 2);
        g.setColor(new Color(24, 58, 16));
        g.fillRoundRect(x + 1, y + 3, 8, 11, 2, 2);
        // Arrow pointing right (exit)
        g.setColor(new Color(255, 120, 80));
        g.fillRect(x + 10, y + 7, 6, 3);
        g.fillPolygon(new int[]{x+15, x+19, x+15}, new int[]{y+5, y+8, y+11}, 3);
    }
    private void drawCrownIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(255, 200, 50));
        int[] px = {x, x+3, x+7, x+11, x+14, x+12, x+2};
        int[] py = {y+8, y+2, y+6, y+2, y+8,  y+10,  y+10};
        g.fillPolygon(px, py, 7);
        g.setColor(new Color(255, 150, 50));
        g.fillOval(x+1,  y+5, 3, 3);
        g.fillOval(x+6,  y+3, 3, 3);
        g.fillOval(x+11, y+5, 3, 3);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  INPUT HANDLING
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public void onMouseMoved(MouseEvent e) {
        hovered = -1;
        if (buttons == null) return;
        for (int i = 0; i < buttons.length; i++)
            if (buttons[i].contains(e.getX(), e.getY())) { hovered = i; break; }
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (buttons == null) return;
        int mx = e.getX(), my = e.getY();

        // buttons[0] = Menu
        if (buttons[0].contains(mx, my)) {
            panel.switchToWithFade(GameScreenType.MAIN_MENU);
            return;
        }
        // buttons[1] = Retry
        if (buttons[1].contains(mx, my)) {
            panel.switchToWithFade(GameScreenType.GAME);
            return;
        }
        // buttons[2] = Exit
        if (buttons[2].contains(mx, my)) {
            System.exit(0);
            return;
        }
        // "View Leaderboard" text link – approximate hit area below buttons
        int pw = 500, px = (GamePanel.W - pw) / 2;
        int linkY = 90 + 470 - 20; // near bottom of panel
        if (my >= linkY - 16 && my <= linkY + 4
                && mx >= px + 50 && mx <= px + pw - 50) {
            panel.switchToWithFade(GameScreenType.LEADERBOARD);
        }
    }
}