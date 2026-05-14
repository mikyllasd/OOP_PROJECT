package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.*;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.renderers.CharacterRenderer;
import OOP_PROJECT.CatchTheBall.src.renderers.FarmHouseRenderer;
import OOP_PROJECT.CatchTheBall.src.renderers.FarmBackgroundRenderer;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class CharacterCreationScreen extends Screen {
    private StringBuilder nameInput    = new StringBuilder();
    private int           selectedSkin = 0;
    private int           selectedDiff = 1;

    private static final SkinType[]   STARTERS   = {SkinType.FARMER_MALE, SkinType.FARMER_FEMALE, SkinType.FARM_KID};
    private static final Difficulty[] DIFFS       = {Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD};
    private static final Color[]      DIFF_COLORS = {new Color(80,200,80), new Color(255,200,50), new Color(255,80,80)};

    // ── layout constants ────────────────────────────────────────────────────
    // Two-column layout: card on left, farmhouse panel on right.
    private static final int CARD_Y          = 85;
    private static final int CARD_H          = 470;
    private static final int CARD_W          = 520;

    // Y-positions (X is computed dynamically from cardX in draw)
    private static final int TITLE_Y         = CARD_Y + 40;
    private static final int NAME_LBL_Y      = CARD_Y + 72;
    private static final int NAME_BOX_Y      = CARD_Y + 80;
    private static final int SKIN_LBL_Y      = CARD_Y + 138;
    private static final int SKIN_BOX_Y      = CARD_Y + 150;
    private static final int SKIN_BH         = 120;
    private static final int DIFF_LBL_Y      = CARD_Y + 295;
    private static final int DIFF_BTN_Y      = CARD_Y + 307;
    private static final int START_BTN_Y     = CARD_Y + 362;
    private static final int BACK_LBL_Y      = CARD_Y + 448;

    // Farmhouse panel (right column)
    private static final int HOUSE_PANEL_W   = 220;
    private static final int HOUSE_PANEL_H   = 300;

    public CharacterCreationScreen(GamePanel panel) { super(panel); }

    @Override
    public void onEnter() {
        super.onEnter();
        nameInput.setLength(0);
        String pn = panel.getPlayerData().getProfile().getFarmerName();
        if (pn != null && !pn.equals("Farmer")) nameInput.append(pn);
        selectedSkin = 0;
        selectedDiff = panel.getPlayerData().getDefaultDifficulty().ordinal();
    }

    @Override public void update() { tickCount++; }

    private static final int GAP          = 16;   // gap between card and house panel

    /** Total width of both panels side by side. */
    private int totalW()  { return CARD_W + GAP + HOUSE_PANEL_W + 40; }
    /** Left edge of the whole two-column group, centred on screen. */
    private int groupX()  { return (GamePanel.W - totalW()) / 2; }
    /** Left edge of the card (left column). */
    private int cardX()   { return groupX(); }
    /** Left edge of the farmhouse panel (right column). */
    private int houseX()  { return groupX() + CARD_W + GAP; }

    @Override
    public void draw(Graphics2D g) {
        // 1. Background
        FarmBackgroundRenderer.draw(g, GamePanel.W, GamePanel.H, tickCount);

        // 2. Dark overlay
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);

        // 3. Header bar
        RenderUtils.drawHeaderBar(g, GamePanel.W, "Character Creation");

        // 4. Farmhouse showcase panel (right column) – draw BEFORE card so card is on top
        drawFarmhouseShowcase(g);

        // 5. Card panel (left column)
        int cx = cardX();
        RenderUtils.drawGradientPanel(g, cx, CARD_Y, CARD_W, CARD_H,
                new Color(24, 58, 16, 230), new Color(14, 38, 8, 230),
                new Color(90, 175, 65), 2f, 22);

        // 6. Card title
        RenderUtils.drawCenteredText(g, "Create Your Farmhand",
                cx + CARD_W / 2, TITLE_Y, FontManager.getBold(24), ColorPalette.TEXT_GOLD);

        // 7. Sections
        drawNameField(g, cx);
        drawSkinPicker(g, cx);
        drawDifficultyPicker(g, cx);
        drawStartButton(g, cx);

        // 8. Back link
        g.setFont(FontManager.getBodyBold(13));
        g.setColor(new Color(180, 220, 150));
        int backX = cx + 12;
        drawArrowIcon(g, backX, BACK_LBL_Y - 11, false);
        g.drawString("Back to Menu", backX + 18, BACK_LBL_Y);
    }

    // ── Name field ───────────────────────────────────────────────────────────
    private void drawNameField(Graphics2D g, int cx) {
        int lx = cx + 18;
        int boxW = CARD_W - 36;

        drawTagIcon(g, lx, NAME_LBL_Y - 13);
        g.setFont(FontManager.getBodyBold(15));
        g.setColor(ColorPalette.TEXT_GREEN_LIGHT);
        g.drawString("Farmer Name:", lx + 18, NAME_LBL_Y);

        RenderUtils.drawRoundPanel(g, lx, NAME_BOX_Y, boxW, 38,
                new Color(38, 85, 28), new Color(100, 200, 75), 2f, 8);

        g.setFont(FontManager.getBody(17));
        g.setColor(Color.WHITE);
        g.drawString(nameInput.toString() + (tickCount % 60 < 30 ? "|" : ""),
                lx + 12, NAME_BOX_Y + 26);
    }

    // ── Skin picker ──────────────────────────────────────────────────────────
    private void drawSkinPicker(Graphics2D g, int cx) {
        int lx = cx + 18;

        drawShirtIcon(g, lx, SKIN_LBL_Y - 13);
        g.setFont(FontManager.getBodyBold(14));
        g.setColor(ColorPalette.TEXT_GREEN_LIGHT);
        g.drawString("Choose Starter Skin:", lx + 18, SKIN_LBL_Y);

        int bw = 148;
        int totalW = STARTERS.length * bw + (STARTERS.length - 1) * 8;
        int sx = cx + (CARD_W - totalW) / 2;

        for (int i = 0; i < STARTERS.length; i++) {
            int bx = sx + i * (bw + 8);
            boolean sel = (i == selectedSkin);

            RenderUtils.drawGradientPanel(g, bx, SKIN_BOX_Y, bw, SKIN_BH,
                    sel ? new Color(78, 158, 58)   : new Color(36, 75, 26),
                    sel ? new Color(54, 118, 38)   : new Color(24, 52, 16),
                    sel ? new Color(148, 238, 100) : new Color(68, 128, 58),
                    sel ? 3f : 1.5f, 12);

            drawMiniCharacter(g, STARTERS[i], bx + bw / 2, SKIN_BOX_Y + 8,
                    bw - 10, SKIN_BH - 30, tickCount);

            g.setFont(FontManager.getBodyBold(11));
            FontMetrics fm = g.getFontMetrics();
            String dn = STARTERS[i].getDisplayName();
            g.setColor(new Color(200, 240, 170));
            g.drawString(dn, bx + (bw - fm.stringWidth(dn)) / 2, SKIN_BOX_Y + SKIN_BH - 10);
        }
    }

    /**
     * Draws a mini character centred at (cx, y) scaled to fit within (w × h).
     * BUG FIX: scale is now derived from the available height so the character
     * actually fills – and stays inside – the card.
     */
    private void drawMiniCharacter(Graphics2D g, SkinType skin,
                                   int cx, int y, int w, int h, int tick) {
        // The standalone character spans roughly 70 px tall and 50 px wide.
        // Cap at 0.75 so characters stay proportionally small inside the card.
        float scale = Math.min(w / 50f, h / 70f) * 0.75f;

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Translate so the character is horizontally centred and vertically placed from y
        g2.translate(cx - 25 * scale, y);
        g2.scale(scale, scale);

        drawStandaloneCharacter(g2, skin, 0, 0, tick);
        g2.dispose();
    }

    private void drawStandaloneCharacter(Graphics2D g, SkinType skin, int x, int y, int tick) {
        int cx = x + 25;
        boolean walk = (tick / 8) % 2 == 0;

        Color skinTone, hairColor, shirtColor, pantsColor, shoeColor;
        switch (skin) {
            case FARMER_FEMALE:
                skinTone  = new Color(240, 195, 145); hairColor  = new Color(180, 100, 60);
                shirtColor= new Color(240, 140, 160); pantsColor = new Color(200, 100, 130);
                shoeColor = new Color(180, 100, 120);
                break;
            case FARM_KID:
                skinTone  = new Color(255, 210, 160); hairColor  = new Color(200, 150, 80);
                shirtColor= new Color(100, 180, 240); pantsColor = new Color(80, 130, 200);
                shoeColor = new Color(80, 60, 200);
                break;
            default: // FARMER_MALE
                skinTone  = new Color(220, 175, 120); hairColor  = new Color(110, 70, 30);
                shirtColor= new Color(100, 140, 200); pantsColor = new Color(60, 90, 160);
                shoeColor = new Color(60, 40, 20);
        }

        // BUG FIX: cast bob to int once so arithmetic is unambiguous
        int bob = (int) (Math.sin(tick * 0.1) * 2);

        // Shadow
        g.setColor(new Color(0, 0, 0, 35));
        g.fillOval(cx - 16, y + 58 + bob, 32, 7);

        // Legs
        g.setColor(pantsColor);
        g.fillRect(cx - 8, y + 38 + bob, 8, 18);
        g.fillRect(cx + 1,  y + 38 + bob, 8, 18);

        // Shoes
        g.setColor(shoeColor);
        g.fillRoundRect(cx - 10 + (walk ? 2 : 0), y + 54 + bob, 12, 6, 4, 4);
        g.fillRoundRect(cx + 1  + (walk ? 0 : 2), y + 54 + bob, 12, 6, 4, 4);

        // Body
        g.setColor(shirtColor);
        int[] bxArr = {cx - 12, cx + 12, cx + 10, cx - 10};
        int[] byArr = {y + 18 + bob, y + 18 + bob, y + 38 + bob, y + 38 + bob};
        g.fillPolygon(bxArr, byArr, 4);

        // Shirt highlight
        g.setColor(new Color(255, 255, 255, 35));
        g.fillRect(cx - 10, y + 19 + bob, 8, 16);

        // Arms (swinging)
        int swing = (int) (Math.sin(tick * 0.12) * 5);
        g.setColor(shirtColor.darker());
        g.fillRoundRect(cx - 18, y + 22 + bob + swing, 7, 13, 3, 3);
        g.fillRoundRect(cx + 11, y + 22 + bob - swing, 7, 13, 3, 3);
        g.setColor(skinTone);
        g.fillOval(cx - 18, y + 33 + bob + swing, 8, 8);
        g.fillOval(cx + 11, y + 33 + bob - swing, 8, 8);

        // Neck
        g.setColor(skinTone);
        g.fillRoundRect(cx - 4, y + 11 + bob, 8, 9, 4, 4);

        // Head
        g.fillOval(cx - 12, y + bob, 24, 22);

        // Cheeks
        g.setColor(new Color(255, 160, 140, 70));
        g.fillOval(cx - 12, y + 9 + bob, 8, 6);
        g.fillOval(cx + 4,  y + 9 + bob, 8, 6);

        // Eyes (white)
        g.setColor(Color.WHITE);
        g.fillOval(cx - 9, y + 7 + bob, 7, 6);
        g.fillOval(cx + 2, y + 7 + bob, 7, 6);
        // Iris
        g.setColor(new Color(40, 60, 180));
        g.fillOval(cx - 7, y + 9 + bob, 4, 4);
        g.fillOval(cx + 4, y + 9 + bob, 4, 4);
        // Pupil
        g.setColor(Color.BLACK);
        g.fillOval(cx - 6, y + 10 + bob, 2, 2);
        g.fillOval(cx + 5, y + 10 + bob, 2, 2);
        // Highlight
        g.setColor(Color.WHITE);
        g.fillOval(cx - 5, y + 10 + bob, 1, 1);
        g.fillOval(cx + 6, y + 10 + bob, 1, 1);

        // Blink
        if (tick % 100 < 3) {
            g.setColor(skinTone);
            g.fillRect(cx - 9, y + 9 + bob, 7, 4);
            g.fillRect(cx + 2, y + 9 + bob, 7, 4);
        }

        // Smile
        Stroke origStroke = g.getStroke();   // BUG FIX: save and restore stroke
        g.setColor(new Color(180, 80, 80));
        g.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(cx - 4, y + 14 + bob, 8, 5, 200, 140);

        // Eyebrows
        g.setColor(hairColor.darker());
        g.drawLine(cx - 8, y + 5 + bob, cx - 4, y + 4 + bob);
        g.drawLine(cx + 4, y + 4 + bob, cx + 8, y + 5 + bob);
        g.setStroke(origStroke);  // BUG FIX: restore instead of hard-coding new BasicStroke(1f)

        // Hat / Hair per skin
        switch (skin) {
            case FARMER_MALE:
                // Straw hat brim
                g.setColor(new Color(210, 180, 80));
                g.fillOval(cx - 18, y - 2 + bob, 36, 10);
                // Crown
                g.setColor(new Color(190, 155, 60));
                g.fillRoundRect(cx - 10, y - 13 + bob, 20, 14, 6, 6);
                // Band
                g.setColor(new Color(160, 120, 40));
                g.fillRect(cx - 10, y - 2 + bob, 20, 3);
                break;

            case FARMER_FEMALE:
                // Pink hat brim
                g.setColor(new Color(220, 160, 180));
                g.fillOval(cx - 16, y - 3 + bob, 32, 12);
                // Crown
                g.setColor(new Color(200, 130, 160));
                g.fillRoundRect(cx - 10, y - 14 + bob, 20, 14, 8, 8);
                // Band
                g.setColor(new Color(255, 150, 180));
                g.fillRect(cx - 10, y - 3 + bob, 20, 3);
                // Flower centre
                g.setColor(new Color(255, 200, 50));
                g.fillOval(cx + 4, y - 16 + bob, 7, 7);
                // Petals
                g.setColor(new Color(255, 100, 100));
                for (int p = 0; p < 5; p++) {
                    double a = Math.toRadians(p * 72);
                    g.fillOval((int)(cx + 7 + Math.cos(a) * 4) - 2,
                               (int)(y - 13 + bob + Math.sin(a) * 4) - 2, 4, 4);
                }
                break;

            case FARM_KID:
                // Red cap
                g.setColor(new Color(200, 60, 60));
                g.fillOval(cx - 11, y - 10 + bob, 22, 16);
                g.fillOval(cx - 13, y       + bob, 26,  7);
                // Cap seam
                g.setColor(Color.WHITE);
                Stroke cs = g.getStroke();
                g.setStroke(new BasicStroke(1f));
                g.drawLine(cx, y - 10 + bob, cx, y - 4 + bob);
                g.setStroke(cs);
                break;
        }

        // Hair tufts (peeking below hat)
        g.setColor(hairColor);
        g.fillRect(cx - 10, y + 17 + bob, 20, 5);
    }

    // ── Difficulty picker ────────────────────────────────────────────────────
    private void drawDifficultyPicker(Graphics2D g, int cx) {
        int lx = cx + 18;

        drawStarIcon(g, lx, DIFF_LBL_Y - 13, new Color(255, 200, 50));
        g.setFont(FontManager.getBodyBold(14));
        g.setColor(ColorPalette.TEXT_GREEN_LIGHT);
        g.drawString("Difficulty:", lx + 18, DIFF_LBL_Y);

        int bw = 148, bh = 42;
        int totalDW = DIFFS.length * bw + (DIFFS.length - 1) * 8;
        int dsx = cx + (CARD_W - totalDW) / 2;

        for (int i = 0; i < DIFFS.length; i++) {
            int bx = dsx + i * (bw + 8);
            boolean sel = (i == selectedDiff);
            Color c = DIFF_COLORS[i];
            RenderUtils.drawGradientPanel(g, bx, DIFF_BTN_Y, bw, bh,
                    sel ? c           : new Color(40, 80, 28),
                    sel ? c.darker()  : new Color(26, 56, 18),
                    sel ? c.brighter(): new Color(68, 120, 52),
                    sel ? 2.5f : 1.2f, 10);

            RenderUtils.drawCenteredText(g, DIFFS[i].getDisplayName(),
                    bx + bw / 2, DIFF_BTN_Y + bh / 2 + 6,
                    FontManager.getBold(14),
                    sel ? Color.WHITE : new Color(180, 220, 150));
        }
    }

    // ── Start button ─────────────────────────────────────────────────────────
    private void drawStartButton(Graphics2D g, int cx) {
        boolean can = nameInput.length() > 0;
        int btnW = CARD_W - 60;
        Rectangle r = new Rectangle(cx + 30, START_BTN_Y, btnW, 48);
        if (can) {
            RenderUtils.drawButton(g, r, "Start Farming!", true, FontManager.getBold(18));
            drawTractorIcon(g, r.x + 14, r.y + 12);
        } else {
            RenderUtils.drawGradientPanel(g, r.x, r.y, r.width, r.height,
                    new Color(40, 70, 30), new Color(28, 52, 20),
                    new Color(70, 110, 55), 1.5f, 14);
            drawLockIcon(g, r.x + 14, r.y + 12);
            RenderUtils.drawCenteredText(g, "Enter a name first",
                    cx + CARD_W / 2, START_BTN_Y + 28,
                    FontManager.getBody(14), new Color(140, 180, 110));
        }
    }

    // ── Farmhouse showcase ───────────────────────────────────────────────────
    private void drawFarmhouseShowcase(Graphics2D g) {
        int hx = houseX();
        int hy = CARD_Y;
        int pw = HOUSE_PANEL_W + 40;   // panel width
        int ph = CARD_H;               // same height as the card

        // Panel background – same style as the card
        RenderUtils.drawGradientPanel(g, hx, hy, pw, ph,
                new Color(20, 50, 12, 220), new Color(10, 30, 6, 220),
                new Color(80, 160, 55), 2f, 22);

        // Section title
        RenderUtils.drawCenteredText(g, "Your Farm",
                hx + pw / 2, hy + 38,
                FontManager.getBold(18), ColorPalette.TEXT_GOLD);

        // Decorative divider
        g.setColor(new Color(90, 175, 65, 120));
        g.fillRect(hx + 20, hy + 48, pw - 40, 2);

        // House image – large and centred
        int houseW = pw - 30;
        int houseH = (int)(houseW * 0.85f);
        int houseDrawX = hx + 15;
        int houseDrawY = hy + 65;
        FarmHouseRenderer.draw(g, panel.getPlayerData().getFarmStage(),
                houseDrawX, houseDrawY, houseW, houseH);

        // Farm stage label (e.g. "Stage 2")
        String stageLabel = "Stage " + (panel.getPlayerData().getFarmStage() + 1);
        g.setFont(FontManager.getBodyBold(13));
        g.setColor(new Color(200, 240, 160));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(stageLabel,
                hx + (pw - fm.stringWidth(stageLabel)) / 2,
                houseDrawY + houseH + 22);

        // Small flavour text
        g.setFont(FontManager.getBody(11));
        g.setColor(new Color(160, 210, 130, 200));
        String sub = "Upgrade your farm as you play!";
        fm = g.getFontMetrics();
        g.drawString(sub, hx + (pw - fm.stringWidth(sub)) / 2,
                houseDrawY + houseH + 40);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SMALL ICON HELPERS  (replace blank boxes)
    // ════════════════════════════════════════════════════════════════════════

    /** ◀ arrow icon */
    private void drawArrowIcon(Graphics2D g, int x, int y, boolean right) {
        g.setColor(new Color(180, 220, 150));
        int[] px = right ? new int[]{x, x + 10, x}     : new int[]{x + 10, x, x + 10};
        int[] py = {y, y + 6, y + 12};
        g.fillPolygon(px, py, 3);
    }

    /** Small 5-point star icon */
    private void drawStarIcon(Graphics2D g, int x, int y, Color c) {
        g.setColor(c);
        int[] px = new int[10];
        int[] py = new int[10];
        for (int i = 0; i < 10; i++) {
            double angle = Math.toRadians(-90 + i * 36);
            double r = (i % 2 == 0) ? 6 : 3;
            px[i] = x + 6 + (int)(Math.cos(angle) * r);
            py[i] = y + 6 + (int)(Math.sin(angle) * r);
        }
        g.fillPolygon(px, py, 10);
    }

    /** Simple tag / label icon */
    private void drawTagIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(180, 220, 150));
        g.fillRoundRect(x, y, 14, 10, 4, 4);
        g.setColor(new Color(24, 58, 16));
        g.fillOval(x + 2, y + 2, 3, 3);
        g.setColor(new Color(180, 220, 150));
        Stroke s = g.getStroke();
        g.setStroke(new BasicStroke(1f));
        g.drawLine(x + 6, y + 5, x + 12, y + 5);
        g.setStroke(s);
    }

    /** Small shirt silhouette icon */
    private void drawShirtIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(180, 220, 150));
        // collar notch
        int[] px = {x, x+3, x+5, x+9, x+11, x+14, x+11, x+3};
        int[] py = {y+3, y,   y+4, y+4, y,    y+3,  y+13, y+13};
        g.fillPolygon(px, py, 8);
    }

    /** Tiny tractor silhouette */
    private void drawTractorIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(255, 220, 80));
        g.fillRect(x + 2, y + 6, 16, 10);          // body
        g.fillRect(x + 12, y + 2, 8, 8);            // cabin
        g.setColor(new Color(60, 60, 60));
        g.fillOval(x,      y + 11, 10, 10);          // rear wheel
        g.fillOval(x + 14, y + 14, 7, 7);            // front wheel
    }

    /** Padlock icon */
    private void drawLockIcon(Graphics2D g, int x, int y) {
        g.setColor(new Color(140, 180, 110));
        g.fillRoundRect(x + 2, y + 7, 14, 12, 4, 4);  // body
        Stroke s = g.getStroke();
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g.drawArc(x + 4, y, 10, 10, 0, 180);           // shackle arc
        g.setStroke(s);
        g.setColor(new Color(24, 58, 16));
        g.fillOval(x + 7, y + 10, 4, 4);               // keyhole
        g.fillRect(x + 8, y + 13, 2, 4);
    }

    // ════════════════════════════════════════════════════════════════════════
    //  INPUT HANDLING
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE && nameInput.length() > 0)
            nameInput.deleteCharAt(nameInput.length() - 1);
        if (e.getKeyCode() == KeyEvent.VK_ENTER && nameInput.length() > 0) startGame();
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }

    @Override
    public void onKeyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (c >= 32 && c < 127 && nameInput.length() < 16) nameInput.append(c);
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        int cx = cardX();

        // Skin cards
        int bw    = 148;
        int totalW = STARTERS.length * bw + (STARTERS.length - 1) * 8;
        int sx    = cx + (CARD_W - totalW) / 2;
        for (int i = 0; i < STARTERS.length; i++) {
            if (new Rectangle(sx + i * (bw + 8), SKIN_BOX_Y, bw, SKIN_BH).contains(mx, my)) {
                selectedSkin = i;
                return;
            }
        }

        // Difficulty buttons
        int dbw   = 148;
        int totalDW = DIFFS.length * dbw + (DIFFS.length - 1) * 8;
        int dsx   = cx + (CARD_W - totalDW) / 2;
        for (int i = 0; i < DIFFS.length; i++) {
            int bx = dsx + i * (dbw + 8);
            if (new Rectangle(bx, DIFF_BTN_Y, dbw, 42).contains(mx, my)) {
                selectedDiff = i;
                return;
            }
        }

        // Start button
        int btnW = CARD_W - 60;
        if (new Rectangle(cx + 30, START_BTN_Y, btnW, 48).contains(mx, my)
                && nameInput.length() > 0) {
            startGame();
            return;
        }

        // Back to menu
        int backX = cx + 12;
        if (new Rectangle(backX, BACK_LBL_Y - 14, 140, 18).contains(mx, my)) {
            panel.switchToWithFade(GameScreenType.MAIN_MENU);
        }
    }

    private void startGame() {
        panel.getPlayerData().getProfile().setFarmerName(nameInput.toString().trim());
        panel.getPlayerData().equipSkin(STARTERS[selectedSkin]);
        panel.getPlayerData().save();
        GameScreen gs = panel.getScreenManager().getGameScreen();
        gs.setPlayerName(nameInput.toString().trim());
        gs.setDifficulty(DIFFS[selectedDiff]);
        panel.switchToWithFade(GameScreenType.GAME);
    }
}