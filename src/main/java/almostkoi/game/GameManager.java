package almostkoi.game;

import almostkoi.AlmostKoisChatGames;
import almostkoi.api.event.ChatGameEndEvent;
import almostkoi.api.event.ChatGameStartEvent;
import almostkoi.game.impl.*;
import almostkoi.scheduler.TaskWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.util.*;

public class GameManager {
    private final AlmostKoisChatGames plugin;
    private final Random random = new Random();
    private ChatGame activeGame = null;
    private TaskWrapper.Task activeLifetimeTask = null;
    private TaskWrapper.Task activeIntervalTask = null;
    private long gameStartTime = 0;

    private List<String> wordLibrary = new ArrayList<>();
    private List<TriviaEntry> triviaLibrary = new ArrayList<>();
    private List<GameType> enabledGames = new ArrayList<>();
    private List<String> rewards = new ArrayList<>();

    private static class TriviaEntry {
        String question;
        List<String> answers;

        TriviaEntry(String question, List<String> answers) {
            this.question = question;
            this.answers = answers;
        }
    }

    public GameManager(AlmostKoisChatGames plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        stopActiveGame(null);
        if (activeIntervalTask != null) {
            activeIntervalTask.cancel();
            activeIntervalTask = null;
        }

        loadLibraries();
        loadEnabledGames();
        loadRewards();

        long interval = plugin.getConfig().getLong("game-interval", 300);
        if (interval > 0) {
            activeIntervalTask = TaskWrapper.runAsyncTimer(this::tickGameTimer, interval, interval);
        }
    }

    private void tickGameTimer() {
        if (activeGame != null) {
            return;
        }
        TaskWrapper.runSync(this::startRandomGame);
    }

    private void loadLibraries() {
        wordLibrary.clear();
        File wordFile = new File(plugin.getDataFolder(), "game-library.yml");
        if (wordFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(wordFile);
            wordLibrary = config.getStringList("words");
        }
        if (wordLibrary.isEmpty()) {
            wordLibrary.add("Minecraft");
        }

        triviaLibrary.clear();
        File triviaFile = new File(plugin.getDataFolder(), "trivia-library.yml");
        if (triviaFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(triviaFile);
            if (config.contains("trivia")) {
                for (Map<String, Object> map : (List<Map<String, Object>>) config.getList("trivia")) {
                    String q = (String) map.get("question");
                    List<String> ans = (List<String>) map.get("answers");
                    if (q != null && ans != null && !ans.isEmpty()) {
                        triviaLibrary.add(new TriviaEntry(q, ans));
                    }
                }
            }
        }
        if (triviaLibrary.isEmpty()) {
            triviaLibrary.add(new TriviaEntry("What is Minecraft?", Arrays.asList("game", "block game")));
        }
    }

    private void loadEnabledGames() {
        enabledGames.clear();
        List<String> list = plugin.getConfig().getStringList("enabled-games");
        for (String s : list) {
            try {
                enabledGames.add(GameType.valueOf(s.toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }
        if (enabledGames.isEmpty()) {
            enabledGames.addAll(Arrays.asList(GameType.values()));
        }
    }

    private void loadRewards() {
        rewards.clear();
        File rewardsFile = new File(plugin.getDataFolder(), "rewards.yml");
        if (rewardsFile.exists()) {
            YamlConfiguration config = YamlConfiguration.loadConfiguration(rewardsFile);
            rewards = config.getStringList("rewards");
        }
    }

    public synchronized void startRandomGame() {
        if (enabledGames.isEmpty()) return;
        GameType type = enabledGames.get(random.nextInt(enabledGames.size()));
        startGame(type);
    }

    public synchronized boolean startGame(GameType type) {
        if (activeGame != null) {
            stopActiveGame(null);
        }

        String word = wordLibrary.get(random.nextInt(wordLibrary.size()));
        ChatGame game;

        switch (type) {
            case UNSCRAMBLE:
                game = new UnscrambleGame(word);
                break;
            case SOLVE:
                game = new SolveGame();
                break;
            case TYPE:
                game = new TypeGame(word);
                break;
            case TYPE_RANDOM:
                game = new TypeRandomGame();
                break;
            case TRIVIA:
                if (triviaLibrary.isEmpty()) {
                    return false;
                }
                TriviaEntry entry = triviaLibrary.get(random.nextInt(triviaLibrary.size()));
                game = new TriviaGame(entry.question, entry.answers);
                break;
            case FILL_IN:
                game = new FillInGame(word);
                break;
            case WORD_UNSHUFFLE:
                game = new WordUnshuffleGame(word);
                break;
            case REVERSE_WORD_UNSHUFFLE:
                game = new ReverseWordUnshuffleGame(word);
                break;
            case UNREVERSE:
                game = new UnreverseGame(word);
                break;
            default:
                return false;
        }

        final ChatGameStartEvent startEvent = new ChatGameStartEvent(type, game.getAnswers(), game.getQuestion());
        TaskWrapper.runAsync(() -> Bukkit.getPluginManager().callEvent(startEvent));

        activeGame = game;
        gameStartTime = System.currentTimeMillis();

        broadcastGameStart(game);
        playSound("sounds.start");

        long lifetime = plugin.getConfig().getLong("game-lifetime", 30);
        activeLifetimeTask = TaskWrapper.runSyncLater(() -> stopActiveGame(null), lifetime * 20);
        return true;
    }

    public synchronized void stopActiveGame(Player winner) {
        if (activeGame == null) return;

        if (activeLifetimeTask != null) {
            activeLifetimeTask.cancel();
            activeLifetimeTask = null;
        }

        final ChatGame finishedGame = activeGame;
        activeGame = null;

        final ChatGameEndEvent endEvent = new ChatGameEndEvent(
                finishedGame.getType(),
                finishedGame.getAnswers().get(0),
                winner,
                winner != null
        );
        TaskWrapper.runAsync(() -> Bukkit.getPluginManager().callEvent(endEvent));

        if (winner == null) {
            broadcastGameExpire(finishedGame);
            playSound("sounds.expire");
        } else {
            playSound("sounds.win");
        }
    }

    public synchronized boolean handleChat(Player player, String message) {
        if (activeGame == null) return false;

        boolean caseSensitive = plugin.getConfig().getBoolean("case-sensitive", false);
        if (activeGame.isCorrect(message, caseSensitive)) {
            final Player winner = player;
            final ChatGame solvedGame = activeGame;
            final long timeTaken = System.currentTimeMillis() - gameStartTime;

            stopActiveGame(winner);

            plugin.getStorage().addWin(winner.getUniqueId(), winner.getName());

            TaskWrapper.runSync(() -> giveRewards(winner, solvedGame, timeTaken));
            return true;
        }
        return false;
    }

    private void giveRewards(Player player, ChatGame game, long timeMs) {
        double seconds = timeMs / 1000.0;
        String secondsStr = String.format("%.2f", seconds);
        String gameName = game.getType().getDisplayName();
        String answer = game.getAnswers().get(0);

        for (String reward : rewards) {
            String processed = reward
                    .replace("%player%", player.getName())
                    .replace("%game%", gameName)
                    .replace("%answer%", answer)
                    .replace("%time%", secondsStr);

            if (processed.startsWith("{player-msg}:")) {
                String msg = processed.substring("{player-msg}:".length()).trim();
                player.sendMessage(AlmostKoisChatGames.colorize(msg));
            } else if (processed.startsWith("{console-cmd}:")) {
                String cmd = processed.substring("{console-cmd}:".length()).trim();
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
            } else if (processed.startsWith("{player-cmd}:")) {
                String cmd = processed.substring("{player-cmd}:".length()).trim();
                TaskWrapper.executePlayerCommand(player, cmd);
            } else if (processed.startsWith("{broadcast-all}:")) {
                String msg = processed.substring("{broadcast-all}:".length()).trim();
                Bukkit.broadcastMessage(AlmostKoisChatGames.colorize(msg));
            } else if (processed.startsWith("{broadcast-safe}:")) {
                String msg = processed.substring("{broadcast-safe}:".length()).trim();
                String formatted = AlmostKoisChatGames.colorize(msg);
                boolean usePerms = plugin.getConfig().getBoolean("use-game-permissions", false);
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (usePerms && !p.hasPermission("chatgames.see")) {
                        continue;
                    }
                    if (plugin.getStorage().isToggled(p.getUniqueId())) {
                        p.sendMessage(formatted);
                    }
                }
            } else if (processed.startsWith("{vault-money}:")) {
                if (plugin.getVaultHook().isEnabled()) {
                    String amountStr = processed.substring("{vault-money}:".length()).trim();
                    try {
                        double amount = Double.parseDouble(amountStr);
                        plugin.getVaultHook().deposit(player, amount);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }
    }

    private void broadcastGameStart(ChatGame game) {
        String key = game.getType().name().toLowerCase();
        List<String> lines = plugin.getLang().getStringList("games." + key + ".start");
        if (lines == null || lines.isEmpty()) return;

        boolean usePerms = plugin.getConfig().getBoolean("use-game-permissions", false);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (usePerms && !player.hasPermission("chatgames.see")) {
                continue;
            }
            if (plugin.getStorage().isToggled(player.getUniqueId())) {
                for (String line : lines) {
                    player.sendMessage(AlmostKoisChatGames.colorize(line
                            .replace("{PREFIX}", plugin.getPrefix())
                            .replace("{QUESTION}", game.getQuestion())));
                }
            }
        }
    }

    private void broadcastGameExpire(ChatGame game) {
        List<String> lines = plugin.getLang().getStringList("messages.game-expired");
        if (lines == null || lines.isEmpty()) return;

        boolean usePerms = plugin.getConfig().getBoolean("use-game-permissions", false);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (usePerms && !player.hasPermission("chatgames.see")) {
                continue;
            }
            if (plugin.getStorage().isToggled(player.getUniqueId())) {
                for (String line : lines) {
                    player.sendMessage(AlmostKoisChatGames.colorize(line
                            .replace("{PREFIX}", plugin.getPrefix())
                            .replace("{ANSWER}", game.getAnswers().get(0))));
                }
            }
        }
    }

    private void playSound(String path) {
        String soundName = plugin.getConfig().getString(path);
        if (soundName == null || soundName.isEmpty()) return;
        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            boolean usePerms = plugin.getConfig().getBoolean("use-game-permissions", false);
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (usePerms && !player.hasPermission("chatgames.see")) {
                    continue;
                }
                if (plugin.getStorage().isToggled(player.getUniqueId())) {
                    player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                }
            }
        } catch (IllegalArgumentException ignored) {}
    }

    public ChatGame getActiveGame() {
        return activeGame;
    }

    public int getWordLibrarySize() {
        return wordLibrary.size();
    }

    public int getTriviaLibrarySize() {
        return triviaLibrary.size();
    }
}
