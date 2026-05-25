package almostkoi;

import almostkoi.command.ACGCommand;
import almostkoi.data.PlayerStorage;
import almostkoi.game.GameManager;
import almostkoi.hook.PlaceholderAPIHook;
import almostkoi.hook.VaultHook;
import almostkoi.listener.ChatListener;
import almostkoi.scheduler.TaskWrapper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AlmostKoisChatGames extends JavaPlugin {
    private PlayerStorage storage;
    private GameManager gameManager;
    private YamlConfiguration lang;
    private String prefix;
    private VaultHook vaultHook;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        saveResourceIfNotExists("rewards.yml");
        saveResourceIfNotExists("lang.yml");
        saveResourceIfNotExists("game-library.yml");
        saveResourceIfNotExists("trivia-library.yml");

        TaskWrapper.init(this);
        storage = new PlayerStorage(this);
        loadLang();

        vaultHook = new VaultHook(this);

        gameManager = new GameManager(this);

        ACGCommand cmd = new ACGCommand(this);
        getCommand("acg").setExecutor(cmd);
        getCommand("acg").setTabCompleter(cmd);

        getServer().getPluginManager().registerEvents(new ChatListener(this), this);

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PlaceholderAPIHook(this).register();
        }
    }

    @Override
    public void onDisable() {
        if (storage != null) {
            storage.save();
        }
    }

    private void saveResourceIfNotExists(String resourceName) {
        File file = new File(getDataFolder(), resourceName);
        if (!file.exists()) {
            saveResource(resourceName, false);
        }
    }

    public void loadLang() {
        File file = new File(getDataFolder(), "lang.yml");
        if (!file.exists()) {
            saveResource("lang.yml", false);
        }
        lang = YamlConfiguration.loadConfiguration(file);
        prefix = lang.getString("prefix", "&6&lChatGames &7» ");
    }

    public PlayerStorage getStorage() {
        return storage;
    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public VaultHook getVaultHook() {
        return vaultHook;
    }

    public YamlConfiguration getLang() {
        return lang;
    }

    public String getPrefix() {
        return prefix;
    }

    @SuppressWarnings("deprecation")
    public static String colorize(String message) {
        if (message == null) return null;
        Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
        Matcher matcher = pattern.matcher(message);
        StringBuilder buffer = new StringBuilder();
        while (matcher.find()) {
            String color = matcher.group(1);
            StringBuilder replacement = new StringBuilder("§x");
            for (char c : color.toCharArray()) {
                replacement.append('§').append(c);
            }
            matcher.appendReplacement(buffer, Matcher.quoteReplacement(replacement.toString()));
        }
        matcher.appendTail(buffer);
        return ChatColor.translateAlternateColorCodes('&', buffer.toString());
    }
}
