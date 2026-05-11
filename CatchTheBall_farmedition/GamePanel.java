import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class GamePanel extends JPanel implements Runnable, KeyListener, MouseMotionListener, MouseListener {

    // Screen dimensions (responsive - updated on resize)
    private int W = 900, H = 650;
    private int ARENA_W = 650, SIDEBAR_W = 250;

    private Thread gameThread;
    private volatile boolean running;

    private GameScreen screen = GameScreen.MAIN_MENU;
    private PlayerData playerData;
    private ScoreManager scoreManager;
    private AchievementManager achievements;
    private SoundManager sound;
    private FarmProgression farm;

    private int score, level, combo, highestCombo;
    private float comboMultiplier;
    private int timeLeft;
    private int tickCount;
    private int coinsThisGame;
    private boolean shieldActive, magnetActive, doublePointsActive;
    private int magnetTimer, doubleTimer;
    private int screenShakeTimer;
    private float screenShakeX, screenShakeY;

    private Basket basket;
    private Character character;
    private List<Ball> balls;
    private List<PowerUp> powerUps;
    private List<Particle> particles;
    private List<Cloud> clouds;
    private List<RainDrop> rainDrops;

    private Random rand;
    private int spawnTimer, powerUpSpawnTimer;
    private final Object lock = new Object();

    // Animated background stars/fireflies
    private float[] fireflyX, fireflyY, fireflyPhase;
    private float[] starTwinkle;

    private String comboFlashText;
    private int comboFlashTimer;
    private String toastText;
    private int toastTimer;

    private StringBuilder nameInput = new StringBuilder();
    private int selectedStarterSkin = 0;
    private int selectedSkinIndex = 0;
    private SkinType[] editableSkins;
    private Rectangle[] skinRects;
    private Rectangle saveButton, backButton;
    private static final SkinType[] STARTER_SKINS = {SkinType.FARMER_MALE, SkinType.FARMER_FEMALE, SkinType.FARM_KID};

    private Rectangle[] menuButtons;
    private Rectangle[] gameButtons;
    private int hoveredButton = -1;
    private int gameHoveredButton = -1;

    private int shopTab = 0;
    private int selectedShopItem = -1;

    private String pendingLeaderboardName = "";

    private static final int[] LEVEL_TARGETS = {
        200, 400, 700, 1100, 1600, 2200, 2900, 3700, 4600, 5600
    };

    private int getLevelTarget() { return LEVEL_TARGETS[Math.min(level-1, LEVEL_TARGETS.length-1)]; }

    // ── inner helpers ──────────────────────────────────────────────────────────

    private static class Cloud {
        float x, y, speed;
        int w, h;
        float alpha;
        Cloud(int panelW) { reset(panelW, true); }
        void reset(int panelW, boolean randomX) {
            x     = randomX ? (float)(Math.random() * panelW) : -220;
            y     = (float)(25 + Math.random() * 110);
            speed = (float)(0.25 + Math.random() * 0.55);
            w     = (int)(90 + Math.random() * 130);
            h     = (int)(28 + Math.random() * 32);
            alpha = (float)(0.45 + Math.random() * 0.45);
        }
        void update(int panelW) { x += speed; if (x > panelW + 220) reset(panelW, false); }
    }

    private static class RainDrop {
        float x, y, speed, len;
        RainDrop(int w, int h) {
            x = (float)(Math.random() * w);
            y = (float)(Math.random() * h);
            speed = (float)(7 + Math.random() * 5);
            len   = (float)(12 + Math.random() * 22);
        }
        void update(int h) {
            y += speed;
            if (y > h) { y = -len; x = (float)(Math.random() * 650); }
        }
    }

    // ── constructor ────────────────────────────────────────────────────────────

    public GamePanel() {
        setPreferredSize(new Dimension(W, H));
        setBackground(new Color(30, 60, 20));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);

        playerData   = new PlayerData();
        scoreManager = new ScoreManager();
        achievements = new AchievementManager();
        sound        = new SoundManager();
        farm         = new FarmProgression(playerData.getFarmStage());

        rand = new Random();
        achievements.loadFromString(playerData.getAchievementData());
        initClouds();
        initAmbient();
    }

    private void initClouds() {
        clouds   = new ArrayList<>();
        for (int i = 0; i < 6; i++) clouds.add(new Cloud(ARENA_W));
        rainDrops = new ArrayList<>();
        for (int i = 0; i < 140; i++) rainDrops.add(new RainDrop(ARENA_W, H));
    }

    private void initAmbient() {
        // Fireflies for dusk/night levels
        fireflyX     = new float[18];
        fireflyY     = new float[18];
        fireflyPhase = new float[18];
        for (int i = 0; i < 18; i++) {
            fireflyX[i]     = rand.nextFloat() * ARENA_W;
            fireflyY[i]     = 100 + rand.nextFloat() * (H - 200);
            fireflyPhase[i] = rand.nextFloat() * (float)(Math.PI * 2);
        }
        starTwinkle = new float[60];
        for (int i = 0; i < 60; i++) starTwinkle[i] = rand.nextFloat() * (float)(Math.PI * 2);
    }

    // ── game loop ──────────────────────────────────────────────────────────────
    public void onResize(int newW, int newH) {
        W = Math.max(800, newW);
        H = Math.max(600, newH);
        SIDEBAR_W = Math.max(180, W / 4);
        ARENA_W = W - SIDEBAR_W;

        // Reposition basket and character if in game
        if (basket != null) {
            if (basket.getX() > ARENA_W - basket.getWidth())
                basket.setTargetX(ARENA_W / 2f);
        }
        if (character != null) {
            character.x = ARENA_W / 2f - 25;
            character.y = H - 160;
        }
    }


    public void startGameLoop() {
        running    = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long   lastTime    = System.nanoTime();
        double nsPerTick   = 1_000_000_000.0 / 60.0;
        double delta       = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;
            while (delta >= 1) { tick(); delta--; }
            repaint();
            try { Thread.sleep(1); } catch (InterruptedException ex) {}
        }
    }

    private void tick() {
        tickCount++;
        for (Cloud c : clouds) c.update(ARENA_W);

        // Animate fireflies
        for (int i = 0; i < fireflyX.length; i++) {
            fireflyPhase[i] += 0.03f;
            fireflyX[i]     += (float)(Math.sin(fireflyPhase[i] * 0.7) * 0.4);
            fireflyY[i]     += (float)(Math.cos(fireflyPhase[i] * 0.5) * 0.3);
            if (fireflyX[i] < 0) fireflyX[i] = ARENA_W;
            if (fireflyX[i] > ARENA_W) fireflyX[i] = 0;
            if (fireflyY[i] < 80)  fireflyY[i] = 80;
            if (fireflyY[i] > H - 100) fireflyY[i] = H - 100;
        }
        for (int i = 0; i < starTwinkle.length; i++) starTwinkle[i] += 0.04f;

        if (screen == GameScreen.GAME) synchronized (lock) { gameUpdate(); }
        if (toastTimer     > 0) toastTimer--;
        if (comboFlashTimer > 0) comboFlashTimer--;
        if (screenShakeTimer > 0) {
            screenShakeTimer--;
            screenShakeX = (float)((rand.nextFloat() - 0.5) * 10 * (screenShakeTimer / 20f));
            screenShakeY = (float)((rand.nextFloat() - 0.5) * 10 * (screenShakeTimer / 20f));
        } else { screenShakeX = 0; screenShakeY = 0; }

        if (toastTimer == 0) {
            Achievement a = achievements.pollToast();
            if (a != null) showToast(a.getEmoji() + " " + a.getName() + " unlocked!");
        }
    }

    // ── game logic (unchanged) ─────────────────────────────────────────────────

    private void gameUpdate() {
        if (tickCount % 60 == 0 && timeLeft > 0) {
            timeLeft--;
            if (timeLeft == 0) endGame();
        }
        basket.update();
        float targetCharX = basket.getX() + basket.getShakeOffset() + basket.getWidth() / 2f - character.getWidth() / 2f;
        float targetCharY = basket.getY() - character.getHeight() + 10;
        character.setTarget(targetCharX, targetCharY);
        character.update();
        character.setActive(true);

        if (magnetActive && magnetTimer > 0) {
            magnetTimer--;
            if (magnetTimer == 0) magnetActive = false;
            for (Ball b : balls) {
                if (!b.isActive()) continue;
                float cx  = basket.getX() + basket.getWidth() / 2f;
                float bx2 = b.getX() + b.getWidth() / 2f;
                float dist = Math.abs(cx - bx2);
                if (dist < 200) b.x += (cx - bx2) * 0.06f;
            }
        }
        if (doublePointsActive && doubleTimer > 0) {
            doubleTimer--;
            if (doubleTimer == 0) doublePointsActive = false;
        }

        spawnTimer--;
        if (spawnTimer <= 0) {
            spawnBall();
            int base = Math.max(20, 60 - level * 4);
            spawnTimer = base + rand.nextInt(20);
        }
        powerUpSpawnTimer--;
        if (powerUpSpawnTimer <= 0) {
            if (rand.nextInt(4) == 0) spawnPowerUp();
            powerUpSpawnTimer = 300 + rand.nextInt(300);
        }

        Iterator<Ball> bi = balls.iterator();
        while (bi.hasNext()) {
            Ball b = bi.next();
            b.update();
            if (b.getY() > H) {
                if (!b.getType().isBad()) { combo = 0; comboMultiplier = 1f; }
                bi.remove();
                continue;
            }
            if (b.intersects(basket)) { handleCatch(b); bi.remove(); }
        }

        Iterator<PowerUp> pi = powerUps.iterator();
        while (pi.hasNext()) {
            PowerUp p = pi.next();
            p.update();
            if (p.getY() > H) { pi.remove(); continue; }
            if (p.intersects(basket)) {
                activatePowerUp(p.getType());
                spawnParticles((int)(p.getX() + 20), (int)(p.getY()), p.getType().color, 12);
                pi.remove();
            }
        }

        particles.removeIf(Particle::isDead);
        for (Particle p : particles) p.update();
        if (level >= 7) for (RainDrop r : rainDrops) r.update(H);

        // Level up every 30 seconds
        if (tickCount % 1800 == 0 && tickCount > 0) {
            level++;
            sound.playLevelUp();
            showToast("🎉 Level Up! Level " + level);
            spawnParticles(ARENA_W / 2, H / 2, new Color(255, 215, 0), 30);
            checkAchievements();
        }
    }

    private void handleCatch(Ball b) {
        boolean isBad = b.getType().isBad();
        int pts = b.getType().getPoints();
        if (isBad) {
            if (shieldActive) {
                shieldActive = false;
                showToast("🛡️ Shield blocked it!");
                basket.triggerCatch(); character.triggerCatch();
            } else {
                score = Math.max(0, score + pts);
                screenShakeTimer = 20;
                basket.triggerShake(); character.triggerShake();
                combo = 0; comboMultiplier = 1f;
                sound.playCatch(false);
                addFloatingText((int)(b.getX()), (int)(b.getY()), "" + pts, new Color(255, 80, 80));
            }
        } else {
            combo++;
            float mult = 1f;
            if      (combo >= 11) mult = 5f;
            else if (combo >= 8)  mult = 4f;
            else if (combo >= 5)  mult = 3f;
            else if (combo >= 3)  mult = 2f;
            if (mult > comboMultiplier) {
                comboMultiplier = mult;
                showComboFlash("x" + (int)comboMultiplier + " COMBO!");
                sound.playCombo();
            }
            if (doublePointsActive) mult *= 2;
            int earned = (int)(pts * mult);
            score += earned;
            int coins = Math.max(1, earned / 5);
            coinsThisGame += coins;
            playerData.addCoins(coins);
            if (highestCombo < combo) highestCombo = combo;
            basket.triggerCatch(); character.triggerCatch();
            sound.playCatch(true);
            String prefix = mult > 1 ? "x" + (int)mult + " " : "";
            addFloatingText((int)(b.getX()), (int)(b.getY()), prefix + "+" + earned, new Color(100, 255, 100));
            spawnParticles((int)(b.getX() + 20), (int)(b.getY()), new Color(255, 220, 80), 6);
        }
        checkAchievements();
        playerData.setAchievementData(achievements.saveToString());
    }

    private void checkAchievements() {
        if (combo >= 1 && !achievements.isUnlocked("first_harvest")) achievements.tryUnlock("first_harvest");
        if (combo >= 3) achievements.tryUnlock("on_fire");
        if (playerData.getTotalCoins() >= 500) achievements.tryUnlock("rich_farmer");
        if (level >= 5 && timeLeft >= 90) achievements.tryUnlock("speed_farmer");
        if (farm.getStage() >= 10) achievements.tryUnlock("dream_farm");
        if (level >= 10) achievements.tryUnlock("legend");
    }

    private void activatePowerUp(PowerUp.PowerUpType type) {
        sound.playPowerUp();
        switch (type) {
            case MAGNET:        magnetActive = true; magnetTimer = 300; showToast("🧲 Magnet activated!"); break;
            case TIME_PLUS:     timeLeft += 15; showToast("⏰ +15 seconds!"); break;
            case SHIELD:        shieldActive = true; showToast("🛡️ Shield ready!"); break;
            case DOUBLE_POINTS: doublePointsActive = true; doubleTimer = 480; showToast("2️⃣ Double Points!"); break;
        }
    }

    private void spawnBall() {
        float x     = 30 + rand.nextFloat() * (ARENA_W - 70);
        float speed = Math.min(1.5f + level * 0.3f + rand.nextFloat() * 1.5f, 8f);
        int r = rand.nextInt(100);
        BallType type;
        if      (r < 5)  type = BallType.STRAWBERRY;
        else if (r < 15) type = BallType.MUSHROOM;
        else if (r < 25) type = BallType.EGGPLANT;
        else if (r < 60) type = BallType.APPLE;
        else             type = BallType.ORANGE;
        balls.add(new Ball(x, -40, type, speed));
    }

    private void spawnPowerUp() {
        float x = 30 + rand.nextFloat() * (ARENA_W - 70);
        PowerUp.PowerUpType[] types = PowerUp.PowerUpType.values();
        powerUps.add(new PowerUp(x, -40, types[rand.nextInt(types.length)]));
    }

    private void spawnParticles(int x, int y, Color color, int count) {
        for (int i = 0; i < count; i++) {
            float vx = (float)(rand.nextGaussian() * 2.5);
            float vy = (float)(-2 - rand.nextFloat() * 3);
            particles.add(new Particle(x, y, vx, vy, color, 6 + rand.nextInt(6), 30 + rand.nextInt(20)));
        }
    }

    private void addFloatingText(int x, int y, String text, Color color) {
        particles.add(new Particle(x, y, (float)(rand.nextGaussian() * 0.5), -2.5f, text, 50));
    }

    private void showToast(String text)      { toastText = text; toastTimer = 180; }
    private void showComboFlash(String text) { comboFlashText = text; comboFlashTimer = 90; }

    private void startNewGame() {
        score = 0; level = 1; combo = 0; comboMultiplier = 1f;
        timeLeft = 180; tickCount = 0; coinsThisGame = 0; highestCombo = 0;
        shieldActive = false; magnetActive = false; doublePointsActive = false;
        magnetTimer = 0; doubleTimer = 0; screenShakeTimer = 0;
        balls = new ArrayList<>(); powerUps = new ArrayList<>(); particles = new ArrayList<>();
        spawnTimer = 60; powerUpSpawnTimer = 300;
        basket = new Basket(ARENA_W / 2f - 40, H - 120, playerData.getEquippedBasket());
        String name = character != null ? character.getFarmerName() : "Farmer";
        character = new Character(ARENA_W / 2f - 25, H - 175, playerData.getEquippedSkin(), name);
        farm      = new FarmProgression(playerData.getFarmStage());
        screen    = GameScreen.GAME;
    }

    private void endGame() {
        screen = GameScreen.GAME_OVER;
        pendingLeaderboardName = character != null ? character.getFarmerName() : "Farmer";
    }

    // ── PAINTING ───────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);

        switch (screen) {
            case MAIN_MENU:         drawMainMenu(g);   break;
            case CHARACTER_CREATION:drawCharCreation(g);break;
            case EDIT_FARMER:       drawEditFarmer(g); break;
            case GAME:              drawGame(g);        break;
            case PAUSED:            drawGame(g); drawPause(g); break;
            case WARDROBE:          drawWardrobe(g);    break;
            case FARM_UPGRADE:      drawFarmUpgrade(g); break;
            case ACHIEVEMENTS:      drawAchievements(g);break;
            case GAME_OVER:         drawGameOver(g);    break;
            case LEADERBOARD:       drawLeaderboard(g); break;
        }
        if (toastTimer > 0) drawToast(g);
    }

    // ── Sky background ─────────────────────────────────────────────────────────

    private void drawSkyBackground(Graphics2D g, int lv) {
        int theme = (lv - 1) / 2;
        // Sky gradient per theme (every 2 levels)
        Color skyTop, skyBot;
        switch (theme % 4) {
            case 0: // Day - light blue to pale blue
                skyTop = new Color(135, 206, 250);
                skyBot = new Color(173, 216, 230);
                break;
            case 1: // Evening
                skyTop = new Color(255, 160, 40);
                skyBot = new Color(255, 220, 120);
                break;
            case 2: // Night
                skyTop = new Color(8, 12, 45);
                skyBot = new Color(30, 42, 80);
                break;
            case 3: // Snow
            default:
                skyTop = new Color(200, 220, 255);
                skyBot = new Color(240, 250, 255);
                break;
        }

        GradientPaint sky = new GradientPaint(0, 0, skyTop, 0, H - 80, skyBot);
        g.setPaint(sky);
        g.fillRect(0, 0, W, H);
        g.setPaint(null);

        // Mountains in the distance (parallax layer)
        drawMountains(g, theme);

        // Random trees for variety
        drawRandomTrees(g, theme);

        // Stars at night
        if (theme % 4 == 2) {
            Random sr = new Random(42);
            for (int i = 0; i < 60; i++) {
                int sx = sr.nextInt(W);
                int sy = sr.nextInt(H / 2);
                float brightness = 0.5f + 0.5f * (float)Math.sin(starTwinkle[i]);
                int alpha = (int)(brightness * 220);
                g.setColor(new Color(255, 255, 210, alpha));
                int ss = 1 + (i % 3 == 0 ? 1 : 0);
                g.fillOval(sx, sy, ss, ss);
                // Cross sparkle on bright stars
                if (i % 8 == 0 && brightness > 0.8f) {
                    g.setColor(new Color(255, 255, 200, 80));
                    g.drawLine(sx - 3, sy, sx + 3, sy);
                    g.drawLine(sx, sy - 3, sx, sy + 3);
                }
            }
        }

        // Sun for day/evening
        if (theme % 4 == 0 || theme % 4 == 1) {
            int sunX = W - 80, sunY = 60;
            g.setColor(new Color(255, 240, 120, 40));
            g.fillOval(sunX - 18, sunY - 18, 56, 56);
            g.setColor(new Color(255, 235, 80, 80));
            g.fillOval(sunX - 10, sunY - 10, 40, 40);
            g.setColor(new Color(255, 230, 60));
            g.fillOval(sunX, sunY, 22, 22);
            g.setColor(new Color(255, 230, 60, 130));
            g.setStroke(new BasicStroke(1.5f));
            for (int a = 0; a < 8; a++) {
                double angle = Math.toRadians(a * 45 + (tickCount * 0.3));
                int x1 = (int)(sunX + 11 + Math.cos(angle) * 16);
                int y1 = (int)(sunY + 11 + Math.sin(angle) * 16);
                int x2 = (int)(sunX + 11 + Math.cos(angle) * 22);
                int y2 = (int)(sunY + 11 + Math.sin(angle) * 22);
                g.drawLine(x1, y1, x2, y2);
            }
            g.setStroke(new BasicStroke(1f));
        } else if (lv >= 8) {
            int mx2 = W - 80, my2 = 55;
            g.setColor(new Color(220, 230, 255, 30));
            g.fillOval(mx2 - 12, my2 - 12, 48, 48);
            g.setColor(new Color(230, 238, 255));
            g.fillOval(mx2, my2, 26, 26);
            g.setColor(new Color(72, 90, 140));
            g.fillOval(mx2 + 6, my2 - 4, 20, 20);
        }

        for (Cloud c : clouds) {
            Color cloudColor;
            switch (theme % 4) {
                case 0: cloudColor = new Color(255, 255, 255, (int)(c.alpha * 210)); break;
                case 1: cloudColor = new Color(255, 210, 150, (int)(c.alpha * 180)); break;
                case 2: cloudColor = new Color(180, 120, 100, (int)(c.alpha * 140)); break;
                case 3: cloudColor = new Color(240, 240, 250, (int)(c.alpha * 160)); break;
                default: cloudColor = new Color(255, 255, 255, (int)(c.alpha * 210)); break;
            }
            g.setColor(cloudColor);
            g.fillOval((int)c.x, (int)c.y, c.w, c.h);
            g.fillOval((int)(c.x + c.w * 0.18), (int)(c.y - c.h * 0.32), (int)(c.w * 0.68), (int)(c.h * 0.95));
            g.fillOval((int)(c.x + c.w * 0.52), (int)(c.y - c.h * 0.08), (int)(c.w * 0.48), (int)(c.h * 0.75));
        }

        // Fireflies for evening/night
        if (theme % 4 == 1 || theme % 4 == 2) {
            for (int i = 0; i < fireflyX.length; i++) {
                float glow = 0.4f + 0.6f * (float)Math.sin(fireflyPhase[i] * 1.8);
                int alpha  = Math.max(0, (int)(glow * 200));
                g.setColor(new Color(180, 255, 120, alpha / 4));
                g.fillOval((int)fireflyX[i] - 5, (int)fireflyY[i] - 5, 10, 10);
                g.setColor(new Color(200, 255, 100, alpha));
                g.fillOval((int)fireflyX[i] - 2, (int)fireflyY[i] - 2, 4, 4);
            }
        }

        // Background elements: trees and houses
        drawBackgroundElements(g, theme);

        drawFullGrass(g, W, theme);

        if (lv >= 7) {
            g.setStroke(new BasicStroke(1.2f));
            for (RainDrop r : rainDrops) {
                int alpha = 90 + (int)(60 * (float)Math.sin(tickCount * 0.05 + r.x));
                g.setColor(new Color(160, 210, 255, alpha));
                g.drawLine((int)r.x, (int)r.y, (int)(r.x - 3), (int)(r.y + r.len));
            }
            g.setStroke(new BasicStroke(1f));

            if (tickCount % 240 == 0) {
                g.setColor(new Color(255, 255, 255, 60));
                g.fillRect(0, 0, W, H);
            }
        }

        if (lv >= 4 && lv <= 6) {
            float shimmer = 0.3f + 0.2f * (float)Math.sin(tickCount * 0.07);
            g.setColor(new Color(255, 180, 60, (int)(shimmer * 40)));
            g.fillRect(0, H - 100, W, 20);
        }
    }

    private void drawSkyBackgroundFull(Graphics2D g, int lv) {
        Color skyTop, skyBot;
        if      (lv <= 3) { skyTop = new Color(72, 158, 255);  skyBot = new Color(178, 224, 255); }
        else if (lv <= 6) { skyTop = new Color(255, 160, 40);  skyBot = new Color(255, 220, 120); }
        else if (lv <= 9) { skyTop = new Color(200, 60,  30);  skyBot = new Color(255, 140,  60); }
        else              { skyTop = new Color(8,   12,  45);  skyBot = new Color(30,  42,  80); }

        GradientPaint sky = new GradientPaint(0, 0, skyTop, 0, H - 80, skyBot);
        g.setPaint(sky);
        g.fillRect(0, 0, W, H);
        g.setPaint(null);

        if (lv >= 8) {
            Random sr = new Random(42);
            for (int i = 0; i < 60; i++) {
                int sx = sr.nextInt(W);
                int sy = sr.nextInt(H / 2);
                float brightness = 0.5f + 0.5f * (float)Math.sin(starTwinkle[i]);
                int alpha = (int)(brightness * 220);
                g.setColor(new Color(255, 255, 210, alpha));
                int ss = 1 + (i % 3 == 0 ? 1 : 0);
                g.fillOval(sx, sy, ss, ss);
                if (i % 8 == 0 && brightness > 0.8f) {
                    g.setColor(new Color(255, 255, 200, 80));
                    g.drawLine(sx - 3, sy, sx + 3, sy);
                    g.drawLine(sx, sy - 3, sx, sy + 3);
                }
            }
        }

        if (lv <= 6) {
            int sunX = W - 80, sunY = 60;
            g.setColor(new Color(255, 240, 120, 40));
            g.fillOval(sunX - 18, sunY - 18, 56, 56);
            g.setColor(new Color(255, 235, 80, 80));
            g.fillOval(sunX - 10, sunY - 10, 40, 40);
            g.setColor(new Color(255, 230, 60));
            g.fillOval(sunX, sunY, 22, 22);
            g.setColor(new Color(255, 230, 60, 130));
            g.setStroke(new BasicStroke(1.5f));
            for (int a = 0; a < 8; a++) {
                double angle = Math.toRadians(a * 45 + (tickCount * 0.3));
                int x1 = (int)(sunX + 11 + Math.cos(angle) * 16);
                int y1 = (int)(sunY + 11 + Math.sin(angle) * 16);
                int x2 = (int)(sunX + 11 + Math.cos(angle) * 22);
                int y2 = (int)(sunY + 11 + Math.sin(angle) * 22);
                g.drawLine(x1, y1, x2, y2);
            }
            g.setStroke(new BasicStroke(1f));
        } else if (lv >= 8) {
            int mx2 = W - 80, my2 = 55;
            g.setColor(new Color(220, 230, 255, 30));
            g.fillOval(mx2 - 12, my2 - 12, 48, 48);
            g.setColor(new Color(230, 238, 255));
            g.fillOval(mx2, my2, 26, 26);
            g.setColor(new Color(72, 90, 140));
            g.fillOval(mx2 + 6, my2 - 4, 20, 20);
        }

        drawFullGrass(g, W, 0);
    }

    private void drawFullGrass(Graphics2D g, int width, int theme) {
        if (theme % 4 == 3) { // Snow
            g.setColor(Color.WHITE);
            g.fillRect(0, H - 100, width, 100);
            g.setColor(new Color(255, 255, 255, 200));
            for (int i = 0; i < width; i += 50) {
                g.fillOval(i, H - 110, 30, 20);
            }
        } else {
            GradientPaint grassBack = new GradientPaint(0, H - 100, new Color(45, 115, 35), 0, H, new Color(28, 80, 20));
            g.setPaint(grassBack);
            g.fillRect(0, H - 100, width, 100);
            g.setPaint(null);

            g.setColor(new Color(60, 145, 48));
            for (int i = 0; i < width; i += 18) {
                g.fillArc(i - 2, H - 106, 16, 14, 0, 180);
            }
            g.setColor(new Color(90, 185, 65, 140));
            for (int i = 5; i < width; i += 24) {
                g.fillArc(i, H - 108, 10, 10, 0, 180);
            }

            g.setColor(new Color(255, 255, 255, 14));
            for (int i = 12; i < width; i += 36) {
                g.fillOval(i, H - 86 - (i % 72 == 0 ? 2 : 0), 4, 4);
            }
        }
    }

    private void drawBackgroundElements(Graphics2D g, int theme) {
        int baseY = H - 100;
        Color leafColor = new Color(34, 139, 34);
        Color trunkColor = new Color(139, 69, 19);
        Color houseColor = new Color(210, 180, 140);
        Color roofColor = new Color(139, 69, 19);
        Color trimColor = new Color(255, 240, 180);

        switch (theme % 4) {
            case 1: // Evening
                leafColor = new Color(235, 120, 45);
                trunkColor = new Color(120, 50, 18);
                houseColor = new Color(220, 160, 110);
                roofColor = new Color(140, 60, 20);
                trimColor = new Color(255, 220, 140);
                break;
            case 2: // Night
                leafColor = new Color(80, 115, 145);
                trunkColor = new Color(90, 70, 50);
                houseColor = new Color(95, 115, 145);
                roofColor = new Color(55, 75, 110);
                trimColor = new Color(210, 220, 255);
                break;
            case 3: // Snow
                leafColor = new Color(180, 230, 255);
                trunkColor = new Color(130, 110, 90);
                houseColor = new Color(230, 240, 250);
                roofColor = new Color(180, 200, 220);
                trimColor = new Color(255, 255, 255);
                break;
        }

        // Trees
        // Tree 1 (large)
        g.setColor(trunkColor);
        g.fillRect(80, baseY - 70, 18, 70);
        g.setColor(leafColor);
        g.fillOval(55, baseY - 130, 75, 80);
        g.fillOval(65, baseY - 150, 55, 90);
        if (theme % 4 == 3) {
            g.setColor(new Color(255, 255, 255, 220));
            g.fillOval(58, baseY - 126, 40, 26);
            g.fillOval(68, baseY - 146, 34, 30);
        }

        // Tree 2 (medium)
        g.setColor(trunkColor);
        g.fillRect(260, baseY - 60, 14, 60);
        g.setColor(leafColor);
        g.fillOval(240, baseY - 110, 60, 56);
        g.fillOval(250, baseY - 130, 46, 70);
        if (theme % 4 == 3) {
            g.setColor(new Color(255, 255, 255, 210));
            g.fillOval(244, baseY - 106, 36, 22);
        }

        // Tree 3 (small)
        g.setColor(trunkColor);
        g.fillRect(150, baseY - 50, 12, 50);
        g.setColor(leafColor);
        g.fillOval(130, baseY - 90, 50, 45);
        if (theme % 4 == 3) {
            g.setColor(new Color(255, 255, 255, 200));
            g.fillOval(135, baseY - 85, 25, 15);
        }

        // Tree 4 (tall)
        g.setColor(trunkColor);
        g.fillRect(350, baseY - 80, 16, 80);
        g.setColor(leafColor);
        g.fillOval(325, baseY - 140, 65, 75);
        g.fillOval(335, baseY - 160, 45, 85);
        if (theme % 4 == 3) {
            g.setColor(new Color(255, 255, 255, 215));
            g.fillOval(330, baseY - 135, 35, 25);
        }

        // Tree 5 (bushy)
        g.setColor(trunkColor);
        g.fillRect(500, baseY - 55, 13, 55);
        g.setColor(leafColor);
        g.fillOval(480, baseY - 100, 55, 50);
        g.fillOval(485, baseY - 115, 45, 60);
        if (theme % 4 == 3) {
            g.setColor(new Color(255, 255, 255, 205));
            g.fillOval(485, baseY - 95, 30, 20);
        }

        // Houses
        // House 1
        g.setColor(houseColor);
        g.fillRect(440, baseY - 90, 90, 90);
        g.setColor(roofColor);
        int[] xPoints1 = {430, 485, 540};
        int[] yPoints1 = {baseY - 90, baseY - 130, baseY - 90};
        g.fillPolygon(xPoints1, yPoints1, 3);
        g.setColor(trimColor);
        g.fillRect(475, baseY - 50, 20, 40);
        g.setColor(new Color(120, 70, 40));
        g.fillRect(470, baseY - 38, 10, 28);
        if (theme % 4 == 3) {
            g.setColor(new Color(255, 255, 255, 220));
            g.fillRect(462, baseY - 132, 30, 14);
            g.fillRect(500, baseY - 132, 30, 14);
        }

        // House 2
        g.setColor(houseColor.darker());
        g.fillRect(610, baseY - 80, 70, 80);
        g.setColor(roofColor.darker());
        int[] xPoints2 = {600, 645, 690};
        int[] yPoints2 = {baseY - 80, baseY - 110, baseY - 80};
        g.fillPolygon(xPoints2, yPoints2, 3);
        g.setColor(trimColor);
        g.fillRect(640, baseY - 40, 16, 40);
        if (theme % 4 == 3) {
            g.setColor(new Color(255, 255, 255, 210));
            g.fillArc(607, baseY - 83, 18, 12, 0, 180);
        }

        // House 3 (small cottage)
        g.setColor(houseColor.brighter());
        g.fillRect(720, baseY - 70, 60, 70);
        g.setColor(roofColor);
        int[] xPoints3 = {710, 750, 790};
        int[] yPoints3 = {baseY - 70, baseY - 100, baseY - 70};
        g.fillPolygon(xPoints3, yPoints3, 3);
        g.setColor(trimColor);
        g.fillRect(745, baseY - 35, 15, 35);
        if (theme % 4 == 3) {
            g.setColor(new Color(255, 255, 255, 200));
            g.fillRect(735, baseY - 102, 20, 10);
        }
    }

    private void drawMountains(Graphics2D g, int theme) {
        // Simple mountain silhouettes for parallax effect
        g.setColor(new Color(80, 80, 80, 120)); // distant gray
        // Mountain 1
        int[] x1 = {0, 120, 240};
        int[] y1 = {H - 80, H - 150, H - 80};
        g.fillPolygon(x1, y1, 3);
        // Mountain 2
        int[] x2 = {180, 300, 420};
        int[] y2 = {H - 80, H - 130, H - 80};
        g.fillPolygon(x2, y2, 3);
        // Mountain 3
        int[] x3 = {360, 480, 600};
        int[] y3 = {H - 80, H - 140, H - 80};
        g.fillPolygon(x3, y3, 3);
        // Mountain 4
        int[] x4 = {540, 660, 780};
        int[] y4 = {H - 80, H - 160, H - 80};
        g.fillPolygon(x4, y4, 3);
        // Mountain 5
        int[] x5 = {720, 840, 960};
        int[] y5 = {H - 80, H - 120, H - 80};
        g.fillPolygon(x5, y5, 3);
    }

    private void drawRandomTrees(Graphics2D g, int theme) {
        Random rand = new Random(12345); // fixed seed for consistent generation
        Color treeColor = new Color(34, 139, 34);
        if (theme % 4 == 1) treeColor = new Color(235, 120, 45);
        else if (theme % 4 == 2) treeColor = new Color(80, 115, 145);
        else if (theme % 4 == 3) treeColor = new Color(180, 230, 255);
        g.setColor(treeColor);

        int treeSpacing = 40; // spacing to avoid big gaps
        for (int i = 0; i < ARENA_W; i += treeSpacing) {
            int x = i + rand.nextInt(treeSpacing / 2); // vary position
            int y = H - 120 + rand.nextInt(10); // near ground
            int size = 15 + rand.nextInt(10); // vary size
            if (rand.nextBoolean()) { // Pine tree (triangular)
                int[] px = {x, x + size / 2, x + size};
                int[] py = {y, y - size, y};
                g.fillPolygon(px, py, 3);
            } else { // Oak tree (round)
                g.fillOval(x, y - size, size, size);
            }
        }
    }

    private void drawSidebarActionButton(Graphics2D g, Rectangle rect, String label, boolean hovered) {
        Color bg = hovered ? new Color(100, 190, 70) : new Color(70, 130, 50);
        Color border = hovered ? new Color(190, 245, 110) : new Color(120, 180, 80);
        GradientPaint btnGrad = new GradientPaint(rect.x, rect.y, bg.brighter(), rect.x, rect.y + rect.height, bg.darker());
        g.setPaint(btnGrad);
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);
        g.setPaint(null);
        g.setColor(border);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 12, 12);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, rect.x + (rect.width - fm.stringWidth(label)) / 2, rect.y + rect.height / 2 + 5);
    }

    // ── Game screen ────────────────────────────────────────────────────────────

    private void drawGame(Graphics2D g) {
        g.translate((int)screenShakeX, (int)screenShakeY);
        drawSkyBackground(g, level);

        synchronized (lock) {
            for (Ball b    : balls)    b.draw(g);
            for (PowerUp p : powerUps) p.draw(g);
            for (Particle p: particles)p.draw(g);
        }
        character.draw(g);
        basket.draw(g);

        // Shield aura around basket
        if (shieldActive) {
            float pulse = 0.6f + 0.4f * (float)Math.sin(tickCount * 0.15);
            g.setColor(new Color(80, 180, 255, (int)(pulse * 80)));
            g.setStroke(new BasicStroke(3f));
            g.drawOval((int)(basket.getX() - 6), (int)(basket.getY() - 6),
                       basket.getWidth() + 12, basket.getHeight() + 12);
            g.setStroke(new BasicStroke(1f));
        }

        // Magnet aura
        if (magnetActive) {
            float pulse = 0.5f + 0.5f * (float)Math.sin(tickCount * 0.2);
            g.setColor(new Color(255, 80, 200, (int)(pulse * 60)));
            g.fillOval((int)(basket.getX() - 90), (int)(basket.getY() - 90),
                       basket.getWidth() + 180, basket.getHeight() + 180);
        }

        // Combo flash
        if (comboFlashTimer > 0) {
            float alpha = Math.min(1f, comboFlashTimer / 30f);
            float scale = 1f + (1f - comboFlashTimer / 90f) * 0.5f;
            Font comboFont = new Font("Arial Black", Font.BOLD, (int)(28 * scale));
            g.setFont(comboFont);
            FontMetrics fm = g.getFontMetrics();
            int tw2 = fm.stringWidth(comboFlashText);
            int tx  = (ARENA_W - tw2) / 2;
            // Shadow
            g.setColor(new Color(80, 40, 0, (int)(alpha * 160)));
            g.drawString(comboFlashText, tx + 2, H / 2 - 28);
            g.setColor(new Color(255, 220, 50, (int)(alpha * 240)));
            g.drawString(comboFlashText, tx, H / 2 - 30);
        }

        g.translate(-(int)screenShakeX, -(int)screenShakeY);
        drawSidebar(g);
    }

    // ── Sidebar ────────────────────────────────────────────────────────────────

    private void drawSidebar(Graphics2D g) {
        int sx = ARENA_W;

        // Background with wood-panel feel
        GradientPaint sbg = new GradientPaint(sx, 0, new Color(38, 68, 28), sx + SIDEBAR_W, 0, new Color(28, 52, 18));
        g.setPaint(sbg);
        g.fillRect(sx, 0, SIDEBAR_W, H);

        // Decorative border line
        g.setPaint(null);
        g.setColor(new Color(100, 170, 70));
        g.setStroke(new BasicStroke(2f));
        g.drawLine(sx, 0, sx, H);
        g.setStroke(new BasicStroke(1f));

        // Subtle wood grain
        g.setColor(new Color(0, 0, 0, 18));
        for (int gy = 0; gy < H; gy += 6) g.drawLine(sx, gy, sx + SIDEBAR_W, gy);

        int py = 20;
        Font titleFont = new Font("Arial Black", Font.PLAIN, 13);
        Font valFont   = new Font("Arial", Font.BOLD, 22);
        Font smallFont = new Font("Arial", Font.PLAIN, 11);

        drawSidebarItem(g, sx + 12, py, "LEVEL",  "" + level, new Color(255, 228, 80), titleFont, valFont); py += 58;
        drawSidebarItem(g, sx + 12, py, "SCORE",  "" + score, new Color(100, 228, 255), titleFont, valFont); py += 58;

        boolean pulse      = timeLeft <= 30 && tickCount % 30 < 15;
        Color timerColor   = timeLeft <= 30 ? new Color(255, 80, 80) : new Color(100, 255, 150);
        if (pulse) {
            g.setColor(new Color(255, 50, 50, 55));
            g.fillRoundRect(sx + 5, py - 5, SIDEBAR_W - 10, 55, 10, 10);
        }
        String mins = "" + (timeLeft / 60);
        String secs = String.format("%02d", timeLeft % 60);
        drawSidebarItem(g, sx + 12, py, "TIME", mins + ":" + secs, timerColor, titleFont, valFont); py += 62;

        drawSidebarItem(g, sx + 12, py, "COINS 💰", "" + playerData.getTotalCoins(), new Color(255, 215, 50), titleFont, new Font("Arial", Font.BOLD, 18)); py += 52;

        if (combo > 0) {
            Color cc = combo >= 8 ? new Color(255, 100, 255) : combo >= 5 ? new Color(255, 180, 50) : new Color(100, 255, 200);
            drawSidebarItem(g, sx + 12, py, "COMBO " + combo, "x" + (int)comboMultiplier, cc, titleFont, valFont);
        }
        py += 58;

        // Power-up indicators
        int pip = sx + 10;
        Font emoji20 = new Font("Segoe UI Emoji", Font.PLAIN, 20);
        if (shieldActive)       { g.setFont(emoji20); g.setColor(Color.WHITE); g.drawString("🛡️", pip, py); pip += 30; }
        if (magnetActive)       { g.setFont(emoji20); g.setColor(Color.WHITE); g.drawString("🧲", pip, py); pip += 30; }
        if (doublePointsActive) { g.setFont(emoji20); g.setColor(Color.WHITE); g.drawString("2️⃣", pip, py); pip += 30; }
        py += 30;

        // Progress bar
        g.setFont(smallFont);
        g.setColor(new Color(160, 200, 140));
        g.drawString("Level Progress", sx + 10, py - 3);
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRoundRect(sx + 10, py, SIDEBAR_W - 20, 14, 7, 7);
        float prog = Math.min(1f, (float)score / getLevelTarget());
        GradientPaint barGrad = new GradientPaint(sx + 10, 0, new Color(80, 220, 80), sx + 10 + (SIDEBAR_W - 20) * prog, 0, new Color(160, 255, 80));
        g.setPaint(barGrad);
        g.fillRoundRect(sx + 10, py, (int)((SIDEBAR_W - 20) * prog), 14, 7, 7);
        g.setPaint(null);
        py += 28;

        gameButtons = new Rectangle[3];
        gameButtons[0] = new Rectangle(sx + 12, py, 108, 36);
        gameButtons[1] = new Rectangle(sx + 12, py + 44, 108, 36);
        gameButtons[2] = new Rectangle(sx + 130, py + 44, 108, 36);
        drawSidebarActionButton(g, gameButtons[0], "🏦 Bank", gameHoveredButton == 0);
        drawSidebarActionButton(g, gameButtons[1], "🛒 Shop", gameHoveredButton == 1);
        drawSidebarActionButton(g, gameButtons[2], "❌ Quit",   gameHoveredButton == 2);
        py += 92;

        // Farm emoji
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 34));
        FontMetrics fm = g.getFontMetrics();
        String farmEmoji = farm.getEmoji();
        int fw = fm.stringWidth(farmEmoji);
        g.drawString(farmEmoji, sx + (SIDEBAR_W - fw) / 2, py + 34);
        g.setFont(smallFont);
        g.setColor(new Color(180, 220, 150));
        FontMetrics fm2 = g.getFontMetrics();
        String fn = farm.getName();
        g.drawString(fn, sx + (SIDEBAR_W - fm2.stringWidth(fn)) / 2, py + 52);
        py += 66;

        drawIconButton(g, sx + 15, py, sound.isMuted() ? "🔇" : "🔊", "M", 60, 32);
        drawIconButton(g, sx + 85, py, "⏸", "P", 60, 32);
    }

    private void drawSidebarItem(Graphics2D g, int x, int y, String label, String value, Color valueColor, Font labelFont, Font valFont) {
        g.setFont(labelFont);
        g.setColor(new Color(155, 190, 135));
        g.drawString(label, x, y + 13);
        g.setFont(valFont);
        g.setColor(valueColor);
        g.drawString(value, x, y + 38);
    }

    private void drawIconButton(Graphics2D g, int x, int y, String emoji, String key, int w, int h) {
        GradientPaint bp = new GradientPaint(x, y, new Color(70, 120, 55), x, y + h, new Color(45, 85, 35));
        g.setPaint(bp);
        g.fillRoundRect(x, y, w, h, 8, 8);
        g.setPaint(null);
        g.setColor(new Color(100, 165, 80));
        g.drawRoundRect(x, y, w, h, 8, 8);
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 15));
        g.setColor(Color.WHITE);
        g.drawString(emoji, x + 6, y + 20);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.setColor(new Color(180, 220, 150));
        g.drawString("[" + key + "]", x + 28, y + 20);
    }

    // ── Main Menu ──────────────────────────────────────────────────────────────

    private void drawMainMenu(Graphics2D g) {
        drawSkyBackground(g, 2);

        // Full overlay with vignette feel
        g.setColor(new Color(0, 0, 0, 70));
        g.fillRect(0, 0, W, H);

        // Animated title shimmer band
        float shimmer = (float)((Math.sin(tickCount * 0.04) + 1) / 2.0);
        g.setColor(new Color(255, 240, 100, (int)(shimmer * 30)));
        g.fillRect(0, 80, W, 80);

        // Title drop shadow
        g.setFont(new Font("Arial Black", Font.BOLD, 52));
        String title = "🌾 Catch the Ball! 🚜";
        FontMetrics fm = g.getFontMetrics();
        int tx = (W - fm.stringWidth(title)) / 2;
        g.setColor(new Color(20, 60, 10, 180));
        g.drawString(title, tx + 3, 133);
        // Title gradient
        GradientPaint titleGrad = new GradientPaint(0, 80, new Color(255, 238, 90), 0, 140, new Color(210, 130, 20));
        g.setPaint(titleGrad);
        g.drawString(title, tx, 130);
        g.setPaint(null);

        // Subtitle
        g.setFont(new Font("Arial", Font.ITALIC, 18));
        g.setColor(new Color(210, 255, 170));
        String sub = "A farm-fresh catching adventure!";
        FontMetrics fm2 = g.getFontMetrics();
        g.drawString(sub, (W - fm2.stringWidth(sub)) / 2, 165);

        // Buttons with hover glow
        boolean hasFarmer = playerData.getFarmerName() != null && !playerData.getFarmerName().isEmpty();
        String[] labels = hasFarmer ? new String[]{"▶  Play Game", "🏆  Leaderboard", "🧺  Wardrobe", "🏡  Farm Upgrade", "🏅  Achievements", "✏️  Edit Farmer"} : new String[]{"▶  Play Game", "🏆  Leaderboard", "🧺  Wardrobe", "🏡  Farm Upgrade", "🏅  Achievements"};
        menuButtons = new Rectangle[labels.length];
        int bw = 270, bh = 50, bx = (W - bw) / 2, by = 205;
        for (int i = 0; i < labels.length; i++) {
            menuButtons[i] = new Rectangle(bx, by + i * 62, bw, bh);
            drawMenuButton(g, menuButtons[i], labels[i], i == hoveredButton);
        }

        // Bottom info bar
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, H - 40, W, 40);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(new Color(255, 215, 50));
        g.drawString("🪙 " + playerData.getTotalCoins() + " coins", 20, H - 14);
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        g.drawString(farm.getEmoji(), W - 100, H - 10);
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.setColor(new Color(200, 230, 160));
        g.drawString(farm.getName(), W - 80, H - 14);
    }

    private void drawMenuButton(Graphics2D g, Rectangle r, String label, boolean hovered) {
        // Glow on hover
        if (hovered) {
            g.setColor(new Color(130, 220, 80, 50));
            g.fillRoundRect(r.x - 4, r.y - 4, r.width + 8, r.height + 8, 18, 18);
        }
        Color bg     = hovered ? new Color(90, 175, 65)  : new Color(52, 115, 42);
        Color border = hovered ? new Color(160, 240, 100) : new Color(88, 155, 68);
        GradientPaint bgGrad = new GradientPaint(r.x, r.y, bg.brighter(), r.x, r.y + r.height, bg.darker());
        g.setPaint(bgGrad);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 14, 14);
        g.setPaint(null);
        g.setColor(border);
        g.setStroke(new BasicStroke(hovered ? 2.5f : 1.8f));
        g.drawRoundRect(r.x, r.y, r.width, r.height, 14, 14);
        g.setStroke(new BasicStroke(1f));
        // Inner highlight
        g.setColor(new Color(255, 255, 255, 30));
        g.fillRoundRect(r.x + 2, r.y + 2, r.width - 4, r.height / 2 - 2, 12, 12);
        // Label
        g.setFont(new Font("Arial Black", Font.PLAIN, 16));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, r.x + (r.width - fm.stringWidth(label)) / 2, r.y + r.height / 2 + 6);
    }

    // ── Character creation ─────────────────────────────────────────────────────

    private void drawCharCreation(Graphics2D g) {
        drawSkyBackground(g, 1);
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, W, H);

        int px = W/2 - 280, py = 60;
        // Panel background
        GradientPaint panelBg = new GradientPaint(px, py, new Color(28, 65, 18, 230), px, py + 520, new Color(18, 45, 10, 230));
        g.setPaint(panelBg);
        g.fillRoundRect(px, py, 560, 520, 20, 20);
        g.setPaint(null);
        g.setColor(new Color(90, 170, 65));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(px, py, 560, 520, 20, 20);
        g.setStroke(new BasicStroke(1f));

        g.setFont(new Font("Arial Black", Font.BOLD, 26));
        g.setColor(new Color(255, 230, 80));
        String t = "🌾 Create Your Farmer";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(t, (W - fm.stringWidth(t)) / 2, 113);

        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.setColor(new Color(200, 230, 160));
        g.drawString("Farmer Name:", W/2 - 200, 163);
        g.setColor(new Color(45, 95, 35));
        g.fillRoundRect(W/2 - 200, 173, 400, 40, 8, 8);
        g.setColor(new Color(100, 200, 80));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(W/2 - 200, 173, 400, 40, 8, 8);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(Color.WHITE);
        g.drawString(nameInput.toString() + (tickCount % 60 < 30 ? "|" : ""), W/2 - 188, 201);

        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.setColor(new Color(200, 230, 160));
        g.drawString("Choose Your Starter:", W/2 - 200, 248);

        int skinBoxW = 120, skinBoxH = 130;
        int startX = W/2 - (int)(STARTER_SKINS.length * skinBoxW / 2.0 + (STARTER_SKINS.length - 1) * 10 / 2.0);
        for (int i = 0; i < STARTER_SKINS.length; i++) {
            int bx = startX + i * (skinBoxW + 10);
            boolean sel = i == selectedStarterSkin;
            if (sel) {
                g.setColor(new Color(120, 220, 80, 40));
                g.fillRoundRect(bx - 4, 261, skinBoxW + 8, skinBoxH + 8, 14, 14);
            }
            g.setColor(sel ? new Color(80, 160, 60, 210) : new Color(40, 80, 30, 190));
            g.fillRoundRect(bx, 265, skinBoxW, skinBoxH, 12, 12);
            g.setColor(sel ? new Color(150, 235, 100) : new Color(70, 130, 60));
            g.setStroke(new BasicStroke(sel ? 3f : 1.5f));
            g.drawRoundRect(bx, 265, skinBoxW, skinBoxH, 12, 12);
            g.setStroke(new BasicStroke(1f));
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
            FontMetrics emf = g.getFontMetrics();
            String emoji = STARTER_SKINS[i].getEmoji();
            g.drawString(emoji, bx + (skinBoxW - emf.stringWidth(emoji)) / 2, 323);
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.setColor(new Color(200, 240, 170));
            FontMetrics sfm = g.getFontMetrics();
            String sn = STARTER_SKINS[i].getDisplayName();
            g.drawString(sn, bx + (skinBoxW - sfm.stringWidth(sn)) / 2, 358);
        }

        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(new Color(180, 210, 140));
        g.drawString("All Farmer Skins (buyable in Wardrobe):", W/2 - 220, 423);
        int px2 = W/2 - 200;
        for (SkinType s : SkinType.values()) {
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            g.drawString(s.getEmoji(), px2, 458);
            px2 += 40;
        }

        boolean canStart = nameInput.length() > 0;
        GradientPaint startGrad = new GradientPaint(W/2 - 130, 488,
            canStart ? new Color(90, 200, 65) : new Color(60, 80, 50),
            W/2 - 130, 540,
            canStart ? new Color(55, 140, 38) : new Color(40, 55, 35));
        g.setPaint(startGrad);
        g.fillRoundRect(W/2 - 130, 488, 260, 50, 12, 12);
        g.setPaint(null);
        g.setColor(canStart ? new Color(130, 235, 100) : new Color(80, 120, 70));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(W/2 - 130, 488, 260, 50, 12, 12);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("Arial Black", Font.BOLD, 17));
        g.setColor(canStart ? Color.WHITE : new Color(120, 150, 110));
        String btnText = "Start Farming! 🌾";
        FontMetrics bfm = g.getFontMetrics();
        g.drawString(btnText, W/2 - bfm.stringWidth(btnText) / 2, 521);

        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(new Color(180, 220, 150));
        g.drawString("← Back to Menu", W/2 - 280 + 15, 553);
    }

    // ── Edit Farmer ────────────────────────────────────────────────────────────

    private void drawEditFarmer(Graphics2D g) {
        drawSkyBackground(g, 1);
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, W, H);

        // Initialize variables
        editableSkins = playerData.getOwnedSkins().toArray(new SkinType[0]);
        skinRects = new Rectangle[editableSkins.length];
        saveButton = new Rectangle(W/2 - 130, 488, 260, 50);
        backButton = new Rectangle(W/2 - 280, 540, 120, 20);

        // Prefill values
        if (nameInput.length() == 0 && playerData.getFarmerName() != null) {
            nameInput.append(playerData.getFarmerName());
        }
        selectedSkinIndex = Arrays.asList(editableSkins).indexOf(playerData.getEquippedSkin());

        int px = W/2 - 280, py = 60;
        // Panel background
        GradientPaint panelBg = new GradientPaint(px, py, new Color(28, 65, 18, 230), px, py + 520, new Color(18, 45, 10, 230));
        g.setPaint(panelBg);
        g.fillRoundRect(px, py, 560, 520, 20, 20);
        g.setPaint(null);
        g.setColor(new Color(90, 170, 65));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(px, py, 560, 520, 20, 20);
        g.setStroke(new BasicStroke(1f));

        g.setFont(new Font("Arial Black", Font.BOLD, 26));
        g.setColor(new Color(255, 230, 80));
        String t = "✏️ Edit Your Farmer";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(t, (W - fm.stringWidth(t)) / 2, 113);

        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.setColor(new Color(200, 230, 160));
        g.drawString("Farmer Name:", W/2 - 200, 163);
        g.setColor(new Color(45, 95, 35));
        g.fillRoundRect(W/2 - 200, 173, 400, 40, 8, 8);
        g.setColor(new Color(100, 200, 80));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(W/2 - 200, 173, 400, 40, 8, 8);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(Color.WHITE);
        g.drawString(nameInput.toString() + (tickCount % 60 < 30 ? "|" : ""), W/2 - 188, 201);

        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.setColor(new Color(200, 230, 160));
        g.drawString("Choose Your Skin:", W/2 - 200, 248);

        int skinBoxW = 120, skinBoxH = 130;
        int cols = 4, rows = (editableSkins.length + cols - 1) / cols;
        int startX = W/2 - (cols * skinBoxW + (cols - 1) * 10) / 2;
        int startY = 265;
        for (int i = 0; i < editableSkins.length; i++) {
            int col = i % cols, row = i / cols;
            int bx = startX + col * (skinBoxW + 10);
            int by = startY + row * (skinBoxH + 15);
            skinRects[i] = new Rectangle(bx, by, skinBoxW, skinBoxH);
            boolean owned = playerData.ownsSkin(editableSkins[i]);
            boolean sel = i == selectedSkinIndex;
            if (sel) {
                g.setColor(new Color(120, 220, 80, 40));
                g.fillRoundRect(bx - 4, by - 4, skinBoxW + 8, skinBoxH + 8, 14, 14);
            }
            g.setColor(sel ? new Color(80, 160, 60, 210) : owned ? new Color(40, 80, 30, 190) : new Color(20, 40, 20, 150));
            g.fillRoundRect(bx, by, skinBoxW, skinBoxH, 12, 12);
            g.setColor(sel ? new Color(150, 235, 100) : owned ? new Color(70, 130, 60) : new Color(50, 70, 50));
            g.setStroke(new BasicStroke(sel ? 3f : 1.5f));
            g.drawRoundRect(bx, by, skinBoxW, skinBoxH, 12, 12);
            g.setStroke(new BasicStroke(1f));
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
            FontMetrics emf = g.getFontMetrics();
            String emoji = editableSkins[i].getEmoji();
            g.drawString(emoji, bx + (skinBoxW - emf.stringWidth(emoji)) / 2, by + 55);
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.setColor(owned ? new Color(200, 240, 170) : new Color(150, 150, 150));
            FontMetrics sfm = g.getFontMetrics();
            String sn = editableSkins[i].getDisplayName();
            g.drawString(sn, bx + (skinBoxW - sfm.stringWidth(sn)) / 2, by + 90);
            if (!owned) {
                g.setColor(new Color(255, 100, 100, 150));
                g.setFont(new Font("Arial", Font.BOLD, 10));
                g.drawString("LOCKED", bx + (skinBoxW - sfm.stringWidth("LOCKED")) / 2, by + 105);
            }
        }

        boolean canSave = nameInput.length() > 0 && playerData.ownsSkin(editableSkins[selectedSkinIndex]);
        GradientPaint startGrad = new GradientPaint(W/2 - 130, 488,
            canSave ? new Color(90, 200, 65) : new Color(60, 80, 50),
            W/2 - 130, 540,
            canSave ? new Color(55, 140, 38) : new Color(40, 55, 35));
        g.setPaint(startGrad);
        g.fillRoundRect(W/2 - 130, 488, 260, 50, 12, 12);
        g.setPaint(null);
        g.setColor(canSave ? new Color(130, 235, 100) : new Color(80, 120, 70));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(W/2 - 130, 488, 260, 50, 12, 12);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("Arial Black", Font.BOLD, 17));
        g.setColor(canSave ? Color.WHITE : new Color(120, 150, 110));
        String btnText = "Save Changes 🌾";
        FontMetrics bfm = g.getFontMetrics();
        g.drawString(btnText, W/2 - bfm.stringWidth(btnText) / 2, 521);

        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(new Color(180, 220, 150));
        g.drawString("← Back to Menu", W/2 - 280 + 15, 553);
    }

    // ── Pause ──────────────────────────────────────────────────────────────────

    private void drawPause(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 165));
        g.fillRect(0, 0, W, H);
        int pw = 400, ph = 280, px = (W - pw) / 2, py = (H - ph) / 2;
        GradientPaint panelBg = new GradientPaint(px, py, new Color(30, 62, 25, 245), px, py + ph, new Color(18, 42, 12, 245));
        g.setPaint(panelBg);
        g.fillRoundRect(px, py, pw, ph, 20, 20);
        g.setPaint(null);
        g.setColor(new Color(100, 185, 70));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(px, py, pw, ph, 20, 20);
        g.setStroke(new BasicStroke(1f));

        g.setFont(new Font("Arial Black", Font.BOLD, 30));
        g.setColor(new Color(255, 220, 80));
        g.drawString("⏸ PAUSED", px + 100, py + 55);

        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score,  px + 30,  py + 100);
        g.drawString("Level: " + level,  px + 185, py + 100);
        g.drawString("Combo: " + combo,  px + 30,  py + 130);
        g.drawString("Coins: " + playerData.getTotalCoins(), px + 185, py + 130);

        menuButtons    = new Rectangle[2];
        menuButtons[0] = new Rectangle(px + 30,  py + 165, 160, 44);
        menuButtons[1] = new Rectangle(px + 210, py + 165, 160, 44);
        drawMenuButton(g, menuButtons[0], "▶ Resume", hoveredButton == 0);
        drawMenuButton(g, menuButtons[1], "🏠 Menu",  hoveredButton == 1);

        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(new Color(160, 200, 140));
        g.drawString("Press P to resume • ESC for menu", px + 68, py + 250);
    }

    // ── Wardrobe / Shop ────────────────────────────────────────────────────────

    private void drawWardrobe(Graphics2D g) {
        drawBackground(g, new Color(22, 48, 18), new Color(12, 32, 10));
        // Subtle pattern overlay
        g.setColor(new Color(255, 255, 255, 8));
        for (int i = 0; i < H; i += 20) g.drawLine(0, i, W, i);

        drawScreenTitle(g, "🧺 Wardrobe & Shop");

        // Tabs
        String[] tabs = {"👨‍🌾 Farmer Skins", "🧺 Basket Skins"};
        for (int i = 0; i < 2; i++) {
            boolean sel = shopTab == i;
            GradientPaint tabGrad = new GradientPaint(50 + i * 220, 85,
                sel ? new Color(88, 168, 62) : new Color(40, 82, 30),
                50 + i * 220, 121,
                sel ? new Color(62, 130, 42) : new Color(28, 60, 20));
            g.setPaint(tabGrad);
            g.fillRoundRect(50 + i * 220, 85, 200, 36, 10, 10);
            g.setPaint(null);
            g.setColor(sel ? new Color(155, 235, 105) : new Color(68, 128, 58));
            g.drawRoundRect(50 + i * 220, 85, 200, 36, 10, 10);
            g.setFont(new Font("Arial Black", Font.PLAIN, 13));
            g.setColor(sel ? Color.WHITE : new Color(155, 198, 138));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(tabs[i], 50 + i * 220 + (200 - fm.stringWidth(tabs[i])) / 2, 110);
        }

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(new Color(255, 215, 50));
        g.drawString("🪙 " + playerData.getTotalCoins() + " coins", W - 200, 100);

        if (shopTab == 0) {
            SkinType[] skins = SkinType.values();
            int cols = 4, boxW = 130, boxH = 150;
            for (int i = 0; i < skins.length; i++) {
                int col = i % cols, row = i / cols;
                int bx = 40 + col * (boxW + 15);
                int by = 140 + row * (boxH + 15);
                boolean owned    = playerData.ownsSkin(skins[i]);
                boolean equipped = playerData.getEquippedSkin() == skins[i];
                drawShopItemEmoji(g, bx, by, boxW, boxH, skins[i].getEmoji(), skins[i].getDisplayName(), skins[i].getCost(), owned, equipped);
            }
        } else {
            // ── FIX: basket skins drawn via skin.draw(), not getEmoji() ──
            BasketSkin[] baskets = BasketSkin.values();
            int cols = 5, boxW = 130, boxH = 150;
            for (int i = 0; i < baskets.length; i++) {
                int bx = 40 + i % cols * (boxW + 15);
                int by = 140 + i / cols * (boxH + 15);
                boolean owned    = playerData.ownsBasket(baskets[i]);
                boolean equipped = playerData.getEquippedBasket() == baskets[i];
                drawShopItemBasket(g, bx, by, boxW, boxH, baskets[i], owned, equipped);
            }
        }
        drawBackButton(g);
    }

    /** Shop card for SkinType (emoji-based character skins) */
    private void drawShopItemEmoji(Graphics2D g, int x, int y, int w, int h,
                                   String emoji, String name, int cost,
                                   boolean owned, boolean equipped) {
        drawShopCard(g, x, y, w, h, owned, equipped);

        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(Color.WHITE);
        g.drawString(emoji, x + (w - fm.stringWidth(emoji)) / 2, y + 55);

        drawShopCardLabels(g, x, y, w, h, name, cost, owned, equipped);
    }

    /** Shop card for BasketSkin (drawn via skin.draw()) — FIX for getEmoji() error */
    private void drawShopItemBasket(Graphics2D g, int x, int y, int w, int h,
                                    BasketSkin skin, boolean owned, boolean equipped) {
        drawShopCard(g, x, y, w, h, owned, equipped);

        // Render the basket drawing scaled to fit the card preview area
        // Preview area: centered in card, 60x36 px
        int previewW = 60, previewH = 36;
        int previewX = x + (w - previewW) / 2;
        int previewY = y + 22;

        // Clip to card so nothing overflows
        Shape oldClip = g.getClip();
        g.setClip(x + 2, y + 2, w - 4, 70);
        skin.draw(g, previewX, previewY, previewW, previewH);
        g.setClip(oldClip);

        drawShopCardLabels(g, x, y, w, h, skin.getDisplayName(), skin.getCost(), owned, equipped);
    }

    /** Shared card background */
    private void drawShopCard(Graphics2D g, int x, int y, int w, int h, boolean owned, boolean equipped) {
        if (equipped) {
            g.setColor(new Color(55, 130, 45, 40));
            g.fillRoundRect(x - 3, y - 3, w + 6, h + 6, 14, 14);
        }
        Color bg = equipped ? new Color(58, 138, 48) : owned ? new Color(38, 88, 28) : new Color(28, 58, 22);
        GradientPaint cardGrad = new GradientPaint(x, y, bg.brighter(), x, y + h, bg.darker());
        g.setPaint(cardGrad);
        g.fillRoundRect(x, y, w, h, 12, 12);
        g.setPaint(null);
        // Inner highlight
        g.setColor(new Color(255, 255, 255, 18));
        g.fillRoundRect(x + 2, y + 2, w - 4, h / 2, 10, 10);
        Color border = equipped ? new Color(148, 242, 100) : owned ? new Color(78, 158, 58) : new Color(58, 98, 48);
        g.setColor(border);
        g.setStroke(new BasicStroke(equipped ? 2.5f : 1.5f));
        g.drawRoundRect(x, y, w, h, 12, 12);
        g.setStroke(new BasicStroke(1f));
    }

    /** Shared card labels (name + equipped/buy) */
    private void drawShopCardLabels(Graphics2D g, int x, int y, int w, int h,
                                    String name, int cost, boolean owned, boolean equipped) {
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.setColor(new Color(200, 235, 170));
        FontMetrics fm2 = g.getFontMetrics();
        g.drawString(name, x + (w - fm2.stringWidth(name)) / 2, y + 78);

        if (equipped) {
            g.setColor(new Color(148, 255, 100));
            g.setFont(new Font("Arial Black", Font.PLAIN, 11));
            FontMetrics efm = g.getFontMetrics();
            g.drawString("✓ EQUIPPED", x + (w - efm.stringWidth("✓ EQUIPPED")) / 2, y + 100);
        } else if (owned) {
            g.setColor(new Color(100, 205, 80));
            g.setFont(new Font("Arial", Font.BOLD, 11));
            FontMetrics ofm = g.getFontMetrics();
            g.drawString("OWNED — Equip", x + (w - ofm.stringWidth("OWNED — Equip")) / 2, y + 100);
        } else {
            GradientPaint buyGrad = new GradientPaint(x + 10, y + 88, new Color(65, 130, 52), x + 10, y + 116, new Color(42, 92, 32));
            g.setPaint(buyGrad);
            g.fillRoundRect(x + 10, y + 88, w - 20, 28, 8, 8);
            g.setPaint(null);
            g.setColor(new Color(100, 185, 80));
            g.drawRoundRect(x + 10, y + 88, w - 20, 28, 8, 8);
            g.setFont(new Font("Arial Black", Font.PLAIN, 11));
            g.setColor(new Color(255, 218, 50));
            String costStr = "🪙 " + cost;
            FontMetrics fm3 = g.getFontMetrics();
            g.drawString(costStr, x + 10 + (w - 20 - fm3.stringWidth(costStr)) / 2, y + 108);
        }
    }

    // ── Farm upgrade ───────────────────────────────────────────────────────────

    private void drawFarmUpgrade(Graphics2D g) {
        drawBackground(g, new Color(28, 58, 18), new Color(12, 38, 8));
        g.setColor(new Color(255, 255, 255, 6));
        for (int i = 0; i < H; i += 20) g.drawLine(0, i, W, i);
        drawScreenTitle(g, "🏡 Farm Upgrade");

        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        FontMetrics fm = g.getFontMetrics();
        String farmEmoji = farm.getEmoji();
        // Glow behind farm emoji
        g.setColor(new Color(255, 220, 80, 35));
        g.fillOval((W - 120) / 2 - 10, 120, 140, 120);
        g.drawString(farmEmoji, (W - fm.stringWidth(farmEmoji)) / 2, 218);

        g.setFont(new Font("Arial Black", Font.BOLD, 22));
        g.setColor(new Color(255, 220, 100));
        String fn = farm.getName() + " (Stage " + farm.getStage() + "/20)";
        FontMetrics fm2 = g.getFontMetrics();
        g.drawString(fn, (W - fm2.stringWidth(fn)) / 2, 268);

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(new Color(200, 235, 160));
        g.drawString("🪙 Coins: " + playerData.getTotalCoins(), W/2 - 80, 308);

        if (farm.canUpgrade()) {
            g.setFont(new Font("Arial", Font.BOLD, 17));
            g.setColor(new Color(180, 225, 150));
            String nextName = new FarmProgression(farm.getStage() + 1).getName();
            g.drawString("Next: " + nextName, W/2 - 70, 342);

            boolean canAfford = playerData.getTotalCoins() >= farm.getUpgradeCost();
            GradientPaint btnGrad = new GradientPaint(W/2 - 150, 368,
                canAfford ? new Color(85, 188, 62) : new Color(85, 62, 62),
                W/2 - 150, 422,
                canAfford ? new Color(55, 138, 38) : new Color(62, 38, 38));
            g.setPaint(btnGrad);
            g.fillRoundRect(W/2 - 150, 368, 300, 52, 14, 14);
            g.setPaint(null);
            g.setColor(canAfford ? new Color(148, 235, 100) : new Color(155, 82, 82));
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(W/2 - 150, 368, 300, 52, 14, 14);
            g.setStroke(new BasicStroke(1f));
            g.setFont(new Font("Arial Black", Font.BOLD, 15));
            g.setColor(Color.WHITE);
            String upgStr = "Upgrade for 🪙 " + farm.getUpgradeCost();
            FontMetrics fm3 = g.getFontMetrics();
            g.drawString(upgStr, W/2 - fm3.stringWidth(upgStr) / 2, 401);
        } else {
            g.setFont(new Font("Arial Black", Font.BOLD, 22));
            g.setColor(new Color(255, 215, 50));
            g.drawString("👑 MAX LEVEL FARM!", W/2 - 140, 378);
        }

        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(new Color(160, 200, 140));
        g.drawString("Farm Stages:", 30, 458);
        int pfx = 30;
        for (int i = 1; i <= 20; i++) {
            FarmProgression fp2 = new FarmProgression(i);
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            g.setColor(i <= farm.getStage() ? Color.WHITE : new Color(100, 100, 100, 155));
            g.drawString(fp2.getEmoji(), pfx, 488);
            pfx += 42;
            if (pfx > W - 50) pfx = 30;
        }
        drawBackButton(g);
    }

    // ── Achievements ───────────────────────────────────────────────────────────

    private void drawAchievements(Graphics2D g) {
        drawBackground(g, new Color(22, 43, 18), new Color(12, 28, 10));
        drawScreenTitle(g, "🏅 Achievements");

        List<Achievement> all = achievements.getAll();
        int cols = 3, boxW = 230, boxH = 100, startX = 40, startY = 108;
        for (int i = 0; i < all.size(); i++) {
            Achievement a   = all.get(i);
            int col = i % cols, row = i / cols;
            int bx  = startX + col * (boxW + 20);
            int by  = startY + row * (boxH + 15);
            boolean unlocked = a.isUnlocked();

            GradientPaint ach = new GradientPaint(bx, by,
                unlocked ? new Color(52, 105, 36) : new Color(28, 48, 22),
                bx, by + boxH,
                unlocked ? new Color(36, 80, 24) : new Color(20, 38, 16));
            g.setPaint(ach);
            g.fillRoundRect(bx, by, boxW, boxH, 12, 12);
            g.setPaint(null);
            if (unlocked) {
                g.setColor(new Color(255, 255, 255, 15));
                g.fillRoundRect(bx + 2, by + 2, boxW - 4, boxH / 2, 10, 10);
            }
            g.setColor(unlocked ? new Color(118, 222, 80) : new Color(58, 88, 48));
            g.setStroke(new BasicStroke(unlocked ? 2f : 1f));
            g.drawRoundRect(bx, by, boxW, boxH, 12, 12);
            g.setStroke(new BasicStroke(1f));

            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            g.setColor(unlocked ? Color.WHITE : new Color(100, 118, 88));
            g.drawString(a.getEmoji(), bx + 12, by + 40);

            g.setFont(new Font("Arial Black", Font.PLAIN, 12));
            g.setColor(unlocked ? new Color(200, 255, 148) : new Color(98, 128, 88));
            g.drawString(a.getName(), bx + 52, by + 33);

            g.setFont(new Font("Arial", Font.PLAIN, 11));
            g.setColor(unlocked ? new Color(158, 212, 128) : new Color(78, 108, 68));
            g.drawString(a.getDescription(), bx + 52, by + 53);

            if (unlocked) {
                g.setFont(new Font("Arial", Font.BOLD, 11));
                g.setColor(new Color(100, 225, 80));
                g.drawString("✓ UNLOCKED", bx + 52, by + 76);
            }
        }
        drawBackButton(g);
    }

    // ── Game Over ──────────────────────────────────────────────────────────────

    private void drawGameOver(Graphics2D g) {
        drawSkyBackground(g, level);
        g.setColor(new Color(0, 0, 0, 185));
        g.fillRect(0, 0, W, H);

        int pw = 500, ph = 440, px = (W - pw) / 2, py = 58;
        GradientPaint panelBg = new GradientPaint(px, py, new Color(24, 54, 18, 245), px, py + ph, new Color(14, 36, 10, 245));
        g.setPaint(panelBg);
        g.fillRoundRect(px, py, pw, ph, 20, 20);
        g.setPaint(null);
        g.setColor(new Color(100, 205, 70));
        g.setStroke(new BasicStroke(2.5f));
        g.drawRoundRect(px, py, pw, ph, 20, 20);
        g.setStroke(new BasicStroke(1f));

        g.setFont(new Font("Arial Black", Font.BOLD, 34));
        g.setColor(new Color(255, 215, 50));
        String t = "🌾 Game Over!";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(t, px + (pw - fm.stringWidth(t)) / 2, py + 58);

        int statY = py + 98;
        drawStatLine(g, px + 30, statY, "Final Score",   "" + score);       statY += 34;
        drawStatLine(g, px + 30, statY, "Level Reached", "" + level);       statY += 34;
        drawStatLine(g, px + 30, statY, "Best Combo",    "" + highestCombo); statY += 34;
        drawStatLine(g, px + 30, statY, "Coins Earned",  "+" + coinsThisGame + " 🪙"); statY += 34;

        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(new Color(200, 230, 160));
        g.drawString("Enter name for leaderboard:", px + 30, statY + 8); statY += 24;
        g.setColor(new Color(38, 88, 28));
        g.fillRoundRect(px + 30, statY, pw - 60, 38, 8, 8);
        g.setColor(new Color(100, 205, 80));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(px + 30, statY, pw - 60, 38, 8, 8);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.setColor(Color.WHITE);
        g.drawString(pendingLeaderboardName + (tickCount % 60 < 30 ? "|" : ""), px + 42, statY + 25);
        statY += 52;

        menuButtons    = new Rectangle[3];
        menuButtons[0] = new Rectangle(px + 22,  statY, 140, 46);
        menuButtons[1] = new Rectangle(px + 182, statY, 145, 46);
        menuButtons[2] = new Rectangle(px + 348, statY, 130, 46);
        drawMenuButton(g, menuButtons[0], "🔄 Retry",     hoveredButton == 0);
        drawMenuButton(g, menuButtons[1], "🏆 Save Score", hoveredButton == 1);
        drawMenuButton(g, menuButtons[2], "🏠 Menu",       hoveredButton == 2);
    }

    private void drawStatLine(Graphics2D g, int x, int y, String label, String val) {
        g.setFont(new Font("Arial", Font.PLAIN, 15));
        g.setColor(new Color(158, 198, 138));
        g.drawString(label + ":", x, y);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.setColor(Color.WHITE);
        g.drawString(val, x + 200, y);
    }

    // ── Leaderboard ────────────────────────────────────────────────────────────

    private void drawLeaderboard(Graphics2D g) {
        drawBackground(g, new Color(18, 42, 13), new Color(8, 28, 6));
        drawScreenTitle(g, "🏆 Leaderboard");

        List<ScoreEntry> top = scoreManager.getTop10();
        String[] ranks = {"👑","🥈","🥉","4️⃣","5️⃣","6️⃣","7️⃣","8️⃣","9️⃣","🔟"};
        int rowH = 44, startY = 108;

        if (top.isEmpty()) {
            g.setFont(new Font("Arial", Font.ITALIC, 17));
            g.setColor(new Color(158, 200, 138));
            g.drawString("No scores yet. Play to get on the board!", W/2 - 200, 250);
        }

        for (int i = 0; i < top.size(); i++) {
            ScoreEntry e = top.get(i);
            int ry = startY + i * rowH;
            boolean isTop3 = i < 3;
            GradientPaint rowGrad = new GradientPaint(30, ry,
                isTop3 ? new Color(62, 125, 42, 210) : new Color(32, 72, 22, 165),
                W - 30, ry,
                isTop3 ? new Color(45, 100, 30, 210) : new Color(22, 52, 14, 165));
            g.setPaint(rowGrad);
            g.fillRoundRect(30, ry, W - 60, rowH - 6, 10, 10);
            g.setPaint(null);
            if (isTop3) {
                g.setColor(new Color(255, 255, 255, 12));
                g.fillRoundRect(30, ry, W - 60, (rowH - 6) / 2, 10, 10);
            }
            g.setColor(isTop3 ? new Color(148, 235, 100) : new Color(78, 148, 58));
            g.drawRoundRect(30, ry, W - 60, rowH - 6, 10, 10);

            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            g.setColor(Color.WHITE);
            g.drawString(ranks[i], 44, ry + 26);

            g.setFont(new Font("Arial Black", Font.PLAIN, 14));
            g.setColor(isTop3 ? new Color(255, 232, 100) : Color.WHITE);
            g.drawString(e.getName(), 98, ry + 27);

            g.setFont(new Font("Arial", Font.BOLD, 14));
            g.setColor(new Color(100, 225, 100));
            g.drawString("" + e.getScore(), 380, ry + 27);

            g.setFont(new Font("Arial", Font.PLAIN, 12));
            g.setColor(new Color(158, 200, 138));
            g.drawString("Lv." + e.getLevel(), 518, ry + 27);
        }
        drawBackButton(g);
    }

    // ── Toast ──────────────────────────────────────────────────────────────────

    private void drawToast(Graphics2D g) {
        float alpha = Math.min(1f, toastTimer / 30f);
        int tw2 = 340, th = 42;
        int tx = (W - tw2) / 2, ty = 28;
        // Shadow
        g.setColor(new Color(0, 0, 0, (int)(alpha * 80)));
        g.fillRoundRect(tx + 2, ty + 2, tw2, th, 12, 12);
        GradientPaint toastBg = new GradientPaint(tx, ty, new Color(28, 78, 18, (int)(alpha * 230)), tx, ty + th, new Color(18, 55, 10, (int)(alpha * 230)));
        g.setPaint(toastBg);
        g.fillRoundRect(tx, ty, tw2, th, 12, 12);
        g.setPaint(null);
        g.setColor(new Color(118, 212, 80, (int)(alpha * 255)));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(tx, ty, tw2, th, 12, 12);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        g.setColor(new Color(255, 255, 255, (int)(alpha * 255)));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(toastText, tx + (tw2 - fm.stringWidth(toastText)) / 2, ty + 27);
    }

    // ── Shared helpers ─────────────────────────────────────────────────────────

    private void drawBackground(Graphics2D g, Color top, Color bot) {
        GradientPaint bg = new GradientPaint(0, 0, top, 0, H, bot);
        g.setPaint(bg);
        g.fillRect(0, 0, W, H);
        g.setPaint(null);
    }

    private void drawScreenTitle(Graphics2D g, String title) {
        GradientPaint hdrBg = new GradientPaint(0, 0, new Color(45, 100, 30, 210), 0, 75, new Color(28, 65, 18, 210));
        g.setPaint(hdrBg);
        g.fillRect(0, 0, W, 75);
        g.setPaint(null);
        g.setColor(new Color(80, 160, 55, 180));
        g.setStroke(new BasicStroke(1.5f));
        g.drawLine(0, 75, W, 75);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("Arial Black", Font.BOLD, 26));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(new Color(255, 220, 80));
        g.drawString(title, (W - fm.stringWidth(title)) / 2, 50);
    }

    private void drawBackButton(Graphics2D g) {
        menuButtons    = new Rectangle[1];
        menuButtons[0] = new Rectangle(20, H - 55, 140, 38);
        drawMenuButton(g, menuButtons[0], "← Back", hoveredButton == 0);
    }

    // ── Input handlers ─────────────────────────────────────────────────────────

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (screen == GameScreen.GAME) {
            if      (code == KeyEvent.VK_LEFT  || code == KeyEvent.VK_A) basket.setTargetX(Math.max(40, basket.getX() + basket.getWidth() / 2f - 30));
            else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) basket.setTargetX(Math.min(ARENA_W - 40, basket.getX() + basket.getWidth() / 2f + 30));
            else if (code == KeyEvent.VK_P)      screen = GameScreen.PAUSED;
            else if (code == KeyEvent.VK_ESCAPE) screen = GameScreen.MAIN_MENU;
            else if (code == KeyEvent.VK_M)      sound.toggleMute();
        } else if (screen == GameScreen.PAUSED) {
            if      (code == KeyEvent.VK_P)      screen = GameScreen.GAME;
            else if (code == KeyEvent.VK_ESCAPE) screen = GameScreen.MAIN_MENU;
        } else if (screen == GameScreen.CHARACTER_CREATION) {
            if      (code == KeyEvent.VK_BACK_SPACE && nameInput.length() > 0) nameInput.deleteCharAt(nameInput.length() - 1);
            else if (code == KeyEvent.VK_ENTER && nameInput.length() > 0)      beginGame();
            else if (code == KeyEvent.VK_ESCAPE) screen = GameScreen.MAIN_MENU;
        } else if (screen == GameScreen.EDIT_FARMER) {
            if      (code == KeyEvent.VK_BACK_SPACE && nameInput.length() > 0) nameInput.deleteCharAt(nameInput.length() - 1);
            else if (code == KeyEvent.VK_ENTER && nameInput.length() > 0 && playerData.ownsSkin(editableSkins[selectedSkinIndex])) {
                playerData.equipSkin(editableSkins[selectedSkinIndex]);
                playerData.setFarmerName(nameInput.toString());
                screen = GameScreen.MAIN_MENU;
            }
            else if (code == KeyEvent.VK_ESCAPE) screen = GameScreen.MAIN_MENU;
        } else if (screen == GameScreen.GAME_OVER) {
            if (code == KeyEvent.VK_BACK_SPACE && pendingLeaderboardName.length() > 0)
                pendingLeaderboardName = pendingLeaderboardName.substring(0, pendingLeaderboardName.length() - 1);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (screen == GameScreen.CHARACTER_CREATION) {
            if (c >= 32 && c < 127 && nameInput.length() < 16) nameInput.append(c);
        } else if (screen == GameScreen.EDIT_FARMER) {
            if (c >= 32 && c < 127 && nameInput.length() < 16) nameInput.append(c);
        } else if (screen == GameScreen.GAME_OVER) {
            if (c >= 32 && c < 127 && pendingLeaderboardName.length() < 16) pendingLeaderboardName += c;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        if (screen == GameScreen.GAME) {
            if (mx < ARENA_W) basket.setTargetX(mx);
            gameHoveredButton = -1;
            if (gameButtons != null) {
                for (int i = 0; i < gameButtons.length; i++) {
                    if (gameButtons[i] != null && gameButtons[i].contains(mx, my)) { gameHoveredButton = i; break; }
                }
            }
        } else if (screen == GameScreen.EDIT_FARMER) {
            selectedSkinIndex = -1;
            if (skinRects != null) {
                for (int i = 0; i < skinRects.length; i++) {
                    if (skinRects[i] != null && skinRects[i].contains(mx, my)) { selectedSkinIndex = i; break; }
                }
            }
        } else {
            hoveredButton = -1;
            if (menuButtons != null) {
                for (int i = 0; i < menuButtons.length; i++) {
                    if (menuButtons[i] != null && menuButtons[i].contains(mx, my)) { hoveredButton = i; break; }
                }
            }
        }
    }

    @Override public void mouseDragged(MouseEvent e) { mouseMoved(e); }
    @Override public void mouseClicked(MouseEvent e) { handleClick(e.getX(), e.getY()); }

    private void handleClick(int mx, int my) {
        switch (screen) {
            case MAIN_MENU:
                if (menuButtons == null) return;
                boolean hasFarmer = playerData.getFarmerName() != null && !playerData.getFarmerName().isEmpty();
                for (int i = 0; i < menuButtons.length; i++) {
                    if (menuButtons[i] != null && menuButtons[i].contains(mx, my)) {
                        switch (i) {
                            case 0: if (hasFarmer) startNewGame(); else screen = GameScreen.CHARACTER_CREATION; break;
                            case 1: screen = GameScreen.LEADERBOARD;        break;
                            case 2: screen = GameScreen.WARDROBE;           break;
                            case 3: screen = GameScreen.FARM_UPGRADE;       break;
                            case 4: screen = GameScreen.ACHIEVEMENTS;       break;
                            case 5: screen = GameScreen.EDIT_FARMER;        break;
                        }
                    }
                }
                break;
            case CHARACTER_CREATION:
                int skinBoxW = 120, skinBoxH = 130;
                int startX   = W/2 - (int)(STARTER_SKINS.length * skinBoxW / 2.0 + (STARTER_SKINS.length - 1) * 10 / 2.0);
                for (int i = 0; i < STARTER_SKINS.length; i++) {
                    int bx = startX + i * (skinBoxW + 10);
                    if (new Rectangle(bx, 265, skinBoxW, skinBoxH).contains(mx, my)) selectedStarterSkin = i;
                }
                if (new Rectangle(W/2 - 130, 490, 260, 50).contains(mx, my) && nameInput.length() > 0) beginGame();
                if (my > 530 && mx < 220) screen = GameScreen.MAIN_MENU;
                break;
            case EDIT_FARMER:
                if (skinRects != null) {
                    for (int i = 0; i < skinRects.length; i++) {
                        if (skinRects[i] != null && skinRects[i].contains(mx, my)) selectedSkinIndex = i;
                    }
                }
                if (saveButton != null && saveButton.contains(mx, my) && nameInput.length() > 0 && selectedSkinIndex >= 0 && playerData.ownsSkin(editableSkins[selectedSkinIndex])) {
                    playerData.equipSkin(editableSkins[selectedSkinIndex]);
                    playerData.setFarmerName(nameInput.toString());
                    screen = GameScreen.MAIN_MENU;
                }
                if (backButton != null && backButton.contains(mx, my)) screen = GameScreen.MAIN_MENU;
                break;
            case PAUSED:
                if (menuButtons == null) return;
                if (menuButtons.length > 0 && menuButtons[0] != null && menuButtons[0].contains(mx, my)) screen = GameScreen.GAME;
                if (menuButtons.length > 1 && menuButtons[1] != null && menuButtons[1].contains(mx, my)) screen = GameScreen.MAIN_MENU;
                break;
            case WARDROBE:
                if (new Rectangle(50, 85, 200, 36).contains(mx, my))  shopTab = 0;
                if (new Rectangle(270, 85, 200, 36).contains(mx, my)) shopTab = 1;
                handleShopClick(mx, my);
                if (menuButtons != null && menuButtons.length > 0 && menuButtons[0] != null && menuButtons[0].contains(mx, my)) screen = GameScreen.MAIN_MENU;
                break;
            case FARM_UPGRADE:
                if (new Rectangle(W/2 - 150, 370, 300, 52).contains(mx, my) && farm.canUpgrade()) {
                    int cost = farm.getUpgradeCost();
                    if (playerData.spendCoins(cost)) {
                        farm.upgrade(); playerData.upgradeFarm();
                        showToast("🏡 Farm upgraded to " + farm.getName() + "!");
                        checkAchievements();
                        playerData.setAchievementData(achievements.saveToString());
                    } else {
                        showToast("Not enough coins! Need 🪙 " + cost);
                    }
                }
                if (menuButtons != null && menuButtons.length > 0 && menuButtons[0] != null && menuButtons[0].contains(mx, my)) screen = GameScreen.MAIN_MENU;
                break;
            case ACHIEVEMENTS:
                if (menuButtons != null && menuButtons.length > 0 && menuButtons[0] != null && menuButtons[0].contains(mx, my)) screen = GameScreen.MAIN_MENU;
                break;
            case GAME_OVER:
                if (menuButtons == null) return;
                if (menuButtons.length > 0 && menuButtons[0] != null && menuButtons[0].contains(mx, my)) startNewGame();
                if (menuButtons.length > 1 && menuButtons[1] != null && menuButtons[1].contains(mx, my)) {
                    if (!pendingLeaderboardName.isEmpty()) {
                        scoreManager.addScore(pendingLeaderboardName, score, level);
                        showToast("Score saved! 🏆");
                        screen = GameScreen.LEADERBOARD;
                    }
                }
                if (menuButtons.length > 2 && menuButtons[2] != null && menuButtons[2].contains(mx, my)) screen = GameScreen.MAIN_MENU;
                break;
            case LEADERBOARD:
                if (menuButtons != null && menuButtons.length > 0 && menuButtons[0] != null && menuButtons[0].contains(mx, my)) screen = GameScreen.MAIN_MENU;
                break;
            case GAME:
                if (mx > ARENA_W) {
                    if (gameButtons != null) {
                        if (gameButtons.length > 0 && gameButtons[0] != null && gameButtons[0].contains(mx, my)) {
                            showToast("🪙 Wallet: " + playerData.getTotalCoins() + " coins");
                        } else if (gameButtons.length > 1 && gameButtons[1] != null && gameButtons[1].contains(mx, my)) {
                            screen = GameScreen.WARDROBE;
                        } else if (gameButtons.length > 2 && gameButtons[2] != null && gameButtons[2].contains(mx, my)) {
                            screen = GameScreen.MAIN_MENU;
                        }
                    }
                    if (new Rectangle(ARENA_W + 15, H - 65, 60, 32).contains(mx, my)) sound.toggleMute();
                    if (new Rectangle(ARENA_W + 85, H - 65, 60, 32).contains(mx, my)) screen = GameScreen.PAUSED;
                }
                break;
        }
    }

    private void handleShopClick(int mx, int my) {
        if (shopTab == 0) {
            SkinType[] skins = SkinType.values();
            int cols = 4, boxW = 130, boxH = 150;
            for (int i = 0; i < skins.length; i++) {
                int bx = 40 + i % cols * (boxW + 15);
                int by = 140 + i / cols * (boxH + 15);
                if (new Rectangle(bx, by, boxW, boxH).contains(mx, my)) {
                    if (playerData.ownsSkin(skins[i])) {
                        playerData.equipSkin(skins[i]);
                        showToast("Equipped " + skins[i].getDisplayName() + "!");
                    } else if (playerData.spendCoins(skins[i].getCost())) {
                        playerData.buySkin(skins[i]);
                        playerData.equipSkin(skins[i]);
                        showToast("Bought & equipped " + skins[i].getDisplayName() + "!");
                    } else {
                        showToast("Not enough coins! Need 🪙 " + skins[i].getCost());
                    }
                }
            }
        } else {
            BasketSkin[] baskets = BasketSkin.values();
            int cols = 5, boxW = 130, boxH = 150;
            for (int i = 0; i < baskets.length; i++) {
                int bx = 40 + i % cols * (boxW + 15);
                int by = 140 + i / cols * (boxH + 15);
                if (new Rectangle(bx, by, boxW, boxH).contains(mx, my)) {
                    if (playerData.ownsBasket(baskets[i])) {
                        playerData.equipBasket(baskets[i]);
                        showToast("Equipped " + baskets[i].getDisplayName() + "!");
                    } else if (playerData.spendCoins(baskets[i].getCost())) {
                        playerData.buyBasket(baskets[i]);
                        playerData.equipBasket(baskets[i]);
                        showToast("Bought & equipped " + baskets[i].getDisplayName() + "!");
                    } else {
                        showToast("Not enough coins! Need 🪙 " + baskets[i].getCost());
                    }
                }
            }
        }
    }

    @Override public void mousePressed(MouseEvent e)  {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e)  {}
    @Override public void mouseExited(MouseEvent e)   {}

    private void beginGame() {
        String farmerName = nameInput.toString().trim();
        if (farmerName.isEmpty()) farmerName = "Farmer";
        playerData.equipSkin(STARTER_SKINS[selectedStarterSkin]);
        character = new Character(0, 0, playerData.getEquippedSkin(), farmerName);
        startNewGame();
    }
}