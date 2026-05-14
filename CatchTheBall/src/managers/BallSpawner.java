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
        else if (difficulty == Difficulty.NORMAL) maxBalls = 7;
        else                                      maxBalls = 10;
        if (balls.size() >= maxBalls) return;

        spawnTimer--;
        if (spawnTimer <= 0) {
            // For NORMAL and HARD, chance to spawn 2 balls at once
            balls.add(createBall(level, difficulty));
            if (difficulty == Difficulty.NORMAL && rand.nextFloat() < 0.30f) {
                balls.add(createBall(level, difficulty));
            } else if (difficulty == Difficulty.HARD && rand.nextFloat() < 0.55f) {
                balls.add(createBall(level, difficulty));
                if (rand.nextFloat() < 0.25f) {
                    balls.add(createBall(level, difficulty)); // occasional 3rd ball on HARD
                }
            }

            int base;
            if      (difficulty == Difficulty.EASY)   base = Math.max(110, 150 - level * 2);
            else if (difficulty == Difficulty.NORMAL) base = Math.max(45,   80 - level * 2);
            else                                      base = Math.max(25,   55 - level * 2);
            spawnTimer = base + rand.nextInt(15);
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
        else if (diff == Difficulty.NORMAL) maxSpeed = 10.5f;
        else                                maxSpeed = 15.0f;
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
            if (r < 3)   return BallType.RAINBOW;
            if (r < 7)   return BallType.FROZEN;
            if (r < 13)  return BallType.GIANT;
            if (r < 20)  return BallType.TINY;
            // Bombs appear earlier and more often
            if (level >= 2 && r < 80)  return BallType.BOMB;
            if (level >= 1 && r < 130) return BallType.BOMB; // extra bomb roll
            if (r < 160)               return BallType.STRAWBERRY;
            if (level >= 3 && r < 230) return BallType.MUSHROOM;
            if (level >= 4 && r < 300) return BallType.EGGPLANT;
            if (level >= 6 && r < 360) return BallType.GOLDEN_APPLE;
            if (level >= 6 && r < 420) return BallType.MYSTERY;
            if (r < 680) return BallType.APPLE;
            return BallType.ORANGE;
        }

        // HARD — bombs are very frequent
        if (r < 3)  return BallType.RAINBOW;
        if (r < 8)  return BallType.FROZEN;
        if (r < 15) return BallType.GIANT;
        if (r < 22) return BallType.TINY;
        if (level >= 8) {
            if (r < 50)  return BallType.GOLDEN_APPLE;
            if (r < 180) return BallType.BOMB;      // massively increased
            if (r < 230) return BallType.MYSTERY;
            if (r < 330) return BallType.STRAWBERRY;
            if (r < 470) return BallType.MUSHROOM;
            if (r < 580) return BallType.EGGPLANT;
            if (r < 800) return BallType.APPLE;
            return BallType.ORANGE;
        }
        // HARD early levels
        if (r < 100) return BallType.BOMB;           // bombs from level 1 on HARD
        if (r < 160) return BallType.STRAWBERRY;
        if (level >= 3 && r < 280) return BallType.MUSHROOM;
        if (level >= 4 && r < 400) return BallType.EGGPLANT;
        if (r < 680) return BallType.APPLE;
        return BallType.ORANGE;
    }

    public void reset() { spawnTimer = 120; }
}