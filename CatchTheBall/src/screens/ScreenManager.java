package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.enums.GameScreenType;
import OOP_PROJECT.CatchTheBall.src.managers.GamePanel;

import java.awt.Graphics2D;
import java.util.*;

public class ScreenManager {
    private Map<GameScreenType, Screen> screens;
    private Screen         currentScreen;
    private GameScreenType currentType;
    private GamePanel      panel;

    public ScreenManager(GamePanel panel) {
        this.panel   = panel;
        this.screens = new HashMap<>();
        registerScreens();
    }

    private void registerScreens() {
        screens.put(GameScreenType.ACCOUNT_SELECT,     new AccountSelectScreen(panel));
        screens.put(GameScreenType.MAIN_MENU,          new MainMenuScreen(panel));
        screens.put(GameScreenType.CHARACTER_CREATION, new CharacterCreationScreen(panel));
        screens.put(GameScreenType.GAME,               new GameScreen(panel));
        screens.put(GameScreenType.PAUSED,             new PauseScreen(panel));
        screens.put(GameScreenType.GAME_OVER,          new GameOverScreen(panel));
        screens.put(GameScreenType.WARDROBE,           new WardrobeScreen(panel));
        screens.put(GameScreenType.FARM_UPGRADE,       new FarmUpgradeScreen(panel));
        screens.put(GameScreenType.ACHIEVEMENTS,       new AchievementsScreen(panel));
        screens.put(GameScreenType.LEADERBOARD,        new LeaderboardScreen(panel));
        screens.put(GameScreenType.SETTINGS,           new SettingsScreen(panel));
        screens.put(GameScreenType.STATS,              new StatsScreen(panel));
    }

    public void switchTo(GameScreenType type) {
        if (currentScreen != null) currentScreen.onExit();
        currentType   = type;
        currentScreen = screens.get(type);
        if (currentScreen != null) currentScreen.onEnter();
    }

    public void update()           { if (currentScreen != null) currentScreen.update(); }
    public void draw(Graphics2D g) { if (currentScreen != null) currentScreen.draw(g); }

    public Screen         getCurrentScreen() { return currentScreen; }
    public GameScreenType getCurrentType()   { return currentType; }

    public GameScreen getGameScreen() {
        return (GameScreen) screens.get(GameScreenType.GAME);
    }
}