package ml.karmaconfigs.api.common;

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.PrefixConsoleData;
import ml.karmaconfigs.api.common.utils.StringUtils;
import ml.karmaconfigs.api.common.utils.enums.Level;
import org.jetbrains.annotations.NotNull;

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
 * Common console utilities
 *
 * This is very basic and does not support
 * leveled messages for now.
 */
public interface Console {

    /**
     * Get the console prefix data
     *
     * @param source the karma source
     * @return the karma source prefix console data
     */
    static PrefixConsoleData getData(final KarmaSource source) {
        return new PrefixConsoleData(source);
    }

    /**
     * Send a message translating bukkit
     * color message to console colors
     *
     * @param message the message to send
     */
    static void send(String message) {
        System.out.println(StringUtils.toConsoleColor(message) + Colors.RESET);
    }

    /**
     * Send a message translating bukkit
     * color message to console colors
     *
     * @param message the message to send
     * @param replaces the replaces of the message
     */
    static void send(String message, final Object... replaces) {
        for (int i = 0; i < replaces.length; i++) {
            String placeholder = "{" + i + "}";
            String value = replaces[i].toString();

            message = message.replace(placeholder, value);
        }

        System.out.println(StringUtils.toConsoleColor(message) + Colors.RESET);
    }

    /**
     * Send an alert to the console
     *
     * @param sender  the plugin sender
     * @param message the message
     * @param level   the message level
     */
    static void send(@NotNull final KarmaSource sender, @NotNull String message, @NotNull final Level level) {
        String prefix = "&b[ &fALERT &b] &7NONE: &b";

        PrefixConsoleData data = new PrefixConsoleData(sender);

        switch (level) {
            case OK:
                prefix = data.getOkPrefix();
                break;
            case INFO:
                prefix = data.getInfoPrefix();
                break;
            case WARNING:
                prefix = data.getWarningPrefix();
                break;
            case GRAVE:
                prefix = data.getGravePrefix();
                break;
        }

        message = StringUtils.stripColor(message);
        if (message.contains("\n")) {
            for (String msg : message.split("\n"))
                ml.karmaconfigs.api.common.Console.send(msg);
        } else {
            ml.karmaconfigs.api.common.Console.send(prefix + message);
        }
    }

    /**
     * Send an alert to the console
     * with the specified replaces
     *
     * @param sender   the plugin sender
     * @param message  the message
     * @param level    the message level
     * @param replaces the replaces
     */
    static void send(@NotNull final KarmaSource sender, @NotNull String message, @NotNull final Level level, @NotNull final Object... replaces) {
        String prefix = "&b[ &fALERT &b] &7NONE: &b";

        PrefixConsoleData data = new PrefixConsoleData(sender);

        switch (level) {
            case OK:
                prefix = data.getOkPrefix();
                break;
            case INFO:
                prefix = data.getInfoPrefix();
                break;
            case WARNING:
                prefix = data.getWarningPrefix();
                break;
            case GRAVE:
                prefix = data.getGravePrefix();
                break;
        }

        for (int i = 0; i < replaces.length; i++) {
            String placeholder = "{" + i + "}";
            String value = replaces[i].toString();

            message = message.replace(placeholder, value);
        }

        message = StringUtils.stripColor(message);
        if (message.contains("\n")) {
            for (String msg : message.split("\n"))
                ml.karmaconfigs.api.common.Console.send(msg);
        } else {
            ml.karmaconfigs.api.common.Console.send(prefix + message);
        }
    }

    /**
     * Valid console colors limited by
     * bukkit colors.
     */
    class Colors {
        /**
         * Reset color
         */
        public static final String RESET = "\033[0m";

        /**
         * Black
         */
        public static final String BLACK = "\033[0;30m";

        /**
         * Red
         */
        public static final String RED = "\033[0;31m";

        /**
         * Green
         */
        public static final String GREEN = "\033[0;32m";

        /**
         * Gold
         */
        public static final String YELLOW = "\033[0;33m";

        /**
         * Dark blue
         */
        public static final String BLUE = "\033[0;34m";

        /**
         * Purple
         */
        public static final String PURPLE = "\033[0;35m";

        /**
         * Blue
         */
        public static final String CYAN = "\033[0;36m";

        /**
         * Gray
         */
        public static final String WHITE = "\033[0;37m";

        /**
         * Dark gray
         */
        public static final String BLACK_BRIGHT = "\033[0;90m";

        /**
         * Light red
         */
        public static final String RED_BRIGHT = "\033[0;91m";

        /**
         * Lime
         */
        public static final String GREEN_BRIGHT = "\033[0;92m";

        /**
         * Yellow
         */
        public static final String YELLOW_BRIGHT = "\033[0;93m";

        /**
         * Light blue
         */
        public static final String BLUE_BRIGHT = "\033[0;94m";

        /**
         * Magenta
         */
        public static final String PURPLE_BRIGHT = "\033[0;95m";

        /**
         * Cyan
         */
        public static final String CYAN_BRIGHT = "\033[0;96m";

        /**
         * White
         */
        public static final String WHITE_BRIGHT = "\033[0;97m";
    }
}