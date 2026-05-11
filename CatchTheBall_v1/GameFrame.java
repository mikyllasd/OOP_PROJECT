import javax.swing.*;

/**
 * The main application window.
 * Hosts the GamePanel and starts the game loop thread.
 *
 * Demonstrates: Composition (has-a GamePanel), separation of concerns.
 */
public class GameFrame extends JFrame {

    public GameFrame() {
        setTitle("Catch the Ball");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        GamePanel panel = new GamePanel();
        add(panel);
        pack();                          // Size frame to panel's preferred size
        setSize(1000, 720);
        setLocationRelativeTo(null);     // Centre on screen

        // Start game loop on a daemon thread
        Thread gameThread = new Thread(panel);
        gameThread.setDaemon(true);
        gameThread.start();
    }
}
