package ml.karmaconfigs.api.bungee;

import ml.karmaconfigs.api.common.*;
import ml.karmaconfigs.api.common.utils.StringUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
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
interface KarmaConsoleSender {

    /**
     * Send a message to the console
     *
     * @param message the message
     */
    static void send(@NotNull final String message) {
        ProxyServer.getInstance().getConsole().sendMessage(ComponentUtil.toComponent(message));
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

        ProxyServer.getInstance().getConsole().sendMessage(ComponentUtil.toComponent(message));
    }

    /**
     * Send an alert to the console
     *
     * @param message the message
     * @param level   the message level
     */
    static void send(@NotNull String message, @NotNull final Level level) {
        String prefix = "&b[ &fALERT &b] &7NONE: &b";

        switch (level) {
            case OK:
                prefix = "&b[ &fALERT &b] &aOK: &b";
                break;
            case INFO:
                prefix = "&b[ &fALERT &b] &7INFO: &b";
                break;
            case WARNING:
                prefix = "&b[ &fALERT &b] &eWARNING: &b";
                break;
            case GRAVE:
                prefix = "&b[ &fALERT &b] &cGRAVE: &b";
                break;
        }


        message = StringUtils.stripColor(message);
        ProxyServer.getInstance().getConsole().sendMessage(ComponentUtil.toComponent(prefix + message));
    }

    /**
     * Send an alert to the console
     * with the specified replaces
     *
     * @param message  the message
     * @param level    the message level
     * @param replaces the replaces
     */
    static void send(@NotNull String message, @NotNull final Level level, @NotNull final Object... replaces) {
        String prefix = "&b[ &fALERT &b] &7NONE: &b";

        switch (level) {
            case OK:
                prefix = "&b[ &fALERT &b] &aOK: &b";
                break;
            case INFO:
                prefix = "&b[ &fALERT &b] &7INFO: &b";
                break;
            case WARNING:
                prefix = "&b[ &fALERT &b] &eWARNING: &b";
                break;
            case GRAVE:
                prefix = "&b[ &fALERT &b] &cGRAVE: &b";
                break;
        }

        for (int i = 0; i < replaces.length; i++) {
            String placeholder = "{" + i + "}";
            String value = replaces[i].toString();

            message = message.replace(placeholder, value);
        }

        message = StringUtils.stripColor(message);
        ProxyServer.getInstance().getConsole().sendMessage(ComponentUtil.toComponent(prefix + message));
    }
}

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 */
interface ComponentUtil {

    static BaseComponent[] toComponent(@NotNull final String text) {
        return TextComponent.fromLegacyText(StringUtils.toColor(text));
    }
}