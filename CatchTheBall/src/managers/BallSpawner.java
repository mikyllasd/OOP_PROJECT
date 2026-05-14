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
        if      (difficulty == Difficulty.EASY)   maxBalls = 6;
        else if (difficulty == Difficulty.NORMAL) maxBalls = 9;
        else                                      maxBalls = 13;
        if (balls.size() >= maxBalls) return;

        spawnTimer--;
        if (spawnTimer <= 0) {
            // Always spawn at least one ball
            balls.add(createBall(level, difficulty));

            if (difficulty == Difficulty.EASY && rand.nextFloat() < 0.20f) {
                // Easy: occasional 2nd ball
                balls.add(createBall(level, difficulty));
            } else if (difficulty == Difficulty.NORMAL) {
                // Normal: frequent 2nd ball, occasional 3rd
                if (rand.nextFloat() < 0.45f) {
                    balls.add(createBall(level, difficulty));
                }
                if (rand.nextFloat() < 0.20f) {
                    balls.add(createBall(level, difficulty));
                }
            } else if (difficulty == Difficulty.HARD) {
                // Hard: almost always 2nd, frequent 3rd, occasional 4th
                if (rand.nextFloat() < 0.75f) {
                    balls.add(createBall(level, difficulty));
                }
                if (rand.nextFloat() < 0.50f) {
                    balls.add(createBall(level, difficulty));
                }
                if (rand.nextFloat() < 0.25f) {
                    balls.add(createBall(level, difficulty));
                }
            }

            int base;
            if      (difficulty == Difficulty.EASY)   base = Math.max(55,  90 - level * 2);
            else if (difficulty == Difficulty.NORMAL) base = Math.max(35,   65 - level * 2);
            else                                      base = Math.max(15,   40 - level * 2);
            spawnTimer = base + rand.nextInt(12);
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
        float baseSpeed = 0.9f + level * 0.09f + rand.nextFloat() * 0.4f;
        float maxSpeed;
        if      (diff == Difficulty.EASY)   maxSpeed = 7.0f;
        else if (diff == Difficulty.NORMAL) maxSpeed = 12.0f;
        else                                maxSpeed = 18.0f;
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
            if (r < 30) return BallType.STRAWBERRY;
            if (level >= 5 && r < 80)  return BallType.MUSHROOM;
            if (level >= 7 && r < 110) return BallType.EGGPLANT;
            if (level >= 9 && r < 130) return BallType.BOMB;
            if (r < 560) return BallType.APPLE;
            return BallType.ORANGE;
        }

        if (diff == Difficulty.NORMAL) {
            if (r < 3)   return BallType.RAINBOW;
            if (r < 8)   return BallType.FROZEN;
            if (r < 14)  return BallType.GIANT;
            if (r < 22)  return BallType.TINY;
            // Bombs — heavy presence from level 1
            if (r < 100)               return BallType.BOMB;
            if (level >= 2 && r < 180) return BallType.BOMB;
            if (r < 220)               return BallType.STRAWBERRY;
            if (level >= 3 && r < 300) return BallType.MUSHROOM;
            if (level >= 4 && r < 380) return BallType.EGGPLANT;
            if (level >= 5 && r < 440) return BallType.GOLDEN_APPLE;
            if (level >= 5 && r < 500) return BallType.MYSTERY;
            if (r < 720) return BallType.APPLE;
            return BallType.ORANGE;
        }

        // HARD — bombs everywhere
        if (r < 3)  return BallType.RAINBOW;
        if (r < 8)  return BallType.FROZEN;
        if (r < 14) return BallType.GIANT;
        if (r < 20) return BallType.TINY;
        if (level >= 8) {
            if (r < 50)  return BallType.GOLDEN_APPLE;
            if (r < 280) return BallType.BOMB;       // 23% chance — very heavy
            if (r < 330) return BallType.MYSTERY;
            if (r < 430) return BallType.STRAWBERRY;
            if (r < 560) return BallType.MUSHROOM;
            if (r < 670) return BallType.EGGPLANT;
            if (r < 870) return BallType.APPLE;
            return BallType.ORANGE;
        }
        // HARD early levels — bombs still very common
        if (r < 200) return BallType.BOMB;
        if (r < 270) return BallType.STRAWBERRY;
        if (level >= 3 && r < 390) return BallType.MUSHROOM;
        if (level >= 4 && r < 510) return BallType.EGGPLANT;
        if (r < 750) return BallType.APPLE;
        return BallType.ORANGE;
    }

    public void reset() { spawnTimer = 120; }
}