package utils;
import java.awt.*;
public class RenderUtils {
    public static void drawRoundPanel(Graphics2D g,
            int x, int y, int w, int h,
            Color fill, Color border, float borderWidth, int arc) {
        g.setColor(fill);
        g.fillRoundRect(x, y, w, h, arc, arc);
        g.setColor(border);
        g.setStroke(new BasicStroke(borderWidth));
        g.drawRoundRect(x, y, w, h, arc, arc);
        g.setStroke(new BasicStroke(1f));
    }
    public static void drawGradientPanel(Graphics2D g,
            int x, int y, int w, int h,
            Color top, Color bottom,
            Color border, float borderWidth, int arc) {
        GradientPaint gp = new GradientPaint(x, y, top, x, y + h, bottom);
        g.setPaint(gp);
        g.fillRoundRect(x, y, w, h, arc, arc);
        g.setPaint(null);
        g.setColor(border);
        g.setStroke(new BasicStroke(borderWidth));
        g.drawRoundRect(x, y, w, h, arc, arc);
        g.setStroke(new BasicStroke(1f));
    }
    public static void drawButton(Graphics2D g,
            Rectangle r, String label, boolean hovered, Font font) {
        if (hovered) {
            g.setColor(new Color(130, 220, 80, 50));
            g.fillRoundRect(r.x - 4, r.y - 4, r.width + 8, r.height + 8, 18, 18);
        }
        Color bg     = hovered ? ColorPalette.BTN_HOVER   : ColorPalette.BTN_NORMAL;
        Color border = hovered ? ColorPalette.BTN_BORDER_HOVER : ColorPalette.BTN_BORDER_NORMAL;
        GradientPaint gp = new GradientPaint(
                r.x, r.y, bg.brighter(), r.x, r.y + r.height, bg.darker());
        g.setPaint(gp);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 14, 14);
        g.setPaint(null);
        g.setColor(new Color(255, 255, 255, 30));
        g.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height / 2 - 2, 12, 12);
        g.setColor(border);
        g.setStroke(new BasicStroke(hovered ? 2.5f : 1.8f));
        g.drawRoundRect(r.x, r.y, r.width, r.height, 14, 14);
        g.setStroke(new BasicStroke(1f));
        g.setFont(font);
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        int tx = r.x + (r.width  - fm.stringWidth(label)) / 2;
        int ty = r.y + r.height / 2 + fm.getAscent() / 2 - 2;
        g.drawString(label, tx, ty);
    }
    public static void drawCenteredText(Graphics2D g,
            String text, int cx, int y, Font font, Color color) {
        g.setFont(font);
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, cx - fm.stringWidth(text) / 2, y);
    }
    public static void drawShadowText(Graphics2D g,
            String text, int x, int y,
            Font font, Color main, Color shadow) {
        g.setFont(font);
        g.setColor(shadow);
        g.drawString(text, x + 2, y + 2);
        g.setColor(main);
        g.drawString(text, x, y);
    }
    public static void drawProgressBar(Graphics2D g,
            int x, int y, int w, int h, float progress,
            Color bg, Color fillStart, Color fillEnd) {
        g.setColor(bg);
        g.fillRoundRect(x, y, w, h, h, h);
        int fillW = (int)(w * Math.min(1f, progress));
        if (fillW > 0) {
            GradientPaint gp = new GradientPaint(
                    x, y, fillStart, x + fillW, y, fillEnd);
            g.setPaint(gp);
            g.fillRoundRect(x, y, fillW, h, h, h);
            g.setPaint(null);
        }
    }
    public static void drawHeaderBar(Graphics2D g, int w, String title) {
        GradientPaint hdr = new GradientPaint(
                0, 0, new Color(45, 100, 30, 210),
                0, 75, new Color(28, 65, 18, 210));
        g.setPaint(hdr);
        g.fillRect(0, 0, w, 75);
        g.setPaint(null);
        g.setColor(new Color(80, 160, 55, 180));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(0, 75, w, 75);
        g.setStroke(new BasicStroke(1f));
        drawCenteredText(g, title, w / 2, 50,
                FontManager.getBold(26), ColorPalette.TEXT_GOLD);
    }
    private RenderUtils() {}
}