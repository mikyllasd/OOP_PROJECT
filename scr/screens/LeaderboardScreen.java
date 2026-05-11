package screens;

import core.Screen;
import enums.GameScreenType;
import managers.GamePanel;
import models.ScoreEntry;
import utils.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class LeaderboardScreen extends Screen {

    private Rectangle backBtn;
    private int hovered = -1;

    private static final String[] RANKS = {
        "\uD83D\uDC51","\uD83E\uDD48","\uD83E\uDD49",
        "4\uFE0F\u20E3","5\uFE0F\u20E3","6\uFE0F\u20E3",
        "7\uFE0F\u20E3","8\uFE0F\u20E3","9\uFE0F\u20E3",
        "\uD83D\uDD1F"
    };

    public LeaderboardScreen(GamePanel panel) {
        super(panel);
    }

    @Override
    public void update() { tickCount++; }

    @Override
    public void draw(Graphics2D g) {
        GradientPaint bg = new GradientPaint(
                0, 0, new Color(18, 42, 13),
                0, GamePanel.H, new Color(8, 28, 6));
        g.setPaint(bg);
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);
        g.setPaint(null);

        RenderUtils.drawHeaderBar(g, GamePanel.W,
                "\uD83C\uDFC6 Leaderboard");

        List<ScoreEntry> top =
                panel.getScoreManager().getTop10();

        if (top.isEmpty()) {
            RenderUtils.drawCenteredText(g,
                    "No scores yet. Play to get on the board!",
                    GamePanel.W / 2, 250,
                    FontManager.getBody(17, Font.ITALIC),
                    new Color(158, 200, 138));
        }

        int rowH = 44, startY = 108;
        for (int i = 0; i < top.size(); i++) {
            ScoreEntry e = top.get(i);
            int ry    = startY + i * rowH;
            boolean t = i < 3;

            RenderUtils.drawGradientPanel(g, 30, ry,
                    GamePanel.W - 60, rowH - 6,
                    t ? new Color(62, 125, 42, 210)
                      : new Color(32, 72, 22, 165),
                    t ? new Color(45, 100, 30, 210)
                      : new Color(22, 52, 14, 165),
                    t ? new Color(148, 235, 100)
                      : new Color(78, 148, 58),
                    1f, 10);

            g.setFont(FontManager.getEmoji(20));
            g.setColor(Color.WHITE);
            g.drawString(RANKS[i], 44, ry + 26);

            g.setFont(FontManager.getBold(14));
            g.setColor(t ? new Color(255, 232, 100)
                         : Color.WHITE);
            g.drawString(e.getName(), 98, ry + 27);

            g.setFont(FontManager.getBodyBold(14));
            g.setColor(new Color(100, 225, 100));
            g.drawString("" + e.getScore(), 380, ry + 27);

            g.setFont(FontManager.getBody(12));
            g.setColor(new Color(158, 200, 138));
            g.drawString("Lv." + e.getLevel(),
                    518, ry + 27);

            if (!e.getDate().isEmpty()) {
                g.setColor(new Color(120, 160, 110));
                g.drawString(e.getDate(), 600, ry + 27);
            }
        }

        backBtn = new Rectangle(20, GamePanel.H - 55,
                140, 38);
        RenderUtils.drawButton(g, backBtn, "\u2190 Back",
                hovered == 0, FontManager.getBold(14));
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