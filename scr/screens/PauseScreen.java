package screens;

import core.Screen;
import enums.GameScreenType;
import managers.GamePanel;
import utils.*;
import java.awt.*;
import java.awt.event.*;

public class PauseScreen extends Screen {

    private Rectangle resumeBtn;
    private Rectangle menuBtn;
    private int hovered = -1;

    public PauseScreen(GamePanel panel) {
        super(panel);
    }

    @Override
    public void update() { tickCount++; }

    @Override
    public void draw(Graphics2D g) {
        GameScreen gs = panel.getScreenManager()
                             .getGameScreen();
        if (gs != null) gs.draw(g);

        g.setColor(new Color(0, 0, 0, 165));
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);

        int pw = 400, ph = 280;
        int px = (GamePanel.W - pw) / 2;
        int py = (GamePanel.H - ph) / 2;

        RenderUtils.drawGradientPanel(g, px, py, pw, ph,
                new Color(30, 62, 25, 245),
                new Color(18, 42, 12, 245),
                new Color(100, 185, 70), 2f, 20);

        RenderUtils.drawCenteredText(g, "\u23F8 PAUSED",
                px + pw / 2, py + 55,
                FontManager.getBold(30),
                ColorPalette.TEXT_GOLD);

        resumeBtn = new Rectangle(px + 30, py + 165, 160, 44);
        menuBtn   = new Rectangle(px + 210, py + 165, 160, 44);

        RenderUtils.drawButton(g, resumeBtn, "\u25B6 Resume",
                hovered == 0, FontManager.getBold(15));
        RenderUtils.drawButton(g, menuBtn, "\uD83C\uDFE0 Menu",
                hovered == 1, FontManager.getBold(15));

        RenderUtils.drawCenteredText(g,
                "Press P to resume  \u2022  ESC for menu",
                px + pw / 2, py + 250,
                FontManager.getBody(12),
                new Color(160, 200, 140));
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        hovered = -1;
        if (resumeBtn != null
                && resumeBtn.contains(e.getX(), e.getY()))
            hovered = 0;
        if (menuBtn != null
                && menuBtn.contains(e.getX(), e.getY()))
            hovered = 1;
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (resumeBtn != null
                && resumeBtn.contains(e.getX(), e.getY()))
            panel.getScreenManager()
                 .switchTo(GameScreenType.GAME);
        if (menuBtn != null
                && menuBtn.contains(e.getX(), e.getY()))
            panel.getScreenManager()
                 .switchTo(GameScreenType.MAIN_MENU);
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P
                || e.getKeyCode() == KeyEvent.VK_ESCAPE)
            panel.getScreenManager()
                 .switchTo(e.getKeyCode() == KeyEvent.VK_P
                         ? GameScreenType.GAME
                         : GameScreenType.MAIN_MENU);
    }
}