package OOP_PROJECT.CatchTheBall.src.accounts;

import OOP_PROJECT.CatchTheBall.src.models.AccountData;
import OOP_PROJECT.CatchTheBall.src.utils.FileUtils;

import java.io.*;
import java.util.*;

public class AccountManager {
    private static final String ACCOUNTS_DIR = "accounts/";
    private static final String ACTIVE_FILE  = "accounts/active.txt";

    private String       activeAccountName;
    private List<String> accountNames;

    public AccountManager() {
        accountNames = new ArrayList<>();
        new File(ACCOUNTS_DIR).mkdirs();
        loadAccountList();
        loadActiveAccount();
    }

    private void loadAccountList() {
        accountNames.clear();
        String[] files = FileUtils.listFiles(ACCOUNTS_DIR, ".txt");
        for (String f : files) {
            if (!f.equals("active.txt") && !f.endsWith("_scores.txt")) {
                accountNames.add(f.replace(".txt", ""));
            }
        }
    }

    private void loadActiveAccount() {
        if (FileUtils.fileExists(ACTIVE_FILE)) {
            try (BufferedReader br = new BufferedReader(new FileReader(ACTIVE_FILE))) {
                String line = br.readLine();
                if (line != null) activeAccountName = line.trim();
            } catch (Exception ignored) {}
        }
    }

    public void setActiveAccount(String name) {
        activeAccountName = name;
        try { FileUtils.writeLines(ACTIVE_FILE, name + "\n"); }
        catch (Exception ignored) {}
    }

    public String  getActiveAccountName()       { return activeAccountName; }
    public String  getAccountFilePath(String n) { return ACCOUNTS_DIR + n + ".txt"; }
    public boolean accountExists(String n)      { return FileUtils.fileExists(getAccountFilePath(n)); }

    public void createAccount(String name) {
        if (!accountExists(name)) {
            try { FileUtils.writeLines(getAccountFilePath(name), "coins=0\n"); }
            catch (Exception ignored) {}
        }
        if (!accountNames.contains(name)) accountNames.add(name);
        setActiveAccount(name);
    }

    public void deleteAccount(String name) {
        FileUtils.deleteFile(getAccountFilePath(name));
        FileUtils.deleteFile(getAccountFilePath(name) + ".bak");
        FileUtils.deleteFile("accounts/" + name + "_scores.txt");
        accountNames.remove(name);
        if (name.equals(activeAccountName)) activeAccountName = null;
    }

    public void renameAccount(String oldName, String newName) {
        if (!accountExists(oldName) || accountExists(newName)) return;
        try {
            Map<String, String> kv = FileUtils.readKeyValue(getAccountFilePath(oldName));
            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, String> e : kv.entrySet())
                sb.append(e.getKey()).append("=").append(e.getValue()).append("\n");
            FileUtils.writeLines(getAccountFilePath(newName), sb.toString());
        } catch (Exception ignored) {}
        FileUtils.deleteFile(getAccountFilePath(oldName));
        accountNames.remove(oldName);
        accountNames.add(newName);
        if (oldName.equals(activeAccountName)) setActiveAccount(newName);
    }

    public List<String> getAccountNames() {
        loadAccountList();
        return Collections.unmodifiableList(accountNames);
    }

    public AccountData getAccountSummary(String name) {
        int bestScore = 0, totalCoins = 0, farmStage = 1, gamesPlayed = 0;
        String path = getAccountFilePath(name);
        if (FileUtils.fileExists(path)) {
            try {
                Map<String, String> kv = FileUtils.readKeyValue(path);
                bestScore   = parseInt(kv, "bestScore",   0);
                totalCoins  = parseInt(kv, "coins",       0);
                farmStage   = parseInt(kv, "farmStage",   1);
                gamesPlayed = parseInt(kv, "gamesPlayed", 0);
            } catch (Exception ignored) {}
        }
        return new AccountData(name, bestScore, totalCoins, farmStage, gamesPlayed);
    }

    private int parseInt(Map<String, String> kv, String key, int def) {
        try { return kv.containsKey(key) ? Integer.parseInt(kv.get(key)) : def; }
        catch (Exception e) { return def; }
    }
}