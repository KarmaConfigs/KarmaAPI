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
 * Karma sources appender
 */
public interface SourceAppender extends AutoCloseable {

    /**
     * Add a source to the current path
     *
     * @param paramURL the source file url
     */
    void addSource(URL paramURL);

    /**
     * Add a source to the current path
     *
     * @param paramURI the source file URI
     */
    void addSource(URI paramURI);

    /**
     * Add a source to the current path
     *
     * @param paramFile the source file
     */
    void addSource(File paramFile);

    /**
     * Add a source to the current path
     *
     * @param paramPath the source file path
     */
    void addSource(Path paramPath);

    /**
     * Close the current appender
     */
    void close();

    /**
     * Get the current URL loader
     *
     * @return the current URL loader
     */
    URLClassLoader getLoader();
}
