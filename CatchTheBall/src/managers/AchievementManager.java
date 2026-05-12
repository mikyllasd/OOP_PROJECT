package OOP_PROJECT.CatchTheBall.src.managers;

import OOP_PROJECT.CatchTheBall.src.models.Achievement;

import java.util.*;

public class AchievementManager {
    private List<Achievement>  achievements;
    private Queue<Achievement> pendingToasts;

    public AchievementManager() {
        achievements  = new ArrayList<>();
        pendingToasts = new LinkedList<>();
        initAchievements();
    }

    private void initAchievements() {
        add("first_catch",   "\uD83C\uDF3E", "First Harvest",    "Catch your first fruit",       1);
        add("on_fire",       "\uD83D\uDD25", "On Fire",          "Reach x3 combo",               3);
        add("hot_streak",    "\uD83E\uDD75", "Hot Streak",       "Reach x8 combo",               8);
        add("untouchable",   "\uD83D\uDEE1\uFE0F","Untouchable", "Reach x12 combo",             12);
        add("rich_farmer",   "\uD83D\uDCB0", "Rich Farmer",      "Earn 500 total coins",        500);
        add("millionaire",   "\uD83D\uDCB0", "Gold Farmer",      "Earn 2000 total coins",      2000);
        add("dream_farm",    "\uD83C\uDFF0", "Dream Farm",       "Reach farm stage 10",          10);
        add("legend",        "\uD83D\uDC51", "Legend",           "Reach Level 10",               10);
        add("century",       "\uD83C\uDF4E", "Century",          "Catch 100 balls in one game", 100);
        add("veteran",       "\uD83C\uDFC5", "Veteran",          "Play 20 games",                20);
        add("rainbow_catch", "\uD83C\uDF08", "Rainbow Catcher",  "Catch a rainbow ball",          1);
        add("frozen_catch",  "\u2744",       "Ice Harvester",    "Catch a frozen ball",           1);
    }

    private void add(String id, String emoji, String name, String desc, int target) {
        achievements.add(new Achievement(id, emoji, name, desc, target));
    }

    public boolean tryUnlock(String id) {
        for (Achievement a : achievements) {
            if (a.getId().equals(id) && !a.isUnlocked()) {
                a.unlock(); pendingToasts.offer(a); return true;
            }
        }
        return false;
    }

    public void updateProgress(String id, int value) {
        for (Achievement a : achievements) {
            if (a.getId().equals(id) && !a.isUnlocked()) {
                boolean before = a.isUnlocked();
                a.setProgress(value);
                if (!before && a.isUnlocked()) pendingToasts.offer(a);
            }
        }
    }

    public boolean isUnlocked(String id) {
        for (Achievement a : achievements)
            if (a.getId().equals(id)) return a.isUnlocked();
        return false;
    }

    public Achievement       pollToast() { return pendingToasts.poll(); }
    public List<Achievement> getAll()    { return achievements; }

    public void loadFromString(String data) {
        if (data == null || data.isEmpty()) return;
        for (String token : data.split(";")) {
            String[] parts = token.split(":");
            if (parts.length < 1) continue;
            String id = parts[0].trim();
            for (Achievement a : achievements) {
                if (a.getId().equals(id)) {
                    if (parts.length >= 2) {
                        try { a.setProgress(Integer.parseInt(parts[1].trim())); }
                        catch (NumberFormatException ignored) {}
                    }
                    a.unlock();
                }
            }
        }
    }

    public String saveToString() {
        StringBuilder sb = new StringBuilder();
        for (Achievement a : achievements) {
            if (a.isUnlocked()) {
                if (sb.length() > 0) sb.append(";");
                sb.append(a.getId()).append(":").append(a.getProgressCurrent());
            }
        }
        return sb.toString();
    }
}