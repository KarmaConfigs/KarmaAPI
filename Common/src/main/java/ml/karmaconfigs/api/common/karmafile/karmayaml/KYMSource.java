package ml.karmaconfigs.api.common.karmafile.karmayaml;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.util.Map;

public final class KYMSource {

    private final Object source;

    /**
     * Initialize the KarmaConfigurationSource
     *
     * @param configuration the configuration
     */
    public KYMSource(final Reader configuration) {
        source = configuration;
    }

    /**
     * Initialize the KarmaConfigurationSource
     *
     * @param configuration the configuration
     */
    public KYMSource(final InputStream configuration) {
        source = configuration;
    }

    /**
     * Initialize the KarmaConfigurationSource
     *
     * @param configuration the configuration
     * @param isPath if the
     */
    public KYMSource(final String configuration, final boolean isPath) {
        if (isPath) {
            source = new File(configuration);
        } else {
            source = configuration;
        }
    }

    /**
     * Initialize the KarmaConfigurationSource
     *
     * @param configuration the configuration
     */
    public KYMSource(final File configuration) {
        source = configuration;
    }

    /**
     * Initialize the KarmaConfigurationSource
     *
     * @param configuration the configuration
     */
    public KYMSource(final Path configuration) {
        source = configuration;
    }

    /**
     * Initialize the KarmaConfigurationSource
     *
     * @param values the values
     */
    public KYMSource(final Map<?, ?> values) {
        source = values;
    }

    /**
     * Get KarmaConfigurationSource source
     *
     * @return the KarmaYamlManager file source
     */
    @NotNull
    public final Object getSource() {
        return source;
    }
}
