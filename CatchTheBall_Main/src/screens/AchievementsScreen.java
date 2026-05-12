package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.models.Achievement;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AchievementsScreen extends Screen {
    private Rectangle backBtn;
    private int hovered      = -1;
    private int scrollOffset = 0;

    public AchievementsScreen(GamePanel panel) { super(panel); }

    @Override
    public void onEnter() {
        super.onEnter();
        scrollOffset=0;
        panel.getAchievementManager().loadFromString(panel.getPlayerData().getAchievementData());
    }

    @Override public void update() { tickCount++; }

    @Override
    public void draw(Graphics2D g) {
        g.setPaint(new GradientPaint(0,0,new Color(22,43,18),0,GamePanel.H,new Color(12,28,10)));
        g.fillRect(0,0,GamePanel.W,GamePanel.H); g.setPaint(null);
        RenderUtils.drawHeaderBar(g,GamePanel.W,"\uD83C\uDFC5 Achievements");

        List<Achievement> all=panel.getAchievementManager().getAll();
        int unlocked=(int)all.stream().filter(Achievement::isUnlocked).count();
        RenderUtils.drawCenteredText(g,unlocked+"/"+all.size()+" Unlocked",
                GamePanel.W/2,96,FontManager.getBodyBold(13),new Color(180,220,150));

        int cols=3,bw=230,bh=115,sx=40,sy=108;
        Shape oldClip=g.getClip();
        g.setClip(0,100,GamePanel.W,GamePanel.H-150);
        for (int i=0;i<all.size();i++) {
            int col=i%cols, row=i/cols;
            int bx=sx+col*(bw+20), by=sy+row*(bh+14)-scrollOffset;
            if (by+bh<100||by>GamePanel.H-100) continue;
            drawAchievementCard(g,all.get(i),bx,by,bw,bh);
        }
        g.setClip(oldClip);

        backBtn=new Rectangle(20,GamePanel.H-52,140,36);
        RenderUtils.drawButton(g,backBtn,"\u2190 Back",hovered==0,FontManager.getBold(14));
        g.setFont(FontManager.getBody(11));
        g.setColor(new Color(140,180,130));
        g.drawString("Scroll: UP/DOWN keys",GamePanel.W-220,GamePanel.H-18);
    }

    private void drawAchievementCard(Graphics2D g,Achievement a,int x,int y,int w,int h) {
        boolean u=a.isUnlocked();
        float glow=u?(float)(0.5+0.5*Math.sin(tickCount*0.08)):0f;
        RenderUtils.drawGradientPanel(g,x,y,w,h,
                u?new Color(52,105,36):new Color(28,48,22),
                u?new Color(36,80,24):new Color(20,38,16),
                u?new Color((int)(100+55*glow),220,80):new Color(58,88,48),
                u?2f:1f,12);
        if (u) {
            g.setColor(new Color(100,255,80,(int)(40*glow)));
            g.fillRoundRect(x,y,w,h,12,12);
        }
        g.setFont(FontManager.getEmoji(30));
        g.setColor(u?Color.WHITE:new Color(100,118,88));
        g.drawString(a.getEmoji(),x+10,y+42);
        g.setFont(FontManager.getBold(12));
        g.setColor(u?new Color(200,255,148):new Color(98,128,88));
        g.drawString(a.getName(),x+50,y+28);
        g.setFont(FontManager.getBody(11));
        g.setColor(u?new Color(155,212,128):new Color(78,108,68));
        String desc=a.getDescription();
        if (g.getFontMetrics().stringWidth(desc)>w-55)
            desc=desc.substring(0,Math.min(desc.length(),30))+"...";
        g.drawString(desc,x+50,y+48);
        if (!u&&a.getProgressTarget()>1) {
            RenderUtils.drawProgressBar(g,x+10,y+h-26,w-20,9,a.getProgressRatio(),
                    new Color(0,0,0,80),new Color(80,180,80),new Color(140,255,100));
            g.setFont(FontManager.getBody(10));
            g.setColor(new Color(160,200,140));
            g.drawString(a.getProgressCurrent()+"/"+a.getProgressTarget(),x+10,y+h-30);
        }
        if (u) {
            g.setFont(FontManager.getBodyBold(11));
            g.setColor(new Color(100,225,80));
            g.drawString("\u2713 UNLOCKED",x+50,y+76);
        }
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        hovered=(backBtn!=null&&backBtn.contains(e.getX(),e.getY()))?0:-1;
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (backBtn!=null&&backBtn.contains(e.getX(),e.getY()))
            panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_ESCAPE) panel.switchToWithFade(GameScreenType.MAIN_MENU);
        if (e.getKeyCode()==KeyEvent.VK_DOWN) scrollOffset=Math.min(scrollOffset+30,300);
        if (e.getKeyCode()==KeyEvent.VK_UP)   scrollOffset=Math.max(scrollOffset-30,0);
    }

    public void scroll(int amount) {
        scrollOffset=Math.max(0,Math.min(scrollOffset+amount,400));
    }
}