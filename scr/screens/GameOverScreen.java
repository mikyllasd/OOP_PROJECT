package screens;

import core.Screen;
import enums.GameScreenType;
import managers.GamePanel;
import models.GameState;
import utils.*;
import java.awt.*;
import java.awt.event.*;

public class GameOverScreen extends Screen {

    private GameState lastState;
    private StringBuilder nameInput = new StringBuilder();
    private Rectangle[] buttons;
    private int hovered = -1;

    public GameOverScreen(GamePanel panel) {
        super(panel);
    }

    public void setLastState(GameState state,
                              String defaultName) {
        lastState = state;
        nameInput.setLength(0);
        nameInput.append(defaultName);
    }

    @Override
    public void onEnter() {
        super.onEnter();
        GameScreen gs = panel.getScreenManager()
                             .getGameScreen();
        if (gs != null && gs.getState() != null) {
            lastState = gs.getState();
            nameInput.setLength(0);
            nameInput.append("Farmer");
        }
        panel.getSoundManager().playGameOver();
    }

    @Override
    public void update() { tickCount++; }

    @Override
    public void draw(Graphics2D g) {
        if (lastState != null)
            BackgroundRenderer.drawSky(g,
                    lastState.getLevel(), tickCount,
                    GamePanel.ARENA_W, GamePanel.H,
                    null, null);

        g.setColor(new Color(0, 0, 0, 185));
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);

        int pw = 500, ph = 460;
        int px = (GamePanel.W - pw) / 2;
        int py = 50;

        RenderUtils.drawGradientPanel(g, px, py, pw, ph,
                new Color(24, 54, 18, 245),
                new Color(14, 36, 10, 245),
                new Color(100, 205, 70), 2.5f, 20);

        RenderUtils.drawCenteredText(g,
                "\uD83C\uDF3E Game Over!",
                px + pw / 2, py + 58,
                FontManager.getBold(34),
                ColorPalette.TEXT_GOLD);

        if (lastState != null) {
            int sy = py + 98;
            drawStat(g, px + 30, sy,
                    "Final Score",
                    "" + lastState.getScore());
            sy += 34;
            drawStat(g, px + 30, sy,
                    "Level Reached",
                    "" + lastState.getLevel());
            sy += 34;
            drawStat(g, px + 30, sy,
                    "Best Combo",
                    "" + lastState.getHighestCombo());
            sy += 34;
            drawStat(g, px + 30, sy,
                    "Coins Earned",
                    "+" + lastState.getCoinsThisGame()
                    + " \uD83E\uDE99");
            sy += 44;

            g.setFont(FontManager.getBodyBold(13));
            g.setColor(new Color(200, 230, 160));
            g.drawString("Enter name for leaderboard:",
                    px + 30, sy + 8);
            sy += 24;
            RenderUtils.drawRoundPanel(g,
                    px + 30, sy, pw - 60, 38,
                    new Color(38, 88, 28),
                    new Color(100, 205, 80), 2f, 8);
            g.setFont(FontManager.getBody(16));
            g.setColor(Color.WHITE);
            String cursor = tickCount % 60 < 30 ? "|" : "";
            g.drawString(nameInput.toString() + cursor,
                    px + 42, sy + 25);
            sy += 52;

            buttons    = new Rectangle[3];
            buttons[0] = new Rectangle(px + 22, sy, 140, 46);
            buttons[1] = new Rectangle(px + 182, sy, 145, 46);
            buttons[2] = new Rectangle(px + 348, sy, 130, 46);

            RenderUtils.drawButton(g, buttons[0],
                    "\uD83D\uDD04 Retry",
                    hovered == 0, FontManager.getBold(14));
            RenderUtils.drawButton(g, buttons[1],
                    "\uD83C\uDFC6 Save Score",
                    hovered == 1, FontManager.getBold(14));
            RenderUtils.drawButton(g, buttons[2],
                    "\uD83C\uDFE0 Menu",
                    hovered == 2, FontManager.getBold(14));
        }
    }

    private void drawStat(Graphics2D g, int x, int y,
                           String label, String val) {
        g.setFont(FontManager.getBody(15));
        g.setColor(new Color(158, 198, 138));
        g.drawString(label + ":", x, y);
        g.setFont(FontManager.getBodyBold(15));
        g.setColor(Color.WHITE);
        g.drawString(val, x + 200, y);
    }

    @Override
    public void onKeyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (c >= 32 && c < 127 && nameInput.length() < 16)
            nameInput.append(c);
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE
                && nameInput.length() > 0)
            nameInput.deleteCharAt(nameInput.length() - 1);
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
        if (buttons[0].contains(e.getX(), e.getY()))
            panel.getScreenManager()
                 .switchTo(GameScreenType.GAME);
        if (buttons[1].contains(e.getX(), e.getY())
                && !nameInput.toString().isEmpty()) {
            panel.getScoreManager().addScore(
                    nameInput.toString(),
                    lastState.getScore(),
                    lastState.getLevel(),
                    lastState.getDifficulty()
                              .getDisplayName());
            panel.getScreenManager()
                 .switchTo(GameScreenType.LEADERBOARD);
        }
        if (buttons[2].contains(e.getX(), e.getY()))
            panel.getScreenManager()
                 .switchTo(GameScreenType.MAIN_MENU);
    }
}