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

import ml.karmaconfigs.api.common.utils.TriConsumer;

import java.util.function.BiConsumer;

/**
 * KarmaAPI bi-object late scheduler
 *
 * @param <A> type A object
 * @param <B> type B object
 */
public interface BiLateScheduler<A, B> extends CancellableScheduler {

    /**
     * Set the complete action
     *
     * @param paramRunnable the action to perform
     *               when the scheduler is completed
     * @return this instance
     */
    BiLateScheduler<A, B> whenComplete(final Runnable paramRunnable);

    /**
     * Set the complete action
     *
     * @param paramBiConsumer the action to perform
     *               when the scheduler is completed
     * @return this instance
     */
    BiLateScheduler<A, B> whenComplete(final BiConsumer<A, B> paramBiConsumer);

    /**
     * Set the complete action
     *
     * @param paramTriConsumer the action to perform
     *               when the scheduler is completed
     * @return this instance
     */
    BiLateScheduler<A, B> whenComplete(final TriConsumer<A, B, Throwable> paramTriConsumer);

    /**
     * Get if the scheduler is completed
     *
     * @return if the scheduler is complete
     */
    boolean isCompleted();

    /**
     * Get the A object
     *
     * @return the A object
     */
    A getObject();

    /**
     * Get the B object
     *
     * @return the B object
     */
    B getSubObject();

    /**
     * Complete the scheduler
     *
     * @param paramA the type A object
     * @param paramB the type B object
     */
    void complete(final A paramA, final B paramB);

    /**
     * Complete the scheduler
     *
     * @param paramA the type A object
     * @param paramB the type B object
     * @param paramThrowable any error that has been thrown
     */
    void complete(final A paramA, final B paramB, final Throwable paramThrowable);
}
