import java.util.*;
import java.io.*;

public class PlayerData {
    private static final String FILE = "player.txt";

    private int totalCoins;
    private Set<SkinType> ownedSkins;
    private Set<BasketSkin> ownedBaskets;
    private SkinType equippedSkin;
    private BasketSkin equippedBasket;
    private int farmStage;
    private String achievementData;
    private String farmerName;

    public PlayerData() {
        totalCoins = 0;
        ownedSkins = new HashSet<>();
        ownedSkins.add(SkinType.FARMER_MALE);
        ownedBaskets = new HashSet<>();
        ownedBaskets.add(BasketSkin.WOVEN);
        equippedSkin = SkinType.FARMER_MALE;
        equippedBasket = BasketSkin.WOVEN;
        farmStage = 1;
        achievementData = "";
        farmerName = "";
        load();
    }

    public int getTotalCoins() { return totalCoins; }
    public void addCoins(int amount) { totalCoins += amount; save(); }
    public boolean spendCoins(int amount) {
        if (totalCoins >= amount) { totalCoins -= amount; save(); return true; }
        return false;
    }

    public Set<SkinType> getOwnedSkins() { return ownedSkins; }
    public Set<BasketSkin> getOwnedBaskets() { return ownedBaskets; }
    public boolean ownsSkin(SkinType s) { return ownedSkins.contains(s); }
    public boolean ownsBasket(BasketSkin b) { return ownedBaskets.contains(b); }
    public void buySkin(SkinType s) { ownedSkins.add(s); save(); }
    public void buyBasket(BasketSkin b) { ownedBaskets.add(b); save(); }

    public SkinType getEquippedSkin() { return equippedSkin; }
    public BasketSkin getEquippedBasket() { return equippedBasket; }
    public void equipSkin(SkinType s) { equippedSkin = s; save(); }
    public void equipBasket(BasketSkin b) { equippedBasket = b; save(); }

    public int getFarmStage() { return farmStage; }
    public void upgradeFarm() { if (farmStage < 20) { farmStage++; save(); } }

    public String getAchievementData() { return achievementData; }
    public void setAchievementData(String data) { this.achievementData = data; save(); }

    public String getFarmerName() { return farmerName; }
    public void setFarmerName(String name) { farmerName = name; save(); }

    private void save() {
        synchronized (this) {
            try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            pw.println("coins=" + totalCoins);
            pw.println("farmStage=" + farmStage);
            pw.println("equippedSkin=" + equippedSkin.name());
            pw.println("equippedBasket=" + equippedBasket.name());
            StringBuilder skins = new StringBuilder();
            for (SkinType s : ownedSkins) { if (skins.length()>0) skins.append(","); skins.append(s.name()); }
            pw.println("ownedSkins=" + skins);
            StringBuilder baskets = new StringBuilder();
            for (BasketSkin b : ownedBaskets) { if (baskets.length()>0) baskets.append(","); baskets.append(b.name()); }
            pw.println("ownedBaskets=" + baskets);
            pw.println("achievements=" + achievementData);
            pw.println("farmerName=" + farmerName);
        } catch (Exception e) { e.printStackTrace(); }
        }
    }

    private void load() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] kv = line.split("=", 2);
                if (kv.length < 2) continue;
                String key = kv[0].trim(), val = kv[1].trim();
                switch (key) {
                    case "coins": totalCoins = Integer.parseInt(val); break;
                    case "farmStage": farmStage = Math.max(1, Integer.parseInt(val)); break;
                    case "equippedSkin": try { equippedSkin = SkinType.valueOf(val); } catch (Exception e2) {} break;
                    case "equippedBasket": try { equippedBasket = BasketSkin.valueOf(val); } catch (Exception e2) {} break;
                    case "ownedSkins":
                        for (String s : val.split(",")) {
                            try { ownedSkins.add(SkinType.valueOf(s.trim())); } catch (Exception e2) {}
                        }
                        break;
                    case "ownedBaskets":
                        for (String b : val.split(",")) {
                            try { ownedBaskets.add(BasketSkin.valueOf(b.trim())); } catch (Exception e2) {}
                        }
                        break;
                    case "achievements": achievementData = val; break;
                    case "farmerName": farmerName = val; break;
                }
            }
        } catch (Exception e) { /* fresh start */ }
    }
}
