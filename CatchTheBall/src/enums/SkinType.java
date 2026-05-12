package OOP_PROJECT.CatchTheBall.src.enums;

public enum SkinType {
    FARMER_MALE("Farmer Boy",    0),
    FARMER_FEMALE("Farm Girl",  100),
    FARM_KID("Farm Kid",        150),
    COWBOY("Cowboy",            300),
    WIZARD("Wizard",            450),
    NINJA("Ninja",              600),
    ROYAL("Royal Farmer",       800),
    CHEF("Chef Farmer",         500),
    PIRATE("Pirate Farmer",     700),
    EXPLORER("Explorer",        900);

    private final String displayName;
    private final int cost;
    SkinType(String d,int c){displayName=d;cost=c;}
    public String getDisplayName(){return displayName;}
    public int getCost(){return cost;}
}