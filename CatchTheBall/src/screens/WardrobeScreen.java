package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.*;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.renderers.BasketRenderer;
import OOP_PROJECT.CatchTheBall.src.renderers.FarmBackgroundRenderer;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class WardrobeScreen extends Screen {

    // ── layout ────────────────────────────────────────────────────────────────
    private static final int COLS   = 5;
    private static final int BW     = 130;   // card width
    private static final int BH     = 190;   // card height (taller for description)
    private static final int GAP    = 12;

    // total grid width for COLS cards
    private static final int GRID_W = COLS * BW + (COLS - 1) * GAP;

    // horizontal start so the grid is perfectly centred in W=900
    private static final int STARTX = (GamePanel.W - GRID_W) / 2;

    // vertical start — below header (75px) + tabs (38px) + coins bar + spacing
    private static final int STARTY = 138;

    // ── state ─────────────────────────────────────────────────────────────────
    private int       shopTab   = 0;
    private Rectangle backBtn;
    private int       hovered   = -1;

    public WardrobeScreen(GamePanel panel) { super(panel); }

    @Override public void update() { tickCount++; }

    // ── skin descriptions ─────────────────────────────────────────────────────
    private String getSkinDesc(SkinType skin) {
        switch (skin) {
            case FARMER_MALE:   return "The classic farmer, ready to harvest!";
            case FARMER_FEMALE: return "Graceful farm girl with a sunny smile.";
            case FARM_KID:      return "Energetic kid who loves farm life.";
            case COWBOY:        return "Rides the range, lasso in hand.";
            case WIZARD:        return "Casts magical spells on the farm.";
            case NINJA:         return "Silent, swift, stealthy harvester.";
            case ROYAL:         return "Noble ruler of the finest crops.";
            case CHEF:          return "Master chef straight from the fields.";
            case PIRATE:        return "Sails the seas for rare fruit.";
            case EXPLORER:      return "Adventurer mapping new farm lands.";
            default:            return "";
        }
    }

    private String getBasketDesc(BasketSkin skin) {
        switch (skin) {
            case WOVEN:     return "Classic handwoven country basket.";
            case METAL:     return "Sturdy metal bucket, dent-proof.";
            case GOLDEN:    return "Shimmering gold, for the wealthy.";
            case CART:      return "Spacious farm cart hauls it all.";
            case DIAMOND:   return "Glittering diamond rarity.";
            case BAMBOO:    return "Eco-friendly bamboo weave.";
            case CLAY:      return "Rustic clay pot from the kiln.";
            case CRYSTAL:   return "Transparent crystal clarity.";
            case MAGIC:     return "Bubbling cauldron of surprises.";
            case LEGENDARY: return "The ancient Legendary Ark itself.";
            default:        return "";
        }
    }

    // ── draw ──────────────────────────────────────────────────────────────────
    @Override
    public void draw(Graphics2D g) {
        FarmBackgroundRenderer.draw(g, GamePanel.W, GamePanel.H, tickCount);

        // dark overlay for legibility
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);

        RenderUtils.drawHeaderBar(g, GamePanel.W, "Wardrobe & Shop");

        drawTabs(g);
        drawCoinsBar(g);

        if (shopTab == 0) drawSkins(g);
        else              drawBaskets(g);

        drawHintBar(g);

        backBtn = new Rectangle(18, GamePanel.H - 52, 130, 36);
        RenderUtils.drawButton(g, backBtn, "<  Back", hovered == 9999,
                FontManager.getBold(13));
    }

    // ── drawn icon helpers ────────────────────────────────────────────────────

    /**
     * Draws a small straw-hat icon (farmer) at pixel (ix, iy), size ~18x14.
     * Used in the "Farmer Skins" tab label.
     */
    private void drawFarmerTabIcon(Graphics2D g, int ix, int iy) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // brim
        g2.setColor(new Color(210, 180, 80));
        g2.fillOval(ix, iy + 6, 18, 7);
        // crown
        g2.setColor(new Color(190, 155, 60));
        g2.fillRoundRect(ix + 4, iy, 10, 9, 4, 4);
        // band
        g2.setColor(new Color(160, 100, 30));
        g2.fillRect(ix + 4, iy + 6, 10, 2);
        // outline
        g2.setColor(new Color(120, 80, 20));
        g2.setStroke(new BasicStroke(1f));
        g2.drawOval(ix, iy + 6, 18, 7);
        g2.drawRoundRect(ix + 4, iy, 10, 9, 4, 4);
        g2.dispose();
    }

    /**
     * Draws a small woven-basket icon at pixel (ix, iy), size ~16x14.
     * Used in the "Baskets" tab label.
     */
    private void drawBasketTabIcon(Graphics2D g, int ix, int iy) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // body
        g2.setColor(new Color(180, 130, 60));
        int[] bx = {ix, ix + 2, ix + 14, ix + 16};
        int[] by = {iy + 5, iy + 14, iy + 14, iy + 5};
        g2.fillPolygon(bx, by, 4);
        // weave lines
        g2.setColor(new Color(140, 95, 35));
        g2.setStroke(new BasicStroke(1f));
        g2.drawLine(ix + 5,  iy + 5,  ix + 5,  iy + 14);
        g2.drawLine(ix + 8,  iy + 5,  ix + 8,  iy + 14);
        g2.drawLine(ix + 11, iy + 5,  ix + 11, iy + 14);
        g2.drawLine(ix + 1,  iy + 8,  ix + 15, iy + 8);
        g2.drawLine(ix + 1,  iy + 11, ix + 15, iy + 11);
        // rim
        g2.setColor(new Color(210, 160, 80));
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(ix, iy + 5, ix + 16, iy + 5);
        // handle arc
        g2.setColor(new Color(160, 110, 45));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawArc(ix + 3, iy - 2, 10, 8, 0, 180);
        // outline
        g2.setColor(new Color(100, 65, 20));
        g2.setStroke(new BasicStroke(1f));
        g2.drawPolygon(bx, by, 4);
        g2.dispose();
    }

    /**
     * Draws a small gold coin at pixel (ix, iy), diameter ~14.
     * Used in the coins bar and buy badges.
     */
    private void drawCoinIcon(Graphics2D g, int ix, int iy, int size) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // outer gold disc
        g2.setColor(new Color(220, 170, 20));
        g2.fillOval(ix, iy, size, size);
        // inner highlight
        g2.setColor(new Color(255, 230, 80));
        g2.fillOval(ix + size / 4, iy + size / 6, size / 2, size / 3);
        // dollar/star mark
        g2.setColor(new Color(180, 130, 10));
        g2.setFont(FontManager.getBold(size - 4));
        FontMetrics fm = g2.getFontMetrics();
        String mark = "$";
        g2.drawString(mark,
                ix + (size - fm.stringWidth(mark)) / 2,
                iy + size / 2 + fm.getDescent() + 1);
        // rim
        g2.setColor(new Color(160, 115, 5));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawOval(ix, iy, size, size);
        g2.dispose();
    }

    // ── tabs ──────────────────────────────────────────────────────────────────
    private void drawTabs(Graphics2D g) {
        // Labels — plain text only (no emoji), icons drawn separately
        String[] tabs  = { "Farmer Skins", "Baskets" };
        int tabW = 200, tabH = 34, tabY = 82;
        int totalW = tabs.length * tabW + (tabs.length - 1) * 8;
        int startTab = (GamePanel.W - totalW) / 2;

        for (int i = 0; i < tabs.length; i++) {
            int tx = startTab + i * (tabW + 8);
            boolean sel = shopTab == i;
            if (sel) {
                GradientPaint tp = new GradientPaint(tx, tabY, new Color(88, 170, 58),
                        tx, tabY + tabH, new Color(55, 125, 35));
                g.setPaint(tp);
                g.fillRoundRect(tx, tabY, tabW, tabH, 10, 10);
                g.setPaint(null);
                g.setColor(new Color(150, 240, 100));
                g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(tx, tabY, tabW, tabH, 10, 10);
                g.setStroke(new BasicStroke(1f));
                g.setColor(Color.WHITE);
            } else {
                g.setColor(new Color(35, 70, 25, 200));
                g.fillRoundRect(tx, tabY, tabW, tabH, 10, 10);
                g.setColor(new Color(65, 120, 48));
                g.setStroke(new BasicStroke(1.5f));
                g.drawRoundRect(tx, tabY, tabW, tabH, 10, 10);
                g.setStroke(new BasicStroke(1f));
                g.setColor(new Color(155, 200, 135));
            }

            // draw icon then text side-by-side, centred in tab
            int iconW = 18; // approximate icon width
            int spacing = 5;
            g.setFont(FontManager.getBold(13));
            FontMetrics fm = g.getFontMetrics();
            int textW = fm.stringWidth(tabs[i]);
            int totalContentW = iconW + spacing + textW;
            int contentStartX = tx + (tabW - totalContentW) / 2;
            int iconTopY = tabY + (tabH - 14) / 2; // vertically centre 14px-tall icon

            if (i == 0) {
                drawFarmerTabIcon(g, contentStartX, iconTopY);
            } else {
                drawBasketTabIcon(g, contentStartX, iconTopY);
            }

            // text right of icon
            g.setFont(FontManager.getBold(13));
            g.drawString(tabs[i], contentStartX + iconW + spacing, tabY + 22);
        }
    }

    // ── coins bar ─────────────────────────────────────────────────────────────
    private void drawCoinsBar(Graphics2D g) {
        int coins = panel.getPlayerData().getTotalCoins();
        int cw = 180, ch = 28, cx = GamePanel.W - cw - 16, cy = 82;
        g.setColor(new Color(40, 80, 25, 200));
        g.fillRoundRect(cx, cy, cw, ch, 14, 14);
        g.setColor(new Color(200, 160, 30));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(cx, cy, cw, ch, 14, 14);
        g.setStroke(new BasicStroke(1f));

        // drawn coin icon
        int coinSize = 14;
        drawCoinIcon(g, cx + 10, cy + (ch - coinSize) / 2, coinSize);

        // coins text
        g.setFont(FontManager.getBodyBold(14));
        g.setColor(new Color(255, 215, 50));
        g.drawString(coins + " coins", cx + 10 + coinSize + 6, cy + 19);
    }

    // ── skin grid ─────────────────────────────────────────────────────────────
    private void drawSkins(Graphics2D g) {
        SkinType[] skins = SkinType.values();
        for (int i = 0; i < skins.length; i++) {
            int col = i % COLS, row = i / COLS;
            int bx  = STARTX + col * (BW + GAP);
            int by  = STARTY + row * (BH + GAP);
            boolean owned    = panel.getPlayerData().ownsSkin(skins[i]);
            boolean equipped = panel.getPlayerData().getEquippedSkin() == skins[i];
            boolean hov      = (hovered == i);
            drawSkinCard(g, bx, by, BW, BH, skins[i], owned, equipped, hov);
        }
    }

    private void drawSkinCard(Graphics2D g, int x, int y, int w, int h,
            SkinType skin, boolean owned, boolean equipped, boolean hov) {

        // card background
        Color bgTop, bgBot, border;
        float borderW;
        if (equipped) {
            bgTop = new Color(65, 140, 45); bgBot = new Color(42, 100, 28);
            border = new Color(150, 245, 100); borderW = 2.5f;
        } else if (owned) {
            bgTop = new Color(38, 82, 26);  bgBot = new Color(24, 56, 16);
            border = new Color(85, 160, 60); borderW = 1.5f;
        } else {
            bgTop = new Color(26, 55, 18);  bgBot = new Color(16, 36, 10);
            border = new Color(58, 95, 45);  borderW = 1f;
        }
        if (hov && !equipped) { bgTop = bgTop.brighter(); border = border.brighter(); borderW += 1f; }

        GradientPaint gp = new GradientPaint(x, y, bgTop, x, y + h, bgBot);
        g.setPaint(gp);
        g.fillRoundRect(x, y, w, h, 12, 12);
        g.setPaint(null);
        g.setColor(border);
        g.setStroke(new BasicStroke(borderW));
        g.drawRoundRect(x, y, w, h, 12, 12);
        g.setStroke(new BasicStroke(1f));

        // glow pulse on equipped
        if (equipped) {
            float pulse = (float)(0.5 + 0.5 * Math.sin(tickCount * 0.08));
            g.setColor(new Color(100, 255, 80, (int)(30 * pulse)));
            g.fillRoundRect(x, y, w, h, 12, 12);
        }

        // ── character preview (centred, top portion) ──────────────────────────
        drawMiniCharacter(g, x + w / 2, y + 8, skin);

        // ── name ──────────────────────────────────────────────────────────────
        g.setFont(FontManager.getBold(10));
        FontMetrics fm = g.getFontMetrics();
        String name = skin.getDisplayName();
        g.setColor(new Color(220, 245, 185));
        g.drawString(name, x + (w - fm.stringWidth(name)) / 2, y + h - 80);

        // ── description (two lines if needed, small font) ─────────────────────
        drawWrappedDesc(g, getSkinDesc(skin), x + 4, y + h - 68, w - 8,
                FontManager.getBody(8), new Color(170, 210, 145));

        // ── badge + buy/equip button ──────────────────────────────────────────
        drawSkinBadge(g, x, y, w, h, skin, owned, equipped);
    }

    // ── basket grid ───────────────────────────────────────────────────────────
    private void drawBaskets(Graphics2D g) {
        BasketSkin[] baskets = BasketSkin.values();
        for (int i = 0; i < baskets.length; i++) {
            int col = i % COLS, row = i / COLS;
            int bx  = STARTX + col * (BW + GAP);
            int by  = STARTY + row * (BH + GAP);
            boolean owned    = panel.getPlayerData().ownsBasket(baskets[i]);
            boolean equipped = panel.getPlayerData().getEquippedBasket() == baskets[i];
            boolean hov      = (hovered == 200 + i);
            drawBasketCard(g, bx, by, BW, BH, baskets[i], owned, equipped, hov);
        }
    }

    private void drawBasketCard(Graphics2D g, int x, int y, int w, int h,
            BasketSkin skin, boolean owned, boolean equipped, boolean hov) {

        Color bgTop, bgBot, border; float borderW;
        if (equipped) {
            bgTop = new Color(65, 140, 45); bgBot = new Color(42, 100, 28);
            border = new Color(150, 245, 100); borderW = 2.5f;
        } else if (owned) {
            bgTop = new Color(38, 82, 26); bgBot = new Color(24, 56, 16);
            border = new Color(85, 160, 60); borderW = 1.5f;
        } else {
            bgTop = new Color(26, 55, 18); bgBot = new Color(16, 36, 10);
            border = new Color(58, 95, 45); borderW = 1f;
        }
        if (hov && !equipped) { bgTop = bgTop.brighter(); border = border.brighter(); borderW += 1f; }

        GradientPaint gp = new GradientPaint(x, y, bgTop, x, y + h, bgBot);
        g.setPaint(gp);
        g.fillRoundRect(x, y, w, h, 12, 12);
        g.setPaint(null);
        g.setColor(border);
        g.setStroke(new BasicStroke(borderW));
        g.drawRoundRect(x, y, w, h, 12, 12);
        g.setStroke(new BasicStroke(1f));

        if (equipped) {
            float pulse = (float)(0.5 + 0.5 * Math.sin(tickCount * 0.08));
            g.setColor(new Color(100, 255, 80, (int)(30 * pulse)));
            g.fillRoundRect(x, y, w, h, 12, 12);
        }

        // basket preview — centred in card
        int previewW = 64, previewH = 48;
        int px = x + (w - previewW) / 2, py = y + 18;
        Shape oldClip = g.getClip();
        g.setClip(x + 3, y + 3, w - 6, h - 6);
        BasketRenderer.draw(g, skin, px, py, previewW, previewH);
        g.setClip(oldClip);

        // name
        g.setFont(FontManager.getBold(10));
        FontMetrics fm = g.getFontMetrics();
        String name = skin.getDisplayName();
        g.setColor(new Color(220, 245, 185));
        if (fm.stringWidth(name) > w - 8) {
            String[] words = name.split(" ");
            String line1 = words[0];
            String line2 = words.length > 1 ? String.join(" ",
                    java.util.Arrays.copyOfRange(words, 1, words.length)) : "";
            g.drawString(line1, x + (w - fm.stringWidth(line1)) / 2, y + h - 82);
            g.drawString(line2, x + (w - fm.stringWidth(line2)) / 2, y + h - 70);
        } else {
            g.drawString(name, x + (w - fm.stringWidth(name)) / 2, y + h - 80);
        }

        // description
        drawWrappedDesc(g, getBasketDesc(skin), x + 4, y + h - 66, w - 8,
                FontManager.getBody(8), new Color(170, 210, 145));

        drawBasketBadge(g, x, y, w, h, skin, owned, equipped);
    }

    // ── helpers ───────────────────────────────────────────────────────────────

    /** Draws text wrapped into up to 2 lines inside maxW, small font. */
    private void drawWrappedDesc(Graphics2D g, String text, int x, int y,
            int maxW, Font font, Color color) {
        g.setFont(font);
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics();
        if (fm.stringWidth(text) <= maxW) {
            g.drawString(text, x + (maxW - fm.stringWidth(text)) / 2, y);
            return;
        }
        String[] words = text.split(" ");
        StringBuilder line1 = new StringBuilder();
        String line2 = "";
        for (int i = 0; i < words.length; i++) {
            String attempt = line1.length() == 0 ? words[i] : line1 + " " + words[i];
            if (fm.stringWidth(attempt) <= maxW) {
                line1 = new StringBuilder(attempt);
            } else {
                line2 = String.join(" ",
                        java.util.Arrays.copyOfRange(words, i, words.length));
                break;
            }
        }
        String l1 = line1.toString();
        g.drawString(l1,    x + (maxW - fm.stringWidth(l1))    / 2, y);
        g.drawString(line2, x + (maxW - fm.stringWidth(line2))  / 2, y + fm.getHeight());
    }

    private void drawMiniCharacter(Graphics2D g, int cx, int y, SkinType skin) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        float scale = 0.92f;
        g2.translate(cx - 23, y);
        g2.scale(scale, scale);
        drawMiniChar(g2, skin, 0, 0, tickCount);
        g2.dispose();
    }

    private void drawMiniChar(Graphics2D g, SkinType skin, int x, int y, int tick) {
        g.setColor(new Color(0, 0, 0, 30));
        g.fillOval(x + 7, y + 59, 36, 6);
        int cx = x + 25;
        Color[] colors = getMiniColors(skin);
        Color skinTone = colors[0], hairC = colors[1], shirtC = colors[2],
              pantsC   = colors[3], shoeC  = colors[4];
        // legs
        g.setColor(pantsC); g.fillRect(cx - 8, y + 40, 8, 16); g.fillRect(cx + 1, y + 40, 8, 16);
        g.setColor(shoeC);
        g.fillRoundRect(cx - 10, y + 54, 12, 6, 4, 4);
        g.fillRoundRect(cx + 1,  y + 54, 12, 6, 4, 4);
        // body
        g.setColor(shirtC);
        int[] bxc = {cx-12,cx+12,cx+10,cx-10};
        int[] byc = {y+20, y+20, y+40, y+40};
        g.fillPolygon(bxc, byc, 4);
        // arms
        g.setColor(shirtC.darker());
        g.fillRoundRect(cx - 18, y + 22, 7, 14, 3, 3);
        g.fillRoundRect(cx + 11, y + 22, 7, 14, 3, 3);
        g.setColor(skinTone);
        g.fillOval(cx - 18, y + 34, 8, 8);
        g.fillOval(cx + 11, y + 34, 8, 8);
        // neck + head
        g.setColor(skinTone);
        g.fillRoundRect(cx - 4, y + 12, 8, 10, 4, 4);
        g.fillOval(cx - 12, y, 24, 22);
        g.setColor(new Color(255, 150, 130, 70));
        g.fillOval(cx - 12, y + 9, 8, 6);
        g.fillOval(cx + 4,  y + 9, 8, 6);
        // eyes
        g.setColor(Color.WHITE);
        g.fillOval(cx - 9, y + 8, 7, 6); g.fillOval(cx + 2, y + 8, 7, 6);
        g.setColor(new Color(40, 40, 180));
        g.fillOval(cx - 7, y + 10, 4, 4); g.fillOval(cx + 4, y + 10, 4, 4);
        g.setColor(new Color(10, 10, 10));
        g.fillOval(cx - 6, y + 11, 2, 2); g.fillOval(cx + 5, y + 11, 2, 2);
        g.setColor(Color.WHITE);
        g.fillOval(cx - 5, y + 11, 1, 1); g.fillOval(cx + 6, y + 11, 1, 1);
        // smile
        g.setColor(new Color(180, 80, 80));
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(cx - 4, y + 15, 8, 4, 200, 140);
        g.setStroke(new BasicStroke(1f));
        // hair + hat
        g.setColor(hairC);
        g.fillRect(cx - 11, y, 22, 4);
        drawMiniHat(g, cx, y, skin, hairC);
    }

    private void drawMiniHat(Graphics2D g, int cx, int y, SkinType skin, Color hairC) {
        switch (skin) {
            case FARMER_MALE:
                g.setColor(new Color(210,180,80)); g.fillOval(cx-16,y-2,32,8);
                g.setColor(new Color(190,155,60)); g.fillRoundRect(cx-9,y-10,18,12,6,6); break;
            case FARMER_FEMALE:
                g.setColor(new Color(220,160,180)); g.fillOval(cx-14,y-2,28,10);
                g.setColor(new Color(200,130,160)); g.fillRoundRect(cx-10,y-12,20,13,8,8);
                g.setColor(new Color(255,150,180)); g.fillRect(cx-10,y-2,20,3); break;
            case FARM_KID:
                g.setColor(new Color(200,60,60));
                g.fillOval(cx-11,y-8,22,14); g.fillOval(cx-13,y,26,7); break;
            case COWBOY:
                g.setColor(new Color(130,80,30));
                g.fillOval(cx-18,y-1,36,8); g.fillRoundRect(cx-10,y-14,20,16,5,5); break;
            case WIZARD:
                g.setColor(new Color(80,40,140));
                int[] hx={cx-12,cx+12,cx+6,cx-6,cx};
                int[] hy={y-1,y-1,y-7,y-7,y-22};
                g.fillPolygon(hx,hy,5); g.fillOval(cx-13,y-4,26,8); break;
            case NINJA:
                g.setColor(new Color(20,20,20)); g.fillRect(cx-12,y+2,24,16);
                g.setColor(new Color(200,50,50)); g.fillRect(cx-12,y+5,24,3); break;
            case ROYAL:
                g.setColor(new Color(220,180,30));
                int[] crx={cx-11,cx-11,cx-7,cx-4,cx,cx+1,cx+5,cx+8,cx+11,cx+11};
                int[] cry={y+2,y-7,y-4,y-10,y-6,y-10,y-4,y-7,y-7,y+2};
                g.fillPolygon(crx,cry,10); break;
            case CHEF:
                g.setColor(Color.WHITE);
                g.fillRoundRect(cx-10,y-20,20,22,6,6); g.fillRect(cx-12,y-2,24,5); break;
            case PIRATE:
                g.setColor(new Color(20,20,20)); g.fillOval(cx-15,y-2,30,8);
                int[] px2={cx-10,cx,cx+10}; int[] py2={y-1,y-14,y-1};
                g.fillPolygon(px2,py2,3); break;
            case EXPLORER:
                g.setColor(new Color(180,150,80)); g.fillOval(cx-16,y-1,32,8);
                g.fillRoundRect(cx-10,y-12,20,14,6,6); break;
        }
    }

    private Color[] getMiniColors(SkinType skin) {
        switch (skin) {
            case FARMER_MALE:   return new Color[]{new Color(220,175,120),new Color(110,70,30),new Color(100,140,200),new Color(60,90,160),new Color(60,40,20)};
            case FARMER_FEMALE: return new Color[]{new Color(240,195,145),new Color(180,100,60),new Color(240,140,160),new Color(200,100,130),new Color(180,100,120)};
            case FARM_KID:      return new Color[]{new Color(255,210,160),new Color(200,150,80),new Color(100,180,240),new Color(80,130,200),new Color(80,60,200)};
            case COWBOY:        return new Color[]{new Color(200,155,100),new Color(80,50,20),new Color(180,120,60),new Color(100,70,40),new Color(50,30,10)};
            case WIZARD:        return new Color[]{new Color(210,180,155),new Color(220,215,200),new Color(100,60,180),new Color(80,40,160),new Color(50,30,100)};
            case NINJA:         return new Color[]{new Color(180,140,100),new Color(20,20,20),new Color(20,20,20),new Color(10,10,10),new Color(15,15,15)};
            case ROYAL:         return new Color[]{new Color(230,185,130),new Color(60,40,100),new Color(120,60,160),new Color(80,40,120),new Color(60,40,80)};
            case CHEF:          return new Color[]{new Color(230,185,130),new Color(50,40,30),Color.WHITE,new Color(40,40,40),new Color(30,30,30)};
            case PIRATE:        return new Color[]{new Color(200,155,100),new Color(20,15,10),new Color(40,30,20),new Color(30,20,10),new Color(20,15,5)};
            case EXPLORER:      return new Color[]{new Color(190,145,90),new Color(90,60,30),new Color(180,160,100),new Color(120,100,60),new Color(80,60,30)};
            default:            return new Color[]{new Color(220,175,120),new Color(110,70,30),new Color(100,140,200),new Color(60,90,160),new Color(60,40,20)};
        }
    }

    // ── skin badge ────────────────────────────────────────────────────────────
    private void drawSkinBadge(Graphics2D g, int x, int y, int w, int h,
            SkinType skin, boolean owned, boolean equipped) {
        int by2 = y + h - 46;
        if (equipped) {
            g.setColor(new Color(50,180,50,180));  g.fillRoundRect(x+6,by2,w-12,20,8,8);
            g.setColor(new Color(120,255,100)); g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x+6,by2,w-12,20,8,8); g.setStroke(new BasicStroke(1f));
            g.setFont(FontManager.getBold(9)); g.setColor(Color.WHITE);
            FontMetrics fm=g.getFontMetrics(); String t="[OK] EQUIPPED";
            g.drawString(t, x+6+(w-12-fm.stringWidth(t))/2, by2+13);
        } else if (owned) {
            g.setColor(new Color(40,120,40,180));  g.fillRoundRect(x+6,by2,w-12,20,8,8);
            g.setColor(new Color(90,200,70)); g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x+6,by2,w-12,20,8,8); g.setStroke(new BasicStroke(1f));
            g.setFont(FontManager.getBold(9)); g.setColor(new Color(150,255,120));
            FontMetrics fm=g.getFontMetrics(); String t="OWNED  Equip";
            g.drawString(t, x+6+(w-12-fm.stringWidth(t))/2, by2+13);
        } else {
            // draw coin icon + cost text
            g.setColor(new Color(30,60,20,200));   g.fillRoundRect(x+6,by2,w-12,20,8,8);
            g.setColor(new Color(180,140,30)); g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x+6,by2,w-12,20,8,8); g.setStroke(new BasicStroke(1f));
            // coin icon
            int coinSz = 11;
            String costStr = "" + skin.getCost();
            g.setFont(FontManager.getBold(10));
            FontMetrics fm = g.getFontMetrics();
            int totalW2 = coinSz + 3 + fm.stringWidth(costStr);
            int startX2 = x + 6 + (w - 12 - totalW2) / 2;
            drawCoinIcon(g, startX2, by2 + (20 - coinSz) / 2, coinSz);
            g.setColor(new Color(255, 215, 50));
            g.drawString(costStr, startX2 + coinSz + 3, by2 + 13);
        }
        int btnY = y + h - 23;
        if (!equipped) {
            String label  = owned ? "Equip" : "Buy";
            Color  btnC   = owned ? new Color(60,180,50) : new Color(180,130,20);
            g.setColor(btnC.darker()); g.fillRoundRect(x+6,btnY,w-12,18,6,6);
            g.setColor(btnC.brighter()); g.setStroke(new BasicStroke(1f));
            g.drawRoundRect(x+6,btnY,w-12,18,6,6); g.setStroke(new BasicStroke(1f));
            g.setFont(FontManager.getBold(9)); g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(label, x+6+(w-12-fm.stringWidth(label))/2, btnY+12);
        }
    }

    // ── basket badge ──────────────────────────────────────────────────────────
    private void drawBasketBadge(Graphics2D g, int x, int y, int w, int h,
            BasketSkin skin, boolean owned, boolean equipped) {
        int by2 = y + h - 46;
        if (equipped) {
            g.setColor(new Color(50,180,50,180));  g.fillRoundRect(x+6,by2,w-12,20,8,8);
            g.setColor(new Color(120,255,100)); g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x+6,by2,w-12,20,8,8); g.setStroke(new BasicStroke(1f));
            g.setFont(FontManager.getBold(9)); g.setColor(Color.WHITE);
            FontMetrics fm=g.getFontMetrics(); String t="[OK] EQUIPPED";
            g.drawString(t, x+6+(w-12-fm.stringWidth(t))/2, by2+13);
        } else if (owned) {
            g.setColor(new Color(40,120,40,180));  g.fillRoundRect(x+6,by2,w-12,20,8,8);
            g.setColor(new Color(90,200,70)); g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x+6,by2,w-12,20,8,8); g.setStroke(new BasicStroke(1f));
            g.setFont(FontManager.getBold(9)); g.setColor(new Color(150,255,120));
            FontMetrics fm=g.getFontMetrics(); String t="OWNED  Equip";
            g.drawString(t, x+6+(w-12-fm.stringWidth(t))/2, by2+13);
        } else {
            g.setColor(new Color(30,60,20,200));   g.fillRoundRect(x+6,by2,w-12,20,8,8);
            g.setColor(new Color(180,140,30)); g.setStroke(new BasicStroke(1.2f));
            g.drawRoundRect(x+6,by2,w-12,20,8,8); g.setStroke(new BasicStroke(1f));
            // coin icon + cost
            int coinSz = 11;
            String costStr = "" + skin.getCost();
            g.setFont(FontManager.getBold(10));
            FontMetrics fm = g.getFontMetrics();
            int totalW2 = coinSz + 3 + fm.stringWidth(costStr);
            int startX2 = x + 6 + (w - 12 - totalW2) / 2;
            drawCoinIcon(g, startX2, by2 + (20 - coinSz) / 2, coinSz);
            g.setColor(new Color(255, 215, 50));
            g.drawString(costStr, startX2 + coinSz + 3, by2 + 13);
        }
        int btnY = y + h - 23;
        if (!equipped) {
            String label = owned ? "Equip" : "Buy";
            Color  btnC  = owned ? new Color(60,180,50) : new Color(180,130,20);
            g.setColor(btnC.darker()); g.fillRoundRect(x+6,btnY,w-12,18,6,6);
            g.setColor(btnC.brighter()); g.setStroke(new BasicStroke(1f));
            g.drawRoundRect(x+6,btnY,w-12,18,6,6); g.setStroke(new BasicStroke(1f));
            g.setFont(FontManager.getBold(9)); g.setColor(Color.WHITE);
            FontMetrics fm = g.getFontMetrics();
            g.drawString(label, x+6+(w-12-fm.stringWidth(label))/2, btnY+12);
        }
    }

    // ── hint bar at bottom centre ─────────────────────────────────────────────
    private void drawHintBar(Graphics2D g) {
        int pw = 280, ph = 36, py = GamePanel.H - 52;
        int px = GamePanel.W / 2 - pw / 2;
        g.setColor(new Color(20, 50, 14, 200));
        g.fillRoundRect(px, py, pw, ph, 12, 12);
        g.setColor(new Color(80, 160, 55));
        g.setStroke(new BasicStroke(1.2f));
        g.drawRoundRect(px, py, pw, ph, 12, 12);
        g.setStroke(new BasicStroke(1f));
        g.setFont(FontManager.getBody(11));
        g.setColor(new Color(180, 220, 150));
        String hint = shopTab == 0
                ? "Click a skin to equip or buy"
                : "Click a basket to equip or buy";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(hint, px + (pw - fm.stringWidth(hint)) / 2, py + 23);
    }

    // ── mouse / key events ────────────────────────────────────────────────────
    @Override
    public void onMouseMoved(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        hovered = -1;
        if (backBtn != null && backBtn.contains(mx, my)) { hovered = 9999; return; }

        // tabs
        int tabW = 200;
        int totalW = 2 * tabW + 8;
        int startTab = (GamePanel.W - totalW) / 2;
        for (int i = 0; i < 2; i++) {
            int tx = startTab + i * (tabW + 8);
            if (new Rectangle(tx, 82, tabW, 34).contains(mx, my)) { hovered = 1000 + i; return; }
        }

        if (shopTab == 0) {
            SkinType[] skins = SkinType.values();
            for (int i = 0; i < skins.length; i++) {
                int col = i % COLS, row = i / COLS;
                int bx = STARTX + col * (BW + GAP), by = STARTY + row * (BH + GAP);
                if (new Rectangle(bx, by, BW, BH).contains(mx, my)) { hovered = i; return; }
            }
        } else {
            BasketSkin[] baskets = BasketSkin.values();
            for (int i = 0; i < baskets.length; i++) {
                int col = i % COLS, row = i / COLS;
                int bx = STARTX + col * (BW + GAP), by = STARTY + row * (BH + GAP);
                if (new Rectangle(bx, by, BW, BH).contains(mx, my)) { hovered = 200 + i; return; }
            }
        }
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        int mx = e.getX(), my = e.getY();

        // tabs
        int tabW = 200;
        int totalW = 2 * tabW + 8;
        int startTab = (GamePanel.W - totalW) / 2;
        for (int i = 0; i < 2; i++) {
            int tx = startTab + i * (tabW + 8);
            if (new Rectangle(tx, 82, tabW, 34).contains(mx, my)) { shopTab = i; return; }
        }

        // back
        if (backBtn != null && backBtn.contains(mx, my)) {
            panel.switchToWithFade(GameScreenType.MAIN_MENU); return;
        }

        if (shopTab == 0) {
            SkinType[] skins = SkinType.values();
            for (int i = 0; i < skins.length; i++) {
                int col = i % COLS, row = i / COLS;
                int bx = STARTX + col * (BW + GAP), by = STARTY + row * (BH + GAP);
                if (new Rectangle(bx, by, BW, BH).contains(mx, my)) {
                    SkinType s = skins[i];
                    if (panel.getPlayerData().ownsSkin(s)) {
                        panel.getPlayerData().equipSkin(s);
                    } else if (s.getCost() == 0) {
                        panel.getPlayerData().buySkin(s);
                        panel.getPlayerData().equipSkin(s);
                    } else if (panel.getPlayerData().spendCoins(s.getCost())) {
                        panel.getPlayerData().buySkin(s);
                        panel.getPlayerData().equipSkin(s);
                    }
                    return;
                }
            }
        } else {
            BasketSkin[] baskets = BasketSkin.values();
            for (int i = 0; i < baskets.length; i++) {
                int col = i % COLS, row = i / COLS;
                int bx = STARTX + col * (BW + GAP), by = STARTY + row * (BH + GAP);
                if (new Rectangle(bx, by, BW, BH).contains(mx, my)) {
                    BasketSkin b = baskets[i];
                    if (panel.getPlayerData().ownsBasket(b)) {
                        panel.getPlayerData().equipBasket(b);
                    } else if (b.getCost() == 0) {
                        panel.getPlayerData().buyBasket(b);
                        panel.getPlayerData().equipBasket(b);
                    } else if (panel.getPlayerData().spendCoins(b.getCost())) {
                        panel.getPlayerData().buyBasket(b);
                        panel.getPlayerData().equipBasket(b);
                    }
                    return;
                }
            }
        }
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }
}