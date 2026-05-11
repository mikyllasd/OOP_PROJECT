public enum SkinType {
    FARMER_MALE   ("Default Farmer",    0,    "👨‍🌾"),
    FARMER_FEMALE ("Female Farmer",     100,  "👩‍🌾"),
    FARM_KID      ("Farm Kid",          200,  "🧒"),
    COWBOY        ("Cowboy",            350,  "🤠"),
    WIZARD        ("Wizard Farmer",     500,  "🧙"),
    NINJA         ("Ninja Farmer",      750,  "🥷"),
    BEEKEEPER     ("Beekeeper",         1200, "🐝"),
    FLOWER_FARMER ("Flower Farmer",     1400, "💐"),
    TRACTOR_DRIVER("Tractor Driver",    1600, "🚜"),
    ROYAL         ("Royal Farmer",      1000, "👑");

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