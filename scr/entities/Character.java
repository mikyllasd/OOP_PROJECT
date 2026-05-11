package entities;
import core.Entity;
import enums.SkinType;
import renderers.CharacterRenderer;
import java.awt.*;
public class Character extends Entity {
    private SkinType skin;
    private String farmerName;
    private int animState;
    private int animTimer;
    private float bobOffset;
    private int bobTimer;
    private float leanAngle;

    public static final int ANIM_IDLE  = 0;
    public static final int ANIM_CATCH = 1;
    public static final int ANIM_SHAKE = 2;

    public Character(float x, float y, SkinType skin, String farmerName) {
        super(x, y, 50, 60);
        this.skin       = skin;
        this.farmerName = farmerName;
        this.animState  = ANIM_IDLE;
        this.animTimer  = 0;
        this.bobTimer   = 0;
        this.leanAngle  = 0f;
    }
    @Override
    public void update() {
        bobTimer++;
        bobOffset = (float)(Math.sin(bobTimer * 0.05) * 2);
        if (animState != ANIM_IDLE) {
            if (animTimer > 0) animTimer--;
            if (animTimer == 0) animState = ANIM_IDLE;
        }
        leanAngle *= 0.85f;
    }
    @Override
    public void draw(Graphics2D g) {
        CharacterRenderer.draw(g, this);
    }
    public void triggerCatch() { animState = ANIM_CATCH; animTimer = 15; }
    public void triggerShake() { animState = ANIM_SHAKE; animTimer = 20; }
    public void setLean(float direction) { leanAngle = direction * 8f; }
    public SkinType getSkin()              { return skin; }
    public void setSkin(SkinType skin)     { this.skin = skin; }
    public String getFarmerName()          { return farmerName; }
    public void setFarmerName(String name) { farmerName = name; }
    public int getAnimState()              { return animState; }
    public int getAnimTimer()              { return animTimer; }
    public float getBobOffset()            { return bobOffset; }
    public int getBobTimer()               { return bobTimer; }
    public float getLeanAngle()            { return leanAngle; }
}