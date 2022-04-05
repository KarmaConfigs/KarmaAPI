package ml.karmaconfigs.api.common.timer.worker;

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.timer.scheduler.Scheduler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Bukkit special asynchronous scheduler
 *
 * @param <T> the karma plugin
 */
public class AsyncScheduler<T extends KarmaSource> extends Scheduler {

    private final static Map<KarmaSource, Consumer<Integer>> taskStart = new HashMap<>();
    private final static Map<KarmaSource, Consumer<Integer>> taskComplete = new HashMap<>();
    private final static Map<Integer, Runnable> tasks = new HashMap<>();

    private final KarmaSource source;

    private static ScheduledExecutorService runner;
    private static int taskId = 0;
    private static int current_task = 0;

    public AsyncScheduler(final T src) {
        source = src;
        boolean initialize = false;

        if (runner == null) {
            //We will use the half of processors to have the best performance without using all CPU
            int threadCount = Math.abs(Runtime.getRuntime().availableProcessors() / 2);
            if (threadCount <= 0)
                threadCount = 1;

            runner = Executors.newScheduledThreadPool(threadCount);
            initialize = true;
        }

        if (!runner.isShutdown() || runner.isTerminated()) {
            //We will use the half of processors to have the best performance without using all CPU
            int threadCount = Math.abs(Runtime.getRuntime().availableProcessors() / 2);
            if (threadCount <= 0)
                threadCount = 1;

            runner = Executors.newScheduledThreadPool(threadCount);
            initialize = true;
        }

        if (initialize) {
            runner.scheduleAtFixedRate(() -> {
                Integer[] ids = tasks.keySet().toArray(new Integer[0]);
                Arrays.sort(ids);

                current_task = ids[0];
                if (tasks.containsKey(current_task)) {
                    Runnable task = tasks.remove(current_task);
                    if (task != null) {
                        Consumer<Integer> start = taskStart.getOrDefault(source, null);
                        Consumer<Integer> complete = taskComplete.getOrDefault(source, null);

                        runner.execute(() -> {
                            if (start != null) start.accept(current_task);
                            task.run();
                            if (complete != null) complete.accept(current_task);
                        });
                    }
                }
            }, 0, 1, TimeUnit.SECONDS);
        }
    }

    /**
     * Action to perform when a task has been
     * started
     *
     * @param paramConsumer the action to perform
     */
    @Override
    public void onTaskStart(final Consumer<Integer> paramConsumer) {
        taskStart.put(source, paramConsumer);
    }

    /**
     * Action to perform when a task has been
     * completed
     *
     * @param paramConsumer the action to perform
     */
    @Override
    public void onTaskComplete(final Consumer<Integer> paramConsumer) {
        taskComplete.put(source, paramConsumer);
    }

    /**
     * Queue another task to the scheduler
     *
     * @param paramRunnable the task to perform
     * @return the task id
     */
    @Override
    public int queue(final Runnable paramRunnable) {
        tasks.put(taskId++, paramRunnable);
        return taskId - 1;
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
