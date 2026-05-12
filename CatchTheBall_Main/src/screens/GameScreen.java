package OOP_PROJECT.CatchTheBall.src.screens;

import OOP_PROJECT.CatchTheBall.src.core.Screen;
import OOP_PROJECT.CatchTheBall.src.entities.Ball;
import OOP_PROJECT.CatchTheBall.src.entities.Basket;
import OOP_PROJECT.CatchTheBall.src.entities.Character;
import OOP_PROJECT.CatchTheBall.src.entities.PowerUp;
import OOP_PROJECT.CatchTheBall.src.entities.Particle;
import OOP_PROJECT.CatchTheBall.src.enums.*;
import OOP_PROJECT.CatchTheBall.src.managers.*;
import OOP_PROJECT.CatchTheBall.src.models.Achievement;
import OOP_PROJECT.CatchTheBall.src.models.FarmProgression;
import OOP_PROJECT.CatchTheBall.src.models.GameState;
import OOP_PROJECT.CatchTheBall.src.renderers.BackgroundRenderer;
import OOP_PROJECT.CatchTheBall.src.renderers.SidebarRenderer;
import OOP_PROJECT.CatchTheBall.src.utils.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class GameScreen extends Screen {
    private GameState       state;
    private Basket          basket;
    private Character       character;
    private List<Ball>      balls;
    private List<PowerUp>   powerUps;
    private ParticleManager particles;
    private BallSpawner     ballSpawner;
    private PowerUpManager  powerUpManager;
    private FarmProgression farm;

    private int   screenShakeTimer;
    private float screenShakeX, screenShakeY;
    private int   screenFlashTimer;
    private Color screenFlashColor = new Color(255,215,0,120);

    private String toastText;
    private int    toastTimer;
    private String comboFlashText;
    private int    comboFlashTimer;
    private Color  comboFlashColor = new Color(255,220,50);

    private String achievementToast;
    private int    achievementToastTimer;

    private boolean tutorialActive = false;
    private int     tutorialStep   = 0;

    private String     playerName = "Farmer";
    private Difficulty difficulty = Difficulty.NORMAL;
    private int        nextBonusWaveLevel = 5;

    public GameScreen(GamePanel panel) { super(panel); }

    @Override
    public void onEnter() {
        super.onEnter();
        startNewGame();
        if (!panel.getPlayerData().getProfile().isTutorialShown()) {
            tutorialActive = true; tutorialStep = 0;
            panel.getPlayerData().getProfile().setTutorialShown(true);
            panel.getPlayerData().save();
        }
        panel.getMusicManager().setThemeForLevel(1);
    }

    private void startNewGame() {
        state          = new GameState(difficulty);
        balls          = new ArrayList<>();
        powerUps       = new ArrayList<>();
        particles      = new ParticleManager();
        ballSpawner    = new BallSpawner(GamePanel.ARENA_W);
        powerUpManager = new PowerUpManager(GamePanel.ARENA_W);
        farm           = new FarmProgression(panel.getPlayerData().getFarmStage());
        basket    = new Basket(GamePanel.ARENA_W/2f-40, GamePanel.H-110,
                panel.getPlayerData().getEquippedBasket());
        character = new Character(GamePanel.ARENA_W/2f-25, GamePanel.H-170,
                panel.getPlayerData().getEquippedSkin(), playerName);
        screenShakeTimer=0; toastTimer=0; comboFlashTimer=0;
        screenFlashTimer=0; achievementToastTimer=0; nextBonusWaveLevel=5;
    }

    @Override
    public void update() {
        tickCount++;
        if (tutorialActive) return;
        updateTimers();
        updateGameLogic();
        updateEntities();
        checkLevelUp();
        pollAchievementToast();
    }

    private void updateTimers() {
        if (tickCount%60==0 && state.getTimeLeft()>0) {
            state.decrementTime();
            if (state.getTimeLeft()==0) { endGame(); return; }
            if (state.getTimeLeft()==10) panel.getSoundManager().playLowTime();
        }
        if (screenShakeTimer>0) {
            screenShakeTimer--;
            screenShakeX=(float)((Math.random()-0.5)*10*(screenShakeTimer/20f));
            screenShakeY=(float)((Math.random()-0.5)*10*(screenShakeTimer/20f));
        } else { screenShakeX=0; screenShakeY=0; }
        if (toastTimer>0) toastTimer--;
        if (comboFlashTimer>0) comboFlashTimer--;
        if (screenFlashTimer>0) screenFlashTimer--;
        if (achievementToastTimer>0) achievementToastTimer--;
    }

    private void updateGameLogic() {
        ballSpawner.update(balls, state.getLevel(), difficulty);
        powerUpManager.update(powerUps, state);
        if (state.isMagnetActive()) applyMagnet();
        basket.setWide(state.isWideBasketActive());
        character.setX(basket.getX()+basket.getWidth()/2f-character.getWidth()/2f);
        character.setY(basket.getY()-character.getHeight()-2);
    }

    private void applyMagnet() {
        float cx=basket.getX()+basket.getWidth()/2f;
        for (Ball b : balls) {
            if (!b.isActive()||b.getType().isBad()) continue;
            float bx=b.getX()+b.getWidth()/2f, dist=Math.abs(cx-bx);
            if (dist < 120) b.setX(b.getX() + (cx - bx) * 0.04f);
        }
    }

    private void updateEntities() {
        basket.update(); character.update();
        Iterator<Ball> bi=balls.iterator();
        while (bi.hasNext()) {
            Ball b=bi.next(); b.update();
            if (b.isCaughtAnimDone()) { bi.remove(); continue; }
            if (b.getY()>GamePanel.H) {
                if (!b.getType().isBad()) { state.resetCombo(); particles.spawnBurst((int)(b.getX()+18),GamePanel.H-10,new Color(200,100,100),4); }
                bi.remove();
            } else if (!b.isCaughtAnimDone()&&b.intersects(basket)) {
                handleCatch(b); b.triggerCatch();
            }
        }
        Iterator<PowerUp> pi=powerUps.iterator();
        while (pi.hasNext()) {
            PowerUp p=pi.next(); p.update();
            if (p.getY()>GamePanel.H) pi.remove();
            else if (p.intersects(basket)) {
                powerUpManager.activate(p.getType(),state,panel.getSoundManager());
                showToast(getPowerUpToast(p.getType()));
                particles.spawnShockwave((int)(p.getX()+20),(int)p.getY(),p.getType().getColor());
                pi.remove();
            }
        }
        particles.update();
    }

    private void handleCatch(Ball b) {
        BallType type=b.getType(); boolean bad=type.isBad(); int pts=type.getPoints();

        if (type==BallType.FROZEN) {
            powerUpManager.applyFrozen(state,balls);
            panel.getSoundManager().playCatch(true);
            particles.spawnBurst((int)b.getX(),(int)b.getY(),new Color(150,220,255),12);
            showToast("\u2744 Frozen! Balls slowed!"); basket.triggerCatch(); character.triggerCatch();
            state.incrementBallsCaught(); panel.getAchievementManager().tryUnlock("frozen_catch"); return;
        }
        if (type==BallType.RAINBOW) {
            state.addScore(500); state.addCoins(50); panel.getPlayerData().addCoins(50);
            particles.spawnFirework((int)(b.getX()+b.getWidth()/2),(int)b.getY(),40);
            showToast("\uD83C\uDF08 RAINBOW! +500!"); basket.triggerCatch(); character.triggerCatch();
            state.incrementBallsCaught(); panel.getAchievementManager().tryUnlock("rainbow_catch"); return;
        }
        if (type==BallType.MYSTERY) { handleMystery(b); return; }

        if (bad) {
            if (state.isShieldActive()) {
                state.setShieldActive(false); showToast("\uD83D\uDEE1\uFE0F Shield blocked it!");
                particles.spawnShockwave((int)b.getX(),(int)b.getY(),new Color(80,180,255));
                basket.triggerCatch(); character.triggerCatch();
            } else {
                state.addScore(Math.max(-state.getScore(),(int)(pts*difficulty.getPenaltyMultiplier()/-20f)));
                screenShakeTimer=8; basket.triggerShake(); character.triggerShake(); state.resetCombo();
                panel.getSoundManager().playBadCatch();
                screenFlashColor=new Color(255,0,0,80); screenFlashTimer=20;
                particles.spawnFloatingText((int)b.getX(),(int)b.getY(),""+pts,ColorPalette.TEXT_BAD_CATCH);
            }
        } else {
            state.incrementBallsCaught(); state.incrementCombo();
            int c=state.getCombo(); float mult=1f;
            if      (c>=12) mult=5f;
            else if (c>=8)  mult=4f;
            else if (c>=5)  mult=3f;
            else if (c>=3)  mult=2f;
            if (mult>state.getComboMultiplier()) {
                state.setComboMultiplier(mult);
                comboFlashColor = c>=12?new Color(200,0,255):c>=8?new Color(255,50,50):c>=5?new Color(255,150,0):new Color(255,230,50);
                String flashMsg = c>=12?"\uD83D\uDC4E LEGENDARY x5!":c>=8?"\uD83D\uDD25 ON FIRE x4!":c>=5?"\u2B50 HOT STREAK x3!":"COMBO x2!";
                showComboFlash(flashMsg, comboFlashColor); panel.getSoundManager().playCombo();
                if (c>=12) screenShakeTimer=4;
            }
            particles.spawnComboParticles((int)(b.getX()+b.getWidth()/2),(int)b.getY(),c);
            float finalMult=mult*(state.isDoublePointsActive()?2f:1f);
            int earned=(int)(pts*finalMult);
            if (type==BallType.GIANT) earned*=3;
            state.addScore(earned);
            int coins=Math.max(1,earned/5);
            state.addCoins(coins); panel.getPlayerData().addCoins(coins);
            basket.triggerCatch(); character.triggerCatch(); panel.getSoundManager().playCatch(true);
            String prefix=finalMult>1f?"x"+(int)finalMult+" ":"";
            particles.spawnFloatingText((int)b.getX(),(int)b.getY(),prefix+"+"+earned,ColorPalette.TEXT_GOOD_CATCH);
            panel.getAchievementManager().updateProgress("first_catch",1);
            panel.getAchievementManager().updateProgress("on_fire",c);
            panel.getAchievementManager().updateProgress("hot_streak",c);
            panel.getAchievementManager().updateProgress("untouchable",c);
            panel.getAchievementManager().updateProgress("century",state.getBallsCaughtThisGame());
        }
    }

    private void handleMystery(Ball b) {
        Random rand=new Random(); int roll=rand.nextInt(4);
        basket.triggerCatch(); character.triggerCatch(); state.incrementBallsCaught();
        switch(roll) {
            case 0:
                int reward=200+rand.nextInt(300); state.addScore(reward);
                particles.spawnFirework((int)b.getX(),(int)b.getY(),30);
                showToast("\uD83C\uDF89 Mystery! +"+reward+" pts!"); break;
            case 1:
                state.addScore(-50); screenShakeTimer=6;
                showToast("\uD83D\uDCA5 Mystery Bomb! -50!");
                screenFlashColor=new Color(255,0,0,80); screenFlashTimer=20; break;
            case 2:
                PowerUpType[] pus=PowerUpType.values();
                PowerUpType pu=pus[rand.nextInt(pus.length)];
                powerUpManager.activate(pu,state,panel.getSoundManager());
                showToast("\u2728 Mystery: "+pu.getLabel()+"!"); break;
            case 3:
                state.addScore(500);
                particles.spawnLevelUpBurst(GamePanel.ARENA_W/2,GamePanel.H/2);
                showToast("\uD83D\uDCB0 Mystery: Bonus +500!"); break;
        }
        particles.spawnBurst((int)b.getX(),(int)b.getY(),new Color(160,80,255),15);
    }

    private void checkLevelUp() {
        if (state.getScore()>=state.getLevelTarget()) {
            state.levelUp(); panel.getSoundManager().playLevelUp();
            showToast("\uD83C\uDF89 Level Up! Level "+state.getLevel());
            particles.spawnLevelUpBurst(GamePanel.ARENA_W/2,GamePanel.H/2);
            screenFlashColor=new Color(255,215,0,120); screenFlashTimer=30;
            character.triggerCelebrate();
            panel.getMusicManager().setThemeForLevel(state.getLevel());
            panel.getAchievementManager().updateProgress("legend",state.getLevel());
            if (state.getLevel()==nextBonusWaveLevel) {
    if (difficulty == Difficulty.EASY) {
        // no bonus wave on easy
    } else if (difficulty == Difficulty.NORMAL) {
        ballSpawner.spawnWave(balls,state.getLevel(),difficulty,2);
        showToast("\uD83C\uDF4E BONUS WAVE!");
    } else {
        ballSpawner.spawnWave(balls,state.getLevel(),difficulty,4);
        showToast("\uD83C\uDF4E BONUS WAVE!");
    }
    nextBonusWaveLevel+=5;
}
        }
    }

    private void pollAchievementToast() {
        if (achievementToastTimer<=0) {
            Achievement a=panel.getAchievementManager().pollToast();
            if (a!=null) { achievementToast=a.getEmoji()+" "+a.getName()+" Unlocked!"; achievementToastTimer=180; }
        }
    }

    private void endGame() {
        panel.getPlayerData().getProfile().updateBestScore(state.getScore());
        panel.getPlayerData().getProfile().updateBestCombo(state.getHighestCombo());
        panel.getPlayerData().getProfile().incrementGamesPlayed();
        panel.getPlayerData().getProfile().addBallsCaught(state.getBallsCaughtThisGame());
        panel.getAchievementManager().updateProgress("veteran",
                panel.getPlayerData().getProfile().getTotalGamesPlayed());
        panel.getAchievementManager().updateProgress("rich_farmer",
                panel.getPlayerData().getTotalCoins());
        panel.getAchievementManager().updateProgress("millionaire",
                panel.getPlayerData().getTotalCoins());
        panel.getPlayerData().setAchievementData(panel.getAchievementManager().saveToString());
        panel.getPlayerData().save();
        character.triggerSad();
        panel.getSoundManager().playGameOver();
        panel.getScreenManager().switchTo(GameScreenType.GAME_OVER);
    }

    @Override
    public void draw(Graphics2D g) {
        if (tutorialActive) { drawGameBehind(g); drawTutorial(g); return; }
        drawGameBehind(g);
        SidebarRenderer.draw(g, state, farm, panel.getPlayerData(),
                panel.getSoundManager(), tickCount);
        if (toastTimer>0) drawToast(g);
        if (achievementToastTimer>0) drawAchievementToast(g);
    }

    private void drawGameBehind(Graphics2D g) {
        g.translate((int)screenShakeX,(int)screenShakeY);
        BackgroundRenderer.drawSky(g,state.getLevel(),tickCount,GamePanel.ARENA_W,GamePanel.H);
        if (screenFlashTimer>0) {
            float alpha=(float)screenFlashTimer/30f;
            g.setColor(new Color(screenFlashColor.getRed(),screenFlashColor.getGreen(),
                    screenFlashColor.getBlue(),(int)(screenFlashColor.getAlpha()*alpha)));
            g.fillRect(0,0,GamePanel.ARENA_W,GamePanel.H);
        }
        for (Ball b    : balls)    b.draw(g);
        for (PowerUp p : powerUps) p.draw(g);
        particles.draw(g);
        drawCharacterArms(g);
        basket.draw(g);
        character.draw(g);
        drawPowerUpAuras(g);
        drawComboFlash(g);
        g.translate(-(int)screenShakeX,-(int)screenShakeY);
    }

    private void drawCharacterArms(Graphics2D g) {
        float bCX  = basket.getX()+basket.getWidth()/2f;
        float bTop = basket.getY();
        float cCX  = character.getX()+character.getWidth()/2f;
        float cBot = character.getY()+character.getHeight();
        float sway = character.getLeanAngle()*0.5f;
        g.setStroke(new BasicStroke(6f,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        g.setColor(new Color(180,130,80));
        g.drawLine((int)(cCX-10+sway),(int)cBot,(int)(bCX-basket.getWidth()/3f),(int)bTop);
        g.drawLine((int)(cCX+10+sway),(int)cBot,(int)(bCX+basket.getWidth()/3f),(int)bTop);
        g.setStroke(new BasicStroke(1f));
    }

    private void drawPowerUpAuras(Graphics2D g) {
        if (state.isShieldActive()) {
            float pulse=MathUtils.pulse(tickCount,0.15f);
            g.setColor(new Color(80,180,255,(int)(pulse*80))); g.setStroke(new BasicStroke(3f));
            g.drawOval((int)(basket.getX()-6),(int)(basket.getY()-6),basket.getWidth()+12,basket.getHeight()+12);
            g.setStroke(new BasicStroke(1f));
        }
        if (state.isMagnetActive()) {
            float pulse=MathUtils.pulse(tickCount,0.2f);
            g.setColor(new Color(255,80,200,(int)(pulse*50)));
            g.fillOval((int)(basket.getX()-90),(int)(basket.getY()-90),basket.getWidth()+180,basket.getHeight()+180);
        }
    }

    private void drawComboFlash(Graphics2D g) {
        if (comboFlashTimer<=0||comboFlashText==null) return;
        float alpha=Math.min(1f,comboFlashTimer/30f);
        float scale=1f+(1f-comboFlashTimer/90f)*0.5f;
        Font f=FontManager.getBold((int)(28*scale));
        g.setFont(f); FontMetrics fm=g.getFontMetrics();
        int tw=fm.stringWidth(comboFlashText), tx=(GamePanel.ARENA_W-tw)/2, ty=GamePanel.H/2-30;
        g.setColor(new Color(0,0,0,(int)(alpha*160))); g.drawString(comboFlashText,tx+2,ty+2);
        Color fc=comboFlashColor!=null?comboFlashColor:new Color(255,220,50);
        g.setColor(new Color(fc.getRed(),fc.getGreen(),fc.getBlue(),(int)(alpha*240)));
        g.drawString(comboFlashText,tx,ty);
    }

    private void drawToast(Graphics2D g) {
        float alpha=Math.min(1f,toastTimer/30f);
        int tw=370,th=42,tx=(GamePanel.W-tw)/2,ty=20;
        RenderUtils.drawGradientPanel(g,tx,ty,tw,th,
                new Color(24,72,16,(int)(alpha*230)),new Color(14,50,8,(int)(alpha*230)),
                new Color(110,210,75,(int)(alpha*255)),1.5f,12);
        g.setFont(FontManager.getEmoji(13));
        g.setColor(new Color(255,255,255,(int)(alpha*255)));
        FontMetrics fm=g.getFontMetrics();
        g.drawString(toastText, tx+(tw-fm.stringWidth(toastText))/2, ty+27);
    }

    private void drawAchievementToast(Graphics2D g) {
        float alpha=Math.min(1f,achievementToastTimer/30f);
        int tw=340,th=38,tx=GamePanel.ARENA_W-tw-10,ty=10;
        RenderUtils.drawGradientPanel(g,tx,ty,tw,th,
                new Color(60,120,30,(int)(alpha*230)),new Color(40,85,18,(int)(alpha*230)),
                new Color(150,240,80,(int)(alpha*255)),1.5f,10);
        g.setFont(FontManager.getBodyBold(12));
        g.setColor(new Color(255,255,255,(int)(alpha*255)));
        FontMetrics fm=g.getFontMetrics();
        g.drawString(achievementToast,tx+(tw-fm.stringWidth(achievementToast))/2,ty+24);
    }

    private void drawTutorial(Graphics2D g) {
        g.setColor(new Color(0,0,0,180)); g.fillRect(0,0,GamePanel.W,GamePanel.H);
        int pw=520,ph=300,px=(GamePanel.W-pw)/2,py=(GamePanel.H-ph)/2;
        RenderUtils.drawGradientPanel(g,px,py,pw,ph,
                new Color(24,60,16,245),new Color(12,40,8,245),new Color(100,200,70),2f,20);
        String[] titles={"Move the Basket","Good vs Bad Fruits","Power-Ups","Combos & Multipliers","Coins & Shop"};
        String[] bodies={
            "Move your mouse or A/D keys to move the basket and catch falling fruits!",
            "Green fruits (Apple, Orange) = GOOD! Red fruits (Mushroom, Bomb) = BAD! Avoid bad ones!",
            "Glowing power-ups grant shields, magnets, time bonuses, double points and more!",
            "Catch in a row for combo multipliers: x2, x3, x4, x5 - go for LEGENDARY x5!",
            "Earn coins every catch. Spend them in the Wardrobe for skins and baskets!"
        };
        RenderUtils.drawCenteredText(g,"Tutorial ("+(tutorialStep+1)+"/5)",
                px+pw/2,py+35,FontManager.getBold(16),ColorPalette.TEXT_GOLD);
        RenderUtils.drawCenteredText(g,titles[tutorialStep],
                px+pw/2,py+80,FontManager.getBold(20),Color.WHITE);
        RenderUtils.drawCenteredText(g,bodies[tutorialStep],
                px+pw/2,py+130,FontManager.getBody(14),new Color(200,240,170));
        RenderUtils.drawButton(g,new Rectangle(px+pw-140,py+ph-55,120,38),
                tutorialStep<4?"Next \u2192":"Start! \uD83C\uDF3E",true,FontManager.getBold(13));
        RenderUtils.drawButton(g,new Rectangle(px+20,py+ph-55,80,38),"Skip",false,FontManager.getBold(13));
    }

    private String getPowerUpToast(PowerUpType t) {
        switch(t) {
            case MAGNET:        return "\uD83E\uDDF2 Magnet!";
            case TIME_PLUS:     return "\u23F0 +15 seconds!";
            case SHIELD:        return "\uD83D\uDEE1\uFE0F Shield!";
            case DOUBLE_POINTS: return "2\uFE0F\u20E3 Double Points!";
            case SLOW_TIME:     return "\uD83D\uDD5B Slow Time!";
            case WIDE_BASKET:   return "\uD83E\uDDF3 Wide Basket!";
            default:            return "Power-up!";
        }
    }

    public void showToast(String text)           { toastText=text; toastTimer=180; }
    public void showComboFlash(String t, Color c){ comboFlashText=t; comboFlashColor=c; comboFlashTimer=90; }

    @Override
    public void onKeyPressed(KeyEvent e) {
        if (tutorialActive) { if(e.getKeyCode()==KeyEvent.VK_ESCAPE) tutorialActive=false; return; }
        int code=e.getKeyCode();
        float cx=basket.getX()+basket.getWidth()/2f;
        if (code==KeyEvent.VK_LEFT||code==KeyEvent.VK_A)
            basket.setTargetX(Math.max(40,cx-30));
        else if (code==KeyEvent.VK_RIGHT||code==KeyEvent.VK_D)
            basket.setTargetX(Math.min(GamePanel.ARENA_W-40,cx+30));
        else if (code==KeyEvent.VK_P)      panel.getScreenManager().switchTo(GameScreenType.PAUSED);
        else if (code==KeyEvent.VK_ESCAPE) panel.switchToWithFade(GameScreenType.MAIN_MENU);
        else if (code==KeyEvent.VK_M)      panel.getSoundManager().toggleMute();
    }

    @Override
    public void onMouseMoved(MouseEvent e) {
        if (tutorialActive) return;
        if (e.getX()<GamePanel.ARENA_W) basket.setTargetX(e.getX());
        float dir=(e.getX()-(basket.getX()+basket.getWidth()/2f))/GamePanel.ARENA_W;
        character.setLean(dir*0.3f);
    }

    @Override
    public void onMouseClicked(MouseEvent e) {
        if (tutorialActive) {
            int mx=e.getX(),my=e.getY();
            int pw=520,ph=300,px=(GamePanel.W-pw)/2,py=(GamePanel.H-ph)/2;
            if (new Rectangle(px+pw-140,py+ph-55,120,38).contains(mx,my)) {
                tutorialStep++; if(tutorialStep>=5) tutorialActive=false;
            } else if (new Rectangle(px+20,py+ph-55,80,38).contains(mx,my)) tutorialActive=false;
            return;
        }
        int mx=e.getX(),my=e.getY();
        if (mx>GamePanel.ARENA_W) {
            if (new Rectangle(GamePanel.ARENA_W+15,GamePanel.H-65,60,32).contains(mx,my)) panel.getSoundManager().toggleMute();
            if (new Rectangle(GamePanel.ARENA_W+85,GamePanel.H-65,60,32).contains(mx,my)) panel.getScreenManager().switchTo(GameScreenType.PAUSED);
        }
    }

    public void setPlayerName(String n)    { playerName=n; }
    public void setDifficulty(Difficulty d){ difficulty=d; }
    public GameState getState()            { return state; }
    public String    getPlayerName()       { return playerName; }
}