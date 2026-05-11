package screens;

import core.Screen;
import enums.GameScreenType;
import managers.GamePanel;
import models.Achievement;
import utils.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AchievementsScreen extends Screen {

    private Rectangle backBtn;
    private int hovered = -1;

    public AchievementsScreen(GamePanel panel) {
        super(panel);
    }

    @Override
    public void update() { tickCount++; }

    @Override
    public void draw(Graphics2D g) {
        GradientPaint bg = new GradientPaint(
                0, 0, new Color(22, 43, 18),
                0, GamePanel.H, new Color(12, 28, 10));
        g.setPaint(bg);
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);
        g.setPaint(null);

        RenderUtils.drawHeaderBar(g, GamePanel.W,
                "\uD83C\uDFC5 Achievements");

        List<Achievement> all =
                panel.getScreenManager()
                     .getGameScreen() != null
                ? java.util.Collections.emptyList()
                : java.util.Collections.emptyList();

        int cols = 3, bw = 230, bh = 110;
        int sx = 40, sy = 108;

        for (int i = 0; i < all.size(); i++) {
            Achievement a = all.get(i);
            int col = i % cols, row = i / cols;
            int bx  = sx + col * (bw + 20);
            int by  = sy + row * (bh + 15);
            drawAchievementCard(g, a, bx, by, bw, bh);
        }

        backBtn = new Rectangle(20, GamePanel.H - 55,
                140, 38);
        RenderUtils.drawButton(g, backBtn, "\u2190 Back",
                hovered == 0, FontManager.getBold(14));
    }

    private void drawAchievementCard(Graphics2D g,
            Achievement a,
            int x, int y, int w, int h) {
        boolean u = a.isUnlocked();
        RenderUtils.drawGradientPanel(g, x, y, w, h,
                u ? new Color(52, 105, 36)
                  : new Color(28, 48, 22),
                u ? new Color(36, 80, 24)
                  : new Color(20, 38, 16),
                u ? new Color(118, 222, 80)
                  : new Color(58, 88, 48),
                u ? 2f : 1f, 12);

        g.setFont(FontManager.getEmoji(28));
        g.setColor(u ? Color.WHITE
                     : new Color(100, 118, 88));
        g.drawString(a.getEmoji(), x + 12, y + 40);

        g.setFont(FontManager.getBold(12));
        g.setColor(u ? new Color(200, 255, 148)
                     : new Color(98, 128, 88));
        g.drawString(a.getName(), x + 52, y + 30);

        g.setFont(FontManager.getBody(11));
        g.setColor(u ? new Color(158, 212, 128)
                     : new Color(78, 108, 68));
        g.drawString(a.getDescription(), x + 52, y + 50);

        if (!u && a.getProgressTarget() > 1) {
            float prog = a.getProgressRatio();
            RenderUtils.drawProgressBar(g,
                    x + 12, y + h - 22, w - 24, 8,
                    prog,
                    new Color(0, 0, 0, 80),
                    new Color(80, 180, 80),
                    new Color(140, 255, 100));
            g.setFont(FontManager.getBody(10));
            g.setColor(new Color(160, 200, 140));
            g.drawString(a.getProgressCurrent()
                    + " / " + a.getProgressTarget(),
                    x + 12, y + h - 26);
        }

        if (u) {
            g.setFont(FontManager.getBodyBold(11));
            g.setColor(new Color(100, 225, 80));
            g.drawString("\u2713 UNLOCKED", x + 52, y + 76);
        }
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        hovered = backBtn != null
                && backBtn.contains(e.getX(), e.getY())
                ? 0 : -1;
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (backBtn != null
                && backBtn.contains(e.getX(), e.getY()))
            panel.getScreenManager()
                 .switchTo(GameScreenType.MAIN_MENU);
    }
}