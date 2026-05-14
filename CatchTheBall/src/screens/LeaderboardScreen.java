package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.models.ScoreEntry;
import OOP_PROJECT.CatchTheBall.src.renderers.FarmBackgroundRenderer;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.awt.geom.*;

public class LeaderboardScreen extends Screen {
    private Rectangle backBtn;
    private Rectangle refreshBtn;
    private Rectangle[] filterBtns;
    private int    hovered        = -1;
    private int    selectedFilter = 0;
    private int    scrollOffset   = 0;
    private String statusMsg      = "";
    private int    statusTimer    = 0;

    // Layout constants
    private static final int FILTER_Y  = 90;   // top of filter pill row — gap below header
    private static final int FILTER_H  = 30;
    private static final int ROW_START = 140;  // first row Y — well below filters + gap
    private static final int ROW_H     = 48;
    private static final int ROW_GAP   = 6;    // gap between rows

    public LeaderboardScreen(GamePanel panel) { super(panel); }

    @Override public void onEnter() { super.onEnter(); scrollOffset = 0; }
    @Override public void update()  { tickCount++; if (statusTimer > 0) statusTimer--; }

    @Override
    public void draw(Graphics2D g) {
        FarmBackgroundRenderer.draw(g, GamePanel.W, GamePanel.H, tickCount);

        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);

        RenderUtils.drawHeaderBar(g, GamePanel.W, "Leaderboard");

        // ── Filter buttons ──────────────────────────────────────────────────
        String[] filterLabels = {"All", "Easy", "Normal", "Hard"};
        filterBtns = new Rectangle[filterLabels.length];
        int fx = 24;
        for (int i = 0; i < filterLabels.length; i++) {
            filterBtns[i] = new Rectangle(fx, FILTER_Y, 90, FILTER_H);
            RenderUtils.drawButton(g, filterBtns[i], filterLabels[i],
                    selectedFilter == i, FontManager.getBodyBold(13));
            fx += 100;
        }

        // ── Score rows ──────────────────────────────────────────────────────
        List<ScoreEntry> top = selectedFilter == 0
                ? panel.getScoreManager().getAll()
                : panel.getScoreManager().getTopByDifficulty(filterLabels[selectedFilter]);

        if (top.isEmpty()) {
            RenderUtils.drawCenteredText(g,
                    "No scores yet for " + filterLabels[selectedFilter] + ".",
                    GamePanel.W / 2, 300,
                    FontManager.getBody(17, Font.ITALIC),
                    new Color(155, 200, 135));
        }

        // Clip so rows never overlap the filter row or the bottom bar
        int clipTop = FILTER_Y + FILTER_H + 4;
        Shape oldClip = g.getClip();
        g.setClip(0, clipTop, GamePanel.W, GamePanel.H - clipTop - 55);

        for (int i = 0; i < top.size(); i++) {
            ScoreEntry e = top.get(i);
            int ry = ROW_START + i * (ROW_H + ROW_GAP) - scrollOffset;
            if (ry + ROW_H < clipTop || ry > GamePanel.H - 55) continue;

            boolean top3 = (i < 3);
            Color rowTop = top3 ? new Color(62,  125,  42, 210) : new Color(32,  72,  22, 165);
            Color rowBot = top3 ? new Color(45,  100,  30, 210) : new Color(22,  52,  14, 165);
            Color rowBdr = top3 ? new Color(148, 235, 100)      : new Color(78,  148,  58);
            RenderUtils.drawGradientPanel(g, 30, ry, GamePanel.W - 60, ROW_H - 2,
                    rowTop, rowBot, rowBdr, 1f, 10);

            // Rank badge (drawn, no emoji dependency)
            drawRankBadge(g, i, 52, ry + ROW_H / 2);

            // Name
            g.setFont(FontManager.getBold(14));
            g.setColor(top3 ? new Color(255, 232, 100) : Color.WHITE);
            g.drawString(e.getName(), 82, ry + ROW_H / 2 + 5);

            // Score
            g.setFont(FontManager.getBodyBold(14));
            g.setColor(new Color(100, 225, 100));
            g.drawString(String.valueOf(e.getScore()), 370, ry + ROW_H / 2 + 5);

            // Level
            g.setFont(FontManager.getBody(12));
            g.setColor(new Color(155, 200, 135));
            g.drawString("Lv." + e.getLevel(), 510, ry + ROW_H / 2 + 5);

            // Difficulty
            g.setColor(new Color(120, 170, 120));
            g.drawString(e.getDifficulty(), 570, ry + ROW_H / 2 + 5);

            // Date
            if (!e.getDate().isEmpty()) {
                g.setColor(new Color(110, 155, 105));
                g.drawString(e.getDate(), 680, ry + ROW_H / 2 + 5);
            }
        }

        g.setClip(oldClip);

        // ── Bottom bar ──────────────────────────────────────────────────────
        backBtn    = new Rectangle(20, GamePanel.H - 52, 140, 36);
        refreshBtn = new Rectangle(GamePanel.W - 170, GamePanel.H - 52, 145, 36);

        // Back button — plain text, no emoji
        RenderUtils.drawButton(g, backBtn, "< Back", hovered == 0, FontManager.getBold(14));

        // Refresh button — plain text, no emoji
        RenderUtils.drawButton(g, refreshBtn, "Refresh", hovered == 1, FontManager.getBold(13));

        if (statusTimer > 0)
            RenderUtils.drawCenteredText(g, statusMsg, GamePanel.W / 2, GamePanel.H - 60,
                    FontManager.getBodyBold(12), new Color(150, 220, 150));

        g.setFont(FontManager.getBody(11));
        g.setColor(new Color(130, 170, 120));
        g.drawString("Showing " + top.size() + " entries", GamePanel.W / 2 - 50, GamePanel.H - 16);
    }

    /**
     * Draws a rank badge entirely with Java2D — no emoji, no Unicode symbols.
     * Rank 1 → gold crown, Rank 2 → silver medal, Rank 3 → bronze medal,
     * Rank 4+ → plain numbered circle.
     *
     * @param g   graphics context
     * @param rank 0-based rank index
     * @param cx  horizontal center of the badge
     * @param cy  vertical center of the badge
     */
    private void drawRankBadge(Graphics2D g, int rank, int cx, int cy) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int r = 14; // badge circle radius

        if (rank == 0) {
            // Gold crown
            drawCrown(g2, cx, cy, r, new Color(255, 200, 20), new Color(255, 230, 120));
        } else if (rank == 1) {
            // Silver medal circle
            drawMedalCircle(g2, cx, cy, r,
                    new Color(180, 190, 200), new Color(220, 225, 235), new Color(140, 150, 160));
            drawMedalNumber(g2, cx, cy, "2", new Color(80, 90, 100));
        } else if (rank == 2) {
            // Bronze medal circle
            drawMedalCircle(g2, cx, cy, r,
                    new Color(180, 110, 50), new Color(220, 160, 90), new Color(140, 80, 30));
            drawMedalNumber(g2, cx, cy, "3", new Color(90, 50, 20));
        } else {
            // Numbered dark circle
            g2.setColor(new Color(40, 80, 25, 200));
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);
            g2.setColor(new Color(90, 160, 60));
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawOval(cx - r, cy - r, r * 2, r * 2);
            g2.setStroke(new BasicStroke(1f));
            drawMedalNumber(g2, cx, cy, String.valueOf(rank + 1), new Color(180, 230, 140));
        }

        g2.dispose();
    }

    /** Draws a filled circle with gradient-like layered fill for medal ranks. */
    private void drawMedalCircle(Graphics2D g, int cx, int cy, int r,
                                  Color base, Color highlight, Color shadow) {
        // Shadow offset circle
        g.setColor(shadow);
        g.fillOval(cx - r + 1, cy - r + 1, r * 2, r * 2);
        // Main fill
        g.setColor(base);
        g.fillOval(cx - r, cy - r, r * 2, r * 2);
        // Top highlight arc
        g.setColor(highlight);
        g.fillArc(cx - r + 2, cy - r + 2, r * 2 - 4, r - 2, 20, 140);
        // Border
        g.setColor(shadow);
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(cx - r, cy - r, r * 2, r * 2);
        g.setStroke(new BasicStroke(1f));
    }

    /** Draws a rank number string centered at (cx, cy). */
    private void drawMedalNumber(Graphics2D g, int cx, int cy, String num, Color col) {
        g.setFont(FontManager.getBold(11));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(col);
        g.drawString(num, cx - fm.stringWidth(num) / 2, cy + fm.getAscent() / 2 - 1);
    }

    /**
     * Draws a simple crown shape using polygons — no emoji, fully portable.
     * Used for rank 1 (gold).
     */
    private void drawCrown(Graphics2D g, int cx, int cy, int r,
                            Color fill, Color highlight) {
        // Crown body: wide base with three upward points
        int bY  = cy + r / 2;       // base bottom y
        int tY  = cy - r;           // tallest point y
        int mY  = cy - r / 3;       // side point y
        int bL  = cx - r;           // base left x
        int bR  = cx + r;           // base right x

        int[] xs = { bL, bL,  cx - r/2, cx, cx + r/2, bR, bR };
        int[] ys = { bY, mY,  tY,        mY, tY,        mY, bY };

        g.setColor(fill);
        g.fillPolygon(xs, ys, 7);
        g.setColor(highlight);
        // Inner highlight stripe across top half
        int[] hxs = { bL + 2, cx - r/2, cx, cx + r/2, bR - 2 };
        int[] hys = { mY,      tY + 4,   mY - 4, tY + 4, mY };
        g.fillPolygon(hxs, hys, 5);
        // Border
        g.setColor(new Color(180, 140, 0));
        g.setStroke(new BasicStroke(1.5f));
        g.drawPolygon(xs, ys, 7);
        g.setStroke(new BasicStroke(1f));
        // Three jewel dots on the points
        int[][] jewels = {{cx, tY + 3}, {bL + 1, mY + 2}, {bR - 1, mY + 2}};
        for (int[] j : jewels) {
            g.setColor(new Color(255, 80, 80));
            g.fillOval(j[0] - 2, j[1] - 2, 5, 5);
        }
    }

    // ── Input handling ──────────────────────────────────────────────────────

    @Override
    public void onMouseMoved(MouseEvent e) {
        hovered = -1;
        if (backBtn    != null && backBtn.contains(e.getX(), e.getY()))    hovered = 0;
        if (refreshBtn != null && refreshBtn.contains(e.getX(), e.getY())) hovered = 1;
        if (filterBtns != null) {
            for (int i = 0; i < filterBtns.length; i++)
                if (filterBtns[i].contains(e.getX(), e.getY())) { hovered = 2 + i; break; }
        }
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (backBtn != null && backBtn.contains(e.getX(), e.getY()))
            panel.switchToWithFade(GameScreenType.MAIN_MENU);
        if (refreshBtn != null && refreshBtn.contains(e.getX(), e.getY())) {
            panel.getScoreManager().load();
            statusMsg = "Refreshed!"; statusTimer = 100;
        }
        if (filterBtns != null) {
            for (int i = 0; i < filterBtns.length; i++) {
                if (filterBtns[i].contains(e.getX(), e.getY())) {
                    selectedFilter = i;
                    scrollOffset = 0;
                    return;
                }
            }
        }
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) panel.switchToWithFade(GameScreenType.MAIN_MENU);
        if (e.getKeyCode() == KeyEvent.VK_DOWN)   scrollOffset = Math.min(scrollOffset + 30, 400);
        if (e.getKeyCode() == KeyEvent.VK_UP)      scrollOffset = Math.max(scrollOffset - 30, 0);
    }
}