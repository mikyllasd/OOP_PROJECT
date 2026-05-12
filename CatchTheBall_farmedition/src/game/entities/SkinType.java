package game.entities;

public enum SkinType {
    FARMER_MALE   ("Default Farmer",    0,    "ðŸ‘¨â€ðŸŒ¾"),
    FARMER_FEMALE ("Female Farmer",     100,  "ðŸ‘©â€ðŸŒ¾"),
    FARM_KID      ("Farm Kid",          200,  "ðŸ§’"),
    COWBOY        ("Cowboy",            350,  "ðŸ¤ "),
    WIZARD        ("Wizard Farmer",     500,  "ðŸ§™"),
    NINJA         ("Ninja Farmer",      750,  "ðŸ¥·"),
    BEEKEEPER     ("Beekeeper",         1200, "ðŸ"),
    FLOWER_FARMER ("Flower Farmer",     1400, "ðŸ’"),
    TRACTOR_DRIVER("Tractor Driver",    1600, "ðŸšœ"),
    ROYAL         ("Royal Farmer",      1000, "ðŸ‘‘");

    private final String displayName;
    private final int    cost;
    private final String emoji;

    SkinType(String displayName, int cost, String emoji) {
        this.displayName = displayName;
        this.cost        = cost;
        this.emoji       = emoji;
    }

    public String getDisplayName() { return displayName; }
    public int    getCost()        { return cost; }
    public String getEmoji()       { return emoji; }
}
