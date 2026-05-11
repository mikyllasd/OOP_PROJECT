package screens;

import core.Screen;
import enums.*;
import managers.GamePanel;
import utils.*;
import java.awt.*;
import java.awt.event.*;

public class WardrobeScreen extends Screen {

    private int shopTab = 0;
    private Rectangle[] menuButtons;
    private int hovered = -1;

    public WardrobeScreen(GamePanel panel) {
        super(panel);
    }

    @Override
    public void update() { tickCount++; }

    @Override
    public void draw(Graphics2D g) {
        drawBg(g);
        RenderUtils.drawHeaderBar(g, GamePanel.W,
                "\uD83E\uDDFA Wardrobe & Shop");
        drawTabs(g);
        drawCoins(g);
        if (shopTab == 0) drawSkins(g);
        else drawBaskets(g);
        drawBack(g);
    }

    private void drawBg(Graphics2D g) {
        GradientPaint bg = new GradientPaint(
                0, 0, new Color(22, 48, 18),
                0, GamePanel.H, new Color(12, 32, 10));
        g.setPaint(bg);
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);
        g.setPaint(null);
    }

    private void drawTabs(Graphics2D g) {
        String[] tabs = {"\uD83D\uDC68\u200D\uD83C\uDF3E Farmer Skins",
                         "\uD83E\uDDFA Basket Skins"};
        for (int i = 0; i < 2; i++) {
            boolean sel = shopTab == i;
            RenderUtils.drawGradientPanel(g,
                    50 + i * 220, 85, 200, 36,
                    sel ? new Color(88, 168, 62)
                        : new Color(40, 82, 30),
                    sel ? new Color(62, 130, 42)
                        : new Color(28, 60, 20),
                    sel ? new Color(155, 235, 105)
                        : new Color(68, 128, 58),
                    1f, 10);
            RenderUtils.drawCenteredText(g, tabs[i],
                    50 + i * 220 + 100, 110,
                    FontManager.getBold(13),
                    sel ? Color.WHITE
                        : new Color(155, 198, 138));
        }
    }

    private void drawCoins(Graphics2D g) {
        g.setFont(FontManager.getBodyBold(14));
        g.setColor(ColorPalette.TEXT_COINS);
        g.drawString("\uD83E\uDE99 "
                + panel.getPlayerData().getTotalCoins()
                + " coins",
                GamePanel.W - 200, 100);
    }

    private void drawSkins(Graphics2D g) {
        SkinType[] skins = SkinType.values();
        int cols = 4, bw = 130, bh = 150;
        for (int i = 0; i < skins.length; i++) {
            int bx = 40 + i % cols * (bw + 15);
            int by = 140 + i / cols * (bh + 15);
            boolean owned = panel.getPlayerData()
                                 .ownsSkin(skins[i]);
            boolean eqd   = panel.getPlayerData()
                                 .getEquippedSkin()
                                 == skins[i];
            drawShopCard(g, bx, by, bw, bh,
                    skins[i].getDisplayName(),
                    skins[i].getCost(), owned, eqd);
        }
    }

    private void drawBaskets(Graphics2D g) {
        BasketSkin[] baskets = BasketSkin.values();
        int cols = 5, bw = 130, bh = 150;
        for (int i = 0; i < baskets.length; i++) {
            int bx = 40 + i % cols * (bw + 15);
            int by = 140 + i / cols * (bh + 15);
            boolean owned = panel.getPlayerData()
                                 .ownsBasket(baskets[i]);
            boolean eqd   = panel.getPlayerData()
                                 .getEquippedBasket()
                                 == baskets[i];
            drawShopCard(g, bx, by, bw, bh,
                    baskets[i].getDisplayName(),
                    baskets[i].getCost(), owned, eqd);
            Shape clip = g.getClip();
            g.setClip(bx + 2, by + 2, bw - 4, 70);
            renderers.BasketRenderer.draw(g, baskets[i],
                    bx + (bw - 60) / 2, by + 22, 60, 36);
            g.setClip(clip);
        }
    }

    private void drawShopCard(Graphics2D g,
            int x, int y, int w, int h,
            String name, int cost,
            boolean owned, boolean equipped) {
        Color bg = equipped ? ColorPalette.CARD_EQUIPPED
                : owned    ? ColorPalette.CARD_OWNED
                           : ColorPalette.CARD_LOCKED;
        Color br = equipped ? ColorPalette.CARD_BORDER_EQ
                : owned    ? ColorPalette.CARD_BORDER_OW
                           : ColorPalette.CARD_BORDER_LK;
        RenderUtils.drawGradientPanel(g, x, y, w, h,
                bg.brighter(), bg.darker(), br,
                equipped ? 2.5f : 1.5f, 12);
        g.setFont(FontManager.getBodyBold(11));
        g.setColor(new Color(200, 235, 170));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(name,
                x + (w - fm.stringWidth(name)) / 2,
                y + 78);
        if (equipped) {
            g.setColor(new Color(148, 255, 100));
            g.setFont(FontManager.getBold(11));
            FontMetrics ef = g.getFontMetrics();
            g.drawString("\u2713 EQUIPPED",
                    x + (w - ef.stringWidth("\u2713 EQUIPPED")) / 2,
                    y + 100);
        } else if (owned) {
            g.setColor(new Color(100, 205, 80));
            g.drawString("OWNED \u2014 Equip",
                    x + (w - fm.stringWidth("OWNED \u2014 Equip")) / 2,
                    y + 100);
        } else {
            RenderUtils.drawRoundPanel(g,
                    x + 10, y + 88, w - 20, 28,
                    new Color(65, 130, 52),
                    new Color(100, 185, 80), 1f, 8);
            g.setFont(FontManager.getBold(11));
            g.setColor(new Color(255, 218, 50));
            String cs = "\uD83E\uDE99 " + cost;
            FontMetrics cf = g.getFontMetrics();
            g.drawString(cs,
                    x + 10 + (w - 20 - cf.stringWidth(cs)) / 2,
                    y + 108);
        }
    }

    private void drawBack(Graphics2D g) {
        menuButtons    = new Rectangle[1];
        menuButtons[0] = new Rectangle(20, GamePanel.H - 55,
                140, 38);
        RenderUtils.drawButton(g, menuButtons[0],
                "\u2190 Back",
                hovered == 0, FontManager.getBold(14));
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        hovered = -1;
        if (menuButtons != null
                && menuButtons[0].contains(e.getX(), e.getY()))
            hovered = 0;
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        if (new Rectangle(50, 85, 200, 36).contains(mx, my))
            shopTab = 0;
        if (new Rectangle(270, 85, 200, 36).contains(mx, my))
            shopTab = 1;
        handleShopClick(mx, my);
        if (menuButtons != null
                && menuButtons[0].contains(mx, my))
            panel.getScreenManager()
                 .switchTo(GameScreenType.MAIN_MENU);
    }

    private void handleShopClick(int mx, int my) {
        if (shopTab == 0) {
            SkinType[] skins = SkinType.values();
            int cols = 4, bw = 130, bh = 150;
            for (int i = 0; i < skins.length; i++) {
                int bx = 40 + i % cols * (bw + 15);
                int by = 140 + i / cols * (bh + 15);
                if (new Rectangle(bx, by, bw, bh)
                        .contains(mx, my)) {
                    if (panel.getPlayerData()
                             .ownsSkin(skins[i]))
                        panel.getPlayerData()
                             .equipSkin(skins[i]);
                    else if (panel.getPlayerData()
                                  .spendCoins(
                                          skins[i].getCost())) {
                        panel.getPlayerData()
                             .buySkin(skins[i]);
                        panel.getPlayerData()
                             .equipSkin(skins[i]);
                    }
                }
            }
        } else {
            BasketSkin[] baskets = BasketSkin.values();
            int cols = 5, bw = 130, bh = 150;
            for (int i = 0; i < baskets.length; i++) {
                int bx = 40 + i % cols * (bw + 15);
                int by = 140 + i / cols * (bh + 15);
                if (new Rectangle(bx, by, bw, bh)
                        .contains(mx, my)) {
                    if (panel.getPlayerData()
                             .ownsBasket(baskets[i]))
                        panel.getPlayerData()
                             .equipBasket(baskets[i]);
                    else if (panel.getPlayerData()
                                  .spendCoins(
                                          baskets[i].getCost())) {
                        panel.getPlayerData()
                             .buyBasket(baskets[i]);
                        panel.getPlayerData()
                             .equipBasket(baskets[i]);
                    }
                }
            }
        }
    }
}