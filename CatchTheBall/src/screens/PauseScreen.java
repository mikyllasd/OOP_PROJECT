package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;

public class PauseScreen extends Screen {
    private Rectangle resumeBtn, restartBtn, menuBtn;
    private int hovered = -1;

    public PauseScreen(GamePanel panel) { super(panel); }

    @Override public void update() { tickCount++; }

    @Override
    public void draw(Graphics2D g) {
        GameScreen gs = panel.getScreenManager().getGameScreen();
        if (gs != null) gs.draw(g);
        g.setColor(new Color(0,0,0,170)); g.fillRect(0,0,GamePanel.W,GamePanel.H);
        int pw=420,ph=320,px=(GamePanel.W-pw)/2,py=(GamePanel.H-ph)/2;
        RenderUtils.drawGradientPanel(g,px,py,pw,ph,
                new Color(28,60,22,245),new Color(16,40,12,245),new Color(100,190,70),2f,20);
        RenderUtils.drawCenteredText(g,"\u23F8 PAUSED",px+pw/2,py+55,FontManager.getBold(32),ColorPalette.TEXT_GOLD);
        if (gs!=null&&gs.getState()!=null) {
            var st=gs.getState();
            RenderUtils.drawCenteredText(g,"Score: "+st.getScore(),px+pw/2,py+95,FontManager.getBodyBold(16),ColorPalette.TEXT_SCORE);
            RenderUtils.drawCenteredText(g,"Level: "+st.getLevel()+"   Combo: "+st.getCombo(),
                    px+pw/2,py+118,FontManager.getBodyBold(14),new Color(200,240,160));
        }
        resumeBtn  = new Rectangle(px+20,py+188,118,44);
        restartBtn = new Rectangle(px+154,py+188,118,44);
        menuBtn    = new Rectangle(px+286,py+188,118,44);
        RenderUtils.drawButton(g,resumeBtn, "\u25B6 Resume",   hovered==0, FontManager.getBold(13));
        RenderUtils.drawButton(g,restartBtn,"\uD83D\uDD04 Restart",hovered==1,FontManager.getBold(13));
        RenderUtils.drawButton(g,menuBtn,   "\uD83C\uDFE0 Menu",   hovered==2, FontManager.getBold(13));
        RenderUtils.drawCenteredText(g,"P = resume   ESC = menu",px+pw/2,py+268,
                FontManager.getBody(12),new Color(155,195,135));
    }

    @Override public void onMouseMoved(MouseEvent e) {
        hovered=-1;
        if (resumeBtn!=null&&resumeBtn.contains(e.getX(),e.getY()))   hovered=0;
        if (restartBtn!=null&&restartBtn.contains(e.getX(),e.getY())) hovered=1;
        if (menuBtn!=null&&menuBtn.contains(e.getX(),e.getY()))       hovered=2;
    }

    @Override public void onMouseClicked(MouseEvent e) {
        if (resumeBtn!=null&&resumeBtn.contains(e.getX(),e.getY()))   panel.getScreenManager().switchTo(GameScreenType.GAME);
        if (restartBtn!=null&&restartBtn.contains(e.getX(),e.getY())) panel.switchToWithFade(GameScreenType.GAME);
        if (menuBtn!=null&&menuBtn.contains(e.getX(),e.getY()))       panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }

    @Override public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_P)           panel.getScreenManager().switchTo(GameScreenType.GAME);
        else if (e.getKeyCode()==KeyEvent.VK_ESCAPE) panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }
}