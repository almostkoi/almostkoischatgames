package almostkoi.listener;

import almostkoi.AlmostKoisChatGames;
import almostkoi.game.ChatGame;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {
    private final AlmostKoisChatGames plugin;

    public ChatListener(AlmostKoisChatGames plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onChat(AsyncChatEvent event) {
        ChatGame activeGame = plugin.getGameManager().getActiveGame();
        if (activeGame == null) {
            return;
        }

        Player player = event.getPlayer();
        boolean usePerms = plugin.getConfig().getBoolean("use-game-permissions", false);
        if (usePerms && !player.hasPermission("chatgames.see")) {
            return;
        }

        if (!plugin.getStorage().isToggled(player.getUniqueId())) {
            return;
        }

        String message = PlainTextComponentSerializer.plainText().serialize(event.message()).trim();
        boolean caseSensitive = plugin.getConfig().getBoolean("case-sensitive", false);

        if (activeGame.isCorrect(message, caseSensitive)) {
            event.setCancelled(true);
            plugin.getGameManager().handleChat(player, message);
        }
    }
}
