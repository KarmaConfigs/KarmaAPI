package ml.karmaconfigs.api.common.karma.loader;

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
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

/**
 * Karma source injector
 */
public final class SourceInjector implements SourceAppender {

    private final SourceLoader loader;

    /**
     * Initialize the source injector
     *
     * @param cl the source class loader
     */
    public SourceInjector(final ClassLoader cl) {
        if (cl instanceof SourceLoader) {
            this.loader = (SourceLoader) cl;
        } else {
            throw new IllegalArgumentException("Loader is not a SubJarLoader " + cl.getClass().getName());
        }
    }

    /**
     * Add a source to the current path
     *
     * @param url the source file url
     */
    @Override
    public void addSource(final URL url) {
        try {
            this.loader.addJarToClasspath(url);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Add a source to the current path
     *
     * @param uri the source file URI
     */
    @Override
    public void addSource(final URI uri) {
        try {
            this.loader.addJarToClasspath(uri.toURL());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Add a source to the current path
     *
     * @param file the source file
     */
    @Override
    public void addSource(final File file) {
        try {
            this.loader.addJarToClasspath(file.toURI().toURL());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Add a source to the current path
     *
     * @param path the source file path
     */
    @Override
    public void addSource(final Path path) {
        try {
            this.loader.addJarToClasspath(path.toFile().toURI().toURL());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Close the current appender
     */
    @Override
    public void close() {
        this.loader.closeLoader();
    }

    /**
     * Get the current URL loader
     *
     * @return the current URL loader
     */
    @Override
    public URLClassLoader getLoader() {
        return this.loader;
    }
}
