import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Abstract base class for all game objects (Ball, Basket).
 * Provides shared position, size, and collision data.
 *
 * Demonstrates: Abstraction and Inheritance (core OOP principles)
 */
public abstract class Entity {

    protected double x, y;
    protected int width, height;

    public Entity(double x, double y, int width, int height) {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
    }

    /** Called every frame to advance the entity's state. */
    public abstract void update();

    /** Called every frame to render the entity. */
    public abstract void draw(Graphics2D g);

    /** Returns the axis-aligned bounding box for collision detection. */
    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    // ── Getters ──────────────────────────────────────────────────────
    public double getX()     { return x; }
    public double getY()     { return y; }
    public int    getWidth() { return width; }
    public int    getHeight(){ return height; }
}
