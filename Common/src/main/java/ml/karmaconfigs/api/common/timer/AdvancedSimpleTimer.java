package ml.karmaconfigs.api.common.timer;

import ml.karmaconfigs.api.common.karma.KarmaSource;

import javax.swing.*;
import java.io.Serializable;
import java.util.Timer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/*
 * This file is part of KarmaAPI, licensed under the MIT License.
 *
 *  Copyright (c) karma (KarmaDev) <karmaconfigs@gmail.com>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

/**
 * Advanced simple timer, to create timers and specify
 * actions on certain events
 */
public final class AdvancedSimpleTimer implements Serializable {

    private int back;
    private final int original;
    private int period = 1000;

    private final boolean restart;

    private boolean cancelled = false;
    private boolean refresh = false;

    private final Map<Integer, Runnable> tasksActions = new ConcurrentHashMap<>();
    private final Map<Integer, Runnable> tasksActionsSpecified = new ConcurrentHashMap<>();
    private final Map<Integer, Runnable> tasksActionsCondition = new ConcurrentHashMap<>();

    private final static Map<Integer, AdvancedSimpleTimer> timersData = new ConcurrentHashMap<>();
    private final static Map<KarmaSource, Set<Integer>> runningTimers = new ConcurrentHashMap<>();

    private final Set<Runnable> tasksActionsOnEnd = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<Runnable> tasksActionsOnCancel = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<Runnable> tasksActionsOnStart = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<Runnable> tasksActionsOnRestart = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private static int lastId = 0;

    private boolean async = false;

    private final int timerId;

    private final KarmaSource owner;

    /**
     * Start a new timer
     *
     * @param source the source that wants to create the timer
     * @param time the time to count from
     * @param restartTimer restart the timer when it ends
     */
    public AdvancedSimpleTimer(final KarmaSource source, final int time, final boolean restartTimer) {
        owner = source;
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
     * @param source the source that wants to create the timer
     * @param periodTime the period time
     */
    public AdvancedSimpleTimer(final KarmaSource source, final int periodTime) {
        owner = source;
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
            new Thread(() -> {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (!cancelled && !refresh) {
                            Set<Integer> ids = runningTimers.getOrDefault(owner, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                            ids.add(timerId);
                            runningTimers.put(owner, ids);

                            if (back == 0) {
                                executeOnEndTasks();
                                if (restart) {
                                    back = original;
                                    executeOnRestartTasks();
                                } else {
                                    timer.cancel();
                                    timersData.remove(timerId);

                                    ids = runningTimers.getOrDefault(owner, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                                    ids.remove(timerId);
                                    runningTimers.put(owner, ids);
                                }
                            }
                            executeTaskActionAt(back);
                            back--;
                        } else {
                            cancel();
                            if (!refresh) {
                                timersData.remove(timerId);

                                Set<Integer> ids = runningTimers.getOrDefault(owner, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                                ids.remove(timerId);
                                runningTimers.put(owner, ids);

                                executeOnCancelTasks();
                            }

                            refresh = false;
                        }
                    }
                }, 0, 1000);
            }).start();
        } else {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (!cancelled && !refresh) {
                        Set<Integer> ids = runningTimers.getOrDefault(owner, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                        ids.add(timerId);
                        runningTimers.put(owner, ids);

                        if (back == 0) {
                            executeOnEndTasks();
                            if (restart) {
                                back = original;
                                executeOnRestartTasks();
                            } else {
                                timer.cancel();
                                timersData.remove(timerId);

                                ids = runningTimers.getOrDefault(owner, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                                ids.remove(timerId);
                                runningTimers.put(owner, ids);
                            }
                        }
                        executeTaskActionAt(back);
                        back--;
                    } else {
                        cancel();
                        if (!refresh) {
                            timersData.remove(timerId);

                            Set<Integer> ids = runningTimers.getOrDefault(owner, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                            ids.remove(timerId);
                            runningTimers.put(owner, ids);

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
                    new Thread(() -> {
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (!cancelled && !refresh) {
                                    Set<Integer> ids = runningTimers.getOrDefault(owner, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                                    ids.add(timerId);
                                    runningTimers.put(owner, ids);

                                    if (back == 0) {
                                        executeOnEndTasks();
                                        if (restart) {
                                            back = original;
                                            executeOnRestartTasks();
                                        } else {
                                            cancel();
                                            timersData.remove(timerId);

                                            ids = runningTimers.getOrDefault(owner, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                                            ids.remove(timerId);
                                            runningTimers.put(owner, ids);
                                        }
                                    }
                                    executeTaskActionAt(back);
                                    back--;
                                } else {
                                    cancel();
                                    if (!refresh) {
                                        timersData.remove(timerId);

                                        Set<Integer> ids = runningTimers.getOrDefault(owner, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                                        ids.remove(timerId);
                                        runningTimers.put(owner, ids);

                                        executeOnCancelTasks();
                                    }

                                    refresh = false;
                                }
                            }
                        }, 0, period);
                    }).start();
                } else {
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (!cancelled && !refresh) {
                                Set<Integer> ids = runningTimers.getOrDefault(owner, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                                ids.add(timerId);
                                runningTimers.put(owner, ids);

                                if (back == 0) {
                                    executeOnEndTasks();
                                    if (restart) {
                                        back = original;
                                        executeOnRestartTasks();
                                    } else {
                                        cancel();
                                        timersData.remove(timerId);

                                        ids = runningTimers.getOrDefault(owner, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                                        ids.remove(timerId);
                                        runningTimers.put(owner, ids);
                                    }
                                }
                                executeTaskActionAt(back);
                                back--;
                            } else {
                                cancel();
                                if (!refresh) {
                                    timersData.remove(timerId);

                                    Set<Integer> ids = runningTimers.getOrDefault(owner, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                                    ids.remove(timerId);
                                    runningTimers.put(owner, ids);

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
     * Request a sync function
     *
     * @param runnable the function to run
     */
    public final synchronized void requestSynchronous(final Runnable runnable) {
        try {
            if (async && !cancelled)
                SwingUtilities.invokeAndWait(runnable);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Get the timer source
     *
     * @return the timer source
     */
    public final KarmaSource getSource() {
        return owner;
    }

    /**
     * Set if the timer must run async or not
     *
     * @param _async if the timer must run async or not
     * @return the advanced plugin timer instance
     */
    public AdvancedSimpleTimer setAsync(final boolean _async) {
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
    public final AdvancedSimpleTimer addAction(final Runnable run) {
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
    public final AdvancedSimpleTimer addAction(final int onPerform, final Runnable run) {
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
    public final AdvancedSimpleTimer addAction(final TimeCondition condition, final int condition_value, final Runnable run) {
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
    public final AdvancedSimpleTimer addActionOnEnd(final Runnable run) {
        tasksActionsOnEnd.add(run);

        return this;
    }

    /**
     * Execute a task when the timer is cancelled
     *
     * @param run the runnable
     * @return the advanced plugin timer instance
     */
    public final AdvancedSimpleTimer addActionOnCancel(final Runnable run) {
        tasksActionsOnCancel.add(run);

        return this;
    }

    /**
     * Execute a task when the timer starts
     *
     * @param run the runnable
     * @return the advanced plugin timer instance
     */
    public final AdvancedSimpleTimer addActionOnStart(final Runnable run) {
        tasksActionsOnStart.add(run);

        return this;
    }

    /**
     * Execute a task when the timer restarts
     *
     * @param run the runnable
     * @return the advanced plugin timer instance
     */
    public final AdvancedSimpleTimer addActionOnRestart(final Runnable run) {
        tasksActionsOnRestart.add(run);

        return this;
    }

    /**
     * Restart the timer
     */
    public final void resetTimer() {
        back = original;
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
        Set<Integer> ids = runningTimers.getOrDefault(owner, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        return ids.contains(timerId);
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
        static AdvancedSimpleTimer getTimer(int timerId) throws TimerNotFoundException {
            if (timersData.containsKey(timerId)) {
                return timersData.get(timerId);
            } else {
                throw new TimerNotFoundException(timerId);
            }
        }

        /**
         * Cancel all the running timers
         *
         * @param owner the timer owner
         */
        static void cancelAllTimers(final KarmaSource owner) {
            for (int id : timersData.keySet()) {
                AdvancedSimpleTimer timer = timersData.getOrDefault(id, null);
                if (timer != null) {
                    if (timer.getSource().getClass().getName().equals(owner.getClass().getName())) {
                        timer.setCancelled();
                        timersData.remove(id);
                    }
                } else {
                    timersData.remove(id);
                }
            }

            runningTimers.remove(owner);
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
