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

import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.common.utils.file.FileUtilities;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
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
        try {
            File jarFile = FileUtilities.getSourceFile(source);
            Path randomLocation = Files.createTempFile(StringUtils.randomString(), StringUtils.randomString());

            Files.copy(jarFile.toPath(), randomLocation, StandardCopyOption.REPLACE_EXISTING);
            Files.move(randomLocation, jarFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Files.deleteIfExists(randomLocation);
            return false;
        } catch (Throwable ex) {
            return true;
        }
    }
}
