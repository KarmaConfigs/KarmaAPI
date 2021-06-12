package ml.karmaconfigs.api.common;

import ml.karmaconfigs.api.common.utils.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.ml/license/"> here </a>
 *
 * @since 2.8 uses LockLogin like version checker system
 * @deprecated This is being re-worked
 */
@Deprecated
public final class VersionChecker implements Serializable {

    private final List<String> replaced = new ArrayList<>();
    private int latest;
    private String version = "";
    private String updateURL = "";

    /**
     * Starts retrieving the info from the html file
     *
     * @param update_url the update url
     */
    public VersionChecker(final String update_url) {
        try {
            URL url = new URL(update_url);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String word;
            List<String> lines = new ArrayList<>();
            while ((word = reader.readLine()) != null)
                lines.add((word.replaceAll("\\s", "").isEmpty() ? "\u00a7f" : word));

            reader.close();
            for (String str : lines) {
                replaced.add(str
                        .replace("[", "{open}")
                        .replace("]", "{close}")
                        .replace(",", "{comma}")
                        .replace("_", "\u00a7"));
            }

            this.latest = Integer.parseInt(replaced.get(0).replaceAll("[aA-zZ]", "").replaceAll("\\s", "").replace(".", ""));
            this.version = replaced.get(0);
            this.updateURL = replaced.get(1);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Check if the specified version needs update
     *
     * @param current_version the current version
     * @return if the current version needs update
     */
    public final boolean needsUpdate(final String current_version) {
        int current = Integer.parseInt(current_version.replaceAll("[aA-zZ]", "").replaceAll("\\s", "").replace(".", ""));

        if (current == latest)
            return false;
        else
            return current < latest;
    }

    /**
     * Get the latest version
     *
     * @return the latest version
     */
    public final String getLatest() {
        String type = version.replaceAll("[A-z]", "");
        String versionTxt = version.replaceAll("[0-9]", "").replace(".", "").replace(" ", "");
        if (!versionTxt.isEmpty()) {
            return versionTxt + " / " + type.replace(" ", "");
        } else {
            return type.replace(" ", "");
        }
    }

    /**
     * Get the plugin update url
     *
     * @return the plugin update, url where the client
     * should go to update the plugin
     */
    public final String getUpdateURL() {
        return updateURL;
    }

    /**
     * Gets the changelog
     *
     * @return the latest version changelog
     */
    public final String getChangeLog() {
        List<String> replace = new ArrayList<>();

        for (int i = 0; i < replaced.size(); i++) {
            if (i == 0) {
                replace.add(StringUtils.toColor("&b--------- &eChangeLog &6: &a{version} &b---------")
                        .replace("{version}", replaced.get(0)) + "\u00a7r");
            } else {
                if (i != 1)
                    replace.add(StringUtils.toColor(replaced.get(i)));
            }
        }

        return replace.toString()
                .replace("[", "")
                .replace("]", "")
                .replace(",", "\n")
                .replace("{open}", "[")
                .replace("{close}", "]")
                .replace("{comma}", ",");
    }
}
