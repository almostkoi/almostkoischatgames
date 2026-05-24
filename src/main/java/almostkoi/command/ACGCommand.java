package almostkoi.command;

import almostkoi.AlmostKoisChatGames;
import almostkoi.game.GameType;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import java.util.*;
import java.util.stream.Collectors;

public class ACGCommand implements CommandExecutor, TabCompleter {
    private final AlmostKoisChatGames plugin;

    public ACGCommand(AlmostKoisChatGames plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendInfo(sender);
            return true;
        }

        String sub = args[0].toLowerCase();
        if (sub.equals("toggle")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(AlmostKoisChatGames.colorize(plugin.getLang().getString("messages.only-players")));
                return true;
            }
            Player player = (Player) sender;
            if (!player.hasPermission("chatgames.toggle")) {
                player.sendMessage(AlmostKoisChatGames.colorize(plugin.getLang().getString("messages.no-permission").replace("{PREFIX}", plugin.getPrefix())));
                return true;
            }
            boolean current = plugin.getStorage().isToggled(player.getUniqueId());
            plugin.getStorage().setToggled(player.getUniqueId(), player.getName(), !current);
            if (!current) {
                player.sendMessage(AlmostKoisChatGames.colorize(plugin.getLang().getString("messages.toggled-on").replace("{PREFIX}", plugin.getPrefix())));
            } else {
                player.sendMessage(AlmostKoisChatGames.colorize(plugin.getLang().getString("messages.toggled-off").replace("{PREFIX}", plugin.getPrefix())));
            }
            return true;
        }

        if (sub.equals("forcestart")) {
            if (!sender.hasPermission("chatgames.admin")) {
                sender.sendMessage(AlmostKoisChatGames.colorize(plugin.getLang().getString("messages.no-permission").replace("{PREFIX}", plugin.getPrefix())));
                return true;
            }
            if (args.length < 2) {
                plugin.getGameManager().startRandomGame();
                return true;
            }
            try {
                GameType type = GameType.valueOf(args[1].toUpperCase());
                boolean started = plugin.getGameManager().startGame(type);
                if (!started) {
                    sender.sendMessage(AlmostKoisChatGames.colorize(plugin.getLang().getString("messages.invalid-game-type").replace("{PREFIX}", plugin.getPrefix()).replace("{TYPES}", getGameTypesString())));
                }
            } catch (IllegalArgumentException e) {
                sender.sendMessage(AlmostKoisChatGames.colorize(plugin.getLang().getString("messages.invalid-game-type").replace("{PREFIX}", plugin.getPrefix()).replace("{TYPES}", getGameTypesString())));
            }
            return true;
        }

        if (sub.equals("reload")) {
            if (!sender.hasPermission("chatgames.admin")) {
                sender.sendMessage(AlmostKoisChatGames.colorize(plugin.getLang().getString("messages.no-permission").replace("{PREFIX}", plugin.getPrefix())));
                return true;
            }
            plugin.reloadConfig();
            plugin.loadLang();
            plugin.getGameManager().reload();
            sender.sendMessage(AlmostKoisChatGames.colorize(plugin.getLang().getString("messages.reload-success").replace("{PREFIX}", plugin.getPrefix())));
            return true;
        }

        if (sub.equals("info")) {
            if (!sender.hasPermission("chatgames.admin")) {
                sender.sendMessage(AlmostKoisChatGames.colorize(plugin.getLang().getString("messages.no-permission").replace("{PREFIX}", plugin.getPrefix())));
                return true;
            }
            sendInfo(sender);
            return true;
        }

        sendInfo(sender);
        return true;
    }

    private void sendInfo(CommandSender sender) {
        List<String> info = plugin.getLang().getStringList("messages.info");
        if (info == null) return;
        for (String line : info) {
            sender.sendMessage(AlmostKoisChatGames.colorize(line
                    .replace("{PREFIX}", plugin.getPrefix())
                    .replace("{WORDS}", String.valueOf(plugin.getGameManager().getWordLibrarySize()))
                    .replace("{TRIVIA}", String.valueOf(plugin.getGameManager().getTriviaLibrarySize()))
            ));
        }
    }

    private String getGameTypesString() {
        return Arrays.stream(GameType.values()).map(Enum::name).collect(Collectors.joining(", "));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> subcommands = new ArrayList<>();
            if (sender.hasPermission("chatgames.toggle") && sender instanceof Player) {
                subcommands.add("toggle");
            }
            if (sender.hasPermission("chatgames.admin")) {
                subcommands.add("forcestart");
                subcommands.add("reload");
                subcommands.add("info");
            }
            return subcommands.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("forcestart")) {
            if (sender.hasPermission("chatgames.admin")) {
                return Arrays.stream(GameType.values())
                        .map(Enum::name)
                        .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }
}
