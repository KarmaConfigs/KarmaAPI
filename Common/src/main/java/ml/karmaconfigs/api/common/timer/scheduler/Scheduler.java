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

import ml.karmaconfigs.api.common.timer.worker.ScheduledTask;
import ml.karmaconfigs.api.common.timer.worker.event.TaskListener;

import java.util.function.Consumer;

/**
 * Karma scheduler
 */
public abstract class Scheduler {

    /**
     * Add a task listener
     *
     * @param listener the task listener
     */
    public abstract void addTaskListener(final TaskListener listener);

    /**
     * Remove a task listener
     *
     * @param listener the listener to remove
     */
    public abstract void removeTaskListener(final TaskListener listener);

    /**
     * Action to perform when a task has been
     * started
     *
     * @param paramConsumer the action to perform
     * @deprecated Use {@link Scheduler#addTaskListener(TaskListener)} instead
     */
    @Deprecated
    public abstract void onTaskStart(final Consumer<Integer> paramConsumer);

    /**
     * Action to perform when a task has been
     * completed
     *
     * @param paramConsumer the action to perform
     * @deprecated Use {@link Scheduler#addTaskListener(TaskListener)} instead
     */
    @Deprecated
    public abstract void onTaskComplete(final Consumer<Integer> paramConsumer);

    /**
     * Queue another task to the scheduler
     *
     * @param paramRunnable the task to perform
     * @return the task id
     * @deprecated Use {@link Scheduler#queue(String, Runnable)} instead
     */
    @Deprecated
    public abstract int queue(final Runnable paramRunnable);

    /**
     * Queue another task to the scheduler
     *
     * @param name the task name
     * @param paramRunnable the task to perform
     */
    public abstract void queue(final String name, final Runnable paramRunnable);

    /**
     * Get a scheduled task by name
     *
     * @param name the task name
     * @return the task
     */
    public abstract ScheduledTask[] getByName(final String name);

    /**
     * Get a scheduled task by id
     *
     * @param id the task id
     * @return the task
     */
    public abstract ScheduledTask getById(final int id);

    /**
     * Get the current task id
     *
     * @return the current task id
     */
    public abstract int currentTask();
}
