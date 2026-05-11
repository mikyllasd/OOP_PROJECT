package screens;

import core.Screen;
import enums.GameScreenType;
import enums.SkinType;
import managers.GamePanel;
import utils.*;
import java.awt.*;
import java.awt.event.*;

public class CharacterCreationScreen extends Screen {

    private StringBuilder nameInput = new StringBuilder();
    private int selectedSkin = 0;

    private static final SkinType[] STARTERS = {
        SkinType.FARMER_MALE,
        SkinType.FARMER_FEMALE,
        SkinType.FARM_KID
    };

    public CharacterCreationScreen(GamePanel panel) {
        super(panel);
    }

    @Override
    public void onEnter() {
        super.onEnter();
        nameInput.setLength(0);
        selectedSkin = 0;
    }

    @Override
    public void update() { tickCount++; }

    @Override
    public void draw(Graphics2D g) {
        BackgroundRenderer.drawSky(g, 1, tickCount,
                GamePanel.ARENA_W, GamePanel.H, null, null);
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);
        drawPanel(g);
        drawTitle(g);
        drawNameField(g);
        drawSkinPicker(g);
        drawStartButton(g);
        drawBackLink(g);
    }

    private void drawPanel(Graphics2D g) {
        RenderUtils.drawGradientPanel(g,
                GamePanel.W / 2 - 280, 60,
                560, 520,
                new Color(28, 65, 18, 230),
                new Color(18, 45, 10, 230),
                new Color(90, 170, 65), 2f, 20);
    }

    private void drawTitle(Graphics2D g) {
        RenderUtils.drawCenteredText(g,
                "\uD83C\uDF3E Create Your Farmer",
                GamePanel.W / 2, 113,
                FontManager.getBold(26),
                ColorPalette.TEXT_GOLD);
    }

    private void drawNameField(Graphics2D g) {
        g.setFont(FontManager.getBodyBold(15));
        g.setColor(ColorPalette.TEXT_GREEN_LIGHT);
        g.drawString("Farmer Name:", GamePanel.W / 2 - 200, 163);
        RenderUtils.drawRoundPanel(g,
                GamePanel.W / 2 - 200, 173, 400, 40,
                new Color(45, 95, 35),
                new Color(100, 200, 80), 2f, 8);
        g.setFont(FontManager.getBody(18));
        g.setColor(Color.WHITE);
        String cursor = tickCount % 60 < 30 ? "|" : "";
        g.drawString(nameInput.toString() + cursor,
                GamePanel.W / 2 - 188, 201);
    }

    private void drawSkinPicker(Graphics2D g) {
        g.setFont(FontManager.getBodyBold(15));
        g.setColor(ColorPalette.TEXT_GREEN_LIGHT);
        g.drawString("Choose Your Starter:",
                GamePanel.W / 2 - 200, 248);
        int bw = 120, bh = 130;
        int sx = GamePanel.W / 2
                - (STARTERS.length * bw
                + (STARTERS.length - 1) * 10) / 2;
        for (int i = 0; i < STARTERS.length; i++) {
            int bx = sx + i * (bw + 10);
            boolean sel = i == selectedSkin;
            Color bg  = sel
                    ? new Color(80, 160, 60, 210)
                    : new Color(40, 80, 30, 190);
            Color bdr = sel
                    ? new Color(150, 235, 100)
                    : new Color(70, 130, 60);
            RenderUtils.drawRoundPanel(g,
                    bx, 265, bw, bh,
                    bg, bdr, sel ? 3f : 1.5f, 12);
            g.setFont(FontManager.getEmoji(40));
            FontMetrics fm = g.getFontMetrics();
            String em = STARTERS[i].getDisplayName();
            g.setColor(Color.WHITE);
            g.drawString(em, bx + (bw - fm.stringWidth(em)) / 2,
                    323);
            g.setFont(FontManager.getBodyBold(11));
            FontMetrics fm2 = g.getFontMetrics();
            String dn = STARTERS[i].getDisplayName();
            g.setColor(new Color(200, 240, 170));
            g.drawString(dn,
                    bx + (bw - fm2.stringWidth(dn)) / 2, 358);
        }
    }

    private void drawStartButton(Graphics2D g) {
        boolean can = nameInput.length() > 0;
        Rectangle r = new Rectangle(
                GamePanel.W / 2 - 130, 488, 260, 50);
        RenderUtils.drawButton(g, r,
                "Start Farming! \uD83C\uDF3E", can,
                FontManager.getBold(17));
    }

    private void drawBackLink(Graphics2D g) {
        g.setFont(FontManager.getBodyBold(13));
        g.setColor(new Color(180, 220, 150));
        g.drawString("\u2190 Back to Menu",
                GamePanel.W / 2 - 280 + 15, 553);
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE
                && nameInput.length() > 0)
            nameInput.deleteCharAt(nameInput.length() - 1);
        if (e.getKeyCode() == KeyEvent.VK_ENTER
                && nameInput.length() > 0)
            startGame();
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            panel.getScreenManager()
                 .switchTo(GameScreenType.MAIN_MENU);
    }

    @Override
    public void onKeyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (c >= 32 && c < 127 && nameInput.length() < 16)
            nameInput.append(c);
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        int bw = 120, bh = 130;
        int sx = GamePanel.W / 2
                - (STARTERS.length * bw
                + (STARTERS.length - 1) * 10) / 2;
        for (int i = 0; i < STARTERS.length; i++) {
            int bx = sx + i * (bw + 10);
            if (new Rectangle(bx, 265, bw, bh)
                    .contains(mx, my))
                selectedSkin = i;
        }
        if (new Rectangle(
                GamePanel.W / 2 - 130, 488, 260, 50)
                .contains(mx, my)
                && nameInput.length() > 0)
            startGame();
        if (my > 530 && mx < 220)
            panel.getScreenManager()
                 .switchTo(GameScreenType.MAIN_MENU);
    }

    private void startGame() {
        panel.getPlayerData().equipSkin(
                STARTERS[selectedSkin]);
        GameScreen gs = panel.getScreenManager()
                             .getGameScreen();
        gs.setPlayerName(nameInput.toString().trim());
        panel.getScreenManager()
             .switchTo(GameScreenType.GAME);
    }

    public String getNameInput() {
        return nameInput.toString();
    }
}