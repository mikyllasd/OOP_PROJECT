import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The core game panel.
 * Handles the game loop (Runnable), all input, rendering, and logic.
 *
 * Demonstrates: Runnable, event-driven programming, composition, 
 *               use of polymorphic Entity references.
 */
public class GamePanel extends JPanel implements Runnable, KeyListener, MouseMotionListener, MouseListener {

    // ── Constants ─────────────────────────────────────────────────────
    private static final int   FPS        = 60;
    private static final int   GAME_SECS  = 180;   // 3-minute timer
    private static final Color BACKGROUND = new Color(15, 12, 41);

    // ── Game objects ──────────────────────────────────────────────────
    private Basket           basket;
    private final List<Ball>      balls     = new CopyOnWriteArrayList<>();
    private final List<Particle>  particles = new CopyOnWriteArrayList<>();
    private final ScoreManager    scores    = new ScoreManager();

    // ── State ─────────────────────────────────────────────────────────
    private GameScreen screen   = GameScreen.NAME_ENTRY;
    private int  level          = 1;
    private int  score          = 0;
    private int  coins          = 0;
    private int  timerSecs      = GAME_SECS;
    private int  combo          = 0;
    private int  multiplier     = 1;
    private int  consecutiveMiss= 0;
    private int  coinsAwarded   = 0;   // Tracks 100-point thresholds for coin conversion
    private boolean newRecord   = false;
    private int  finalLevel, finalScore, finalCoins;

    // ── Input ─────────────────────────────────────────────────────────
    private double mouseX = 400;
    private boolean leftHeld  = false;
    private boolean rightHeld = false;

    // ── Spawn timer ───────────────────────────────────────────────────
    private long lastSpawnMs   = 0;
    private long spawnInterval = 1400;  // milliseconds

    // ── Game clock ────────────────────────────────────────────────────
    private long lastTimerMs   = 0;

    // ── Flash & FX text ───────────────────────────────────────────────
    private String  comboFlashText = "";
    private float   comboFlashLife = 0;
    private String  missWarnText   = "";
    private float   missWarnLife   = 0;
    private final List<FxText> fxTexts = new CopyOnWriteArrayList<>();

    // ── Stars background ──────────────────────────────────────────────
    private final int[]    starX, starY;
    private final float[]  starPhase;
    private final float[]  starSpeed;
    private       double   starTime  = 0;

    // ── Screen shake ──────────────────────────────────────────────────
    private int    shakeFrames = 0;
    private final java.util.Random rng = new java.util.Random();

    // ── Leaderboard & player name input ─────────────────────────────────
    private String nameInput = "";
    private String playerName = "";
    private boolean nameDone = false;

    // ── Shop / housing system ───────────────────────────────────────────
    private int houses = 0;
    private Rectangle btnPlay, btnLeader, btnBack, btnMenuBack, btnRetry, btnInstructions, btnShop, btnShopBack, btnBuySmall, btnBuyMedium, btnBuyLarge;
    private Rectangle hoverButton;

    // ─────────────────────────────────────────────────────────────────
    public GamePanel() {
        setPreferredSize(new Dimension(1000, 720));
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(this);
        addMouseMotionListener(this);
        addMouseListener(this);

        // Pre-compute star field
        starX     = new int  [120];
        starY     = new int  [120];
        starPhase = new float[120];
        starSpeed = new float[120];
        for (int i = 0; i < 120; i++) {
            starX[i]     = rng.nextInt(1000);
            starY[i]     = rng.nextInt(720);
            starPhase[i] = rng.nextFloat() * (float)(Math.PI * 2);
            starSpeed[i] = 0.003f + rng.nextFloat() * 0.008f;
        }
    }

    // ── Game loop ─────────────────────────────────────────────────────

    @Override
    public void run() {
        long targetMs = 1000 / FPS;
        while (true) {
            long start = System.currentTimeMillis();
            tick();
            repaint();
            long elapsed = System.currentTimeMillis() - start;
            long sleep   = targetMs - elapsed;
            if (sleep > 0) {
                try { Thread.sleep(sleep); } catch (InterruptedException ignored) {}
            }
        }
    }

    private void tick() {
        starTime += 0.016;

        if (screen != GameScreen.PLAYING) return;

        // ── Timer countdown ───────────────────────────────────────────
        long now = System.currentTimeMillis();
        if (now - lastTimerMs >= 1000) {
            lastTimerMs = now;
            timerSecs--;
            if (timerSecs <= 30 && timerSecs > 0) SoundManager.playTick();
            if (timerSecs <= 0) { endGame(); return; }
        }

        // ── Move basket ───────────────────────────────────────────────
        if (leftHeld)  basket.moveLeft();
        if (rightHeld) basket.moveRight(getWidth());
        basket.update();

        // ── Update balls ──────────────────────────────────────────────
        List<Ball> toRemove = new ArrayList<>();
        for (Ball b : balls) {
            b.update();
            if (tryCollect(b)) {
                toRemove.add(b);
            } else if (b.getY() > getHeight()) {
                onMiss(b);
                toRemove.add(b);
            }
        }
        balls.removeAll(toRemove);

        // ── Spawn balls ───────────────────────────────────────────────
        spawnInterval = Math.max(400, 1600 - level * 80);
        if (now - lastSpawnMs > spawnInterval) {
            lastSpawnMs = now;
            spawnBall();
        }

        // ── Update particles & FX ─────────────────────────────────────
        particles.removeIf(Particle::isDead);
        for (Particle p : particles) p.update();

        fxTexts.removeIf(f -> f.life <= 0);
        for (FxText f : fxTexts) { f.y -= 1.5; f.life -= 0.025f; }

        // ── Point to coin conversion (100 points = 20 coins) ──────────
        int earnedCoins = (score / 100) - coinsAwarded;
        if (earnedCoins > 0) {
            coins += earnedCoins * 20;
            coinsAwarded += earnedCoins;
        }

        // ── Decay flash timers ────────────────────────────────────────
        if (comboFlashLife > 0) comboFlashLife -= 0.022f;
        if (missWarnLife  > 0) missWarnLife  -= 0.025f;
        if (shakeFrames   > 0) shakeFrames--;
    }

    // ── Ball spawn ────────────────────────────────────────────────────

    private void spawnBall() {
        int w = getWidth();
        if (w == 0) w = 800;
        double bx = 20 + rng.nextDouble() * (w - 60);
        BallType t = BallType.random();
        balls.add(new Ball(bx, -36, t, level, w));
    }

    // ── Collision / catch ─────────────────────────────────────────────

    private boolean tryCollect(Ball b) {
        int bCX = basket.getCatchCenterX();
        int bCY = basket.getCatchY();
        int bfX = (int)(b.getX() + b.getWidth()  / 2.0);
        int bfY = (int)(b.getY() + b.getHeight() / 2.0);
        int catchWidth = Math.max(40, getWidth() / 14);

        if (Math.abs(bfX - bCX) < catchWidth && bfY >= bCY - 10 && bfY <= bCY + basket.getHeight() + 10) {
            handleCatch(b, bfX, bCY);
            return true;
        }
        return false;
    }

    private void handleCatch(Ball b, int px, int py) {
        BallType t = b.getType();
        int pts = t.getPoints() * multiplier;

        switch (t) {
            case BOMB:
                score = Math.max(0, score + t.getPoints());
                combo = 0; multiplier = 1;
                spawnParticles(px, py, new Color(255, 100, 20), 18, true);
                spawnParticles(px, py, new Color(255, 220, 50), 10, true);
                spawnFx(px, py, "💥 " + t.getPoints(), Color.RED);
                shakeFrames = 10;
                SoundManager.playBomb();
                consecutiveMiss = 0;
                break;

            case ROTTEN:
                score = Math.max(0, score + t.getPoints());
                combo = 0; multiplier = 1;
                spawnParticles(px, py, new Color(130, 190, 50), 10, false);
                spawnFx(px, py, "🤢 " + t.getPoints(), new Color(170, 220, 80));
                consecutiveMiss = 0;
                break;

            case STAR:
                int baseStar = t.getPoints() * multiplier;
                int bonusStar = baseStar + (int)Math.round(baseStar * houses * 0.05);
                score += bonusStar;
                coins += Math.max(1, bonusStar / 5) + houses;
                combo++;
                updateCombo(px, py);
                spawnParticles(px, py, new Color(255, 215, 0), 16, false);
                spawnParticles(px, py, Color.WHITE, 6, false);
                spawnFx(px, py, "⭐ +" + bonusStar, Color.YELLOW);
                SoundManager.playStar();
                consecutiveMiss = 0;
                break;

            default: // NORMAL
                int baseNormal = pts;
                int bonusNormal = baseNormal + (int)Math.round(baseNormal * houses * 0.05);
                score += bonusNormal;
                coins += Math.max(1, bonusNormal / 5) + houses;
                combo++;
                updateCombo(px, py);
                spawnParticles(px, py, new Color(100, 210, 100), 8, false);
                spawnParticles(px, py, Color.WHITE, 3, false);
                spawnFx(px, py, "+" + bonusNormal, new Color(160, 230, 160));
                SoundManager.playCatch();
                consecutiveMiss = 0;
                break;
        }

        // Level up check
        while (score >= targetScore()) levelUp();
    }

    private void onMiss(Ball b) {
        if (b.getType() == BallType.BOMB || b.getType() == BallType.ROTTEN) return; // ignore misses for bad balls
        combo = 0; multiplier = 1;
        consecutiveMiss++;
        spawnParticles((int)(b.getX() + 16), getHeight() - 80, Color.GRAY, 5, false);
        spawnFx((int)(b.getX() + 16), getHeight() - 90, "✕", Color.GRAY);
        if (consecutiveMiss >= 3) {
            missWarnText = "⚠ Too many misses!";
            missWarnLife = 1f;
            consecutiveMiss = 0;
        }
    }

    // ── Combo ─────────────────────────────────────────────────────────

    private void updateCombo(int px, int py) {
        int prev = multiplier;
        if      (combo >= 11) multiplier = 5;
        else if (combo >= 8)  multiplier = 4;
        else if (combo >= 5)  multiplier = 3;
        else if (combo >= 3)  multiplier = 2;
        else                  multiplier = 1;

        if (multiplier > prev) {
            comboFlashText = "🔥 x" + multiplier;
            comboFlashLife = 1f;
            SoundManager.playCombo();
        }
    }

    // ── Level ─────────────────────────────────────────────────────────

    private int targetScore() {
        switch (level) {
            case 1: return 30;
            case 2: return 80;
            case 3: return 140;
            case 4: return 220;
            case 5: return 320;
            default: return 320 + (level - 5) * 80;
        }
    }

    private void levelUp() {
        level++;
        combo = 0; multiplier = 1;
        balls.clear();
        SoundManager.playLevelUp();
        // Burst of particles
        for (int i = 0; i < 6; i++) {
            spawnParticles(getWidth() / 2 + rng.nextInt(200) - 100,
                           getHeight() / 2, new Color(255, 213, 79), 10, false);
        }
    }

    // ── Game over ─────────────────────────────────────────────────────

    private void endGame() {
        finalLevel = level;
        finalScore = score;
        finalCoins = coins;
        newRecord  = scores.isNewRecord(score);
        balls.clear();
        particles.clear();
        SoundManager.playGameOver();
        nameDone  = false;
        screen = GameScreen.GAME_OVER;
    }

    // ── Start / reset ─────────────────────────────────────────────────

    private void startNewGame() {
        if (!nameInput.isBlank()) {
            playerName = nameInput.trim();
        }
        if (playerName.isBlank()) {
            playerName = "Player";
        }

        level  = 1; score = 0; coins = 0; houses = 0; timerSecs = GAME_SECS;
        combo  = 0; multiplier = 1; consecutiveMiss = 0; coinsAwarded = 0;
        balls.clear(); particles.clear(); fxTexts.clear();
        lastTimerMs = System.currentTimeMillis();
        lastSpawnMs = 0;
        basket = new Basket(getWidth() / 2.0 - 36, getHeight() - 90);
        comboFlashLife = 0; missWarnLife = 0; shakeFrames = 0;
        screen = GameScreen.PLAYING;
    }

    // ── Particles / FX helpers ────────────────────────────────────────

    private void spawnParticles(int x, int y, Color c, int count, boolean bomb) {
        for (int i = 0; i < count; i++) particles.add(new Particle(x, y, c, bomb));
    }

    private void spawnFx(int x, int y, String text, Color color) {
        fxTexts.add(new FxText(x - 20, y, text, color));
    }

    // ─────────────────────────────────────────────────────────────────
    // RENDERING
    // ─────────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Screen shake
        if (shakeFrames > 0) {
            int sx = rng.nextInt(14) - 7;
            int sy = rng.nextInt(10) - 5;
            g2.translate(sx, sy);
        }

        switch (screen) {
            case NAME_ENTRY:   drawNameEntry(g2);   break;
            case MENU:         drawMenu(g2);        break;
            case INSTRUCTIONS: drawInstructions(g2); break;
            case PLAYING:      drawGame(g2);        break;
            case SHOP:         drawShop(g2);        break;
            case PAUSED:       drawGame(g2); drawPause(g2); break;
            case GAME_OVER:    drawGameOver(g2);    break;
            case LEADERBOARD:  drawLeaderboard(g2); break;
        }
    }

    // ── Star background (shared) ──────────────────────────────────────

    private void drawStarsBg(Graphics2D g) {
        g.setColor(BACKGROUND);
        g.fillRect(0, 0, getWidth(), getHeight());
        for (int i = 0; i < starX.length; i++) {
            float alpha = 0.25f + 0.75f * Math.abs((float) Math.sin(starTime * starSpeed[i] + starPhase[i]));
            g.setColor(new Color(1f, 1f, 1f, Math.min(1f, alpha)));
            int r = (int)(1 + rng.nextInt(2));
            g.fillOval(starX[i], starY[i], r, r);
        }
    }

    // ── MENU ─────────────────────────────────────────────────────────

    private void drawMenu(Graphics2D g) {
        drawStarsBg(g);
        int w = getWidth(), h = getHeight(), cx = w / 2;

        // Title
        g.setFont(new Font("SansSerif", Font.BOLD, 52));
        String title = "Catch the Ball";
        FontMetrics fm = g.getFontMetrics();
        int tx = cx - fm.stringWidth(title) / 2;
        // Gradient text
        GradientPaint titleGrad = new GradientPaint(
            tx, h / 3 - 60, new Color(255, 213, 79),
            tx + fm.stringWidth(title), h / 3 - 60, new Color(255, 111, 0)
        );
        g.setPaint(titleGrad);
        g.drawString(title, tx, h / 3);

        // Subtitle
        g.setFont(new Font("SansSerif", Font.PLAIN, 17));
        g.setColor(new Color(255, 255, 255, 160));
        String sub = "Move your basket · Catch the balls · Earn coins!";
        g.drawString(sub, cx - g.getFontMetrics().stringWidth(sub) / 2, h / 3 + 80);

        // Ball icon preview
        drawMenuBalls(g, cx, h / 3 + 120);

        // Buttons
        btnPlay   = drawMenuButton(g, cx - 110, h * 2 / 3 - 20, 200, 50,
                                    new Color(249, 168, 37), "▶  Play Game", Color.WHITE);
        btnInstructions = drawMenuButton(g, cx - 110, h * 2 / 3 + 50, 200, 50,
                                    new Color(100, 150, 200), "📖  Instructions", Color.WHITE);
        btnLeader = drawMenuButton(g, cx - 110, h * 2 / 3 + 120, 200, 50,
                                    new Color(50, 60, 90), "🏆  Leaderboard", Color.WHITE);

        // Controls hint
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.setColor(new Color(255, 255, 255, 90));
        String hint = "← → Arrow Keys or Mouse to move";
        g.drawString(hint, cx - g.getFontMetrics().stringWidth(hint) / 2, h - 24);
    }

    private void drawNameEntry(Graphics2D g) {
        drawStarsBg(g);
        int w = getWidth(), h = getHeight(), cx = w / 2;

        // Title
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        String title = "Welcome!";
        FontMetrics fm = g.getFontMetrics();
        int tx = cx - fm.stringWidth(title) / 2;
        GradientPaint titleGrad = new GradientPaint(
            tx, h / 3 - 40, new Color(255, 213, 79),
            tx + fm.stringWidth(title), h / 3 - 40, new Color(255, 111, 0)
        );
        g.setPaint(titleGrad);
        g.drawString(title, tx, h / 3);

        // Instructions
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        g.setColor(new Color(255, 255, 255, 180));
        String instr = "Please enter your name to start playing";
        g.drawString(instr, cx - g.getFontMetrics().stringWidth(instr) / 2, h / 3 + 50);

        // Name input box
        g.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g.setColor(new Color(255, 255, 255, 200));
        drawCentered(g, "Player Name", cx, h / 2 - 20);
        int nameBoxW = 350, nameBoxH = 45;
        int nameBoxX = cx - nameBoxW / 2;
        int nameBoxY = h / 2;
        g.setColor(new Color(255, 255, 255, 25));
        g.fillRoundRect(nameBoxX, nameBoxY, nameBoxW, nameBoxH, 20, 20);
        g.setColor(new Color(249, 168, 37, 200));
        g.setStroke(new BasicStroke(2.0f));
        g.drawRoundRect(nameBoxX, nameBoxY, nameBoxW, nameBoxH, 20, 20);
        g.setFont(new Font("SansSerif", Font.PLAIN, 18));
        String displayedName = !nameInput.isBlank() ? nameInput : "Type your name here...";
        g.setColor(displayedName.equals("Type your name here...") ? new Color(255, 255, 255, 120) : Color.WHITE);
        g.drawString(displayedName, nameBoxX + 20, nameBoxY + 28);

        // Continue button
        btnPlay = drawMenuButton(g, cx - 100, h / 2 + 100, 200, 50,
                                new Color(249, 168, 37), "Continue →", Color.WHITE);

        // Skip hint
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.setColor(new Color(255, 255, 255, 100));
        String skip = "Press Enter to continue";
        g.drawString(skip, cx - g.getFontMetrics().stringWidth(skip) / 2, h - 30);
    }

    private void drawMenuBalls(Graphics2D g, int cx, int y) {
        // Show one example of each ball type
        BallType[] types = BallType.values();
        int spacing = 60;
        int startX  = cx - (types.length - 1) * spacing / 2;
        for (int i = 0; i < types.length; i++) {
            Ball demo = new Ball(startX + i * spacing - 16, y, types[i], 1, 800);
            demo.draw(g);
        }
        // Labels
        g.setFont(new Font("SansSerif", Font.BOLD, 10));
        String[] labels = { "+10 pts", "+50 pts", "-30 pts", "-20 pts" };
        Color[]  lclrs  = {
            new Color(140, 220, 140),
            Color.YELLOW,
            new Color(255, 100, 100),
            new Color(180, 210, 80)
        };
        for (int i = 0; i < labels.length; i++) {
            g.setColor(lclrs[i]);
            int lx = startX + i * spacing;
            g.drawString(labels[i], lx - g.getFontMetrics().stringWidth(labels[i]) / 2, y + 46);
        }
    }

    private Rectangle drawMenuButton(Graphics2D g, int x, int y, int w, int h,
                                     Color bg, String label, Color fg) {
        RoundRectangle2D rr = new RoundRectangle2D.Float(x, y, w, h, 28, 28);
        g.setColor(bg);
        g.fill(rr);
        g.setColor(bg.darker());
        g.setStroke(new BasicStroke(1.5f));
        g.draw(rr);
        g.setColor(fg);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, x + (w - fm.stringWidth(label)) / 2, y + (h + fm.getAscent() - fm.getDescent()) / 2);
        return new Rectangle(x, y, w, h);
    }

    // ── INSTRUCTIONS ──────────────────────────────────────────────────

    private void drawInstructions(Graphics2D g) {
        drawStarsBg(g);
        int w = getWidth(), h = getHeight(), cx = w / 2;

        // Title
        g.setFont(new Font("SansSerif", Font.BOLD, 42));
        String title = "How to Play";
        FontMetrics fm = g.getFontMetrics();
        int tx = cx - fm.stringWidth(title) / 2;
        GradientPaint titleGrad = new GradientPaint(
            tx, 80, new Color(255, 213, 79),
            tx + fm.stringWidth(title), 80, new Color(255, 111, 0)
        );
        g.setPaint(titleGrad);
        g.drawString(title, tx, 80);

        // Instructions content
        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.setColor(Color.WHITE);
        int y = 120;
        int lineHeight = 25;

        String[] instructions = {
            "🎯 OBJECTIVE:",
            "Catch falling balls with your basket to earn points and coins!",
            "Avoid missing balls or you'll lose points.",
            "",
            "🎮 CONTROLS:",
            "• Use ← → arrow keys or move mouse to control the basket",
            "",
            "⚽ BALL TYPES:",
            "• Green ● (Normal): +10 points",
            "• Gold ★ (Star): +50 points - rare!",
            "• Dark ✦ (Bomb): -30 points - avoid!",
            "• Green ✕ (Rotten): -20 points - avoid!",
            "",
            "⏰ GAMEPLAY:",
            "• You have 3 minutes to catch as many balls as possible",
            "• Difficulty increases with each level",
            "• Build combos by catching multiple balls in a row",
            "• Higher combos give bonus multipliers!",
            "",
            "🏆 SCORING:",
            "• Points determine your final score",
            "• Coins are earned from successful catches",
            "• Try to beat your high score!",
            "",
            "🏡 SHOP:",
            "• Open the shop while playing to buy houses with coins",
            "• Houses give extra coins and a small score bonus"
        };

        for (String line : instructions) {
            if (line.isEmpty()) {
                y += lineHeight / 2; // Smaller gap for empty lines
            } else {
                g.drawString(line, 50, y);
                y += lineHeight;
            }
        }

        // Back button
        btnBack = drawMenuButton(g, w - 160, h - 80, 140, 50,
                                new Color(100, 100, 100), "← Back to Menu", Color.WHITE);
    }

    // ── SHOP ─────────────────────────────────────────────────────────

    private void drawShop(Graphics2D g) {
        drawStarsBg(g);
        int w = getWidth(), h = getHeight(), cx = w / 2;

        g.setFont(new Font("SansSerif", Font.BOLD, 42));
        GradientPaint gp = new GradientPaint(cx - 140, 20, new Color(255, 213, 79), cx + 140, 20, new Color(255, 140, 0));
        g.setPaint(gp);
        drawCentered(g, "🏠 House Shop", cx, 70);

        // Display current coins prominently
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        g.setColor(new Color(255, 215, 0));
        drawCentered(g, "💰 " + coins + " Coins", cx, 110);

        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.setColor(new Color(255, 255, 255, 180));
        drawCentered(g, "Spend coins to buy houses and boost your run.", cx, 140);
        drawCentered(g, "Each house gives +1 coin per catch and +5% score bonus.", cx, 162);

        int cardW = 260, cardH = 130, gap = 24;
        int totalWidth = 3 * cardW + 2 * gap;
        int startX = cx - totalWidth / 2;
        int startY = 190;

        drawShopCard(g, startX, startY, cardW, cardH, "Cottage", "+1 house", 50, "Small house", true);
        drawShopCard(g, startX + cardW + gap, startY, cardW, cardH, "Villa", "+2 houses", 120, "Medium house", false);
        drawShopCard(g, startX + 2*(cardW + gap), startY, cardW, cardH, "Mansion", "+4 houses", 220, "Luxury house", false);

        btnBuySmall  = new Rectangle(startX + cardW - 120, startY + cardH - 42, 100, 30);
        btnBuyMedium = new Rectangle(startX + cardW + gap + cardW - 120, startY + cardH - 42, 100, 30);
        btnBuyLarge  = new Rectangle(startX + 2*(cardW + gap) + cardW - 120, startY + cardH - 42, 100, 30);

        g.setFont(new Font("SansSerif", Font.BOLD, 14));
        drawButtonLabel(g, btnBuySmall, "Buy 50", coins >= 50, hoverButton == btnBuySmall);
        drawButtonLabel(g, btnBuyMedium, "Buy 120", coins >= 120, hoverButton == btnBuyMedium);
        drawButtonLabel(g, btnBuyLarge, "Buy 220", coins >= 220, hoverButton == btnBuyLarge);

        g.setFont(new Font("SansSerif", Font.PLAIN, 16));
        g.setColor(Color.WHITE);
        drawCentered(g, "Houses owned: " + houses, cx, startY + cardH + 90);

        btnShopBack = drawMenuButton(g, cx - 100, h - 80, 200, 50, new Color(50, 60, 90), "← Back to Game", Color.WHITE);
    }

    private void drawShopCard(Graphics2D g, int x, int y, int w, int h, String title, String benefit, int cost, String subtitle, boolean first) {
        drawCard(g, x, y, w, h);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.setColor(Color.WHITE);
        g.drawString(title, x + 18, y + 34);
        g.setFont(new Font("SansSerif", Font.PLAIN, 13));
        g.setColor(new Color(255, 255, 255, 180));
        g.drawString(subtitle, x + 18, y + 56);
        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.setColor(new Color(249, 168, 37));
        g.drawString(benefit, x + 18, y + 90);
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.setColor(new Color(255, 255, 255, 160));
        g.drawString("Cost: " + cost + " coins", x + 18, y + 110);
    }

    private void drawButtonLabel(Graphics2D g, Rectangle rect, String label, boolean enabled, boolean hovered) {
        Color base = enabled ? new Color(80, 110, 150) : new Color(70, 70, 80);
        if (hovered && enabled) base = new Color(110, 150, 190);
        g.setColor(base);
        g.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 14, 14);
        g.setColor(new Color(255, 255, 255, enabled ? 255 : 140));
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, rect.x + (rect.width - fm.stringWidth(label)) / 2, rect.y + (rect.height + fm.getAscent() - fm.getDescent()) / 2);
    }

    // ── GAME SCREEN ───────────────────────────────────────────────────

    private void drawGame(Graphics2D g) {
        int w = getWidth(), h = getHeight();

        // Sky gradient (changes with level)
        drawSkyBg(g, w, h);

        // Ground
        drawGround(g, w, h);

        // Particles
        for (Particle p : particles) p.draw(g);

        // Balls
        for (Ball b : balls) b.draw(g);

        // Basket
        if (basket != null) basket.draw(g);

        // FX text
        g.setFont(new Font("SansSerif", Font.BOLD, 19));
        for (FxText f : fxTexts) {
            int alpha = Math.max(0, Math.min(255, (int)(f.life * 255)));
            g.setColor(new Color(f.color.getRed(), f.color.getGreen(), f.color.getBlue(), alpha));
            g.drawString(f.text, (int) f.x, (int) f.y);
        }

        // Shop button
        btnShop = drawMenuButton(g, w - 140, 18, 120, 34, new Color(85, 125, 165), "🏠 Shop", Color.WHITE);

        // Combo flash
        if (comboFlashLife > 0) {
            float scale = 1f + 0.25f * comboFlashLife;
            Font comboFont = new Font("SansSerif", Font.BOLD, (int)(40 * scale));
            g.setFont(comboFont);
            FontMetrics fm = g.getFontMetrics();
            int alpha = Math.max(0, Math.min(255, (int)(comboFlashLife * 255)));
            g.setColor(new Color(255, 220, 0, alpha));
            g.drawString(comboFlashText,
                w / 2 - fm.stringWidth(comboFlashText) / 2, h / 2);
        }

        // Miss warning
        if (missWarnLife > 0) {
            g.setFont(new Font("SansSerif", Font.BOLD, 22));
            FontMetrics fm = g.getFontMetrics();
            int alpha = Math.max(0, Math.min(255, (int)(missWarnLife * 255)));
            g.setColor(new Color(239, 83, 80, alpha));
            g.drawString(missWarnText, w / 2 - fm.stringWidth(missWarnText) / 2, h / 3);
        }

        // HUD
        drawHud(g, w);
    }

    private void drawSkyBg(Graphics2D g, int w, int h) {
        Color top, bot;
        if (level <= 3) {
            top = new Color(26, 107, 154); bot = new Color(133, 193, 233);
        } else if (level <= 6) {
            top = new Color(139, 69, 19); bot = new Color(240, 165, 0);
        } else if (level <= 9) {
            top = new Color(26, 26, 46); bot = new Color(74, 96, 112);
        } else {
            top = new Color(0, 0, 5); bot = new Color(26, 42, 58);
        }
        GradientPaint sky = new GradientPaint(0, 0, top, 0, h, bot);
        g.setPaint(sky);
        g.fillRect(0, 0, w, h);

        // Stars visible at night levels
        if (level >= 7) {
            for (int i = 0; i < starX.length; i++) {
                float alpha = 0.3f + 0.7f * Math.abs((float) Math.sin(starTime * starSpeed[i] + starPhase[i]));
                g.setColor(new Color(1f, 1f, 1f, Math.min(1f, alpha)));
                g.fillOval(starX[i], starY[i], 2, 2);
            }
        }
    }

    private void drawGround(Graphics2D g, int w, int h) {
        // Grass stripe
        GradientPaint ground = new GradientPaint(0, h - 64, new Color(46, 125, 50), 0, h, new Color(27, 94, 32));
        g.setPaint(ground);
        g.fillRect(0, h - 64, w, 64);

        // Grass bumps
        g.setColor(new Color(67, 160, 71));
        for (int bx = 0; bx < w; bx += 36) {
            g.fillOval(bx, h - 70, 40, 16);
        }
    }

    private void drawHud(Graphics2D g, int w) {
        // Dark bar
        g.setColor(new Color(0, 0, 0, 130));
        g.fillRect(0, 0, w, 52);
        g.setColor(new Color(255, 255, 255, 18));
        g.drawLine(0, 52, w, 52);

        int x = 12;

        // Level
        x = drawHudPill(g, x, 8, 36, " Lv " + level + " ");

        // Houses
        x = drawHudPill(g, x, 8, 36, " 🏠 " + houses + " ");

        // Score / target
        x = drawHudPill(g, x, 8, 36, " Score: " + score + " / " + targetScore() + " ");

        // Timer
        String timeStr = " ⏱ " + String.format("%d:%02d", timerSecs / 60, timerSecs % 60) + " ";
        Color timerBg = timerSecs <= 30
                ? new Color(200, 50, 40, 180)
                : new Color(255, 255, 255, 25);
        x = drawHudPill(g, x, 8, 36, timeStr, timerBg);

        // Coins
        drawHudPill(g, x, 8, 36, " 💰 " + coins + " ");

        // Combo (right side)
        if (multiplier > 1) {
            String comboStr = " 🔥 x" + multiplier + " ";
            drawHudPillRight(g, w - 12, 8, 36, comboStr, new Color(255, 140, 0, 200));
        }

        // Pause hint (far right)
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.setColor(new Color(255, 255, 255, 80));
        g.drawString("[P] Pause  [ESC] Menu", w - 160, 44);
    }

    private int drawHudPill(Graphics2D g, int x, int y, int h, String text) {
        return drawHudPill(g, x, y, h, text, new Color(255, 255, 255, 25));
    }

    private int drawHudPill(Graphics2D g, int x, int y, int h, String text, Color bg) {
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(text) + 8;
        g.setColor(bg);
        g.fillRoundRect(x, y, w, h, 14, 14);
        g.setColor(Color.WHITE);
        g.drawString(text, x + 4, y + (h + fm.getAscent() - fm.getDescent()) / 2);
        return x + w + 6;
    }

    private void drawHudPillRight(Graphics2D g, int right, int y, int h, String text, Color bg) {
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        int w = fm.stringWidth(text) + 8;
        int x = right - w;
        g.setColor(bg);
        g.fillRoundRect(x, y, w, h, 14, 14);
        g.setColor(Color.WHITE);
        g.drawString(text, x + 4, y + (h + fm.getAscent() - fm.getDescent()) / 2);
    }

    // ── PAUSE ─────────────────────────────────────────────────────────

    private void drawPause(Graphics2D g) {
        int w = getWidth(), h = getHeight(), cx = w / 2, cy = h / 2;

        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(0, 0, w, h);

        int bw = 300, bh = 260;
        int bx = cx - bw / 2, by = cy - bh / 2;
        drawCard(g, bx, by, bw, bh);

        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        g.setColor(Color.WHITE);
        drawCentered(g, "⏸  Paused", cx, by + 48);

        g.setFont(new Font("SansSerif", Font.PLAIN, 15));
        g.setColor(new Color(255, 255, 255, 170));
        drawCentered(g, "Level " + level + "  |  Score " + score, cx, by + 82);
        drawCentered(g, "Coins " + coins + "  |  " + String.format("%d:%02d", timerSecs / 60, timerSecs % 60), cx, by + 106);

        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.setColor(new Color(255, 255, 255, 130));
        drawCentered(g, "Press  P  or  ESC  to resume", cx, by + bh - 28);
    }

    // ── GAME OVER ─────────────────────────────────────────────────────

    private void drawGameOver(Graphics2D g) {
        drawStarsBg(g);
        int w = getWidth(), h = getHeight(), cx = w / 2;

        // New record
        if (newRecord) {
            g.setFont(new Font("SansSerif", Font.BOLD, 22));
            float pulse = 0.85f + 0.15f * (float) Math.sin(starTime * 4);
            g.setColor(new Color(1f, 0.84f, 0.3f, pulse));
            drawCentered(g, "🎉  NEW RECORD!  🎉", cx, 52);
        }

        // Title
        g.setFont(new Font("SansSerif", Font.BOLD, 48));
        String gTitle = finalLevel >= 10 ? "Legendary!" :
                        finalLevel >= 7  ? "Impressive!" :
                        finalLevel >= 4  ? "Not Bad!"   : "Game Over!";
        GradientPaint titleGrad = finalLevel >= 10
                ? new GradientPaint(cx - 120, 80, new Color(255, 213, 79), cx + 120, 80, new Color(255, 140, 0))
                : new GradientPaint(cx - 120, 80, new Color(239, 83, 80),  cx + 120, 80, new Color(183, 28, 28));
        g.setPaint(titleGrad);
        drawCentered(g, gTitle, cx, 105);

        // Stat cards
        int cardY = 130, cardH = 80, cardW = 130, gap = 14;
        int totalW = 3 * cardW + 2 * gap;
        int cardX = cx - totalW / 2;

        drawStatCard(g, cardX,            cardY, cardW, cardH, "Level Reached", String.valueOf(finalLevel));
        drawStatCard(g, cardX + cardW + gap, cardY, cardW, cardH, "Final Score",   String.valueOf(finalScore));
        drawStatCard(g, cardX + 2*(cardW + gap), cardY, cardW, cardH, "Total Coins",  String.valueOf(finalCoins));

        // Name input
        int niY = cardY + cardH + 24;
        g.setFont(new Font("SansSerif", Font.PLAIN, 14));
        g.setColor(new Color(255, 255, 255, 170));
        drawCentered(g, "Enter your name for the leaderboard:", cx, niY);

        int niX = cx - 120, niW = 240, niH = 40;
        g.setColor(new Color(255, 255, 255, 18));
        g.fillRoundRect(niX, niY + 8, niW, niH, 20, 20);
        g.setColor(new Color(249, 168, 37, 200));
        g.setStroke(new BasicStroke(1.5f));
        g.drawRoundRect(niX, niY + 8, niW, niH, 20, 20);

        g.setFont(new Font("SansSerif", Font.BOLD, 16));
        g.setColor(Color.WHITE);
        String displayed = !nameInput.isBlank() ? nameInput : (playerName.isBlank() ? "Type your name..." : playerName);
        Color niColor = displayed.equals("Type your name...") ? new Color(255, 255, 255, 80) : Color.WHITE;
        g.setColor(niColor);
        drawCentered(g, displayed, cx, niY + 8 + niH / 2 + 6);

        // Blink cursor
        if (!nameDone && (int)(starTime * 2) % 2 == 0) {
            FontMetrics fm = g.getFontMetrics();
            int textW = fm.stringWidth(nameInput);
            g.setColor(Color.WHITE);
            g.fillRect(cx + textW / 2 + 2, niY + 14, 2, 26);
        }

        // Buttons
        int btnY = niY + niH + 22;
        btnRetry  = drawMenuButton(g, cx - 110, btnY,      210, 50, new Color(249, 168, 37), "▶  Play Again", Color.WHITE);
        btnBack   = drawMenuButton(g, cx - 110, btnY + 62, 210, 50, new Color(50, 60, 90),   "🏠  Main Menu", Color.WHITE);
        btnLeader = drawMenuButton(g, cx - 110, btnY + 124,210, 50, new Color(40, 80, 130),  "🏆  Leaderboard", Color.WHITE);
    }

    private void drawStatCard(Graphics2D g, int x, int y, int w, int h, String label, String value) {
        drawCard(g, x, y, w, h);
        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.setColor(new Color(255, 255, 255, 130));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(label, x + (w - fm.stringWidth(label)) / 2, y + 22);
        g.setFont(new Font("SansSerif", Font.BOLD, 28));
        g.setColor(new Color(255, 213, 79));
        fm = g.getFontMetrics();
        g.drawString(value, x + (w - fm.stringWidth(value)) / 2, y + h - 16);
    }

    // ── LEADERBOARD ───────────────────────────────────────────────────

    private void drawLeaderboard(Graphics2D g) {
        drawStarsBg(g);
        int w = getWidth(), cx = w / 2;

        g.setFont(new Font("SansSerif", Font.BOLD, 32));
        GradientPaint gp = new GradientPaint(cx - 100, 20, new Color(255, 213, 79), cx + 100, 20, new Color(255, 140, 0));
        g.setPaint(gp);
        drawCentered(g, "🏆  Top Scores", cx, 55);

        List<ScoreEntry> list = scores.getScores();
        if (list.isEmpty()) {
            g.setFont(new Font("SansSerif", Font.PLAIN, 16));
            g.setColor(new Color(255, 255, 255, 130));
            drawCentered(g, "No scores yet — play a game!", cx, 130);
        } else {
            int rowH = 46, startY = 80, rx = cx - 240, rw = 480;
            String[] medals = { "🥇", "🥈", "🥉" };
            for (int i = 0; i < list.size(); i++) {
                ScoreEntry e  = list.get(i);
                int        ry = startY + i * (rowH + 6);
                Color      rc = i == 0 ? new Color(255, 213, 79, 40)
                              : i == 1 ? new Color(192, 192, 192, 30)
                              : i == 2 ? new Color(205, 127, 50, 30)
                              :          new Color(255, 255, 255, 15);
                g.setColor(rc);
                g.fillRoundRect(rx, ry, rw, rowH, 14, 14);
                g.setColor(new Color(255, 255, 255, 25));
                g.setStroke(new BasicStroke(1f));
                g.drawRoundRect(rx, ry, rw, rowH, 14, 14);

                // Rank
                String rank = i < 3 ? medals[i] : String.valueOf(i + 1);
                g.setFont(new Font("SansSerif", Font.BOLD, 15));
                g.setColor(Color.WHITE);
                g.drawString(rank, rx + 10, ry + 29);

                // Name
                g.setFont(new Font("SansSerif", Font.BOLD, 14));
                g.setColor(Color.WHITE);
                g.drawString(e.getName(), rx + 50, ry + 29);

                // Score
                g.setColor(new Color(255, 213, 79));
                FontMetrics fm = g.getFontMetrics();
                g.drawString(String.valueOf(e.getScore()), rx + rw - fm.stringWidth(String.valueOf(e.getScore())) - 14, ry + 29);

                // Meta
                g.setFont(new Font("SansSerif", Font.PLAIN, 11));
                g.setColor(new Color(255, 255, 255, 130));
                String meta = "Lv" + e.getLevel() + " | 💰" + e.getCoins();
                g.drawString(meta, rx + rw - g.getFontMetrics().stringWidth(meta) - 14, ry + 41);
            }
        }

        btnMenuBack = drawMenuButton(g, cx - 100, getHeight() - 70, 200, 48,
                                      new Color(50, 60, 90), "← Back", Color.WHITE);
    }

    // ── Drawing utilities ─────────────────────────────────────────────

    private void drawCard(Graphics2D g, int x, int y, int w, int h) {
        g.setColor(new Color(255, 255, 255, 18));
        g.fillRoundRect(x, y, w, h, 18, 18);
        g.setColor(new Color(255, 255, 255, 30));
        g.setStroke(new BasicStroke(1f));
        g.drawRoundRect(x, y, w, h, 18, 18);
    }

    private void drawCentered(Graphics2D g, String text, int cx, int y) {
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, cx - fm.stringWidth(text) / 2, y);
    }

    // ── Input ─────────────────────────────────────────────────────────

    @Override public void keyTyped(KeyEvent e) {
        char c = e.getKeyChar();
        if (screen == GameScreen.NAME_ENTRY || screen == GameScreen.GAME_OVER || screen == GameScreen.MENU) {
            if (Character.isLetterOrDigit(c) || c == ' ') {
                if (nameInput.length() < 16) nameInput += c;
            } else if (c == '\b' && nameInput.length() > 0) {
                nameInput = nameInput.substring(0, nameInput.length() - 1);
            }
        }
    }

    @Override public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (screen == GameScreen.NAME_ENTRY) {
            if (code == KeyEvent.VK_ENTER) {
                if (!nameInput.trim().isEmpty()) {
                    playerName = nameInput.trim();
                }
                screen = GameScreen.MENU;
                return;
            }
        } else if (screen == GameScreen.PLAYING) {
            if (code == KeyEvent.VK_LEFT  || code == KeyEvent.VK_A) leftHeld  = true;
            if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) rightHeld = true;
            if (code == KeyEvent.VK_P)     { screen = GameScreen.PAUSED; }
            if (code == KeyEvent.VK_ESCAPE) { screen = GameScreen.MENU; balls.clear(); }
            if (code == KeyEvent.VK_M)      SoundManager.toggleMute();
        } else if (screen == GameScreen.PAUSED) {
            if (code == KeyEvent.VK_P || code == KeyEvent.VK_ESCAPE) {
                lastTimerMs = System.currentTimeMillis();  // avoid timer jump
                screen = GameScreen.PLAYING;
            }
        } else if (screen == GameScreen.GAME_OVER) {
            if (code == KeyEvent.VK_ENTER) submitScoreAndRetry();
        }
    }

    @Override public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_LEFT  || code == KeyEvent.VK_A) leftHeld  = false;
        if (code == KeyEvent.VK_RIGHT || code == KeyEvent.VK_D) rightHeld = false;
    }

    @Override public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        if (screen == GameScreen.PLAYING && basket != null) {
            basket.setTargetX(mouseX, getWidth());
        }
        if (screen == GameScreen.SHOP) {
            hoverButton = null;
            if (btnBuySmall != null && btnBuySmall.contains(e.getX(), e.getY())) hoverButton = btnBuySmall;
            else if (btnBuyMedium != null && btnBuyMedium.contains(e.getX(), e.getY())) hoverButton = btnBuyMedium;
            else if (btnBuyLarge != null && btnBuyLarge.contains(e.getX(), e.getY())) hoverButton = btnBuyLarge;
            else if (btnShopBack != null && btnShopBack.contains(e.getX(), e.getY())) hoverButton = btnShopBack;
        }
    }

    @Override public void mouseDragged(MouseEvent e) { mouseMoved(e); }

    @Override public void mouseClicked(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        if (screen == GameScreen.NAME_ENTRY) {
            if (btnPlay != null && btnPlay.contains(mx, my)) { 
                if (!nameInput.trim().isEmpty()) {
                    playerName = nameInput.trim();
                }
                screen = GameScreen.MENU; 
                return; 
            }
        } else if (screen == GameScreen.MENU) {
            if (btnPlay   != null && btnPlay.contains(mx, my))   { startNewGame(); return; }
            if (btnInstructions != null && btnInstructions.contains(mx, my)) { screen = GameScreen.INSTRUCTIONS; return; }
            if (btnLeader != null && btnLeader.contains(mx, my)) { screen = GameScreen.LEADERBOARD; }
        } else if (screen == GameScreen.PLAYING) {
            if (btnShop != null && btnShop.contains(mx, my)) { screen = GameScreen.SHOP; return; }
        } else if (screen == GameScreen.INSTRUCTIONS) {
            if (btnBack != null && btnBack.contains(mx, my)) { screen = GameScreen.MENU; }
        } else if (screen == GameScreen.SHOP) {
            if (btnBuySmall != null && btnBuySmall.contains(mx, my) && coins >= 50) {
                coins -= 50; houses += 1; return; }
            if (btnBuyMedium != null && btnBuyMedium.contains(mx, my) && coins >= 120) {
                coins -= 120; houses += 2; return; }
            if (btnBuyLarge != null && btnBuyLarge.contains(mx, my) && coins >= 220) {
                coins -= 220; houses += 4; return; }
            if (btnShopBack != null && btnShopBack.contains(mx, my)) { screen = GameScreen.PLAYING; return; }
        } else if (screen == GameScreen.GAME_OVER) {
            if (btnRetry  != null && btnRetry.contains(mx, my))  { submitScoreAndRetry(); return; }
            if (btnBack   != null && btnBack.contains(mx, my))   { screen = GameScreen.MENU; return; }
            if (btnLeader != null && btnLeader.contains(mx, my)) { screen = GameScreen.LEADERBOARD; }
        } else if (screen == GameScreen.LEADERBOARD) {
            if (btnMenuBack != null && btnMenuBack.contains(mx, my)) screen = GameScreen.MENU;
        }
    }

    private void submitScoreAndRetry() {
        String name = !nameInput.isBlank() ? nameInput.trim() : (playerName.isBlank() ? "Player" : playerName);
        playerName = name;
        scores.addScore(name, finalScore, finalLevel, finalCoins);
        startNewGame();
    }

    // Unused interface methods
    @Override public void mousePressed(MouseEvent e)  {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e)  {}
    @Override public void mouseExited(MouseEvent e)   {}

    // ── Inner helper class ────────────────────────────────────────────

    /** Floating score/penalty text that rises and fades. */
    private static class FxText {
        double x, y;
        final String text;
        final Color  color;
        float  life = 1f;

        FxText(double x, double y, String text, Color color) {
            this.x = x; this.y = y;
            this.text = text; this.color = color;
        }
    }
}
