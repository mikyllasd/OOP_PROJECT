package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.*;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.renderers.BasketRenderer;
import OOP_PROJECT.CatchTheBall.src.renderers.CharacterRenderer;
import OOP_PROJECT.CatchTheBall.src.entities.Character;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;

public class WardrobeScreen extends Screen {
    private int shopTab = 0;
    private Rectangle backBtn;
    private int hovered = -1;
    private int previewSkin = -1;
    private int previewBasket = -1;

    private static final int COLS = 5;
    private static final int BW   = 112;
    private static final int BH   = 160;
    private static final int GAP  = 10;
    private static final int STARTX = 18;
    private static final int STARTY = 138;

    public WardrobeScreen(GamePanel panel){super(panel);}

    @Override public void update(){tickCount++;}

    @Override
    public void draw(Graphics2D g){
        // Background
        g.setPaint(new GradientPaint(0,0,new Color(18,44,14),0,GamePanel.H,new Color(10,28,8)));
        g.fillRect(0,0,GamePanel.W,GamePanel.H);g.setPaint(null);

        // Decorative top stars/particles
        drawDecorativeBg(g);

        // Header
        drawHeader(g);

        // Tabs
        drawTabs(g);

        // Coins bar
        drawCoinsBar(g);

        // Cards
        if(shopTab==0) drawSkins(g);
        else           drawBaskets(g);

        // Preview panel
        drawPreviewPanel(g);

        // Back button
        backBtn=new Rectangle(18,GamePanel.H-52,130,36);
        RenderUtils.drawButton(g,backBtn,"\u2190 Back",hovered==9999,FontManager.getBold(13));
    }

    private void drawDecorativeBg(Graphics2D g){
        // Subtle floating leaves
        g.setFont(FontManager.getEmoji(14));
        String[] deco={"\uD83C\uDF43","\uD83C\uDF3F","\uD83C\uDF3E","\uD83C\uDF3B"};
        for(int i=0;i<10;i++){
            int dx=(int)((i*137+tickCount*0.3)%GamePanel.W);
            int dy=(int)((tickCount*0.5+i*65)%(GamePanel.H+20))-10;
            g.setColor(new Color(255,255,255,30));
            g.drawString(deco[i%deco.length],dx,dy);
        }
    }

    private void drawHeader(Graphics2D g){
        // Gradient header bar
        GradientPaint hdr=new GradientPaint(0,0,new Color(30,75,20,220),0,72,new Color(15,45,10,220));
        g.setPaint(hdr);g.fillRect(0,0,GamePanel.W,72);g.setPaint(null);
        g.setColor(new Color(100,200,60,160));g.setStroke(new BasicStroke(2f));
        g.drawLine(0,72,GamePanel.W,72);g.setStroke(new BasicStroke(1f));
        // Title
        g.setFont(FontManager.getBold(26));
        String title="\uD83E\uDDFA Wardrobe & Shop";
        FontMetrics fm=g.getFontMetrics();
        int tx=(GamePanel.W-fm.stringWidth(title))/2;
        g.setColor(new Color(0,0,0,100));g.drawString(title,tx+2,48);
        g.setColor(new Color(255,220,80));g.drawString(title,tx,46);
    }

    private void drawTabs(Graphics2D g){
        String[] tabs={"\uD83D\uDC68\u200D\uD83C\uDF3E Farmer Skins","\uD83E\uDDFA Baskets"};
        int tabW=210,tabH=34,tabY=80;
        int startTab=(GamePanel.W-(tabs.length*tabW+(tabs.length-1)*8))/2;
        for(int i=0;i<tabs.length;i++){
            int tx=startTab+i*(tabW+8);
            boolean sel=shopTab==i;
            if(sel){
                GradientPaint tp=new GradientPaint(tx,tabY,new Color(88,170,58),tx,tabY+tabH,new Color(55,125,35));
                g.setPaint(tp);g.fillRoundRect(tx,tabY,tabW,tabH,10,10);g.setPaint(null);
                g.setColor(new Color(150,240,100));g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(tx,tabY,tabW,tabH,10,10);g.setStroke(new BasicStroke(1f));
                g.setColor(Color.WHITE);
            } else {
                g.setColor(new Color(35,70,25,200));g.fillRoundRect(tx,tabY,tabW,tabH,10,10);
                g.setColor(new Color(65,120,48));g.setStroke(new BasicStroke(1.5f));
                g.drawRoundRect(tx,tabY,tabW,tabH,10,10);g.setStroke(new BasicStroke(1f));
                g.setColor(new Color(155,200,135));
            }
            g.setFont(FontManager.getBold(13));
            FontMetrics fm=g.getFontMetrics();
            g.drawString(tabs[i],tx+(tabW-fm.stringWidth(tabs[i]))/2,tabY+22);
        }
    }

    private void drawCoinsBar(Graphics2D g){
        int coins=panel.getPlayerData().getTotalCoins();
        // Pill background
        int cw=170,ch=28,cx=GamePanel.W-cw-16,cy=82;
        g.setColor(new Color(40,80,25,200));g.fillRoundRect(cx,cy,cw,ch,14,14);
        g.setColor(new Color(200,160,30));g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(cx,cy,cw,ch,14,14);g.setStroke(new BasicStroke(1f));
        g.setFont(FontManager.getBodyBold(14));g.setColor(new Color(255,215,50));
        g.drawString("\uD83E\uDE99 "+coins+" coins",cx+10,cy+19);
    }

    private void drawSkins(Graphics2D g){
        SkinType[] skins=SkinType.values();
        for(int i=0;i<skins.length;i++){
            int col=i%COLS,row=i/COLS;
            int bx=STARTX+col*(BW+GAP),by=STARTY+row*(BH+GAP);
            boolean owned=panel.getPlayerData().ownsSkin(skins[i]);
            boolean eqd=panel.getPlayerData().getEquippedSkin()==skins[i];
            boolean hov=(hovered==i);
            boolean prev=(previewSkin==i);
            drawSkinCard(g,bx,by,BW,BH,skins[i],owned,eqd,hov||prev);
        }
    }

    private void drawSkinCard(Graphics2D g,int x,int y,int w,int h,
            SkinType skin,boolean owned,boolean equipped,boolean hovered){
        // Card background
        Color bgTop,bgBot,border;
        float borderW;
        if(equipped){
            bgTop=new Color(65,140,45);bgBot=new Color(42,100,28);
            border=new Color(150,245,100);borderW=2.5f;
        } else if(owned){
            bgTop=new Color(38,82,26);bgBot=new Color(24,56,16);
            border=new Color(85,160,60);borderW=1.5f;
        } else {
            bgTop=new Color(26,55,18);bgBot=new Color(16,36,10);
            border=new Color(58,95,45);borderW=1f;
        }
        if(hovered&&!equipped){
            bgTop=bgTop.brighter();border=border.brighter();borderW+=1f;
        }
        GradientPaint gp=new GradientPaint(x,y,bgTop,x,y+h,bgBot);
        g.setPaint(gp);g.fillRoundRect(x,y,w,h,12,12);g.setPaint(null);
        g.setColor(border);g.setStroke(new BasicStroke(borderW));
        g.drawRoundRect(x,y,w,h,12,12);g.setStroke(new BasicStroke(1f));

        // Glow on equipped
        if(equipped){
            float pulse=(float)(0.5+0.5*Math.sin(tickCount*0.08));
            g.setColor(new Color(100,255,80,(int)(25*pulse)));
            g.fillRoundRect(x,y,w,h,12,12);
        }

        // Character preview inside card
        drawMiniCharacter(g,x+w/2,y+10,skin);

        // Name
        g.setFont(FontManager.getBold(10));
        FontMetrics fm=g.getFontMetrics();
        String name=skin.getDisplayName();
        g.setColor(new Color(220,245,185));
        g.drawString(name,x+(w-fm.stringWidth(name))/2,y+h-62);

        // Status / price badge
        drawSkinBadge(g,x,y,w,h,skin,owned,equipped);
    }

    private void drawMiniCharacter(Graphics2D g,int cx,int y,SkinType skin){
        // Draw a 48x64 character preview
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        // Scale down the character (it's drawn at ~50px wide, we want ~44px wide)
        float scale=0.88f;
        g2.translate(cx-22,y);
        g2.scale(scale,scale);
        // Create a temporary Character-like draw
        drawMiniChar(g2,skin,0,0,tickCount);
        g2.dispose();
    }

    private void drawMiniChar(Graphics2D g,SkinType skin,int x,int y,int tick){
        // Inline simplified character for card preview
        // Re-use the same CharacterRenderer logic but positioned
        // We'll just call a static-like draw at fixed position
        // Shadow
        g.setColor(new Color(0,0,0,30));g.fillOval(x+7,y+59,36,6);
        // Use the full renderer but at this position
        int cx=x+25;
        // Get skin colors via reflection-free approach: use fields
        Color[] colors=getMiniColors(skin);
        Color skinTone=colors[0],hairC=colors[1],shirtC=colors[2],pantsC=colors[3],shoeC=colors[4];
        // Legs
        g.setColor(pantsC);g.fillRect(cx-8,y+40,8,16);g.fillRect(cx+1,y+40,8,16);
        g.setColor(shoeC);g.fillRoundRect(cx-10,y+54,12,6,4,4);g.fillRoundRect(cx+1,y+54,12,6,4,4);
        // Body
        g.setColor(shirtC);
        int[] bxc={cx-12,cx+12,cx+10,cx-10};int[] byc={y+20,y+20,y+40,y+40};
        g.fillPolygon(bxc,byc,4);
        // Arms
        g.setColor(shirtC.darker());g.fillRoundRect(cx-18,y+22,7,14,3,3);g.fillRoundRect(cx+11,y+22,7,14,3,3);
        g.setColor(skinTone);g.fillOval(cx-18,y+34,8,8);g.fillOval(cx+11,y+34,8,8);
        // Neck
        g.setColor(skinTone);g.fillRoundRect(cx-4,y+12,8,10,4,4);
        // Head
        g.setColor(skinTone);g.fillOval(cx-12,y,24,22);
        g.setColor(new Color(255,150,130,70));g.fillOval(cx-12,y+9,8,6);g.fillOval(cx+4,y+9,8,6);
        // Eyes
        g.setColor(Color.WHITE);g.fillOval(cx-9,y+8,7,6);g.fillOval(cx+2,y+8,7,6);
        g.setColor(new Color(40,40,180));g.fillOval(cx-7,y+10,4,4);g.fillOval(cx+4,y+10,4,4);
        g.setColor(new Color(10,10,10));g.fillOval(cx-6,y+11,2,2);g.fillOval(cx+5,y+11,2,2);
        g.setColor(Color.WHITE);g.fillOval(cx-5,y+11,1,1);g.fillOval(cx+6,y+11,1,1);
        // Smile
        g.setColor(new Color(180,80,80));g.setStroke(new BasicStroke(1.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawArc(cx-4,y+15,8,4,200,140);g.setStroke(new BasicStroke(1f));
        // Hair/Hat (skin specific top)
        g.setColor(hairC);g.fillRect(cx-11,y,22,4);
        drawMiniHat(g,cx,y,skin,hairC);
    }

    private void drawMiniHat(Graphics2D g,int cx,int y,SkinType skin,Color hairC){
        switch(skin){
            case FARMER_MALE:
                g.setColor(new Color(210,180,80));g.fillOval(cx-16,y-2,32,8);
                g.setColor(new Color(190,155,60));g.fillRoundRect(cx-9,y-10,18,12,6,6);
                break;
            case FARMER_FEMALE:
                g.setColor(new Color(220,160,180));g.fillOval(cx-14,y-2,28,10);
                g.setColor(new Color(200,130,160));g.fillRoundRect(cx-10,y-12,20,13,8,8);
                g.setColor(new Color(255,150,180));g.fillRect(cx-10,y-2,20,3);
                break;
            case FARM_KID:
                g.setColor(new Color(200,60,60));g.fillOval(cx-11,y-8,22,14);
                g.fillOval(cx-13,y,26,7);
                break;
            case COWBOY:
                g.setColor(new Color(130,80,30));g.fillOval(cx-18,y-1,36,8);
                g.fillRoundRect(cx-10,y-14,20,16,5,5);
                break;
            case WIZARD:
                g.setColor(new Color(80,40,140));
                int[] hx={cx-12,cx+12,cx+6,cx-6,cx};int[] hy={y-1,y-1,y-7,y-7,y-22};
                g.fillPolygon(hx,hy,5);
                g.fillOval(cx-13,y-4,26,8);
                break;
            case NINJA:
                g.setColor(new Color(20,20,20));g.fillRect(cx-12,y+2,24,16);
                g.setColor(new Color(200,50,50));g.fillRect(cx-12,y+5,24,3);
                break;
            case ROYAL:
                g.setColor(new Color(220,180,30));
                int[] crx={cx-11,cx-11,cx-7,cx-4,cx,cx+1,cx+5,cx+8,cx+11,cx+11};
                int[] cry={y+2,y-7,y-4,y-10,y-6,y-10,y-4,y-7,y-7,y+2};
                g.fillPolygon(crx,cry,10);
                break;
            case CHEF:
                g.setColor(Color.WHITE);g.fillRoundRect(cx-10,y-20,20,22,6,6);g.fillRect(cx-12,y-2,24,5);
                break;
            case PIRATE:
                g.setColor(new Color(20,20,20));g.fillOval(cx-15,y-2,30,8);
                int[] px2={cx-10,cx,cx+10};int[] py2={y-1,y-14,y-1};
                g.fillPolygon(px2,py2,3);
                break;
            case EXPLORER:
                g.setColor(new Color(180,150,80));g.fillOval(cx-16,y-1,32,8);
                g.fillRoundRect(cx-10,y-12,20,14,6,6);
                break;
        }
    }

    private Color[] getMiniColors(SkinType skin){
        switch(skin){
            case FARMER_MALE:   return new Color[]{new Color(220,175,120),new Color(110,70,30),new Color(100,140,200),new Color(60,90,160),new Color(60,40,20)};
            case FARMER_FEMALE: return new Color[]{new Color(240,195,145),new Color(180,100,60),new Color(240,140,160),new Color(200,100,130),new Color(180,100,120)};
            case FARM_KID:      return new Color[]{new Color(255,210,160),new Color(200,150,80),new Color(100,180,240),new Color(80,130,200),new Color(80,60,200)};
            case COWBOY:        return new Color[]{new Color(200,155,100),new Color(80,50,20),new Color(180,120,60),new Color(100,70,40),new Color(50,30,10)};
            case WIZARD:        return new Color[]{new Color(210,180,155),new Color(220,215,200),new Color(100,60,180),new Color(80,40,160),new Color(50,30,100)};
            case NINJA:         return new Color[]{new Color(180,140,100),new Color(20,20,20),new Color(20,20,20),new Color(10,10,10),new Color(15,15,15)};
            case ROYAL:         return new Color[]{new Color(230,185,130),new Color(60,40,100),new Color(120,60,160),new Color(80,40,120),new Color(60,40,80)};
            case CHEF:          return new Color[]{new Color(230,185,130),new Color(50,40,30),Color.WHITE,new Color(40,40,40),new Color(30,30,30)};
            case PIRATE:        return new Color[]{new Color(200,155,100),new Color(20,15,10),new Color(40,30,20),new Color(30,20,10),new Color(20,15,5)};
            case EXPLORER:      return new Color[]{new Color(190,145,90),new Color(90,60,30),new Color(180,160,100),new Color(120,100,60),new Color(80,60,30)};
            default:            return new Color[]{new Color(220,175,120),new Color(110,70,30),new Color(100,140,200),new Color(60,90,160),new Color(60,40,20)};
        }
    }

    private void drawSkinBadge(Graphics2D g,int x,int y,int w,int h,
            SkinType skin,boolean owned,boolean equipped){
        int by=y+h-54;
        if(equipped){
            g.setColor(new Color(50,180,50,180));g.fillRoundRect(x+6,by,w-12,22,8,8);
            g.setColor(new Color(120,255,100));g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x+6,by,w-12,22,8,8);g.setStroke(new BasicStroke(1f));
            g.setFont(FontManager.getBold(10));g.setColor(Color.WHITE);
            FontMetrics fm=g.getFontMetrics();String t="\u2713 EQUIPPED";
            g.drawString(t,x+6+(w-12-fm.stringWidth(t))/2,by+15);
        } else if(owned){
            g.setColor(new Color(40,120,40,180));g.fillRoundRect(x+6,by,w-12,22,8,8);
            g.setColor(new Color(90,200,70));g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x+6,by,w-12,22,8,8);g.setStroke(new BasicStroke(1f));
            g.setFont(FontManager.getBold(9));g.setColor(new Color(150,255,120));
            FontMetrics fm=g.getFontMetrics();String t="OWNED \u2022 Equip";
            g.drawString(t,x+6+(w-12-fm.stringWidth(t))/2,by+15);
        } else {
            g.setColor(new Color(30,60,20,200));g.fillRoundRect(x+6,by,w-12,22,8,8);
            g.setColor(new Color(180,140,30));g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x+6,by,w-12,22,8,8);g.setStroke(new BasicStroke(1f));
            g.setFont(FontManager.getBold(10));g.setColor(new Color(255,215,50));
            FontMetrics fm=g.getFontMetrics();String t="\uD83E\uDE99 "+skin.getCost();
            g.drawString(t,x+6+(w-12-fm.stringWidth(t))/2,by+15);
        }
        // Equip / Buy action hint at bottom
        int btnY=y+h-28;
        if(!equipped){
            String label=owned?"Equip":"Buy";
            Color btnC=owned?new Color(60,180,50):new Color(180,130,20);
            g.setColor(btnC.darker());g.fillRoundRect(x+6,btnY,w-12,20,6,6);
            g.setColor(btnC.brighter());g.setStroke(new BasicStroke(1f));
            g.drawRoundRect(x+6,btnY,w-12,20,6,6);g.setStroke(new BasicStroke(1f));
            g.setFont(FontManager.getBold(9));g.setColor(Color.WHITE);
            FontMetrics fm=g.getFontMetrics();
            g.drawString(label,x+6+(w-12-fm.stringWidth(label))/2,btnY+13);
        }
    }

    private void drawBaskets(Graphics2D g){
        BasketSkin[] baskets=BasketSkin.values();
        for(int i=0;i<baskets.length;i++){
            int col=i%COLS,row=i/COLS;
            int bx=STARTX+col*(BW+GAP),by=STARTY+row*(BH+GAP);
            boolean owned=panel.getPlayerData().ownsBasket(baskets[i]);
            boolean eqd=panel.getPlayerData().getEquippedBasket()==baskets[i];
            boolean hov=(hovered==200+i);
            drawBasketCard(g,bx,by,BW,BH,baskets[i],owned,eqd,hov);
        }
    }

    private void drawBasketCard(Graphics2D g,int x,int y,int w,int h,
            BasketSkin skin,boolean owned,boolean equipped,boolean hov){
        Color bgTop,bgBot,border;float borderW;
        if(equipped){bgTop=new Color(65,140,45);bgBot=new Color(42,100,28);border=new Color(150,245,100);borderW=2.5f;}
        else if(owned){bgTop=new Color(38,82,26);bgBot=new Color(24,56,16);border=new Color(85,160,60);borderW=1.5f;}
        else{bgTop=new Color(26,55,18);bgBot=new Color(16,36,10);border=new Color(58,95,45);borderW=1f;}
        if(hov&&!equipped){bgTop=bgTop.brighter();border=border.brighter();borderW+=1f;}
        GradientPaint gp=new GradientPaint(x,y,bgTop,x,y+h,bgBot);
        g.setPaint(gp);g.fillRoundRect(x,y,w,h,12,12);g.setPaint(null);
        g.setColor(border);g.setStroke(new BasicStroke(borderW));
        g.drawRoundRect(x,y,w,h,12,12);g.setStroke(new BasicStroke(1f));
        if(equipped){
            float pulse=(float)(0.5+0.5*Math.sin(tickCount*0.08));
            g.setColor(new Color(100,255,80,(int)(25*pulse)));g.fillRoundRect(x,y,w,h,12,12);
        }
        // Basket preview — centered in card, big enough
        int previewW=56,previewH=40;
        int px=x+(w-previewW)/2,py=y+22;
        // Clip to card
        Shape oldClip=g.getClip();
        g.setClip(x+3,y+3,w-6,h-6);
        BasketRenderer.draw(g,skin,px,py,previewW,previewH);
        g.setClip(oldClip);
        // Name
        g.setFont(FontManager.getBold(10));
        FontMetrics fm=g.getFontMetrics();
        String name=skin.getDisplayName();
        // Wrap if needed
        if(fm.stringWidth(name)>w-10){
            // two-line
            String[] words=name.split(" ");
            if(words.length>=2){
                String line1=words[0];
                String line2=String.join(" ",java.util.Arrays.copyOfRange(words,1,words.length));
                g.setColor(new Color(220,245,185));
                g.drawString(line1,x+(w-fm.stringWidth(line1))/2,y+h-70);
                g.drawString(line2,x+(w-fm.stringWidth(line2))/2,y+h-58);
            } else {
                g.setColor(new Color(220,245,185));g.drawString(name,x+(w-fm.stringWidth(name))/2,y+h-64);
            }
        } else {
            g.setColor(new Color(220,245,185));g.drawString(name,x+(w-fm.stringWidth(name))/2,y+h-64);
        }
        // Badge
        drawBasketBadge(g,x,y,w,h,skin,owned,equipped);
    }

    private void drawBasketBadge(Graphics2D g,int x,int y,int w,int h,
            BasketSkin skin,boolean owned,boolean equipped){
        int by2=y+h-50;
        if(equipped){
            g.setColor(new Color(50,180,50,180));g.fillRoundRect(x+6,by2,w-12,20,8,8);
            g.setColor(new Color(120,255,100));g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x+6,by2,w-12,20,8,8);g.setStroke(new BasicStroke(1f));
            g.setFont(FontManager.getBold(9));g.setColor(Color.WHITE);
            FontMetrics fm=g.getFontMetrics();String t="\u2713 EQUIPPED";
            g.drawString(t,x+6+(w-12-fm.stringWidth(t))/2,by2+13);
        } else if(owned){
            g.setColor(new Color(40,120,40,180));g.fillRoundRect(x+6,by2,w-12,20,8,8);
            g.setColor(new Color(90,200,70));g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x+6,by2,w-12,20,8,8);g.setStroke(new BasicStroke(1f));
            g.setFont(FontManager.getBold(9));g.setColor(new Color(150,255,120));
            FontMetrics fm=g.getFontMetrics();String t="OWNED \u2022 Equip";
            g.drawString(t,x+6+(w-12-fm.stringWidth(t))/2,by2+13);
        } else {
            g.setColor(new Color(30,60,20,200));g.fillRoundRect(x+6,by2,w-12,20,8,8);
            g.setColor(new Color(180,140,30));g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x+6,by2,w-12,20,8,8);g.setStroke(new BasicStroke(1f));
            g.setFont(FontManager.getBold(10));g.setColor(new Color(255,215,50));
            FontMetrics fm=g.getFontMetrics();String t="\uD83E\uDE99 "+skin.getCost();
            g.drawString(t,x+6+(w-12-fm.stringWidth(t))/2,by2+13);
        }
        int btnY=y+h-26;
        if(!equipped){
            String label=owned?"Equip":"Buy";
            Color btnC=owned?new Color(60,180,50):new Color(180,130,20);
            g.setColor(btnC.darker());g.fillRoundRect(x+6,btnY,w-12,18,6,6);
            g.setColor(btnC.brighter());g.setStroke(new BasicStroke(1f));
            g.drawRoundRect(x+6,btnY,w-12,18,6,6);g.setStroke(new BasicStroke(1f));
            g.setFont(FontManager.getBold(9));g.setColor(Color.WHITE);
            FontMetrics fm=g.getFontMetrics();
            g.drawString(label,x+6+(w-12-fm.stringWidth(label))/2,btnY+12);
        }
    }

    private void drawPreviewPanel(Graphics2D g){
        // Small info bar at bottom
        int py=GamePanel.H-52,pw=260,ph=40;
        int px=GamePanel.W/2-pw/2;
        g.setColor(new Color(20,50,14,200));g.fillRoundRect(px,py,pw,ph,12,12);
        g.setColor(new Color(80,160,55));g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(px,py,pw,ph,12,12);g.setStroke(new BasicStroke(1f));
        g.setFont(FontManager.getBody(11));g.setColor(new Color(180,220,150));
        String hint=shopTab==0?"Click a skin to equip or buy":"Click a basket to equip or buy";
        FontMetrics fm=g.getFontMetrics();
        g.drawString(hint,px+(pw-fm.stringWidth(hint))/2,py+25);
    }

    @Override
    public void onMouseMoved(MouseEvent e){
        int mx=e.getX(),my=e.getY();
        hovered=-1;previewSkin=-1;previewBasket=-1;
        if(backBtn!=null&&backBtn.contains(mx,my)){hovered=9999;return;}
        if(shopTab==0){
            SkinType[] skins=SkinType.values();
            for(int i=0;i<skins.length;i++){
                int col=i%COLS,row=i/COLS;
                int bx=STARTX+col*(BW+GAP),by=STARTY+row*(BH+GAP);
                if(new Rectangle(bx,by,BW,BH).contains(mx,my)){hovered=i;previewSkin=i;return;}
            }
        } else {
            BasketSkin[] baskets=BasketSkin.values();
            for(int i=0;i<baskets.length;i++){
                int col=i%COLS,row=i/COLS;
                int bx=STARTX+col*(BW+GAP),by=STARTY+row*(BH+GAP);
                if(new Rectangle(bx,by,BW,BH).contains(mx,my)){hovered=200+i;previewBasket=i;return;}
            }
        }
        // Tabs
        String[] tabs={"a","b"};int tabW=210;
        int startTab=(GamePanel.W-(tabs.length*tabW+(tabs.length-1)*8))/2;
        for(int i=0;i<2;i++){int tx=startTab+i*(tabW+8);if(new Rectangle(tx,80,tabW,34).contains(mx,my)){hovered=1000+i;return;}}
    }

    @Override
    public void onMouseClicked(MouseEvent e){
        int mx=e.getX(),my=e.getY();
        // Tab clicks
        int tabW=210;int startTab=(GamePanel.W-(2*tabW+8))/2;
        for(int i=0;i<2;i++){int tx=startTab+i*(tabW+8);if(new Rectangle(tx,80,tabW,34).contains(mx,my)){shopTab=i;return;}}
        // Back
        if(backBtn!=null&&backBtn.contains(mx,my)){panel.switchToWithFade(GameScreenType.MAIN_MENU);return;}
        if(shopTab==0){
            SkinType[] skins=SkinType.values();
            for(int i=0;i<skins.length;i++){
                int col=i%COLS,row=i/COLS;
                int bx=STARTX+col*(BW+GAP),by=STARTY+row*(BH+GAP);
                if(new Rectangle(bx,by,BW,BH).contains(mx,my)){
                    SkinType s=skins[i];
                    if(panel.getPlayerData().ownsSkin(s)){
                        panel.getPlayerData().equipSkin(s);
                    } else if(s.getCost()==0){
                        panel.getPlayerData().buySkin(s);panel.getPlayerData().equipSkin(s);
                    } else if(panel.getPlayerData().spendCoins(s.getCost())){
                        panel.getPlayerData().buySkin(s);panel.getPlayerData().equipSkin(s);
                    }
                    return;
                }
            }
        } else {
            BasketSkin[] baskets=BasketSkin.values();
            for(int i=0;i<baskets.length;i++){
                int col=i%COLS,row=i/COLS;
                int bx=STARTX+col*(BW+GAP),by=STARTY+row*(BH+GAP);
                if(new Rectangle(bx,by,BW,BH).contains(mx,my)){
                    BasketSkin b=baskets[i];
                    if(panel.getPlayerData().ownsBasket(b)){
                        panel.getPlayerData().equipBasket(b);
                    } else if(b.getCost()==0){
                        panel.getPlayerData().buyBasket(b);panel.getPlayerData().equipBasket(b);
                    } else if(panel.getPlayerData().spendCoins(b.getCost())){
                        panel.getPlayerData().buyBasket(b);panel.getPlayerData().equipBasket(b);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void onKeyPressed(KeyEvent e){
        if(e.getKeyCode()==KeyEvent.VK_ESCAPE) panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }
}