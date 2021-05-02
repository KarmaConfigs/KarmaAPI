package ml.karmaconfigs.api.bungee.timer;

import ml.karmaconfigs.api.common.timer.TimeCondition;
import ml.karmaconfigs.api.common.timer.TimerNotFoundException;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Private GSA code
 *
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="http://karmaconfigs.cf/license/"> here </a>
 * or (fallback domain) <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
@SuppressWarnings("unused")
public final class AdvancedPluginTimer {

    private int back;
    private final int original;
    private int period = 1000;

    private final boolean restart;

    private boolean cancelled = false;
    private boolean refresh = false;

    private final HashMap<Integer, Runnable> tasksActions = new HashMap<>();
    private final HashMap<Integer, Runnable> tasksActionsSpecified = new HashMap<>();
    private final HashMap<Integer, Runnable> tasksActionsCondition = new HashMap<>();
    private final static HashMap<Integer, AdvancedPluginTimer> timersData = new HashMap<>();

    private final static HashSet<Integer> runningTimers = new HashSet<>();
    private final HashSet<Runnable> tasksActionsOnEnd = new HashSet<>();
    private final HashSet<Runnable> tasksActionsOnCancel = new HashSet<>();
    private final HashSet<Runnable> tasksActionsOnStart = new HashSet<>();
    private final HashSet<Runnable> tasksActionsOnRestart = new HashSet<>();

    private static int lastId = 0;

    private boolean async = false;

    private final Plugin plugin;
    private final int timerId;

    /**
     * Start a new timer
     *
     * @param owner the owner of the plugin timer
     * @param time the time to count from
     * @param restartTimer restart the timer when it ends
     */
    public AdvancedPluginTimer(final Plugin owner, final int time, final boolean restartTimer) {
        plugin = owner;
        back = time;
        original = time;
        restart = restartTimer;
        lastId++;
        timerId = lastId;
        timersData.put(timerId, this);
    }

    /**
     * Initialize the new timer class
     * whit a basic and generic repeat
     * timer
     *
     * @param owner the owner of the plugin timer
     * @param periodTime the period time
     */
    public AdvancedPluginTimer(final Plugin owner, final int periodTime) {
        plugin = owner;
        back = 1;
        original = 1;
        restart = true;
        lastId++;
        timerId = lastId;
        timersData.put(timerId, this);
        period = (int) TimeUnit.SECONDS.toMillis(periodTime);
    }

    /**
     * Cancel the timer
     */
    public final void setCancelled() {
        cancelled = true;
    }

    /**
     * Start counting back
     */
    public final void start() {
        executeOnStartTasks();
        if (async) {
            plugin.getProxy().getScheduler().runAsync(plugin, () -> {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!cancelled && !refresh) {
                            runningTimers.add(timerId);
                            if (back == 0) {
                                executeOnEndTasks();
                                if (restart) {
                                    back = original;
                                    executeOnRestartTasks();
                                } else {
                                    timer.cancel();
                                    timersData.remove(timerId);
                                    runningTimers.remove(timerId);
                                }
                            }
                            executeTaskActionAt(back);
                            back--;
                        } else {
                            cancel();
                            if (!refresh) {
                                timersData.remove(timerId);
                                runningTimers.remove(timerId);
                                executeOnCancelTasks();
                            }

                            refresh = false;
                        }
                    }
                }, 0, 1000);
            });
        } else {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!cancelled && !refresh) {
                        runningTimers.add(timerId);
                        if (back == 0) {
                            executeOnEndTasks();
                            if (restart) {
                                back = original;
                                executeOnRestartTasks();
                            } else {
                                timer.cancel();
                                timersData.remove(timerId);
                                runningTimers.remove(timerId);
                            }
                        }
                        executeTaskActionAt(back);
                        back--;
                    } else {
                        cancel();
                        if (!refresh) {
                            timersData.remove(timerId);
                            runningTimers.remove(timerId);
                            executeOnCancelTasks();
                        }

                        refresh = false;
                    }
                }
            }, 0, 1000);
        }
    }

    /**
     * Refresh the timer
     */
    private void refreshTimer() {
        if (isRunning()) {
            refresh = true;

                executeOnRestartTasks();

                if (async) {
                    plugin.getProxy().getScheduler().runAsync(plugin, () -> {
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (!cancelled && !refresh) {
                                    runningTimers.add(timerId);
                                    if (back == 0) {
                                        executeOnEndTasks();
                                        if (restart) {
                                            back = original;
                                            executeOnRestartTasks();
                                        } else {
                                            cancel();
                                            timersData.remove(timerId);
                                            runningTimers.remove(timerId);
                                        }
                                    }
                                    executeTaskActionAt(back);
                                    back--;
                                } else {
                                    cancel();
                                    if (!refresh) {
                                        timersData.remove(timerId);
                                        runningTimers.remove(timerId);
                                        executeOnCancelTasks();
                                    }

                                    refresh = false;
                                }
                            }
                        }, 0, period);
                    });
                } else {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (!cancelled && !refresh) {
                                runningTimers.add(timerId);
                                if (back == 0) {
                                    executeOnEndTasks();
                                    if (restart) {
                                        back = original;
                                        executeOnRestartTasks();
                                    } else {
                                        cancel();
                                        timersData.remove(timerId);
                                        runningTimers.remove(timerId);
                                    }
                                }
                                executeTaskActionAt(back);
                                back--;
                            } else {
                                cancel();
                                if (!refresh) {
                                    timersData.remove(timerId);
                                    runningTimers.remove(timerId);
                                    executeOnCancelTasks();
                                }

                                refresh = false;
                            }
                        }
                    }, 0, period);
                }
        }
    }

    /**
     * Set if the timer must run async or not
     *
     * @param _async if the timer must run async or not
     * @return the advanced plugin timer instance
     */
    public AdvancedPluginTimer setAsync(final boolean _async) {
        async = _async;
        refreshTimer();

        return this;
    }

    /**
     * Add a task to the timer
     *
     * @param run the runnable
     * @return the advanced plugin timer instance
     */
    public final AdvancedPluginTimer addAction(final Runnable run) {
        int i = 0;
        while (i <= back) {
            if (!tasksActions.containsKey(i)) {
                tasksActions.put(i, run);
            }
            i++;
        }

        return this;
    }

    /**
     * Add a task to the timer
     *
     * @param onPerform the time to perform
     * @param run the runnable
     * @return the advanced plugin timer instance
     */
    public final AdvancedPluginTimer addAction(final int onPerform, final Runnable run) {
        tasksActionsSpecified.put(onPerform, run);

        return this;
    }

    /**
     * Add a task to the timer
     *
     * @param condition the condition under condition_value
     * @param condition_value the time to perform
     * @param run the runnable
     * @return the advanced plugin timer instance
     */
    public final AdvancedPluginTimer addAction(final TimeCondition condition, final int condition_value, final Runnable run) {
        switch (condition) {
            case EQUALS:
                tasksActionsCondition.put(condition_value, run);
                break;
            case OVER_OF:
                int c_over_val = condition_value;
                while (c_over_val <= original) {
                    int second = c_over_val;
                    tasksActionsCondition.put(second, run);
                    c_over_val++;
                }
                break;
            case MINUS_TO:
                int c_minus_val = condition_value;
                while (c_minus_val >= 0) {
                    int second = c_minus_val;
                    tasksActionsCondition.put(second, run);
                    c_minus_val--;
                }
                break;
        }

        return this;
    }

    /**
     * Execute a task when the timer ends
     *
     * @param run the runnable
     * @return the advanced plugin timer instance
     */
    public final AdvancedPluginTimer addActionOnEnd(final Runnable run) {
        tasksActionsOnEnd.add(run);

        return this;
    }

    /**
     * Execute a task when the timer is cancelled
     *
     * @param run the runnable
     * @return the advanced plugin timer instance
     */
    public final AdvancedPluginTimer addActionOnCancel(final Runnable run) {
        tasksActionsOnCancel.add(run);

        return this;
    }

    /**
     * Execute a task when the timer starts
     *
     * @param run the runnable
     * @return the advanced plugin timer instance
     */
    public final AdvancedPluginTimer addActionOnStart(final Runnable run) {
        tasksActionsOnStart.add(run);

        return this;
    }

    /**
     * Execute a task when the timer restarts
     *
     * @param run the runnable
     * @return the advanced plugin timer instance
     */
    public final AdvancedPluginTimer addActionOnRestart(final Runnable run) {
        tasksActionsOnRestart.add(run);

        return this;
    }

    /**
     * Restart the timer
     *
     * @return the advanced plugin timer instance
     */
    public final AdvancedPluginTimer resetTimer() {
        back = original;

        return this;
    }

    /**
     * Get the timer time left
     *
     * @return the time left
     */
    public final int getTime() {
        return back;
    }

    /**
     * Get the time as his correspondent time unit
     *
     * @param secondName second unit name
     * @param minuteName minute unit name
     * @param hourName hour unit name
     *
     * @return the timer time left with units
     */
    public final String getTimeAsUnit(final String secondName, final String minuteName, final String hourName) {
        int hours = (int) TimeUnit.SECONDS.toHours(back);
        int minutes = (int) TimeUnit.SECONDS.toMinutes(back);
        if (back <= 59) {
            return back + " " + secondName;
        } else {
            if (minutes <= 59) {
                return minutes + " " + minuteName + " and " + (minutes - back) + " " + secondName;
            } else {
                return hours + " " + hourName + ", " + (hours - minutes) + " " + minuteName + ", " + ((hours - minutes) - back) + " " + secondName;
            }
        }
    }

    /**
     * Get the timer id
     *
     * @return the timer id
     */
    public final int getTimerId() {
        for (int i : timersData.keySet()) {
            if (timersData.get(i).equals(this)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * Check if the timer is cancelled
     *
     * @return if the timer is cancelled
     */
    public final boolean isCancelled() {
        return cancelled;
    }

    /**
     * Check if the timer is running
     *
     * @return if the timer is running
     */
    public final boolean isRunning() {
        return runningTimers.contains(timerId);
    }

    /**
     * Get the NewTimer manager
     */
    public interface getManager {

        /**
         * Get the timer from a timer ID
         *
         * @param timerId the timer id
         * @return a NewTimer
         *
         * @throws TimerNotFoundException if the timer is not
         * found
         */
        static AdvancedPluginTimer getTimer(int timerId) throws TimerNotFoundException {
            if (timersData.containsKey(timerId)) {
                return timersData.get(timerId);
            } else {
                throw new TimerNotFoundException(timerId);
            }
        }

        /**
         * Cancel all the running timers
         */
        static void cancelAllTimers() {
            for (AdvancedPluginTimer timer : timersData.values()) {
                timer.setCancelled();
                timer.executeOnEndTasks();
            }
        }
    }

    /**
     * Execute the task attached to that time
     *
     * @param task the time
     */
    private void executeTaskActionAt(final int task) {
        if (tasksActions.containsKey(task)) {
            Runnable runnable = tasksActions.get(task);
            runnable.run();
        }
        if (tasksActionsSpecified.containsKey(task)) {
            Runnable runnable = tasksActionsSpecified.get(task);
            runnable.run();
        }
        if (tasksActionsCondition.containsKey(task)) {
            Runnable runnable = tasksActionsCondition.get(task);
            runnable.run();
        }
    }

    /**
     * Execute the tasks attached to end timer
     */
    protected final void executeOnEndTasks() {
        for (Runnable runnable : tasksActionsOnEnd) {
            runnable.run();
        }
    }

    /**
     * Execute the tasks attached to cancel timer
     */
    protected final void executeOnCancelTasks() {
        for (Runnable runnable : tasksActionsOnCancel) {
            runnable.run();
        }
    }

    /**
     * Execute the tasks attached to start timer
     */
    protected final void executeOnStartTasks() {
        for (Runnable runnable : tasksActionsOnStart) {
            runnable.run();
        }
    }

    /**
     * Execute the tasks attached to restart timer
     */
    protected final void executeOnRestartTasks() {
        for (Runnable runnable : tasksActionsOnRestart) {
            runnable.run();
        }
    }
}
