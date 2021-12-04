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

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.timer.scheduler.SimpleScheduler;
import ml.karmaconfigs.api.common.timer.scheduler.errors.IllegalTimerAccess;
import ml.karmaconfigs.api.common.timer.scheduler.errors.TimerAlreadyStarted;
import ml.karmaconfigs.api.common.timer.scheduler.errors.TimerNotFound;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Karma simple scheduler
 */
public final class SourceSimpleTimer extends SimpleScheduler {

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
    private final long original;
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
    private final Map<Long, Set<Runnable>> periodActions;
    /**
     * A map containing second => actions to perform
     */
    private final Map<Integer, Set<Runnable>> secondsActions;
    /**
     * A map containing second => actions to perform
     */
    private final Map<Long, Set<Consumer<Integer>>> secondsConsumer;
    /**
     * A map containing second => actions to perform
     */
    private final Map<Long, Set<Consumer<Long>>> periodConsumer;

    /**
     * Actions to perform when the scheduler ends
     */
    private final Set<Runnable> onEndTasks;
    /**
     * Actions to perform when the scheduler starts
     */
    private final Set<Runnable> onStartTasks;
    /**
     * Actions to perform when the scheduler restarts
     */
    private final Set<Runnable> onRestartTasks;

    /**
     * The scheduler decrement passed time from original
     */
    private long back;
    /**
     * The scheduler period
     */
    private long period;

    /**
     * Send a warning to the console when the timer is
     * cancelled because its owner has been unloaded
     */
    private boolean noticeUnloaded;
    /**
     * If the timer is cancelled
     */
    private boolean cancel;
    /**
     * If the timer is paused
     */
    private boolean pause;
    /**
     * If the timer should restart
     */
    private boolean restart;
    /**
     * If the timer is queued for restart
     */
    private boolean temp_restart;
    /**
     * If the timer has multi-threading
     */
    private boolean thread;

    /**
     * Action to perform when the timer gets paused
     */
    private Consumer<Long> pauseAction;
    /**
     * Action to perform when the timer gets cancelled
     */
    private Consumer<Long> cancelAction;

    /**
     * Initialize the scheduler
     *
     * @param owner the scheduler owner
     * @param time the scheduler start time
     * @param autoRestart if the scheduler should auto-restart
     *                    when it ends
     */
    public SourceSimpleTimer(final KarmaSource owner, final Number time, final boolean autoRestart) {
        super(owner);
        int seconds;
        this.period = 1L;
        this.noticeUnloaded = false;
        this.cancel = false;
        this.pause = false;
        this.temp_restart = false;
        this.thread = false;
        this.periodActions = new ConcurrentHashMap<>();
        this.secondsActions = new ConcurrentHashMap<>();
        this.secondsConsumer = new ConcurrentHashMap<>();
        this.periodConsumer = new ConcurrentHashMap<>();
        this.onEndTasks = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.onStartTasks = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.onRestartTasks = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.pauseAction = null;
        this.cancelAction = null;
        this.source = owner;
        this.restart = autoRestart;
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
        this.original = (seconds + milli);
        this.back = this.original;
        this.id = getId();
        timersData.put(this.id, this);
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
    public SourceSimpleTimer(final KarmaSource owner, final int builtId) throws TimerNotFound, IllegalTimerAccess {
        super(owner);
        this.period = 1L;
        this.noticeUnloaded = false;
        this.cancel = false;
        this.pause = false;
        this.temp_restart = false;
        this.thread = false;
        this.periodActions = new ConcurrentHashMap<>();
        this.secondsActions = new ConcurrentHashMap<>();
        this.secondsConsumer = new ConcurrentHashMap<>();
        this.periodConsumer = new ConcurrentHashMap<>();
        this.onEndTasks = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.onStartTasks = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.onRestartTasks = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.pauseAction = null;
        this.cancelAction = null;
        SimpleScheduler built = timersData.getOrDefault(builtId, null);
        if (built != null) {
            if (built.getSource().isSource(owner)) {
                this.source = built.getSource();
                this.restart = built.autoRestart();
                this.original = built.getOriginalTime();
                this.back = built.getMillis();
                this.id = builtId;
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
    public SimpleScheduler noticeUnloaded(final boolean status) {
        this.noticeUnloaded = status;
        return this;
    }

    /**
     * Cancel the scheduler
     */
    @Override
    public void cancel() {
        this.cancel = true;
    }

    /**
     * Pause the scheduler
     */
    @Override
    public void pause() {
        this.pause = true;
        if (this.pauseAction != null)
            runPeriodWithThread(this.pauseAction);
    }

    /**
     * Start the scheduler
     *
     * @throws TimerAlreadyStarted if the scheduler is already started
     */
    @Override
    public void start() throws TimerAlreadyStarted {
        Set<Integer> ids = runningTimers.getOrDefault(this.source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        if (!ids.contains(this.id)) {
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    if (!SourceSimpleTimer.this.pause)
                        if (SourceSimpleTimer.this.cancel || SourceSimpleTimer.this.temp_restart) {
                            if (!SourceSimpleTimer.this.temp_restart) {
                                SourceSimpleTimer.timersData.remove(SourceSimpleTimer.this.id);
                                Set<Integer> ids = SourceSimpleTimer.runningTimers.getOrDefault(SourceSimpleTimer.this.source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                                ids.remove(SourceSimpleTimer.this.id);
                                SourceSimpleTimer.runningTimers.put(SourceSimpleTimer.this.source, ids);
                                if (SourceSimpleTimer.this.cancelAction != null)
                                    SourceSimpleTimer.this.runPeriodWithThread(SourceSimpleTimer.this.cancelAction);
                                SourceSimpleTimer.this.cancel = false;
                                SourceSimpleTimer.this.pause = false;
                                SourceSimpleTimer.this.temp_restart = false;
                                timer.cancel();
                            } else {
                                SourceSimpleTimer.this.back = SourceSimpleTimer.this.original;
                                SourceSimpleTimer.this.onRestartTasks.forEach(x$0 -> runTaskWithThread(x$0));
                                SourceSimpleTimer.this.temp_restart = false;
                            }
                        } else {
                            SourceSimpleTimer.this.executeTasks();
                            if (SourceSimpleTimer.this.back > 0L) {
                                SourceSimpleTimer.this.back--;
                            } else {
                                SourceSimpleTimer.this.back = SourceSimpleTimer.this.original;
                                if (SourceSimpleTimer.this.restart) {
                                    SourceSimpleTimer.this.onRestartTasks.forEach(x$0 -> runTaskWithThread(x$0));
                                    SourceSimpleTimer.this.back = SourceSimpleTimer.this.original;
                                } else {
                                    SourceSimpleTimer.this.onEndTasks.forEach(x$0 -> runTaskWithThread(x$0));
                                    SourceSimpleTimer.timersData.remove(SourceSimpleTimer.this.id);
                                    Set<Integer> ids = SourceSimpleTimer.runningTimers.getOrDefault(SourceSimpleTimer.this.source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
                                    ids.remove(SourceSimpleTimer.this.id);
                                    SourceSimpleTimer.runningTimers.put(SourceSimpleTimer.this.source, ids);
                                    SourceSimpleTimer.this.cancel = false;
                                    SourceSimpleTimer.this.pause = false;
                                    SourceSimpleTimer.this.temp_restart = false;
                                    timer.cancel();
                                }
                            }
                        }
                }
            }, 0L, this.period);
        } else {
            throw new TimerAlreadyStarted(this);
        }
    }

    /**
     * Restart the scheduler
     */
    @Override
    public void restart() {
        this.temp_restart = true;
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
        this.restart = status;
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
        this.period = (seconds + milli);
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
        this.thread = status;
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
        Set<Runnable> actions = this.secondsActions.getOrDefault(time, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        actions.add(task);
        this.secondsActions.put(time, actions);
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
        Set<Runnable> actions = this.periodActions.getOrDefault(time, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        actions.add(task);
        this.periodActions.put(time, actions);
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
        long second = TimeUnit.MILLISECONDS.toSeconds(this.original);
        while (second >= 0L) {
            final long millis = TimeUnit.SECONDS.toMillis(second--);
            final Set<Consumer<Integer>> actions = this.secondsConsumer.getOrDefault(millis, Collections.newSetFromMap(new ConcurrentHashMap<Consumer<Integer>, Boolean>()));
            actions.add(action);
            this.secondsConsumer.put(millis, actions);
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
        long milli = this.original;
        while (milli >= 0L) {
            final Set<Consumer<Long>> actions = this.periodConsumer.getOrDefault(milli--, Collections.newSetFromMap(new ConcurrentHashMap<Consumer<Long>, Boolean>()));
            actions.add(action);
            this.periodConsumer.put(milli, actions);
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
        this.cancelAction = action;
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
        this.pauseAction = action;
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
        this.onStartTasks.add(task);
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
        this.onEndTasks.add(task);
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
        this.onRestartTasks.add(task);
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
        switch (condition) {
            case EQUALS: {
                final Set<Consumer<Integer>> actions = this.secondsConsumer.getOrDefault(TimeUnit.SECONDS.toMillis(condition_value), Collections.newSetFromMap(new ConcurrentHashMap<Consumer<Integer>, Boolean>()));
                actions.add(action);
                this.secondsConsumer.put(TimeUnit.SECONDS.toMillis(condition_value), actions);
                break;
            }
            case OVER_OF: {
                long c_over_val = condition_value;
                while (c_over_val <= this.original) {
                    final Set<Consumer<Integer>> actions = this.secondsConsumer.getOrDefault(TimeUnit.SECONDS.toMillis(c_over_val++), Collections.newSetFromMap(new ConcurrentHashMap<Consumer<Integer>, Boolean>()));
                    actions.add(action);
                    this.secondsConsumer.put(c_over_val, actions);
                }
                break;
            }
            case MINUS_TO: {
                long c_minus_val = condition_value;
                while (c_minus_val >= 0L) {
                    final Set<Consumer<Integer>> actions = this.secondsConsumer.getOrDefault(TimeUnit.SECONDS.toMillis(c_minus_val--), Collections.newSetFromMap(new ConcurrentHashMap<Consumer<Integer>, Boolean>()));
                    actions.add(action);
                    this.secondsConsumer.put(c_minus_val, actions);
                }
                break;
            }
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
        switch (condition) {
            case EQUALS: {
                final Set<Consumer<Long>> actions = this.periodConsumer.getOrDefault(condition_value, Collections.newSetFromMap(new ConcurrentHashMap<Consumer<Long>, Boolean>()));
                actions.add(action);
                this.periodConsumer.put(TimeUnit.SECONDS.toMillis(condition_value), actions);
                break;
            }
            case OVER_OF: {
                long c_over_val = condition_value;
                while (c_over_val <= this.original) {
                    final Set<Consumer<Long>> actions = this.periodConsumer.getOrDefault(c_over_val++, Collections.newSetFromMap(new ConcurrentHashMap<Consumer<Long>, Boolean>()));
                    actions.add(action);
                    this.periodConsumer.put(c_over_val, actions);
                }
                break;
            }
            case MINUS_TO: {
                long c_minus_val = condition_value;
                while (c_minus_val >= 0L) {
                    final Set<Consumer<Long>> actions = this.periodConsumer.getOrDefault(c_minus_val--, Collections.newSetFromMap(new ConcurrentHashMap<Consumer<Long>, Boolean>()));
                    actions.add(action);
                    this.periodConsumer.put(c_minus_val, actions);
                }
                break;
            }
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
        return this.cancel;
    }

    /**
     * Get if the timer is running
     *
     * @return if the timer is running
     */
    @Override
    public boolean isRunning() {
        Set<Integer> ids = runningTimers.getOrDefault(this.source, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        return ids.contains(this.id);
    }

    /**
     * Get if the timer is paused
     *
     * @return if the timer is paused
     */
    @Override
    public boolean isPaused() {
        return this.pause;
    }

    /**
     * Get if the timer auto restarts
     *
     * @return if the timer starts the timer
     * automatically when it ends
     */
    @Override
    public boolean autoRestart() {
        return this.restart;
    }

    /**
     * Get if the timer has multi-threading enabled
     *
     * @return if the timer runs on another thread
     */
    @Override
    public boolean isMultiThreading() {
        return this.thread;
    }

    /**
     * Get the timer start time
     *
     * @return the timer start time
     */
    @Override
    public long getOriginalTime() {
        return this.original;
    }

    /**
     * Get the timer configured period
     *
     * @return the timer update period
     */
    @Override
    public long getPeriod() {
        return this.period;
    }

    /**
     * Get the timer milliseconds
     *
     * @return the timer exact time
     */
    @Override
    public long getMillis() {
        return this.back;
    }

    /**
     * Execute the tasks corresponding to the current
     * second/millisecond
     */
    private void executeTasks() {
        Set<Consumer<Long>> periodConsumers = this.periodConsumer.getOrDefault(this.back, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        Set<Consumer<Integer>> secondConsumers = this.secondsConsumer.getOrDefault(this.back, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        Set<Runnable> runnables = this.periodActions.getOrDefault(this.back, Collections.newSetFromMap(new ConcurrentHashMap<>()));
        Set<Runnable> secondRunnable = this.secondsActions.getOrDefault((int) TimeUnit.MILLISECONDS.toSeconds(this.back), Collections.newSetFromMap(new ConcurrentHashMap<>()));
        runnables.addAll(secondRunnable);
        for (Consumer<Long> consumer : periodConsumers)
            runPeriodWithThread(consumer);
        for (Consumer<Integer> consumer : secondConsumers)
            runSecondsWithThread(consumer);
        for (Runnable runnable : runnables)
            runTaskWithThread(runnable);
    }

    /**
     * Run a seconds task corresponding the current
     * thread configuration
     *
     * @param task the task to run
     */
    private void runSecondsWithThread(final Consumer<Integer> task) {
        if (this.thread) {
            (new Thread(() -> task.accept((int) TimeUnit.MILLISECONDS.toSeconds(this.back)))).start();
        } else {
            task.accept((int) TimeUnit.MILLISECONDS.toSeconds(this.back));
        }
    }

    /**
     * Run a milliseconds task corresponding the current
     * thread configuration
     *
     * @param task the task to run
     */
    private void runPeriodWithThread(final Consumer<Long> task) {
        if (this.thread) {
            (new Thread(() -> task.accept(this.back))).start();
        } else {
            task.accept(this.back);
        }
    }

    /**
     * Run a simple task corresponding the current
     * thread configuration
     *
     * @param task the task to run
     */
    private void runTaskWithThread(final Runnable task) {
        if (this.thread) {
            (new Thread(task)).start();
        } else {
            task.run();
        }
    }
}
