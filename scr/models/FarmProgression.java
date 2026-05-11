package models;
public class FarmProgression {
    private static final String[] EMOJIS = {
        "\uD83C\uDFDA\uFE0F","\uD83C\uDFDA\uFE0F",
        "\uD83C\uDFE0","\uD83C\uDFE0","\uD83C\uDFE0",
        "\uD83C\uDFE1","\uD83C\uDFE1","\uD83C\uDFE1",
        "\uD83C\uDFD8\uFE0F","\uD83C\uDFD8\uFE0F",
        "\uD83C\uDFD8\uFE0F","\uD83C\uDFF0","\uD83C\uDFF0",
        "\uD83C\uDFF0","\uD83C\uDFF0","\uD83C\uDFEF",
        "\uD83C\uDFEF","\uD83C\uDFEF","\uD83C\uDFF0",
        "\uD83D\uDC51"
    };
    private static final String[] NAMES = {
        "Old Shack","Old Shack","House","House","House",
        "Farm House","Farm House","Farm House",
        "Village","Village","Village",
        "Grand Farm","Grand Farm","Grand Farm","Grand Farm",
        "Castle Farm","Castle Farm","Castle Farm",
        "Royal Farm","Legend Farm"
    };
    private static final int[] UPGRADE_COSTS = {
        50,100,150,200,250,300,350,400,500,600,
        700,800,900,1000,1200,1400,1600,1800,2000,0
    };
    private int stage;
    public FarmProgression(int stage) {
        this.stage = Math.max(1, Math.min(20, stage));
    }
    public String getEmoji()    { return EMOJIS[stage - 1]; }
    public String getName()     { return NAMES[stage - 1]; }
    public int getStage()       { return stage; }
    public boolean canUpgrade() { return stage < 20; }
    public void upgrade()       { if (stage < 20) stage++; }
    public int getUpgradeCost() {
        return stage >= 20 ? 0 : UPGRADE_COSTS[stage - 1];
    }
}