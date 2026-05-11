package screens;

import core.Screen;
import enums.GameScreenType;
import managers.GamePanel;
import models.FarmProgression;
import utils.*;
import java.awt.*;
import java.awt.event.*;

public class FarmUpgradeScreen extends Screen {

    private FarmProgression farm;
    private Rectangle upgradeBtn;
    private Rectangle backBtn;
    private int hovered = -1;

    public FarmUpgradeScreen(GamePanel panel) {
        super(panel);
    }

    @Override
    public void onEnter() {
        super.onEnter();
        farm = new FarmProgression(1);
    }

    @Override
    public void update() { tickCount++; }

    @Override
    public void draw(Graphics2D g) {
        GradientPaint bg = new GradientPaint(
                0, 0, new Color(28, 58, 18),
                0, GamePanel.H, new Color(12, 38, 8));
        g.setPaint(bg);
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);
        g.setPaint(null);

        RenderUtils.drawHeaderBar(g, GamePanel.W,
                "\uD83C\uDFE1 Farm Upgrade");

        g.setFont(FontManager.getEmoji(80));
        FontMetrics fm = g.getFontMetrics();
        String emoji = farm.getEmoji();
        g.setColor(Color.WHITE);
        g.drawString(emoji,
                (GamePanel.W - fm.stringWidth(emoji)) / 2,
                218);

        RenderUtils.drawCenteredText(g,
                farm.getName() + " (Stage "
                + farm.getStage() + "/20)",
                GamePanel.W / 2, 268,
                FontManager.getBold(22),
                new Color(255, 220, 100));

        RenderUtils.drawCenteredText(g,
                "\uD83E\uDE99 Coins: "
                + panel.getPlayerData().getTotalCoins(),
                GamePanel.W / 2, 308,
                FontManager.getBodyBold(16),
                new Color(200, 235, 160));

        if (farm.canUpgrade()) {
            FarmProgression next =
                    new FarmProgression(farm.getStage() + 1);
            RenderUtils.drawCenteredText(g,
                    "Next: " + next.getName(),
                    GamePanel.W / 2, 342,
                    FontManager.getBodyBold(17),
                    new Color(180, 225, 150));

            boolean afford = panel.getPlayerData()
                    .getTotalCoins() >= farm.getUpgradeCost();
            upgradeBtn = new Rectangle(
                    GamePanel.W / 2 - 150, 368, 300, 52);
            RenderUtils.drawButton(g, upgradeBtn,
                    "Upgrade for \uD83E\uDE99 "
                    + farm.getUpgradeCost(),
                    hovered == 0 && afford,
                    FontManager.getBold(15));
        } else {
            RenderUtils.drawCenteredText(g,
                    "\uD83D\uDC51 MAX LEVEL FARM!",
                    GamePanel.W / 2, 378,
                    FontManager.getBold(22),
                    new Color(255, 215, 50));
        }

        drawFarmStages(g);

        backBtn = new Rectangle(20, GamePanel.H - 55,
                140, 38);
        RenderUtils.drawButton(g, backBtn, "\u2190 Back",
                hovered == 1, FontManager.getBold(14));
    }

    private void drawFarmStages(Graphics2D g) {
        g.setFont(FontManager.getBodyBold(13));
        g.setColor(new Color(160, 200, 140));
        g.drawString("Farm Stages:", 30, 458);
        int fx = 30;
        for (int i = 1; i <= 20; i++) {
            FarmProgression fp = new FarmProgression(i);
            g.setFont(FontManager.getEmoji(18));
            g.setColor(i <= farm.getStage()
                    ? Color.WHITE
                    : new Color(100, 100, 100, 155));
            g.drawString(fp.getEmoji(), fx, 488);
            fx += 42;
            if (fx > GamePanel.W - 50) fx = 30;
        }
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        hovered = -1;
        if (upgradeBtn != null
                && upgradeBtn.contains(e.getX(), e.getY()))
            hovered = 0;
        if (backBtn != null
                && backBtn.contains(e.getX(), e.getY()))
            hovered = 1;
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (upgradeBtn != null
                && upgradeBtn.contains(e.getX(), e.getY())
                && farm.canUpgrade()) {
            int cost = farm.getUpgradeCost();
            if (panel.getPlayerData().spendCoins(cost)) {
                farm.upgrade();
                panel.getPlayerData().upgradeFarm();
            }
        }
        if (backBtn != null
                && backBtn.contains(e.getX(), e.getY()))
            panel.getScreenManager()
                 .switchTo(GameScreenType.MAIN_MENU);
    }
}