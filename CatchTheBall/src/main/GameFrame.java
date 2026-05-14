package OOP_PROJECT.CatchTheBall.src.main;

import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {

    private GamePanel gamePanel;

    public GameFrame() {
        setTitle("Catch the Ball - Farm Edition");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setUndecorated(false);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(GamePanel.W, GamePanel.H));
        add(gamePanel);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try { gamePanel.getPlayerData().save(); } catch (Exception ignored) {}
                gamePanel.stopGameLoop();
                dispose();
                System.exit(0);
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        gamePanel.requestFocusInWindow();
        gamePanel.startGameLoop();
    }
}