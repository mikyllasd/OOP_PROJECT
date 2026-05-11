package core;
import interfaces.Drawable;
import interfaces.Updatable;
import managers.GamePanel;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
public abstract class Screen implements Drawable, Updatable {
    protected GamePanel panel;
    protected int tickCount;
    public Screen(GamePanel panel) {
        this.panel = panel;
        this.tickCount = 0;
    }
    @Override public abstract void update();
    @Override public abstract void draw(Graphics2D g);
    public void onEnter() { tickCount = 0; }
    public void onExit()  {}
    public void onKeyPressed(KeyEvent e)     {}
    public void onKeyTyped(KeyEvent e)       {}
    public void onKeyReleased(KeyEvent e)    {}
    public void onMouseMoved(MouseEvent e)   {}
    public void onMouseClicked(MouseEvent e) {}
    public void onMousePressed(MouseEvent e) {}
    public void onMouseReleased(MouseEvent e){}
    protected int getTickCount() { return tickCount; }
}