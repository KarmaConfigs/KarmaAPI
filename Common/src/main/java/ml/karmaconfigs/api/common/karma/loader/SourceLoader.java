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
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;
import java.nio.file.attribute.FileAttribute;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Karma source loader
 */
public final class SourceLoader extends URLClassLoader {

    /**
     * List of loaded sources
     */
    private static final Set<URL> loaded_urls = Collections.newSetFromMap(new ConcurrentHashMap<>());

    static {
        ClassLoader.registerAsParallelCapable();
    }

    /**
     * Initialize the source loader
     *
     * @param currentLoader the current class loader
     * @param subJar the jar containing bootstrap sources
     */
    public SourceLoader(final ClassLoader currentLoader, final URL subJar) {
        super(new URL[]{extractJar(subJar)}, currentLoader);
    }

    /**
     * Initialize the source loader
     *
     * @param currentLoader the current class loader
     * @param subJar the jar containing bootstrap sources
     * @throws IOException if something goes wrong
     */
    public SourceLoader(ClassLoader currentLoader, File subJar) throws IOException {
        super(new URL[]{extractJar(subJar.toURI().toURL())}, currentLoader);
    }

    /**
     * Initialize the source loader
     *
     * @param currentLoader the current class loader
     * @param subJar the jar containing bootstrap sources
     * @throws IOException if something goes wrong
     */
    public SourceLoader(ClassLoader currentLoader, Path subJar) throws IOException {
        super(new URL[]{extractJar(subJar.toFile().toURI().toURL())}, currentLoader);
    }

    /**
     * Extract the jar sources to a temporal file
     * to inject them other sources
     *
     * @param jarFile the
     * @return the url of the temporal file
     */
    private static URL extractJar(final URL jarFile) {
        if (jarFile != null) {
            Path tempFile;
            File file = new File(jarFile.getPath().replaceAll("%20", " "));
            try {
                tempFile = Files.createTempFile(file.getName(), ".jar.tmp");
            } catch (IOException e) {
                throw new RuntimeException("Unable to create a temporary file", e);
            }
            tempFile.toFile().deleteOnExit();
            try {
                InputStream in = jarFile.openStream();
                try {
                    Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
                    in.close();
                } catch (Throwable throwable) {
                    if (in != null)
                        try {
                            in.close();
                        } catch (Throwable throwable1) {
                            throwable.addSuppressed(throwable1);
                        }
                    throw throwable;
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to copy jar-in-jar to temporary path", e);
            }
            try {
                return tempFile.toUri().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException("Unable to get URL from path", e);
            }
        }
        throw new RuntimeException("Unable to find sub jar file");
    }

    /**
     * Add a source to the current path
     *
     * @param url the source file url
     */
    public void addJarToClasspath(final URL url) {
        addURL(url);
    }

    /**
     * Delete the jar sources file
     */
    public void deleteJarResource() {
        URL[] urls = getURLs();
        if (urls.length == 0)
            return;

        try {
            Path path = Paths.get(urls[0].toURI());
            Files.deleteIfExists(path);
        } catch (Throwable ignored) {
        }
    }

    /**
     * Instantiate the bootstrap class
     *
     * @param bootstrapClass the bootstrap class
     * @param loaderPluginType the loader type
     * @param loaderPlugin the loader
     * @param <T> the return type
     * @return the instance
     */
    public <T> KarmaBootstrap instantiate(final String bootstrapClass, final Class<T> loaderPluginType, final T loaderPlugin) {
        Class<? extends KarmaBootstrap> plugin;
        Constructor<? extends KarmaBootstrap> constructor;
        try {
            plugin = loadClass(bootstrapClass).asSubclass(KarmaBootstrap.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to load bootstrap class", e);
        }
        try {
            constructor = plugin.getConstructor(loaderPluginType);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to get bootstrap constructor", e);
        }
        try {
            return constructor.newInstance(loaderPlugin);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to create bootstrap plugin instance", e);
        }
    }

    /**
     * Instantiate the bootstrap class
     *
     * @param bootstrapClass the bootstrap class
     * @param <T> the return type
     * @return the instance
     */
    public <T> KarmaBootstrap instantiate(final String bootstrapClass) {
        Class<? extends KarmaBootstrap> plugin;
        try {
            plugin = loadClass(bootstrapClass).asSubclass(KarmaBootstrap.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to load bootstrap class", e);
        }
        try {
            return plugin.getConstructor(new Class[0]).newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to get bootstrap constructor", e);
        }
    }

    /**
     * Close this source loader
     */
    public void closeLoader() {
        deleteJarResource();
        try {
            close();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
