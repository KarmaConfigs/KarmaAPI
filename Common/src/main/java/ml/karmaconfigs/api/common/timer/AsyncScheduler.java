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
import ml.karmaconfigs.api.common.timer.scheduler.Scheduler;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Karma async scheduler
 */
public final class AsyncScheduler extends Scheduler implements Serializable {

    /**
     * A map of source/scheduler to fetch scheduler instead
     * of creating tons of them
     */
    private static final Map<KarmaSource, Scheduler> schedulers = new ConcurrentHashMap<>();

    /**
     * The tasks of the scheduler
     */
    private final Map<Integer, Runnable> tasks = new HashMap<>();

    /**
     * The scheduler timer
     */
    private final Timer scheduler;

    /**
     * When a task is started
     */
    private Consumer<Integer> start = null;
    /**
     * When a task is completed
     */
    private Consumer<Integer> complete = null;

    /**
     * The current task
     */
    private int current = 0;
    /**
     * Completed tasks
     */
    private int completed = 0;

    /**
     * Initialize the scheduler
     *
     * @param source the scheduler owner
     */
    public AsyncScheduler(final KarmaSource source) {
        AsyncScheduler async = (AsyncScheduler) schedulers.getOrDefault(source, null);
        if (async != null) {
            this.tasks.putAll(async.tasks);
            this.scheduler = async.scheduler;
            this.start = async.start;
            this.complete = async.complete;
            this.current = async.current;
            this.completed = async.completed;
        } else {
            this.scheduler = new Timer();
            this.scheduler.schedule(new TimerTask() {
                public void run() {
                    int next = AsyncScheduler.this.completed + 1;
                    if (AsyncScheduler.this.tasks.containsKey(next) && AsyncScheduler.this.tasks.get(next) != null) {
                        AsyncScheduler.this.current = next;
                        if (AsyncScheduler.this.start != null)
                            AsyncScheduler.this.start.accept(AsyncScheduler.this.current);
                        (new Thread(AsyncScheduler.this.tasks.get(next))).start();
                        AsyncScheduler.this.completed++;
                        if (AsyncScheduler.this.start != null)
                            AsyncScheduler.this.complete.accept(AsyncScheduler.this.current);
                    }
                }
            }, 0L, 250);
            schedulers.put(source, this);
        }
    }

    /**
     * Action to perform when a task has been
     * started
     *
     * @param taskId the action to perform
     */
    @Override
    public void onTaskStart(final Consumer<Integer> taskId) {
        this.start = taskId;
    }

    /**
     * Action to perform when a task has been
     * completed
     *
     * @param taskId the action to perform
     */
    @Override
    public void onTaskComplete(final Consumer<Integer> taskId) {
        this.complete = taskId;
    }

    /**
     * Queue another task to the scheduler
     *
     * @param task the task to perform
     * @return the task id
     */
    @Override
    public int queue(final Runnable task) {
        int amount = this.tasks.size();
        int index = amount + 1;
        this.tasks.put(index, task);
        return index;
    }

    /**
     * Get the current task id
     *
     * @return the current task id
     */
    @Override
    public int currentTask() {
        return this.current;
    }
}
