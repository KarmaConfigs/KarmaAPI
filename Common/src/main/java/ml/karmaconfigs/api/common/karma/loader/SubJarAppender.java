package ml.karmaconfigs.api.common.karma.loader;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
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
 * KarmaAPI external jar appender ( makes uses of JarAppender interface )
 */
public final class SubJarAppender implements JarAppender {

    private final SubJarLoader loader;

    /**
     * Initialize the sub jar appender
     *
     * @param cl the current class loader
     */
    public SubJarAppender(final ClassLoader cl) {
        if (cl instanceof SubJarLoader) {
            loader = (SubJarLoader) cl;
        } else {
            throw new IllegalArgumentException("Loader is not a SubJarLoader " + cl.getClass().getName());
        }
    }

    /**
     * Add a new jar file to the class path
     *
     * @param url the jar location
     */
    @Override
    public final void addJarToClasspath(URL url) {
        try {
            loader.addJarToClasspath(url);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Add a new jar file to the class path
     *
     * @param uri the jar location
     */
    @Override
    public final void addJarToClasspath(URI uri) {
        try {
            loader.addJarToClasspath(uri.toURL());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Add a new jar file to the class path
     *
     * @param file the jar file
     */
    @Override
    public final void addJarToClasspath(File file) {
        try {
            loader.addJarToClasspath(file.toURI().toURL());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Add a new jar file to the class path
     *
     * @param path the jar file
     */
    @Override
    public final void addJarToClasspath(Path path) {
        try {
            loader.addJarToClasspath(path.toFile().toURI().toURL());
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Close the loader
     */
    @Override
    public void close() {
        loader.closeLoader();
    }

    /**
     * Get the appender URL class loader
     *
     * @return the appender URL class loader
     */
    @Override
    public final URLClassLoader getLoader() {
        return loader;
    }
}
