package ml.karmaconfigs.api.common;

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.PrefixConsoleData;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.common.utils.enums.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public final class Console {

    public static boolean useConsoleOut = false;
    public static boolean resetColors = true;
    private static Consumer<String> messageAction = null;
    private final KarmaSource source;

    public Console(KarmaSource src) {
        this.source = src;
    }

    public Console messageRequest(Consumer<String> action) {
        messageAction = action;
        send("&b[ KarmaAPI &b]&7 Using custom console message sender");
        return this;
    }

    public PrefixConsoleData getData() {
        return new PrefixConsoleData(this.source);
    }

    public void send(String message) {
        if (messageAction == null) {
            Printer.write(this.source, StringUtils.toConsoleColor(message) + "\033[0m");
        } else {
            messageAction.accept(message);
        }
    }

    public void send(String message, Object... replaces) {
        for (int i = 0; i < replaces.length; i++) {
            String placeholder = "{" + i + "}";
            String value = replaces[i].toString();
            message = message.replace(placeholder, value);
        }
        if (messageAction == null) {
            Printer.write(this.source, StringUtils.toConsoleColor(message) + "\033[0m");
        } else {
            messageAction.accept(message);
        }
    }

    public void send(@NotNull String message, @NotNull Level level) {
        String prefix = "&b[ &fALERT &b] &7NONE: &b";
        PrefixConsoleData data = getData();
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
        if (messageAction == null) {
            if (message.contains("\n")) {
                for (String msg : message.split("\n"))
                    send(msg);
            } else {
                send(prefix + message);
            }
        } else {
            if (message.contains("\n"))
                message = StringUtils.listToString(Arrays.asList(message.split("\n")), false);

            messageAction.accept(prefix + message);
        }
    }

    public void send(@NotNull String message, @NotNull Level level, @NotNull Object... replaces) {
        String prefix = "&b[ &fALERT &b] &7NONE: &b";
        PrefixConsoleData data = getData();
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
        if (messageAction == null) {
            if (message.contains("\n")) {
                for (String msg : message.split("\n"))
                    send(msg);
            } else {
                send(prefix + message);
            }
        } else {
            if (message.contains("\n"))
                message = StringUtils.listToString(Arrays.asList(message.split("\n")), false);

            messageAction.accept(prefix + message);
        }
    }

    protected static class Printer {
        public static void write(KarmaSource source, String text) {
            Logger logger = Logger.getLogger(source.name());
            LogManager.getLogManager().addLogger(logger);
            logger.setLevel(java.util.logging.Level.INFO);

            if (Console.useConsoleOut) {
                System.out.println((Console.resetColors ? "\033[0m" : "") + StringUtils.toConsoleColor(text) + (Console.resetColors ? "\033[0m" : ""));
            } else {
                logger.info((Console.resetColors ? "\033[0m" : "") + StringUtils.toConsoleColor(text) + (Console.resetColors ? "\033[0m" : ""));
            }
        }
    }

    public static class Colors {
        public static final String RESET = "\033[0m";

        public static final String BLACK = "\033[0;30m";

        public static final String RED = "\033[0;31m";

        public static final String GREEN = "\033[0;32m";

        public static final String YELLOW = "\033[0;33m";

        public static final String BLUE = "\033[0;34m";

        public static final String PURPLE = "\033[0;35m";

        public static final String CYAN = "\033[0;36m";

        public static final String WHITE = "\033[0;37m";

        public static final String BLACK_BRIGHT = "\033[0;90m";

        public static final String RED_BRIGHT = "\033[0;91m";

        public static final String GREEN_BRIGHT = "\033[0;92m";

        public static final String YELLOW_BRIGHT = "\033[0;93m";

        public static final String BLUE_BRIGHT = "\033[0;94m";

        public static final String PURPLE_BRIGHT = "\033[0;95m";

        public static final String CYAN_BRIGHT = "\033[0;96m";

        public static final String WHITE_BRIGHT = "\033[0;97m";
    }
}
