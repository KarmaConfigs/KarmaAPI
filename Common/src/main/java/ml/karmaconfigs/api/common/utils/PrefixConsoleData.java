package ml.karmaconfigs.api.common.utils;

import ml.karmaconfigs.api.common.karma.KarmaSource;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
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
public final class PrefixConsoleData implements Serializable {

    private final static HashMap<KarmaSource, String> okPrefix = new HashMap<>();
    private final static HashMap<KarmaSource, String> infoPrefix = new HashMap<>();
    private final static HashMap<KarmaSource, String> warnPrefix = new HashMap<>();
    private final static HashMap<KarmaSource, String> gravPrefix = new HashMap<>();

    private final KarmaSource Main;

    /**
     * Initialize the prefix console data
     * storager
     *
     * @param p the plugin
     */
    public PrefixConsoleData(@NotNull final KarmaSource p) {
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
