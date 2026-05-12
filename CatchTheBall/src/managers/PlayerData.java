package OOP_PROJECT.CatchTheBall.src.managers;

import OOP_PROJECT.CatchTheBall.src.enums.BasketSkin;
import OOP_PROJECT.CatchTheBall.src.enums.SkinType;
import OOP_PROJECT.CatchTheBall.src.interfaces.Saveable;
import OOP_PROJECT.CatchTheBall.src.models.PlayerProfile;
import OOP_PROJECT.CatchTheBall.src.utils.FileUtils;

import java.util.*;

public class PlayerData implements Saveable {
    private String        saveFile = "player.txt";
    private PlayerProfile profile;
    private Set<SkinType>   ownedSkins;
    private Set<BasketSkin> ownedBaskets;
    private String        achievementData;
    private int           farmStage;

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

    public void setSaveFile(String path) {
        saveFile        = path;
        profile         = new PlayerProfile();
        ownedSkins      = new HashSet<>();
        ownedBaskets    = new HashSet<>();
        achievementData = "";
        farmStage       = 1;
        ownedSkins.add(SkinType.FARMER_MALE);
        ownedBaskets.add(BasketSkin.WOVEN);
        load();
    }

    public PlayerProfile getProfile()    { return profile; }
    public int  getTotalCoins()          { return profile.getTotalCoins(); }

    public void addCoins(int a) {
        profile.addCoins(a);
        save();
    }

    public boolean spendCoins(int amount) {
        boolean ok = profile.spendCoins(amount);
        if (ok) save();
        return ok;
    }

    public boolean ownsSkin(SkinType s)     { return ownedSkins.contains(s); }
    public boolean ownsBasket(BasketSkin b) { return ownedBaskets.contains(b); }
    public void    buySkin(SkinType s)      { ownedSkins.add(s); save(); }
    public void    buyBasket(BasketSkin b)  { ownedBaskets.add(b); save(); }

    public SkinType   getEquippedSkin()         { return profile.getEquippedSkin(); }
    public BasketSkin getEquippedBasket()        { return profile.getEquippedBasket(); }
    public void       equipSkin(SkinType s)     { profile.setEquippedSkin(s); save(); }
    public void       equipBasket(BasketSkin b) { profile.setEquippedBasket(b); save(); }

    public int  getFarmStage()     { return farmStage; }
    public void setFarmStage(int s){ farmStage = s; profile.setFarmStage(s); save(); }
    public void upgradeFarm()      { farmStage++; profile.setFarmStage(farmStage); save(); }

    public String getAchievementData()         { return achievementData; }
    public void   setAchievementData(String d) { achievementData = d; save(); }

    @Override
    public void save() {
        StringBuilder sb = new StringBuilder();
        sb.append("coins=").append(profile.getTotalCoins()).append("\n");
        sb.append("farmerName=").append(profile.getFarmerName()).append("\n");
        sb.append("equippedSkin=").append(profile.getEquippedSkin().name()).append("\n");
        sb.append("equippedBasket=").append(profile.getEquippedBasket().name()).append("\n");
        sb.append("gamesPlayed=").append(profile.getTotalGamesPlayed()).append("\n");
        sb.append("bestScore=").append(profile.getAllTimeBestScore()).append("\n");
        sb.append("bestCombo=").append(profile.getAllTimeBestCombo()).append("\n");
        sb.append("farmStage=").append(farmStage).append("\n");
        sb.append("tutorialShown=").append(profile.isTutorialShown()).append("\n");
        sb.append("totalCoinsEarned=").append(profile.getTotalCoinsEarned()).append("\n");
        sb.append("totalCoinsSpent=").append(profile.getTotalCoinsSpent()).append("\n");
        sb.append("totalBallsCaught=").append(profile.getTotalBallsCaught()).append("\n");
        sb.append("lastLogin=").append(profile.getLastLoginDate()).append("\n");
        sb.append("loginStreak=").append(profile.getConsecutiveLoginDays()).append("\n");

        int[] hist = profile.getLastTenScores();
        int   cnt  = profile.getScoreHistoryCount();
        StringBuilder hs = new StringBuilder();
        for (int i = 0; i < cnt; i++) {
            if (i > 0) hs.append(",");
            hs.append(hist[i]);
        }
        sb.append("scoreHistory=").append(hs).append("\n");

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

        try {
            FileUtils.writeLines(saveFile, sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        if (!FileUtils.fileExists(saveFile)) return;
        try {
            Map<String, String> kv = FileUtils.readKeyValue(saveFile);

            if (kv.containsKey("coins")) {
                int saved = Integer.parseInt(kv.get("coins"));
                // Reset to 0 first then add saved amount
                while (profile.getTotalCoins() > 0)
                    profile.spendCoins(profile.getTotalCoins());
                profile.addCoins(saved);
            }
            if (kv.containsKey("farmerName"))
                profile.setFarmerName(kv.get("farmerName"));
            if (kv.containsKey("equippedSkin"))
                try { profile.setEquippedSkin(SkinType.valueOf(kv.get("equippedSkin"))); }
                catch (Exception ignored) {}
            if (kv.containsKey("equippedBasket"))
                try { profile.setEquippedBasket(BasketSkin.valueOf(kv.get("equippedBasket"))); }
                catch (Exception ignored) {}
            if (kv.containsKey("gamesPlayed")) {
                int gp = Integer.parseInt(kv.get("gamesPlayed"));
                for (int i = 0; i < gp; i++) profile.incrementGamesPlayed();
            }
            if (kv.containsKey("bestScore"))
                profile.updateBestScore(Integer.parseInt(kv.get("bestScore")));
            if (kv.containsKey("bestCombo"))
                profile.updateBestCombo(Integer.parseInt(kv.get("bestCombo")));
            if (kv.containsKey("farmStage"))
                farmStage = Integer.parseInt(kv.get("farmStage"));
            if (kv.containsKey("tutorialShown"))
                profile.setTutorialShown(Boolean.parseBoolean(kv.get("tutorialShown")));
            if (kv.containsKey("totalCoinsEarned"))
                profile.setTotalCoinsEarned(Integer.parseInt(kv.get("totalCoinsEarned")));
            if (kv.containsKey("totalCoinsSpent"))
                profile.setTotalCoinsSpent(Integer.parseInt(kv.get("totalCoinsSpent")));
            if (kv.containsKey("totalBallsCaught"))
                profile.addBallsCaught(Integer.parseInt(kv.get("totalBallsCaught")));
            if (kv.containsKey("lastLogin"))
                profile.setLastLoginDate(kv.get("lastLogin"));
            if (kv.containsKey("loginStreak"))
                profile.setConsecutiveLoginDays(Integer.parseInt(kv.get("loginStreak")));
            if (kv.containsKey("scoreHistory")) {
                String[] parts = kv.get("scoreHistory").split(",");
                int[] arr = new int[parts.length];
                for (int i = 0; i < parts.length; i++)
                    try { arr[i] = Integer.parseInt(parts[i].trim()); }
                    catch (Exception ignored) {}
                profile.setScoreHistory(arr, parts.length);
            }
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}