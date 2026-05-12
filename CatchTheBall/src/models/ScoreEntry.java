package OOP_PROJECT.CatchTheBall.src.models;

public class ScoreEntry implements Comparable<ScoreEntry> {
    private String name;
    private int    score;
    private int    level;
    private String difficulty;
    private String date;

    public ScoreEntry(String name, int score, int level,
                      String difficulty, String date) {
        this.name       = name;
        this.score      = score;
        this.level      = level;
        this.difficulty = difficulty;
        this.date       = date;
    }

    @Override
    public int compareTo(ScoreEntry other) {
        return Integer.compare(other.score, this.score);
    }

    public String getName()       { return name; }
    public int    getScore()      { return score; }
    public int    getLevel()      { return level; }
    public String getDifficulty() { return difficulty; }
    public String getDate()       { return date; }

    @Override
    public String toString() {
        return name + "," + score + "," + level + "," + difficulty + "," + date;
    }
}
