package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.models.Achievement;
import OOP_PROJECT.CatchTheBall.src.renderers.FarmBackgroundRenderer;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AchievementsScreen extends Screen {
    private Rectangle backBtn;
    private int hovered      = -1;
    private int scrollOffset = 0;

    private static final int COLS   = 3;
    private static final int GAP_X  = 16;
    private static final int GAP_Y  = 14;
    // Cards start at 120px — safely below the badge pill (which ends at ~100px)
    private static final int TOP_Y  = 120;
    private static final int CARD_H = 118;

    public AchievementsScreen(GamePanel panel) { super(panel); }

    @Override
    public void onEnter() {
        super.onEnter();
        scrollOffset = 0;
        panel.getAchievementManager().loadFromString(panel.getPlayerData().getAchievementData());
    }

    @Override public void update() { tickCount++; }

    @Override
    public void draw(Graphics2D g) {
        FarmBackgroundRenderer.draw(g, GamePanel.W, GamePanel.H, tickCount);

        g.setColor(new Color(0, 0, 0, 130));
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);

        RenderUtils.drawHeaderBar(g, GamePanel.W, "Achievements");

        List<Achievement> all = panel.getAchievementManager().getAll();
        int unlocked = (int) all.stream().filter(Achievement::isUnlocked).count();

        // Badge — clean text, no star/asterisk that risks rendering as []
        String badge = unlocked + " / " + all.size() + " Unlocked";
        g.setFont(FontManager.getBold(13));
        FontMetrics bfm = g.getFontMetrics();
        int badgeW = bfm.stringWidth(badge) + 32;
        int badgeX = GamePanel.W / 2 - badgeW / 2;
        // Badge sits at y=72, height=26, so it ends at y=98 — cards start at TOP_Y=120
        int badgeY = 72;
        int badgeH = 26;

        GradientPaint badgeGP = new GradientPaint(badgeX, badgeY,
                new Color(60, 160, 20), badgeX + badgeW, badgeY, new Color(120, 220, 40));
        g.setPaint(badgeGP);
        g.fillRoundRect(badgeX, badgeY, badgeW, badgeH, 13, 13);
        g.setPaint(null);
        g.setColor(new Color(200, 255, 100));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(badgeX, badgeY, badgeW, badgeH, 13, 13);
        g.setStroke(new BasicStroke(1f));
        RenderUtils.drawCenteredText(g, badge, GamePanel.W / 2, badgeY + 17,
                FontManager.getBold(13), Color.WHITE);

        // Card grid
        int margin = 28;
        int cardW  = (GamePanel.W - margin * 2 - GAP_X * (COLS - 1)) / COLS;

        // Clip starts just below the badge so cards can never overlap it
        int clipTop = badgeY + badgeH + 4;
        Shape oldClip = g.getClip();
        g.setClip(0, clipTop, GamePanel.W, GamePanel.H - clipTop - 50);

        for (int i = 0; i < all.size(); i++) {
            int col = i % COLS;
            int row = i / COLS;
            int bx  = margin + col * (cardW + GAP_X);
            int by  = TOP_Y  + row * (CARD_H + GAP_Y) - scrollOffset;
            if (by + CARD_H < clipTop || by > GamePanel.H - 50) continue;
            drawAchievementCard(g, all.get(i), bx, by, cardW, CARD_H, i);
        }

        g.setClip(oldClip);

        // Scrollbar
        int totalRows = (int) Math.ceil(all.size() / (double) COLS);
        int totalH    = totalRows * (CARD_H + GAP_Y);
        int visibleH  = GamePanel.H - 150 - TOP_Y;
        if (totalH > visibleH) {
            int trackH    = GamePanel.H - 160;
            int thumbH    = Math.max(36, trackH * visibleH / totalH);
            int maxScroll = totalH - visibleH;
            int thumbY    = clipTop + (scrollOffset * (trackH - thumbH)) / Math.max(1, maxScroll);
            g.setColor(new Color(0, 0, 0, 80));
            g.fillRoundRect(GamePanel.W - 12, clipTop, 7, trackH, 7, 7);
            GradientPaint sbGP = new GradientPaint(0, thumbY,
                    new Color(80, 220, 40), 0, thumbY + thumbH, new Color(40, 160, 20));
            g.setPaint(sbGP);
            g.fillRoundRect(GamePanel.W - 12, thumbY, 7, thumbH, 7, 7);
            g.setPaint(null);
        }

        backBtn = new Rectangle(20, GamePanel.H - 52, 140, 36);
        RenderUtils.drawButton(g, backBtn, "\u2190 Back", hovered == 0, FontManager.getBold(14));

        g.setFont(FontManager.getBody(11));
        g.setColor(new Color(160, 210, 130));
        g.drawString("Scroll: mouse wheel or \u2191\u2193", GamePanel.W - 230, GamePanel.H - 18);
    }

    private void drawAchievementCard(Graphics2D g, Achievement a,
                                     int x, int y, int w, int h, int idx) {
        boolean u    = a.isUnlocked();
        float   glow = u ? (float)(0.5 + 0.5 * Math.sin(tickCount * 0.08 + idx * 0.4)) : 0f;

        if (u) {
            GradientPaint cp = new GradientPaint(x, y,
                    new Color(75, 175, 12), x, y + h, new Color(45, 120, 6));
            g.setPaint(cp);
            g.fillRoundRect(x, y, w, h, 14, 14);
            g.setPaint(null);
            g.setColor(new Color(255, 255, 120, (int)(28 * glow)));
            g.fillRoundRect(x, y, w, h, 14, 14);
            g.setColor(new Color(210, 255, 50, (int)(130 + 110 * glow)));
            g.setStroke(new BasicStroke(2.5f));
            g.drawRoundRect(x, y, w, h, 14, 14);
            g.setStroke(new BasicStroke(1f));
            GradientPaint stripe = new GradientPaint(x, y,
                    new Color(255, 255, 255, 45), x, y + 26, new Color(255, 255, 255, 0));
            g.setPaint(stripe);
            g.fillRoundRect(x, y, w, 26, 14, 14);
            g.setPaint(null);
        } else {
            GradientPaint cp = new GradientPaint(x, y,
                    new Color(26, 58, 14), x, y + h, new Color(16, 38, 8));
            g.setPaint(cp);
            g.fillRoundRect(x, y, w, h, 14, 14);
            g.setPaint(null);
            g.setColor(new Color(52, 90, 35, 200));
            g.setStroke(new BasicStroke(1.3f));
            g.drawRoundRect(x, y, w, h, 14, 14);
            g.setStroke(new BasicStroke(1f));
        }

        // Icon box
        int iconBoxSize = 52;
        int iconX = x + 12;
        int iconY = y + (h - iconBoxSize) / 2;
        drawIconBox(g, a.getName(), iconX, iconY, iconBoxSize, u, glow);

        // Text area
        int tx     = iconX + iconBoxSize + 12;
        int availW = w - (tx - x) - 10;

        g.setFont(FontManager.getBold(13));
        g.setColor(u ? new Color(235, 255, 140) : new Color(108, 148, 78));
        String name = a.getName();
        FontMetrics nfm = g.getFontMetrics();
        if (nfm.stringWidth(name) > availW)
            name = name.substring(0, Math.min(name.length(), 18)) + "...";
        g.drawString(name, tx, y + 26);

        g.setFont(FontManager.getBody(11));
        g.setColor(u ? new Color(190, 240, 145) : new Color(78, 112, 58));
        String desc = a.getDescription();
        FontMetrics dfm = g.getFontMetrics();
        if (dfm.stringWidth(desc) > availW)
            desc = desc.substring(0, Math.min(desc.length(), 32)) + "...";
        g.drawString(desc, tx, y + 44);

        // Unlocked pill
        if (u) {
            int pillX = tx, pillY = y + 52;
            GradientPaint pill = new GradientPaint(pillX, pillY,
                    new Color(200, 240, 20), pillX + 82, pillY, new Color(255, 200, 20));
            g.setPaint(pill);
            g.fillRoundRect(pillX, pillY, 82, 17, 9, 9);
            g.setPaint(null);
            RenderUtils.drawCenteredText(g, "UNLOCKED", pillX + 41, pillY + 12,
                    FontManager.getBold(9), new Color(30, 70, 5));
        }

        // Progress bar
        if (a.getProgressTarget() > 1) {
            int barX = tx;
            int barY = y + h - 26;
            int barW = availW;
            int barH = 10;

            g.setColor(new Color(0, 0, 0, 120));
            g.fillRoundRect(barX, barY, barW, barH, barH, barH);

            float ratio = a.getProgressRatio();
            int   fillW = (int)(barW * ratio);
            if (fillW > 0) {
                Color c1 = u ? new Color(90,  225, 15)  : new Color(225, 145, 8);
                Color c2 = u ? new Color(175, 255, 55)  : new Color(255, 205, 45);
                GradientPaint fp = new GradientPaint(barX, barY, c1, barX + fillW, barY, c2);
                g.setPaint(fp);
                g.fillRoundRect(barX, barY, fillW, barH, barH, barH);
                g.setPaint(null);
                g.setColor(new Color(255, 255, 255, 55));
                g.fillRoundRect(barX, barY, fillW, barH / 2, barH, barH);
            }
            g.setColor(u ? new Color(155, 255, 55, 160) : new Color(88, 135, 50, 160));
            g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(barX, barY, barW, barH, barH, barH);
            g.setStroke(new BasicStroke(1f));

            g.setFont(FontManager.getBody(10));
            g.setColor(u ? new Color(205, 255, 115) : new Color(185, 150, 65));
            g.drawString(a.getProgressCurrent() + " / " + a.getProgressTarget(),
                    barX, barY - 3);
        }
    }

    /**
     * Draws the dark rounded icon box and a large color emoji centered inside it.
     * Java's drawString renders emoji in color on Windows (Segoe UI Emoji),
     * macOS (Apple Color Emoji), and Linux with Noto Color Emoji installed.
     */
    private void drawIconBox(Graphics2D g, String name, int x, int y,
                             int size, boolean u, float glow) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        // Background
        GradientPaint ibg = new GradientPaint(x, y,
                u ? new Color(35, 110, 8, 200) : new Color(18, 48, 8, 170),
                x, y + size,
                u ? new Color(18,  72, 4, 200) : new Color(10, 28, 4, 170));
        g2.setPaint(ibg);
        g2.fillRoundRect(x, y, size, size, 10, 10);
        g2.setPaint(null);

        // Border
        if (u) {
            g2.setColor(new Color(175, 255, 50, (int)(100 + 90 * glow)));
            g2.setStroke(new BasicStroke(1.8f));
        } else {
            g2.setColor(new Color(55, 90, 35, 160));
            g2.setStroke(new BasicStroke(1.2f));
        }
        g2.drawRoundRect(x, y, size, size, 10, 10);
        g2.setStroke(new BasicStroke(1f));

        // Emoji — pick platform color emoji font, fall back gracefully
        String emoji = pickEmoji(name.toLowerCase());
        Font emojiFont = resolvEmojiFont(26);
        g2.setFont(emojiFont);
        FontMetrics fm = g2.getFontMetrics();
        int ew = fm.stringWidth(emoji);
        int ex = x + (size - ew) / 2;
        // Vertically center: ascent positions the baseline, so center = y + (size + ascent - descent) / 2
        int ey = y + (size + fm.getAscent() - fm.getDescent()) / 2 - 1;

        // If the achievement is locked, draw emoji at reduced opacity so it looks greyed out
        if (!u) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.45f));
        }
        g2.drawString(emoji, ex, ey);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        g2.dispose();
    }

    /** Tries common color-emoji fonts in priority order; returns a usable Font. */
    private Font resolvEmojiFont(int size) {
        String[] names = {"Segoe UI Emoji", "Apple Color Emoji", "Noto Color Emoji", "Symbola", "Dialog"};
        for (String n : names) {
            Font f = new Font(n, Font.PLAIN, size);
            // Font(name,…) never throws; it falls back to Dialog when name is unknown.
            // We accept Dialog only as the last resort.
            if (!f.getFamily(java.util.Locale.ROOT).equalsIgnoreCase("dialog") || n.equals("Dialog")) {
                return f;
            }
        }
        return new Font(Font.DIALOG, Font.PLAIN, size);
    }

    /**
     * Maps achievement-name keywords → a single Unicode emoji string.
     * All codepoints are expressed as surrogate-pair literals so they compile
     * cleanly on any Java source encoding.
     */
    private String pickEmoji(String n) {
        // 🍎  red apple  — Harvest / Fruit
        if (n.contains("harvest") || n.contains("fruit"))  return "\uD83C\uDF4E";
        // 🔥  fire       — On Fire / Streak
        if (n.contains("fire")    || n.contains("streak")) return "\uD83D\uDD25";
        // ⚡  lightning  — Hot / Combo
        if (n.contains("hot")     || n.contains("combo"))  return "\u26A1\uFE0F";
        // 🛡️  shield     — Untouchable / Shield
        if (n.contains("touch")   || n.contains("shield")) return "\uD83D\uDEE1\uFE0F";
        // 💰  money bag  — Rich / Coin
        if (n.contains("rich")    || n.contains("coin"))   return "\uD83D\uDCB0";
        // 🥇  gold medal — Gold Farmer
        if (n.contains("gold"))                            return "\uD83E\uDD47";
        // 🏡  house      — Dream Farm
        if (n.contains("dream")   || n.contains("farm"))   return "\uD83C\uDFE1";
        // 👑  crown      — Legend / Level
        if (n.contains("legend")  || n.contains("level"))  return "\uD83D\uDC51";
        // ⚽  soccer ball — Century / Ball
        if (n.contains("century") || n.contains("ball"))   return "\u26BD\uFE0F";
        // 🏅  medal      — Veteran / Game
        if (n.contains("veteran") || n.contains("game"))   return "\uD83C\uDFC5";
        // 🌈  rainbow    — Rainbow Catcher
        if (n.contains("rainbow"))                         return "\uD83C\uDF08";
        // ❄️  snowflake  — Ice / Frozen
        if (n.contains("ice")     || n.contains("frozen")) return "\u2744\uFE0F";
        // ⭐  star       — fallback
        return "\u2B50\uFE0F";
    }

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
    public void onMouseWheelMoved(MouseWheelEvent e) {
        scroll((int)(e.getPreciseWheelRotation() * 35));
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) panel.switchToWithFade(GameScreenType.MAIN_MENU);
        if (e.getKeyCode() == KeyEvent.VK_DOWN)   scroll(38);
        if (e.getKeyCode() == KeyEvent.VK_UP)      scroll(-38);
    }

    public void scroll(int amount) {
        scrollOffset = Math.max(0, Math.min(scrollOffset + amount, 700));
    }
}