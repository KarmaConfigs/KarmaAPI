package ml.karmaconfigs.api.common.utils;

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

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Karma console prefix data
 */
public final class PrefixConsoleData implements Serializable {

    /**
     * Map containing source => ok prefix
     */
    private static final HashMap<KarmaSource, String> okPrefix = new HashMap<>();
    /**
     * Map containing source => info prefix
     */
    private static final HashMap<KarmaSource, String> infoPrefix = new HashMap<>();
    /**
     * Map containing source => warning prefix
     */
    private static final HashMap<KarmaSource, String> warnPrefix = new HashMap<>();
    /**
     * Map containing source => grave prefix
     */
    private static final HashMap<KarmaSource, String> gravPrefix = new HashMap<>();

    /**
     * The prefix console data source
     */
    private final KarmaSource source;

    /**
     * Initialize the prefix console data
     *
     * @param p the source owner
     */
    public PrefixConsoleData(final @NotNull KarmaSource p) {
        this.source = p;
    }

    /**
     * Set the OK prefix
     *
     * @param prefix the prefix
     */
    public void setOkPrefix(final @NotNull String prefix) {
        okPrefix.put(this.source, StringUtils.toConsoleColor(prefix));
    }

    /**
     * Set the info prefix
     *
     * @param prefix the prefix
     */
    public void setInfoPrefix(final @NotNull String prefix) {
        infoPrefix.put(this.source, StringUtils.toConsoleColor(prefix));
    }

    /**
     * Set the warning prefix
     *
     * @param prefix the prefix
     */
    public void setWarnPrefix(final @NotNull String prefix) {
        warnPrefix.put(this.source, StringUtils.toConsoleColor(prefix));
    }

    /**
     * Set the grave prefix
     *
     * @param prefix the prefix
     */
    public void setGravePrefix(final @NotNull String prefix) {
        gravPrefix.put(this.source, StringUtils.toConsoleColor(prefix));
    }

    /**
     * Get the OK prefix
     *
     * @return the prefix
     */
    public String getOkPrefix() {
        return okPrefix.getOrDefault(this.source, StringUtils.toConsoleColor("&b[ &3" + source.name() + " &b| &2OK &b] >> &9"));
    }

    /**
     * Get the info prefix
     *
     * @return the info prefix
     */
    public String getInfoPrefix() {
        return infoPrefix.getOrDefault(this.source, StringUtils.toConsoleColor("&b[ &3" + source.name() + " &b| &7INFO &b] >> &9"));
    }

    /**
     * Get the warning prefix
     *
     * @return the warning prefix
     */
    public String getWarningPrefix() {
        return warnPrefix.getOrDefault(this.source, StringUtils.toConsoleColor("&b[ &3" + source.name() + " &b| &6WARNING &b] >> &9"));
    }

    /**
     * Set the grave prefix
     *
     * @return the grave prefix
     */
    public String getGravePrefix() {
        return gravPrefix.getOrDefault(this.source, StringUtils.toConsoleColor("&b[ &3" + source.name() + " &b| &cGRAVE &b] >> &9"));
    }
}
