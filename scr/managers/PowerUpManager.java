package managers;
import entities.Ball;
import entities.PowerUp;
import enums.PowerUpType;
import models.GameState;
import java.util.*;
public class PowerUpManager {
    private int spawnTimer;
    private Random rand;
    private int arenaWidth;
    public PowerUpManager(int arenaWidth) {
        this.arenaWidth = arenaWidth;
        this.rand       = new Random();
        this.spawnTimer = 300;
    }
    public void update(List<PowerUp> powerUps, GameState state) {
        spawnTimer--;
        if (spawnTimer <= 0) {
            if (rand.nextInt(4) == 0)
                powerUps.add(createPowerUp(state));
            spawnTimer = 300 + rand.nextInt(300);
        }
        tickActiveEffects(state);
    }
    private PowerUp createPowerUp(GameState state) {
        float x = 30 + rand.nextFloat() * (arenaWidth - 70);
        PowerUpType type = chooseSmartType(state);
        return new PowerUp(x, -40, type);
    }
    private PowerUpType chooseSmartType(GameState state) {
        if (state.getTimeLeft() < 30 && rand.nextInt(3) == 0)
            return PowerUpType.TIME_PLUS;
        if (!state.isShieldActive() && rand.nextInt(4) == 0)
            return PowerUpType.SHIELD;
        PowerUpType[] all = PowerUpType.values();
        return all[rand.nextInt(all.length)];
    }
    public void activate(PowerUpType type, GameState state,
                         SoundManager sound, List<Ball> balls) {
        sound.playPowerUp();
        switch (type) {
            case MAGNET:
                state.setMagnetActive(true);
                state.setMagnetTimer(300);
                break;
            case TIME_PLUS:
                state.addTime(15);
                break;
            case SHIELD:
                state.setShieldActive(true);
                break;
            case DOUBLE_POINTS:
                state.setDoublePointsActive(true);
                state.setDoubleTimer(480);
                break;
            case SLOW_TIME:
                state.setSlowTimeActive(true);
                state.setSlowTimer(300);
                for (Ball b : balls) b.applySlowTime();
                break;
            case WIDE_BASKET:
                state.setWideBasketActive(true);
                state.setWideTimer(400);
                break;
        }
    }
    private void tickActiveEffects(GameState state) {
        if (state.isMagnetActive()) {
            state.setMagnetTimer(state.getMagnetTimer() - 1);
            if (state.getMagnetTimer() <= 0) state.setMagnetActive(false);
        }
        if (state.isDoublePointsActive()) {
            state.setDoubleTimer(state.getDoubleTimer() - 1);
            if (state.getDoubleTimer() <= 0) state.setDoublePointsActive(false);
        }
        if (state.isSlowTimeActive()) {
            state.setSlowTimer(state.getSlowTimer() - 1);
            if (state.getSlowTimer() <= 0) state.setSlowTimeActive(false);
        }
        if (state.isWideBasketActive()) {
            state.setWideTimer(state.getWideTimer() - 1);
            if (state.getWideTimer() <= 0) state.setWideBasketActive(false);
        }
    }
    public void reset() { spawnTimer = 300; }
}