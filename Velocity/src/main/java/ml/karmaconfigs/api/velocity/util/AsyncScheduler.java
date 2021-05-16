package ml.karmaconfigs.api.velocity.util;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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
    private static Timer scheduler = null;

    /**
     * Initialize the AsyncScheduler
     */
    public AsyncScheduler() {
        if (scheduler == null) {
            scheduler = new Timer();

            new Thread(() -> scheduler.schedule(new TimerTask() {
                @Override
                public void run() {
                    int next = completed + 1;

                    if (tasks.containsKey(next) && tasks.get(next) != null) {
                        Runnable runnable = tasks.get(next);
                        runnable.run();

                        completed++;
                    }
                }
            }, 0, 1000)).start();
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
