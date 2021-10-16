package ml.karmaconfigs.api.common.karma;

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

import ml.karmaconfigs.api.common.Console;
import ml.karmaconfigs.api.common.timer.AsyncScheduler;
import ml.karmaconfigs.api.common.timer.SyncScheduler;
import ml.karmaconfigs.api.common.timer.scheduler.Scheduler;
import ml.karmaconfigs.api.common.utils.enums.Level;

/**
 * Karma API source
 */
public final class APISource implements KarmaSource {

    /**
     * Current karma source
     */
    private static KarmaSource instance;

    /**
     * If the source assignment is locked
     */
    private static boolean locked = false;

    /**
     * The current async scheduler
     */
    private static Scheduler async = null;

    /**
     * The current sync scheduler
     */
    private static Scheduler sync = null;

    /**
     * Initialize the API source
     */
    APISource() {
        instance = this;
        async = new AsyncScheduler(this);
        sync = new SyncScheduler(this);
    }

    /**
     * Set the API source provider
     *
     * @param source the API source
     */
    public static void setProvider(KarmaSource source) {
        if (!locked) {
            instance = source;
            async = new AsyncScheduler(source);
            sync = new SyncScheduler(source);
        } else {
            getConsole().send("&b[ KarmaAPI ] &cSource {0} tried to overwrite the current source provider", Level.GRAVE, source.name());
        }
        locked = true;
    }

    /**
     * Get the source
     *
     * @return the current API source
     */
    public static KarmaSource getSource() {
        if (instance == null)
            return new APISource();
        return instance;
    }

    /**
     * Get the API console
     *
     * @return the API console
     */
    public static Console getConsole() {
        return getSource().out();
    }

    /**
     * Get the API async scheduler
     *
     * @return an scheduler
     */
    public static Scheduler asyncScheduler() {
        return async;
    }

    /**
     * Get the API sync scheduler
     *
     * @return an scheduler
     */
    public static Scheduler syncScheduler() {
        return sync;
    }

    /**
     * Get the API source name
     *
     * @return the API source name
     */
    public String name() {
        return "KarmaAPI";
    }

    /**
     * Get the API source version
     *
     * @return the API source version
     */
    public String version() {
        return KarmaAPI.getVersion();
    }

    /**
     * Get the API source description
     *
     * @return the API source description
     */
    public String description() {
        return "KarmaAPI is an API made specially for plugin developer but also with utilities for base java.";
    }

    /**
     * Get the API source authors
     *
     * @return the API source authors
     */
    public String[] authors() {
        return new String[]{"KarmaDev"};
    }

    /**
     * Get the API source updater
     *
     * @return the API source updater
     */
    public String updateURL() {
        return "";
    }
}
