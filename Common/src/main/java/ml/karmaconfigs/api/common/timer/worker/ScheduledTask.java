package ml.karmaconfigs.api.common.timer.worker;

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
 * Scheduled task
 */
public final class ScheduledTask {

    private final String name;
    private final Runnable task;
    private final int task_id;

    /**
     * Initialize the scheduled task
     *
     * @param nm the task name
     * @param tsk the task to run
     * @param id the task id
     */
    ScheduledTask(final String nm, final Runnable tsk, final int id) {
        name = nm;
        task = tsk;
        task_id = id;
    }

    /**
     * Get the task name
     *
     * @return the task name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the task to run
     *
     * @return the task to run
     */
    public Runnable getTask() {
        return task;
    }

    /**
     * Get the task id
     *
     * @return the task id
     */
    public int getId() {
        return task_id;
    }
}
