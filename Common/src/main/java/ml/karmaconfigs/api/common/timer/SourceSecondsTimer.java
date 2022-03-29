package ml.karmaconfigs.api.common.timer;

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

import ml.karmaconfigs.api.common.karma.KarmaAPI;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.karma.file.KarmaConfig;
import ml.karmaconfigs.api.common.timer.scheduler.SimpleScheduler;
import ml.karmaconfigs.api.common.timer.scheduler.errors.IllegalTimerAccess;
import ml.karmaconfigs.api.common.timer.scheduler.errors.TimerAlreadyStarted;
import ml.karmaconfigs.api.common.timer.scheduler.errors.TimerNotFound;
import ml.karmaconfigs.api.common.utils.enums.Level;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static ml.karmaconfigs.api.common.karma.KarmaAPI.source;

/**
 * Karma seconds scheduler
 */
public final class SourceSecondsTimer extends SimpleScheduler {

    /**
     * A map containing id => scheduler
     */
    private static final Map<Integer, SimpleScheduler> timersData = new ConcurrentHashMap<>();
    /**
     * A map containing source => schedulers ID
     */
    private static final Map<KarmaSource, Set<Integer>> runningTimers = new ConcurrentHashMap<>();

    /**
     * The original scheduler time
     */
    private final int original;
    /**
     * The scheduler ID
     */
    private final int id;

    /**
     * The scheduler source
     */
    private final KarmaSource source;

    /**
     * A map containing second => actions to perform
     */
    private final Map<Integer, Set<Runnable>> secondsActions = new ConcurrentHashMap<>();
    /**
     * A map containing second => actions to perform
     */
    private final Map<Integer, Set<Consumer<Integer>>> secondsConsumer = new ConcurrentHashMap<>();
    /**
     * A map containing second => actions to perform
     */
    private final Map<Integer, Set<Consumer<Long>>> secondsLongConsumer = new ConcurrentHashMap<>();

    /**
     * Actions to perform when the scheduler ends
     */
    private final Set<Runnable> onEndTasks = Collections.newSetFromMap(new ConcurrentHashMap<>());
    /**
     * Actions to perform when the scheduler starts
     */
    private final Set<Runnable> onStartTasks = Collections.newSetFromMap(new ConcurrentHashMap<>());
    /**
     * Actions to perform when the scheduler restarts
     */
    private final Set<Runnable> onRestartTasks = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * The scheduler decrement passed time from original
     */
    private int back;

    /**
     * The scheduler period
     */
    private long period = TimeUnit.SECONDS.toMillis(1L);

    /**
     * Send a warning to the console when the timer is
     * cancelled because its owner has been unloaded
     */
    private boolean cancelUnloaded = true;
    /**
     * If the timer is cancelled
     */
    private boolean cancel = false;
    /**
     * If the timer is paused
     */
    private boolean pause = false;
    /**
     * If the timer should restart
     */
    private boolean restart;
    /**
     * If the timer is queued for restart
     */
    private boolean temp_restart = false;
    /**
     * If the timer has multi-threading
     */
    private boolean thread = false;

    /**
     * Action to perform when the timer gets paused
     */
    private Consumer<Long> pauseAction = null;
    /**
     * Action to perform when the timer gets cancelled
     */
    private Consumer<Long> cancelAction = null;

    /**
     * Initialize the scheduler
     *
     * @param owner the scheduler owner
     * @param time the scheduler start time
     * @param autoRestart if the scheduler should auto-restart
     *                    when it ends
     */
    public SourceSecondsTimer(final KarmaSource owner, final Number time, final boolean autoRestart) {
        super(owner);
        source = owner;
        restart = autoRestart;
        original = (int) time.longValue();
        back = original;
        id = getId();
        timersData.put(id, this);
    }

    /**
     * Initialize the scheduler
     *
     * @param owner the scheduler owner
     * @param builtId the scheduler ID
     * @throws TimerNotFound if the scheduler does not exist
     * @throws IllegalTimerAccess if the scheduler owner does not match with
     * provided
     */
    public SourceSecondsTimer(final KarmaSource owner, final int builtId) throws TimerNotFound, IllegalTimerAccess {
        super(owner);
        SimpleScheduler built = timersData.getOrDefault(builtId, null);
        if (built != null) {
            if (built.getSource().isSource(owner)) {
                source = built.getSource();
                restart = built.autoRestart();
                original = (int) built.getOriginalTime();
                back = (int) TimeUnit.MILLISECONDS.toSeconds(built.getMillis());
                id = builtId;
            } else {
                throw new IllegalTimerAccess(owner, built);
            }
        } else {
            throw new TimerNotFound(builtId);
        }
    }

    /**
     * Notice when the timer has been stopped because
     * its source has been also unloaded
     *
     * @param status the notice unloaded status
     * @return this instance
     */
    public SimpleScheduler cancelUnloaded(final boolean status) {
        cancelUnloaded = status;
        return this;
    }

    /**
     * Cancel the scheduler
     */
    @Override
    public void cancel() {
        cancel = true;
    }

    /**
     * Pause the scheduler
     */
    @Override
    public void pause() {
        pause = true;
        if (pauseAction != null)
            runSecondsLongWithThread(pauseAction);
    }

    /**
     * Start the scheduler
     *
     * @throws TimerAlreadyStarted if the scheduler is already started
     */
    @Override
    public void start() throws TimerAlreadyStarted {
        KarmaConfig config = new KarmaConfig();
        Set<Integer> ids = runningTimers.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));

        if (!ids.contains(id)) {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    boolean run = (!cancelUnloaded || KarmaAPI.isLoaded(source));
                    
                    if (run) {
                        if (!pause) {
                            if (cancel || temp_restart) {
                                if (!temp_restart) {
                                    timersData.remove(id);
                                    Set<Integer> ids = runningTimers.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                                    ids.remove(id);

                                    runningTimers.put(source, ids);
                                    if (cancelAction != null)
                                        runSecondsLongWithThread(cancelAction);

                                    cancel = false;
                                    pause = false;
                                    temp_restart = false;

                                    timer.cancel();
                                } else {
                                    back = original;
                                    onRestartTasks.forEach(task -> runTaskWithThread(task));
                                    temp_restart = false;
                                }
                            } else {
                                executeTasks();

                                if (back > 0) {
                                    back--;
                                } else {
                                    back = original;
                                    if (restart) {
                                        onRestartTasks.forEach(task -> runTaskWithThread(task));
                                        back = original;
                                    } else {
                                        onEndTasks.forEach(task -> runTaskWithThread(task));

                                        timersData.remove(id);
                                        Set<Integer> ids = runningTimers.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                                        ids.remove(id);

                                        runningTimers.put(source, ids);

                                        cancel = false;
                                        pause = false;
                                        temp_restart = false;

                                        timer.cancel();
                                    }
                                }
                            }
                        }
                    } else {
                        timersData.remove(id);
                        Set<Integer> ids = runningTimers.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                        ids.remove(id);

                        runningTimers.put(source, ids);
                        if (cancelAction != null)
                            runSecondsLongWithThread(cancelAction);

                        cancel = false;
                        pause = false;
                        temp_restart = false;

                        timer.cancel();

                        if (config.debug(Level.INFO)) {
                            source(true).console().send("Timer task with ID {0} has been cancelled because its source {1} has been unloaded", Level.INFO, id, source.name());
                        }
                    }
                }
            }, 0L, period);
        } else {
            throw new TimerAlreadyStarted(this);
        }
    }

    /**
     * Restart the scheduler
     */
    @Override
    public void restart() {
        temp_restart = true;
    }

    /**
     * Set if the timer should auto restart
     * when it ends
     *
     * @param status if the timer should auto restart
     * @return this instance
     */
    @Override
    public SimpleScheduler updateAutoRestart(final boolean status) {
        restart = status;
        return this;
    }

    /**
     * Set the timer update period
     *
     * @param time the period
     * @return this instance
     */
    @Override
    public SimpleScheduler withPeriod(final Number time) {
        int seconds;
        String value = time.toString();
        int milli = 0;
        if (value.contains(".")) {
            String[] data = value.split("\\.");
            String first = data[0];
            String millis = value.replaceFirst(first + ".", "");
            seconds = (int) TimeUnit.SECONDS.toMillis(Integer.parseInt(first));
            if (millis.length() != 2) {
                if (millis.length() < 2)
                    millis = millis + "000";
                milli = Integer.parseInt(millis.substring(0, 2));
            } else {
                milli = Integer.parseInt(millis);
            }
        } else {
            seconds = (int) TimeUnit.SECONDS.toMillis(time.intValue());
        }
        period = (seconds + milli);
        return this;
    }

    /**
     * Set if the timer runs on another thread
     *
     * @param status if the timer has multi-threading
     * @return this instance
     */
    @Override
    public SimpleScheduler multiThreading(final boolean status) {
        thread = status;
        return this;
    }

    /**
     * Add an action to perform when the timer reaches
     * the specified second
     *
     * @param time the second
     * @param task the action to perform
     * @return this instance
     */
    @Override
    public SimpleScheduler exactSecondPeriodAction(final int time, final Runnable task) {
        Set<Runnable> actions = secondsActions.getOrDefault(time, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        actions.add(task);
        secondsActions.put(time, actions);
        return this;
    }

    /**
     * Add an action to perform when the timer reaches
     * the specified millisecond
     *
     * @param time the millisecond
     * @param task the action to perform
     * @return this instance
     */
    @Override
    public SimpleScheduler exactPeriodAction(final long time, final Runnable task) {
        Set<Runnable> actions = secondsActions.getOrDefault((int) TimeUnit.MILLISECONDS.toSeconds(time), Collections.newSetFromMap(new ConcurrentHashMap<>()));
        actions.add(task);
        secondsActions.put((int) TimeUnit.MILLISECONDS.toSeconds(time), actions);
        return this;
    }

    /**
     * Add an action when the timer passes a second
     *
     * @param action the action to perform
     * @return this instance
     */
    @Override
    public SimpleScheduler secondChangeAction(final Consumer<Integer> action) {
        int second = original;
        while (second >= 0) {
            Set<Consumer<Integer>> actions = secondsConsumer.getOrDefault(second--, Collections.newSetFromMap(new ConcurrentHashMap<>()));
            actions.add(action);
            secondsConsumer.put(second, actions);
        }
        return this;
    }

    /**
     * Add an action when the timer passes a millisecond
     *
     * @param action the action to perform
     * @return this instance
     */
    @Override
    public SimpleScheduler periodChangeAction(final Consumer<Long> action) {
        int second = original;
        while (second >= 0) {
            Set<Consumer<Long>> actions = secondsLongConsumer.getOrDefault(second--, Collections.newSetFromMap(new ConcurrentHashMap<>()));
            actions.add(action);
            secondsLongConsumer.put(second, actions);
        }
        return this;
    }

    /**
     * Set the action to perform when the timer is cancelled
     *
     * @param action the action to perform
     * @return this instance
     */
    @Override
    public SimpleScheduler cancelAction(final Consumer<Long> action) {
        cancelAction = action;
        return this;
    }

    /**
     * Set the action to perform when the timer is paused
     *
     * @param action the action to perform
     * @return this instance
     */
    @Override
    public SimpleScheduler pauseAction(final Consumer<Long> action) {
        pauseAction = action;
        return this;
    }

    /**
     * Set the action to perform when the timer is started
     *
     * @param task the action to perform
     * @return this instance
     */
    @Override
    public SimpleScheduler startAction(final Runnable task) {
        onStartTasks.add(task);
        return this;
    }

    /**
     * Set the action to perform when the timer is
     * completely ended
     *
     * @param task the action to perform
     * @return this instance
     */
    @Override
    public SimpleScheduler endAction(final Runnable task) {
        onEndTasks.add(task);
        return this;
    }

    /**
     * Set the action to perform when the timer is restarted
     *
     * @param task the action to perform
     * @return this instance
     */
    @Override
    public SimpleScheduler restartAction(final Runnable task) {
        onRestartTasks.add(task);
        return this;
    }

    /**
     * Add a conditional action
     *
     * @param condition the condition that the timer
     *                           must complete
     * @param condition_value the timer second
     * @param action the action to perform
     * @return this instance
     */
    @Override
    public SimpleScheduler conditionalAction(final TimeCondition condition, final int condition_value, final Consumer<Integer> action) {
        Set<Consumer<Integer>> actions;
        int c_over_val;
        int c_minus_val;
        switch (condition) {
            case EQUALS:
                actions = secondsConsumer.getOrDefault(condition_value, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                actions.add(action);
                secondsConsumer.put(condition_value, actions);
                break;
            case OVER_OF:
                c_over_val = condition_value;
                while (c_over_val <= original) {
                    actions = secondsConsumer.getOrDefault(c_over_val++, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                    actions.add(action);
                    secondsConsumer.put(c_over_val, actions);
                }
                break;
            case MINUS_TO:
                c_minus_val = condition_value;
                while (c_minus_val >= 0) {
                    actions = secondsConsumer.getOrDefault(c_minus_val--, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                    actions.add(action);
                    secondsConsumer.put(c_minus_val, actions);
                }
                break;
        }
        return this;
    }

    /**
     * Add a conditional action
     *
     * @param condition the condition that the timer must complete
     * @param condition_value the timer millisecond
     * @param action the action to perform
     * @return this instance
     */
    @Override
    public SimpleScheduler conditionalPeriodAction(final TimeCondition condition, final long condition_value, final Consumer<Long> action) {
        Set<Consumer<Long>> actions;
        int c_over_val, c_minus_val, seconds = (int) TimeUnit.MILLISECONDS.toSeconds(condition_value);
        switch (condition) {
            case EQUALS:
                actions = secondsLongConsumer.getOrDefault(seconds, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                actions.add(action);
                secondsLongConsumer.put(seconds, actions);
                break;
            case OVER_OF:
                c_over_val = seconds;
                while (c_over_val <= original) {
                    actions = secondsLongConsumer.getOrDefault(c_over_val++, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                    actions.add(action);
                    secondsLongConsumer.put(c_over_val, actions);
                }
                break;
            case MINUS_TO:
                c_minus_val = seconds;
                while (c_minus_val >= 0) {
                    actions = secondsLongConsumer.getOrDefault(c_minus_val--, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                    actions.add(action);
                    secondsLongConsumer.put(c_minus_val, actions);
                }
                break;
        }
        return this;
    }

    /**
     * Get if the timer is cancelled
     *
     * @return if the timer is cancelled
     */
    @Override
    public boolean isCancelled() {
        return cancel;
    }

    /**
     * Get if the timer is running
     *
     * @return if the timer is running
     */
    @Override
    public boolean isRunning() {
        Set<Integer> ids = runningTimers.getOrDefault(source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        return ids.contains(id);
    }

    /**
     * Get if the timer is paused
     *
     * @return if the timer is paused
     */
    @Override
    public boolean isPaused() {
        return pause;
    }

    /**
     * Get if the timer auto restarts
     *
     * @return if the timer starts the timer
     * automatically when it ends
     */
    @Override
    public boolean autoRestart() {
        return restart;
    }

    /**
     * Get if the timer has multi-threading enabled
     *
     * @return if the timer runs on another thread
     */
    @Override
    public boolean isMultiThreading() {
        return thread;
    }

    /**
     * Get the timer start time
     *
     * @return the timer start time
     */
    @Override
    public long getOriginalTime() {
        return original;
    }

    /**
     * Get the timer configured period
     *
     * @return the timer update period
     */
    @Override
    public long getPeriod() {
        return period;
    }

    /**
     * Get the timer milliseconds
     *
     * @return the timer exact time
     */
    @Override
    public long getMillis() {
        return TimeUnit.SECONDS.toMillis(back);
    }

    /**
     * Execute the tasks corresponding to the current
     * second/millisecond
     */
    private void executeTasks() {
        Set<Consumer<Integer>> secondConsumers = secondsConsumer.getOrDefault(back, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        Set<Consumer<Long>> secondLongConsumers = secondsLongConsumer.getOrDefault(back, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        Set<Runnable> actions = secondsActions.getOrDefault(back, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        for (Consumer<Integer> consumer : secondConsumers)
            runSecondsWithThread(consumer);
        for (Consumer<Long> consumer : secondLongConsumers)
            runSecondsLongWithThread(consumer);
        for (Runnable runnable : actions)
            runTaskWithThread(runnable);
    }

    /**
     * Run a seconds task corresponding the current
     * thread configuration
     *
     * @param task the task to run
     */
    private void runSecondsWithThread(final Consumer<Integer> task) {
        if (thread) {
            (new Thread(() -> task.accept(back))).start();
        } else {
            task.accept(back);
        }
    }

    /**
     * Run a milliseconds task corresponding the current
     * thread configuration
     *
     * @param task the task to run
     */
    private void runSecondsLongWithThread(final Consumer<Long> task) {
        if (thread) {
            (new Thread(() -> task.accept(TimeUnit.SECONDS.toMillis(back)))).start();
        } else {
            task.accept(TimeUnit.SECONDS.toMillis(back));
        }
    }

    /**
     * Run a simple task corresponding the current
     * thread configuration
     *
     * @param task the task to run
     */
    private void runTaskWithThread(final Runnable task) {
        if (thread) {
            (new Thread(task)).start();
        } else {
            task.run();
        }
    }
}
