package OOP_PROJECT.CatchTheBall.src.renderers;

import java.awt.*;

public class BackgroundRenderer {

    public static void drawSky(Graphics2D g, int level, int tickCount,
                               int arenaW, int h) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color top,bot;
        if      (level<=3) {top=new Color(72,158,255); bot=new Color(178,224,255);}
        else if (level<=6) {top=new Color(255,160,40);  bot=new Color(255,220,120);}
        else if (level<=9) {top=new Color(200,60,30);   bot=new Color(255,140,60);}
        else               {top=new Color(8,12,45);     bot=new Color(30,42,80);}
        g.setPaint(new GradientPaint(0,0,top,0,h-80,bot));
        g.fillRect(0,0,arenaW,h);g.setPaint(null);
        if(level<=6) drawSun(g,arenaW,tickCount);
        else         drawMoon(g,arenaW,tickCount);
        drawClouds(g,arenaW,tickCount,level);
        drawMountains(g,arenaW,h);
        drawFarmScene(g,arenaW,h,tickCount,level);
        drawGround(g,arenaW,h,tickCount);
    }

    private static void drawSun(Graphics2D g,int arenaW,int tick){
        int sx=arenaW-80,sy=55;
        g.setColor(new Color(255,230,60,30));g.fillOval(sx-20,sy-20,62,62);
        g.setColor(new Color(255,230,60,50));g.fillOval(sx-10,sy-10,42,42);
        GradientPaint sg=new GradientPaint(sx,sy,new Color(255,240,100),sx+22,sy+22,new Color(255,180,0));
        g.setPaint(sg);g.fillOval(sx,sy,22,22);g.setPaint(null);
        g.setColor(new Color(255,230,60,130));g.setStroke(new BasicStroke(1.5f));
        for(int a=0;a<8;a++){
            double angle=Math.toRadians(a*45+tick*0.3);
            g.drawLine((int)(sx+11+Math.cos(angle)*16),(int)(sy+11+Math.sin(angle)*16),
                       (int)(sx+11+Math.cos(angle)*24),(int)(sy+11+Math.sin(angle)*24));
        }
        g.setStroke(new BasicStroke(1f));
    }

    private static void drawMoon(Graphics2D g,int arenaW,int tick){
        int mx=arenaW-80,my=50;
        g.setColor(new Color(220,230,255,25));g.fillOval(mx-14,my-14,50,50);
        g.setColor(new Color(230,238,255));g.fillOval(mx,my,28,28);
        g.setColor(new Color(72,90,140));g.fillOval(mx+7,my-3,22,22);
        g.setColor(new Color(255,255,255,200));
        int[][] stars={{50,25},{115,12},{200,40},{310,18},{440,32},{530,10}};
        for(int[] s:stars) g.fillOval(s[0],s[1],3,3);
        int stx=(int)((tick*2)%(arenaW+100))-50;
        g.setColor(new Color(255,255,200,160));g.setStroke(new BasicStroke(1.5f));
        g.drawLine(stx,20,stx+30,14);g.setStroke(new BasicStroke(1f));
    }

    private static void drawClouds(Graphics2D g,int arenaW,int tick,int level){
        if(level>9) return;
        int alpha=level<=3?210:150;
        g.setColor(new Color(255,255,255,alpha));
        int off=(int)(tick*0.35)%(arenaW+220);
        drawCloud(g,80-off+arenaW,55,90,32);
        drawCloud(g,280-off+arenaW,42,65,24);
        drawCloud(g,470-off+arenaW,68,110,38);
        drawCloud(g,680-off+arenaW,50,75,28);
        int off2=(int)(tick*0.18)%(arenaW+300);
        g.setColor(new Color(255,255,255,alpha-40));
        drawCloud(g,150-off2+arenaW,88,55,18);
        drawCloud(g,400-off2+arenaW,75,70,22);
    }

    private static void drawCloud(Graphics2D g,int x,int y,int w,int h){
        g.fillOval(x,y,w,h);
        g.fillOval(x+w/4,y-h/3,w/2,h);
        g.fillOval(x+w/2,y,w/2,h-4);
        g.fillOval(x+w/6,y+h/4,w*2/3,h*2/3);
    }

    private static void drawMountains(Graphics2D g,int arenaW,int h){
        g.setColor(new Color(100,140,180,100));
        int[] mx1={0,80,160,240,320,400,480,560,640,arenaW,arenaW,0};
        int[] my1={h-180,h-260,h-200,h-290,h-220,h-270,h-210,h-280,h-230,h-190,h-80,h-80};
        g.fillPolygon(mx1,my1,12);
        g.setColor(new Color(80,140,60,120));
        int[] mx2={0,100,200,300,400,500,arenaW,arenaW,0};
        int[] my2={h-120,h-180,h-140,h-190,h-150,h-170,h-130,h-80,h-80};
        g.fillPolygon(mx2,my2,9);
    }

    private static void drawFarmScene(Graphics2D g,int arenaW,int h,int tick,int level){
        int groundY=h-80;
        drawRiceField(g,10,groundY-44,175,50,tick);
        drawFence(g,0,groundY-8,arenaW);
        drawFarmHouse(g,arenaW-235,groundY-125,165,125,tick,level);
        drawBarn(g,145,groundY-95,80,95,level);
        if(level<=6) drawCow(g,arenaW/2+15,groundY-40,tick);
        drawTree(g,28,groundY-72,tick);
        drawTree(g,arenaW-62,groundY-72,tick);
        drawFlowers(g,205,groundY-10,tick);
        drawFlowers(g,385,groundY-10,tick);
        drawWindmill(g,arenaW/2-22,groundY-105,tick);
    }

    private static void drawRiceField(Graphics2D g,int x,int y,int w,int h,int tick){
        g.setColor(new Color(100,170,220,80));g.fillRect(x,y+h/2,w,h/2);
        for(int row=0;row<4;row++){
            int ry=y+row*10;
            for(int col=0;col<w/12;col++){
                int rx=x+col*12;
                int sway=(int)(Math.sin(tick*0.05+col*0.4)*2);
                g.setColor(new Color(70,160,50));g.fillRect(rx+sway,ry,2,8);
                g.setColor(new Color(50,130,35));g.fillRect(rx+3+sway,ry+2,2,6);
            }
        }
        g.setColor(new Color(80,50,20));g.setStroke(new BasicStroke(1.5f));
        g.drawRect(x,y,w,h);g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("SansSerif",Font.PLAIN,9));
        g.setColor(new Color(50,120,30));g.drawString("Rice Field",x+4,y-2);
    }

    private static void drawFence(Graphics2D g,int x,int y,int w){
        g.setColor(new Color(200,170,110));g.fillRect(x,y+5,w,5);
        g.fillRect(x,y+16,w,4);
        for(int px=x;px<x+w;px+=30){
            g.setColor(new Color(170,130,70));g.fillRect(px,y,6,28);
            g.setColor(new Color(210,175,100));g.fillOval(px-1,y-3,8,7);
        }
    }

    private static void drawFarmHouse(Graphics2D g,int x,int y,int w,int h,int tick,int level){
        // Wall
        GradientPaint wallGp=new GradientPaint(x,y+h/3,new Color(245,225,185),x+w,y+h,new Color(205,185,145));
        g.setPaint(wallGp);g.fillRect(x,y+h/3,w,h*2/3);g.setPaint(null);
        g.setColor(new Color(190,170,130,100));
        for(int row=0;row<6;row++) g.drawLine(x,y+h/3+row*14,x+w,y+h/3+row*14);
        // Roof
        int[] rx={x-10,x+w/2,x+w+10};int[] ry={y+h/3,y,y+h/3};
        GradientPaint roofGp=new GradientPaint(x,y,new Color(185,65,45),x+w,y+h/3,new Color(145,42,28));
        g.setPaint(roofGp);g.fillPolygon(rx,ry,3);g.setPaint(null);
        g.setColor(new Color(100,30,15,120));
        for(int row=0;row<5;row++){int ry2=y+row*(h/3)/5;int indent=row*7;g.drawLine(x-10+indent,ry2,x+w+10-indent,ry2);}
        // Chimney
        g.setColor(new Color(155,105,65));g.fillRect(x+w-42,y-22,18,h/3+12);
        g.setColor(new Color(125,82,52));g.fillRect(x+w-45,y-24,24,8);
        if(level<=6){
            for(int s=0;s<3;s++){
                int sx2=(int)(Math.sin(tick*0.03+s)*4);
                g.setColor(new Color(200,200,200,60-s*15));
                g.fillOval(x+w-39+sx2,y-42-s*14,10+s*3,10+s*3);
            }
        }
        // Door
        GradientPaint doorGp=new GradientPaint(x+w/2-14,0,new Color(100,60,20),x+w/2+14,0,new Color(80,45,15));
        g.setPaint(doorGp);g.fillRoundRect(x+w/2-14,y+h*2/3,28,h/3,5,5);g.setPaint(null);
        g.setColor(new Color(200,160,80));g.fillOval(x+w/2+5,y+h*5/6,5,5);
        drawWindow(g,x+12,y+h/2,30,25,tick,level);
        drawWindow(g,x+w-42,y+h/2,30,25,tick,level);
        g.setColor(new Color(120,90,50));g.setStroke(new BasicStroke(1.5f));
        g.drawRect(x,y+h/3,w,h*2/3);g.drawPolygon(rx,ry,3);g.setStroke(new BasicStroke(1f));
    }

    private static void drawWindow(Graphics2D g,int x,int y,int w,int h,int tick,int level){
        g.setColor(new Color(150,100,50));g.fillRoundRect(x-3,y-3,w+6,h+6,4,4);
        Color gc=level<=6?new Color(200,230,255,180):new Color(255,240,150,180);
        g.setColor(gc);g.fillRoundRect(x,y,w,h,3,3);
        g.setColor(new Color(100,80,50,150));
        g.drawLine(x+w/2,y,x+w/2,y+h);g.drawLine(x,y+h/2,x+w,y+h/2);
        g.setColor(new Color(255,255,255,100));g.fillOval(x+2,y+2,8,6);
        if(level>6){g.setColor(new Color(255,200,80,40));g.fillOval(x-5,y-5,w+10,h+10);}
    }

    private static void drawBarn(Graphics2D g,int x,int y,int w,int h,int level){
        GradientPaint barnGp=new GradientPaint(x,y,new Color(185,52,42),x+w,y+h,new Color(142,32,27));
        g.setPaint(barnGp);g.fillRect(x,y,w,h);g.setPaint(null);
        g.setColor(new Color(100,20,15,120));
        for(int row=1;row<6;row++) g.drawLine(x,y+h*row/6,x+w,y+h*row/6);
        int[] rx={x-8,x+w/2,x+w+8};int[] ry={y,y-35,y};
        g.setColor(new Color(82,52,32));g.fillPolygon(rx,ry,3);
        g.setColor(new Color(52,32,16));g.setStroke(new BasicStroke(3f));
        g.drawLine(x+w/2,y-35,x+w/2,y);g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(100,60,20));g.fillRoundRect(x+w/2-15,y+h/2,30,h/2,4,4);
        g.setColor(new Color(140,90,40));g.drawLine(x+w/2,y+h/2,x+w/2,y+h);
        g.setColor(new Color(80,50,15));g.setStroke(new BasicStroke(2f));
        g.drawLine(x+w/2-14,y+h/2,x+w/2+14,y+h-2);
        g.drawLine(x+w/2+14,y+h/2,x+w/2-14,y+h-2);g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(80,30,20));g.setStroke(new BasicStroke(1.5f));
        g.drawRect(x,y,w,h);g.setStroke(new BasicStroke(1f));
        // Barn label
        g.setFont(new Font("SansSerif",Font.PLAIN,8));
        g.setColor(new Color(255,220,180));g.drawString("BARN",x+w/2-10,y-2);
    }

    private static void drawCow(Graphics2D g,int x,int y,int tick){
        g.setColor(new Color(240,230,210));g.fillOval(x,y+10,50,28);
        g.setColor(new Color(50,40,30));g.fillOval(x+8,y+13,14,10);g.fillOval(x+30,y+20,10,8);
        g.setColor(new Color(240,230,210));g.fillOval(x+40,y+5,22,18);
        g.setColor(new Color(220,180,160));g.fillOval(x+50,y+14,12,9);
        g.setColor(new Color(160,100,100));g.fillOval(x+52,y+16,3,3);g.fillOval(x+57,y+16,3,3);
        g.setColor(new Color(20,15,10));g.fillOval(x+48,y+8,4,4);
        g.setColor(Color.WHITE);g.fillOval(x+49,y+8,1,1);
        g.setColor(new Color(230,180,160));g.fillOval(x+42,y+3,8,7);
        g.setColor(new Color(200,180,100));g.fillOval(x+44,y,6,8);g.fillOval(x+54,y,6,8);
        g.setColor(new Color(230,220,200));
        int legSwing=(int)(Math.sin(tick*0.08)*4);
        g.fillRect(x+8,y+35,7,16+legSwing);g.fillRect(x+18,y+35,7,16-legSwing);
        g.fillRect(x+30,y+35,7,16+legSwing);g.fillRect(x+40,y+35,7,16-legSwing);
        g.setColor(new Color(50,40,30));
        g.fillRect(x+8,y+49+legSwing,7,4);g.fillRect(x+18,y+49-legSwing,7,4);
        g.fillRect(x+30,y+49+legSwing,7,4);g.fillRect(x+40,y+49-legSwing,7,4);
        g.setColor(new Color(200,180,160));g.setStroke(new BasicStroke(2f));
        int tSwing=(int)(Math.sin(tick*0.1)*8);
        g.drawArc(x-12+tSwing,y+18,20,20,0,200);g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(230,180,180));g.fillOval(x+18,y+33,14,8);
    }

    private static void drawTree(Graphics2D g,int x,int y,int tick){
        GradientPaint tp=new GradientPaint(x+8,y+50,new Color(100,65,25),x+20,y+50,new Color(70,45,15));
        g.setPaint(tp);g.fillRoundRect(x+8,y+50,14,42,5,5);g.setPaint(null);
        g.setColor(new Color(80,50,20));g.fillOval(x+2,y+84,12,8);g.fillOval(x+18,y+84,12,8);
        int sway=(int)(Math.sin(tick*0.04)*3);
        g.setColor(new Color(40,130,40));g.fillOval(x-8+sway,y+30,46,30);
        g.setColor(new Color(50,155,45));g.fillOval(x-2+sway,y+12,40,32);
        g.setColor(new Color(60,180,50));g.fillOval(x+2+sway,y,34,28);
        g.setColor(new Color(120,220,80,80));g.fillOval(x+4+sway,y+4,12,10);
        g.setColor(new Color(220,40,40));
        g.fillOval(x+6+sway,y+28,8,8);g.fillOval(x+20+sway,y+22,7,7);
        // Apple stems
        g.setColor(new Color(80,50,20));g.setStroke(new BasicStroke(1f));
        g.drawLine(x+10+sway,y+28,x+10+sway,y+24);
        g.drawLine(x+23+sway,y+22,x+23+sway,y+18);
        g.setStroke(new BasicStroke(1f));
    }

    private static void drawFlowers(Graphics2D g,int x,int y,int tick){
        Color[] pc={new Color(255,100,100),new Color(255,200,50),new Color(200,100,255),new Color(100,200,255)};
        for(int f=0;f<4;f++){
            int fx=x+f*18;int sway=(int)(Math.sin(tick*0.06+f)*2);
            g.setColor(new Color(60,140,40));g.fillRect(fx+sway,y-12,2,14);
            g.setColor(pc[f%pc.length]);
            for(int p=0;p<5;p++){
                double a=Math.toRadians(p*72);
                g.fillOval((int)(fx+sway+Math.cos(a)*4)-2,(int)(y-16+Math.sin(a)*4)-2,5,5);
            }
            g.setColor(new Color(255,230,50));g.fillOval(fx+sway-2,y-18,6,6);
        }
    }

    private static void drawWindmill(Graphics2D g,int x,int y,int tick){
        // Tower
        int[] tx={x+(30-18)/2,x+(30+18)/2,x+30,x};
        int[] ty={y,y,y+70,y+70};
        GradientPaint towerGp=new GradientPaint(x,y,new Color(220,200,160),x+30,y,new Color(180,160,120));
        g.setPaint(towerGp);g.fillPolygon(tx,ty,4);g.setPaint(null);
        g.setColor(new Color(100,70,30));g.fillRoundRect(x+7,y+45,16,25,4,4);
        // Hub
        g.setColor(new Color(150,100,50));g.fillOval(x+11,y+12,8,8);
        // Blades
        double angle=tick*0.04;
        for(int b=0;b<4;b++){
            double ba=angle+b*Math.PI/2;
            int bx2=(int)(x+15+Math.cos(ba)*28);int by2=(int)(y+16+Math.sin(ba)*28);
            g.setColor(new Color(240,220,180));g.setStroke(new BasicStroke(8f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g.drawLine(x+15,y+16,bx2,by2);
            g.setColor(new Color(200,180,140));g.setStroke(new BasicStroke(3f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
            g.drawLine(x+15,y+16,bx2,by2);
        }
        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(180,130,60));g.fillOval(x+10,y+11,10,10);
        g.setColor(new Color(220,180,80));g.fillOval(x+12,y+13,6,6);
        g.setColor(new Color(80,50,20));g.setStroke(new BasicStroke(1.5f));
        g.drawPolygon(tx,ty,4);g.setStroke(new BasicStroke(1f));
    }

    private static void drawGround(Graphics2D g,int arenaW,int h,int tick){
        g.setPaint(new GradientPaint(0,h-80,new Color(55,130,40),0,h,new Color(35,95,25)));
        g.fillRect(0,h-80,arenaW,80);g.setPaint(null);
        // Path
        g.setColor(new Color(160,120,60,140));
        int[] pathX={arenaW/2-25,arenaW/2+25,arenaW/2+20,arenaW/2-20};
        int[] pathY={h-80,h-80,h,h};
        g.fillPolygon(pathX,pathY,4);
        // Grass tufts
        g.setColor(new Color(70,160,50));
        for(int i=0;i<arenaW;i+=16){
            int sway=(int)(Math.sin(tick*0.04+i*0.15)*2);
            g.fillArc(i-2+sway,h-86,14,12,0,180);
        }
        g.setColor(new Color(100,200,65,140));
        for(int i=6;i<arenaW;i+=22){
            int sway=(int)(Math.sin(tick*0.04+i*0.2)*2);
            g.fillArc(i+sway,h-82,9,9,0,180);
        }
        g.setColor(new Color(40,110,30));g.setStroke(new BasicStroke(2f));
        g.drawLine(0,h-80,arenaW,h-80);g.setStroke(new BasicStroke(1f));
    }

    private BackgroundRenderer(){}
}