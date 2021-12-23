package ml.karmaconfigs.api.common.karma;

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

import ml.karmaconfigs.api.common.karma.loader.BruteLoader;
import ml.karmaconfigs.api.common.karma.loader.component.NameComponent;
import ml.karmaconfigs.api.common.utils.URLUtils;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.string.StringUtils;

import java.io.InputStream;
import java.io.Serializable;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Karma API
 */
public interface KarmaAPI extends Serializable {

    /**
     * Get the current API version
     *
     * @return the current API version
     */
    static String getVersion() {
        String version = "-1";
        try {
            InputStream in = KarmaAPI.class.getResourceAsStream("/api.properties");
            if (in != null) {
                Properties properties = new Properties();
                properties.load(in);
                version = properties.getProperty("version", "-1");
            }
        } catch (Throwable ignored) {
        }
        return version;
    }

    /**
     * Get the used java version to compile
     * the API
     *
     * @return the java version used to compile the API
     */
    static String getCompilerVersion() {
        String version = "16";
        try {
            InputStream in = KarmaAPI.class.getResourceAsStream("/api.properties");
            if (in != null) {
                Properties properties = new Properties();
                properties.load(in);
                version = properties.getProperty("java_version", "15");
            }
        } catch (Throwable ignored) {
        }
        return version;
    }

    /**
     * Get the API build date
     *
     * @return the API build date
     */
    static String getBuildDate() {
        String compile_date = "01-01-1999 00:00:00";
        try {
            InputStream in = KarmaAPI.class.getResourceAsStream("/api.properties");
            if (in != null) {
                Properties properties = new Properties();
                properties.load(in);
                compile_date = properties.getProperty("compile_date", "01-01-1999 00:00:00");
            }
        } catch (Throwable ignored) {
        }
        return compile_date;
    }

    /**
     * Get if the specified source jar is loaded
     *
     * @param source the source
     * @return if the source jar is loaded
     */
    static boolean isLoaded(final KarmaSource source) {
        boolean status = false;
        Path destJar = null;

        try {
            Path sourceJar = source.getSourceFile().toPath();
            destJar = Files.createTempFile(StringUtils.generateString().create() + "_", StringUtils.generateString().create());

            Files.move(sourceJar, destJar);
            Files.move(destJar, sourceJar);
        } catch (Throwable ex) {
            status = true;
        } finally {
            try {
                Files.deleteIfExists(destJar);
            } catch (Throwable ignored) {}
        }

        return status;
    }

    /**
     * Get the API source
     *
     * @param force force default KarmaAPI
     * @return a KarmaSource
     */
    static KarmaSource source(final boolean force) {
        return APISource.getOriginal(force);
    }

    /**
     * Install KarmaAPI dependencies
     */
    static void install() {
        BruteLoader loader = null;
        try {
            loader = new BruteLoader(
                    (URLClassLoader) source(false).getClass().getClassLoader());
        } catch (Throwable ex) {
            try {
                loader = new BruteLoader(
                        (URLClassLoader) Thread.currentThread().getContextClassLoader());
            } catch (Throwable exc) {
                source(false).console().send("Failed to install KarmaAPI dependencies because of {0}", Level.GRAVE, ex.fillInStackTrace());
                for (StackTraceElement element : ex.getStackTrace()) {
                    source(false).console().send("&c             {0}", element);
                }
            }
        }

        if (loader != null) {
            try {
                Class.forName("com.google.gson.Gson");
            } catch (Throwable ex) {
                source(false).console().send("Google GSON dependency not found for UUID utilities, downloading it...", Level.WARNING);

                loader.downloadAndInject(
                        URLUtils.getOrNull("https://repo1.maven.org/maven2/com/google/code/gson/gson/2.8.9/gson-2.8.9.jar"),
                        NameComponent.forFile("GoogleGSON", "jar"));
            }

            try {
                Class.forName("org.apache.http.HttpResponse");
            } catch (Throwable ex) {
                source(false).console().send("Apache HTTP components not found for URL utilities, downloading it...", Level.WARNING);

                loader.downloadAndInject(
                        URLUtils.getOrNull("https://repo1.maven.org/maven2/org/apache/httpcomponents/httpclient/4.5.13/httpclient-4.5.13.jar"),
                        NameComponent.forFile("ApacheHTTP", "jar"));
            }
        }
    }
}
