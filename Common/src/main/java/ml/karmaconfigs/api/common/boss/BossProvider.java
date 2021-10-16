package ml.karmaconfigs.api.common.boss;

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

import java.util.Collection;

/**
 * Karma boss bar provider
 *
 * @param <T> the bar provider ( target type to display to )
 */
public abstract class BossProvider<T> {

    /**
     * Set the boss bar color
     *
     * @param paramBossColor the boss bar color
     * @return this boss bar instance
     */
    public abstract BossProvider<T> color(BossColor paramBossColor);

    /**
     * Set the boss bar style
     *
     * @param paramBossType the boss bar style
     * @return this boss bar instance
     */
    public abstract BossProvider<T> style(BossType paramBossType);

    /**
     * Set the boss bar progress type
     *
     * @param paramProgressiveBar the boss bar progress type
     * @return this boss bar instance
     */
    public abstract BossProvider<T> progress(ProgressiveBar paramProgressiveBar);

    /**
     * Destroy the current boss bar
     */
    public abstract void cancel();

    /**
     * Display the boss bar to the specified players
     *
     * @param paramCollection the players to display to
     */
    protected abstract void displayBar(Collection<T> paramCollection);

    /**
     * Schedule the bar to the specified players
     *
     * @param paramCollection the players to display to
     */
    public abstract void scheduleBar(Collection<T> paramCollection);

    /**
     * Schedule the bar to the specified player
     *
     * @param paramT the player to display to
     */
    public abstract void scheduleBar(T paramT);

    /**
     * Get the amount of bars that exist
     *
     * @return the amount of bars created
     */
    public abstract int getBarsAmount();

    /**
     * Get the current boss bar id
     *
     * @return the current boss bar id
     */
    public abstract int getBarId();

    /**
     * Update the boss bar
     *
     * @param paramString the new boss bar text
     * @param paramBoolean restart the bar progress
     * @return if the boss bar could be updated
     */
    public abstract boolean update(String paramString, boolean paramBoolean);

    /**
     * Set the boss bar display time
     *
     * @param paramDouble the boss bar display time
     * @return the boss bar display time
     */
    public abstract BossProvider<T> displayTime(double paramDouble);

    /**
     * Get if the boss bar is valid
     *
     * @return if the boss bar is valid
     */
    public abstract boolean isValid();

    /**
     * Get if the boss bar is cancelled
     *
     * @return if the boss bar is cancelled
     */
    public abstract boolean isCancelled();

    /**
     * Get the boss bar title
     *
     * @return the boss bar title
     */
    public abstract String getTitle();

    /**
     * Get the boss bar color
     *
     * @return the boss bar color
     */
    public abstract BossColor getColor();

    /**
     * Get the boss bar style
     *
     * @return the boss bar style
     */
    public abstract BossType getStyle();
}
