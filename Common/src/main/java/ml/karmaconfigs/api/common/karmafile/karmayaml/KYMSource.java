package ml.karmaconfigs.api.common.karmafile.karmayaml;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Map;

/*
 * This file is part of KarmaAPI, licensed under the MIT License.
 *
 *  Copyright (c) karma (KarmaDev) <karmaconfigs@gmail.com>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

/**
 * Karma yaml manager source
 */
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
