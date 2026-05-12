package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.models.GameState;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;

public class GameOverScreen extends Screen {
    private GameState     lastState;
    private StringBuilder nameInput = new StringBuilder();
    private Rectangle[]   buttons;
    private int           hovered   = -1;

    public GameOverScreen(GamePanel panel) { super(panel); }

    @Override
    public void onEnter() {
        super.onEnter();
        GameScreen gs = panel.getScreenManager().getGameScreen();
        if (gs != null && gs.getState() != null) {
            lastState = gs.getState();
            nameInput.setLength(0);
            String pn = gs.getPlayerName();
            nameInput.append(pn != null && !pn.isEmpty() ? pn : "Farmer");

            // Save best score and player data immediately on game over
            panel.getPlayerData().getProfile()
                    .updateBestScore(lastState.getScore());
            panel.getPlayerData().getProfile()
                    .updateBestCombo(lastState.getHighestCombo());
            panel.getPlayerData().getProfile()
                    .incrementGamesPlayed();
            panel.getPlayerData().getProfile()
                    .addBallsCaught(lastState.getBallsCaughtThisGame());
            panel.getPlayerData().save();
        }
    }

    @Override public void update() { tickCount++; }

    @Override
    public void draw(Graphics2D g) {
        g.setPaint(new GradientPaint(0,0,new Color(15,35,10),0,GamePanel.H,new Color(6,20,4)));
        g.fillRect(0,0,GamePanel.W,GamePanel.H); g.setPaint(null);

        int pw=500, ph=490, px=(GamePanel.W-pw)/2, py=30;
        RenderUtils.drawGradientPanel(g,px,py,pw,ph,
                new Color(22,52,16,245),new Color(12,34,8,245),
                new Color(100,210,70),2.5f,20);

        RenderUtils.drawCenteredText(g,"\uD83C\uDF3E Game Over!",
                px+pw/2,py+52,FontManager.getBold(34),ColorPalette.TEXT_GOLD);

        // Show all-time best score for comparison
        int allTimeBest = panel.getPlayerData().getProfile().getAllTimeBestScore();
        boolean isNewBest = lastState != null
                && lastState.getScore() >= allTimeBest
                && lastState.getScore() > 0;
        if (isNewBest) {
            RenderUtils.drawCenteredText(g,"\uD83C\uDF1F NEW BEST SCORE!",
                    px+pw/2,py+76,FontManager.getBold(15),new Color(255,215,50));
        } else {
            RenderUtils.drawCenteredText(g,"Best: "+allTimeBest,
                    px+pw/2,py+76,FontManager.getBodyBold(13),new Color(180,220,150));
        }

        if (lastState != null) {
            int sy = py + 95;
            drawStat(g,px+30,sy,"Final Score",  ""+lastState.getScore());        sy+=34;
            drawStat(g,px+30,sy,"Level Reached",""+lastState.getLevel());         sy+=34;
            drawStat(g,px+30,sy,"Difficulty",   lastState.getDifficulty().getDisplayName()); sy+=34;
            drawStat(g,px+30,sy,"Best Combo",   "x"+lastState.getHighestCombo()); sy+=34;
            drawStat(g,px+30,sy,"Lives Left",   ""+lastState.getLives());         sy+=34;
            drawStat(g,px+30,sy,"Coins Earned", "+"+lastState.getCoinsThisGame()
                    +" \uD83E\uDE99");                                             sy+=34;
            drawStat(g,px+30,sy,"Balls Caught", ""+lastState.getBallsCaughtThisGame()); sy+=44;

            g.setFont(FontManager.getBodyBold(13));
            g.setColor(new Color(200,230,160));
            g.drawString("Enter name for leaderboard:", px+30, sy+8); sy+=24;

            RenderUtils.drawRoundPanel(g,px+30,sy,pw-60,38,
                    new Color(36,82,26),new Color(100,205,80),2f,8);
            g.setFont(FontManager.getBody(16));
            g.setColor(Color.WHITE);
            g.drawString(nameInput+(tickCount%60<30?"|":""), px+42, sy+25); sy+=50;

            buttons = new Rectangle[3];
            buttons[0] = new Rectangle(px+18,  sy, 135, 46);
            buttons[1] = new Rectangle(px+168, sy, 152, 46);
            buttons[2] = new Rectangle(px+336, sy, 130, 46);

            RenderUtils.drawButton(g,buttons[0],"\uD83D\uDD04 Retry",
                    hovered==0, FontManager.getBold(14));
            RenderUtils.drawButton(g,buttons[1],"\uD83C\uDFC6 Save Score",
                    hovered==1, FontManager.getBold(14));
            RenderUtils.drawButton(g,buttons[2],"\uD83C\uDFE0 Menu",
                    hovered==2, FontManager.getBold(14));
        }
    }

    private void drawStat(Graphics2D g, int x, int y, String label, String val) {
        g.setFont(FontManager.getBody(15));
        g.setColor(new Color(155,198,135));
        g.drawString(label+":", x, y);
        g.setFont(FontManager.getBodyBold(15));
        g.setColor(Color.WHITE);
        g.drawString(val, x+200, y);
    }

    @Override
    public void onKeyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (c >= 32 && c < 127 && nameInput.length() < 16) nameInput.append(c);
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && nameInput.length() > 0)
            nameInput.deleteCharAt(nameInput.length()-1);
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        hovered = -1;
        if (buttons == null) return;
        for (int i = 0; i < buttons.length; i++)
            if (buttons[i].contains(e.getX(), e.getY())) { hovered = i; break; }
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (buttons == null) return;

        if (buttons[0].contains(e.getX(), e.getY())) {
            panel.switchToWithFade(GameScreenType.GAME);
            return;
        }

        if (buttons[1].contains(e.getX(), e.getY())
                && lastState != null
                && !nameInput.toString().trim().isEmpty()) {
            // Save best score again just in case
            panel.getPlayerData().getProfile()
                    .updateBestScore(lastState.getScore());
            panel.getPlayerData().save();
            // Add to leaderboard
            panel.getScoreManager().addScore(
                    nameInput.toString().trim(),
                    lastState.getScore(),
                    lastState.getLevel(),
                    lastState.getDifficulty().getDisplayName());
            panel.switchToWithFade(GameScreenType.LEADERBOARD);
            return;
        }

        if (buttons[2].contains(e.getX(), e.getY())) {
            panel.switchToWithFade(GameScreenType.MAIN_MENU);
        }
    }
}