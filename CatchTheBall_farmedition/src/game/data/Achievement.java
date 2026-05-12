package game.data;

public class Achievement {
    private String id;
    private String emoji;
    private String name;
    private String description;
    private boolean unlocked;

    public Achievement(String id, String emoji, String name, String description) {
        this.id = id;
        this.emoji = emoji;
        this.name = name;
        this.description = description;
        this.unlocked = false;
    }

    public String getId() { return id; }
    public String getEmoji() { return emoji; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isUnlocked() { return unlocked; }
    public void unlock() { this.unlocked = true; }
}

