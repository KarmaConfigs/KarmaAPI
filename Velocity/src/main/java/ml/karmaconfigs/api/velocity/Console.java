package ml.karmaconfigs.api.velocity;

import com.velocitypowered.api.plugin.PluginContainer;
import ml.karmaconfigs.api.common.Level;
import ml.karmaconfigs.api.common.utils.StringUtils;
import ml.karmaconfigs.api.velocity.util.PrefixConsoleData;
import org.jetbrains.annotations.NotNull;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
public interface Console {

    /**
     * Set the plugin ok alert prefix
     *
     * @param issuer the issuer plugin
     * @param prefix the prefix
     */
    static void setOkPrefix(@NotNull final PluginContainer issuer, @NotNull String prefix) {
        PrefixConsoleData data = new PrefixConsoleData(issuer);
        data.setOkPrefix(prefix);
    }

    /**
     * Set the plugin info alert prefix
     *
     * @param issuer the issuer plugin
     * @param prefix the prefix
     */
    static void setInfoPrefix(@NotNull final PluginContainer issuer, @NotNull String prefix) {
        PrefixConsoleData data = new PrefixConsoleData(issuer);
        data.setInfoPrefix(prefix);
    }

    /**
     * Set the plugin warning alert prefix
     *
     * @param issuer the issuer plugin
     * @param prefix the prefix
     */
    static void setWarningPrefix(@NotNull final PluginContainer issuer, @NotNull String prefix) {
        PrefixConsoleData data = new PrefixConsoleData(issuer);
        data.setWarnPrefix(prefix);
    }

    /**
     * Set the plugin grave alert prefix
     *
     * @param issuer the issuer plugin
     * @param prefix the prefix
     */
    static void setGravePrefix(@NotNull final PluginContainer issuer, @NotNull String prefix) {
        PrefixConsoleData data = new PrefixConsoleData(issuer);
        data.setGravPrefix(prefix);
    }

    /**
     * Send a message to the console
     *
     * @param message the message
     */
    static void send(@NotNull final String message) {
        if (message.contains("\n")) {
            for (String msg : message.split("\n"))
                ml.karmaconfigs.api.common.Console.send(msg);
        } else {
            ml.karmaconfigs.api.common.Console.send(message);
        }
    }

    /**
     * Send a message to the console
     * with the specified replaces
     *
     * @param message  the message
     * @param replaces the replaces
     */
    static void send(@NotNull String message, @NotNull final Object... replaces) {
        for (int i = 0; i < replaces.length; i++) {
            String placeholder = "{" + i + "}";
            String value = replaces[i].toString();

            message = message.replace(placeholder, value);
        }

        if (message.contains("\n")) {
            for (String msg : message.split("\n"))
                ml.karmaconfigs.api.common.Console.send(msg);
        } else {
            ml.karmaconfigs.api.common.Console.send(message);
        }
    }

    /**
     * Send an alert to the console
     *
     * @param sender  the plugin sender
     * @param message the message
     * @param level   the message level
     */
    static void send(@NotNull final PluginContainer sender, @NotNull String message, @NotNull final Level level) {
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
    static void send(@NotNull final PluginContainer sender, @NotNull String message, @NotNull final Level level, @NotNull final Object... replaces) {
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
}


