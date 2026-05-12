package game.entities;

import java.awt.*;
import java.awt.geom.*;

public class Character extends Entity {

    private SkinType skin;
    private String   name;
    private int      animState;   // 0=idle, 1=catch, 2=shake
    private int      animTimer;
    private float    bobOffset;
    private int      bobTimer;
    private float    targetX, targetY;

    // Effects
    private float catchGlow;
    private float squishX, squishY;
    private float shadowAlpha;
    private int   catchParticleTimer;
    private float shakeIntensity;

    public Character(float x, float y, SkinType skin, String name) {
        super(x, y, 56, 80);
        this.skin        = skin;
        this.name        = name;
        this.animState   = 0;
        this.bobTimer    = 0;
        this.animTimer   = 0;
        this.targetX     = x;
        this.targetY     = y;
        this.squishX     = 1f;
        this.squishY     = 1f;
        this.shadowAlpha = 0.25f;
    }

    // â”€â”€ Entity override â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Override
    public void setTarget(float tx, float ty) {
        this.targetX = tx;
        this.targetY = ty;
    }

    // â”€â”€ Update â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Override
    public void update() {
        x += (targetX - x) * 0.28f;
        y += (targetY - y) * 0.28f;

        bobTimer++;
        bobOffset = (float)(Math.sin(bobTimer * 0.05) * 2.5);

        if (animState != 0) {
            animTimer--;
            if (animTimer <= 0) {
                animState      = 0;
                shakeIntensity = 0f;
            }
        }

        if (animState == 1) {
            float t = animTimer / 15f;
            if (t > 0.5f) {
                squishX = 0.85f + 0.15f * (1f - t) * 2f;
                squishY = 1.0f  + 0.18f * (1f - t) * 2f;
            } else {
                squishX = 1.1f  + 0.05f * t * 2f;
                squishY = 0.9f  - 0.05f * t * 2f;
            }
            catchGlow          = t;
            catchParticleTimer = animTimer;
        } else if (animState == 2) {
            shakeIntensity = animTimer / 20f;
            squishX        = 1f;
            squishY        = 1f;
            catchGlow      = 0f;
        } else {
            squishX += (1f - squishX) * 0.18f;
            squishY += (1f - squishY) * 0.18f;
            catchGlow *= 0.85f;
            catchParticleTimer = 0;
        }
    }

    // â”€â”€ Draw â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    @Override
    public void draw(Graphics2D g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_OFF);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,     RenderingHints.VALUE_RENDER_SPEED);

        float drawY = y + bobOffset;

        float jumpOffset = 0f;
        if (animState == 1) {
            float t = animTimer / 15f;
            jumpOffset = -(float)(Math.sin(t * Math.PI) * 16);
        }

        float shakeX = 0f;
        if (animState == 2)
            shakeX = (float)(Math.sin(bobTimer * 0.9) * 5 * shakeIntensity);

        float cx = x + width / 2f + shakeX;
        float cy = drawY + jumpOffset;

        drawShadow(g2, cx, y + height, jumpOffset);
        if (catchGlow > 0.02f)       drawCatchGlow(g2, cx, cy, catchGlow);
        if (animState == 1 && catchParticleTimer > 0)
            drawSparkles(g2, cx, cy, catchParticleTimer / 15f);

        // squish/stretch pivot at feet
        g2.translate(cx, cy + height);
        g2.scale(squishX, squishY);
        g2.translate(-cx, -(cy + height));

        drawPixelBody(g2, (int)cx, (int)(cy + height), skin);

        g2.translate(cx, cy + height);
        g2.scale(1f / squishX, 1f / squishY);
        g2.translate(-cx, -(cy + height));

        drawNameLabel(g2, cx, cy);
        g2.dispose();
    }

    // â”€â”€ Pixel-art body â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private void drawPixelBody(Graphics2D g2, int ox, int oy, SkinType skin) {
        SkinColors sc = SkinColors.from(skin);

        float legSwing = (float)(Math.sin(bobTimer * 0.1) * 2.5);
        int   ls       = Math.round(legSwing);

        // BOOTS
        px(g2, sc.shoeDark, ox - 10, oy -  6,  8, 6);
        px(g2, sc.shoes,    ox - 10, oy - 10,  7, 5);
        px(g2, sc.shoeHi,   ox -  9, oy - 10,  3, 2);
        px(g2, sc.shoeDark, ox +  3, oy -  6,  8, 6);
        px(g2, sc.shoes,    ox +  3, oy - 10,  7, 5);
        px(g2, sc.shoeHi,   ox +  4, oy - 10,  3, 2);

        // LEGS
        px(g2, sc.pantsDark, ox - 9,  oy - 24 + ls, 7, 14);
        px(g2, sc.pants,     ox - 8,  oy - 24 + ls, 4, 14);
        px(g2, sc.pantsHi,   ox - 7,  oy - 24 + ls, 2,  7);
        px(g2, sc.pantsDark, ox + 3,  oy - 24 - ls, 7, 14);
        px(g2, sc.pants,     ox + 3,  oy - 24 - ls, 4, 14);
        px(g2, sc.pantsHi,   ox + 4,  oy - 24 - ls, 2,  7);

        // TORSO
        px(g2, sc.shirtDark, ox - 13, oy - 44, 26, 22);
        px(g2, sc.shirt,     ox - 12, oy - 43, 24, 20);
        px(g2, sc.shirtHi,   ox - 11, oy - 43, 10, 10);
        px(g2, sc.shirtDark, ox -  3, oy - 38,  8,  7);
        px(g2, sc.shirt,     ox -  2, oy - 37,  6,  5);
        px(g2, sc.shirtDark, ox -  4, oy - 44,  8,  4);
        px(g2, sc.collar,    ox -  3, oy - 44,  6,  3);

        // ARMS
        int armSwing = ls;
        px(g2, sc.shirtDark, ox - 22, oy - 43 - armSwing, 9, 18);
        px(g2, sc.shirt,     ox - 21, oy - 43 - armSwing, 7, 17);
        px(g2, sc.shirtHi,   ox - 20, oy - 43 - armSwing, 3,  8);
        px(g2, sc.shirtDark, ox + 13, oy - 43 + armSwing, 9, 18);
        px(g2, sc.shirt,     ox + 14, oy - 43 + armSwing, 7, 17);
        px(g2, sc.shirtHi,   ox + 15, oy - 43 + armSwing, 3,  8);

        // HANDS
        px(g2, sc.skinDark, ox - 21, oy - 27 - armSwing, 8, 8);
        px(g2, sc.skin,     ox - 20, oy - 27 - armSwing, 6, 7);
        px(g2, sc.skinHi,   ox - 19, oy - 27 - armSwing, 2, 2);
        px(g2, sc.skinDark, ox + 14, oy - 27 + armSwing, 8, 8);
        px(g2, sc.skin,     ox + 14, oy - 27 + armSwing, 6, 7);
        px(g2, sc.skinHi,   ox + 15, oy - 27 + armSwing, 2, 2);

        // NECK
        px(g2, sc.skinDark, ox - 4, oy - 50, 8, 7);
        px(g2, sc.skin,     ox - 3, oy - 50, 6, 6);

        // HEAD
        px(g2, sc.skinDark, ox - 14, oy - 72, 28, 24);
        px(g2, sc.skin,     ox - 13, oy - 71, 26, 22);
        px(g2, sc.skinHi,   ox - 12, oy - 71, 10,  9);
        px(g2, sc.skinSh,   ox +  6, oy - 55,  7,  7);

        // Blush
        int   blushA = animState == 1 ? 110 : 55;
        Color blush  = new Color(220, 110, 90, blushA);
        px(g2, blush, ox - 12, oy - 57, 5, 3);
        px(g2, blush, ox +  7, oy - 57, 5, 3);

        // EYES
        px(g2, sc.eyeSocket, ox - 10, oy - 67, 8, 7);
        px(g2, sc.eyeSocket, ox +  3, oy - 67, 8, 7);
        px(g2, Color.WHITE,  ox -  9, oy - 66, 6, 5);
        px(g2, Color.WHITE,  ox +  4, oy - 66, 6, 5);
        int pShift = animState == 2
                ? Math.round((float)(Math.sin(bobTimer * 0.9) * 2 * shakeIntensity)) : 0;
        px(g2, sc.pupil, ox -  8 + pShift, oy - 65, 4, 4);
        px(g2, sc.pupil, ox +  5 + pShift, oy - 65, 4, 4);
        px(g2, Color.WHITE, ox - 7 + pShift, oy - 65, 1, 1);
        px(g2, Color.WHITE, ox + 6 + pShift, oy - 65, 1, 1);

        // EYEBROWS
        px(g2, sc.hair, ox - 10, oy - 69, 7, 2);
        px(g2, sc.hair, ox +  4, oy - 69, 7, 2);

        // MOUTH
        if (animState == 1) {
            px(g2, sc.mouthDark,             ox - 4, oy - 57, 8, 7);
            px(g2, new Color(200, 50, 50),   ox - 3, oy - 56, 6, 5);
            px(g2, Color.WHITE,              ox - 2, oy - 55, 5, 2);
            px(g2, new Color(240, 100, 80, 160), ox - 2, oy - 55, 3, 2);
        } else if (animState == 2) {
            px(g2, sc.mouthDark, ox - 5, oy - 56, 4, 2);
            px(g2, sc.mouthDark, ox - 1, oy - 58, 4, 2);
            px(g2, sc.mouthDark, ox + 3, oy - 56, 4, 2);
        } else {
            px(g2, sc.mouthDark, ox - 5, oy - 57, 3, 2);
            px(g2, sc.mouthDark, ox - 2, oy - 55, 4, 2);
            px(g2, sc.mouthDark, ox + 2, oy - 57, 3, 2);
        }

        // HAIR
        px(g2, sc.hairDark, ox - 14, oy - 72, 28,  9);
        px(g2, sc.hair,     ox - 13, oy - 72, 26,  8);
        px(g2, sc.hairHi,   ox -  8, oy - 72,  8,  3);
        px(g2, sc.hair,     ox - 14, oy - 64,  4, 10);
        px(g2, sc.hair,     ox + 10, oy - 64,  4, 10);

        // HAT
        drawPixelHat(g2, ox, oy, sc, skin);

        // OUTLINE
        drawOutline(g2, ox, oy);
    }

    // â”€â”€ Per-skin hats â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private void drawPixelHat(Graphics2D g2, int ox, int oy, SkinColors sc, SkinType skin) {
        switch (skin) {
            case FARMER_MALE: case FARMER_FEMALE: case FARM_KID:
                px(g2, sc.hatDark, ox - 18, oy - 76, 36,  5);
                px(g2, sc.hat,     ox - 17, oy - 75, 34,  4);
                px(g2, sc.hatHi,   ox - 15, oy - 75, 12,  2);
                px(g2, sc.hatDark, ox - 11, oy - 90, 22, 15);
                px(g2, sc.hat,     ox - 10, oy - 89, 20, 14);
                px(g2, sc.hatHi,   ox -  9, oy - 89,  8,  5);
                px(g2, sc.hatBand, ox - 10, oy - 77, 20,  3);
                px(g2, sc.hatDark, ox -  4, oy - 89,  2, 12);
                px(g2, sc.hatDark, ox +  3, oy - 89,  2, 12);
                break;
            case COWBOY:
                px(g2, sc.hatDark, ox - 20, oy - 76, 40,  5);
                px(g2, sc.hat,     ox - 19, oy - 75, 38,  4);
                px(g2, sc.hatHi,   ox - 17, oy - 75, 12,  2);
                px(g2, sc.hatDark, ox - 12, oy - 91, 24, 16);
                px(g2, sc.hat,     ox - 11, oy - 90, 22, 15);
                px(g2, sc.hatHi,   ox - 10, oy - 90,  8,  5);
                px(g2, sc.hatDark, ox -  5, oy - 92,  4,  3);
                px(g2, sc.hatDark, ox +  2, oy - 92,  4,  3);
                px(g2, sc.hatBand, ox - 11, oy - 77, 22,  3);
                px(g2, new Color(220, 200, 60), ox - 2, oy - 77, 4, 3);
                break;
            case WIZARD:
                px(g2, sc.hatDark, ox - 16, oy - 76, 32,  5);
                px(g2, sc.hat,     ox - 15, oy - 75, 30,  4);
                px(g2, sc.hatDark, ox - 12, oy - 88, 24, 13);
                px(g2, sc.hat,     ox - 10, oy - 90, 20, 15);
                px(g2, sc.hatDark, ox -  8, oy -102, 16, 13);
                px(g2, sc.hat,     ox -  7, oy -103, 14, 14);
                px(g2, sc.hatDark, ox -  4, oy -114,  8, 12);
                px(g2, sc.hat,     ox -  3, oy -113,  6, 13);
                px(g2, sc.hatDark, ox -  1, oy -122,  2,  9);
                px(g2, sc.hatBand, ox -  2, oy -100,  4,  4);
                px(g2, sc.hatBand, ox -  3, oy - 98,  6,  2);
                px(g2, sc.hatBand, ox -  1, oy -102,  2,  6);
                px(g2, sc.hatBand, ox - 10, oy - 77, 20,  3);
                px(g2, sc.hatHi,   ox -  9, oy - 90,  4,  6);
                break;
            case NINJA:
                px(g2, sc.hatDark, ox - 15, oy - 73, 30, 16);
                px(g2, sc.hat,     ox - 14, oy - 72, 28, 15);
                px(g2, sc.skin,    ox - 11, oy - 65, 22, 10);
                px(g2, sc.hatBand, ox - 14, oy - 68, 28,  4);
                px(g2, sc.hatBand, ox - 15, oy - 72,  6,  8);
                px(g2, sc.hatBand, ox + 10, oy - 72,  5,  8);
                px(g2, new Color(150, 20, 20), ox - 18, oy - 69, 4, 5);
                px(g2, new Color(120, 15, 15), ox - 19, oy - 68, 3, 3);
                break;
            case BEEKEEPER:
                px(g2, sc.hatDark, ox - 16, oy - 78, 32, 16);
                px(g2, sc.hat,     ox - 15, oy - 77, 30, 15);
                px(g2, sc.hatHi,   ox - 13, oy - 77, 10,  7);
                for (int vx = ox - 13; vx < ox + 15; vx += 4)
                    for (int vy = oy - 74; vy < oy - 65; vy += 4)
                        px(g2, sc.hatDark, vx, vy, 2, 2);
                px(g2, sc.hatBand, ox - 15, oy - 77, 30,  4);
                px(g2, sc.hatDark, ox - 18, oy - 62, 36,  4);
                px(g2, sc.hat,     ox - 17, oy - 61, 34,  3);
                break;
            case FLOWER_FARMER:
                px(g2, sc.hat,   ox - 14, oy - 75, 28, 5);
                px(g2, sc.hatHi, ox - 12, oy - 75, 12, 3);
                int[]   fx = {ox - 11, ox - 1, ox + 9};
                int[]   fy = {oy - 80, oy - 83, oy - 80};
                Color[] fc = {new Color(255,100,150), new Color(255,220,50), new Color(180,100,240)};
                for (int fi = 0; fi < 3; fi++) {
                    px(g2, fc[fi], fx[fi]-3, fy[fi],   3, 3);
                    px(g2, fc[fi], fx[fi]+3, fy[fi],   3, 3);
                    px(g2, fc[fi], fx[fi],   fy[fi]-3, 3, 3);
                    px(g2, fc[fi], fx[fi],   fy[fi]+3, 3, 3);
                    px(g2, new Color(255,240,100), fx[fi], fy[fi], 3, 3);
                }
                px(g2, new Color(60,160,60), ox-5, oy-77, 3, 4);
                px(g2, new Color(60,160,60), ox+3, oy-77, 3, 4);
                break;
            case TRACTOR_DRIVER:
                px(g2, sc.hatDark, ox - 15, oy - 80, 30, 16);
                px(g2, sc.hat,     ox - 14, oy - 79, 28, 15);
                px(g2, sc.hatHi,   ox - 12, oy - 79, 10,  6);
                px(g2, sc.hatDark, ox - 18, oy - 65, 36,  4);
                px(g2, sc.hat,     ox - 17, oy - 64, 34,  3);
                px(g2, sc.hatBand, ox - 14, oy - 72, 28,  4);
                px(g2, sc.hatDark, ox - 11, oy - 71,  3,  3);
                px(g2, sc.hatDark, ox +  9, oy - 71,  3,  3);
                break;
            case ROYAL:
                px(g2, sc.hatDark, ox - 14, oy - 75, 28, 6);
                px(g2, sc.hat,     ox - 13, oy - 74, 26, 5);
                px(g2, sc.hatHi,   ox - 12, oy - 74,  9, 3);
                int[] cpx = {ox-12, ox-6, ox, ox+6, ox+12};
                for (int cp = 0; cp < 5; cp++) {
                    int pw2 = (cp % 2 == 0) ? 5 : 4;
                    int ph2 = (cp % 2 == 0) ? 10 : 7;
                    px(g2, sc.hatDark, cpx[cp]-1, oy-75-ph2, pw2+2, ph2);
                    px(g2, sc.hat,     cpx[cp],   oy-75-ph2, pw2,   ph2);
                }
                px(g2, new Color(220, 30,  60), ox-10, oy-75, 4, 4);
                px(g2, new Color(50, 100, 220), ox- 2, oy-75, 4, 4);
                px(g2, new Color(50, 200, 100), ox+ 7, oy-75, 4, 4);
                px(g2, new Color(255,250,150),  ox-12, oy-74, 4, 2);
                break;
        }
    }

    // â”€â”€ Outline â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private void drawOutline(Graphics2D g2, int ox, int oy) {
        g2.setColor(new Color(30, 20, 10, 190));
        g2.setStroke(new BasicStroke(1f));
        g2.drawRect(ox-14, oy-72, 27, 23);
        g2.drawRect(ox-13, oy-44, 25, 21);
        g2.drawRect(ox-22, oy-43,  8, 17);
        g2.drawRect(ox+13, oy-43,  8, 17);
        g2.drawRect(ox- 9, oy-23,  6, 13);
        g2.drawRect(ox+ 3, oy-23,  6, 13);
    }

    // â”€â”€ Effects â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private void drawShadow(Graphics2D g2, float cx, float groundY, float jumpOffset) {
        float scale = 1f - Math.abs(jumpOffset) / 60f;
        int sw    = (int)(34 * scale);
        int sh    = (int)( 8 * scale);
        int alpha = (int)(shadowAlpha * scale * 255);
        g2.setColor(new Color(0, 0, 0, Math.max(0, alpha)));
        g2.fillOval((int)(cx - sw/2f), (int)(groundY - sh/2f), sw, sh);
    }

    private void drawCatchGlow(Graphics2D g2, float cx, float cy, float intensity) {
        int   r     = (int)(42 + (1f - intensity) * 12);
        int   alpha = (int)(intensity * 130);
        g2.setColor(new Color(255, 240, 60, Math.max(0, alpha)));
        g2.fillOval((int)(cx - r), (int)(cy + height/2f - r), r*2, r*2);
    }

    private void drawSparkles(Graphics2D g2, float cx, float cy, float t) {
        g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        int   count  = 7;
        float radius = 22 + (1f - t) * 20f;
        int   alpha  = (int)(t * 230);
        for (int i = 0; i < count; i++) {
            double angle = i * Math.PI * 2 / count + bobTimer * 0.04;
            int sx = (int)(cx + Math.cos(angle) * radius);
            int sy = (int)(cy + height/2f + Math.sin(angle) * radius * 0.55f);
            int ex = (int)(cx + Math.cos(angle) * (radius + 6 + t*5));
            int ey = (int)(cy + height/2f + Math.sin(angle) * (radius + 6 + t*5) * 0.55f);
            g2.setColor(new Color(255, 240, 60, Math.max(0, alpha)));
            g2.drawLine(sx, sy, ex, ey);
            g2.setColor(new Color(255, 255, 180, Math.max(0, alpha)));
            g2.fillRect(sx-2, sy-2, 4, 4);
        }
    }

    // â”€â”€ Name label â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private void drawNameLabel(Graphics2D g2, float cx, float cy) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        FontMetrics fm = g2.getFontMetrics();
        int nw     = fm.stringWidth(name);
        int nx     = (int)(cx - nw / 2f);
        int labelY = (int)(cy - 8);

        g2.setColor(new Color(255, 255, 255, 215));
        g2.fill(new RoundRectangle2D.Float(nx-5, labelY - fm.getAscent() - 2,
                nw+10, fm.getHeight() + 2, 8, 8));
        g2.setColor(new Color(160, 120, 60, 160));
        g2.setStroke(new BasicStroke(1f));
        g2.draw(new RoundRectangle2D.Float(nx-5, labelY - fm.getAscent() - 2,
                nw+10, fm.getHeight() + 2, 8, 8));
        g2.setColor(new Color(50, 30, 10));
        g2.drawString(name, nx, labelY);
    }

    // â”€â”€ Utility â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    private void px(Graphics2D g2, Color c, int x, int y, int w, int h) {
        g2.setColor(c);
        g2.fillRect(x, y, w, h);
    }

    // â”€â”€ Public API â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    public void     triggerCatch()           { animState = 1; animTimer = 15; }
    public void     triggerShake()           { animState = 2; animTimer = 20; }
    public SkinType getSkin()                { return skin; }
    public void     setSkin(SkinType skin)   { this.skin = skin; }
    public String   getFarmerName()          { return name; }
    public void     setFarmerName(String n)  { this.name = n; }

    // â”€â”€ Colour palettes â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€â”€
    static class SkinColors {
        Color skin, skinHi, skinDark, skinSh;
        Color hair, hairHi, hairDark;
        Color shirt, shirtHi, shirtDark, collar;
        Color pants, pantsHi, pantsDark;
        Color shoes, shoeHi, shoeDark;
        Color hat, hatHi, hatDark, hatBand;
        Color pupil, eyeSocket, mouthDark;

        static SkinColors from(SkinType type) {
            SkinColors s   = new SkinColors();
            s.eyeSocket    = new Color(30, 18, 8);
            s.mouthDark    = new Color(140, 40, 40);

            switch (type) {
                case FARMER_MALE:
                    skin (s,0xEBC39B,0xF5DFC0,0xB8885A,0x9A6E42);
                    hair (s,0x5A3214,0x7A4820,0x3A1E08);
                    shirt(s,0x4CAA3C,0x70CC55,0x2E7A22,0x1E5518);
                    pants(s,0x4A5FA0,0x6070C0,0x303D80);
                    shoes(s,0x5A3A1A,0x7A5530,0x3A200A);
                    hat  (s,0xC8A830,0xE8CA60,0x907018,0x7A4A10);
                    s.pupil = new Color(40, 25, 10); break;
                case FARMER_FEMALE:
                    skin (s,0xF0C8A0,0xFFE4C8,0xC09070,0xA07050);
                    hair (s,0xB46E32,0xD0904A,0x804818);
                    shirt(s,0xE06080,0xFF8CB0,0xB03060,0x902050);
                    pants(s,0x826042,0xA08060,0x5A3A22);
                    shoes(s,0x643C20,0x886040,0x3C2010);
                    hat  (s,0xC8A830,0xE8CA60,0x907018,0xDC6482);
                    s.pupil = new Color(80, 40, 15); break;
                case FARM_KID:
                    skin (s,0xF5D2AA,0xFFEDD0,0xC8986A,0xAA7848);
                    hair (s,0xC8A03C,0xE8C060,0x906018);
                    shirt(s,0xE8C820,0xFFE840,0xB09810,0x887200);
                    pants(s,0x5070C0,0x7090E0,0x3050A0);
                    shoes(s,0x503220,0x7A5238,0x301808);
                    hat  (s,0xBE9E28,0xDEC050,0x887015,0x9A6420);
                    s.pupil = new Color(50, 30, 10); break;
                case COWBOY:
                    skin (s,0xDCAF82,0xF0CCA0,0xAA7844,0x8A5828);
                    hair (s,0x3C230F,0x5A3820,0x200C00);
                    shirt(s,0xC8503C,0xE87060,0x982820,0x701810);
                    pants(s,0xAA8C55,0xCCB070,0x786030);
                    shoes(s,0x46280E,0x6A4020,0x281000);
                    hat  (s,0x6E4620,0x9A6A38,0x402800,0x3C1C08);
                    s.pupil = new Color(35, 20, 8); break;
                case WIZARD:
                    skin (s,0xD2AF91,0xEED0B4,0xA07C5A,0x80583A);
                    hair (s,0xDCDCE8,0xF5F5FF,0xA0A0B8);
                    shirt(s,0x8C5AC8,0xB080F0,0x5A2A9A,0x3A1870);
                    pants(s,0x4B2882,0x6A3CB0,0x2A1060);
                    shoes(s,0x321E50,0x503070,0x180A30);
                    hat  (s,0x6437AF,0x9060DC,0x3A1880,0xFFD23C);
                    s.pupil = new Color(100, 20, 160); break;
                case NINJA:
                    skin (s,0xC8A078,0xE0C09A,0x90684A,0x704830);
                    hair (s,0x0F0A0A,0x281E1E,0x040202);
                    shirt(s,0x37373C,0x555560,0x191920,0x0F0F14);
                    pants(s,0x1E1E22,0x303038,0x0A0A10);
                    shoes(s,0x141416,0x252528,0x060608);
                    hat  (s,0x1E1E22,0x37373C,0x0A0A10,0xB41E1E);
                    s.pupil = new Color(200, 20, 20); break;
                case BEEKEEPER:
                    skin (s,0xEBC8A0,0xFFE4C0,0xB89060,0x987040);
                    hair (s,0x644014,0x8A6030,0x3A2008);
                    shirt(s,0xF5F5E0,0xFFFFFF,0xD2D2C0,0xB8B8A4);
                    pants(s,0xE6E6D0,0xFAFAEC,0xC4C4A8);
                    shoes(s,0x786030,0xA08850,0x4A3A10);
                    hat  (s,0xF0EBC8,0xFFFFF0,0xC8C4A0,0xE6BE1E);
                    s.pupil = new Color(50, 30, 10); break;
                case FLOWER_FARMER:
                    skin (s,0xF5D2AA,0xFFEDD0,0xC8986A,0xAA7848);
                    hair (s,0xD28298,0xF0A0B8,0xA05070);
                    shirt(s,0xDCB0EC,0xF8D0FF,0xA878C8,0x8058A8);
                    pants(s,0xA0C8A0,0xC0E8C0,0x70A070);
                    shoes(s,0xB48264,0xD0A888,0x7A5040);
                    hat  (s,0xF0B4D0,0xFFD8EC,0xC078A0,0xFF6496);
                    s.pupil = new Color(80, 30, 60); break;
                case TRACTOR_DRIVER:
                    skin (s,0xE1B991,0xF5D5B5,0xAF8055,0x8F6035);
                    hair (s,0x463218,0x6A5030,0x241408);
                    shirt(s,0xFF9E28,0xFFBE50,0xC06810,0x904808);
                    pants(s,0x505A64,0x707E8A,0x303C48);
                    shoes(s,0x373228,0x524C3C,0x1C1810);
                    hat  (s,0x5A5F69,0x848D98,0x363C46,0xFFC800);
                    s.pupil = new Color(40, 28, 12); break;
                case ROYAL:
                    skin (s,0xE6BE96,0xFADBB6,0xB48860,0x946840);
                    hair (s,0x32200A,0x503418,0x180C00);
                    shirt(s,0x3C5AC8,0x6080F0,0x1C3498,0x0C1E78);
                    pants(s,0x23328C,0x3C4EC0,0x101A6A);
                    shoes(s,0xB48C1E,0xD8B040,0x7A5C08);
                    hat  (s,0xDCB414,0xFFE040,0x9A7C08,0xB41E32);
                    s.pupil = new Color(20, 50, 170); break;
                default:
                    skin (s,0xE6BE96,0xFADBB6,0xB48860,0x946840);
                    hair (s,0x503418,0x6A4C28,0x301C08);
                    shirt(s,0x96AAE0,0xB4C8FF,0x6070C0,0x4050A0);
                    pants(s,0x465A8C,0x6070B0,0x283870);
                    shoes(s,0x3C2D22,0x5A4538,0x1E140C);
                    hat  (s,0x5064AA,0x7080D0,0x303C80,0x203070);
                    s.pupil = new Color(40, 28, 12); break;
            }
            return s;
        }

        private static void skin (SkinColors s,int b,int hi,int dk,int sh){s.skin=hx(b);s.skinHi=hx(hi);s.skinDark=hx(dk);s.skinSh=hx(sh);}
        private static void hair (SkinColors s,int b,int hi,int dk)       {s.hair=hx(b);s.hairHi=hx(hi);s.hairDark=hx(dk);}
        private static void shirt(SkinColors s,int b,int hi,int dk,int col){s.shirt=hx(b);s.shirtHi=hx(hi);s.shirtDark=hx(dk);s.collar=hx(col);}
        private static void pants(SkinColors s,int b,int hi,int dk)       {s.pants=hx(b);s.pantsHi=hx(hi);s.pantsDark=hx(dk);}
        private static void shoes(SkinColors s,int b,int hi,int dk)       {s.shoes=hx(b);s.shoeHi=hx(hi);s.shoeDark=hx(dk);}
        private static void hat  (SkinColors s,int b,int hi,int dk,int band){s.hat=hx(b);s.hatHi=hx(hi);s.hatDark=hx(dk);s.hatBand=hx(band);}
        private static Color hx(int rgb){return new Color(rgb);}
    }
}
