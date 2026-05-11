import java.util.*;
import java.io.*;

public class ScoreManager {
    private static final String FILE = "scores.txt";
    private List<ScoreEntry> entries;

    public ScoreManager() {
        entries = new ArrayList<>();
        load();
    }

    public void addScore(String name, int score, int level) {
        entries.add(new ScoreEntry(name, score, level));
        Collections.sort(entries);
        if (entries.size() > 10) entries = entries.subList(0, 10);
        save();
    }

    public List<ScoreEntry> getTop10() { return entries; }

    private void load() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    entries.add(new ScoreEntry(parts[0], Integer.parseInt(parts[1].trim()), Integer.parseInt(parts[2].trim())));
                }
            }
            Collections.sort(entries);
        } catch (Exception e) { /* file may not exist yet */ }
    }

    private void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE))) {
            for (ScoreEntry e : entries) pw.println(e);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
