package ml.karmaconfigs.api.common.version;

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

import ml.karmaconfigs.api.common.Logger;
import ml.karmaconfigs.api.common.karma.KarmaSource;
import ml.karmaconfigs.api.common.karma.file.KarmaConfig;
import ml.karmaconfigs.api.common.timer.scheduler.LateScheduler;
import ml.karmaconfigs.api.common.timer.scheduler.worker.AsyncLateScheduler;
import ml.karmaconfigs.api.common.utils.url.URLUtils;
import ml.karmaconfigs.api.common.utils.enums.Level;
import ml.karmaconfigs.api.common.utils.file.FileUtilities;
import ml.karmaconfigs.api.common.utils.string.ComparatorBuilder;
import ml.karmaconfigs.api.common.utils.string.StringUtils;
import ml.karmaconfigs.api.common.utils.string.VersionComparator;
import ml.karmaconfigs.api.common.version.util.VersionCheckType;
import ml.karmaconfigs.api.common.version.util.VersionResolver;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Karma legacy version updater
 */
public final class LegacyVersionUpdater extends VersionUpdater {

    /**
     * A map containing source => version fetch
     */
    private static final Map<KarmaSource, VersionFetchResult> results = new ConcurrentHashMap<>();

    /**
     * The updater source
     */
    private KarmaSource source;

    /**
     * The url where to check for updates
     */
    private URL checkURL;

    /**
     * The version check type
     */
    private VersionCheckType versionType;
    /**
     * The version resolver
     */
    private VersionResolver versionResolver;

    /**
     * Initialize the legacy version updater
     */
    private LegacyVersionUpdater() {
    }

    /**
     * Create a new legacy version updater builder
     *
     * @param owner the updater source
     * @return a new version updater builder
     */
    public static VersionUpdater.VersionBuilder createNewBuilder(final KarmaSource owner) {
        return new LegacyVersionBuilder(owner);
    }


    /**
     * Fetch for updates
     *
     * @param force force the update instead
     *              of returning the cached result
     * @return the fetch result
     */
    public LateScheduler<VersionFetchResult> fetch(final boolean force) {
        KarmaConfig config = new KarmaConfig();

        AsyncLateScheduler<VersionFetchResult> asyncLateScheduler = new AsyncLateScheduler<>();
        if (force || !results.containsKey(this.source) || results.getOrDefault(this.source, null) == null) {
            source.async().queue(() -> {
                Logger logger = new Logger(source);

                try {
                    boolean updated;
                    URLConnection connection = this.checkURL.openConnection();
                    InputStream file = connection.getInputStream();
                    Path temp = Files.createTempFile("kfetcher_", StringUtils.generateString().create());
                    File tempFile = FileUtilities.getFixedFile(temp.toFile());
                    tempFile.deleteOnExit();
                    if (!tempFile.exists())
                        Files.createFile(temp);
                    Files.copy(file, temp, StandardCopyOption.REPLACE_EXISTING);
                    List<String> lines = Files.readAllLines(tempFile.toPath());
                    String version = lines.get(0);
                    String update = lines.get(1);
                    List<String> changelog = new ArrayList<>();
                    for (int i = 2; i < lines.size(); i++)
                        changelog.add(lines.get(i));

                    ComparatorBuilder builder;
                    VersionComparator comparator;
                    switch (this.versionType) {
                        case ID:
                            updated = this.source.version().equals(version);
                            break;
                        case RESOLVABLE_ID:
                            builder = VersionComparator.createBuilder()
                                    .currentVersion(versionResolver.resolve(source.version()))
                                    .checkVersion(versionResolver.resolve(version));
                            comparator = StringUtils.compareTo(builder);

                            updated = comparator.isUpToDate();
                            break;
                        default:
                            builder = VersionComparator.createBuilder()
                                    .currentVersion(source.version())
                                    .checkVersion(version);
                            comparator = StringUtils.compareTo(builder);

                            updated = comparator.isUpToDate();
                            break;
                    }
                    VersionFetchResult result = new VersionFetchResult(updated, version, this.source.version(), update, changelog.<String>toArray(new String[0]), this.versionResolver);
                    results.put(this.source, result);
                    asyncLateScheduler.complete(result);
                } catch (Throwable ex) {
                    if (config.log(Level.GRAVE)) {
                        logger.scheduleLog(Level.GRAVE, ex);
                    }
                    if (config.log(Level.INFO)) {
                        logger.scheduleLog(Level.INFO, "Failed to check for updates for source {0}", source.name());
                    }

                    if (config.debug(Level.GRAVE)) {
                        source.console().send("Failed to check for updates at {0}", Level.GRAVE, source.updateURL());
                    }

                    asyncLateScheduler.complete(null, ex);
                }
            });
        } else {
            asyncLateScheduler.complete(results.get(this.source));
        }
        return asyncLateScheduler;
    }

    /**
     * Get the last update fetch result
     *
     * @return the last update fetch result
     */
    public LateScheduler<VersionFetchResult> get() {
        AsyncLateScheduler<VersionFetchResult> asyncLateScheduler = new AsyncLateScheduler<>();
        source.async().queue(() -> {
            VersionFetchResult result = results.getOrDefault(this.source, null);
            if (result == null) {
                fetch(true).whenComplete((Consumer<VersionFetchResult>) asyncLateScheduler::complete);
            } else {
                asyncLateScheduler.complete(result);
            }
        });
        return asyncLateScheduler;
    }

    /**
     * Legacy version updater builder
     */
    public static class LegacyVersionBuilder extends VersionUpdater.VersionBuilder {

        /**
         * Initialize the version builder
         *
         * @param owner the builder source
         */
        LegacyVersionBuilder(final KarmaSource owner) {
            super(owner);
        }

        /**
         * Build the version builder
         *
         * @return a new version updater instance
         * @throws IllegalStateException if something goes wrong
         */
        public VersionUpdater build() throws IllegalStateException {
            if (!StringUtils.isNullOrEmpty(URLUtils.getOrNull(getSource().updateURL()))) {
                LegacyVersionUpdater analyzer = new LegacyVersionUpdater();
                analyzer.source = getSource();
                analyzer.checkURL = URLUtils.getOrNull(getSource().updateURL());
                analyzer.versionType = getType();
                if (getType().equals(VersionCheckType.RESOLVABLE_ID) && getResolver() == null)
                    throw new IllegalStateException("Cannot build a version updater with null version resolver and using RESOLVABLE_ID version type");
                analyzer.versionResolver = getResolver();
                return analyzer;
            }

            throw new IllegalStateException("Cannot build a version updater with null/invalid version check URL");
        }
    }
}
