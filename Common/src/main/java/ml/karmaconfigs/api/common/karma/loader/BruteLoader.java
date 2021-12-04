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

import ml.karmaconfigs.api.common.JavaVM;
import org.burningwave.core.assembler.StaticComponentContainer;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;

/**
 * Brute source loader
 *
 * This new way of loading sources to the project class
 * path export all the modules to all the modules
 * "ExportAllToAll" using BurningWave utility class.
 *
 * That way, there's no need to create any class loader
 * nor bootstrap
 */
public final class BruteLoader {

    private static boolean open = false;

    private final URLClassLoader loader;

    /**
     * Initialize the brute loader
     *
     * @param ucl the main class loader
     */
    public BruteLoader(final URLClassLoader ucl) {
        loader = ucl;

        if (JavaVM.javaVersion() > 11 && !open) {
            open = true;
            StaticComponentContainer.Modules.exportAllToAll();
            StaticComponentContainer.ManagedLoggersRepository.disableLogging();
        }
    }

    /**
     * Tries to add the specified source to the
     * application classpath
     *
     * @return if the source could be added
     */
    public boolean add(final URL source) {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(loader, source);
            method.setAccessible(false);

            return true;
        } catch (Throwable ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Tries to add the specified source to the
     * application classpath
     *
     * @return if the source could be added
     */
    public boolean add(final File source) {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(loader, source.toURI().toURL());
            method.setAccessible(false);

            return true;
        } catch (Throwable ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Tries to add the specified source to the
     * application classpath
     *
     * @return if the source could be added
     */
    public boolean add(final Path source) {
        try {
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(loader, source.toUri().toURL());
            method.setAccessible(false);

            return true;
        } catch (Throwable ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Get the used class loader
     *
     * @return the class loader
     */
    public URLClassLoader getLoader() {
        return loader;
    }
}
