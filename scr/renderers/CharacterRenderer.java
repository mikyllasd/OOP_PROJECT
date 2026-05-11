package renderers;
import entities.Character;
import utils.FontManager;
import java.awt.*;
import java.awt.geom.AffineTransform;
public class CharacterRenderer {
    public static void draw(Graphics2D g, Character c) {
        float drawY = c.getY() + c.getBobOffset();
        if (c.getAnimState() == Character.ANIM_CATCH) drawY -= 8;
        else if (c.getAnimState() == Character.ANIM_SHAKE)
            drawY += (float)(Math.sin(c.getBobTimer() * 0.8) * 4);

        if (c.getLeanAngle() != 0) {
            AffineTransform old = g.getTransform();
            g.rotate(Math.toRadians(c.getLeanAngle()),
                    c.getX() + c.getWidth() / 2f,
                    drawY + c.getHeight());
            drawEmoji(g, c, drawY);
            drawNameLabel(g, c, drawY);
            g.setTransform(old);
        } else {
            drawEmoji(g, c, drawY);
            drawNameLabel(g, c, drawY);
        }
    }
    private static void drawEmoji(Graphics2D g, Character c, float drawY) {
        g.setFont(FontManager.getEmoji(42));
        FontMetrics fm = g.getFontMetrics();
        String emoji   = getEmoji(c.getSkin());
        int tw         = fm.stringWidth(emoji);
        g.setColor(Color.WHITE);
        g.drawString(emoji,
                (int)(c.getX() + (c.getWidth() - tw) / 2f),
                (int)(drawY + c.getHeight() - 2));
    }
    private static void drawNameLabel(Graphics2D g, Character c, float drawY) {
        g.setFont(FontManager.getBodyBold(11));
        FontMetrics fm = g.getFontMetrics();
        int nw = fm.stringWidth(c.getFarmerName());
        int nx = (int)(c.getX() + (c.getWidth() - nw) / 2f);
        g.setColor(new Color(255, 255, 255, 200));
        g.fillRoundRect(nx - 3, (int)(drawY - 18), nw + 6, 16, 6, 6);
        g.setColor(new Color(60, 40, 20));
        g.drawString(c.getFarmerName(), nx, (int)(drawY - 5));
    }
    private static String getEmoji(enums.SkinType skin) {
        switch (skin) {
            case FARMER_MALE:   return "\uD83D\uDC68\u200D\uD83C\uDF3E";
            case FARMER_FEMALE: return "\uD83D\uDC69\u200D\uD83C\uDF3E";
            case FARM_KID:      return "\uD83E\uDDD1\u200D\uD83C\uDF3E";
            case COWBOY:        return "\uD83E\uDD20";
            case WIZARD:        return "\uD83E\uDDD9";
            case NINJA:         return "\uD83E\uDD77";
            case ROYAL:         return "\uD83D\uDC51";
            default:            return "\uD83D\uDC68\u200D\uD83C\uDF3E";
        }
    }
    private CharacterRenderer() {}
}