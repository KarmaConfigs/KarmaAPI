package ml.karmaconfigs.api.common.karmafile.karmayaml;

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

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Map;

/**
 * Karma yaml manager source
 */
public final class KYMSource {

    /**
     * The yaml source
     */
    private final Object source;

    /**
     * Initialize the yaml source
     *
     * @param configuration the yaml
     */
    public KYMSource(final Reader configuration) {
        this.source = configuration;
    }

    /**
     * Initialize the yaml source
     *
     * @param configuration the yaml
     */
    public KYMSource(final InputStream configuration) {
        this.source = configuration;
    }

    /**
     * Initialize the yaml source
     *
     * @param configuration the yaml configuration/path
     * @param isPath if the yaml configuration string is a path
     */
    public KYMSource(final String configuration, final boolean isPath) {
        if (isPath) {
            this.source = new File(configuration);
        } else {
            this.source = configuration;
        }
    }

    /**
     * Initialize the yaml source
     *
     * @param configuration the yaml
     */
    public KYMSource(final File configuration) {
        this.source = configuration;
    }

    /**
     * Initialize the yaml source
     *
     * @param configuration the yaml
     */
    public KYMSource(final Path configuration) {
        this.source = configuration;
    }

    /**
     * Initialize the yaml source
     *
     * @param values the yaml key/value
     */
    public KYMSource(final Map<?, ?> values) {
        this.source = values;
    }

    /**
     * Get the yaml source
     *
     * @return the yaml source
     */
    @NotNull
    public Object getSource() {
        return this.source;
    }
}
