package OOP_PROJECT.CatchTheBall.src.enums;

public enum BasketSkin {
    WOVEN("Woven Basket",    0),
    METAL("Metal Bucket",   80),
    GOLDEN("Gold Basket",  200),
    CART("Farm Cart",      300),
    DIAMOND("Diamond",     500),
    BAMBOO("Bamboo Basket", 120),
    CLAY("Clay Pot",        180),
    CRYSTAL("Crystal Bowl", 600),
    MAGIC("Magic Cauldron", 750),
    LEGENDARY("Legend Ark",1000);

    private final String displayName;
    private final int cost;
    BasketSkin(String d,int c){displayName=d;cost=c;}
    public String getDisplayName(){return displayName;}
    public int getCost(){return cost;}
}