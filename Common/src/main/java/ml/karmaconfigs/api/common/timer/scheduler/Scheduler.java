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

import java.util.function.Consumer;

/**
 * Karma scheduler
 */
public abstract class Scheduler {

    /**
     * Action to perform when a task has been
     * started
     *
     * @param paramConsumer the action to perform
     */
    public abstract void onTaskStart(final Consumer<Integer> paramConsumer);

    /**
     * Action to perform when a task has been
     * completed
     *
     * @param paramConsumer the action to perform
     */
    public abstract void onTaskComplete(final Consumer<Integer> paramConsumer);

    /**
     * Queue another task to the scheduler
     *
     * @param paramRunnable the task to perform
     * @return the task id
     */
    public abstract int queue(final Runnable paramRunnable);

    /**
     * Get the current task id
     *
     * @return the current task id
     */
    public abstract int currentTask();
}
