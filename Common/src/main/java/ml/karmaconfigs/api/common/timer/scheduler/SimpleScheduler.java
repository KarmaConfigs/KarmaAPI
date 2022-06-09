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

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.karma.file.KarmaConfig;
import ml.karmaconfigs.api.common.timer.SchedulerUnit;
import ml.karmaconfigs.api.common.timer.TimeCondition;
import ml.karmaconfigs.api.common.timer.scheduler.errors.TimerAlreadyStarted;
import ml.karmaconfigs.api.common.utils.enums.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static ml.karmaconfigs.api.common.karma.KarmaAPI.source;

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
     * The scheduler working unit
     */
    protected SchedulerUnit working_unit;

    /**
     * Initialize the scheduler
     *
     * @param owner the scheduler source
     * @param workingUnit the scheduler working unit
     */
    public SimpleScheduler(final KarmaSource owner, final SchedulerUnit workingUnit) {
        this.source = owner;
        this.id = ++global_id;
        id_instance.put(this.id, this);
        working_unit = workingUnit;
    }

    /**
     * Cancel the scheduler
     *
     * @param owner the scheduler source
     */
    public static void cancelFor(final KarmaSource owner) {
        KarmaConfig config = new KarmaConfig();

        for (int id : id_instance.keySet()) {
            SimpleScheduler scheduler = id_instance.getOrDefault(id, null);
            if (scheduler != null && scheduler.source == null) {
                if (config.debug(Level.GRAVE)) {
                    source(true).console().send("Cancelling timer with id {0} because its source is not valid", Level.GRAVE, id);
                }

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
     * the specified time
     *
     * Please note; the consumer will be always second if the working unit is second or over.
     * It's impossible to retrieve change action as ms when using seconds or over
     *
     * @param paramInt the time
     * @param paramRunnable the action to perform
     * @return this instance
     */
    public abstract SimpleScheduler exactAction(final long paramInt, final Runnable paramRunnable);

    /**
     * Add an action when the timer passes a time
     *
     * Please note; the consumer will be always second if the working unit is second or over.
     * It's impossible to retrieve change action as ms when using seconds or over. For more specific triggers
     * take a look to {@link SimpleScheduler#changeSpecificAction(Consumer, SchedulerUnit)} (Consumer, SchedulerUnit)}
     *
     * @param paramConsumer the action to perform
     * @return this instance
     */
    public abstract SimpleScheduler changeAction(final Consumer<Long> paramConsumer);

    /**
     * Add an action when the timer passes a time
     *
     * This will add a specific action when a specific time unit changes. This won't work with milliseconds
     * and or seconds, only with minutes, hours and days.
     *
     * @param paramConsumer the action to perform
     * @param paramUnit the time unit
     * @return this instance
     */
    public abstract SimpleScheduler changeSpecificAction(final Consumer<Integer> paramConsumer, SchedulerUnit paramUnit);

    /**
     * Add an action to perform when the timer reaches
     * the specified second
     *
     * @param paramInt the second
     * @param paramRunnable the action to perform
     * @return this instance
     * @deprecated Use {@link SimpleScheduler#exactAction(long, Runnable)} instead
     */
    @Deprecated
    public abstract SimpleScheduler exactSecondPeriodAction(final int paramInt, final Runnable paramRunnable);

    /**
     * Add an action to perform when the timer reaches
     * the specified millisecond
     *
     * @param paramLong the millisecond
     * @param paramRunnable the action to perform
     * @return this instance
     * @deprecated Use {@link SimpleScheduler#exactAction(long, Runnable)} instead
     */
    @Deprecated
    public abstract SimpleScheduler exactPeriodAction(final long paramLong, final Runnable paramRunnable);

    /**
     * Add an action when the timer passes a second
     *
     * @param paramConsumer the action to perform
     * @return this instance
     * @deprecated Use {@link SimpleScheduler#changeAction(Consumer)} instead
     */
    @Deprecated
    public abstract SimpleScheduler secondChangeAction(final Consumer<Integer> paramConsumer);

    /**
     * Add an action when the timer passes a millisecond
     *
     * @param paramConsumer the action to perform
     * @return this instance
     * @deprecated Use {@link SimpleScheduler#changeAction(Consumer)} instead
     */
    @Deprecated
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
     * @param paramInt the time
     * @param paramConsumer the action to perform
     * @return this instance
     */
    public abstract SimpleScheduler condition(final TimeCondition paramTimeCondition, final long paramInt, final Consumer<Long> paramConsumer);

    /**
     * Add a conditional action
     *
     * @param paramTimeCondition the condition that the timer
     *                           must complete
     * @param paramInt the timer second
     * @param paramConsumer the action to perform
     * @return this instance
     * @deprecated Use {@link SimpleScheduler#condition(TimeCondition, long, Consumer)} instead
     */
    @Deprecated
    public abstract SimpleScheduler conditionalAction(final TimeCondition paramTimeCondition, final int paramInt, final Consumer<Integer> paramConsumer);

    /**
     * Add a conditional action
     *
     * @param paramTimeCondition the condition that the timer must complete
     * @param paramLong the timer millisecond
     * @param paramConsumer the action to perform
     * @return this instance
     * @deprecated Use {@link SimpleScheduler#condition(TimeCondition, long, Consumer)} instead
     */
    @Deprecated
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
    public final long getTime(final SchedulerUnit unit) {
        long time = getMillis();
        return unit.toJavaUnit().convert(time, TimeUnit.MILLISECONDS);
    }

    /**
     * Format the current timer time
     *
     * @param unit the time unit
     * @param name the unit name
     * @return the formatted timer time
     */
    public final String format(final SchedulerUnit unit, final String name) {
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
        long seconds = getTime(SchedulerUnit.SECOND);
        long minutes = getTime(SchedulerUnit.MINUTE);
        long hours = getTime(SchedulerUnit.HOUR);
        long days = getTime(SchedulerUnit.DAY);

        StringBuilder builder = new StringBuilder();
        if (days > 0L)
            builder.append(days).append(" day(s) ");
        if (hours > 0L)
            if (hours > 24L) {
                builder.append(Math.abs(days - hours)).append(" hour(s) ");
            } else {
                builder.append(hours).append("hour(s)");
            }
        if (minutes > 0L)
            if (minutes > 59L) {
                builder.append(Math.abs((days - hours) - minutes)).append(" min(s) ");
            } else {
                builder.append(minutes).append(" min(s) ");
            }
        if (seconds > 0L)
            if (seconds > 59L) {
                builder.append(Math.abs((days - hours) - (minutes - seconds))).append(" sec(s) ");
            } else {
                builder.append(seconds).append(" sec(s) ");
            }
        if (millis || builder.length() <= 0)
            builder.append(milliseconds).append(" ms");

        return builder.toString();
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
