import java.awt.*;

public class Basket extends Entity {
    private BasketSkin skin;
    private float targetX;
    private int catchAnimTimer;
    private int shakeTimer;
    private float shakeOffset;

    public Basket(float x, float y, BasketSkin skin) {
        super(x, y, 80, 40);
        this.skin = skin;
        this.targetX = x;
    }

    @Override
    public void update() {
        // Smooth movement
        x += (targetX - x) * 0.25f;

        if (catchAnimTimer > 0) catchAnimTimer--;
        if (shakeTimer > 0) {
            shakeTimer--;
            shakeOffset = (float)(Math.sin(shakeTimer * 0.8) * 6);
        } else {
            shakeOffset = 0;
        }
    }

    @Override
    public void draw(Graphics2D g) {
        float drawX = x + shakeOffset;

        // Basket body
        Color basketColor = getBasketColor();
        Color darkColor = basketColor.darker();

        // Main basket shape
        int[] xp = {(int)drawX, (int)(drawX + 10), (int)(drawX + width - 10), (int)(drawX + width)};
        int[] yp = {(int)y, (int)(y + height), (int)(y + height), (int)y};
        g.setColor(basketColor);
        g.fillPolygon(xp, yp, 4);

        // Weave lines
        g.setColor(darkColor);
        g.setStroke(new BasicStroke(1.5f));
        for (int i = 1; i < 4; i++) {
            int lineY = (int)(y + (height / 4f) * i);
            g.drawLine((int)drawX + i * 2, lineY, (int)(drawX + width) - i * 2, lineY);
        }
        for (int i = 1; i < 5; i++) {
            int lineX = (int)(drawX + (width / 5f) * i);
            g.drawLine(lineX, (int)y, lineX + 8, (int)(y + height));
        }
        g.setStroke(new BasicStroke(1f));

        // Top rim
        g.setColor(darkColor);
        g.fillRoundRect((int)drawX - 3, (int)y - 5, width + 6, 10, 6, 6);

        // Skin emoji label
        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 20);
        g.setFont(emojiFont);
        FontMetrics fm = g.getFontMetrics();
        String emoji = skin.getEmoji();
        int tw = fm.stringWidth(emoji);
        g.drawString(emoji, (int)(drawX + (width - tw) / 2f), (int)(y + height / 2f + 6));

        // Catch animation bounce
        if (catchAnimTimer > 0) {
            g.setColor(new Color(255, 255, 100, 150));
            g.setStroke(new BasicStroke(3f));
            g.drawArc((int)drawX, (int)y - 10, width, 20, 0, 180);
            g.setStroke(new BasicStroke(1f));
        }
    }

    private Color getBasketColor() {
        switch (skin) {
            case METAL: return new Color(160, 160, 175);
            case GOLDEN: return new Color(255, 200, 50);
            case CART: return new Color(100, 160, 80);
            case DIAMOND: return new Color(180, 230, 255);
            default: return new Color(180, 130, 70);
        }
    }

    public void setTargetX(float tx) {
        this.targetX = tx - width / 2f;
    }

    public void triggerCatch() { catchAnimTimer = 12; }
    public void triggerShake() { shakeTimer = 18; }
    public BasketSkin getSkin() { return skin; }
    public void setSkin(BasketSkin skin) { this.skin = skin; }
    public float getShakeOffset() { return shakeOffset; }
}
