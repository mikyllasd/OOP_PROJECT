package game.data;

import java.util.*;
import java.io.*;

public class ScoreEntry implements Comparable<ScoreEntry> {
    private String name;
    private int score;
    private int level;

    public ScoreEntry(String name, int score, int level) {
        this.name = name;
        this.score = score;
        this.level = level;
    }

    @Override
    public int compareTo(ScoreEntry other) {
        return Integer.compare(other.score, this.score); // descending
    }

    public String getName() { return name; }
    public int getScore() { return score; }
    public int getLevel() { return level; }

    @Override
    public String toString() { return name + "," + score + "," + level; }
}

