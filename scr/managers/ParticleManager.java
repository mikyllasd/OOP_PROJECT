package managers;
import entities.Particle;
import java.awt.*;
import java.util.*;
public class ParticleManager {
    private List<Particle> particles;
    private Random rand;
    public ParticleManager() {
        particles = new ArrayList<>();
        rand      = new Random();
    }
    public void update() {
        particles.removeIf(Particle::isDead);
        for (Particle p : particles) p.update();
    }
    public void draw(Graphics2D g) {
        for (Particle p : particles) p.draw(g);
    }
    public void spawnBurst(int x, int y, Color color, int count) {
        for (int i = 0; i < count; i++) {
            float vx = (float)(rand.nextGaussian() * 2.5);
            float vy = -2f - rand.nextFloat() * 3f;
            int   sz = 6 + rand.nextInt(6);
            float lf = 30 + rand.nextInt(20);
            particles.add(new Particle(x, y, vx, vy, color, sz, lf));
        }
    }
    public void spawnFloatingText(int x, int y, String text, Color color) {
        float vx = (float)(rand.nextGaussian() * 0.5);
        particles.add(new Particle(x, y, vx, -2.5f, text, color, 50f));
    }
    public void spawnCoinFly(int fromX, int fromY, int toX, int toY) {
        particles.add(new Particle(fromX, fromY,
                (toX - fromX) * 0.03f, (toY - fromY) * 0.03f,
                "\uD83E\uDE99", new Color(255, 215, 50), 40f));
    }
    public void clear()        { particles.clear(); }
    public int getCount()      { return particles.size(); }
}