package almostkoi.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import java.util.concurrent.TimeUnit;

public class FoliaSchedulerImpl implements TaskWrapper.IScheduler {
    private final Plugin plugin;

    public FoliaSchedulerImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public TaskWrapper.Task runAsync(Runnable runnable) {
        var task = Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> runnable.run());
        return task::cancel;
    }

    @Override
    public TaskWrapper.Task runAsyncLater(Runnable runnable, long delayTicks) {
        var task = Bukkit.getAsyncScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), delayTicks * 50, TimeUnit.MILLISECONDS);
        return task::cancel;
    }

    @Override
    public TaskWrapper.Task runAsyncTimer(Runnable runnable, long delayTicks, long periodTicks) {
        var task = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, scheduledTask -> runnable.run(), delayTicks * 50, periodTicks * 50, TimeUnit.MILLISECONDS);
        return task::cancel;
    }

    @Override
    public TaskWrapper.Task runSync(Runnable runnable) {
        var task = Bukkit.getGlobalRegionScheduler().run(plugin, scheduledTask -> runnable.run());
        return task::cancel;
    }

    @Override
    public TaskWrapper.Task runSyncLater(Runnable runnable, long delayTicks) {
        var task = Bukkit.getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), delayTicks);
        return task::cancel;
    }

    @Override
    public TaskWrapper.Task runSyncTimer(Runnable runnable, long delayTicks, long periodTicks) {
        var task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> runnable.run(), delayTicks, periodTicks);
        return task::cancel;
    }

    @Override
    public void executePlayerCommand(Player player, String command) {
        player.getScheduler().run(plugin, scheduledTask -> player.performCommand(command), null);
    }
}
