package OOP_PROJECT.CatchTheBall.src.renderers;

import OOP_PROJECT.CatchTheBall.src.entities.Character;
import OOP_PROJECT.CatchTheBall.src.enums.SkinType;
import OOP_PROJECT.CatchTheBall.src.utils.FontManager;

import java.awt.*;
import java.awt.geom.*;

public class CharacterRenderer {

    public static void draw(Graphics2D g, Character c) {
        float drawY = c.getY() + c.getBobOffset();
        if (c.getAnimState() == Character.ANIM_CATCH) drawY -= 8;
        else if (c.getAnimState() == Character.ANIM_SHAKE)
            drawY += (float)(Math.sin(c.getBobTimer() * 0.8) * 4);

        AffineTransform old = g.getTransform();
        if (c.getAnimState() == Character.ANIM_CELEBRATE) {
            double spinAngle = Math.toRadians(c.getAnimTimer() * 12.0);
            g.rotate(spinAngle, c.getX() + c.getWidth()/2f, drawY + c.getHeight()/2f);
        } else if (c.getLeanAngle() != 0) {
            g.rotate(Math.toRadians(c.getLeanAngle()),
                    c.getX() + c.getWidth()/2f, drawY + c.getHeight());
        } else if (c.getAnimState() == Character.ANIM_SAD) {
            drawY += 6;
        }

        drawCharacter(g, c.getSkin(), (int)(c.getX()), (int)drawY,
                c.getBobTimer(), c.getAnimState(), c.getAnimTimer());
        drawNameLabel(g, c, drawY);
        g.setTransform(old);
    }

    private static void drawCharacter(Graphics2D g, SkinType skin, int x, int y,
                                       int bobTimer, int animState, int animTimer) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        SkinColors sc = getSkinColors(skin);
        int cx = x + 25;

        // Shadow
        g.setColor(new Color(0,0,0,40));
        g.fillOval(cx-18, y+56, 36, 8);

        // Legs
        boolean walk = (bobTimer/8)%2==0;
        drawLeg(g, cx-8, y+38, walk?6:-4, sc.pantsColor);
        drawLeg(g, cx+4, y+38, walk?-4:6, sc.pantsColor);

        // Shoes
        drawShoe(g, cx-8+(walk?6:-4), y+50, sc.shoeColor);
        drawShoe(g, cx+4+(walk?-4:6), y+50, sc.shoeColor);

        // Body
        drawBody(g, cx, y+18, sc, skin);

        // Arms
        int armSwing = animState==1 ? -12 : (int)(Math.sin(bobTimer*0.12)*5);
        drawArm(g, cx-14, y+22, armSwing, sc.skinTone, sc.shirtColor);
        drawArm(g, cx+10, y+22, -armSwing, sc.skinTone, sc.shirtColor);

        // Neck
        g.setColor(sc.skinTone);
        g.fillRoundRect(cx-5, y+10, 10, 10, 5, 5);

        // Head
        drawHead(g, cx, y, sc, skin, bobTimer);
    }

    private static void drawLeg(Graphics2D g, int x, int y, int swing, Color pantsColor) {
        g.setColor(pantsColor);
        int[] lx={x+swing,x+swing+8,x+10,x};
        int[] ly={y,y,y+18,y+18};
        g.fillPolygon(lx,ly,4);
        g.setColor(new Color(255,255,255,30));
        g.fillRect(x+swing+1,y+2,3,14);
    }

    private static void drawShoe(Graphics2D g, int x, int y, Color shoeColor) {
        g.setColor(shoeColor);
        g.fillRoundRect(x-2,y,14,7,5,5);
        g.setColor(shoeColor.darker());
        g.fillRoundRect(x-2,y+4,14,3,3,3);
        g.setColor(new Color(255,255,255,50));
        g.fillRoundRect(x,y+1,5,2,2,2);
    }

    private static void drawBody(Graphics2D g, int cx, int y, SkinColors sc, SkinType skin) {
        int[] bx={cx-14,cx+14,cx+12,cx-12};
        int[] by={y,y,y+22,y+22};
        g.setColor(sc.shirtColor);
        g.fillPolygon(bx,by,4);
        g.setColor(new Color(255,255,255,40));
        g.fillRect(cx-12,y+1,10,18);
        drawShirtDetail(g,cx,y,sc,skin);
        g.setColor(new Color(80,50,20));
        g.fillRect(cx-13,y+18,25,4);
        g.setColor(new Color(180,140,60));
        g.fillRect(cx-3,y+19,6,2);
    }

    private static void drawShirtDetail(Graphics2D g, int cx, int y, SkinColors sc, SkinType skin) {
        switch(skin) {
            case FARMER_MALE:
                g.setColor(new Color(60,100,160));
                g.fillRect(cx-10,y+1,5,16);
                g.fillRect(cx+5,y+1,5,16);
                g.setColor(new Color(80,130,200,80));
                g.fillRect(cx-10,y+1,2,16);
                break;
            case FARMER_FEMALE:
                g.setColor(sc.shirtColor.brighter());
                for(int i=0;i<3;i++) g.fillOval(cx-8+i*7,y+5+i*4,5,5);
                g.setColor(new Color(255,180,200,120));
                g.fillRect(cx-13,y,25,4);
                break;
            case FARM_KID:
                g.setColor(new Color(255,255,255,60));
                for(int i=0;i<4;i++) g.fillRect(cx-12,y+2+i*5,24,2);
                break;
            case COWBOY:
                g.setColor(new Color(200,180,50));
                int[] sx={cx,cx+3,cx+8,cx+4,cx+5,cx,cx-5,cx-4,cx-8,cx-3};
                int[] sy={y+3,y+8,y+8,y+11,y+16,y+13,y+16,y+11,y+8,y+8};
                g.fillPolygon(sx,sy,10);
                break;
            case WIZARD:
                g.setColor(new Color(255,220,50,180));
                for(int i=0;i<4;i++) g.fillOval(cx-10+i*7,y+3+i*4,4,4);
                g.setColor(new Color(200,200,255));
                g.drawArc(cx-2,y+8,10,10,30,300);
                break;
            case NINJA:
                g.setColor(new Color(200,50,50));
                g.fillRect(cx-13,y+10,25,3);
                g.fillRect(cx-2,y+10,4,12);
                break;
            case ROYAL:
                g.setColor(new Color(220,180,60));
                g.fillRect(cx-13,y,25,5);
                g.setColor(new Color(200,50,50));
                for(int i=0;i<3;i++) g.fillOval(cx-6+i*6,y+1,4,4);
                break;
            case CHEF:
                g.setColor(Color.WHITE);
                g.fillRect(cx-13,y,25,22);
                g.setColor(new Color(220,220,220));
                for(int i=0;i<3;i++){
                    g.fillOval(cx-5,y+3+i*6,4,4);
                    g.fillOval(cx+1,y+3+i*6,4,4);
                }
                g.setColor(new Color(180,50,50));
                g.fillRect(cx-13,y,25,4);
                break;
            case PIRATE:
                g.setColor(new Color(40,30,20));
                g.fillRect(cx-13,y,25,22);
                g.setColor(new Color(200,160,40));
                g.fillRect(cx-13,y,3,22);
                g.fillRect(cx+10,y,3,22);
                for(int i=0;i<4;i++) g.fillOval(cx-2,y+2+i*5,4,4);
                break;
            case EXPLORER:
                g.setColor(new Color(180,160,100));
                g.fillRect(cx-13,y,25,22);
                g.setColor(new Color(160,140,80));
                g.fillRect(cx-12,y+8,9,7);
                g.fillRect(cx+3,y+8,9,7);
                g.fillRect(cx-12,y+8,9,1);
                g.fillRect(cx+3,y+8,9,1);
                break;
        }
    }

    private static void drawArm(Graphics2D g, int x, int y, int swing, Color skinTone, Color shirtColor) {
        g.setColor(shirtColor.darker());
        g.fillRoundRect(x,y+swing,8,14,4,4);
        g.setColor(skinTone);
        g.fillOval(x,y+swing+12,9,9);
        g.setColor(skinTone.darker());
        for(int i=0;i<3;i++) g.fillOval(x+2+i*2,y+swing+13,2,2);
    }

    private static void drawHead(Graphics2D g, int cx, int y, SkinColors sc, SkinType skin, int bobTimer) {
        g.setColor(sc.skinTone);
        g.fillOval(cx-14,y-2,28,26);
        g.setColor(new Color(255,160,140,80));
        g.fillOval(cx-14,y+8,10,7);
        g.fillOval(cx+4,y+8,10,7);
        drawEyes(g,cx,y,bobTimer,sc);
        drawMouth(g,cx,y,sc);
        g.setColor(sc.hairColor.darker());
        g.setStroke(new BasicStroke(2f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawLine(cx-10,y+4,cx-5,y+3);
        g.drawLine(cx+5,y+3,cx+10,y+4);
        g.setStroke(new BasicStroke(1f));
        drawHat(g,cx,y,sc,skin);
    }

    private static void drawEyes(Graphics2D g, int cx, int y, int bobTimer, SkinColors sc) {
        g.setColor(Color.WHITE);
        g.fillOval(cx-11,y+7,9,8);
        g.fillOval(cx+2,y+7,9,8);
        g.setColor(sc.eyeColor);
        g.fillOval(cx-9,y+9,5,5);
        g.fillOval(cx+4,y+9,5,5);
        g.setColor(new Color(20,10,5));
        g.fillOval(cx-8,y+10,3,3);
        g.fillOval(cx+5,y+10,3,3);
        g.setColor(Color.WHITE);
        g.fillOval(cx-7,y+10,2,2);
        g.fillOval(cx+6,y+10,2,2);
        if(bobTimer%120<4){
            g.setColor(sc.skinTone);
            g.fillRect(cx-11,y+9,9,5);
            g.fillRect(cx+2,y+9,9,5);
        }
    }

    private static void drawMouth(Graphics2D g, int cx, int y, SkinColors sc) {
        g.setColor(new Color(180,80,80));
        g.setStroke(new BasicStroke(2f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawArc(cx-5,y+15,10,6,200,140);
        g.setStroke(new BasicStroke(1f));
        g.setColor(Color.WHITE);
        g.fillRect(cx-3,y+16,6,3);
    }

    private static void drawHat(Graphics2D g, int cx, int y, SkinColors sc, SkinType skin) {
        switch(skin) {
            case FARMER_MALE:
                g.setColor(new Color(210,180,80));
                g.fillOval(cx-20,y-3,40,12);
                g.setColor(new Color(190,155,60));
                g.fillRoundRect(cx-11,y-14,22,16,8,8);
                g.setColor(new Color(160,120,40));
                g.fillRect(cx-11,y-3,22,3);
                g.setColor(new Color(255,220,100,120));
                g.fillOval(cx-18,y-2,15,5);
                g.setColor(new Color(180,140,50,100));
                for(int i=0;i<5;i++) g.drawLine(cx-10+i*5,y-14,cx-8+i*5,y+2);
                break;
            case FARMER_FEMALE:
                g.setColor(sc.hatColor);
                g.fillOval(cx-18,y-4,36,14);
                g.setColor(sc.hatColor.darker());
                g.fillRoundRect(cx-12,y-16,24,16,10,10);
                g.setColor(new Color(255,150,180));
                g.fillRect(cx-12,y-4,24,3);
                g.setColor(new Color(255,200,50));
                g.fillOval(cx+4,y-18,8,8);
                g.setColor(new Color(255,100,100));
                for(int i=0;i<5;i++){
                    double a=Math.toRadians(i*72);
                    g.fillOval((int)(cx+8+Math.cos(a)*5)-3,(int)(y-14+Math.sin(a)*5)-3,6,6);
                }
                break;
            case FARM_KID:
                g.setColor(new Color(200,60,60));
                g.fillOval(cx-13,y-12,26,18);
                g.fillOval(cx-16,y-1,32,8);
                g.setColor(new Color(255,255,255,180));
                g.drawLine(cx,y-12,cx,y-3);
                g.setFont(new Font("SansSerif",Font.BOLD,8));
                g.setColor(new Color(220,220,50));
                g.drawString("★",cx-4,y-2);
                break;
            case COWBOY:
                g.setColor(new Color(130,80,30));
                g.fillOval(cx-22,y-2,44,10);
                g.fillRoundRect(cx-13,y-18,26,20,6,6);
                g.setColor(new Color(180,130,60));
                g.fillRect(cx-13,y-4,26,3);
                g.setColor(new Color(255,200,100,80));
                g.fillOval(cx-20,y-1,18,5);
                break;
            case WIZARD:
                g.setColor(new Color(80,40,140));
                int[] hx={cx-16,cx+16,cx+8,cx-8,cx};
                int[] hy={y-2,y-2,y-10,y-10,y-30};
                g.fillPolygon(hx,hy,5);
                g.setColor(new Color(120,60,200));
                g.fillOval(cx-16,y-5,32,10);
                g.setColor(new Color(255,230,50));
                g.fillOval(cx-3,y-25,6,6);
                g.fillOval(cx+3,y-15,4,4);
                g.fillOval(cx-8,y-14,4,4);
                break;
            case NINJA:
                g.setColor(new Color(20,20,20));
                g.fillRect(cx-14,y+5,28,14);
                g.fillRect(cx-12,y-2,24,10);
                g.setColor(new Color(200,50,50));
                g.fillRect(cx-14,y+3,28,4);
                g.setColor(new Color(60,60,60));
                g.fillRect(cx-10,y+7,20,4);
                g.setColor(new Color(30,30,30));
                for(int i=0;i<4;i++){
                    int[] spx={cx-10+i*7,cx-6+i*7,cx-13+i*7};
                    int[] spy={y-10,y-2,y-2};
                    g.fillPolygon(spx,spy,3);
                }
                break;
            case ROYAL:
                g.setColor(sc.hairColor);
                g.fillOval(cx-13,y-2,26,16);
                g.setColor(new Color(220,180,30));
                int[] crx={cx-14,cx-14,cx-9,cx-6,cx-1,cx+1,cx+6,cx+9,cx+14,cx+14};
                int[] cry={y+2,y-10,y-6,y-14,y-8,y-14,y-6,y-10,y-10,y+2};
                g.fillPolygon(crx,cry,10);
                g.setColor(new Color(200,50,50));
                g.fillOval(cx-4,y-13,6,6);
                g.setColor(new Color(50,100,200));
                g.fillOval(cx-11,y-8,5,5);
                g.fillOval(cx+6,y-8,5,5);
                break;
            case CHEF:
                g.setColor(Color.WHITE);
                g.fillRoundRect(cx-12,y-28,24,30,8,8);
                g.fillRect(cx-14,y-4,28,6);
                g.setColor(new Color(230,230,230));
                g.fillRect(cx-10,y-26,4,24);
                break;
            case PIRATE:
                g.setColor(new Color(20,20,20));
                g.fillOval(cx-18,y-3,36,10);
                int[] px2={cx-12,cx,cx+12};
                int[] py2={y-2,y-18,y-2};
                g.fillPolygon(px2,py2,3);
                g.setColor(new Color(200,160,40));
                g.fillRect(cx-12,y-3,24,2);
                g.setColor(Color.WHITE);
                g.fillOval(cx-3,y-16,6,6);
                break;
            case EXPLORER:
                g.setColor(new Color(180,150,80));
                g.fillOval(cx-20,y-2,40,10);
                g.fillRoundRect(cx-13,y-16,26,18,8,8);
                g.setColor(new Color(150,120,60));
                g.fillRect(cx-13,y-2,26,3);
                g.setColor(new Color(220,190,100,100));
                g.fillOval(cx-18,y-1,16,5);
                break;
        }
        if(skin==SkinType.FARMER_MALE||skin==SkinType.FARMER_FEMALE){
            g.setColor(sc.hairColor);
            g.fillRect(cx-12,y+16,24,6);
        }
    }

    private static void drawNameLabel(Graphics2D g, Character c, float drawY) {
        g.setFont(FontManager.getBodyBold(10));
        FontMetrics fm=g.getFontMetrics();
        int nw=fm.stringWidth(c.getFarmerName());
        int nx=(int)(c.getX()+(c.getWidth()-nw)/2f);
        g.setColor(new Color(0,0,0,140));
        g.fillRoundRect(nx-4,(int)(drawY-20),nw+8,16,6,6);
        g.setColor(Color.WHITE);
        g.drawString(c.getFarmerName(),nx,(int)(drawY-7));
    }

    private static class SkinColors {
        Color skinTone,hairColor,eyeColor,shirtColor,pantsColor,shoeColor,hatColor;
        SkinColors(Color sk,Color h,Color e,Color sh,Color p,Color so,Color hat){
            skinTone=sk;hairColor=h;eyeColor=e;
            shirtColor=sh;pantsColor=p;shoeColor=so;hatColor=hat;
        }
    }

    private static SkinColors getSkinColors(SkinType skin) {
        switch(skin) {
            case FARMER_MALE:
                return new SkinColors(new Color(220,175,120),new Color(110,70,30),
                    new Color(60,100,180),new Color(100,140,200),
                    new Color(60,90,160),new Color(60,40,20),new Color(210,180,80));
            case FARMER_FEMALE:
                return new SkinColors(new Color(240,195,145),new Color(180,100,60),
                    new Color(80,160,80),new Color(240,140,160),
                    new Color(200,100,130),new Color(180,100,120),new Color(220,160,180));
            case FARM_KID:
                return new SkinColors(new Color(255,210,160),new Color(200,150,80),
                    new Color(100,180,80),new Color(100,180,240),
                    new Color(80,130,200),new Color(80,60,200),new Color(220,60,60));
            case COWBOY:
                return new SkinColors(new Color(200,155,100),new Color(80,50,20),
                    new Color(100,70,30),new Color(180,120,60),
                    new Color(100,70,40),new Color(50,30,10),new Color(130,80,30));
            case WIZARD:
                return new SkinColors(new Color(210,180,155),new Color(220,215,200),
                    new Color(120,80,200),new Color(100,60,180),
                    new Color(80,40,160),new Color(50,30,100),new Color(80,40,140));
            case NINJA:
                return new SkinColors(new Color(180,140,100),new Color(20,20,20),
                    new Color(50,180,50),new Color(20,20,20),
                    new Color(10,10,10),new Color(15,15,15),new Color(20,20,20));
            case ROYAL:
                return new SkinColors(new Color(230,185,130),new Color(60,40,100),
                    new Color(100,60,180),new Color(120,60,160),
                    new Color(80,40,120),new Color(60,40,80),new Color(220,180,30));
            case CHEF:
                return new SkinColors(new Color(230,185,130),new Color(50,40,30),
                    new Color(80,140,80),Color.WHITE,
                    new Color(40,40,40),new Color(30,30,30),Color.WHITE);
            case PIRATE:
                return new SkinColors(new Color(200,155,100),new Color(20,15,10),
                    new Color(60,180,200),new Color(40,30,20),
                    new Color(30,20,10),new Color(20,15,5),new Color(20,20,20));
            case EXPLORER:
                return new SkinColors(new Color(190,145,90),new Color(90,60,30),
                    new Color(100,160,60),new Color(180,160,100),
                    new Color(120,100,60),new Color(80,60,30),new Color(180,150,80));
            default:
                return new SkinColors(new Color(220,175,120),new Color(110,70,30),
                    new Color(60,100,180),new Color(100,140,200),
                    new Color(60,90,160),new Color(60,40,20),new Color(210,180,80));
        }
    }

    private CharacterRenderer(){}
}