package ml.karmaconfigs.api.common;

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.FileUtilities;
import ml.karmaconfigs.api.common.utils.ReflectionUtil;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.cert.X509Certificate;

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
 * KarmaSource resource downloader, to download resources
 */
public final class ResourceDownloader {

    private final File destFile;
    private final String url;

    /**
     * Initialize the resource downloader
     *
     * @param destination the file destination
     * @param _url the download url
     */
    public ResourceDownloader(final File destination, final String _url) {
        destFile = destination;
        url = _url;
    }

    /**
     * Tries to download the file
     *
     * @return if the file could be downloaded
     */
    public final boolean download() {
        boolean result = true;

        InputStream is = null;
        ReadableByteChannel rbc = null;
        FileOutputStream fos = null;
        try {
            FileUtilities.create(destFile);

            TrustManager[] trustManagers = new TrustManager[]{new NvbTrustManager()};
            final SSLContext context = SSLContext.getInstance("SSL");
            context.init(null, trustManagers, null);

            // Set connections to use lenient TrustManager and HostnameVerifier
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new NvbHostnameVerifier());

            URL url_obj = new URL(url);

            is = url_obj.openStream();
            rbc = Channels.newChannel(is);
            fos = new FileOutputStream(destFile);

            ReflectionUtil.tryBroadcast("&3Downloading file &f{0}", destFile.getName());

            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (Throwable ex) {
            ex.printStackTrace();
            ReflectionUtil.tryBroadcast("&cAn error occurred while downloading file &f{0}", destFile.getName());

            result = false;
        } finally {
            try {
                if (rbc != null) {
                    rbc.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (is != null) {
                    is.close();
                }
                ReflectionUtil.tryBroadcast("&aDownloaded file &f{0}", destFile.getName());
            } catch (Throwable ignored) {}
        }

        return result;
    }

    /**
     * Get the dest file
     *
     * @return the dest file
     */
    public final File getDestFile() {
        return destFile;
    }

    /**
     * Get if the file is downloaded by comparing their size
     *
     * @return if the resource is downloaded
     */
    public final boolean isDownloaded() {
        if (destFile.exists()) {
            try {
                TrustManager[] trustManagers = new TrustManager[]{new NvbTrustManager()};
                final SSLContext context = SSLContext.getInstance("SSL");
                context.init(null, trustManagers, null);

                // Set connections to use lenient TrustManager and HostnameVerifier
                HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
                HttpsURLConnection.setDefaultHostnameVerifier(new NvbHostnameVerifier());

                URL url_obj = new URL(url);
                URLConnection connection = url_obj.openConnection();

                return connection.getContentLengthLong() == destFile.length();
            } catch (Throwable ignored) {}
        }

        return false;
    }

    /**
     * Simple <code>TrustManager</code> that allows unsigned certificates.
     */
    private static final class NvbTrustManager implements TrustManager, X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) { }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) { }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    /**
     * Simple <code>HostnameVerifier</code> that allows any hostname and session.
     */
    private static final class NvbHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * Download a file directly to karma source cache
     *
     * @param source the karma source
     * @param fileName the file name
     * @param downloadURL the file download url
     * @param sub the file sub directory
     * @return the downloaded file
     */
    public static ResourceDownloader toCache(final KarmaSource source, final String fileName, final String downloadURL, final String... sub) {
        File target;
        if (sub.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String dir : sub)
                builder.append(File.separator).append(dir);

            target = new File(source.getDataPath() + File.separator + "cache" + builder.toString(), fileName);
        } else {
            target = new File(source.getDataPath() + File.separator + "cache", fileName);
        }

        if (FileUtilities.isValidFile(target)) {
            return new ResourceDownloader(target, downloadURL);
        } else {
            throw new RuntimeException("Tried to download invalid resource file");
        }
    }
}
