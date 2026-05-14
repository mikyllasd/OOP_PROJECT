package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.renderers.FarmHouseRenderer;
import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.models.FarmProgression;
import OOP_PROJECT.CatchTheBall.src.renderers.FarmBackgroundRenderer;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class FarmUpgradeScreen extends Screen {
    private FarmProgression farm;
    private Rectangle upgradeBtn;
    private Rectangle backBtn;
    private int    hovered     = -1;
    private String statusMsg   = "";
    private int    statusTimer = 0;

    public FarmUpgradeScreen(GamePanel panel) { super(panel); }

    @Override
    public void onEnter() {
        super.onEnter();
        farm = new FarmProgression(panel.getPlayerData().getFarmStage());
        statusMsg = ""; statusTimer = 0;
    }

    @Override public void update() { tickCount++; if (statusTimer>0) statusTimer--; }

    @Override
    public void draw(Graphics2D g) {
        FarmBackgroundRenderer.draw(g, GamePanel.W, GamePanel.H, tickCount);

        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);

        RenderUtils.drawHeaderBar(g, GamePanel.W, "Farm Upgrades");

        drawFarmDisplay(g);
        drawUpgradeSection(g);
        drawFarmStagesRow(g);

        backBtn = new Rectangle(20, GamePanel.H - 55, 140, 38);
        RenderUtils.drawButton(g, backBtn, "\u2190 Back", hovered == 1, FontManager.getBold(14));

        if (statusTimer > 0)
            RenderUtils.drawCenteredText(g, statusMsg, GamePanel.W / 2, GamePanel.H - 65,
                    FontManager.getBodyBold(13), new Color(100, 255, 150));
    }

    private void drawFarmDisplay(Graphics2D g) {
        int imgW = 200, imgH = 170;
        int imgX = GamePanel.W / 2 - imgW / 2;
        int imgY = 82;

        float pulse = (float)(0.5 + 0.5 * Math.sin(tickCount * 0.06));
        g.setColor(new Color(100, 220, 80, (int)(30 * pulse)));
        g.fillOval(imgX - 15, imgY - 10, imgW + 30, imgH + 20);

        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(imgX + 20, imgY + imgH - 8, imgW - 40, 16);

        FarmHouseRenderer.draw(g, farm.getStage(), imgX, imgY, imgW, imgH);

        RenderUtils.drawCenteredText(g,
                farm.getName() + " (Stage " + farm.getStage() + "/20)",
                GamePanel.W / 2, 270, FontManager.getBold(22), new Color(255, 220, 100));
        RenderUtils.drawCenteredText(g,
                "Coins: " + panel.getPlayerData().getTotalCoins(),
                GamePanel.W / 2, 295, FontManager.getBodyBold(15), new Color(200, 235, 160));
    }

    private void drawUpgradeSection(Graphics2D g) {
        if (farm.canUpgrade()) {
            FarmProgression next = new FarmProgression(farm.getStage() + 1);

            int px = GamePanel.W / 2 + 125, py = 88;
            g.setColor(new Color(0, 0, 0, 80));
            g.fillRoundRect(px - 6, py - 6, 88, 78, 10, 10);
            g.setColor(new Color(100, 200, 80, 120));
            g.setStroke(new BasicStroke(1.5f));
            g.drawRoundRect(px - 6, py - 6, 88, 78, 10, 10);
            g.setStroke(new BasicStroke(1f));
            FarmHouseRenderer.draw(g, farm.getStage() + 1, px, py, 76, 64);
            g.setFont(FontManager.getBody(10));
            g.setColor(new Color(180, 220, 150));
            FontMetrics fm = g.getFontMetrics();
            String nextLabel = "Next: " + next.getName();
            g.drawString(nextLabel, px + (76 - fm.stringWidth(nextLabel)) / 2, py + 76);

            boolean afford = panel.getPlayerData().getTotalCoins() >= farm.getUpgradeCost();
            upgradeBtn = new Rectangle(GamePanel.W / 2 - 160, 318, 320, 52);

            Color btnBg = afford ? new Color(80, 170, 55) : new Color(80, 80, 80);
            GradientPaint gp = new GradientPaint(
                    upgradeBtn.x, upgradeBtn.y, btnBg.brighter(),
                    upgradeBtn.x, upgradeBtn.y + upgradeBtn.height, btnBg.darker());
            g.setPaint(gp);
            g.fillRoundRect(upgradeBtn.x, upgradeBtn.y,
                    upgradeBtn.width, upgradeBtn.height, 14, 14);
            g.setPaint(null);

            g.setColor(afford ? new Color(150, 240, 100) : new Color(120, 120, 120));
            g.setStroke(new BasicStroke(hovered == 0 && afford ? 2.5f : 1.8f));
            g.drawRoundRect(upgradeBtn.x, upgradeBtn.y,
                    upgradeBtn.width, upgradeBtn.height, 14, 14);
            g.setStroke(new BasicStroke(1f));

            String label = "Upgrade for " + farm.getUpgradeCost() + " coins";
            if (!afford) label = "Need " + farm.getUpgradeCost()
                    + " (short " + (farm.getUpgradeCost() - panel.getPlayerData().getTotalCoins()) + ")";
            RenderUtils.drawCenteredText(g, label, GamePanel.W / 2, 351,
                    FontManager.getBold(15), afford ? Color.WHITE : new Color(180, 180, 180));
        } else {
            RenderUtils.drawCenteredText(g, "MAX LEVEL FARM!",
                    GamePanel.W / 2, 350, FontManager.getBold(24), new Color(255, 215, 50));
        }
    }

    private void drawFarmStagesRow(Graphics2D g) {
        // Panel background for the stages section
        int panelX = 20;
        int panelY = 390;
        int panelW = GamePanel.W - 40;
        int panelH = 110;
        RenderUtils.drawGradientPanel(g, panelX, panelY, panelW, panelH,
                new Color(18, 45, 12, 200), new Color(10, 28, 6, 200),
                new Color(60, 120, 40), 1.5f, 14);

        g.setFont(FontManager.getBodyBold(12));
        g.setColor(new Color(160, 200, 140));
        g.drawString("Farm Stages:", panelX + 12, panelY + 18);

        // Fit all 20 stages in one row inside the panel
        int totalStages = 20;
        int cellW = (panelW - 24) / totalStages;  // ~72px each for 1536 wide, ~36 for 800
        int cellH = 52;
        int rowY  = panelY + 22;

        for (int i = 1; i <= totalStages; i++) {
            boolean reached = (i <= farm.getStage());
            boolean current = (i == farm.getStage());
            int fx = panelX + 12 + (i - 1) * cellW;

            if (current) {
                // Gold glow highlight for current stage
                g.setColor(new Color(255, 215, 0, 70));
                g.fillRoundRect(fx - 2, rowY - 2, cellW - 2, cellH + 4, 8, 8);
                g.setColor(new Color(255, 215, 0, 180));
                g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(fx - 2, rowY - 2, cellW - 2, cellH + 4, 8, 8);
                g.setStroke(new BasicStroke(1f));
                // Draw house animated
                float pulse = (float)(1.0 + 0.12 * Math.sin(tickCount * 0.1));
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int houseW = cellW - 6;
                int houseH = cellH - 14;
                int houseX = fx + 3;
                int houseY = rowY + 2;
                g2.translate(houseX + houseW / 2, houseY + houseH / 2);
                g2.scale(pulse, pulse);
                g2.translate(-(houseW / 2), -(houseH / 2));
                FarmHouseRenderer.draw(g2, i, 0, 0, houseW, houseH);
                g2.dispose();
            } else if (reached) {
                // Unlocked but not current — draw house normally, slightly dim
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
                FarmHouseRenderer.draw(g2, i, fx + 2, rowY + 2, cellW - 6, cellH - 14);
                g2.dispose();
            } else {
                // LOCKED — draw dark mystery box with animated ? mark
                // Dark box
                g.setColor(new Color(10, 20, 8, 200));
                g.fillRoundRect(fx, rowY, cellW - 4, cellH, 6, 6);
                g.setColor(new Color(40, 80, 30, 180));
                g.setStroke(new BasicStroke(1.2f));
                g.drawRoundRect(fx, rowY, cellW - 4, cellH, 6, 6);
                g.setStroke(new BasicStroke(1f));

                // Pulsing purple/mystery glow
                float glow = (float)(0.4 + 0.3 * Math.sin(tickCount * 0.07 + i * 0.5));
                g.setColor(new Color(120, 60, 200, (int)(60 * glow)));
                g.fillRoundRect(fx + 1, rowY + 1, cellW - 6, cellH - 2, 5, 5);

                // Animated ? mark
                float questionPulse = (float)(0.85 + 0.15 * Math.sin(tickCount * 0.08 + i * 0.3));
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int qx = fx + cellW / 2;
                int qy = rowY + cellH / 2 + 5;
                g2.translate(qx, qy);
                g2.scale(questionPulse, questionPulse);
                g2.translate(-qx, -qy);
                // Shadow of ?
                g2.setFont(FontManager.getBold(16));
                g2.setColor(new Color(0, 0, 0, 120));
                FontMetrics fm2 = g2.getFontMetrics();
                g2.drawString("?", qx - fm2.stringWidth("?") / 2 + 1, qy + 1);
                // Bright ? with mystery color
                Color qColor = new Color(
                    180 + (int)(40 * Math.sin(tickCount * 0.05 + i)),
                    80,
                    220 + (int)(35 * Math.cos(tickCount * 0.07 + i))
                );
                g2.setColor(qColor);
                g2.drawString("?", qx - fm2.stringWidth("?") / 2, qy);
                g2.dispose();
            }

            // Stage number below each cell
            g.setFont(FontManager.getBody(9));
            g.setColor(current ? new Color(255, 215, 0) :
                       reached ? new Color(160, 200, 130) :
                                 new Color(80, 100, 70));
            FontMetrics fm = g.getFontMetrics();
            String num = String.valueOf(i);
            g.drawString(num, fx + (cellW - fm.stringWidth(num)) / 2 - 2, rowY + cellH + 12);
        }

        // Progress bar
        RenderUtils.drawProgressBar(g, panelX + 12, panelY + panelH - 18, panelW - 24, 10,
                (float)(farm.getStage() - 1) / 19f,
                new Color(0, 0, 0, 80), new Color(80, 200, 80), new Color(150, 255, 80));

        g.setFont(FontManager.getBody(10));
        g.setColor(new Color(160, 200, 140));
        g.drawString("Progress: " + farm.getStage() + " / 20",
                panelX + 12, panelY + panelH - 22);
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        hovered = -1;
        if (upgradeBtn != null && upgradeBtn.contains(e.getX(), e.getY())) hovered = 0;
        if (backBtn    != null && backBtn.contains(e.getX(), e.getY()))    hovered = 1;
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (upgradeBtn != null && upgradeBtn.contains(e.getX(), e.getY()) && farm.canUpgrade()) {
            int cost = farm.getUpgradeCost();
            if (panel.getPlayerData().spendCoins(cost)) {
                farm.upgrade();
                panel.getPlayerData().setFarmStage(farm.getStage());
                statusMsg   = "Upgraded to Stage " + farm.getStage() + "!";
                statusTimer = 150;
            } else {
                statusMsg = "Not enough coins!"; statusTimer = 120;
            }
        }
        if (backBtn != null && backBtn.contains(e.getX(), e.getY()))
            panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }
}