package OOP_PROJECT.CatchTheBall.src.models;

public class FarmProgression {

    private static final String[] EMOJIS = {
        "\uD83C\uDFDA\uFE0F","\uD83C\uDFDA\uFE0F",
        "\uD83C\uDFE0","\uD83C\uDFE0","\uD83C\uDFE0",
        "\uD83C\uDFE1","\uD83C\uDFE1","\uD83C\uDFE1",
        "\uD83C\uDFD8\uFE0F","\uD83C\uDFD8\uFE0F","\uD83C\uDFD8\uFE0F",
        "\uD83C\uDFF0","\uD83C\uDFF0","\uD83C\uDFF0","\uD83C\uDFF0",
        "\uD83C\uDFEF","\uD83C\uDFEF","\uD83C\uDFEF",
        "\uD83C\uDFF0","\uD83D\uDC51"
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
        300,600,1000,1500,2100,2800,3600,4500,5500,6600,
        7800,9100,10500,12000,13600,15300,17100,19000,21000,0
    };

    private int stage;

    public FarmProgression(int stage) {
        this.stage = Math.max(1, Math.min(20, stage));
    }

    public String  getEmoji()   { return EMOJIS[stage - 1]; }
    public String  getName()    { return NAMES[stage - 1]; }
    public int     getStage()   { return stage; }
    public boolean canUpgrade() { return stage < 20; }
    public void    upgrade()    { if (stage < 20) stage++; }

    public int getUpgradeCost() {
        return stage >= 20 ? 0 : UPGRADE_COSTS[stage - 1];
    }
}