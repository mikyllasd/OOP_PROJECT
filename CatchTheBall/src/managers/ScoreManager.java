package OOP_PROJECT.CatchTheBall.src.managers;

import OOP_PROJECT.CatchTheBall.src.interfaces.Saveable;
import OOP_PROJECT.CatchTheBall.src.models.ScoreEntry;
import OOP_PROJECT.CatchTheBall.src.utils.FileUtils;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class ScoreManager implements Saveable {
    private static final String SHARED_SCORES = "leaderboard.txt";
    private String           scoreFile = SHARED_SCORES;
    private static final int LIMIT     = 50;
    private List<ScoreEntry> entries;

    public ScoreManager() {
        entries = new ArrayList<>();
        System.out.println("Leaderboard path: " 
            + new java.io.File(SHARED_SCORES).getAbsolutePath());
        load();
    }

    public void setScoreFile(String path) {
        scoreFile = SHARED_SCORES;
        entries.clear();
        load();
    }

    public void addScore(String name, int score, int level, String difficulty) {
        load();
        entries.removeIf(e -> e.getName().equalsIgnoreCase(name)
                && e.getDifficulty().equalsIgnoreCase(difficulty));
        entries.add(new ScoreEntry(name, score, level, difficulty,
                LocalDate.now().toString()));
        Collections.sort(entries);
        if (entries.size() > LIMIT)
            entries = new ArrayList<>(entries.subList(0, LIMIT));
        save();
        System.out.println("Score saved: " + name + " - " + score);
    }

    public List<ScoreEntry> getTopByDifficulty(String difficulty) {
        if (difficulty == null || difficulty.isEmpty()) return getAll();
        List<ScoreEntry> filtered = new ArrayList<>();
        for (ScoreEntry e : entries) {
            if (difficulty.equalsIgnoreCase(e.getDifficulty())) filtered.add(e);
        }
        return filtered;
    }

    public List<ScoreEntry> getTop10() {
        return entries.size() > 10
                ? new ArrayList<>(entries.subList(0, 10))
                : Collections.unmodifiableList(entries);
    }

    public List<ScoreEntry> getAll() {
        return Collections.unmodifiableList(entries);
    }

    @Override
    public void save() {
        StringBuilder sb = new StringBuilder();
        for (ScoreEntry e : entries)
            sb.append(e.toString()).append("\n");
        try {
            // Create file if it doesn't exist
            java.io.File f = new java.io.File(SHARED_SCORES);
            if (!f.exists()) f.createNewFile();
            FileUtils.writeLines(SHARED_SCORES, sb.toString());
            System.out.println("Leaderboard saved successfully with " 
                + entries.size() + " entries.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void load() {
        entries.clear();
        java.io.File f = new java.io.File(SHARED_SCORES);
        if (!f.exists()) {
            System.out.println("Leaderboard file not found at: " 
                + f.getAbsolutePath());
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] p = line.split(",");
                try {
                    if (p.length >= 5)
                        entries.add(new ScoreEntry(
                                p[0].trim(),
                                Integer.parseInt(p[1].trim()),
                                Integer.parseInt(p[2].trim()),
                                p[3].trim(),
                                p[4].trim()));
                    else if (p.length >= 3)
                        entries.add(new ScoreEntry(
                                p[0].trim(),
                                Integer.parseInt(p[1].trim()),
                                Integer.parseInt(p[2].trim()),
                                "Normal", ""));
                } catch (Exception ignored) {}
            }
            Collections.sort(entries);
            System.out.println("Leaderboard loaded with " 
                + entries.size() + " entries.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}