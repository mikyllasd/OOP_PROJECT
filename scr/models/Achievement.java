package models;
public class Achievement {
    private String id;
    private String emoji;
    private String name;
    private String description;
    private boolean unlocked;
    private int progressCurrent;
    private int progressTarget;

    public Achievement(String id, String emoji, String name,
                       String description, int progressTarget) {
        this.id             = id;
        this.emoji          = emoji;
        this.name           = name;
        this.description    = description;
        this.progressTarget = progressTarget;
        this.progressCurrent = 0;
        this.unlocked       = false;
    }
    public void unlock() { this.unlocked = true; }
    public void setProgress(int progress) {
        this.progressCurrent = Math.min(progress, progressTarget);
        if (this.progressCurrent >= progressTarget) this.unlocked = true;
    }
    public String getId()           { return id; }
    public String getEmoji()        { return emoji; }
    public String getName()         { return name; }
    public String getDescription()  { return description; }
    public boolean isUnlocked()     { return unlocked; }
    public int getProgressCurrent() { return progressCurrent; }
    public int getProgressTarget()  { return progressTarget; }
    public float getProgressRatio() {
        if (progressTarget <= 0) return unlocked ? 1f : 0f;
        return Math.min(1f, (float) progressCurrent / progressTarget);
    }
}