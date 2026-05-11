import java.util.*;

public class AchievementManager {
    private List<Achievement> achievements;
    private List<Achievement> pendingToasts;

    public AchievementManager() {
        achievements = new ArrayList<>();
        pendingToasts = new ArrayList<>();
        achievements.add(new Achievement("first_harvest", "🌾", "First Harvest", "Catch your first fruit"));
        achievements.add(new Achievement("on_fire", "🔥", "On Fire", "Reach x3 combo"));
        achievements.add(new Achievement("rich_farmer", "💰", "Rich Farmer", "Earn 500 coins total"));
        achievements.add(new Achievement("speed_farmer", "⚡", "Speed Farmer", "Reach Level 5 with 90s remaining"));
        achievements.add(new Achievement("dream_farm", "🏰", "Dream Farm", "Reach farm stage 10"));
        achievements.add(new Achievement("legend", "👑", "Legend", "Reach Level 10"));
    }

    public boolean tryUnlock(String id) {
        for (Achievement a : achievements) {
            if (a.getId().equals(id) && !a.isUnlocked()) {
                a.unlock();
                pendingToasts.add(a);
                return true;
            }
        }
        return false;
    }

    public boolean isUnlocked(String id) {
        for (Achievement a : achievements) {
            if (a.getId().equals(id)) return a.isUnlocked();
        }
        return false;
    }

    public Achievement pollToast() {
        if (pendingToasts.isEmpty()) return null;
        return pendingToasts.remove(0);
    }

    public List<Achievement> getAll() { return achievements; }

    public void loadFromString(String data) {
        if (data == null || data.isEmpty()) return;
        String[] ids = data.split(",");
        for (String id : ids) {
            for (Achievement a : achievements) {
                if (a.getId().equals(id.trim())) a.unlock();
            }
        }
    }

    public String saveToString() {
        StringBuilder sb = new StringBuilder();
        for (Achievement a : achievements) {
            if (a.isUnlocked()) {
                if (sb.length() > 0) sb.append(",");
                sb.append(a.getId());
            }
        }
        return sb.toString();
    }
}
