package OOP_PROJECT.CatchTheBall.src.core;

import OOP_PROJECT.CatchTheBall.src.interfaces.Drawable;
import OOP_PROJECT.CatchTheBall.src.interfaces.Updatable;

import java.awt.Graphics2D;

public abstract class GameObject implements Drawable, Updatable {
    protected boolean visible = true;
    protected boolean active  = true;

    @Override public abstract void update();
    @Override public abstract void draw(Graphics2D g);

    public boolean isVisible()          { return visible; }
    public void    setVisible(boolean v){ visible = v; }
    public boolean isActive()           { return active; }
    public void    setActive(boolean a) { active = a; }
}