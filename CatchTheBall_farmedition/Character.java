import java.awt.*;

public class Character extends Entity {
    private SkinType skin;
    private String name;
    private int animState; // 0=idle, 1=catch, 2=shake
    private int animTimer;
    private float bobOffset;
    private int bobTimer;
    private float targetX, targetY;

    public Character(float x, float y, SkinType skin, String name) {
        super(x, y, 50, 60);
        this.skin = skin;
        this.name = name;
        this.animState = 0;
        this.bobTimer = 0;
        this.animTimer = 0;
        this.targetX = x;
        this.targetY = y;
    }

    @Override
    public void update() {
        x += (targetX - x) * 0.28f;
        y += (targetY - y) * 0.28f;

        bobTimer++;
        bobOffset = (float)(Math.sin(bobTimer * 0.05) * 2);

        if (animState != 0) {
            if (animTimer > 0) {
                animTimer--;
            }
            if (animTimer == 0) animState = 0;
        }
    }

    public void setTarget(float tx, float ty) {
        this.targetX = tx;
        this.targetY = ty;
    }

    @Override
    public void draw(Graphics2D g) {
        float drawY = y + bobOffset;

        if (animState == 1) {
            drawY -= 8; // jump on catch
        } else if (animState == 2) {
            // FIX 3: shake should use bobTimer not animTimer (animTimer counts DOWN,
            // giving a decaying/stuttering wave instead of a smooth shake)
            drawY += (float)(Math.sin(bobTimer * 0.8) * 4);
        }

        Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 42);
        g.setFont(emojiFont);
        FontMetrics fm = g.getFontMetrics();
        String emoji = skin.getEmoji();
        int tw = fm.stringWidth(emoji);
        g.drawString(emoji, (int)(x + (width - tw) / 2f), (int)(drawY + height - 2));

        // Name label
        g.setFont(new Font("Arial", Font.BOLD, 11));
        FontMetrics fm2 = g.getFontMetrics();
        int nw = fm2.stringWidth(name);
        int nx = (int)(x + (width - nw) / 2f);

        // FIX 4: draw background rect BEFORE setting text color, and use correct fm2 metrics
        g.setColor(new Color(255, 255, 255, 200));
        g.fillRoundRect(nx - 3, (int)(drawY - 18), nw + 6, 16, 6, 6);
        g.setColor(new Color(60, 40, 20));
        g.drawString(name, nx, (int)(drawY - 5));
    }

    public void triggerCatch() { animState = 1; animTimer = 15; }
    public void triggerShake() { animState = 2; animTimer = 20; }
    public SkinType getSkin() { return skin; }
    public void setSkin(SkinType skin) { this.skin = skin; }
    public String getFarmerName() { return name; }
    public void setFarmerName(String name) { this.name = name; }
}