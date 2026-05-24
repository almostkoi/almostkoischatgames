package almostkoi.data;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class PlayerStorage {
    public static class PlayerData {
        private final String uuid;
        private String name;
        private int wins;
        private boolean toggled;

        public PlayerData(String uuid, String name, int wins, boolean toggled) {
            this.uuid = uuid;
            this.name = name;
            this.wins = wins;
            this.toggled = toggled;
        }

        public String getUuid() {
            return uuid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getWins() {
            return wins;
        }

        public void addWin() {
            this.wins++;
        }

        public boolean isToggled() {
            return toggled;
        }

        public void setToggled(boolean toggled) {
            this.toggled = toggled;
        }
    }

    private final Plugin plugin;
    private final File file;
    private YamlConfiguration config;
    private final Map<UUID, PlayerData> players = new HashMap<>();
    private List<PlayerData> cachedLeaderboard = new ArrayList<>();
    private boolean cacheDirty = true;

    public PlayerStorage(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "playerstorage.yml");
        load();
    }

    public synchronized void load() {
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        players.clear();
        if (config.contains("players")) {
            for (String key : config.getConfigurationSection("players").getKeys(false)) {
                try {
                    UUID uuid = UUID.fromString(key);
                    String name = config.getString("players." + key + ".name", "Unknown");
                    int wins = config.getInt("players." + key + ".wins", 0);
                    boolean toggled = config.getBoolean("players." + key + ".toggled", true);
                    players.put(uuid, new PlayerData(key, name, wins, toggled));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
        }
        cacheDirty = true;
    }

    public synchronized void save() {
        config.set("players", null);
        for (Map.Entry<UUID, PlayerData> entry : players.entrySet()) {
            String key = entry.getKey().toString();
            PlayerData data = entry.getValue();
            config.set("players." + key + ".name", data.getName());
            config.set("players." + key + ".wins", data.getWins());
            config.set("players." + key + ".toggled", data.isToggled());
        }
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized PlayerData getOrCreatePlayer(UUID uuid, String name) {
        PlayerData data = players.get(uuid);
        if (data == null) {
            data = new PlayerData(uuid.toString(), name, 0, true);
            players.put(uuid, data);
            cacheDirty = true;
        } else if (name != null && !name.equalsIgnoreCase(data.getName())) {
            data.setName(name);
        }
        return data;
    }

    public synchronized void addWin(UUID uuid, String name) {
        PlayerData data = getOrCreatePlayer(uuid, name);
        data.addWin();
        cacheDirty = true;
        save();
    }

    public synchronized boolean isToggled(UUID uuid) {
        PlayerData data = players.get(uuid);
        return data == null || data.isToggled();
    }

    public synchronized void setToggled(UUID uuid, String name, boolean toggled) {
        PlayerData data = getOrCreatePlayer(uuid, name);
        data.setToggled(toggled);
        save();
    }

    public synchronized int getWins(UUID uuid) {
        PlayerData data = players.get(uuid);
        return data == null ? 0 : data.getWins();
    }

    public synchronized int getServerTotalWins() {
        return players.values().stream().mapToInt(PlayerData::getWins).sum();
    }

    private synchronized void updateLeaderboard() {
        if (!cacheDirty) return;
        cachedLeaderboard = players.values().stream()
                .filter(p -> p.getWins() > 0)
                .sorted((p1, p2) -> Integer.compare(p2.getWins(), p1.getWins()))
                .collect(Collectors.toList());
        cacheDirty = false;
    }

    public synchronized String getLeaderboardPlayerName(int rank) {
        updateLeaderboard();
        int index = rank - 1;
        if (index >= 0 && index < cachedLeaderboard.size()) {
            return cachedLeaderboard.get(index).getName();
        }
        return "None";
    }

    public synchronized int getLeaderboardPlayerWins(int rank) {
        updateLeaderboard();
        int index = rank - 1;
        if (index >= 0 && index < cachedLeaderboard.size()) {
            return cachedLeaderboard.get(index).getWins();
        }
        return 0;
    }
}
