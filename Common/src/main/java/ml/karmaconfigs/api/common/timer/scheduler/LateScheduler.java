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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * KarmaAPI single-object late scheduler
 *
 * @param <A> type A object
 */
public interface LateScheduler<A> extends CancellableScheduler {

    /**
     * Set the complete action
     *
     * @param paramRunnable the action to perform
     *               when the scheduler is completed
     * @return this instance
     */
    LateScheduler<A> whenComplete(final Runnable paramRunnable);

    /**
     * Set the complete action
     *
     * @param paramConsumer the action to perform
     *               when the scheduler is completed
     * @return this instance
     */
    LateScheduler<A> whenComplete(final Consumer<A> paramConsumer);

    /**
     * Set the complete action
     *
     * @param paramBiConsumer the action to perform
     *               when the scheduler is completed
     * @return this instance
     */
    LateScheduler<A> whenComplete(final BiConsumer<A, Throwable> paramBiConsumer);

    /**
     * Get if the scheduler is completed
     *
     * @return if the shcheduler is complete
     */
    boolean isCompleted();

    /**
     * Get the A object
     *
     * @return the A object
     */
    A get();

    /**
     * Complete the scheduler
     *
     * @param paramA the type A object
     */
    void complete(final A paramA);

    /**
     * Complete the scheduler
     *
     * @param paramA the type A object
     * @param paramThrowable any error that has been thrown
     */
    void complete(final A paramA, final Throwable paramThrowable);

    /**
     * Complete another single consumer with these
     * objects
     *
     * @param next the other single consumer
     * @return the other bi consumer
     */
    default LateScheduler<A> thenCompleteUnder(final LateScheduler<A> next) {
        next.complete(get());
        return next;
    }
}
