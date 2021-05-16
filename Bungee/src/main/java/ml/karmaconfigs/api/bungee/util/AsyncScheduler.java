package ml.karmaconfigs.api.bungee.util;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

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
    private static TaskScheduler scheduler = null;

    /**
     * Initialize the AsyncScheduler
     * located on the specified plugin
     *
     * @param p the plugin
     */
    public AsyncScheduler(@NotNull final Plugin p) {
        if (scheduler == null) {
            scheduler = p.getProxy().getScheduler();

            scheduler.schedule(p, () -> scheduler.runAsync(p, () -> {
                int next = completed + 1;

                if (tasks.containsKey(next) && tasks.get(next) != null) {
                    Runnable runnable = tasks.get(next);
                    runnable.run();

                    completed++;
                }
            }), 0, 1, TimeUnit.SECONDS);
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
