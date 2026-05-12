package OOP_PROJECT.CatchTheBall.src.managers;

import OOP_PROJECT.CatchTheBall.src.screens.ScreenManager;

import java.awt.event.*;
import java.util.*;

public class InputManager implements KeyListener, MouseMotionListener, MouseListener {
    private Set<Integer>  heldKeys;
    private int           mouseX, mouseY;
    private ScreenManager screenManager;
    private GamePanel     panel;

    public InputManager(ScreenManager sm, GamePanel panel) {
        this.screenManager = sm;
        this.panel         = panel;
        heldKeys = new HashSet<>();
    }

    private MouseEvent translate(MouseEvent e) {
        return new MouseEvent(
            e.getComponent(), e.getID(), e.getWhen(),
            e.getModifiersEx(),
            panel.translateX(e.getX()),
            panel.translateY(e.getY()),
            e.getClickCount(),
            e.isPopupTrigger(),
            e.getButton()
        );
    }

    @Override public void keyPressed(KeyEvent e)  { heldKeys.add(e.getKeyCode());    screenManager.getCurrentScreen().onKeyPressed(e); }
    @Override public void keyTyped(KeyEvent e)    { screenManager.getCurrentScreen().onKeyTyped(e); }
    @Override public void keyReleased(KeyEvent e) { heldKeys.remove(e.getKeyCode()); screenManager.getCurrentScreen().onKeyReleased(e); }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = panel.translateX(e.getX());
        mouseY = panel.translateY(e.getY());
        screenManager.getCurrentScreen().onMouseMoved(translate(e));
    }

    @Override public void mouseDragged(MouseEvent e)  { mouseMoved(e); }
    @Override public void mouseClicked(MouseEvent e)  { screenManager.getCurrentScreen().onMouseClicked(translate(e)); }
    @Override public void mousePressed(MouseEvent e)  { screenManager.getCurrentScreen().onMousePressed(translate(e)); }
    @Override public void mouseReleased(MouseEvent e) { screenManager.getCurrentScreen().onMouseReleased(translate(e)); }
    @Override public void mouseEntered(MouseEvent e)  {}
    @Override public void mouseExited(MouseEvent e)   {}

    public boolean isKeyHeld(int k) { return heldKeys.contains(k); }
    public int     getMouseX()      { return mouseX; }
    public int     getMouseY()      { return mouseY; }
}