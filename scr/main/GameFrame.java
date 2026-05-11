package main;

import managers.GamePanel;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GameFrame extends JFrame {

    private GamePanel gamePanel;

    public GameFrame() {
        setTitle("\uD83C\uDF3E Catch the Ball - Farm Edition");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setResizable(false);

        gamePanel = new GamePanel();
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                gamePanel.getPlayerData().save();
                gamePanel.stopGameLoop();
                dispose();
                System.exit(0);
            }
        });

        setVisible(true);
        gamePanel.requestFocusInWindow();
        gamePanel.startGameLoop();
    }
}