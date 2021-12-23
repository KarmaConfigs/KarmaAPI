package ml.karmaconfigs.api.common.timer.scheduler.worker;

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

import ml.karmaconfigs.api.common.timer.scheduler.CancellableScheduler;
import ml.karmaconfigs.api.common.timer.scheduler.LateScheduler;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static ml.karmaconfigs.api.common.karma.KarmaAPI.source;

/**
 * This scheduler will run a task when X is completed
 *
 * @param <A> the A object consumer
 */
public final class AsyncLateScheduler<A> implements LateScheduler<A> {

    /**
     * Basic when complete action
     */
    private Runnable whenCompleteRunner;
    /**
     * When cancel action
     */
    private Runnable onCancel;

    /**
     * When complete action
     */
    private Consumer<A> whenComplete;

    /**
     * When complete action with error
     */
    private BiConsumer<A, Throwable> whenCompleteWithError;

    /**
     * If the scheduler is cancelled
     */
    private boolean cancelled = false;
    /**
     * If the scheduler is completed
     */
    private boolean completed = false;


    /**
     * The scheduler type A object
     */
    private A typeA = null;

    /**
     * Set the complete action
     *
     * @param action the action to perform
     *               when the scheduler is completed
     * @return this instance
     */
    @Override
    public LateScheduler<A> whenComplete(final Runnable action) {
        if (!this.cancelled && !this.completed)
            this.whenCompleteRunner = action;
        return this;
    }

    /**
     * Set the complete action
     *
     * @param action the action to perform
     *               when the scheduler is completed
     * @return this instance
     */
    @Override
    public LateScheduler<A> whenComplete(final Consumer<A> action) {
        if (!this.cancelled && !this.completed)
            this.whenComplete = action;
        return this;
    }

    /**
     * Set the complete action
     *
     * @param caughtAction the action to perform
     *                     when the scheduler is completed
     * @return this instance
     */
    @Override
    public LateScheduler<A> whenComplete(final BiConsumer<A, Throwable> caughtAction) {
        if (!this.cancelled && !this.completed)
            this.whenCompleteWithError = caughtAction;
        return this;
    }

    /**
     * Get if the scheduler is completed
     *
     * @return if the scheduler is complete
     */
    @Override
    public boolean isCompleted() {
        return this.completed;
    }

    /**
     * Get the A object
     *
     * @return the A object
     */
    @Override
    public A get() {
        return typeA;
    }

    /**
     * Complete the scheduler
     *
     * @param target the type A object
     */
    @Override
    public void complete(final A target) {
        if (this.cancelled || this.completed) {
            return;
        }
        try {
            source(true).async().queue(() -> {
                if (this.whenComplete != null) {
                    this.whenComplete.accept(target);
                }
                if (this.whenCompleteWithError != null) {
                    this.whenCompleteWithError.accept(target, null);
                }
                if (this.whenCompleteRunner != null) {
                    this.whenCompleteRunner.run();
                }

                typeA = target;
            });
            this.completed = true;
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Complete the scheduler
     *
     * @param target the type A object
     * @param error any error that has been thrown
     */
    @Override
    public void complete(final A target, final Throwable error) {
        if (this.cancelled || this.completed) {
            return;
        }
        try {
            source(true).async().queue(() -> {
                if (this.whenCompleteWithError != null)
                    this.whenCompleteWithError.accept(target, error);
                if (this.whenComplete != null)
                    this.whenComplete.accept(target);
                if (this.whenCompleteRunner != null)
                    this.whenCompleteRunner.run();

                typeA = target;
            });
            this.completed = true;
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Set the cancelled action
     *
     * @param action the action to perform
     *               when the timer is cancelled
     * @return a cancellable instance of this
     */
    @Override
    public CancellableScheduler whenCancelled(Runnable action) {
        this.onCancel = action;
        return this;
    }

    /**
     * Get if the scheduler is cancelled
     *
     * @return if the scheduler is cancelled
     */
    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    /**
     * Cancel the scheduler
     */
    @Override
    public void setCancelled() {
        this.cancelled = true;
        if (this.onCancel != null)
            this.onCancel.run();
    }
}
