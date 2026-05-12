package OOP_PROJECT.CatchTheBall.src.managers;

import OOP_PROJECT.CatchTheBall.src.entities.Particle;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ParticleManager {
    private List<Particle> particles;
    private Random         rand;
    private static final int MAX = 80;

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
        if (particles.size() >= MAX) return;
        int actual = Math.min(count, MAX - particles.size());
        for (int i = 0; i < actual; i++) {
            float vx = (float)(rand.nextGaussian() * 2.5);
            float vy = -2f - rand.nextFloat() * 3f;
            int   sz = 6 + rand.nextInt(6);
            particles.add(new Particle(x, y, vx, vy, color, sz, 18 + rand.nextInt(10)));
        }
    }

    public void spawnFirework(int x, int y, int count) {
        if (particles.size() >= MAX) return;
        for (int i = 0; i < count && particles.size() < MAX; i++) {
            float angle = (float)(i * Math.PI * 2 / count);
            float speed = 3f + rand.nextFloat() * 4f;
            Color c     = Color.getHSBColor(rand.nextFloat(), 1f, 1f);
            particles.add(new Particle(x, y,
                    (float)(Math.cos(angle) * speed),
                    (float)(Math.sin(angle) * speed),
                    c, 8, 50f));
        }
    }

    public void spawnComboParticles(int x, int y, int comboLevel) {
        if (particles.size() >= MAX) return;
        int   count;
        Color c;
        if      (comboLevel >= 12) { count = 30; c = new Color(200,  0, 255); }
        else if (comboLevel >= 8)  { count = 20; c = new Color(255, 50,  50); }
        else if (comboLevel >= 5)  { count = 12; c = new Color(255,150,   0); }
        else                       { count =  6; c = new Color(255,230,  50); }
        spawnBurst(x, y, c, count);
        if (comboLevel >= 12) spawnFirework(x, y, 20);
    }

    public void spawnFloatingText(int x, int y, String text, Color color) {
        if (particles.size() >= MAX) return;
        float vx = (float)(rand.nextGaussian() * 0.5);
        particles.add(new Particle(x, y, vx, -2.5f, text, color, 50f));
    }

    public void spawnLevelUpBurst(int x, int y) {
        spawnFirework(x, y, 50);
        spawnBurst(x, y, new Color(255, 215, 0), 20);
    }

    public void spawnShockwave(int x, int y, Color c) {
        for (int i = 0; i < 16 && particles.size() < MAX; i++) {
            float angle = (float)(i * Math.PI * 2 / 16);
            float spd   = 5f + rand.nextFloat() * 3f;
            particles.add(new Particle(x, y,
                    (float)(Math.cos(angle) * spd),
                    (float)(Math.sin(angle) * spd),
                    c, 6, 20f));
        }
    }

    public void clear()    { particles.clear(); }
    public int  getCount() { return particles.size(); }
}