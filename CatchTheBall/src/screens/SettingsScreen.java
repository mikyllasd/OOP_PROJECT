package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.Difficulty;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;

public class SettingsScreen extends Screen {
    private Rectangle    backBtn;
    private Rectangle    muteBtn;
    private Rectangle    muteMusicBtn;
    private Rectangle[]  diffBtns;
    private int          hovered       = -1;
    private float        sfxVolume     = 0.5f;
    private float        musicVolume   = 0.4f;
    private int          selectedDiff  = 1;
    private boolean      draggingSFX   = false;
    private boolean      draggingMusic = false;

    private static final int SLIDER_X  = 280;
    private static final int SLIDER_W  = 300;
    private static final int SFX_Y     = 160;
    private static final int MUSIC_Y   = 212;

    public SettingsScreen(GamePanel panel) { super(panel); }

    @Override
    public void onEnter() {
        super.onEnter();
        sfxVolume   = panel.getSoundManager().getVolume();
        musicVolume = panel.getMusicManager().getVolume();
    }

    @Override public void update() { tickCount++; }

    @Override
    public void draw(Graphics2D g) {
        g.setPaint(new GradientPaint(0,0,new Color(20,48,15),0,GamePanel.H,new Color(10,30,8)));
        g.fillRect(0,0,GamePanel.W,GamePanel.H); g.setPaint(null);
        RenderUtils.drawHeaderBar(g,GamePanel.W,"\u2699\uFE0F Settings");

        int panelX=GamePanel.W/2-280, panelY=90, panelW=560, panelH=450;
        RenderUtils.drawGradientPanel(g,panelX,panelY,panelW,panelH,
                new Color(24,55,18,230),new Color(14,38,10,230),new Color(90,175,65),2f,18);

        drawSectionLabel(g,"Sound & Music",panelX+20,130);
        drawSliderRow(g,"SFX Volume",panelX+20,SFX_Y,sfxVolume);
        drawSliderRow(g,"Music Volume",panelX+20,MUSIC_Y,musicVolume);

        drawSectionLabel(g,"Default Difficulty",panelX+20,274);
        diffBtns=new Rectangle[3];
        Difficulty[] diffs={Difficulty.EASY,Difficulty.NORMAL,Difficulty.HARD};
        Color[] diffColors={new Color(80,200,80),new Color(255,200,50),new Color(255,80,80)};
        for (int i=0;i<3;i++) {
            int bx=panelX+30+i*170;
            diffBtns[i]=new Rectangle(bx,290,150,40);
            boolean sel=(i==selectedDiff);
            Color c=diffColors[i];
            RenderUtils.drawGradientPanel(g,bx,290,150,40,
                    sel?c:new Color(38,78,28),
                    sel?c.darker():new Color(24,52,16),
                    sel?c.brighter():new Color(65,115,50),
                    sel?2.5f:1.2f,10);
            RenderUtils.drawCenteredText(g,diffs[i].getDisplayName(),bx+75,316,
                    FontManager.getBold(14),sel?Color.WHITE:new Color(180,220,150));
        }

        drawSectionLabel(g,"Other",panelX+20,352);
        muteBtn=new Rectangle(panelX+30,368,200,38);
        muteMusicBtn=new Rectangle(panelX+250,368,200,38);
        String sfxLabel=panel.getSoundManager().isMuted()?
                "\uD83D\uDD07 SFX: Muted":"\uD83D\uDD0A SFX: On";
        String musicLabel=panel.getMusicManager().isMuted()?
                "\uD83D\uDD07 Music: Muted":"\uD83C\uDFB5 Music: On";
        RenderUtils.drawButton(g,muteBtn,sfxLabel,hovered==10,FontManager.getBold(13));
        RenderUtils.drawButton(g,muteMusicBtn,musicLabel,hovered==11,FontManager.getBold(13));

        drawSectionLabel(g,"Account",panelX+20,428);
        Rectangle switchAccBtn=new Rectangle(panelX+30,444,250,38);
        RenderUtils.drawButton(g,switchAccBtn,"\uD83D\uDC64 Switch Account",hovered==12,FontManager.getBold(13));
        String acc=panel.getAccountManager().getActiveAccountName();
        g.setFont(FontManager.getBody(12));
        g.setColor(new Color(155,200,135));
        g.drawString("Active: "+(acc!=null?acc:"None"),panelX+300,468);

        backBtn=new Rectangle(20,GamePanel.H-52,140,36);
        RenderUtils.drawButton(g,backBtn,"\u2190 Back",hovered==0,FontManager.getBold(14));
    }

    private void drawSectionLabel(Graphics2D g,String label,int x,int y) {
        g.setFont(FontManager.getBold(14));
        g.setColor(ColorPalette.TEXT_GOLD);
        g.drawString(label,x,y);
        g.setColor(new Color(100,180,70,120));
        g.setStroke(new BasicStroke(1f));
        g.drawLine(x,y+4,x+200,y+4);
        g.setStroke(new BasicStroke(1f));
    }

    private void drawSliderRow(Graphics2D g,String label,int panelX,int y,float value) {
        g.setFont(FontManager.getBodyBold(13));
        g.setColor(new Color(200,235,170));
        g.drawString(label,panelX,y+16);
        int sx=SLIDER_X, sw=SLIDER_W;
        g.setColor(new Color(0,0,0,80));
        g.fillRoundRect(sx,y+6,sw,12,12,12);
        int fillW=(int)(sw*value);
        if (fillW>0) {
            GradientPaint gp=new GradientPaint(sx,y,new Color(80,200,80),sx+fillW,y,new Color(150,255,80));
            g.setPaint(gp);
            g.fillRoundRect(sx,y+6,fillW,12,12,12);
            g.setPaint(null);
        }
        g.setColor(new Color(130,220,100));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(sx,y+6,sw,12,12,12);
        g.setStroke(new BasicStroke(1f));
        int knobX=sx+(int)(sw*value);
        g.setColor(Color.WHITE);
        g.fillOval(knobX-8,y+2,16,20);
        g.setColor(new Color(100,180,80));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(knobX-8,y+2,16,20);
        g.setStroke(new BasicStroke(1f));
        g.setFont(FontManager.getBodyBold(12));
        g.setColor(new Color(200,235,170));
        g.drawString((int)(value*100)+"%",sx+sw+12,y+16);
    }

    private float sliderValue(int mx) {
        return Math.max(0f, Math.min(1f, (float)(mx-SLIDER_X)/SLIDER_W));
    }

    private boolean overSFXSlider(int mx, int my) {
        return mx>=SLIDER_X&&mx<=SLIDER_X+SLIDER_W&&my>=SFX_Y&&my<=SFX_Y+24;
    }

    private boolean overMusicSlider(int mx, int my) {
        return mx>=SLIDER_X&&mx<=SLIDER_X+SLIDER_W&&my>=MUSIC_Y&&my<=MUSIC_Y+24;
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        int mx=e.getX(), my=e.getY();
        hovered=-1;
        if (backBtn!=null&&backBtn.contains(mx,my))           hovered=0;
        if (muteBtn!=null&&muteBtn.contains(mx,my))           hovered=10;
        if (muteMusicBtn!=null&&muteMusicBtn.contains(mx,my)) hovered=11;
        if (diffBtns!=null)
            for (int i=0;i<diffBtns.length;i++)
                if (diffBtns[i].contains(mx,my)) { hovered=20+i; break; }
        int panelX=GamePanel.W/2-280;
        if (new Rectangle(panelX+30,444,250,38).contains(mx,my)) hovered=12;
        if (draggingSFX) {
            sfxVolume=sliderValue(mx);
            panel.getSoundManager().setVolume(sfxVolume);
        }
        if (draggingMusic) {
            musicVolume=sliderValue(mx);
            panel.getMusicManager().setVolume(musicVolume);
        }
    }

    @Override
    public void onMousePressed(MouseEvent e) {
        int mx=e.getX(), my=e.getY();
        if (overSFXSlider(mx,my))   draggingSFX=true;
        if (overMusicSlider(mx,my)) draggingMusic=true;
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        draggingSFX=false;
        draggingMusic=false;
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        int mx=e.getX(), my=e.getY();
        if (backBtn!=null&&backBtn.contains(mx,my)) {
            panel.switchToWithFade(GameScreenType.MAIN_MENU); return;
        }
        if (muteBtn!=null&&muteBtn.contains(mx,my)) {
            panel.getSoundManager().toggleMute(); return;
        }
        if (muteMusicBtn!=null&&muteMusicBtn.contains(mx,my)) {
            panel.getMusicManager().toggleMute(); return;
        }
        if (diffBtns!=null)
            for (int i=0;i<diffBtns.length;i++)
                if (diffBtns[i].contains(mx,my)) { selectedDiff=i; return; }
        int panelX=GamePanel.W/2-280;
        if (new Rectangle(panelX+30,444,250,38).contains(mx,my)) {
            panel.switchToWithFade(GameScreenType.ACCOUNT_SELECT); return;
        }
        if (overSFXSlider(mx,my)) {
            sfxVolume=sliderValue(mx);
            panel.getSoundManager().setVolume(sfxVolume);
        }
        if (overMusicSlider(mx,my)) {
            musicVolume=sliderValue(mx);
            panel.getMusicManager().setVolume(musicVolume);
        }
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_ESCAPE)
            panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }
}