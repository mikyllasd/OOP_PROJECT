package OOP_PROJECT.CatchTheBall.src.managers;

import OOP_PROJECT.CatchTheBall.src.accounts.AccountManager;
import OOP_PROJECT.CatchTheBall.src.audio.MusicManager;
import OOP_PROJECT.CatchTheBall.src.audio.SoundManager;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.screens.ScreenManager;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {

    public static final int W         = 900;
    public static final int H         = 650;
    public static final int ARENA_W   = 650;
    public static final int SIDEBAR_W = 250;
    public static final int TARGET_FPS = 60;

    private Thread           gameThread;
    private volatile boolean running;

    private ScreenManager      screenManager;
    private InputManager       inputManager;
    private SoundManager       soundManager;
    private MusicManager       musicManager;
    private PlayerData         playerData;
    private ScoreManager       scoreManager;
    private AchievementManager achievementManager;
    private AccountManager     accountManager;

    private float          fadeAlpha    = 1f;
    private boolean        fadingIn     = true;
    private GameScreenType pendingSwitch = null;

    // Scale fields
    private float scaleX  = 1f;
    private float scaleY  = 1f;

    public GamePanel() {
        setBackground(Color.BLACK);
        setFocusable(true);

        accountManager     = new AccountManager();
        soundManager       = new SoundManager();
        musicManager       = new MusicManager();
        playerData         = new PlayerData();
        scoreManager       = new ScoreManager();
        achievementManager = new AchievementManager();
        screenManager      = new ScreenManager(this);
        inputManager       = new InputManager(screenManager, this);

        addKeyListener(inputManager);
        addMouseMotionListener(inputManager);
        addMouseListener(inputManager);

        screenManager.switchTo(GameScreenType.ACCOUNT_SELECT);
    }

    public void startGameLoop() {
        running    = true;
        gameThread = new Thread(this);
        gameThread.start();
        musicManager.start();
    }

    public void stopGameLoop() {
        running = false;
        musicManager.stop();
    }

    @Override
    public void run() {
        long lastTime    = System.nanoTime();
        double nsPerTick = 1_000_000_000.0 / TARGET_FPS;
        double delta     = 0;
        while (running) {
            long now = System.nanoTime();
            delta   += (now - lastTime) / nsPerTick;
            lastTime = now;
            while (delta >= 1) { tick(); delta--; }
            repaint();
            try { Thread.sleep(1); } catch (InterruptedException ignored) {}
        }
    }

    private void tick() {
        screenManager.update();
        if (fadingIn) {
            fadeAlpha -= 0.05f;
            if (fadeAlpha <= 0f) { fadeAlpha = 0f; fadingIn = false; }
        }
        if (!fadingIn && pendingSwitch != null) {
            fadeAlpha += 0.05f;
            if (fadeAlpha >= 1f) {
                fadeAlpha = 1f;
                screenManager.switchTo(pendingSwitch);
                pendingSwitch = null;
                fadingIn = true;
            }
        }
    }

    public void switchToWithFade(GameScreenType type) {
        if (pendingSwitch != null) return;
        pendingSwitch = type;
        fadingIn      = false;
    }

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0);
        Graphics2D g = (Graphics2D) g0;

        // Stretch to fill entire screen
        scaleX = (float) getWidth()  / W;
        scaleY = (float) getHeight() / H;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_SPEED);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,     RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        g.scale(scaleX, scaleY);

        screenManager.draw(g);

        if (fadeAlpha > 0f) {
            g.setColor(new Color(0f, 0f, 0f, Math.min(1f, fadeAlpha)));
            g.fillRect(0, 0, W, H);
        }
    }

    // Translate real screen mouse coords back to game coords
    public int translateX(int screenX) { return (int)(screenX / scaleX); }
    public int translateY(int screenY) { return (int)(screenY / scaleY); }

    public void loadAccountData(String accountName) {
        String filePath  = accountManager.getAccountFilePath(accountName);
        String scoreFile = "accounts/" + accountName + "_scores.txt";
        playerData.setSaveFile(filePath);
        scoreManager.setScoreFile(scoreFile);
        achievementManager.loadFromString(playerData.getAchievementData());
    }

    public SoundManager       getSoundManager()       { return soundManager; }
    public MusicManager       getMusicManager()        { return musicManager; }
    public PlayerData         getPlayerData()          { return playerData; }
    public ScoreManager       getScoreManager()        { return scoreManager; }
    public ScreenManager      getScreenManager()       { return screenManager; }
    public InputManager       getInputManager()        { return inputManager; }
    public AchievementManager getAchievementManager()  { return achievementManager; }
    public AccountManager     getAccountManager()      { return accountManager; }
}