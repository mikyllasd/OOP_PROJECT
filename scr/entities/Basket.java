package entities;
import core.Entity;
import enums.BasketSkin;
import renderers.BasketRenderer;
import java.awt.*;
public class Basket extends Entity {
    private BasketSkin skin;
    private float targetX;
    private int catchAnimTimer;
    private int shakeTimer;
    private float shakeOffset;
    private boolean isWide;
    private int originalWidth;

    public Basket(float x, float y, BasketSkin skin) {
        super(x, y, 80, 50);
        this.skin          = skin;
        this.targetX       = x;
        this.originalWidth = 80;
    }
    @Override
    public void update() {
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
        RenderingHints old = g.getRenderingHints();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        float drawX = x + shakeOffset;
        float drawY = catchAnimTimer > 0 ? y + 4 : y;
        BasketRenderer.draw(g, skin, (int) drawX, (int) drawY, width, height);
        if (catchAnimTimer > 0) {
            float alpha = catchAnimTimer / 12f;
            g.setColor(new Color(1f, 1f, 0.4f, alpha * 0.7f));
            g.setStroke(new BasicStroke(3f,
                    BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawArc((int) drawX - 4, (int) drawY - 8, width + 8, 18, 0, 180);
            g.setStroke(new BasicStroke(1f));
        }
        g.setRenderingHints(old);
    }
    public void setTargetX(float tx)     { targetX = tx - width / 2f; }
    public void triggerCatch()           { catchAnimTimer = 12; }
    public void triggerShake()           { shakeTimer = 18; }
    public BasketSkin getSkin()          { return skin; }
    public void setSkin(BasketSkin skin) { this.skin = skin; }
    public float getShakeOffset()        { return shakeOffset; }
    public boolean isWide()              { return isWide; }
    public void setWide(boolean wide) {
        isWide = wide;
        width  = wide ? (int)(originalWidth * 1.6f) : originalWidth;
    }
}