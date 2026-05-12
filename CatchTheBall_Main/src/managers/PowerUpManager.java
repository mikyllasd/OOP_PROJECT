package OOP_PROJECT.CatchTheBall.src.managers;

import OOP_PROJECT.CatchTheBall.src.audio.SoundManager;
import OOP_PROJECT.CatchTheBall.src.entities.Ball;
import OOP_PROJECT.CatchTheBall.src.entities.PowerUp;
import OOP_PROJECT.CatchTheBall.src.enums.PowerUpType;
import OOP_PROJECT.CatchTheBall.src.models.GameState;

import java.util.*;

public class PowerUpManager {
    private int    spawnTimer;
    private Random rand;
    private int    arenaWidth;

    public PowerUpManager(int arenaWidth) {
        this.arenaWidth = arenaWidth;
        this.rand       = new Random();
        this.spawnTimer = 300;
    }

    public void update(List<PowerUp> powerUps, GameState state) {
        spawnTimer--;
        if (spawnTimer <= 0) {
            if (rand.nextInt(4) == 0) powerUps.add(createPowerUp(state));
            spawnTimer = 400 + rand.nextInt(100);
        }
        tickActiveEffects(state);
    }

    private PowerUp createPowerUp(GameState state) {
        float x = 30 + rand.nextFloat() * (arenaWidth - 70);
        return new PowerUp(x, -40, chooseSmartType(state));
    }

    private PowerUpType chooseSmartType(GameState state) {
        if (state.getTimeLeft() < 30 && rand.nextInt(3) == 0) return PowerUpType.TIME_PLUS;
        if (!state.isShieldActive()  && rand.nextInt(4) == 0) return PowerUpType.SHIELD;
        PowerUpType[] all = PowerUpType.values();
        return all[rand.nextInt(all.length)];
    }

    public void activate(PowerUpType type, GameState state, SoundManager sound) {
        sound.playPowerUp();
        switch (type) {
            case MAGNET:        state.setMagnetActive(true);       state.setMagnetTimer(300); break;
            case TIME_PLUS:     state.addTime(15);                                             break;
            case SHIELD:        state.setShieldActive(true);                                   break;
            case DOUBLE_POINTS: state.setDoublePointsActive(true); state.setDoubleTimer(480); break;
            case SLOW_TIME:     state.setSlowTimeActive(true);     state.setSlowTimer(300);   break;
            case WIDE_BASKET:   state.setWideBasketActive(true);   state.setWideTimer(400);   break;
        }
    }

    public void applyFrozen(GameState state, List<Ball> balls) {
        state.setFrozenActive(true);
        state.setFrozenTimer(300);
        for (Ball b : balls) b.setSpeed(b.getSpeed() * 0.4f);
    }

    private void tickActiveEffects(GameState state) {
        if (state.isMagnetActive())       { state.setMagnetTimer(state.getMagnetTimer()-1);       if (state.getMagnetTimer()<=0) state.setMagnetActive(false); }
        if (state.isDoublePointsActive()) { state.setDoubleTimer(state.getDoubleTimer()-1);       if (state.getDoubleTimer()<=0) state.setDoublePointsActive(false); }
        if (state.isSlowTimeActive())     { state.setSlowTimer(state.getSlowTimer()-1);           if (state.getSlowTimer()<=0)  state.setSlowTimeActive(false); }
        if (state.isWideBasketActive())   { state.setWideTimer(state.getWideTimer()-1);           if (state.getWideTimer()<=0)  state.setWideBasketActive(false); }
        if (state.isFrozenActive())       { state.setFrozenTimer(state.getFrozenTimer()-1);       if (state.getFrozenTimer()<=0) state.setFrozenActive(false); }
    }

    public void reset() { spawnTimer = 300; }
}