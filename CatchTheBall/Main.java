import javax.swing.SwingUtilities;

/**
 * Main entry point for Catch the Ball game.
 * Launches the game window on the Event Dispatch Thread.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
        });
    }
}
