package ml.karmaconfigs.api.common.karma;

import ml.karmaconfigs.api.common.utils.StringUtils;

import java.io.File;
import java.io.Serializable;
import java.nio.file.Path;

public interface KarmaSource extends Serializable {

    /**
     * Get the KarmaSource name
     *
     * @return the KarmaSource name
     */
    String name();

    /**
     * Get the KarmaSource version
     *
     * @return the KarmaSource version
     */
    String version();

    /**
     * Get the KarmaSource description
     *
     * @return the KarmaSource description
     */
    String description();

    /**
     * Get the KarmaSource authors
     *
     * @return the KarmaSource authors
     */
    String[] authors();

    /**
     * Get the KarmaSource resource data folder
     *
     * @return the KarmaSource data folder
     */
    default Path getDataPath() {
        File mainJar = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " "));
        File parent = mainJar.getParentFile();

        File dataFolder;
        if (StringUtils.isNullOrEmpty(name())) {
            dataFolder = new File(parent, StringUtils.randomString(5, StringUtils.StringGen.ONLY_LETTERS, StringUtils.StringType.ALL_UPPER));
        } else {
            dataFolder = new File(parent, name());
        }

        return dataFolder.toPath();
    }
}
