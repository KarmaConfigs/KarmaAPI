package ml.karmaconfigs.api.bungee;

import ml.karmaconfigs.api.common.*;
import ml.karmaconfigs.api.common.utils.StringUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

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
    static void setOkPrefix(@NotNull final Plugin issuer, @NotNull String prefix) {
        PrefixConsoleData data = new PrefixConsoleData(issuer);
        data.setOkPrefix(prefix);
    }

    /**
     * Set the plugin info alert prefix
     *
     * @param issuer the issuer plugin
     * @param prefix the prefix
     */
    static void setInfoPrefix(@NotNull final Plugin issuer, @NotNull String prefix) {
        PrefixConsoleData data = new PrefixConsoleData(issuer);
        data.setInfoPrefix(prefix);
    }

    /**
     * Set the plugin warning alert prefix
     *
     * @param issuer the issuer plugin
     * @param prefix the prefix
     */
    static void setWarningPrefix(@NotNull final Plugin issuer, @NotNull String prefix) {
        PrefixConsoleData data = new PrefixConsoleData(issuer);
        data.setWarnPrefix(prefix);
    }

    /**
     * Set the plugin grave alert prefix
     *
     * @param issuer the issuer plugin
     * @param prefix the prefix
     */
    static void setGravePrefix(@NotNull final Plugin issuer, @NotNull String prefix) {
        PrefixConsoleData data = new PrefixConsoleData(issuer);
        data.setGravPrefix(prefix);
    }

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
     * @param sender  the plugin sender
     * @param message the message
     * @param level   the message level
     */
    static void send(@NotNull final Plugin sender, @NotNull String message, @NotNull final Level level) {
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
        ProxyServer.getInstance().getConsole().sendMessage(ComponentUtil.toComponent(prefix + message));
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
    static void send(@NotNull final Plugin sender, @NotNull String message, @NotNull final Level level, @NotNull final Object... replaces) {
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
final class PrefixConsoleData {

    private final static HashMap<Plugin, String> okPrefix = new HashMap<>();
    private final static HashMap<Plugin, String> infoPrefix = new HashMap<>();
    private final static HashMap<Plugin, String> warnPrefix = new HashMap<>();
    private final static HashMap<Plugin, String> gravPrefix = new HashMap<>();

    private final Plugin Main;

    /**
     * Initialize the prefix console data
     * storager
     *
     * @param p the plugin
     */
    public PrefixConsoleData(@NotNull final Plugin p) {
        Main = p;
    }

    /**
     * Set the ok prefix
     *
     * @param prefix the prefix
     */
    public final void setOkPrefix(@NotNull final String prefix) {
        okPrefix.put(Main, StringUtils.toColor(prefix));
    }

    /**
     * Set the info prefix
     *
     * @param prefix the prefix
     */
    public final void setInfoPrefix(@NotNull final String prefix) {
        infoPrefix.put(Main, StringUtils.toColor(prefix));
    }

    /**
     * Set the info prefix
     *
     * @param prefix the prefix
     */
    public final void setWarnPrefix(@NotNull final String prefix) {
        warnPrefix.put(Main, StringUtils.toColor(prefix));
    }

    /**
     * Set the info prefix
     *
     * @param prefix the prefix
     */
    public final void setGravPrefix(@NotNull final String prefix) {
        gravPrefix.put(Main, StringUtils.toColor(prefix));
    }

    /**
     * Get the info prefix
     *
     * @return a String
     */
    public final String getOkPrefix() {
        return okPrefix.getOrDefault(Main, "&b[ &fSERVER &b] &aOK: &b");
    }

    /**
     * Get the info prefix
     *
     * @return a String
     */
    public final String getInfoPrefix() {
        return infoPrefix.getOrDefault(Main, "&b[ &fSERVER &b] &7INFO: &b");
    }

    /**
     * Get the warning prefix
     *
     * @return a String
     */
    public final String getWarningPrefix() {
        return warnPrefix.getOrDefault(Main, "&b[ &fSERVER &b] &aWARNING&7: &b");
    }

    /**
     * Get the grave prefix
     *
     * @return a String
     */
    public final String getGravePrefix() {
        return gravPrefix.getOrDefault(Main, "&b[ &fSERVER &b] &cGRAVE&7: &b");
    }
}