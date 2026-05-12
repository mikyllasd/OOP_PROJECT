package OOP_PROJECT.CatchTheBall.src.models;

import OOP_PROJECT.CatchTheBall.src.enums.BasketSkin;
import OOP_PROJECT.CatchTheBall.src.enums.SkinType;

public class PlayerProfile {
    private String     farmerName;
    private SkinType   equippedSkin;
    private BasketSkin equippedBasket;
    private int        totalCoins;
    private int        totalGamesPlayed;
    private int        totalBallsCaught;
    private int        allTimeBestScore;
    private int        allTimeBestCombo;
    private int        totalCoinsEarned;
    private int        totalCoinsSpent;
    private int        consecutiveLoginDays;
    private String     lastLoginDate;
    private int        farmStage;
    private boolean    tutorialShown;
    private int[]      lastTenScores;
    private int        scoreHistoryCount;

    public PlayerProfile() {
        farmerName           = "Farmer";
        equippedSkin         = SkinType.FARMER_MALE;
        equippedBasket       = BasketSkin.WOVEN;
        totalCoins           = 0;
        totalGamesPlayed     = 0;
        totalBallsCaught     = 0;
        allTimeBestScore     = 0;
        allTimeBestCombo     = 0;
        totalCoinsEarned     = 0;
        totalCoinsSpent      = 0;
        consecutiveLoginDays = 0;
        lastLoginDate        = "";
        farmStage            = 1;
        tutorialShown        = false;
        lastTenScores        = new int[10];
        scoreHistoryCount    = 0;
    }

    public String     getFarmerName()                { return farmerName; }
    public void       setFarmerName(String n)        { farmerName = n; }
    public SkinType   getEquippedSkin()              { return equippedSkin; }
    public void       setEquippedSkin(SkinType s)    { equippedSkin = s; }
    public BasketSkin getEquippedBasket()            { return equippedBasket; }
    public void       setEquippedBasket(BasketSkin b){ equippedBasket = b; }
    public int        getTotalCoins()                { return totalCoins; }
    public void       addCoins(int amount)           { totalCoins += amount; totalCoinsEarned += amount; }

    public boolean spendCoins(int amount) {
        if (totalCoins >= amount) {
            totalCoins -= amount;
            totalCoinsSpent += amount;
            return true;
        }
        return false;
    }

    public int  getTotalGamesPlayed()      { return totalGamesPlayed; }
    public void incrementGamesPlayed()     { totalGamesPlayed++; }
    public int  getTotalBallsCaught()      { return totalBallsCaught; }
    public void addBallsCaught(int c)      { totalBallsCaught += c; }
    public int  getAllTimeBestScore()       { return allTimeBestScore; }

    public void updateBestScore(int score) {
        addScoreHistory(score);
        if (score > allTimeBestScore) allTimeBestScore = score;
    }

    public int  getAllTimeBestCombo()           { return allTimeBestCombo; }
    public void updateBestCombo(int combo)     { if (combo > allTimeBestCombo) allTimeBestCombo = combo; }
    public int  getTotalCoinsEarned()          { return totalCoinsEarned; }
    public int  getTotalCoinsSpent()           { return totalCoinsSpent; }
    public void setTotalCoinsEarned(int v)     { totalCoinsEarned = v; }
    public void setTotalCoinsSpent(int v)      { totalCoinsSpent = v; }
    public int  getConsecutiveLoginDays()      { return consecutiveLoginDays; }
    public void setConsecutiveLoginDays(int d) { consecutiveLoginDays = d; }
    public String getLastLoginDate()           { return lastLoginDate; }
    public void setLastLoginDate(String date)  { lastLoginDate = date; }
    public int  getFarmStage()                 { return farmStage; }
    public void setFarmStage(int s)            { farmStage = Math.max(1, Math.min(20, s)); }
    public boolean isTutorialShown()           { return tutorialShown; }
    public void setTutorialShown(boolean b)    { tutorialShown = b; }
    public int[] getLastTenScores()            { return lastTenScores; }
    public int   getScoreHistoryCount()        { return scoreHistoryCount; }

    private void addScoreHistory(int score) {
        if (scoreHistoryCount < 10) {
            lastTenScores[scoreHistoryCount++] = score;
        } else {
            System.arraycopy(lastTenScores, 1, lastTenScores, 0, 9);
            lastTenScores[9] = score;
        }
    }

    public void setScoreHistory(int[] arr, int count) {
        int len = Math.min(count, 10);
        for (int i = 0; i < len; i++) lastTenScores[i] = arr[i];
        scoreHistoryCount = len;
    }
}