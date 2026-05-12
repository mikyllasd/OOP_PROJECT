package OOP_PROJECT.CatchTheBall.src.models;

public class AccountData {
    private String accountName;
    private int    bestScore;
    private int    totalCoins;
    private int    farmStage;
    private int    gamesPlayed;

    public AccountData(String accountName, int bestScore,
                       int totalCoins, int farmStage, int gamesPlayed) {
        this.accountName = accountName;
        this.bestScore   = bestScore;
        this.totalCoins  = totalCoins;
        this.farmStage   = farmStage;
        this.gamesPlayed = gamesPlayed;
    }

    public String getAccountName() { return accountName; }
    public int    getBestScore()   { return bestScore; }
    public int    getTotalCoins()  { return totalCoins; }
    public int    getFarmStage()   { return farmStage; }
    public int    getGamesPlayed() { return gamesPlayed; }
}