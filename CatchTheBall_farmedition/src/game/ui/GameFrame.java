package game.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GameFrame extends JFrame {
    private GamePanel gamePanel;

    public GameFrame() {
        setTitle("Catch the Ball - Farm Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        gamePanel = new GamePanel();
        add(gamePanel);

        // Start maximized
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(800, 600));
        setVisible(true);

        // Listen for resize
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                gamePanel.onResize(getContentPane().getWidth(), getContentPane().getHeight());
            }
        });

        // F11 = toggle fullscreen
        addKeyListener(new KeyAdapter() {
            private boolean fullscreen = false;
            private GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_F11) {
                    fullscreen = !fullscreen;
                    dispose();
                    setUndecorated(fullscreen);
                    if (fullscreen) {
                        gd.setFullScreenWindow(GameFrame.this);
                    } else {
                        gd.setFullScreenWindow(null);
                        setExtendedState(JFrame.MAXIMIZED_BOTH);
                    }
                    setVisible(true);
                    gamePanel.requestFocusInWindow();
                }
            }
        });

        gamePanel.requestFocusInWindow();
        gamePanel.startGameLoop();
    }
}
