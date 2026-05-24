package almostkoi.scheduler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TaskWrapper {

    public interface Task {
        void cancel();
    }

    public interface IScheduler {
        Task runAsync(Runnable runnable);
        Task runAsyncLater(Runnable runnable, long delayTicks);
        Task runAsyncTimer(Runnable runnable, long delayTicks, long periodTicks);
        Task runSync(Runnable runnable);
        Task runSyncLater(Runnable runnable, long delayTicks);
        Task runSyncTimer(Runnable runnable, long delayTicks, long periodTicks);
        void executePlayerCommand(Player player, String command);
    }

    private static final boolean isFolia;
    private static IScheduler schedulerInstance;

    static {
        boolean foliaDetected = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            foliaDetected = true;
        } catch (ClassNotFoundException e) {
            foliaDetected = false;
        }
        isFolia = foliaDetected;
    }

    public static void init(Plugin plugin) {
        if (isFolia) {
            schedulerInstance = new FoliaSchedulerImpl(plugin);
        } else {
            schedulerInstance = new BukkitSchedulerImpl(plugin);
        }
    }

    public static boolean isFolia() {
        return isFolia;
    }

    public static Task runAsync(Runnable runnable) {
        return schedulerInstance.runAsync(runnable);
    }

    public static Task runAsyncLater(Runnable runnable, long delayTicks) {
        return schedulerInstance.runAsyncLater(runnable, delayTicks);
    }

    public static Task runAsyncTimer(Runnable runnable, long delayTicks, long periodTicks) {
        return schedulerInstance.runAsyncTimer(runnable, delayTicks, periodTicks);
    }

    public static Task runSync(Runnable runnable) {
        return schedulerInstance.runSync(runnable);
    }

    public static Task runSyncLater(Runnable runnable, long delayTicks) {
        return schedulerInstance.runSyncLater(runnable, delayTicks);
    }

    public static Task runSyncTimer(Runnable runnable, long delayTicks, long periodTicks) {
        return schedulerInstance.runSyncTimer(runnable, delayTicks, periodTicks);
    }

    public static void executePlayerCommand(Player player, String command) {
        schedulerInstance.executePlayerCommand(player, command);
    }
}
