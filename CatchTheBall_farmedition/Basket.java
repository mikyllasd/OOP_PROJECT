import java.awt.*;
import java.awt.geom.*;

public class Basket extends Entity {
    private BasketSkin skin;
    private float targetX;
    private int catchAnimTimer;
    private int shakeTimer;
    private float shakeOffset;

    public Basket(float x, float y, BasketSkin skin) {
        super(x, y, 80, 50);
        this.skin = skin;
        this.targetX = x;
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
        RenderingHints oldHints = g.getRenderingHints();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        float drawX = x + shakeOffset;

        // Slight press-down when catching
        float drawY = (catchAnimTimer > 0) ? y + 4 : y;

        skin.draw(g, (int)drawX, (int)drawY, width, height);

        // Catch flash arc
        if (catchAnimTimer > 0) {
            float alpha = catchAnimTimer / 12f;
            g.setColor(new Color(1f, 1f, 0.4f, alpha * 0.7f));
            g.setStroke(new BasicStroke(3f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawArc((int)drawX - 4, (int)drawY - 8, width + 8, 18, 0, 180);
            g.setStroke(new BasicStroke(1f));
        }

        g.setRenderingHints(oldHints);
    }

    public void setTargetX(float tx) { this.targetX = tx - width / 2f; }
    public void triggerCatch()       { catchAnimTimer = 12; }
    public void triggerShake()       { shakeTimer = 18; }
    public BasketSkin getSkin()      { return skin; }
    public void setSkin(BasketSkin skin) { this.skin = skin; }
    public float getShakeOffset()    { return shakeOffset; }
}