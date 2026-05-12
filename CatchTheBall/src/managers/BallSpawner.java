package OOP_PROJECT.CatchTheBall.src.managers;

import OOP_PROJECT.CatchTheBall.src.entities.Ball;
import OOP_PROJECT.CatchTheBall.src.enums.BallType;
import OOP_PROJECT.CatchTheBall.src.enums.Difficulty;

import java.util.*;

public class BallSpawner {
    private int    spawnTimer;
    private Random rand;
    private int    arenaWidth;

    public BallSpawner(int arenaWidth) {
        this.arenaWidth = arenaWidth;
        this.rand       = new Random();
        this.spawnTimer = 120;
    }

    public void update(List<Ball> balls, int level, Difficulty difficulty) {
        int maxBalls;
        if      (difficulty == Difficulty.EASY)   maxBalls = 3;
        else if (difficulty == Difficulty.NORMAL) maxBalls = 5;
        else                                      maxBalls = 7;
        if (balls.size() >= maxBalls) return;

        spawnTimer--;
        if (spawnTimer <= 0) {
            balls.add(createBall(level, difficulty));
            int base;
            if      (difficulty == Difficulty.EASY)   base = Math.max(110, 150 - level * 2);
            else if (difficulty == Difficulty.NORMAL) base = Math.max(75,  115 - level * 2);
            else                                      base = Math.max(50,   90 - level * 2);
            spawnTimer = base + rand.nextInt(20);
        }
    }

    public void spawnWave(List<Ball> balls, int level, Difficulty difficulty, int count) {
        for (int i = 0; i < count; i++) {
            Ball b = createBall(level, difficulty);
            b.setX(b.getX() + i * 55);
            balls.add(b);
        }
    }

    private Ball createBall(int level, Difficulty diff) {
        float x         = 30 + rand.nextFloat() * (arenaWidth - 70);
        float baseSpeed = 0.7f + level * 0.07f + rand.nextFloat() * 0.3f;
        float maxSpeed;
        if      (diff == Difficulty.EASY)   maxSpeed = 5.5f;
        else if (diff == Difficulty.NORMAL) maxSpeed = 8.5f;
        else                                maxSpeed = 12.0f;
        float speed = Math.min(baseSpeed * diff.getSpeedMultiplier(), maxSpeed);
        return new Ball(x, -40, rollBallType(level, diff), speed);
    }

    private BallType rollBallType(int level, Difficulty diff) {
        int r = rand.nextInt(1000);

        if (diff == Difficulty.EASY) {
            if (r < 2)  return BallType.RAINBOW;
            if (r < 5)  return BallType.FROZEN;
            if (r < 10) return BallType.GIANT;
            if (r < 15) return BallType.TINY;
            if (r < 25) return BallType.STRAWBERRY;
            if (level >= 6 && r < 70)  return BallType.MUSHROOM;
            if (level >= 8 && r < 100) return BallType.EGGPLANT;
            if (r < 550) return BallType.APPLE;
            return BallType.ORANGE;
        }

        if (diff == Difficulty.NORMAL) {
            if (r < 3)  return BallType.RAINBOW;
            if (r < 7)  return BallType.FROZEN;
            if (r < 13) return BallType.GIANT;
            if (r < 20) return BallType.TINY;
            if (r < 40) return BallType.STRAWBERRY;
            if (level >= 3 && r < 110) return BallType.MUSHROOM;
            if (level >= 4 && r < 180) return BallType.EGGPLANT;
            if (level >= 6 && r < 220) return BallType.BOMB;
            if (level >= 8 && r < 260) return BallType.GOLDEN_APPLE;
            if (level >= 8 && r < 300) return BallType.MYSTERY;
            if (r < 620) return BallType.APPLE;
            return BallType.ORANGE;
        }

        // HARD
        if (r < 3)  return BallType.RAINBOW;
        if (r < 8)  return BallType.FROZEN;
        if (r < 15) return BallType.GIANT;
        if (r < 22) return BallType.TINY;
        if (level >= 8) {
            if (r < 40)  return BallType.GOLDEN_APPLE;
            if (r < 90)  return BallType.BOMB;
            if (r < 140) return BallType.MYSTERY;
            if (r < 240) return BallType.STRAWBERRY;
            if (r < 390) return BallType.MUSHROOM;
            if (r < 510) return BallType.EGGPLANT;
            if (r < 750) return BallType.APPLE;
            return BallType.ORANGE;
        }
        if (r < 50)  return BallType.STRAWBERRY;
        if (level >= 3 && r < 150) return BallType.MUSHROOM;
        if (level >= 4 && r < 250) return BallType.EGGPLANT;
        if (r < 600) return BallType.APPLE;
        return BallType.ORANGE;
    }

    public void reset() { spawnTimer = 120; }
}