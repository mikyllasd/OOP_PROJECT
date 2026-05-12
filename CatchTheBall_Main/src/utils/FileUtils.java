package OOP_PROJECT.CatchTheBall.src.utils;

import java.io.*;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public class FileUtils {

    public static void writeLines(String path, String content) throws IOException {
        Path file   = Paths.get(path);
        Path parent = file.getParent();
        if (parent != null) Files.createDirectories(parent);
        Path backup = Paths.get(path + ".bak");
        if (Files.exists(file)) Files.copy(file, backup, StandardCopyOption.REPLACE_EXISTING);
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            pw.print(content);
        }
    }

    public static Map<String, String> readKeyValue(String path) throws IOException {
        Map<String, String> map = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] kv = line.split("=", 2);
                if (kv.length == 2) map.put(kv[0].trim(), kv[1].trim());
            }
        }
        return map;
    }

    public static boolean fileExists(String path) { return Files.exists(Paths.get(path)); }

    public static void deleteFile(String path) {
        try { Files.deleteIfExists(Paths.get(path)); }
        catch (IOException ignored) {}
    }

    public static String[] listFiles(String dir, String suffix) {
        File folder = new File(dir);
        if (!folder.exists()) return new String[0];
        File[] files = folder.listFiles((d, name) -> name.endsWith(suffix));
        if (files == null) return new String[0];
        String[] names = new String[files.length];
        for (int i = 0; i < files.length; i++) names[i] = files[i].getName();
        return names;
    }

    private FileUtils() {}
}