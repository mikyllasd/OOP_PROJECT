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
    private static final Color BG_TOP        = new Color(34,  62,  24);
    private static final Color BG_BOT        = new Color(22,  44,  14);
    private static final Color CARD_BG       = new Color(255, 255, 255, 18);
    private static final Color CARD_BORDER   = new Color(255, 255, 255, 38);
    private static final Color DIVIDER       = new Color(120, 185, 80,  55);
    private static final Color LABEL_COL     = new Color(168, 210, 130);
    private static final Color SIDEBAR_EDGE  = new Color(138, 205, 78);

    // ── stat accent colours ───────────────────────────────────────────────────
    private static final Color C_DIFF   = new Color(255, 218,  68);
    private static final Color C_LEVEL  = new Color(255, 238,  90);
    private static final Color C_SCORE  = new Color( 85, 235, 255);
    private static final Color C_TARGET = new Color(195, 195, 195);
    private static final Color C_LIVES  = new Color(255, 105, 105);
    private static final Color C_TIME   = new Color(100, 255, 155);
    private static final Color C_COINS  = new Color(255, 215,  50);
    private static final Color C_COMBO  = new Color(255, 100, 255);

    // ── public API ────────────────────────────────────────────────────────────

    public static void draw(Graphics2D g, GameState state, FarmProgression farm,
                            PlayerData playerData, SoundManager sound, int tickCount) {

        int sx = GamePanel.ARENA_W;
        int sw = GamePanel.SIDEBAR_W;
        int h  = GamePanel.H;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_QUALITY);

        // ── background ───────────────────────────────────────────────────────
        g2.setPaint(new GradientPaint(sx, 0, BG_TOP, sx, h, BG_BOT));
        g2.fillRect(sx, 0, sw, h);
        g2.setPaint(null);

        // subtle wood-grain texture overlay
        drawWoodTexture(g2, sx, sw, h);

        // left accent border
        g2.setPaint(new GradientPaint(sx, 0, SIDEBAR_EDGE, sx, h, new Color(60, 140, 30)));
        g2.fillRect(sx, 0, 3, h);
        g2.setPaint(null);

        // ── header banner ────────────────────────────────────────────────────
        drawHeaderBanner(g2, sx, sw, tickCount);

        int py = 62;

        // ── stat cards ───────────────────────────────────────────────────────
        py = drawStatCard(g2, sx, py, sw, "\uD83C\uDF1F", "DIFFICULTY",
                state.getDifficulty().getDisplayName(), C_DIFF);
        py = drawStatCard(g2, sx, py, sw, "\uD83D\uDCC8", "LEVEL",
                "" + state.getLevel(), C_LEVEL);
        py = drawStatCard(g2, sx, py, sw, "\u2B50", "SCORE",
                formatNumber(state.getScore()), C_SCORE);
        py = drawTargetCard(g2, sx, py, sw, state);
        py = drawLivesCard(g2, sx, py, sw, state.getLives(), tickCount);
        py = drawTimerCard(g2, sx, py, sw, state, tickCount);
        py = drawCoinsCard(g2, sx, py, sw, playerData.getTotalCoins(), tickCount);

        // ── combo ────────────────────────────────────────────────────────────
        if (state.getCombo() > 0) {
            py = drawComboCard(g2, sx, py, sw, state, tickCount);
        }

        drawDivider(g2, sx, py, sw); py += 12;

        // ── level progress bar ────────────────────────────────────────────────
        drawProgressSection(g2, sx, py, sw, state); py += 38;

        // ── active power-ups ─────────────────────────────────────────────────
        drawActivePowerUps(g2, sx, sw, py, state); py += 40;

        // ── farm badge ───────────────────────────────────────────────────────
        drawFarmBadge(g2, sx, sw, py, farm); py += 72;

        // ── control buttons ──────────────────────────────────────────────────
        drawControlButtons(g2, sx, sw, py, sound);

        g2.dispose();
    }

    // ── header banner ─────────────────────────────────────────────────────────

    private static void drawHeaderBanner(Graphics2D g, int sx, int sw, int tick) {
        int bx = sx + 6;
        int bw = sw - 12;

        // banner bg
        GradientPaint bg = new GradientPaint(bx, 4, new Color(60, 115, 30), bx, 50, new Color(38, 78, 18));
        g.setPaint(bg);
        g.fillRoundRect(bx, 4, bw, 50, 12, 12);
        g.setPaint(null);

        // sheen
        g.setColor(new Color(255, 255, 255, 22));
        g.fillRoundRect(bx + 2, 5, bw - 4, 22, 10, 10);

        // border
        g.setColor(new Color(138, 218, 68, 155));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(bx, 4, bw, 50, 12, 12);
        g.setStroke(new BasicStroke(1f));

        // animated leaf icon
        int leafOff = (int)(Math.sin(tick * 0.04) * 2);
        g.setFont(FontManager.getEmoji(16));
        g.setColor(Color.WHITE);
        g.drawString("\uD83C\uDF3E", bx + 6, 34 + leafOff);  // 🌾

        // title
        g.setFont(FontManager.getBold(13));
        g.setColor(new Color(255, 238, 90));
        g.drawString("FARM STATS", bx + 28, 35);

        // small subtitle
        g.setFont(FontManager.getBody(9));
        g.setColor(new Color(168, 218, 118));
        g.drawString("catch the harvest", bx + 28, 47);
    }

    // ── stat card (generic) ───────────────────────────────────────────────────

    /**
     * Draws a pill-shaped stat card and returns the new y offset.
     */
    private static int drawStatCard(Graphics2D g, int sx, int py, int sw,
                                    String icon, String label, String value, Color accent) {
        int bx = sx + 8;
        int bw = sw - 16;
        int bh = 48;

        // card background
        g.setColor(CARD_BG);
        g.fillRoundRect(bx, py, bw, bh, 10, 10);

        // left accent stripe
        g.setPaint(new GradientPaint(bx, py, accent, bx, py + bh, accent.darker()));
        g.fillRoundRect(bx, py, 4, bh, 4, 4);
        g.setPaint(null);

        // card border
        g.setColor(CARD_BORDER);
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(bx, py, bw, bh, 10, 10);

        // icon
        g.setFont(FontManager.getEmoji(15));
        g.setColor(Color.WHITE);
        g.drawString(icon, bx + 8, py + 18);

        // label
        g.setFont(FontManager.getBold(9));
        g.setColor(LABEL_COL);
        g.drawString(label, bx + 27, py + 16);

        // value
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

        // mini inline bar
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

    // ── lives card ────────────────────────────────────────────────────────────

    private static int drawLivesCard(Graphics2D g, int sx, int py, int sw, int lives, int tick) {
        int bx = sx + 8;
        int bw = sw - 16;
        int bh = 50;

        g.setColor(CARD_BG);
        g.fillRoundRect(bx, py, bw, bh, 10, 10);

        g.setPaint(new GradientPaint(bx, py, C_LIVES, bx, py + bh, C_LIVES.darker()));
        g.fillRoundRect(bx, py, 4, bh, 4, 4);
        g.setPaint(null);

        g.setColor(CARD_BORDER);
        g.drawRoundRect(bx, py, bw, bh, 10, 10);

        // label
        g.setFont(FontManager.getBold(9));
        g.setColor(LABEL_COL);
        g.drawString("LIVES", bx + 8, py + 14);

        // draw individual hearts
        int maxLives = 5;
        int heartSize = 16;
        int heartGap = 4;
        int totalW = maxLives * (heartSize + heartGap) - heartGap;
        int startX = bx + (bw - totalW) / 2;
        int heartY = py + 22;

        g.setFont(FontManager.getEmoji(heartSize));
        for (int i = 0; i < maxLives; i++) {
            int hx = startX + i * (heartSize + heartGap);
            // pulse the last remaining heart when lives are low
            int pulse = (lives <= 1 && i == 0) ? (int)(Math.sin(tick * 0.15) * 2) : 0;
            if (i < lives) {
                g.setColor(C_LIVES);
                // filled heart using drawHeart helper
                drawHeart(g, hx, heartY - pulse, heartSize, true, tick, lives);
            } else {
                // empty heart
                g.setColor(new Color(80, 40, 40, 120));
                drawHeart(g, hx, heartY, heartSize, false, tick, lives);
            }
        }

        return py + bh + 5;
    }

    private static void drawHeart(Graphics2D g, int x, int y, int size, boolean filled, int tick, int lives) {
        // draw emoji hearts — filled or dim
        Color prev = g.getColor();
        if (filled) {
            g.setColor(C_LIVES);
        } else {
            g.setColor(new Color(90, 50, 50, 100));
        }
        // Use a simple polygon heart shape
        int s = size;
        int cx = x + s / 2;
        int cy = y + s / 2;
        GeneralPath heart = new GeneralPath();
        // Bézier heart centred at (cx, cy)
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
            // glow behind filled hearts
            g.setColor(new Color(255, 100, 100, 55));
            Graphics2D gx = (Graphics2D) g.create();
            gx.setStroke(new BasicStroke(4f));
            gx.draw(heart);
            gx.dispose();
            g.setColor(C_LIVES);
            g.fill(heart);
            // specular
            g.setColor(new Color(255, 200, 200, 100));
            GeneralPath spec = new GeneralPath();
            spec.moveTo(cx - hs * 0.4f, cy - hs * 0.3f);
            spec.curveTo(cx - hs * 0.8f, cy - hs * 0.6f, cx - hs * 0.1f, cy - hs * 0.7f, cx - hs * 0.1f, cy - hs * 0.2f);
            g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.draw(spec);
            g.setStroke(new BasicStroke(1f));
        } else {
            g.setColor(new Color(90, 50, 50, 90));
            g.fill(heart);
            g.setColor(new Color(120, 70, 70, 120));
            g.setStroke(new BasicStroke(1f));
            g.draw(heart);
            g.setStroke(new BasicStroke(1f));
        }
        g.setColor(prev);
    }

    // ── timer card ────────────────────────────────────────────────────────────

    private static int drawTimerCard(Graphics2D g, int sx, int py, int sw,
                                     GameState state, int tick) {
        int bx = sx + 8;
        int bw = sw - 16;
        int bh = 56;

        boolean urgent = state.getTimeLeft() <= 60;
        boolean pulse  = urgent && tick % 30 < 15;
        Color accent   = urgent ? new Color(255, 70, 70) : C_TIME;

        // pulse glow
        if (pulse) {
            g.setColor(new Color(255, 50, 50, 35));
            g.fillRoundRect(bx - 3, py - 3, bw + 6, bh + 6, 13, 13);
        }

        g.setColor(CARD_BG);
        g.fillRoundRect(bx, py, bw, bh, 10, 10);

        g.setPaint(new GradientPaint(bx, py, accent, bx, py + bh, accent.darker()));
        g.fillRoundRect(bx, py, 4, bh, 4, 4);
        g.setPaint(null);

        g.setColor(pulse ? new Color(255, 90, 90, 140) : CARD_BORDER);
        g.setStroke(new BasicStroke(pulse ? 1.5f : 1f));
        g.drawRoundRect(bx, py, bw, bh, 10, 10);
        g.setStroke(new BasicStroke(1f));

        // clock icon
        g.setFont(FontManager.getEmoji(15));
        g.setColor(Color.WHITE);
        g.drawString(urgent ? "\u23F0" : "\u231A", bx + 8, py + 20);   // ⏰ or 🕚

        // label
        g.setFont(FontManager.getBold(9));
        g.setColor(LABEL_COL);
        g.drawString("TIME LEFT", bx + 27, py + 17);

        // big time value
        String mins = "" + state.getTimeLeft() / 60;
        String secs = String.format("%02d", state.getTimeLeft() % 60);
        g.setFont(FontManager.getBodyBold(26));
        g.setColor(accent);
        g.drawString(mins + ":" + secs, bx + 18, py + 46);

        // time bar (full width, thin)
        // assumes some maxTime constant; we'll derive from "60 sec per level" heuristic
        int maxTime = Math.max(state.getTimeLeft(), 90); // fallback
        float tPct = Math.max(0f, (float) state.getTimeLeft() / 90f);
        int barX = bx + 8;
        int barW = bw - 16;
        g.setColor(new Color(0, 0, 0, 70));
        g.fillRoundRect(barX, py + bh - 10, barW, 6, 3, 3);
        g.setPaint(new GradientPaint(barX, 0, urgent ? new Color(255,80,80) : new Color(80,220,80),
                barX + barW, 0, urgent ? new Color(255,180,50) : new Color(180,255,80)));
        g.fillRoundRect(barX, py + bh - 10, Math.max(4, (int)(barW * tPct)), 6, 3, 3);
        g.setPaint(null);

        return py + bh + 5;
    }

    // ── coins card ────────────────────────────────────────────────────────────

    private static int drawCoinsCard(Graphics2D g, int sx, int py, int sw, int coins, int tick) {
        int bx = sx + 8;
        int bw = sw - 16;
        int bh = 48;

        // subtle golden shimmer animation on the card border
        float shimmer = (float)(Math.sin(tick * 0.05) * 0.5 + 0.5);
        int alpha = (int)(55 + shimmer * 60);

        g.setColor(CARD_BG);
        g.fillRoundRect(bx, py, bw, bh, 10, 10);

        g.setPaint(new GradientPaint(bx, py, C_COINS, bx, py + bh, new Color(200, 155, 20)));
        g.fillRoundRect(bx, py, 4, bh, 4, 4);
        g.setPaint(null);

        g.setColor(new Color(255, 215, 50, alpha));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(bx, py, bw, bh, 10, 10);
        g.setStroke(new BasicStroke(1f));

        // coin icon
        drawCoinIcon(g, bx + 9, py + 12, 16, tick);

        g.setFont(FontManager.getBold(9));
        g.setColor(LABEL_COL);
        g.drawString("COINS", bx + 32, py + 16);

        g.setFont(FontManager.getBodyBold(20));
        g.setColor(C_COINS);
        g.drawString(formatNumber(coins), bx + 32, py + 38);

        return py + bh + 5;
    }

    /** Draw a shiny pixel-art-style coin. */
    private static void drawCoinIcon(Graphics2D g, int x, int y, int size, int tick) {
        // coin body
        g.setPaint(new GradientPaint(x, y, new Color(255, 240, 80), x + size, y + size, new Color(200, 145, 10)));
        g.fillOval(x, y, size, size);
        g.setPaint(null);
        // rim
        g.setColor(new Color(180, 120, 10));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(x, y, size, size);
        g.setStroke(new BasicStroke(1f));
        // specular
        g.setColor(new Color(255, 255, 200, 180));
        g.fillOval(x + size / 4, y + size / 5, size / 3, size / 4);
        // centre mark
        g.setFont(FontManager.getBold(9));
        g.setColor(new Color(160, 105, 10));
        g.drawString("$", x + size / 2 - 4, y + size / 2 + 4);
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

        // rainbow shimmer
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

        // fire icon
        g.setFont(FontManager.getEmoji(16));
        g.setColor(Color.WHITE);
        g.drawString("\uD83D\uDD25", bx + 7, py + 22);  // 🔥

        g.setFont(FontManager.getBold(9));
        g.setColor(LABEL_COL);
        g.drawString("COMBO  x" + c, bx + 26, py + 16);

        g.setFont(FontManager.getBodyBold(22));
        g.setColor(accent);
        g.drawString("x" + String.format("%.1f", state.getComboMultiplier()), bx + 26, py + 40);

        // tiny star sparkles
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

    private static void drawProgressSection(Graphics2D g, int sx, int py, int sw, GameState state) {
        int bx = sx + 8;
        int bw = sw - 16;

        g.setFont(FontManager.getBold(9));
        g.setColor(LABEL_COL);
        g.drawString("\uD83D\uDCCA  LEVEL PROGRESS", bx, py + 11);

        float prog = Math.min(1f, (float) state.getScore() / state.getLevelTarget());

        // track
        g.setColor(new Color(0, 0, 0, 80));
        g.fillRoundRect(bx, py + 15, bw, 12, 6, 6);

        // fill gradient
        g.setPaint(new GradientPaint(bx, 0, new Color(70, 200, 70), bx + bw, 0, new Color(155, 255, 80)));
        g.fillRoundRect(bx, py + 15, Math.max(6, (int)(bw * prog)), 12, 6, 6);
        g.setPaint(null);

        // track border
        g.setColor(new Color(80, 180, 50, 90));
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(bx, py + 15, bw, 12, 6, 6);
        g.setStroke(new BasicStroke(1f));

        // percentage label centred on bar
        g.setFont(FontManager.getBold(8));
        g.setColor(Color.WHITE);
        String pctStr = (int)(prog * 100) + "%";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(pctStr, bx + (bw - fm.stringWidth(pctStr)) / 2, py + 25);
    }

    // ── power-ups ─────────────────────────────────────────────────────────────

    private static void drawActivePowerUps(Graphics2D g, int sx, int sw, int py, GameState state) {
        // section label
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
        int tw = 54;
        int th = 28;
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

        // card
        g.setPaint(new GradientPaint(bx, py, new Color(48, 95, 28), bx, py + bh, new Color(28, 60, 14)));
        g.fillRoundRect(bx, py, bw, bh, 12, 12);
        g.setPaint(null);
        g.setColor(new Color(100, 185, 55, 120));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(bx, py, bw, bh, 12, 12);
        g.setStroke(new BasicStroke(1f));

        // sheen
        g.setColor(new Color(255, 255, 255, 14));
        g.fillRoundRect(bx + 2, py + 2, bw - 4, bh / 2, 10, 10);

        // emoji
        g.setFont(FontManager.getEmoji(30));
        FontMetrics fm = g.getFontMetrics();
        String emoji = farm.getEmoji();
        g.setColor(Color.WHITE);
        g.drawString(emoji, bx + (bw - fm.stringWidth(emoji)) / 2, py + 38);

        // name
        g.setFont(FontManager.getBold(9));
        fm = g.getFontMetrics();
        String name = farm.getName();
        g.setColor(new Color(185, 230, 150));
        g.drawString(name, bx + (bw - fm.stringWidth(name)) / 2, py + 56);
    }

    // ── control buttons ───────────────────────────────────────────────────────

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

        // border
        g.setColor(new Color(255, 255, 255, 55));
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(x, y, w, h, 8, 8);
        g.setStroke(new BasicStroke(1f));

        // icon
        g.setFont(FontManager.getEmoji(13));
        g.setColor(Color.WHITE);
        g.drawString(emoji, x + 6, y + 19);

        // label text
        g.setFont(FontManager.getBold(8));
        g.setColor(new Color(210, 240, 185));
        g.drawString(label, x + 24, y + 20);
    }

    // ── divider ───────────────────────────────────────────────────────────────

    private static void drawDivider(Graphics2D g, int sx, int py, int sw) {
        g.setPaint(new GradientPaint(sx + 10, py, new Color(0, 0, 0, 0),
                sx + sw / 2, py, DIVIDER, sx + sw - 10, py, new Color(0, 0, 0, 0)));
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

    // ── number formatter ──────────────────────────────────────────────────────

    private static String formatNumber(int n) {
        if (n >= 1_000_000) return String.format("%.1fM", n / 1_000_000.0);
        if (n >= 1_000)     return String.format("%.1fK", n / 1_000.0);
        return "" + n;
    }

    private SidebarRenderer() {}
}