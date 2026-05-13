package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.renderers.MenuBackgroundRenderer;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class MainMenuScreen extends Screen {
    private Rectangle[] buttons;
    private Rectangle   exitBtn;
    private Rectangle   settingsBtn;
    private int         hovered = -1;

    private static final String[] LABELS = {
        "Play Game",
        "Leaderboard",
        "Wardrobe",
        "Farm Upgrade",
        "Achievements",
        "Stats"
    };

    // Floating fruit particles
    private float[] fruitX, fruitY, fruitSpeed, fruitRot;
    private int[]   fruitType;
    private static final int FRUIT_COUNT = 10;

    // ── layout constants ──────────────────────────────────────────────────────
    private static final int BTN_W    = 230;
    private static final int BTN_H    = 44;
    private static final int BTN_GAP  = 10;
    private static final int GRID_TOP = 168;

    // Icon shapes per button: each int[] is a list of shape codes drawn inside the button
    // We draw simple geometric icons using Java2D — no external font needed.

    public MainMenuScreen(GamePanel panel) {
        super(panel);
        Random rand = new Random();
        fruitX     = new float[FRUIT_COUNT];
        fruitY     = new float[FRUIT_COUNT];
        fruitSpeed = new float[FRUIT_COUNT];
        fruitRot   = new float[FRUIT_COUNT];
        fruitType  = new int[FRUIT_COUNT];
        for (int i = 0; i < FRUIT_COUNT; i++) {
            fruitX[i]     = rand.nextFloat() * GamePanel.W;
            fruitY[i]     = rand.nextFloat() * GamePanel.H;
            fruitSpeed[i] = 0.4f + rand.nextFloat() * 1.2f;
            fruitRot[i]   = rand.nextFloat() * 360f;
            fruitType[i]  = rand.nextInt(4);
        }
    }

    @Override
    public void onEnter() {
        super.onEnter();
        buttons = null;
        hovered = -1;
        checkDailyLogin();
    }

    private void checkDailyLogin() {
        String today = java.time.LocalDate.now().toString();
        String last  = panel.getPlayerData().getProfile().getLastLoginDate();
        if (!today.equals(last)) {
            int streak = panel.getPlayerData().getProfile().getConsecutiveLoginDays();
            try {
                java.time.LocalDate lastDate = java.time.LocalDate.parse(last);
                streak = lastDate.plusDays(1).toString().equals(today) ? streak + 1 : 1;
            } catch (Exception e) { streak = 1; }
            int reward = Math.min(streak, 7) * 10;
            panel.getPlayerData().getProfile().setConsecutiveLoginDays(streak);
            panel.getPlayerData().getProfile().setLastLoginDate(today);
            panel.getPlayerData().addCoins(reward);
            panel.getPlayerData().save();
        }
    }

    @Override
    public void update() {
        tickCount++;
        for (int i = 0; i < FRUIT_COUNT; i++) {
            fruitY[i]   += fruitSpeed[i];
            fruitRot[i] += fruitSpeed[i] * 0.8f;
            if (fruitY[i] > GamePanel.H + 30) {
                fruitY[i]   = -30;
                fruitX[i]   = (float)(Math.random() * GamePanel.W);
                fruitRot[i] = (float)(Math.random() * 360);
            }
        }
    }

    // ── draw ─────────────────────────────────────────────────────────────────

    @Override
    public void draw(Graphics2D g) {
        MenuBackgroundRenderer.draw(g, tickCount, GamePanel.W, GamePanel.H);
        drawFallingFruits(g);
        drawTitle(g);
        drawButtons(g);
        drawBottomBar(g);

        exitBtn = new Rectangle(GamePanel.W - 150, GamePanel.H - 80, 130, 34);
        RenderUtils.drawExitButton(g, exitBtn, hovered == 9998);

        settingsBtn = new Rectangle(GamePanel.W - 54, 10, 40, 40);
        drawSettingsGear(g, settingsBtn, hovered == 9999);
    }

    // ── falling fruits ────────────────────────────────────────────────────────

    private void drawFallingFruits(Graphics2D g) {
        Color[][] fruitColors = {
            {new Color(220,40,40),  new Color(255,110,110)},
            {new Color(255,140,0),  new Color(255,210, 80)},
            {new Color(220,20,60),  new Color(255,100,120)},
            {new Color(230,210,0),  new Color(255,252,120)}
        };
        for (int i = 0; i < FRUIT_COUNT; i++) {
            int   type  = fruitType[i];
            Color main  = fruitColors[type][0];
            Color light = fruitColors[type][1];
            int   fx    = (int)fruitX[i], fy = (int)fruitY[i];
            Graphics2D fg = (Graphics2D) g.create();
            fg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            fg.rotate(Math.toRadians(fruitRot[i]), fx + 11, fy + 11);
            fg.setColor(new Color(main.getRed(),  main.getGreen(),  main.getBlue(),  55));
            fg.fillOval(fx, fy, 22, 22);
            fg.setColor(new Color(light.getRed(), light.getGreen(), light.getBlue(), 35));
            fg.fillOval(fx + 3, fy + 3, 9, 7);
            fg.setColor(new Color(80, 50, 20, 45));
            fg.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            fg.drawLine(fx + 11, fy, fx + 13, fy - 5);
            fg.setStroke(new BasicStroke(1f));
            fg.dispose();
        }
    }

    // ── title ─────────────────────────────────────────────────────────────────

    private void drawTitle(Graphics2D g) {
        float bobY = (float)(Math.sin(tickCount * 0.03) * 4);

        int tw = 540, th = 88;
        int tx = (GamePanel.W - tw) / 2;
        int ty = (int)(45 + bobY);
        g.setColor(new Color(0, 0, 0, 110));
        g.fillRoundRect(tx, ty, tw, th, 22, 22);
        g.setColor(new Color(120, 210, 70, 60));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(tx, ty, tw, th, 22, 22);
        g.setStroke(new BasicStroke(1f));

        g.setFont(FontManager.getBold(44));
        FontMetrics fm = g.getFontMetrics();
        String title = "Catch the Harvest!";
        int titleX = (GamePanel.W - fm.stringWidth(title)) / 2;
        int titleY = (int)(107 + bobY);
        g.setColor(new Color(5, 25, 2, 200));
        g.drawString(title, titleX + 3, titleY + 3);
        g.setColor(ColorPalette.TEXT_GOLD);
        g.drawString(title, titleX, titleY);

        RenderUtils.drawCenteredText(g,
                "A rustic farm chase with fruit, bonuses, and charm.",
                GamePanel.W / 2, 142,
                FontManager.getBody(15, Font.ITALIC),
                new Color(210, 255, 170, 215));
    }

    // ── buttons (single-column, with icons) ───────────────────────────────────

    private void drawButtons(Graphics2D g) {
        buttons = new Rectangle[LABELS.length];

        // Centre the single column horizontally
        int gridX = (GamePanel.W - BTN_W) / 2;

        for (int i = 0; i < LABELS.length; i++) {
            int by = GRID_TOP + i * (BTN_H + BTN_GAP);
            buttons[i] = new Rectangle(gridX, by, BTN_W, BTN_H);

            // Draw button background (Play Game gets a gold highlight)
            drawMenuButton(g, buttons[i], LABELS[i], i == hovered, i == 0);

            // Draw icon on the left side of the button
            int iconX = gridX + 14;
            int iconY = by + BTN_H / 2;
            drawButtonIcon(g, i, iconX, iconY, i == hovered, i == 0);
        }
    }

    /**
     * Draws a single menu button. Play Game (index 0) gets a gold style;
     * all others use the standard green style.
     */
    private void drawMenuButton(Graphics2D g, Rectangle r, String label,
                                boolean hovered, boolean isPlay) {
        Graphics2D gg = (Graphics2D) g.create();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow
        gg.setColor(isPlay ? new Color(154, 88, 0, 200) : new Color(35, 92, 10, 200));
        gg.fillRoundRect(r.x, r.y + 4, r.width, r.height, 12, 12);

        // Body gradient: top colour → bottom colour
        Color top    = isPlay
                ? (hovered ? new Color(255, 215, 80)  : new Color(247, 193, 42))
                : (hovered ? new Color(100, 210, 60)  : new Color(90,  192, 48));
        Color bottom = isPlay
                ? (hovered ? new Color(230, 145, 10)  : new Color(224, 138, 0))
                : (hovered ? new Color(60,  160, 24)  : new Color(61,  144, 24));

        GradientPaint gp = new GradientPaint(r.x, r.y, top, r.x, r.y + r.height, bottom);
        gg.setPaint(gp);
        gg.fillRoundRect(r.x, r.y, r.width, r.height, 12, 12);
        gg.setPaint(null);

        // Border
        gg.setColor(new Color(255, 255, 255, 55));
        gg.setStroke(new BasicStroke(1.5f));
        gg.drawRoundRect(r.x, r.y, r.width, r.height, 12, 12);
        gg.setStroke(new BasicStroke(1f));

        // Label — shifted right to leave room for icon (icon area = 36 px)
        Font font = isPlay ? FontManager.getBold(15) : FontManager.getBold(14);
        gg.setFont(font);
        FontMetrics fm = gg.getFontMetrics();
        // Centre the label in the space to the right of the icon column
        int textAreaX  = r.x + 36;
        int textAreaW  = r.width - 36;
        int labelX     = textAreaX + (textAreaW - fm.stringWidth(label)) / 2;
        int labelY     = r.y + (r.height + fm.getAscent() - fm.getDescent()) / 2;

        // Shadow
        gg.setColor(new Color(0, 0, 0, 80));
        gg.drawString(label, labelX + 1, labelY + 1);
        // Text
        gg.setColor(isPlay ? new Color(58, 26, 0) : Color.WHITE);
        gg.drawString(label, labelX, labelY);

        gg.dispose();
    }

    /**
     * Draws a small Java2D icon at (cx, cy) — centred vertically on the button.
     *
     * Index → icon:
     *   0  Play Game     — right-pointing triangle (play symbol)
     *   1  Leaderboard   — three ascending bars
     *   2  Wardrobe      — simple shirt silhouette
     *   3  Farm Upgrade  — upward arrow with base line
     *   4  Achievements  — star outline
     *   5  Stats         — line chart
     */
    private void drawButtonIcon(Graphics2D g, int index, int cx, int cy,
                                boolean hovered, boolean isPlay) {
        Graphics2D ig = (Graphics2D) g.create();
        ig.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color iconCol = isPlay ? new Color(90, 45, 0, 220) : new Color(255, 255, 255, 220);
        ig.setColor(iconCol);
        ig.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        // All icons are drawn in a ~16×16 box centred on (cx, cy)
        int s = 8; // half-size

        switch (index) {
            case 0: { // Play — filled triangle
                int[] px = { cx - s + 2, cx - s + 2, cx + s };
                int[] py = { cy - s,     cy + s,     cy     };
                ig.fillPolygon(px, py, 3);
                break;
            }
            case 1: { // Leaderboard — 3 ascending bars
                int bw = 4, gap = 2;
                int[] heights = { 8, 12, 16 };
                int startX = cx - s;
                for (int b = 0; b < 3; b++) {
                    int bx = startX + b * (bw + gap);
                    int bh = heights[b];
                    ig.fillRoundRect(bx, cy + s - bh, bw, bh, 2, 2);
                }
                break;
            }
            case 2: { // Wardrobe — simple shirt outline
                // collar V
                ig.drawLine(cx - 3, cy - s, cx, cy - s + 4);
                ig.drawLine(cx + 3, cy - s, cx, cy - s + 4);
                // sleeves
                ig.drawLine(cx - s, cy - s + 3, cx - 3, cy - s);
                ig.drawLine(cx + s, cy - s + 3, cx + 3, cy - s);
                // sleeve bottoms
                ig.drawLine(cx - s, cy - s + 3, cx - s, cy - 1);
                ig.drawLine(cx + s, cy - s + 3, cx + s, cy - 1);
                // body sides
                ig.drawLine(cx - s, cy - 1, cx - s + 2, cy - 1);
                ig.drawLine(cx + s, cy - 1, cx + s - 2, cy - 1);
                ig.drawLine(cx - s + 2, cy - 1, cx - s + 2, cy + s);
                ig.drawLine(cx + s - 2, cy - 1, cx + s - 2, cy + s);
                // bottom hem
                ig.drawLine(cx - s + 2, cy + s, cx + s - 2, cy + s);
                break;
            }
            case 3: { // Farm Upgrade — up arrow
                // arrow head
                ig.drawLine(cx, cy - s, cx - s + 2, cy - 1);
                ig.drawLine(cx, cy - s, cx + s - 2, cy - 1);
                // shaft
                ig.drawLine(cx, cy - s, cx, cy + s - 2);
                // base line
                ig.drawLine(cx - s, cy + s, cx + s, cy + s);
                break;
            }
            case 4: { // Achievements — 5-pointed star
                double[] starX = new double[10];
                double[] starY = new double[10];
                for (int p = 0; p < 10; p++) {
                    double angle = Math.toRadians(-90 + p * 36);
                    double r2 = (p % 2 == 0) ? s : s * 0.42;
                    starX[p] = cx + r2 * Math.cos(angle);
                    starY[p] = cy + r2 * Math.sin(angle);
                }
                int[] spx = new int[10], spy = new int[10];
                for (int p = 0; p < 10; p++) { spx[p] = (int)starX[p]; spy[p] = (int)starY[p]; }
                ig.fillPolygon(spx, spy, 10);
                break;
            }
            case 5: { // Stats — simple line chart with dots
                int[] px = { cx - s, cx - 2, cx + 2, cx + s };
                int[] py = { cy + 3,  cy - 3, cy + 1, cy - s };
                ig.drawPolyline(px, py, 4);
                for (int p = 0; p < 4; p++) {
                    ig.fillOval(px[p] - 2, py[p] - 2, 4, 4);
                }
                // x-axis baseline
                ig.setColor(new Color(iconCol.getRed(), iconCol.getGreen(), iconCol.getBlue(), 100));
                ig.drawLine(cx - s, cy + s, cx + s, cy + s);
                break;
            }
        }

        ig.dispose();
    }

    // ── settings gear icon ────────────────────────────────────────────────────

    private void drawSettingsGear(Graphics2D g, Rectangle rect, boolean hover) {
        Graphics2D gg = (Graphics2D) g.create();
        gg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        gg.setColor(hover ? new Color(70, 140, 40, 210) : new Color(0, 0, 0, 160));
        gg.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);
        gg.setColor(new Color(150, 220, 100, hover ? 200 : 100));
        gg.setStroke(new BasicStroke(1.5f));
        gg.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 10, 10);

        int cx = rect.x + rect.width  / 2;
        int cy = rect.y + rect.height / 2;
        gg.translate(cx, cy);

        gg.setColor(hover ? new Color(255, 255, 180) : new Color(200, 240, 160));
        gg.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        int outerR = 11, innerR = 7, teeth = 8;
        for (int i = 0; i < teeth; i++) {
            double a0 = Math.toRadians(i * 360.0 / teeth - 14);
            double a1 = Math.toRadians(i * 360.0 / teeth + 14);
            int ix0 = (int)(innerR * Math.cos(a0)), iy0 = (int)(innerR * Math.sin(a0));
            int ix1 = (int)(innerR * Math.cos(a1)), iy1 = (int)(innerR * Math.sin(a1));
            int ox0 = (int)(outerR * Math.cos(a0)), oy0 = (int)(outerR * Math.sin(a0));
            int ox1 = (int)(outerR * Math.cos(a1)), oy1 = (int)(outerR * Math.sin(a1));
            gg.drawLine(ix0, iy0, ox0, oy0);
            gg.drawLine(ix1, iy1, ox1, oy1);
            gg.drawLine(ox0, oy0, ox1, oy1);
        }
        gg.drawOval(-innerR, -innerR, innerR * 2, innerR * 2);
        gg.fillOval(-3, -3, 6, 6);

        gg.dispose();
    }

    // ── bottom bar ────────────────────────────────────────────────────────────

    private void drawBottomBar(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRect(0, GamePanel.H - 36, GamePanel.W, 36);

        g.setFont(FontManager.getBodyBold(13));
        g.setColor(ColorPalette.TEXT_COINS);
        g.drawString("Coins: " + panel.getPlayerData().getTotalCoins() + " coins",
                14, GamePanel.H - 12);

        String acc = "Account: " + (panel.getAccountManager().getActiveAccountName() != null
                ? panel.getAccountManager().getActiveAccountName() : "Guest");
        g.setFont(FontManager.getBody(12));
        g.setColor(new Color(180, 220, 150));
        g.drawString(acc, GamePanel.W - 200, GamePanel.H - 12);

        g.setColor(new Color(120, 200, 100));
        g.drawString("[ Switch Account ]", GamePanel.W / 2 - 60, GamePanel.H - 12);
    }

    // ── input ─────────────────────────────────────────────────────────────────

    @Override
    public void onMouseMoved(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        hovered = -1;
        if (exitBtn     != null && exitBtn.contains(mx, my))     { hovered = 9998; return; }
        if (settingsBtn != null && settingsBtn.contains(mx, my)) { hovered = 9999; return; }
        if (buttons == null) return;
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] != null && buttons[i].contains(mx, my)) { hovered = i; return; }
        }
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        if (exitBtn     != null && exitBtn.contains(mx, my))     { System.exit(0); return; }
        if (settingsBtn != null && settingsBtn.contains(mx, my)) {
            panel.switchToWithFade(GameScreenType.SETTINGS); return;
        }
        if (my > GamePanel.H - 36) {
            if (mx > GamePanel.W / 2 - 70 && mx < GamePanel.W / 2 + 90)
                panel.switchToWithFade(GameScreenType.ACCOUNT_SELECT);
            return;
        }
        if (buttons == null) return;
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i] != null && buttons[i].contains(mx, my)) {
                switch (i) {
                    case 0: panel.switchToWithFade(GameScreenType.CHARACTER_CREATION); break;
                    case 1: panel.switchToWithFade(GameScreenType.LEADERBOARD);        break;
                    case 2: panel.switchToWithFade(GameScreenType.WARDROBE);           break;
                    case 3: panel.switchToWithFade(GameScreenType.FARM_UPGRADE);       break;
                    case 4: panel.switchToWithFade(GameScreenType.ACHIEVEMENTS);       break;
                    case 5: panel.switchToWithFade(GameScreenType.STATS);              break;
                }
                return;
            }
        }
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) { System.exit(0); }
    }
}