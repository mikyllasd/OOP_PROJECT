package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.accounts.AccountManager;
import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.models.AccountData;
import OOP_PROJECT.CatchTheBall.src.models.FarmProgression;
import OOP_PROJECT.CatchTheBall.src.renderers.FarmHouseRenderer;
import OOP_PROJECT.CatchTheBall.src.renderers.FarmBackgroundRenderer;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.awt.geom.*;

public class AccountSelectScreen extends Screen {
    private StringBuilder newNameInput  = new StringBuilder();
    private boolean       creatingNew   = false;
    private int           hovered       = -1;
    private int           deleteConfirm = -1;
    private boolean       renaming      = false;
    private int           renamingIndex = -1;
    private StringBuilder renameInput   = new StringBuilder();
    private String        statusMsg     = "";
    private int           statusTimer   = 0;
    private Rectangle     exitBtn;

    public AccountSelectScreen(GamePanel panel) { super(panel); }

    @Override
    public void onEnter() {
        super.onEnter();
        newNameInput.setLength(0);
        creatingNew=false; renaming=false;
        renamingIndex=-1; statusMsg="";
    }

    @Override
    public void update() { tickCount++; if (statusTimer>0) statusTimer--; }

    @Override
    public void draw(Graphics2D g) {
        FarmBackgroundRenderer.draw(g, GamePanel.W, GamePanel.H, tickCount);

        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);

        RenderUtils.drawHeaderBar(g, GamePanel.W, "Choose Your Account");

        AccountManager am = panel.getAccountManager();
        List<String> names = am.getAccountNames();

        // Card is 200 wide, 210 tall — compact but room for house+text+buttons
        int cardW = 200, cardH = 210, cols = 4;
        int totalRowCards = Math.min(names.size(), cols);
        int startX = totalRowCards > 0
                ? (GamePanel.W - (totalRowCards * (cardW + 20) - 20)) / 2
                : GamePanel.W / 2 - cardW / 2;
        int startY = 110;

        for (int i = 0; i < names.size(); i++) {
            int col = i % cols, row = i / cols;
            int cx = startX + col * (cardW + 20);
            int cy = startY + row * (cardH + 20);
            drawAccountCard(g, am.getAccountSummary(names.get(i)), cx, cy, cardW, cardH, i);
        }

        int rows = names.size() / cols + (names.size() % cols > 0 ? 1 : 0);
        int ny = startY + rows * (cardH + 20) + 10;

        if (!creatingNew) {
            RenderUtils.drawButton(g, new Rectangle(GamePanel.W / 2 - 135, ny, 270, 44),
                    "+ Create New Account", hovered == 999, FontManager.getBold(14));
        } else {
            drawCreatePanel(g, ny);
        }

        if (renaming && renamingIndex >= 0 && renamingIndex < names.size())
            drawRenamePanel(g, names.get(renamingIndex));

        if (statusTimer > 0)
            RenderUtils.drawCenteredText(g, statusMsg, GamePanel.W / 2, GamePanel.H - 20,
                    FontManager.getBodyBold(13), ColorPalette.TEXT_SUCCESS);
    }

    private void drawAccountCard(Graphics2D g, AccountData data,
                                 int x, int y, int w, int h, int idx) {
        boolean hov = hovered == idx;

        // Card background
        RenderUtils.drawGradientPanel(g, x, y, w, h,
                hov ? new Color(60, 120, 40) : new Color(35, 75, 25),
                hov ? new Color(40, 90, 28)  : new Color(22, 52, 15),
                hov ? new Color(150, 240, 100) : new Color(80, 150, 60),
                hov ? 2.5f : 1.5f, 14);

        // House: 70x70, clipped strictly inside card, 8px from top
        int houseW = 70, houseH = 70;
        int houseX = x + (w - houseW) / 2;
        int houseY = y + 8;

        Shape oldClip = g.getClip();
        // Clip rect is exactly the house box — nothing bleeds outside
        g.setClip(houseX, houseY, houseW, houseH);
        FarmHouseRenderer.draw(g, data.getFarmStage(), houseX, houseY, houseW, houseH);
        g.setClip(oldClip);

        // house bottom = houseY + houseH = y + 78
        // name at y+92 — 14px gap
        RenderUtils.drawCenteredText(g, data.getAccountName(),
                x + w / 2, y + 92,
                FontManager.getBold(12), ColorPalette.TEXT_GOLD);
        RenderUtils.drawCenteredText(g, "Best: " + data.getBestScore(),
                x + w / 2, y + 109,
                FontManager.getBodyBold(10), new Color(200, 240, 160));
        RenderUtils.drawCenteredText(g, "Coins: " + data.getTotalCoins(),
                x + w / 2, y + 126,
                FontManager.getBodyBold(10), ColorPalette.TEXT_COINS);

        // Buttons pinned to bottom
        Rectangle sel = new Rectangle(x + 8,         y + h - 46, w / 2 - 12, 32);
        Rectangle ren = new Rectangle(x + w / 2 + 4, y + h - 46, w / 2 - 12, 32);

        RenderUtils.drawButton(g, sel, "Select", hov, FontManager.getBold(10));

        RenderUtils.drawGradientPanel(g, ren.x, ren.y, ren.width, ren.height,
                new Color(60, 100, 180), new Color(40, 70, 140),
                new Color(100, 150, 220), 1.5f, 8);
        RenderUtils.drawCenteredText(g, "Rename",
                ren.x + ren.width / 2, ren.y + 20,
                FontManager.getBold(10), Color.WHITE);

        // Delete X
        g.setFont(FontManager.getBold(11));
        g.setColor(new Color(220, 60, 60));
        g.drawString("X", x + w - 16, y + 15);

        // Delete confirmation overlay
        if (deleteConfirm == idx) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRoundRect(x, y, w, h, 14, 14);
            RenderUtils.drawCenteredText(g, "Delete?",
                    x + w / 2, y + h / 2 - 10,
                    FontManager.getBold(13), new Color(255, 80, 80));
            RenderUtils.drawCenteredText(g, "Click again",
                    x + w / 2, y + h / 2 + 8,
                    FontManager.getBody(10), Color.WHITE);
        }
    }

    private void drawCreatePanel(Graphics2D g, int y) {
        RenderUtils.drawGradientPanel(g, GamePanel.W / 2 - 200, y, 400, 76,
                new Color(30, 65, 20, 230), new Color(18, 45, 12, 230),
                new Color(100, 190, 70), 2f, 14);
        RenderUtils.drawCenteredText(g, "Enter account name:", GamePanel.W / 2, y + 20,
                FontManager.getBodyBold(12), ColorPalette.TEXT_GREEN_LIGHT);
        RenderUtils.drawRoundPanel(g, GamePanel.W / 2 - 130, y + 26, 180, 30,
                new Color(45, 90, 32), new Color(100, 190, 70), 1.5f, 6);
        g.setFont(FontManager.getBody(13));
        g.setColor(Color.WHITE);
        g.drawString(newNameInput + (tickCount % 60 < 30 ? "|" : ""),
                GamePanel.W / 2 - 122, y + 47);
        RenderUtils.drawButton(g, new Rectangle(GamePanel.W / 2 + 58, y + 26, 58, 30),
                "OK", hovered == 998, FontManager.getBold(11));
        RenderUtils.drawButton(g, new Rectangle(GamePanel.W / 2 + 122, y + 26, 68, 30),
                "Cancel", hovered == 997, FontManager.getBold(10));
    }

    private void drawRenamePanel(Graphics2D g, String oldName) {
        int pw = 400, ph = 120, px = (GamePanel.W - pw) / 2, py = (GamePanel.H - ph) / 2;
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);
        RenderUtils.drawGradientPanel(g, px, py, pw, ph,
                new Color(30, 65, 20, 245), new Color(18, 45, 12, 245),
                new Color(100, 200, 70), 2f, 16);
        RenderUtils.drawCenteredText(g, "Rename: " + oldName,
                px + pw / 2, py + 28,
                FontManager.getBold(14), ColorPalette.TEXT_GOLD);
        RenderUtils.drawRoundPanel(g, px + 20, py + 38, pw - 120, 32,
                new Color(45, 90, 32), new Color(100, 190, 70), 1.5f, 6);
        g.setFont(FontManager.getBody(14));
        g.setColor(Color.WHITE);
        g.drawString(renameInput + (tickCount % 60 < 30 ? "|" : ""), px + 28, py + 60);
        RenderUtils.drawButton(g, new Rectangle(px + pw - 92, py + 38, 70, 32),
                "Rename", hovered == 996, FontManager.getBold(11));
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        hovered = -1;
        if (exitBtn != null && exitBtn.contains(mx, my)) { hovered = 9998; return; }

        AccountManager am = panel.getAccountManager();
        List<String> names = am.getAccountNames();
        int cardW = 200, cardH = 210, cols = 4;
        int totalRowCards = Math.min(names.size(), cols);
        int startX = totalRowCards > 0
                ? (GamePanel.W - (totalRowCards * (cardW + 20) - 20)) / 2
                : GamePanel.W / 2 - cardW / 2;
        int startY = 110;

        for (int i = 0; i < names.size(); i++) {
            int cx = startX + (i % cols) * (cardW + 20);
            int cy = startY + (i / cols) * (cardH + 20);
            if (new Rectangle(cx, cy, cardW, cardH).contains(mx, my)) { hovered = i; break; }
        }

        int rows = (names.size() / cols) + (names.size() % cols > 0 ? 1 : 0);
        int ny = startY + rows * (cardH + 20) + 10;
        if (new Rectangle(GamePanel.W / 2 - 135, ny, 270, 44).contains(mx, my)) hovered = 999;
        if (new Rectangle(GamePanel.W / 2 + 58,  ny + 26, 58, 30).contains(mx, my)) hovered = 998;
        if (new Rectangle(GamePanel.W / 2 + 122, ny + 26, 68, 30).contains(mx, my)) hovered = 997;
        if (renaming) {
            int pw = 400, ph = 120, px = (GamePanel.W - pw) / 2, py = (GamePanel.H - ph) / 2;
            if (new Rectangle(px + pw - 92, py + 38, 70, 32).contains(mx, my)) hovered = 996;
        }
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        if (exitBtn != null && exitBtn.contains(mx, my)) { System.exit(0); return; }

        AccountManager am = panel.getAccountManager();
        List<String> names = am.getAccountNames();

        if (renaming && renamingIndex >= 0) {
            int pw = 400, ph = 120, px = (GamePanel.W - pw) / 2, py = (GamePanel.H - ph) / 2;
            if (new Rectangle(px + pw - 92, py + 38, 70, 32).contains(mx, my)
                    && renameInput.length() > 0) {
                String oldName = names.get(renamingIndex);
                String newName = renameInput.toString().trim();
                if (!newName.isEmpty() && !am.accountExists(newName)) {
                    am.renameAccount(oldName, newName);
                    statusMsg = "Renamed to " + newName; statusTimer = 120;
                } else { statusMsg = "Name already taken!"; statusTimer = 120; }
                renaming = false; renameInput.setLength(0); return;
            }
            if (!new Rectangle(px, py, pw, ph).contains(mx, my)) {
                renaming = false; renameInput.setLength(0);
            }
            return;
        }

        int cardW = 200, cardH = 210, cols = 4;
        int totalRowCards = Math.min(names.size(), cols);
        int startX = totalRowCards > 0
                ? (GamePanel.W - (totalRowCards * (cardW + 20) - 20)) / 2
                : GamePanel.W / 2 - cardW / 2;
        int startY = 110;

        for (int i = 0; i < names.size(); i++) {
            int cx = startX + (i % cols) * (cardW + 20);
            int cy = startY + (i / cols) * (cardH + 20);
            Rectangle card = new Rectangle(cx, cy, cardW, cardH);
            Rectangle sel  = new Rectangle(cx + 8,            cy + cardH - 46, cardW / 2 - 12, 32);
            Rectangle ren  = new Rectangle(cx + cardW / 2 + 4, cy + cardH - 46, cardW / 2 - 12, 32);
            Rectangle del  = new Rectangle(cx + cardW - 20,   cy + 2, 18, 18);

            if (del.contains(mx, my)) {
                if (deleteConfirm == i) {
                    am.deleteAccount(names.get(i)); deleteConfirm = -1;
                    statusMsg = "Account deleted."; statusTimer = 120;
                } else deleteConfirm = i;
                return;
            }
            if (ren.contains(mx, my)) {
                renamingIndex = i; renaming = true; renameInput.setLength(0); return;
            }
            if (sel.contains(mx, my) || card.contains(mx, my)) {
                selectAccount(names.get(i)); return;
            }
        }
        deleteConfirm = -1;

        int rows = (names.size() / cols) + (names.size() % cols > 0 ? 1 : 0);
        int ny = startY + rows * (cardH + 20) + 10;
        if (!creatingNew && new Rectangle(GamePanel.W / 2 - 135, ny, 270, 44).contains(mx, my)) {
            creatingNew = true; newNameInput.setLength(0); return;
        }
        if (creatingNew) {
            if (new Rectangle(GamePanel.W / 2 + 58, ny + 26, 58, 30).contains(mx, my)
                    && newNameInput.length() > 0) {
                String name = newNameInput.toString().trim();
                if (!name.isEmpty() && !am.accountExists(name)) {
                    am.createAccount(name); panel.loadAccountData(name);
                    creatingNew = false; panel.switchToWithFade(GameScreenType.MAIN_MENU);
                } else { statusMsg = "Name taken or invalid!"; statusTimer = 120; }
            } else if (new Rectangle(GamePanel.W / 2 + 122, ny + 26, 68, 30).contains(mx, my)) {
                creatingNew = false;
            }
        }
    }

    private void selectAccount(String name) {
        panel.getAccountManager().setActiveAccount(name);
        panel.loadAccountData(name);
        panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }

    @Override
    public void onKeyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (renaming) {
            if (c >= 32 && c < 127 && renameInput.length() < 16) renameInput.append(c);
        } else if (creatingNew) {
            if (c >= 32 && c < 127 && newNameInput.length() < 16) newNameInput.append(c);
        }
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
            if (renaming && renameInput.length() > 0)
                renameInput.deleteCharAt(renameInput.length() - 1);
            else if (creatingNew && newNameInput.length() > 0)
                newNameInput.deleteCharAt(newNameInput.length() - 1);
        }
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (renaming) { renaming = false; renameInput.setLength(0); }
            else if (creatingNew) creatingNew = false;
        }
    }
}