import java.io.*;
import java.util.*;

/**
 * Manages the persistent leaderboard (top 10 scores).
 * Saves to / loads from a plain-text file "scores.txt".
 *
 * Demonstrates: Encapsulation, File I/O, use of Collections.
 */
public class ScoreManager {

    private static final String FILE_NAME  = "scores.txt";
    private static final int    MAX_SCORES = 10;

    private final List<ScoreEntry> scores;

    public ScoreManager() {
        scores = new ArrayList<>();
        load();
    }

    // ── Public API ────────────────────────────────────────────────────

    /**
     * Adds a new score, re-sorts, trims to top 10, and saves.
     *
     * @return rank (1-based) of the new entry
     */
    public int addScore(String name, int score, int level, int coins) {
        ScoreEntry entry = new ScoreEntry(name, score, level, coins);
        scores.add(entry);
        Collections.sort(scores);
        if (scores.size() > MAX_SCORES) {
            scores.subList(MAX_SCORES, scores.size()).clear();
        }
        save();
        return scores.indexOf(entry) + 1;
    }

    public List<ScoreEntry> getScores() {
        return Collections.unmodifiableList(scores);
    }

    /** True if this score beats the current top entry. */
    public boolean isNewRecord(int score) {
        return scores.isEmpty() || score > scores.get(0).getScore();
    }

    // ── File I/O ──────────────────────────────────────────────────────

    private void load() {
        File f = new File(FILE_NAME);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                ScoreEntry e = ScoreEntry.deserialize(line);
                if (e != null) scores.add(e);
            }
            Collections.sort(scores);
        } catch (IOException ignored) { }
    }

    private void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (ScoreEntry e : scores) pw.println(e.serialize());
        } catch (IOException ignored) { }
    }
}
