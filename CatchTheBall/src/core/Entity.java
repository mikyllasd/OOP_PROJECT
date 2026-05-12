package OOP_PROJECT.CatchTheBall.src.core;

import OOP_PROJECT.CatchTheBall.src.interfaces.Collidable;
import OOP_PROJECT.CatchTheBall.src.interfaces.Drawable;
import OOP_PROJECT.CatchTheBall.src.interfaces.Updatable;

import java.awt.Graphics2D;

public abstract class Entity implements Drawable, Updatable, Collidable {
    protected float   x, y;
    protected int     width, height;
    protected boolean active;

    public Entity(float x, float y, int width, int height) {
        this.x = x; this.y = y;
        this.width = width; this.height = height;
        this.active = true;
    }

    @Override public abstract void update();
    @Override public abstract void draw(Graphics2D g);

    @Override
    public boolean intersects(Object other) {
        if (!(other instanceof Entity)) return false;
        Entity o = (Entity) other;
        return x < o.x + o.width  && x + width  > o.x
            && y < o.y + o.height && y + height > o.y;
    }

    public float   getX()               { return x; }
    public float   getY()               { return y; }
    public int     getWidth()           { return width; }
    public int     getHeight()          { return height; }
    public boolean isActive()           { return active; }
    public void    setActive(boolean a) { active = a; }
    public void    setX(float x)        { this.x = x; }
    public void    setY(float y)        { this.y = y; }
}