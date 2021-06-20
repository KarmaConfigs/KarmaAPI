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
 * KarmaAPI jar appender
 */
public interface JarAppender extends AutoCloseable {

    /**
     * Add a new jar file to the class path
     *
     * @param url the jar location
     */
    void addJarToClasspath(URL url);

    /**
     * Add a new jar file to the class path
     *
     * @param uri the jar location
     */
    void addJarToClasspath(URI uri);

    /**
     * Add a new jar file to the class path
     *
     * @param file the jar file
     */
    void addJarToClasspath(File file);

    /**
     * Add a new jar file to the class path
     *
     * @param path the jar file
     */
    void addJarToClasspath(Path path);

    /**
     * Close the loader
     */
    @Override
    void close();

    /**
     * Get the appender URL class loader
     *
     * @return the appender URL class loader
     */
    URLClassLoader getLoader();
}
