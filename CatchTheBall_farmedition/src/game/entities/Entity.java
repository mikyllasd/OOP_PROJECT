package game.entities;

import java.awt.Graphics2D;

public abstract class Entity {
    protected float x, y;
    protected int width, height;
    protected boolean active;

    public Entity(float x, float y, int width, int height) {
        this.x      = x;
        this.y      = y;
        this.width  = width;
        this.height = height;
        this.active = true;
    }

    public abstract void update();
    public abstract void draw(Graphics2D g);

    public float   getX()      { return x; }
    public float   getY()      { return y; }
    public int     getWidth()  { return width; }
    public int     getHeight() { return height; }
    public boolean isActive()  { return active; }
    public void    setActive(boolean active) { this.active = active; }

    /** Optional smooth-movement target â€” overridden by Character. */
    public void setTarget(float tx, float ty) { /* no-op by default */ }

    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void translate(float dx, float dy) { this.x += dx; this.y += dy; }

    public boolean intersects(Entity other) {
        return x < other.x + other.width  &&
               x + width  > other.x       &&
               y < other.y + other.height &&
               y + height > other.y;
    }
}
