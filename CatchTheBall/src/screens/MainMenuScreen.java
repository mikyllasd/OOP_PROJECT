package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.renderers.FarmHouseRenderer;
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

    private float[] fruitX, fruitY, fruitSpeed;
    private int[]   fruitType;
    private static final int FRUIT_COUNT = 12;

    public MainMenuScreen(GamePanel panel) {
        super(panel);
        Random rand = new Random();
        fruitX     = new float[FRUIT_COUNT];
        fruitY     = new float[FRUIT_COUNT];
        fruitSpeed = new float[FRUIT_COUNT];
        fruitType  = new int[FRUIT_COUNT];
        for (int i = 0; i < FRUIT_COUNT; i++) {
            fruitX[i]     = rand.nextFloat() * GamePanel.W;
            fruitY[i]     = rand.nextFloat() * GamePanel.H;
            fruitSpeed[i] = 0.5f + rand.nextFloat() * 1.5f;
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
            fruitY[i] += fruitSpeed[i];
            if (fruitY[i] > GamePanel.H + 30) {
                fruitY[i] = -30;
                fruitX[i] = (float)(Math.random() * GamePanel.W);
            }
        }
    }

    @Override
    public void draw(Graphics2D g) {
        drawBackground(g);
        drawFallingFruits(g);
        drawTitle(g);
        drawButtons(g);
        drawBottomBar(g);

        // Exit button — bottom right
        exitBtn = new Rectangle(GamePanel.W - 150, GamePanel.H - 50, 130, 34);
        RenderUtils.drawExitButton(g, exitBtn, hovered == 9998);
    }

    private void drawBackground(Graphics2D g) {
        g.setPaint(new GradientPaint(0, 0, new Color(18,52,12), 0, GamePanel.H, new Color(8,32,5)));
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);
        g.setPaint(null);

        // Cartoon farm house bottom right, changes per farm stage
        int stage = panel.getPlayerData().getFarmStage();
        FarmHouseRenderer.draw(g, stage, GamePanel.W - 210, GamePanel.H - 215, 195, 170);

        int off1 = (int)(tickCount * 0.3) % (GamePanel.W + 200);
        g.setColor(new Color(30, 80, 20, 100));
        for (int x = -off1; x < GamePanel.W + 200; x += 200)
            g.fillArc(x, GamePanel.H - 200, 240, 160, 0, 180);

        int off2 = (int)(tickCount * 0.15) % (GamePanel.W + 300);
        g.setColor(new Color(40, 100, 28, 80));
        for (int x = -off2; x < GamePanel.W + 300; x += 280)
            g.fillArc(x, GamePanel.H - 140, 320, 110, 0, 180);
    }

    private void drawFallingFruits(Graphics2D g) {
        // Draw simple colored cartoon fruits instead of emojis
        Color[][] fruitColors = {
            {new Color(220,40,40), new Color(255,100,100)},   // apple
            {new Color(255,140,0), new Color(255,200,80)},    // orange
            {new Color(220,20,60), new Color(255,100,120)},   // strawberry
            {new Color(255,220,0), new Color(255,255,100)}    // lemon
        };
        for (int i = 0; i < FRUIT_COUNT; i++) {
            int type = fruitType[i];
            Color main = fruitColors[type][0];
            Color light = fruitColors[type][1];
            int fx = (int)fruitX[i], fy = (int)fruitY[i];

            g.setColor(new Color(main.getRed(),main.getGreen(),main.getBlue(),60));
            g.fillOval(fx, fy, 20, 20);
            g.setColor(new Color(light.getRed(),light.getGreen(),light.getBlue(),40));
            g.fillOval(fx + 3, fy + 3, 8, 6);
        }
    }

    private void drawTitle(Graphics2D g) {
        float bobY = (float)(Math.sin(tickCount * 0.03) * 4);

        // Shadow
        g.setFont(FontManager.getBold(48));
        g.setColor(new Color(10, 40, 5, 180));
        g.drawString("Catch the Ball!", 
                (GamePanel.W - g.getFontMetrics().stringWidth("Catch the Ball!")) / 2 + 3,
                (int)(125 + bobY) + 3);

        // Main title
        g.setColor(ColorPalette.TEXT_GOLD);
        g.drawString("Catch the Harvest!",
                (GamePanel.W - g.getFontMetrics().stringWidth("Catch the Harvest!")) / 2,
                (int)(125 + bobY));

        // Subtitle
        RenderUtils.drawCenteredText(g, "A rustic farm chase with fruit, bonuses, and charm.",
                GamePanel.W / 2, 158,
                FontManager.getBody(16, Font.ITALIC),
                new Color(200, 255, 160, 200));
    }

    private void drawButtons(Graphics2D g) {
        int bw = 270, bh = 50, bx = (GamePanel.W - bw) / 2, by = 185;
        buttons = new Rectangle[LABELS.length];
        for (int i = 0; i < LABELS.length; i++) {
            buttons[i] = new Rectangle(bx, by + i * 56, bw, bh);
            RenderUtils.drawButton(g, buttons[i], LABELS[i], i == hovered,
                    FontManager.getBold(15));
        }
    }

    private void drawBottomBar(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 120));
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