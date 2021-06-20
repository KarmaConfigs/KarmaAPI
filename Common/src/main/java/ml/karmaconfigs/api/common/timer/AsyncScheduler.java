package ml.karmaconfigs.api.common.timer;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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
 * Run tasks asynchronously
 */
public final class AsyncScheduler implements Serializable {

    private final static HashMap<Integer, Runnable> tasks = new HashMap<>();
    private static int completed = 0;
    private static Timer scheduler = null;

    /**
     * Initialize the AsyncScheduler
     */
    public AsyncScheduler() {
        if (scheduler == null) {
            scheduler = new Timer();

            new Thread(() -> scheduler.schedule(new TimerTask() {
                @Override
                public void run() {
                    int next = completed + 1;

                    if (tasks.containsKey(next) && tasks.get(next) != null) {
                        Runnable runnable = tasks.get(next);
                        runnable.run();

                        completed++;
                    }
                }
            }, 0, 1000)).start();
        }
    }

    /**
     * Add a task to the task list
     *
     * @param task the task
     */
    public final void addTask(Runnable task) {
        int amount = tasks.size();
        int index = amount + 1;
        tasks.put(index, task);
    }
}
