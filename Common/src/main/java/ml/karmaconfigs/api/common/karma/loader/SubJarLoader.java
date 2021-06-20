package ml.karmaconfigs.api.common.karma.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

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
 * Classloader that can load a jar from within another jar file.
 *
 * <p>The "loader" jar contains the loading code and public API classes,
 * and is class-loaded by the platform.</p>
 *
 * <p>The external jar contains the code itself, and is class-loaded
 * by the loading code and this classloader.</p>
 */
public final class SubJarLoader extends URLClassLoader {

    static {
        ClassLoader.registerAsParallelCapable();
    }

    /**
     * Initialize the sub jar loader
     *
     * @param currentLoader the current class loader
     * @param subJar the sub jar
     */
    public SubJarLoader(final ClassLoader currentLoader, final URL subJar) {
        super(new URL[]{extractJar(subJar)}, currentLoader);
    }

    /**
     * Initialize the sub jar loader
     *
     * @param currentLoader the current class loader
     * @param subJar the sub jar
     *
     * @throws IOException if something goes wrong
     */
    public SubJarLoader(final ClassLoader currentLoader, final File subJar) throws IOException {
        super(new URL[]{extractJar(subJar.toURI().toURL())}, currentLoader);
    }

    /**
     * Initialize the sub jar loader
     *
     * @param currentLoader the current class loader
     * @param subJar the sub jar
     *
     * @throws IOException if something goes wrong
     */
    public SubJarLoader(final ClassLoader currentLoader, final Path subJar) throws IOException {
        super(new URL[]{extractJar(subJar.toFile().toURI().toURL())}, currentLoader);
    }

    /**
     * Add a new jar file to the class path
     *
     * @param url the jar location
     */
    public final void addJarToClasspath(URL url) {
        addURL(url);
    }

    /**
     * Delete the jar resource
     */
    public final void deleteJarResource() {
        URL[] urls = getURLs();
        if (urls.length == 0) {
            return;
        }

        try {
            Path path = Paths.get(urls[0].toURI());
            Files.deleteIfExists(path);
        } catch (Throwable ignored) {}
    }

    /**
     * Creates a new plugin instance.
     *
     * @param bootstrapClass the name of the bootstrap plugin class
     * @param loaderPluginType the type of the loader plugin, the only parameter of the bootstrap
     *                         plugin constructor
     * @param loaderPlugin the loader plugin instance
     * @param <T> the type of the loader plugin
     * @return the instantiated bootstrap plugin
     */
    public final <T> KarmaBootstrap instantiate(final String bootstrapClass, final Class<T> loaderPluginType, final T loaderPlugin) {
        Class<? extends KarmaBootstrap> plugin;
        try {
            plugin = loadClass(bootstrapClass).asSubclass(KarmaBootstrap.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to load bootstrap class", e);
        }

        Constructor<? extends KarmaBootstrap> constructor;
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
     * Creates a new plugin instance.
     *
     * @param bootstrapClass the name of the bootstrap plugin class
     * @param <T> the type of the loader plugin
     * @return the instantiated bootstrap plugin
     */
    public final <T> KarmaBootstrap instantiate(final String bootstrapClass) {
        Class<? extends KarmaBootstrap> plugin;
        try {
            plugin = loadClass(bootstrapClass).asSubclass(KarmaBootstrap.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to load bootstrap class", e);
        }

        try {
            return plugin.getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Unable to get bootstrap constructor", e);
        }
    }

    /**
     * Close the sub jar class loader
     */
    public final void closeLoader() {
        deleteJarResource();
        try {
            close();
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Extracts the "jar-in-jar" from the loader plugin into a temporary file,
     * then returns a URL that can be used by the {@link SubJarLoader}.
     *
     * @param jarFile the jar file to load
     * @return a URL to the extracted file
     */
    private static URL extractJar(final URL jarFile) {
        // get the jar-in-jar resource
        if (jarFile != null) {
            Path tempFile;
            File file = new File(jarFile.getPath().replaceAll("%20", " "));

            try {
                tempFile = Files.createTempFile(file.getName(), ".jar.tmp");
            } catch (IOException e) {
                throw new RuntimeException("Unable to create a temporary file", e);
            }

            // mark that the file should be deleted on exit
            tempFile.toFile().deleteOnExit();

            try (InputStream in = jarFile.openStream()) {
                Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException("Unable to copy jar-in-jar to temporary path", e);
            }

            try {
                return tempFile.toUri().toURL();
            } catch (MalformedURLException e) {
                throw new RuntimeException("Unable to get URL from path", e);
            }
        } else {
            throw new RuntimeException("Unable to find sub jar file");
        }
    }
}
