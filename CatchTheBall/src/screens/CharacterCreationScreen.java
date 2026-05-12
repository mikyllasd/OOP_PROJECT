package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.*;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.renderers.CharacterRenderer;
import OOP_PROJECT.CatchTheBall.src.renderers.FarmHouseRenderer;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;

public class CharacterCreationScreen extends Screen {
    private StringBuilder nameInput    = new StringBuilder();
    private int           selectedSkin = 0;
    private int           selectedDiff = 1;

    private static final SkinType[]   STARTERS   = {SkinType.FARMER_MALE, SkinType.FARMER_FEMALE, SkinType.FARM_KID};
    private static final Difficulty[] DIFFS       = {Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD};
    private static final Color[]      DIFF_COLORS = {new Color(80,200,80), new Color(255,200,50), new Color(255,80,80)};

    public CharacterCreationScreen(GamePanel panel) { super(panel); }

    @Override
    public void onEnter() {
        super.onEnter();
        nameInput.setLength(0);
        String pn = panel.getPlayerData().getProfile().getFarmerName();
        if (pn != null && !pn.equals("Farmer")) nameInput.append(pn);
        selectedSkin = 0;
        selectedDiff = panel.getPlayerData().getDefaultDifficulty().ordinal();
    }

    @Override public void update() { tickCount++; }

    @Override
    public void draw(Graphics2D g) {
        g.setPaint(new GradientPaint(0,0,new Color(18,52,14),0,GamePanel.H,new Color(8,32,6)));
        g.fillRect(0,0,GamePanel.W,GamePanel.H); g.setPaint(null);

        // Animated house in background top-right
        FarmHouseRenderer.draw(g, panel.getPlayerData().getFarmStage(),
                GamePanel.W - 210, 60, 180, 150);

        RenderUtils.drawGradientPanel(g,GamePanel.W/2-310,40,620,580,
                new Color(24,58,16,230),new Color(14,38,8,230),new Color(90,175,65),2f,22);

        RenderUtils.drawCenteredText(g,"Create Your Farmhand",
                GamePanel.W/2,95,FontManager.getBold(28),ColorPalette.TEXT_GOLD);

        drawNameField(g);
        drawSkinPicker(g);
        drawDifficultyPicker(g);
        drawStartButton(g);

        g.setFont(FontManager.getBodyBold(13));
        g.setColor(new Color(180,220,150));
        g.drawString("< Back to Menu", GamePanel.W/2-295, 545);
    }

    private void drawNameField(Graphics2D g) {
        g.setFont(FontManager.getBodyBold(15));
        g.setColor(ColorPalette.TEXT_GREEN_LIGHT);
        g.drawString("Farmer Name:", GamePanel.W/2-240, 130);
        RenderUtils.drawRoundPanel(g,GamePanel.W/2-240,138,480,38,
                new Color(38,85,28),new Color(100,200,75),2f,8);
        g.setFont(FontManager.getBody(17));
        g.setColor(Color.WHITE);
        g.drawString(nameInput+(tickCount%60<30?"|":""), GamePanel.W/2-228, 164);
    }

    private void drawSkinPicker(Graphics2D g) {
        g.setFont(FontManager.getBodyBold(14));
        g.setColor(ColorPalette.TEXT_GREEN_LIGHT);
        g.drawString("Choose Starter Skin:", GamePanel.W/2-240, 210);

        int bw=145, bh=145;
        int sx=GamePanel.W/2-(STARTERS.length*bw+(STARTERS.length-1)*10)/2;

        for (int i=0;i<STARTERS.length;i++) {
            int bx=sx+i*(bw+10);
            boolean sel=(i==selectedSkin);

            // Card background
            RenderUtils.drawGradientPanel(g,bx,220,bw,bh,
                    sel?new Color(78,158,58):new Color(36,75,26),
                    sel?new Color(54,118,38):new Color(24,52,16),
                    sel?new Color(148,238,100):new Color(68,128,58),
                    sel?3f:1.5f,12);

            // Draw cartoon character (animated with tickCount)
            drawMiniCharacter(g, STARTERS[i], bx+bw/2, 220+10, bw-10, 95, tickCount);

            // Name label
            g.setFont(FontManager.getBodyBold(11));
            FontMetrics fm=g.getFontMetrics();
            String dn=STARTERS[i].getDisplayName();
            g.setColor(new Color(200,240,170));
            g.drawString(dn, bx+(bw-fm.stringWidth(dn))/2, 220+bh-12);
        }
    }

    private void drawMiniCharacter(Graphics2D g, SkinType skin, int cx, int y, int w, int h, int tick) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Scale to fit the card
        float scale = 0.72f;
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.translate(cx - 25 * scale, y);
        g2.scale(scale, scale);

        drawStandaloneCharacter(g2, skin, 0, 0, tick);
        g2.dispose();
    }

    private void drawStandaloneCharacter(Graphics2D g, SkinType skin, int x, int y, int tick) {
        int cx = x + 25;
        boolean walk = (tick / 8) % 2 == 0;

        Color skinTone, hairColor, shirtColor, pantsColor, shoeColor;
        switch (skin) {
            case FARMER_FEMALE:
                skinTone=new Color(240,195,145); hairColor=new Color(180,100,60);
                shirtColor=new Color(240,140,160); pantsColor=new Color(200,100,130); shoeColor=new Color(180,100,120);
                break;
            case FARM_KID:
                skinTone=new Color(255,210,160); hairColor=new Color(200,150,80);
                shirtColor=new Color(100,180,240); pantsColor=new Color(80,130,200); shoeColor=new Color(80,60,200);
                break;
            default: // FARMER_MALE
                skinTone=new Color(220,175,120); hairColor=new Color(110,70,30);
                shirtColor=new Color(100,140,200); pantsColor=new Color(60,90,160); shoeColor=new Color(60,40,20);
        }

        // Bob animation
        float bob = (float)(Math.sin(tick * 0.1) * 2);

        // Shadow
        g.setColor(new Color(0,0,0,35));
        g.fillOval(cx-16, y+58+(int)bob, 32, 7);

        // Legs
        g.setColor(pantsColor);
        g.fillRect(cx-8, y+38+(int)bob, 8, 18);
        g.fillRect(cx+1, y+38+(int)bob, 8, 18);

        // Shoes
        g.setColor(shoeColor);
        g.fillRoundRect(cx-10+(walk?2:0), y+54+(int)bob, 12, 6, 4,4);
        g.fillRoundRect(cx+1+(walk?0:2), y+54+(int)bob, 12, 6, 4,4);

        // Body
        g.setColor(shirtColor);
        int[] bxArr={cx-12,cx+12,cx+10,cx-10};
        int[] byArr={y+18+(int)bob,y+18+(int)bob,y+38+(int)bob,y+38+(int)bob};
        g.fillPolygon(bxArr,byArr,4);

        // Shirt highlight
        g.setColor(new Color(255,255,255,35));
        g.fillRect(cx-10, y+19+(int)bob, 8, 16);

        // Arms (swinging)
        int swing = (int)(Math.sin(tick*0.12)*5);
        g.setColor(shirtColor.darker());
        g.fillRoundRect(cx-18, y+22+(int)bob+swing, 7, 13, 3,3);
        g.fillRoundRect(cx+11, y+22+(int)bob-swing, 7, 13, 3,3);
        g.setColor(skinTone);
        g.fillOval(cx-18, y+33+(int)bob+swing, 8, 8);
        g.fillOval(cx+11, y+33+(int)bob-swing, 8, 8);

        // Neck
        g.setColor(skinTone);
        g.fillRoundRect(cx-4, y+11+(int)bob, 8, 9, 4,4);

        // Head
        g.setColor(skinTone);
        g.fillOval(cx-12, y+(int)bob, 24, 22);

        // Cheeks
        g.setColor(new Color(255,160,140,70));
        g.fillOval(cx-12, y+9+(int)bob, 8, 6);
        g.fillOval(cx+4, y+9+(int)bob, 8, 6);

        // Eyes
        g.setColor(Color.WHITE);
        g.fillOval(cx-9, y+7+(int)bob, 7, 6);
        g.fillOval(cx+2, y+7+(int)bob, 7, 6);
        g.setColor(new Color(40,60,180));
        g.fillOval(cx-7, y+9+(int)bob, 4, 4);
        g.fillOval(cx+4, y+9+(int)bob, 4, 4);
        g.setColor(Color.BLACK);
        g.fillOval(cx-6, y+10+(int)bob, 2, 2);
        g.fillOval(cx+5, y+10+(int)bob, 2, 2);
        g.setColor(Color.WHITE);
        g.fillOval(cx-5, y+10+(int)bob, 1, 1);
        g.fillOval(cx+6, y+10+(int)bob, 1, 1);

        // Blink
        if (tick % 100 < 3) {
            g.setColor(skinTone);
            g.fillRect(cx-9, y+9+(int)bob, 7, 4);
            g.fillRect(cx+2, y+9+(int)bob, 7, 4);
        }

        // Smile
        g.setColor(new Color(180,80,80));
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(cx-4, y+14+(int)bob, 8, 5, 200, 140);
        g.setStroke(new BasicStroke(1f));

        // Eyebrows
        g.setColor(hairColor.darker());
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawLine(cx-8, y+5+(int)bob, cx-4, y+4+(int)bob);
        g.drawLine(cx+4, y+4+(int)bob, cx+8, y+5+(int)bob);
        g.setStroke(new BasicStroke(1f));

        // Hat / Hair per skin
        switch (skin) {
            case FARMER_MALE:
                g.setColor(new Color(210,180,80));
                g.fillOval(cx-18, y-2+(int)bob, 36, 10);
                g.setColor(new Color(190,155,60));
                g.fillRoundRect(cx-10, y-13+(int)bob, 20, 14, 6,6);
                g.setColor(new Color(160,120,40));
                g.fillRect(cx-10, y-2+(int)bob, 20, 3);
                break;
            case FARMER_FEMALE:
                g.setColor(new Color(220,160,180));
                g.fillOval(cx-16, y-3+(int)bob, 32, 12);
                g.setColor(new Color(200,130,160));
                g.fillRoundRect(cx-10, y-14+(int)bob, 20, 14, 8,8);
                g.setColor(new Color(255,150,180));
                g.fillRect(cx-10, y-3+(int)bob, 20, 3);
                // Flower
                g.setColor(new Color(255,200,50));
                g.fillOval(cx+4, y-16+(int)bob, 7, 7);
                g.setColor(new Color(255,100,100));
                for (int p=0;p<5;p++) {
                    double a=Math.toRadians(p*72);
                    g.fillOval((int)(cx+7+Math.cos(a)*4)-2,(int)(y-13+(int)bob+Math.sin(a)*4)-2,4,4);
                }
                break;
            case FARM_KID:
                g.setColor(new Color(200,60,60));
                g.fillOval(cx-11, y-10+(int)bob, 22, 16);
                g.fillOval(cx-13, y+(int)bob, 26, 7);
                g.setColor(Color.WHITE);
                g.setStroke(new BasicStroke(1f));
                g.drawLine(cx, y-10+(int)bob, cx, y-4+(int)bob);
                g.setStroke(new BasicStroke(1f));
                break;
        }

        // Hair tufts
        g.setColor(hairColor);
        g.fillRect(cx-10, y+17+(int)bob, 20, 5);
    }

    private void drawDifficultyPicker(Graphics2D g) {
        g.setFont(FontManager.getBodyBold(14));
        g.setColor(ColorPalette.TEXT_GREEN_LIGHT);
        g.drawString("Difficulty:", GamePanel.W/2-240, 390);

        int bw=110, bh=38;
        for (int i=0;i<DIFFS.length;i++) {
            int bx=GamePanel.W/2-180+i*(bw+12);
            boolean sel=(i==selectedDiff);
            Color c=DIFF_COLORS[i];
            RenderUtils.drawGradientPanel(g,bx,400,bw,bh,
                    sel?c:new Color(40,80,28),
                    sel?c.darker():new Color(26,56,18),
                    sel?c.brighter():new Color(68,120,52),
                    sel?2.5f:1.2f,10);
            RenderUtils.drawCenteredText(g,DIFFS[i].getDisplayName(),bx+bw/2,425,
                    FontManager.getBold(13),sel?Color.WHITE:new Color(180,220,150));
        }
    }

    private void drawStartButton(Graphics2D g) {
        boolean can=nameInput.length()>0;
        Rectangle r=new Rectangle(GamePanel.W/2-150, 460, 300, 52);
        if (can) RenderUtils.drawButton(g,r,"Start Farming!",true,FontManager.getBold(18));
        else {
            RenderUtils.drawGradientPanel(g,r.x,r.y,r.width,r.height,
                    new Color(40,70,30),new Color(28,52,20),new Color(70,110,55),1.5f,14);
            RenderUtils.drawCenteredText(g,"Enter a name first",GamePanel.W/2,492,
                    FontManager.getBody(14),new Color(140,180,110));
        }
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_BACK_SPACE&&nameInput.length()>0)
            nameInput.deleteCharAt(nameInput.length()-1);
        if (e.getKeyCode()==KeyEvent.VK_ENTER&&nameInput.length()>0) startGame();
        if (e.getKeyCode()==KeyEvent.VK_ESCAPE) panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }

    @Override
    public void onKeyTyped(KeyEvent e) {
        char c=e.getKeyChar();
        if (c>=32&&c<127&&nameInput.length()<16) nameInput.append(c);
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        int mx=e.getX(),my=e.getY();
        int bw=145,bh=145;
        int sx=GamePanel.W/2-(STARTERS.length*bw+(STARTERS.length-1)*10)/2;
        for (int i=0;i<STARTERS.length;i++)
            if (new Rectangle(sx+i*(bw+10),220,bw,bh).contains(mx,my)) { selectedSkin=i; return; }
        int dbw=110;
        for (int i=0;i<DIFFS.length;i++) {
            int bx=GamePanel.W/2-180+i*(dbw+12);
            if (new Rectangle(bx,400,dbw,38).contains(mx,my)) { selectedDiff=i; return; }
        }
        if (new Rectangle(GamePanel.W/2-150,460,300,52).contains(mx,my)&&nameInput.length()>0) {
            startGame(); return;
        }
        if (my>530&&mx<GamePanel.W/2-130) panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }

    private void startGame() {
        panel.getPlayerData().getProfile().setFarmerName(nameInput.toString().trim());
        panel.getPlayerData().equipSkin(STARTERS[selectedSkin]);
        panel.getPlayerData().save();
        GameScreen gs=panel.getScreenManager().getGameScreen();
        gs.setPlayerName(nameInput.toString().trim());
        gs.setDifficulty(DIFFS[selectedDiff]);
        panel.switchToWithFade(GameScreenType.GAME);
    }
}