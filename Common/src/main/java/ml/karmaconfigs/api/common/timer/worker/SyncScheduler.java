package ml.karmaconfigs.api.common.timer.worker;

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.karma.KarmaConfig;
import ml.karmaconfigs.api.common.timer.scheduler.Scheduler;
import ml.karmaconfigs.api.common.timer.worker.event.TaskListener;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.string.StringUtils;

import javax.swing.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Bukkit special synchronous scheduler
 *
 * @param <T> the karma plugin
 */
public class SyncScheduler<T extends KarmaSource> extends Scheduler {

    private final static Map<KarmaSource, Consumer<Integer>> taskStart = new ConcurrentHashMap<>();
    private final static Map<KarmaSource, Consumer<Integer>> taskComplete = new ConcurrentHashMap<>();

    private final static Map<KarmaSource, Set<TaskListener>> listeners = new ConcurrentHashMap<>();
    private final static Map<Integer, ScheduledTask> tasks = new ConcurrentHashMap<>();

    private final KarmaSource source;

    private static ScheduledExecutorService runner;
    private static int taskId = 0;
    private static int current_task = 0;

    public SyncScheduler(final T src) {
        source = src;
        boolean initialize = false;

        if (runner == null) {
            runner = Executors.newSingleThreadScheduledExecutor();
            initialize = true;
        }

        if (!runner.isShutdown() || runner.isTerminated()) {
            runner = Executors.newSingleThreadScheduledExecutor();
            initialize = true;
        }

        if (initialize) {
            runner.scheduleAtFixedRate(() -> {
                Integer[] ids = tasks.keySet().toArray(new Integer[0]);
                Arrays.sort(ids);

                current_task = ids[0];
                if (tasks.containsKey(current_task)) {
                    ScheduledTask task = tasks.remove(current_task);
                    if (task != null) {
                        Consumer<Integer> start = taskStart.getOrDefault(source, null);
                        Consumer<Integer> complete = taskComplete.getOrDefault(source, null);

                        try {
                            SwingUtilities.invokeAndWait(() -> {
                                if (start != null) start.accept(current_task);
                                Set<TaskListener> registered = listeners.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                                registered.forEach((listener) -> listener.onSyncTaskStart(task));

                                task.getTask().run();

                                if (complete != null) complete.accept(current_task);
                                registered.forEach((listener) -> listener.onSyncTaskComplete(task));
                            });
                        } catch (Throwable ex) {
                            KarmaConfig config = new KarmaConfig();
                            if (config.log(Level.GRAVE)) {
                                source.logger().scheduleLog(Level.GRAVE, ex);
                            }
                            if (config.log(Level.INFO)) {
                                source.logger().scheduleLog(Level.INFO, "Failed to schedule task {0}", task.getId());
                            }

                            if (config.debug(Level.GRAVE)) {
                                source.console().send("Failed to schedule sync task {0} with id {1}", Level.GRAVE, task.getName(), task.getId());
                            }
                        }
                    }
                }
            }, 1, 1, TimeUnit.SECONDS);
        }
    }

    /**
     * Add a task listener
     *
     * @param listener the task listener
     */
    @Override
    public void addTaskListener(TaskListener listener) {
        Set<TaskListener> registered = listeners.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        registered.add(listener);

        listeners.put(source, registered);
    }

    /**
     * Remove a task listener
     *
     * @param listener the listener to remove
     */
    @Override
    public void removeTaskListener(TaskListener listener) {
        Set<TaskListener> registered = listeners.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        registered.remove(listener);

        listeners.put(source, registered);
    }

    /**
     * Action to perform when a task has been
     * started
     *
     * @param paramConsumer the action to perform
     * @deprecated Use {@link Scheduler#addTaskListener(TaskListener)} instead
     */
    @Override
    public @Deprecated void onTaskStart(final Consumer<Integer> paramConsumer) {
        taskStart.put(source, paramConsumer);
    }

    /**
     * Action to perform when a task has been
     * completed
     *
     * @param paramConsumer the action to perform
     * @deprecated Use {@link Scheduler#addTaskListener(TaskListener)} instead
     */
    @Override
    public @Deprecated void onTaskComplete(final Consumer<Integer> paramConsumer) {
        taskComplete.put(source, paramConsumer);
    }

    /**
     * Queue another task to the scheduler
     *
     * @param paramRunnable the task to perform
     * @return the task id
     */
    @Override
    public @Deprecated int queue(final Runnable paramRunnable) {
        int task = taskId++;

        ScheduledTask tsk = new ScheduledTask(StringUtils.generateString().create(), paramRunnable, task);
        tasks.put(task, tsk);

        Set<TaskListener> registered = listeners.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        registered.forEach((listener) -> listener.onSyncTaskSchedule(tsk));

        return task;
    }

    /**
     * Queue another task to the scheduler
     *
     * @param name          the task name
     * @param paramRunnable the task to perform
     */
    @Override
    public void queue(String name, Runnable paramRunnable) {
        int task = taskId++;

        ScheduledTask tsk = new ScheduledTask(name, paramRunnable, task);
        tasks.put(task, tsk);

        Set<TaskListener> registered = listeners.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        registered.forEach((listener) -> listener.onSyncTaskSchedule(tsk));
    }

    /**
     * Get a scheduled task by name
     *
     * @param name the task name
     * @return the task
     */
    @Override
    public ScheduledTask[] getByName(String name) {
        return new ScheduledTask[0];
    }

    /**
     * Get a scheduled task by id
     *
     * @param id the task id
     * @return the task
     */
    @Override
    public ScheduledTask getById(int id) {
        return null;
    }

    /**
     * Get the current task id
     *
     * @return the current task id
     */
    @Override
    public int currentTask() {
        return current_task;
    }
}
