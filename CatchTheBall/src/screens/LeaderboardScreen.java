package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.Difficulty;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.models.ScoreEntry;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class LeaderboardScreen extends Screen {
    private Rectangle backBtn;
    private Rectangle refreshBtn;
    private Rectangle[] filterBtns;
    private int    hovered      = -1;
    private int    selectedFilter = 0;
    private int    scrollOffset = 0;
    private String statusMsg    = "";
    private int    statusTimer  = 0;

    private static final String[] RANKS = {
        "\uD83D\uDC51","\uD83E\uDD48","\uD83E\uDD49",
        "4\uFE0F\u20E3","5\uFE0F\u20E3","6\uFE0F\u20E3",
        "7\uFE0F\u20E3","8\uFE0F\u20E3","9\uFE0F\u20E3",
        "\uD83D\uDD1F","11","12","13","14","15","16","17","18","19","20"
    };

    public LeaderboardScreen(GamePanel panel) { super(panel); }

    @Override
    public void onEnter() { super.onEnter(); scrollOffset=0; }

    @Override public void update() { tickCount++; if(statusTimer>0) statusTimer--; }

    @Override
    public void draw(Graphics2D g) {
        g.setPaint(new GradientPaint(0,0,new Color(18,42,13),0,GamePanel.H,new Color(8,28,6)));
        g.fillRect(0,0,GamePanel.W,GamePanel.H); g.setPaint(null);
        RenderUtils.drawHeaderBar(g,GamePanel.W,"\uD83C\uDFC6 Leaderboard");

        String[] filterLabels = {"All","Easy","Normal","Hard"};
        filterBtns = new Rectangle[filterLabels.length];
        int fx = 24;
        for (int i = 0; i < filterLabels.length; i++) {
            filterBtns[i] = new Rectangle(fx, 78, 90, 30);
            RenderUtils.drawButton(g, filterBtns[i], filterLabels[i], selectedFilter == i,
                    FontManager.getBodyBold(13));
            fx += 100;
        }

        List<ScoreEntry> top = selectedFilter == 0 ? panel.getScoreManager().getAll()
                : panel.getScoreManager().getTopByDifficulty(filterLabels[selectedFilter]);
        if (top.isEmpty()) {
            RenderUtils.drawCenteredText(g,"No scores yet for " + filterLabels[selectedFilter] + ".",
                    GamePanel.W/2,300,FontManager.getBody(17,Font.ITALIC),new Color(155,200,135));
        }

        int rowH=44, startY=105;
        Shape oldClip=g.getClip();
        g.setClip(0,100,GamePanel.W,GamePanel.H-150);
        for (int i=0;i<top.size();i++) {
            ScoreEntry e=top.get(i);
            int ry=startY+i*rowH-scrollOffset;
            if (ry+rowH<100||ry>GamePanel.H-100) continue;
            boolean t=(i<3);
            Color rowTop=t?new Color(62,125,42,210):new Color(32,72,22,165);
            Color rowBot=t?new Color(45,100,30,210):new Color(22,52,14,165);
            Color rowBdr=t?new Color(148,235,100):new Color(78,148,58);
            RenderUtils.drawGradientPanel(g,30,ry,GamePanel.W-60,rowH-5,rowTop,rowBot,rowBdr,1f,10);
            String rank=i<RANKS.length?RANKS[i]:String.valueOf(i+1);
            g.setFont(FontManager.getEmoji(18)); g.setColor(Color.WHITE);
            g.drawString(rank,44,ry+26);
            g.setFont(FontManager.getBold(14));
            g.setColor(t?new Color(255,232,100):Color.WHITE);
            g.drawString(e.getName(),95,ry+26);
            g.setFont(FontManager.getBodyBold(14));
            g.setColor(new Color(100,225,100));
            g.drawString(""+e.getScore(),370,ry+26);
            g.setFont(FontManager.getBody(12));
            g.setColor(new Color(155,200,135));
            g.drawString("Lv."+e.getLevel(),510,ry+26);
            g.setColor(new Color(120,170,120));
            g.drawString(e.getDifficulty(),570,ry+26);
            if (!e.getDate().isEmpty()) {
                g.setColor(new Color(110,155,105));
                g.drawString(e.getDate(),680,ry+26);
            }
        }
        g.setClip(oldClip);

        backBtn=new Rectangle(20,GamePanel.H-52,140,36);
        refreshBtn=new Rectangle(GamePanel.W-170,GamePanel.H-52,145,36);
        RenderUtils.drawButton(g,backBtn,"\u2190 Back",hovered==0,FontManager.getBold(14));
        RenderUtils.drawButton(g,refreshBtn,"\uD83D\uDD04 Refresh",hovered==1,FontManager.getBold(13));
        if (statusTimer>0)
            RenderUtils.drawCenteredText(g,statusMsg,GamePanel.W/2,GamePanel.H-60,
                    FontManager.getBodyBold(12),new Color(150,220,150));
        g.setFont(FontManager.getBody(11));
        g.setColor(new Color(130,170,120));
        g.drawString("Showing "+top.size()+" entries",GamePanel.W/2-50,GamePanel.H-16);
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        hovered=-1;
        if (backBtn!=null&&backBtn.contains(e.getX(),e.getY()))       hovered=0;
        if (refreshBtn!=null&&refreshBtn.contains(e.getX(),e.getY())) hovered=1;
        if (filterBtns != null) {
            for (int i=0; i<filterBtns.length; i++)
                if (filterBtns[i].contains(e.getX(),e.getY())) { hovered=2+i; break; }
        }
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (backBtn!=null&&backBtn.contains(e.getX(),e.getY()))
            panel.switchToWithFade(GameScreenType.MAIN_MENU);
        if (refreshBtn!=null&&refreshBtn.contains(e.getX(),e.getY())) {
            panel.getScoreManager().load();
            statusMsg="Refreshed!"; statusTimer=100;
        }
        if (filterBtns != null) {
            for (int i=0; i<filterBtns.length; i++) {
                if (filterBtns[i].contains(e.getX(), e.getY())) {
                    selectedFilter = i;
                    scrollOffset = 0;
                    return;
                }
            }
        }
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_ESCAPE) panel.switchToWithFade(GameScreenType.MAIN_MENU);
        if (e.getKeyCode()==KeyEvent.VK_DOWN) scrollOffset=Math.min(scrollOffset+30,400);
        if (e.getKeyCode()==KeyEvent.VK_UP)   scrollOffset=Math.max(scrollOffset-30,0);
    }
}