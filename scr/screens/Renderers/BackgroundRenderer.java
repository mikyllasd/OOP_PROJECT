package screens;

import utils.FontManager;
import utils.MathUtils;
import java.awt.*;
import java.util.List;
import java.util.Random;

public class BackgroundRenderer {

    public static void drawSky(Graphics2D g,
            int level, int tickCount,
            int arenaW, int h,
            List<?> clouds, List<?> rainDrops) {

        Color top, bot;
        if      (level <= 3) { top = new Color(72,158,255); bot = new Color(178,224,255); }
        else if (level <= 6) { top = new Color(255,160,40); bot = new Color(255,220,120); }
        else if (level <= 9) { top = new Color(200,60,30);  bot = new Color(255,140,60); }
        else                 { top = new Color(8,12,45);    bot = new Color(30,42,80); }

        GradientPaint sky = new GradientPaint(
                0, 0, top, 0, h - 80, bot);
        g.setPaint(sky);
        g.fillRect(0, 0, arenaW, h);
        g.setPaint(null);

        if (level <= 6) drawSun(g, arenaW, tickCount);
        else if (level >= 8) drawMoon(g, arenaW);

        drawGround(g, arenaW, h);
    }

    private static void drawSun(Graphics2D g,
            int arenaW, int tickCount) {
        int sx = arenaW - 80, sy = 60;
        g.setColor(new Color(255, 240, 120, 40));
        g.fillOval(sx - 18, sy - 18, 56, 56);
        g.setColor(new Color(255, 235, 80, 80));
        g.fillOval(sx - 10, sy - 10, 40, 40);
        g.setColor(new Color(255, 230, 60));
        g.fillOval(sx, sy, 22, 22);
        g.setColor(new Color(255, 230, 60, 130));
        g.setStroke(new BasicStroke(1.5f));
        for (int a = 0; a < 8; a++) {
            double angle = Math.toRadians(
                    a * 45 + tickCount * 0.3);
            int x1 = (int)(sx + 11 + Math.cos(angle) * 16);
            int y1 = (int)(sy + 11 + Math.sin(angle) * 16);
            int x2 = (int)(sx + 11 + Math.cos(angle) * 22);
            int y2 = (int)(sy + 11 + Math.sin(angle) * 22);
            g.drawLine(x1, y1, x2, y2);
        }
        g.setStroke(new BasicStroke(1f));
    }

    private static void drawMoon(Graphics2D g, int arenaW) {
        int mx = arenaW - 80, my = 55;
        g.setColor(new Color(220, 230, 255, 30));
        g.fillOval(mx - 12, my - 12, 48, 48);
        g.setColor(new Color(230, 238, 255));
        g.fillOval(mx, my, 26, 26);
        g.setColor(new Color(72, 90, 140));
        g.fillOval(mx + 6, my - 4, 20, 20);
    }

    private static void drawGround(Graphics2D g,
            int arenaW, int h) {
        GradientPaint grass = new GradientPaint(
                0, h - 80, new Color(45, 115, 35),
                0, h, new Color(28, 80, 20));
        g.setPaint(grass);
        g.fillRect(0, h - 80, arenaW, 80);
        g.setPaint(null);
        g.setColor(new Color(60, 145, 48));
        for (int i = 0; i < arenaW; i += 18)
            g.fillArc(i - 2, h - 86, 16, 14, 0, 180);
        g.setColor(new Color(90, 185, 65, 140));
        for (int i = 5; i < arenaW; i += 24)
            g.fillArc(i, h - 82, 10, 10, 0, 180);
    }

    private BackgroundRenderer() {}
}