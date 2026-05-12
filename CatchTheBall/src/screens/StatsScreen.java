package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.models.PlayerProfile;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;

public class StatsScreen extends Screen {
    private Rectangle backBtn;
    private int hovered = -1;

    public StatsScreen(GamePanel panel) { super(panel); }

    @Override public void onEnter() { super.onEnter(); }
    @Override public void update()  { tickCount++; }

    @Override
    public void draw(Graphics2D g) {
        g.setPaint(new GradientPaint(0,0,new Color(20,45,15),0,GamePanel.H,new Color(10,28,8)));
        g.fillRect(0,0,GamePanel.W,GamePanel.H); g.setPaint(null);
        RenderUtils.drawHeaderBar(g,GamePanel.W,"\uD83D\uDCCA Player Stats");

        PlayerProfile p=panel.getPlayerData().getProfile();
        int px=40,py=100,pw=GamePanel.W-80,ph=GamePanel.H-170;
        RenderUtils.drawGradientPanel(g,px,py,pw,ph,
                new Color(22,52,16,220),new Color(12,34,8,220),new Color(88,172,62),2f,16);

        int col1=px+30, col2=px+pw/2+20, sy=py+30;
        drawStatRow(g,col1,sy,"\uD83C\uDFC5 Games Played",""+p.getTotalGamesPlayed()); sy+=32;
        drawStatRow(g,col1,sy,"\uD83C\uDFC6 Best Score",""+p.getAllTimeBestScore()); sy+=32;
        drawStatRow(g,col1,sy,"\uD83D\uDD25 Best Combo","x"+p.getAllTimeBestCombo()); sy+=32;
        drawStatRow(g,col1,sy,"\uD83C\uDF4E Balls Caught",""+p.getTotalBallsCaught()); sy+=32;
        drawStatRow(g,col1,sy,"\uD83E\uDE99 Total Earned",""+p.getTotalCoinsEarned()); sy+=32;
        drawStatRow(g,col1,sy,"\uD83D\uDED2 Total Spent",""+p.getTotalCoinsSpent());

        sy=py+30;
        String acc=panel.getAccountManager().getActiveAccountName();
        drawStatRow(g,col2,sy,"\uD83D\uDC64 Account",acc!=null?acc:"Guest"); sy+=32;
        drawStatRow(g,col2,sy,"\uD83C\uDFE1 Farm Stage",panel.getPlayerData().getFarmStage()+"/20"); sy+=32;
        String streak=p.getConsecutiveLoginDays()>0?
                "\uD83D\uDD25 "+p.getConsecutiveLoginDays()+" day streak":"No streak";
        drawStatRow(g,col2,sy,"\uD83D\uDDD3\uFE0F Login Streak",streak); sy+=32;
        drawStatRow(g,col2,sy,"\uD83C\uDF3E Farmer Name",p.getFarmerName()); sy+=32;
        drawStatRow(g,col2,sy,"\uD83D\uDC54 Skin",p.getEquippedSkin().getDisplayName()); sy+=32;
        drawStatRow(g,col2,sy,"\uD83E\uDDFA Basket",p.getEquippedBasket().getDisplayName());

        int chartY=py+230, chartH=140;
        drawScoreHistoryChart(g,px+30,chartY,pw-60,chartH,p);

        backBtn=new Rectangle(20,GamePanel.H-52,140,36);
        RenderUtils.drawButton(g,backBtn,"\u2190 Back",hovered==0,FontManager.getBold(14));
    }

    private void drawStatRow(Graphics2D g,int x,int y,String label,String val) {
        g.setFont(FontManager.getBody(13));
        g.setColor(new Color(155,195,135));
        g.drawString(label,x,y);
        g.setFont(FontManager.getBodyBold(14));
        g.setColor(Color.WHITE);
        g.drawString(val,x+220,y);
    }

    private void drawScoreHistoryChart(Graphics2D g,int x,int y,int w,int h,PlayerProfile p) {
        g.setFont(FontManager.getBodyBold(12));
        g.setColor(ColorPalette.TEXT_GOLD);
        g.drawString("Score History (last 10 games)",x,y-6);
        g.setColor(new Color(0,0,0,80));
        g.fillRoundRect(x,y,w,h,8,8);
        g.setColor(new Color(60,110,45));
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(x,y,w,h,8,8);
        g.setStroke(new BasicStroke(1f));

        int cnt=p.getScoreHistoryCount();
        if (cnt==0) {
            RenderUtils.drawCenteredText(g,"No games played yet",x+w/2,y+h/2+5,
                    FontManager.getBody(13,Font.ITALIC),new Color(130,170,120));
            return;
        }
        int[] scores=p.getLastTenScores();
        int maxScore=1;
        for (int i=0;i<cnt;i++) if (scores[i]>maxScore) maxScore=scores[i];

        int barW=Math.max(10,(w-20)/Math.max(cnt,1)-4);
        int totalUsed=(barW+4)*cnt;
        int startX=x+(w-totalUsed)/2;

        for (int i=0;i<cnt;i++) {
            float ratio=(float)scores[i]/maxScore;
            int bh2=(int)((h-30)*ratio);
            int bx=startX+i*(barW+4);
            int by=y+h-bh2-12;
            if (bh2>0) {
                GradientPaint gp=new GradientPaint(bx,by,new Color(100,220,80),bx,by+bh2,new Color(50,140,40));
                g.setPaint(gp);
                g.fillRoundRect(bx,by,barW,bh2,4,4);
                g.setPaint(null);
                g.setColor(new Color(150,240,100));
                g.setStroke(new BasicStroke(1f));
                g.drawRoundRect(bx,by,barW,bh2,4,4);
                g.setStroke(new BasicStroke(1f));
            }
            if (scores[i]>0) {
                g.setFont(FontManager.getBody(9));
                g.setColor(new Color(200,235,170));
                String sv=scores[i]>=1000?(scores[i]/1000)+"k":""+scores[i];
                FontMetrics fm=g.getFontMetrics();
                g.drawString(sv,bx+(barW-fm.stringWidth(sv))/2,by-2);
            }
            g.setFont(FontManager.getBody(9));
            g.setColor(new Color(130,170,120));
            String idx=""+(i+1);
            FontMetrics fm=g.getFontMetrics();
            g.drawString(idx,bx+(barW-fm.stringWidth(idx))/2,y+h-2);
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
        if (e.getKeyCode()==KeyEvent.VK_ESCAPE)
            panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }
}