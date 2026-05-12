package OOP_PROJECT.CatchTheBall.src.renderers;

import OOP_PROJECT.CatchTheBall.src.enums.BasketSkin;
import java.awt.*;

public class BasketRenderer {

    public static void draw(Graphics2D g, BasketSkin skin, int x, int y, int w, int h) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        switch(skin){
            case WOVEN:     drawWoven(g,x,y,w,h);     break;
            case METAL:     drawMetal(g,x,y,w,h);     break;
            case GOLDEN:    drawGolden(g,x,y,w,h);    break;
            case CART:      drawCart(g,x,y,w,h);      break;
            case DIAMOND:   drawDiamond(g,x,y,w,h);   break;
            case BAMBOO:    drawBamboo(g,x,y,w,h);    break;
            case CLAY:      drawClay(g,x,y,w,h);      break;
            case CRYSTAL:   drawCrystal(g,x,y,w,h);  break;
            case MAGIC:     drawMagic(g,x,y,w,h);     break;
            case LEGENDARY: drawLegendary(g,x,y,w,h); break;
        }
    }

    private static void drawWoven(Graphics2D g,int x,int y,int w,int h){
        GradientPaint gp=new GradientPaint(x,y,new Color(0xD4924A),x+w,y+h,new Color(0x8B5A1A));
        g.setPaint(gp);
        int[] bx={x,x+w,x+w-8,x+8};int[] by={y,y,y+h,y+h};
        g.fillPolygon(bx,by,4);g.setPaint(null);
        g.setColor(new Color(100,60,20,160));
        for(int row=1;row<=4;row++){int ly=y+h*row/5;g.drawLine(x+3,ly,x+w-3,ly);}
        for(int col=1;col<=5;col++){int lx=x+w*col/6;g.drawLine(lx,y+3,lx+4,y+h-3);}
        g.setColor(new Color(255,200,120,60));
        g.fillPolygon(new int[]{x,x+w/2,x+10},new int[]{y,y,y+h},3);
        g.setPaint(new GradientPaint(x,y,new Color(0xD4924A),x+w,y,new Color(0xA06828)));
        g.fillRoundRect(x-3,y-5,w+6,10,6,6);g.setPaint(null);
        g.setColor(new Color(80,50,15));g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(x-3,y-5,w+6,10,6,6);g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(0x8B5A1A));g.setStroke(new BasicStroke(3.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawArc(x+w/5,y-h/2,w*3/5,h/2,0,180);g.setStroke(new BasicStroke(1f));
    }

    private static void drawMetal(Graphics2D g,int x,int y,int w,int h){
        GradientPaint gp=new GradientPaint(x,y,new Color(0x8899AA),x+w,y,new Color(0xCCDCE8));
        g.setPaint(gp);
        int[] bx={x,x+w,x+w-6,x+6};int[] by={y,y,y+h,y+h};
        g.fillPolygon(bx,by,4);g.setPaint(null);
        g.setColor(new Color(0xD8E8F0));g.setStroke(new BasicStroke(1.5f));
        for(int i=1;i<=3;i++){int ly=y+h*i/4;g.drawLine(x+2,ly,x+w-2,ly);}
        g.setColor(new Color(0x6A7A8A));
        for(int i=1;i<=3;i++){int ly=y+h*i/4;g.fillOval(x+4,ly-3,6,6);g.fillOval(x+w-10,ly-3,6,6);}
        g.setStroke(new BasicStroke(1.5f));g.drawPolygon(bx,by,4);g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(255,255,255,50));
        g.fillPolygon(new int[]{x,x+w/3,x+6},new int[]{y,y,y+h-5},3);
        g.setPaint(new GradientPaint(x,y,new Color(0x7A8A9A),x+w,y,new Color(0xDCECF4)));
        g.fillRoundRect(x-3,y-5,w+6,10,5,5);g.setPaint(null);
        g.setColor(new Color(0x5A6A7A));g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(x-3,y-5,w+6,10,5,5);g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(0x8A9AAA));g.setStroke(new BasicStroke(3.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawArc(x+w/5,y-h/2,w*3/5,h/2,0,180);g.setStroke(new BasicStroke(1f));
    }

    private static void drawGolden(Graphics2D g,int x,int y,int w,int h){
        GradientPaint gp=new GradientPaint(x,y,new Color(0xB8860B),x+w/2,y,new Color(0xFFD700));
        g.setPaint(gp);
        int[] bx={x,x+w,x+w-8,x+8};int[] by={y,y,y+h,y+h};
        g.fillPolygon(bx,by,4);g.setPaint(null);
        g.setColor(new Color(100,80,0,80));
        for(int i=1;i<=3;i++){int ly=y+h*i/4;g.drawLine(x+2,ly,x+w-2,ly);}
        g.setColor(new Color(255,220,50,100));
        for(int i=0;i<3;i++) g.drawOval(x+10+i*15,y+5,12,h-12);
        g.setColor(new Color(255,255,200,80));
        g.fillPolygon(new int[]{x,x+w/2,x+8},new int[]{y,y,y+h},3);
        g.setPaint(new GradientPaint(x,y,new Color(0xC8960C),x+w/2,y,new Color(0xFFE840)));
        g.fillRoundRect(x-3,y-5,w+6,10,6,6);g.setPaint(null);
        g.setColor(new Color(0xB8860B));g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x-3,y-5,w+6,10,6,6);g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(0xB8860B));g.setStroke(new BasicStroke(3.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawArc(x+w/5,y-h/2,w*3/5,h/2,0,180);g.setStroke(new BasicStroke(1f));
    }

    private static void drawCart(Graphics2D g,int x,int y,int w,int h){
        int boxH=h-14,wheelR=12;
        g.setPaint(new GradientPaint(x,y,new Color(0xC8883A),x,y+boxH,new Color(0x8B5A1A)));
        g.fillRoundRect(x,y,w,boxH,4,4);g.setPaint(null);
        g.setColor(new Color(0x6B3A10));g.setStroke(new BasicStroke(0.8f));
        for(int i=1;i<=2;i++) g.drawLine(x,y+boxH*i/3,x+w,y+boxH*i/3);
        for(int i=1;i<=3;i++) g.drawLine(x+w*i/4,y,x+w*i/4,y+boxH);
        g.setStroke(new BasicStroke(1.5f));g.drawRoundRect(x,y,w,boxH,4,4);
        g.setColor(new Color(0x888888));g.fillRoundRect(x-1,y-3,w+2,6,3,3);
        for(int wx:new int[]{x+wheelR+4,x+w-wheelR-4}){
            int wy=y+boxH+wheelR-2;
            g.setColor(new Color(0x3A2208));g.setStroke(new BasicStroke(4f));
            g.drawOval(wx-wheelR,wy-wheelR,wheelR*2,wheelR*2);
            g.setColor(new Color(0x5A3A10));g.setStroke(new BasicStroke(1.5f));
            g.drawLine(wx,wy-wheelR,wx,wy+wheelR);
            g.drawLine(wx-wheelR,wy,wx+wheelR,wy);
            g.setColor(new Color(0x6B3A10));g.fillOval(wx-4,wy-4,8,8);
        }
        g.setStroke(new BasicStroke(1f));
    }

    private static void drawDiamond(Graphics2D g,int x,int y,int w,int h){
        int cx=x+w/2;
        int[] px={cx-w/2,cx,cx+w/2,cx+w/2-6,cx,cx-w/2+6};
        int[] py={y+h/4,y,y+h/4,y+h,y+h,y+h};
        g.setPaint(new GradientPaint(x,y,new Color(0xC8EEFF),x+w,y+h,new Color(0x60A8D0)));
        g.fillPolygon(px,py,6);g.setPaint(null);
        g.setColor(new Color(100,180,220,150));
        g.drawLine(cx,y,cx,y+h);g.drawLine(x,y+h/4,x+w,y+h/4);
        g.setColor(new Color(200,240,255,100));
        g.fillPolygon(new int[]{cx,cx+w/4,cx},new int[]{y,y+h/4,y+h/3},3);
        g.setColor(new Color(0x3880B8));g.setStroke(new BasicStroke(1.5f));
        g.drawPolygon(px,py,6);g.setStroke(new BasicStroke(1f));
        g.setPaint(new GradientPaint(x,y,new Color(0x5898C8),x+w,y,new Color(0xC8ECFF)));
        g.fillRoundRect(x-3,y-5,w+6,10,5,5);g.setPaint(null);
        g.setColor(new Color(0x3880B8));g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(x-3,y-5,w+6,10,5,5);g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(0x5898C8));g.setStroke(new BasicStroke(3.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawArc(x+w/5,y-h/2,w*3/5,h/2,0,180);g.setStroke(new BasicStroke(1f));
    }

    private static void drawBamboo(Graphics2D g,int x,int y,int w,int h){
        GradientPaint gp=new GradientPaint(x,y,new Color(120,180,60),x+w,y,new Color(80,140,30));
        g.setPaint(gp);
        int[] bx={x,x+w,x+w-8,x+8};int[] by={y,y,y+h,y+h};
        g.fillPolygon(bx,by,4);g.setPaint(null);
        g.setColor(new Color(60,110,20));g.setStroke(new BasicStroke(2.5f));
        for(int i=1;i<=4;i++){int ly=y+h*i/5;g.drawLine(x+3,ly,x+w-3,ly);}
        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(90,150,40));
        for(int i=1;i<=4;i++){int ly=y+h*i/5;g.fillOval(x+2,ly-3,w-4,6);}
        g.setColor(new Color(180,240,100,80));
        g.fillPolygon(new int[]{x,x+w/3,x+6},new int[]{y,y,y+h-5},3);
        g.setColor(new Color(50,100,15));
        g.fillRoundRect(x-3,y-5,w+6,10,6,6);
        g.setColor(new Color(100,180,40));g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x-3,y-5,w+6,10,6,6);g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(80,140,30));g.setStroke(new BasicStroke(3.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawArc(x+w/5,y-h/2,w*3/5,h/2,0,180);g.setStroke(new BasicStroke(1f));
    }

    private static void drawClay(Graphics2D g,int x,int y,int w,int h){
        GradientPaint gp=new GradientPaint(x,y,new Color(200,110,60),x+w,y+h,new Color(150,70,30));
        g.setPaint(gp);
        int[] bx={x+4,x+w-4,x+w,x};int[] by={y,y,y+h,y+h};
        g.fillPolygon(bx,by,4);g.setPaint(null);
        g.setColor(new Color(120,60,20,120));
        g.drawLine(x+w/3,y+5,x+w/3-3,y+h-8);
        g.drawLine(x+w*2/3,y+8,x+w*2/3+2,y+h-5);
        g.setColor(new Color(80,40,10));
        g.fillRect(x,y+h/3,w,5);
        g.setColor(new Color(220,160,80));
        g.fillRect(x,y+h/3+1,w,3);
        g.setColor(new Color(255,180,120,70));
        g.fillOval(x+4,y+4,w/3,h/3);
        g.setPaint(new GradientPaint(x,y,new Color(210,130,70),x+w,y,new Color(170,90,45)));
        g.fillRoundRect(x-4,y-6,w+8,12,8,8);g.setPaint(null);
        g.setColor(new Color(120,60,20));g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x-4,y-6,w+8,12,8,8);g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(150,80,40));g.setStroke(new BasicStroke(3.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawArc(x+w/5,y-h/2,w*3/5,h/2,0,180);g.setStroke(new BasicStroke(1f));
    }

    private static void drawCrystal(Graphics2D g,int x,int y,int w,int h){
        GradientPaint gp=new GradientPaint(x,y,new Color(180,220,255,200),x+w,y+h,new Color(100,160,240,180));
        g.setPaint(gp);
        int[] bx={x,x+w,x+w-6,x+6};int[] by={y,y,y+h,y+h};
        g.fillPolygon(bx,by,4);g.setPaint(null);
        g.setColor(new Color(200,240,255,150));
        g.fillPolygon(new int[]{x,x+w/2,x+8},new int[]{y,y,y+h},3);
        g.setColor(new Color(150,200,255,80));
        g.fillPolygon(new int[]{x+w/2,x+w,x+w-8},new int[]{y,y,y+h},3);
        g.setColor(new Color(255,255,255,220));
        int[][] sparkles={{x+10,y+8},{x+w-15,y+12},{x+w/2,y+h/2}};
        for(int[] sp:sparkles){
            g.drawLine(sp[0]-4,sp[1],sp[0]+4,sp[1]);
            g.drawLine(sp[0],sp[1]-4,sp[0],sp[1]+4);
        }
        g.setPaint(new GradientPaint(x,y,new Color(200,240,255),x+w,y,new Color(150,200,255)));
        g.fillRoundRect(x-3,y-5,w+6,10,6,6);g.setPaint(null);
        g.setColor(new Color(100,160,240));g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x-3,y-5,w+6,10,6,6);g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(150,200,255,200));g.setStroke(new BasicStroke(3f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawArc(x+w/5,y-h/2,w*3/5,h/2,0,180);g.setStroke(new BasicStroke(1f));
    }

    private static void drawMagic(Graphics2D g,int x,int y,int w,int h){
        GradientPaint gp=new GradientPaint(x,y,new Color(50,50,60),x+w,y+h,new Color(20,20,30));
        g.setPaint(gp);g.fillOval(x-4,y,w+8,h+4);g.setPaint(null);
        GradientPaint potionGp=new GradientPaint(x,y,new Color(50,200,80,200),x,y+20,new Color(20,150,50,100));
        g.setPaint(potionGp);g.fillOval(x,y-4,w,16);g.setPaint(null);
        g.setColor(new Color(100,255,120,180));
        g.fillOval(x+8,y-8,8,8);g.fillOval(x+w-18,y-6,6,6);g.fillOval(x+w/2,y-12,10,10);
        g.setColor(new Color(50,255,80,30));g.fillOval(x-8,y-8,w+16,h+16);
        g.setColor(new Color(40,40,50));
        g.fillRect(x+5,y+h,8,8);g.fillRect(x+w-13,y+h,8,8);
        g.setColor(new Color(80,80,90));g.setStroke(new BasicStroke(3f));
        g.drawOval(x,y-4,w,16);g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(80,80,90));g.setStroke(new BasicStroke(3.5f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawArc(x+w/5,y-h/2,w*3/5,h/2,0,180);g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(255,220,50,180));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(x-2,y+8,x+4,y+8);g.drawLine(x+1,y+4,x+1,y+12);
        g.drawLine(x+w-4,y+15,x+w+2,y+15);g.drawLine(x+w-1,y+11,x+w-1,y+19);
        g.setStroke(new BasicStroke(1f));
    }

    private static void drawLegendary(Graphics2D g,int x,int y,int w,int h){
        GradientPaint gp=new GradientPaint(x,y,new Color(180,140,20),x+w,y+h,new Color(100,70,5));
        g.setPaint(gp);g.fillRect(x,y,w,h);g.setPaint(null);
        g.setColor(new Color(140,100,10));
        for(int i=1;i<=3;i++) g.fillRect(x,y+h*i/4,w,2);
        g.setColor(new Color(220,180,30));
        g.fillRect(x,y,12,12);g.fillRect(x+w-12,y,12,12);
        g.fillRect(x,y+h-12,12,12);g.fillRect(x+w-12,y+h-12,12,12);
        g.setColor(new Color(255,215,0,40));g.fillRect(x-3,y-3,w+6,h+6);
        g.setColor(new Color(220,180,30));g.fillOval(x+w/2-8,y+h/2-8,16,16);
        g.setColor(new Color(180,140,10));g.fillOval(x+w/2-5,y+h/2-5,10,10);
        g.setColor(new Color(255,200,50,160));
        g.setFont(new Font("SansSerif",Font.BOLD,8));
        g.drawString("★",x+2,y+10);g.drawString("★",x+w-12,y+10);
        g.drawString("★",x+2,y+h-2);g.drawString("★",x+w-12,y+h-2);
        g.setPaint(new GradientPaint(x,y,new Color(255,215,30),x+w,y,new Color(200,160,10)));
        g.fillRoundRect(x-3,y-5,w+6,10,4,4);g.setPaint(null);
        g.setColor(new Color(150,110,5));g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(x-3,y-5,w+6,10,4,4);g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(200,160,20));g.setStroke(new BasicStroke(4f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.drawArc(x+w/5,y-h/2,w*3/5,h/2,0,180);g.setStroke(new BasicStroke(1f));
    }

    private BasketRenderer(){}
}