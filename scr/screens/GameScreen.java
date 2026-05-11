package screens;

import core.Screen;
import entities.*;
import enums.*;
import managers.*;
import models.GameState;
import models.FarmProgression;
import utils.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameScreen extends Screen {

    private GameState state;
    private Basket basket;
    private Character character;
    private List<Ball> balls;
    private List<PowerUp> powerUps;
    private ParticleManager particles;
    private BallSpawner ballSpawner;
    private PowerUpManager powerUpManager;
    private FarmProgression farm;

    private int screenShakeTimer;
    private float screenShakeX, screenShakeY;
    private String toastText;
    private int toastTimer;
    private String comboFlashText;
    private int comboFlashTimer;
    private String playerName = "Farmer";
    private Difficulty difficulty = Difficulty.NORMAL;

    public GameScreen(GamePanel panel) {
        super(panel);
    }

    @Override
    public void onEnter() {
        super.onEnter();
        startNewGame();
    }

    private void startNewGame() {
        state         = new GameState(difficulty);
        balls         = new ArrayList<>();
        powerUps      = new ArrayList<>();
        particles     = new ParticleManager();
        ballSpawner   = new BallSpawner(GamePanel.ARENA_W);
        powerUpManager= new PowerUpManager(GamePanel.ARENA_W);
        farm          = new FarmProgression(
                panel.getPlayerData()
                     .getProfile().getAllTimeBestScore() > 0
                     ? 1 : 1);
        basket    = new Basket(
                GamePanel.ARENA_W / 2f - 40,
                GamePanel.H - 110,
                panel.getPlayerData().getEquippedBasket());
        character = new Character(
                GamePanel.ARENA_W / 2f - 25,
                GamePanel.H - 160,
                panel.getPlayerData().getEquippedSkin(),
                playerName);
        screenShakeTimer = 0;
        toastTimer       = 0;
        comboFlashTimer  = 0;
    }

    @Override
    public void update() {
        tickCount++;
        updateTimers();
        updateGameLogic();
        updateEntities();
        checkLevelUp();
    }

    private void updateTimers() {
        if (tickCount % 60 == 0 && state.getTimeLeft() > 0) {
            state.decrementTime();
            if (state.getTimeLeft() == 0) endGame();
            if (state.getTimeLeft() == 10)
                panel.getSoundManager().playLowTime();
        }
        if (screenShakeTimer > 0) {
            screenShakeTimer--;
            screenShakeX = (float)(
                    (Math.random() - 0.5) * 10
                    * (screenShakeTimer / 20f));
            screenShakeY = (float)(
                    (Math.random() - 0.5) * 10
                    * (screenShakeTimer / 20f));
        } else { screenShakeX = 0; screenShakeY = 0; }
        if (toastTimer > 0) toastTimer--;
        if (comboFlashTimer > 0) comboFlashTimer--;
    }

    private void updateGameLogic() {
        ballSpawner.update(balls, state.getLevel(),
                difficulty);
        powerUpManager.update(powerUps, state);
        if (state.isMagnetActive()) applyMagnet();
        if (state.isWideBasketActive()) basket.setWide(true);
        else basket.setWide(false);
    }

    private void applyMagnet() {
        float cx = basket.getX() + basket.getWidth() / 2f;
        for (Ball b : balls) {
            if (!b.isActive()) continue;
            float bx   = b.getX() + b.getWidth() / 2f;
            float dist = Math.abs(cx - bx);
            if (dist < 200) b.setX(b.getX()
                    + (cx - bx) * 0.06f);
        }
    }

    private void updateEntities() {
        basket.update();
        character.update();

        Iterator<Ball> bi = balls.iterator();
        while (bi.hasNext()) {
            Ball b = bi.next();
            b.update();
            if (b.getY() > GamePanel.H) {
                if (!b.getType().isBad()) {
                    state.resetCombo();
                    particles.spawnBurst(
                            (int)(b.getX() + 18),
                            GamePanel.H - 10,
                            new Color(200, 100, 100), 4);
                }
                bi.remove();
            } else if (b.intersects(basket)) {
                handleCatch(b);
                bi.remove();
            }
        }

        Iterator<PowerUp> pi = powerUps.iterator();
        while (pi.hasNext()) {
            PowerUp p = pi.next();
            p.update();
            if (p.getY() > GamePanel.H) {
                pi.remove();
            } else if (p.intersects(basket)) {
                powerUpManager.activate(
                        p.getType(), state,
                        panel.getSoundManager());
                showToast(getPowerUpToast(p.getType()));
                particles.spawnBurst(
                        (int)(p.getX() + 20),
                        (int) p.getY(),
                        p.getType().getColor(), 12);
                pi.remove();
            }
        }

        particles.update();

        if (toastTimer == 0) {
            models.Achievement a =
                    panel.getScreenManager()
                         .getGameScreen() == this
                    ? null : null;
        }
    }

    private void handleCatch(Ball b) {
        boolean bad = b.getType().isBad();
        int pts     = b.getType().getPoints();

        if (bad) {
            if (state.isShieldActive()) {
                state.setShieldActive(false);
                showToast("\uD83D\uDEE1\uFE0F Shield blocked it!");
                basket.triggerCatch();
                character.triggerCatch();
            } else {
                int penalty = (int)(pts
                        * difficulty.getPenaltyMultiplier()
                        / -20f);
                state.addScore(Math.max(
                        -state.getScore(),
                        (int)(pts * difficulty
                                .getPenaltyMultiplier()
                                / -20f)));
                screenShakeTimer = 20;
                basket.triggerShake();
                character.triggerShake();
                state.resetCombo();
                panel.getSoundManager().playBadCatch();
                particles.spawnFloatingText(
                        (int) b.getX(), (int) b.getY(),
                        "" + pts,
                        ColorPalette.TEXT_BAD_CATCH);
            }
        } else {
            state.incrementCombo();
            float mult = 1f;
            int c = state.getCombo();
            if      (c >= 11) mult = 5f;
            else if (c >= 8)  mult = 4f;
            else if (c >= 5)  mult = 3f;
            else if (c >= 3)  mult = 2f;

            if (mult > state.getComboMultiplier()) {
                state.setComboMultiplier(mult);
                showComboFlash("x" + (int) mult + " COMBO!");
                panel.getSoundManager().playCombo();
            }

            float finalMult = mult
                    * (state.isDoublePointsActive() ? 2f : 1f);
            int earned = (int)(pts * finalMult);
            state.addScore(earned);
            int coins = Math.max(1, earned / 5);
            state.addCoins(coins);
            panel.getPlayerData().addCoins(coins);

            basket.triggerCatch();
            character.triggerCatch();
            panel.getSoundManager().playCatch(true);

            String prefix = finalMult > 1f
                    ? "x" + (int) finalMult + " " : "";
            particles.spawnFloatingText(
                    (int) b.getX(), (int) b.getY(),
                    prefix + "+" + earned,
                    ColorPalette.TEXT_GOOD_CATCH);
            particles.spawnBurst(
                    (int)(b.getX() + 20),
                    (int) b.getY(),
                    ColorPalette.PARTICLE_CATCH, 6);
        }
    }

    private void checkLevelUp() {
        if (state.getScore() >= state.getLevelTarget()) {
            state.levelUp();
            panel.getSoundManager().playLevelUp();
            showToast("\uD83C\uDF89 Level Up! Level "
                    + state.getLevel());
            particles.spawnBurst(
                    GamePanel.ARENA_W / 2,
                    GamePanel.H / 2,
                    ColorPalette.PARTICLE_LEVELUP, 30);
        }
    }

    private void endGame() {
        panel.getPlayerData().getProfile()
             .updateBestScore(state.getScore());
        panel.getPlayerData().getProfile()
             .updateBestCombo(state.getHighestCombo());
        panel.getPlayerData().getProfile()
             .incrementGamesPlayed();
        panel.getPlayerData().save();

        GameOverScreen gos = (GameOverScreen)
                panel.getScreenManager()
                     .getCurrentScreen();
        panel.getScreenManager()
             .switchTo(GameScreenType.GAME_OVER);
    }

    @Override
    public void draw(Graphics2D g) {
        g.translate((int) screenShakeX, (int) screenShakeY);
        BackgroundRenderer.drawSky(g, state.getLevel(),
                tickCount, GamePanel.ARENA_W,
                GamePanel.H,
                null, null);

        for (Ball b : balls)      b.draw(g);
        for (PowerUp p : powerUps) p.draw(g);
        particles.draw(g);
        basket.draw(g);
        character.draw(g);

        drawPowerUpAuras(g);
        drawComboFlash(g);

        g.translate(-(int) screenShakeX, -(int) screenShakeY);

        SidebarRenderer.draw(g, state, farm,
                panel.getPlayerData(),
                panel.getSoundManager(), tickCount);

        if (toastTimer > 0) drawToast(g);
    }

    private void drawPowerUpAuras(Graphics2D g) {
        if (state.isShieldActive()) {
            float pulse = MathUtils.pulse(tickCount, 0.15f);
            g.setColor(new Color(80, 180, 255,
                    (int)(pulse * 80)));
            g.setStroke(new BasicStroke(3f));
            g.drawOval((int)(basket.getX() - 6),
                    (int)(basket.getY() - 6),
                    basket.getWidth() + 12,
                    basket.getHeight() + 12);
            g.setStroke(new BasicStroke(1f));
        }
        if (state.isMagnetActive()) {
            float pulse = MathUtils.pulse(tickCount, 0.2f);
            g.setColor(new Color(255, 80, 200,
                    (int)(pulse * 60)));
            g.fillOval((int)(basket.getX() - 90),
                    (int)(basket.getY() - 90),
                    basket.getWidth() + 180,
                    basket.getHeight() + 180);
        }
    }

    private void drawComboFlash(Graphics2D g) {
        if (comboFlashTimer <= 0) return;
        float alpha = Math.min(1f, comboFlashTimer / 30f);
        float scale = 1f + (1f - comboFlashTimer / 90f) * 0.5f;
        Font f = FontManager.getBold((int)(28 * scale));
        g.setFont(f);
        FontMetrics fm = g.getFontMetrics();
        int tw = fm.stringWidth(comboFlashText);
        int tx = (GamePanel.ARENA_W - tw) / 2;
        g.setColor(new Color(80, 40, 0,
                (int)(alpha * 160)));
        g.drawString(comboFlashText, tx + 2,
                GamePanel.H / 2 - 28);
        g.setColor(new Color(255, 220, 50,
                (int)(alpha * 240)));
        g.drawString(comboFlashText, tx,
                GamePanel.H / 2 - 30);
    }

    private void drawToast(Graphics2D g) {
        float alpha = Math.min(1f, toastTimer / 30f);
        int tw = 340, th = 42;
        int tx = (GamePanel.W - tw) / 2, ty = 28;
        RenderUtils.drawGradientPanel(g, tx, ty, tw, th,
                new Color(28, 78, 18, (int)(alpha * 230)),
                new Color(18, 55, 10, (int)(alpha * 230)),
                new Color(118, 212, 80, (int)(alpha * 255)),
                1.5f, 12);
        g.setFont(FontManager.getEmoji(14));
        g.setColor(new Color(255, 255, 255,
                (int)(alpha * 255)));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(toastText,
                tx + (tw - fm.stringWidth(toastText)) / 2,
                ty + 27);
    }

    private String getPowerUpToast(PowerUpType t) {
        switch (t) {
            case MAGNET:        return "\uD83E\uDDF2 Magnet activated!";
            case TIME_PLUS:     return "\u23F0 +15 seconds!";
            case SHIELD:        return "\uD83D\uDEE1\uFE0F Shield ready!";
            case DOUBLE_POINTS: return "2\uFE0F\u20E3 Double Points!";
            case SLOW_TIME:     return "\uD83D\uDD5B Slow Time!";
            case WIDE_BASKET:   return "\uD83E\uDDF3 Wide Basket!";
            default: return "Power-up!";
        }
    }

    public void showToast(String text) {
        toastText  = text;
        toastTimer = 180;
    }

    public void showComboFlash(String text) {
        comboFlashText  = text;
        comboFlashTimer = 90;
    }

    @Override
    public void onKeyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        float cx = basket.getX() + basket.getWidth() / 2f;
        if (code == KeyEvent.VK_LEFT
                || code == KeyEvent.VK_A)
            basket.setTargetX(Math.max(40, cx - 30));
        else if (code == KeyEvent.VK_RIGHT
                || code == KeyEvent.VK_D)
            basket.setTargetX(Math.min(
                    GamePanel.ARENA_W - 40, cx + 30));
        else if (code == KeyEvent.VK_P)
            panel.getScreenManager()
                 .switchTo(GameScreenType.PAUSED);
        else if (code == KeyEvent.VK_ESCAPE)
            panel.getScreenManager()
                 .switchTo(GameScreenType.MAIN_MENU);
        else if (code == KeyEvent.VK_M)
            panel.getSoundManager().toggleMute();
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        if (e.getX() < GamePanel.ARENA_W)
            basket.setTargetX(e.getX());
        float dir = (e.getX()
                - (basket.getX() + basket.getWidth() / 2f))
                / GamePanel.ARENA_W;
        character.setLean(dir * 0.3f);
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        int mx = e.getX(), my = e.getY();
        if (mx > GamePanel.ARENA_W) {
            if (new Rectangle(GamePanel.ARENA_W + 15,
                    GamePanel.H - 65, 60, 32)
                    .contains(mx, my))
                panel.getSoundManager().toggleMute();
            if (new Rectangle(GamePanel.ARENA_W + 85,
                    GamePanel.H - 65, 60, 32)
                    .contains(mx, my))
                panel.getScreenManager()
                     .switchTo(GameScreenType.PAUSED);
        }
    }

    public void setPlayerName(String name) {
        playerName = name;
    }

    public void setDifficulty(Difficulty d) {
        difficulty = d;
    }

    public GameState getState() { return state; }
}