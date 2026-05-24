package almostkoi.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BukkitSchedulerImpl implements TaskWrapper.IScheduler {
    private final Plugin plugin;

    public BukkitSchedulerImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public TaskWrapper.Task runAsync(Runnable runnable) {
        var task = Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        return task::cancel;
    }

    @Override
    public TaskWrapper.Task runAsyncLater(Runnable runnable, long delayTicks) {
        var task = Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delayTicks);
        return task::cancel;
    }

    @Override
    public TaskWrapper.Task runAsyncTimer(Runnable runnable, long delayTicks, long periodTicks) {
        var task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, runnable, delayTicks, periodTicks);
        return task::cancel;
    }

    @Override
    public TaskWrapper.Task runSync(Runnable runnable) {
        var task = Bukkit.getScheduler().runTask(plugin, runnable);
        return task::cancel;
    }

    @Override
    public TaskWrapper.Task runSyncLater(Runnable runnable, long delayTicks) {
        var task = Bukkit.getScheduler().runTaskLater(plugin, runnable, delayTicks);
        return task::cancel;
    }

    @Override
    public TaskWrapper.Task runSyncTimer(Runnable runnable, long delayTicks, long periodTicks) {
        var task = Bukkit.getScheduler().runTaskTimer(plugin, runnable, delayTicks, periodTicks);
        return task::cancel;
    }

    @Override
    public void executePlayerCommand(Player player, String command) {
        Bukkit.getScheduler().runTask(plugin, () -> player.performCommand(command));
    }
}
