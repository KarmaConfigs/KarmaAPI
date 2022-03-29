package ml.karmaconfigs.api.common.timer.worker;

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.timer.scheduler.Scheduler;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Bukkit special synchronous scheduler
 *
 * @param <T> the karma plugin
 */
public class SyncScheduler<T extends KarmaSource> extends Scheduler {

    private final static Map<KarmaSource, Consumer<Integer>> taskStart = new HashMap<>();
    private final static Map<KarmaSource, Consumer<Integer>> taskComplete = new HashMap<>();
    private final static Map<Integer, Runnable> tasks = new HashMap<>();

    private final KarmaSource source;

    private static Thread runner;
    private static int taskId = 0;
    private static int current_task = 0;

    public SyncScheduler(final T src) {
        source = src;

        if (runner == null) {
            runner = new Thread(() -> {
                while (!runner.isInterrupted()) {
                    int random = 1000 + (int) ( Math.random() * ((2500 - 1000) + 1) );
                    int taskId = current_task;

                    if (tasks.containsKey(taskId)) {
                        Runnable task = tasks.remove(taskId);
                        if (task != null) {
                            Consumer<Integer> start = taskStart.getOrDefault(source, null);
                            Consumer<Integer> complete = taskComplete.getOrDefault(source, null);

                            SwingUtilities.invokeLater(() -> {
                                if (start != null) start.accept(taskId);
                                task.run();
                                if (complete != null) complete.accept(taskId);

                                current_task++;
                            });
                        } else {
                            current_task++;
                        }
                    }

                    try {
                        synchronized (this) {
                            wait(random);
                        }
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                }
            });

            runner.setName("SyncScheduler");
        }

        if (!runner.isAlive() || runner.isInterrupted())
            runner.start();
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
        return taskId;
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
