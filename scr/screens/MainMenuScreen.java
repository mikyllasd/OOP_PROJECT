package screens;

import core.Screen;
import enums.GameScreenType;
import managers.GamePanel;
import utils.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenuScreen extends Screen {

    private Rectangle[] buttons;
    private int hovered = -1;

    private static final String[] LABELS = {
        "\u25B6  Play Game",
        "\uD83C\uDFC6  Leaderboard",
        "\uD83E\uDDFA  Wardrobe",
        "\uD83C\uDFE1  Farm Upgrade",
        "\uD83C\uDFC5  Achievements"
    };

    public MainMenuScreen(GamePanel panel) {
        super(panel);
    }

    @Override
    public void onEnter() {
        super.onEnter();
        buttons = null;
        hovered = -1;
    }

    @Override
    public void update() {
        tickCount++;
    }

    @Override
    public void draw(Graphics2D g) {
        drawBackground(g);
        drawTitle(g);
        drawButtons(g);
        drawBottomBar(g);
    }

    private void drawBackground(Graphics2D g) {
        BackgroundRenderer.drawSky(g, 2,
                tickCount, GamePanel.ARENA_W,
                GamePanel.H, null, null);
        g.setColor(new Color(0, 0, 0, 70));
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);
    }

    private void drawTitle(Graphics2D g) {
        float shimmer = (float)(
                (Math.sin(tickCount * 0.04) + 1) / 2.0);
        g.setColor(new Color(255, 240, 100,
                (int)(shimmer * 30)));
        g.fillRect(0, 80, GamePanel.W, 80);

        String title = "\uD83C\uDF3E Catch the Ball!";
        Font tf = FontManager.getBold(52);
        RenderUtils.drawShadowText(g, title,
                (GamePanel.W - g.getFontMetrics(tf)
                        .stringWidth(title)) / 2,
                130, tf,
                ColorPalette.TEXT_GOLD,
                new Color(20, 60, 10, 180));

        RenderUtils.drawCenteredText(g,
                "A farm-fresh catching adventure!",
                GamePanel.W / 2, 165,
                FontManager.getBody(18, Font.ITALIC),
                new Color(210, 255, 170));
    }

    private void drawButtons(Graphics2D g) {
        int bw = 270, bh = 50;
        int bx = (GamePanel.W - bw) / 2;
        int by = 205;
        buttons = new Rectangle[LABELS.length];
        for (int i = 0; i < LABELS.length; i++) {
            buttons[i] = new Rectangle(
                    bx, by + i * 62, bw, bh);
            RenderUtils.drawButton(g, buttons[i],
                    LABELS[i],
                    i == hovered,
                    FontManager.getBold(16));
        }
    }

    private void drawBottomBar(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, GamePanel.H - 40,
                GamePanel.W, 40);
        g.setFont(FontManager.getBodyBold(14));
        g.setColor(ColorPalette.TEXT_COINS);
        g.drawString("\uD83E\uDE99 "
                + panel.getPlayerData().getTotalCoins()
                + " coins",
                20, GamePanel.H - 14);
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        hovered = -1;
        if (buttons == null) return;
        for (int i = 0; i < buttons.length; i++)
            if (buttons[i].contains(e.getX(), e.getY()))
            { hovered = i; break; }
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (buttons == null) return;
        for (int i = 0; i < buttons.length; i++) {
            if (buttons[i].contains(e.getX(), e.getY())) {
                switch (i) {
                    case 0: panel.getScreenManager()
                            .switchTo(GameScreenType
                                    .CHARACTER_CREATION); break;
                    case 1: panel.getScreenManager()
                            .switchTo(GameScreenType
                                    .LEADERBOARD); break;
                    case 2: panel.getScreenManager()
                            .switchTo(GameScreenType
                                    .WARDROBE); break;
                    case 3: panel.getScreenManager()
                            .switchTo(GameScreenType
                                    .FARM_UPGRADE); break;
                    case 4: panel.getScreenManager()
                            .switchTo(GameScreenType
                                    .ACHIEVEMENTS); break;
                }
            }
        }
    }
}