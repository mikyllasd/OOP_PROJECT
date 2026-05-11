package enums;
public enum SkinType {
    FARMER_MALE("Default Farmer", 0),
    FARMER_FEMALE("Female Farmer", 100),
    FARM_KID("Farm Kid", 200),
    COWBOY("Cowboy", 350),
    WIZARD("Wizard Farmer", 500),
    NINJA("Ninja Farmer", 750),
    ROYAL("Royal Farmer", 1000);

    private final String displayName;
    private final int cost;
    SkinType(String displayName, int cost) {
        this.displayName = displayName;
        this.cost = cost;
    }
    public String getDisplayName() { return displayName; }
    public int getCost()           { return cost; }
}