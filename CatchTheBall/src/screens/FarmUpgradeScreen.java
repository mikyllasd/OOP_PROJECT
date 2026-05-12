package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.renderers.FarmHouseRenderer;
import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.models.FarmProgression;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;

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
        g.setPaint(new GradientPaint(0,0,new Color(28,58,18),0,GamePanel.H,new Color(12,38,8)));
        g.fillRect(0,0,GamePanel.W,GamePanel.H); g.setPaint(null);
        RenderUtils.drawHeaderBar(g, GamePanel.W, "Farm Upgrade");
        drawFarmDisplay(g);
        drawUpgradeSection(g);
        drawFarmStagesRow(g);
        backBtn=new Rectangle(20,GamePanel.H-55,140,38);
        RenderUtils.drawButton(g,backBtn,"\u2190 Back",hovered==1,FontManager.getBold(14));
        if (statusTimer>0)
            RenderUtils.drawCenteredText(g,statusMsg,GamePanel.W/2,GamePanel.H-65,
                    FontManager.getBodyBold(13),new Color(100,255,150));
    }

private void drawFarmDisplay(Graphics2D g) {
    // Draw large 2D cartoon house, animated pulse
    int imgW = 200, imgH = 170;
    int imgX = GamePanel.W / 2 - imgW / 2;
    int imgY = 82;

    // Glow behind house
    float pulse = (float)(0.5 + 0.5 * Math.sin(tickCount * 0.06));
    g.setColor(new Color(100, 220, 80, (int)(30 * pulse)));
    g.fillOval(imgX - 15, imgY - 10, imgW + 30, imgH + 20);

    // Shadow under house
    g.setColor(new Color(0, 0, 0, 50));
    g.fillOval(imgX + 20, imgY + imgH - 8, imgW - 40, 16);

    // Draw the cartoon house
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

        // Show next house preview small on the right
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
    g.setFont(FontManager.getBodyBold(13));
    g.setColor(new Color(160,200,140));
    g.drawString("Farm Stages:", 30, 430);

    int fx = 30;
    for (int i = 1; i <= 20; i++) {
        boolean reached = (i <= farm.getStage());
        boolean current = (i == farm.getStage());

        // Dim box for locked stages
        if (!reached) {
            g.setColor(new Color(0, 0, 0, 80));
            g.fillRoundRect(fx - 1, 438, 34, 30, 4, 4);
        }

        // Highlight current stage
        if (current) {
            float pulse = (float)(1.0 + 0.15 * Math.sin(tickCount * 0.1));
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(255, 215, 0, 60));
            g2.fillRoundRect(fx - 3, 436, 38, 34, 6, 6);
            g2.translate(fx + 17, 455);
            g2.scale(pulse, pulse);
            g2.translate(-(fx + 17), -455);
            FarmHouseRenderer.draw(g2, i, fx, 438, 34, 28);
            g2.dispose();
        } else {
            Graphics2D g2 = (Graphics2D) g.create();
            if (!reached) g2.setComposite(
                    java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.35f));
            FarmHouseRenderer.draw(g2, i, fx, 438, 34, 28);
            g2.dispose();
        }

        fx += 43;
        if (fx > GamePanel.W - 50) fx = 30;
    }

    RenderUtils.drawProgressBar(g, 30, 476, GamePanel.W - 60, 12,
            (float)(farm.getStage() - 1) / 19f,
            new Color(0, 0, 0, 80), new Color(80, 200, 80), new Color(150, 255, 80));
    g.setFont(FontManager.getBody(11));
    g.setColor(new Color(160, 200, 140));
    g.drawString("Progress: " + farm.getStage() + "/20", 30, 504);
}

    @Override
    public void onMouseMoved(MouseEvent e) {
        hovered=-1;
        if (upgradeBtn!=null&&upgradeBtn.contains(e.getX(),e.getY())) hovered=0;
        if (backBtn!=null&&backBtn.contains(e.getX(),e.getY())) hovered=1;
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (upgradeBtn!=null&&upgradeBtn.contains(e.getX(),e.getY())&&farm.canUpgrade()) {
            int cost=farm.getUpgradeCost();
            if (panel.getPlayerData().spendCoins(cost)) {
                farm.upgrade();
                panel.getPlayerData().setFarmStage(farm.getStage());
                statusMsg = "Upgraded to Stage " + farm.getStage() + "!";
                statusTimer=150;
            } else {
                statusMsg="Not enough coins!"; statusTimer=120;
            }
        }
        if (backBtn!=null&&backBtn.contains(e.getX(),e.getY()))
            panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode()==KeyEvent.VK_ESCAPE)
            panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }
}