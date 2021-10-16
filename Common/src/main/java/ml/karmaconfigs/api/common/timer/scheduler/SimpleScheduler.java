package ml.karmaconfigs.api.common.timer.scheduler;

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

import ml.karmaconfigs.api.common.karma.APISource;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.timer.TimeCondition;
import ml.karmaconfigs.api.common.timer.scheduler.errors.TimerAlreadyStarted;

import javax.swing.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Karma simple scheduler
 */
public abstract class SimpleScheduler {

    /**
     * The id/scheduler map to obtain a scheduler from its ID
     */
    private static final Map<Integer, SimpleScheduler> id_instance = new ConcurrentHashMap<>();

    /**
     * The generated IDs, being the latest generated this value
     */
    private static int global_id = 0;

    /**
     * The scheduler source
     */
    private final KarmaSource source;

    /**
     * The scheduler ID
     */
    private final int id;

    /**
     * Initialize the scheduler
     *
     * @param owner the scheduler source
     */
    public SimpleScheduler(final KarmaSource owner) {
        this.source = owner;
        this.id = ++global_id;
        id_instance.put(this.id, this);
    }

    /**
     * Cancel the scheduler
     *
     * @param owner the scheduler source
     */
    public static void cancelFor(final KarmaSource owner) {
        for (int id : id_instance.keySet()) {
            SimpleScheduler scheduler = id_instance.getOrDefault(id, null);
            if (scheduler != null && scheduler.source == null) {
                APISource.getConsole().send("&cCancelling timer with id {0} because its source is not valid");
                scheduler.cancel();
                continue;
            }
            if (scheduler != null &&
                    scheduler.source.isSource(owner)) {
                scheduler.cancel();
                id_instance.remove(id);
            }
        }
    }

    /**
     * Cancel the scheduler
     */
    public abstract void cancel();

    /**
     * Pause the scheduler
     */
    public abstract void pause();

    /**
     * Start the scheduler
     *
     * @throws TimerAlreadyStarted if the scheduler is already started
     */
    public abstract void start() throws TimerAlreadyStarted;

    /**
     * Restart the scheduler
     */
    public abstract void restart();

    /**
     * Set if the timer should auto restart
     * when it ends
     *
     * @param paramBoolean if the timer should auto restart
     * @return this instance
     */
    public abstract SimpleScheduler updateAutoRestart(final boolean paramBoolean);

    /**
     * Set the timer update period
     *
     * @param paramNumber the period
     * @return this instance
     */
    public abstract SimpleScheduler withPeriod(final Number paramNumber);

    /**
     * Set if the timer runs on another thread
     *
     * @param paramBoolean if the timer has multi-threading
     * @return this instance
     */
    public abstract SimpleScheduler multiThreading(final boolean paramBoolean);

    /**
     * Add an action to perform when the timer reaches
     * the specified second
     *
     * @param paramInt the second
     * @param paramRunnable the action to perform
     * @return this instance
     */
    public abstract SimpleScheduler exactSecondPeriodAction(final int paramInt, final Runnable paramRunnable);

    /**
     * Add an action to perform when the timer reaches
     * the specified millisecond
     *
     * @param paramLong the millisecond
     * @param paramRunnable the action to perform
     * @return this instance
     */
    public abstract SimpleScheduler exactPeriodAction(final long paramLong, final Runnable paramRunnable);

    /**
     * Add an action when the timer passes a second
     *
     * @param paramConsumer the action to perform
     * @return this instance
     */
    public abstract SimpleScheduler secondChangeAction(final Consumer<Integer> paramConsumer);

    /**
     * Add an action when the timer passes a millisecond
     *
     * @param paramConsumer the action to perform
     * @return this instance
     */
    public abstract SimpleScheduler periodChangeAction(final Consumer<Long> paramConsumer);

    /**
     * Set the action to perform when the timer is cancelled
     *
     * @param paramConsumer the action to perform
     * @return this instance
     */
    public abstract SimpleScheduler cancelAction(final Consumer<Long> paramConsumer);

    /**
     * Set the action to perform when the timer is paused
     *
     * @param paramConsumer the action to perform
     * @return this instance
     */
    public abstract SimpleScheduler pauseAction(final Consumer<Long> paramConsumer);

    /**
     * Set the action to perform when the timer is started
     *
     * @param paramRunnable the action to perform
     * @return this instance
     */
    public abstract SimpleScheduler startAction(final Runnable paramRunnable);

    /**
     * Set the action to perform when the timer is
     * completely ended
     *
     * @param paramRunnable the action to perform
     * @return this instance
     */
    public abstract SimpleScheduler endAction(final Runnable paramRunnable);

    /**
     * Set the action to perform when the timer is restarted
     *
     * @param paramRunnable the action to perform
     * @return this instance
     */
    public abstract SimpleScheduler restartAction(final Runnable paramRunnable);

    /**
     * Add a conditional action
     *
     * @param paramTimeCondition the condition that the timer
     *                           must complete
     * @param paramInt the timer second
     * @param paramConsumer the action to perform
     * @return this instance
     */
    public abstract SimpleScheduler conditionalAction(final TimeCondition paramTimeCondition, final int paramInt, final Consumer<Integer> paramConsumer);

    /**
     * Add a conditional action
     *
     * @param paramTimeCondition the condition that the timer must complete
     * @param paramLong the timer millisecond
     * @param paramConsumer the action to perform
     * @return this instance
     */
    public abstract SimpleScheduler conditionalPeriodAction(final TimeCondition paramTimeCondition, final long paramLong, final Consumer<Long> paramConsumer);

    /**
     * Get if the timer is cancelled
     *
     * @return if the timer is cancelled
     */
    public abstract boolean isCancelled();

    /**
     * Get if the timer is running
     *
     * @return if the timer is running
     */
    public abstract boolean isRunning();

    /**
     * Get if the timer is paused
     *
     * @return if the timer is paused
     */
    public abstract boolean isPaused();

    /**
     * Get if the timer auto restarts
     *
     * @return if the timer starts the timer
     * automatically when it ends
     */
    public abstract boolean autoRestart();

    /**
     * Get if the timer has multi-threading enabled
     *
     * @return if the timer runs on another thread
     */
    public abstract boolean isMultiThreading();

    /**
     * Get the timer start time
     *
     * @return the timer start time
     */
    public abstract long getOriginalTime();

    /**
     * Get the timer configured period
     *
     * @return the timer update period
     */
    public abstract long getPeriod();

    /**
     * Get the timer milliseconds
     *
     * @return the timer exact time
     */
    public abstract long getMillis();

    /**
     * Get the timer time under the specified
     * time unit
     *
     * @param unit the time unit
     * @return the timer time in the specified time unit if possible
     */
    public final long getTime(final TimeUnit unit) {
        long time = getMillis();
        switch (unit) {
            case NANOSECONDS:
                return TimeUnit.MILLISECONDS.toNanos(time);
            case MICROSECONDS:
                return TimeUnit.MILLISECONDS.toMicros(time);
            case SECONDS:
                return TimeUnit.MILLISECONDS.toSeconds(time);
            case MINUTES:
                return TimeUnit.MILLISECONDS.toMinutes(time);
            case HOURS:
                return TimeUnit.MILLISECONDS.toHours(time);
            case DAYS:
                return TimeUnit.MILLISECONDS.toDays(time);
        }
        return time;
    }

    /**
     * Format the current timer time
     *
     * @param unit the time unit
     * @param name the unit name
     * @return the formatted timer time
     */
    public final String format(final TimeUnit unit, final String name) {
        return getTime(unit) + " " + name;
    }

    /**
     * Get the timer time left to be finished
     *
     * @param millis include the milliseconds on
     *               the format
     * @return the time left format
     */
    public final String timeLeft(final boolean millis) {
        long milliseconds = getMillis();
        long seconds = getTime(TimeUnit.SECONDS);
        long minutes = getTime(TimeUnit.MINUTES);
        long hours = getTime(TimeUnit.HOURS);
        StringBuilder builder = new StringBuilder();
        if (hours > 0L)
            builder.append(hours).append(" hour(s) ");
        if (minutes > 0L)
            if (minutes > 59L) {
                builder.append(Math.abs(hours - minutes)).append(" min(s) ");
            } else {
                builder.append(minutes).append(" min(s) ");
            }
        if (seconds > 0L)
            if (seconds > 59L) {
                builder.append(Math.abs(hours - minutes - seconds)).append(" sec(s) ");
            } else {
                builder.append(seconds).append(" sec(s) ");
            }
        if (millis || builder.length() <= 0)
            builder.append(milliseconds).append(" ms");
        return builder.toString();
    }

    /**
     * Request a synchronous task
     *
     * @param action the action to perform
     * @deprecated Use {@link APISource#syncScheduler()}
     */
    @Deprecated
    public void requestSync(final Runnable action) {
        try {
            SwingUtilities.invokeAndWait(action);
        } catch (Throwable ignored) {
        }
    }

    /**
     * Request an asynchronous task
     *
     * @param action the action to perform
     * @deprecated Use {@link APISource#asyncScheduler()}
     */
    @Deprecated
    public void requestAsync(final Runnable action) {
        SwingUtilities.invokeLater(action);
    }

    /**
     * Get the scheduler source
     *
     * @return the scheduler source
     */
    public final KarmaSource getSource() {
        return this.source;
    }

    /**
     * Get the scheduler ID
     *
     * @return the scheduler ID
     */
    public final int getId() {
        return this.id;
    }
}
