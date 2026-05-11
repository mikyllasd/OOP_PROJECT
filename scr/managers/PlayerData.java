package managers;
import enums.SkinType;
import enums.BasketSkin;
import interfaces.Saveable;
import models.PlayerProfile;
import models.FarmProgression;
import utils.FileUtils;
import java.util.*;
public class PlayerData implements Saveable {
    private static final String FILE = "player.txt";
    private PlayerProfile profile;
    private Set<SkinType>   ownedSkins;
    private Set<BasketSkin> ownedBaskets;
    private String achievementData;
    private int farmStage;

    public PlayerData() {
        profile         = new PlayerProfile();
        ownedSkins      = new HashSet<>();
        ownedBaskets    = new HashSet<>();
        achievementData = "";
        farmStage       = 1;
        ownedSkins.add(SkinType.FARMER_MALE);
        ownedBaskets.add(BasketSkin.WOVEN);
        load();
    }
    public PlayerProfile getProfile()     { return profile; }
    public int getTotalCoins()            { return profile.getTotalCoins(); }
    public void addCoins(int amount)      { profile.addCoins(amount); save(); }
    public boolean spendCoins(int amount) {
        boolean ok = profile.spendCoins(amount);
        if (ok) save();
        return ok;
    }
    public boolean ownsSkin(SkinType s)     { return ownedSkins.contains(s); }
    public boolean ownsBasket(BasketSkin b) { return ownedBaskets.contains(b); }
    public void buySkin(SkinType s)         { ownedSkins.add(s); save(); }
    public void buyBasket(BasketSkin b)     { ownedBaskets.add(b); save(); }
    public SkinType getEquippedSkin()       { return profile.getEquippedSkin(); }
    public BasketSkin getEquippedBasket()   { return profile.getEquippedBasket(); }
    public void equipSkin(SkinType s)       { profile.setEquippedSkin(s); save(); }
    public void equipBasket(BasketSkin b)   { profile.setEquippedBasket(b); save(); }
    public int getFarmStage()               { return farmStage; }
    public void setFarmStage(int s)         { farmStage = s; profile.setFarmStage(s); save(); }
    public void upgradeFarm() {
        if (farmStage < 20) { farmStage++; profile.setFarmStage(farmStage); save(); }
    }
    public String getAchievementData()          { return achievementData; }
    public void setAchievementData(String d)    { achievementData = d; save(); }
    public int getOwnedSkinsCount()             { return ownedSkins.size(); }

    @Override
    public void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("coins=").append(profile.getTotalCoins()).append("\n");
        sb.append("equippedSkin=").append(profile.getEquippedSkin().name()).append("\n");
        sb.append("equippedBasket=").append(profile.getEquippedBasket().name()).append("\n");
        sb.append("gamesPlayed=").append(profile.getTotalGamesPlayed()).append("\n");
        sb.append("bestScore=").append(profile.getAllTimeBestScore()).append("\n");
        sb.append("bestCombo=").append(profile.getAllTimeBestCombo()).append("\n");
        sb.append("ballsCaught=").append(profile.getTotalBallsCaught()).append("\n");
        sb.append("farmStage=").append(farmStage).append("\n");
        StringBuilder skins = new StringBuilder();
        for (SkinType s : ownedSkins) {
            if (skins.length() > 0) skins.append(",");
            skins.append(s.name());
        }
        sb.append("ownedSkins=").append(skins).append("\n");
        StringBuilder baskets = new StringBuilder();
        for (BasketSkin b : ownedBaskets) {
            if (baskets.length() > 0) baskets.append(",");
            baskets.append(b.name());
        }
        sb.append("ownedBaskets=").append(baskets).append("\n");
        sb.append("achievements=").append(achievementData).append("\n");
        try { FileUtils.writeLines(FILE, sb.toString()); }
        catch (Exception e) { e.printStackTrace(); }
    }
    @Override
    public void load() {
        if (!FileUtils.fileExists(FILE)) return;
        try {
            Map<String, String> kv = FileUtils.readKeyValue(FILE);
            if (kv.containsKey("coins"))
                profile.addCoins(Integer.parseInt(kv.get("coins")));
            if (kv.containsKey("equippedSkin"))
                try { profile.setEquippedSkin(SkinType.valueOf(kv.get("equippedSkin"))); }
                catch (Exception ignored) {}
            if (kv.containsKey("equippedBasket"))
                try { profile.setEquippedBasket(BasketSkin.valueOf(kv.get("equippedBasket"))); }
                catch (Exception ignored) {}
            if (kv.containsKey("gamesPlayed"))
                for (int i = 0; i < Integer.parseInt(kv.get("gamesPlayed")); i++)
                    profile.incrementGamesPlayed();
            if (kv.containsKey("bestScore"))
                profile.updateBestScore(Integer.parseInt(kv.get("bestScore")));
            if (kv.containsKey("bestCombo"))
                profile.updateBestCombo(Integer.parseInt(kv.get("bestCombo")));
            if (kv.containsKey("ballsCaught"))
                profile.addBallsCaught(Integer.parseInt(kv.get("ballsCaught")));
            if (kv.containsKey("farmStage"))
                farmStage = Integer.parseInt(kv.get("farmStage"));
            if (kv.containsKey("ownedSkins"))
                for (String s : kv.get("ownedSkins").split(","))
                    try { ownedSkins.add(SkinType.valueOf(s.trim())); }
                    catch (Exception ignored) {}
            if (kv.containsKey("ownedBaskets"))
                for (String b : kv.get("ownedBaskets").split(","))
                    try { ownedBaskets.add(BasketSkin.valueOf(b.trim())); }
                    catch (Exception ignored) {}
            if (kv.containsKey("achievements"))
                achievementData = kv.get("achievements");
        } catch (Exception ignored) {}
    }
}