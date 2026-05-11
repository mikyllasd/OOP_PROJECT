package managers;
import interfaces.Saveable;
import models.ScoreEntry;
import utils.FileUtils;
import java.time.LocalDate;
import java.util.*;
public class ScoreManager implements Saveable {
    private static final String FILE  = "scores.txt";
    private static final int    LIMIT = 10;
    private List<ScoreEntry> entries;

    public ScoreManager() {
        entries = new ArrayList<>();
        load();
    }
    public void addScore(String name, int score, int level, String difficulty) {
        String date = LocalDate.now().toString();
        entries.add(new ScoreEntry(name, score, level, difficulty, date));
        Collections.sort(entries);
        if (entries.size() > LIMIT)
            entries = new ArrayList<>(entries.subList(0, LIMIT));
        save();
    }
    public List<ScoreEntry> getTop10() { return entries; }
    public int getPersonalBest(String name) {
        return entries.stream()
                .filter(e -> e.getName().equals(name))
                .mapToInt(ScoreEntry::getScore)
                .max().orElse(0);
    }
    @Override
    public void save() {
        StringBuilder sb = new StringBuilder();
        for (ScoreEntry e : entries)
            sb.append(e.toString()).append("\n");
        try { FileUtils.writeLines(FILE, sb.toString()); }
        catch (Exception e) { e.printStackTrace(); }
    }
    @Override
    public void load() {
        if (!FileUtils.fileExists(FILE)) return;
        try {
            java.io.BufferedReader br =
                    new java.io.BufferedReader(new java.io.FileReader(FILE));
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                if (p.length >= 5)
                    entries.add(new ScoreEntry(p[0],
                            Integer.parseInt(p[1].trim()),
                            Integer.parseInt(p[2].trim()),
                            p[3].trim(), p[4].trim()));
                else if (p.length >= 3)
                    entries.add(new ScoreEntry(p[0],
                            Integer.parseInt(p[1].trim()),
                            Integer.parseInt(p[2].trim()),
                            "Normal", ""));
            }
            br.close();
            Collections.sort(entries);
        } catch (Exception ignored) {}
    }
}