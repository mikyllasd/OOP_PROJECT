package OOP_PROJECT.CatchTheBall.src.main;

import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {

    private GamePanel gamePanel;

    public GameFrame() {
        setTitle("\uD83C\uDF3E Catch the Ball - Farm Edition");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        gamePanel = new GamePanel();
        add(gamePanel);

        GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        gd.setFullScreenWindow(this);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try { gamePanel.getPlayerData().save(); } catch (Exception ignored) {}
                gamePanel.stopGameLoop();
                gd.setFullScreenWindow(null);
                dispose();
                System.exit(0);
            }
        });

        gamePanel.requestFocusInWindow();
        gamePanel.startGameLoop();
    }
}