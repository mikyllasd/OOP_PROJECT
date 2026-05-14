package OOP_PROJECT.CatchTheBall.src.renderers;

import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.managers.PlayerData;
import OOP_PROJECT.CatchTheBall.src.audio.SoundManager;
import OOP_PROJECT.CatchTheBall.src.models.FarmProgression;
import OOP_PROJECT.CatchTheBall.src.models.GameState;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.geom.*;

public class SidebarRenderer {

    // ── colour palette ────────────────────────────────────────────────────────
    private static final Color BG_TOP       = new Color(34,  62,  24);
    private static final Color BG_BOT       = new Color(22,  44,  14);
    private static final Color CARD_BG      = new Color(255, 255, 255, 18);
    private static final Color CARD_BORDER  = new Color(255, 255, 255, 38);
    private static final Color DIVIDER      = new Color(120, 185, 80, 55);
    private static final Color LABEL_COL    = new Color(168, 210, 130);
    private static final Color SIDEBAR_EDGE = new Color(138, 205, 78);

    // ── stat accent colours ───────────────────────────────────────────────────
    private static final Color C_DIFF  = new Color(255, 218,  68);
    private static final Color C_LEVEL = new Color(255, 238,  90);
    private static final Color C_SCORE = new Color( 85, 235, 255);
    private static final Color C_TARGET= new Color(195, 195, 195);
    private static final Color C_LIVES = new Color(255, 105, 105);
    private static final Color C_TIME  = new Color(100, 255, 155);
    private static final Color C_COINS = new Color(255, 215,  50);
    private static final Color C_COMBO = new Color(255, 100, 255);

    // ── layout constants ──────────────────────────────────────────────────────
    /**
     * Height of the pinned header block (banner + stat-trio).
     * Everything above this line is fixed; nothing below it can shift it.
     */
    private static final int HEADER_H = 130;   // banner(58) + trio(62) + gap(10)

    /**
     * Height reserved at the bottom for the control buttons.
     * The buttons always sit at  H - FOOTER_H.
     */
    private static final int FOOTER_H = 44;

    // ── public API ────────────────────────────────────────────────────────────

    public static void draw(Graphics2D g, GameState state, FarmProgression farm,
                            PlayerData playerData, SoundManager sound, int tickCount) {

        int sx = GamePanel.ARENA_W;
        int sw = GamePanel.SIDEBAR_W;
        int h  = GamePanel.H;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

        // ── background ────────────────────────────────────────────────────────
        g2.setPaint(new GradientPaint(sx, 0, BG_TOP, sx, h, BG_BOT));
        g2.fillRect(sx, 0, sw, h);
        g2.setPaint(null);

        drawWoodTexture(g2, sx, sw, h);

        // left accent border
        g2.setPaint(new GradientPaint(sx, 0, SIDEBAR_EDGE, sx, h, new Color(60, 140, 30)));
        g2.fillRect(sx, 0, 3, h);
        g2.setPaint(null);

        // ── PINNED HEADER ─────────────────────────────────────────────────────
        drawHeaderBanner(g2, sx, sw, tickCount);           // y = 4..54  (h=50+gap)

        // Compact 3-column row: LIVES | TIME | COINS  (y = 58..118)
        drawStatTrioHeader(g2, sx, sw, state, playerData, tickCount);

        // thin separator under the header block
        drawDivider(g2, sx, HEADER_H, sw);

        // ── SCROLLABLE MIDDLE (fixed-height window, clipped) ──────────────────
        int midY  = HEADER_H + 8;
        int midH  = h - HEADER_H - FOOTER_H - 12;

        // We don't actually scroll — just lay out cards in the available space.
        // Cards that don't fit are hidden naturally (rare; sidebar is tall enough).
        Graphics2D gm = (Graphics2D) g2.create();
        gm.setClip(sx, midY, sw, midH);

        int py = midY;
        py = drawStatCard(gm, sx, py, sw, "\uD83C\uDF1F", "DIFFICULTY",
                state.getDifficulty().getDisplayName(), C_DIFF);
        py = drawStatCard(gm, sx, py, sw, "\uD83D\uDCC8", "LEVEL",
                "" + state.getLevel(), C_LEVEL);
        py = drawStatCard(gm, sx, py, sw, "\u2B50", "SCORE",
                formatNumber(state.getScore()), C_SCORE);
        py = drawTargetCard(gm, sx, py, sw, state);

        // Combo card — present only when active; does NOT affect button position
        if (state.getCombo() > 0) {
            py = drawComboCard(gm, sx, py, sw, state, tickCount);
        }

        drawDivider(gm, sx, py, sw); py += 12;

        py = drawProgressSection(gm, sx, py, sw, state); py += 5;
        drawActivePowerUps(gm, sx, sw, py, state);       py += 40;
        drawFarmBadge(gm, sx, sw, py, farm);

        gm.dispose();

        // ── PINNED FOOTER (buttons always at bottom) ──────────────────────────
        int btnY = h - FOOTER_H + 2;
        drawDivider(g2, sx, btnY - 4, sw);
        drawControlButtons(g2, sx, sw, btnY, sound);

        g2.dispose();
    }

    // ── PINNED HEADER: banner ──────────────────────────────────────────────────

    private static void drawHeaderBanner(Graphics2D g, int sx, int sw, int tick) {
        int bx = sx + 6;
        int bw = sw - 12;

        GradientPaint bg = new GradientPaint(bx, 4, new Color(60, 115, 30), bx, 54, new Color(38, 78, 18));
        g.setPaint(bg);
        g.fillRoundRect(bx, 4, bw, 50, 12, 12);
        g.setPaint(null);

        g.setColor(new Color(255, 255, 255, 22));
        g.fillRoundRect(bx + 2, 5, bw - 4, 22, 10, 10);

        g.setColor(new Color(138, 218, 68, 155));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(bx, 4, bw, 50, 12, 12);
        g.setStroke(new BasicStroke(1f));

        int leafOff = (int)(Math.sin(tick * 0.04) * 2);
        g.setFont(FontManager.getEmoji(16));
        g.setColor(Color.WHITE);
        g.drawString("\uD83C\uDF3E", bx + 6, 34 + leafOff);  // 🌾

        g.setFont(FontManager.getBold(13));
        g.setColor(new Color(255, 238, 90));
        g.drawString("FARM STATS", bx + 28, 35);

        g.setFont(FontManager.getBody(9));
        g.setColor(new Color(168, 218, 118));
        g.drawString("catch the harvest", bx + 28, 47);
    }

    // ── PINNED HEADER: compact Lives | Time | Coins trio ─────────────────────

    /**
     * Three mini-cards side by side — always at a fixed Y position.
     * Combined height ≈ 62 px. Starts at y=58, ends at y=120.
     */
    private static void drawStatTrioHeader(Graphics2D g, int sx, int sw,
                                           GameState state, PlayerData playerData,
                                           int tick) {
        int topY = 58;
        int bx   = sx + 6;
        int bw   = sw - 12;

        // ── outer container ──
        g.setColor(new Color(0, 0, 0, 30));
        g.fillRoundRect(bx, topY, bw, 62, 10, 10);
        g.setColor(new Color(255, 255, 255, 12));
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(bx, topY, bw, 62, 10, 10);
        g.setStroke(new BasicStroke(1f));

        int cw = (bw - 4) / 3;  // column width

        // LIVES
        drawTrioCell(g, bx,            topY, cw, 62, state, playerData, tick, 0);
        // separator
        g.setColor(new Color(255, 255, 255, 20));
        g.drawLine(bx + cw, topY + 8, bx + cw, topY + 54);
        // TIME
        drawTrioCell(g, bx + cw,       topY, cw, 62, state, playerData, tick, 1);
        // separator
        g.drawLine(bx + cw * 2, topY + 8, bx + cw * 2, topY + 54);
        // COINS
        drawTrioCell(g, bx + cw * 2,   topY, cw, 62, state, playerData, tick, 2);
    }

    /**
     * @param col 0=lives, 1=time, 2=coins
     */
    private static void drawTrioCell(Graphics2D g, int x, int y, int w, int h,
                                     GameState state, PlayerData playerData,
                                     int tick, int col) {
        int cx = x + w / 2;

        switch (col) {
            // ── LIVES ────────────────────────────────────────────────────────
            case 0: {
                g.setFont(FontManager.getBold(8));
                g.setColor(LABEL_COL);
                FontMetrics fm = g.getFontMetrics();
                g.drawString("LIVES", cx - fm.stringWidth("LIVES") / 2, y + 14);

                int lives = state.getLives();
                int maxLives = 5;
                // draw two rows: top row 3, bottom row 2
                int hs = 11;           // heart size
                int gap = 2;
                // row1: up to 3 hearts, row2: up to 2 hearts
                int[] rowCount = { Math.min(lives, 3), Math.max(0, Math.min(lives - 3, 2)) };
                int[] rowMax   = { 3, 2 };
                int[] rowY     = { y + 24, y + 40 };
                int heartIdx = 0;
                for (int r = 0; r < 2; r++) {
                    int rowW = rowMax[r] * (hs + gap) - gap;
                    int startX = cx - rowW / 2;
                    for (int i = 0; i < rowMax[r]; i++) {
                        boolean filled = heartIdx < lives;
                        int pulse = (lives <= 1 && heartIdx == 0)
                                ? (int)(Math.sin(tick * 0.15) * 2) : 0;
                        drawHeart(g, startX + i * (hs + gap), rowY[r] - pulse, hs, filled, tick, lives);
                        heartIdx++;
                    }
                }
                break;
            }

            // ── TIME ─────────────────────────────────────────────────────────
            case 1: {
                boolean urgent = state.getTimeLeft() <= 60;
                boolean pulse  = urgent && tick % 30 < 15;
                Color accent   = urgent ? new Color(255, 70, 70) : C_TIME;

                if (pulse) {
                    g.setColor(new Color(255, 50, 50, 28));
                    g.fillRoundRect(x + 2, y + 2, w - 4, h - 4, 8, 8);
                }

                g.setFont(FontManager.getBold(8));
                g.setColor(pulse ? new Color(255, 120, 120) : LABEL_COL);
                FontMetrics fm = g.getFontMetrics();
                String lbl = "TIME";
                g.drawString(lbl, cx - fm.stringWidth(lbl) / 2, y + 14);

                // clock icon
                g.setFont(FontManager.getEmoji(13));
                g.setColor(Color.WHITE);
                String icon = urgent ? "\u23F0" : "\u231A";
                FontMetrics ifm = g.getFontMetrics();
                g.drawString(icon, cx - ifm.stringWidth(icon) / 2, y + 31);

                // time value
                String mins = "" + state.getTimeLeft() / 60;
                String secs = String.format("%02d", state.getTimeLeft() % 60);
                g.setFont(FontManager.getBodyBold(14));
                g.setColor(accent);
                FontMetrics tfm = g.getFontMetrics();
                String timeStr = mins + ":" + secs;
                g.drawString(timeStr, cx - tfm.stringWidth(timeStr) / 2, y + 50);

                // tiny bar
                int barW = w - 10;
                int barX = x + 5;
                float tPct = Math.max(0f, Math.min(1f, state.getTimeLeft() / 90f));
                g.setColor(new Color(0, 0, 0, 70));
                g.fillRoundRect(barX, y + h - 8, barW, 4, 2, 2);
                g.setPaint(new GradientPaint(barX, 0,
                        urgent ? new Color(255, 80, 80) : new Color(80, 220, 80),
                        barX + barW, 0,
                        urgent ? new Color(255, 180, 50) : new Color(180, 255, 80)));
                g.fillRoundRect(barX, y + h - 8, Math.max(3, (int)(barW * tPct)), 4, 2, 2);
                g.setPaint(null);
                break;
            }

            // ── COINS ────────────────────────────────────────────────────────
            case 2: {
                float shimmer = (float)(Math.sin(tick * 0.05) * 0.5 + 0.5);

                g.setFont(FontManager.getBold(8));
                g.setColor(LABEL_COL);
                FontMetrics fm = g.getFontMetrics();
                String lbl = "COINS";
                g.drawString(lbl, cx - fm.stringWidth(lbl) / 2, y + 14);

                // coin icon centred
                int coinSize = 14;
                drawCoinIcon(g, cx - coinSize / 2, y + 20, coinSize, tick);

                // value
                g.setFont(FontManager.getBodyBold(12));
                g.setColor(C_COINS);
                FontMetrics cfm = g.getFontMetrics();
                String coinsStr = formatNumber(playerData.getTotalCoins());
                g.drawString(coinsStr, cx - cfm.stringWidth(coinsStr) / 2, y + 52);
                break;
            }
        }
    }

    // ── stat card (generic) ───────────────────────────────────────────────────

    private static int drawStatCard(Graphics2D g, int sx, int py, int sw,
                                    String icon, String label, String value, Color accent) {
        int bx = sx + 8;
        int bw = sw - 16;
        int bh = 48;

        g.setColor(CARD_BG);
        g.fillRoundRect(bx, py, bw, bh, 10, 10);

        g.setPaint(new GradientPaint(bx, py, accent, bx, py + bh, accent.darker()));
        g.fillRoundRect(bx, py, 4, bh, 4, 4);
        g.setPaint(null);

        g.setColor(CARD_BORDER);
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(bx, py, bw, bh, 10, 10);

        g.setFont(FontManager.getEmoji(15));
        g.setColor(Color.WHITE);
        g.drawString(icon, bx + 8, py + 18);

        g.setFont(FontManager.getBold(9));
        g.setColor(LABEL_COL);
        g.drawString(label, bx + 27, py + 16);

        g.setFont(FontManager.getBodyBold(18));
        g.setColor(accent);
        g.drawString(value, bx + 27, py + 36);

        return py + bh + 5;
    }

    // ── target card with mini progress bar ───────────────────────────────────

    private static int drawTargetCard(Graphics2D g, int sx, int py, int sw, GameState state) {
        int bx = sx + 8;
        int bw = sw - 16;
        int bh = 52;

        g.setColor(CARD_BG);
        g.fillRoundRect(bx, py, bw, bh, 10, 10);

        g.setPaint(new GradientPaint(bx, py, C_TARGET, bx, py + bh, C_TARGET.darker()));
        g.fillRoundRect(bx, py, 4, bh, 4, 4);
        g.setPaint(null);

        g.setColor(CARD_BORDER);
        g.drawRoundRect(bx, py, bw, bh, 10, 10);

        g.setFont(FontManager.getEmoji(15));
        g.setColor(Color.WHITE);
        g.drawString("\uD83C\uDFAF", bx + 8, py + 18);   // 🎯

        g.setFont(FontManager.getBold(9));
        g.setColor(LABEL_COL);
        g.drawString("TARGET", bx + 27, py + 16);

        g.setFont(FontManager.getBodyBold(16));
        g.setColor(C_TARGET);
        g.drawString(formatNumber(state.getScore()) + " / " + formatNumber(state.getLevelTarget()),
                bx + 27, py + 32);

        int barX = bx + 8;
        int barW = bw - 16;
        float pct = Math.min(1f, (float) state.getScore() / state.getLevelTarget());
        g.setColor(new Color(0, 0, 0, 60));
        g.fillRoundRect(barX, py + 38, barW, 7, 4, 4);
        g.setPaint(new GradientPaint(barX, 0, new Color(180, 180, 180), barX + barW, 0, Color.WHITE));
        g.fillRoundRect(barX, py + 38, (int)(barW * pct), 7, 4, 4);
        g.setPaint(null);

        return py + bh + 5;
    }

    // ── lives heart helper ────────────────────────────────────────────────────

    private static void drawHeart(Graphics2D g, int x, int y, int size,
                                  boolean filled, int tick, int lives) {
        Color prev = g.getColor();
        int s  = size;
        int cx = x + s / 2;
        int cy = y + s / 2;
        GeneralPath heart = new GeneralPath();
        float hs = s * 0.45f;
        heart.moveTo(cx, cy + hs * 0.9f);
        heart.curveTo(cx - hs * 1.6f, cy + hs * 0.1f,
                      cx - hs * 1.6f, cy - hs * 0.9f,
                      cx,             cy - hs * 0.2f);
        heart.curveTo(cx + hs * 1.6f, cy - hs * 0.9f,
                      cx + hs * 1.6f, cy + hs * 0.1f,
                      cx,             cy + hs * 0.9f);
        heart.closePath();

        if (filled) {
            g.setColor(new Color(255, 100, 100, 55));
            Graphics2D gx = (Graphics2D) g.create();
            gx.setStroke(new BasicStroke(4f));
            gx.draw(heart);
            gx.dispose();
            g.setColor(C_LIVES);
            g.fill(heart);
            g.setColor(new Color(255, 200, 200, 100));
            GeneralPath spec = new GeneralPath();
            spec.moveTo(cx - hs * 0.4f, cy - hs * 0.3f);
            spec.curveTo(cx - hs * 0.8f, cy - hs * 0.6f,
                         cx - hs * 0.1f, cy - hs * 0.7f,
                         cx - hs * 0.1f, cy - hs * 0.2f);
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(spec);
            g.setStroke(new BasicStroke(1f));
        } else {
            g.setColor(new Color(90, 50, 50, 90));
            g.fill(heart);
            g.setColor(new Color(120, 70, 70, 120));
            g.setStroke(new BasicStroke(1f));
            g.draw(heart);
        }
        g.setColor(prev);
    }

    // ── combo card ────────────────────────────────────────────────────────────

    private static int drawComboCard(Graphics2D g, int sx, int py, int sw,
                                     GameState state, int tick) {
        int c = state.getCombo();
        Color accent = c >= 8 ? new Color(255, 80, 255)
                     : c >= 5 ? new Color(255, 175, 40)
                     :          new Color(80, 255, 200);

        int bx = sx + 8;
        int bw = sw - 16;
        int bh = 50;

        float hue = (tick % 120) / 120f;
        Color shimCol = Color.getHSBColor(hue, 0.7f, 1f);
        g.setColor(new Color(shimCol.getRed(), shimCol.getGreen(), shimCol.getBlue(), 28));
        g.fillRoundRect(bx - 2, py - 2, bw + 4, bh + 4, 12, 12);

        g.setColor(CARD_BG);
        g.fillRoundRect(bx, py, bw, bh, 10, 10);

        g.setPaint(new GradientPaint(bx, py, accent, bx, py + bh, accent.darker()));
        g.fillRoundRect(bx, py, 4, bh, 4, 4);
        g.setPaint(null);

        g.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 140));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(bx, py, bw, bh, 10, 10);
        g.setStroke(new BasicStroke(1f));

        g.setFont(FontManager.getEmoji(16));
        g.setColor(Color.WHITE);
        g.drawString("\uD83D\uDD25", bx + 7, py + 22);  // 🔥

        g.setFont(FontManager.getBold(9));
        g.setColor(LABEL_COL);
        g.drawString("COMBO  x" + c, bx + 26, py + 16);

        g.setFont(FontManager.getBodyBold(22));
        g.setColor(accent);
        g.drawString("x" + String.format("%.1f", state.getComboMultiplier()), bx + 26, py + 40);

        drawSparkles(g, bx + bw - 22, py + 8, tick, accent);

        return py + bh + 5;
    }

    private static void drawSparkles(Graphics2D g, int cx, int cy, int tick, Color c) {
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 180));
        for (int i = 0; i < 4; i++) {
            double a = Math.toRadians(i * 90 + tick * 3);
            int d = 8;
            int sx2 = (int)(cx + Math.cos(a) * d);
            int sy2 = (int)(cy + Math.sin(a) * d);
            g.fillOval(sx2 - 2, sy2 - 2, 4, 4);
        }
    }

    // ── progress section ──────────────────────────────────────────────────────

    /** Returns py advanced by the section height (38 px). */
    private static int drawProgressSection(Graphics2D g, int sx, int py, int sw, GameState state) {
        int bx = sx + 8;
        int bw = sw - 16;

        g.setFont(FontManager.getBold(9));
        g.setColor(LABEL_COL);
        g.drawString("\uD83D\uDCCA  LEVEL PROGRESS", bx, py + 11);

        float prog = Math.min(1f, (float) state.getScore() / state.getLevelTarget());

        g.setColor(new Color(0, 0, 0, 80));
        g.fillRoundRect(bx, py + 15, bw, 12, 6, 6);

        g.setPaint(new GradientPaint(bx, 0, new Color(70, 200, 70), bx + bw, 0, new Color(155, 255, 80)));
        g.fillRoundRect(bx, py + 15, Math.max(6, (int)(bw * prog)), 12, 6, 6);
        g.setPaint(null);

        g.setColor(new Color(80, 180, 50, 90));
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(bx, py + 15, bw, 12, 6, 6);
        g.setStroke(new BasicStroke(1f));

        g.setFont(FontManager.getBold(8));
        g.setColor(Color.WHITE);
        String pctStr = (int)(prog * 100) + "%";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(pctStr, bx + (bw - fm.stringWidth(pctStr)) / 2, py + 25);

        return py + 38;
    }

    // ── power-ups ─────────────────────────────────────────────────────────────

    private static void drawActivePowerUps(Graphics2D g, int sx, int sw, int py, GameState state) {
        g.setFont(FontManager.getBold(9));
        g.setColor(LABEL_COL);
        g.drawString("\u26A1  POWER-UPS", sx + 8, py + 11);

        int px = sx + 8;
        py += 14;
        if (state.isMagnetActive())       { drawPowerTag(g, px, py, "\uD83E\uDDF2", state.getMagnetTimer(),     new Color(255, 100, 100)); px += 60; }
        if (state.isDoublePointsActive()) { drawPowerTag(g, px, py, "2\u00D7",      state.getDoubleTimer(),     new Color(255, 225, 50));  px += 60; }
        if (state.isSlowTimeActive())     { drawPowerTag(g, px, py, "\uD83D\uDD5B", state.getSlowTimer(),       new Color(175, 100, 255)); px += 60; }
        if (state.isWideBasketActive())   { drawPowerTag(g, px, py, "\u2194",       state.getWideTimer(),       new Color(80,  230, 200)); }
        if (!state.isMagnetActive() && !state.isDoublePointsActive()
                && !state.isSlowTimeActive() && !state.isWideBasketActive()) {
            g.setFont(FontManager.getBody(9));
            g.setColor(new Color(120, 160, 100));
            g.drawString("None active", px, py + 14);
        }
    }

    private static void drawPowerTag(Graphics2D g, int x, int y, String icon, int timer, Color c) {
        int tw = 54, th = 28;
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 55));
        g.fillRoundRect(x, y, tw, th, 8, 8);
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 140));
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(x, y, tw, th, 8, 8);
        g.setStroke(new BasicStroke(1f));

        g.setFont(FontManager.getEmoji(13));
        g.setColor(Color.WHITE);
        g.drawString(icon, x + 4, y + 17);

        float ratio = Math.min(1f, timer / 300f);
        g.setPaint(new GradientPaint(x, 0, c, x + tw, 0, c.brighter()));
        g.fillRoundRect(x + 2, y + th - 5, (int)((tw - 4) * ratio), 4, 2, 2);
        g.setPaint(null);
    }

    // ── farm badge ────────────────────────────────────────────────────────────

    private static void drawFarmBadge(Graphics2D g, int sx, int sw, int py, FarmProgression farm) {
        int bx = sx + 8;
        int bw = sw - 16;
        int bh = 64;

        g.setPaint(new GradientPaint(bx, py, new Color(48, 95, 28), bx, py + bh, new Color(28, 60, 14)));
        g.fillRoundRect(bx, py, bw, bh, 12, 12);
        g.setPaint(null);
        g.setColor(new Color(100, 185, 55, 120));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(bx, py, bw, bh, 12, 12);
        g.setStroke(new BasicStroke(1f));

        g.setColor(new Color(255, 255, 255, 14));
        g.fillRoundRect(bx + 2, py + 2, bw - 4, bh / 2, 10, 10);

        g.setFont(FontManager.getEmoji(30));
        FontMetrics fm = g.getFontMetrics();
        String emoji = farm.getEmoji();
        g.setColor(Color.WHITE);
        g.drawString(emoji, bx + (bw - fm.stringWidth(emoji)) / 2, py + 38);

        g.setFont(FontManager.getBold(9));
        fm = g.getFontMetrics();
        String name = farm.getName();
        g.setColor(new Color(185, 230, 150));
        g.drawString(name, bx + (bw - fm.stringWidth(name)) / 2, py + 56);
    }

    // ── control buttons (PINNED FOOTER) ──────────────────────────────────────

    private static void drawControlButtons(Graphics2D g, int sx, int sw, int py, SoundManager sound) {
        int bw = (sw - 24) / 2;
        drawIconButton(g, sx + 8,       py, bw, 32,
                sound.isMuted() ? "\uD83D\uDD07" : "\uD83D\uDD0A",
                sound.isMuted() ? "MUTED" : "SOUND",
                sound.isMuted() ? new Color(120, 60, 60) : new Color(50, 105, 35));
        drawIconButton(g, sx + 12 + bw, py, bw, 32,
                "\u23F8", "PAUSE",
                new Color(50, 85, 105));
    }

    private static void drawIconButton(Graphics2D g, int x, int y, int w, int h,
                                       String emoji, String label, Color base) {
        g.setPaint(new GradientPaint(x, y, base.brighter(), x, y + h, base));
        g.fillRoundRect(x, y, w, h, 8, 8);
        g.setPaint(null);

        g.setColor(new Color(255, 255, 255, 55));
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(x, y, w, h, 8, 8);
        g.setStroke(new BasicStroke(1f));

        g.setFont(FontManager.getEmoji(13));
        g.setColor(Color.WHITE);
        g.drawString(emoji, x + 6, y + 19);

        g.setFont(FontManager.getBold(8));
        g.setColor(new Color(210, 240, 185));
        g.drawString(label, x + 24, y + 20);
    }

    // ── divider ───────────────────────────────────────────────────────────────

    private static void drawDivider(Graphics2D g, int sx, int py, int sw) {
        g.setPaint(new LinearGradientPaint(
                sx + 10, py, sx + sw - 10, py,
                new float[]{ 0f, 0.5f, 1f },
                new Color[] { new Color(0,0,0,0), DIVIDER, new Color(0,0,0,0) }
        ));
        g.setStroke(new BasicStroke(1f));
        g.drawLine(sx + 10, py, sx + sw - 10, py);
        g.setStroke(new BasicStroke(1f));
        g.setPaint(null);
    }

    // ── wood texture overlay ──────────────────────────────────────────────────

    private static void drawWoodTexture(Graphics2D g, int sx, int sw, int h) {
        g.setColor(new Color(255, 255, 255, 4));
        for (int y = 0; y < h; y += 18) {
            g.setStroke(new BasicStroke(1f));
            g.drawLine(sx, y, sx + sw, y + 3);
        }
        g.setStroke(new BasicStroke(1f));
    }

    // ── coin icon ─────────────────────────────────────────────────────────────

    private static void drawCoinIcon(Graphics2D g, int x, int y, int size, int tick) {
        g.setPaint(new GradientPaint(x, y,
                new Color(255, 240, 80), x + size, y + size, new Color(200, 145, 10)));
        g.fillOval(x, y, size, size);
        g.setPaint(null);
        g.setColor(new Color(180, 120, 10));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(x, y, size, size);
        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(255, 255, 200, 180));
        g.fillOval(x + size / 4, y + size / 5, size / 3, size / 4);
        g.setFont(FontManager.getBold(9));
        g.setColor(new Color(160, 105, 10));
        g.drawString("$", x + size / 2 - 4, y + size / 2 + 4);
    }

    // ── number formatter ──────────────────────────────────────────────────────

    private static String formatNumber(int n) {
        if (n >= 1_000_000) return String.format("%.1fM", n / 1_000_000.0);
        if (n >= 1_000)     return String.format("%.1fK", n / 1_000.0);
        return "" + n;
    }

    private SidebarRenderer() {}
}