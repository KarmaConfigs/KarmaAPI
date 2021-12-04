package ml.karmaconfigs.api.common.timer.scheduler.errors;

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

import ml.karmaconfigs.api.common.timer.scheduler.SimpleScheduler;

/**
 * This exception is thrown when a timer that has been already
 * started is tried to be started again
 */
public final class TimerAlreadyStarted extends RuntimeException {

    /**
     * The scheduler
     */
    private final SimpleScheduler scheduler;

    /**
     * Initialize the exception
     *
     * @param source the scheduler
     */
    public TimerAlreadyStarted(final SimpleScheduler source) {
        super("Tried to schedule an already started scheduler with id " + source.getId());
        this.scheduler = source;
    }

    /**
     * Try to auto-fix the timer, so it can
     * be started again
     *
     * @return if the timer has been started again
     */
    public boolean tryFix() {
        try {
            this.scheduler.cancel();
            this.scheduler.start();
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    /**
     * Get the scheduler that has been tried
     * to be started
     *
     * @return the scheduler
     */
    public SimpleScheduler getScheduler() {
        return this.scheduler;
    }
}
