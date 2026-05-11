/**
 * Immutable data class representing one leaderboard entry.
 * Implements Comparable for easy sorting by score (descending).
 *
 * Demonstrates: Encapsulation, Comparable interface.
 */
public class ScoreEntry implements Comparable<ScoreEntry> {

    private final String name;
    private final int    score;
    private final int    level;
    private final int    coins;

    public ScoreEntry(String name, int score, int level, int coins) {
        this.name  = name;
        this.score = score;
        this.level = level;
        this.coins = coins;
    }

    /** Sort descending by score. */
    @Override
    public int compareTo(ScoreEntry other) {
        return Integer.compare(other.score, this.score);
    }

    // ── Serialisation ─────────────────────────────────────────────────

    /** Convert to a comma-separated line for file storage. */
    public String serialize() {
        return name + "," + score + "," + level + "," + coins;
    }

    /** Parse a line written by serialize(). Returns null on failure. */
    public static ScoreEntry deserialize(String line) {
        if (line == null || line.isBlank()) return null;
        String[] p = line.split(",");
        if (p.length < 4) return null;
        try {
            return new ScoreEntry(p[0].trim(),
                    Integer.parseInt(p[1].trim()),
                    Integer.parseInt(p[2].trim()),
                    Integer.parseInt(p[3].trim()));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // ── Getters ──────────────────────────────────────────────────────

    public String getName()  { return name;  }
    public int    getScore() { return score; }
    public int    getLevel() { return level; }
    public int    getCoins() { return coins; }
}
