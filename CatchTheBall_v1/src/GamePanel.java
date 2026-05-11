import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class GamePanel extends JPanel implements Runnable, KeyListener, MouseMotionListener, MouseListener {

    // Screen dimensions
    private static final int W = 900, H = 650;
    private static final int ARENA_W = 650, SIDEBAR_W = 250;

    // Game loop
    private Thread gameThread;
    private volatile boolean running;

    // State
    private GameScreen screen = GameScreen.MAIN_MENU;
    private PlayerData playerData;
    private ScoreManager scoreManager;
    private AchievementManager achievements;
    private SoundManager sound;
    private FarmProgression farm;

    // Game variables
    private int score, level, combo, highestCombo;
    private float comboMultiplier;
    private int timeLeft; // seconds
    private int tickCount;
    private int coinsThisGame;
    private boolean shieldActive, magnetActive, doublePointsActive;
    private int magnetTimer, doubleTimer;
    private int screenShakeTimer;
    private float screenShakeX, screenShakeY;

    // Entities
    private Basket basket;
    private Character character;
    private List<Ball> balls;
    private List<PowerUp> powerUps;
    private List<Particle> particles;
    private List<Cloud> clouds;
    private List<RainDrop> rainDrops;

    // Combo flash
    private String comboFlashText;
    private int comboFlashTimer;

    // Toast notifications
    private String toastText;
    private int toastTimer;

    // Character creation
    private StringBuilder nameInput = new StringBuilder();
    private int selectedStarterSkin = 0;
    private static final SkinType[] STARTER_SKINS = {SkinType.FARMER_MALE, SkinType.FARMER_FEMALE, SkinType.FARM_KID};

    // Menus button areas
    private Rectangle[] menuButtons;
    private int hoveredButton = -1;

    // Wardrobe / Shop
    private int shopTab = 0; // 0=skins, 1=baskets
    private int selectedShopItem = -1;

    // Leaderboard
    private String pendingLeaderboardName = "";

    // Spawn control
    private int spawnTimer = 0;
    private int powerUpSpawnTimer = 0;
    private Random rand = new Random();

    // Level targets
    private static final int[] LEVEL_TARGETS = {
        200, 400, 700, 1100, 1600, 2200, 2900, 3700, 4600, 5600
    };

    // Inner classes for clouds and rain
    private static class Cloud {
        float x, y, speed;
        int w, h;
        float alpha;
        Cloud(int panelW) {
            reset(panelW, true);
        }
        void reset(int panelW, boolean randomX) {
            x = randomX ? (float)(Math.random() * panelW) : -200;
            y = (float)(30 + Math.random() * 100);
            speed = (float)(0.3 + Math.random() * 0.5);
            w = (int)(80 + Math.random() * 120);
            h = (int)(30 + Math.random() * 30);
            alpha = (float)(0.5 + Math.random() * 0.4);
        }
        void update(int panelW) {
            x += speed;
            if (x > panelW + 200) reset(panelW, false);
        }
    }

    private static class RainDrop {
        float x, y, speed, len;
        RainDrop(int w, int h) {
            x = (float)(Math.random() * w);
            y = (float)(Math.random() * h);
            speed = (float)(6 + Math.random() * 4);
            len = (float)(10 + Math.random() * 20);
        }
        void update(int h) {
            y += speed;
            if (y > h) { y = -len; x = (float)(Math.random() * 650); }
        }
    }

    public GamePanel() {
        setPreferredSize(new Dimension(W, H));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);

        playerData = new PlayerData();
        scoreManager = new ScoreManager();
        achievements = new AchievementManager();
        sound = new SoundManager();
        farm = new FarmProgression(playerData.getFarmStage());

        achievements.loadFromString(playerData.getAchievementData());
        initClouds();
    }

    private void initClouds() {
        clouds = new ArrayList<>();
        for (int i = 0; i < 5; i++) clouds.add(new Cloud(ARENA_W));
        rainDrops = new ArrayList<>();
        for (int i = 0; i < 120; i++) rainDrops.add(new RainDrop(ARENA_W, H));
    }

    public void startGameLoop() {
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerTick = 1_000_000_000.0 / 60.0;
        double delta = 0;
        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;
            while (delta >= 1) {
                tick();
                delta--;
            }
            repaint();
            try { Thread.sleep(1); } catch (InterruptedException e) {}
        }
    }

    private void tick() {
        tickCount++;
        for (Cloud c : clouds) c.update(ARENA_W);

        if (screen == GameScreen.GAME) {
            gameUpdate();
        }
        if (toastTimer > 0) toastTimer--;
        if (comboFlashTimer > 0) comboFlashTimer--;
        if (screenShakeTimer > 0) {
            screenShakeTimer--;
            screenShakeX = (float)((rand.nextFloat() - 0.5) * 10 * (screenShakeTimer / 20f));
            screenShakeY = (float)((rand.nextFloat() - 0.5) * 10 * (screenShakeTimer / 20f));
        } else { screenShakeX = 0; screenShakeY = 0; }

        // Poll achievement toasts
        if (toastTimer == 0) {
            Achievement a = achievements.pollToast();
            if (a != null) showToast(a.getEmoji() + " " + a.getName() + " unlocked!");
        }
    }

    private void gameUpdate() {
        // Timer ticks at 60fps
        if (tickCount % 60 == 0 && timeLeft > 0) {
            timeLeft--;
            if (timeLeft == 0) endGame();
        }

        basket.update();
        character.update();
        character.setActive(true);

        // Magnet: pull balls toward basket
        if (magnetActive && magnetTimer > 0) {
            magnetTimer--;
            if (magnetTimer == 0) magnetActive = false;
            for (Ball b : balls) {
                if (!b.isActive()) continue;
                float cx = basket.getX() + basket.getWidth() / 2f;
                float bx = b.getX() + b.getWidth() / 2f;
                float dist = Math.abs(cx - bx);
                if (dist < 200) {
                    b.x += (cx - bx) * 0.06f;
                }
            }
        }

        if (doublePointsActive && doubleTimer > 0) {
            doubleTimer--;
            if (doubleTimer == 0) doublePointsActive = false;
        }

        // Spawn balls
        spawnTimer--;
        if (spawnTimer <= 0) {
            spawnBall();
            int baseInterval = Math.max(20, 60 - level * 4);
            spawnTimer = baseInterval + rand.nextInt(20);
        }

        // Spawn power-ups
        powerUpSpawnTimer--;
        if (powerUpSpawnTimer <= 0) {
            if (rand.nextInt(4) == 0) spawnPowerUp();
            powerUpSpawnTimer = 300 + rand.nextInt(300);
        }

        // Update balls
        Iterator<Ball> bi = balls.iterator();
        while (bi.hasNext()) {
            Ball b = bi.next();
            b.update();
            if (b.getY() > H) {
                if (!b.getType().isBad()) {
                    // missed good fruit
                    if (combo > 0) {
                        combo = 0;
                        comboMultiplier = 1f;
                    }
                }
                bi.remove();
                continue;
            }
            // Collision with basket
            if (b.intersects(basket)) {
                handleCatch(b);
                bi.remove();
            }
        }

        // Update power-ups
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

        // Update particles
        particles.removeIf(Particle::isDead);
        for (Particle p : particles) p.update();

        // Rain
        if (level >= 7) for (RainDrop r : rainDrops) r.update(H);

        // Check level up
        int targetScore = getLevelTarget();
        if (score >= targetScore) {
            level++;
            timeLeft = 180; // 3 min reset
            sound.playLevelUp();
            showToast("🎉 Level Up! Level " + level);
            spawnParticles(ARENA_W / 2, H / 2, new Color(255, 215, 0), 30);
            checkAchievements();
        }
    }

    private int getLevelTarget() {
        int idx = Math.min(level - 1, LEVEL_TARGETS.length - 1);
        return LEVEL_TARGETS[idx];
    }

    private void handleCatch(Ball b) {
        boolean isBad = b.getType().isBad();
        int pts = b.getType().getPoints();

        if (isBad) {
            if (shieldActive) {
                shieldActive = false;
                showToast("🛡️ Shield blocked it!");
                basket.triggerCatch();
                character.triggerCatch();
            } else {
                score = Math.max(0, score + pts);
                screenShakeTimer = 20;
                basket.triggerShake();
                character.triggerShake();
                combo = 0;
                comboMultiplier = 1f;
                sound.playCatch(false);
                addFloatingText((int)(b.getX()), (int)(b.getY()), "" + pts, new Color(255, 80, 80));
            }
        } else {
            combo++;
            float mult = comboMultiplier;
            if (combo >= 11) mult = 5f;
            else if (combo >= 8) mult = 4f;
            else if (combo >= 5) mult = 3f;
            else if (combo >= 3) mult = 2f;
            else mult = 1f;

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
            basket.triggerCatch();
            character.triggerCatch();
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
            case MAGNET: magnetActive = true; magnetTimer = 60 * 5; showToast("🧲 Magnet activated!"); break;
            case TIME_PLUS: timeLeft += 15; showToast("⏰ +15 seconds!"); break;
            case SHIELD: shieldActive = true; showToast("🛡️ Shield ready!"); break;
            case DOUBLE_POINTS: doublePointsActive = true; doubleTimer = 60 * 8; showToast("2️⃣ Double Points!"); break;
        }
    }

    private void spawnBall() {
        float x = 30 + rand.nextFloat() * (ARENA_W - 70);
        float speed = 1.5f + level * 0.3f + rand.nextFloat() * 1.5f;
        speed = Math.min(speed, 8f);

        BallType type;
        int r = rand.nextInt(100);
        if (r < 5) type = BallType.STRAWBERRY;
        else if (r < 15) type = BallType.MUSHROOM;
        else if (r < 25) type = BallType.EGGPLANT;
        else if (r < 60) type = BallType.APPLE;
        else type = BallType.ORANGE;

        balls.add(new Ball(x, -40, type, speed));
    }

    private void spawnPowerUp() {
        float x = 30 + rand.nextFloat() * (ARENA_W - 70);
        PowerUp.PowerUpType[] types = PowerUp.PowerUpType.values();
        PowerUp.PowerUpType t = types[rand.nextInt(types.length)];
        powerUps.add(new PowerUp(x, -40, t));
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
        // set color on last particle
        // (text particle uses white by default - override color via subclass isn't needed since we can just tint)
    }

    private void showToast(String text) {
        toastText = text;
        toastTimer = 180;
    }

    private void showComboFlash(String text) {
        comboFlashText = text;
        comboFlashTimer = 90;
    }

    private void startNewGame() {
        score = 0; level = 1; combo = 0; comboMultiplier = 1f;
        timeLeft = 180; tickCount = 0; coinsThisGame = 0;
        highestCombo = 0;
        shieldActive = false; magnetActive = false; doublePointsActive = false;
        magnetTimer = 0; doubleTimer = 0;
        screenShakeTimer = 0;

        balls = new ArrayList<>();
        powerUps = new ArrayList<>();
        particles = new ArrayList<>();
        spawnTimer = 60;
        powerUpSpawnTimer = 300;

        basket = new Basket(ARENA_W / 2f - 40, H - 110, playerData.getEquippedBasket());
        String name = character != null ? character.getFarmerName() : "Farmer";
        character = new Character(ARENA_W / 2f - 25, H - 160, playerData.getEquippedSkin(), name);
        farm = new FarmProgression(playerData.getFarmStage());

        screen = GameScreen.GAME;
    }

    private void endGame() {
        screen = GameScreen.GAME_OVER;
        pendingLeaderboardName = character != null ? character.getFarmerName() : "Farmer";
    }

    // ========== PAINTING ===========

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        switch (screen) {
            case MAIN_MENU: drawMainMenu(g); break;
            case CHARACTER_CREATION: drawCharCreation(g); break;
            case GAME: drawGame(g); break;
            case PAUSED: drawGame(g); drawPause(g); break;
            case WARDROBE: drawWardrobe(g); break;
            case FARM_UPGRADE: drawFarmUpgrade(g); break;
            case ACHIEVEMENTS: drawAchievements(g); break;
            case GAME_OVER: drawGameOver(g); break;
            case LEADERBOARD: drawLeaderboard(g); break;
        }

        // Toast
        if (toastTimer > 0) drawToast(g);
    }

    private void drawSkyBackground(Graphics2D g, int lv) {
        Color top, bottom;
        if (lv <= 3) { top = new Color(120, 180, 255); bottom = new Color(200, 230, 255); }
        else if (lv <= 6) { top = new Color(255, 200, 80); bottom = new Color(255, 230, 150); }
        else if (lv <= 9) { top = new Color(220, 100, 50); bottom = new Color(255, 170, 80); }
        else { top = new Color(15, 20, 50); bottom = new Color(40, 50, 90); }

        GradientPaint sky = new GradientPaint(0, 0, top, 0, H - 80, bottom);
        g.setPaint(sky);
        g.fillRect(0, 0, ARENA_W, H);

        // Clouds
        for (Cloud c : clouds) {
            g.setColor(new Color(255, 255, 255, (int)(c.alpha * 200)));
            g.fillOval((int)c.x, (int)c.y, c.w, c.h);
            g.fillOval((int)(c.x + c.w * 0.2), (int)(c.y - c.h * 0.3), (int)(c.w * 0.7), (int)(c.h * 0.9));
            g.fillOval((int)(c.x + c.w * 0.5), (int)(c.y - c.h * 0.1), (int)(c.w * 0.5), (int)(c.h * 0.7));
        }

        // Stars at night
        if (lv >= 10) {
            g.setColor(new Color(255, 255, 200, 180));
            Random sr = new Random(42);
            for (int i = 0; i < 50; i++) {
                int sx = sr.nextInt(ARENA_W);
                int sy = sr.nextInt(H / 2);
                int ss = 1 + sr.nextInt(2);
                g.fillOval(sx, sy, ss, ss);
            }
        }

        // Ground
        GradientPaint grass = new GradientPaint(0, H - 80, new Color(70, 160, 60), 0, H, new Color(40, 120, 30));
        g.setPaint(grass);
        g.fillRect(0, H - 80, ARENA_W, 80);

        // Ground details
        g.setColor(new Color(55, 140, 45));
        for (int i = 0; i < ARENA_W; i += 30) {
            int gy = H - 80;
            g.fillArc(i, gy - 5, 20, 12, 0, 180);
        }

        // Rain
        if (lv >= 7) {
            g.setColor(new Color(150, 200, 255, 100));
            g.setStroke(new BasicStroke(1f));
            for (RainDrop r : rainDrops) {
                g.drawLine((int)r.x, (int)r.y, (int)(r.x - 2), (int)(r.y + r.len));
            }
            g.setStroke(new BasicStroke(1f));
        }
    }

    private void drawGame(Graphics2D g) {
        // Screen shake offset
        g.translate((int)screenShakeX, (int)screenShakeY);

        // Background
        drawSkyBackground(g, level);

        // Entities
        for (Ball b : balls) b.draw(g);
        for (PowerUp p : powerUps) p.draw(g);
        for (Particle p : particles) p.draw(g);
        basket.draw(g);
        character.draw(g);

        // Combo flash
        if (comboFlashTimer > 0) {
            float alpha = Math.min(1f, comboFlashTimer / 30f);
            float scale = 1f + (1f - comboFlashTimer / 90f) * 0.5f;
            g.setFont(new Font("Arial Black", Font.BOLD, (int)(28 * scale)));
            FontMetrics fm = g.getFontMetrics();
            int tw = fm.stringWidth(comboFlashText);
            int tx = (ARENA_W - tw) / 2;
            g.setColor(new Color(255, 220, 50, (int)(alpha * 230)));
            g.drawString(comboFlashText, tx, H / 2 - 30);
        }

        // Reset translate
        g.translate(-(int)screenShakeX, -(int)screenShakeY);

        // Sidebar
        drawSidebar(g);
    }

    private void drawSidebar(Graphics2D g) {
        int sx = ARENA_W;
        // Background
        GradientPaint sbg = new GradientPaint(sx, 0, new Color(40, 70, 30), sx, H, new Color(25, 50, 20));
        g.setPaint(sbg);
        g.fillRect(sx, 0, SIDEBAR_W, H);
        g.setColor(new Color(80, 140, 60));
        g.drawLine(sx, 0, sx, H);

        int py = 20;
        Font titleFont = new Font("Arial Black", Font.PLAIN, 14);
        Font valFont = new Font("Arial", Font.BOLD, 22);
        Font smallFont = new Font("Arial", Font.PLAIN, 11);

        // Level
        drawSidebarItem(g, sx + 10, py, "LEVEL", "" + level, new Color(255, 220, 80), titleFont, valFont); py += 60;
        // Score
        drawSidebarItem(g, sx + 10, py, "SCORE", "" + score, new Color(100, 220, 255), titleFont, valFont); py += 60;
        // Target
        drawSidebarItem(g, sx + 10, py, "TARGET", "" + getLevelTarget(), new Color(200, 200, 200), titleFont, new Font("Arial", Font.BOLD, 16)); py += 50;

        // Timer
        boolean pulse = timeLeft <= 30 && tickCount % 30 < 15;
        Color timerColor = timeLeft <= 30 ? new Color(255, 80, 80) : new Color(100, 255, 150);
        if (pulse) {
            g.setColor(new Color(255, 50, 50, 60));
            g.fillRoundRect(sx + 5, py - 5, SIDEBAR_W - 10, 55, 10, 10);
        }
        String mins = "" + (timeLeft / 60);
        String secs = String.format("%02d", timeLeft % 60);
        drawSidebarItem(g, sx + 10, py, "TIME", mins + ":" + secs, timerColor, titleFont, valFont); py += 65;

        // Coins
        drawSidebarItem(g, sx + 10, py, "COINS 🪙", "" + playerData.getTotalCoins(), new Color(255, 215, 50), titleFont, new Font("Arial", Font.BOLD, 18)); py += 55;

        // Combo
        if (combo > 0) {
            String comboStr = "x" + (int)comboMultiplier;
            Color cc = combo >= 8 ? new Color(255, 100, 255) : combo >= 5 ? new Color(255, 180, 50) : new Color(100, 255, 200);
            drawSidebarItem(g, sx + 10, py, "COMBO " + combo, comboStr, cc, titleFont, valFont);
        }
        py += 60;

        // Power-up indicators
        int pip = sx + 10;
        if (shieldActive) { g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20)); g.setColor(Color.WHITE); g.drawString("🛡️", pip, py); pip += 30; }
        if (magnetActive) { g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20)); g.setColor(Color.WHITE); g.drawString("🧲", pip, py); pip += 30; }
        if (doublePointsActive) { g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20)); g.setColor(Color.WHITE); g.drawString("2️⃣", pip, py); pip += 30; }
        py += 30;

        // Progress bar to next level
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRoundRect(sx + 10, py, SIDEBAR_W - 20, 14, 7, 7);
        float prog = Math.min(1f, (float)score / getLevelTarget());
        g.setColor(new Color(100, 220, 100));
        g.fillRoundRect(sx + 10, py, (int)((SIDEBAR_W - 20) * prog), 14, 7, 7);
        g.setColor(Color.WHITE);
        g.setFont(smallFont);
        g.drawString("Level Progress", sx + 10, py - 3);
        py += 30;

        // Farm sidebar
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        FontMetrics fm = g.getFontMetrics();
        String farmEmoji = farm.getEmoji();
        int fw = fm.stringWidth(farmEmoji);
        g.drawString(farmEmoji, sx + (SIDEBAR_W - fw) / 2, py + 36);
        g.setFont(smallFont);
        g.setColor(new Color(180, 220, 150));
        FontMetrics fm2 = g.getFontMetrics();
        String fn = farm.getName();
        g.drawString(fn, sx + (SIDEBAR_W - fm2.stringWidth(fn)) / 2, py + 55);
        py += 70;

        // Mute and Pause buttons
        drawIconButton(g, sx + 15, py, sound.isMuted() ? "🔇" : "🔊", "M", 60, 32);
        drawIconButton(g, sx + 85, py, "⏸", "P", 60, 32);
    }

    private void drawSidebarItem(Graphics2D g, int x, int y, String label, String value, Color valueColor, Font labelFont, Font valFont) {
        g.setFont(labelFont);
        g.setColor(new Color(160, 190, 140));
        g.drawString(label, x, y + 13);
        g.setFont(valFont);
        g.setColor(valueColor);
        g.drawString(value, x, y + 38);
    }

    private void drawIconButton(Graphics2D g, int x, int y, String emoji, String key, int w, int h) {
        g.setColor(new Color(60, 100, 50));
        g.fillRoundRect(x, y, w, h, 8, 8);
        g.setColor(new Color(100, 160, 80));
        g.drawRoundRect(x, y, w, h, 8, 8);
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        g.setColor(Color.WHITE);
        g.drawString(emoji, x + 6, y + 20);
        g.setFont(new Font("Arial", Font.BOLD, 10));
        g.setColor(new Color(180, 220, 150));
        g.drawString("[" + key + "]", x + 28, y + 20);
    }

    private void drawMainMenu(Graphics2D g) {
        drawSkyBackground(g, 1);
        g.setColor(new Color(0, 0, 0, 80));
        g.fillRect(0, 0, W, H);

        // Title
        g.setFont(new Font("Arial Black", Font.BOLD, 52));
        String title = "🌾 Catch the Ball!";
        FontMetrics fm = g.getFontMetrics();
        // Drop shadow
        g.setColor(new Color(30, 80, 20, 180));
        g.drawString(title, (W - fm.stringWidth(title)) / 2 + 3, 133);
        GradientPaint titleGrad = new GradientPaint(0, 80, new Color(255, 230, 80), 0, 140, new Color(200, 120, 20));
        g.setPaint(titleGrad);
        g.drawString(title, (W - fm.stringWidth(title)) / 2, 130);

        g.setFont(new Font("Arial", Font.ITALIC, 18));
        g.setColor(new Color(220, 255, 180));
        String sub = "A farm-fresh catching adventure!";
        FontMetrics fm2 = g.getFontMetrics();
        g.drawString(sub, (W - fm2.stringWidth(sub)) / 2, 165);

        // Buttons
        String[] labels = {"▶  Play Game", "🏆  Leaderboard", "🧺  Wardrobe", "🏡  Farm Upgrade", "🏅  Achievements"};
        menuButtons = new Rectangle[labels.length];
        int bw = 260, bh = 48, bx = (W - bw) / 2, by = 210;
        for (int i = 0; i < labels.length; i++) {
            menuButtons[i] = new Rectangle(bx, by + i * 62, bw, bh);
            drawMenuButton(g, menuButtons[i], labels[i], i == hoveredButton);
        }

        // Player coins
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.setColor(new Color(255, 215, 50));
        g.drawString("🪙 " + playerData.getTotalCoins() + " coins", 20, H - 20);

        // Farm info
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        g.drawString(farm.getEmoji(), W - 120, H - 30);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.setColor(new Color(200, 230, 160));
        g.drawString(farm.getName(), W - 105, H - 15);
    }

    private void drawMenuButton(Graphics2D g, Rectangle r, String label, boolean hovered) {
        Color bg = hovered ? new Color(80, 160, 60) : new Color(50, 110, 40);
        Color border = hovered ? new Color(150, 220, 100) : new Color(90, 160, 70);
        GradientPaint bgGrad = new GradientPaint(r.x, r.y, bg.brighter(), r.x, r.y + r.height, bg.darker());
        g.setPaint(bgGrad);
        g.fillRoundRect(r.x, r.y, r.width, r.height, 14, 14);
        g.setColor(border);
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(r.x, r.y, r.width, r.height, 14, 14);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("Arial Black", Font.PLAIN, 16));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, r.x + (r.width - fm.stringWidth(label)) / 2, r.y + r.height / 2 + 6);
    }

    private void drawCharCreation(Graphics2D g) {
        drawSkyBackground(g, 1);
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, W, H);

        // Panel
        g.setColor(new Color(30, 70, 20, 220));
        g.fillRoundRect(W/2 - 280, 60, 560, 520, 20, 20);
        g.setColor(new Color(80, 160, 60));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(W/2 - 280, 60, 560, 520, 20, 20);
        g.setStroke(new BasicStroke(1f));

        g.setFont(new Font("Arial Black", Font.BOLD, 28));
        g.setColor(new Color(255, 230, 80));
        String t = "🌾 Create Your Farmer";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(t, (W - fm.stringWidth(t)) / 2, 115);

        // Name input
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(new Color(200, 230, 160));
        g.drawString("Farmer Name:", W/2 - 200, 165);

        g.setColor(new Color(50, 100, 40));
        g.fillRoundRect(W/2 - 200, 175, 400, 40, 8, 8);
        g.setColor(new Color(100, 200, 80));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(W/2 - 200, 175, 400, 40, 8, 8);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.setColor(Color.WHITE);
        String displayName = nameInput.toString() + (tickCount % 60 < 30 ? "|" : "");
        g.drawString(displayName, W/2 - 188, 203);

        // Skin selection
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(new Color(200, 230, 160));
        g.drawString("Choose Your Starter:", W/2 - 200, 250);

        int skinBoxW = 120, skinBoxH = 130;
        int startX = W/2 - (int)(STARTER_SKINS.length * skinBoxW / 2.0 + (STARTER_SKINS.length-1) * 10 / 2.0);
        for (int i = 0; i < STARTER_SKINS.length; i++) {
            int bx = startX + i * (skinBoxW + 10);
            boolean sel = i == selectedStarterSkin;
            g.setColor(sel ? new Color(80, 160, 60, 200) : new Color(40, 80, 30, 180));
            g.fillRoundRect(bx, 265, skinBoxW, skinBoxH, 12, 12);
            g.setColor(sel ? new Color(150, 230, 100) : new Color(70, 130, 60));
            g.setStroke(new BasicStroke(sel ? 3f : 1.5f));
            g.drawRoundRect(bx, 265, skinBoxW, skinBoxH, 12, 12);
            g.setStroke(new BasicStroke(1f));

            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
            FontMetrics emf = g.getFontMetrics();
            String emoji = STARTER_SKINS[i].getEmoji();
            g.drawString(emoji, bx + (skinBoxW - emf.stringWidth(emoji)) / 2, 325);
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.setColor(new Color(200, 240, 170));
            FontMetrics sfm = g.getFontMetrics();
            String sn = STARTER_SKINS[i].getDisplayName();
            g.drawString(sn, bx + (skinBoxW - sfm.stringWidth(sn)) / 2, 360);
        }

        // All skins preview
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(new Color(180, 210, 140));
        g.drawString("All Farmer Skins (buyable in Wardrobe):", W/2 - 220, 425);
        int px2 = W/2 - 200;
        for (SkinType s : SkinType.values()) {
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
            g.drawString(s.getEmoji(), px2, 460);
            px2 += 40;
        }

        // Start button
        boolean canStart = nameInput.length() > 0;
        g.setColor(canStart ? new Color(80, 180, 60) : new Color(60, 80, 50));
        g.fillRoundRect(W/2 - 130, 490, 260, 50, 12, 12);
        g.setColor(canStart ? new Color(130, 230, 100) : new Color(80, 120, 70));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(W/2 - 130, 490, 260, 50, 12, 12);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("Arial Black", Font.BOLD, 18));
        g.setColor(canStart ? Color.WHITE : new Color(120, 150, 110));
        String btnText = "Start Farming! 🌾";
        FontMetrics bfm = g.getFontMetrics();
        g.drawString(btnText, W/2 - bfm.stringWidth(btnText)/2, 523);

        // Back
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(new Color(180, 220, 150));
        g.drawString("← Back to Menu", W/2 - 280 + 15, 555);
    }

    private void drawPause(Graphics2D g) {
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, W, H);

        int pw = 400, ph = 280, px = (W - pw) / 2, py = (H - ph) / 2;
        g.setColor(new Color(30, 60, 25, 240));
        g.fillRoundRect(px, py, pw, ph, 20, 20);
        g.setColor(new Color(100, 180, 70));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(px, py, pw, ph, 20, 20);
        g.setStroke(new BasicStroke(1f));

        g.setFont(new Font("Arial Black", Font.BOLD, 32));
        g.setColor(new Color(255, 220, 80));
        g.drawString("⏸ PAUSED", px + 100, py + 55);

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, px + 30, py + 100);
        g.drawString("Level: " + level, px + 180, py + 100);
        g.drawString("Combo: " + combo, px + 30, py + 130);
        g.drawString("Coins: " + playerData.getTotalCoins(), px + 180, py + 130);

        menuButtons = new Rectangle[2];
        menuButtons[0] = new Rectangle(px + 30, py + 165, 160, 44);
        menuButtons[1] = new Rectangle(px + 210, py + 165, 160, 44);
        drawMenuButton(g, menuButtons[0], "▶ Resume", hoveredButton == 0);
        drawMenuButton(g, menuButtons[1], "🏠 Menu", hoveredButton == 1);

        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(new Color(160, 200, 140));
        g.drawString("Press P to resume • ESC for menu", px + 70, py + 250);
    }

    private void drawWardrobe(Graphics2D g) {
        drawBackground(g, new Color(25, 50, 20), new Color(15, 35, 12));
        drawScreenTitle(g, "🧺 Wardrobe & Shop");

        // Tabs
        String[] tabs = {"👨‍🌾 Farmer Skins", "🧺 Basket Skins"};
        for (int i = 0; i < 2; i++) {
            boolean sel = shopTab == i;
            g.setColor(sel ? new Color(80, 160, 60) : new Color(40, 80, 30));
            g.fillRoundRect(50 + i * 220, 85, 200, 36, 10, 10);
            g.setColor(sel ? new Color(150, 230, 100) : new Color(70, 130, 60));
            g.drawRoundRect(50 + i * 220, 85, 200, 36, 10, 10);
            g.setFont(new Font("Arial Black", Font.PLAIN, 13));
            g.setColor(sel ? Color.WHITE : new Color(160, 200, 140));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(tabs[i], 50 + i * 220 + (200 - fm.stringWidth(tabs[i])) / 2, 110);
        }

        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(new Color(255, 215, 50));
        g.drawString("🪙 " + playerData.getTotalCoins() + " coins", W - 200, 100);

        if (shopTab == 0) {
            // Character skins
            SkinType[] skins = SkinType.values();
            int cols = 4, boxW = 130, boxH = 150;
            for (int i = 0; i < skins.length; i++) {
                int col = i % cols, row = i / cols;
                int bx = 40 + col * (boxW + 15);
                int by = 140 + row * (boxH + 15);
                boolean owned = playerData.ownsSkin(skins[i]);
                boolean equipped = playerData.getEquippedSkin() == skins[i];
                drawShopItem(g, bx, by, boxW, boxH, skins[i].getEmoji(), skins[i].getDisplayName(), skins[i].getCost(), owned, equipped);
            }
        } else {
            BasketSkin[] baskets = BasketSkin.values();
            int cols = 5, boxW = 130, boxH = 150;
            for (int i = 0; i < baskets.length; i++) {
                int bx = 40 + i % cols * (boxW + 15);
                int by = 140 + i / cols * (boxH + 15);
                boolean owned = playerData.ownsBasket(baskets[i]);
                boolean equipped = playerData.getEquippedBasket() == baskets[i];
                drawShopItem(g, bx, by, boxW, boxH, baskets[i].getEmoji(), baskets[i].getDisplayName(), baskets[i].getCost(), owned, equipped);
            }
        }

        drawBackButton(g);
    }

    private void drawShopItem(Graphics2D g, int x, int y, int w, int h, String emoji, String name, int cost, boolean owned, boolean equipped) {
        Color bg = equipped ? new Color(60, 140, 50) : owned ? new Color(40, 90, 30) : new Color(30, 60, 25);
        g.setColor(bg);
        g.fillRoundRect(x, y, w, h, 12, 12);
        Color border = equipped ? new Color(150, 240, 100) : owned ? new Color(80, 160, 60) : new Color(60, 100, 50);
        g.setColor(border);
        g.setStroke(new BasicStroke(equipped ? 3f : 1.5f));
        g.drawRoundRect(x, y, w, h, 12, 12);
        g.setStroke(new BasicStroke(1f));

        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(emoji, x + (w - fm.stringWidth(emoji)) / 2, y + 55);

        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.setColor(new Color(200, 230, 170));
        FontMetrics fm2 = g.getFontMetrics();
        g.drawString(name, x + (w - fm2.stringWidth(name)) / 2, y + 75);

        if (equipped) {
            g.setColor(new Color(150, 255, 100));
            g.setFont(new Font("Arial Black", Font.PLAIN, 11));
            g.drawString("EQUIPPED", x + 20, y + 100);
        } else if (owned) {
            g.setColor(new Color(100, 200, 80));
            g.setFont(new Font("Arial", Font.BOLD, 11));
            g.drawString("OWNED - Equip", x + 12, y + 100);
        } else {
            // Buy button
            g.setColor(new Color(60, 120, 50));
            g.fillRoundRect(x + 10, y + 88, w - 20, 28, 8, 8);
            g.setColor(new Color(100, 180, 80));
            g.drawRoundRect(x + 10, y + 88, w - 20, 28, 8, 8);
            g.setFont(new Font("Arial Black", Font.PLAIN, 11));
            g.setColor(new Color(255, 215, 50));
            String costStr = "🪙 " + cost;
            FontMetrics fm3 = g.getFontMetrics();
            g.drawString(costStr, x + 10 + (w - 20 - fm3.stringWidth(costStr)) / 2, y + 108);
        }
    }

    private void drawFarmUpgrade(Graphics2D g) {
        drawBackground(g, new Color(30, 60, 20), new Color(15, 40, 10));
        drawScreenTitle(g, "🏡 Farm Upgrade");

        // Current farm display
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        FontMetrics fm = g.getFontMetrics();
        String farmEmoji = farm.getEmoji();
        g.drawString(farmEmoji, (W - fm.stringWidth(farmEmoji)) / 2, 220);

        g.setFont(new Font("Arial Black", Font.BOLD, 24));
        g.setColor(new Color(255, 220, 100));
        String fn = farm.getName() + " (Stage " + farm.getStage() + "/20)";
        FontMetrics fm2 = g.getFontMetrics();
        g.drawString(fn, (W - fm2.stringWidth(fn)) / 2, 270);

        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(new Color(200, 230, 160));
        g.drawString("🪙 Coins: " + playerData.getTotalCoins(), W/2 - 80, 310);

        if (farm.canUpgrade()) {
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.setColor(new Color(180, 220, 150));
            String nextName = new FarmProgression(farm.getStage() + 1).getName();
            g.drawString("Next: " + nextName, W/2 - 70, 345);

            boolean canAfford = playerData.getTotalCoins() >= farm.getUpgradeCost();
            g.setColor(canAfford ? new Color(80, 180, 60) : new Color(80, 60, 60));
            g.fillRoundRect(W/2 - 150, 370, 300, 52, 14, 14);
            g.setColor(canAfford ? new Color(150, 230, 100) : new Color(150, 80, 80));
            g.setStroke(new BasicStroke(2f));
            g.drawRoundRect(W/2 - 150, 370, 300, 52, 14, 14);
            g.setStroke(new BasicStroke(1f));
            g.setFont(new Font("Arial Black", Font.BOLD, 16));
            g.setColor(Color.WHITE);
            String upgStr = "Upgrade for 🪙 " + farm.getUpgradeCost();
            FontMetrics fm3 = g.getFontMetrics();
            g.drawString(upgStr, W/2 - fm3.stringWidth(upgStr)/2, 403);
        } else {
            g.setFont(new Font("Arial Black", Font.BOLD, 22));
            g.setColor(new Color(255, 215, 50));
            g.drawString("👑 MAX LEVEL FARM!", W/2 - 140, 380);
        }

        // All stages mini preview
        g.setFont(new Font("Arial", Font.BOLD, 13));
        g.setColor(new Color(160, 200, 140));
        g.drawString("Farm Stages:", 30, 460);
        FarmProgression fp = new FarmProgression(1);
        int pfx = 30;
        for (int i = 1; i <= 20; i++) {
            fp = new FarmProgression(i);
            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            Color stageColor = i <= farm.getStage() ? new Color(255, 255, 255) : new Color(100, 100, 100, 150);
            g.setColor(stageColor);
            g.drawString(fp.getEmoji(), pfx, 490);
            pfx += 42;
            if (pfx > W - 50) { pfx = 30; }
        }

        drawBackButton(g);
    }

    private void drawAchievements(Graphics2D g) {
        drawBackground(g, new Color(25, 45, 20), new Color(15, 30, 12));
        drawScreenTitle(g, "🏅 Achievements");

        List<Achievement> all = achievements.getAll();
        int cols = 3, boxW = 230, boxH = 100, startX = 40, startY = 110;
        for (int i = 0; i < all.size(); i++) {
            Achievement a = all.get(i);
            int col = i % cols, row = i / cols;
            int bx = startX + col * (boxW + 20);
            int by = startY + row * (boxH + 15);
            boolean unlocked = a.isUnlocked();
            g.setColor(unlocked ? new Color(50, 100, 35) : new Color(30, 50, 25));
            g.fillRoundRect(bx, by, boxW, boxH, 12, 12);
            g.setColor(unlocked ? new Color(120, 220, 80) : new Color(60, 90, 50));
            g.setStroke(new BasicStroke(unlocked ? 2.5f : 1f));
            g.drawRoundRect(bx, by, boxW, boxH, 12, 12);
            g.setStroke(new BasicStroke(1f));

            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
            g.setColor(unlocked ? Color.WHITE : new Color(100, 120, 90));
            g.drawString(a.getEmoji(), bx + 12, by + 42);

            g.setFont(new Font("Arial Black", Font.PLAIN, 13));
            g.setColor(unlocked ? new Color(200, 255, 150) : new Color(100, 130, 90));
            g.drawString(a.getName(), bx + 55, by + 35);

            g.setFont(new Font("Arial", Font.PLAIN, 11));
            g.setColor(unlocked ? new Color(160, 210, 130) : new Color(80, 110, 70));
            g.drawString(a.getDescription(), bx + 55, by + 55);

            if (unlocked) {
                g.setFont(new Font("Arial", Font.BOLD, 11));
                g.setColor(new Color(100, 220, 80));
                g.drawString("✓ UNLOCKED", bx + 55, by + 78);
            }
        }

        drawBackButton(g);
    }

    private void drawGameOver(Graphics2D g) {
        drawBackground(g, new Color(20, 40, 15), new Color(10, 25, 8));
        drawSkyBackground(g, level);
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRect(0, 0, W, H);

        int pw = 500, ph = 440, px = (W - pw) / 2, py = 60;
        g.setColor(new Color(25, 55, 20, 240));
        g.fillRoundRect(px, py, pw, ph, 20, 20);
        g.setColor(new Color(100, 200, 70));
        g.setStroke(new BasicStroke(2.5f));
        g.drawRoundRect(px, py, pw, ph, 20, 20);
        g.setStroke(new BasicStroke(1f));

        g.setFont(new Font("Arial Black", Font.BOLD, 36));
        g.setColor(new Color(255, 215, 50));
        String t = "🌾 Game Over!";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(t, px + (pw - fm.stringWidth(t)) / 2, py + 60);

        g.setFont(new Font("Arial", Font.BOLD, 17));
        g.setColor(Color.WHITE);
        int statY = py + 100;
        drawStatLine(g, px + 30, statY, "Final Score", "" + score); statY += 35;
        drawStatLine(g, px + 30, statY, "Level Reached", "" + level); statY += 35;
        drawStatLine(g, px + 30, statY, "Best Combo", "" + highestCombo); statY += 35;
        drawStatLine(g, px + 30, statY, "Coins Earned", "+" + coinsThisGame + " 🪙"); statY += 35;

        // Leaderboard name input
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.setColor(new Color(200, 230, 160));
        g.drawString("Enter name for leaderboard:", px + 30, statY + 10); statY += 25;
        g.setColor(new Color(40, 90, 30));
        g.fillRoundRect(px + 30, statY, pw - 60, 38, 8, 8);
        g.setColor(new Color(100, 200, 80));
        g.setStroke(new BasicStroke(2f));
        g.drawRoundRect(px + 30, statY, pw - 60, 38, 8, 8);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.setColor(Color.WHITE);
        g.drawString(pendingLeaderboardName + (tickCount % 60 < 30 ? "|" : ""), px + 42, statY + 25);
        statY += 55;

        menuButtons = new Rectangle[3];
        menuButtons[0] = new Rectangle(px + 25, statY, 140, 46);
        menuButtons[1] = new Rectangle(px + 185, statY, 140, 46);
        menuButtons[2] = new Rectangle(px + 345, statY, 130, 46);
        drawMenuButton(g, menuButtons[0], "🔄 Retry", hoveredButton == 0);
        drawMenuButton(g, menuButtons[1], "🏆 Save Score", hoveredButton == 1);
        drawMenuButton(g, menuButtons[2], "🏠 Menu", hoveredButton == 2);
    }

    private void drawStatLine(Graphics2D g, int x, int y, String label, String val) {
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.setColor(new Color(160, 200, 140));
        g.drawString(label + ":", x, y);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        g.drawString(val, x + 200, y);
    }

    private void drawLeaderboard(Graphics2D g) {
        drawBackground(g, new Color(20, 45, 15), new Color(10, 30, 8));
        drawScreenTitle(g, "🏆 Leaderboard");

        List<ScoreEntry> top = scoreManager.getTop10();
        String[] ranks = {"👑","🥈","🥉","4️⃣","5️⃣","6️⃣","7️⃣","8️⃣","9️⃣","🔟"};
        int rowH = 44, startY = 110;

        if (top.isEmpty()) {
            g.setFont(new Font("Arial", Font.ITALIC, 18));
            g.setColor(new Color(160, 200, 140));
            g.drawString("No scores yet. Play to get on the board!", W/2 - 200, 250);
        }

        for (int i = 0; i < top.size(); i++) {
            ScoreEntry e = top.get(i);
            int ry = startY + i * rowH;
            boolean isTop3 = i < 3;
            g.setColor(isTop3 ? new Color(60, 120, 40, 200) : new Color(35, 75, 25, 160));
            g.fillRoundRect(30, ry, W - 60, rowH - 6, 10, 10);
            g.setColor(isTop3 ? new Color(150, 230, 100) : new Color(80, 150, 60));
            g.drawRoundRect(30, ry, W - 60, rowH - 6, 10, 10);

            g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
            g.setColor(Color.WHITE);
            g.drawString(ranks[i], 45, ry + 26);

            g.setFont(new Font("Arial Black", Font.PLAIN, 15));
            g.setColor(isTop3 ? new Color(255, 230, 100) : Color.WHITE);
            g.drawString(e.getName(), 100, ry + 27);

            g.setFont(new Font("Arial", Font.BOLD, 15));
            g.setColor(new Color(100, 220, 100));
            g.drawString("" + e.getScore(), 380, ry + 27);

            g.setFont(new Font("Arial", Font.PLAIN, 13));
            g.setColor(new Color(160, 200, 140));
            g.drawString("Lv." + e.getLevel(), 520, ry + 27);
        }

        drawBackButton(g);
    }

    private void drawToast(Graphics2D g) {
        float alpha = Math.min(1f, toastTimer / 30f);
        int tw2 = 340, th = 40;
        int tx = (W - tw2) / 2, ty = 30;
        g.setColor(new Color(30, 80, 20, (int)(alpha * 220)));
        g.fillRoundRect(tx, ty, tw2, th, 12, 12);
        g.setColor(new Color(120, 210, 80, (int)(alpha * 255)));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(tx, ty, tw2, th, 12, 12);
        g.setStroke(new BasicStroke(1f));
        g.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        g.setColor(new Color(255, 255, 255, (int)(alpha * 255)));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(toastText, tx + (tw2 - fm.stringWidth(toastText)) / 2, ty + 26);
    }

    private void drawBackground(Graphics2D g, Color top, Color bot) {
        GradientPaint bg = new GradientPaint(0, 0, top, 0, H, bot);
        g.setPaint(bg);
        g.fillRect(0, 0, W, H);
    }

    private void drawScreenTitle(Graphics2D g, String title) {
        g.setFont(new Font("Arial Black", Font.BOLD, 28));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(new Color(50, 110, 35, 200));
        g.fillRect(0, 0, W, 75);
        g.setColor(new Color(255, 220, 80));
        g.drawString(title, (W - fm.stringWidth(title)) / 2, 52);
    }

    private void drawBackButton(Graphics2D g) {
        menuButtons = new Rectangle[1];
        menuButtons[0] = new Rectangle(20, H - 55, 140, 38);
        drawMenuButton(g, menuButtons[0], "← Back", hoveredButton == 0);
    }

    // ========== INPUT ===========

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (screen == GameScreen.GAME) {
            if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_A) {
                basket.setTargetX(Math.max(40, basket.getX() + basket.getWidth() / 2f - 30));
            } else if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) {
                basket.setTargetX(Math.min(ARENA_W - 40, basket.getX() + basket.getWidth() / 2f + 30));
            } else if (code == KeyEvent.VK_P) {
                screen = GameScreen.PAUSED;
            } else if (code == KeyEvent.VK_ESCAPE) {
                screen = GameScreen.MAIN_MENU;
            } else if (code == KeyEvent.VK_M) {
                sound.toggleMute();
            }
        } else if (screen == GameScreen.PAUSED) {
            if (code == KeyEvent.VK_P) screen = GameScreen.GAME;
            else if (code == KeyEvent.VK_ESCAPE) screen = GameScreen.MAIN_MENU;
        } else if (screen == GameScreen.CHARACTER_CREATION) {
            if (code == KeyEvent.VK_BACK_SPACE && nameInput.length() > 0) {
                nameInput.deleteCharAt(nameInput.length() - 1);
            } else if (code == KeyEvent.VK_ENTER && nameInput.length() > 0) {
                beginGame();
            } else if (code == KeyEvent.VK_ESCAPE) {
                screen = GameScreen.MAIN_MENU;
            }
        } else if (screen == GameScreen.GAME_OVER) {
            if (code == KeyEvent.VK_BACK_SPACE && pendingLeaderboardName.length() > 0) {
                pendingLeaderboardName = pendingLeaderboardName.substring(0, pendingLeaderboardName.length() - 1);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (screen == GameScreen.CHARACTER_CREATION) {
            if (c >= 32 && c < 127 && nameInput.length() < 16) {
                nameInput.append(c);
            }
        } else if (screen == GameScreen.GAME_OVER) {
            if (c >= 32 && c < 127 && pendingLeaderboardName.length() < 16) {
                pendingLeaderboardName += c;
            }
        }
    }

    @Override public void keyReleased(KeyEvent e) {}

    @Override
    public void mouseMoved(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        if (screen == GameScreen.GAME) {
            if (mx < ARENA_W) basket.setTargetX(mx);
        }
        hoveredButton = -1;
        if (menuButtons != null) {
            for (int i = 0; i < menuButtons.length; i++) {
                if (menuButtons[i] != null && menuButtons[i].contains(mx, my)) {
                    hoveredButton = i;
                    break;
                }
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) { mouseMoved(e); }

    @Override
    public void mouseClicked(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        handleClick(mx, my);
    }

    private void handleClick(int mx, int my) {
        switch (screen) {
            case MAIN_MENU:
                if (menuButtons == null) return;
                for (int i = 0; i < menuButtons.length; i++) {
                    if (menuButtons[i] != null && menuButtons[i].contains(mx, my)) {
                        switch (i) {
                            case 0: screen = GameScreen.CHARACTER_CREATION; break;
                            case 1: screen = GameScreen.LEADERBOARD; break;
                            case 2: screen = GameScreen.WARDROBE; break;
                            case 3: screen = GameScreen.FARM_UPGRADE; break;
                            case 4: screen = GameScreen.ACHIEVEMENTS; break;
                        }
                    }
                }
                break;
            case CHARACTER_CREATION:
                // Starter skin selection
                SkinType[] starters = STARTER_SKINS;
                int skinBoxW = 120, skinBoxH = 130;
                int startX = W/2 - (int)(starters.length * skinBoxW / 2.0 + (starters.length-1) * 10 / 2.0);
                for (int i = 0; i < starters.length; i++) {
                    int bx = startX + i * (skinBoxW + 10);
                    Rectangle r = new Rectangle(bx, 265, skinBoxW, skinBoxH);
                    if (r.contains(mx, my)) selectedStarterSkin = i;
                }
                // Start button
                Rectangle startBtn = new Rectangle(W/2 - 130, 490, 260, 50);
                if (startBtn.contains(mx, my) && nameInput.length() > 0) beginGame();
                // Back
                if (my > 530 && mx < 220) screen = GameScreen.MAIN_MENU;
                break;
            case PAUSED:
                if (menuButtons == null) return;
                if (menuButtons.length > 0 && menuButtons[0] != null && menuButtons[0].contains(mx, my)) screen = GameScreen.GAME;
                if (menuButtons.length > 1 && menuButtons[1] != null && menuButtons[1].contains(mx, my)) screen = GameScreen.MAIN_MENU;
                break;
            case WARDROBE:
                // Tabs
                if (new Rectangle(50, 85, 200, 36).contains(mx, my)) shopTab = 0;
                if (new Rectangle(270, 85, 200, 36).contains(mx, my)) shopTab = 1;
                // Items
                handleShopClick(mx, my);
                // Back
                if (menuButtons != null && menuButtons.length > 0 && menuButtons[0] != null && menuButtons[0].contains(mx, my)) screen = GameScreen.MAIN_MENU;
                break;
            case FARM_UPGRADE:
                // Upgrade button area
                Rectangle upgradeBtn = new Rectangle(W/2 - 150, 370, 300, 52);
                if (upgradeBtn.contains(mx, my) && farm.canUpgrade()) {
                    int cost = farm.getUpgradeCost();
                    if (playerData.spendCoins(cost)) {
                        farm.upgradeFarm();
                        playerData.upgradeFarm();
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
                if (menuButtons.length > 0 && menuButtons[0] != null && menuButtons[0].contains(mx, my)) {
                    // Retry
                    startNewGame();
                }
                if (menuButtons.length > 1 && menuButtons[1] != null && menuButtons[1].contains(mx, my)) {
                    // Save score
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
                // Mute / Pause buttons in sidebar
                if (mx > ARENA_W) {
                    Rectangle muteBtn = new Rectangle(ARENA_W + 15, H - 65, 60, 32);
                    Rectangle pauseBtn = new Rectangle(ARENA_W + 85, H - 65, 60, 32);
                    if (muteBtn.contains(mx, my)) sound.toggleMute();
                    if (pauseBtn.contains(mx, my)) screen = GameScreen.PAUSED;
                }
                break;
        }
    }

    private void handleShopClick(int mx, int my) {
        if (shopTab == 0) {
            SkinType[] skins = SkinType.values();
            int cols = 4, boxW = 130, boxH = 150;
            for (int i = 0; i < skins.length; i++) {
                int col = i % cols, row = i / cols;
                int bx = 40 + col * (boxW + 15);
                int by = 140 + row * (boxH + 15);
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

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    private void beginGame() {
        String farmerName = nameInput.toString().trim();
        if (farmerName.isEmpty()) farmerName = "Farmer";
        playerData.equipSkin(STARTER_SKINS[selectedStarterSkin]);
        character = new Character(0, 0, playerData.getEquippedSkin(), farmerName);
        startNewGame();
    }
}
