public enum SkinType {
    FARMER_MALE("👨‍🌾", "Default Farmer", 0),
    FARMER_FEMALE("👩‍🌾", "Female Farmer", 100),
    FARM_KID("🧑‍🌾", "Farm Kid", 200),
    COWBOY("🤠", "Cowboy", 350),
    WIZARD("🧙", "Wizard Farmer", 500),
    NINJA("🥷", "Ninja Farmer", 750),
    BEEKEEPER("🐝", "Beekeeper", 1200),
    FLOWER_FARMER("🌸", "Flower Farmer", 1400),
    TRACTOR_DRIVER("🚜", "Tractor Driver", 1600),
    ROYAL("👑", "Royal Farmer", 1000);

    private final String emoji;
    private final String displayName;
    private final int cost;

    SkinType(String emoji, String displayName, int cost) {
        this.emoji = emoji;
        this.displayName = displayName;
        this.cost = cost;
    }

    public String getEmoji() { return emoji; }
    public String getDisplayName() { return displayName; }
    public int getCost() { return cost; }
}
