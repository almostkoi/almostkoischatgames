package almostkoi.hook;

import almostkoi.AlmostKoisChatGames;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderAPIHook extends PlaceholderExpansion {
    private final AlmostKoisChatGames plugin;

    public PlaceholderAPIHook(AlmostKoisChatGames plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "almostkoischatgames";
    }

    @Override
    public String getAuthor() {
        return "almostkoi";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer offlinePlayer, String params) {
        if (params.equalsIgnoreCase("wins")) {
            if (offlinePlayer == null) return "0";
            return String.valueOf(plugin.getStorage().getWins(offlinePlayer.getUniqueId()));
        }

        if (params.equalsIgnoreCase("server_total_wins")) {
            return String.valueOf(plugin.getStorage().getServerTotalWins());
        }

        if (params.equalsIgnoreCase("is_toggled")) {
            if (offlinePlayer == null) return "false";
            return String.valueOf(plugin.getStorage().isToggled(offlinePlayer.getUniqueId()));
        }

        if (params.toLowerCase().startsWith("lb_topname_")) {
            try {
                int rank = Integer.parseInt(params.substring("lb_topname_".length()));
                return plugin.getStorage().getLeaderboardPlayerName(rank);
            } catch (NumberFormatException e) {
                return "Invalid Rank";
            }
        }

        if (params.toLowerCase().startsWith("lb_topwins_")) {
            try {
                int rank = Integer.parseInt(params.substring("lb_topwins_".length()));
                return String.valueOf(plugin.getStorage().getLeaderboardPlayerWins(rank));
            } catch (NumberFormatException e) {
                return "0";
            }
        }

        return null;
    }
}
