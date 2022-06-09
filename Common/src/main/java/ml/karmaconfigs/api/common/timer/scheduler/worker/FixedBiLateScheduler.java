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

import ml.karmaconfigs.api.common.timer.scheduler.BiLateScheduler;
import ml.karmaconfigs.api.common.timer.scheduler.CancellableScheduler;
import ml.karmaconfigs.api.common.utils.TriConsumer;

import java.util.function.BiConsumer;

/**
 * This scheduler will run a task when X is completed
 *
 * @param <A> the A object consumer
 * @param <B> the B object consumer
 */
public final class FixedBiLateScheduler<A, B> implements BiLateScheduler<A, B> {

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
    private BiConsumer<A, B> whenComplete;

    /**
     * When complete action with error
     */
    private TriConsumer<A, B, Throwable> whenCompleteWithError;

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
     * The scheduler type B object
     */
    private B typeB = null;

    /**
     * The scheduler error
     */
    private Throwable typeE = null;

    /**
     * Set the complete action
     *
     * @param action the action to perform
     *               when the scheduler is completed
     * @return this instance
     */
    @Override
    public BiLateScheduler<A, B> whenComplete(final Runnable action) {
        whenCompleteRunner = action;

        if (completed) {
            if (whenCompleteRunner != null) {
                whenCompleteRunner.run();
            }
        }

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
    public BiLateScheduler<A, B> whenComplete(final BiConsumer<A, B> action) {
        whenComplete = action;

        if (completed) {
            if (whenComplete != null) {
                whenComplete.accept(typeA, typeB);
            }
        }

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
    public BiLateScheduler<A, B> whenComplete(final TriConsumer<A, B, Throwable> caughtAction) {
        whenCompleteWithError = caughtAction;

        if (completed) {
            if (whenCompleteWithError != null) {
                whenCompleteWithError.accept(typeA, typeB, typeE);
            }
        }

        return this;
    }

    /**
     * Get if the scheduler is completed
     *
     * @return if the shcheduler is complete
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
    public A getObject() {
        return typeA;
    }

    /**
     * Get the B object
     *
     * @return the B object
     */
    @Override
    public B getSubObject() {
        return typeB;
    }

    /**
     * Complete the scheduler
     *
     * @param target the type A object
     * @param subTarget the type B object
     */
    @Override
    public void complete(final A target, final B subTarget) {
        if (this.cancelled || this.completed)
            return;
        try {
            typeA = target;
            typeB = subTarget;

            if (this.whenComplete != null)
                this.whenComplete.accept(target, subTarget);

            if (this.whenCompleteWithError != null)
                this.whenCompleteWithError.accept(target, subTarget, null);

            if (this.whenCompleteRunner != null)
                this.whenCompleteRunner.run();

            this.completed = true;
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Complete the scheduler
     *
     * @param target the type A object
     * @param subTarget the type B object
     * @param error any error that has been thrown
     */
    @Override
    public void complete(final A target, final B subTarget, final Throwable error) {
        if (this.cancelled || this.completed)
            return;
        try {
            typeA = target;
            typeB = subTarget;
            typeE = error;

            if (this.whenCompleteWithError != null)
                this.whenCompleteWithError.accept(target, subTarget, error);

            if (this.whenComplete != null)
                this.whenComplete.accept(target, subTarget);

            if (this.whenCompleteRunner != null)
                this.whenCompleteRunner.run();

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
    public CancellableScheduler whenCancelled(final Runnable action) {
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
