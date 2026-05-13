package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.Difficulty;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.renderers.FarmBackgroundRenderer;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class SettingsScreen extends Screen {
    private Rectangle backBtn;
    private Rectangle muteBtn;
    private Rectangle muteMusicBtn;
    private Rectangle switchAccBtn;
    private Rectangle[] diffBtns;
    private int     hovered       = -1;
    private float   sfxVolume     = 0.5f;
    private float   musicVolume   = 0.4f;
    private int     selectedDiff  = 1;
    private boolean draggingSFX   = false;
    private boolean draggingMusic = false;

    // ── layout constants ───────────────────────────────────────────────────────
    private static final int SLIDER_X = 280;
    private static final int SLIDER_W = 300;
    private static final int SFX_Y    = 160;
    private static final int MUSIC_Y  = 212;

    public SettingsScreen(GamePanel panel) { super(panel); }

    @Override
    public void onEnter() {
        super.onEnter();
        sfxVolume    = panel.getSoundManager().getVolume();
        musicVolume  = panel.getMusicManager().getVolume();
        selectedDiff = panel.getPlayerData().getDefaultDifficulty().ordinal();
    }

    @Override public void update() { tickCount++; }

    // ── main draw ─────────────────────────────────────────────────────────────
    @Override
    public void draw(Graphics2D g) {
        // ── shared farm background (matches main menu) ────────────────────────
        FarmBackgroundRenderer.draw(g, GamePanel.W, GamePanel.H, tickCount);

        // ── dark overlay so the panel content is legible ──────────────────────
        g.setColor(new Color(0, 0, 0, 90));
        g.fillRect(0, 0, GamePanel.W, GamePanel.H);

        RenderUtils.drawHeaderBar(g, GamePanel.W, "Settings");

        int panelX = GamePanel.W / 2 - 280, panelY = 90, panelW = 560, panelH = 460;

        RenderUtils.drawGradientPanel(g, panelX, panelY, panelW, panelH,
                new Color(30, 80, 140, 215),
                new Color(15, 55, 100, 215),
                new Color(91, 184, 232),
                2f, 18);

        // ── Sound & Music ─────────────────────────────────────────────────────
        drawSectionLabel(g, "Sound & Music", panelX + 20, 130);
        drawSliderRow(g, "SFX Volume",   panelX + 20, SFX_Y,   sfxVolume);
        drawSliderRow(g, "Music Volume", panelX + 20, MUSIC_Y, musicVolume);

        // ── Default Difficulty ────────────────────────────────────────────────
        drawSectionLabel(g, "Default Difficulty", panelX + 20, 274);
        diffBtns = new Rectangle[3];
        Difficulty[] diffs      = {Difficulty.EASY, Difficulty.NORMAL, Difficulty.HARD};
        Color[]      diffColors = {new Color(80, 200, 80), new Color(255, 200, 50), new Color(255, 80, 80)};
        for (int i = 0; i < 3; i++) {
            int bx  = panelX + 30 + i * 170;
            diffBtns[i] = new Rectangle(bx, 290, 150, 40);
            boolean sel = (i == selectedDiff);
            Color c = diffColors[i];
            RenderUtils.drawGradientPanel(g, bx, 290, 150, 40,
                    sel ? c           : new Color(38, 78, 28),
                    sel ? c.darker()  : new Color(24, 52, 16),
                    sel ? c.brighter(): new Color(65, 115, 50),
                    sel ? 2.5f : 1.2f, 10);
            RenderUtils.drawCenteredText(g, diffs[i].getDisplayName(), bx + 75, 316,
                    FontManager.getBold(14), sel ? Color.WHITE : new Color(180, 220, 150));
        }

        // ── Other (SFX mute / Music mute) ────────────────────────────────────
        drawSectionLabel(g, "Other", panelX + 20, 352);
        muteBtn      = new Rectangle(panelX + 30,  368, 200, 38);
        muteMusicBtn = new Rectangle(panelX + 250, 368, 200, 38);

        boolean sfxMuted   = panel.getSoundManager().isMuted();
        boolean musicMuted = panel.getMusicManager().isMuted();

        drawIconButton(g, muteBtn,      sfxMuted   ? "SFX: Muted"   : "SFX: On",
                hovered == 10, sfxMuted   ? IconType.MUTE : IconType.SPEAKER);
        drawIconButton(g, muteMusicBtn, musicMuted ? "Music: Muted" : "Music: On",
                hovered == 11, musicMuted ? IconType.MUTE : IconType.MUSIC_NOTE);

        // ── Account ───────────────────────────────────────────────────────────
        drawSectionLabel(g, "Account", panelX + 20, 428);
        switchAccBtn = new Rectangle(panelX + 30, 444, 250, 38);
        drawIconButton(g, switchAccBtn, "Switch Account", hovered == 12, IconType.PERSON);

        String acc = panel.getAccountManager().getActiveAccountName();
        g.setFont(FontManager.getBody(12));
        g.setColor(new Color(180, 220, 255));
        g.drawString("Active: " + (acc != null ? acc : "None"), panelX + 300, 468);

        // ── Back button ───────────────────────────────────────────────────────
        backBtn = new Rectangle(20, GamePanel.H - 52, 140, 36);
        drawIconButton(g, backBtn, "Back", hovered == 0, IconType.ARROW_LEFT);
    }

    // ── section label ─────────────────────────────────────────────────────────
    private void drawSectionLabel(Graphics2D g, String label, int x, int y) {
        g.setFont(FontManager.getBold(14));
        g.setColor(ColorPalette.TEXT_GOLD);
        g.drawString(label, x, y);
        g.setColor(new Color(255, 220, 80, 100));
        g.setStroke(new BasicStroke(1f));
        g.drawLine(x, y + 4, x + 200, y + 4);
        g.setStroke(new BasicStroke(1f));
    }

    // ── slider row ────────────────────────────────────────────────────────────
    private void drawSliderRow(Graphics2D g, String label, int panelX, int y, float value) {
        g.setFont(FontManager.getBodyBold(13));
        g.setColor(new Color(210, 240, 255));
        g.drawString(label, panelX, y + 16);

        int sx = SLIDER_X, sw = SLIDER_W;

        g.setColor(new Color(0, 0, 0, 90));
        g.fillRoundRect(sx, y + 6, sw, 12, 12, 12);

        int fillW = (int)(sw * value);
        if (fillW > 0) {
            GradientPaint gp = new GradientPaint(
                    sx, y, new Color(91, 184, 232),
                    sx + fillW, y, new Color(150, 230, 255));
            g.setPaint(gp);
            g.fillRoundRect(sx, y + 6, fillW, 12, 12, 12);
            g.setPaint(null);
        }

        g.setColor(new Color(91, 184, 232, 180));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(sx, y + 6, sw, 12, 12, 12);
        g.setStroke(new BasicStroke(1f));

        int knobX = sx + (int)(sw * value);
        g.setColor(Color.WHITE);
        g.fillOval(knobX - 8, y + 2, 16, 20);
        g.setColor(new Color(91, 184, 232));
        g.setStroke(new BasicStroke(1.5f));
        g.drawOval(knobX - 8, y + 2, 16, 20);
        g.setStroke(new BasicStroke(1f));

        g.setFont(FontManager.getBodyBold(12));
        g.setColor(new Color(210, 240, 255));
        g.drawString((int)(value * 100) + "%", sx + sw + 12, y + 16);
    }

    // ── icon types ────────────────────────────────────────────────────────────
    private enum IconType { SPEAKER, MUTE, MUSIC_NOTE, PERSON, ARROW_LEFT }

    private void drawIconButton(Graphics2D g, Rectangle r, String label,
                                boolean hov, IconType icon) {
        RenderUtils.drawButton(g, r, "  " + label, hov, FontManager.getBold(13));
        int ix = r.x + 12;
        int iy = r.y + r.height / 2;
        drawIcon(g, icon, ix, iy, 14, Color.WHITE);
    }

    private void drawIcon(Graphics2D g, IconType type, int cx, int cy, int size, Color col) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(col);
        g2.setStroke(new BasicStroke(1.6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        switch (type) {

            case SPEAKER: {
                int bx = cx - size / 2, by = cy - size * 2 / 5;
                int bw = size * 2 / 5, bh = size * 4 / 5;
                g2.fillRect(bx, by, bw, bh);
                int[] px = {bx + bw, bx + bw + size / 2, bx + bw + size / 2, bx + bw};
                int[] py = {by, by - size / 4, by + bh + size / 4, by + bh};
                g2.fillPolygon(px, py, 4);
                g2.drawArc(bx + bw + size / 2 - 2, cy - size * 3 / 8,
                        size / 3, size * 3 / 4, -45, 90);
                g2.drawArc(bx + bw + size / 2 + 2, cy - size / 2,
                        size / 2, size, -45, 90);
                break;
            }

            case MUTE: {
                int bx = cx - size / 2, by = cy - size * 2 / 5;
                int bw = size * 2 / 5, bh = size * 4 / 5;
                g2.fillRect(bx, by, bw, bh);
                int[] px = {bx + bw, bx + bw + size / 2, bx + bw + size / 2, bx + bw};
                int[] py = {by, by - size / 4, by + bh + size / 4, by + bh};
                g2.fillPolygon(px, py, 4);
                g2.setColor(new Color(255, 80, 80));
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int rx = bx + bw + size / 2 - 2;
                g2.drawLine(rx, cy - size / 2, rx + size / 2 + 4, cy + size / 2);
                g2.drawLine(rx + size / 2 + 4, cy - size / 2, rx, cy + size / 2);
                break;
            }

            case MUSIC_NOTE: {
                g2.fillOval(cx - size / 3, cy + size / 6, size * 2 / 3, size / 2);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(cx + size / 3, cy + size / 3, cx + size / 3, cy - size / 2);
                g2.drawLine(cx + size / 3, cy - size / 2, cx + size * 2 / 3, cy - size / 4);
                break;
            }

            case PERSON: {
                g2.drawOval(cx - size / 4, cy - size / 2, size / 2, size / 2);
                g2.drawArc(cx - size / 2, cy, size, size / 2, 0, 180);
                break;
            }

            case ARROW_LEFT: {
                int ax = cx + size / 3;
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(ax, cy, cx - size / 2, cy);
                g2.drawLine(cx - size / 2, cy, cx, cy - size / 3);
                g2.drawLine(cx - size / 2, cy, cx, cy + size / 3);
                break;
            }
        }
        g2.dispose();
    }

    // ── slider helpers ────────────────────────────────────────────────────────
    private float sliderValue(int mx) {
        return Math.max(0f, Math.min(1f, (float)(mx - SLIDER_X) / SLIDER_W));
    }
    private boolean overSFXSlider(int mx, int my) {
        return mx >= SLIDER_X && mx <= SLIDER_X + SLIDER_W && my >= SFX_Y && my <= SFX_Y + 24;
    }
    private boolean overMusicSlider(int mx, int my) {
        return mx >= SLIDER_X && mx <= SLIDER_X + SLIDER_W && my >= MUSIC_Y && my <= MUSIC_Y + 24;
    }

    // ── input handlers ────────────────────────────────────────────────────────
    @Override
    public void onMouseMoved(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        hovered = -1;
        if (backBtn      != null && backBtn.contains(mx, my))      hovered = 0;
        if (muteBtn      != null && muteBtn.contains(mx, my))      hovered = 10;
        if (muteMusicBtn != null && muteMusicBtn.contains(mx, my)) hovered = 11;
        if (switchAccBtn != null && switchAccBtn.contains(mx, my)) hovered = 12;
        if (diffBtns     != null)
            for (int i = 0; i < diffBtns.length; i++)
                if (diffBtns[i].contains(mx, my)) { hovered = 20 + i; break; }

        if (draggingSFX) {
            sfxVolume = sliderValue(mx);
            panel.getSoundManager().setVolume(sfxVolume);
            panel.getPlayerData().setSfxVolume(sfxVolume);
        }
        if (draggingMusic) {
            musicVolume = sliderValue(mx);
            panel.getMusicManager().setVolume(musicVolume);
            panel.getPlayerData().setMusicVolume(musicVolume);
        }
    }

    @Override
    public void onMousePressed(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        if (overSFXSlider(mx, my))   draggingSFX   = true;
        if (overMusicSlider(mx, my)) draggingMusic = true;
    }

    @Override
    public void onMouseReleased(MouseEvent e) {
        draggingSFX   = false;
        draggingMusic = false;
        panel.getPlayerData().save();
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        if (backBtn     != null && backBtn.contains(mx, my)) {
            panel.switchToWithFade(GameScreenType.MAIN_MENU); return;
        }
        if (muteBtn     != null && muteBtn.contains(mx, my)) {
            panel.getSoundManager().toggleMute();
            panel.getPlayerData().setSfxMuted(panel.getSoundManager().isMuted()); return;
        }
        if (muteMusicBtn != null && muteMusicBtn.contains(mx, my)) {
            panel.getMusicManager().toggleMute();
            panel.getPlayerData().setMusicMuted(panel.getMusicManager().isMuted()); return;
        }
        if (diffBtns != null)
            for (int i = 0; i < diffBtns.length; i++)
                if (diffBtns[i].contains(mx, my)) {
                    selectedDiff = i;
                    panel.getPlayerData().setDefaultDifficulty(Difficulty.values()[i]); return;
                }
        if (switchAccBtn != null && switchAccBtn.contains(mx, my)) {
            panel.switchToWithFade(GameScreenType.ACCOUNT_SELECT); return;
        }
        if (overSFXSlider(mx, my)) {
            sfxVolume = sliderValue(mx);
            panel.getSoundManager().setVolume(sfxVolume);
        }
        if (overMusicSlider(mx, my)) {
            musicVolume = sliderValue(mx);
            panel.getMusicManager().setVolume(musicVolume);
        }
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            panel.switchToWithFade(GameScreenType.MAIN_MENU);
    }
}