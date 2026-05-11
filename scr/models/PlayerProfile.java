package models;
import enums.SkinType;
import enums.BasketSkin;
public class PlayerProfile {
    private String farmerName;
    private SkinType equippedSkin;
    private BasketSkin equippedBasket;
    private int totalCoins;
    private int totalGamesPlayed;
    private int totalBallsCaught;
    private int allTimeBestScore;
    private int allTimeBestCombo;
    private int farmStage;
    private String lastLoginDate;

    public PlayerProfile() {
        farmerName       = "Farmer";
        equippedSkin     = SkinType.FARMER_MALE;
        equippedBasket   = BasketSkin.WOVEN;
        totalCoins       = 0;
        totalGamesPlayed = 0;
        totalBallsCaught = 0;
        allTimeBestScore = 0;
        allTimeBestCombo = 0;
        farmStage        = 1;
        lastLoginDate    = "";
    }
    public String getFarmerName()               { return farmerName; }
    public void setFarmerName(String name)      { farmerName = name; }
    public SkinType getEquippedSkin()           { return equippedSkin; }
    public void setEquippedSkin(SkinType s)     { equippedSkin = s; }
    public BasketSkin getEquippedBasket()       { return equippedBasket; }
    public void setEquippedBasket(BasketSkin b) { equippedBasket = b; }
    public int getTotalCoins()                  { return totalCoins; }
    public void addCoins(int amount)            { totalCoins += amount; }
    public boolean spendCoins(int amount) {
        if (totalCoins >= amount) { totalCoins -= amount; return true; }
        return false;
    }
    public int getTotalGamesPlayed()            { return totalGamesPlayed; }
    public void incrementGamesPlayed()          { totalGamesPlayed++; }
    public int getTotalBallsCaught()            { return totalBallsCaught; }
    public void addBallsCaught(int count)       { totalBallsCaught += count; }
    public int getAllTimeBestScore()             { return allTimeBestScore; }
    public void updateBestScore(int score) {
        if (score > allTimeBestScore) allTimeBestScore = score;
    }
    public int getAllTimeBestCombo()             { return allTimeBestCombo; }
    public void updateBestCombo(int combo) {
        if (combo > allTimeBestCombo) allTimeBestCombo = combo;
    }
    public int getFarmStage()                   { return farmStage; }
    public void setFarmStage(int s)             { farmStage = Math.max(1, Math.min(20, s)); }
    public String getLastLoginDate()            { return lastLoginDate; }
    public void setLastLoginDate(String date)   { lastLoginDate = date; }
}