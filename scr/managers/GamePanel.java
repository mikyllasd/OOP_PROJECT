package managers;

import screens.ScreenManager;
import enums.GameScreenType;
import javax.swing.JPanel;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    public static final int W        = 900;
    public static final int H        = 650;
    public static final int ARENA_W  = 650;
    public static final int SIDEBAR_W= 250;
    public static final int TARGET_FPS = 60;

    private Thread gameThread;
    private volatile boolean running;

    private ScreenManager screenManager;
    private InputManager  inputManager;
    private SoundManager  soundManager;
    private PlayerData    playerData;
    private ScoreManager  scoreManager;

    public GamePanel() {
        setPreferredSize(new Dimension(W, H));
        setBackground(Color.BLACK);
        setFocusable(true);

        soundManager  = new SoundManager();
        playerData    = new PlayerData();
        scoreManager  = new ScoreManager();
        screenManager = new ScreenManager(this);
        inputManager  = new InputManager(screenManager);

        addKeyListener(inputManager);
        addMouseMotionListener(inputManager);
        addMouseListener(inputManager);

        screenManager.switchTo(GameScreenType.MAIN_MENU);
    }

    public void startGameLoop() {
        running    = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stopGameLoop() {
        running = false;
    }

    @Override
    public void run() {
        long lastTime  = System.nanoTime();
        double nsPerTick = 1_000_000_000.0 / TARGET_FPS;
        double delta   = 0;

        while (running) {
            long now = System.nanoTime();
            delta   += (now - lastTime) / nsPerTick;
            lastTime = now;

            while (delta >= 1) {
                tick();
                delta--;
            }

            repaint();

            try { Thread.sleep(1); }
            catch (InterruptedException ignored) {}
        }
    }

    private void tick() {
        screenManager.update();
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        screenManager.draw(g);
    }

    public SoundManager getSoundManager()   { return soundManager; }
    public PlayerData getPlayerData()       { return playerData; }
    public ScoreManager getScoreManager()   { return scoreManager; }
    public ScreenManager getScreenManager() { return screenManager; }
    public InputManager getInputManager()   { return inputManager; }
}