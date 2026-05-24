package almostkoi.hook;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class VaultHook {
    private final JavaPlugin plugin;
    private Economy economy;
    private boolean searched = false;

    public VaultHook(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private Economy getEconomy() {
        if (searched) {
            return economy;
        }
        searched = true;
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            plugin.getLogger().warning("[Vault] Vault plugin not found - economy rewards disabled.");
            return null;
        }
        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            plugin.getLogger().warning("[Vault] No economy provider registered - is EssentialsX or another economy plugin installed?");
            return null;
        }
        economy = rsp.getProvider();
        if (economy != null) {
            plugin.getLogger().info("[Vault] Economy hooked: " + economy.getName());
        }
        return economy;
    }

    public boolean isEnabled() {
        return getEconomy() != null;
    }

    public void deposit(Player player, double amount) {
        Economy eco = getEconomy();
        if (eco == null) {
            return;
        }
        final OfflinePlayer offlinePlayer = player;
        final Economy finalEco = eco;
        player.getScheduler().run(plugin, task -> {
            try {
                EconomyResponse response = finalEco.depositPlayer(offlinePlayer, amount);
                if (!response.transactionSuccess()) {
                    plugin.getLogger().warning("[Vault] Deposit failed for " + player.getName() + ": " + response.errorMessage);
                }
            } catch (Exception e) {
                plugin.getLogger().warning("[Vault] Exception during deposit for " + player.getName() + ": " + e.getMessage());
            }
        }, null);
    }
}
