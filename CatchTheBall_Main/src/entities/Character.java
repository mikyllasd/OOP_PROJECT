package OOP_PROJECT.CatchTheBall.src.entities;

import OOP_PROJECT.CatchTheBall.src.core.Entity;
import OOP_PROJECT.CatchTheBall.src.enums.SkinType;
import OOP_PROJECT.CatchTheBall.src.renderers.CharacterRenderer;

import java.awt.Graphics2D;

public class Character extends Entity {
    private SkinType skin;
    private String   farmerName;
    private int      animState;
    private int      animTimer;
    private float    bobOffset;
    private int      bobTimer;
    private float    leanAngle;

    public static final int ANIM_IDLE      = 0;
    public static final int ANIM_CATCH     = 1;
    public static final int ANIM_SHAKE     = 2;
    public static final int ANIM_CELEBRATE = 3;
    public static final int ANIM_SAD       = 4;

    public Character(float x, float y, SkinType skin, String farmerName) {
        super(x, y, 50, 60);
        this.skin       = skin;
        this.farmerName = farmerName;
        this.animState  = ANIM_IDLE;
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
    public void draw(Graphics2D g) { CharacterRenderer.draw(g, this); }

    public void triggerCatch()     { animState = ANIM_CATCH;     animTimer = 15; }
    public void triggerShake()     { animState = ANIM_SHAKE;     animTimer = 20; }
    public void triggerCelebrate() { animState = ANIM_CELEBRATE; animTimer = 60; }
    public void triggerSad()       { animState = ANIM_SAD;       animTimer = 90; }
    public void setLean(float dir) { leanAngle = dir * 8f; }

    public SkinType getSkin()               { return skin; }
    public void     setSkin(SkinType s)     { skin = s; }
    public String   getFarmerName()         { return farmerName; }
    public void     setFarmerName(String n) { farmerName = n; }
    public int      getAnimState()          { return animState; }
    public int      getAnimTimer()          { return animTimer; }
    public float    getBobOffset()          { return bobOffset; }
    public int      getBobTimer()           { return bobTimer; }
    public float    getLeanAngle()          { return leanAngle; }
}