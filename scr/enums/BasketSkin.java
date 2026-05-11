package enums;
public enum BasketSkin {
    WOVEN("Woven Basket", 0),
    METAL("Metal Bucket", 80),
    GOLDEN("Golden Basket", 200),
    CART("Farm Cart", 300),
    DIAMOND("Diamond Basket", 500);

    private final String displayName;
    private final int cost;
    BasketSkin(String displayName, int cost) {
        this.displayName = displayName;
        this.cost = cost;
    }
    public String getDisplayName() { return displayName; }
    public int getCost()           { return cost; }
}