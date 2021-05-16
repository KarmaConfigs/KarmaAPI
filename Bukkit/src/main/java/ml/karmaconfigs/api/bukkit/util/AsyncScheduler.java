package ml.karmaconfigs.api.bukkit.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
public final class AsyncScheduler {

    private final static HashMap<Integer, Runnable> tasks = new HashMap<>();
    private static int completed = 0;
    private static BukkitScheduler scheduler = null;

    /**
     * Initialize the AsyncScheduler
     * located on the specified plugin
     *
     * @param p the plugin
     */
    public AsyncScheduler(@NotNull final JavaPlugin p) {
        if (scheduler == null) {
            scheduler = p.getServer().getScheduler();

            scheduler.runTaskTimerAsynchronously(p, () -> {
                int next = completed + 1;

                if (tasks.containsKey(next) && tasks.get(next) != null) {
                    Runnable runnable = tasks.get(next);
                    runnable.run();

                    completed++;
                }
            }, 0, 20);
        }
    }

    /**
     * Add a task to the task list
     *
     * @param task the task
     */
    public final void addTask(Runnable task) {
        int amount = tasks.size();
        int index = amount + 1;
        tasks.put(index, task);
    }
}
