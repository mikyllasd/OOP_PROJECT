package managers;

import models.Achievement;
import java.util.*;

public class AchievementManager {

    private List<Achievement> achievements;
    private Queue<Achievement> pendingToasts;

    public AchievementManager() {
        achievements  = new ArrayList<>();
        pendingToasts = new LinkedList<>();
        initAchievements();
    }

    private void initAchievements() {
        add("first_catch",    "\uD83C\uDF3E",
                "First Harvest",     "Catch your first fruit",       1);
        add("on_fire",        "\uD83D\uDD25",
                "On Fire",           "Reach x3 combo",               3);
        add("hot_streak",     "\uD83E\uDD75",
                "Hot Streak",        "Reach x8 combo",               8);
        add("untouchable",    "\uD83D\uDEE1\uFE0F",
                "Untouchable",       "Reach x12 combo",              12);
        add("rich_farmer",    "\uD83D\uDCB0",
                "Rich Farmer",       "Earn 500 total coins",         500);
        add("millionaire",    "\uD83D\uDCB0",
                "Gold Farmer",       "Earn 2000 total coins",        2000);
        add("speed_farmer",   "\u26A1",
                "Speed Farmer",      "Reach Level 5 with 90s left",  1);
        add("dream_farm",     "\uD83C\uDFF0",
                "Dream Farm",        "Reach farm stage 10",          10);
        add("legend",         "\uD83D\uDC51",
                "Legend",            "Reach Level 10",               10);
        add("century",        "\uD83C\uDF4E",
                "Century",           "Catch 100 balls in one game",  100);
        add("collector",      "\uD83C\uDF1F",
                "Collector",         "Unlock 5 skins",               5);
        add("perfectionist",  "\u2B50",
                "Perfectionist",     "Finish a level missing nothing",1);
        add("veteran",        "\uD83C\uDFC5",
                "Veteran",           "Play 20 games",                20);
    }

    private void add(String id, String emoji,
                     String name, String desc, int target) {
        achievements.add(new Achievement(
                id, emoji, name, desc, target));
    }

    public boolean tryUnlock(String id) {
        for (Achievement a : achievements) {
            if (a.getId().equals(id) && !a.isUnlocked()) {
                a.unlock();
                pendingToasts.offer(a);
                return true;
            }
        }
        return false;
    }

    public void updateProgress(String id, int value) {
        for (Achievement a : achievements) {
            if (a.getId().equals(id) && !a.isUnlocked()) {
                boolean wasBefore = a.isUnlocked();
                a.setProgress(value);
                if (!wasBefore && a.isUnlocked())
                    pendingToasts.offer(a);
            }
        }
    }

    public boolean isUnlocked(String id) {
        for (Achievement a : achievements)
            if (a.getId().equals(id)) return a.isUnlocked();
        return false;
    }

    public Achievement pollToast() { return pendingToasts.poll(); }
    public List<Achievement> getAll() { return achievements; }

    public void loadFromString(String data) {
        if (data == null || data.isEmpty()) return;
        for (String token : data.split(";")) {
            String[] parts = token.split(":");
            if (parts.length < 1) continue;
            String id = parts[0].trim();
            for (Achievement a : achievements) {
                if (a.getId().equals(id)) {
                    if (parts.length >= 2) {
                        try {
                            a.setProgress(
                                    Integer.parseInt(parts[1].trim()));
                        } catch (NumberFormatException ignored) {}
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
                sb.append(a.getId())
                  .append(":")
                  .append(a.getProgressCurrent());
            }
        }
        return sb.toString();
    }
}