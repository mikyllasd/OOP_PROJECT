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
    private int         hovered = -1;

    private static final String[] LABELS = {
        "> Play Game",
        "Leaderboard",
        "Wardrobe",
        "Farm Upgrade",
        "Achievements",
        "Stats"
    };

    // Floating fruit particles (drawn ON TOP of the farm background)
    private float[] fruitX, fruitY, fruitSpeed, fruitRot;
    private int[]   fruitType;
    private static final int FRUIT_COUNT = 10;

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
                fruitY[i]  = -30;
                fruitX[i]  = (float)(Math.random() * GamePanel.W);
                fruitRot[i] = (float)(Math.random() * 360);
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        // ── 1. Animated farm background ──────────────────────────────────────
        MenuBackgroundRenderer.draw(g, tickCount, GamePanel.W, GamePanel.H);

        // ── 2. Falling fruit particles (translucent, above background) ───────
        drawFallingFruits(g);

        // ── 3. Dark vignette so UI pops against the busy background ──────────
        drawVignette(g);

        // ── 4. UI layer ───────────────────────────────────────────────────────
        drawTitle(g);
        drawButtons(g);
        drawBottomBar(g);

        // ── 5. Exit button ────────────────────────────────────────────────────
        exitBtn = new Rectangle(GamePanel.W - 150, GamePanel.H - 50, 130, 34);
        RenderUtils.drawExitButton(g, exitBtn, hovered == 9998);
    }

    // ── vignette ──────────────────────────────────────────────────────────────

    /**
     * Soft dark oval that darkens screen edges, helping the centred UI stand
     * out from the colourful farm scene behind it.
     */
    private void drawVignette(Graphics2D g) {
        int w = GamePanel.W, h = GamePanel.H;

        // left edge fade
        GradientPaint left = new GradientPaint(
                0, 0, new Color(0, 0, 0, 145),
                w / 5f, 0, new Color(0, 0, 0, 0));
        g.setPaint(left);
        g.fillRect(0, 0, w / 5, h);

        // right edge fade
        GradientPaint right = new GradientPaint(
                w * 4f / 5, 0, new Color(0, 0, 0, 0),
                w, 0, new Color(0, 0, 0, 145));
        g.setPaint(right);
        g.fillRect(w * 4 / 5, 0, w / 5, h);

        // top fade (helps title area)
        GradientPaint top = new GradientPaint(
                0, 0, new Color(0, 0, 0, 110),
                0, h / 4f, new Color(0, 0, 0, 0));
        g.setPaint(top);
        g.fillRect(0, 0, w, h / 4);

        // bottom fade (ground area behind bottom bar)
        GradientPaint bottom = new GradientPaint(
                0, h * 3f / 4, new Color(0, 0, 0, 0),
                0, h, new Color(0, 0, 0, 140));
        g.setPaint(bottom);
        g.fillRect(0, h * 3 / 4, w, h / 4);

        g.setPaint(null);
    }

    // ── falling fruits ────────────────────────────────────────────────────────

    private void drawFallingFruits(Graphics2D g) {
        // Colours: [main, highlight]
        Color[][] fruitColors = {
            {new Color(220, 40,  40),  new Color(255, 110, 110)},  // apple
            {new Color(255, 140,  0),  new Color(255, 210,  80)},  // orange
            {new Color(220, 20,  60),  new Color(255, 100, 120)},  // strawberry
            {new Color(230, 210,  0),  new Color(255, 252, 120)}   // lemon
        };
        for (int i = 0; i < FRUIT_COUNT; i++) {
            int type = fruitType[i];
            Color main  = fruitColors[type][0];
            Color light = fruitColors[type][1];
            int fx = (int)fruitX[i], fy = (int)fruitY[i];

            Graphics2D fg = (Graphics2D) g.create();
            fg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // rotate around fruit centre
            fg.rotate(Math.toRadians(fruitRot[i]), fx + 11, fy + 11);

            // body
            fg.setColor(new Color(main.getRed(), main.getGreen(), main.getBlue(), 55));
            fg.fillOval(fx, fy, 22, 22);
            // highlight
            fg.setColor(new Color(light.getRed(), light.getGreen(), light.getBlue(), 35));
            fg.fillOval(fx + 3, fy + 3, 9, 7);
            // stem
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

        // decorative backdrop panel behind title so it reads against any sky colour
        int tw = 540, th = 88;
        int tx = (GamePanel.W - tw) / 2;
        int ty = (int)(55 + bobY);
        g.setColor(new Color(0, 0, 0, 110));
        g.fillRoundRect(tx, ty, tw, th, 22, 22);
        g.setColor(new Color(120, 210, 70, 60));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(tx, ty, tw, th, 22, 22);
        g.setStroke(new BasicStroke(1f));

        // drop shadow
        g.setFont(FontManager.getBold(44));
        FontMetrics fm = g.getFontMetrics();
        String title = "Catch the Harvest!";
        int titleX = (GamePanel.W - fm.stringWidth(title)) / 2;
        int titleY = (int)(117 + bobY);
        g.setColor(new Color(5, 25, 2, 200));
        g.drawString(title, titleX + 3, titleY + 3);

        // main title – gold
        g.setColor(ColorPalette.TEXT_GOLD);
        g.drawString(title, titleX, titleY);

        // subtitle
        RenderUtils.drawCenteredText(g, "A rustic farm chase with fruit, bonuses, and charm.",
                GamePanel.W / 2, (int)(148 + bobY),
                FontManager.getBody(15, Font.ITALIC),
                new Color(210, 255, 170, 215));
    }

    // ── buttons ───────────────────────────────────────────────────────────────

    private void drawButtons(Graphics2D g) {
        int bw = 270, bh = 50, bx = (GamePanel.W - bw) / 2, by = 185;
        buttons = new Rectangle[LABELS.length];
        for (int i = 0; i < LABELS.length; i++) {
            buttons[i] = new Rectangle(bx, by + i * 56, bw, bh);
            RenderUtils.drawButton(g, buttons[i], LABELS[i], i == hovered,
                    FontManager.getBold(15));
        }
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
        if (exitBtn != null && exitBtn.contains(mx, my)) { hovered = 9998; return; }
        if (buttons == null) return;
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].contains(mx, my)) { hovered = i; return; }
        }
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        if (exitBtn != null && exitBtn.contains(mx, my)) { System.exit(0); return; }
        if (my > GamePanel.H - 36) {
            if (mx > GamePanel.W / 2 - 70 && mx < GamePanel.W / 2 + 90)
                panel.switchToWithFade(GameScreenType.ACCOUNT_SELECT);
            return;
        }
        if (buttons == null) return;
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].contains(mx, my)) {
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