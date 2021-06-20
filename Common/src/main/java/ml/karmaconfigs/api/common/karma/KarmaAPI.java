package ml.karmaconfigs.api.common.karma;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

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
 * KarmaAPI utilities, such a version, compiler and build date
 */
public interface KarmaAPI extends Serializable {

    static String getVersion() {
        String version = "-1";

        try {
            InputStream in = KarmaAPI.class.getResourceAsStream("/api.properties");
            if (in != null) {
                Properties properties = new Properties();
                properties.load(in);

                version = properties.getProperty("version", "-1");
            }
        } catch (Throwable ignored) {}

        return version;
    }

    static String getCompilerVersion() {
        String version = "15";

        try {
            InputStream in = KarmaAPI.class.getResourceAsStream("/api.properties");
            if (in != null) {
                Properties properties = new Properties();
                properties.load(in);

                version = properties.getProperty("java_version", "15");
            }
        } catch (Throwable ignored) {}

        return version;
    }

    static String getBuildDate() {
        String compile_date = "01-01-1999 00:00:00";

        try {
            InputStream in = KarmaAPI.class.getResourceAsStream("/api.properties");
            if (in != null) {
                Properties properties = new Properties();
                properties.load(in);

                compile_date = properties.getProperty("compile_date", "01-01-1999 00:00:00");
            }
        } catch (Throwable ignored) {}

        return compile_date;
    }
}
