package ml.karmaconfigs.api.common.karmafile.karmayaml;

import ml.karmaconfigs.api.common.karma.KarmaSource;

import java.io.File;
import java.nio.file.Path;

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
 * Since version 1.2.5 this does not longer save the file, as it
 * should be saved by calling {@link KarmaYamlManager#save(File)} or
 * {@link KarmaYamlManager#save(File, KarmaSource, String)}
*/
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