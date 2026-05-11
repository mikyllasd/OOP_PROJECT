package managers;
import entities.Ball;
import enums.BallType;
import enums.Difficulty;
import java.util.*;
public class BallSpawner {
    private int spawnTimer;
    private Random rand;
    private int arenaWidth;
    public BallSpawner(int arenaWidth) {
        this.arenaWidth = arenaWidth;
        this.rand       = new Random();
        this.spawnTimer = 60;
    }
    public void update(List<Ball> balls, int level, Difficulty difficulty) {
        spawnTimer--;
        if (spawnTimer <= 0) {
            balls.add(createBall(level, difficulty));
            int base   = Math.max(18, 60 - level * 4);
            spawnTimer = base + rand.nextInt(20);
        }
    }
    public void spawnWave(List<Ball> balls, int level, Difficulty difficulty, int count) {
        for (int i = 0; i < count; i++) {
            Ball b = createBall(level, difficulty);
            b.setX(b.getX() + i * 60);
            balls.add(b);
        }
    }
    private Ball createBall(int level, Difficulty diff) {
        float x = 30 + rand.nextFloat() * (arenaWidth - 70);
        float baseSpeed = 1.5f + level * 0.3f + rand.nextFloat() * 1.5f;
        float speed = Math.min(baseSpeed * diff.getSpeedMultiplier(), 9f);
        BallType type = rollBallType(level);
        return new Ball(x, -40, type, speed);
    }
    private BallType rollBallType(int level) {
        int r = rand.nextInt(100);
        if (level >= 8) {
            if (r < 3)  return BallType.GOLDEN_APPLE;
            if (r < 8)  return BallType.BOMB;
            if (r < 13) return BallType.MYSTERY;
            if (r < 23) return BallType.STRAWBERRY;
            if (r < 38) return BallType.MUSHROOM;
            if (r < 50) return BallType.EGGPLANT;
            if (r < 75) return BallType.APPLE;
            return BallType.ORANGE;
        }
        if (r < 5)  return BallType.STRAWBERRY;
        if (r < 15) return BallType.MUSHROOM;
        if (r < 25) return BallType.EGGPLANT;
        if (r < 60) return BallType.APPLE;
        return BallType.ORANGE;
    }
    public void reset() { spawnTimer = 60; }
}