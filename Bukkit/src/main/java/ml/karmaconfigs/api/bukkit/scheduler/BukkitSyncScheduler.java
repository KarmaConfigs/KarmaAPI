package ml.karmaconfigs.api.bukkit.scheduler;

import ml.karmaconfigs.api.bukkit.KarmaPlugin;
import ml.karmaconfigs.api.common.timer.scheduler.Scheduler;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Bukkit special synchronous scheduler
 *
 * @param <T> the karma plugin
 */
public class BukkitSyncScheduler<T extends KarmaPlugin> extends Scheduler {

    private static final Map<KarmaPlugin, SchedulerData> tasks = new ConcurrentHashMap<>();

    private final T instance;

    /**
     * Initialize the scheduler
     *
     * @param source the scheduler owner
     */
    public BukkitSyncScheduler(final T source) {
        instance = source;
        SchedulerData tmpData = tasks.getOrDefault(source, null);
        if (tmpData == null) {
            tmpData = new SchedulerData();
            tasks.put(source, tmpData);
        }

        SchedulerData data = tmpData;
        if (data.getScheduler() == null) {
            BukkitScheduler scheduler = source.getServer().getScheduler();
            scheduler.runTaskTimer(source, () -> {
                int next = data.getCurrentId() + 1;

                Runnable runnable = data.getTask(next);
                if (runnable != null) {
                    if (data.onTaskStart() != null)
                        data.onTaskStart().accept(next);

                    runnable.run();
                    data.updateId(next);

                    if (data.onTaskEnd() != null)
                        data.onTaskEnd().accept(next);
                }
            }, 0L, 20L);

            data.updateScheduler(scheduler);
        }
    }

    /**
     * Action to perform when a task has been
     * started
     *
     * @param taskId the action to perform
     */
    @Override
    public final void onTaskStart(final Consumer<Integer> taskId) {
        SchedulerData data = tasks.getOrDefault(instance, null);
        if (data == null) {
            data = new SchedulerData();
            tasks.put(instance, data);
        }

        data.taskStart = taskId;
    }

    /**
     * Action to perform when a task has been
     * completed
     *
     * @param taskId the action to perform
     */
    @Override
    public final void onTaskComplete(final Consumer<Integer> taskId) {
        SchedulerData data = tasks.getOrDefault(instance, null);
        if (data == null) {
            data = new SchedulerData();
            tasks.put(instance, data);
        }

        data.taskEnd = taskId;
    }

    /**
     * Queue another task to the scheduler
     *
     * @param task the task to perform
     * @return the task id
     */
    @Override
    public final int queue(final Runnable task) {
        SchedulerData data = tasks.getOrDefault(instance, null);
        if (data == null) {
            data = new SchedulerData();
            tasks.put(instance, data);
        }

        return data.addTask(task);
    }

    /**
     * Get the current task id
     *
     * @return the current task id
     */
    @Override
    public final int currentTask() {
        SchedulerData data = tasks.getOrDefault(instance, null);
        if (data == null) {
            data = new SchedulerData();
            tasks.put(instance, data);
        }

        return data.getCurrentId();
    }

    /**
     * Get if the scheduler has more tasks
     *
     * @return if the scheduler has more tasks
     */
    public final boolean hasMoreTasks() {
        SchedulerData data = tasks.getOrDefault(instance, null);
        if (data == null) {
            data = new SchedulerData();
            tasks.put(instance, data);
        }

        return data.hasNext();
    }
}
