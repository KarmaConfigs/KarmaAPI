package ml.karmaconfigs.api.common;

import ml.karmaconfigs.api.common.utils.StringUtils;

/**
 * Common console utilities
 *
 * This is very basic and does not support
 * leveled messages for now.
 */
public interface Console {

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