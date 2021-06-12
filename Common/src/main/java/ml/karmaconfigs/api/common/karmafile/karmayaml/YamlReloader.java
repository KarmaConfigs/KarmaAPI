package ml.karmaconfigs.api.common.karmafile.karmayaml;

import ml.karmaconfigs.api.common.karma.KarmaSource;

import java.io.File;
import java.nio.file.Path;

/**
 * Private GSA code
 * <p>
 * The use of this code
 * without GSA team authorization
 * will be a violation of
 * terms of use determined
 * in <a href="https://karmaconfigs.github.io/page/license"> here </a>
 *
 * Since version 1.2.5 this does not longer save the file, as it
 * should be saved by calling {@link KarmaYamlManager#save(File)} or
 * {@link KarmaYamlManager#save(File, KarmaSource, String)}
 */
@SuppressWarnings("unused")
public final class YamlReloader {

    private final KarmaYamlManager current;

    /**
     * Initialize the yaml reloader
     *
     * @param currentConfiguration the current KarmaConfiguration
     */
    public YamlReloader(final KarmaYamlManager currentConfiguration) {
        current = currentConfiguration;
    }

    /**
     * Reload the configuration file
     *
     * @param ignored the ignored paths to not
     *                update
     */
    public final void reload(final String... ignored) {
        Object source = current.getSourceRoot().getSource();
        if (source instanceof File || source instanceof Path) {
            File file;

            if (source instanceof File) {
                file = (File) source;
            } else {
                file = ((Path) source).toFile();
            }

            KarmaYamlManager newConfiguration = new KarmaYamlManager(file);
            current.update(newConfiguration, true, ignored);
        } else {
            throw new RuntimeException("Tried to reload karma configuration from a non file/path source karma configuration");
        }
    }
}