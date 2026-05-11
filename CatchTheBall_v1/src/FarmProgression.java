public class FarmProgression {
    private static final String[] STAGES = {
        "🏚️","🏚️","🏠","🏠","🏠","🏡","🏡","🏡","🏘️","🏘️",
        "🏘️","🏰","🏰","🏰","🏰","🏯","🏯","🏯","🏰","👑"
    };
    private static final String[] NAMES = {
        "Old Shack","Old Shack","House","House","House",
        "Farm House","Farm House","Farm House","Village","Village",
        "Village","Grand Farm","Grand Farm","Grand Farm","Grand Farm",
        "Castle Farm","Castle Farm","Castle Farm","Royal Farm","Legend Farm"
    };
    private static final int[] UPGRADE_COSTS = {
        50,100,150,200,250,300,350,400,500,600,
        700,800,900,1000,1200,1400,1600,1800,2000,0
    };

    private int stage; // 1-based

    public FarmProgression(int stage) {
        this.stage = Math.max(1, Math.min(20, stage));
    }

    public String getEmoji() { return STAGES[stage - 1]; }
    public String getName() { return NAMES[stage - 1]; }
    public int getStage() { return stage; }
    public int getUpgradeCost() {
        if (stage >= 20) return 0;
        return UPGRADE_COSTS[stage - 1];
    }
    public boolean canUpgrade() { return stage < 20; }
    public void upgrade() { if (stage < 20) stage++; }
}
