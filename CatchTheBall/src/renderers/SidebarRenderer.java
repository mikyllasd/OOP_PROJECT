package OOP_PROJECT.CatchTheBall.src.renderers;

import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;
import OOP_PROJECT.CatchTheBall.src.managers.PlayerData;
import OOP_PROJECT.CatchTheBall.src.audio.SoundManager;
import OOP_PROJECT.CatchTheBall.src.models.FarmProgression;
import OOP_PROJECT.CatchTheBall.src.models.GameState;
import OOP_PROJECT.CatchTheBall.src.utils.*;

import java.awt.*;

public class SidebarRenderer {

    public static void draw(Graphics2D g, GameState state, FarmProgression farm,
                            PlayerData playerData, SoundManager sound, int tickCount) {
        int sx = GamePanel.ARENA_W;
        int sw = GamePanel.SIDEBAR_W;
        int h  = GamePanel.H;

        g.setPaint(new GradientPaint(sx, 0, new Color(38,68,28), sx+sw, 0, new Color(28,52,18)));
        g.fillRect(sx, 0, sw, h); g.setPaint(null);
        g.setColor(new Color(100,170,70)); g.setStroke(new BasicStroke(2f));
        g.drawLine(sx, 0, sx, h); g.setStroke(new BasicStroke(1f));

        Font titleF = FontManager.getBold(13);
        Font valF   = FontManager.getBodyBold(22);

        int py = 20;
        drawItem(g, sx+12, py, "LEVEL",  ""+state.getLevel(),
                new Color(255,228,80), titleF, valF); py += 58;
        drawItem(g, sx+12, py, "SCORE",  ""+state.getScore(),
                new Color(100,228,255), titleF, valF); py += 58;
        drawItem(g, sx+12, py, "TARGET", ""+state.getLevelTarget(),
                new Color(200,200,200), titleF, FontManager.getBodyBold(16)); py += 48;

       boolean pulse = state.getTimeLeft() <= 60 && tickCount % 30 < 15;
Color tc = state.getTimeLeft() <= 60 ? new Color(255,80,80) : new Color(100,255,150);
        if (pulse) { g.setColor(new Color(255,50,50,55)); g.fillRoundRect(sx+5, py-5, sw-10, 55, 10, 10); }
        String mins = ""+state.getTimeLeft()/60;
        String secs = String.format("%02d", state.getTimeLeft()%60);
        drawItem(g, sx+12, py, "TIME", mins+":"+secs, tc, titleF, valF); py += 62;

        drawItem(g, sx+12, py, "COINS \uD83E\uDE99", ""+playerData.getTotalCoins(),
                new Color(255,215,50), titleF, FontManager.getBodyBold(18)); py += 52;

        if (state.getCombo() > 0) {
            int c = state.getCombo();
            Color cc = c>=8 ? new Color(255,100,255) : c>=5 ? new Color(255,180,50) : new Color(100,255,200);
            drawItem(g, sx+12, py, "COMBO "+c, "x"+(int)state.getComboMultiplier(), cc, titleF, valF);
        }
        py += 58;

        drawProgressSection(g, sx, py, sw, state); py += 30;
        drawActivePowerUps(g, sx, sw, py, state); py += 30;
        drawFarm(g, sx, sw, py, farm); py += 66;

        drawIconButton(g, sx+15, py, sound.isMuted() ? "\uD83D\uDD07" : "\uD83D\uDD0A", "M", 60, 32);
        drawIconButton(g, sx+85, py, "\u23F8", "P", 60, 32);
    }

    private static void drawItem(Graphics2D g, int x, int y, String label, String value,
                                  Color vc, Font lf, Font vf) {
        g.setFont(lf); g.setColor(new Color(155,190,135)); g.drawString(label, x, y+13);
        g.setFont(vf); g.setColor(vc);                     g.drawString(value, x, y+38);
    }

    private static void drawProgressSection(Graphics2D g, int sx, int py, int sw, GameState state) {
        g.setFont(FontManager.getBody(11));
        g.setColor(new Color(160,200,140));
        g.drawString("Level Progress", sx+10, py-3);
        float prog = Math.min(1f, (float)state.getScore() / state.getLevelTarget());
        RenderUtils.drawProgressBar(g, sx+10, py, sw-20, 14, prog,
                new Color(0,0,0,100), new Color(80,220,80), new Color(160,255,80));
    }

    private static void drawActivePowerUps(Graphics2D g, int sx, int sw, int py, GameState state) {
        int px = sx + 8;
        if (state.isMagnetActive()) {
            drawPowerTag(g, px, py, "\uD83E\uDDF2", state.getMagnetTimer(), new Color(255,100,100));
            px += 58;
        }
        if (state.isDoublePointsActive()) {
            drawPowerTag(g, px, py, "2x", state.getDoubleTimer(), new Color(255,220,50));
            px += 58;
        }
        if (state.isSlowTimeActive()) {
            drawPowerTag(g, px, py, "\uD83D\uDD5B", state.getSlowTimer(), new Color(180,100,255));
            px += 58;
        }
        if (state.isWideBasketActive()) {
            drawPowerTag(g, px, py, "\u2194\uFE0F", state.getWideTimer(), new Color(100,255,220));
        }
    }

    private static void drawPowerTag(Graphics2D g, int x, int y, String icon, int timer, Color c) {
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 80));
        g.fillRoundRect(x, y, 50, 22, 8, 8);
        g.setFont(FontManager.getEmoji(12));
        g.setColor(Color.WHITE);
        g.drawString(icon, x + 4, y + 15);
        float ratio = Math.min(1f, timer / 300f);
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 180));
        g.fillRoundRect(x, y + 18, (int)(50 * ratio), 4, 2, 2);
    }

    private static void drawFarm(Graphics2D g, int sx, int sw, int py, FarmProgression farm) {
        g.setFont(FontManager.getEmoji(34));
        FontMetrics fm = g.getFontMetrics();
        String emoji = farm.getEmoji();
        g.setColor(Color.WHITE);
        g.drawString(emoji, sx + (sw - fm.stringWidth(emoji)) / 2, py + 34);
        g.setFont(FontManager.getBody(11));
        fm = g.getFontMetrics();
        String name = farm.getName();
        g.setColor(new Color(180,220,150));
        g.drawString(name, sx + (sw - fm.stringWidth(name)) / 2, py + 52);
    }

    private static void drawIconButton(Graphics2D g, int x, int y, String emoji, String key, int w, int h) {
        g.setPaint(new GradientPaint(x, y, new Color(70,120,55), x, y+h, new Color(45,85,35)));
        g.fillRoundRect(x, y, w, h, 8, 8); g.setPaint(null);
        g.setColor(new Color(100,165,80)); g.drawRoundRect(x, y, w, h, 8, 8);
        g.setFont(FontManager.getEmoji(15)); g.setColor(Color.WHITE); g.drawString(emoji, x+6, y+20);
        g.setFont(FontManager.getBodyBold(10)); g.setColor(new Color(180,220,150)); g.drawString("["+key+"]", x+28, y+20);
    }

    private SidebarRenderer() {}
}