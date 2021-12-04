package ml.karmaconfigs.api.common;

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

import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.utils.file.FileUtilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import static ml.karmaconfigs.api.common.karma.KarmaAPI.source;

/**
 * Karma resource downloader
 */
public final class ResourceDownloader {

    /**
     * The destination download file
     */
    private final File destFile;

    /**
     * The download URL
     */
    private final String url;

    /**
     * Initialize the resource downloader
     *
     * @param destination the resource destination
     * @param _url the resource download URL
     */
    public ResourceDownloader(final File destination, final String _url) {
        this.destFile = destination;
        this.url = _url;
    }

    /**
     * Download something to cache
     *
     * @param source the resource source
     * @param fileName the destination file name
     * @param downloadURL the resource download URL
     * @param sub the resource path
     * @return a new resource download instance
     */
    public static ResourceDownloader toCache(final KarmaSource source, final String fileName, final String downloadURL, final String... sub) {
        File target;
        if (sub.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String dir : sub)
                builder.append(File.separator).append(dir);
            target = new File(source().getDataPath() + File.separator + "cache" + builder, fileName);
        } else {
            target = new File(source().getDataPath() + File.separator + "cache", fileName);
        }
        if (FileUtilities.isValidFile(target))
            return new ResourceDownloader(target, downloadURL);
        throw new RuntimeException("Tried to download invalid resource file");
    }

    /**
     * Download the resource
     */
    public void download() {
        ReadableByteChannel rbc = null;
        InputStream stream = null;
        FileOutputStream output = null;
        HttpURLConnection connection = null;
        try {
            FileUtilities.create(this.destFile);
            URL download_url = new URL(this.url);
            connection = (HttpURLConnection) download_url.openConnection();
            connection.connect();
            stream = connection.getInputStream();
            long destSize = this.destFile.length();
            long connSize = connection.getContentLengthLong();
            if (destSize != connSize) {
                source().console().send("&b[ KarmaAPI ] &3Downloading file {0}", this.destFile.getName());
                rbc = Channels.newChannel(download_url.openStream());
                output = new FileOutputStream(this.destFile);
                output.getChannel().transferFrom(rbc, 0L, Long.MAX_VALUE);
            }
            connection.disconnect();
        } catch (Throwable ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rbc != null)
                    rbc.close();
                if (stream != null)
                    stream.close();
                if (output != null)
                    output.close();
                if (connection != null)
                    connection.disconnect();
            } catch (Throwable ignored) {
            }
            source().console().send("&b[ KarmaAPI ] &3Downloaded file {0}", this.destFile.getName());
        }
    }

    /**
     * Get the resource destination file
     *
     * @return the resource destination file
     */
    public File getDestFile() {
        return this.destFile;
    }
}
