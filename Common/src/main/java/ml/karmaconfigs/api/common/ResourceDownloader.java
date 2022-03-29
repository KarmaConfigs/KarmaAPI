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
import ml.karmaconfigs.api.common.karma.file.KarmaConfig;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.file.FileUtilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static ml.karmaconfigs.api.common.karma.KarmaAPI.source;

/**
 * Karma resource downloader
 */
public final class ResourceDownloader {

    private final static Set<String> success = Collections.newSetFromMap(new ConcurrentHashMap<>());

    /**
     * The destination download file
     */
    private final File destFile;

    /**
     * The download URL
     */
    private final String url;

    private boolean history = false;

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
            Path dataPath = source.getDataPath();
            for (String dir : sub)
                dataPath = dataPath.resolve(dir);

            target = dataPath.resolve(fileName).toFile();
        } else {
            target = source.getDataPath().resolve("cache").resolve(fileName).toFile();
        }
        if (FileUtilities.isValidFile(target))
            return new ResourceDownloader(target, downloadURL);
        throw new RuntimeException("Tried to download invalid resource file");
    }

    /**
     * Set this download history status
     *
     * @param status the history
     * @return this instance
     */
    public ResourceDownloader history(final boolean status) {
        history = status;

        return this;
    }

    /**
     * Download the resource
     */
    public void download() {
        KarmaConfig config = new KarmaConfig();
        ReadableByteChannel rbc = null;

        InputStream stream = null;
        FileOutputStream output = null;
        try {
            boolean process = true;
            if (history) {
                if (success.contains(FileUtilities.getPrettyFile(destFile))) {
                    process = !destFile.exists();
                }
            }
            if (process) {
                FileUtilities.create(destFile);

                URL download_url = new URL(this.url);
                stream = download_url.openStream();

                if (config.debug(Level.INFO)) {
                    source(false).console().send("Downloading file {0}", Level.INFO, this.destFile.getName());
                }

                rbc = Channels.newChannel(stream);
                output = new FileOutputStream(destFile);
                output.getChannel().transferFrom(rbc, 0L, Long.MAX_VALUE);

                if (history) {
                    success.add(FileUtilities.getPrettyFile(destFile));
                }
            }
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
            } catch (Throwable ignored) {
            }

            if (config.debug(Level.OK)) {
                source(false).console().send("Downloaded file {0}", Level.OK, this.destFile.getName());
            }
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

    /**
     * Clear the download history
     */
    public static void clearHistory() {
        success.clear();
    }

    /**
     * Remove a file from the history
     *
     * @param file the file
     */
    public static void removeHistory(final File file) {
        success.remove(FileUtilities.getPrettyFile(file));
    }
}
