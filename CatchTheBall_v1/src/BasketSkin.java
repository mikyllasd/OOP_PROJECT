public enum BasketSkin {
    WOVEN("🧺", "Woven Basket", 0),
    METAL("🪣", "Metal Bucket", 80),
    GOLDEN("🎁", "Golden Basket", 200),
    CART("🛒", "Farm Cart", 300),
    DIAMOND("💎", "Diamond Basket", 500);

    private final String emoji;
    private final String displayName;
    private final int cost;

    BasketSkin(String emoji, String displayName, int cost) {
        this.emoji = emoji;
        this.displayName = displayName;
        this.cost = cost;
    }

    public String getEmoji() { return emoji; }
    public String getDisplayName() { return displayName; }
    public int getCost() { return cost; }
}
