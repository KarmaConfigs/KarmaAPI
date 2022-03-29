package ml.karmaconfigs.api.common.utils;

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

import ml.karmaconfigs.api.common.utils.url.HttpUtil;
import org.jetbrains.annotations.Nullable;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Karma URL utilities
 *
 * @deprecated This has been moved to {@link ml.karmaconfigs.api.common.utils.url.URLUtils}
 */
@Deprecated
public final class URLUtils {

    /**
     * Connect and instantly disconnect from a URL.
     * This method may be called only when actually
     * needed
     *
     * @param url the url to connect
     */
    public static void fastConnect(final URL url) {
        HttpUtil utils = ml.karmaconfigs.api.common.utils.url.URLUtils.extraUtils(url);
        if (utils != null)
            utils.push();
    }

    /**
     * Get if the URL exists
     *
     * @param url the url
     * @return if the url exists
     */
    public static boolean exists(final String url) {
        return (getResponseCode(url) == 200);
    }

    /**
     * Get the URL response code
     *
     * @param url the url
     * @return the URL response code
     */
    public static int getResponseCode(final String url) {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) (new URL(url)).openConnection();
            con.setInstanceFollowRedirects(false);
            con.setRequestMethod("HEAD");
            return con.getResponseCode();
        } catch (Throwable e) {
            return 503;
        }
    }

    /**
     * Get the URL or null if couldn't retrieve
     * specified URL
     *
     * @param url the url
     * @return the url or null
     */
    @Nullable
    public static URL getOrNull(final String url) {
        try {
            return new URL(url);
        } catch (Throwable ex) {
            return null;
        }
    }

    /**
     * Get the url or default
     *
     * @param def the default url
     * @param backup the backup urls
     * @return the url or defaults
     */
    @Nullable
    public static URL getOrBackup(final String def, final String... backup) {
        URL defURL = getOrNull(def);
        if (defURL == null) {
            int trie = 0;
            do {
                defURL = getOrNull(backup[trie]);
                trie++;
            } while (trie < backup.length);
        }

        return defURL;
    }

    /**
     * Get the web response
     *
     * @param url the url to fetch response from
     * @return the web response or empty
     */
    public static String getResponse(final URL url) {
        HttpUtil utils = ml.karmaconfigs.api.common.utils.url.URLUtils.extraUtils(url);
        if (utils != null)
            return utils.getResponse();

        return null;
    }
}
