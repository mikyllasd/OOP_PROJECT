package managers;

import java.awt.event.*;
import java.util.HashSet;
import java.util.Set;

public class InputManager
        implements KeyListener, MouseMotionListener,
                   MouseListener {

    private Set<Integer> heldKeys;
    private int mouseX;
    private int mouseY;
    private boolean mouseClicked;
    private int clickX;
    private int clickY;

    private screens.ScreenManager screenManager;

    public InputManager(screens.ScreenManager sm) {
        this.screenManager = sm;
        heldKeys           = new HashSet<>();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        heldKeys.add(e.getKeyCode());
        screenManager.getCurrentScreen()
                     .onKeyPressed(e);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        screenManager.getCurrentScreen()
                     .onKeyTyped(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        heldKeys.remove(e.getKeyCode());
        screenManager.getCurrentScreen()
                     .onKeyReleased(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
        screenManager.getCurrentScreen()
                     .onMouseMoved(e);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        clickX        = e.getX();
        clickY        = e.getY();
        mouseClicked  = true;
        screenManager.getCurrentScreen()
                     .onMouseClicked(e);
    }

    @Override public void mousePressed(MouseEvent e)  {
        screenManager.getCurrentScreen().onMousePressed(e);
    }
    @Override public void mouseReleased(MouseEvent e) {
        screenManager.getCurrentScreen().onMouseReleased(e);
    }
    @Override public void mouseEntered(MouseEvent e)  {}
    @Override public void mouseExited(MouseEvent e)   {}

    public boolean isKeyHeld(int keyCode) {
        return heldKeys.contains(keyCode);
    }

    public int getMouseX()         { return mouseX; }
    public int getMouseY()         { return mouseY; }
    public boolean wasMouseClicked(){ return mouseClicked; }
    public int getClickX()         { return clickX; }
    public int getClickY()         { return clickY; }
    public void consumeClick()     { mouseClicked = false; }
}