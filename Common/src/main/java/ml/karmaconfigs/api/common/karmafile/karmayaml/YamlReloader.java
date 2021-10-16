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

import java.io.File;
import java.nio.file.Path;

/**
 * Karma yaml reloader
 */
public final class YamlReloader {

    /**
     * The yaml manager to reload
     */
    private final KarmaYamlManager current;

    /**
     * Initialize the yaml reloader
     *
     * @param currentConfiguration the current yaml configuration
     */
    public YamlReloader(final KarmaYamlManager currentConfiguration) {
        this.current = currentConfiguration;
    }

    /**
     * Reload the yaml configuration
     *
     * @param ignored the ignored yaml values to update
     */
    public void reload(final String... ignored) {
        Object source = this.current.getSourceRoot().getSource();
        if (source instanceof File || source instanceof Path) {
            File file;
            if (source instanceof File) {
                file = (File) source;
            } else {
                file = ((Path) source).toFile();
            }
            KarmaYamlManager newConfiguration = new KarmaYamlManager(file);
            this.current.update(newConfiguration, true, ignored);
        } else {
            throw new RuntimeException("Tried to reload karma configuration from a non file/path source karma configuration");
        }
    }
}
